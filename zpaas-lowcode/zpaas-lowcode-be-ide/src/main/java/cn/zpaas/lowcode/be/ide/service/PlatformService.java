package cn.zpaas.lowcode.be.ide.service;

import java.util.List;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.ide.vo.BusinessSystemGrantVo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemInfo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemTestVo;
import cn.zpaas.lowcode.domain.eo.Attribute;
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
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;
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

/**
 * @author zjy
 * 服务端领域平台服务接口类
 */
public interface PlatformService {
	
	/**
	 * 查询状态为有效的业务系统列表
	 * @return 业务系统列表
	 */
	public List<BusinessSystem> listBusinessSystem(String tenantId);

	/**
	 * 查询状态为有效的业务系统列表
	 * @return 业务系统列表
	 */
	public List<BusinessSystem> listBusinessSystemByUser(String tenantId, String userId);
	
	/**
	 * 加载业务系统信息
	 * @param businessSystemId
	 * @return
	 */
	public BusinessSystemInfo loadBusinessSystemInfo(String businessSystemId);

	/**
	 * 加载业务系统测试相关信息
	 * @param systemId
	 * @return
	 */
	public BusinessSystemTestVo loadBusinessSystemTestInfo(String systemId);
	
	/**
	 * 保存业务系统
	 * @param businessSystem
	 * @return
	 */
	public int saveBusinessSystem(BusinessSystem businessSystem) throws EngineException ;

	/**
	 * 业务系统授权
	 * @param businessSystemGrantRo
	 * @return
	 */
	public int grantBusinessSystem(BusinessSystemGrantVo businessSystemGrantVo) throws EngineException;

	/**
	 * 加载用的业务系统授权信息
	 * @param userId
	 * @return
	 */
	public BusinessSystemGrantVo loadBusinessSystemGrantInfo(String userId);
	
	/**
	 * 保存业务系统认证信息
	 * @param businessSystemAuth
	 * @return
	 */
	public BusinessSystemAuth saveBusinessSystemAuth(BusinessSystemAuth businessSystemAuth);
	
	/**
	 * 保存业务系统自动加载信息
	 * @param businessSystemAutoLoad
	 * @return
	 */
	public BusinessSystemAutoLoad saveBusinessSystemAutoLoad(BusinessSystemAutoLoad businessSystemAutoLoad);
	
	/**
	 * 新建业务系统
	 * @param businessSystem
	 * @param userId
	 * @return
	 */
	public BusinessSystem createBusinessSystem(BusinessSystem businessSystem, String userId) throws EngineException ;
	
	/**
	 * 作废业务系统
	 * @param id
	 * @return
	 */
	public int deleteBusinessSystem(String id, String tenantId) throws EngineException;
	
	/**
	 * 查询对象编码
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public ManagedObject queryManagedObject(String objectType, String objectId);
	
	/**
	 * 加载管理对象信息
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public ManagedObjectInfo loadManagedObjectInfo(String objectType, String objectId);
	
	/**
	 * 删除管理对象
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public int deleteManagedObject(String objectType, String objectId, String tenantId, JsonObject grantedSystems) throws EngineException;
	/**
	 * 根据主键加载领域对象信息
	 * @param id
	 * @return
	 */
	public DomainObject loadDomainObject(String id);
	
	/**
	 * 领域对象方法一键暴露
	 * @param domainOperationExpose
	 */
	public void exposeDomainOperation(DomainOperationExpose domainOperationExpose) throws EngineException;
	
	/**
	 * 根据主键加载值传递对象信息
	 * @param id
	 * @return
	 */
	public ValueObject loadValueObject(String id);
	
	/**
	 * 加载方法信息
	 * @param operationId
	 * @return
	 */
	public OperationInfo loadOperationInfo(String operationId);

	/**
	 * 加载方法名称
	 * @param operationId
	 * @return
	 */
	public String queryOperationName(String operationId);

	/**
	 * 加载方法信息
	 * @param operationId
	 * @return
	 */
	public Operation queryOperation(String operationId);
	
	/**
	 * 加载方法参数信息
	 * @param operationId
	 * @return
	 */
	public List<Parameter> loadParameters(String operationId);
	
