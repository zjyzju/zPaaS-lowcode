package cn.zpaas.lowcode.be.engine.auth.impl;


import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.vo.AuthResult;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 基于用户名密码的认证管理器
 */
public class UserNamePasswordAuthenticateManager extends BaseAuthenticateManager {
	public static Logger logger = LoggerFactory.getLogger(UserNamePasswordAuthenticateManager.class);
	
	public static final String HEADER_KEY_USERNAME = "X-AUTH-HEADER-USERNAME";//Header中存放用户名的默认Key，优先使用BusinessSystemAuth.authConfig.userNameKey
	public static final String HEADER_KEY_PASSWORD = "X-AUTH-HEADER-PASSWORD";//Header中存放密码的默认Key，优先使用BusinessSystemAuth.authConfig.passwordKey
	public static final String HEADER_KEY_REQUEST_TIME = "X-AUTH-HEADER-REQUEST-TIME";//Header中存放请求时间的默认Key，优先使用BusinessSystemAuth.authConfig.requestTimeKey
	
	//zjy
	public static final String AUTH_CONFIG_USERNAME_KEY = "userNameKey";//authConfig中配置用户名的Key
	//MD5Utils.encode("zjy" + MD5Utils.encode("123456") +"1667290982993")
	//ba5c78066bbcf83c8e2dae6ad4afdaac
	public static final String AUTH_CONFIG_PASSWORD_KEY = "passwordKey";//authConfig中配置密码的Key
	
	private static UserNamePasswordAuthenticateManager authenticateManager = new UserNamePasswordAuthenticateManager();
	
	/**
	 * 实现单例
	 * @return
	 */
	public static UserNamePasswordAuthenticateManager instance() {
		return authenticateManager;
	}
	
	//私有构造函数
	private UserNamePasswordAuthenticateManager() {
		
	}

	@Override
	public AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException {
		if(auth == null) {
			logger.error("Authenticate failed! businessSystemAuth is null!");
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed! businessSystemAuth is null!");
		}
		//处理userNameKey、passwordKey、requestTimeKey的配置默认值和配置值，优先使用配置的值
		String userNameKey = HEADER_KEY_USERNAME;
		String passwordKey = HEADER_KEY_PASSWORD;
		String requestTimeKey = HEADER_KEY_REQUEST_TIME;
		if(auth.getAuthConfig() != null && auth.getAuthConfig().trim().length() > 0) {
			JsonObject authConfig = JsonUtils.toJsonObject(auth.getAuthConfig());
			if(authConfig.get(AUTH_CONFIG_USERNAME_KEY) != null && JsonUtils.getString(authConfig, AUTH_CONFIG_USERNAME_KEY).trim().length() > 0) {
				userNameKey = JsonUtils.getString(authConfig, AUTH_CONFIG_USERNAME_KEY);
			}
			if(authConfig.get(AUTH_CONFIG_PASSWORD_KEY) != null && JsonUtils.getString(authConfig, AUTH_CONFIG_PASSWORD_KEY).trim().length() > 0) {
				passwordKey = JsonUtils.getString(authConfig, AUTH_CONFIG_PASSWORD_KEY);
			}
			if(authConfig.get(AUTH_CONFIG_REQUEST_TIME_KEY) != null && JsonUtils.getString(authConfig, AUTH_CONFIG_REQUEST_TIME_KEY).trim().length() > 0) {
				requestTimeKey = JsonUtils.getString(authConfig, AUTH_CONFIG_REQUEST_TIME_KEY);
			}
		}
		
		//尝试从Header中获取用户名、密码、请求时间信息
		String userName = request.getHeader(userNameKey);
		String password = request.getHeader(passwordKey);
		String requestTime = request.getHeader(requestTimeKey);
		//如果未获取到，则认为未登录
		if(userName == null || userName.trim().length() == 0
				|| password == null || password.trim().length() == 0
				|| requestTime == null || requestTime.trim().length() == 0) {
			logger.error("T[{}] Authenticate failed: not found userName, password or requestTime in http header!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: not found userName, password or requestTime in http header!");
		}
		
		// 未配置完整的userInfoService和userInfoMethod，则直接抛出异常
		if (auth.getUserInfoService() == null || auth.getUserInfoService().trim().length() == 0
				|| auth.getUserInfoMethod() == null || auth.getUserInfoMethod().trim().length() == 0) {
			logger.error("T[{}] Authenticate failed: not config serInfoService and userInfoMethod!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: not config serInfoService and userInfoMethod!");
		}
		
		// 构建服务调用命令对象
		JsonObject reqData = new JsonObject();
		reqData.addProperty(userNameKey, userName);
		reqData.addProperty(passwordKey, password);
		reqData.addProperty(requestTimeKey, requestTime);
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
		JsonObject userInfo = null;
		try {
			userInfo = (JsonObject) applicationServiceProxy.execute(serviceCommand, null);
		} catch (EngineException e) {
			logger.error("T[{}] authenticate username and password failed! {}", auth.getTenantId(), e.getMessage());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "authenticate username and password failed!", e.getMessage(), e);
		}

		if (userInfo == null) {
			logger.error("T[{}] Authenticate failed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] authenticate: use userInfo loaded by authenticate {}.{}!", auth.getTenantId(), auth.getUserInfoService(), auth.getUserInfoMethod());
		}
		// 处理userInfoKey的配置默认值和配置值，优先使用配置的值
		String userInfoKey = HEADER_KEY_USER_INFO;
		if (auth.getUserInfoKey() != null && auth.getUserInfoKey().trim().length() > 0) {
			userInfoKey = auth.getUserInfoKey();
		}
		String authResult = JsonUtils.getString(userInfo, AUTH_RESULT_KEY);
		
		AuthResult returnResult = new AuthResult();
		returnResult.setAuthResult(authResult);
		
		if(AuthResult.AUTH_RESULT_SUCCEED.equals(authResult)) {
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_CLIENT_ADDR_KEY, LoginInfoUtils.getClientAddr(request));
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_SESSION_ID_KEY, request.getSession().getId());
			SecurityContextHolder.setUserInfo(userInfo, userInfoKey);
		}else if(AuthResult.AUTH_RESULT_NEED_MODIFY.equals(authResult)) {
			String userIdPath = USER_ID_PATH;
			if(auth.getUserIdAttr() != null && auth.getUserIdAttr().trim().length() > 0) {
				userIdPath = auth.getUserIdAttr();
			}
			String userId = (String)JsonUtils.eval(userInfo, userIdPath);
			if (userId == null || userId.trim().length() == 0) {
				logger.error("T[{}] Authenticate failed: load userId by authenticate failed!", auth.getTenantId());
				throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed! load userId failed!");
			}
			returnResult.setUserId(userId);
		}else if(AuthResult.AUTH_RESULT_LOCKED.equals(authResult)) {
			returnResult.setMsg("user is locked");
		}else {
			returnResult.setMsg("invalid username or password");
		}
		
		return returnResult;
	}
}
