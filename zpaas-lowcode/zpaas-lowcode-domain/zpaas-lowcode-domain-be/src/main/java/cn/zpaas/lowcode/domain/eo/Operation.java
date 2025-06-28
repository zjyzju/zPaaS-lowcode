package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.mapper.OperationMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class Operation {
	private String id;

    private String name;

    private String code;

    private String objectType;

    private String objectId;

    private String businessFlowId;
    
    private String ruleGroupId;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private Integer displayOrder;
    
    private Parameter outParam;
    
    private List<Parameter> inParams;
    
    private ValidateRuleGroup validateRuleGroup;
    
    @Autowired
    private OperationMapper mapper;
    
    @Autowired
    private Parameter parameter;

    @Autowired
    private DomainObject domainObjectSV;
    
    @Autowired
    private ValueObject valueObjectSV;
    
    public int updateById(Operation operation) {
    	return mapper.updateByPrimaryKey(operation);
    }
    
    public int disableOperation(String id) {
    	Operation operation = this.byId(id);
    	if(operation == null) {
    		return 0;
    	}
    	operation.setStatus(Status.EXP);
    	operation.setUpdateTime(new Date());
    	return this.updateById(operation);
    }
    
    public int save(Operation operation) {
    	if(operation.getId() == null || operation.getId().trim().length() == 0) {
    		operation.setId(KeyGenerate.uuidKey());
    	}
    	if(operation.getBusinessFlowId() == null || operation.getBusinessFlowId().trim().length() == 0) {
    		operation.setBusinessFlowId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(operation);
    }
    
    public Operation byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public int delete(String id) {
    	return mapper.deleteByPrimaryKey(id);
    }

    public Operation loadOperationAll(String id) {
        Operation operation = this.byId(id);
        if(operation != null) {
            List<Parameter> parameters = parameter.listByOperationId(operation.getId());
    		if(!CollectionUtils.isEmpty(parameters)) {
    			List<Parameter> inParams = new ArrayList<>();
    			for(Parameter parameter: parameters) {
    				if(YesOrNo.NO.equals(parameter.getIsInParam())) {
    					operation.setOutParam(parameter);
    				}else {
    					if(ManagedObjectType.DOMAIN_OBJECT.equals(parameter.getParamType())) {
    						parameter.setParamClassObject(domainObjectSV.loadDomainObjectAndAttrs(parameter.getParamClass()));
    					}else if(ManagedObjectType.VALUE_OBJECT.equals(parameter.getParamType())) {
    						parameter.setParamClassObject(valueObjectSV.byId(parameter.getParamClass()));
    					}
    					inParams.add(parameter);
    				}
    			}
    			operation.setInParams(inParams);
    		}
        }
        return operation;
    }
    
    public List<Operation> listByObjectId(String objectType, String domainObjectId) {
    	OperationExample criteria = new OperationExample();
    	criteria.createCriteria().andObjectIdEqualTo(domainObjectId).andObjectTypeEqualTo(objectType).andStatusEqualTo(Status.EFF);
    	List<Operation> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		for(Operation operation : list) {
    			List<Parameter> parameters = parameter.listByOperationId(operation.getId());
    			List<Parameter> inParams = new ArrayList<>();
    			Parameter outParam = null;
    			if(parameters != null && !parameters.isEmpty()) {
    				for(Parameter param : parameters) {
    					if(YesOrNo.NO.equals(param.getIsInParam())) {
    						outParam = param;
    					}else {
    						inParams.add(param);
    					}
    				}
    			}
    			operation.setInParams(inParams);
    			operation.setOutParam(outParam);
    		}
    	}
    	return list;
    }
    
    public List<Operation> listByObjectIdNoParam(String objectType, String objectId) {
    	OperationExample criteria = new OperationExample();
    	criteria.createCriteria().andObjectIdEqualTo(objectId).andObjectTypeEqualTo(objectType).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<Operation> listByObjectType(String objectType) {
    	OperationExample criteria = new OperationExample();
    	criteria.createCriteria().andObjectTypeEqualTo(objectType).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<Operation> listByObjectTypeAndSystem(String objectType, String systemId) {
    	OperationExample criteria = new OperationExample();
    	criteria.createCriteria().andObjectTypeEqualTo(objectType).andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType == null ? null : objectType.trim();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId == null ? null : objectId.trim();
    }

    public String getBusinessFlowId() {
        return businessFlowId;
    }

    public void setBusinessFlowId(String businessFlowId) {
        this.businessFlowId = businessFlowId == null ? null : businessFlowId.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

	public Parameter getOutParam() {
		return outParam;
	}

	public void setOutParam(Parameter outParam) {
		this.outParam = outParam;
	}

	public List<Parameter> getInParams() {
		return inParams;
	}

	public void setInParams(List<Parameter> inParams) {
		this.inParams = inParams;
	}

	public String getRuleGroupId() {
		return ruleGroupId;
	}

	public void setRuleGroupId(String ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}
    
    public ValidateRuleGroup getValidateRuleGroup() {
        return validateRuleGroup;
    }

    public void setValidateRuleGroup(ValidateRuleGroup validateRuleGroup) {
        this.validateRuleGroup = validateRuleGroup;
    }
}