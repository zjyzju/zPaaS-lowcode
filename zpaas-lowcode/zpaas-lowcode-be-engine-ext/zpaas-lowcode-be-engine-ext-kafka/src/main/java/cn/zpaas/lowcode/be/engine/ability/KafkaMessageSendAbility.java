package cn.zpaas.lowcode.be.engine.ability;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy Kafka消息发送能力
 */
public class KafkaMessageSendAbility {

	private static Logger logger = LoggerFactory.getLogger(KafkaMessageSendAbility.class);

	// KafkaMessageSendAbility实例，实现单例模式
	private static KafkaMessageSendAbility instance = null;
	// KafkaProducer的缓存对象，第一层Key是systemId，第二层Key是ftpId
	private Map<String, Map<String, KafkaProducer<String, String>>> kafkaProducerMap = new HashMap<>();

	
	// 私有化构造函数
	private KafkaMessageSendAbility() {

	}

	public static KafkaMessageSendAbility getInstance() {
		return instance;
	}

	/**
	 * KafkaMessageSendAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache KafkaProducer resource to Map...");
		}
		// 初始化KafkaMessageSendAbility实例
		KafkaMessageSendAbility repository = new KafkaMessageSendAbility();
		
		// 加载ServerResource数据-Kafka-producer
		List<ServerResource> serverResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_KAFKA_PRODUCER);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_KAFKA_PRODUCER);
		}

		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条Kafka-producer数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, KafkaProducer<String, String>> map = repository.kafkaProducerMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.kafkaProducerMap.put(serverResource.getSystemId(), map);
				}
				try {
					map.put(serverResource.getId(), initKafkaProducer(serverResource));
				}catch (Exception ex) {
					logger.error("init Kafka producer failed!", ex);
				}
				
			}
		} else {
			logger.info("no valid KafkaProducer ServerResource.");
		}
		
		// 缓存刷新时，释放原资源
		if (instance != null && instance.kafkaProducerMap != null && !instance.kafkaProducerMap.isEmpty()) {// 缓存刷新时，释放原资源
			instance.kafkaProducerMap.forEach((system, map) -> {
				if (map != null && !map.isEmpty()) {
					map.forEach((resourceId, kafkaProducer) -> {
						kafkaProducer.close();
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
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache KafkaProducer resource to Map...");
		}
		// 缓存刷新时，释放原资源
		Map<String, KafkaProducer<String, String>> oldMap = instance.kafkaProducerMap.get(systemId);
		if (oldMap != null && !oldMap.isEmpty()) {
			oldMap.forEach((resourceId, kafkaProducer) -> {
				kafkaProducer.close();
			});
		}
		// 加载ServerResource数据-Kafka-producer
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_KAFKA_PRODUCER);
		Map<String, KafkaProducer<String, String>> map = new HashMap<>();
		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条Kafka-producer数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				try {
					map.put(serverResource.getId(), initKafkaProducer(serverResource));
				}catch (Exception ex) {
					logger.error("init Kafka producer failed!", ex);
				}
				
			}
		} 
		instance.kafkaProducerMap.put(systemId, map);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据配置信息，初始化KafkaProducer
	 * @param serverResource
	 * @return
	 */
	private static KafkaProducer<String, String> initKafkaProducer(ServerResource serverResource) {
		if(serverResource == null || serverResource.getUrl() == null || serverResource.getUrl().trim().length() == 0) {
			logger.error("bootstrap.servers of kafka cann't be null!");
			return null;
		}
		
		Properties properties = new Properties();
		// bootstrap.servers kafka集群地址
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverResource.getUrl());
		
		//当用户名和密码都不为空时，增加SASL_PLAINTEXT鉴权机制
		if(serverResource.getUsername() != null && serverResource.getUsername().trim().length() > 0 &&
				serverResource.getPassword() != null && serverResource.getPassword().trim().length() > 0) {
			properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
			properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
			properties.put("sasl.jaas.config",
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + 
							serverResource.getUsername() + "\" password=\"" + serverResource.getPassword() + "\";");
		}
		
		//当服务器配置信息不为空时，加入配置信息
		if(serverResource.getServerCfg() != null && serverResource.getServerCfg().trim().length() > 0) {
			JsonObject cfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
			cfg.keySet().forEach(key-> {
				properties.put(key, JsonUtils.getObject(cfg, key));
			});
			
			// key.serializer 消息key序列化方式，未设置时
        	if(!cfg.has(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG))  {
        		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        	}
        	// value.serializer 消息体序列化方式，未设置时
        	if(!cfg.has(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)) {
        		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        	}
		}else {
			properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		}
		return new KafkaProducer<>(properties);
	}
	
	/**
	 * 根据systemId和KafkaProducer资源Id获取KafkaProducer客户端实例
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 */
	public KafkaProducer<String, String> getKafkaProducer(String systemId, String resourceId) {
		Map<String, KafkaProducer<String, String>> map = kafkaProducerMap.get(systemId);
		if(map != null) {
			return map.get(resourceId);
		}else {
			return null;
		}
	}
	
	/**
	 * 发送kafka消息
	 * @param topic
	 * @param key
	 * @param msg
	 * @param systemId
	 * @param resourceId
	 * @throws EngineException
	 */
	public void send(String topic, String key, String msg, String systemId, String resourceId) throws EngineException {
		//获取KafkaProducer实例
		KafkaProducer<String, String> sender = this.getKafkaProducer(systemId, resourceId);
		if(sender == null) {
			logger.error("KafkaProducer is null! invalid serverResourceId:{}", resourceId);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "KafkaProducer is null! invalid serverResourceId!");
		}
		if(topic == null || topic.trim().length() == 0 || msg == null || msg.trim().length() == 0) {
			logger.error("topic and msg can't be null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "topic and msg can't be null!");
		}
		
		//初始化消息体
		ProducerRecord<String, String> record = null;
		if(key != null && key.trim().length() > 0) {
			record = new ProducerRecord<String, String>(topic, key, msg);
		}else {
			record = new ProducerRecord<String, String>(topic, msg);
		}
		
		//发送消息
		sender.send(record);
	}
}
