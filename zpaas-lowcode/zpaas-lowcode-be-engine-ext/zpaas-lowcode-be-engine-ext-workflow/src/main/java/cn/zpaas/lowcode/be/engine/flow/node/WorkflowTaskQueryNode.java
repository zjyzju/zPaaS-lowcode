package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.WorkflowTask;
import cn.zpaas.lowcode.be.engine.ability.WorkflowAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         工作流流程任务查询节点的实现类
 */
public class WorkflowTaskQueryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(WorkflowTaskQueryNode.class);

	public static final String CFG_PROCESS_DEF_ID_SOURCE_KEY = "processDefIdSource"; // 流程定义标识来源存放的Key
	public static final String CFG_PROCESS_DEF_ID_KEY = "processDefIdKey"; // 流程定义标识存放的Key
	public static final String CFG_PROCESS_DEF_ID_ATTR_KEY = "processDefIdAttr"; // 流程定义标识存放的属性

	public static final String CFG_PROCESS_DEF_KEY_SOURCE_KEY = "processDefKeySource"; // 流程定义关键字来源存放的Key
	public static final String CFG_PROCESS_DEF_KEY_KEY = "processDefKeyKey"; // 流程定义关键字存放的Key
	public static final String CFG_PROCESS_DEF_KEY_ATTR_KEY = "processDefKeyAttr"; // 流程定义关键字存放的属性

	public static final String CFG_CANDIDATE_GROUPS_SOURCE_KEY = "candidateGroupsSource"; // 任务候选组关键字来源存放的Key
	public static final String CFG_CANDIDATE_GROUPS_KEY_KEY = "candidateGroupsKey"; // 任务候选组关键字存放的Key
	public static final String CFG_CANDIDATE_GROUPS_ATTR_KEY = "candidateGroupsAttr"; // 任务候选组关键字存放的属性

	public static final String CFG_ASSIGNEE_SOURCE_KEY = "assigneeSource"; // 任务绑定员工关键字来源存放的Key
	public static final String CFG_ASSIGNEE_KEY_KEY = "assigneeKey"; // 任务绑定员工关键字存放的Key
	public static final String CFG_ASSIGNEE_ATTR_KEY = "assigneeAttr"; // 任务绑定员工关键字存放的属性

	public static final String CFG_TASK_DEF_KEY_SOURCE_KEY = "taskDefKeySource"; // 任务绑定员工关键字来源存放的Key
	public static final String CFG_TASK_DEF_KEY_KEY = "taskDefKeyKey"; // 任务绑定员工关键字存放的Key
	public static final String CFG_TASK_DEF_KEY_ATTR_KEY = "taskDefKeyAttr"; // 任务绑定员工关键字存放的属性

	public static final String CFG_PROCESS_INST_ID_SOURCE_KEY = "processInstIdSource"; // 流程实例标识来源存放的Key
	public static final String CFG_PROCESS_INST_ID_KEY = "processInstIdKey"; // 流程实例标识存放的Key
	public static final String CFG_PROCESS_INST_ID_ATTR_KEY = "processInstIdAttr"; // 流程实例标识存放的属性

	
	@Override
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		// 获取流程定义标识
		String processDefIdSource = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_ID_SOURCE_KEY);
		String processDefIdKey = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_ID_KEY);
		String processDefIdAttr = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_ID_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(processDefIdAttr)) {
			processDefIdAttr = this.dynamicAttrProcess(processDefIdAttr, businessFlowNode, context);
		}
		Object processDefIdObject = getContextObject(processDefIdSource, processDefIdKey, false, processDefIdAttr,
				context, businessFlowNode);
		if (processDefIdObject != null && !(processDefIdObject instanceof String)) {
			logger.error("T[{}] invalid processDefId! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide processDefId!");
		}
		String processDefId = (String) processDefIdObject;

		// 获取流程定义关键字
		String processDefKeySource = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_KEY_SOURCE_KEY);
		String processDefKeyKey = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_KEY_KEY);
		String processDefKeyAttr = JsonUtils.getString(nodeCfg, CFG_PROCESS_DEF_KEY_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(processDefKeyAttr)) {
			processDefKeyAttr = this.dynamicAttrProcess(processDefKeyAttr, businessFlowNode, context);
		}
		Object processDefKeyObject = getContextObject(processDefKeySource, processDefKeyKey, false, processDefKeyAttr,
				context, businessFlowNode);
		if (processDefKeyObject != null && !(processDefKeyObject instanceof String)) {
			logger.error("T[{}] invalid processDefKey! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide processDefKey!");
		}
		String processDefKey = (String) processDefKeyObject;

		// 获取任务绑定候选组
		String candidateGroupsSource = JsonUtils.getString(nodeCfg, CFG_CANDIDATE_GROUPS_SOURCE_KEY);
		String candidateGroupsKey = JsonUtils.getString(nodeCfg, CFG_CANDIDATE_GROUPS_KEY_KEY);
		String candidateGroupsAttr = JsonUtils.getString(nodeCfg, CFG_CANDIDATE_GROUPS_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(candidateGroupsAttr)) {
			candidateGroupsAttr = this.dynamicAttrProcess(candidateGroupsAttr, businessFlowNode, context);
		}
		Object candidateGroupsObject = getContextObject(candidateGroupsSource, candidateGroupsKey, true,
				candidateGroupsAttr,
				context, businessFlowNode);

		if (candidateGroupsObject != null && OBJECT_INSTANCE_SOURCE_F.equals(candidateGroupsSource)) {
			candidateGroupsObject = JsonUtils.toJsonArray((String) candidateGroupsObject);
		} else if (candidateGroupsObject != null && !(candidateGroupsObject instanceof JsonArray)) {
			logger.error("T[{}] invalid candidateGroups! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide candidateGroups!");
		}
		JsonArray candidateGroups = (JsonArray) candidateGroupsObject;

		// 获取任务绑定员工
		String assigneeSource = JsonUtils.getString(nodeCfg, CFG_ASSIGNEE_SOURCE_KEY);
		String assigneeKey = JsonUtils.getString(nodeCfg, CFG_ASSIGNEE_KEY_KEY);
		String assigneeAttr = JsonUtils.getString(nodeCfg, CFG_ASSIGNEE_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(assigneeAttr)) {
			assigneeAttr = this.dynamicAttrProcess(assigneeAttr, businessFlowNode, context);
		}
		Object assigneeObject = getContextObject(assigneeSource, assigneeKey, false, assigneeAttr,
				context, businessFlowNode);
		if (assigneeObject != null && !(assigneeObject instanceof String)) {
			logger.error("T[{}] invalid assignee! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide assignee!");
		}
		String assignee = (String) assigneeObject;

		// 获取任务绑定候选组
		String taskDefKeysSource = JsonUtils.getString(nodeCfg, CFG_TASK_DEF_KEY_SOURCE_KEY);
		String taskDefKeysKey = JsonUtils.getString(nodeCfg, CFG_TASK_DEF_KEY_KEY);
		String taskDefKeysAttr = JsonUtils.getString(nodeCfg, CFG_TASK_DEF_KEY_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(taskDefKeysAttr)) {
			taskDefKeysAttr = this.dynamicAttrProcess(taskDefKeysAttr, businessFlowNode, context);
		}
		Object taskDefKeysObject = getContextObject(taskDefKeysSource, taskDefKeysKey, true,
				taskDefKeysAttr, context, businessFlowNode);
		if (taskDefKeysObject != null && OBJECT_INSTANCE_SOURCE_F.equals(taskDefKeysSource)) {
			taskDefKeysObject = JsonUtils.toJsonArray((String) taskDefKeysObject);
		} else if (taskDefKeysObject != null && (taskDefKeysObject instanceof String)) {
			taskDefKeysObject = JsonUtils.toJsonArray((String) taskDefKeysObject);
		} else if (taskDefKeysObject != null && !(taskDefKeysObject instanceof JsonArray)) {
			logger.error("T[{}] invalid taskDefKeys! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide taskDefKeys!");
		}
		JsonArray taskDefKeys = (JsonArray) taskDefKeysObject;

		// 获取流程定义标识
		String processInstIdSource = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_SOURCE_KEY);
		String processInstIdKey = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_KEY);
		String processInstIdAttr = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_ATTR_KEY);
		// 动态属性的情况
		if (!StringUtils.isBlank(processInstIdAttr)) {
			processInstIdAttr = this.dynamicAttrProcess(processInstIdAttr, businessFlowNode, context);
		}
		Object processInstIdObject = getContextObject(processInstIdSource, processInstIdKey, false, processInstIdAttr,
				context, businessFlowNode);
		if (processInstIdObject != null && !(processInstIdObject instanceof String)) {
			logger.error("T[{}] invalid processInstId! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide processInstId!");
		}
		String processInstId = (String) processInstIdObject;

		List<WorkflowTask> workflowTasks = WorkflowAbility.getInstance().queryTaskList(processDefId, processDefKey,
				candidateGroups, assignee, taskDefKeys, processInstId);

		JsonArray results = new JsonArray();
		if (!CollectionUtils.isEmpty(workflowTasks)) {
			for (WorkflowTask workflowTask : workflowTasks) {
				results.add(JsonUtils.toJsonObject(JsonUtils.toJson(workflowTask)));
			}
		}
		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, results);
	}

}
