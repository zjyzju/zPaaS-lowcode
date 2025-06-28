package cn.zpaas.lowcode.vo;


/**
 * 领域对象方法一键暴露辅助功能vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class DomainOperationExpose {
	private String domainObjectId;
	private String operationId;
	private String serviceObjectId;
	private String httpMapping;
	private String httpMethod;
	private String systemId;
	private String tenantId;
	public String getDomainObjectId() {
		return domainObjectId;
	}
	public void setDomainObjectId(String domainObjectId) {
		this.domainObjectId = domainObjectId;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	public String getServiceObjectId() {
		return serviceObjectId;
	}
	public void setServiceObjectId(String serviceObjectId) {
		this.serviceObjectId = serviceObjectId;
	}
	public String getHttpMapping() {
		return httpMapping;
	}
	public void setHttpMapping(String httpMapping) {
		this.httpMapping = httpMapping;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
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
	
	
	
}
