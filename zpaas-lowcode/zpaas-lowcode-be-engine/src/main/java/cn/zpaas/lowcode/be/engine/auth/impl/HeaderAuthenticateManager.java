package cn.zpaas.lowcode.be.engine.auth.impl;

import java.net.URLDecoder;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.AuthResult;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 基于Header传递用户信息的认证管理器
 */
public class HeaderAuthenticateManager extends BaseAuthenticateManager {
	public static Logger logger = LoggerFactory.getLogger(HeaderAuthenticateManager.class);
	
	private static HeaderAuthenticateManager authenticateManager = new HeaderAuthenticateManager();
	
	/**
	 * 实现单例
	 * @return
	 */
	public static HeaderAuthenticateManager instance() {
		return authenticateManager;
	}
	
	//私有构造函数
	protected HeaderAuthenticateManager() {
		
	}
	
	/**
	 * 对用户身份认证进行进一步安全检测
	 * @param auth
	 * @param request
	 * @param userInfo
	 * @return
	 * @throws EngineException
	 */
	protected boolean securityCheck(BusinessSystemAuth auth, HttpServletRequest request, JsonObject userInfo) throws EngineException {
		//默认实现无进一步的安全检测，直接返回true
		return true;
	}

	@Override
	public AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException {
		if(auth == null) {
			logger.error("Authenticate failed! businessSystemAuth is null!");
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed! businessSystemAuth is null!");
		}
		//处理userInfoKey的配置默认值和配置值，优先使用配置的值
		String userInfoKey = HEADER_KEY_USER_INFO;
		if(!StringUtils.isBlank(auth.getUserInfoKey())) {
			userInfoKey = auth.getUserInfoKey();
		}
		
		//尝试从Header中获取用户信息
		String userInfoString = request.getHeader(userInfoKey);
		//如果未获取到，则认为未登录
		if(StringUtils.isBlank(userInfoString)) {
			logger.error("T[{}] Authenticate failed: not found userInfo in http header!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed! not found userInfo in http header!");
		}

		// 处理userIdAttr的配置默认值和配置值，优先使用配置的值
		String userIdAttr = USER_ID_PATH;
		if (!StringUtils.isBlank(auth.getUserIdAttr())) {
			userIdAttr = auth.getUserIdAttr();
		}
		
		JsonObject userInfo = null;
		try {
			//将用户信息字符串转换成JsonObject类型的对象
			userInfo = JsonUtils.toJsonObject(URLDecoder.decode(userInfoString, UTF_8));
		} catch (Exception e) {
			logger.error("T[{}] Authenticate failed: parse userInfo String to JsonObject failed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: parse userInfo String to JsonObject failed!", e.getMessage(), e);
		}
		if (userInfo == null) {
			userInfo = new JsonObject();
			JsonUtils.set(userInfo, userIdAttr, userInfoString);
			logger.info("T[{}] userInfoString is String, init userInfo, set userInfoString as userIdAttr!", auth.getTenantId());
			// throw new EngineException(ResultStatus.NOT_LOGON_ERROR,
			// 		"Authenticate failed! parse userInfo String to JsonObject failed!");
		}
		
		//对用户信息以及本次请求进行全法性校验
		if(!securityCheck(auth, request, userInfo)) {
			logger.error("T[{}] Authenticate failed: security check of the request isn't passed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR,
					"Authenticate failed: security check of the request isn't passed!");
		}

		// 未配置完整的userInfoService和userInfoMethod，直接使用获取到的UserInfo
		if (StringUtils.isBlank(auth.getUserInfoService()) || StringUtils.isBlank(auth.getUserInfoMethod())) {
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_CLIENT_ADDR_KEY, LoginInfoUtils.getClientAddr(request));
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_SESSION_ID_KEY, request.getSession().getId());
			SecurityContextHolder.setUserInfo(userInfo, userInfoKey);
			if (logger.isDebugEnabled()) {
				logger.debug("T[{}] authenticate: userInfoService not configured. use userInfo from Header!", auth.getTenantId());
			}
			AuthResult returnResult = new AuthResult();
			returnResult.setAuthResult(AuthResult.AUTH_RESULT_SUCCEED);
			return returnResult;
		}


		// 配置完整的userInfoService和userInfoMethod的情况下，使用该方法来加载用户信息
		// 尝试获取userId的值
		String userId = (String) JsonUtils.eval(userInfo, userIdAttr);
		if (StringUtils.isBlank(userId)) {
			logger.error("T[{}] Authenticate failed: get userId from UserInfo failed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: get userId from UserInfo failed!");
		}

		// 构建服务调用命令对象
		JsonObject reqData = new JsonObject();
		reqData.add(USER_ID_KEY, JsonUtils.toJsonElement(userId));
		reqData.addProperty(CLIENT_ADDR_KEY, LoginInfoUtils.getClientAddr(request));
		ServiceCommand serviceCommand = new ServiceCommand();
		serviceCommand.setSystemId(auth.getSystemId());
		serviceCommand.setTenantId(auth.getTenantId());
		serviceCommand.setServiceId(auth.getUserInfoService());
		serviceCommand.setOperationId(auth.getUserInfoMethod());
		serviceCommand.setReqData(reqData);
		serviceCommand.setMultipartFileMap(null);

		// 调用ApplicationSerivceProxy的execute方法，获得返回结果
		ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
		try {
			userInfo = (JsonObject) applicationServiceProxy.execute(serviceCommand, null);
		} catch (EngineException e) {
			logger.error("T[{}] Authenticate failed: load userInfo by userInfoService failed!", auth.getTenantId(), e);
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: load userInfo by userInfoService failed!", e.getMessage(), e);
		}

		if (userInfo == null) {
			logger.error("T[{}] Authenticate failed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] authenticate: use userInfo loaded by authenticate {}.{}!", auth.getTenantId(), auth.getUserInfoService(), auth.getUserInfoMethod());
		}
		String authResult = JsonUtils.getString(userInfo, AUTH_RESULT_KEY);
		
		AuthResult returnResult = new AuthResult();
		returnResult.setAuthResult(authResult);
		
		if(AuthResult.AUTH_RESULT_SUCCEED.equals(authResult)) {
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_CLIENT_ADDR_KEY, LoginInfoUtils.getClientAddr(request));
			SecurityContextHolder.setUserInfo(userInfo, userInfoKey);
		}else if(AuthResult.AUTH_RESULT_NEED_MODIFY.equals(authResult)) {
			returnResult.setUserId(userId);
		}else if(AuthResult.AUTH_RESULT_LOCKED.equals(authResult)) {
			returnResult.setMsg("user is locked");
		}
		
		return returnResult;
	}
}
