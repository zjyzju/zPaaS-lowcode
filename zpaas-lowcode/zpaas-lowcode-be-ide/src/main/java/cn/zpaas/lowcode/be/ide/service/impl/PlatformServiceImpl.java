package cn.zpaas.lowcode.be.ide.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessDomainInfo;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.vo.DomainOperationExpose;
import cn.zpaas.lowcode.vo.ManagedObjectInfo;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.vo.MdaGenerateRequest;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.vo.NewManagedObject;
import cn.zpaas.lowcode.vo.OperationFlowInfo;
import cn.zpaas.lowcode.vo.OperationInfo;
import cn.zpaas.lowcode.vo.UpdateManagedObject;
import cn.zpaas.lowcode.vo.ValidateRuleGroupInitInfo;
import cn.zpaas.lowcode.constant.AuthTypes;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAutoLoad;
import cn.zpaas.lowcode.domain.eo.CronJob;
import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DataTransferMethod;
import cn.zpaas.lowcode.domain.eo.DbColumn;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.DictResource;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.DynamicMappingDetail;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.FileObjectMapping;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.ManagedObject;
import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.domain.eo.UserBusinessSystem;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.be.engine.ability.ValidateAbility;
import cn.zpaas.lowcode.be.engine.auth.impl.BaseAuthenticateManager;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.be.ide.ability.ModelDrivenAbility;
import cn.zpaas.lowcode.be.ide.domain.service.ExposedServiceService;
import cn.zpaas.lowcode.be.ide.service.PlatformService;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemGrantVo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemInfo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemTestVo;

/**
 * @author zjy
 * 服务端领域平台服务
 */
@Service
public class PlatformServiceImpl implements PlatformService {
	private static Logger logger = LoggerFactory.getLogger(PlatformService.class);
	
	@Autowired
	private BusinessSystem businessSystemSV;
	
	@Autowired
	private BusinessSystemAuth businessSystemAuthSV;
	
	@Autowired
	private BusinessSystemAutoLoad businessSystemAutoLoadSV;
	
	@Autowired
	private BusinessDomain businessDomainSV;
	
	@Autowired
	private ServiceObject serviceObjectSV;
	
	@Autowired
	private DomainObject domainObjectSV;
	
	@Autowired
	private ValueObject valueObjectSV;
	
	@Autowired
	private Operation operationSV;
	
	@Autowired
	private Attribute attributeSV;
	
	@Autowired
	private Parameter parameterSV;
	
	@Autowired
	private BusinessFlowNode businessFlowNodeSV;
	
	@Autowired
	private FlowNode flowNodeSV;
	
	@Autowired
	private ServerResource serverResourceSV;
	
	@Autowired
	private DbSchema dbSchemaSV;
	
	@Autowired
	private DbTable dbTableSV;
	
	@Autowired
	private DbColumn dbColumnSV;
	
	@Autowired
	private ExposedService exposedServiceSV;

	@Autowired
	private ExposedServiceService exposedServiceService;
	
	@Autowired
	private ModelDrivenAbility modelDrivenAbility;
	
	@Autowired
	private ObjectRelationMapping objectRelationMappingSV;
	
	@Autowired
	private DataMapping dataMappingSV;
	
	@Autowired
	private SqlManagement sqlManagementSV;
	
	@Autowired
	private ValidateRuleGroup validateRuleGroupSV;
	
	@Autowired
	private WorkflowProcessResource workflowProcessResourceSV;

	@Autowired
	private MessageConsumer messageConsumerSV;

	@Autowired
	private DataTransferMethod dataTransferMethodSV;

	@Autowired
	private DynamicMapping dynamicMappingSV;

	@Autowired
	private DynamicMappingDetail dynamicMappingDetailSV;

	@Autowired
	private FileObjectMapping fileObjectMappingSV;

	@Autowired
	private DictResource dictResourceSV;

	@Autowired
	private UserBusinessSystem userBusinessSystemSV;

	@Autowired
	private SecurityKeyResource securityKeyResourceSV;

	@Autowired
	private BusinessFlowService businessFlowService;

	@Autowired
	private CronJob cronJobSV;

	@Override
	public List<BusinessSystem> listBusinessSystem(String tenantId) {
		return businessSystemSV.listByTenant(tenantId);
	}

	@Override
	public List<BusinessSystem> listBusinessSystemByUser(String tenantId, String userId){
		List<UserBusinessSystem> userBusinessSystems = userBusinessSystemSV.loadByUserId(userId);
		if(CollectionUtils.isEmpty(userBusinessSystems)) {
			return new ArrayList<>();
		}
		List<String> systemIds = new ArrayList<>();
		for(UserBusinessSystem userBusinessSystem : userBusinessSystems) {
			systemIds.add(userBusinessSystem.getSystemId());
		}
		return businessSystemSV.listByIds(tenantId, systemIds);
	}
	
