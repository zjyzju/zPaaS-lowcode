package cn.zpaas.lowcode.be.ide.service;

import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * Workflow服务端领域平台服务接口类
 */
public interface WorkflowPlatformService {
	/**
	 * 保存工作流流程资源信息
	 * @param workflowProcessResource
	 * @return
	 */
	public WorkflowProcessResource saveWorkflowProcessResource(WorkflowProcessResource workflowProcessResource)throws EngineException;
	
	/**
	 * 删除工作流流程资源
	 * @param resourceId
	 * @param systemId
	 * @return
	 * @throws EngineException
	 */
	public Integer deleteWorkflowProcessResource(String resourceId, String systemId)throws EngineException;
}