	/**
	 * 加载业务流信息
	 * @param businessFlowId
	 * @return
	 */
	public BusinessFlowInfo loadBusinessFlowInfo(String businessFlowId);
	
	/**
	 * 保存业务流信息
	 * @param businessFlow
	 * @return
	 */
	public BusinessFlow saveBusinessFlow(BusinessFlow businessFlow);
	
	/**
	 * 保存业务流全量信息，包括业务流节点、子业务流以及bpmn xml串
	 * @param businessFlowInfo
	 * @return
	 */
	public BusinessFlowInfo saveBusinessFlowAllInfo(BusinessFlowInfo businessFlowInfo)  throws EngineException;
	
	/**
	 * 新建业务流节点
	 * @param businessFlowNode
	 * @return
	 */
	public BusinessFlowNode addBusinessFlowNode(BusinessFlowNode businessFlowNode);
	
	/**
	 * 保存业务流节点信息
	 * @param businessFlowNode
	 * @return
	 */
	public int saveBusinessFlowNode(BusinessFlowNode businessFlowNode);

	/**
	 * 查询业务流节点定义列表
	 * @param systemId
	 * @return
	 */
	public List<FlowNode> listFlowNode(String systemId);
	
	/**
	 * 新建管理对象
	 * @param managedObject
	 * @return
	 */
	public ManagedObjectInfo addManagedObject(NewManagedObject managedObject);
	
	/**
	 * 更新管理对象
	 * @param managedObject
	 * @return
	 */
	public int saveManagedObject(UpdateManagedObject managedObject);
	
	/**
	 * 查询属性列表
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<Attribute> listAttributes(String objectType, String objectId);
	
	/**
	 * 新建属性
	 * @param attribute
	 * @return
	 */
	public Attribute addAttribute(Attribute attribute);
	
	/**
	 * 保存属性信息
	 * @param attribute
	 * @return
	 */
	public int saveAttribute(Attribute attribute);
	
	/**
	 * 删除属性信息
	 * @param attribute
	 * @return
	 */
	public int deleteAttribute(String attrId);

	/**
	 * 查询属性详情
	 * @param attrId
	 * @return
	 */
	public Attribute queryAttribute(String attrId);
	
	/**
	 * 新建方法
	 * @param operation
	 * @return
	 */
	public Operation addOperation(Operation operation);
	
	/**
	 * 新建方法和对应的BusinessFlow
	 * @param operationFlowInfo
	 * @return
	 */
	public Operation addOperationAndFlow(OperationFlowInfo operationFlowInfo);
	
	/**
	 * 修改方法
	 * @param operation
	 * @return
	 */
	public int saveOperation(Operation operation);
	
	/**
	 * 删除方法
	 * @param operationId
	 * @return
	 */
	public int deleteOperation(String operationId);
	
	
	
	/**
	 * 新建参数
	 * @param parameter
	 * @return
	 */
	public Parameter addParameter(Parameter parameter);
	
	/**
	 * 保存参数信息
	 * @param parameter
	 * @return
	 */
	public Parameter saveParameter(Parameter parameter) throws EngineException;
	
	/**
	 * 删除参数信息
	 * @param paramId
	 * @return
	 */
	public int deleteParameter(String paramId);

	/**
	 * 加载参数信息
	 * @param paramId
	 * @return
	 */
	public Parameter queryParameter(String paramId);
	
	/**
	 * 暴露服务方法
	 * @param exposedService
	 * @return
	 */
	public ExposedService exposeOperation(ExposedService exposedService);
	
	/**
	 * 取消方法发布
	 * @param exposedService
	 * @return
	 */
	public int cancelExposeOperation(ExposedService exposedService);
	
	/**
	 * 新建业务域
	 * @param businessDomain
	 * @return
	 */
	public BusinessDomain addBusinessDomain(BusinessDomain businessDomain);

	/**
	 * 查询业务域
	 * @param businessDomainId
	 * @return
	 */
	public BusinessDomain queryBusinessDomain(String businessDomainId);

	/**
	 * 查询业务域列表
	 * @param systemId
	 * @return
	 */
	public List<BusinessDomain> listBusinessDomain(String systemId);
	
