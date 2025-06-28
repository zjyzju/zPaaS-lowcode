package cn.zpaas.lowcode.be.engine.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.validator.Validator;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.ValidateRuleType;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 实现参数校验能力的能力
 */
public class ValidateAbility {
	private static Logger logger = LoggerFactory.getLogger(ValidateAbility.class);

	public static final String OBJECT_KEY = "object"; //对象key
	public static final String PARENT_OBJECT_KEY = "parent"; //上级对象key
	

	// DataTransferAbility实例，用于实现单例模式
	private static ValidateAbility instance = null;

	// 用来缓存ValidateRuleGroup对象数据的Map
	private Map<String, Map<String, ValidateRuleGroup>> ruleGroupMap = new HashMap<>();
	// 用来缓存ValidateRuleType对象
	private List<ValidateRuleType> validateRuleTypes;
	private Map<String, ValidateRuleType> validateRuleTypeMap;
	// 用于缓存校验器实例，Key为validateRuleTypeId
	private Map<String, Validator> validatorMap = new HashMap<>();

	// 私有化构造函数
	private ValidateAbility() {

	}

	// 获取DataTransferAbility实例，用于实现单例模式
	public static ValidateAbility getInstance() {
		return instance;
	}

	/**
	 * ValidateAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化DataTransferAbility对象
		ValidateAbility ability = new ValidateAbility();

		// 加载并缓存ValidateRuleType数据
		ability.validateRuleTypes = initService.listValidateRuleTypes();
		ability.validateRuleTypeMap = new HashMap<>();
		if (ability.validateRuleTypes != null && !ability.validateRuleTypes.isEmpty()) {
			Validator validator = null;
			for (ValidateRuleType validateRuleType : ability.validateRuleTypes) {
				if (validateRuleType.getRuleClass() != null && validateRuleType.getRuleClass().trim().length() > 0) {
					try {
						validator = (Validator) Class.forName(validateRuleType.getRuleClass()).getDeclaredConstructor().newInstance();
						validator.setValidateRuleType(validateRuleType);
						ability.validatorMap.put(validateRuleType.getId(), validator);
					} catch (Exception e) {
						logger.error("Init validateRuleType's ruleClass failed: {}", validateRuleType.getRuleClass());
					}
				} else {
					logger.error("Init validateRuleType's ruleClass is null: {}", validateRuleType.getId());
				}
				ability.validateRuleTypeMap.put(validateRuleType.getId(), validateRuleType);
			}
		} else {
			logger.info("no valid validateRuleTypes.");
		}

		// 加载ValidateRuleGroup数据
		List<ValidateRuleGroup> ruleGroups = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			ruleGroups = initService.listValidateRuleGroups();
		} else {// 加载指定业务系统的数据
			ruleGroups = initService.listValidateRuleGroupBySystemId(systemId);
		}

		if (ruleGroups != null && !ruleGroups.isEmpty()) {
			// 加载所有ValidateRule
			List<ValidateRule> allValidateRules = null;
			if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
				allValidateRules = initService.listValidateRule();
			} else {// 加载指定业务系统的数据
				allValidateRules = initService.listValidateRuleBySystem(systemId);
			}
			if (allValidateRules == null) {
				allValidateRules = new ArrayList<>();
			}
			// 按validateRuleGroupId进行分组
			Map<String, List<ValidateRule>> validateRuleMap = allValidateRules.stream()
					.collect(Collectors.groupingBy(ValidateRule::getRuleGroupId));

			// 循环处理每条ValidateRuleGroup数据
			for (ValidateRuleGroup validateRuleGroup : ruleGroups) {
				// systemId为空的数据不缓存
				if (validateRuleGroup.getSystemId() == null || validateRuleGroup.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将validateRuleGroup对象加入缓存
				Map<String, ValidateRuleGroup> map = ability.ruleGroupMap.get(validateRuleGroup.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					ability.ruleGroupMap.put(validateRuleGroup.getSystemId(), map);
				}
				map.put(validateRuleGroup.getId(), validateRuleGroup);
				// 加载ValidateRule数据
				validateRuleGroup.setValidateRules(validateRuleMap.get(validateRuleGroup.getId()));

				// 处理校验对象是服务方法入参的情况，设置attributeCode和subAttributeCode
				if (validateRuleGroup.getValidateRules() != null) {
					Attribute attr = null;
					for (ValidateRule rule : validateRuleGroup.getValidateRules()) {
						rule.setValidateRuleType(ability.validateRuleTypeMap.get(rule.getRuleTypeId()));
						if (ManagedObjectType.SERVICE_METHOD.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(rule.getAttributeId());
							attr = DomainObjectProxy.getInstance().getAttribute(rule.getSystemId(),
									rule.getSubAttributeId());
							if (attr != null) {
								rule.setSubAttributeCode(attr.getCode());
							} else {
								attr = ValueObjectProxy.getInstance().getAttribute(rule.getSystemId(),
										rule.getSubAttributeId());
								if (attr != null) {
									rule.setSubAttributeCode(attr.getCode());
								} else {
									logger.error("invalid subAttributeId: {}", rule.getSubAttributeId());
								}
							}
						} else if (ManagedObjectType.DOMAIN_OBJECT.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(DomainObjectProxy.getInstance()
									.getAttribute(rule.getSystemId(), rule.getAttributeId()).getCode());
						} else if (ManagedObjectType.VALUE_OBJECT.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(ValueObjectProxy.getInstance()
									.getAttribute(rule.getSystemId(), rule.getAttributeId()).getCode());
						} else {
							logger.error("invalid objectType: {}", validateRuleGroup.getObjectType());
						}
					}
				}
			}
		} else {
			logger.info("no valid ruleGroups.");
		}

		

		// 初始化完成，将ValidateAbility对象赋值给属性instance
		instance = ability;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		// 加载并缓存ValidateRuleType数据
		// instance.validateRuleTypes = initService.listValidateRuleTypes();
		// instance.validateRuleTypeMap = new HashMap<>();
		// if (instance.validateRuleTypes != null && !instance.validateRuleTypes.isEmpty()) {
		// 	Validator validator = null;
		// 	for (ValidateRuleType validateRuleType : instance.validateRuleTypes) {
		// 		if (validateRuleType.getRuleClass() != null && validateRuleType.getRuleClass().trim().length() > 0) {
		// 			try {
		// 				validator = (Validator) Class.forName(validateRuleType.getRuleClass()).getDeclaredConstructor().newInstance();
		// 				validator.setValidateRuleType(validateRuleType);
		// 				instance.validatorMap.put(validateRuleType.getId(), validator);
		// 			} catch (Exception e) {
		// 				logger.error("Init validateRuleType's ruleClass failed: {}", validateRuleType.getRuleClass());
		// 			}
		// 		} else {
		// 			logger.error("Init validateRuleType's ruleClass is null: {}", validateRuleType.getId());
		// 		}
		// 		instance.validateRuleTypeMap.put(validateRuleType.getId(), validateRuleType);
		// 	}
		// } else {
		// 	logger.info("no valid validateRuleTypes.");
		// }

		// 加载ValidateRuleGroup数据
		List<ValidateRuleGroup> ruleGroups = initService.listValidateRuleGroupBySystemId(systemId);
		Map<String, ValidateRuleGroup> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(ruleGroups)) {
			// 加载所有ValidateRule
			List<ValidateRule> allValidateRules = initService.listValidateRuleBySystem(systemId);
			if (allValidateRules == null) {
				allValidateRules = new ArrayList<>();
			}
			// 按validateRuleGroupId进行分组
			Map<String, List<ValidateRule>> validateRuleMap = allValidateRules.stream().collect(Collectors.groupingBy(ValidateRule::getRuleGroupId));

			// 循环处理每条ValidateRuleGroup数据
			for (ValidateRuleGroup validateRuleGroup : ruleGroups) {
				// systemId为空的数据不缓存
				if (validateRuleGroup.getSystemId() == null || validateRuleGroup.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将validateRuleGroup对象加入缓存
				map.put(validateRuleGroup.getId(), validateRuleGroup);
				// 加载ValidateRule数据
				validateRuleGroup.setValidateRules(validateRuleMap.get(validateRuleGroup.getId()));

				// 处理校验对象是服务方法入参的情况，设置attributeCode和subAttributeCode
				if (validateRuleGroup.getValidateRules() != null) {
					Attribute attr = null;
					for (ValidateRule rule : validateRuleGroup.getValidateRules()) {
						rule.setValidateRuleType(instance.validateRuleTypeMap.get(rule.getRuleTypeId()));
						if (ManagedObjectType.SERVICE_METHOD.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(rule.getAttributeId());
							attr = DomainObjectProxy.getInstance().getAttribute(rule.getSystemId(),
									rule.getSubAttributeId());
							if (attr != null) {
								rule.setSubAttributeCode(attr.getCode());
							} else {
								attr = ValueObjectProxy.getInstance().getAttribute(rule.getSystemId(),
										rule.getSubAttributeId());
								if (attr != null) {
									rule.setSubAttributeCode(attr.getCode());
								} else {
									logger.error("invalid subAttributeId: {}", rule.getSubAttributeId());
								}
							}
						} else if (ManagedObjectType.DOMAIN_OBJECT.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(DomainObjectProxy.getInstance()
									.getAttribute(rule.getSystemId(), rule.getAttributeId()).getCode());
						} else if (ManagedObjectType.VALUE_OBJECT.equals(validateRuleGroup.getObjectType())) {
							rule.setAttributeCode(ValueObjectProxy.getInstance()
									.getAttribute(rule.getSystemId(), rule.getAttributeId()).getCode());
						} else {
							logger.error("invalid objectType: {}", validateRuleGroup.getObjectType());
						}
					}
				}
			}
		} 
		instance.ruleGroupMap.put(systemId, map);

		

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和参数校验规则组标识获取对应的参数校验规则组对象
	 * 
	 * @param systemId    业务系统标识
	 * @param ruleGroupId 参数校验规则组标识
	 * @return 返回ValidateRuleGroup对象
	 */
	public ValidateRuleGroup getValidateRuleGroup(String systemId, String ruleGroupId) {
		if (systemId == null || systemId.trim().length() == 0) {
			return null;
		}
		Map<String, ValidateRuleGroup> map = ruleGroupMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(ruleGroupId);
	}

