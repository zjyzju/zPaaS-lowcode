package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CompareUtils;
import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.BusinessFlowProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * ConditionNode是条件分支节点的实现类，主要实现对设置的条件进行判断，如果满足条件则执行配置的子业务流
 */
public class ConditionNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ConditionNode.class);

	private static final String CFG_CONDITION_EXPR_KEY = "conditionExpr"; //条件表达式存放的Key
	private static final String CFG_CONDITIONS_KEY = "conditions"; //条件列表存放的Key
	private static final String CFG_CONDITION_OBJECT_SOURCE_KEY = "conditionObjectSource"; //条件对象的来源存放的Key
	private static final String CFG_CONDITION_OBJECT_IS_LISt_KEY = "conditionObjectIsList"; //条件对象是否是列表存放的Key
	private static final String CFG_CONDITION_OBJECT_KEY_KEY = "conditionObjectKey"; //条件对象的Key值存放的Key
	private static final String CFG_CONDITION_ATTR_PATH_KEY = "conditionAttrPath"; //条件对象属性对应的json path存放的Key
	private static final String CFG_CONDITION_OPERATOR_KEY = "conditionOperator"; //条件比较操作符存放的Key
	private static final String CFG_COMPARE_TYPE_KEY = "compareType"; //条件值比较类型存放的Key
	private static final String CFG_VALUE_OBJECT_SOURCE_KEY = "valueObjectSource"; //值对象的来源存放的Key
	private static final String CFG_VALUE_OBJECT_IS_LIST_KEY = "valueObjectIsList"; //值对象是否是列表存放的Key
	private static final String CFG_VALUE_OBJECT_KEY_KEY = "valueObjectKey"; //值对象的Key值存放的Key
	private static final String CFG_VALUE_ATTR_PATH_KEY = "valueAttrPath"; //值对象属性对应的json path存放的Key
	public static final String CFG_SUB_BUSINESS_FLOW_ID_KEY = "subBusinessFlowId"; //分支执行的子业务流标识存放的Key
	
	
	
	private static final String CFG_COMPARE_TYPE_B = "B"; //B（boolean比较）
	private static final String CFG_COMPARE_TYPE_S = "S"; //S（字符串比较）
	private static final String CFG_COMPARE_TYPE_D = "D"; //D（日期比较）
	private static final String CFG_COMPARE_TYPE_N = "N"; //N（数值比较）
	private static final String CFG_COMPARE_TYPE_C = "C"; //C（数组的大小比较）
	private static final String CFG_COMPARE_TYPE_L = "L"; //L（长度比较）
	private static final String CFG_COMPARE_TYPE_H = "H"; //H（是否包含）
	private static final String CFG_COMPARE_TYPE_O = "O"; //O（是否对象）
	private static final String CFG_COMPARE_TYPE_A = "A"; //A（是否数组）
	
	private static final String COLON_FLAG = ":";//冒号
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
		String conditionExpr = JsonUtils.getString(nodeCfg, CFG_CONDITION_EXPR_KEY);
		JsonArray conditions = JsonUtils.getJsonArray(nodeCfg, CFG_CONDITIONS_KEY);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);
		
		//条件表达式、条件列表和子业务流标识不能为空
		if(StringUtils.isBlank(conditionExpr) || JsonUtils.isEmpty(conditions) || StringUtils.isBlank(subBusinessFlowId)) {
			logger.error("T[{}] conditionExpr conditions and subBusinessFlowId can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionExpr conditions and subBusinessFlowId can't be empty.");
		}
		
		//循环处理每个条件，获得每个条件的boolean值
		JsonObject condition = null;
		String conditionObjectSource = null;
		boolean conditionObjectIsList = false;
		String conditionObjectKey = null;
		String conditionAttrPath = null;
		String conditionOperator = null;
		String compareType = null;
		String valueObjectSource = null;
		boolean valueObjectIsList = false;
		String valueObjectKey = null;
		String valueAttrPath = null;
		Object conditionObject =null, valueObject = null;
		List<Boolean> conditionResultsList = new ArrayList<>();
		for(int i=0; i<conditions.size(); i++) {
			conditionObject = null;
			valueObject = null;
			condition = conditions.get(i).getAsJsonObject();
			//获取比较条件对象信息
			conditionObjectSource = JsonUtils.getString(condition, CFG_CONDITION_OBJECT_SOURCE_KEY);
			conditionObjectIsList = JsonUtils.getBoolean(condition, CFG_CONDITION_OBJECT_IS_LISt_KEY);
			conditionObjectKey = JsonUtils.getString(condition, CFG_CONDITION_OBJECT_KEY_KEY);
			conditionAttrPath = JsonUtils.getString(condition, CFG_CONDITION_ATTR_PATH_KEY);
			if(StringUtils.isBlank(conditionObjectSource) || StringUtils.isBlank(conditionObjectKey)) {
				logger.error("T[{}] conditionObjectSource and conditionObjectKey can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionObjectSource and conditionObjectKey can't be empty. ");
			}
			//动态属性的情况
			if(!StringUtils.isBlank(conditionAttrPath)) {
				conditionAttrPath = this.dynamicAttrProcess(conditionAttrPath, businessFlowNode, context);
			}
			//当对象是列表时或固定值时，不能设置conditionAttrPath
			if(conditionObjectIsList || OBJECT_INSTANCE_SOURCE_F.equals(conditionObjectSource)) {
				if(!StringUtils.isBlank(conditionAttrPath)) {
					logger.error("T[{}] when conditionObjectIsList is true, conditionAttrPath can't has value. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when conditionObjectIsList is true, conditionAttrPath can't has value.");
				}
			}		
			
			conditionObject = getContextObject(conditionObjectSource, conditionObjectKey, conditionObjectIsList, conditionAttrPath, context, businessFlowNode);
			
			//获取比较值对象信息
			valueObjectSource = JsonUtils.getString(condition, CFG_VALUE_OBJECT_SOURCE_KEY);
			valueObjectIsList = JsonUtils.getBoolean(condition, CFG_VALUE_OBJECT_IS_LIST_KEY);
			valueObjectKey = JsonUtils.getString(condition, CFG_VALUE_OBJECT_KEY_KEY);
			valueAttrPath = JsonUtils.getString(condition, CFG_VALUE_ATTR_PATH_KEY);
			// if(StringUtils.isBlank(valueObjectSource) || StringUtils.isBlank(valueObjectKey)) {
			// 	logger.error("valueObjectSource and valueObjectKey can't be empty. businessflowNodeId: {}", businessFlowNode.getId());
			// 	throw new EngineException(ResultStatus.INTERNAL_ERROR, "valueObjectSource and valueObjectKey can't be empty. ");
			// }
			//动态属性的情况
			if(!StringUtils.isBlank(valueAttrPath)) {
				valueAttrPath = this.dynamicAttrProcess(valueAttrPath, businessFlowNode, context);
			}
			//当对象是列表时或固定值时，不能设置valueAttrPath
			if(valueObjectIsList || OBJECT_INSTANCE_SOURCE_F.equals(valueObjectSource)) {
				if(!StringUtils.isBlank(valueAttrPath)) {
					logger.error("T[{}] when valueObjectIsList is true, valueAttrPath can't has value. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when valueObjectIsList is true, valueAttrPath can't has value.");
				}
			}		
			if(!StringUtils.isBlank(valueObjectSource) && !StringUtils.isBlank(valueObjectKey)) {
				valueObject = getContextObject(valueObjectSource, valueObjectKey, valueObjectIsList, valueAttrPath, context, businessFlowNode);
			}
			
			
			//获取比较操作符以及比较类型
			
			//compareType：条件值比较类型，包括B（boolean比较）、S（字符串比较）、D（日期比较）、N（数值比较）、C（数组的大小比较）、L（长度比较）
			conditionOperator = JsonUtils.getString(condition, CFG_CONDITION_OPERATOR_KEY);
			compareType = JsonUtils.getString(condition, CFG_COMPARE_TYPE_KEY);
			if(conditionObject == null && conditionObjectIsList && CFG_COMPARE_TYPE_C.equals(compareType)) {//当条件对象为null，且进行数组大小的比较时，赋值为空数组
				conditionObject = new JsonArray();
			}
			if(valueObject == null && valueObjectIsList && CFG_COMPARE_TYPE_C.equals(compareType)) {//当条件对象为null，且进行数组大小的比较时，赋值为空数组
				valueObject = new JsonArray();
			}
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] conditionObject:{}, valueObject:{}", businessFlowNode.getTenantId(), conditionObject, valueObject);
			}
			
			if(conditionObject == null || valueObject == null) {//条件对象或比较值对象为null时		
				if(CFG_COMPARE_TYPE_O.equals(compareType) || CFG_COMPARE_TYPE_A.equals(compareType))	 {
					if(conditionObject == null) {
						conditionResultsList.add(false);
						continue;
					}else {
						valueObject = false;
					}
				}else if(conditionObject == null && valueObject == null) {//条件对象和比较值对象都为null时，直接作为true处理
					if(CompareUtils.CFG_CONDITION_OPERATOR_EQ.equals(conditionOperator)) {//等于
						conditionResultsList.add(true);
					}else {
						conditionResultsList.add(false);
					}
					continue;
				} else {//一个为null，另一个不为null
					if(CompareUtils.CFG_CONDITION_OPERATOR_NE.equals(conditionOperator)) {//不等于
						conditionResultsList.add(true);
					}else {
						conditionResultsList.add(false);
					}
					continue;
				}
			}
			
			
			switch(compareType) {
				case CFG_COMPARE_TYPE_B://B（boolean比较）
					boolean conditionBool, valueBool;
					try {
						if(conditionObject instanceof String) {
							conditionBool = Boolean.valueOf((String)conditionObject);
						}else {
							conditionBool = (boolean) conditionObject;
						}
						if(valueObject instanceof String) {
							valueBool = Boolean.valueOf((String)valueObject);
						}else {
							valueBool = (boolean) valueObject;
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionBool, valueBool, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_S://S（字符串比较）
					String conditionString=null, valueString=null;
					try {
						if(conditionObject instanceof String) {
							conditionString = (String)conditionObject;
						}else {
							conditionString = String.valueOf(conditionObject);
						}
						if(valueObject instanceof String) {
							valueString = (String)valueObject;
						}else {
							valueString = String.valueOf(valueObject);
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionString, valueString, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_D://D（日期比较）
					Date conditionDate=null, valueDate=null;
					try {
						if(conditionObject instanceof String) {
							String string = (String) conditionObject;
							if(string.indexOf(COLON_FLAG)>=0) {
								conditionDate = DateUtils.parseDateTime(string);
							}else {
								conditionDate = DateUtils.parseDate(string);
							}
						}else if(conditionObject instanceof Date){
							conditionDate = (Date)conditionObject;
						}else {
							logger.error("T[{}] conditionObject is not Date type. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionObject is not Date type.");
						}
						if(valueObject instanceof String) {
							String string = (String) valueObject;
							if(string.indexOf(COLON_FLAG)>=0) {
								valueDate = DateUtils.parseDateTime(string);
							}else {
								valueDate = DateUtils.parseDate(string);
							}
						}else if(valueObject instanceof Date){
							valueDate = (Date)valueObject;
						}else {
							logger.error("T[{}] valueObject is not Date type. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "valueObject is not Date type.");
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionDate, valueDate, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_N://N（数值比较）
					double conditionDouble, valueDouble;
					try {
						if(conditionObject instanceof String) {
							conditionDouble = Double.parseDouble((String)conditionObject);
						}else {
							conditionDouble = Double.parseDouble(conditionObject.toString());
						}
						if(valueObject instanceof String) {
							valueDouble = Double.parseDouble((String)valueObject);
						}else {
							valueDouble = Double.parseDouble(valueObject.toString());
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionDouble, valueDouble, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_C://C（数组的大小比较）
					int conditionInt, valueInt;
					try {
						if(conditionObject instanceof JsonArray) {
							conditionInt = ((JsonArray)conditionObject).size();
						}else {
							logger.error("T[{}] conditionObject must be JsonArray for compareType(C). businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionObject must be JsonArray for compareType(C).");
						}
						if(OBJECT_INSTANCE_SOURCE_F.equals(valueObjectSource)) {
							valueInt = Integer.parseInt((String)valueObject);
						}else {
							if(valueObject instanceof JsonArray) {
								valueInt = ((JsonArray)valueObject).size();
							}else {
								logger.error("T[{}] valueObject must be JsonArray for compareType(C). businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
								throw new EngineException(ResultStatus.INTERNAL_ERROR, "valueObject must be JsonArray for compareType(C).");
							}
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionInt, valueInt, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_L://L（长度比较）
					int conditionIntL, valueIntL;
					try {
						if(conditionObject instanceof String) {
							conditionIntL = ((String)conditionObject).length();
						}else {
							logger.error("T[{}] conditionObject must be String for compareType(L). businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionObject must be String for compareType(L).");
						}
						if(OBJECT_INSTANCE_SOURCE_F.equals(valueObjectSource)) {
							valueIntL = Integer.parseInt((String)valueObject);
						}else {
							if(valueObject instanceof String) {
								valueIntL = ((String)valueObject).length();
							}else {
								logger.error("T[{}] valueObject must be String for compareType(L). businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
								throw new EngineException(ResultStatus.INTERNAL_ERROR, "valueObject must be String for compareType(L).");
							}
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					conditionResultsList.add(CompareUtils.compare(conditionIntL, valueIntL, conditionOperator));
					break;
				case CFG_COMPARE_TYPE_H://H（是否包含）
					try {
						if(conditionObject instanceof JsonObject) {
							JsonObject conditionJsonObject = (JsonObject)conditionObject;
							conditionResultsList.add(conditionJsonObject.has(valueObject.toString()));
						}else if(conditionObject instanceof JsonArray) {
							JsonArray conditionJsonArray = (JsonArray)conditionObject;
							conditionResultsList.add(conditionJsonArray.contains(JsonUtils.toJsonElement(valueObject)));
						}else {
							logger.error("T[{}] conditionObject must be JsonObject or JsonArray for compareType(H). businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "conditionObject must be JsonObject or JsonArray for compareType(H).");
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					break;
				case CFG_COMPARE_TYPE_O://O（是否对象）
					try {
						conditionResultsList.add((conditionObject instanceof JsonObject));
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					break;
				case CFG_COMPARE_TYPE_A://A（是否数组）
					try {
						conditionResultsList.add((conditionObject instanceof JsonArray));
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.",null, e);
					}
					break;
				default:
					logger.error("T[{}] invalid compareType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid compareType.");
			}
		}
		//替换表达式中的点位符${1}、${2}
		int count = 1;
		for(Boolean valueBoolean : conditionResultsList) {
			conditionExpr = conditionExpr.replaceAll(LEFT_$_BRACES+(count)+RIGHT_BRACES, String.valueOf(valueBoolean));
			count++;
		}
		
		//使用SpringEL解析表达式
		SpelExpressionParser parser = new SpelExpressionParser();
		boolean conditionResult = (boolean)parser.parseExpression(conditionExpr).getValue();
		logger.info("T[{}] conditionResult:{}", businessFlowNode.getTenantId(), conditionResult);
		
		if(conditionResult) {//条件判定通过
			//初始化业务流命令对象
			BusinessFlowCommand businessFlowCommand = new BusinessFlowCommand();
			businessFlowCommand.setBusinessFlowId(subBusinessFlowId);
			businessFlowCommand.setSystemId(context.getSystemId());
			businessFlowCommand.setReqData(context.getReqData());
			
			BusinessFlowProxy.getInstance().execute(businessFlowCommand, context);
			
			//由于是子业务流，已经将目标对象保存到节点处理结果中，不需要再设置
//			context.setAttribute(NODE_RESULT_KEY, result);
		}else {//条件判定未通过
			//将目标对象保存到节点处理结果中
			context.setAttribute(NODE_RESULT_KEY, null);
		}
	}

	@Override
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {
		super.nodeCfgLoad(nodeCfg, node, info, businessFlowService);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);
		BusinessFlowInfo subBusinessFlowInfo = businessFlowService.loadBusinessFlowInfo(subBusinessFlowId);
		if(subBusinessFlowInfo != null) {
			info.getSubBusinessFlowMap().put(node.getBpmnNodeId(), subBusinessFlowInfo);
		}
	}

	@Override
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) throws EngineException {
		super.beforeSave(node, info, businessFlowService);
		businessFlowService.saveSubBusinessFlow(node, info, businessFlowService, CFG_SUB_BUSINESS_FLOW_ID_KEY);
	}

	
}
