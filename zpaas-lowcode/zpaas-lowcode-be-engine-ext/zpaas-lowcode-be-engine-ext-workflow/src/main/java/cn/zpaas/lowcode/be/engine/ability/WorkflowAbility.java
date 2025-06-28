package cn.zpaas.lowcode.be.engine.ability;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.ProcessDiagramInfo;
import cn.zpaas.lowcode.vo.WorkflowTask;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *
 */
public class WorkflowAbility {
	public static Logger logger = LoggerFactory.getLogger(WorkflowAbility.class);
	
	//单例实例
	private static WorkflowAbility instance = null;
	
	private RepositoryService repositoryService;
	
	private RuntimeService runtimeService;
	
	private TaskService taskService;
	
	private HistoryService historyService;

	// 用来缓存WorkflowProcessResource对象数据的Map
	private Map<String, Map<String, WorkflowProcessResource>> resourceMap = new HashMap<>();

	// 私有化构造函数
	private WorkflowAbility() {
		
	}
	
	public static WorkflowAbility getInstance() {
		return instance;
	}
	
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		WorkflowAbility workflowAbility = new WorkflowAbility();
		
		workflowAbility.repositoryService = SpringContextUtils.getBean(RepositoryService.class);
		workflowAbility.runtimeService = SpringContextUtils.getBean(RuntimeService.class);
		workflowAbility.historyService = SpringContextUtils.getBean(HistoryService.class);
		workflowAbility.taskService = SpringContextUtils.getBean(TaskService.class);
		
