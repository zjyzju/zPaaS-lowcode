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
 * @author zjy 
动态属性取值节点的实现类，主要实现从已经存存的对象中获取属性值，支持动态和静态两种方式：
动态方式时，表示属性的配置信息为另一个变量；
静态方式时，表示属性的配置信息为固定值
 */
public class DynamicGetterNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(DynamicGetterNode.class);

	private static final String CFG_IS_LIST_TYPE_KEY = "isListType"; //是否列表类型存放的Key
	private static final String CFG_FROM_OBJECT_SOURCE_KEY = "fromObjectSource"; //源对象实例的来源存放的Key
	private static final String CFG_FROM_OBJECT_KEY_KEY = "fromObjectKey"; //源对象实例的Key值存放的Key
//	private static final String CFG_FROM_OBJECT_TYPE_KEY = "fromObjectType"; //源对象的类型存放的Key
//	private static final String CFG_FROM_OBJECT_CLASS_KEY = "fromObjectClass"; //源对象的类存放的Key
	
	private static final String CFG_FROM_OBJECT_ATTR_SOURCE_KEY = "fromObjectAttrSource"; //源对象实例属性的来源存放的Key
	private static final String CFG_FROM_OBJECT_ATTR_KEY_KEY = "fromObjectAttrKey"; //源对象实例属性的Key值存放的Key
	private static final String CFG_FROM_OBJECT_ATTR_ATTR_KEY = "fromObjectAttrAttr"; //源对象实例属性对应对象属性存放的Key
		
	
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
		String fromObjectSource = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_SOURCE_KEY);
		String fromObjectKey = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_KEY_KEY);
//		String fromObjectType = nodeCfg, CFG_FROM_OBJECT_TYPE_KEY);
//		String fromObjectClass = nodeCfg, CFG_FROM_OBJECT_CLASS_KEY);
		
		String fromObjectAttrSource = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_ATTR_SOURCE_KEY);
		String fromObjectAttrKey = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_ATTR_KEY_KEY);
		String fromObjectAttrAttr = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_ATTR_ATTR_KEY);
		
		if(StringUtils.isBlank(fromObjectSource) || StringUtils.isBlank(fromObjectKey)) {
			logger.error("T[{}] fromObjectSource and fromObjectKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObjectSource and fromObjectKey can't be null. ");
		}
		
		//获取源对象实例
		Object fromObject = getContextObject(fromObjectSource, fromObjectKey, isListType, null, context, businessFlowNode);
		if (fromObject == null) {
			logger.error("T[{}] fromObject can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject can't be null. ");
		}
		
		Object valueObject  = null;
		
		if(StringUtils.isBlank(fromObjectAttrSource) || StringUtils.isBlank(fromObjectAttrKey)) {
			valueObject = fromObject;
		}else {		
			String fromObjectAttr = null;
			//获取源对象实例属性的值
			Object fromObjectAttrObject = getContextObject(fromObjectAttrSource, fromObjectAttrKey, false, fromObjectAttrAttr, context, businessFlowNode);			
			try {
				fromObjectAttr =  String.valueOf(fromObjectAttrObject);
			} catch (Exception e) {
				logger.error("T[{}] fromObjectAttr's Value must be String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObjectAttr's Value must be String. ",e.getMessage(),e);
			}
			if(StringUtils.isBlank(fromObjectAttr)) {
				logger.error("T[{}] fromObjectAttr can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObjectAttr can't be null. ");
			}
			//获取目标属性值
			valueObject = JsonUtils.eval(fromObject, fromObjectAttr);
		}
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, valueObject);
	}

}
