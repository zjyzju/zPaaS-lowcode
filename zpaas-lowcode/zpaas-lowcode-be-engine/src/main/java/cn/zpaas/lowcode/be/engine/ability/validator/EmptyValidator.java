package cn.zpaas.lowcode.be.engine.ability.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 空值校验器EmptyValidator的主要用途是对参数值是否不能为null以及是否不能为empty进行校验。
 */
public class EmptyValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(EmptyValidator.class);
	
	public static final String RULE_UNNULL_KEY = "unNull";//静态常量，配置是否进行不能为null的校验的Key
	public static final String RULE_UNEMPTY_KEY = "unEmpty";//静态常量，配置是否进行不能为empty的校验的Key
	
	
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
		
		boolean unNull = JsonUtils.getBoolean(ruleValue, RULE_UNNULL_KEY);
		boolean unEmpty = JsonUtils.getBoolean(ruleValue, RULE_UNEMPTY_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 两个属性的值都是false的情况下，是无效的校验规则
		if (!unNull && !unEmpty) {
			logger.info("both unNull and unEmpty are false. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of unNull and unEmpty must be configured!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		//是否为null的校验
		if((unNull || unEmpty) && attrValue == null) {
			logger.info("EmptyValidator: attrValue is null!");
			JsonObject msg = new JsonObject();
			msg.addProperty(RULE_MESSAGE_KEY, message);
			msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
			messages.add(msg);
			result.addProperty(RESULT_STATUS_KEY, false);
			if(interrupt) {
				result.addProperty(RESULT_INTERRUPTED_KEY, true);
			}
			return result;
		}
		//是否为空的校验
		if(unEmpty) {
			boolean valid = true;
			if(attrValue instanceof String) {//字符串的情况
				if(((String)attrValue).trim().length() == 0) {
					valid = false;
				}
			}else if(attrValue instanceof JsonArray) {//数组的情况
				if(((JsonArray)attrValue).isEmpty()) {
					valid = false;
				}
			}
			
			if(!valid) {
				logger.info("EmptyValidator: attrValue is empty!");
				JsonObject msg = new JsonObject();
				msg.addProperty(RULE_MESSAGE_KEY, message);
				msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
				messages.add(msg);
				result.addProperty(RESULT_STATUS_KEY, false);
				if(interrupt) {
					result.addProperty(RESULT_INTERRUPTED_KEY, true);
				}
				return result;
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
		//两个属性至少有一个有配置
		if(ruleValue == null || (!ruleValue.has(RULE_UNNULL_KEY) && !ruleValue.has(RULE_UNEMPTY_KEY))) {
			logger.error("one of unNull and unEmpty must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of unNull and unEmpty must be configured!");
		}
		
		Boolean unNull = JsonUtils.getBoolean(ruleValue, RULE_UNNULL_KEY);
		Boolean unEmpty = JsonUtils.getBoolean(ruleValue, RULE_UNEMPTY_KEY);
		//两个属性的值都是false的情况下，是无效的校验规则
		if(!unNull && !unEmpty) {
			logger.info("both unNull and unEmpty are false. validateRuleId: {}", validateRule.getId());
		}
	}
}
