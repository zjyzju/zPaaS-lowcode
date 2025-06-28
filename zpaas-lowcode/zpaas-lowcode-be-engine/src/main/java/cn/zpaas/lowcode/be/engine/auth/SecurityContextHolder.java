package cn.zpaas.lowcode.be.engine.auth;


import com.google.gson.JsonObject;

/**
 * @author zjy
 *	用于存放和获取用户信息
 */
public class SecurityContextHolder {
	private static ThreadLocal<JsonObject> userInfoHolder = new ThreadLocal<>();//用于存放用户信息的ThreadLocal变量
	private static ThreadLocal<String> userInfoKeyHolder = new ThreadLocal<>();//用于存放用户信息Key的ThreadLocal变量
	private static ThreadLocal<Boolean> authEnabledHolder = new ThreadLocal<>();//用于存放认证是否生效的ThreadLocal变量
	
	/**
	 * 设置Auth是否生效
	 * @param authEnabled
	 */
	public static void setAuthEnabled(Boolean authEnabled) {
		authEnabledHolder.set(authEnabled);
	}
	
	/**
	 * 判断当前Auth是否生效
	 * @return
	 */
	public static boolean isAuthEnabled() {
		if(authEnabledHolder.get() == null) {
			return false;
		}
		return authEnabledHolder.get();
	}
	
	/**
	 * 移除Auth是否生效的值
	 */
	public static void removeAuthEnabled() {
		authEnabledHolder.remove();
	}
	
	/**
	 * 存放用户信息
	 * @param userInfo
	 */
	public static void setUserInfo(JsonObject userInfo, String userInfoKey) {
		userInfoHolder.set(userInfo);
		userInfoKeyHolder.set(userInfoKey);
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public static JsonObject getUserInfo() {
		return userInfoHolder.get();
	}
	
	/**
	 * 获取用户信息Key
	 * @return
	 */
	public static String getUserInfoKey() {
		return userInfoKeyHolder.get();
	}
	
	/**
	 * 移除用户信息
	 */
	public static void removeUserInfo() {
		userInfoHolder.remove();
		userInfoKeyHolder.remove();
	}
}
