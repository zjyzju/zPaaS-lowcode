package cn.zpaas.lowcode.vo;

import java.util.Date;

import com.google.gson.JsonObject;

/**
 * 工作流任务信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class WorkflowTask {
	private String id;
	private String name;
	private String owner;
	private String assignee;
	private int priority;
	private String processDefId;
	private String taskDefKey;
	private String processInstId;
	private Date createTime;
	private String description;
	private Date dueDate;
	private Date followUPDate;
	private JsonObject variables;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getProcessDefId() {
		return processDefId;
	}
	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}
	public String getTaskDefKey() {
		return taskDefKey;
	}
	public void setTaskDefKey(String taskDefKey) {
		this.taskDefKey = taskDefKey;
	}
	public String getProcessInstId() {
		return processInstId;
	}
	public void setProcessInstId(String processInstId) {
		this.processInstId = processInstId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getFollowUPDate() {
		return followUPDate;
	}
	public void setFollowUPDate(Date followUPDate) {
		this.followUPDate = followUPDate;
	}
	public JsonObject getVariables() {
		return variables;
	}
	public void setVariables(JsonObject variables) {
		this.variables = variables;
	}

}
