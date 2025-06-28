package cn.zpaas.lowcode.security;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CommonUtils;

/**
 * 对称加解密工具类
 *
 * @author zjy
 * createTime 2025年04月15日-14:00:19
 */
public class CipherUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private static final Logger log = LoggerFactory.getLogger(CipherUtils.class);

    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_TRIDES = "DESede";
    public static final String ALGORITHM_SM4 = "SM4";
    
    public static final int KEY_AES_LENGTH_128 = 128;
    public static final int KEY_AES_LENGTH_192 = 192;
    public static final int KEY_AES_LENGTH_256 = 256;
    public static final int KEY_DES_LENGTH_56 = 56;
    public static final int KEY_TRIDES_LENGTH_112 = 112;
    public static final int KEY_TRIDES_LENGTH_168 = 168;
    public static final int KEY_SM4_LENGTH_128 = 128;
    public static final int KEY_SM4_LENGTH_192 = 192;
    public static final int KEY_SM4_LENGTH_256 = 256;

    public static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
    public static final String AES_ECB_NOPADDING = "AES/ECB/NoPadding";
    public static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";

    public static final String TRIDES_ECB_PKCS5PADDING = "DESede/ECB/PKCS5Padding";
    public static final String TRIDES_ECB_NOPADDING = "DESede/ECB/NoPadding";
    public static final String TRIDES_CBC_PKCS5PADDING = "DESede/CBC/PKCS5Padding";
    public static final String TRIDES_CBC_NOPADDING = "DESede/CBC/NoPadding";
    
    public static final String DES_ECB_PKCS5PADDING = "DES/ECB/PKCS5Padding";
    public static final String DES_ECB_NOPADDING = "DES/ECB/NoPadding";
    public static final String DES_CBC_PKCS5PADDING = "DES/CBC/PKCS5Padding";
    public static final String DES_CBC_NOPADDING = "DES/CBC/NoPadding";

    public static final String SM4_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";
    public static final String SM4_ECB_NOPADDING = "SM4/ECB/NoPadding";
    public static final String SM4_CBC_PKCS5PADDING = "SM4/CBC/PKCS5Padding";
    public static final String SM4_CBC_NOPADDING = "SM4/CBC/NoPadding";

    private static final String SECURITY_KEY = "04daa44fa2865815";
    private static final byte[] CBC_IV_8 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] CBC_IV_16 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * 私有化构造方法
     */
    private CipherUtils() {

    }

    /**
     * 生成Key
     * 
     * @return
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_DES);
            keyGenerator.init(56);
            SecretKey secretKey = keyGenerator.generateKey();
            return CommonUtils.hex2Ascii(secretKey.getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    /**
     * 生成Key
     * @return
     */
    public static byte[] generateKey(String keyAlgorithm, int keyLength) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
            keyGenerator.init(keyLength);
            SecretKey secretKey = keyGenerator.generateKey();
            return CommonUtils.hex2Ascii(secretKey.getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    /**
     * 生成Key
     * 
     * @param key
     * @return
     */
    private static Key toKey(byte[] key, String keyAlgorithm) {
        return new SecretKeySpec(key, keyAlgorithm);
    }

    /**
     * 加密
     * 
     * @param data
     * @param key
     * @param algorithm
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key, String algorithm, String keyAlgorithm) {
        try {
            Key k = toKey(key, keyAlgorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (TRIDES_CBC_PKCS5PADDING.equals(algorithm) || TRIDES_CBC_NOPADDING.equals(algorithm) ||
                DES_CBC_PKCS5PADDING.equals(algorithm) || DES_CBC_NOPADDING.equals(algorithm)) {
                IvParameterSpec spec = new IvParameterSpec(CBC_IV_8);
                cipher.init(Cipher.ENCRYPT_MODE, k, spec);
            } else if (AES_CBC_PKCS5PADDING.equals(algorithm) || AES_CBC_NOPADDING.equals(algorithm) ||
                SM4_CBC_PKCS5PADDING.equals(algorithm) || SM4_CBC_NOPADDING.equals(algorithm)) {
                IvParameterSpec spec = new IvParameterSpec(CBC_IV_16);
                cipher.init(Cipher.ENCRYPT_MODE, k, spec);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, k);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    /**
     * 解密
     * 
     * @param data
     * @param key
     * @param algorithm
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key, String algorithm, String keyAlgorithm) {
        try {
            Key k = toKey(key, keyAlgorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (TRIDES_CBC_PKCS5PADDING.equals(algorithm) || TRIDES_CBC_NOPADDING.equals(algorithm) ||
                DES_CBC_PKCS5PADDING.equals(algorithm) || DES_CBC_NOPADDING.equals(algorithm)) {
                IvParameterSpec spec = new IvParameterSpec(CBC_IV_8);
                cipher.init(Cipher.DECRYPT_MODE, k, spec);
            } else if (AES_CBC_PKCS5PADDING.equals(algorithm) || AES_CBC_NOPADDING.equals(algorithm) ||
                SM4_CBC_PKCS5PADDING.equals(algorithm) || SM4_CBC_NOPADDING.equals(algorithm)) {
                IvParameterSpec spec = new IvParameterSpec(CBC_IV_16);
                cipher.init(Cipher.DECRYPT_MODE, k, spec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, k);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new byte[0];
    }

    /**
     * 加密
     * 
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        byte[] aa = encrypt(data.getBytes(), CommonUtils.ascii2Hex(SECURITY_KEY.getBytes()), DES_ECB_PKCS5PADDING, ALGORITHM_DES);
        return new String(CommonUtils.hex2Ascii(aa));
    }

    /**
     * 解密
     * 
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        byte[] aa = CommonUtils.ascii2Hex(data.getBytes());
        return new String(decrypt(aa, CommonUtils.ascii2Hex(SECURITY_KEY.getBytes()), DES_ECB_PKCS5PADDING, ALGORITHM_DES));
    }

    /**
     * 加密
     * 
     * @param data
     * @param algorithm
     * @return
     */
    public static String encrypt(String data, String key, String algorithm, String keyAlgorithm) {
        byte[] aa = encrypt(data.getBytes(), CommonUtils.ascii2Hex(key.getBytes()), algorithm, keyAlgorithm);
        return new String(CommonUtils.hex2Ascii(aa));
    }

    /**
     * 解密
     * 
     * @param data
     * @param algorithm
     * @return
     */
    public static String decrypt(String data, String key, String algorithm, String keyAlgorithm) {
        byte[] aa = CommonUtils.ascii2Hex(data.getBytes());
        return new String(decrypt(aa, CommonUtils.ascii2Hex(key.getBytes()), algorithm, keyAlgorithm));
    }

    /**
     * 补0
     * 
     * @param in
     * @return
     */
    public static byte[] paddingZero(byte[] in) {
        if (in == null || in.length == 0) {
            return new byte[0];
        }
        int inLen = in.length;
        int m = inLen % 8;
        byte[] out = null;
        if (m == 0) {
            out = new byte[inLen];
        } else {
            out = new byte[inLen + 8 - m];
        }
        int outLen = out.length;
        for (int i = 0; i < outLen; i++) {
            if (i < inLen) {
                out[i] = in[i];
            } else {
                out[i] = 0x00;
            }
        }
        return out;
    }

    /**
     * XOR
     * 
     * @param edata
     * @param temp
     * @return
     */
    public static byte[] xor(byte[] edata, byte[] temp) {
        byte[] result = new byte[8];
        for (int i = 0, j = result.length; i < j; i++) {
            result[i] = (byte) (edata[i] ^ temp[i]);
        }
        return result;
    }

    /**
     * mac_encrypt
     * @param data
     * @param macKey
     * @return
     */
    public static byte[] macEncrypt(byte[] data, byte[] macKey) {
        // 进行分组
        int group = (data.length + (8 - 1)) / 8;
        // 偏移量
        int offset = 0;
        // 输入计算数据
        byte[] edata = null;
        for (int i = 0; i < group; i++) {
            byte[] temp = new byte[8];
            if (i != group - 1) {
                System.arraycopy(data, offset, temp, 0, 8);
                offset += 8;
            } else {// 只有最后一组数据才进行填充0x00
                System.arraycopy(data, offset, temp, 0, data.length - offset);
            }
            if (i != 0) {// 只有第一次不做异或
                temp = xor(edata, temp);
            }
            edata = encrypt(temp, macKey, DES_CBC_NOPADDING, ALGORITHM_DES);
        }
        return edata;
    }

    /**
     * macDecrypt
     * @param data
     * @param macKey
     * @return
     */
    public static byte[] macDecrypt(byte[] data, byte[] macKey) {
        return decrypt(data, macKey, DES_CBC_NOPADDING, ALGORITHM_DES);
    }

    // public static void main(String[] args) {
    //     System.out.println(new String(generateKey(ALGORITHM_DES, 56)));
    //     String ecrypted = encrypt("DES_CBC_IV", "ccf9c9cf7957e614965038ac252d6dce", SM4_ECB_PKCS5PADDING, ALGORITHM_SM4);
    //     System.out.println(ecrypted + " : " + decrypt(ecrypted, "ccf9c9cf7957e614965038ac252d6dce", SM4_ECB_PKCS5PADDING, ALGORITHM_SM4));
    // }
}
