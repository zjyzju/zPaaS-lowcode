package cn.zpaas.lowcode.be.engine.ability.validator;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.TimeOffset;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 日期格式校验器DateValidator主要用来校验日期类型的参数值是否合法。主要针对采用字符串类型定义的属性。
 */
public class DateValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(DateValidator.class);
	
	public static final String RULE_DATE_FORMATE_KEY = "dateFormat";//静态常量，配置dateFormat的Key
	public static final String RULE_RELATIVE_DATE_KEY = "relativeDate";//静态常量，配置relativeDate的Key
	public static final String RULE_VALIDATE_TYPE_KEY = "validateType";//静态常量，配置validateType的Key
	public static final String RULE_VALIDATE_VALUE1_KEY = "validateValue1";//静态常量，配置validateValue1的Key
	public static final String RULE_VALIDATE_VALUE2_KEY = "validateValue2";//静态常量，配置validateValue2的Key
	
	public static final String VALIDATE_TYPE_RELATIVE_RANGE = "relativeRange";//relativeRange
	public static final String VALIDATE_TYPE_NUMBER_DATE = "numberDate";//numberDate
	public static final String NOW_DATE = "now()";//表示当前时间
	
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
		
		String dateFormat = JsonUtils.getString(ruleValue, RULE_DATE_FORMATE_KEY);
		String relativeDate = JsonUtils.getString(ruleValue, RULE_RELATIVE_DATE_KEY);
		String validateType = JsonUtils.getString(ruleValue, RULE_VALIDATE_TYPE_KEY);
		String validateValue1 = JsonUtils.getString(ruleValue, RULE_VALIDATE_VALUE1_KEY);
		String validateValue2 = JsonUtils.getString(ruleValue, RULE_VALIDATE_VALUE2_KEY);
		
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// dateFormat为空的情况下，是无效的校验规则
		if (StringUtils.isBlank(dateFormat)) {
			logger.info("dateFormat is empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "dateFormat must be configured!");
		}
		//validateType不为空时，relativeDate不能为空
		if(validateType != null && validateType.trim().length() > 0) {
			if (relativeDate == null || relativeDate.trim().length() == 0) {
				logger.info("relativeDate can't be empty when validateType is configured. validateRuleId: {}", validateRule.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "relativeDate can't be empty when validateType is configured!");
			}
			if(VALIDATE_TYPE_RELATIVE_RANGE.equals(validateType) && 
					(StringUtils.isBlank(validateValue1) && StringUtils.isBlank(validateValue2))) {//相对日期范围
				logger.info("one of validateValue1 and validateValue2 must be configured when validateType is relativeRange. validateRuleId: {}", validateRule.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of validateValue1 and validateValue2 must be configured when validateType is relativeRange!");
			}
			if(VALIDATE_TYPE_NUMBER_DATE.equals(validateType) &&
					(StringUtils.isBlank(validateValue1) && StringUtils.isBlank(validateValue2))) {//相对日期所在的第几个时间 
				logger.info("both of validateValue1 and validateValue2 must be configured when validateType is numberDate. validateRuleId: {}", validateRule.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "both of validateValue1 and validateValue2 must be configured when validateType is relativeRange!");
			}
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		//如果属性值为null，直接校验通过
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		
		//将attrValue转换成Date类型
		Date originDate = null;
		if(attrValue instanceof Date) {
			originDate = (Date) attrValue;
		}else {
			originDate = DateUtils.parseDateTime(attrValue.toString(), dateFormat);
		}
		
		if(originDate == null) {//转换失败
			logger.info("DateValidator: convert attrValue to Date failed!");
			isPass = false;
		}else {
			//需要与相对时间进行比较
			if(validateType != null && validateType.trim().length() > 0) {
				//处理相对时间
				Date compareDate = null;
				if(NOW_DATE.equals(relativeDate)) {
					compareDate = new Date();
				}else {
					compareDate = DateUtils.parseDateTime(relativeDate, dateFormat);
				}
				
				if(VALIDATE_TYPE_RELATIVE_RANGE.equals(relativeDate)) {//相对日期范围
					if(validateValue1 != null && validateValue1.trim().length() > 0) {
						Date startDate = TimeOffset.calculateDate(compareDate, TimeOffset.parseDuration(validateValue1));
						if ((originDate).compareTo(startDate) < 0) {
							logger.info("DateValidator: attrValue is less then start date!");
							isPass = false;
						}
					}
					if(validateValue2 != null && validateValue2.trim().length() > 0) {
						Date enDate = TimeOffset.calculateDate(compareDate, TimeOffset.parseDuration(validateValue2));
						if ((originDate).compareTo(enDate) > 0) {
							logger.info("DateValidator: attrValue is greater then end date!");
							isPass = false;
						}
					}
				}else if(VALIDATE_TYPE_NUMBER_DATE.equals(relativeDate)) {//相对日期所在的第几个时间
					isPass = TimeOffset.isOffsetDateOf(originDate, validateValue1, TimeOffset.parseDuration(validateValue2));
					
				}else {
					logger.error("invalid relativeDate!"); 
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid relativeDate!");
				}
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
		//三个属性至少有一个有配置
		if(ruleValue == null || !ruleValue.has(RULE_DATE_FORMATE_KEY)) {
			logger.error("dateFormat must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "dateFormat must be configured!");
		}
		
		
	}
}
