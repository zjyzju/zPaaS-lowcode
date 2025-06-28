package cn.zpaas.lowcode.be.engine.ability.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 空值校验器EmptyValidator的主要用途是对参数值是否不能为null以及是否不能为empty进行校验。
 */
public class CustomizedValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(EmptyValidator.class);
	
	public static final String VALIDATOR_CLASS = "validatorClass";//静态常量，自定义校验器的实现类
	
	
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
		
		String validatorClass = JsonUtils.getString(ruleValue, VALIDATOR_CLASS);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// alidatorClass为空时，是无效的校验规则
		if (StringUtils.isBlank(validatorClass)) {
			logger.error("validatorClass can't be null. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "alidatorClass can't be null!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);

		Validator validator = null;

		try {
			validator = (Validator)Class.forName(validatorClass).getDeclaredConstructor().newInstance();
			JsonObject customResult = validator.validate(validateRule, attrValue, srcObject, parentObject);
			if(!JsonUtils.isEmpty(customResult) && !JsonUtils.getBoolean(customResult, RESULT_STATUS_KEY)) {
				JsonObject msg = new JsonObject();
				msg.addProperty(RULE_MESSAGE_KEY, message);
				msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
				messages.add(msg);
				result.addProperty(RESULT_STATUS_KEY, false);
				if(interrupt) {
					result.addProperty(RESULT_INTERRUPTED_KEY, true);
				}
			}
		}catch (Exception e) {
			logger.error("execute custom validator failed!", e);
            throw new EngineException(ResultStatus.INTERNAL_ERROR, "execute custom validator failed!");
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
		//两个属性至少有一个有配置
		if(ruleValue == null || !ruleValue.has(VALIDATOR_CLASS)) {
			logger.error("validatorClass can't be null! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of unNull and unEmpty must be configured!");
		}
		
		String validatorClass = JsonUtils.getString(ruleValue, VALIDATOR_CLASS);
		
		//两个属性的值都是false的情况下，是无效的校验规则
		if(StringUtils.isBlank(validatorClass)) {
			logger.info("validatorClass can't be null. validateRuleId: {}", validateRule.getId());
		}
	}
}
