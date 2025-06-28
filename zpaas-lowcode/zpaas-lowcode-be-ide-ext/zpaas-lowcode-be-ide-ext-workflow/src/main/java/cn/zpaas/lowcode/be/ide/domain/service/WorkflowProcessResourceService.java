package cn.zpaas.lowcode.be.ide.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.be.engine.ability.WorkflowAbility;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * 工作流流程资源领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-11:44:03
 */
@Service
public class WorkflowProcessResourceService {
    
    @Autowired
    private WorkflowProcessResource workflowProcessResourceSV;//工作流流程资源领域对象

    public Integer delete(String resourceId, String systemId) throws EngineException {
    	WorkflowProcessResource workflowProcessResource = workflowProcessResourceSV.delete(resourceId, systemId);
    	
    	if(workflowProcessResource != null && workflowProcessResource.getProcessDefId() != null) {
    		WorkflowAbility.getInstance().undeployWorkflowProcess(workflowProcessResource.getProcessDefId());
    	}
        if(workflowProcessResource != null) {
            return 1;
        }else {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "删除流程资源失败！");
        }
    }

    public WorkflowProcessResource save(WorkflowProcessResource workflowProcessResource) throws EngineException{
    	String processDefId = WorkflowAbility.getInstance().deployWorkflowProcess(workflowProcessResource.getName(), workflowProcessResource.getBpmnXml());
        if(StringUtils.isBlank(processDefId)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "部署流程资源失败！");
        }
		workflowProcessResource.setProcessDefId(processDefId);
    	WorkflowProcessResource result = workflowProcessResourceSV.save(workflowProcessResource);
        if(result == null) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "保存流程资源失败！");
        }
        return result;
    }
}
