package cn.zpaas.lowcode.be.engine.proxy;

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
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.command.DomainCommand;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy DomainObjectProxy是领域对象的解析和执行引擎，初始化时只初始化一个实例，并在该实例中缓存所有的领域对象数据。
 *         领域对象的数据通过AttributedObjectContainer对象保存。
 */
public class DomainObjectProxy {
	private static Logger logger = LoggerFactory.getLogger(DomainObjectProxy.class);

	// DomainObjectProxy实例，用于实现单例模式
	private static DomainObjectProxy instance = null;

	// 用于缓存领域对象，第一层Key为systemId，第二层Key为domainObjectId
	private Map<String, Map<String, DomainObject>> domainObjectMap = new HashMap<>();
	// 用于缓存属性对象，便于使用，第一层Key为systemId，第二层Key为attributeId
	private Map<String, Map<String, Attribute>> attributeMap = new HashMap<>();
	// 用于缓存方法对象，便于使用，第一层Key为systemId，第二层Key为operationId
	private Map<String, Map<String, Operation>> operationMap = new HashMap<>();

	// 私有化构造函数
	private DomainObjectProxy() {

	}

	// 获取DomainObjectProxy实例，用于实现单例模式
	public static DomainObjectProxy getInstance() {
		return instance;
	}

	/**
	 * 根据业务系统标识和领域对象标识，获取领域对象信息
	 * 
	 * @param systemId       业务系统标识
	 * @param domainObjectId 领域对象标识
	 * @return 返回领域对象
	 */
	public DomainObject getDomainObject(String systemId, String domainObjectId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, DomainObject> map = domainObjectMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(domainObjectId);
	}

	/**
	 * 根据业务系统标识和属性标识，获取属性对象信息
	 * 
	 * @param systemId    业务系统标识
	 * @param attributeId 属性标识
	 * @return 返回属性对象
	 */
	public Attribute getAttribute(String systemId, String attributeId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, Attribute> map = attributeMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(attributeId);
	}
	
	/**
	 * 根据业务系统标识和方法标识，获取方法对象信息
	 * 
	 * @param systemId    业务系统标识
	 * @param operationId 方法标识
	 * @return 返回方法对象
	 */
	public Operation getOperation(String systemId, String operationId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, Operation> map = operationMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(operationId);
	}

