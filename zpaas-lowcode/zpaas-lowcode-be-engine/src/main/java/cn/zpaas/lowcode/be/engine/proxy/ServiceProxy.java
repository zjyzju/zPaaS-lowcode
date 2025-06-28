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
import cn.zpaas.lowcode.be.engine.ability.ValidateAbility;
import cn.zpaas.lowcode.be.engine.ability.validator.Validator;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.OperationUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * ServiceProxy类是服务解析、执行的引擎类，初始化时需要实例化4个实例，分别对应应用服务、技术服务、第三方服务以及领域服务四类服务对象的解析、执行。
 */
public class ServiceProxy {
	private static Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
	
	//用于存放ServiceProxy实例
	private static ServiceProxy instance = null;
	//是否已经完成ServiceProxy实例的初始化，当初始化完成时设置，execute方法中首先需要判断该属性，如果未初始化完成则拒绝服务
	private static boolean initialized = false;
	
	//服务对象map，用于存放管理的服务对象，第一个Key为业务系统标识，第二个Key为服务对象标识
	private Map<String, Map<String,ServiceObject>> serviceMap = new HashMap<>();
	//方法对象map，用于存放管理的方法对象，第一个Key为业务系统标识，第二个Key为方法对象标识
	private Map<String, Map<String,Operation>> operationMap = new HashMap<>();
	
	//私有化构造函数，防止人为创建
	private ServiceProxy() {
		
	}
	
