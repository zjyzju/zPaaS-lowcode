package cn.zpaas.lowcode.be.engine.ability;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ServerResource;

/**
 * @author zjy Kafka消息消费能力
 */
public class KafkaMessageConsumeAbility {

	private static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumeAbility.class);

	// KafkaMessageConsumeAbility实例，实现单例模式
	private static KafkaMessageConsumeAbility instance = null;
	// KafkaConsumer的缓存对象，第一层Key是systemId，第二层Key是ftpId
	private Map<String, Map<String, KafkaConsumer<String, String>>> kafkaConsumerMap = new HashMap<>();
	//KafkaMessageConsumer对象列表
	private Map<String, List<KafkaMessageConsumer>> consumerMap = new HashMap<>();

	
	// 私有化构造函数
	private KafkaMessageConsumeAbility() {

	}

	public static KafkaMessageConsumeAbility getInstance() {
		return instance;
	}

	/**
	 * KafkaMessageConsumeAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache KafkaConsumer resource to Map...");
		}
		// 初始化KafkaMessageSendAbility实例
		KafkaMessageConsumeAbility repository = new KafkaMessageConsumeAbility();
		
		// 加载ServerResource数据-Kafka-producer
		List<ServerResource> serverResources = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_KAFKA_CONSUMER);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_KAFKA_CONSUMER);
		}

		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条Kafka-producer数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, KafkaConsumer<String, String>> map = repository.kafkaConsumerMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.kafkaConsumerMap.put(serverResource.getSystemId(), map);
				}
				try {
					map.put(serverResource.getId(), initKafkaConsumer(serverResource));
				}catch(Exception ex) {
					logger.error("init KafkaConsumer failed", ex);
				}
				
			}
		} else {
			logger.info("no valid KafkaProducer ServerResource.");
		}
		
		//启动kafka消息消费服务
		// 加载MessageConsumer数据
		List<MessageConsumer> messageConsumers = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			messageConsumers = initService.listMessageConsumers();
		} else {// 加载指定业务系统的数据
			messageConsumers = initService.listMessageConsumers(systemId);
		}
		if(!CollectionUtils.isEmpty(messageConsumers) && !repository.kafkaConsumerMap.isEmpty()) {
			//第一个key为systemId，第二个key为resourceId
			Map<String, Map<String, List<MessageConsumer>>> messageConsumerSystemMap = messageConsumers.stream().collect(
					Collectors.groupingBy(MessageConsumer::getSystemId, Collectors.groupingBy(MessageConsumer::getConsumerResourceId)));
			//循环处理两个map：repository.kafkaConsumerMap和messageConsumerSystemMap
			repository.kafkaConsumerMap.keySet().forEach((keySystemId) -> {
				Map<String, KafkaConsumer<String, String>> KafkaConsumerMap = repository.kafkaConsumerMap.get(keySystemId);
				Map<String, List<MessageConsumer>> messageConsumerMap = messageConsumerSystemMap.get(keySystemId);
				if(KafkaConsumerMap != null && !KafkaConsumerMap.isEmpty() && messageConsumerMap != null && !messageConsumerMap.isEmpty()) {
					KafkaConsumerMap.keySet().forEach((resourceId)-> {
						//找到绑定kafkaConsumer的所有MessageConsumer
						KafkaConsumer<String, String> kafkaConsumer = KafkaConsumerMap.get(resourceId);
						List<MessageConsumer> messageConsumerList = messageConsumerMap.get(resourceId);
						if(kafkaConsumer != null && messageConsumerList != null && !messageConsumerList.isEmpty()) {
							KafkaMessageConsumer consumer = new KafkaMessageConsumer(kafkaConsumer, messageConsumerList);
							List<KafkaMessageConsumer> list = repository.consumerMap.get(keySystemId);
							if (list == null) {
								list = new ArrayList<>();
								repository.consumerMap.put(keySystemId, list);
							}
							list.add(consumer);
							consumer.start();
						}
					});
				}
			});
		}
		 

		// 初始化完成，赋值给属性instance
		instance = repository;
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * KafkaMessageConsumeAbility类的缓存重新加载方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache KafkaConsumer resource to Map...");
		}
		if(StringUtils.isBlank(systemId)) {
            logger.error("systemId is null!");
            return;
        }
		// 加载ServerResource数据-Kafka-producer
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_KAFKA_CONSUMER);
		Map<String, KafkaConsumer<String, String>> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条Kafka-producer数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				try {
					map.put(serverResource.getId(), initKafkaConsumer(serverResource));
				}catch(Exception ex) {
					logger.error("init KafkaConsumer failed", ex);
				}
				
			}
		} 
		instance.kafkaConsumerMap.put(systemId, map);
		
		// 缓存刷新时，释放原资源
		if (instance != null && !CollectionUtils.isEmpty(instance.consumerMap.get(systemId))) {
			instance.consumerMap.get(systemId).forEach((consumer)-> {
				consumer.stopConsumer();
			});
		}
		
		//启动kafka消息消费服务
		// 加载MessageConsumer数据
		List<MessageConsumer> messageConsumers = initService.listMessageConsumers(systemId);
		List<KafkaMessageConsumer> consumers = new ArrayList<>();
		if(!CollectionUtils.isEmpty(messageConsumers) && !map.isEmpty()) {
			Map<String, List<MessageConsumer>> messageConsumerMap = messageConsumers.stream().collect(Collectors.groupingBy(MessageConsumer::getConsumerResourceId));
			if(!MapUtils.isEmpty(map) && !MapUtils.isEmpty(messageConsumerMap)) {
				map.keySet().forEach((resourceId)-> {
					//找到绑定kafkaConsumer的所有MessageConsumer
					KafkaConsumer<String, String> kafkaConsumer = map.get(resourceId);
					List<MessageConsumer> messageConsumerList = messageConsumerMap.get(resourceId);
					if(kafkaConsumer != null && messageConsumerList != null && !messageConsumerList.isEmpty()) {
						KafkaMessageConsumer consumer = new KafkaMessageConsumer(kafkaConsumer, messageConsumerList);
						consumers.add(consumer);
						consumer.start();
					}
				});
			}
		}
		instance.consumerMap.put(systemId, consumers);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据配置信息，初始化KafkaConsumer
	 * @param serverResource
	 * @return
	 */
	private static KafkaConsumer<String, String> initKafkaConsumer(ServerResource serverResource) {
		if(serverResource == null || StringUtils.isBlank(serverResource.getUrl())) {
			logger.error("bootstrap.servers of kafka cann't be null!");
			return null;
		}
		
		Properties properties = new Properties();
		// bootstrap.servers kafka集群地址
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverResource.getUrl());
		
		//当用户名和密码都不为空时，增加SASL_PLAINTEXT鉴权机制
		if(!StringUtils.isBlank(serverResource.getUsername()) && !StringUtils.isBlank(serverResource.getPassword())) {
			properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
			properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
			properties.put("sasl.jaas.config",
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + 
							serverResource.getUsername() + "\" password=\"" + serverResource.getPassword() + "\";");
		}
		
		//当服务器配置信息不为空时，加入配置信息
		if(!StringUtils.isBlank(serverResource.getServerCfg())) {
			JsonObject cfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
			cfg.keySet().forEach(key-> {
				properties.put(key, JsonUtils.getObject(cfg, key));
			});
			
			// key.deserializer 消息key序列化方式，未设置时
        	if(!cfg.has(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG))  {
        		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        	}
        	// value.deserializer 消息体序列化方式，未设置时
        	if(!cfg.has(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)) {
        		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        	}
		}else {
			properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		}
		return new KafkaConsumer<>(properties);
	}
	
	/**
	 * 根据systemId和KafkaConsumer资源Id获取KafkaConsumer客户端实例
	 * @param systemId
	 * @param resourceId
	 * @return
	 */
	public KafkaConsumer<String, String> getKafkaConsumer(String systemId, String resourceId) {
		Map<String, KafkaConsumer<String, String>> map = kafkaConsumerMap.get(systemId);
		if(map != null) {
			return map.get(resourceId);
		}else {
			return null;
		}
	}
}

