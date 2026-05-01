package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowBpmnFile;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.Dict;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.ManagedObject;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.flow.BusinessFlowTemplate;
import cn.zpaas.lowcode.be.engine.flow.node.LocalInvokeNode;
import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.be.engine.flow.node.ORMRepositoryNode;
import cn.zpaas.lowcode.be.engine.proxy.DictProxy;
import cn.zpaas.lowcode.be.ide.ability.AiDrivenAbility;
import cn.zpaas.lowcode.be.ide.vo.AiFlowComposerChatInfo;
import cn.zpaas.lowcode.be.ide.vo.AiGenBusinessFlowInfo;

/**
 * 方法业务流领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:43:22
 */
@Service
public class BusinessFlowGenService {
    private static Logger logger = LoggerFactory.getLogger(BusinessFlowGenService.class);

    @Autowired
    private FlowNode flowNodeSV;//业务流节点类型领域对象

    @Autowired
    private BusinessFlowNode businessFlowNodeSV;//业务流节点领域对象

    @Autowired
    private BusinessFlowBpmnFile businessFlowBpmnFileSV;//业务流流程文件领域对象

	@Autowired
	private AiDrivenAbility aiDrivenAbility;//ai生成能力

	@Autowired
	private BusinessFlow businessFlowSV;//业务流领域对象

	@Autowired
	private Operation operationSV;//方法领域对象

	@Autowired
	private Parameter parameterSV;//参数领域对象

	@Autowired
	private DomainObject domainObjectSV;//领域对象对应的领域对象

	@Autowired
	private ValueObject valueObjectSV;//值传递对象对应的领域对象

	@Autowired
	private Attribute attributeSV;//对象属性对应的领域对象

	@Autowired
	private ServerResource serverResourceSV;//技术服务资源对应的领域对象


