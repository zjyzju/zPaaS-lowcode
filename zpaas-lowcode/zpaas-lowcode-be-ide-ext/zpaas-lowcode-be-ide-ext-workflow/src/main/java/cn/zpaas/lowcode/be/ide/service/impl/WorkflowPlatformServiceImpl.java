package cn.zpaas.lowcode.be.ide.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.ide.domain.service.WorkflowProcessResourceService;
import cn.zpaas.lowcode.be.ide.service.WorkflowPlatformService;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 服务端领域平台服务
 */
@Service
public class WorkflowPlatformServiceImpl implements WorkflowPlatformService {
	private static Logger logger = LoggerFactory.getLogger(WorkflowPlatformService.class);
	
	@Autowired
	private WorkflowProcessResourceService WorkflowProcessResourceService;
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public WorkflowProcessResource saveWorkflowProcessResource(WorkflowProcessResource workflowProcessResource) throws EngineException {
		logger.debug(JsonUtils.toJson(workflowProcessResource));
		if(workflowProcessResource == null || workflowProcessResource.getBpmnXml() == null || workflowProcessResource.getBpmnXml().trim().length() == 0) {
    		logger.error("bpmnXml is null!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "bpmnXml is null!");
    	}
		
		return this.WorkflowProcessResourceService.save(workflowProcessResource);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteWorkflowProcessResource(String resourceId, String systemId) throws EngineException {
		return this.WorkflowProcessResourceService.delete(resourceId, systemId);
	}
}
