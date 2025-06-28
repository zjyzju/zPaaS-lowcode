package cn.zpaas.lowcode.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.zpaas.lowcode.utils.CommonUtils;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * MD5摘要以及验证工具类
 *
 * @author zjy
 * createTime 2025年04月15日-10:46:36
 */
public class MD5Utils {
    /**
     * 私有化构造方法
     */
    private MD5Utils() {
    }

    private static final String MD5_STRING = "MD5";

    /**
     * 使用MD5算法进行消息摘要
     * @param rawPass
     * @return
     */
    public static String encode(String rawPass) {
        MessageDigest messageDigest = getMessageDigest();
        byte[] digest = messageDigest.digest(rawPass.getBytes());
        return new String(CommonUtils.hex2Ascii(digest)).toLowerCase();
    }

    /**
     * 获取MD5消息摘要实现类
     * @return
     * @throws IllegalArgumentException
     */
    protected static MessageDigest getMessageDigest() throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(MD5_STRING);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm MD5");
        }
    }

    /**
     * 摘要信息匹配验证
     * @param encPass
     * @param rawPass
     * @return
     */
    public static boolean isValid(String encPass, String rawPass) {
        if(StringUtils.isNull(encPass)) {
            return false;
        }
        return encPass.equalsIgnoreCase(encode(rawPass));
    }
}
