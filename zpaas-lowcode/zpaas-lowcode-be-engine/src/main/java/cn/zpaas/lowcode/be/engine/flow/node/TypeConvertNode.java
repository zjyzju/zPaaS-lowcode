package cn.zpaas.lowcode.be.engine.flow.node;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 参数转换节点的实现类
 */
public class TypeConvertNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(TypeConvertNode.class);

	private static final String CFG_TO_TYPE_KEY = "toType"; //目标对象的类型存放的Key
	
	private static final String CFG_FROM_OBJECT_INSTANCE_SOURCE_KEY = "fromObjectInstanceSource"; //源对象实例的来源存放的Key
	private static final String CFG_FROM_OBJECT_INSTANCE_KEY_KEY = "fromObjectInstanceKey"; //源对象实例的Key值存放的Key
	private static final String CFG_FROM_OBJECT_INSTANCE_ATTR_KEY = "fromObjectInstanceAttr"; //源对象实例对应对象属性存放的Key
	
	private static final String CFG_TO_TYPE_STRING = "S";//String
	private static final String CFG_TO_TYPE_JSON = "J";//JSON
		
	
	
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
		
		String toType = JsonUtils.getString(nodeCfg, CFG_TO_TYPE_KEY);
		String fromObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_SOURCE_KEY);
		String fromObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_KEY_KEY);
		String fromObjectInstanceAttr = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_ATTR_KEY);
		if (!StringUtils.isBlank(fromObjectInstanceAttr)) {
			fromObjectInstanceAttr = this.dynamicAttrProcess(fromObjectInstanceAttr, businessFlowNode, context);
		}
		boolean isListResult = JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
		if(StringUtils.isBlank(toType) || StringUtils.isBlank(fromObjectInstanceSource) || StringUtils.isBlank(fromObjectInstanceKey)) {
			logger.error("T[{}] toType fromObjectInstanceSource and fromObjectInstanceKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "toType fromObjectInstanceSource and fromObjectInstanceKey can't be null. ");
		}
		
		//获取源对象实例
		Object fromObject = getContextObject(fromObjectInstanceSource, fromObjectInstanceKey, false, fromObjectInstanceAttr, context, businessFlowNode);
		
		
		//获取或实例化目标对象实例
		Object toObject = null;
		
		if(fromObject != null) {
			//新建目标对象实例
			if(CFG_TO_TYPE_STRING.equals(toType)) {
				toObject = JsonUtils.toJson(fromObject);
			}else if(CFG_TO_TYPE_JSON.equals(toType)) {
				if(!(fromObject instanceof String)) {
					logger.error("T[{}] fromObject's type must be String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject's type must be String.");
				}
				if(isListResult) {
					toObject = JsonUtils.toJsonArray((String)fromObject);
				}else {
					toObject = JsonUtils.toJsonObject((String)fromObject);
				}
			} else {//获取目标对象实例
				logger.error("T[{}] invalid toType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid toType.");
			}
		}
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, toObject);
	}

}
