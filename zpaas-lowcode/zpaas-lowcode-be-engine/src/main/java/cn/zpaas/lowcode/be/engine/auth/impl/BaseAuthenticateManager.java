package cn.zpaas.lowcode.be.engine.auth.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.be.engine.auth.AuthenticateManager;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 认证管理器基类
 */
public abstract class BaseAuthenticateManager implements AuthenticateManager {
	public static Logger logger = LoggerFactory.getLogger(BaseAuthenticateManager.class);
	
	//"{\"userId\":\"11111\"}"//%7B%22userId%22%3A%2211111%22%7D
	public static final String SESSION_KEY_USER_INFO = "SESSION-KEY-USER-INFO";//Session中存放用户信息的默认Key，优先使用BusinessSystemAuth.userInfoKey
	public static final String HEADER_KEY_USER_INFO = "X-AUTH-HEADER-USER";//Header中存放用户信息的默认Key，优先使用BusinessSystemAuth.userInfoKey
	//1667290982993
	public static final String HEADER_KEY_REQUEST_TIME = "X-AUTH-HEADER-REQUEST-TIME"; //Header中存放requestTime的默认Key，优先使用BusinessSystemAuth.authConfig.requestTimeKey
	
	//1667290982993
	public static final String AUTH_CONFIG_REQUEST_TIME_KEY = "requestTimeKey";//authConfig中配置请求时间的Key
	
	public static final String UTF_8 = "UTF-8";
	
	public static final String LOGOUT_URL = "/logout";//默认的登出url：logout
	public static final String LOGOUT_URL_KEY = "logoutUrl";//配置的logoutUrl的Key
	
	public static final String USER_ID_KEY = "userId";//存放用户标识的Key
	public static final String USER_ID_PATH = "loginUser.id";//存放用户标识的jsonpath
	public static final String AUTH_RESULT_KEY = "authResult";//存放鉴权结果的Key，结果包括：0：失败；1：成功；2：需要修改密码；3：账号锁定；

	public static final String CLIENT_ADDR_KEY = "clientAddr";

	
	@Override
	public boolean loginCheck(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException{
		if(auth == null) {
			logger.error("businessSystemAuth is null!");
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "businessSystemAuth is null!");
		}
		//处理userInfoKey的配置默认值和配置值，优先使用配置的值
		String userInfoKey = SESSION_KEY_USER_INFO;
		if(auth.getUserInfoKey() != null && auth.getUserInfoKey().trim().length() > 0) {
			userInfoKey = auth.getUserInfoKey();
		}
		//尝试从Session中获取UserInfo信息
		HttpSession session = request.getSession();
		Object userInfoObject = session.getAttribute(userInfoKey);
		
		//存在用户信息的情况下，将用户信息对象转换成JsonObject类型，存放到SecurityContextHolder中
		if(userInfoObject != null) {
			JsonObject userInfo = JsonUtils.toJsonObject(JsonUtils.toJson(userInfoObject));
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_CLIENT_ADDR_KEY, LoginInfoUtils.getClientAddr(request));
			userInfo.addProperty(LoginInfoUtils.LOGIN_USER_SESSION_ID_KEY, session.getId());
			SecurityContextHolder.setUserInfo(userInfo, userInfoKey);
			return true;
		}
		return false;
	}

	@Override
	public void setUserInfo(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException{
		if(auth == null) {
			logger.error("businessSystemAuth is null!");
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "businessSystemAuth is null!");
		}
		//尝试UserInfo信息保存到Session中
		HttpSession session = request.getSession();
		if(SecurityContextHolder.isAuthEnabled()) {
			if(logger.isDebugEnabled()) {
				logger.debug("T[{}] add userInfo to Session!", auth.getTenantId());
			}
			session.setAttribute(SecurityContextHolder.getUserInfoKey(), SecurityContextHolder.getUserInfo());
		}
	}
	
	@Override
	public void logoutCheck(BusinessSystemAuth auth, HttpServletRequest request, String reqUrl) throws EngineException{
		if(auth == null) {
			logger.error("businessSystemAuth is null!");
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "businessSystemAuth is null!");
		}
		//处理登出url的配置默认值和配置值，优先使用配置的值
		String logoutUrl = LOGOUT_URL;
		if(auth.getAuthConfig() != null && auth.getAuthConfig().trim().length() > 0) {
			JsonObject authConfig = JsonUtils.toJsonObject(auth.getAuthConfig());
			if(authConfig.get(LOGOUT_URL_KEY) != null && JsonUtils.getString(authConfig, LOGOUT_URL_KEY).trim().length() > 0) {
				logoutUrl = JsonUtils.getString(authConfig, LOGOUT_URL_KEY);
			}
		}
		if(!logoutUrl.equals(reqUrl)) {
			return ;
		}
		
		// 处理userInfoKey的配置默认值和配置值，优先使用配置的值
		String userInfoKey = SESSION_KEY_USER_INFO;
		if (auth.getUserInfoKey() != null && auth.getUserInfoKey().trim().length() > 0) {
			userInfoKey = auth.getUserInfoKey();
		}
		//从Session中删除登录信息
		HttpSession session = request.getSession();
		session.removeAttribute(userInfoKey);
		logger.debug("T[{}] user is logged out!", auth.getTenantId());
		
		return ;
	}
}