	/**
	 * 修改业务域
	 * @param businessDomain
	 * @return
	 */
	public int saveBusinessDomain(BusinessDomain businessDomain);

	/**
	 * 删除业务域
	 * @param businessDomain
	 * @return
	 */
	public int deleteBusinessDomain(BusinessDomain businessDomain);
	
	/**
	 * 查询某个数据库中表和视图的列表
	 * @param systemId 业务系统标识
	 * @param dbSchemaId 数据库标识
	 * @return
	 */
	public List<MdaTable> queryTableList(String systemId, String dbSchemaId) throws EngineException;
	
	/**
	 * 查询某个表的字段列表
	 * @param systemId 业务系统标识
	 * @param dbSchemaId 数据库标识
	 * @param tableName 表名
	 * @return
	 * @throws EngineException
	 */
	public List<MdaColumn> queryColumnList(String systemId, String dbSchemaId, String tableName)  throws EngineException;
	
	/**
	 * 查询指定表的表信息，包括字段信息
	 * @param systemId 业务系统标识
	 * @param dbSchemaId 数据库标识
	 * @param talbeList 表名列表
	 * @return
	 * @throws EngineException
	 */
	public List<MdaTable> queryTableColumnList(String systemId, String dbSchemaId, List<String> talbeList)  throws EngineException;
	
	/**
	 * 根据指定的表列表，生成所有的相关信息
	 * @param request 模型驱动能力代码生成请求
	 * @return
	 * @throws EngineException
	 */
	public boolean generateAll(MdaGenerateRequest request)  throws EngineException;
	
	/**
	 * 新增数据库
	 * @param dbSchema
	 * @return
	 * @throws EngineException
	 */
	public DbSchema addDbSchema(DbSchema dbSchema) throws EngineException;
	
	/**
	 * 修改数据库信息
	 * @param dbSchema
	 * @return
	 * @throws EngineException
	 */
	public int saveDbSchema(DbSchema dbSchema) throws EngineException;
	
	/**
	 * 加载数据库信息
	 * @param id
	 * @return
	 */
	public DbSchema loadDbSchema(String id) ;

	/**
	 * 查询数据库信息列表
	 * @param systemId
	 * @return
	 */
	public List<DbSchema> listDbSchema(String systemId);
	
	/**
	 * 删除数据库
	 * @param id
	 * @return
	 */
	public int deleteDbSchema(String id) ;
	
	/**
	 * 新增技术服务资源
	 * @param serverResource
	 * @return
	 */
	public ServerResource addServerResource(ServerResource serverResource);
	
	/**
	 * 更新技术服务资源信息
	 * @param serverResource
	 * @return
	 */
	public int saveServerResource(ServerResource serverResource);
	
	/**
	 * 删除技术服务资源
	 * @param serverResourceId
	 * @return
	 */
	public int deleteServerResource(String serverResourceId);
	
	/**
	 * 根据主键加载技术服务资源
	 * @param serverResourceId
	 * @return
	 */
	public ServerResource queryServerResource(String serverResourceId);
	
	/**
	 * 新增数据库表
	 * @param dbTable
	 * @return
	 * @throws EngineException
	 */
	public DbTable addDbTable(DbTable dbTable) throws EngineException;
	
	/**
	 * 加载数据库表信息
	 * @param id
	 * @return
	 */
	public DbTable loadDbTable(String id);
	
	/**
	 * 根据系统标识查询数据库表列表
	 * @param systemId
	 * @return
	 */
	public List<DbTable> listDbTables(String systemId);
	
	/**
	 * 修改数据库表信息
	 * @param dbTable
	 * @return
	 * @throws EngineException
	 */
	public int saveDbTable(DbTable dbTable) throws EngineException;
	
	/**
	 * 删除数据库表
	 * @param id
	 * @return
	 */
	public int deleteDbTable(String id);
	/**
	 * 新增数据库表字段
	 * @param dbColumn
	 * @return
	 * @throws EngineException
	 */
	public DbColumn addDbColumn(DbColumn dbColumn) throws EngineException;
	
	/**
	 * 根据主键加载数据库表字段信息
	 * @param dbColumnId
	 * @return
	 */
	public DbColumn loadDbColumn(String dbColumnId);
	
