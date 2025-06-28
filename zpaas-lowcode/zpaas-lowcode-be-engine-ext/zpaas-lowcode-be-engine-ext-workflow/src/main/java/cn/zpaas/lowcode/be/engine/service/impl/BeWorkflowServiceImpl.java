package cn.zpaas.lowcode.be.engine.service.impl;

import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.be.engine.ability.WorkflowAbility;
import cn.zpaas.lowcode.be.engine.service.BeWorkflowService;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.ProcessDiagramInfo;

/**
 * @author zjy
 * 工作流后端领域平台服务实现类
 */
@Service
public class BeWorkflowServiceImpl implements BeWorkflowService {
	@Override
	public ProcessDiagramInfo loadProcessDiagramInfo(String processInstId) throws EngineException {
		return WorkflowAbility.getInstance().loadDiagramInfo(processInstId);
	}
}
