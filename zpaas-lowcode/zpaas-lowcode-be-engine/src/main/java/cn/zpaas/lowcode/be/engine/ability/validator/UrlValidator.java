package cn.zpaas.lowcode.be.engine.ability.validator;

import java.util.regex.Pattern;

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
 * URL地址校验器UrlValidator的主要用来校验参数值是否是合法的URL地址/域名。主要针对采用字符串类型定义的属性。
 */
public class UrlValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(UrlValidator.class);
	
	public static final String URL_ADDR_TYPE_KEY = "urlType";//静态常量，ip类型的Key，1:URL，2:域名
	public static final String MULTI_URL_KEY = "isMultiUrl";//静态常量，是否允许多个地址的Key，true/false
	public static final String SPLIT_FLAG_KEY = "splitFlag";//静态常量，分隔符的Key，默认使用逗号“,”

	public static final String DEFAULT_SPLIT_FLAG = ",";//静态常量，默认分隔符
	public static final String URL_ADDR_TYPE_URL = "1";//URL地址
	public static final String URL_ADDR_TYPE_DOMAIN_NAME = "2";//域名
	

	public static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

	public static final Pattern URL_PATTERN = Pattern.compile("^(((http|ftp|https)://)?)((((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6})|((1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d))|(\\[([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}\\])|(\\[([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6})\\]))(:[0-9]{1,4})?(/[a-zA-Z0-9&%./-~-]*)?$");
	
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
		
		String urlType = JsonUtils.getString(ruleValue, URL_ADDR_TYPE_KEY);
		String splitFlag = JsonUtils.getString(ruleValue, SPLIT_FLAG_KEY);
		boolean isMultiUrl = JsonUtils.getBoolean(ruleValue, MULTI_URL_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);

		// 默认为url
		if (StringUtils.isBlank(urlType)) {
			urlType = URL_ADDR_TYPE_URL;
		}
		//分隔符默认为逗号
		if (StringUtils.isBlank(splitFlag)) {
			splitFlag = DEFAULT_SPLIT_FLAG;
		}

		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		//如果属性值为null，直接校验通过
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		//使用正则表达式进行校验
		if(URL_ADDR_TYPE_URL.equals(urlType)) {//url
			isPass = isValidUrl(attrValue, isMultiUrl, splitFlag);
		}else if(URL_ADDR_TYPE_DOMAIN_NAME.equals(urlType)) {//域名
			isPass = isValidDomainName(attrValue, isMultiUrl, splitFlag);
		}else {
			logger.error("invalid urlType: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid urlType!");
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
	 * 校验是否是合法的URL
	 * @param attrValue
	 * @param isMultiUrl
	 * @param splitFlag
	 * @return
	 */
	private boolean isValidUrl(Object attrValue, boolean isMultiUrl, String splitFlag) {
		boolean isPass = true;
		if(isMultiUrl) {//多url校验
			String[] urlAddrs = attrValue.toString().split(splitFlag);
			for(String urlAddr : urlAddrs) {
				if(!StringUtils.isBlank(urlAddr)) {
					isPass = URL_PATTERN.matcher(urlAddr).matches();
					if(!isPass) {//只要有一个url地址不合法，中断循环
						break;
					}
				}
			}
		}else {//单url校验
			isPass = URL_PATTERN.matcher(attrValue.toString()).matches();
		}
		return isPass;
	}

	/**
	 * 校验是否是合法的域名
	 * @param attrValue
	* @param isMultiUrl
	 * @param splitFlag
	 * @return
	 */
	private boolean isValidDomainName(Object attrValue,boolean isMultiUrl, String splitFlag) {
		boolean isPass = true;
		if(isMultiUrl) {//多域名校验
			String[] domainNames = attrValue.toString().split(splitFlag);
			for(String domainName : domainNames) {
				if(!StringUtils.isBlank(domainName)) {
					isPass = DOMAIN_NAME_PATTERN.matcher(domainName).matches();
					if(!isPass) {//只要有一个域名不合法，中断循环
						break;
					}
				}
			}
		}else {//单域名校验
			isPass = DOMAIN_NAME_PATTERN.matcher(attrValue.toString()).matches();
		}
		return isPass;
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
	}
}
