package cn.zpaas.lowcode.be.ide.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.zpaas.lowcode.be.ide.service.WorkflowPlatformService;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.vo.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zjy
 * 工作流平台管理API控制器
 */
@RestController
@RequestMapping("/platform/be/api/")
public class BeWorkflowPlatformController {
	public static final Logger logger = LoggerFactory.getLogger(BeWorkflowPlatformController.class);

	//登录信息存放的Key
	@Value("${init.param.loginInfoKey:loginInfo}")
	private  String loginInfoKey;

	@Autowired
	private WorkflowPlatformService platformService;
	
	@RequestMapping("workflowProcessResource/add")
	public ResponseResult<WorkflowProcessResource> saveWorkflowProcessResource(@RequestBody WorkflowProcessResource workflowProcessResource, HttpServletRequest request)throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, workflowProcessResource.getSystemId(), workflowProcessResource.getTenantId());
		return ResponseResult.success(platformService.saveWorkflowProcessResource(workflowProcessResource));
	}
	
	@RequestMapping("workflowProcessResource/save")
	public ResponseResult<WorkflowProcessResource> updateWorkflowProcessResource(@RequestBody WorkflowProcessResource workflowProcessResource, HttpServletRequest request)throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, workflowProcessResource.getSystemId(), workflowProcessResource.getTenantId());
		return ResponseResult.success(platformService.saveWorkflowProcessResource(workflowProcessResource));
	}
	
	@RequestMapping("workflowProcessResource/delete/{systemId}/{resourceId}")
	public ResponseResult<Integer> deleteWorkflowProcessResource(@PathVariable String systemId, @PathVariable String resourceId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.deleteWorkflowProcessResource(resourceId, systemId));
	}
}
