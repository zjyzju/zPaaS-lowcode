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
 * IP地址校验器IpAddrValidator的主要用来校验参数值是否是合法的IP地址。主要针对采用字符串类型定义的属性。
 */
public class IpAddrValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(IpAddrValidator.class);
	
	public static final String IP_ADDR_TYPE_KEY = "ipType";//静态常量，ip类型的Key，1:IPV4，2:IPV6，3:IPV4/IPV6
	public static final String MULTI_IP_KEY = "isMultiIp";//静态常量，是否允许多个地址的Key，true/false
	public static final String IS_CIDR_KEY = "isCidr";//静态常量，是否CIDR的Key，true/false
	public static final String SPLIT_FLAG_KEY = "splitFlag";//静态常量，分隔符的Key，默认使用逗号“,”

	public static final String DEFAULT_SPLIT_FLAG = ",";//静态常量，默认分隔符
	public static final String IP_ADDR_TYPE_V4 = "1";//ipv4
	public static final String IP_ADDR_TYPE_V6 = "2";//ipv6
	public static final String IP_ADDR_TYPE_V4V6 = "3";//ipv4/ipv6


	public static final Pattern IPV4_PATTERN = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
	public static final Pattern IPV6_PATTERN = Pattern.compile("^([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}$");
	public static final Pattern IPV6_PATTERN1 = Pattern.compile("^(([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6}))$");
	public static final Pattern IPV4_CIDR_PATTERN = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)/(1[0-9]|2[0-9]|3[0-2]|\\d)$");
	public static final Pattern IPV6_CIDR_PATTERN = Pattern.compile("^([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}/(1[0-1][0-9]|12[0-8]|[1-9][0-9]|[1-9]|\\d)$");
	public static final Pattern IPV6_CIDR_PATTERN1 = Pattern.compile("^(([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6}))/(1[0-1][0-9]|12[0-8]|[1-9][0-9]|[1-9]|\\d)$");

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
		
		String ipType = JsonUtils.getString(ruleValue, IP_ADDR_TYPE_KEY);
		boolean isCidr = JsonUtils.getBoolean(ruleValue, IS_CIDR_KEY);
		String splitFlag = JsonUtils.getString(ruleValue, SPLIT_FLAG_KEY);
		boolean isMultiIp = JsonUtils.getBoolean(ruleValue, MULTI_IP_KEY);
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);

		// 默认为IPV4
		if (StringUtils.isBlank(ipType)) {
			ipType = IP_ADDR_TYPE_V4;
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
		if(IP_ADDR_TYPE_V4.equals(ipType)) {//ipv4
			isPass = isValidIpv4(attrValue, isCidr, isMultiIp, splitFlag);
		}else if(IP_ADDR_TYPE_V6.equals(ipType)) {//ipv6
			isPass = isValidIpv6(attrValue, isCidr, isMultiIp, splitFlag);
		}else if(IP_ADDR_TYPE_V4V6.equals(ipType)) {//ipv4/ipv6
			isPass = isValidIpv4(attrValue, isCidr, isMultiIp, splitFlag) || isValidIpv6(attrValue, isCidr, isMultiIp, splitFlag);
		}else {
			logger.error("invalid ipType: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid ipType!");
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
	 * 校验是否是合法的IPV4地址
	 * @param attrValue
	 * @param isCidr
	 * @param isMultiIp
	 * @param splitFlag
	 * @return
	 */
	private boolean isValidIpv4(Object attrValue, boolean isCidr, boolean isMultiIp, String splitFlag) {
		boolean isPass = true;
		if(isCidr) {//CIDR
			if(isMultiIp) {//多CIDR校验
				String[] ipAddrs = attrValue.toString().split(splitFlag);
				for(String ipAddr : ipAddrs) {
					if(!StringUtils.isBlank(ipAddr)) {
						isPass = IPV4_CIDR_PATTERN.matcher(ipAddr).matches();
						if(!isPass) {//只要有一个多CIDR校验地址不合法，中断循环
							break;
						}
					}
				}
			}else {//单多CIDR校验校验
				isPass = IPV4_CIDR_PATTERN.matcher(attrValue.toString()).matches();
			}
		}else {//ip地址校验
			if(isMultiIp) {//多ip校验
				String[] ipAddrs = attrValue.toString().split(splitFlag);
				for(String ipAddr : ipAddrs) {
					if(!StringUtils.isBlank(ipAddr)) {
						isPass = IPV4_PATTERN.matcher(ipAddr).matches();
						if(!isPass) {//只要有一个ip地址不合法，中断循环
							break;
						}
					}
				}
			}else {//单ip校验
				isPass = IPV4_PATTERN.matcher(attrValue.toString()).matches();
			}
		}
		return isPass;
	}

	/**
	 * 校验是否是合法的IPV6地址
	 * @param attrValue
	 * @param isCidr
	 * @param isMultiIp
	 * @param splitFlag
	 * @return
	 */
	private boolean isValidIpv6(Object attrValue, boolean isCidr, boolean isMultiIp, String splitFlag) {
		boolean isPass = true;
		if(isCidr) {//CIDR
			if(isMultiIp) {//多CIDR校验
				String[] ipAddrs = attrValue.toString().split(splitFlag);
				for(String ipAddr : ipAddrs) {
					if(!StringUtils.isBlank(ipAddr)) {
						isPass = IPV6_CIDR_PATTERN.matcher(ipAddr).matches() || IPV6_CIDR_PATTERN1.matcher(ipAddr).matches();
						if(!isPass) {//只要有一个多CIDR校验地址不合法，中断循环
							break;
						}
					}
				}
			}else {//单多CIDR校验校验
				isPass = IPV6_CIDR_PATTERN.matcher(attrValue.toString()).matches() || IPV6_CIDR_PATTERN1.matcher(attrValue.toString()).matches();
			}
		}else {//ip地址校验
			if(isMultiIp) {//多ip校验
				String[] ipAddrs = attrValue.toString().split(splitFlag);
				for(String ipAddr : ipAddrs) {
					if(!StringUtils.isBlank(ipAddr)) {
						isPass = IPV6_PATTERN.matcher(ipAddr).matches() || IPV6_PATTERN1.matcher(ipAddr).matches();
						if(!isPass) {//只要有一个ip地址不合法，中断循环
							break;
						}
					}
				}
			}else {//单ip校验
				isPass = IPV6_PATTERN.matcher(attrValue.toString()).matches() || IPV6_PATTERN1.matcher(attrValue.toString()).matches();
			}
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
