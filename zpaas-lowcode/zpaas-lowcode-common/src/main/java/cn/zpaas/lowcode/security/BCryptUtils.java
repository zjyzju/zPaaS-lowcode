package cn.zpaas.lowcode.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月18日-18:19:40
 */
public class BCryptUtils {
    private static final int SALT = 10;//盐

	/**
	 * 私有化构造方法
	 */
	private BCryptUtils() {

	}
	
	/**
	 * 加密
	 * @param rawPass
	 * @return
	 */
	public static String encode(String rawPass) {
		return BCrypt.hashpw(rawPass, BCrypt.gensalt(SALT));
	}
	
	/**
	 * 验证
	 * @param encPass
	 * @param rawPass
	 * @return
	 */
	public static boolean isValid(String encPass, String rawPass) {
		return BCrypt.checkpw(rawPass, encPass);
	}
}
