package cn.zpaas.lowcode.be.engine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributeMapping;
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
import cn.zpaas.lowcode.domain.eo.Dict;
import cn.zpaas.lowcode.domain.eo.DictResource;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.FileObjectMapping;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.domain.eo.ThirdpartySystem;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.ValidateRuleType;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;

/**
 * @author zjy
 * 引擎初始化时的统一数据获取服务
 */
@Service
public class EngineInitService {
	@Autowired
	private ObjectRelationMapping objectRelationMapping;
	
	@Autowired
	private DbTable dbTable;
	
	@Autowired
	private DbColumn dbColumn;
	
	@Autowired
	private BusinessSystem businessSystem;
	
	@Autowired
	private BusinessSystemAuth businessSystemAuth;
	
	@Autowired
	private BusinessSystemAutoLoad businessSystemAutoLoad;
	
	@Autowired
	private ThirdpartySystem thirdpartySystem;
	
	@Autowired
	private ExposedService exposedService;
	
	@Autowired
	private ServiceObject serviceObject;
	
	@Autowired
	private Operation operation;
	
	@Autowired 
	private Parameter parameter;
	
	@Autowired
	private BusinessFlow businessFlow;
	
	@Autowired 
	private BusinessFlowNode businessFlowNode;
	
	@Autowired
	private FlowNode flowNode;
	
	@Autowired
	private DataMapping dataMapping;
	
	@Autowired
	private AttributeMapping attributeMapping;
	
	@Autowired
	private DomainObject domainObject;
	
	@Autowired
	private Attribute attribute;
	
	@Autowired
	private ValueObject valueObject;
	
	@Autowired
	private DbSchema dbSchema;
	
	@Autowired
	private ValidateRuleGroup validateRuleGroup;
	
	@Autowired
	private ValidateRule validateRule;
	
	@Autowired 
	private ValidateRuleType validateRuleType;
	
	@Autowired
	private SqlManagement sqlManagement;
	
	@Autowired
	private ServerResource serverResource;
	
	@Autowired
	private MessageConsumer messageConsumerSV;

	@Autowired
	private WorkflowProcessResource workflowProcessResourceSV;

	@Autowired
	private DataTransferMethod dataTransferMethodSV;

	@Autowired
	private DynamicMapping dynamicMappingSV;

	@Autowired
	private FileObjectMapping fileObjectMappingSV;

	@Autowired
	private DictResource dictResourceSV;

	@Autowired
	private SecurityKeyResource securityKeyResourceSV;

	@Autowired
	private CronJob cronJobSV;

	@Autowired
	private Dict dictSV;
	
	@Value("${init.param.tempFilePath:./tempFile/}")
	private String tempFilePath;

	@Value("${init.param.fileRootPath:./fileRoot/}")
	private String fileRootPath;
	
	public String getTempFilePath() {
		return tempFilePath;
	}

