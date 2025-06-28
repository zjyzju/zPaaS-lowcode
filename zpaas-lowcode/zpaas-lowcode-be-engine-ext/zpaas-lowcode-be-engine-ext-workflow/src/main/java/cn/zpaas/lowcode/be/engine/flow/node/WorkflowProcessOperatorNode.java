package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.WorkflowAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         工作流流程操作节点的实现类
 */
public class WorkflowProcessOperatorNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(WorkflowProcessOperatorNode.class);

	public static final String CFG_PROCESS_INST_ID_SOURCE_KEY = "processInstIdSource"; // 流程实例标识来源存放的Key
	public static final String CFG_PROCESS_INST_ID_KEY_KEY = "processInstIdKey"; // 流程实例标识存放的Key
	public static final String CFG_PROCESS_INST_ID_ATTR_KEY = "processInstIdAttr"; // 流程实例标识存放的属性

	public static final String CFG_PROCESS_RESOURCE_ID_KEY = "processResourceId"; // 流程资源标识存放的Key

	public static final String CFG_PROCESS_OPERATION_KEY = "processOperation"; // 要执行的流程操作存放的Key

	public static final String CFG_PROCESS_VARIABLES_SOURCE_KEY = "processVariablesSource"; // 流程参数来源存放的Key
	public static final String CFG_PROCESS_VARIABLES_KEY_KEY = "processVariablesKey"; // 流程参数Key存放的Key

	public static final String PROCESS_OPERATION_START = "S"; // 启动操作
	public static final String PROCESS_OPERATION_RESTART = "R"; // 重启操作
	public static final String PROCESS_OPERATION_DELETE = "D"; // 删除操作
	public static final String PROCESS_OPERATION_SUSPEND = "P"; // 挂起操作
	public static final String PROCESS_OPERATION_ACTIVATE = "A"; // 激活操作

	
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
		String processInstIdSource = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_SOURCE_KEY);
		String processInstIdKey = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_KEY_KEY);
		String processInstIdAttr = JsonUtils.getString(nodeCfg, CFG_PROCESS_INST_ID_ATTR_KEY);

		String processResourceId = JsonUtils.getString(nodeCfg, CFG_PROCESS_RESOURCE_ID_KEY);
		String processOperation = JsonUtils.getString(nodeCfg, CFG_PROCESS_OPERATION_KEY);
		String processVariablesSource = JsonUtils.getString(nodeCfg, CFG_PROCESS_VARIABLES_SOURCE_KEY);
		String processVariablesKey = JsonUtils.getString(nodeCfg, CFG_PROCESS_VARIABLES_KEY_KEY);
		// 当非启动流程时，processInstIdSource processInstIdKey不能为空
		if (!PROCESS_OPERATION_START.equals(processOperation)
				&& (StringUtils.isBlank(processInstIdSource) || StringUtils.isBlank(processInstIdKey))) {
			logger.error("T[{}] when not start process, processInstIdSource and processInstIdKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "when not start process, processInstIdSource and processInstIdKey can't be null.");
		}
		// 当启动流程时，workflowProcessResourceId不能为空
		if (PROCESS_OPERATION_START.equals(processOperation) && StringUtils.isBlank(processResourceId)) {
			logger.error("T[{}] when start process, processResourceId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "when start process, processResourceId can't be null.");
		}

		// 启动流程
		if (PROCESS_OPERATION_START.equals(processOperation)) {
			// 获取流程参数
			Object processVariablesObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if (processVariablesObject != null && !(processVariablesObject instanceof JsonObject)) {
				logger.error("T[{}] invalid processVariables! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid processVariables!");
			}

			String processDefId = null;
			WorkflowProcessResource processResource = WorkflowAbility.getInstance().getWorkflowProcessResource(businessFlowNode.getSystemId(), processResourceId);
			if(processResource != null) {
				processDefId = processResource.getProcessDefId();
			}
			if(StringUtils.isBlank(processDefId)) {
				logger.error("T[{}] when start process, processDefId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "when start process, processDefId can't be null.");
			}
			JsonObject processVariables = (JsonObject) processVariablesObject;
			// 启动流程
			String processInstId = WorkflowAbility.getInstance().startWorkflowProcessResource(processDefId,
					JsonUtils.toMap(processVariables, true));
			// 将目标对象保存到节点处理结果中
			context.setAttribute(NODE_RESULT_KEY, processInstId);
		} else {
			// 动态属性的情况
			if (!StringUtils.isBlank(processInstIdAttr)) {
				processInstIdAttr = this.dynamicAttrProcess(processInstIdAttr, businessFlowNode, context);
			}
			// 获取流程实例标识
			Object processInstIdObject = getContextObject(processInstIdSource, processInstIdKey, false,
					processInstIdAttr, context, businessFlowNode);
			if (processInstIdObject == null || !(processInstIdObject instanceof String)) {
				logger.error("T[{}] invalid processInstId! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalide processInstId!");
			}
			String processInstId = (String) processInstIdObject;

			if (PROCESS_OPERATION_DELETE.equals(processOperation)) {// 删除流程实例
				// 获取删除原因
				Object deleteReasonObject = getContextObject(processVariablesSource, processVariablesKey, false, null,
						context, businessFlowNode);
				if (deleteReasonObject != null && !(deleteReasonObject instanceof String)) {
					logger.error("T[{}] invalid delete processInst reason! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid delete processInst reason!");
				}
				String deleteReason = (String) deleteReasonObject;
				// 删除流程实例
				WorkflowAbility.getInstance().deleteWorkflowProcessInst(processInstId, deleteReason);
			} else if (PROCESS_OPERATION_RESTART.equals(processOperation)) {// 重启流程实例
				WorkflowAbility.getInstance().restartProcessInst(processInstId);
			} else if (PROCESS_OPERATION_SUSPEND.equals(processOperation)) {// 挂起流程实例
				WorkflowAbility.getInstance().suspendProcessInst(processInstId);
			} else if (PROCESS_OPERATION_ACTIVATE.equals(processOperation)) {// 激活流程实例
				WorkflowAbility.getInstance().activateProcessInst(processInstId);
			} else {
				logger.error("T[{}] invalid process operation! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid process operation!");
			}
			// 将目标对象保存到节点处理结果中
			context.setAttribute(NODE_RESULT_KEY, null);
		}
	}

}