	/**
	 * 保存数据库表字段信息
	 * @param dbColumn
	 * @return
	 * @throws EngineException
	 */
	public DbColumn saveDbColumn(DbColumn dbColumn) throws EngineException;
	
	/**
	 * 删除数据库表字段
	 * @param dbColumnId
	 * @return
	 */
	public int deleteDbColumn(String dbColumnId);
	
	/**
	 * 根据数据库表标识查询ObjectRelationMapping列表
	 * @param dbTableId
	 * @return
	 */
	public List<ObjectRelationMapping> queryObjectRelationMappingsByTableId(String dbTableId);
	
	/**
	 * 根据领域对象标识查询ObjectRelationMapping列表
	 * @param domainObjectId
	 * @return
	 */
	public List<ObjectRelationMapping> queryObjectRelationMappingsByDomainObjectId(String domainObjectId);
	
	/**
	 * 查询ObjectRelationMapping列表
	 * @param domainObjectId
	 * @return
	 */
	public List<ObjectRelationMapping> queryObjectRelationMappings(String systemId);
	
	/**
	 * 查询orm
	 * @param ormId
	 * @return
	 */
	public ObjectRelationMapping queryObjectRelationMapping(String ormId);
	
	/**
	 * 新增对象映射关系，含字段映射
	 * @param objectRelationMapping
	 * @return
	 * @throws EngineException
	 */
	public ObjectRelationMapping addObjectRelationMappingWithColumnMapping(ObjectRelationMapping objectRelationMapping) throws EngineException;
	
	/**
	 * 修改对象映射关系，含字段映射
	 * @param objectRelationMapping
	 * @return
	 * @throws EngineException
	 */
	public ObjectRelationMapping modifyObjectRelationMappingWithColumnMapping(ObjectRelationMapping objectRelationMapping) throws EngineException;
	
	/**
	 * 根据源对象查询DataMapping列表
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<DataMapping> queryDataMappingsByFromObject(String objectType, String objectId) throws EngineException;
	
	/**
	 * 根据目标对象查询DataMapping列表
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<DataMapping> queryDataMappingsByToObject(String objectType, String objectId) throws EngineException;
	
	/**
	 * 根据对象查询DataMapping列表（双向查询）
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<DataMapping> queryDataMappingsByObject(String objectType, String objectId) throws EngineException;
	
	/**
	 * 查询DataMapping列表
	 * @param systemId
	 * @return
	 */
	public List<DataMapping> queryDataMappings(String systemId) throws EngineException;
	
	/**
	 * 查询DataMapping
	 * @param dataMappingId
	 * @return
	 * @throws EngineException
	 */
	public DataMapping queryDataMapping(String dataMappingId);
	
	/**
	 * 新增数据映射，含属性映射信息
	 * @param dataMapping
	 * @return
	 * @throws EngineException
	 */
	public DataMapping addDataMappingWidthAttributeMapping(DataMapping dataMapping) throws EngineException;
	
	/**
	 * 修改数据映射，含属性映射信息
	 * @param dataMapping
	 * @return
	 * @throws EngineException
	 */
	public DataMapping modifyDataMappingWithAttributMapping(DataMapping dataMapping) throws EngineException;
	
	/**
	 * 查询sql语句
	 * @param systemId
	 * @return
	 */
	public List<SqlManagement> querySqlManagements(String systemId, String resourceType, String resourceId);
	
	/**
	 * 查询领域对象
	 * @param systemId
	 * @return
	 */
	public List<DomainObject> queryDomainObjects(String systemId);
	
	/**
	 * 查询值传递对象
	 * @param systemId
	 * @return
	 */
	public List<ValueObject> queryValueObjects(String systemId);
	
	/**
	 * 查询服务对象
	 * @param systemId
	 * @return
	 */
	public List<ServiceObject> queryServiceObjects(String systemId);
	
	/**
	 * 查询方法列表
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<Operation> queryOperations(String objectType, String objectId);
	
	/**
	 * 查询方法列表
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<Operation> queryOperationsNoParam(String objectType, String objectId);
	
	/**
	 * 查询校验规则组
	 * @param systemId
	 * @return
	 */
	public List<ValidateRuleGroup> queryValidateRuleGroups(String systemId);
	
