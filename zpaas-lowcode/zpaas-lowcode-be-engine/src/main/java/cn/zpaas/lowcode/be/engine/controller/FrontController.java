package cn.zpaas.lowcode.be.engine.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.be.engine.ability.PageAbility;
import cn.zpaas.lowcode.be.engine.auth.AuthenticateManager;
import cn.zpaas.lowcode.be.engine.auth.AuthenticateManagerFactory;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.RequestCommand;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.DictProxy;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.be.engine.registry.ServiceRegistry;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.CustomizedSseEmitter;
import cn.zpaas.lowcode.bean.PageParam;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.FileUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.AuthResult;
import cn.zpaas.lowcode.vo.ListResult;
import cn.zpaas.lowcode.vo.Result;
import cn.zpaas.lowcode.constant.AuthTypes;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAutoLoad;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.ThirdpartySystem;
import cn.zpaas.lowcode.exception.EngineException;


/**
 * @author zjy
 * 低代码平台引擎中的服务调用请求响应统一Controller
 */
@RestController
@RequestMapping
@DependsOn("springContextUtils")
public class FrontController {
	private final Logger logger = LoggerFactory.getLogger(FrontController.class);
	
	private static final String REQ_DATA_KEY = "reqData";
	private static final String REQ_PARAMS = "params";
	private static final String PAGE_PARAM_KEY = "pageParam";
	private static final String HTTP_METHOD_KEY = "httpMethod";
	private static final String REQ_URL_KEY = "reqUrl";
	private static final String BATCH_REQUEST_KEY = "batchRequest";
	private static final String MINUS_FLAG = "-";
	private static final String SLASH_FLAG = "/";
	private static final String EMPTY_JSON = "{}";
	
	//业务系统Map，FrontController可以处理多个业务系统的请求，并按业务系统进行业务逻辑的隔离，Key为业务的servletContext
	private Map<String, BusinessSystem> businessSystemMap = new HashMap<>();
	//业务系统Map，FrontController可以处理多个业务系统的请求，并按业务系统进行业务逻辑的隔离，Key为业务的业务系统标识
	private Map<String, BusinessSystem> businessSystemIdMap = new HashMap<>();
	//对外暴露服务的Map，结构为两层Map，第一层Map的Key为业务系统标识systemId，第二层Map的Key为服务对应的URL+httpMethod
	private Map<String, Map<String, ExposedService>> exposedServiceMap = new HashMap<>();
	//对外暴露服务的Map，结构为两层Map，第一层Map的Key为业务系统标识systemId，第二层Map的Key为exposedServiceId
	private Map<String, Map<String, ExposedService>> exposedServiceIdMap = new HashMap<>();
	
	//从配置文件中读取初始的启动参数
	@Value("${init.param.systemId}")
	private String initParamSystemId;
	@Value("${init.param.tenantId}")
	private String initParamTenantId;
	@Value("${init.param.servicePath:service}")
	private String initServicePath;
	@Value("${init.param.streamServicePath:streamService}")
	private String initStreamServicePath;
	@Value("${init.param.batchServicePath:batchService}")
	private String initBatchServicePath;
	@Value("${init.param.fileUploadServicePath:fileUploadService}")
	private String initFileUploadServicePath;
	@Value("${init.param.fileDownloadServicePath:fileDownloadService}")
	private String initFileDownloadServicePath;
	@Value("${init.param.stream.timeout:60000}")
	private String streamTimeout;
	@Value("${init.param.stream.pools:50}")
	private String streamPools;

	private ExecutorService executorService = null;
	
	
	@Autowired
	private EngineInitService initService;

	@Autowired
	private ServiceRegistry serviceRegistry;
	
