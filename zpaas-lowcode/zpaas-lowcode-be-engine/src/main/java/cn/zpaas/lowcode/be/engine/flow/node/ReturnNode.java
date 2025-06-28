package cn.zpaas.lowcode.be.engine.flow.node;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * ReturnNode是返回节点的实现类，主要用于设置子业务流的返回值并中断业务流执行
 */
public class ReturnNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ReturnNode.class);

	private static final String CFG_IS_LIST_TYPE_KEY = "isListType"; //是否列表类型存放的Key
	private static final String CFG_RETURN_OBJECT_INSTANCE_SOURCE_KEY = "returnObjectInstanceSource"; //返回对象实例的来源存放的Key
	private static final String CFG_RETURN_OBJECT_INSTANCE_KEY_KEY = "returnObjectInstanceKey"; //返回对象实例的Key值存放的Key
	private static final String CFG_RETURN_OBJECT_INSTANCE_ATTR_KEY = "returnObjectInstanceAttr"; //返回对象实例对应对象属性存放的Key
	
	public static final String CFG_BUSINESS_FLOW_RETURN_KEY = "businessFlowReturn";//流程中断标志
		
	
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
		boolean isListType = JsonUtils.getBoolean(nodeCfg, CFG_IS_LIST_TYPE_KEY);
		String returnObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_RETURN_OBJECT_INSTANCE_SOURCE_KEY);
		String returnObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_RETURN_OBJECT_INSTANCE_KEY_KEY);
		String returnObjectInstanceAttr = JsonUtils.getString(nodeCfg, CFG_RETURN_OBJECT_INSTANCE_ATTR_KEY);
		boolean isListResult = JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
		if(StringUtils.isBlank(returnObjectInstanceSource)) {
			logger.error("T[{}] returnObjectInstanceSource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "returnObjectInstanceSource can't be null. ");
		}

		// 动态属性的情况
		if (!StringUtils.isBlank(returnObjectInstanceAttr)) {
			returnObjectInstanceAttr = this.dynamicAttrProcess(returnObjectInstanceAttr, businessFlowNode, context);
		}
		
		//获取返回对象实例
		Object returnObject = getContextObject(returnObjectInstanceSource, returnObjectInstanceKey, isListType, returnObjectInstanceAttr, context, businessFlowNode);
		
		if(!isListResult && returnObject != null && returnObject instanceof JsonArray) {//当返回结果不是列表但returnObject是列表时，返回第一个元素
			JsonArray array = (JsonArray) returnObject;
			if(array.isEmpty()) {
				returnObject = null;
			}else {
				returnObject = JsonUtils.toObject(array.get(0));
			}
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, returnObject);
		//设置业务流中断标志
		context.setAttribute(CFG_BUSINESS_FLOW_RETURN_KEY, true);
	}

}
