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
		
	/*
	 * { 
	 * 	isListType: 是否列表类型，true/false
	 * 
	 * 	returnObjectInstanceSource：返回对象实例的来源，包括：P（过程数据）；D（领域对象）；I（输入参数）；N（预处理产生的nodeParams）；
	 * 			O（业务流的属主领域对象）；F（固定的值）；
	 * 	returnObjectInstanceKey：返回对象实例的Key值，当返回对象的来源为I（输入参数）时，表示输入参数中的Key；
	 * 			为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据conditionObjectIsList的值进行区分）中的key值；
	 * 			为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）或O（业务流的属主领域对象）时，Key值无效；
	 * 			为F（固定值）时，该值为具体的值
	 *  returnObjectInstanceAttr：当返回对象是指定对象的某个属性时有效，通过该字段指定对应属性的code。
	 * 	isListResult：节点执行结果是否是List类型，包括：true/false
	 * 	nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
	 * 	nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）
	 * 					或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构 
	 * }
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
