package cn.zpaas.lowcode.be.engine.ability.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * Sql校验器SqlValidator支持以自定义sql实现Validator类的方式接入参数校验能力。
 */
public class SqlValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(SqlValidator.class);
	
	public static final String VALIDATE_SQL_KEY = "validateSql";//静态常量，自定义SQL的Key
	public static final String CONDITIONS_KEY = "conditions";//静态常量，自定义SQL参数列表的Key
	public static final String DATASOURCE_KEY = "datasource";//静态常量，数据源的Key

	public static final String RULE_SPRING_EL_EXPR_KEY = "springELExpr";//静态常量，SpringEL表达式的Key
	
	public static final String VALIDATE_ATTR_VALUE_KEY = "value"; //Spring EL的EvaluationContext中存放属性值的Key
	public static final String VALIDATE_OBJECT_KEY = "srcObject"; //Spring EL的EvaluationContext中存放校验属性归属源对象的Key
	public static final String VALIDATE_PARENT_OBJECT_KEY = "parentObject"; //Spring EL的EvaluationContext中存放上级对象的Key
	public static final String SQL_RESULT_KEY = "sqlResult"; //Spring EL的EvaluationContext中存放上级对象的Key

	public static final String SLASH_FLAG = "/";
	public static final String PARENT_OBJECT_FLAG = "$p.";
	
	
	/**
	 * 校验实现方法，由子类实现
	 * @param validateRule 校验规则
	 * @param attrValue 属性值
	 * @param srcObject 校验对象（完整的对象）
	 * @return 校验结果
	 */
	@Override
	protected JsonObject validateImpl(ValidateRule validateRule, Object attrValue, JsonObject srcObject, JsonObject parentObject)
			throws EngineException {
		//初始化返回结果对象JSON
		JsonObject result = new JsonObject();
		result.addProperty(RESULT_STATUS_KEY, true);
		result.addProperty(RESULT_INTERRUPTED_KEY, false);
		JsonArray messages = new JsonArray();
		result.add(RESULT_MESSAGES_KEY, messages);
		
		// 获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		
		String springELExpr = JsonUtils.getString(ruleValue, RULE_SPRING_EL_EXPR_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 属性的值是空的情况下，是无效的校验规则
		if (springELExpr == null || springELExpr.trim().length() == 0) {
			logger.info("springELExpr are empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of springELExpr must be configured!");
		}
		// 获取校验Sql
        String validateSql = JsonUtils.getString(ruleValue, VALIDATE_SQL_KEY);
		JsonArray conditions = JsonUtils.getJsonArray(ruleValue, CONDITIONS_KEY);

        // 配置的校验Sql为空时，返回原值
        if (StringUtils.isBlank(validateSql)) {
            logger.info("validateSql are empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of validateSql must be configured!");
        }
        String datasource = JsonUtils.getString(ruleValue, DATASOURCE_KEY);

		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;

		//如果属性值为null，直接校验通过
		if(attrValue == null  || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}

        List<Object> valueList = new ArrayList<>();
        // 加入条件参数值
        if(!JsonUtils.isEmpty(conditions)) {
			String jsonPath = null;
			for(int i=0; i<conditions.size(); i++) {
				jsonPath = conditions.get(i).getAsString();
				if(StringUtils.isBlank(jsonPath)) {//为空时
					valueList.add(null);
				}else if(SLASH_FLAG.equals(jsonPath)){//表示当前属性值
					valueList.add(attrValue);
				}else if(jsonPath.startsWith(PARENT_OBJECT_FLAG)){//表示取上级对象的属性
					String parentJsonPath = jsonPath.substring(PARENT_OBJECT_FLAG.length());
					valueList.add(JsonUtils.eval(parentObject, parentJsonPath));
				}else {//表示从归属对象取值
					valueList.add(JsonUtils.eval(srcObject, jsonPath));
				}
			}
		}
        //执行sql语句
        List<Map<String, Object>> resultList = ORMRepositoryAbility.getInstance().executeQuerySqlL(validateSql, valueList, validateRule.getSystemId(), datasource, validateRule.getTenantId());
		
		//使用SpringEL表达式进行校验，如"#{#value  &&  #srcObject.get('code')}"
		SpelExpressionParser parser = new SpelExpressionParser();
		EvaluationContext ctx = new StandardEvaluationContext();
		ctx.setVariable(VALIDATE_ATTR_VALUE_KEY, attrValue);//当前校验的属性值
		ctx.setVariable(VALIDATE_OBJECT_KEY, srcObject);//srcObject
		ctx.setVariable(SQL_RESULT_KEY, resultList);//sql返回结果列表
		ctx.setVariable(VALIDATE_PARENT_OBJECT_KEY, parentObject);//sql返回结果列表
		isPass = parser.parseExpression(springELExpr, new TemplateParserContext()).getValue(ctx, Boolean.TYPE);
		
		if(!isPass) {
			JsonObject msg = new JsonObject();
			msg.addProperty(RULE_MESSAGE_KEY, message);
			msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
			messages.add(msg);
			result.addProperty(RESULT_STATUS_KEY, false);
			if(interrupt) {
				result.addProperty(RESULT_INTERRUPTED_KEY, true);
			}
		}

		return result;
	}
	
	/**
	 * 对校验规则中个性属性实现个性的逻辑。规则配置信息校验不通过时，抛出异常。
	 * @param validateRule 校验规则配置信息
	 * @throws EngineException
	 */
	@Override
	protected void ruleCheck(ValidateRule validateRule) throws EngineException {
		super.ruleCheck(validateRule);
		if(validateRule.getRuleValue1() == null || validateRule.getRuleValue1().trim().length() == 0) {
			logger.error("validateRule's cfg informarion(ruleValue1) can't be null! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "validateRule's cfg informarion(ruleValue1) can't be null!");
		}
		
		//获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		//属性必须有个有配置
		if(ruleValue == null || !ruleValue.has(RULE_SPRING_EL_EXPR_KEY)|| !ruleValue.has(VALIDATE_SQL_KEY)) {
			logger.error("properties of springELExpr and validateSql must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of springELExpr and validateSql must be configured!");
		}
	}
}
