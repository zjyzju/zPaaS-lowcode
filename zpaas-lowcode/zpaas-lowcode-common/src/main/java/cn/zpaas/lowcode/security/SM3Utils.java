package cn.zpaas.lowcode.security;

import org.bouncycastle.crypto.digests.SM3Digest;

import cn.zpaas.lowcode.utils.CommonUtils;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * SM3摘要以及验证工具类
 *
 * @author zjy
 * createTime 2025年04月15日-10:46:36
 */
public class SM3Utils {
    /**
     * 私有化构造方法
     */
    private SM3Utils() {
    }

    
    /**
     * 使用SM3算法进行消息摘要
     * @param rawPass
     * @return
     */
    public static String encode(String rawPass) {
        SM3Digest messageDigest = getMessageDigest();
        byte[] rawBytes = rawPass.getBytes();
        messageDigest.update(rawBytes, 0, rawBytes.length);;
        byte[] digest = new byte[messageDigest.getDigestSize()];
        messageDigest.doFinal(digest, 0);
        return new String(CommonUtils.hex2Ascii(digest)).toLowerCase();
    }

    /**
     * 获取SM3消息摘要实现类
     * @return
     */
    protected static SM3Digest getMessageDigest() {
        return new SM3Digest();
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
