package cn.zpaas.lowcode.be.engine.ability.validator;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.exception.CommException;
import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.PrimitiveJavaType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 类型校验器TypeValidator的主要用途是校验参数值是否是合法的类型。主要针对采用字符串类型定义的属性，但实际代表不同的类型。
 */
public class TypeValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(TypeValidator.class);
	
	public static final String RULE_ATTR_TYPE_KEY = "attrType";//静态常量，属性类型的Key，支持String、Date、Long、Integer、Float、Double、Boolean、Byte、Character等原始类型
	
	
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
		
		String attrType = JsonUtils.getString(ruleValue, RULE_ATTR_TYPE_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 属性的值是空的情况下，是无效的校验规则
		if (attrType == null || attrType.trim().length() == 0) {
			logger.info("attrType are empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of attrType must be configured!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		//如果属性值为null，直接校验通过
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		
		if (PrimitiveJavaType.STRING_TYPE.getName().equals(attrType)) {// 字符串的情况
			if (!attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.STRING_TYPE.getCode());
				isPass = false;
			}
		} else if (PrimitiveJavaType.DATE_TYPE.getName().equals(attrType)) {// 日期的情况
			try {
				if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
					try {
						if(DateUtils.parseDateTryFormat((String)attrValue) == null) {
							logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.DATE_TYPE.getCode());
							isPass = false;
						}
					}catch(CommException ex) {
						logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.DATE_TYPE.getCode());
						isPass = false;
					}
				}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.DATE_TYPE.getCode()) && DateUtils.parseDateTryFormat(attrValue.toString()) == null) {
					logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.DATE_TYPE.getCode());
					isPass = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.DATE_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.BOOLEAN_TYPE.getName().equals(attrType)) {// Boolean的情况
			if (!attrValue.getClass().getName().equals(PrimitiveJavaType.BOOLEAN_TYPE.getCode()) && !attrValue.getClass().getName().equals(Boolean.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.BOOLEAN_TYPE.getCode());
				isPass = false;
			}
		} else if (PrimitiveJavaType.BYTE_TYPE.getName().equals(attrType)) {// Byte的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					Byte.parseByte((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.BYTE_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.BYTE_TYPE.getCode()) && !attrValue.getClass().getName().equals(Byte.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.BYTE_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.INTEGER_TYPE.getName().equals(attrType)) {// Integer的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					Integer.parseInt((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.INTEGER_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.INTEGER_TYPE.getCode()) && !attrValue.getClass().getName().equals(Integer.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.INTEGER_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.LONG_TYPE.getName().equals(attrType)) {// Long的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					Long.parseLong((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.LONG_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.LONG_TYPE.getCode()) && !attrValue.getClass().getName().equals(Long.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.LONG_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.BIGINTEGER_TYPE.getName().equals(attrType)) {// BigInteger的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					new BigInteger((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.LONG_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.LONG_TYPE.getCode()) && !attrValue.getClass().getName().equals(Long.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.LONG_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.BIGDECIMAL_TYPE.getName().equals(attrType)) {// BigDecimal的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					new BigDecimal((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.LONG_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.LONG_TYPE.getCode()) && !attrValue.getClass().getName().equals(Long.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.LONG_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.FLOAT_TYPE.getName().equals(attrType)) {// Float的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					Float.parseFloat((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.FLOAT_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.FLOAT_TYPE.getCode()) && !attrValue.getClass().getName().equals(Float.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.FLOAT_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.DOUBLE_TYPE.getName().equals(attrType)) {// Double的情况
			if(attrValue.getClass().getName().equals(PrimitiveJavaType.STRING_TYPE.getCode())) {
				try {
					Double.parseDouble((String)attrValue);
				}catch(NumberFormatException ex) {
					logger.info("TypeValidator: attrValue's value is not a valid {}!", PrimitiveJavaType.DOUBLE_TYPE.getCode());
					isPass = false;
				}
			}else if (!attrValue.getClass().getName().equals(PrimitiveJavaType.DOUBLE_TYPE.getCode()) && !attrValue.getClass().getName().equals(Double.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.DOUBLE_TYPE.getCode());
				isPass = false;
			}
		}else if (PrimitiveJavaType.CHARACTER_TYPE.getName().equals(attrType)) {// Character的情况
			if (!attrValue.getClass().getName().equals(PrimitiveJavaType.CHARACTER_TYPE.getCode()) && !attrValue.getClass().getName().equals(Character.TYPE.getName())) {
				logger.info("TypeValidator: attrValue's type is not {}!", PrimitiveJavaType.CHARACTER_TYPE.getCode());
				isPass = false;
			}
		} else {
			logger.error("TypeValidator: invalid  type :{}",attrType);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "TypeValidator: invalid  type!");
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
		//属性必须有个有配置
		if(ruleValue == null || !ruleValue.has(RULE_ATTR_TYPE_KEY)) {
			logger.error("properties of attrType must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "properties of attrType must be configured!");
		}
	}
}