		// 加载WorkflowProcessResource数据
		List<WorkflowProcessResource> processResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			processResources = initService.listWorkflowProcessResources();
		} else {// 加载指定业务系统的数据
			processResources = initService.listWorkflowProcessResourceBySystemId(systemId);
		}

		if (!CollectionUtils.isEmpty(processResources)) {
			for(WorkflowProcessResource processResource : processResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(processResource.getSystemId())) {
					continue;
				}
				// 将WorkflowProcessResource对象加入缓存
				Map<String, WorkflowProcessResource> map = workflowAbility.resourceMap.get(processResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					workflowAbility.resourceMap.put(processResource.getSystemId(), map);
				}
				map.put(processResource.getId(), processResource);
			}
		} else {
			logger.info("no valid WorkflowProcessResource.");
		}

		// 初始化完成，赋值给属性instance
		instance = workflowAbility;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		// 加载WorkflowProcessResource数据
		List<WorkflowProcessResource> processResources = initService.listWorkflowProcessResourceBySystemId(systemId);
		Map<String, WorkflowProcessResource> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(processResources)) {
			for(WorkflowProcessResource processResource : processResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(processResource.getSystemId())) {
					continue;
				}
				// 将WorkflowProcessResource对象加入缓存
				map.put(processResource.getId(), processResource);
			}
		} 
		instance.resourceMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和流程资源组标识获取对应的流程资源对象
	 * 
	 * @param systemId      业务系统标识
	 * @param resourceId 流程资源标识
	 * @return 返回WorkflowProcessResource对象
	 */
	public WorkflowProcessResource getWorkflowProcessResource(String systemId, String resourceId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, WorkflowProcessResource> map = resourceMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(resourceId);
	}
	
	/**
	 * 部署流程
	 * @param resourceName
	 * @param bpmnXml
	 * @return
	 * @throws EngineException
	 */
	public String deployWorkflowProcess(String resourceName, String bpmnXml) throws EngineException{
		Deployment deployment = repositoryService.createDeployment().addString(resourceName + ".bpmn", bpmnXml).deploy();
		if(deployment == null) {
			logger.error("deploy process define failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "deploy process define failed!");
		}
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
		if(processDefinition == null) {
			logger.error("get ProcessDefinition info failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "get ProcessDefinition info failed!");
		}
		return processDefinition.getId();
	}
	
	/**
	 * 删除流程定义
	 * @param processDefId
	 * @return
	 */
	public boolean undeployWorkflowProcess(String processDefId) {
		try {
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(processDefId).singleResult();
			if(processDefinition == null) {
				return true;
			}
			repositoryService.deleteProcessDefinition(processDefId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 启动流程
	 * @param processDefId
	 * @param variables
	 * @return
	 */
	public String startWorkflowProcessResource(String processDefId, Map<String, Object> variables) {
		if(processDefId == null || processDefId.trim().length() == 0) {
			logger.error("processDefId  is null!");
			return null;
		}
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefId).singleResult();
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinition.getKey(), variables);
		
		return processInstance.getProcessInstanceId();
	}
	
	/**
	 * 查询所有任务列表
	 * @return
	 */
	public List<WorkflowTask> queryTaskList() {
		List<Task> tasks = taskService.createTaskQuery().active().list();
		return transferToWorkflowTask(tasks);
	}
	
	/**
	 * 查询任务列表
	 * @param processDefId
	 * @param processDefKey
	 * @param candidateGroups
	 * @param assignee
	 * @param taskDefKeys
	 * @param processInstId
	 * @return
	 */
	public List<WorkflowTask> queryTaskList(String processDefId, String processDefKey, JsonArray candidateGroups, String assignee, 
			JsonArray taskDefKeys, String processInstId) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		if(!StringUtils.isBlank(processDefId)) {
			taskQuery.processDefinitionId(processDefId);
		}
		if(!StringUtils.isBlank(processDefKey)) {
			taskQuery.processDefinitionKey(processDefKey);
		}
		if(!JsonUtils.isEmpty(candidateGroups)) {
			taskQuery.taskCandidateGroupIn(candidateGroups.asList().stream().map(element->element.getAsString()).collect(Collectors.toList()));
		}
		if(!StringUtils.isBlank(assignee)) {
			taskQuery.taskAssignee(assignee);
		}
		if(!JsonUtils.isEmpty(taskDefKeys)) {
			taskQuery.taskDefinitionKeyIn(taskDefKeys.asList().stream().map(element->element.getAsString()).collect(Collectors.toList()).toArray(new String[0]));
		}
		if(!StringUtils.isBlank(processInstId)) {
			taskQuery.processInstanceId(processInstId);
		}
		List<Task> tasks = taskQuery.active().list();
		return transferToWorkflowTask(tasks);
	}
	
	/**
	 * 转换成WorkflowTask类型
	 * @param tasks
	 * @return
	 */
	private List<WorkflowTask> transferToWorkflowTask(List<Task> tasks) {
		List<WorkflowTask> results = null;
		if(!CollectionUtils.isEmpty(tasks)) {
			results = new ArrayList<>();
			WorkflowTask workflowTask = null;
			for(Task task : tasks) {
				workflowTask = new WorkflowTask();
				workflowTask.setId(task.getId());
				workflowTask.setAssignee(task.getAssignee());
				workflowTask.setCreateTime(task.getCreateTime());
				workflowTask.setDescription(task.getDescription());
				workflowTask.setDueDate(task.getDueDate());
				workflowTask.setFollowUPDate(task.getFollowUpDate());
				workflowTask.setName(task.getName());
				workflowTask.setOwner(task.getOwner());
				workflowTask.setPriority(task.getPriority());
				workflowTask.setProcessDefId(task.getProcessDefinitionId());
				workflowTask.setProcessInstId(task.getProcessInstanceId());
				workflowTask.setTaskDefKey(task.getTaskDefinitionKey());
				workflowTask.setVariables(JsonUtils.toJsonObject(taskService.getVariables(task.getId())));
				results.add(workflowTask);
			}
		}
		return results;
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param variables
	 */
	public void completeTask(String taskId, Map<String, Object> variables) {
		taskService.complete(taskId, variables);
	}
	
	/**
	 * 领取任务
	 * @param taskId
	 * @param userId
	 */
	public void claimTask(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}
	
	/**
	 * 删除任务
	 * @param taskId
	 * @param variables
	 */
	public void deleteTask(String taskId, String reason) {
		taskService.deleteTask(taskId, reason);
	}
	
	/**
	 * 委托任务
	 * @param taskId
	 * @param variables
	 */
	public void delegateTask(String taskId, String userId) {
		taskService.delegateTask(taskId, userId);
	}
	
	/**
	 * 完成任务
	 * @param taskId
	 * @param variables
	 */
	public void resolveTask(String taskId, Map<String, Object> variables) {
		taskService.resolveTask(taskId, variables);
	}
	
	
	
	/**
	 * 查询任务实例列表
	 * @return
	 */
	public List<ProcessInstance> querProcessInstList() {
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().active().list();
		return processInstances;
	}
	
	/**
	 * 删除流程实例
	 * @param processInstId
	 * @param reason
	 */
	public void deleteWorkflowProcessInst(String processInstId, String reason) {
		runtimeService.deleteProcessInstance(processInstId, reason);
	}
	
	/**
	 * 重启流程实例
	 * @param processInstId
	 */
	public void restartProcessInst(String processInstId) {
		runtimeService.restartProcessInstances(processInstId);
	}
	
	/**
	 * 挂起流程实例
	 * @param processInstId
	 */
	public void suspendProcessInst(String processInstId) {
		runtimeService.suspendProcessInstanceById(processInstId);
	}
	
	/**
	 * 激活流程实例
	 * @param processInstId
	 */
	public void activateProcessInst(String processInstId) {
		runtimeService.activateProcessInstanceById(processInstId);
	}
	
	
	
	/**
	 * 查询taskKey
	 * @param taskId
	 * @return
	 */
	public String findTaskKey(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return task.getTaskDefinitionKey();
	}
	
	/**
	 * 查询历史活动实例标识列表
	 * @param procInstId
	 * @return
	 */
	public List<String> findHistoricActivityIds(String procInstId) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).list();
		for(HistoricTaskInstance instance  : list) {
			logger.debug("HistoricTaskInstance name:{}, key:{}", instance.getName(), instance.getTaskDefinitionKey());
		}
		List<String> taskIds = new ArrayList<>();
		logger.debug("ActivityInstance of procInst: {}", procInstId);
		List<HistoricActivityInstance> list2 = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId).list();
		for(HistoricActivityInstance instance  : list2) {
			logger.debug("HistoricActivityInstance name:{}, Id:{}, type:{}", 
					instance.getActivityName(), instance.getActivityId(), instance.getActivityType());
			taskIds.add(instance.getActivityId());
		}
		return taskIds;
	}
	
	/**
	 * 加载流程实例的流程图信息
	 * @param processInstId
	 * @return
	 */
	public ProcessDiagramInfo loadDiagramInfo(String processInstId) throws EngineException{
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstId).singleResult();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
		Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(repositoryService.getResourceAsStream(deployment.getId(), processDefinition.getResourceName())))){
			String sr = br.lines().collect(Collectors.joining("\n"));
			ProcessDiagramInfo processDiagramInfo = new ProcessDiagramInfo();
			processDiagramInfo.setBpmnXml(sr);
			processDiagramInfo.setHistActivityIds(findHistoricActivityIds(processInstId));
			return processDiagramInfo;
		} catch (Exception e) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "loadDiagramInfo failed!", e.getMessage(), e);
		}
	}
}