	/**
	 * DomainObjectProxy类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化DomainObjectProxy对象
		DomainObjectProxy proxy = new DomainObjectProxy();

		List<DomainObject> domainObjects = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			domainObjects = initService.listdDomainObjects();
		} else {// 加载指定业务系统的数据
			domainObjects = initService.listDomainObjectBySystemId(systemId);
		}

		if (!CollectionUtils.isEmpty(domainObjects)) {
			//加载所有的方法Operation
			List<Operation> allOperations = null;
			if(!StringUtils.isBlank(systemId)) {
				allOperations = initService.listOperationByObjectTypeAndSystem(ManagedObjectType.DOMAIN_OBJECT, systemId);
			}else {
				allOperations = initService.listOperationByObjectType(ManagedObjectType.DOMAIN_OBJECT);
			}
			if(allOperations == null) {
				allOperations = new ArrayList<>();
			}
			//按归属对象进行分组，形成Map
			Map<String, List<Operation>> operationMap = allOperations.stream().collect(Collectors.groupingBy(Operation::getObjectId));
			
			
			//加载所有的参数
			List<Parameter> allParameters = null;
			if(!StringUtils.isBlank(systemId)) {
				allParameters = initService.listParametersBySystem(systemId);
			}else {
				allParameters = initService.listParameters();
			}
			if(allParameters == null) {
				allParameters = new ArrayList<>();
			}
			//按归属方法进行分组，形成Map
			Map<String, List<Parameter>> parameterMap = allParameters.stream().collect(Collectors.groupingBy(Parameter::getOperationId));
			
			// 加载所有的属性信息
			List<Attribute> allAttributes = null;
			if(!StringUtils.isBlank(systemId)) {
				allAttributes = initService.listAttributeByObjectTypeAndSystem(ManagedObjectType.DOMAIN_OBJECT, systemId);
			}else {
				allAttributes = initService.listAttributeByObjectType(ManagedObjectType.DOMAIN_OBJECT);
			}
			if(allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			//按归属的对象进行分组，形成Map
			Map<String, List<Attribute>> attributeMap = allAttributes.stream().collect(Collectors.groupingBy(Attribute::getObjectId));
			
			
			// 循环处理每一条领域对象
			for (DomainObject domainObject : domainObjects) {
				// 当领域对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(domainObject.getSystemId())) {
					logger.info("domainObject's systemId is null. {}", domainObject.getId());
					continue;
				}
				// 将领域对象加入缓存
				Map<String, DomainObject> map = proxy.domainObjectMap.get(domainObject.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					proxy.domainObjectMap.put(domainObject.getSystemId(), map);
				}
				map.put(domainObject.getId(), domainObject);

				// 获取并缓存方法信息
				List<Operation> operations = operationMap.get(domainObject.getId());
				domainObject.setOperations(operations);
				if (!CollectionUtils.isEmpty(operations)) {
					// 循环处理每一个Operation对象
					for (Operation operation : operations) {						
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(operation.getSystemId())) {
							logger.info("operation's systemId is null. {}", operation.getId());
							continue;
						}
						//将方法对象加入缓存
						Map<String, Operation> operMap = proxy.operationMap.get(operation.getSystemId());
						if (operMap == null) {
							operMap = new HashMap<>();
							proxy.operationMap.put(operation.getSystemId(), operMap);
						}
						operMap.put(operation.getId(), operation);
						
						// 获取并初始化方法对应的参数
						List<Parameter> parameters = parameterMap.get(operation.getId());
						if (!CollectionUtils.isEmpty(parameters)) {
							// 出参，只有一个
							Parameter outParam = null;
							// 入参，允许有多个
							List<Parameter> inParam = new ArrayList<>();
							for (Parameter parameter : parameters) {
								if (YesOrNo.YES.equals(parameter.getIsInParam())) {
									inParam.add(parameter);
									continue;
								}
								if (YesOrNo.NO.equals(parameter.getIsInParam())) {
									outParam = parameter;
								}
							}
							operation.setOutParam(outParam);
							operation.setInParams(inParam);
						}
					}
				}

				// 获取并缓存领域对象的属性信息
				List<Attribute> attributes = attributeMap.get(domainObject.getId());
				domainObject.setAttributes(attributes);
				// 将属性加入属性缓存
				if (!CollectionUtils.isEmpty(attributes)) {
					for (Attribute attribute : attributes) {
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(attribute.getSystemId())) {
							logger.info("attribute's systemId is null. {}", attribute.getId());
							continue;
						}
						Map<String, Attribute> attrMap = proxy.attributeMap.get(attribute.getSystemId());
						if (attrMap == null) {
							attrMap = new HashMap<>();
							proxy.attributeMap.put(attribute.getSystemId(), attrMap);
						}
						attrMap.put(attribute.getId(), attribute);
					}
				}

			}
		} else {
			logger.info("no valid DomainObject.");
		}

		// 初始化完成将proxy赋值给instance属性
		instance = proxy;

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
		List<DomainObject> domainObjects = initService.listDomainObjectBySystemId(systemId);
		Map<String, DomainObject> map = new HashMap<>();
		Map<String, Operation> operMap = new HashMap<>();
		Map<String, Attribute> attrMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(domainObjects)) {
			//加载所有的方法Operation
			List<Operation> allOperations = initService.listOperationByObjectTypeAndSystem(ManagedObjectType.DOMAIN_OBJECT, systemId);
			if(allOperations == null) {
				allOperations = new ArrayList<>();
			}
			//按归属对象进行分组，形成Map
			Map<String, List<Operation>> operationMap = allOperations.stream().collect(Collectors.groupingBy(Operation::getObjectId));
			
			
			//加载所有的参数
			List<Parameter> allParameters = initService.listParametersBySystem(systemId);
			if(allParameters == null) {
				allParameters = new ArrayList<>();
			}
			//按归属方法进行分组，形成Map
			Map<String, List<Parameter>> parameterMap = allParameters.stream().collect(Collectors.groupingBy(Parameter::getOperationId));
			
			// 加载所有的属性信息
			List<Attribute> allAttributes = initService.listAttributeByObjectTypeAndSystem(ManagedObjectType.DOMAIN_OBJECT, systemId);
			if(allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			//按归属的对象进行分组，形成Map
			Map<String, List<Attribute>> attributeMap = allAttributes.stream().collect(Collectors.groupingBy(Attribute::getObjectId));
			
			
			// 循环处理每一条领域对象
			for (DomainObject domainObject : domainObjects) {
				// 当领域对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(domainObject.getSystemId())) {
					logger.info("domainObject's systemId is null. {}", domainObject.getId());
					continue;
				}
				map.put(domainObject.getId(), domainObject);

				// 获取并缓存方法信息
				List<Operation> operations = operationMap.get(domainObject.getId());
				domainObject.setOperations(operations);
				if (!CollectionUtils.isEmpty(operations)) {
					// 循环处理每一个Operation对象
					for (Operation operation : operations) {						
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(operation.getSystemId())) {
							logger.info("operation's systemId is null. {}", operation.getId());
							continue;
						}
						//将方法对象加入缓存
						operMap.put(operation.getId(), operation);
						
						// 获取并初始化方法对应的参数
						List<Parameter> parameters = parameterMap.get(operation.getId());
						if (!CollectionUtils.isEmpty(parameters)) {
							// 出参，只有一个
							Parameter outParam = null;
							// 入参，允许有多个
							List<Parameter> inParam = new ArrayList<>();
							for (Parameter parameter : parameters) {
								if (YesOrNo.YES.equals(parameter.getIsInParam())) {
									inParam.add(parameter);
									continue;
								}
								if (YesOrNo.NO.equals(parameter.getIsInParam())) {
									outParam = parameter;
								}
							}
							operation.setOutParam(outParam);
							operation.setInParams(inParam);
						}
					}
				}

				// 获取并缓存领域对象的属性信息
				List<Attribute> attributes = attributeMap.get(domainObject.getId());
				domainObject.setAttributes(attributes);
				// 将属性加入属性缓存
				if (!CollectionUtils.isEmpty(attributes)) {
					for (Attribute attribute : attributes) {
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(attribute.getSystemId())) {
							logger.info("attribute's systemId is null. {}", attribute.getId());
							continue;
						}
						attrMap.put(attribute.getId(), attribute);
					}
				}

			}
		} 
		instance.domainObjectMap.put(systemId, map);
		instance.operationMap.put(systemId, operMap);
		instance.attributeMap.put(systemId, attrMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	public Object execute(DomainCommand command, BusinessFlowContext context) throws EngineException {
		// 获取到要调用方法的目标领域对象
		DomainObject domainObject = this.getDomainObject(command.getSystemId(), command.getDomainObjectId());

		// 如果服务对象为空或其方法列表为空，则返回null
		if (domainObject == null || CollectionUtils.isEmpty(domainObject.getOperations())) {
			logger.error("T[{}] DomainObject is null or has zero operation: {}", command.getTenantId(), command);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "DomainObject is null or has zero operation");
		}

		// 获取要调用的目标Operation对象
		Operation operation = getOperation(command.getSystemId(), command.getOperationId());
		
		// 未找到目标方法，直接返回null
		if (operation == null || operation.getBusinessFlowId() == null) {
			logger.error("T[{}] operation not found or businessFlowId is null: {}", command.getTenantId(), command);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "operation not found or businessFlowId is null");
		}

		logger.info("T[{}] DomainObject({})'s operation({}) is invoked.", command.getTenantId(), domainObject.getCode(), operation.getCode());
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] operation's reqData is {}", command.getTenantId(), command.getReqData());
		}
		
		
		// 进行输入参数格式校验
		if (!CollectionUtils.isEmpty(operation.getInParams())) {
			// 当方法有输入参数时，传入的参数不能为空
			if (command.getReqData() == null) {
				logger.error("T[{}] operation has in params, reqData can't be null: {}", command.getTenantId(), command);
				throw new EngineException(ResultStatus.INTERNAL_ERROR,
						"operation has in params, reqData can't be null");
			}
			// 默认格式相符
			boolean paramValid = true;
			// 循环每一个输入参数
			for (Parameter param : operation.getInParams()) {
				// 如果输入参数中不包含当前参数，则认为格式不符
				if (!command.getReqData().has(param.getCode())) {
					logger.error("T[{}] reqData not contain param: {}", command.getTenantId(), param.getCode());
					paramValid = false;
					break;
				}
				// 如果当前参数是自定义类型，获取参数的定义信息
				AttributedObject type = null;
				if (ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
					type = DomainObjectProxy.getInstance().getDomainObject(command.getSystemId(),
							param.getParamClass());
				} else if (ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
					type = ValueObjectProxy.getInstance().getValueObject(command.getSystemId(),
							param.getParamClass());
				} else {
					continue;
				}

				// 未获取到定义信息，作为非自定义类型，则当前参数认为符合（JAVA原生类型，不作校验）
				if (type == null) {
					continue;
				}

				// 如果是列表类型
				if (YesOrNo.YES.equals(param.getIsListParam()) && command.getReqData().get(param.getCode()) != null) {
					// 如果类型不是JsonArray，则认为格式不符
					if (!(command.getReqData().get(param.getCode()).isJsonArray())) {
						logger.error("T[{}] reqData contain param: {} is not type of JsonArray", command.getTenantId(), param.getCode());
						paramValid = false;
						break;
					}
					// 循环判断列表中的每个元素
					JsonArray array = JsonUtils.getJsonArray(command.getReqData(), param.getCode());
					for (int i = 0; i < array.size(); i++) {
						// 如果不是JsonObject类型，则认为格式不符
						if (!(array.get(i).isJsonObject())) {
							logger.error("T[{}] reqData contain param: {}'s element is not type of JsonObject", command.getTenantId(), param.getCode());
							paramValid = false;
							break;
						}
						// 校验元素的格式与定义的格式是否相符
						if (!AttributedObject.validateAttributedObject(type, array.get(i).getAsJsonObject())) {
							logger.error("T[{}] reqData param: {}'s element is not valid with {}", command.getTenantId(), param.getCode(),
									type.getCode());
							paramValid = false;
							break;
						}
					}
					if (!paramValid) {
						break;
					}
				} else {// 非列表类型
						// 如果参数值为空，则不作校验
					if (command.getReqData().get(param.getCode()) == null) {
						continue;
					}
					// 如果参数值类型不是JsonObject类型，则认为不符
					if (!(command.getReqData().get(param.getCode()).isJsonObject())) {
						logger.error("T[{}] reqData contain param: {} is not type of JsonObject", command.getTenantId(), param.getCode());
						paramValid = false;
						break;
					}
					// 校验元素的格式与定义的格式是否相符
					if (!AttributedObject.validateAttributedObject(type, JsonUtils.getJsonObject(command.getReqData(), param.getCode()))) {
						logger.error("T[{}] reqData param: {} is not valid with {}", command.getTenantId(), param.getCode(), type.getCode());
						paramValid = false;
						break;
					}
				}
			}
			if (!paramValid) {
				logger.error("T[{}] reqData's format is not valid with operation's param definition", command.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "reqData's format is not valid with operation's param definition");
			}
		}

		// 初始化业务流命令对象
		BusinessFlowCommand businessFlowCommand = new BusinessFlowCommand();
		businessFlowCommand.setBusinessFlowId(operation.getBusinessFlowId());
		businessFlowCommand.setSystemId(command.getSystemId());
		businessFlowCommand.setReqData(command.getReqData());
		businessFlowCommand.setAttributedObject(command.getAttributedObject());

		// 调用BusinessFlowProxy的execute方法
		Object result = BusinessFlowProxy.getInstance().execute(businessFlowCommand, context);
		
		if(logger.isTraceEnabled()) {
			if(result != null) {
				logger.trace("T[{}] Operation({})'s return value class({}), value: {}.", command.getTenantId(), command.getOperationId(), result.getClass(), result);
			}else {
				logger.trace("T[{}] Operation({})'s return value is null.", command.getTenantId(), command.getOperationId());
			}		
		}
		logger.info("T[{}] DomainObject({})'s operation({}) is finished.", command.getTenantId(), domainObject.getCode(), operation.getCode());
		// 当返回结果为null时，直接返回，不不进行格式校验
		if (result == null) {
			return result;
		}
		
		//方法有返回值时，进行格式校验
		if(operation.getOutParam() != null) {
			//获取自定义对象
			AttributedObject attributedObject = null;
			if(ManagedObjectType.DOMAIN_OBJECT.equals(operation.getOutParam().getParamType())) {//返回值是领域对象
				attributedObject = DomainObjectProxy.getInstance().getDomainObject(command.getSystemId(), operation.getOutParam().getParamClass());
			}else if(ManagedObjectType.VALUE_OBJECT.equals(operation.getOutParam().getParamType())) {//返回值是值传递对象
				attributedObject = ValueObjectProxy.getInstance().getValueObject(command.getSystemId(), operation.getOutParam().getParamClass());
			}else {//返回值是源生JAVA类
				try {
					//判断类型是否一致
					@SuppressWarnings("rawtypes")
					Class outParamClass = Class.forName(operation.getOutParam().getParamClass());
					if(outParamClass.isInstance(result)) {//一致时直接返回
						return result;
					}else {
						logger.error("T[{}] result's type {} is not valid with operation's outParam type {}", command.getTenantId(), result.getClass(), outParamClass);
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's type is not valid with operation's outParam type");
					}
				} catch (ClassNotFoundException e) {
					logger.error("T[{}] operation's outParam type is invalid {}", command.getTenantId(), operation.getOutParam().getParamClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "operation's outParam type is invalid");
				}
			}
			
			if(YesOrNo.YES.equals(operation.getOutParam().getIsListParam())) {
				if(!(result instanceof JsonArray)) {
					logger.error("T[{}] result's type must be JsonArray.", command.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's type must be JsonArray.");
				}
				
				JsonArray array = (JsonArray) result;
				boolean valid = true;
				for(int i=0; i<array.size(); i++) {
					if(array.get(i).isJsonArray()) {//JsonArray套JsonArray是不合法的返回值
						valid = false;
						break;
					}
					if(!AttributedObject.validateAttributedObject(attributedObject, array.get(i).getAsJsonObject())) {
						valid = false;
						break;
					}
				}
				if(valid) {
					return result;
				}else {
					logger.error("T[{}] result's element type must be invalid with {}.", command.getTenantId(), attributedObject.getCode());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's element type is not valid.");
				}
				
			} else {
				if(!(result instanceof JsonObject)) {
					logger.error("T[{}] result's type must be JsonObject.", command.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's type must be JsonObject.");
				}
				if(!AttributedObject.validateAttributedObject(attributedObject, (JsonObject)result)) {
					logger.error("T[{}] result's format is not valid with operation's outParam {}",  command.getTenantId(), attributedObject.getCode());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's format is not valid with operation's outParam.");
				}
				return result;
			}
		}else {//方法没有配置输出参数时，返回null
			return null;
		}
	}
}