//Kafka消息消费类
class KafkaMessageConsumer extends Thread {
	private static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
	
	public static final String SEGREGATE_FLAG = ";";
	public static final String PARAM_TOPIC = "topic";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_MSG = "msg";
	
	public static final String STRING_TYPE = "java.lang.String";
	public static final String JSON_TYPE = "com.google.gson.JsonObject";
	
	private KafkaConsumer<String, String> kafkaConsumer;
	private List<MessageConsumer> messageConsumers;
	private boolean stopped = false;
	
	public KafkaMessageConsumer(KafkaConsumer<String, String> kafkaConsumer, List<MessageConsumer> messageConsumers) {
		this.kafkaConsumer = kafkaConsumer;
		this.messageConsumers = messageConsumers;
	}
	
	public void stopConsumer() {
		this.stopped = true;
	}
	
	@Override
	public void run() {
		logger.info("{}-- starting KafkaMessageConsumer...", this.threadId());
		//所有的消息主题，去重
		Set<String> topicSet = new HashSet<>();
		//topic->MessageConsumer列表
		Map<String, List<MessageConsumer>> topicConsumerMap = new HashMap<>();
		//topic->Boolean列表，消息处理方法的第三个参数是否是JSONObect
		Map<String, List<Boolean>> isJsonTypeMap = new HashMap<>();
		
		logger.info("{}-- init topic and consumer...", this.threadId());
		//循环messageConsumers，进行初始化
		this.messageConsumers.forEach((messageConsumer)-> {
			//三个属性都不为空时，才需要处理
			if(messageConsumer.getMessageTopics() != null && messageConsumer.getMessageTopics().trim().length() > 0 &&
					messageConsumer.getServiceObjectId() != null && messageConsumer.getServiceObjectId().trim().length() > 0 &&
					messageConsumer.getOperationId() != null && messageConsumer.getOperationId().trim().length() > 0) {
				logger.info("{}-- process topic: {} and consumer: {}.{}", this.threadId(), messageConsumer.getMessageTopics(), messageConsumer.getServiceObjectId(), messageConsumer.getOperationId());
				//进行消息消费服务方法参数的合法性判断
				Operation operation = ServiceProxy.getProxy().getOperation(messageConsumer.getSystemId(), messageConsumer.getOperationId());
				//当获取到的方法信息为空或者入参数量不为3时，报错
				if(operation == null || operation.getInParams() == null || operation.getInParams().size() != 3) {
					logger.error("operation of MessageConsumer is null or param number of the operation does not equal to 3!");
					return;
				}
				Boolean isJsonType = false;//消息类型是否是JsonObject
				int index = 0;
				for(Parameter parameter : operation.getInParams()) {
					if(index == 0) {//第一个参数
						//如果第一个参数的类型不是String，且参数名不是topic时，报错
						if(!STRING_TYPE.equals(parameter.getParamClass()) || !PARAM_TOPIC.equals(parameter.getCode())) {
							logger.error("the first param's type:{} is not String or param name:{} is not topic!", parameter.getParamClass(), parameter.getCode());
							return;
						}
					}else if (index == 1) {//第二个参数
						//如果第二个参数的类型不是String，且参数名不是key时，报错
						if(!STRING_TYPE.equals(parameter.getParamClass()) || !PARAM_KEY.equals(parameter.getCode())) {
							logger.error("the second param's type:{} is not String or param name:{} is not key!", parameter.getParamClass(), parameter.getCode());
							return;
						}
					}else if (index == 2){//第三个参数
						//如果第三个参数的类型不是String，且参数名不是key时，报错
						if(!(STRING_TYPE.equals(parameter.getParamClass()) || JSON_TYPE.equals(parameter.getParamClass())) || !PARAM_MSG.equals(parameter.getCode())) {
							logger.error("the third param's type:{} is not String/JsonObject or param name:{} is not msg!", parameter.getParamClass(), parameter.getCode());
							return;
						}
						if(JSON_TYPE.equals(parameter.getParamClass())) {
							isJsonType = true;
						}
					}
					index++;
				}
				
				//拆分出主题列表
				List<String> topics = Arrays.asList(messageConsumer.getMessageTopics().split(SEGREGATE_FLAG));
				//加入到主题的set，去重
				topicSet.addAll(topics);
				//循环增加topic与MessageConsumer的映射
				for(String topic : topics) {
					List<MessageConsumer> topicConsumerList = topicConsumerMap.get(topic);
					List<Boolean> isJsonTypeList = isJsonTypeMap.get(topic);
					if (topicConsumerList == null) {
						topicConsumerList = new ArrayList<>();
						topicConsumerMap.put(topic, topicConsumerList);
						isJsonTypeList = new ArrayList<>();
						isJsonTypeMap.put(topic, isJsonTypeList);
					}
					//判断是否已经包含相同的消息处理方法
					boolean isContained = false;
					for (MessageConsumer m : topicConsumerList) {
						if (m.getServiceObjectId().equals(messageConsumer.getServiceObjectId())
								&& m.getOperationId().equals(messageConsumer.getOperationId())) {
							isContained = true;
							break;
						}
					}
					//未包含时，加入列表
					if(!isContained) {
						topicConsumerList.add(messageConsumer);
						isJsonTypeList.add(isJsonType);
					}
				}
			}else {
				logger.error("invalid configuration! messageTopics serviceObjectId or operationId is null!");
			}
		});
		
		logger.info("{}-- init topic and consumer complete!", this.threadId());
		//两个对象都不为空时才需要处理
		if(!topicSet.isEmpty() && !topicConsumerMap.isEmpty()) {
			logger.info("{}-- subscribe topic: {}", this.threadId(), topicSet);
			kafkaConsumer.subscribe(topicSet);
			SecurityContextHolder.setAuthEnabled(false);
			while(!stopped) {
				ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					logger.info("{}-- process kafka message topic({}) key({}) msg: {}", this.threadId(), record.topic(), record.key(), record.value());
					List<MessageConsumer> topicConsumers = topicConsumerMap.get(record.topic());
					List<Boolean> isJsonTypeList = isJsonTypeMap.get(record.topic());
					//组装消息处理方法的入参
					JsonObject param = new JsonObject();
					param.addProperty(PARAM_TOPIC, record.topic());
					param.addProperty(PARAM_KEY, record.key());
					int index=0;
					for(MessageConsumer consumer : topicConsumers) {
						try {
							Boolean isJsonType = isJsonTypeList.get(index++);
							if(isJsonType) {
								param.add(PARAM_MSG, JsonUtils.toJsonObject(record.value()));
							}else {
								param.addProperty(PARAM_MSG, record.value());
							}
							ServiceCommand command = new ServiceCommand();
							command.setServiceId(consumer.getServiceObjectId());
							command.setOperationId(consumer.getOperationId());
							command.setSystemId(consumer.getSystemId());
							command.setReqData(param);
							command.setTenantId(consumer.getTenantId());
							ServiceProxy.getProxy().execute(command, null);
						} catch (Exception e) {
							logger.error("kafka message process failed!", e);
							logger.error("error message is: {}", JsonUtils.getString(param, PARAM_KEY));
						}
					}
				}
			}
		}
		
		if(this.kafkaConsumer != null) {
			this.kafkaConsumer.close();
			this.kafkaConsumer = null;
		}
		logger.info("{}-- consumer is stopped...", this.threadId());
		
	}
}
