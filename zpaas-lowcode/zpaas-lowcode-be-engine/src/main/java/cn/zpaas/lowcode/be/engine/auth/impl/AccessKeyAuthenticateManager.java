package cn.zpaas.lowcode.be.engine.auth.impl;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.security.MD5Utils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.domain.eo.ThirdpartySystem;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 基于Header传递用户信息+AccessKey认证的认证管理器
 */
public class AccessKeyAuthenticateManager extends HeaderAuthenticateManager {
	public static Logger logger = LoggerFactory.getLogger(AccessKeyAuthenticateManager.class);
	
	// e0158ec6d96e4dd5b346b72a6c2b54ec
	public static final String HEADER_KEY_ACCESS_KEY = "X-AUTH-HEADER-ACCESS-KEY"; // Header中存放accessKey的默认Key，优先使用BusinessSystemAuth.authConfig.accessKeyKey
	// 4c11679403b3d0054b6fe9ceec14bf88 //accessSecret: 38778f8eff8d42e19de230901cb54884
	public static final String HEADER_KEY_ACCESS_TOKEN = "X-AUTH-HEADER-ACCESS-TOKEN"; //Header中存放accessToken的默认Key，优先使用BusinessSystemAuth.authConfig.accessTokenKey
		
	public static final String AUTH_CONFIG_ACCESS_KEY_KEY = "accessKeyKey";//authConfig中配置accessKey的Key
	public static final String AUTH_CONFIG_ACCESS_TOKEN_KEY = "accessTokenKey";//authConfig中配置accessToken的Key
	
	private static AccessKeyAuthenticateManager authenticateManager = new AccessKeyAuthenticateManager();

	/**
	 * 实现单例
	 * 
	 * @return
	 */
	public static AccessKeyAuthenticateManager instance() {
		return authenticateManager;
	}

	// 私有构造函数
	private AccessKeyAuthenticateManager() {

	}

	
	
	@Override
	protected boolean securityCheck(BusinessSystemAuth auth, HttpServletRequest request, JsonObject userInfo) throws EngineException {
		//处理accessKeyKey、accessTokenKey、requestTimeKey的配置默认值和配置值，优先使用配置的值
		String accessKeyKey = HEADER_KEY_ACCESS_KEY;
		String accessTokenKey = HEADER_KEY_ACCESS_TOKEN;
		String requestTimeKey = HEADER_KEY_REQUEST_TIME;
		if (!StringUtils.isBlank(auth.getAuthConfig())) {
			JsonObject authConfig = JsonUtils.toJsonObject(auth.getAuthConfig());
			if(authConfig != null) {
				if(authConfig.has(AUTH_CONFIG_ACCESS_KEY_KEY)) {
					accessKeyKey = JsonUtils.getString(authConfig, AUTH_CONFIG_ACCESS_KEY_KEY);
				}
				if(authConfig.has(AUTH_CONFIG_ACCESS_TOKEN_KEY)) {
					accessTokenKey = JsonUtils.getString(authConfig, AUTH_CONFIG_ACCESS_TOKEN_KEY);
				}
				if(authConfig.has(AUTH_CONFIG_REQUEST_TIME_KEY)) {
					requestTimeKey = JsonUtils.getString(authConfig, AUTH_CONFIG_REQUEST_TIME_KEY);
				}
			}
		}
		// 尝试从Header中获取AccessKey相关信息
		String accessKey = request.getHeader(accessKeyKey);
		String accessToken = request.getHeader(accessTokenKey);
		String requestTime = request.getHeader(requestTimeKey);
		// 如果未获取到，则认为未登录
		if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(accessToken) || StringUtils.isBlank(requestTime)) {
			logger.error("T[{}] Authenticate failed: not found accessKey accessToken and requestTime in http header!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: not found accessKey accessToken and requestTime in http header!");
		}
		
		// 处理userIdAttr的配置默认值和配置值，优先使用配置的值
		String userIdAttr = USER_ID_PATH;
		if (!StringUtils.isBlank(auth.getUserIdAttr())) {
			userIdAttr = auth.getUserIdAttr();
		}

		// 尝试获取userId的值
		String userId = (String) JsonUtils.eval(userInfo, userIdAttr);
		if (StringUtils.isBlank(userId)) {
			logger.error("T[{}] Authenticate failed: get userId from UserInfo failed!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: get userId from UserInfo failed!");
		}
		
		//根据accessKey获取accessToken
		if(MapUtils.isEmpty(auth.getThirdparthSystemMap())) {
			logger.error("T[{}] Authenticate failed: invalid accessKey!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: invalid accessKey!");
		}
		ThirdpartySystem thirdpartySystem = auth.getThirdparthSystemMap().get(accessKey);
		if(thirdpartySystem == null || StringUtils.isBlank(thirdpartySystem.getAccessSecret())) {
			logger.error("T[{}] Authenticate failed: invalid accessKey!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: invalid accessKey!");
		}
		
		//对accessToken进行合法性校验
		String validateString = new StringBuilder().append(accessKey).append(thirdpartySystem.getAccessSecret()).
				append(requestTime).append(userId).toString();		
		if(!MD5Utils.isValid(accessToken, validateString)) {
			logger.error("T[{}] Authenticate failed: invalid accessToken!", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "Authenticate failed: invalid accessToken!");
		}
		
		return super.securityCheck(auth, request, userInfo);
	}
	
}
