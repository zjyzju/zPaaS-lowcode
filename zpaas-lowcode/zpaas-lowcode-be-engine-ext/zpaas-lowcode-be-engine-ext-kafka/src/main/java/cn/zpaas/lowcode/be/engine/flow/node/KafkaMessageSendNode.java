package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.KafkaMessageSendAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;


/**
 * @author zjy
 * KafkaMessageSendNode是Kafka消息发送节点的实现类，主要提供基于Kafka的异步消息发送能力。
 */
public class KafkaMessageSendNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(KafkaMessageSendNode.class);

	private static final String CFG_KAFKA_PRODUCER_RESOURCE_ID_KEY = "kafkaProducerId"; //kafkaProducerId存放的Key
	
	private static final String CFG_TOPIC_SOURCE_KEY = "topicSource"; //消息主题的来源存放的Key
	private static final String CFG_TOPIC_KEY_KEY = "topicKey"; //消息主题的Key值存放的Key
	private static final String CFG_TOPIC_ATTR_KEY = "topicAttr"; //消息主题属性存放的Key
	
	private static final String CFG_KEY_SOURCE_KEY = "keySource"; //消息key的来源存放的Key
	private static final String CFG_KEY_KEY_KEY = "keyKey"; //消息key的Key值存放的Key
	private static final String CFG_KEY_ATTR_KEY = "keyAttr"; //消息key属性存放的Key
	
	private static final String CFG_MSG_SOURCE_KEY = "msgSource"; //消息内容的来源存放的Key
	private static final String CFG_MSG_KEY_KEY = "msgKey"; //消息内容的Key值存放的Key
	private static final String CFG_MSG_ATTR_KEY = "msgAttr"; //消息内容属性存放的Key
	
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		
		String kafkaProducerResourceId = JsonUtils.getString(nodeCfg, CFG_KAFKA_PRODUCER_RESOURCE_ID_KEY);
		
		String topicSource = JsonUtils.getString(nodeCfg, CFG_TOPIC_SOURCE_KEY);
		String topicKey = JsonUtils.getString(nodeCfg, CFG_TOPIC_KEY_KEY);
		String topicAttr = JsonUtils.getString(nodeCfg, CFG_TOPIC_ATTR_KEY);
		if (!StringUtils.isBlank(topicAttr)) {
			topicAttr = this.dynamicAttrProcess(topicAttr, businessFlowNode, context);
		}
		
		String keySource = JsonUtils.getString(nodeCfg, CFG_KEY_SOURCE_KEY);
		String keyKey = JsonUtils.getString(nodeCfg, CFG_KEY_KEY_KEY);
		String keyAttr = JsonUtils.getString(nodeCfg, CFG_KEY_ATTR_KEY);
		if (!StringUtils.isBlank(keyAttr)) {
			keyAttr = this.dynamicAttrProcess(keyAttr, businessFlowNode, context);
		}
		
		String msgSource = JsonUtils.getString(nodeCfg, CFG_MSG_SOURCE_KEY);
		String msgKey = JsonUtils.getString(nodeCfg, CFG_MSG_KEY_KEY);
		String msgAttr = JsonUtils.getString(nodeCfg, CFG_MSG_ATTR_KEY);
		if (!StringUtils.isBlank(msgAttr)) {
			msgAttr = this.dynamicAttrProcess(msgAttr, businessFlowNode, context);
		}
		
		//kafkaProducerResourceId不能为空
		if(StringUtils.isBlank(kafkaProducerResourceId)) {
			logger.error("T[{}] kafkaProducerResourceId can't be empty.", businessFlowNode.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "kafkaProducerResourceId can't be empty.");
		}
		
		if(StringUtils.isBlank(topicSource) || StringUtils.isBlank(topicKey)) {
			logger.error("T[{}] topicSource and topicKey can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "topicSource and topicKey can't be empty. ");
		}
		
		if(StringUtils.isBlank(msgSource)) {
			logger.error("T[{}] msgSource can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "msgSource can't be empty. ");
		}
		
		//从上下文获取消息主题
		Object topicObject = getContextObject(topicSource, topicKey, false, topicAttr, context, businessFlowNode);
		if(topicObject == null || !(topicObject instanceof String)) {
			logger.error("T[{}] topic is null or topic is not String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "topic is null or topic is not String. ");
		}
		String topic = (String) topicObject;
		//从上下文获取消息内容
		Object msgObject = getContextObject(msgSource, msgKey, false, msgAttr, context, businessFlowNode);
		if(msgObject == null) {
			logger.error("T[{}] msg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "msg is null. ");
		}
		String msg = null;
		if(msgObject instanceof String) {
			msg = (String)msgObject;
		}else {
			msg = JsonUtils.toJson(msgObject);
		}
		//从上下文获取消息key
		String key = null;
		if(!StringUtils.isBlank(keySource) && !StringUtils.isBlank(keyKey)) {
			Object keyObject = getContextObject(keySource, keyKey, false, keyAttr, context, businessFlowNode);
			if(keyObject != null) {
				key = keyObject.toString();
			}
		}
		
		//发送异步消息
		KafkaMessageSendAbility.getInstance().send(topic, key, msg, businessFlowNode.getSystemId(), kafkaProducerResourceId);
		
		//将目标对象保存到节点处理结果中
		//context.setAttribute(NODE_RESULT_KEY, result);		
	}
}