	/**
	 * 将方法对象加入缓存
	 * @param operation 方法对象
	 */
	private void cacheOperation(Operation operation) {
		// 当属性的业务系统标识为空时，忽略本条记录
		if (StringUtils.isBlank(operation.getSystemId())) {
			logger.info("operation's systemId is null. {}", operation.getId());
			return;
		}
		//将方法对象加入缓存
		Map<String, Operation> operMap = operationMap.get(operation.getSystemId());
		if (operMap == null) {
			operMap = new HashMap<>();
			operationMap.put(operation.getSystemId(), operMap);
		}
		operMap.put(operation.getId(), operation);		
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

	public Map<String, Operation> getOperations(String systemId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		return operationMap.get(systemId);
	}

	public Map<String, Map<String, Operation>> getOperations() {
		return operationMap;
	}
	
	/**
	 * 用于实现ServiceProxy类的初始化操作
	 * 传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}

		//调用领域对象执行代理的初始化方法
		DomainObjectProxy.init(systemId, tenantId, initService);
		//调用值传递对象执行代理的初始化方法
		ValueObjectProxy.init(systemId, tenantId, initService);
		
		instance = new ServiceProxy();
		
		//根据systemId获取相应的服务对象列表
		List<ServiceObject> serviceObjects = null;
		if(!StringUtils.isBlank(systemId)) {
			serviceObjects = initService.listsServiceObjectBySystemId(systemId);
		}else {
			serviceObjects = initService.listsServiceObjects();
		}
		
		//未取到服务对象时直接返回
		if(!CollectionUtils.isEmpty(serviceObjects)) {
			//加载所有的方法Operation
			List<Operation> allOperations = null;
			if(!StringUtils.isBlank(systemId)) {
				allOperations = initService.listOperationByObjectTypeAndSystem(ManagedObjectType.SERVICE_OBJECT, systemId);
			}else {
				allOperations = initService.listOperationByObjectType(ManagedObjectType.SERVICE_OBJECT);
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
			
			//循环处理每一个服务对象ServiceObject
			for(ServiceObject serviceObject : serviceObjects) {
				//服务对象为空或者systemId为空，继续处理下一个
				if(serviceObject == null || StringUtils.isBlank(serviceObject.getSystemId())) {
					continue;
				}
				
				//加载并初始化服务对象包含的方法Operation
//				List<Operation> operations = initService.listOperationByObjectId(ManagedObjectType.SERVICE_OBJECT, serviceObject.getId());
				serviceObject.setOperations(operationMap.get(serviceObject.getId()));
				if(!CollectionUtils.isEmpty(serviceObject.getOperations())) {
					//循环处理每一个Operation对象
					for(Operation operation: serviceObject.getOperations()) {
						//缓存每个operation对象
						instance.cacheOperation(operation);
						//获取并初始化方法对应的参数
						List<Parameter> parameters = parameterMap.get(operation.getId());
						if(!CollectionUtils.isEmpty(parameters)) {
							//出参，只有一个
							Parameter outParam = null;
							//入参，允许有多个
							List<Parameter> inParam = new ArrayList<>();
							for(Parameter parameter: parameters) {
								if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
									parameter.setParamClassObject(DomainObjectProxy.getInstance().getDomainObject(parameter.getSystemId(),parameter.getParamClass()));
								}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
									parameter.setParamClassObject(ValueObjectProxy.getInstance().getValueObject(parameter.getSystemId(),parameter.getParamClass()));
								}
								if(YesOrNo.YES.equals(parameter.getIsInParam())) {
									inParam.add(parameter);
									continue;
								}
								if(YesOrNo.NO.equals(parameter.getIsInParam())) {
									outParam = parameter;
								}
							}
							inParam.sort((a, b)-> {
								int order1 = a.getDisplayOrder();
								int order2 = b.getDisplayOrder();
								return (order1-order2);
							});
							operation.setOutParam(outParam);
							operation.setInParams(inParam);
							
						}
					}
				}
				
				instance.addServiceObject(serviceObject);
				
			}		
		}else {
			logger.info("no valid ServiceObject");
		}
		
		//调用业务流执行代理的初始化方法
		BusinessFlowProxy.init(systemId, tenantId, initService);
		
		//初始化完成
		initialized = true;
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}

		//调用领域对象执行代理的初始化方法
		DomainObjectProxy.reloadCache(systemId, tenantId, initService);
		//调用值传递对象执行代理的初始化方法
		ValueObjectProxy.reloadCache(systemId, tenantId, initService);
		//调用业务流执行代理的初始化方法
		BusinessFlowProxy.reloadCache(systemId, tenantId, initService);
		
		//根据systemId获取相应的服务对象列表
		List<ServiceObject> serviceObjects = initService.listsServiceObjectBySystemId(systemId);
		Map<String,ServiceObject> applicationServiceMap = new HashMap<>();
		
		Map<String, Operation> applicationServiceOperMap = new HashMap<>();
		
		//未取到服务对象时直接返回
		if(!CollectionUtils.isEmpty(serviceObjects)) {
			//加载所有的方法Operation
			List<Operation> allOperations = initService.listOperationByObjectTypeAndSystem(ManagedObjectType.SERVICE_OBJECT, systemId);
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
			
			//循环处理每一个服务对象ServiceObject
			for(ServiceObject serviceObject : serviceObjects) {
				//服务对象为空或者systemId为空，继续处理下一个
				if(serviceObject == null || StringUtils.isBlank(serviceObject.getSystemId())) {
					continue;
				}
				
				//加载并初始化服务对象包含的方法Operation
//				List<Operation> operations = initService.listOperationByObjectId(ManagedObjectType.SERVICE_OBJECT, serviceObject.getId());
				serviceObject.setOperations(operationMap.get(serviceObject.getId()));
				if(!CollectionUtils.isEmpty(serviceObject.getOperations())) {
					//循环处理每一个Operation对象
					for(Operation operation: serviceObject.getOperations()) {
						//缓存每个operation对象
						applicationServiceOperMap.put(operation.getId(), operation);
						
						//获取并初始化方法对应的参数
						List<Parameter> parameters = parameterMap.get(operation.getId());
						if(!CollectionUtils.isEmpty(parameters)) {
							//出参，只有一个
							Parameter outParam = null;
							//入参，允许有多个
							List<Parameter> inParam = new ArrayList<>();
							for(Parameter parameter: parameters) {
								if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
									parameter.setParamClassObject(DomainObjectProxy.getInstance().getDomainObject(parameter.getSystemId(),parameter.getParamClass()));
								}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
									parameter.setParamClassObject(ValueObjectProxy.getInstance().getValueObject(parameter.getSystemId(),parameter.getParamClass()));
								}
								if(YesOrNo.YES.equals(parameter.getIsInParam())) {
									inParam.add(parameter);
									continue;
								}
								if(YesOrNo.NO.equals(parameter.getIsInParam())) {
									outParam = parameter;
								}
							}
							inParam.sort((a, b)-> {
								int order1 = a.getDisplayOrder();
								int order2 = b.getDisplayOrder();
								return (order1-order2);
							});
							operation.setOutParam(outParam);
							operation.setInParams(inParam);
							
						}
					}
				}
				
				applicationServiceMap.put(serviceObject.getId(), serviceObject);
				
			}		
		}

		instance.serviceMap.put(systemId, applicationServiceMap);
		instance.operationMap.put(systemId, applicationServiceOperMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 将ServiceObject对象加入缓存中
	 * @param serviceObject
	 */
	private void addServiceObject(ServiceObject serviceObject) {
		//初始化业务系统对应的Map
		Map<String, ServiceObject> map = serviceMap.get(serviceObject.getSystemId());
		if(map == null) {
			map = new HashMap<>();
			serviceMap.put(serviceObject.getSystemId(), map);
		}
		map.put(serviceObject.getId(), serviceObject);
	}
	
	/**
	 * 根据serviceId和serviceObjectID获取对应的ServiceObject对象
	 * @param systemId 业务系统标识
	 * @param serviceObjectId 服务对象标识
	 * @return 返回ServiceObject对象
	 */
	public ServiceObject getServiceObject(String systemId, String serviceObjectId) {
		Map<String, ServiceObject> map = serviceMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(serviceObjectId);
	}
	
	/**
	 * 获取的ServiceProxy对象
	 * @return 返回ServiceProxy实例
	 */
	public static ServiceProxy getProxy() {
		return instance;
	}

	/**
	 * 服务执行代理的核心业务逻辑执行方法
	 * @param command 服务调用命令
	 * @return 返回服务方法执行结果，JsonObject类型
	 */
	public Object execute(ServiceCommand command, BusinessFlowContext context) throws EngineException {
		//初始化未完成时，拒绝服务
		if(!initialized) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Engine's initialization is not finished!");
		}
		 
		//获取到要调用方法的目标服务对象
		ServiceObject serviceObject = this.getServiceObject(command.getSystemId(), command.getServiceId());
		
		//如果服务对象为空或其方法列表为空，则返回null
		if(serviceObject == null || CollectionUtils.isEmpty(serviceObject.getOperations())) {
			logger.error("T[{}] ServiceObject is null or has zero operation: {}", command.getTenantId(), command);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ServiceObject is null or has zero operation");
		}
		
		//获取要调用的目标Operation对象
		Operation operation = getOperation(command.getSystemId(), command.getOperationId());
		
		//未找到目标方法，直接返回null
		if(operation == null || StringUtils.isBlank(operation.getBusinessFlowId())) {
			logger.error("T[{}] operation not found or businessFlowId is null: {}", command.getTenantId(), command);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "operation not found or businessFlowId is null");
		}

