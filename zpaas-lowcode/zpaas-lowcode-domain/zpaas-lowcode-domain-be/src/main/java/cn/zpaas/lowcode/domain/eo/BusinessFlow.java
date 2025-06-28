package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.BusinessFlowMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class BusinessFlow {
	
    private String id;

    private String name;

    private String transactionRequired;

    private String dbSchemaId;

    private String flowType;

    private String parentFlowId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<BusinessFlowNode> businessFlowNodes;
    
    @Autowired
    private BusinessFlowMapper mapper;
    
    public int save(BusinessFlow businessFlow) {
    	if(businessFlow.getId() == null || businessFlow.getId().trim().length() == 0) {
    		businessFlow.setId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(businessFlow);
    }
    
    public int updateById(BusinessFlow businessFlow) {
    	if(businessFlow.getId() == null || businessFlow.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(businessFlow);
    }
    
    public BusinessFlow byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<BusinessFlow> list() {
    	return mapper.selectByExample(null);
    }
    
    public List<BusinessFlow> listBySystemId(String systemId) {
    	BusinessFlowExample criteria = new BusinessFlowExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
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

    public String getTransactionRequired() {
        return transactionRequired;
    }

    public void setTransactionRequired(String transactionRequired) {
        this.transactionRequired = transactionRequired == null ? null : transactionRequired.trim();
    }

    public String getDbSchemaId() {
        return dbSchemaId;
    }

    public void setDbSchemaId(String dbSchemaId) {
        this.dbSchemaId = dbSchemaId == null ? null : dbSchemaId.trim();
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType == null ? null : flowType.trim();
    }

    public String getParentFlowId() {
        return parentFlowId;
    }

    public void setParentFlowId(String parentFlowId) {
        this.parentFlowId = parentFlowId == null ? null : parentFlowId.trim();
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

	public List<BusinessFlowNode> getBusinessFlowNodes() {
		return businessFlowNodes;
	}

	public void setBusinessFlowNodes(List<BusinessFlowNode> businessFlowNodes) {
		this.businessFlowNodes = businessFlowNodes;
	}
}