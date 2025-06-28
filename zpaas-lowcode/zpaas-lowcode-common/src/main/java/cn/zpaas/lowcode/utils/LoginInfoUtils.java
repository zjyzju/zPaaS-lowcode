package cn.zpaas.lowcode.utils;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.exception.IllegalAccessException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录信息获取的工具类
 *
 * @author zjy
 * createTime 2025年04月22日-09:32:15
 */
public class LoginInfoUtils {

    public static final String BUSINESS_SYSTEM_GRANT_INFO_KEY = "businessSystemGrants";//业务系统授权信息存放的key
	public static final String LOGIN_USER_KEY = "loginUser";//用户信息存放的key
	public static final String LOGIN_USER_ID_KEY = "id";//用户标识存放的key
    public static final String LOGIN_USER_CLIENT_ADDR_KEY = "_clientAddr";//用户客户端地址存放的key
    public static final String LOGIN_USER_SESSION_ID_KEY = "_sessionId";//用户sessionId存放的key
	public static final String LOGIN_USER_TENANT_ID_KEY = "tenantId";//用户归属租户信息存放的key
    public static final String IS_TENANT_ADMIN_KEY = "isAdmin";//是否租户管理员存放的key
    public static final String UNKNOWN_STR = "unknown";
    public static final String X_FORWARDED_FOR_STR = "x-forwarded-for";
    public static final String PROXY_CLIENT_IP_STR = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP_STR = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP_STR = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR_STR = "HTTP_X_FORWARDED_FOR";


    /**
     * 获取登录session
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static JsonObject getLoginSession(HttpServletRequest request, String loginInfoKey) {
        return (JsonObject)request.getSession().getAttribute(loginInfoKey);
    }

    /**
     * 获取登录用户信息
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static JsonObject getLoginUserInfo(HttpServletRequest request, String loginInfoKey) {
        JsonObject loginInfo = getLoginSession(request, loginInfoKey);
        if(loginInfo != null) {
            return JsonUtils.getJsonObject(loginInfo, LOGIN_USER_KEY);
        }else {
            return null;
        }
    }

    /**
     * 获取登录用户标识
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static String getLoginUserId(HttpServletRequest request, String loginInfoKey) {
        JsonObject loginUser = getLoginUserInfo(request, loginInfoKey);
        if(loginUser != null) {
            return JsonUtils.getString(loginUser, LOGIN_USER_ID_KEY);
        }else {
            return null;
        }
    }

    /**
     * 当前用户是否租户管理员
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static boolean isTenantAdmin(HttpServletRequest request, String loginInfoKey) {
        JsonObject loginUser = getLoginUserInfo(request, loginInfoKey);
        if(loginUser != null) {
            return YesOrNo.YES.equals(JsonUtils.getString(loginUser, IS_TENANT_ADMIN_KEY));
        }else {
            return false;
        }
    }

    /**
     * 租户管理员权限检查
     * @param request
     * @param loginInfoKey
     * @throws IllegalAccessException
     */
    public static void tenantAdminCheck(HttpServletRequest request, String loginInfoKey) throws IllegalAccessException {
        if(!isTenantAdmin(request, loginInfoKey)) {
            throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
        }
    }

    /**
     * 业务系统权限检查
     * @param request
     * @param loginInfoKey
     * @throws IllegalAccessException
     */
    public static void systemGrantCheck(HttpServletRequest request, String loginInfoKey, String systemId) throws IllegalAccessException {
        JsonObject grantedSystems = getLoginUserGrantedSystems(request, loginInfoKey);
        if(grantedSystems != null && !StringUtils.isBlank(systemId) && JsonUtils.getJsonObject(grantedSystems, systemId) != null) {
            return;
        }else {
            throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
        }
    }

    /**
     * 租户及业务系统权限检查
     * @param request
     * @param loginInfoKey
     * @throws IllegalAccessException
     */
    public static void tenantAndSystemGrantCheck(HttpServletRequest request, String loginInfoKey, String systemId, String tenantId) throws IllegalAccessException {
        JsonObject grantedSystems = getLoginUserGrantedSystems(request, loginInfoKey);
        JsonObject loginUser = getLoginUserInfo(request, loginInfoKey);
        if(grantedSystems != null && loginUser != null && !StringUtils.isBlank(systemId) && !StringUtils.isBlank(tenantId) 
                && JsonUtils.getJsonObject(grantedSystems, systemId) != null && tenantId.equals(JsonUtils.getString(loginUser, LOGIN_USER_TENANT_ID_KEY))) {
            return;
        }else {
            throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
        }
    }

    /**
     * 租户检查
     * @param request
     * @param loginInfoKey
     * @throws IllegalAccessException
     */
    public static void tenantCheck(HttpServletRequest request, String loginInfoKey, String tenantId) throws IllegalAccessException {
        String loginTenantId = getLoginUserTenant(request, loginInfoKey);
        if(!StringUtils.isBlank(loginTenantId) && !StringUtils.isBlank(tenantId) && tenantId.equals(loginTenantId)) {
            return;
        }else {
            throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
        }
    }

    /**
     * 获取登录用户租户标识
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static String getLoginUserTenant(HttpServletRequest request, String loginInfoKey) {
        JsonObject loginUser = getLoginUserInfo(request, loginInfoKey);
        if(loginUser != null) {
            return JsonUtils.getString(loginUser, LOGIN_USER_TENANT_ID_KEY);
        }else {
            return null;
        }
    }

    /**
     * 获取登录用户业务系统授权信息
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static JsonObject getLoginUserGrantedSystems(HttpServletRequest request, String loginInfoKey) {
        JsonObject loginInfo = getLoginSession(request, loginInfoKey);
        if(loginInfo != null) {
            return JsonUtils.getJsonObject(loginInfo, BUSINESS_SYSTEM_GRANT_INFO_KEY);
        }else {
            return null;
        }
    }

    /**
     * 获取客户端地址信息
     * @param request
     * @param loginInfoKey
     * @return
     */
    public static String getClientAddr(HttpServletRequest request) {
        String clientAddr = request.getHeader(X_FORWARDED_FOR_STR);  
        if (StringUtils.isBlank(clientAddr) || UNKNOWN_STR.equalsIgnoreCase(clientAddr)) {  
            clientAddr = request.getHeader(PROXY_CLIENT_IP_STR);  
        }  
        if (StringUtils.isBlank(clientAddr) || UNKNOWN_STR.equalsIgnoreCase(clientAddr)) {  
            clientAddr = request.getHeader(WL_PROXY_CLIENT_IP_STR);  
        }  
        if (StringUtils.isBlank(clientAddr) || UNKNOWN_STR.equalsIgnoreCase(clientAddr)) {  
            clientAddr = request.getHeader(HTTP_CLIENT_IP_STR);  
        }  
        if (StringUtils.isBlank(clientAddr) || UNKNOWN_STR.equalsIgnoreCase(clientAddr)) {  
            clientAddr = request.getHeader(HTTP_X_FORWARDED_FOR_STR);  
        }  
        if (StringUtils.isBlank(clientAddr) || UNKNOWN_STR.equalsIgnoreCase(clientAddr)) {  
            clientAddr = request.getRemoteAddr();  
        }  
        return clientAddr;  
    }
}
