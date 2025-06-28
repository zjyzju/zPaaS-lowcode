package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.mapper.FuncRegionOperationMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncRegionOperation {
	private String id;

    private String name;

    private String funcId;

    private String funcRegionId;

    private String tplOperationId;
    
    private String operationType;
    
    private String operationCfg;

    private String isUserDefined;
    
    private String exposedServiceId;

    private String serviceObjectId;

    private String serviceOperationId;

    private String objectAssignId;

    private String targetRegionId;

    private String displayType;

    private Integer displayOrder;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private ExposedService exposedService;
    
    private ServiceObject serviceObject;
    
    private Operation operation;
    
    @Autowired
    private FuncRegionOperationMapper mapper;
    
    @Autowired
    private ExposedService exposedServiceSV;
    
    @Autowired
    private ServiceObject serviceObjectSV;
    
    @Autowired 
    private Operation operationSV;
    
    @Autowired
    private Parameter parameterSV;
    
    @Autowired
    private DomainObject domainObjectSV;
    
    @Autowired
    private ValueObject valueObjectSV;

	public FuncRegionOperation byId(String id) {
    	FuncRegionOperation funcRegionOperation = mapper.selectByPrimaryKey(id);
    	if(funcRegionOperation != null && funcRegionOperation.getExposedServiceId() != null && funcRegionOperation.getExposedServiceId().trim().length() > 0) {
    		funcRegionOperation.setExposedService(exposedServiceSV.byId(funcRegionOperation.getExposedServiceId()));
    		funcRegionOperation.setServiceObject(serviceObjectSV.byId(funcRegionOperation.getServiceObjectId()));
    		funcRegionOperation.setOperation(operationSV.byId(funcRegionOperation.getServiceOperationId()));
			List<Parameter> parameters = parameterSV.listByOperationId(funcRegionOperation.getServiceOperationId());
			if(parameters != null && !parameters.isEmpty()) {
				List<Parameter> inParams = new ArrayList<>();
				for(Parameter parameter: parameters) {
					if(YesOrNo.NO.equals(parameter.getIsInParam())) {
						funcRegionOperation.getOperation().setOutParam(parameter);
					}else {
						if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
							parameter.setParamClassObject(domainObjectSV.loadDomainObjectAndAttrs(parameter.getParamClass()));
						}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
							parameter.setParamClassObject(valueObjectSV.byId(parameter.getParamClass()));
						}
						inParams.add(parameter);
					}
				}
				funcRegionOperation.getOperation().setInParams(inParams);
			}
    	}
    	return funcRegionOperation;
    }
    
    public FuncRegionOperation insert(FuncRegionOperation funcRegionOperation) {
    	if(funcRegionOperation.getId() == null || funcRegionOperation.getId().trim().length() == 0) {
    		funcRegionOperation.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	funcRegionOperation.setCreateTime(nowDate);
    	funcRegionOperation.setUpdateTime(nowDate);
    	if(mapper.insert(funcRegionOperation) > 0) {
    		return funcRegionOperation;
    	}else {
    		return null;
    	}
    }
    
    public FuncRegionOperation save(FuncRegionOperation funcRegionOperation) {
    	funcRegionOperation.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(funcRegionOperation) > 0) {
    		return funcRegionOperation;
    	}else {
    		return null;
    	}
    }
    
    public int delete(String id) {
    	return mapper.deleteByPrimaryKey(id);
    }

	public int delete(String funcId, String funcRegionId) {
		FuncRegionOperationExample criteria = new FuncRegionOperationExample();
		criteria.createCriteria().andFuncIdEqualTo(funcId).andFuncRegionIdEqualTo(funcRegionId);
		return mapper.deleteByExample(criteria);
	}

	public int batchSave(List<FuncRegionOperation> regionOperations) throws EngineException {
		if(CollectionUtils.isEmpty(regionOperations)) {
			return 0;
		}
		for(FuncRegionOperation regionOperation : regionOperations) {
			Date nowDate = new Date();
			if(StringUtils.isBlank(regionOperation.getId())) {
				regionOperation.setId(KeyGenerate.uuidKey());
				regionOperation.setCreateTime(nowDate);
			}
			regionOperation.setUpdateTime(nowDate);
			if(mapper.insert(regionOperation)<=0) {
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "save regionOperation failed!");
			}
		}
		return regionOperations.size();
	}
    
    public FuncRegionOperation create(FuncRegionOperation funcRegionOperation) {
		Date nowDate = new Date();
    	if(StringUtils.isBlank(funcRegionOperation.getId())) {
    		funcRegionOperation.setId(KeyGenerate.uuidKey());
			funcRegionOperation.setCreateTime(nowDate);
    	}
    	funcRegionOperation.setUpdateTime(nowDate);
    	if(mapper.insert(funcRegionOperation) > 0) {
    		return funcRegionOperation;
    	}else {
    		return null;
    	}
    }

	public int saveFuncRegionOperation(FuncRegionOperation funcRegionOperation) {
		Date nowDate = new Date();
    	if(StringUtils.isBlank(funcRegionOperation.getId())) {
    		funcRegionOperation.setId(KeyGenerate.uuidKey());
			funcRegionOperation.setCreateTime(nowDate);
    	}
    	funcRegionOperation.setUpdateTime(nowDate);
    	return mapper.insert(funcRegionOperation);
    }
    
    public List<FuncRegionOperation> loadByFuncRegion(String funcRegionId) {
    	FuncRegionOperationExample criteria = new FuncRegionOperationExample();
    	criteria.createCriteria().andFuncRegionIdEqualTo(funcRegionId);
    	criteria.setOrderByClause("display_order");
    	List<FuncRegionOperation> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		for(FuncRegionOperation operation: list) {
    			if(operation.getServiceOperationId() == null || operation.getServiceObjectId().trim().length() == 0) {
    				continue;
    			}
    			operation.setExposedService(exposedServiceSV.byId(operation.getExposedServiceId()));
    			operation.setServiceObject(serviceObjectSV.byId(operation.getServiceObjectId()));
    			operation.setOperation(operationSV.byId(operation.getServiceOperationId()));
    			
    			List<Parameter> parameters = parameterSV.listByOperationId(operation.getServiceOperationId());
    			if(parameters != null && !parameters.isEmpty()) {
    				List<Parameter> inParams = new ArrayList<>();
    				for(Parameter parameter: parameters) {
    					if(YesOrNo.NO.equals(parameter.getIsInParam())) {
    						operation.getOperation().setOutParam(parameter);
    					}else {
    						if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
    							parameter.setParamClassObject(domainObjectSV.loadDomainObjectAndAttrs(parameter.getParamClass()));
    						}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
    							parameter.setParamClassObject(valueObjectSV.byId(parameter.getParamClass()));
    						}
    						inParams.add(parameter);
    					}
    				}
    				operation.getOperation().setInParams(inParams);
    			}
    		}
    	}
    	return list;
    }

	public List<FuncRegionOperation> listBySystem(String systemId) {
    	FuncRegionOperationExample criteria = new FuncRegionOperationExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
	}

	public List<FuncRegionOperation> list() {
    	FuncRegionOperationExample criteria = new FuncRegionOperationExample();
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
	}

	public List<FuncRegionOperation> loadByFunc(String funcId) {
    	FuncRegionOperationExample criteria = new FuncRegionOperationExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId);
    	criteria.setOrderByClause("display_order");
    	List<FuncRegionOperation> list = mapper.selectByExample(criteria);
    	if(!CollectionUtils.isEmpty(list)) {
    		for(FuncRegionOperation operation: list) {
    			if(StringUtils.isBlank(operation.getServiceOperationId())) {
    				continue;
    			}
    			operation.setExposedService(exposedServiceSV.byId(operation.getExposedServiceId()));
    			operation.setServiceObject(serviceObjectSV.byId(operation.getServiceObjectId()));
    			operation.setOperation(operationSV.byId(operation.getServiceOperationId()));
    			List<Parameter> parameters = parameterSV.listByOperationId(operation.getServiceOperationId());
    			if(parameters != null && !parameters.isEmpty()) {
    				List<Parameter> inParams = new ArrayList<>();
    				for(Parameter parameter: parameters) {
    					if(YesOrNo.NO.equals(parameter.getIsInParam())) {
    						operation.getOperation().setOutParam(parameter);
    					}else {
    						if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
    							parameter.setParamClassObject(domainObjectSV.loadDomainObjectAndAttrs(parameter.getParamClass()));
    						}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
    							parameter.setParamClassObject(valueObjectSV.byId(parameter.getParamClass()));
    						}
    						inParams.add(parameter);
    					}
    				}
    				operation.getOperation().setInParams(inParams);
    			}
				
    		}
    	}
    	return list;
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    public String getFuncRegionId() {
        return funcRegionId;
    }

    public void setFuncRegionId(String funcRegionId) {
        this.funcRegionId = funcRegionId == null ? null : funcRegionId.trim();
    }

    public String getTplOperationId() {
        return tplOperationId;
    }

    public void setTplOperationId(String tplOperationId) {
        this.tplOperationId = tplOperationId == null ? null : tplOperationId.trim();
    }

    public String getIsUserDefined() {
        return isUserDefined;
    }

    public void setIsUserDefined(String isUserDefined) {
        this.isUserDefined = isUserDefined == null ? null : isUserDefined.trim();
    }

    public String getServiceObjectId() {
        return serviceObjectId;
    }

    public void setServiceObjectId(String serviceObjectId) {
        this.serviceObjectId = serviceObjectId == null ? null : serviceObjectId.trim();
    }

    public String getServiceOperationId() {
        return serviceOperationId;
    }

    public void setServiceOperationId(String serviceOperationId) {
        this.serviceOperationId = serviceOperationId == null ? null : serviceOperationId.trim();
    }

    public String getTargetRegionId() {
        return targetRegionId;
    }

    public void setTargetRegionId(String targetRegionId) {
        this.targetRegionId = targetRegionId == null ? null : targetRegionId.trim();
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType == null ? null : displayType.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getExposedServiceId() {
		return exposedServiceId;
	}

	public void setExposedServiceId(String exposedServiceId) {
		this.exposedServiceId = exposedServiceId;
	}

	public ExposedService getExposedService() {
		return exposedService;
	}

	public void setExposedService(ExposedService exposedService) {
		this.exposedService = exposedService;
	}

	public ServiceObject getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(ServiceObject serviceObject) {
		this.serviceObject = serviceObject;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}


	public String getObjectAssignId() {
		return objectAssignId;
	}


	public void setObjectAssignId(String objectAssignId) {
		this.objectAssignId = objectAssignId;
	}


	public String getOperationCfg() {
		return operationCfg;
	}


	public void setOperationCfg(String operationCfg) {
		this.operationCfg = operationCfg;
	}
}