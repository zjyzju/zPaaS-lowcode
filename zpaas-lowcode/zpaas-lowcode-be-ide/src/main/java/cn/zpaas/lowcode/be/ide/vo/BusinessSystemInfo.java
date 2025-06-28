package cn.zpaas.lowcode.be.ide.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.CronJob;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DictResource;
import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.vo.BusinessDomainInfo;

/**
 * 业务系统信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class BusinessSystemInfo {
	
	private BusinessSystem businessSystem;
	
	private List<BusinessDomainInfo> businessDomains;
	
	private List<FlowNode> flowNodes;
	
	private List<DbSchema> dbSchemas;
	
	private List<ServerResource> serverResources;
	
	private List<WorkflowProcessResource> workflowProcessResources;
	
	private List<MessageConsumer> messageConsumers;

	private List<DynamicMapping> dynamicMappings;

	private List<DictResource> dictResources;
	
	private List<SecurityKeyResource> securityKeyResources;

	private List<CronJob> cronJobs;

	public List<CronJob> getCronJobs() {
		return cronJobs;
	}

	public void setCronJobs(List<CronJob> cronJobs) {
		this.cronJobs = cronJobs;
	}

	public List<SecurityKeyResource> getSecurityKeyResources() {
		return securityKeyResources;
	}

	public void setSecurityKeyResources(List<SecurityKeyResource> securityKeyResources) {
		this.securityKeyResources = securityKeyResources;
	}

	public List<DictResource> getDictResources() {
		return dictResources;
	}

	public void setDictResources(List<DictResource> dictResources) {
		this.dictResources = dictResources;
	}

	public List<DynamicMapping> getDynamicMappings() {
		return dynamicMappings;
	}

	public void setDynamicMappings(List<DynamicMapping> dynamicMappings) {
		this.dynamicMappings = dynamicMappings;
	}

	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}

	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}

	public List<BusinessDomainInfo> getBusinessDomains() {
		return businessDomains;
	}

	public void setBusinessDomains(List<BusinessDomainInfo> businessDomains) {
		this.businessDomains = businessDomains;
	}

	public List<FlowNode> getFlowNodes() {
		return flowNodes;
	}

	public void setFlowNodes(List<FlowNode> flowNodes) {
		this.flowNodes = flowNodes;
	}

	public List<DbSchema> getDbSchemas() {
		return dbSchemas;
	}

	public void setDbSchemas(List<DbSchema> dbSchemas) {
		this.dbSchemas = dbSchemas;
	}

	public List<ServerResource> getServerResources() {
		return serverResources;
	}

	public void setServerResources(List<ServerResource> serverResources) {
		this.serverResources = serverResources;
	}

	public List<WorkflowProcessResource> getWorkflowProcessResources() {
		return workflowProcessResources;
	}

	public void setWorkflowProcessResources(List<WorkflowProcessResource> workflowProcessResources) {
		this.workflowProcessResources = workflowProcessResources;
	}

	public List<MessageConsumer> getMessageConsumers() {
		return messageConsumers;
	}

	public void setMessageConsumers(List<MessageConsumer> messageConsumers) {
		this.messageConsumers = messageConsumers;
	}
}
