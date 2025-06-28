package cn.zpaas.lowcode.be.engine.auth;

import jakarta.servlet.http.HttpServletRequest;

import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.AuthResult;

/**
 * @author zjy
 * 认证管理器接口定义
 */
public interface AuthenticateManager {
	
	/**
	 * 在Session中检查是否已经登录，如果已经登录则将用户信息存放到SecurityContextHolder中
	 * @param auth
	 * @param request
	 * @return
	 */
	public boolean loginCheck(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException;
	
	/**
	 * 进行用户信息认证，如果认证通过则将用户信息存放到SecurityContextHolder中
	 * @param auth
	 * @param request
	 * @return
	 * @throws EngineException
	 */
	public AuthResult authenticate(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException;
	
	/**
	 * 将用户信息设置到Session中
	 * @param auth
	 * @param request
	 * @throws EngineException
	 */
	public void setUserInfo(BusinessSystemAuth auth, HttpServletRequest request) throws EngineException;
	
	/**
	 * 检查是否登出操作，并在Session中失效登录信息
	 * @param auth
	 * @param request
	 * @param reqUrl
	 */
	public void logoutCheck(BusinessSystemAuth auth, HttpServletRequest request, String reqUrl) throws EngineException;
}
