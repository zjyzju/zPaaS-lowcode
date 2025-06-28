package cn.zpaas.lowcode.be.engine.service;

import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.ProcessDiagramInfo;


/**
 * @author zjy
 * 工作流后端领域平台服务接口类
 */
public interface BeWorkflowService {
	/**
	 * 加载流程实例的流程图信息
	 * @param processInstId
	 * @return
	 */
	public ProcessDiagramInfo loadProcessDiagramInfo(String processInstId)throws EngineException;
}
