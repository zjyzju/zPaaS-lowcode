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
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 工作流流程操作节点的实现类
 */
public class WorkflowTaskOperatorNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(WorkflowTaskOperatorNode.class);

	public static final String CFG_TASK_ID_SOURCE_KEY = "taskIdSource"; //任务实例标识来源存放的Key
	public static final String CFG_TASK_ID_KEY_KEY = "taskIdKey"; //任务实例标识存放的Key
	public static final String CFG_TASK_ID_ATTR_KEY = "taskIdAttr"; //任务实例标识存放的属性
	
	public static final String CFG_TASK_OPERATION_KEY = "taskOperation"; //要执行的任务操作存放的Key
	
	public static final String CFG_PROCESS_VARIABLES_SOURCE_KEY = "processVariablesSource"; //流程参数来源存放的Key
	public static final String CFG_PROCESS_VARIABLES_KEY_KEY = "processVariablesKey"; //流程参数Key存放的Key
	
	public static final String TASK_OPERATION_CLAIM = "A"; //领取
	public static final String TASK_OPERATION_COMPLETE = "C"; //完成
	public static final String TASK_OPERATION_DELEGATE = "G"; //委托
	public static final String TASK_OPERATION_DELETE = "D"; //删除
	public static final String TASK_OPERATION_RESOLVE = "R"; //解决
	
	
	
	/*
	 {
		taskIdSource：任务实例标识来源，包括：P（过程数据）；D（领域对象）；O（属主对象）；I（输入参数）；F（固定值）。必填。
		taskIdKey：任务实例标识的Key值。必填。
		taskIdAttr：当源对象是指定对象的某个属性时有效，通过该字段指定对应属性的code，此时指定对象不能是列表类型。

		taskOperation：任务操作，包括：A（领取）、C（完成）、G（委托）、D（删除）、R（解决）。必填。

		processVariablesSource：流程参数的来源，包括：I（原始输入参数）；P（过程数据）；N（预处理产生的nodeParams）
		processVariablesKey：流程参数的Key值，源对象类型为I（原始输入参数）和N（预处理产生的nodeParams）时，该值无效；为P（过程数据）时，该值表示context.values中的key值
		
		isListResult：节点执行结果是否是List类型，包括：true/false
		nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
		nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构
	}

	 * 
	 */
	@Override
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context)  throws EngineException{
		//获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		//如果为空，则直接报错
		if(StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}
		
		//获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		String taskIdSource = JsonUtils.getString(nodeCfg, CFG_TASK_ID_SOURCE_KEY);
		String taskIdKey = JsonUtils.getString(nodeCfg, CFG_TASK_ID_KEY_KEY);
		String taskIdAttr = JsonUtils.getString(nodeCfg, CFG_TASK_ID_ATTR_KEY);
		
		String taskOperation	= JsonUtils.getString(nodeCfg, CFG_TASK_OPERATION_KEY);
		String processVariablesSource	= JsonUtils.getString(nodeCfg, CFG_PROCESS_VARIABLES_SOURCE_KEY);
		String processVariablesKey	= JsonUtils.getString(nodeCfg, CFG_PROCESS_VARIABLES_KEY_KEY);
		//taskIdSource processInstIdKey不能为空
		if(StringUtils.isBlank(taskIdSource) || StringUtils.isBlank(taskIdKey)) {
			logger.error("T[{}] taskIdSource and taskIdKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "taskIdSource and taskIdKey can't be null.");
		}
		//taskOperation不能为空
		if (StringUtils.isBlank(taskOperation)) {
			logger.error("T[{}] taskOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "taskOperation can't be null.");
		}
		
		// 动态属性的情况
		if (!StringUtils.isBlank(taskIdAttr)) {
			taskIdAttr = this.dynamicAttrProcess(taskIdAttr, businessFlowNode, context);
		}
		
		// 获取流程实例标识
		Object taskIdObject = getContextObject(taskIdSource, taskIdKey, false, taskIdAttr, context, businessFlowNode);
		if (taskIdObject == null || !(taskIdObject instanceof String)) {
			logger.error("T[{}] invalid taskId: {}! businessflowNodeId: {}", businessFlowNode.getTenantId(), taskIdObject, businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid taskId!");
		}
		String taskId = (String) taskIdObject;
		
		

		if (TASK_OPERATION_COMPLETE.equals(taskOperation)) {// 完成任务
			//获取流程参数
			Object processVariablesObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if(processVariablesObject != null && !(processVariablesObject instanceof JsonObject)) {
				logger.error("T[{}] invalid processVariables! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid processVariables!");
			}
			JsonObject processVariables = (JsonObject)processVariablesObject;
			WorkflowAbility.getInstance().completeTask(taskId, JsonUtils.toMap(processVariables, true));
		} else if (TASK_OPERATION_RESOLVE.equals(taskOperation)) {// 解决任务
			//获取流程参数
			Object processVariablesObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if(processVariablesObject != null && !(processVariablesObject instanceof JsonObject)) {
				logger.error("T[{}] invalid processVariables! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid processVariables!");
			}
			JsonObject processVariables = (JsonObject)processVariablesObject;
			WorkflowAbility.getInstance().resolveTask(taskId, JsonUtils.toMap(processVariables, true));
		} else if (TASK_OPERATION_DELETE.equals(taskOperation)) {// 删除任务
			//获取删除原因
			Object deleteReasonObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if(deleteReasonObject != null && !(deleteReasonObject instanceof String)) {
				logger.error("T[{}] invalid delete task reason! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid delete task reason!");
			}
			String deleteReason = (String)deleteReasonObject;
			WorkflowAbility.getInstance().deleteTask(taskId, deleteReason);
		} else if (TASK_OPERATION_DELEGATE.equals(taskOperation)) {// 委托任务
			//获取目标用户标识
			Object userIdObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if(userIdObject != null && !(userIdObject instanceof String)) {
				logger.error("T[{}] invalid delegate userId! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid delegate userId!");
			}
			String userId = (String)userIdObject;
			WorkflowAbility.getInstance().delegateTask(taskId, userId);
		} else if (TASK_OPERATION_CLAIM.equals(taskOperation)) {// 领取任务
			//获取目标用户标识
			Object userIdObject = getContextObject(processVariablesSource, processVariablesKey, false, null, context, businessFlowNode);
			if(userIdObject != null && !(userIdObject instanceof String)) {
				logger.error("T[{}] invalid delegate userId! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid delegate userId!");
			}
			String userId = (String)userIdObject;
			WorkflowAbility.getInstance().claimTask(taskId, userId);
		} else {
			logger.error("T[{}] invalid task operation! businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid task operation!");
		}
		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, null);
	}

}
