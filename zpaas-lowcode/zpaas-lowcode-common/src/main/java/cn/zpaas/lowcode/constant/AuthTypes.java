package cn.zpaas.lowcode.constant;

/**
 * 业务系统鉴权类型定义
 *
 * @author zjy
 * createTime 2025年04月21日-17:32:58
 */
public class AuthTypes {
    public static final String AUTH_TYPE_NONE = "N";//无权限控制模式
	public static final String AUTH_TYPE_HEADER = "H";//Header模式
	public static final String AUTH_TYPE_SESSION = "S";//共享Session模式
	public static final String AUTH_TYPE_ACCESS_KEY = "A";//AccessKey模式
	public static final String AUTH_TYPE_USER_PWD = "P";//用户密码认证模式
	public static final String AUTH_TYPE_SSO = "O";//单点登录模式

    private AuthTypes() {
        
    }
}