	/**
	 * 参数校验核心逻辑的实现方法，输入参数包括要执行校验规则组的标识，要校验的对象，如果要递归的执行对子属性对象的校验，需要传入子属性对象采用的校验规则组标识。
	 * 
	 * @param systemId         业务系统标识
	 * @param ruleGroupId      校验规则组标识
	 * @param objectToValidate 需要校验的目标对象
	 * @return 返回校验结果
	 * @throws EngineException
	 */
	public JsonObject validate(String systemId, String ruleGroupId, JsonObject objectToValidate, JsonObject parentObject)
			throws EngineException {
		// 如果systemId或ruleGroupId为空，直接返回
		if (StringUtils.isBlank(systemId) || StringUtils.isBlank(ruleGroupId)) {
			logger.error("systemId or ruleGroupId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId or ruleGroupId is null");
		}

		// 初始化校验结果对象
		JsonObject result = new JsonObject();
		JsonArray messages = new JsonArray();
		result.add(Validator.RESULT_MESSAGES_KEY, messages);
		result.addProperty(Validator.RESULT_STATUS_KEY, true);

		// 获取对应的ValidateRuleGroup
		ValidateRuleGroup validateRuleGroup = getValidateRuleGroup(systemId, ruleGroupId);

		// 如果获取不到有效的对象，默认为通过
		if (validateRuleGroup == null || CollectionUtils.isEmpty(validateRuleGroup.getValidateRules())) {
			logger.info("can't get valid validateRuleGroup or ValidateRules. {}", ruleGroupId);
			return result;
		}

		// 目标对象都不能为空
		if (JsonUtils.isEmpty(objectToValidate)) {
			logger.error("objectToValidate is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "objectToValidate is null");
		}
		
		// 循环处理每一个校验规则
		Validator validator = null;
		Object value = null;
		boolean isValid = true;
		// 返回值的格式为：
		// {
		// status：校验是否通过，true/false
		// messages: [
		// {
		// message：提示信息，主要是未通过情况下的错误信息
		// errorCode：错误码
		// }
		// ]
		// }

		JsonObject ruleResult = null;
		for (ValidateRule validateRule : validateRuleGroup.getValidateRules()) {
			// 未配置属性或属性编码为空，则跳过该规则
			if (StringUtils.isBlank(validateRule.getAttributeCode())) {
				logger.error("validate attribute code is null!");
				continue;
			}
			validator = validatorMap.get(validateRule.getRuleTypeId());
			// 获取目标属性值
			Object attrObject = JsonUtils.getObject(objectToValidate, validateRule.getAttributeCode());
			if (StringUtils.isBlank(validateRule.getSubAttributeCode())) {
				value = attrObject;
				// 执行校验器的校验方法
				ruleResult = validator.validate(validateRule, value, objectToValidate, parentObject);
				// 校验未通过时
				if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
					isValid = false;
					messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
					// 如果校验规则配置需要中断校验
					if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
						result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
						break;
					}
				}
			} else {
				if (attrObject instanceof JsonArray) {// 列表
					JsonArray attrObjects = (JsonArray) attrObject;
					for (int i = 0; i < attrObjects.size(); i++) {
						value = JsonUtils
								.toObject(attrObjects.get(i).getAsJsonObject().get(validateRule.getSubAttributeCode()));
						// 执行校验器的校验方法
						ruleResult = validator.validate(validateRule, value, objectToValidate, parentObject);
						// 校验未通过时
						if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
							isValid = false;
							messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
							// 如果校验规则配置需要中断校验
							if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
								result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
								break;
							}
						}
					}
				} else if (attrObject instanceof JsonObject) {// 对象
					value = JsonUtils.toObject(((JsonObject) attrObject).get(validateRule.getSubAttributeCode()));
					// 执行校验器的校验方法
					ruleResult = validator.validate(validateRule, value, objectToValidate, parentObject);
					// 校验未通过时
					if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
						isValid = false;
						messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
						// 如果校验规则配置需要中断校验
						if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
							result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
							break;
						}
					}
				} else {// 配置错误
					throw new EngineException(ResultStatus.INTERNAL_ERROR,
							"invalid validateRule: the param should be an object!");
				}
			}
		}
		//进行对象类子属性的校验
		String subRuleGroup = null;
		if (!StringUtils.isBlank(validateRuleGroup.getSubRuleGroups())) {
			JsonObject subRuleGroups = JsonUtils.toJsonObject(validateRuleGroup.getSubRuleGroups());
			JsonObject subParentObject = new JsonObject();
			subParentObject.add(OBJECT_KEY, objectToValidate);
			subParentObject.add(PARENT_OBJECT_KEY, parentObject);
			for(String attrCode :subRuleGroups.keySet()) {
				subRuleGroup = JsonUtils.getString(subRuleGroups, attrCode);
				// 进行递归校验
				if (!StringUtils.isBlank(subRuleGroup)) {
					Object subObject = JsonUtils.eval(objectToValidate, attrCode);//jsonpath
					if(subObject == null) {
						continue;
					}
					if (subObject instanceof JsonArray) {
						ruleResult = this.validate(systemId, subRuleGroup, (JsonArray) subObject, subParentObject);
						// 校验未通过时
						if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
							isValid = false;
							messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
							// 如果校验规则配置需要中断校验
							if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
								result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
								break;
							}
						}
					} else if (subObject instanceof JsonObject) {
						ruleResult = this.validate(systemId, subRuleGroup, (JsonObject) subObject, subParentObject);
						// 校验未通过时
						if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
							isValid = false;
							messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
							// 如果校验规则配置需要中断校验
							if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
								result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
								break;
							}
						}
					} else {
						throw new EngineException(ResultStatus.INTERNAL_ERROR,
								"invalid validateRule: the param should be an object!");
					}
				}
			}
		}
		
		if (!isValid) {
			result.addProperty(Validator.RESULT_STATUS_KEY, false);
		}

		return result;
	}

	/**
	 * 参数校验核心逻辑的实现方法，输入参数包括要执行校验规则组的标识，要校验的对象，如果要递归的执行对子属性对象的校验，需要传入子属性对象采用的校验规则组标识。
	 * 
	 * @param systemId         业务系统标识
	 * @param ruleGroupId      校验规则组标识
	 * @param objectToValidate 需要校验的目标对象
	 * @return 返回校验结果
	 * @throws EngineException
	 */
	public JsonObject validate(String systemId, String ruleGroupId, JsonArray objectsToValidate, JsonObject parentObject)
			throws EngineException {
		if (logger.isDebugEnabled()) {
			logger.debug("Data validate is started.");
		}
		// 如果systemId或ruleGroupId为空，直接返回
		if (systemId == null || systemId.trim().length() == 0 || ruleGroupId == null
				|| ruleGroupId.trim().length() == 0) {
			logger.error("systemId or ruleGroupId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId or ruleGroupId is null");
		}

		// 目标对象都不能为空
		if (objectsToValidate == null || objectsToValidate.isEmpty()) {
			logger.error("objectsToValidate is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "objectsToValidate is null");
		}
		// 初始化校验结果对象
		JsonObject result = new JsonObject();
		JsonArray messages = new JsonArray();
		result.addProperty(Validator.RESULT_STATUS_KEY, true);
		result.add(Validator.RESULT_MESSAGES_KEY, messages);
		JsonObject ruleResult = null;
		boolean isValid = true;
		for (int i = 0; i < objectsToValidate.size(); i++) {
			ruleResult = this.validate(systemId, ruleGroupId, objectsToValidate.get(i).getAsJsonObject(), parentObject);
			// 校验未通过时
			if (ruleResult != null && !JsonUtils.getBoolean(ruleResult, Validator.RESULT_STATUS_KEY)) {
				isValid = false;
				messages.addAll(JsonUtils.getJsonArray(ruleResult, Validator.RESULT_MESSAGES_KEY));
				// 如果校验规则配置需要中断校验
				if (JsonUtils.getBoolean(ruleResult, Validator.RESULT_INTERRUPTED_KEY)) {
					result.addProperty(Validator.RESULT_INTERRUPTED_KEY, true);
					break;
				}
			}
		}
		if (!isValid) {
			result.addProperty(Validator.RESULT_STATUS_KEY, false);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Data validate is finished. {}", result.toString());
		}
		return result;
	}

	public List<ValidateRuleType> getValidateRuleTypes() {
		return validateRuleTypes;
	}

	public ValidateRuleType getValidateRuleType(String validateRuleTypeId) {
		return validateRuleTypeMap.get(validateRuleTypeId);
	}
}
