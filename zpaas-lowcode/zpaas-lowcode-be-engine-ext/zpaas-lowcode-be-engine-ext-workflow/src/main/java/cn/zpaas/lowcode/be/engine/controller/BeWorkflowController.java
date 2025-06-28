package cn.zpaas.lowcode.be.engine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.zpaas.lowcode.be.engine.service.BeWorkflowService;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.ProcessDiagramInfo;
import cn.zpaas.lowcode.vo.ResponseResult;

/**
 * @author zjy
 * 前端领域API控制器
 */
@RestController
@RequestMapping("/be/api/")
public class BeWorkflowController {
	public static Logger logger = LoggerFactory.getLogger(BeWorkflowController.class);

	@Autowired
	private BeWorkflowService beService;

	@RequestMapping("workflow/loadProcessDiagramInfo/{processInstId}")
	public ResponseResult<ProcessDiagramInfo> loadProcessDiagramInfo(@PathVariable String processInstId)throws EngineException {
		return ResponseResult.success(beService.loadProcessDiagramInfo(processInstId));
	}
}
