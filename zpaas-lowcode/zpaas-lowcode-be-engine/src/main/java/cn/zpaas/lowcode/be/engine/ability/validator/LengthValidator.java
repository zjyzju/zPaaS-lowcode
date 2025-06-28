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
 * 长度校验器LengthValidator的主要用途是校验参数值的长度是否在合法的范围之内，配置最小长度和最大长度，边界值采用闭区间的方式。支持数字类型、符串类型以及数组三类属性值的长度校验。
 */
public class LengthValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(LengthValidator.class);
	
	public static final String RULE_MIN_LENGTH_KEY = "minLength";//静态常量，最小长度的Key
	public static final String RULE_MAX_LENGTH_KEY = "maxLength";//静态常量，最大长度的Key
	
	
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
		
		Integer minLength = JsonUtils.getInteger(ruleValue, RULE_MIN_LENGTH_KEY);
		Integer maxLength = JsonUtils.getInteger(ruleValue, RULE_MAX_LENGTH_KEY);
		
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 两个属性的值都是空的情况下，是无效的校验规则
		if (minLength == null && maxLength == null) {
			logger.info("both minLength and maxLength are empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of minLength and maxLength must be configured!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		//如果属性值为null，直接校验通过
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		//最小长度的校验
		if(minLength != null) {
			if(attrValue instanceof String) {//字符串的情况
				if(((String)attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof Byte) {
				if(String.valueOf(attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof Integer) {
				if(String.valueOf(attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof Long) {
				if(String.valueOf(attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof Float) {
				if(String.valueOf(attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof Double) {
				if(String.valueOf(attrValue).length() < minLength) {
					logger.info("LengthValidator: attrValue's length is less then minLength!");
					isPass = false;
				}
			}else if(attrValue instanceof JsonArray) {
				if(((JsonArray)attrValue).size() < minLength) {
					logger.info("LengthValidator: attrValue's size is less then minLength!");
					isPass = false;
				}
			}else {
				logger.error("LengthValidator: invalid attrValue type :{}", attrValue.getClass());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "RangeValidator: invalid attrValue type!");
			}
		}
		// 最大长度的校验
		if (maxLength != null) {
			if (attrValue instanceof String) {// 字符串的情况
				if (((String) attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof Byte) {
				if (String.valueOf(attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof Integer) {
				if (String.valueOf(attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof Long) {
				if (String.valueOf(attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof Float) {
				if (String.valueOf(attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof Double) {
				if (String.valueOf(attrValue).length() > maxLength) {
					logger.info("LengthValidator: attrValue's length is greater then maxLength!");
					isPass = false;
				}
			} else if (attrValue instanceof JsonArray) {
				if (((JsonArray) attrValue).size() > maxLength) {
					logger.info("LengthValidator: attrValue's size is greater then maxLength!");
					isPass = false;
				}
			} else {
				logger.error("LengthValidator: invalid attrValue type :{}", attrValue.getClass());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "RangeValidator: invalid attrValue type!");
			}
		}
		
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
		if(StringUtils.isBlank(validateRule.getRuleValue1())) {
			logger.error("validateRule's cfg informarion(ruleValue1) can't be null! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "validateRule's cfg informarion(ruleValue1) can't be null!");
		}
		
		//获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		//两个属性至少有一个有配置
		if(ruleValue == null || (!ruleValue.has(RULE_MIN_LENGTH_KEY) && !ruleValue.has(RULE_MAX_LENGTH_KEY))) {
			logger.error("one of minLength and maxLength must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of minLength and maxLength must be configured!");
		}
	}
}