    /**
     * 生成新增方法涉及的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateAddOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, String ormId) throws EngineException{
    	
    	BusinessFlowNode ormNode = new BusinessFlowNode();
    	ormNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(ORMRepositoryNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: ORMRepositoryNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: ORMRepositoryNode");
    	} 	
    	ormNode.setFlowNodeId(node.getId());
    	ormNode.setName(node.getName());
    	ormNode.setNodeOrder(1);
    	ormNode.setTenantId(businessFlow.getTenantId());
    	ormNode.setSystemId(businessFlow.getSystemId());
    	ormNode.setBpmnNodeId(BusinessFlowBpmnFile.EXAMPLE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	ormNode.setCreateTime(nowDate);
    	ormNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	ormNode.setNodePreCfg(null);
    	
    	//set node process cfg
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_IS_LIST_TYPE_KEY, false);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_I);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_KEY, OperationService.NEW+domainObject.getCode());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_ID_KEY, domainObject.getId());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_ORM_ID_KEY, ormId);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_REPOSITORY_OPERATION_KEY, ORMRepositoryNode.REPOSITORY_OPERATION_INSERT);
    	nodeCfg.addProperty(Node.IS_LIST_RESULT_KEY, false);
    	nodeCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, ManagedObjectType.DOMAIN_OBJECT);
    	nodeCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, domainObject.getId());   	
    	ormNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	ormNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(ormNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return ormNode;
    }
    
    /**
     * 生成根据主键修改方法涉及的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateModifyByIdOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, String ormId) throws EngineException{
    	
    	BusinessFlowNode ormNode = new BusinessFlowNode();
    	ormNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(ORMRepositoryNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: ORMRepositoryNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: ORMRepositoryNode");
    	} 	
    	ormNode.setFlowNodeId(node.getId());
    	ormNode.setName(node.getName());
    	ormNode.setNodeOrder(1);
    	ormNode.setTenantId(businessFlow.getTenantId());
    	ormNode.setSystemId(businessFlow.getSystemId());
    	ormNode.setBpmnNodeId(BusinessFlowBpmnFile.EXAMPLE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	ormNode.setCreateTime(nowDate);
    	ormNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	ormNode.setNodePreCfg(null);
    	
    	//set node process cfg
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_IS_LIST_TYPE_KEY, false);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_I);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_KEY, OperationService.MODIFY + domainObject.getCode());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_ID_KEY, domainObject.getId());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_ORM_ID_KEY, ormId);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_REPOSITORY_OPERATION_KEY, ORMRepositoryNode.REPOSITORY_OPERATION_UPDATE_BY_PK_SELECTIVE);
    	nodeCfg.addProperty(Node.IS_LIST_RESULT_KEY, false);
    	nodeCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, ManagedObjectType.JAVA_OBJECT);
    	nodeCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, Integer.class.getName());   	
    	ormNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	ormNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(ormNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return ormNode;
    }
    
    /**
     * 生成根据主键删除方法涉及的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateDeleteByIdOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, String ormId) throws EngineException{
    	
    	BusinessFlowNode ormNode = new BusinessFlowNode();
    	ormNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(ORMRepositoryNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: ORMRepositoryNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: ORMRepositoryNode");
    	} 	
    	ormNode.setFlowNodeId(node.getId());
    	ormNode.setName(node.getName());
    	ormNode.setNodeOrder(1);
    	ormNode.setTenantId(businessFlow.getTenantId());
    	ormNode.setSystemId(businessFlow.getSystemId());
    	ormNode.setBpmnNodeId(BusinessFlowBpmnFile.EXAMPLE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	ormNode.setCreateTime(nowDate);
    	ormNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	ormNode.setNodePreCfg(null);
    	
    	//set node process cfg
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_IS_LIST_TYPE_KEY, false);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_I);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_KEY, OperationService.DELETE + domainObject.getCode());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_ID_KEY, domainObject.getId());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_ORM_ID_KEY, ormId);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_REPOSITORY_OPERATION_KEY, ORMRepositoryNode.REPOSITORY_OPERATION_DELETE_BY_PK);
    	nodeCfg.addProperty(Node.IS_LIST_RESULT_KEY, false);
    	nodeCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, ManagedObjectType.JAVA_OBJECT);
    	nodeCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, Integer.class.getName());   	
    	ormNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	ormNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(ormNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return ormNode;
    }
    
    /**
     * 生成根据主键查询方法涉及的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateQueryByIdOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, String ormId) throws EngineException{
    	
    	BusinessFlowNode ormNode = new BusinessFlowNode();
    	ormNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(ORMRepositoryNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: ORMRepositoryNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: ORMRepositoryNode");
    	} 	
    	ormNode.setFlowNodeId(node.getId());
    	ormNode.setName(node.getName());
    	ormNode.setNodeOrder(1);
    	ormNode.setTenantId(businessFlow.getTenantId());
    	ormNode.setSystemId(businessFlow.getSystemId());
    	ormNode.setBpmnNodeId(BusinessFlowBpmnFile.EXAMPLE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	ormNode.setCreateTime(nowDate);
    	ormNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	ormNode.setNodePreCfg(null);
    	
    	//set node process cfg
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_IS_LIST_TYPE_KEY, false);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_I);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_KEY, OperationService.CONDITION+domainObject.getCode());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_ID_KEY, domainObject.getId());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_ORM_ID_KEY, ormId);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_REPOSITORY_OPERATION_KEY, ORMRepositoryNode.REPOSITORY_OPERATION_QUERY_BY_PK);
    	nodeCfg.addProperty(Node.IS_LIST_RESULT_KEY, false);
    	nodeCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, ManagedObjectType.DOMAIN_OBJECT);
    	nodeCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, domainObject.getId());   	
    	ormNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	ormNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(ormNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return ormNode;
    }
    
    /**
     * 生成根据条件查询方法涉及的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateQueryByConditionOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, String ormId) throws EngineException{
    	
    	BusinessFlowNode ormNode = new BusinessFlowNode();
    	ormNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(ORMRepositoryNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: ORMRepositoryNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: ORMRepositoryNode");
    	} 	
    	ormNode.setFlowNodeId(node.getId());
    	ormNode.setName(node.getName());
    	ormNode.setNodeOrder(1);
    	ormNode.setTenantId(businessFlow.getTenantId());
    	ormNode.setSystemId(businessFlow.getSystemId());
    	ormNode.setBpmnNodeId(BusinessFlowBpmnFile.EXAMPLE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	ormNode.setCreateTime(nowDate);
    	ormNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	ormNode.setNodePreCfg(null);
    	
    	//set node process cfg
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_IS_LIST_TYPE_KEY, false);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_I);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_KEY, OperationService.CONDITION+domainObject.getCode());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_DOMAIN_OBJECT_ID_KEY, domainObject.getId());
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_ORM_ID_KEY, ormId);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_REPOSITORY_OPERATION_KEY, ORMRepositoryNode.REPOSITORY_OPERATION_QUERY_BY_CONDITION);
    	nodeCfg.addProperty(ORMRepositoryNode.CFG_PAGE_FLAG_KEY, true);
    	nodeCfg.addProperty(Node.IS_LIST_RESULT_KEY, true);
    	nodeCfg.addProperty(Node.NODE_RESULT_TYPE_KEY, ManagedObjectType.DOMAIN_OBJECT);
    	nodeCfg.addProperty(Node.NODE_RESULT_CLASS_KEY, domainObject.getId());   	
    	ormNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	ormNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(ormNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return ormNode;
    }
    
    /**
     * 创建本地方法调用的业务流节点
     * @param businessFlow
     * @param domainObject
     * @param ormId
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateLocalInvokeOperationBusinessFlowNodes(BusinessFlow businessFlow, ManagedObject domainObject, DomainObject targetObject, Operation targetOperation, String paramName) throws EngineException{
    	
    	BusinessFlowNode localInvokeNode = new BusinessFlowNode();
    	localInvokeNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(LocalInvokeNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: LocalInvokeNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: LocalInvokeNode");
    	} 	
    	localInvokeNode.setFlowNodeId(node.getId());
    	localInvokeNode.setName(node.getName());
    	localInvokeNode.setNodeOrder(1);
    	localInvokeNode.setTenantId(businessFlow.getTenantId());
    	localInvokeNode.setSystemId(businessFlow.getSystemId());
    	localInvokeNode.setBpmnNodeId(BusinessFlowBpmnFile.LOCAL_INVOKE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	localInvokeNode.setCreateTime(nowDate);
    	localInvokeNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	/*
    	 {
			"paramsRule": [
				{
					"fromObjectType": "I",
					"fromObjectKey": "condition",
					"attrMappings": "[{\"fromAttrPath\":\"/\",\"toObjectAttr\":\"conditionCommodity\"}]"
				}
			],
			"toObjectType": "JSON"
		} 
    	 */
    	JsonObject nodePreCfg = new JsonObject();
    	nodePreCfg.addProperty(Node.TO_OBJECT_TYPE_KEY, Node.TO_OBJECT_TYPE_JSON);
    	
