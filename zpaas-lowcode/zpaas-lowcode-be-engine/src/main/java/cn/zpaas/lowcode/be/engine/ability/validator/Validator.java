package cn.zpaas.lowcode.be.engine.ability.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.domain.eo.ValidateRuleType;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 所有校验规则实现类的基类
 */
public abstract class Validator {
	private static Logger logger = LoggerFactory.getLogger(Validator.class);
	
	private ValidateRuleType validateRuleType = null; //校验器对应的配置信息类
	
	public static final String RULE_MESSAGE_KEY = "message";//校验不通过时的提示信息对应的Key
	public static final String RULE_IS_INTERRUPT_KEY = "interrupt";//是否中断校验对应的Key
	public static final String RULE_ERROR_CODE_KEY = "errorCode";//校验不通过时的错误编码对应的Key
	public static final String RESULT_STATUS_KEY = "status";//校验结果中结果属性对应的Key
	public static final String RESULT_MESSAGES_KEY = "messages";//校验结果中提示信息数组属性对应的Key
	public static final String RESULT_INTERRUPTED_KEY = "interrupted";//校验结果中是否中断属性对应的Key
	
	public static final String DEFAULT_MESSAGE_TEMPLATE = "未通过校验器（$）的校验！";//默认提示信息的模板
	public static final String DEFAULT_MESSAGE_TEMPLATE_REPLACE = "\\$";//默认提示信息的模板
	public static final String COLON = ":";//冒号
	public static final String DOT = ".";//冒号
	
	protected String defaultMessage = null; //校验不通过时默认的提示信息
	
	/**
	 * 校验实现模板方法
	 * @param validateRule 校验规则
	 * @param attrValue 属性值
	 * @param srcObject 校验对象（完整的对象）
	 * @return 校验结果
	 */
	public JsonObject validate(ValidateRule validateRule, Object attrValue, JsonObject srcObject, JsonObject parentObject)  throws EngineException {
		if(logger.isDebugEnabled()) {
			Attribute attr = DomainObjectProxy.getInstance().getAttribute(validateRule.getSystemId(), validateRule.getAttributeId());
			if(attr == null) {
				attr = ValueObjectProxy.getInstance().getAttribute(validateRule.getSystemId(), validateRule.getAttributeId());
			}
			String code = "";
			if(attr != null) {
				code = attr.getCode();
			}
			if(this.validateRuleType != null) {
				logger.debug("validate attr: {}-{}({}) by validator {}", validateRule.getAttributeId(), code, attrValue, this.validateRuleType.getName());
			}else {
				logger.debug("validate attr: {}-{}({}) by customized validator", validateRule.getAttributeId(), code, attrValue);
			}
			
		}
		
		//进行校验规则配置信息的合法性校验
		this.ruleCheck(validateRule);
		
		//调用校验实现方法
		return this.validateImpl(validateRule, attrValue, srcObject, parentObject);
	}
	
	/**
	 * 校验实现方法，由子类实现
	 * @param validateRule 校验规则
	 * @param attrValue 属性值
	 * @param srcObject 校验对象（完整的对象）
	 * @return 校验结果
	 */
	abstract protected JsonObject validateImpl(ValidateRule validateRule, Object attrValue, JsonObject srcObject, JsonObject parentObject) throws EngineException ;
	
	/**
	 * 对校验规则中公共属性的合规性进行校验，如果有个性的校验逻辑，子类实现的时候需要先调用基类的方法，再实现个性的逻辑。规则配置信息校验不通过时，抛出异常。
	 * @param validateRule 校验规则配置信息
	 * @throws EngineException
	 */
	protected void ruleCheck(ValidateRule validateRule) throws EngineException {
		//初始化默认提示信息
		if(validateRuleType != null) {
			defaultMessage = DEFAULT_MESSAGE_TEMPLATE.replaceAll(DEFAULT_MESSAGE_TEMPLATE_REPLACE, validateRuleType.getName());
		}
	}
	
	/**
	 * 对属性值进行空值判断
	 * @param attrValue
	 * @param message
	 * @param errorCode
	 * @param interrupt
	 * @param messages
	 * @param result
	 * @return
	 */
	protected boolean attrValueEmptyCheck(Object attrValue, String message, String errorCode, boolean interrupt, JsonArray messages, JsonObject result) {
		//如果属性值为null或空字符串，直接校验不通过
		if (attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			logger.info("DateValidator: attrValue is null!");
			JsonObject msg = new JsonObject();
			msg.addProperty(RULE_MESSAGE_KEY, message);
			msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
			messages.add(msg);
			result.addProperty(RESULT_STATUS_KEY, false);
			if (interrupt) {
				result.addProperty(RESULT_INTERRUPTED_KEY, true);
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * 组装校验信息
	 * @param attributeCode
	 * @param subAttributeCode
	 * @param message
	 * @return
	 */
	protected String processMessage(String attributeCode, String subAttributeCode, String message) {
		if(!StringUtils.isBlank(message)) {
			return message;
		}else {
			StringBuilder msgBuilder = new StringBuilder();
			if(!StringUtils.isBlank(attributeCode)) {
				msgBuilder.append(attributeCode);
				if(subAttributeCode != null && subAttributeCode.trim().length() != 0) {
					msgBuilder.append(DOT).append(subAttributeCode);
				}
				msgBuilder.append(COLON);
			}
			return msgBuilder.append(defaultMessage).toString();
		}
	}

	public ValidateRuleType getValidateRuleType() {
		return validateRuleType;
	}

	public void setValidateRuleType(ValidateRuleType validateRuleType) {
		this.validateRuleType = validateRuleType;
	}
}
