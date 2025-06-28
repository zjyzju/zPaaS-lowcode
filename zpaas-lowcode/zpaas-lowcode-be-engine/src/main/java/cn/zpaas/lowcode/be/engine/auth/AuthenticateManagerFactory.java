package cn.zpaas.lowcode.be.engine.auth;

import cn.zpaas.lowcode.be.engine.auth.impl.AccessKeyAuthenticateManager;
import cn.zpaas.lowcode.be.engine.auth.impl.HeaderAuthenticateManager;
import cn.zpaas.lowcode.be.engine.auth.impl.SessionAuthenticateManager;
import cn.zpaas.lowcode.be.engine.auth.impl.UserNamePasswordAuthenticateManager;
import cn.zpaas.lowcode.constant.AuthTypes;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *	认证管理器工厂类
 */
public class AuthenticateManagerFactory {
	/**
	 * 创建认证管理器的工厂方法
	 * @param authType
	 * @return
	 * @throws EngineException
	 */
	public static AuthenticateManager create(String authType) throws EngineException{
		switch(authType) {
			case AuthTypes.AUTH_TYPE_SESSION:
				return SessionAuthenticateManager.instance();
				
			case AuthTypes.AUTH_TYPE_HEADER:
				return HeaderAuthenticateManager.instance();
				
			case AuthTypes.AUTH_TYPE_ACCESS_KEY:
				return AccessKeyAuthenticateManager.instance();
				
			case AuthTypes.AUTH_TYPE_USER_PWD:
				return UserNamePasswordAuthenticateManager.instance();
				
			case AuthTypes.AUTH_TYPE_SSO:
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "not implemented!");
				
			default:
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid auth type!");
		}
	}
}
