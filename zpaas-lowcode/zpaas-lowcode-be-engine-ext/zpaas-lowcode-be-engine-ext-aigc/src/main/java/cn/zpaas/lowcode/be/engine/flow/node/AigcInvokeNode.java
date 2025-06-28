package cn.zpaas.lowcode.be.engine.flow.node;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.AigcAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * AigcInvokeNode是Aigc能力调用节点的实现类，主要用于实现Aigc能力的调用
 */
public class AigcInvokeNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(AigcInvokeNode.class);

	private static final String CFG_AIGC_RESOURCE_ID_KEY = "aigcResourceId"; //aigcResourceId存放的Key
	private static final String CFG_MODEL_KEY = "model"; //模型名称存放的Key
	private static final String CFG_INVOKE_API_KEY = "invokeApi"; //调用API类型存放的Key
	private static final String CFG_IS_STREAM_KEY = "isStream"; //是否流式存放的Key
	private static final String CFG_OPTIONS_KEY = "options"; //模型参数存放的Key
	
	private static final String CFG_MESSAGE_SOURCE_KEY = "messageSource"; //消息来源存放的Key
	private static final String CFG_MESSAGE_KEY_KEY = "messageKey"; //消息Key值存放的Key
	private static final String CFG_MESSAGE_ATTR_KEY = "messageAttr"; //当消息是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_HISTORY_SOURCE_KEY = "historySource"; //历史消息来源存放的Key
	private static final String CFG_HISTORY_KEY_KEY = "historyKey"; //历史消息的Key值存放的Key
	private static final String CFG_HISTORY_ATTR_KEY = "historyAttr"; //当历史消息是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_USERNAME_SOURCE_KEY = "userNameSource"; //用户名来源存放的Key
	private static final String CFG_USERNAME_KEY_KEY = "userNameKey"; //用户名的Key值存放的Key
	private static final String CFG_USERNAME_ATTR_KEY = "userNameAttr"; //当用户名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_ASSISTANT_NAME_SOURCE_KEY = "assistantNameSource"; //助手名来源存放的Key
	private static final String CFG_ASSISTANT_NAME_KEY_KEY = "assistantNameKey"; //助手名的Key值存放的Key
	private static final String CFG_ASSISTANT_NAME_ATTR_KEY = "assistantNameAttr"; //当助手名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String DEFAULT_STREAM_TYPE = "N";
	private static final String DEFAULT_OPTIONS = """
			{
                "temperature": 0.7,
                "max_tokens": 100
            }
			""";

		
	
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
		
		String aigcResourceId = JsonUtils.getString(nodeCfg, CFG_AIGC_RESOURCE_ID_KEY);
		String model = JsonUtils.getString(nodeCfg, CFG_MODEL_KEY);
		String invokeApi = JsonUtils.getString(nodeCfg, CFG_INVOKE_API_KEY);
		String isStream = JsonUtils.getString(nodeCfg, CFG_IS_STREAM_KEY);
		String options = JsonUtils.getString(nodeCfg, CFG_OPTIONS_KEY);
		
		String messageSource = JsonUtils.getString(nodeCfg, CFG_MESSAGE_SOURCE_KEY);
		String messageKey = JsonUtils.getString(nodeCfg, CFG_MESSAGE_KEY_KEY);
		String messageAttr = JsonUtils.getString(nodeCfg, CFG_MESSAGE_ATTR_KEY);
		if (!StringUtils.isBlank(messageAttr)) {
			messageAttr = this.dynamicAttrProcess(messageAttr, businessFlowNode, context);
		}
		
		String historySource = JsonUtils.getString(nodeCfg, CFG_HISTORY_SOURCE_KEY);
		String historyKey = JsonUtils.getString(nodeCfg, CFG_HISTORY_KEY_KEY);
		String historyAttr = JsonUtils.getString(nodeCfg, CFG_HISTORY_ATTR_KEY);
		if (!StringUtils.isBlank(historyAttr)) {
			historyAttr = this.dynamicAttrProcess(historyAttr, businessFlowNode, context);
		}

		String userNameSource = JsonUtils.getString(nodeCfg, CFG_USERNAME_SOURCE_KEY);
		String userNameKey = JsonUtils.getString(nodeCfg, CFG_USERNAME_KEY_KEY);
		String userNameAttr = JsonUtils.getString(nodeCfg, CFG_USERNAME_ATTR_KEY);
		if (!StringUtils.isBlank(userNameAttr)) {
			userNameAttr = this.dynamicAttrProcess(userNameAttr, businessFlowNode, context);
		}

		String assistantNameSource = JsonUtils.getString(nodeCfg, CFG_ASSISTANT_NAME_SOURCE_KEY);
		String assistantNameKey = JsonUtils.getString(nodeCfg, CFG_ASSISTANT_NAME_KEY_KEY);
		String assistantNameAttr = JsonUtils.getString(nodeCfg, CFG_ASSISTANT_NAME_ATTR_KEY);
		if (!StringUtils.isBlank(assistantNameAttr)) {
			assistantNameAttr = this.dynamicAttrProcess(assistantNameAttr, businessFlowNode, context);
		}
		
		//配置参数合法性校验
		if(StringUtils.isBlank(aigcResourceId) || StringUtils.isBlank(model)) {
			logger.error("T[{}] aigcResourceId and model can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "aigcResourceId and model can't be null. ");
		}
		if(StringUtils.isBlank(messageSource) || StringUtils.isBlank(messageKey)) {
			logger.error("T[{}] messageSource and messageKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "messageSource and messageKey can't be null. ");
		}
		if(StringUtils.isBlank(invokeApi)) {
			invokeApi = AigcAbility.GENERATE_API;
		}
		if(StringUtils.isBlank(isStream)) {
			isStream = DEFAULT_STREAM_TYPE;
		}
		if(StringUtils.isBlank(options)) {
			options = DEFAULT_OPTIONS;
		}
		if(AigcAbility.CHAT_API.equals(invokeApi) && (StringUtils.isBlank(historySource) || StringUtils.isBlank(historyKey))) {
			logger.error("T[{}] when chat, historySource and historyKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when chat, historySource and historyKey can't be null. ");
		}		
		
		//获取消息
		String message = (String)getContextObject(messageSource, messageKey, false, messageAttr, context, businessFlowNode);
		if(StringUtils.isBlank(message)) {
			logger.error("T[{}] message can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "message can't be null. ");
		}

		JsonArray historyMsgs = (JsonArray)getContextObject(historySource, historyKey, true, historyAttr, context, businessFlowNode);
		
		String userName = null;
		if(!StringUtils.isBlank(userNameSource) && !StringUtils.isBlank(userNameKey)) {
			userName = (String)getContextObject(userNameSource, userNameKey, false, userNameAttr, context, businessFlowNode);
		}
		String assistantName = null;
		if(!StringUtils.isBlank(assistantNameSource) && !StringUtils.isBlank(assistantNameKey)) {
			assistantName = (String)getContextObject(assistantNameSource, assistantNameKey, false, assistantNameAttr, context, businessFlowNode);
		}
		
		//返回结果
		Object result = AigcAbility.getInstance().invokeAigc(businessFlowNode.getSystemId(), aigcResourceId, model, invokeApi, isStream, options, message, historyMsgs, context.getSseEmitter(), userName, assistantName);

		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
