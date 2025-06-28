package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
 * SpringElNode是SpringEL表达式节点的实现类，主要实现SpringEL表达式的计算功能，表达式的计算结果要求只有一个返回值
 */
public class SpringELNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(SpringELNode.class);

	private static final String CFG_SPRING_EXPR_KEY = "springExpr"; //SpringEL表达式存放的Key
	private static final String CFG_VALUES_KEY = "values"; //表达式元素值存放的Key
	private static final String CFG_VALUE_TYPE_KEY = "valueType"; //值类型存放的Key
	private static final String CFG_VALUE_OBJECT_SOURCE_KEY = "valueObjectSource"; //值对象的来源存放的Key
	private static final String CFG_VALUE_OBJECT_IS_LIST_KEY = "valueObjectIsList"; //值对象是否是列表存放的Key
	private static final String CFG_VALUE_OBJECT_KEY_KEY = "valueObjectKey"; //值对象的Key值存放的Key
	private static final String CFG_VALUE_ATTR_PATH_KEY = "valueAttrPath"; //值对象属性对应的json path存放的Key
	private static final String CFG_CONTEXT_KEY_KEY = "contextKey"; //上下文key存放的Key
	
	
	private static final String CFG_VALUE_TYPE_O = "O"; //O（原值）
	private static final String CFG_VALUE_TYPE_C = "C"; //C（数组的大小比较）
	private static final String CFG_VALUE_TYPE_L = "L"; //L（字符串长度）
	
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
		String springExpr = JsonUtils.getString(nodeCfg, CFG_SPRING_EXPR_KEY);
		JsonArray values = JsonUtils.getJsonArray(nodeCfg, CFG_VALUES_KEY);
		
		//SpringEL表达式、值列表不能为空
		if(StringUtils.isBlank(springExpr)) {
			logger.error("T[{}] springExpr can't be empty.", businessFlowNode.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "springExpr can't be empty.");
		}

		if(values == null) {
			values = new  JsonArray();
		}
		
		//循环处理每个条件，获得每个条件的boolean值
		JsonObject value = null;
		String valueObjectSource = null;
		boolean valueObjectIsList = false;
		String valueObjectKey = null;
		String valueAttrPath = null;
		String valueType = null;
		Object valueObject = null;
		String contextKey = null;
		List<String> valueList = new ArrayList<>();
		EvaluationContext evaluationContext = new StandardEvaluationContext();
		for(int i=0; i<values.size(); i++) {
			value = values.get(i).getAsJsonObject();
			//获取值对象信息
			valueObjectSource = JsonUtils.getString(value, CFG_VALUE_OBJECT_SOURCE_KEY);//值对象来源
			valueObjectIsList = JsonUtils.getBoolean(value, CFG_VALUE_OBJECT_IS_LIST_KEY);//值对象是否列表
			valueObjectKey = JsonUtils.getString(value, CFG_VALUE_OBJECT_KEY_KEY);//值对象Key
			valueAttrPath = JsonUtils.getString(value, CFG_VALUE_ATTR_PATH_KEY);//值对象属性
			valueType = JsonUtils.getString(value, CFG_VALUE_TYPE_KEY);//值对象取值类型
			contextKey = JsonUtils.getString(value, CFG_CONTEXT_KEY_KEY);//上下文Key
			if(StringUtils.isBlank(valueObjectSource) || StringUtils.isBlank(valueType) || StringUtils.isBlank(valueObjectKey)) {
				logger.error("T[{}] valueType valueObjectSource and valueObjectKey can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "valueObjectSource and valueObjectKey can't be empty. ");
			}
			//当对象是列表时或固定值时，不能设置valueAttrPath
			if(valueObjectIsList || OBJECT_INSTANCE_SOURCE_F.equals(valueObjectSource)) {
				if(!StringUtils.isBlank(valueAttrPath)) {
					logger.error("T[{}] when valueObjectIsList is true, valueAttrPath can't has value. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when valueObjectIsList is true, valueAttrPath can't has value.");
				}
			}
			//动态属性的情况
			if(!StringUtils.isBlank(valueAttrPath)) {
				valueAttrPath = this.dynamicAttrProcess(valueAttrPath, businessFlowNode, context);
			}
			
			if(valueAttrPath != null && Pattern.matches(START_WITH_DIGIT, valueAttrPath)) {//以数字开头的Path，暂时按JsonObject对象处理
				JsonObject object = (JsonObject)getContextObject(valueObjectSource, valueObjectKey, valueObjectIsList, null, context, businessFlowNode);
				valueObject = JsonUtils.getObject(object, valueAttrPath);
			}else {
				valueObject = getContextObject(valueObjectSource, valueObjectKey, valueObjectIsList, valueAttrPath, context, businessFlowNode);
			}
			
			String valueString=null;
			switch(valueType) {
				case CFG_VALUE_TYPE_O://O（原值）		
					if(!StringUtils.isBlank(contextKey)) {//当设置了上下文Key且取值类型为原值时
						evaluationContext.setVariable(contextKey, valueObject);//直接将原对象设置到EvaluationContext
						break;//中断
					}		
					try {
						if(valueObject == null) {
							valueString = null;
						}else if(valueObject instanceof String) {
							valueString = (String)valueObject;
						}else if(valueObject instanceof JsonObject || valueObject instanceof JsonArray) {
							logger.error("T[{}] invalid value. value's type must be primitive java type when valueType is O", businessFlowNode.getTenantId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must be primitive java type when valueType is O");
						}else {
							valueString = String.valueOf(valueObject);
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid value. value's type must be primitive java type when valueType is O", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must be primitive java type when valueType is O",null, e);
					}
					valueList.add(valueString);
					break;
				case CFG_VALUE_TYPE_C://C（数组的大小比较）
					Integer arraySize = null;
					try {
						if(valueObject == null) {
							logger.error("T[{}] value can't be null when valueType is C", businessFlowNode.getTenantId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "value can't be null when valueType is C");
						}else if(valueObject instanceof JsonArray) {
							arraySize = ((JsonArray)valueObject).size();
						}else{
							logger.error("T[{}] invalid value. value's type must be JsonArray when valueType is C", businessFlowNode.getTenantId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must be JsonArray when valueType is C");
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid value. value's type must be JsonArray when valueType is C", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must JsonArray type when valueType is C",null, e);
					}
					if(!StringUtils.isBlank(contextKey)) {//当设置了上下文Key
						evaluationContext.setVariable(contextKey, arraySize);//直接将数据大小设置到EvaluationContext
					}else {
						valueList.add(String.valueOf(arraySize));
					}
					
					break;
				case CFG_VALUE_TYPE_L://L（字符串长度）
					Integer stringLength = null;
					try {
						if(valueObject == null) {
							logger.error("T[{}] value can't be null when valueType is L", businessFlowNode.getTenantId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "value can't be null when valueType is L");
						}else if(valueObject instanceof String) {
							stringLength =((String)valueObject).length();
						}else if(valueObject instanceof JsonObject || valueObject instanceof JsonArray) {
							logger.error("T[{}] invalid value. value's type must be String or primitive java type when valueType is L", businessFlowNode.getTenantId());
							throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must be String or primitive java type when valueType is L");
						}else {
							stringLength = String.valueOf(valueObject).length();
						}
					} catch (RuntimeException e) {
						logger.error("T[{}] invalid value. value's type must be String or primitive java type when valueType is L", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid value. value's type must String or primitive java type when valueType is L",null, e);
					}
					if(!StringUtils.isBlank(contextKey)) {//当设置了上下文Key
						evaluationContext.setVariable(contextKey, stringLength);//直接将字符串长度设置到EvaluationContext
					}else {
						valueList.add(String.valueOf(stringLength));
					}
					break;
				default:
					logger.error("T[{}] invalid ValueType.", businessFlowNode.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid ValueType.");
			}
			
		}
		//替换表达式中的点位符${1}、${2}
		int count = 1;
		for(String valueString : valueList) {
			if(valueString == null) {
				valueString = NULL;
			}
			springExpr = springExpr.replaceAll(LEFT_$_BRACES+(count)+RIGHT_BRACES, Matcher.quoteReplacement(valueString));
			count++;
		}
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] springExpr:{}", businessFlowNode.getTenantId(), springExpr);
		}
		
		//使用SpringEL解析表达式
		SpelExpressionParser parser = new SpelExpressionParser();
		Object result = parser.parseExpression(springExpr).getValue(evaluationContext);
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
