package cn.zpaas.lowcode.be.engine.flow.node;


import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.SmsSendAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * SmsSendNode是短信发送节点的实现类，主要用于实现短信的发送能力。
 */
public class SmsSendNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(SmsSendNode.class);

	private static final String CFG_SMS_GATEWAY_RESOURCE_ID = "smsGatewayId";//短信网关资源标识
	
	private static final String CFG_SMS_SEND_RECEIVER_SOURCE = "receiverSource";//发件人
	private static final String CFG_SMS_SEND_RECEIVER_KEY = "receiverKey";//发件人
	private static final String CFG_SMS_SEND_RECEIVER_ATTR = "receiverAttr";//发件人
	private static final String CFG_SMS_SEND_CONTENT_SOURCE = "contentSource";//短信内容
	private static final String CFG_SMS_SEND_CONTENT_KEY = "contentKey";//短信内容
	private static final String CFG_SMS_SEND_CONTENT_ATTR = "contentAttr";//短信内容
	
	private static final String CFG_MAIL_SEND_CONTENT_PARAMS = "contentParams";//短信内容参数列表
	private static final String CFG_MAIL_SEND_PARAM_ID = "paramId";//参数标识
	private static final String CFG_MAIL_SEND_PARAM_SOURCE = "paramSource";//参数值
	private static final String CFG_MAIL_SEND_PARAM_KEY = "paramKey";//参数值
	private static final String CFG_MAIL_SEND_PARAM_ATTR = "paramAttr";//参数值
	
	private static final String LEFT_$_BRACES = "\\$\\{";
	private static final String RIGHT_BRACES = "\\}";
	
	/*
	 {
		smsGatewayId：短信网关资源标识

		receiverSource：收信人的来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；N（预处理产生的nodeParams）；F（固定值）。当多个收集人时，使用逗号分隔。
		receiverKey：收信人的Key值，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据isListType的值进行区分）中的key值；为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）时，Key值无效；当为F（固定值）时，为具体的值
		receiverAttr：当收信人是指定对象的某个属性时有效，通过该字段指定对应属性的code，支持JSONPath，源对象实例属性的值只能是字符串或数字。

		contentSource：短信内容的来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；N（预处理产生的nodeParams）；F（固定值）
		contentKey：短信内容的Key值，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据isListType的值进行区分）中的key值；为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）时，Key值无效；当为F（固定值）时，为具体的值
		contentAttr：当短信内容是指定对象的某个属性时有效，通过该字段指定对应属性的code，支持JSONPath，源对象实例属性的值只能是字符串或数字。

		contentParams：[//短信内容参数列表
			{
				paramId: 参数序号，数字类型，与短信中${1}、${2}、......、${n}占位符对应。短信发送时使用具体的值替换对应的占位符。
				paramSource：参数值的来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；N（预处理产生的nodeParams）；F（固定值）
				paramKey：参数值的Key值，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据srcFileIsList的值进行区分）中的key值；为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）时，Key值无效；当为F（固定值）时，为具体的值
				paramAttr：当参数值是指定对象的某个属性时有效，通过该字段指定对应属性的code，支持JSONPath，源对象实例属性的值只能是字符串或数字。
			},
		]

		isListResult：节点执行结果是否是List类型，包括：true/false
		nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
		nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构
	}
	 */
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
		
		//处理短信网关服务资源标识信息
		String smsGatewayId = JsonUtils.getString(nodeCfg, CFG_SMS_GATEWAY_RESOURCE_ID);
		if (StringUtils.isBlank(smsGatewayId)) {
			logger.error("T[{}] smsGatewayId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "smsGatewayId can't be null. ");
		}
		
		//处理收信人信息
		String receiverSource = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_RECEIVER_SOURCE);
		String receiverKey = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_RECEIVER_KEY);
		String receiverAttr = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_RECEIVER_ATTR);
		if (!StringUtils.isBlank(receiverAttr)) {
			receiverAttr = this.dynamicAttrProcess(receiverAttr, businessFlowNode, context);
		}
		// 配置参数合法性校验
		if (StringUtils.isBlank(receiverSource) || StringUtils.isBlank(receiverKey)) {
			logger.error("T[{}] receiverSource and receiverKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "receiverSource and receiverKey can't be null. ");
		}
		//获取发件人信息
		String receiver = (String)getContextObject(receiverSource, receiverKey, false, receiverAttr, context, businessFlowNode);
		if(StringUtils.isBlank(receiver)) {
			logger.error("T[{}] receiver can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "receiver can't be null. ");
		}
		
		// 处理短信内容信息
		String contentSource = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_CONTENT_SOURCE);
		String contentKey = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_CONTENT_KEY);
		String contentAttr = JsonUtils.getString(nodeCfg, CFG_SMS_SEND_CONTENT_ATTR);
		if (!StringUtils.isBlank(contentAttr)) {
			contentAttr = this.dynamicAttrProcess(contentAttr, businessFlowNode, context);
		}
		String content = "";
		if (!StringUtils.isBlank(contentSource) && !StringUtils.isBlank(contentKey)) {
			// 获取短信内容信息
			content = (String) getContextObject(contentSource, contentKey, false, contentAttr, context, businessFlowNode);
			if (!StringUtils.isBlank(content)) {
				//处理短信内容参数值的替换
				JsonObject contentParam = null;
				String paramId = null;
				String paramSource = null;
				String paramKey = null;
				String paramAttr = null;
				String paramValue = null;
				JsonArray contentParams = JsonUtils.getJsonArray(nodeCfg, CFG_MAIL_SEND_CONTENT_PARAMS);
				if(contentParams != null && !contentParams.isEmpty()) {
					for(int i=0; i<contentParams.size(); i++) {
						contentParam = contentParams.get(i).getAsJsonObject();
						if(!JsonUtils.isEmpty(contentParam)) {
							paramId = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_ID);//参数ID
							paramSource = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_SOURCE);//参数值来源
							paramKey = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_KEY);//参数值Key
							paramAttr = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_ATTR);//参数值属性
							if (!StringUtils.isBlank(contentAttr)) {
								contentAttr = this.dynamicAttrProcess(contentAttr, businessFlowNode, context);
							}
							if(StringUtils.isBlank(paramId) || StringUtils.isBlank(paramSource) || StringUtils.isBlank(paramKey)) {
								logger.error("T[{}] paramId paramSource or paramKey is null! continue...", businessFlowNode.getTenantId());
								continue;
							}
							//获取参数值
							paramValue = (String) getContextObject(paramSource, paramKey, false, paramAttr, context, businessFlowNode);
							if(!StringUtils.isBlank(paramValue)) {
								content = content.replaceAll(LEFT_$_BRACES+(paramId)+RIGHT_BRACES, Matcher.quoteReplacement(paramValue));
							}
						}
					}
				}
			}
		}
	
		SmsSendAbility.getInstance().sendSMS(receiver, content,  businessFlowNode.getSystemId(), smsGatewayId);
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, null);
	}

}