    	JsonArray paramRules = new JsonArray();
    	JsonObject paramRule = new JsonObject();
    	paramRule.addProperty(Node.FROM_OBJECT_TYPE_KEY, Node.FROM_OBJECT_TYPE_INPUT);
    	paramRule.addProperty(Node.FROM_OBJECT_KEY_KEY, paramName);
    	
    	JsonArray attrMappings = new JsonArray();
    	JsonObject attrMapping = new JsonObject();
    	attrMapping.addProperty(Node.FROM_ATTR_PATH_KEY, Node.FROM_ATTR_PATH_ROOT);
    	attrMapping.addProperty(Node.TO_OBJECT_ATTR_KEY, paramName);
    	attrMappings.add(attrMapping);
    	paramRule.addProperty(Node.ATTR_MAPPINGS_KEY, JsonUtils.toJson(attrMappings));
    	
    	paramRules.add(paramRule);
    	nodePreCfg.add(Node.PARAMS_RULE_KEY, paramRules);
    	
    	localInvokeNode.setNodePreCfg(JsonUtils.toJson(nodePreCfg));
    	
    	//set node process cfg
    	/*
    	{
			"objectType": "D",
			"objectId": "455ef565233644f599b0654efbe21522",
			"instanceKey": "",
			"operationId": "e8040e4f4ede43ac8086012e98fe9f5d",
			"objectSubType": "E",
			"inParamInstanceSource": "N"
		} 
    	 */
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OBJECT_TYPE_KEY, ManagedObjectType.DOMAIN_OBJECT);
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OBJECT_ID_KEY, targetObject.getId());
    	nodeCfg.addProperty(LocalInvokeNode.CFG_INSTANCE_KEY_KEY, "");
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OPERATION_ID_KEY, targetOperation.getId());
    	nodeCfg.addProperty(LocalInvokeNode.CFG_IN_PARAM_INSTANCE_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_N);
    	localInvokeNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	localInvokeNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(localInvokeNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return localInvokeNode;
    }
    
    /**
     * 根据领域对象方法创建服务方法
     * @param businessFlow
     * @param targetObject
     * @param targetOperationId
     * @param parameters
     * @return
     * @throws EngineException
     */
    public BusinessFlowNode generateLocalInvokeOperationBusinessFlowNodes(BusinessFlow businessFlow, DomainObject targetObject, String targetOperationId, List<Parameter> parameters) throws EngineException{
    	
    	BusinessFlowNode localInvokeNode = new BusinessFlowNode();
    	localInvokeNode.setBusinessFlowId(businessFlow.getId());
    	
    	FlowNode node = flowNodeSV.queryByNodeClass(LocalInvokeNode.class.getName());
    	if(node == null) {
    		logger.error("can't find FlowNode: LocalInvokeNode");
    		throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find FlowNode: LocalInvokeNode");
    	} 	
    	localInvokeNode.setFlowNodeId(node.getId());
    	localInvokeNode.setName(node.getName());
    	localInvokeNode.setNodeOrder(1);
    	localInvokeNode.setTenantId(businessFlow.getTenantId());
    	localInvokeNode.setSystemId(businessFlow.getSystemId());
    	localInvokeNode.setBpmnNodeId(BusinessFlowBpmnFile.LOCAL_INVOKE_BPMN_NODE_ID);
    	Date nowDate = new Date();
    	localInvokeNode.setCreateTime(nowDate);
    	localInvokeNode.setUpdateTime(nowDate);
    	
    	//set node preProcess cfg
    	/*
    	 {
			"paramsRule": [
				{
					"fromObjectType": "I",
					"fromObjectKey": "condition",
					"attrMappings": "[{\"fromAttrPath\":\"/\",\"toObjectAttr\":\"conditionCommodity\"}]"
				}
			],
			"toObjectType": "JSON"
		} 
    	 */
    	if(parameters != null && !parameters.isEmpty()) {
    		JsonObject nodePreCfg = new JsonObject();
        	nodePreCfg.addProperty(Node.TO_OBJECT_TYPE_KEY, Node.TO_OBJECT_TYPE_JSON);
        	JsonArray paramRules = new JsonArray();
        	for(Parameter parameter : parameters) {
        		if(YesOrNo.NO.equals(parameter.getIsInParam())) {
        			continue;
        		}
            	JsonObject paramRule = new JsonObject();
            	paramRule.addProperty(Node.FROM_OBJECT_TYPE_KEY, Node.FROM_OBJECT_TYPE_INPUT);
            	paramRule.addProperty(Node.FROM_OBJECT_KEY_KEY, parameter.getCode());
            	
            	JsonArray attrMappings = new JsonArray();
            	JsonObject attrMapping = new JsonObject();
            	attrMapping.addProperty(Node.FROM_ATTR_PATH_KEY, Node.FROM_ATTR_PATH_ROOT);
            	attrMapping.addProperty(Node.TO_OBJECT_ATTR_KEY, parameter.getCode());
            	attrMappings.add(attrMapping);
            	paramRule.addProperty(Node.ATTR_MAPPINGS_KEY, JsonUtils.toJson(attrMappings));
            	
            	paramRules.add(paramRule);
            }
        	nodePreCfg.add(Node.PARAMS_RULE_KEY, paramRules);
        	localInvokeNode.setNodePreCfg(JsonUtils.toJson(nodePreCfg));
    	}
    	
    	//set node process cfg
    	/*
    	{
			"objectType": "D",
			"objectId": "455ef565233644f599b0654efbe21522",
			"instanceKey": "",
			"operationId": "e8040e4f4ede43ac8086012e98fe9f5d",
			"objectSubType": "E",
			"inParamInstanceSource": "N"
		} 
    	 */
    	JsonObject nodeCfg = new JsonObject();
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OBJECT_TYPE_KEY, ManagedObjectType.DOMAIN_OBJECT);
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OBJECT_ID_KEY, targetObject.getId());
    	nodeCfg.addProperty(LocalInvokeNode.CFG_INSTANCE_KEY_KEY, "");
    	nodeCfg.addProperty(LocalInvokeNode.CFG_OPERATION_ID_KEY, targetOperationId);
    	nodeCfg.addProperty(LocalInvokeNode.CFG_IN_PARAM_INSTANCE_SOURCE_KEY, Node.OBJECT_INSTANCE_SOURCE_N);
    	localInvokeNode.setNodeCfg(JsonUtils.toJson(nodeCfg));
    	
    	//set node postProcess cfg
    	JsonObject nodePostCfg = new JsonObject();
    	nodePostCfg.addProperty(Node.AS_IN_PROCESS_DATA_KEY, false);
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_TYPE_KEY, "");
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_CLASS_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_IN_PROCESS_DATA_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY, new JsonObject());
    	nodePostCfg.addProperty(Node.IN_PROCESS_DATA_KEY, "");
    	
    	nodePostCfg.addProperty(Node.AS_DOMAIN_OBJECT_VALUE_KEY, false);
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_VALUE_KEY, "");
    	nodePostCfg.addProperty(Node.DOMAIN_OBJECT_ID_KEY, "");
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY, new JsonObject());
    	
    	nodePostCfg.addProperty(Node.AS_BUSINESS_PROCESS_RESULT_KEY, true);
    	nodePostCfg.addProperty(Node.DATA_MAPPING_ID_RESULT_KEY, "");
    	// nodePostCfg.add(Node.SUB_DATA_MAPPINGS_RESULT, new JsonObject());
    	
    	localInvokeNode.setNodePostCfg(JsonUtils.toJson(nodePostCfg));
    	
    	if(businessFlowNodeSV.save(localInvokeNode) <= 0) {
    		logger.error("generate BusinessFlowNode failed!");
    		return null;
    	}
    	
    	return localInvokeNode;
    }
    
    public int generateBusinessFlowBpmnFile(BusinessFlow businessFlow) {
    	BusinessFlowBpmnFile bpmnFile = new BusinessFlowBpmnFile();		
		bpmnFile.setBpmnXml(BusinessFlowBpmnFile.EXAMPLE_BPMN_XML);
		Date nowDate = new Date();
		bpmnFile.setBusinessFlowId(businessFlow.getId());
		bpmnFile.setCreateTime(nowDate);
		bpmnFile.setUpdateTime(nowDate);
		bpmnFile.setSystemId(businessFlow.getSystemId());
		bpmnFile.setTenantId(businessFlow.getTenantId());
		return businessFlowBpmnFileSV.save(bpmnFile);
    }
    
    public int generateLocalInvokeBusinessFlowBpmnFile(BusinessFlow businessFlow) {
    	BusinessFlowBpmnFile bpmnFile = new BusinessFlowBpmnFile();		
		bpmnFile.setBpmnXml(BusinessFlowBpmnFile.LOCAL_INVOKE_BPMN_XML);
		Date nowDate = new Date();
		bpmnFile.setBusinessFlowId(businessFlow.getId());
		bpmnFile.setCreateTime(nowDate);
		bpmnFile.setUpdateTime(nowDate);
		bpmnFile.setSystemId(businessFlow.getSystemId());
		bpmnFile.setTenantId(businessFlow.getTenantId());
		return businessFlowBpmnFileSV.save(bpmnFile);
    }

	private static final String FLOW_NODE_LIST = "${Flow_Node_List}";
	private static final String OPERATION_INFO = "${Operation_Info}";
	private static final String BUSINESS_REQUIREMENT = "${Business_Requirement}";
	private static final String IN_PARAM_INFO = "${In_Param_Info}";
	private static final String OUT_PARAM_INFO = "${Out_Param_Info}";
	private static final String REL_OBJECT_INFO = "${Rel_Object_Info}";
	private static final String AVAILABLE_SERVER_RESOURCE_INFO = "${Available_Server_Resource_Info}";
	private static final String NODE_CFG_LIST = "${Node_Cfg_List}";
	private static final String NODE_LIST = "${Node_List}";


	public String chatWithAiBusinessFlow(AiFlowComposerChatInfo chatInfo, SseEmitter sseEmitter) throws EngineException{
		BusinessFlow businessFlow = businessFlowSV.byId(chatInfo.getBusinessFlowId());
		if(businessFlow == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid businessFlowId!");
		}
		String schemaId = businessFlow.getDbSchemaId();//数据库id
		List<Operation> operations = operationSV.listByBusinessFlowId(chatInfo.getBusinessFlowId(), chatInfo.getSystemId());
		if(CollectionUtils.isEmpty(operations)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "can't find valid operations!");
		}
		Operation operation = operations.get(0);
		String operationCode = operation.getCode();//方法编码
		String operationName = operation.getName();//方法名称

		//获取AI编排的提示词模板
		Dict nodeComposerTplDict = DictProxy.getDict("AI_FLOW_COMPOSER_TIPS", "NODE_COMPOSER_TPL");
		Dict cfgComposerTplDict = DictProxy.getDict("AI_FLOW_COMPOSER_TIPS", "CFG_COMPOSER_TPL");
		if(nodeComposerTplDict == null || cfgComposerTplDict == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "NODE_COMPOSER_TPL or CFG_COMPOSER_TPL not found!");
		}
		String nodeComposerTpl = nodeComposerTplDict.getDictValue3();//节点编排提示词模板
		String cfgComposerTpl = cfgComposerTplDict.getDictValue3();//配置信息补充提示词模板
		if(StringUtils.isBlank(cfgComposerTpl) || StringUtils.isBlank(nodeComposerTpl)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "NODE_COMPOSER_TPL or CFG_COMPOSER_TPL not found!");
		}

		String operationInfoStr = "方法编码：" + operationCode + " 方法名称：" + operationName + " 方法涉及的数据库id：" + schemaId;
		String nodeComposer = nodeComposerTpl.replace(BUSINESS_REQUIREMENT, chatInfo.getBusinessRequirement()).replace(OPERATION_INFO, operationInfoStr);
		String cfgComposer = cfgComposerTpl.replace(BUSINESS_REQUIREMENT, chatInfo.getBusinessRequirement()).replace(OPERATION_INFO, operationInfoStr);
		
		List<Parameter> parameters = parameterSV.listByOperationId(operation.getId());
		String inParamStr = "paramCode, paramName, paramType, paramClass, isList \n", outParamStr = "paramType, paramClass, isList \n";//出参/入参信息
		Set<String> relDomainObjectIds = new HashSet<>(), relValueObjectIds = new HashSet<>();
		if(!CollectionUtils.isEmpty(parameters)) {
			for(Parameter param : parameters) {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
					relDomainObjectIds.add(param.getParamClass());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
					relValueObjectIds.add(param.getParamClass());
				}
				if(YesOrNo.YES.equals(param.getIsInParam())) {
					inParamStr += param.getCode() + ", " + param.getName() + ", " + DictProxy.getDictLabel("OBJECT_TYPE", param.getParamType()) + ", " + param.getParamClass() + ", " + param.getIsListParam() + " \n";
				}else {
					outParamStr += DictProxy.getDictLabel("OBJECT_TYPE", param.getParamType()) + ", " + param.getParamClass() + ", " + param.getIsListParam() + " \n";
				}
			}
		}
		nodeComposer = nodeComposer.replace(IN_PARAM_INFO, inParamStr).replace(OUT_PARAM_INFO, outParamStr);
		cfgComposer = cfgComposer.replace(IN_PARAM_INFO, inParamStr).replace(OUT_PARAM_INFO, outParamStr);

		String objectInfoStr = "";
		if(!CollectionUtils.isEmpty(relDomainObjectIds)) {
			for(String id : relDomainObjectIds) {
				DomainObject domainObject = domainObjectSV.byId(id);
				objectInfoStr += "对象id：" + domainObject.getId() + " 对象编码：" + domainObject.getCode() + " 对象名称：" + domainObject.getName() + " 对象类型：" + DictProxy.getDictLabel("OBJECT_TYPE", ManagedObjectType.DOMAIN_OBJECT) + " \n";
				objectInfoStr += "对象的属性列表包括：attrCode, attrName, attrType, attrClass, isList \n";
				List<Attribute> attributes = attributeSV.listByObjectId(ManagedObjectType.DOMAIN_OBJECT, id);
				if(!CollectionUtils.isEmpty(attributes)) {
					for(Attribute attr : attributes) {
						objectInfoStr += attr.getCode() + ", " + attr.getName() + ", " + DictProxy.getDictLabel("OBJECT_TYPE", attr.getAttrType()) + ", " + attr.getAttrClass() + ", " + attr.getIsListAttr() + " \n";
					}
				}
			}
		}
		if(!CollectionUtils.isEmpty(relValueObjectIds)) {
			for(String id : relValueObjectIds) {
				ValueObject valueObject = valueObjectSV.byId(id);
				objectInfoStr += "对象id：" + valueObject.getId() + " 对象编码：" + valueObject.getCode() + " 对象名称：" + valueObject.getName() + " 对象类型：" + DictProxy.getDictLabel("OBJECT_TYPE", ManagedObjectType.VALUE_OBJECT) + " \n";
				objectInfoStr += "对象的属性列表包括：attrCode, attrName, attrType, attrClass, isList \n";
				List<Attribute> attributes = attributeSV.listByObjectId(ManagedObjectType.VALUE_OBJECT, id);
				if(!CollectionUtils.isEmpty(attributes)) {
					for(Attribute attr : attributes) {
						objectInfoStr += attr.getCode() + ", " + attr.getName() + ", " + DictProxy.getDictLabel("OBJECT_TYPE", attr.getAttrType()) + ", " + attr.getAttrClass() + ", " + attr.getIsListAttr() + " \n";
					}
				}
			}
		}
		nodeComposer = nodeComposer.replace(REL_OBJECT_INFO, objectInfoStr);
		cfgComposer = cfgComposer.replace(REL_OBJECT_INFO, objectInfoStr);

		List<ServerResource> serverResources = serverResourceSV.listBySystem(chatInfo.getSystemId());
		String serverResourceInfoStr = "resourceId, resourceName, resourceType, resourceTypeName \n";
		if(!CollectionUtils.isEmpty(serverResources)) {
			for(ServerResource serverResource: serverResources) {
				serverResourceInfoStr += serverResource.getId() + ", " + serverResource.getName() + ", " + serverResource.getResourceType() + ", " + DictProxy.getDictLabel("SERVER_RESOURCE_TYPE", serverResource.getResourceType()) + " \n";
			}
		}
		nodeComposer = nodeComposer.replace(AVAILABLE_SERVER_RESOURCE_INFO, serverResourceInfoStr);
		cfgComposer = cfgComposer.replace(AVAILABLE_SERVER_RESOURCE_INFO, serverResourceInfoStr);

		nodeComposer = nodeComposer.replace(FLOW_NODE_LIST, BusinessFlowTemplate.getNodeListStr());
		String nodeList = this.aiDrivenAbility.chatWithAi(nodeComposer);
		if(nodeList.indexOf("######") >= 0) {
            nodeList = nodeList.replaceAll("######", StringUtils.emptyString());
        }
        JsonArray nodeArray = JsonUtils.toJsonArray(nodeList);
		if(JsonUtils.isEmpty(nodeArray)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid JsonArray for node list!");
		}
		Set<String> nodeTypeIds = new HashSet<>();
		this.extractNodeType(nodeArray, nodeTypeIds);
		if(CollectionUtils.isEmpty(nodeTypeIds)) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid node list gen by ai!");
		}
		String nodeCfgInfo = "";
		for(String nodeTypeId : nodeTypeIds) {
			Node node = BusinessFlowTemplate.getNode(nodeTypeId);
			if(node == null) {
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid nodeTypeId gen by ai!");
			}
			FlowNode flowNode = node.getFlowNode();
			nodeCfgInfo += "\"" + flowNode.getId() + "\", \"" + flowNode.getName() + "\", \"" + flowNode.getDescription() + "\", \"" + flowNode.getCfgRequirement() + "\" \n"; 
		}
		cfgComposer = cfgComposer.replace(NODE_CFG_LIST, nodeCfgInfo).replace(NODE_LIST, nodeList);
		return this.aiDrivenAbility.chatWithAiStream(cfgComposer, sseEmitter);
	}

	/**
	 * 提取所有的流程节点类型id
	 * @param nodeList
	 * @param nodeTypeIds
	 */
	private void extractNodeType(JsonArray nodeList, Set<String> nodeTypeIds) {
		if(JsonUtils.isEmpty(nodeList)) {
			return;
		}
		for(int i=0; i<nodeList.size(); i++) {
			JsonObject node = nodeList.get(i).getAsJsonObject();
			nodeTypeIds.add(JsonUtils.getString(node, "nodeTypeId"));
			JsonArray subBusinessFlow = JsonUtils.getJsonArray(node, "subBusinessFlow");
			if(!JsonUtils.isEmpty(subBusinessFlow)) {
				extractNodeType(subBusinessFlow, nodeTypeIds);
			}
		}
	}

	public BusinessFlowInfo genBusinessFlowInfoWithAi(AiGenBusinessFlowInfo aiGenInfo) throws EngineException {
		return this.aiDrivenAbility.genBusinessFlow(aiGenInfo.getBusinessFlowNodes(), aiGenInfo.getBusinessFlowId(), aiGenInfo.getTenantId(), aiGenInfo.getSystemId());
	}
}