	public String getFileRootPath() {
		return fileRootPath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

	public List<SqlManagement> listSqlManagements() {
		return sqlManagement.list();
	}
	
	public List<SqlManagement> listSqlManagementsBySystemId(String systemId) {
		return sqlManagement.listBySystemId(systemId);
	}
	
	public List<ValidateRuleType> listValidateRuleTypes() {
		return validateRuleType.list();
	}
	
	public List<ValidateRule> listValidateRuleByGroupId(String ruleGroupId) {
		return validateRule.listByGroupId(ruleGroupId);
	}
	
	public List<ValidateRule> listValidateRuleBySystem(String systemId) {
		return validateRule.listBySystem(systemId);
	}
	
	public List<ValidateRule> listValidateRule() {
		return validateRule.list();
	}
	
	public List<ValidateRuleGroup> listValidateRuleGroups() {
		return validateRuleGroup.list();
	}
	
	public List<ValidateRuleGroup> listValidateRuleGroupBySystemId(String systemId) {
		return validateRuleGroup.listBySystemId(systemId);
	}
	
	public List<DbSchema> listDbSchemas() {
		return dbSchema.list();
	}
	
	public List<DbSchema> listDbSchemaBySystemId(String systemId) {
		return dbSchema.listBySystemId(systemId);
	}
	
	public List<ValueObject> listValueObjects() {
		return valueObject.list();
	}
	
	public List<ValueObject> listValueObjectBySystemId(String systemId) {
		return valueObject.listValueObjectBySystemId(systemId);
	}
	
	public List<Attribute> listAttributeByObjectId(String objectType, String objectId) {
		return attribute.listByObjectId(objectType, objectId);
	}
	
	public List<Attribute> listAttributeByObjectType(String objectType) {
		return attribute.listByObjectType(objectType);
	}
	
	public List<Attribute> listAttributeByObjectTypeAndSystem(String objectType, String systemId) {
		return attribute.listByObjectTypeAndSystem(objectType, systemId);
	}
	
	public List<DomainObject> listdDomainObjects() {
		return domainObject.list();
	}
	
	public List<DomainObject> listDomainObjectBySystemId(String systemId) {
		return domainObject.listDomainObjectBySystemId(systemId);
	}
	
	public List<AttributeMapping> listAttributeMappingByDataMappingId(String dataMappingId) {
		return attributeMapping.listByDataMappingId(dataMappingId);
	}
	
	public List<AttributeMapping> listAttributeMappings() {
		return attributeMapping.list();
	}
	
	public List<AttributeMapping> listAttributeMappingBySystem(String systemId) {
		return attributeMapping.listBySystem(systemId);
	}
	
	public List<DataMapping> listDataMappings() {
		return dataMapping.list();
	}
	
	public List<DataMapping> listDataMappingBySystemId(String systemId) {
		return dataMapping.listBySystemId(systemId);
	}
	
	public List<FlowNode> listFlowNodes() {
		return flowNode.list();
	}
	
	public List<BusinessFlowNode> listBusinessFlowNodeByBusinessFlowId(String businessFlowId) {
		return businessFlowNode.listByBusinessFlowId(businessFlowId);
	}
	
	public List<BusinessFlowNode> listBusinessFlowNodeBySystem(String systemId) {
		return businessFlowNode.listBySystem(systemId);
	}
	
	public List<BusinessFlowNode> listBusinessFlowNodes() {
		return businessFlowNode.list();
	}
	
	public List<BusinessFlow> listBusinessFlows() {
		return businessFlow.list();
	}
	
	public List<BusinessFlow> listBusinessFlowBySystemId(String systemId) {
		return businessFlow.listBySystemId(systemId);
	}
	
	public List<Parameter> listParameterByOperationId(String operationId) {
		return parameter.listByOperationId(operationId);
	}
	
	public List<Parameter> listParameters() {
		return parameter.list();
	}
	
	public List<Parameter> listParametersBySystem(String systemId) {
		return parameter.listBySystem(systemId);
	}
	
	public List<Operation> listOperationByObjectType(String objectType) {
		return operation.listByObjectType(objectType);
	}
	
	public List<Operation> listOperationByObjectTypeAndSystem(String objectType, String systemId) {
		return operation.listByObjectTypeAndSystem(objectType, systemId);
	}
	
	public List<Operation> listOperationByObjectId(String objectType, String objectId) {
		return operation.listByObjectId(objectType, objectId);
	}
	
	public List<ServiceObject> listsServiceObjects() {
		return serviceObject.list();
	}
	
	public List<ServiceObject> listsServiceObjectBySystemId(String systemId) {
		return serviceObject.listBySystemId(systemId);
	}
	
	public List<ExposedService> listExposedServices() {
		return exposedService.list();
	}
	
	public List<ExposedService> listExposedServiceBySystemId(String businessSystemId) {
		return exposedService.listBySystemId(businessSystemId);
	}
	
	public BusinessSystem getBusinessSystemById(String businessSystemId) {
		return businessSystem.byId(businessSystemId);
	}
	
	public List<BusinessSystem> listBusinessSystems() {
		return businessSystem.list();
	}
	
	public BusinessSystemAuth getBusinessSystemAuthBySystem(String businessSystemId) {
		return businessSystemAuth.queryBySystem(businessSystemId);
	}
	
	public BusinessSystemAutoLoad getBusinessSystemAutoLoadBySystem(String businessSystemId) {
		return businessSystemAutoLoad.queryBySystem(businessSystemId);
	}
	
	public List<BusinessSystemAuth> listBusinessSystemAuths() {
		return businessSystemAuth.list();
	}
	
	public List<BusinessSystemAutoLoad> listBusinessSystemAutoLoads() {
		return businessSystemAutoLoad.list();
	}
	
	public List<ThirdpartySystem> getThirdpartySystemBySystem(String businessSystemId) {
		return thirdpartySystem.listBySystem(businessSystemId);
	}
	
	public List<ThirdpartySystem> listThirdpartySystems() {
		return thirdpartySystem.list();
	}
	
	public List<ObjectRelationMapping> listObjectRelationMappings() {
		return objectRelationMapping.list();
	}
	
	public List<ObjectRelationMapping> listObjectRelationMappingBySystem(String systemId) {
		return objectRelationMapping.listBySystem(systemId);
	}
	
	public List<DbTable> listdbDbTables() {
		return dbTable.list();
	}
	
	public List<DbTable> listdbDbTableBySystem(String systemId) {
		return dbTable.listBySystem(systemId);
	}
	
	public List<DbColumn> listDbColumns() {
		return  dbColumn.list();
	}
	
	public List<DbColumn> listDbColumnBySystem(String systemId) {
		return  dbColumn.listBySystem(systemId);
	}
	
	public List<ServerResource> listServerResources(String systemId, String resourceType) {
		return serverResource.list(systemId, resourceType);
	}
	
	public List<ServerResource> listServerResources(String resourceType) {
		return serverResource.list(resourceType);
	}
	
	public List<MessageConsumer> listMessageConsumers(String systemId) {
		return messageConsumerSV.list(systemId);
	}
	
	public List<MessageConsumer> listMessageConsumers() {
		return messageConsumerSV.list();
	}

	public List<WorkflowProcessResource> listWorkflowProcessResources() {
		return workflowProcessResourceSV.list();
	}
	
	public List<WorkflowProcessResource> listWorkflowProcessResourceBySystemId(String systemId) {
		return workflowProcessResourceSV.list(systemId);
	}

	public List<DataTransferMethod> listDataTransferMethods() {
		return dataTransferMethodSV.list();
	}

	public List<DynamicMapping> listDynamicMappings() {
		return dynamicMappingSV.list();
	}

	public List<DynamicMapping> listDynamicMappings(String systemId) {
		return dynamicMappingSV.list(systemId);
	}

	public List<FileObjectMapping> listFileObjectMappings() {
		return fileObjectMappingSV.list();
	}

	public List<FileObjectMapping> listFileObjectMappings(String systemId) {
		return fileObjectMappingSV.list(systemId);
	}

	public List<DictResource> listDictResources() {
		return dictResourceSV.list();
	}

	public List<DictResource> listDictResources(String systemId) {
		return dictResourceSV.listBySystem(systemId);
	}

	public List<SecurityKeyResource> listSecurityKeyResources() {
		return securityKeyResourceSV.listWithBloB();
	}

	public List<SecurityKeyResource> listSecurityKeyResources(String systemId) {
		return securityKeyResourceSV.listWithBloB(systemId);
	}

	public List<CronJob> listCronJobs(String jobType) {
		return cronJobSV.list(jobType);
	}

	public List<CronJob> listCronJobs(String systemId, String jobType) {
		return cronJobSV.listBySystem(systemId, jobType);
	}

	public List<Dict> listDicts() {
		return dictSV.list();
	}
}