	@RequestMapping("cache/reload/{systemId}")
	public synchronized void reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method for system:{} start...", systemId);
		}
		if(StringUtils.isBlank(systemId)) {
			logger.error("systemId is null!");
			return;
		}
		
		//加载指定业务系统的信息
		BusinessSystem businessSystem = initService.getBusinessSystemById(systemId);
		if(businessSystem != null) {
			//当指定业务系统的状态为非有效状态时，终止初始化操作
			if(!Status.EFF.equals(businessSystem.getStatus())) {
				return;
			}
			businessSystem.setBusinessSystemAuth(initService.getBusinessSystemAuthBySystem(businessSystem.getId()));
			if(businessSystem.getBusinessSystemAuth() != null) {
				//根据ignoreUrls初始过url匹配器列表
				businessSystem.getBusinessSystemAuth().initUrlMatchers();
				//加载第三方系统信息
				List<ThirdpartySystem> thirdpartySystems = initService.getThirdpartySystemBySystem(businessSystem.getId());
				if(!CollectionUtils.isEmpty(thirdpartySystems)) {
					businessSystem.getBusinessSystemAuth().setThirdparthSystemMap(
							thirdpartySystems.stream().collect(Collectors.toMap(ThirdpartySystem::getAccessKey, t->t)));
				}
			}
			
			//初始化系统自动加载信息
			businessSystem.setBusinessSystemAutoLoad(initService.getBusinessSystemAutoLoadBySystem(businessSystem.getId()));
			
			//清理原来的缓存
			for(BusinessSystem eo : businessSystemMap.values()) {
				if(systemId.equals(eo.getId())) {
					businessSystemMap.remove(eo.getServletContext());
					break;
				}
			}
			//放到缓存中，以业务系统的context作为Key
			businessSystemMap.put(businessSystem.getServletContext(), businessSystem);
			businessSystemIdMap.put(businessSystem.getId(), businessSystem);
			
			//加载并缓存ExposedService信息
			List<ExposedService> exposedServices = initService.listExposedServiceBySystemId(systemId);
			Map<String, ExposedService> map = new HashMap<>();
			Map<String, ExposedService> idMap = new HashMap<>();
			if(!CollectionUtils.isEmpty(exposedServices)) {
				for(ExposedService exposedService : exposedServices) {
					map.put(exposedService.getHttpMapping() + MINUS_FLAG + exposedService.getHttpMethod(), exposedService);
					idMap.put(exposedService.getId(), exposedService);
				}
			}
			exposedServiceMap.put(systemId, map);
			exposedServiceIdMap.put(systemId, idMap);
		}
		//将缓存加载的鉴权逻辑后移，防止因鉴权信息配置错误而造成无法刷新缓存的死循环
		this.authenticate(systemId, request, "/cache/reload/"+systemId);
		
		//进行服务执行代理的初始化
		ServiceProxy.reloadCache(systemId, null, initService);

		//进行字典参数管理类的初始化
		DictProxy.reloadCache(initParamSystemId, null, initService);
		
		//自动加载服务
		//调用自动加载服务
		if(businessSystem != null && businessSystem.getBusinessSystemAutoLoad() != null && 
				!StringUtils.isBlank(businessSystem.getBusinessSystemAutoLoad().getAutoLoadService()) &&
				!StringUtils.isBlank(businessSystem.getBusinessSystemAutoLoad().getAutoLoadMethod())) {
			logger.info("excute business system autoload method of {}", businessSystem.getName());
			//构建服务调用命令对象
			ServiceCommand serviceCommand = new ServiceCommand();
			serviceCommand.setSystemId(businessSystem.getId());
			serviceCommand.setTenantId(businessSystem.getTenantId());
			serviceCommand.setServiceId(businessSystem.getBusinessSystemAutoLoad().getAutoLoadService());
			serviceCommand.setOperationId(businessSystem.getBusinessSystemAutoLoad().getAutoLoadMethod());
			serviceCommand.setReqData(null);
			serviceCommand.setMultipartFileMap(null);
			SecurityContextHolder.setAuthEnabled(false);
			//调用ApplicationSerivceProxy的execute方法，获得返回结果
			ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
			try {
				applicationServiceProxy.execute(serviceCommand, null);
			} catch (Exception e) {
				logger.error("execute autoLoad method failed!", e);
			}
			logger.info("excute business system autoload method of {} finished!", businessSystem.getName());
		}

		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 从缓存缓存中获取业务系统信息
	 * @param systemId
	 * @return
	 */
	public BusinessSystem getBusinessSystem(String systemId) {
		return businessSystemIdMap.get(systemId);
	}

	/**
	 * 当新增业务系统时，用于加入缓存
	 * @param businessSystem
	 */
	public void addBusinessSystem(BusinessSystem businessSystem) {
		businessSystemIdMap.put(businessSystem.getId(), businessSystem);
		businessSystemMap.put(businessSystem.getServletContext(), businessSystem);
	}
	
	@PostConstruct
	/**
	 * FrontController类的初始化方法，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 */
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		
		if(!initServicePath.startsWith(SLASH_FLAG)) {//防止刷新缓存时重复执行
			initServicePath = SLASH_FLAG + initServicePath + SLASH_FLAG;
			initStreamServicePath = SLASH_FLAG + initStreamServicePath + SLASH_FLAG;
			initFileUploadServicePath = SLASH_FLAG + initFileUploadServicePath + SLASH_FLAG;
			initFileDownloadServicePath = SLASH_FLAG + initFileDownloadServicePath + SLASH_FLAG;
		}
		
		//只加载指定业务系统的数据
		if(!StringUtils.isBlank(initParamSystemId)) {
			//加载指定业务系统的信息
			BusinessSystem businessSystem = initService.getBusinessSystemById(initParamSystemId);
			if(businessSystem != null) {
				//当指定业务系统的状态为非有效状态时，终止初始化操作
				if(!Status.EFF.equals(businessSystem.getStatus())) {
					return;
				}
				businessSystem.setBusinessSystemAuth(initService.getBusinessSystemAuthBySystem(businessSystem.getId()));
				if(businessSystem.getBusinessSystemAuth() != null) {
					//根据ignoreUrls初始过url匹配器列表
					businessSystem.getBusinessSystemAuth().initUrlMatchers();
					//加载第三方系统信息
					List<ThirdpartySystem> thirdpartySystems = initService.getThirdpartySystemBySystem(businessSystem.getId());
					if(!CollectionUtils.isEmpty(thirdpartySystems)) {
						businessSystem.getBusinessSystemAuth().setThirdparthSystemMap(
								thirdpartySystems.stream().collect(Collectors.toMap(ThirdpartySystem::getAccessKey, t->t)));
					}
				}
				
				//初始化系统自动加载信息
				businessSystem.setBusinessSystemAutoLoad(initService.getBusinessSystemAutoLoadBySystem(businessSystem.getId()));
				
				//放到缓存中，以业务系统的context作为Key
				businessSystemMap.put(businessSystem.getServletContext(), businessSystem);
				businessSystemIdMap.put(businessSystem.getId(), businessSystem);
				
				//加载并缓存ExposedService信息
				List<ExposedService> exposedServices = initService.listExposedServiceBySystemId(initParamSystemId);
				if(!CollectionUtils.isEmpty(exposedServices)) {
					for(ExposedService exposedService : exposedServices) {
						Map<String, ExposedService> map = exposedServiceMap.get(exposedService.getSystemId());
						if(map == null) {
							map = new HashMap<>();
							exposedServiceMap.put(exposedService.getSystemId(), map);
						}
						map.put(exposedService.getHttpMapping() + MINUS_FLAG + exposedService.getHttpMethod(), exposedService);
					}
					exposedServiceIdMap = exposedServices.stream().collect(Collectors.groupingBy(ExposedService::getSystemId, Collectors.toMap(ExposedService::getId, t->t)));
				}
			}
		}else {//加载所有状态为有效的数据
			//加载并缓存业务系统以及认证信息
			List<BusinessSystem> businessSystems = initService.listBusinessSystems();
			List<BusinessSystemAuth> businessSystemAuths = initService.listBusinessSystemAuths();
			List<BusinessSystemAutoLoad> businessSystemAutoLoads = initService.listBusinessSystemAutoLoads();
			//加载第三方系统信息
			List<ThirdpartySystem> thirdpartySystems = initService.listThirdpartySystems();
			Map<String, Map<String, ThirdpartySystem>> thirdpartySystemMaps = null;
			if(!CollectionUtils.isEmpty(thirdpartySystems)) {
				thirdpartySystemMaps = thirdpartySystems.stream().collect(
						Collectors.groupingBy(ThirdpartySystem::getSystemId, Collectors.toMap(ThirdpartySystem::getAccessKey, t->t)));
			}else {
				thirdpartySystemMaps = new HashMap<>();
			}
			if(!CollectionUtils.isEmpty(businessSystems)) {
				for(BusinessSystem businessSystem: businessSystems) {
					businessSystemMap.put(businessSystem.getServletContext(), businessSystem);
					businessSystemIdMap.put(businessSystem.getId(), businessSystem);
					//处理系统认证信息
					if(businessSystemAuths != null && businessSystemAuths.size() > 0) {
						for(int i=0; i< businessSystemAuths.size(); i++) {
							BusinessSystemAuth auth = businessSystemAuths.get(i);
							if(businessSystem.getId().equals(auth.getSystemId())) {
								businessSystem.setBusinessSystemAuth(auth);
								
								if(businessSystem.getBusinessSystemAuth() != null) {
									//根据ignoreUrls初始过url匹配器列表
									businessSystem.getBusinessSystemAuth().initUrlMatchers();
									//设置第三方系统信息
									businessSystem.getBusinessSystemAuth().setThirdparthSystemMap(thirdpartySystemMaps.get(businessSystem.getId()));;
								}
								businessSystemAuths.remove(i);
								break;
							}
						}
					}
					//处理系统自动加载信息
					if(!CollectionUtils.isEmpty(businessSystemAutoLoads)) {
						for(int i=0; i< businessSystemAutoLoads.size(); i++) {
							BusinessSystemAutoLoad autoLoad = businessSystemAutoLoads.get(i);
							if(businessSystem.getId().equals(autoLoad.getSystemId())) {
								businessSystem.setBusinessSystemAutoLoad(autoLoad);
								businessSystemAutoLoads.remove(i);
								break;
							}
						}
					}
				}
			}
			
			//加载并缓存ExposedService信息
			List<ExposedService> exposedServices = initService.listExposedServices();
			if(!CollectionUtils.isEmpty(exposedServices)) {
				for(ExposedService exposedService : exposedServices) {
					Map<String, ExposedService> map = exposedServiceMap.get(exposedService.getSystemId());
					if(map == null) {
						map = new HashMap<>();
						exposedServiceMap.put(exposedService.getSystemId(), map);
					}
					map.put(exposedService.getHttpMapping() + MINUS_FLAG + exposedService.getHttpMethod(), exposedService);
				}
				exposedServiceIdMap = exposedServices.stream().collect(Collectors.groupingBy(ExposedService::getSystemId, Collectors.toMap(ExposedService::getId, t->t)));
			}
		}
		
		//进行服务执行代理的初始化
		ServiceProxy.init(initParamSystemId, initParamTenantId, initService);

		//进行字典参数管理类的初始化
		DictProxy.init(initParamSystemId, initParamTenantId, initService);
		
		//自动加载服务
		if(!MapUtils.isEmpty(businessSystemMap)) {
			businessSystemMap.values().forEach((businessSystem)-> {
				//调用自动加载服务
				if(businessSystem.getBusinessSystemAutoLoad() != null && 
						!StringUtils.isBlank(businessSystem.getBusinessSystemAutoLoad().getAutoLoadService()) &&
						!StringUtils.isBlank(businessSystem.getBusinessSystemAutoLoad().getAutoLoadMethod())) {
					logger.info("excute business system autoload method of {}", businessSystem.getName());
					//构建服务调用命令对象
					ServiceCommand serviceCommand = new ServiceCommand();
					serviceCommand.setSystemId(businessSystem.getId());
					serviceCommand.setTenantId(businessSystem.getTenantId());
					serviceCommand.setServiceId(businessSystem.getBusinessSystemAutoLoad().getAutoLoadService());
					serviceCommand.setOperationId(businessSystem.getBusinessSystemAutoLoad().getAutoLoadMethod());
					serviceCommand.setReqData(null);
					serviceCommand.setMultipartFileMap(null);
					SecurityContextHolder.setAuthEnabled(false);
					//调用ApplicationSerivceProxy的execute方法，获得返回结果
					ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
					try {
						applicationServiceProxy.execute(serviceCommand, null);
					} catch (Exception e) {
						logger.error("execute autoLoad method failed!", e);
					}
					logger.info("excute business system autoload method of {} finished!", businessSystem.getName());
				}
			});
		}

		//注册服务实例
		serviceRegistry.init();

		//流式响应线程池
		executorService = Executors.newFixedThreadPool(Integer.parseInt(streamPools));
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}
	
	@PostMapping("/${init.param.fileUploadServicePath:fileUploadService}/{systemContext}/**")
	/**
	 * 文件上传服务调用请求的统一响应入口，统一响应/fileUploadService/的请求
	 * @param params 请求数据，reqData包含的数据
	 * @param systemContext 业务系统的context
	 * @param request 
	 * @return 返回响应数据
	 */
	public Result<Object> fileUploadService(@RequestParam(REQ_DATA_KEY) String params, @PathVariable String systemContext, HttpServletRequest request) {
		//获取上传的文件对象
		StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) request;
		Map<String, List<MultipartFile>> multipartFileMap = multipartRequest.getMultiFileMap();
		
		// 获取服务调用请求的reqUrl
		String reqUrl = request.getServletPath().replace(initFileUploadServicePath + systemContext, "");
		// 获取服务调用的请求参数
		if(StringUtils.isBlank(params)) {
			params = EMPTY_JSON;
		}
		JsonObject reqData = JsonUtils.toJsonObject(params);
		// 处理框架支持的分页参数
		this.processPageParam(reqData);

		logger.info("reqUrl: {}, systemContext: {} of fileUploadService is requested.", reqUrl, systemContext);
		if (logger.isTraceEnabled()) {
			logger.trace("reqData: {}", reqData);
			logger.trace("upload files: {}", JsonUtils.toJson(multipartFileMap.keySet()));
			logger.trace("pageParam: {}", JsonUtils.toJson(PageAbility.getPageParam()));
		}

		Result<Object> result = null;

		// 根据请求路径中的systemContext，从缓存中获取到对应的业务系统信息
		BusinessSystem businessSystem = businessSystemMap.get(systemContext);
		// 如果打不到对应的业务系统，则返回404错误
		if (businessSystem == null) {
			result = new Result<>();
			logger.error("invalid system context : {}", systemContext);
			result.setStatus(ResultStatus.NOT_FOUND);
			result.setMessage("Invalid system context!");
			return result;
		}
		
		//进行认证处理
		try {
			AuthResult authResult = this.authenticate(businessSystem.getBusinessSystemAuth(), request, reqUrl, businessSystem);
			if(authResult == null) {
				result = new Result<>();
				logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
				result.setStatus(ResultStatus.NOT_LOGON_ERROR);
				result.setMessage("Authenticate failed!");
				return result;
			}else {
				if(!AuthResult.AUTH_RESULT_SUCCEED.equals(authResult.getAuthResult())) {
					result = new Result<>();
					logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
					result.setData(authResult);
					result.setStatus(ResultStatus.NOT_LOGON_ERROR);
					result.setMessage("Authenticate failed!");
					return result;
				}
			}
		} catch (EngineException e) {
			result = new Result<>();
			logger.error( e.getMessage());
			result.setStatus(ResultStatus.NOT_LOGON_ERROR);
			result.setMessage(e.getMessage());
			result.setDetailMessage(e.getDetailMessage());
			return result;
		}

		// 构建请求执行命令对象
		RequestCommand command = new RequestCommand();
		command.setHttpMethod("POST");
		command.setReqData(reqData);
		command.setReqUrl(reqUrl);
		command.setSystemId(businessSystem.getId());
		command.setTenantId(businessSystem.getTenantId());
		command.setMultipartFileMap(multipartFileMap);

		// 增加前置过滤器链以及环绕过滤器链的前置部分

		// 调用内部服务请求执行方法
		result = this.execute(command);

		this.setPageInfo(result);

		// 增加环绕过滤器链的后置部分以及后置过滤器链
		logger.info("T[{}] fileUploadService request is finished.", businessSystem.getTenantId());
		
		PageAbility.remove();
		SecurityContextHolder.removeAuthEnabled();
		SecurityContextHolder.removeUserInfo();
		return result;
	}
	
	@GetMapping("/${init.param.fileDownloadServicePath:fileDownloadService}/{systemContext}/**")
	/**
	 * 文件下载服务调用请求的统一响应入口，统一响应/fileDownloadService/的请求
	 * @param params 请求数据，reqData包含的数据
	 * @param systemContext 业务系统的context
	 * @param request 
	 * @param response
	 */
	public void fileDownloadServiceGet(@RequestParam String params, @PathVariable String systemContext, 
			HttpServletRequest request, HttpServletResponse response) throws EngineException {
		JsonObject paramsObject = JsonUtils.toJsonObject(params);
		this.fileDownloadService(paramsObject, systemContext, request, response);
	}
	
	@PostMapping("/${init.param.fileDownloadServicePath:fileDownloadService}/{systemContext}/**")
	/**
	 * 文件下载服务调用请求的统一响应入口，统一响应/fileDownloadService/的请求
	 * @param params 请求数据，reqData包含的数据
	 * @param systemContext 业务系统的context
	 * @param request 
	 * @param response
	 */
	public void fileDownloadService(@RequestBody JsonObject params, @PathVariable String systemContext, 
			HttpServletRequest request, HttpServletResponse response) throws EngineException {
		// 获取服务调用请求的reqUrl
		String reqUrl = request.getServletPath().replace(initFileDownloadServicePath + systemContext, "");
		// 获取服务调用的请求参数
		JsonObject reqData = JsonUtils.getJsonObject(params, REQ_DATA_KEY);
		// 处理框架支持的分页参数
		this.processPageParam(reqData);

		logger.info("reqUrl: {}, systemContext: {} of fileDownloadService is requested.", reqUrl, systemContext);
		if (logger.isTraceEnabled()) {
			logger.trace("reqData: {}", reqData);
			logger.trace("pageParam: {}", JsonUtils.toJson(PageAbility.getPageParam()));
		}

		// 根据请求路径中的systemContext，从缓存中获取到对应的业务系统信息
		BusinessSystem businessSystem = businessSystemMap.get(systemContext);
		// 如果打不到对应的业务系统，则返回404错误
		if (businessSystem == null) {
			logger.error("invalid system context : {}", systemContext);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid system context");
		}
		
		//进行认证处理
		AuthResult authResult = this.authenticate(businessSystem.getBusinessSystemAuth(), request, reqUrl, businessSystem);
		if(authResult == null || !AuthResult.AUTH_RESULT_SUCCEED.equals(authResult.getAuthResult())) {
			logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed!");
		}		

		// 构建请求执行命令对象
		RequestCommand command = new RequestCommand();
		command.setHttpMethod(request.getMethod());
		command.setReqData(reqData);
		command.setReqUrl(reqUrl);
		command.setSystemId(businessSystem.getId());
		command.setTenantId(businessSystem.getTenantId());
		
		// 增加前置过滤器链以及环绕过滤器链的前置部分

		// 调用内部服务请求执行方法
		Result<Object> result = this.execute(command);
		if(!ResultStatus.SUCCEED.equals(result.getStatus())) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, result.getMessage());
		}
		File tempFile = null;
		try {
			if(result != null && result.getData() != null) {
				if(result.getData() instanceof File) {
					tempFile = (File) result.getData();
					//去除文件名中附加的UUID
					Path realPath = FileUtils.removeUUIDFromFilePath(tempFile.toPath());
					//根据文件类型获取内容类型
					String contentType = request.getServletContext().getMimeType(realPath.getFileName().toString());
					if(contentType == null ) {
						contentType = CONTENT_TYPE;
					}
					
					response.setContentType(contentType);
					response.setHeader(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE);
					response.setHeader(CONTENT_DISPOSITION_KEY, String.format(CONTENT_DISPOSITION_VALUE, realPath.getFileName().toString()));
					response.setHeader(PROGMA_KEY, PROGMA_VALUE);
					response.setHeader(EXPIRES_KEY, EXPIRES_VALUE);
					
					Files.copy(tempFile.toPath(), response.getOutputStream());
					
				}else {
					logger.error("T[{}] result data is not instanceof File", businessSystem.getTenantId());
					return ;
				}
				response.flushBuffer();
			}
		} catch (Exception e) {
			logger.error("T[{}] download file failed!", businessSystem.getTenantId(), e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download file failed!", e.getMessage(), e);
		}finally {
			//删除临时文件
			if(tempFile != null) {
				LocalFileAbility.getInstance().deleteTempFile(tempFile.toPath());
			}
		}
		// 增加环绕过滤器链的后置部分以及后置过滤器链
		logger.info("T[{}] fileDownloadService request is finished.", businessSystem.getTenantId());
		
		PageAbility.remove();
		SecurityContextHolder.removeAuthEnabled();
		SecurityContextHolder.removeUserInfo();
	}
	
	private static final String CONTENT_TYPE = "application/octet-stream";//文件下载服务的内容类型
	private static final String CACHE_CONTROL_KEY = "Cache-Control";
	private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
	private static final String PROGMA_KEY = "Pragma";
	private static final String PROGMA_VALUE = "no-cache";
	private static final String EXPIRES_KEY = "Expires";
	private static final String EXPIRES_VALUE = "0";
	private static final String CONTENT_DISPOSITION_KEY = "Content-Disposition";
	private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"%s\"";
	
	@GetMapping("/${init.param.servicePath:service}/{systemContext}/**")
	/**
	 * 服务调用请求的统一响应入口，统一响应/service/的Get请求
	 * @param params 请求体数据，其中包含reqData
	 * @param systemContext 业务系统的context
	 * @param httpMethod httpMethod
	 * @param request http请求
	 * @return 返回响应数据，json格式
	 */
	public Result<Object> serviceGet(@PathVariable String systemContext, HttpMethod httpMethod, HttpServletRequest request) {
		String params = request.getParameter(REQ_PARAMS);
		JsonObject paramsObject = JsonUtils.toJsonObject(params);
		return this.service(paramsObject, systemContext, httpMethod, request);
	}
	
	@PostMapping("/${init.param.servicePath:service}/{systemContext}/**")
	/**
	 * 服务调用请求的统一响应入口，统一响应/service/的请求
	 * @param params 请求体数据，其中包含reqData
	 * @param systemContext 业务系统的context
	 * @param httpMethod httpMethod
	 * @param request http请求
	 * @return 返回响应数据，json格式
	 */
	public Result<Object> service(@RequestBody JsonObject params, @PathVariable String systemContext, HttpMethod httpMethod, HttpServletRequest request) {
		//获取服务调用请求的reqUrl
		String reqUrl = request.getServletPath().replace(initServicePath +systemContext, "");
		//获取服务调用的请求参数
		JsonObject reqData = JsonUtils.getJsonObject(params, REQ_DATA_KEY);
		//处理框架支持的分页参数
		this.processPageParam(reqData);
		
		logger.info("reqUrl: {}, systemContext: {}, httpMethod: {} is requested.", reqUrl, systemContext, httpMethod);
		if(logger.isTraceEnabled()) {
			logger.trace("reqData: {}", reqData);
			logger.trace("pageParam: {}", JsonUtils.toJson(PageAbility.getPageParam()));
		}
		
		Result<Object> result = null;
		
		//根据请求路径中的systemContext，从缓存中获取到对应的业务系统信息
		BusinessSystem businessSystem = businessSystemMap.get(systemContext);
		//如果打不到对应的业务系统，则返回404错误
		if(businessSystem == null) {
			result = new Result<>();
			logger.error("invalid system context : {}", systemContext);
			result.setStatus(ResultStatus.NOT_FOUND);
			result.setMessage("Invalid system context!");
			return result;
		}
		
		//进行认证处理
		try {
			AuthResult authResult = this.authenticate(businessSystem.getBusinessSystemAuth(), request, reqUrl, businessSystem);
			if(authResult == null) {
				result = new Result<>();
				logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
				result.setStatus(ResultStatus.NOT_LOGON_ERROR);
				result.setMessage("Authenticate failed!");
				return result;
			}else {
				if(!AuthResult.AUTH_RESULT_SUCCEED.equals(authResult.getAuthResult())) {
					result = new Result<>();
					logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
					result.setData(authResult);
					result.setStatus(ResultStatus.NOT_LOGON_ERROR);
					result.setMessage("Authenticate failed!");
					return result;
				}
			}
		} catch (EngineException e) {
			result = new Result<>();
			logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId(), e);
			result.setStatus(ResultStatus.NOT_LOGON_ERROR);
			result.setMessage(e.getMessage());
			result.setDetailMessage(e.getDetailMessage());
			return result;
		}
		
		//构建请求执行命令对象
		RequestCommand command = new RequestCommand();
		command.setHttpMethod(httpMethod.name());
		command.setReqData(reqData);
		command.setReqUrl(reqUrl);
		command.setSystemId(businessSystem.getId());
		command.setTenantId(businessSystem.getTenantId());
		
		//增加前置过滤器链以及环绕过滤器链的前置部分
		
		//调用内部服务请求执行方法
		result = this.execute(command);
		
		this.setPageInfo(result);
		
		//增加环绕过滤器链的后置部分以及后置过滤器链
		
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] service request: {} is finished.", businessSystem.getTenantId(), reqUrl);
			logger.trace("T[{}] request: {}'s result is: {}", businessSystem.getTenantId(), reqUrl, JsonUtils.toJson(result));
		}
		
		PageAbility.remove();
		SecurityContextHolder.removeAuthEnabled();
		SecurityContextHolder.removeUserInfo();
		return result;
	}

	@GetMapping("/${init.param.streamService:streamService}/{systemContext}/**")
	/**
	 * 文件下载服务调用请求的统一响应入口，统一响应/streamService/的请求
	 * @param params 请求数据，reqData包含的数据
	 * @param systemContext 业务系统的context
	 * @param request 
	 * @param response
	 */
	public SseEmitter streamServiceGet(@RequestParam String params, @PathVariable String systemContext, 
			HttpServletRequest request, HttpServletResponse response) throws EngineException {
		JsonObject paramsObject = JsonUtils.toJsonObject(params);
		return this.streamService(paramsObject, systemContext, request, response);
	}

	@PostMapping("/${init.param.streamServicePath:streamService}/{systemContext}/**")
	/**
	 * 流式响应调用请求的统一响应入口，统一响应/streamService/的请求
	 * @param params 请求数据，reqData包含的数据
	 * @param systemContext 业务系统的context
	 * @param request 
	 * @param response
	 */
	public SseEmitter streamService(@RequestBody JsonObject params, @PathVariable String systemContext, 
			HttpServletRequest request, HttpServletResponse response) throws EngineException {
		// 获取服务调用请求的reqUrl
		String reqUrl = request.getServletPath().replace(initStreamServicePath + systemContext, "");
		// 获取服务调用的请求参数
		JsonObject reqData = JsonUtils.getJsonObject(params, REQ_DATA_KEY);
		// 处理框架支持的分页参数
		this.processPageParam(reqData);

		logger.info("reqUrl: {}, systemContext: {} of streamService is requested.", reqUrl, systemContext);
		if (logger.isTraceEnabled()) {
			logger.trace("reqData: {}", reqData);
			logger.trace("pageParam: {}", JsonUtils.toJson(PageAbility.getPageParam()));
		}

		// 根据请求路径中的systemContext，从缓存中获取到对应的业务系统信息
		BusinessSystem businessSystem = businessSystemMap.get(systemContext);
		// 如果打不到对应的业务系统，则返回404错误
		if (businessSystem == null) {
			logger.error("invalid system context : {}", systemContext);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid system context");
		}
		
		//进行认证处理
		AuthResult authResult = this.authenticate(businessSystem.getBusinessSystemAuth(), request, reqUrl, businessSystem);
		if(authResult == null || !AuthResult.AUTH_RESULT_SUCCEED.equals(authResult.getAuthResult())) {
			logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed!");
		}		

		SseEmitter sseEmitter = new CustomizedSseEmitter(Long.parseLong(streamTimeout)); // 延长超时时间
		
		// 构建请求执行命令对象
		RequestCommand command = new RequestCommand();
		command.setHttpMethod(request.getMethod());
		command.setReqData(reqData);
		command.setReqUrl(reqUrl);
		command.setSystemId(businessSystem.getId());
		command.setTenantId(businessSystem.getTenantId());
		command.setSseEmitter(sseEmitter);
		
		// 增加前置过滤器链以及环绕过滤器链的前置部分

		// 调用内部服务请求执行方法
		final boolean authEnabled = SecurityContextHolder.isAuthEnabled();
		final String userInfoKey = SecurityContextHolder.getUserInfoKey();
		final JsonObject userInfo = SecurityContextHolder.getUserInfo();
		final PageParam pageParam = PageAbility.getPageParam();
		executorService.submit(()-> {
			SecurityContextHolder.setAuthEnabled(authEnabled);
			SecurityContextHolder.setUserInfo(userInfo, userInfoKey);
			PageAbility.setPageParam(pageParam);
			this.execute(command);
			// 增加环绕过滤器链的后置部分以及后置过滤器链
			logger.info("T[{}] streamService request is finished.", businessSystem.getTenantId());
			
			PageAbility.remove();
			SecurityContextHolder.removeAuthEnabled();
			SecurityContextHolder.removeUserInfo();
		});
		PageAbility.remove();
		SecurityContextHolder.removeAuthEnabled();
		SecurityContextHolder.removeUserInfo();
		return sseEmitter;
	}
	
	/**
	 * 处理分页数据
	 * @param reqData
	 */
	private void processPageParam(JsonObject reqData) {
		try {
			if(reqData != null) {
				JsonObject pageParamJsonObject = JsonUtils.getJsonObject(reqData, PAGE_PARAM_KEY);
				reqData.remove(PAGE_PARAM_KEY);
				if(pageParamJsonObject != null) {				
						PageParam pageParam = JsonUtils.fromJson(JsonUtils.toJson(pageParamJsonObject), PageParam.class);
						if(pageParam.getTotal() == null) {//总数为空时，需要查询总数
							pageParam.setQueryTotal(true);
						}
						PageAbility.setPageParam(pageParam);						
				}else {
					PageAbility.setPageParam(null);
				}
			} else {
				PageAbility.setPageParam(null);
			}
		} catch (Exception e) {
			PageAbility.setPageParam(null);
			if(logger.isDebugEnabled()) {
				logger.debug("no PageParam info found!");
			}			
		}	
	}
	
	/**
	 * 设置并清除分页信息
	 * @param result
	 */
	private void setPageInfo(Result<Object> result) {
		if(result != null && PageAbility.getPageParam() != null && PageAbility.getPageParam().isQueryTotal()) {//需要查询总数时，返回分页信息
			result.setPageParam(PageAbility.getPageParam());
		}
		//清除ThreadLocal中的分页对象
		PageAbility.setPageParam(null);
	}
	
	/**
	 * 获取对应ExposedService对象的方法
	 * @param systemId 业务系统标识
	 * @param reqUrl 请求URL
	 * @param httpMethod httpMethod
	 * @return 返回对应的ExposedService对象
	 */
	private ExposedService getExposedService(String systemId, String reqUrl, String httpMethod) {
		//获取业务系统对应的map
		Map<String, ExposedService> map = this.exposedServiceMap.get(systemId);
		//如果取不到有效的map，则直接返回null
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		
		return map.get(reqUrl + MINUS_FLAG + httpMethod);
	}

	/**
	 * 获取暴露服务信息
	 * @param systemId
	 * @param exposeServiceId
	 * @return
	 */
	public ExposedService getExposedService(String systemId, String exposeServiceId) {
		//获取业务系统对应的map
		Map<String, ExposedService> map = this.exposedServiceIdMap.get(systemId);
		//如果取不到有效的map，则直接返回null
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		
		return map.get(exposeServiceId);
	}
	
	/**
	 * 进行认证处理
	 * @param auth
	 * @param request
	 * @return
	 * @throws EngineException
	 */
	private AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request, BusinessSystem businessSystem) throws EngineException{
		return this.authenticate(auth, request, null, businessSystem);
	}

	/**
	 * 进行认证处理
	 * @param auth
	 * @param request
	 * @return
	 * @throws EngineException
	 */
	public AuthResult authenticate(String systemId, HttpServletRequest request, String reqUrl) throws EngineException{
		BusinessSystem businessSystem = this.businessSystemIdMap.get(systemId);
		if(businessSystem == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "invalid systemId!");
		}
		return this.authenticate(businessSystem.getBusinessSystemAuth(), request, reqUrl, businessSystem);
	}
	
	/**
	 * 进行认证处理
	 * @param auth
	 * @param request
	 * @return
	 * @throws EngineException
	 */
	private AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request, String reqUrl, BusinessSystem businessSystem) throws EngineException{
		AuthResult result = new AuthResult();
		result.setAuthResult(AuthResult.AUTH_RESULT_SUCCEED);
		//为空时默认为无权限控制模式，此时不需要做任何处理
		if(auth == null || AuthTypes.AUTH_TYPE_NONE.equals(auth.getAuthType()) || StringUtils.isBlank(auth.getAuthType())) {
			if(logger.isDebugEnabled()) {
				logger.debug("T[{}] authenticate: no auth or auth type is none. continue...", businessSystem.getTenantId());
			}
			SecurityContextHolder.setAuthEnabled(false);//设置认证模式激活状态为未激活
			return result;
		}
		
		//创建认证管理器
		AuthenticateManager authenticateManager = AuthenticateManagerFactory.create(auth.getAuthType());
		
		//检查并处理登出操作
		authenticateManager.logoutCheck(auth, request, reqUrl);

		//处理忽略认证处理的URL
		if(!CollectionUtils.isEmpty(auth.getIgnoreUrlMatchers())) {
			for(RequestMatcher matcher : auth.getIgnoreUrlMatchers()) {
				if(matcher.matches(request)) {//只要匹配上一个就可以
					if(logger.isDebugEnabled()) {
						logger.debug("T[{}] authenticate: the url mathch ignore urls. continue...", auth.getTenantId());
					}
					SecurityContextHolder.setAuthEnabled(false);//设置认证模式激活状态为未激活
					return result;
				}
			}
		}
		
		//设置认证模式激活状态为激活
		logger.info("T[{}] authenticate: auth is required. auth type is {}", auth.getTenantId(), auth.getAuthType());
		
		SecurityContextHolder.setAuthEnabled(true);
		
		//登录检测未通过
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] authenticate: check session's userInfo...", auth.getTenantId());
		}
		
		if(!authenticateManager.loginCheck(auth, request)) {
			//登录认证失败
			if(logger.isDebugEnabled()) {
				logger.debug("T[{}] authenticate: session's userInfo not found. try autheticate...", auth.getTenantId());
			}
			result = authenticateManager.authenticate(auth, request);
			if(result == null) {
				logger.error("T[{}] Authenticate failed!", auth.getTenantId());
				throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed!");
			}else {
				if(AuthResult.AUTH_RESULT_SUCCEED.equals(result.getAuthResult())) {
					//认证通过后，将用户信息设置到Session中
					authenticateManager.setUserInfo(auth, request);
				}
			}
			return result;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("T[{}] authenticate passed.", auth.getTenantId());
		}
		return result;
	}
	
	/**
	 * 服务请求执行方法，通过ServiceProxy执行客户端的服务调用请求。
	 * @param command 请求执行命令实例
	 * @return 返回服务执行结果Result<JsonObject>
	 */
	private Result<Object> execute(RequestCommand command) {
		//获取请求要调用的ExposedService
		ExposedService exposedService = this.getExposedService(command.getSystemId(), command.getReqUrl(), command.getHttpMethod());
		
		Result<Object> result = new Result<>();
		
		if(exposedService == null) {
			logger.error("T[{}] invalid request url: {}", command.getTenantId(), command.getReqUrl());
			result.setStatus(ResultStatus.NOT_FOUND);
			result.setMessage("invalid request url!");
			return result;
		}
		
		//构建服务调用命令对象
		ServiceCommand serviceCommand = new ServiceCommand();
		serviceCommand.setSystemId(command.getSystemId());
		serviceCommand.setTenantId(command.getTenantId());
		serviceCommand.setServiceId(exposedService.getServiceId());
		serviceCommand.setOperationId(exposedService.getOperationId());
		serviceCommand.setReqData(command.getReqData());
		serviceCommand.setMultipartFileMap(command.getMultipartFileMap());
		serviceCommand.setSseEmitter(command.getSseEmitter());
		
		//调用ApplicationSerivceProxy的execute方法，获得返回结果
		ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
		Object returnData = null;
		try {
			returnData = applicationServiceProxy.execute(serviceCommand, null);
		} catch (EngineException e) {
			result.setStatus(e.getStatus());
			result.setMessage(e.getMessage());
			result.setDetailMessage(e.getDetailMessage());
			return result;
		}
		
		//返回成功调用结果
		result.setStatus(ResultStatus.SUCCEED);
		result.setMessage("service invoke succeed!");
		result.setData(returnData);
		return result;
	}

	@RequestMapping("/${init.param.batchServicePath:batchService}/{systemContext}/**")
	/**
	 * 批量服务调用请求的统一响应入口，统一响应/batchService/的请求
	 * @param params 请求体数据，其中包含batchRequest
	 * {batchRequest: [
	 * 	{“httpMethod”: “post”, “reqUrl”: “xxxx”, “reqData”:{}},
	 * 	{“httpMethod”: “post”, “reqUrl”: “xxxx”, “reqData”:{}},
	 * 	{“httpMethod”: “post”, “reqUrl”: “xxxx”, “reqData”:{}}
	 * ]}
	 * @param systemContext 业务系统的context
	 * @param request http请求
	 * @return 返回响应数据，json格式
	 */
	public ListResult<Result<Object>> batchService(@RequestBody JsonObject params, @PathVariable String systemContext, HttpServletRequest request) {
		//获取服务调用的请求参数
		JsonArray batchRequest = JsonUtils.getJsonArray(params, BATCH_REQUEST_KEY);
		
		logger.info("batchService is requested. batchRequest : {}", batchRequest);
		
		
		ListResult<Result<Object>> batchResult = new ListResult<>();
		
		//根据请求路径中的systemContext，从缓存中获取到对应的业务系统信息
		BusinessSystem businessSystem = businessSystemMap.get(systemContext);
		//如果打不到对应的业务系统，则返回404错误
		if(businessSystem == null) {
			logger.error("invalid system context : {}", systemContext);
			batchResult.setStatus(ResultStatus.NOT_FOUND);
			batchResult.setMessage("Invalid system context!");
			return batchResult;
		}
		if(JsonUtils.isEmpty(batchRequest)) {
			logger.error("T[{}] batchRequest is empty", businessSystem.getTenantId());
			batchResult.setStatus(ResultStatus.INTERNAL_ERROR);
			batchResult.setMessage("Invalid batch request!");
			return batchResult;
		}
		
		//进行认证处理
		try {
			AuthResult authResult = this.authenticate(businessSystem.getBusinessSystemAuth(), request, businessSystem);
			if(authResult == null) {
				logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
				batchResult.setStatus(ResultStatus.NOT_LOGON_ERROR);
				batchResult.setMessage("Authenticate failed!");
				return batchResult;
			}else {
				if(!AuthResult.AUTH_RESULT_SUCCEED.equals(authResult.getAuthResult())) {
					logger.error("T[{}] Authenticate failed!", businessSystem.getTenantId());
					batchResult.setStatus(ResultStatus.NOT_LOGON_ERROR);
					batchResult.setMessage("Authenticate failed!");
					return batchResult;
				}
			}
		} catch (EngineException e) {
			logger.error("T[{}] Authenticate failed! {}", businessSystem.getTenantId(), e.getMessage());
			batchResult.setStatus(ResultStatus.NOT_LOGON_ERROR);
			batchResult.setMessage(e.getMessage());
			batchResult.setDetailMessage(e.getDetailMessage());
			return batchResult;
		}
		
		//循环处理每一个服务调用请求
		int size = batchRequest.size();
		JsonObject serviceRequest = null;
		JsonObject reqData = null;
		String reqUrl = null;
		String httpMethod = null;
		Result<Object> result = null;
		List<Result<Object>> resultList = new ArrayList<>();
		for(int i=0; i<size; i++) {
			//从batchReuest中获取单个请求的相关信息
			serviceRequest = batchRequest.get(i).getAsJsonObject();
			reqData = JsonUtils.getJsonObject(serviceRequest, REQ_DATA_KEY);
			reqUrl = JsonUtils.getString(serviceRequest, REQ_URL_KEY);
			httpMethod = JsonUtils.getString(serviceRequest, HTTP_METHOD_KEY);
			
			//处理框架支持的分页参数
			this.processPageParam(reqData);
			
			if(reqData == null || reqUrl == null || httpMethod == null) {
				logger.error("T[{}] invalid request format: {}", businessSystem.getTenantId(), serviceRequest);
				result =  new Result<>();
				result.setStatus(ResultStatus.BAD_REQUEST);
				result.setMessage("invalid request format");
			}else {
				//构建请求执行命令对象
				RequestCommand command = new RequestCommand();
				command.setHttpMethod(httpMethod);
				command.setReqData(reqData);
				command.setReqUrl(reqUrl);
				command.setSystemId(businessSystem.getId());
				command.setTenantId(businessSystem.getTenantId());
				
				//增加前置过滤器链以及环绕过滤器链的前置部分
				
				//调用内部服务请求执行方法
				result = this.execute(command);
				
				//分页逻辑处理
				this.setPageInfo(result);
				//增加环绕过滤器链的后置部分以及后置过滤器链
			}
			resultList.add(result);
		}
		batchResult.setStatus(ResultStatus.SUCCEED);
		batchResult.setMessage("batch service request executed successfully!");
		batchResult.setData(resultList);
		logger.info("T[{}] batchService is finished.", businessSystem.getTenantId());
		
		PageAbility.remove();
		SecurityContextHolder.removeAuthEnabled();
		SecurityContextHolder.removeUserInfo();
		return batchResult;
	}

	public String getServicePath() {
		return initServicePath;
	}

	public String getBatchServicePath() {
		return initBatchServicePath;
	}

	public String getFileUploadServicePath() {
		return initFileUploadServicePath;
	}

	public String getFileDownloadServicePath() {
		return initFileDownloadServicePath;
	}

	public String getStreamServicePath() {
		return initStreamServicePath;
	}
}
