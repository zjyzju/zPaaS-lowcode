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
