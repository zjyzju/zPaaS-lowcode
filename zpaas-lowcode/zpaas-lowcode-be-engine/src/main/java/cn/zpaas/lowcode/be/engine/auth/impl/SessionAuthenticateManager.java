package cn.zpaas.lowcode.be.engine.auth.impl;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.AuthResult;

/**
 * @author zjy
 * 实现基于Session的登录信息检查逻辑，不提供认证能力
 */
public class SessionAuthenticateManager extends BaseAuthenticateManager {
	public static Logger logger = LoggerFactory.getLogger(SessionAuthenticateManager.class);
	
	private static SessionAuthenticateManager authenticateManager = new SessionAuthenticateManager();
	
	/**
	 * 实现单例
	 * @return
	 */
	public static SessionAuthenticateManager instance() {
		return authenticateManager;
	}
	
	//私有构造函数
	private SessionAuthenticateManager() {
		
	}
	
	@Override
	public boolean loginCheck(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException {
		boolean pass = super.loginCheck(auth, request);
		//共享Session模式时，如果Session中未找到用户信息，直接抛出异常
		if(!pass) {
			logger.error("T[{}] user isn't logged on.", auth.getTenantId());
			throw new EngineException(ResultStatus.NOT_LOGON_ERROR, "user isn't logged on");
		}
		return pass;
	}

	@Override
	public AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException {
		//do nothing
		AuthResult returnResult = new AuthResult();
		returnResult.setAuthResult(AuthResult.AUTH_RESULT_SUCCEED);
		return returnResult;
	}
	
}
