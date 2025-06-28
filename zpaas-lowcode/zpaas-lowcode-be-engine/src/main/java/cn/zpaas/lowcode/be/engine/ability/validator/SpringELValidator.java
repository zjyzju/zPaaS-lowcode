package cn.zpaas.lowcode.be.engine.ability.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 正则表达式校验器RegExprValidator的主要用途通过正则表达式来校验参数值是否合法。主要针对采用字符串类型定义的属性。
 */
public class SpringELValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(SpringELValidator.class);
	
	public static final String RULE_SPRING_EL_EXPR_KEY = "springELExpr";//静态常量，SpringEL表达式的Key
	
	public static final String VALIDATE_ATTR_VALUE_KEY = "value"; //Spring EL的EvaluationContext中存放属性值的Key
	public static final String VALIDATE_OBJECT_KEY = "srcObject"; //Spring EL的EvaluationContext中存放校验属性归属源对象的Key
	public static final String VALIDATE_PARENT_OBJECT_KEY = "parentObject"; //Spring EL的EvaluationContext中存放上级对象的Key
	
	
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
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		
		//使用SpringEL表达式进行校验，如"#{#value  &&  #srcObject, 'code')}"
		SpelExpressionParser parser = new SpelExpressionParser();
		EvaluationContext ctx = new StandardEvaluationContext();
		ctx.setVariable(VALIDATE_ATTR_VALUE_KEY, attrValue);//当前校验的属性值
		ctx.setVariable(VALIDATE_OBJECT_KEY, srcObject);//属性归属的对象
		ctx.setVariable(VALIDATE_PARENT_OBJECT_KEY, parentObject);//属性归属的上级对象
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
		if(ruleValue == null || !ruleValue.has(RULE_SPRING_EL_EXPR_KEY)) {
			logger.error("properties of springELExpr must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of springELExpr must be configured!");
		}
	}
}
