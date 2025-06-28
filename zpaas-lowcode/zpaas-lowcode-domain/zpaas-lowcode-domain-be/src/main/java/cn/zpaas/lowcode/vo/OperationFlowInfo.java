package cn.zpaas.lowcode.vo;

import java.util.Date;

/**
 * 方法流程信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class OperationFlowInfo {
	
	private String id;

    private String name;

    private String code;

    private String objectType;

    private String objectId;

    private String businessFlowId;

    private String status;

    private String accessPrivilege;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private Integer displayOrder;
    
    private String transactionRequired;
    
    private String flowType;
    
    private String dbSchemaId;
    
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

    public String getAccessPrivilege() {
        return accessPrivilege;
    }

    public void setAccessPrivilege(String accessPrivilege) {
        this.accessPrivilege = accessPrivilege == null ? null : accessPrivilege.trim();
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

	public String getTransactionRequired() {
		return transactionRequired;
	}

	public void setTransactionRequired(String transactionRequired) {
		this.transactionRequired = transactionRequired;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getDbSchemaId() {
		return dbSchemaId;
	}

	public void setDbSchemaId(String dbSchemaId) {
		this.dbSchemaId = dbSchemaId;
	}

	
}