	/**
	 * 查询校验规则组
	 * @param systemId
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<ValidateRuleGroup> queryValidateRuleGroups(String systemId, String objectType, String objectId);
	
	/**
	 * 加载校验规则组创建初始化信息
	 * @param initInfo
	 * @return
	 */
	public ValidateRuleGroupInitInfo loadValidateRuleGroupInitInfo(ValidateRuleGroupInitInfo initInfo);
	
	/**
	 * 保存校验规则组信息
	 * @param validateRuleGroup
	 * @return
	 */
	public ValidateRuleGroup saveValidateRuleGroup(ValidateRuleGroup validateRuleGroup);
	
	/**
	 * 加载校验规则组信息
	 * @param ruleGroupId
	 * @return
	 */
	public ValidateRuleGroup loadValidateRuleGroup(String ruleGroupId);
	
	/**
	 * 查询校验规则组
	 * @param ruleGroupId
	 * @return
	 */
	public ValidateRuleGroup queryValidateRuleGroup(String ruleGroupId);
	
	/**
	 * 根据systemId和resourceType加载服务器资源列表
	 * @param systemId
	 * @param resourceType
	 * @return
	 */
	public List<ServerResource> listServerResources(String systemId, String resourceType);

	/**
	 * 根据主键查询服务对象信息
	 * @param id
	 * @return
	 */
	public ServiceObject queryServiceObject(String id);
	
	/**
	 * 加载系统对外暴露的服务方法
	 * @param systemId
	 * @return
	 */
	public List<ExposedService> listExposedServices(String systemId);
	
	/** 加载工作流流程资源信息
	 * @param resourceId
	 * @return
	 */
	public WorkflowProcessResource loadWorkflowProcessResource(String resourceId);
	
	/**
	 * 加载流程资源列表
	 * @param systemId
	 * @return
	 */
	public List<WorkflowProcessResource> listWorkflowProcessResourcesDeployed(String systemId);
	
	/**
	 * 查询登录会话Key
	 * @param systemId
	 * @return
	 */
	public String queryLoginSessionKey(String systemId);
	
	/**
	 * 新建消息消费服务
	 * @param messageConsumer
	 * @return
	 */
	public MessageConsumer saveMessageConsumer(MessageConsumer messageConsumer);
	
	/**
	 * 查询消息消费服务信息
	 * @param systemId
	 * @param id
	 * @return
	 */
	public MessageConsumer queryMessageConsumer(String systemId, String id);
	
	/**
	 * 修改消息消费服务
	 * @param messageConsumer
	 * @return
	 */
	public MessageConsumer modifyMessageConsumer(MessageConsumer messageConsumer);
	
	/**
	 * 作废消息消费服务
	 * @param systemId
	 * @param id
	 * @return
	 */
	public int deleteMessageConsumer(String systemId, String id);

	/**
	 * 查询数据转换方法列表
	 * @return
	 */
	public List<DataTransferMethod> listDataTransferMethod();

	/**
	 * 新增动态映射
	 * @param dynamicMapping
	 * @return
	 */
	public DynamicMapping addDynamicMapping(DynamicMapping dynamicMapping);

	/**
	 * 修改动态映射
	 * @param dynamicMapping
	 * @return
	 */
	public DynamicMapping saveDynamicMapping(DynamicMapping dynamicMapping);

	/**
	 * 作废动态映射
	 * @param dynamicMappingId
	 * @return
	 */
	public int deleteDynamicMapping(String dynamicMappingId);

	/**
	 * 加载动态映射信息
	 * @param dynamicMappingId
	 * @return
	 */
	public DynamicMapping loadDynamicMapping(String dynamicMappingId);

	/**
	 * 根据条件查询动态映射信息
	 * @param dynamicMapping
	 * @return
	 */
	public List<DynamicMapping> queryDynamicMappingByCondition(DynamicMapping dynamicMapping);

	/**
	 * 查询动态映射
	 * @param dynamicMappingId
	 * @return
	 */
	public DynamicMapping queryDynamicMapping(String dynamicMappingId);

