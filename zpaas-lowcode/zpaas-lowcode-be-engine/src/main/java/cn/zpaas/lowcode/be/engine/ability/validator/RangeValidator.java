package cn.zpaas.lowcode.be.engine.ability.validator;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 范围校验器RangeValidator的主要用途是对参数值进行范围校验，判断是否在最小值和最大值范围之内，边界值按闭区间的方式来处理，支持数字、日期和字符串三种类型的校验。
 */
public class RangeValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(RangeValidator.class);
	
	public static final String RULE_START_VALUE_KEY = "startValue";//静态常量，配置起始值的Key
	public static final String RULE_END_VALUE_KEY = "endValue";//静态常量，配置终止值的Key
	
	
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
		
		String startValue = JsonUtils.getString(ruleValue, RULE_START_VALUE_KEY);
		String endValue = JsonUtils.getString(ruleValue, RULE_END_VALUE_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 两个属性的值都是空的情况下，是无效的校验规则
		if ((startValue == null || startValue.trim().length() == 0) && (endValue == null || endValue.trim().length() == 0)) {
			logger.info("both startValue and endValue are empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of startValue and endValue must be configured!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		//如果属性值为null，直接校验通过
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		//起始值的校验
		if(startValue != null && startValue.trim().length() > 0) {
			if(attrValue instanceof String) {//字符串的情况
				if(((String)attrValue).compareTo(startValue) < 0) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Date) {//日期的情况
				if(((Date)attrValue).compareTo(DateUtils.parseDateTryFormat(startValue)) < 0) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Byte) {
				if(((Byte)attrValue) < Byte.parseByte(startValue)) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Integer) {
				if(((Integer)attrValue) < Integer.parseInt(startValue)) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Long) {
				if(((Long)attrValue) < Long.parseLong(startValue)) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Float) {
				if(((Float)attrValue) < Float.parseFloat(startValue)) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else if(attrValue instanceof Double) {
				if(((Double)attrValue) < Double.parseDouble(startValue)) {
					logger.info("RangeValidator: attrValue is less then startValue!");
					isPass = false;
				}
			}else {
				logger.error("RangeValidator: invalid attrValue type :{}", attrValue.getClass());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "RangeValidator: invalid attrValue type!");
			}
		}
		//终止值的校验
		if (endValue != null && endValue.trim().length() > 0) {
			if (attrValue instanceof String) {// 字符串的情况
				if (((String) attrValue).compareTo(endValue) > 0) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Date) {// 日期的情况
				if (((Date) attrValue).compareTo(DateUtils.parseDateTryFormat(endValue)) > 0) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Byte) {
				if (((Byte) attrValue) > Byte.parseByte(endValue)) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Integer) {
				if (((Integer) attrValue) > Integer.parseInt(endValue)) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Long) {
				if (((Long) attrValue) > Long.parseLong(endValue)) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Float) {
				if (((Float) attrValue) > Float.parseFloat(endValue)) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else if (attrValue instanceof Double) {
				if (((Double) attrValue) > Double.parseDouble(endValue)) {
					logger.info("RangeValidator: attrValue is greater then endValue!");
					isPass = false;
				}
			} else {
				logger.error("RangeValidator: invalid attrValue type :{}", attrValue.getClass());
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
		if(validateRule.getRuleValue1() == null || validateRule.getRuleValue1().trim().length() == 0) {
			logger.error("validateRule's cfg informarion(ruleValue1) can't be null! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "validateRule's cfg informarion(ruleValue1) can't be null!");
		}
		
		//获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		//两个属性至少有一个有配置
		if(ruleValue == null || !ruleValue.has(RULE_START_VALUE_KEY) || !ruleValue.has(RULE_END_VALUE_KEY)) {
			logger.error("one of startValue and endValue must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of startValue and endValue must be configured!");
		}
	}
}
