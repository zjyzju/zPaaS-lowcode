package cn.zpaas.lowcode.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.zpaas.lowcode.utils.CommonUtils;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * SHA256摘要及验证工具类
 *
 * @author zjy
 * createTime 2025年04月15日-18:07:47
 */
public class SHA256Utils {
    private static final String SHA256_STRING = "SHA-256";

     private SHA256Utils() {
    }

    /**
     * 使用SHA256生成摘要信息
     * @param rawPass
     * @return
     */
    public static String encode(String rawPass) {
        MessageDigest messageDigest = getMessageDigest();
        byte[] digest = messageDigest.digest(rawPass.getBytes());
        return new String(CommonUtils.hex2Ascii(digest)).toLowerCase();
    }

    /**
     * 获取消息摘要实现类
     * @return
     * @throws IllegalArgumentException
     */
    protected static MessageDigest getMessageDigest() throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(SHA256_STRING);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm SHA-256");
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