	/**
	 * 新增动态映射详细信息
	 * @param dynamicMappingDetail
	 * @return
	 */
	public DynamicMappingDetail addDynamicMappingDetail(DynamicMappingDetail dynamicMappingDetail);

	/**
	 * 修改动态映射详细信息
	 * @param dynamicMappingDetail
	 * @return
	 */
	public DynamicMappingDetail saveDynamicMappingDetail(DynamicMappingDetail dynamicMappingDetail);

	/**
	 * 作废动态映射详细信息
	 * @param dynamicMappingDetailId
	 * @return
	 */
	public int deleteDynamicMappingDetail(String dynamicMappingDetailId);

	/**
	 * 加载动态映射信息详细信息
	 * @param dynamicMappingDetailId
	 * @return
	 */
	public DynamicMappingDetail loadDynamicMappingDetail(String dynamicMappingDetailId);

	/**
	 * 加载文件对象映射信息
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public List<FileObjectMapping> loadFileObjectMappings(String objectType, String objectId);

	/**
	 * 新增文件对象映射
	 * @param fileObjectMapping
	 * @return
	 */
	public FileObjectMapping addWithAttributeColumnMapping(FileObjectMapping fileObjectMapping) throws EngineException;

	/**
	 * 修改文件对象映射
	 * @param fileObjectMapping
	 * @return
	 */
	public FileObjectMapping saveWithAttributeColumnMapping(FileObjectMapping fileObjectMapping) throws EngineException;

	/**
	 * 查询文件对象映射
	 * @param fileObjectMappingId
	 * @return
	 */
	public FileObjectMapping queryFileObjectMapping(String fileObjectMappingId);

	/**
	 * 新增字典资源
	 * @param dictResource
	 * @return
	 */
	public DictResource addDictResource(DictResource dictResource)throws EngineException;

	/**
	 * 修改字典资源
	 * @param dictResource
	 * @return
	 */
	public int saveDictResource(DictResource dictResource)throws EngineException;

	/**
	 * 删除字典资源
	 * @param dictResourceId
	 * @return
	 */
	public int deleteDictResource(String dictResourceId)throws EngineException;

	/**
	 * 加载字典资源
	 * @param dictResourceId
	 * @return
	 */
	public DictResource loadDictResource(String dictResourceId);

	/**
	 * 加载字典资源列表
	 * @param systemId
	 * @return
	 */
	public List<DictResource> listDictResources(String systemId);

	/**
	 * 新增安全密钥资源
	 * @param securityKeyResource
	 * @return
	 */
	public SecurityKeyResource addSecurityKeyResource(SecurityKeyResource securityKeyResource)throws EngineException;

	/**
	 * 加载安全密钥资源信息
	 * @param securityKeyResourceId
	 * @return
	 */
	public SecurityKeyResource loadSecurityKeyResource(String securityKeyResourceId);

	/**
	 * 加载安全密钥资源列表信息
	 * @param systemId
	 * @return
	 */
	public List<SecurityKeyResource> listSecurityKeyResources(String systemId);
	

	/**
	 * 删除安全密钥资源信息
	 * @param securityKeyResourceId
	 * @return
	 */
	public int deleteSecurityKeyResource(String securityKeyResourceId)throws EngineException;

	/**
	 * 保存安全密钥资源
	 * @param securityKeyResource
	 * @return
	 */
	public SecurityKeyResource saveSecurityKeyResource(SecurityKeyResource securityKeyResource)throws EngineException;


	/**
	 * 新增定时任务
	 * @param cronJob
	 * @return
	 */
	public CronJob addCronJob(CronJob cronJob)throws EngineException;

	/**
	 * 修改定时任务
	 * @param cronJob
	 * @return
	 */
	public int saveCronJob(CronJob cronJob)throws EngineException;

	/**
	 * 删除定时任务
	 * @param cronJobId
	 * @return
	 */
	public int deleteCronJob(String cronJobId)throws EngineException;

	/**
	 * 加载定时任务
	 * @param cronJobId
	 * @return
	 */
	public CronJob loadCronJob(String cronJobId);

	/**
	 * 加载定时任务列表
	 * @param systemId
	 * @return
	 */
	public List<CronJob> listCronJobs(String systemId);
}