		logger.info("T[{}] Service {}'s operation:{} is invoked.", command.getTenantId(), serviceObject.getCode(), operation.getCode());
		
		//当方法有配置入参校验规则时，进行参数合法性校验
		if(!StringUtils.isBlank(operation.getRuleGroupId())) {
			JsonObject validateResult = ValidateAbility.getInstance().validate(command.getSystemId(), operation.getRuleGroupId(), command.getReqData(), null);
			//校验未通过时
			if(validateResult != null && !JsonUtils.getBoolean(validateResult, Validator.RESULT_STATUS_KEY)) {
				logger.error("T[{}] inParam validate isn't passed! interrupt!", command.getTenantId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, JsonUtils.toJson(JsonUtils.getJsonArray(validateResult, Validator.RESULT_MESSAGES_KEY)));
			}
		}
		
		//进行输入参数格式校验		
		if(!OperationUtils.validateInParam(operation, command.getReqData())) {
			logger.error("T[{}] reqData's format is not valid with operation's param definition", command.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "reqData's format is not valid with operation's param definition");
		}
		
		
		//初始化业务流命令对象
		BusinessFlowCommand businessFlowCommand = new BusinessFlowCommand();
		businessFlowCommand.setBusinessFlowId(operation.getBusinessFlowId());
		businessFlowCommand.setSystemId(command.getSystemId());
		businessFlowCommand.setReqData(command.getReqData());
		businessFlowCommand.setTenantId(command.getTenantId());
		businessFlowCommand.setMultipartFileMap(command.getMultipartFileMap());
		businessFlowCommand.setSseEmitter(command.getSseEmitter());
		
		//调用BusinessFlowProxy的execute方法
		Object result = BusinessFlowProxy.getInstance().execute(businessFlowCommand, context);

		logger.info("T[{}] Service {}'s operation:{} is finished.", command.getTenantId(), serviceObject.getCode(), operation.getCode());
		
		if(logger.isTraceEnabled()) {
			if(result != null) {
				logger.trace("T[{}] return value class({}), value: {}.", command.getTenantId(), result.getClass(), result);
			}		
		}
		
		//当返回结果为null时，直接返回，不不进行格式校验
		if(result == null) {
			return result;
		}
		//有返回值，但未配置返回参数的情况
		if(operation.getOutParam() == null) {
			logger.error("T[{}] not config outParam!", command.getTenantId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "not config outParam!");
		}
		//当返回结果是列表类型，且列表长度为0时，直接返回
		if(YesOrNo.YES.equals(operation.getOutParam().getIsListParam()) && (result instanceof JsonArray) && ((JsonArray)result).isEmpty()) {
			return result;
		}
		
		// 方法有返回值时，进行格式校验
		if (operation.getOutParam() != null) {
			if (!OperationUtils.validateOutParam(operation, result)) {
				logger.error("T[{}] result's format is not valid with operation's outParam format", command.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "result's format is not valid with operation's outParam.");
			}
			return result;
		} else {// 方法没有配置输出参数时，返回null
			return null;
		}
	}
}
