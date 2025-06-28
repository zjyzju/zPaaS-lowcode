package cn.zpaas.lowcode.vo;

import java.util.Date;

/**
 * 管理对象更新vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class UpdateManagedObject {
	private String id;
	private String code;
	private String name;
	private String objectType;
	private String objectSubType;
	private String businessDomainId;
	private String objectContainerType;
	private String systemId;
	private String tenantId;
	private String description;
	private String status;
	private Date createTime;
	private Date updateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getObjectSubType() {
		return objectSubType;
	}
	public void setObjectSubType(String objectSubType) {
		this.objectSubType = objectSubType;
	}
	public String getBusinessDomainId() {
		return businessDomainId;
	}
	public void setBusinessDomainId(String businessDomainId) {
		this.businessDomainId = businessDomainId;
	}
	public String getObjectContainerType() {
		return objectContainerType;
	}
	public void setObjectContainerType(String objectContainerType) {
		this.objectContainerType = objectContainerType;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	

}
