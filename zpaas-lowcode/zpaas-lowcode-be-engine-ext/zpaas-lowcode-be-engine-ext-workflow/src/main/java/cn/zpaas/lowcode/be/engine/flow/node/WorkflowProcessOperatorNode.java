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

	/*
	 * {
	 * processResourceId: 流程资源标识，选择方式，从工作流流程资源表中提取当前业务系统且状态为已部署的流程资源。启动时必填，其他情况下可空。
	 * 
	 * processInstIdSource：流程实例标识来源，包括：P（过程数据）；D（领域对象）；O（属主对象）；I（输入参数）；F（固定值）。启动时可空，
	 * 其他情况下必填。
	 * processInstIdKey：流程实例标识的Key值。启动时可空，其他情况下必填。
	 * processInstIdAttr：当源对象是指定对象的某个属性时有效，通过该字段指定对应属性的code，此时指定对象不能是列表类型。
	 * 
	 * processOperation：流程操作，包括：S（启动）、R（重启）、D（删除）、P（挂起）、A（激活）
	 * 
	 * processVariablesSource：流程参数的来源，包括：I（原始输入参数）；P（过程数据）；N（预处理产生的nodeParams）
	 * processVariablesKey：流程参数的Key值，源对象类型为I（原始输入参数）和N（预处理产生的nodeParams）时，该值无效；为P（
	 * 过程数据）时，该值表示context.values中的key值
	 * 
	 * isListResult：节点执行结果是否是List类型，包括：true/false
	 * nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
	 * nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）
	 * 或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构
	 * }
	 * 
	 */
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
