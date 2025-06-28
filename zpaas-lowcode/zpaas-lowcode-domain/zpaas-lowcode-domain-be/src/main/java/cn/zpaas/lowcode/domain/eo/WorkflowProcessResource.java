package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.WorkflowProcessResourceMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class WorkflowProcessResource {
	
    private String id;

    private String name;

    private String processDefId;

    private String processDefKey;

    private String actModelId;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private String bpmnXml;
    
    @Autowired
    private WorkflowProcessResourceMapper mapper;
    
    
    public WorkflowProcessResource delete(String resourceId, String systemId) {
    	WorkflowProcessResourceExample criteria = new WorkflowProcessResourceExample();
    	criteria.createCriteria().andIdEqualTo(resourceId).andSystemIdEqualTo(systemId).andStatusNotEqualTo(Status.EXP);
    	List<WorkflowProcessResource> list = mapper.selectByExampleWithBLOBs(criteria);
    	
    	if(list == null || list.isEmpty()) {
    		return null;
    	}
    	
    	WorkflowProcessResource workflowProcessResource = list.get(0);
    	workflowProcessResource.setStatus(Status.EXP);
    	workflowProcessResource.setUpdateTime(new Date());
    	
    	if(mapper.updateByPrimaryKey(workflowProcessResource) > 0 ){
            return workflowProcessResource;
        }else {
            return null;
        }
    } 

    public WorkflowProcessResource save(WorkflowProcessResource workflowProcessResource) throws EngineException{
    	Date nowDate = new Date();
    	workflowProcessResource.setUpdateTime(nowDate);
    	if(workflowProcessResource.getId() == null || workflowProcessResource.getId().trim().length() == 0) {
    		workflowProcessResource.setId(KeyGenerate.uuidKey());
    		workflowProcessResource.setCreateTime(nowDate);
    		workflowProcessResource.setStatus(Status.EFF);
    		if(mapper.insert(workflowProcessResource) > 0) {
            	return workflowProcessResource;
            }else {
            	return null;
            }
        }else {
            workflowProcessResource.setUpdateTime(nowDate);
    		if(mapper.updateByPrimaryKeyWithBLOBs(workflowProcessResource) > 0) {
        		return workflowProcessResource;
        	}else {
        		return null;
        	}
    	}
    }
   
    
    public List<WorkflowProcessResource> list(String systemId) {
    	WorkflowProcessResourceExample criteria = new WorkflowProcessResourceExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusNotEqualTo(Status.EXP);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExampleWithBLOBs(criteria);
    }

    public List<WorkflowProcessResource> list() {
    	WorkflowProcessResourceExample criteria = new WorkflowProcessResourceExample();
    	criteria.createCriteria().andStatusNotEqualTo(Status.EXP);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExampleWithBLOBs(criteria);
    }
    
    public List<WorkflowProcessResource> listDeployed(String systemId) {
    	WorkflowProcessResourceExample criteria = new WorkflowProcessResourceExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExampleWithBLOBs(criteria);
    }
    
    
    
    public WorkflowProcessResource byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId == null ? null : processDefId.trim();
    }

    public String getProcessDefKey() {
        return processDefKey;
    }

    public void setProcessDefKey(String processDefKey) {
        this.processDefKey = processDefKey;
    }


    public String getActModelId() {
        return actModelId;
    }

    public void setActModelId(String actModelId) {
        this.actModelId = actModelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml == null ? null : bpmnXml.trim();
    }
}