	@Override
	public BusinessSystemInfo loadBusinessSystemInfo(String businessSystemId) {
		BusinessSystemInfo info = new BusinessSystemInfo();
		BusinessSystem system = businessSystemSV.byId(businessSystemId);	
		system.setBusinessSystemAuth(businessSystemAuthSV.queryBySystem(businessSystemId));
		system.setBusinessSystemAutoLoad(businessSystemAutoLoadSV.queryBySystem(businessSystemId));
		info.setBusinessSystem(system);
		
		List<ServiceObject> serviceObjects = serviceObjectSV.listBySystemId(businessSystemId);
		Map<String, List<ServiceObject>> soMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(serviceObjects)) {
			soMap = serviceObjects.stream().collect(Collectors.groupingBy((service)->service.getDomainId()));
		}
		
		
		List<DomainObject> domainObjects = domainObjectSV.listDomainObjectBySystemId(businessSystemId);
		Map<String, List<DomainObject>> doMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(domainObjects)) {
			doMap = domainObjects.stream().collect(Collectors.groupingBy(object->object.getDomainId()));
		}
		
		List<ValueObject> voList = valueObjectSV.listValueObjectBySystemId(businessSystemId);
		Map<String,List<ValueObject>> voMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(voList)) {
			voMap = voList.stream().collect(Collectors.groupingBy(vo->vo.getDomainId()));
		}
		
		List<BusinessDomain> domains = businessDomainSV.listBySystemId(businessSystemId);
		List<BusinessDomainInfo> domainInfos = new ArrayList<>();
		if(!CollectionUtils.isEmpty(domains)) {
			BusinessDomainInfo domainInfo = null;
			for(BusinessDomain domain : domains) {
				domainInfo = new BusinessDomainInfo();
				domainInfo.setBusinessDomain(domain);
				domainInfo.setServiceObjects(soMap.get(domain.getId()));
				domainInfo.setDomainObjects(doMap.get(domain.getId()));
				domainInfo.setValueObjects(voMap.get(domain.getId()));
				domainInfos.add(domainInfo);
			}
		}
		info.setBusinessDomains(domainInfos);
		
		List<FlowNode> flowNodes = flowNodeSV.list();
		info.setFlowNodes(flowNodes);
		
		info.setDbSchemas(dbSchemaSV.listBySystemId(businessSystemId));
		if(!CollectionUtils.isEmpty(info.getDbSchemas())) {
			info.getDbSchemas().forEach((db)-> {
				if(!StringUtils.isBlank(db.getPassword())) {
					db.setPassword(DbSchema.PWD_MARK_STR);
				}
				db.setDbTables(dbTableSV.listByDbSchemaId(db.getId()));
			});
		}
		
		info.setServerResources(serverResourceSV.listBySystem(businessSystemId));
		if(!CollectionUtils.isEmpty(info.getServerResources())) {
			info.getServerResources().forEach((serverResource)-> {
				serverResource.setPassword(ServerResource.PWD_MARK_STR);
			});
		}
		
		info.setWorkflowProcessResources(workflowProcessResourceSV.list(businessSystemId));
		
		info.setMessageConsumers(messageConsumerSV.list(businessSystemId));
		
		info.setDynamicMappings(dynamicMappingSV.list(businessSystemId));

		info.setDictResources(dictResourceSV.listBySystem(businessSystemId));

		info.setSecurityKeyResources(securityKeyResourceSV.list(businessSystemId));
		
		info.setCronJobs(cronJobSV.listBySystem(businessSystemId));
		return info;
	}

	
	
	@Override
	public BusinessSystemTestVo loadBusinessSystemTestInfo(String systemId) {
		BusinessSystem system = businessSystemSV.byId(systemId);
		if(system != null) {
			BusinessSystemTestVo testVo = new BusinessSystemTestVo();
			testVo.setId(systemId);
			testVo.setSystemUrl(system.getServletContext());
			return testVo;
		}
		return null;
	}



	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveBusinessSystem(BusinessSystem businessSystem) throws EngineException {
		if(StringUtils.isBlank(businessSystem.getServletContext()) || businessSystem.getServletContext().startsWith("/")) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "请求上下文不能为空且不能以“/”开头！");
		}
		List<BusinessSystem> sameServletContexts = this.businessSystemSV.queryByServletContext(businessSystem.getServletContext(), businessSystem.getId());
		if(!CollectionUtils.isEmpty(sameServletContexts)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "该请求上下文已被使用！");
		}
		Date now = new Date();
		businessSystem.setUpdateTime(now);
		return this.businessSystemSV.updateById(businessSystem);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessSystemAuth saveBusinessSystemAuth(BusinessSystemAuth businessSystemAuth) {
		return this.businessSystemAuthSV.saveOrUpdate(businessSystemAuth);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessSystemAutoLoad saveBusinessSystemAutoLoad(BusinessSystemAutoLoad businessSystemAutoLoad) {
		return this.businessSystemAutoLoadSV.saveOrUpdate(businessSystemAutoLoad);
	}

	
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int grantBusinessSystem(BusinessSystemGrantVo businessSystemGrantVo) throws EngineException{
		if(StringUtils.isBlank(businessSystemGrantVo.getUserId())) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "userId is null!");
		}
		userBusinessSystemSV.deleteByUserId(businessSystemGrantVo.getUserId());
		UserBusinessSystem userBusinessSystem = null;
		if(!JsonUtils.isEmpty(businessSystemGrantVo.getGrantedSystems())) {
			int size = businessSystemGrantVo.getGrantedSystems().size();
			for(int i=0; i<size; i++) {
				userBusinessSystem = new UserBusinessSystem();
				userBusinessSystem.setUserId(businessSystemGrantVo.getUserId());
				userBusinessSystem.setSystemId(businessSystemGrantVo.getGrantedSystems().get(i).getAsString());
				userBusinessSystem.setTenantId(businessSystemGrantVo.getTenantId());
				BusinessSystem businessSystem = SpringContextUtils.getBean(FrontController.class).getBusinessSystem(userBusinessSystem.getSystemId());
				if(businessSystem == null || StringUtils.isBlank(businessSystem.getTenantId()) || !businessSystem.getTenantId().equals(userBusinessSystem.getTenantId()) ) {
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "illegal request!");
				}
				if(userBusinessSystemSV.add(userBusinessSystem)<=0) {
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "business system grant failed!");
				}
			}
		}
		return 1;
	}



	@Override
	public BusinessSystemGrantVo loadBusinessSystemGrantInfo(String userId) {
		if(StringUtils.isBlank(userId)) {
			logger.error("userId is null!");
			return null;
		}
		List<UserBusinessSystem> userBusinessSystems = userBusinessSystemSV.loadByUserId(userId);
		BusinessSystemGrantVo businessSystemGrantVo = new BusinessSystemGrantVo();
		businessSystemGrantVo.setUserId(userId);
		JsonArray grantedSystems = new JsonArray();
		if(!CollectionUtils.isEmpty(userBusinessSystems)) {
			for(UserBusinessSystem userBusinessSystem : userBusinessSystems) {
				grantedSystems.add(userBusinessSystem.getSystemId());
				businessSystemGrantVo.setTenantId(userBusinessSystem.getTenantId());
			}
		}
		businessSystemGrantVo.setGrantedSystems(grantedSystems);
		return businessSystemGrantVo;
	}



	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessSystem createBusinessSystem(BusinessSystem businessSystem, String userId) throws EngineException {
		if(StringUtils.isBlank(businessSystem.getServletContext()) || businessSystem.getServletContext().startsWith("/")) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "请求上下文不能为空且不能以“/”开头！");
		}
		List<BusinessSystem> sameServletContexts = this.businessSystemSV.queryByServletContext(businessSystem.getServletContext());
		if(!CollectionUtils.isEmpty(sameServletContexts)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "该请求上下文已被使用！");
		}

		Date now = new Date();
		businessSystem.setCreateTime(now);
		businessSystem.setUpdateTime(now);
		if(businessSystem.getStatus() == null || businessSystem.getStatus().trim().length() == 0) {
			businessSystem.setStatus(Status.EFF);
		}
		this.businessSystemSV.save(businessSystem);

		UserBusinessSystem userBusinessSystem = new UserBusinessSystem();
		userBusinessSystem.setSystemId(businessSystem.getId());
		userBusinessSystem.setTenantId(businessSystem.getTenantId());
		userBusinessSystem.setUserId(userId);
		userBusinessSystemSV.add(userBusinessSystem);
		return businessSystem;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteBusinessSystem(String id, String tenantId) throws EngineException{
		BusinessSystem system = businessSystemSV.byId(id);
		if(system != null && system.getId() != null) {
			if(StringUtils.isBlank(system.getTenantId()) || !system.getTenantId().equals(tenantId)) {
				throw new EngineException(ResultStatus.BAD_REQUEST, "illegal request!");
			}
			system.setStatus(Status.EXP);
			if(businessSystemSV.updateById(system) > 0) {
				return 1;
			}
		}
		return 0;
	}
	
	@Override
	public ManagedObject queryManagedObject(String objectType, String objectId) {
		ManagedObject mObject = null;
		
		if(ManagedObjectType.SERVICE_OBJECT.equals(objectType)) {
			mObject = serviceObjectSV.byId(objectId);
		}else if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {
			mObject = domainObjectSV.loadDomainObjectAndAttrs(objectId);
		}else if(ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
			mObject = valueObjectSV.byId(objectId);
		}else {
			return null;
		}
		
		return mObject;
	}
	
	@Override
	public ManagedObjectInfo loadManagedObjectInfo(String objectType, String objectId) {
		ManagedObject mObject = null;
		
		if(ManagedObjectType.SERVICE_OBJECT.equals(objectType)) {
			mObject = serviceObjectSV.byId(objectId);
		}else if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {
			mObject = domainObjectSV.loadDomainObjectAndAttrs(objectId);
		}else if(ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
			mObject = valueObjectSV.byId(objectId);
		}else {
			return null;
		}
		
		if(mObject == null) {
			return null;
		}
		
		ManagedObjectInfo info = new ManagedObjectInfo();
		info.setManagedObject(mObject);
		info.setObjectType(objectType);
		if(!ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
			//加载管理对象包含的方法Operation
			List<Operation> operations = operationSV.listByObjectId(objectType, objectId);
			info.setOperations(operations);
			if(operations != null && operations.size() > 0) {
				//循环处理每一个Operation对象
				for(Operation operation: operations) {
					//加载并初始化方法对应的参数
					List<Parameter> parameters = parameterSV.listByOperationId(operation.getId());
					if(parameters != null && parameters.size() > 0) {
						//出参，只有一个
						Parameter outParam = null;
						//入参，允许有多个
						List<Parameter> inParam = new ArrayList<>();
						for(Parameter parameter: parameters) {
							if(YesOrNo.YES.equals(parameter.getIsInParam())) {
								inParam.add(parameter);
								continue;
							}
							if(YesOrNo.NO.equals(parameter.getIsInParam())) {
								outParam = parameter;
							}
						}
						operation.setOutParam(outParam);
						operation.setInParams(inParam);
					}
				}
			}
			
			if(ManagedObjectType.SERVICE_OBJECT.equals(objectType))  {
				List<ExposedService> exposedServices = exposedServiceSV.listByServiceId(mObject.getSystemId(), mObject.getId());
				if(exposedServices != null && exposedServices.size() > 0) {
					Map<String, ExposedService> map = exposedServices.stream().collect(Collectors.toMap(ExposedService::getOperationId, T->T));
					info.setExposedServiceMap(map);
				}
			}
		}
		
		if(!ManagedObjectType.SERVICE_OBJECT.equals(objectType)) {
			// 加载管理对象的属性信息
			List<Attribute> attributes = attributeSV.listByObjectId(objectType, objectId);
			if(attributes != null && attributes.size() >0) {
				attributes.forEach(attr->{
					if(ManagedObjectType.DOMAIN_OBJECT.equals(attr.getAttrType())) {
	    				attr.setAttrClassObject(this.domainObjectSV.loadDomainObjectAndAttrs(attr.getAttrClass()));
	    			}else if(ManagedObjectType.VALUE_OBJECT.equals(attr.getAttrType())) {
	    				attr.setAttrClassObject(this.valueObjectSV.byId(attr.getAttrClass()));
	    			}
				});
			}
			info.setAttributes(attributes);
		}
		
		return info;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteManagedObject(String objectType, String objectId, String tenantId, JsonObject grantedSystems) throws EngineException{
		ManagedObject managedObject = this.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			if(StringUtils.isBlank(managedObject.getTenantId()) || !managedObject.getTenantId().equals(tenantId) || grantedSystems == null || JsonUtils.getJsonObject(grantedSystems, managedObject.getSystemId()) == null) {
				throw new EngineException(ResultStatus.BAD_REQUEST, "illegal request!");
			}
		}
		int result = 0;
		if(ManagedObjectType.SERVICE_OBJECT.equals(objectType)) {
			this.exposedServiceSV.deleteByService(objectId);
			return this.serviceObjectSV.delete(objectId);
		}if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {
			result =  this.domainObjectSV.delete(objectId);
			if(result > 0) {
				this.objectRelationMappingSV.deleteByDomainObjectId(objectId);
				this.dataMappingSV.deleteByFromObject(ManagedObjectType.DOMAIN_OBJECT, objectId);
				this.dataMappingSV.deleteByToObject(ManagedObjectType.DOMAIN_OBJECT, objectId);
				this.validateRuleGroupSV.deleteByObject(objectType, objectId);
			}
			return result;
		}if(ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
			result =  this.valueObjectSV.delete(objectId);
			if(result > 0) {
				this.dataMappingSV.deleteByFromObject(ManagedObjectType.VALUE_OBJECT, objectId);
	    		this.dataMappingSV.deleteByToObject(ManagedObjectType.VALUE_OBJECT, objectId);
	    		this.validateRuleGroupSV.deleteByObject(objectType, objectId);
			}
			return result;
		}else {
			return 0;
		}
	}

	@Override
	public OperationInfo loadOperationInfo(String operationId) {
		Operation operationBean = operationSV.byId(operationId);
		
		if(operationBean == null) {
			return null;
		}
		
		OperationInfo info = new OperationInfo();
		info.setOperation(operationBean);
		
		List<Parameter> parameters = parameterSV.listByOperationId(operationId);
		if(parameters != null && parameters.size() > 0) {
    		parameters.forEach(param -> {
    			if(param.getParamClass() == null || param.getParamClass().trim().length() == 0) {
    				return;
    			}
    			if(ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
    				param.setParamClassObject(this.domainObjectSV.loadDomainObjectAndAttrs(param.getParamClass()));
    			}else if(ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
    				param.setParamClassObject(this.valueObjectSV.byId(param.getParamClass()));
    			}
    		});
    	}
		info.setParameters(parameters);
		
		return info;
	}

	@Override
	public String queryOperationName(String operationId) {
		Operation operation = operationSV.byId(operationId);
		if(operation != null) {
			return operation.getCode();
		}else {
			return null;
		}
	}

	@Override
	public Operation queryOperation(String operationId) {
		return operationSV.byId(operationId);
	}
	
	@Override
	public List<Parameter> loadParameters(String operationId) {
		List<Parameter> parameters = parameterSV.listByOperationId(operationId);
		if(parameters != null && parameters.size() > 0) {
    		parameters.forEach(param -> {
    			if(param.getParamClass() == null || param.getParamClass().trim().length() == 0) {
    				return;
    			}
    			if(ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
    				param.setParamClassObject(this.domainObjectSV.loadDomainObjectAndAttrsTwoLevel(param.getParamClass()));
    			}else if(ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
    				param.setParamClassObject(this.valueObjectSV.loadObjectAndAttrsTwoLevel(param.getParamClass()));
    			}
    		});
    	}
		return parameters;
	}
	
	@Override
	public BusinessFlowInfo loadBusinessFlowInfo(String businessFlowId) {
		return businessFlowService.loadBusinessFlowInfo(businessFlowId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessFlow saveBusinessFlow(BusinessFlow businessFlow) {
		return businessFlowService.saveBusinessFlow(businessFlow);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessFlowInfo saveBusinessFlowAllInfo(BusinessFlowInfo businessFlowInfo) throws EngineException{
		return businessFlowService.saveBusinessFlowAllInfo(businessFlowInfo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessFlowNode addBusinessFlowNode(BusinessFlowNode businessFlowNode) {
		Date now = new Date();
		businessFlowNode.setCreateTime(now);
		businessFlowNode.setUpdateTime(now);
		this.businessFlowNodeSV.save(businessFlowNode);
		return businessFlowNode;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveBusinessFlowNode(BusinessFlowNode businessFlowNode) {
		Date now = new Date();
		businessFlowNode.setUpdateTime(now);
		return this.businessFlowNodeSV.updateById(businessFlowNode);
	}

	@Override
	public List<FlowNode> listFlowNode(String systemId) {
		return flowNodeSV.list();
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ManagedObjectInfo addManagedObject(NewManagedObject managedObject) {
		if(managedObject == null) {
			return null;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("add new ManagedObject:" + JsonUtils.toJson(managedObject));
		}
		if(ManagedObjectType.SERVICE_OBJECT.equals(managedObject.getObjectType())) {
			ServiceObject newObject = new ServiceObject();
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(nowDate);
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(Status.EFF);
			serviceObjectSV.save(newObject);
			ManagedObjectInfo result = new ManagedObjectInfo();
			result.setManagedObject(newObject);
			result.setObjectType(ManagedObjectType.SERVICE_OBJECT);
			return result;
		}else if(ManagedObjectType.DOMAIN_OBJECT.equals(managedObject.getObjectType())) {
			DomainObject newObject = new DomainObject();
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(nowDate);
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(Status.EFF);
			domainObjectSV.save(newObject);
			ManagedObjectInfo result = new ManagedObjectInfo();
			result.setManagedObject(newObject);
			result.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
			return result;
		}else if(ManagedObjectType.VALUE_OBJECT.equals(managedObject.getObjectType())) {
			ValueObject newObject = new ValueObject();
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(nowDate);
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(Status.EFF);
			valueObjectSV.save(newObject);
			ManagedObjectInfo result = new ManagedObjectInfo();
			result.setManagedObject(newObject);
			result.setObjectType(ManagedObjectType.VALUE_OBJECT);
			return result;
		}
		return null;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveManagedObject(UpdateManagedObject managedObject) {
		if(managedObject == null) {
			return 0;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("update ManagedObject:" + JsonUtils.toJson(managedObject));
		}
		if(ManagedObjectType.SERVICE_OBJECT.equals(managedObject.getObjectType())) {
			ServiceObject newObject = new ServiceObject();
			newObject.setId(managedObject.getId());
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(managedObject.getCreateTime());
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(managedObject.getStatus());
			return serviceObjectSV.updateById(newObject);
			
		}else if(ManagedObjectType.DOMAIN_OBJECT.equals(managedObject.getObjectType())) {
			DomainObject newObject = new DomainObject();
			newObject.setId(managedObject.getId());
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(managedObject.getCreateTime());
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(managedObject.getStatus());
			return domainObjectSV.updateById(newObject);
			
		}else if(ManagedObjectType.VALUE_OBJECT.equals(managedObject.getObjectType())) {
			ValueObject newObject = new ValueObject();
			newObject.setId(managedObject.getId());
			newObject.setCode(managedObject.getCode());
			newObject.setName(managedObject.getName());
			newObject.setDomainId(managedObject.getBusinessDomainId());
			newObject.setSystemId(managedObject.getSystemId());
			newObject.setTenantId(managedObject.getTenantId());
			newObject.setDescription(managedObject.getDescription());
			Date nowDate = new Date();
			newObject.setCreateTime(managedObject.getCreateTime());
			newObject.setUpdateTime(nowDate);
			newObject.setStatus(managedObject.getStatus());
			return valueObjectSV.updateById(newObject);
			
		}
		return 0;
	}
	
	@Override
	public DomainObject loadDomainObject(String id) {
		DomainObject domainObject = this.domainObjectSV.loadDomainObjectAndAttrs(id);
		if(domainObject != null) {
			domainObject.setAttributes(this.attributeSV.listByObjectId(ManagedObjectType.DOMAIN_OBJECT, id));
		}
		return domainObject;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void exposeDomainOperation(DomainOperationExpose domainOperationExpose) throws EngineException {
		this.exposedServiceService.exposeDomainOperation(this.serviceObjectSV.byId(domainOperationExpose.getServiceObjectId()), 
				this.domainObjectSV.byId(domainOperationExpose.getDomainObjectId()), domainOperationExpose);
	}
	
	@Override
	public ValueObject loadValueObject(String id) {
		ValueObject valueObject = this.valueObjectSV.byId(id);
		if(valueObject != null) {
			valueObject.setAttributes(this.attributeSV.listByObjectId(ManagedObjectType.VALUE_OBJECT, id));
		}
		return valueObject;
	}
	
	@Override
	public List<Attribute> listAttributes(String objectType, String objectId) {
		return this.attributeSV.listByObjectId(objectType, objectId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Attribute addAttribute(Attribute attribute) {
		Date nowDate = new Date();
		attribute.setCreateTime(nowDate);
		attribute.setUpdateTime(nowDate);
		this.attributeSV.save(attribute);
		return attribute;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveAttribute(Attribute attribute) {
		return this.attributeSV.updateById(attribute);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteAttribute(String attrId) {
		return this.attributeSV.delete(attrId);
	}

	@Override
	public Attribute queryAttribute(String attrId) {
		return this.attributeSV.byId(attrId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Operation addOperation(Operation operation) {
		operation.setStatus(Status.EFF);
		Date nowDate = new Date();
		operation.setCreateTime(nowDate);
		operation.setUpdateTime(nowDate);
		this.operationSV.save(operation);
		return operation;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Operation addOperationAndFlow(OperationFlowInfo operationFlowInfo) {
		Operation operation = new Operation();
		operation.setCode(operationFlowInfo.getCode());
		operation.setName(operationFlowInfo.getName());
		operation.setObjectType(operationFlowInfo.getObjectType());
		operation.setObjectId(operationFlowInfo.getObjectId());
		operation.setDisplayOrder(operationFlowInfo.getDisplayOrder());
		operation.setSystemId(operationFlowInfo.getSystemId());
		operation.setTenantId(operationFlowInfo.getTenantId());
		operation.setStatus(Status.EFF);
		Date nowDate = new Date();
		operation.setCreateTime(nowDate);
		operation.setUpdateTime(nowDate);
		this.operationSV.save(operation);
		
		BusinessFlow businessFlow = new BusinessFlow();
		businessFlow.setId(operation.getBusinessFlowId());
		businessFlow.setName(operation.getCode() + "方法的主业务流");
		businessFlow.setFlowType(operationFlowInfo.getFlowType());
		businessFlow.setTransactionRequired(operationFlowInfo.getTransactionRequired());
		businessFlow.setDbSchemaId(operationFlowInfo.getDbSchemaId());
		businessFlow.setSystemId(operationFlowInfo.getSystemId());
		businessFlow.setTenantId(operationFlowInfo.getTenantId());
		this.saveBusinessFlow(businessFlow);
		
		return operation;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveOperation(Operation operation) {
		Date nowDate = new Date();
		operation.setUpdateTime(nowDate);
		return this.operationSV.updateById(operation);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteOperation(String operationId) {
		return this.operationSV.disableOperation(operationId);
	}
	
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Parameter addParameter(Parameter parameter) {
		Date nowDate = new Date();
		parameter.setCreateTime(nowDate);
		parameter.setUpdateTime(nowDate);
		this.parameterSV.save(parameter);
		return parameter;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Parameter saveParameter(Parameter parameter) throws EngineException {
		Date nowDate = new Date();
		parameter.setUpdateTime(nowDate);
		if(this.parameterSV.updateById(parameter) > 0) {
			return parameter;
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "update parameter failed!");
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteParameter(String paramId) {
		return this.parameterSV.delete(paramId);
	}

	@Override
	public Parameter queryParameter(String paramId) {
		return this.parameterSV.byId(paramId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ExposedService exposeOperation(ExposedService exposedService) {
		Date nowDate = new Date();
		exposedService.setCreateTime(nowDate);
		exposedService.setUpdateTime(nowDate);
		exposedService.setStatus(Status.EFF);
		this.exposedServiceSV.save(exposedService);
		return exposedService;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int cancelExposeOperation(ExposedService exposedService) {
		return this.exposedServiceSV.deleteById(exposedService.getId());
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BusinessDomain addBusinessDomain(BusinessDomain businessDomain) {
		Date nowDate = new Date();
		businessDomain.setCreateTime(nowDate);
		businessDomain.setUpdateTime(nowDate);
		businessDomain.setStatus(Status.EFF);
		this.businessDomainSV.save(businessDomain);
		return businessDomain;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveBusinessDomain(BusinessDomain businessDomain) {
		BusinessDomain businessDomain2 = this.businessDomainSV.byId(businessDomain.getId());
		Date nowDate = new Date();
		businessDomain2.setUpdateTime(nowDate);
		businessDomain2.setName(businessDomain.getName());
		businessDomain2.setDescription(businessDomain.getDescription());
		return this.businessDomainSV.updateById(businessDomain2);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteBusinessDomain(BusinessDomain businessDomain) {
		Date nowDate = new Date();
		businessDomain.setUpdateTime(nowDate);
		businessDomain.setStatus(Status.EXP);
		return this.businessDomainSV.updateById(businessDomain);
	}
	
	@Override
	public List<MdaTable> queryTableList(String systemId, String dbSchemaId) throws EngineException {
		DbSchema dbSchema = DBSchemaProxy.getDbSchema(systemId, dbSchemaId);
		if(dbSchema == null) {
			dbSchema = this.dbSchemaSV.byId(dbSchemaId);
		}
		if(dbSchema == null) {
			logger.error("invalid systemId({}) and dbSchemaId({})!", systemId, dbSchemaId);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid systemId and dbSchemaId!");
		}
		DataSource dataSource = DBSchemaProxy.getDataSource(systemId, dbSchemaId);
		if(dataSource == null) {
			dataSource = DBSchemaProxy.createDataSource(dbSchema);
		}
		return modelDrivenAbility.queryTableList(dataSource, dbSchema.getName(), false);
	}
	
	@Override
	public List<MdaColumn> queryColumnList(String systemId, String dbSchemaId, String tableName)
			throws EngineException {
		DbSchema dbSchema = DBSchemaProxy.getDbSchema(systemId, dbSchemaId);
		if(dbSchema == null) {
			dbSchema = this.dbSchemaSV.byId(dbSchemaId);
		}
		if(dbSchema == null) {
			logger.error("invalid systemId({}) and dbSchemaId({})!", systemId, dbSchemaId);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid systemId and dbSchemaId!");
		}
		DataSource dataSource = DBSchemaProxy.getDataSource(systemId, dbSchemaId);
		if(dataSource == null) {
			dataSource = DBSchemaProxy.createDataSource(dbSchema);
		}
		return modelDrivenAbility.queryColumnList(dataSource, dbSchema.getName(), tableName);
	}
	
	@Override
	public List<MdaTable> queryTableColumnList(String systemId, String dbSchemaId, List<String> talbeList)
			throws EngineException {
		DbSchema dbSchema = DBSchemaProxy.getDbSchema(systemId, dbSchemaId);
		if(dbSchema == null) {
			dbSchema = this.dbSchemaSV.byId(dbSchemaId);
		}
		if(dbSchema == null) {
			logger.error("invalid systemId({}) and dbSchemaId({})!", systemId, dbSchemaId);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid systemId and dbSchemaId!");
		}
		Connection connection = DBSchemaProxy.createConnection(dbSchema);
		try {
			return modelDrivenAbility.queryTableColumnList(connection, dbSchema.getName(), talbeList);
		} catch (EngineException e) {
			throw e;
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					
				}
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean generateAll(MdaGenerateRequest request) throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("generateAll starting. request:{}", JsonUtils.toJson(request));
		}
		if(request == null) {
			logger.error("request param is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "request param is null!");
		}
		BusinessDomain domain = businessDomainSV.byId(request.getBusinessDomainId());
		if(domain == null) {
			logger.error("invalid businessDomainId({})!", request.getBusinessDomainId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid businessDomainId!");
		}
		
		DbSchema dbSchema = this.dbSchemaSV.byId(request.getDbSchemaId());
		if(dbSchema == null) {
			logger.error("invalid dbSchemaId({})!", request.getSystemId(), request.getDbSchemaId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dbSchemaId!");
		}
		
		Connection connection = DBSchemaProxy.createConnection(dbSchema);
		
		//查询表结构信息
		List<MdaTable> tableList = null;
		try {
			tableList = modelDrivenAbility.queryTableColumnList(connection, dbSchema.getName(), request.getTableArray().asList().stream().map(e->e.getAsString()).collect(Collectors.toList()));
		} catch (EngineException e) {
			throw e;
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					
				}
			}
		}
		
		//生成平台中的DbTable和DbColumn信息
		if(!modelDrivenAbility.generateTables(tableList, dbSchema)) {
			logger.error("generateTables failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateTables failed!");
		}
		
		//生成实体对象信息，包括属性、默认方法等
		List<DomainObject> domainObjects = modelDrivenAbility.generateEntityObject(tableList, domain, request.getIgnorePrefixes(), request.getDbSchemaId());
		if(domainObjects == null) {
			logger.error("generateEntityObject failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateEntityObject failed!");
		}
		
		//生成值传递对象及属性信息
		if(request.isGenerateValueObject()) {
			List<ValueObject> valueObjects = modelDrivenAbility.generateValueObject(tableList, domain, request.getIgnorePrefixes(), request.getDbSchemaId());
			if(valueObjects == null) {
				logger.error("generateValueObject failed!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateValueObject failed!");
			}
			if(request.isGenerateDataMapping()) {
				if(!modelDrivenAbility.generateDataMappings(domainObjects, valueObjects)) {
					logger.error("generateDataMappings failed!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateDataMappings failed!");
				}
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("generateAll end.");
		}
		return true;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DbSchema addDbSchema(DbSchema dbSchema) throws EngineException {
		Date nowDate = new Date();
		dbSchema.setCreateTime(nowDate);
		dbSchema.setUpdateTime(nowDate);
		dbSchema.setStatus(Status.EFF);
		return this.dbSchemaSV.save(dbSchema);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveDbSchema(DbSchema dbSchema) throws EngineException {
		Date nowDate = new Date();
		dbSchema.setUpdateTime(nowDate);
		return this.dbSchemaSV.updateById(dbSchema);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ServerResource addServerResource(ServerResource serverResource) {
		Date nowDate = new Date();
		serverResource.setCreateTime(nowDate);
		serverResource.setUpdateTime(nowDate);
		serverResource.setStatus(Status.EFF);
		return this.serverResourceSV.save(serverResource);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteServerResource(String serverResourceId) {
		return this.serverResourceSV.delete(serverResourceId);
	}
	
	@Override
	public ServerResource queryServerResource(String serverResourceId) {
		return this.serverResourceSV.byId(serverResourceId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveServerResource(ServerResource serverResource) {
		serverResource.setUpdateTime(new Date());
		return this.serverResourceSV.updateById(serverResource);
	}
	
	@Override
	public DbSchema loadDbSchema(String id) {
		return this.dbSchemaSV.byId(id);
	}

	@Override
	public List<DbSchema> listDbSchema(String systemId) {
		return dbSchemaSV.listBySystemId(systemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDbSchema(String id) {
		return this.dbSchemaSV.delete(id);
	}
	
	@Override
	public List<BusinessDomain> listBusinessDomain(String systemId) {
		return businessDomainSV.listBySystemId(systemId);
	}
	
	@Override
	public BusinessDomain queryBusinessDomain(String businessDomainId) {
		return businessDomainSV.byId(businessDomainId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DbTable addDbTable(DbTable dbTable) throws EngineException {
		Date nowDate = new Date();
		dbTable.setCreateTime(nowDate);
		dbTable.setUpdateTime(nowDate);
		dbTable.setStatus(Status.EFF);
		return this.dbTableSV.save(dbTable);
	}
	
	@Override
	public DbTable loadDbTable(String id) {
		DbTable table = this.dbTableSV.byId(id);
		if(table != null) {
			table.setDbColumns(this.dbColumnSV.getByTableId(id));
			table.setDbSchema(this.dbSchemaSV.byId(table.getSchemaId()));
		}
		return table;
	}
	
	@Override
	public List<DbTable> listDbTables(String systemId) {
		return this.dbTableSV.listBySystem(systemId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveDbTable(DbTable dbTable) throws EngineException {
		dbTable.setUpdateTime(new Date());
		return this.dbTableSV.updateById(dbTable);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDbTable(String id) {
		int result = this.dbTableSV.delete(id);
		if(result > 0) {
			this.objectRelationMappingSV.deleteByTableId(id);
		}
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DbColumn addDbColumn(DbColumn dbColumn) throws EngineException {
		Date nowDate = new Date();
		dbColumn.setCreateTime(nowDate);
		dbColumn.setUpdateTime(nowDate);
		return this.dbColumnSV.save(dbColumn);
	}
	
	@Override
	public DbColumn loadDbColumn(String dbColumnId) {
		return this.dbColumnSV.byId(dbColumnId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DbColumn saveDbColumn(DbColumn dbColumn) throws EngineException {
		dbColumn.setUpdateTime(new Date());
		if( this.dbColumnSV.updateById(dbColumn) > 0) {
			return dbColumn;
		}else {
			return null;
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDbColumn(String dbColumnId) {
		return this.dbColumnSV.delete(dbColumnId);
	}
	
	@Override
	public List<ObjectRelationMapping> queryObjectRelationMappingsByDomainObjectId(String domainObjectId) {
		List<ObjectRelationMapping>  mappings = this.objectRelationMappingSV.listByDomainObjectId(domainObjectId);
		if(mappings != null && mappings.size() > 0) {
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setRelDbTable(dbTableSV.byId(mapping.getTableId()));
				mapping.setRelDomainObject(domainObjectSV.loadDomainObjectAndAttrs(mapping.getDomainObjectId()));
			}
		}
		return mappings;
	}
	
	@Override
	public List<ObjectRelationMapping> queryObjectRelationMappings(String systemId) {
		List<ObjectRelationMapping>  mappings = this.objectRelationMappingSV.listBySystem(systemId);
		if(mappings != null && mappings.size() > 0) {
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setRelDbTable(dbTableSV.byId(mapping.getTableId()));
				mapping.setRelDomainObject(domainObjectSV.loadDomainObjectAndAttrs(mapping.getDomainObjectId()));
			}
		}
		return mappings;
	}
	
	@Override
	public ObjectRelationMapping queryObjectRelationMapping(String ormId) {
		return this.objectRelationMappingSV.byId(ormId);
	}
	
	@Override
	public List<ObjectRelationMapping> queryObjectRelationMappingsByTableId(String dbTableId) {
		List<ObjectRelationMapping>  mappings = this.objectRelationMappingSV.listByTableId(dbTableId);
		if(mappings != null && mappings.size() > 0) {
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setRelDbTable(dbTableSV.byId(mapping.getTableId()));
				mapping.setRelDomainObject(domainObjectSV.loadDomainObjectAndAttrs(mapping.getDomainObjectId()));
			}
		}
		return mappings;
	}
	
	@Override
	public List<DataMapping> queryDataMappingsByFromObject(String objectType, String objectId) throws EngineException {
		List<DataMapping> list = dataMappingSV.listByFromObject(objectType, objectId);
		if(list != null && list.size() > 0) {
			final AttributedObject fromObject;
			if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {
				fromObject = domainObjectSV.loadDomainObjectAndAttrs(objectId);
			}else if(ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
				fromObject = valueObjectSV.byId(objectId);
			}else {
				logger.error("invalid objectType: {}!", objectType);
				throw new EngineException(ResultStatus.BAD_REQUEST, "invalid objectType!");
			}
			list.forEach((mapping) -> {
				mapping.setFromObject(fromObject);
				AttributedObject toObject = null;
				if(ManagedObjectType.DOMAIN_OBJECT.equals(mapping.getToObjectType())) {
					toObject = domainObjectSV.loadDomainObjectAndAttrs(mapping.getToObjectId());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(mapping.getToObjectType())) {
					toObject = valueObjectSV.byId(mapping.getToObjectId());
				}
				mapping.setToObject(toObject);
				
				if(mapping.getAttributeMappings() != null && mapping.getAttributeMappings().size() > 0) {
					mapping.getAttributeMappings().forEach((attrMapping)-> {
						attrMapping.setFromAttribute(attributeSV.byId(attrMapping.getFromAttributeId()));
						attrMapping.setToAttribute(attributeSV.byId(attrMapping.getToAttributeId()));
					});

					mapping.getAttributeMappings().sort((a,b) -> {
						if(a.getFromAttribute() == null) {
							return -1;
						}else if(b.getFromAttribute() == null) {
							return 1;
						}else {
							return a.getFromAttribute().getCode().compareTo(b.getFromAttribute().getCode());
						}
					});
				}
			});
		}
		return list;
	}
	
	@Override
	public List<DataMapping> queryDataMappingsByToObject(String objectType, String objectId)  throws EngineException{
		List<DataMapping> list =  dataMappingSV.listByToObject(objectType, objectId);
		if(list != null && list.size() > 0) {
			final AttributedObject toObject;
			if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {
				toObject = domainObjectSV.loadDomainObjectAndAttrs(objectId);
			}else if(ManagedObjectType.VALUE_OBJECT.equals(objectType)) {
				toObject = valueObjectSV.byId(objectId);
			}else {
				logger.error("invalid objectType: {}!", objectType);
				throw new EngineException(ResultStatus.BAD_REQUEST, "invalid objectType!");
			}
			list.forEach((mapping) -> {
				mapping.setToObject(toObject);
				AttributedObject fromObject = null;
				if(ManagedObjectType.DOMAIN_OBJECT.equals(mapping.getFromObjectType())) {
					fromObject = domainObjectSV.loadDomainObjectAndAttrs(mapping.getFromObjectId());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(mapping.getFromObjectType())) {
					fromObject = valueObjectSV.byId(mapping.getFromObjectId());
				}
				mapping.setFromObject(fromObject);
				
				if(mapping.getAttributeMappings() != null && mapping.getAttributeMappings().size() > 0) {
					mapping.getAttributeMappings().forEach((attrMapping)-> {
						attrMapping.setFromAttribute(attributeSV.byId(attrMapping.getFromAttributeId()));
						attrMapping.setToAttribute(attributeSV.byId(attrMapping.getToAttributeId()));
					});
					mapping.getAttributeMappings().sort((a,b) -> {
						if(a.getFromAttribute() == null) {
							return -1;
						}else if(b.getFromAttribute() == null) {
							return 1;
						}else {
							return a.getFromAttribute().getCode().compareTo(b.getFromAttribute().getCode());
						}
					});
				}
			});
		}
		return list;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataMapping addDataMappingWidthAttributeMapping(DataMapping dataMapping) throws EngineException {
		Date nowDate = new Date();
		dataMapping.setStatus(Status.EFF);
		dataMapping.setCreateTime(nowDate);
		dataMapping.setUpdateTime(nowDate);
		
		DataMapping dm = this.dataMappingSV.save(dataMapping);
		if(dm == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add DataMapping failed!");
		}
		return dm;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataMapping modifyDataMappingWithAttributMapping(DataMapping dataMapping) throws EngineException {
		Date nowDate = new Date();
		dataMapping.setUpdateTime(nowDate);
		
		DataMapping dm = this.dataMappingSV.update(dataMapping);
		if(dm == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "modify DataMapping failed!");
		}
		return dm;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ObjectRelationMapping addObjectRelationMappingWithColumnMapping(ObjectRelationMapping objectRelationMapping)
			throws EngineException {
		Date nowDate = new Date();
		objectRelationMapping.setStatus(Status.EFF);
		objectRelationMapping.setCreateTime(nowDate);
		objectRelationMapping.setUpdateTime(nowDate);
		ObjectRelationMapping orm = this.objectRelationMappingSV.save(objectRelationMapping);
		if(orm == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add ObjectRelationMapping failed!");
		}
		
		return orm;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ObjectRelationMapping modifyObjectRelationMappingWithColumnMapping(ObjectRelationMapping objectRelationMapping)
			throws EngineException {
		Date nowDate = new Date();
		objectRelationMapping.setUpdateTime(nowDate);
		ObjectRelationMapping orm = this.objectRelationMappingSV.update(objectRelationMapping);
		if(orm == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "modify ObjectRelationMapping failed!");
		}
		
		return orm;
	}
	
	@Override
	public List<DataMapping> queryDataMappingsByObject(String objectType, String objectId) throws EngineException {
		List<DataMapping> retDataMappings = new ArrayList<>();
		retDataMappings.addAll(this.queryDataMappingsByFromObject(objectType, objectId));
		retDataMappings.addAll(this.queryDataMappingsByToObject(objectType, objectId));
		return retDataMappings;
	}
	
	@Override
	public List<DataMapping> queryDataMappings(String systemId)  throws EngineException{
		List<DataMapping> list =  dataMappingSV.listBySystemId(systemId);
		if(list != null && list.size() > 0) {
			list.forEach((mapping) -> {
				AttributedObject fromObject = null, toObject = null;
				if(ManagedObjectType.DOMAIN_OBJECT.equals(mapping.getFromObjectType())) {
					fromObject = domainObjectSV.loadDomainObjectAndAttrs(mapping.getFromObjectId());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(mapping.getFromObjectType())) {
					fromObject = valueObjectSV.byId(mapping.getFromObjectId());
				}
				mapping.setFromObject(fromObject);
				
				if(ManagedObjectType.DOMAIN_OBJECT.equals(mapping.getToObjectType())) {
					toObject = domainObjectSV.loadDomainObjectAndAttrs(mapping.getToObjectId());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(mapping.getToObjectType())) {
					toObject = valueObjectSV.byId(mapping.getToObjectId());
				}
				mapping.setToObject(toObject);
				
				if(mapping.getAttributeMappings() != null && mapping.getAttributeMappings().size() > 0) {
					mapping.getAttributeMappings().forEach((attrMapping)-> {
						attrMapping.setFromAttribute(attributeSV.byId(attrMapping.getFromAttributeId()));
						attrMapping.setToAttribute(attributeSV.byId(attrMapping.getToAttributeId()));
					});
				}
			});
		}
		return list;
	}
	
	@Override
	public DataMapping queryDataMapping(String dataMappingId) {
		return this.dataMappingSV.byId(dataMappingId);
	}
	
	@Override
	public List<SqlManagement> querySqlManagements(String systemId, String resourceType, String resourceId) {		
		List<SqlManagement> list = this.sqlManagementSV.list(systemId, resourceType, resourceId);
		if(list != null && list.size() > 0) {
			if(ServerResourceType.RESOURCE_TYPE_DATABASE.equals(resourceType)) {
				DbSchema dbSchema = this.dbSchemaSV.byId(resourceId);
				list.forEach((rowSql) -> {
					rowSql.setDbSchema(dbSchema) ;
				});
			}if(ServerResourceType.RESOURCE_TYPE_ES.equals(resourceType)) {
				ServerResource serverResource = this.serverResourceSV.byId(resourceId);
				list.forEach((rowSql) -> {
					rowSql.setServerResource(serverResource);
				});
			}
			
		}
		return list;
	}
	
	@Override
	public List<DomainObject> queryDomainObjects(String systemId) {
		return this.domainObjectSV.listDomainObjectBySystemId(systemId);
	}
	
	@Override
	public List<ValueObject> queryValueObjects(String systemId) {
		return this.valueObjectSV.listValueObjectBySystemId(systemId);
	}
	
	@Override
	public List<ServiceObject> queryServiceObjects(String systemId) {
		return this.serviceObjectSV.listBySystemId(systemId);
	}
	
	@Override
	public ServiceObject queryServiceObject(String id) {
		return this.serviceObjectSV.byId(id);
	}
	
	@Override
	public List<Operation> queryOperations(String objectType, String objectId) {
		return this.operationSV.listByObjectId(objectType, objectId);
	}
	
	@Override
	public List<Operation> queryOperationsNoParam(String objectType, String objectId) {
		return this.operationSV.listByObjectIdNoParam(objectType, objectId);
	}
	
	@Override
	public List<ValidateRuleGroup> queryValidateRuleGroups(String systemId) {
		List<ValidateRuleGroup> list = this.validateRuleGroupSV.listBySystemId(systemId);
		if(list != null && list.size() > 0) {
			list.forEach((ruleGroup) -> {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.domainObjectSV.loadDomainObjectAndAttrs(ruleGroup.getObjectId()).getCode());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.valueObjectSV.byId(ruleGroup.getObjectId()).getCode());
				}else if(ManagedObjectType.SERVICE_METHOD.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.operationSV.byId(ruleGroup.getObjectId()).getCode());
				}
				
			});
		}
		return list;
	}
	
	@Override
	public List<ValidateRuleGroup> queryValidateRuleGroups(String systemId, String objectType, String objectId) {
		List<ValidateRuleGroup> list = this.validateRuleGroupSV.listBySystemId(systemId, objectType, objectId);
		if(list != null && list.size() > 0) {
			list.forEach((ruleGroup) -> {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.domainObjectSV.loadDomainObjectAndAttrs(ruleGroup.getObjectId()).getCode());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.valueObjectSV.byId(ruleGroup.getObjectId()).getCode());
				}else if(ManagedObjectType.SERVICE_METHOD.equals(ruleGroup.getObjectType())) {
					ruleGroup.setObjectCode(this.operationSV.byId(ruleGroup.getObjectId()).getCode());
				}
				
			});
		}
		return list;
	}
	
	@Override
	public ValidateRuleGroupInitInfo loadValidateRuleGroupInitInfo(ValidateRuleGroupInitInfo initInfo) {
		if(ManagedObjectType.DOMAIN_OBJECT.equals(initInfo.getObjectType())) {
			initInfo.setTargetObject(this.domainObjectSV.loadDomainObjectAndAttrs(initInfo.getObjectId()));
		}else if(ManagedObjectType.VALUE_OBJECT.equals(initInfo.getObjectType())) {
			initInfo.setTargetObject(this.valueObjectSV.byId(initInfo.getObjectId()));
		}else if(ManagedObjectType.SERVICE_METHOD.equals(initInfo.getObjectType())) {
			initInfo.setOperation(this.operationSV.byId(initInfo.getObjectId()));
			List<Parameter> parameters = this.parameterSV.getInParamsByOperationId(initInfo.getObjectId());
			JsonObject inParams = new JsonObject();
			if(parameters != null && !parameters.isEmpty()) {
				for(Parameter param : parameters) {
					if(ManagedObjectType.JAVA_OBJECT.equals(param.getParamType())) {
						inParams.addProperty(param.getCode(), param.getParamClass());
					}else if(ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
						inParams.add(param.getCode(), JsonUtils.toJsonElement(this.domainObjectSV.loadDomainObjectAndAttrs(param.getParamClass()).getAttributes()));
					}else if(ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
						inParams.add(param.getCode(), JsonUtils.toJsonElement(this.valueObjectSV.byId(param.getParamClass()).getAttributes()));
					}
				}
			}
			initInfo.setInParams(inParams);
		}
		
		initInfo.setValidateRuleTypes(ValidateAbility.getInstance().getValidateRuleTypes());
		
		initInfo.setDbSchemas(this.dbSchemaSV.listBySystemId(initInfo.getSystemId()));
		return initInfo;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ValidateRuleGroup saveValidateRuleGroup(ValidateRuleGroup validateRuleGroup) {
		return this.validateRuleGroupSV.saveValidateRuleGroup(validateRuleGroup);
	}
	
	@Override
	public ValidateRuleGroup loadValidateRuleGroup(String ruleGroupId) {
		return this.validateRuleGroupSV.loadValidateRuleGroup(ruleGroupId);
	}
	
	@Override
	public ValidateRuleGroup queryValidateRuleGroup(String ruleGroupId) {
		return this.validateRuleGroupSV.byId(ruleGroupId);
	}
	
	@Override
	public List<ServerResource> listServerResources(String systemId, String resourceType) {
		return this.serverResourceSV.list(systemId, resourceType);
	}
	
	@Override
	public List<ExposedService> listExposedServices(String systemId) {
		return this.exposedServiceSV.listAllBySystem(systemId);
	}
	
	@Override
	public WorkflowProcessResource loadWorkflowProcessResource(String resourceId) {
		return this.workflowProcessResourceSV.byId(resourceId);
	}
	
	@Override
	public List<WorkflowProcessResource> listWorkflowProcessResourcesDeployed(String systemId) {
		return this.workflowProcessResourceSV.listDeployed(systemId);
	}
	
	@Override
	public String queryLoginSessionKey(String systemId) {
		BusinessSystemAuth auth = this.businessSystemAuthSV.queryBySystem(systemId);
		String key = null;
		if(auth != null && auth.getAuthType() != null && auth.getAuthType().trim().length() > 0 && !AuthTypes.AUTH_TYPE_NONE.equals(auth.getAuthType())) {
			if(auth.getUserInfoKey() != null && auth.getUserInfoKey().trim().length() != 0) {
				key = auth.getUserInfoKey();
			}else {
				key = BaseAuthenticateManager.SESSION_KEY_USER_INFO;
			}
		} else {
			key = "未开启认证模式！";
		}
		return key;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public MessageConsumer saveMessageConsumer(MessageConsumer messageConsumer) {
		return messageConsumerSV.save(messageConsumer);
	}
	
	@Override
	public MessageConsumer queryMessageConsumer(String systemId, String id) {
		return messageConsumerSV.bySystemAndId(systemId, id);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteMessageConsumer(String systemId, String id) {
		return messageConsumerSV.delete(systemId, id);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public MessageConsumer modifyMessageConsumer(MessageConsumer messageConsumer) {
		return messageConsumerSV.update(messageConsumer);
	}

	@Override
	public List<DataTransferMethod> listDataTransferMethod() {
		return dataTransferMethodSV.list();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DynamicMapping addDynamicMapping(DynamicMapping dynamicMapping) {
		return dynamicMappingSV.add(dynamicMapping);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DynamicMapping saveDynamicMapping(DynamicMapping dynamicMapping) {
		return dynamicMappingSV.save(dynamicMapping);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDynamicMapping(String dynamicMappingId) {
		return dynamicMappingSV.delete(dynamicMappingId);
	}

	@Override
	public DynamicMapping loadDynamicMapping(String dynamicMappingId) {
		return dynamicMappingSV.loadDynamicMapping(dynamicMappingId);
	}

	@Override
	public List<DynamicMapping> queryDynamicMappingByCondition(DynamicMapping dynamicMapping) {
		return dynamicMappingSV.queryByCondition(dynamicMapping);
	}

	@Override
	public DynamicMapping queryDynamicMapping(String dynamicMappingId) {
		return dynamicMappingSV.byId(dynamicMappingId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DynamicMappingDetail addDynamicMappingDetail(DynamicMappingDetail dynamicMappingDetail) {
		return dynamicMappingDetailSV.add(dynamicMappingDetail);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DynamicMappingDetail saveDynamicMappingDetail(DynamicMappingDetail dynamicMappingDetail) {
		return dynamicMappingDetailSV.save(dynamicMappingDetail);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDynamicMappingDetail(String dynamicMappingDetailId) {
		return dynamicMappingDetailSV.delete(dynamicMappingDetailId);
	}

	@Override
	public DynamicMappingDetail loadDynamicMappingDetail(String dynamicMappingDetailId) {
		return dynamicMappingDetailSV.byId(dynamicMappingDetailId);
	}

	@Override
	public List<FileObjectMapping> loadFileObjectMappings(String objectType, String objectId) {
		List<FileObjectMapping> fileObjectMappings = fileObjectMappingSV.queryByObject(objectType, objectId);
		if(!CollectionUtils.isEmpty(fileObjectMappings)) {
			ManagedObject managedObject = this.queryManagedObject(objectType, objectId);
			final String objectCode;
			if(managedObject != null) {
				objectCode = managedObject.getCode();
			}else {
				objectCode = null;
			}
			fileObjectMappings.forEach(fileObjectMapping-> {
				fileObjectMapping.setObjectCode(objectCode);
				if(!CollectionUtils.isEmpty(fileObjectMapping.getAttributeColumnMappings())) {
					fileObjectMapping.getAttributeColumnMappings().forEach(attrColMapping-> {
						if(!StringUtils.isBlank(attrColMapping.getAttributeId())) {
							Attribute attr = attributeSV.byId(attrColMapping.getAttributeId());
							if(attr != null) {
								attrColMapping.setAttributeCode(attr.getCode());
							}else {
								attrColMapping.setAttributeCode(attrColMapping.getAttributeId());
							}
							
						}
					});
				}
			});
		}
		return fileObjectMappings;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FileObjectMapping addWithAttributeColumnMapping(FileObjectMapping fileObjectMapping)  throws EngineException{
		FileObjectMapping result = fileObjectMappingSV.add(fileObjectMapping);
		if(result == null) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "addWithAttributeColumnMapping failed!");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FileObjectMapping saveWithAttributeColumnMapping(FileObjectMapping fileObjectMapping)  throws EngineException{
		FileObjectMapping result = fileObjectMappingSV.save(fileObjectMapping);
		if(result == null) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "saveWithAttributeColumnMapping failed!");
		}
		return result;
	}

	@Override
	public FileObjectMapping queryFileObjectMapping(String fileObjectMappingId) {
		return fileObjectMappingSV.byId(fileObjectMappingId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DictResource addDictResource(DictResource dictResource)throws EngineException {
		if(dictResourceSV.add(dictResource) > 0) {
			return dictResource;
		}else {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dictResource failed!");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveDictResource(DictResource dictResource)throws EngineException {
		return dictResourceSV.save(dictResource);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDictResource(String dictResourceId)throws EngineException {
		return dictResourceSV.delete(dictResourceId);
	}

	@Override
	public DictResource loadDictResource(String dictResourceId) {
		return dictResourceSV.byId(dictResourceId);
	}

	@Override
	public List<DictResource> listDictResources(String systemId) {
		return dictResourceSV.listBySystem(systemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public SecurityKeyResource addSecurityKeyResource(SecurityKeyResource securityKeyResource)throws EngineException {
		if(securityKeyResourceSV.add(securityKeyResource) > 0) {
			securityKeyResource.setPrivateKeyFile(null);
			securityKeyResource.setPublicKeyFile(null);
			return securityKeyResource;
		}else {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add securityKeyResource failed!");
		}
	}

	@Override
	public List<SecurityKeyResource> listSecurityKeyResources(String systemId) {
		return securityKeyResourceSV.list(systemId);
	}

	@Override
	public SecurityKeyResource loadSecurityKeyResource(String securityKeyResourceId) {
		return securityKeyResourceSV.byId(securityKeyResourceId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteSecurityKeyResource(String securityKeyResourceId)throws EngineException{
		return securityKeyResourceSV.delete(securityKeyResourceId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public SecurityKeyResource saveSecurityKeyResource(SecurityKeyResource securityKeyResource)throws EngineException {
		if(securityKeyResourceSV.save(securityKeyResource) > 0) {
			securityKeyResource.setPrivateKeyFile(null);
			securityKeyResource.setPublicKeyFile(null);
			return securityKeyResource;
		}else {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add securityKeyResource failed!");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public CronJob addCronJob(CronJob cronJob)throws EngineException {
		if(cronJobSV.add(cronJob) > 0) {
			return cronJob;
		}else {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "add cronJob failed!");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveCronJob(CronJob cronJob)throws EngineException {
		return cronJobSV.save(cronJob);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteCronJob(String cronJobId)throws EngineException {
		return cronJobSV.delete(cronJobId);
	}

	@Override
	public CronJob loadCronJob(String cronJobId) {
		return cronJobSV.byId(cronJobId);
	}

	@Override
	public List<CronJob> listCronJobs(String systemId) {
		return cronJobSV.listBySystem(systemId);
	}
}
