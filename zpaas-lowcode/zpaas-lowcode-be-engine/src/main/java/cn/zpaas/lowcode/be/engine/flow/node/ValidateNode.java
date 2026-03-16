package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.ValidateAbility;
import cn.zpaas.lowcode.be.engine.ability.validator.Validator;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 参数校验节点的实现类，主要实现对领域对象或值传递对象属性值的合法性校验。
 */
public class ValidateNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ValidateNode.class);

	private static final String CFG_IS_LIST_TYPE_KEY = "isListType"; //是否列表类型存放的Key
	private static final String CFG_TARGET_OBJECT_INSTANCE_SOURCE_KEY = "targetObjectInstanceSource"; //目标对象实例的来源存放的Key
	private static final String CFG_TARGET_OBJECT_INSTANCE_KEY_KEY = "targetObjectInstanceKey"; //目标对象实例的Key值存放的Key
	private static final String CFG_TARGET_OBJECT_INSTANCE_ATTR_KEY = "targetObjectInstanceAttr"; //目标对象实例的Attr值存放的Key
	private static final String CFG_TARGET_OBJECT_TYPE_KEY = "targetObjectType"; //目标对象的类型存放的Key
	private static final String CFG_TARGET_OBJECT_CLASS_KEY = "targetObjectClass"; //目标对象的类存放的Key
	private static final String CFG_VALIDATE_RULE_GROUP_ID_KEY = "ruleGroupId"; //校验规则组标识存放的Key
	private static final String CFG_INTERRUPT_BUSINESS_FLOW_KEY = "interruptBusinessFlow"; //是否中断业务流执行存放的Key
	
	
	/*
	 * { isListType: 需要校验的目标数据对象是否列表类型，true/false
	 * 
	 * targetObjectInstanceSource：目标数据对象实例的来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；F（固定值）；N（预处理产生的nodeParams）
	 * targetObjectInstanceKey：目标数据对象实例的Key值，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，
	 * 				表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据isListType的值进行区分）中的key值；
	 * 				为P（过程数据）时，该值表示context.values中的key值，
	 * 				当为N（预处理产生的nodeParams）时，Key值无效 
	 * targetObjectInstanceAttr：当目标数据对象实例是指定对象的某个属性时有效，通过该字段指定对应属性的code，支持JSONPath，源对象实例属性的值只能是字符串或数字，当以“$”开头时（先处理前面等于“$”的情况，再考虑以“$”开头的情况），表示该属性的值是动态取值的情况，只支持从过程数据中获取，如“$user”,表示从过程数据中获取user属性的对象，“$user.id”,表示取过程数据中user属性对象id属性的值，该情况主要支持目标对象是Map的情况（Map的key值是动态确定的）。
	 * targetObjectType：目标数据对象的类型，包括：D（领域对象），R（值传递对象）
	 * targetObjectClass：目标数据对象的类，对应领域对象或值传递对象的标识
	 * 
	 * ruleGroupId：校验规则组标识，包括校验规则的配置信息
	 * interruptBusinessFlow：是否中断业务流执行，true/false
	 * 
	 * isListResult：节点执行结果是否是List类型，包括：true/false
	 * nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
	 * nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）
	 * 						或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构 }
	 */
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException  {
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
		String targetObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_INSTANCE_SOURCE_KEY);
		String targetObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_INSTANCE_KEY_KEY);
		String targetObjectInstanceAttr = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_INSTANCE_ATTR_KEY);
		String targetObjectType = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_TYPE_KEY);
		String targetObjectClass = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_CLASS_KEY);
		String ruleGroupId = JsonUtils.getString(nodeCfg, CFG_VALIDATE_RULE_GROUP_ID_KEY);
		boolean interruptBusinessFlow = JsonUtils.getBoolean(nodeCfg, CFG_INTERRUPT_BUSINESS_FLOW_KEY);
		
		if(StringUtils.isBlank(targetObjectInstanceSource) || StringUtils.isBlank(targetObjectInstanceKey)) {
			logger.error("T[{}] targetObjectInstanceSource and targetObjectInstanceKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "targetObjectInstanceSource and targetObjectInstanceKey can't be null. ");
		}
		
		if(StringUtils.isBlank(targetObjectType) || StringUtils.isBlank(targetObjectClass)) {
			logger.error("T[{}] targetObjectType and targetObjectClass can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "targetObjectType and targetObjectClass can't be null. ");
		}
		
		if(StringUtils.isBlank(ruleGroupId)) {
			logger.error("T[{}] ruleGroupId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ruleGroupId can't be null. ");
		}
		// 动态属性的情况
		if (!StringUtils.isBlank(targetObjectInstanceAttr)) {
			targetObjectInstanceAttr = this.dynamicAttrProcess(targetObjectInstanceAttr, businessFlowNode, context);
		}
		
		//获取源对象实例
		Object targetObject = getContextObject(targetObjectInstanceSource, targetObjectInstanceKey, isListType, targetObjectInstanceAttr, context, businessFlowNode);
		
		//目标校验对象为空时，不进行校验
		if(targetObject == null) {
			return;
		}
		
		JsonObject result = null;
		if(isListType) {//列表类型
			if(! (targetObject instanceof JsonArray)) {
				logger.error("T[{}] targetObject must be JsonArray. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "targetObject must be JsonArray");
			}
			JsonArray targetArray = (JsonArray) targetObject;
			int size = targetArray.size();
			if(size == 0) {
				return;
			}
			result = ValidateAbility.getInstance().validate(context.getSystemId(), ruleGroupId, targetArray, null);
		}else {//非列表类型
			if(! (targetObject instanceof JsonObject)) {
				logger.error("T[{}] targetObject must be JsonObject. type is {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), targetObject.getClass(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "targetObject must be JsonObject");
			}
			result = ValidateAbility.getInstance().validate(context.getSystemId(), ruleGroupId, (JsonObject) targetObject, null);
		}
		//校验未通过时
		if(result != null && !JsonUtils.getBoolean(result, Validator.RESULT_STATUS_KEY)) {
			if(interruptBusinessFlow) {//如果需要中断业务流执行
				context.setInterrupted(true);
			}
			context.setMessage(JsonUtils.toJson(JsonUtils.getJsonArray(result, Validator.RESULT_MESSAGES_KEY)));
		}
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
