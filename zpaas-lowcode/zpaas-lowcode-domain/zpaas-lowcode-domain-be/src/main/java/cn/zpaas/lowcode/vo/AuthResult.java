package cn.zpaas.lowcode.vo;

/**
 * @author zjy
 * 鉴权结果类
 */
public class AuthResult {
	public static final String AUTH_RESULT_FAILED = "0";//鉴权结果：0：失败；1：成功；2：需要修改密码；3：账号锁定；
	public static final String AUTH_RESULT_SUCCEED = "1";
	public static final String AUTH_RESULT_NEED_MODIFY = "2";
	public static final String AUTH_RESULT_LOCKED = "3";
	
	private String userId; //用户标识
	private String authResult; //鉴权结果
	private String msg; //详细信息
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAuthResult() {
		return authResult;
	}
	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
