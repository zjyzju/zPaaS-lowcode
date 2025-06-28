package cn.zpaas.lowcode.fe.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.ResponseResult;
import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.fe.proxy.ComboFuncDefineProxy;
import cn.zpaas.lowcode.fe.proxy.FuncDefineProxy;
import cn.zpaas.lowcode.fe.service.FeInitService;
import cn.zpaas.lowcode.fe.service.FeService;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;

/**
 * @author zjy
 * 前端领域API控制器
 */
@RestController
@RequestMapping("/fe/api/")
@DependsOn({"frontController","biController"})

public class FeController {
	public static Logger logger = LoggerFactory.getLogger(FeController.class);

	//从配置文件中读取初始的启动参数
	@Value("${init.param.systemId}")
	private String initParamSystemId;
	@Value("${init.param.tenantId}")
	private String initParamTenantId;
	
	@Autowired
	private FeService feService;

	@Autowired
	private FeInitService initService;

	@RequestMapping("cache/reload/{systemId}")
	public synchronized void reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method for system:{} start...", systemId);
		}
		if(StringUtils.isBlank(systemId)) {
			logger.error("systemId is null!");
			return;
		}

		SpringContextUtils.getBean(FrontController.class).authenticate(systemId, request, "/cache/reload/"+systemId);

		FuncDefineProxy.reloadCache(systemId, null, initService);
		ComboFuncDefineProxy.reloadCache(systemId, null, initService);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	@PostConstruct
	/**
	 * FdmController类的初始化方法，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 */
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}

		FuncDefineProxy.init(initParamSystemId, initParamTenantId, initService);
		ComboFuncDefineProxy.init(initParamSystemId, initParamTenantId, initService);

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	@RequestMapping("funcDefine/loadAll/{funcId}")
	public ResponseResult<JsonObject> funcInit(@PathVariable String funcId, HttpServletRequest request) throws EngineException {
		//进行登录认证及鉴权处理，主要用于支持可能存在的服务方法调用的情况
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefine(funcId);
		if(funcDefine == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid funcId!");
		}
		SpringContextUtils.getBean(FrontController.class).authenticate(funcDefine.getSystemId(), request, "/funcDefine/loadAll/"+funcId);
		return ResponseResult.success(feService.funcInit(funcId));
	}

	@RequestMapping("operation/loadAll/{operationId}")
	public ResponseResult<OperationVo> loadOperationAll(@PathVariable String operationId, HttpServletRequest request) throws EngineException {
		OperationVo operationVo = FuncDefineProxy.getInstance().getOperation(operationId);
		if(operationVo == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid operationId!");
		}
		SpringContextUtils.getBean(FrontController.class).authenticate(operationVo.getSystemId(), request, "/operation/loadAll/" + operationId);
		return ResponseResult.success(feService.loadOperationAll(operationId));
	}
	
	@RequestMapping("funcDefine/loadFuncDefineAndTpl/{funcId}")
	public ResponseResult<FuncDefineVo> loadFuncDefineAndTpl(@PathVariable String funcId, HttpServletRequest request) throws EngineException {
		//进行登录认证及鉴权处理，主要用于支持可能存在的服务方法调用的情况
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefine(funcId);
		if(funcDefine == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid funcId!");
		}
		SpringContextUtils.getBean(FrontController.class).authenticate(funcDefine.getSystemId(), request, "/funcDefine/loadFuncDefineAndTpl/"+funcId);
		return ResponseResult.success(feService.loadFuncDefineAndTpl(funcId));
	}
	
	@RequestMapping("comboFuncDefine/loadAll/{comboFuncId}") 
	public ResponseResult<ComboFuncDefineVo> loadComboFuncDefineAll(@PathVariable String comboFuncId, HttpServletRequest request) throws EngineException {
		//进行登录认证及鉴权处理，主要用于支持可能存在的服务方法调用的情况
		ComboFuncDefineVo comboFuncDefine = ComboFuncDefineProxy.getInstance().getComboFuncDefine(comboFuncId);
		if(comboFuncDefine == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid comboFuncId!");
		}
		SpringContextUtils.getBean(FrontController.class).authenticate(comboFuncDefine.getSystemId(), request, "/funcDefine/loadFuncDefineAndTpl/"+comboFuncId);
		return ResponseResult.success(feService.comboFuncInit(comboFuncId));
	}
	
	@RequestMapping("funcObjectAttrOptions/getLabelWithParent/{attrOptionsId}/{attrValue}/{parentAttrValue}")
	public ResponseResult<String> loadLabelForObjectAttr(@PathVariable String attrOptionsId, @PathVariable String attrValue, 
			@PathVariable String parentAttrValue, @RequestBody JsonArray params, HttpServletRequest request)  throws EngineException{
		//进行登录认证及鉴权处理，主要用于支持可能存在的服务方法调用的情况
		this.optionsOperationAuth(attrOptionsId, request, "/funcObjectAttrOptions/getLabelWithParent/"+attrOptionsId + "/" + attrValue + "/" + parentAttrValue);
		String label = feService.loadLabelForObjectAttr(attrOptionsId, attrValue, parentAttrValue, params);
		logger.debug("get label:{} for optionId: {}", label, attrOptionsId);
		return ResponseResult.success(label);
	}
	
	@RequestMapping("funcObjectAttrOptions/getLabel/{attrOptionsId}/{attrValue}")
	public ResponseResult<String> loadLabelForObjectAttr(@PathVariable String attrOptionsId, @PathVariable String attrValue, @RequestBody JsonArray params, HttpServletRequest request) throws EngineException{
		this.optionsOperationAuth(attrOptionsId, request, "/funcObjectAttrOptions/getLabel/"+attrOptionsId + "/" + attrValue);
		return ResponseResult.success(feService.loadLabelForObjectAttr(attrOptionsId, attrValue, null, params));
	}
	
	@RequestMapping("funcObjectAttrOptions/getOptionsWithParent/{attrOptionsId}/{parentAttrValue}")
	public ResponseResult<JsonArray> loadOptionsForObjectAttr(@PathVariable String attrOptionsId, @PathVariable String parentAttrValue, @RequestBody JsonArray params, HttpServletRequest request)  throws EngineException{
		this.optionsOperationAuth(attrOptionsId, request, "/funcObjectAttrOptions/getOptionsWithParent/"+attrOptionsId + "/" + parentAttrValue);
		return ResponseResult.success(feService.loadOptionsForObjectAttr(attrOptionsId, parentAttrValue, params));
	}
	
	/**
	 * 进行选项类操作的登录鉴权
	 * @param attrOptionsId
	 * @param request
	 * @param reqUrl
	 * @throws EngineException
	 */
	private void optionsOperationAuth(String attrOptionsId, HttpServletRequest request, String reqUrl) throws EngineException{
		FuncObjectAttrOptions attrOptions = FuncDefineProxy.getInstance().getAttrOptions(attrOptionsId);
		if(attrOptions == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid attrOptionsId!");
		}
		SpringContextUtils.getBean(FrontController.class).authenticate(attrOptions.getSystemId(), request, reqUrl);
	}

	@RequestMapping("reportFunc/loadData")
	public ResponseResult<JsonArray> loadReportDatas(@RequestBody LoadReportDataReq loadReportDataReq, HttpServletRequest request) throws EngineException{
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefineAndTpl(loadReportDataReq.getFuncId());
		SpringContextUtils.getBean(FrontController.class).authenticate(funcDefine.getSystemId(), request, "/reportFunc/loadData");
		return ResponseResult.success(feService.loadReportDatas(loadReportDataReq));
	}

	@RequestMapping("chartFunc/loadData")
	public ResponseResult<JsonObject> loadChartDatas(@RequestBody LoadReportDataReq loadReportDataReq, HttpServletRequest request) throws EngineException{
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefineAndTpl(loadReportDataReq.getFuncId());
		SpringContextUtils.getBean(FrontController.class).authenticate(funcDefine.getSystemId(), request, "/reportFunc/loadData");
		return ResponseResult.success(feService.loadChartDatas(loadReportDataReq));
	}
}
