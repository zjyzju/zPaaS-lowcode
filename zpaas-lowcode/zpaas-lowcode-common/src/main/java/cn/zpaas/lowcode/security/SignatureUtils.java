package cn.zpaas.lowcode.security;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CommandUtils;
import cn.zpaas.lowcode.utils.CommonUtils;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * 非对称加密及验证工具类-RSA&SM2
 *
 * @author zjy
 * createTime 2025年04月15日-10:27:24
 */
public class SignatureUtils {
	static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static final Logger log = LoggerFactory.getLogger(SignatureUtils.class);
	
	public static final String JKS = "JKS";
	private static final String PKCS12 = "PKCS12";
	private static final String X509 = "X.509";

	public static final String RSA = "RSA";
	public static final String SHA256_WITH_RSA = "SHA256withRSA";
	public static final String SHA1_WITH_RSA = "SHA1withRSA";
	public static final String MD5_WITH_RSA = "MD5withRSA";
	
	public static final String SM2 = "SM2";
	public static final String SM3_WITH_SM2 = "1.2.156.10197.1.501";

	private static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
	
    /**
     * 加载私钥
     * @param pfxFile 私钥文件路径
     * @param pfxPwd 库密码
     * @param privateKeyPwd 私钥密码
     * @return
     */
	private static PrivateKey loadPrivateKey(String privateKeyFile, String privateKeyPwd) {
		try (InputStream bis = new FileInputStream(privateKeyFile)){
			KeyStore store = KeyStore.getInstance(PKCS12);
			store.load(bis, privateKeyPwd.toCharArray());
			String alias = store.aliases().nextElement();
			return (PrivateKey)store.getKey(alias, privateKeyPwd.toCharArray());
		} catch (Exception e) {
			log.error("load private key failed！", e);
			return null;
		} 
	}

	/**
	 * 加载私钥
	 * @param privateKey
	 * @param privateKeyPwd
	 * @return
	 */
	private static PrivateKey loadPrivateKey(byte[] privateKey, String privateKeyPwd) {
		try (InputStream bis = new ByteArrayInputStream(privateKey)){
			KeyStore store = KeyStore.getInstance(PKCS12);
			store.load(bis, privateKeyPwd.toCharArray());
			String alias = store.aliases().nextElement();
			return (PrivateKey)store.getKey(alias, privateKeyPwd.toCharArray());
		} catch (Exception e) {
			log.error("load private key failed！", e);
			return null;
		} 
	}
	
    /**
     * 加载公钥
     * @param publicKeyFile 公钥文件路径
     * @return
     */
	private static PublicKey loadPublicKey(String publicKeyFile) {
		try (InputStream bis = new FileInputStream(publicKeyFile)){
			CertificateFactory cf = CertificateFactory.getInstance(X509);
			Certificate cert = cf.generateCertificate(bis);
			return cert.getPublicKey();
		} catch (Exception e) {
			log.error("load public key failed!", e);
			return null;
		} 
	}

	/**
     * 加载公钥
     * @param publicKey 公钥文件
     * @return
     */
	private static PublicKey loadPublicKey(byte[] publicKey) {
		try (InputStream bis = new ByteArrayInputStream(publicKey)){
			CertificateFactory cf = CertificateFactory.getInstance(X509);
			Certificate cert = cf.generateCertificate(bis);
			return cert.getPublicKey();
		} catch (Exception e) {
			log.error("load public key failed!", e);
			return null;
		} 
	}
	
	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKeyFile
	 * @param privateKeyPwd
	 * @return
	 */
	public static String sign(String plainText, String privateKeyFile, String privateKeyPwd, String charset) {
		return sign(plainText, loadPrivateKey(privateKeyFile, privateKeyPwd), charset);
	}

	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKey
	 * @param privateKeyPwd
	 * @return
	 */
	public static String sign(String plainText, byte[] privateKey, String privateKeyPwd, String charset) {
		return sign(plainText, loadPrivateKey(privateKey, privateKeyPwd), charset);
	}

	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKey
	 * @return
	 */
	public static String sign(String plainText, PrivateKey privateKey, String charset) {
		return sign(plainText, privateKey, charset, SHA256_WITH_RSA);
	}

	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKey
	 * @param privateKeyPwd
	 * @param algorithm
	 * @return
	 */
	public static String sign(String plainText, byte[] privateKey, String privateKeyPwd, String charset, String algorithm) {
		return sign(plainText, loadPrivateKey(privateKey, privateKeyPwd), charset, algorithm);
	}

	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKeyFile
	 * @param privateKeyPwd
	 * @param algorithm
	 * @return
	 */
	public static String sign(String plainText, String privateKeyFile, String privateKeyPwd, String charset, String algorithm) {
		return sign(plainText, loadPrivateKey(privateKeyFile, privateKeyPwd), charset, algorithm);
	}

	/**
	 * 签名方法
	 * @param plainText
	 * @param charset
	 * @param privateKey
	 * @param algorithm
	 * @return
	 */
	public static String sign(String plainText, PrivateKey privateKey, String charset, String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			if(!StringUtils.isBlank(charset)) {
				signature.update(plainText.getBytes(charset));
				byte[] sign = signature.sign();
				return new String(CommonUtils.hex2Ascii(sign), charset);
			}else {
				signature.update(plainText.getBytes());
				byte[] sign = signature.sign();
				return new String(CommonUtils.hex2Ascii(sign));
			}			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} 
		return null;
	}
	
	/**
	 * 签名方法，使用默认字符集
	 * @param plainText 原始文本
	 * @param privateKeyFile 私钥文件路径
	 * @param privateKeyPwd 私钥密码
	 * @return 签名信息
	 */
	public static String sign(String plainText, String privateKeyFile, String privateKeyPwd) {
		return sign(plainText, privateKeyFile, privateKeyPwd, null);
	}

	/**
	 * 签名方法，使用默认字符集
	 * @param plainText 原始文本
	 * @param privateKey 私钥文件
	 * @param privateKeyPwd 私钥密码
	 * @return 签名信息
	 */
	public static String sign(String plainText, byte[] privateKey, String privateKeyPwd) {
		return sign(plainText, privateKey, privateKeyPwd, null);
	}
	
	/**
	 * 验签方法，使用默认字符集
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKeyFile 公钥文件路径
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, String publicKeyFile) {
		return verifySignature(sign, plainText, loadPublicKey(publicKeyFile), null);
	}

	/**
	 * 验签方法，使用默认字符集
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKey 公钥文件
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, byte[] publicKey) {
		return verifySignature(sign, plainText, loadPublicKey(publicKey), null);
	}
	
	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKeyFile 公钥文件路径
	 * @param charset 文本字符集
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, String publicKeyFile, String charset) {
		return verifySignature(sign, plainText, loadPublicKey(publicKeyFile), charset);
	}

	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKey 公钥文件
	 * @param charset 文本字符集
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, byte[] publicKey, String charset) {
		return verifySignature(sign, plainText, loadPublicKey(publicKey), charset);
	}

	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKey 公钥文件
	 * @param charset 文本字符集
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, PublicKey publicKey, String charset) {
		return verifySignature(sign, plainText, publicKey, charset, SHA256_WITH_RSA);
	}

	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKey 公钥文件
	 * @param charset 文本字符集
	 * @param algorithm 签名算法
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, byte[] publicKey, String charset, String algorithm) {
		return verifySignature(sign, plainText, loadPublicKey(publicKey), charset, algorithm);
	}

	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKeyFile 公钥文件路径
	 * @param charset 文本字符集
	 * @param algorithm 签名算法
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, String publicKeyFile, String charset, String algorithm) {
		return verifySignature(sign, plainText, loadPublicKey(publicKeyFile), charset, algorithm);
	}

	/**
	 * 验签方法
	 * @param sign	签名信息
	 * @param plainText	原始文本
	 * @param publicKey 公钥文件
	 * @param charset 文本字符集
	 * @param algorithm 签名算法
	 * @return 验签结果
	 */
	public static boolean verifySignature(String sign, String plainText, PublicKey publicKey, String charset, String algorithm) {
		try {
			byte[] signArray = CommonUtils.ascii2Hex(sign.getBytes());
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			if(StringUtils.isBlank(charset)) {
				signature.update(plainText.getBytes());
			}else {
				signature.update(plainText.getBytes(charset));
			}
			return signature.verify(signArray);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} 
		return false;
	}

	/**
	 * 使用公钥进行加密，使用默认字符集
	 * @param plainText
	 * @param publicKeyFile
	 * @return
	 */
	public static String encrypt(String plainText, String publicKeyFile) {
		return encrypt(plainText, loadPublicKey(publicKeyFile), null);
	}

	/**
	 * 使用公钥进行加密，使用默认字符集
	 * @param plainText
	 * @param publicKey
	 * @return
	 */
	public static String encrypt(String plainText, byte[] publicKey) {
		return encrypt(plainText, loadPublicKey(publicKey), null);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKeyFile
	 * @param charset
	 * @return
	 */
	public static String encrypt(String plainText, String publicKeyFile, String charset) {
		return encrypt(plainText, loadPublicKey(publicKeyFile), charset);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKey
	 * @param charset
	 * @return
	 */
	public static String encrypt(String plainText, byte[] publicKey, String charset) {
		return encrypt(plainText, loadPublicKey(publicKey), charset);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKey
	 * @param charset
	 * @return
	 */
	public static String encrypt(String plainText, PublicKey publicKey, String charset) {
		return encrypt(plainText, publicKey, charset, RSA_ECB_PKCS1PADDING);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKeyFile
	 * @param charset
	 * @return
	 */
	public static String encrypt(String plainText, String publicKeyFile, String charset, String algorithm) {
		return encrypt(plainText, loadPublicKey(publicKeyFile), charset, algorithm);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKey
	 * @param charset
	 * @return
	 */
	public static String encrypt(String plainText, byte[] publicKey, String charset, String algorithm) {
		return encrypt(plainText, loadPublicKey(publicKey), charset, algorithm);
	}

	/**
	 * 使用公钥进行加密
	 * @param plainText
	 * @param publicKey
	 * @param charset
	 * @param algorithm
	 * @return
	 */
	public static String encrypt(String plainText, PublicKey publicKey, String charset, String algorithm) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] byteText = null;
			if(StringUtils.isBlank(charset)) {
				byteText = plainText.getBytes();
				cipher.update(byteText);
				return new String(CommonUtils.hex2Ascii(cipher.doFinal()));
			}else {
				byteText = plainText.getBytes(charset);
				cipher.update(byteText);
				return new String(CommonUtils.hex2Ascii(cipher.doFinal()), charset);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 使用私钥进行解密，使用默认字符集
	 * @param encryptedText
	 * @param privateKey
	 * @param privateKeyPwd
	 * @return
	 */
	public static String decrypt(String encryptedText, byte[] privateKey, String privateKeyPwd) {
		return decrypt(encryptedText, loadPrivateKey(privateKey, privateKeyPwd), null);
	}

	/**
	 * 使用私钥进行解密，使用默认字符集
	 * @param encryptedText
	 * @param privateKeyFile
	 * @param privateKeyPwd
	 * @return
	 */
	public static String decrypt(String encryptedText, String privateKeyFile, String privateKeyPwd) {
		return decrypt(encryptedText, loadPrivateKey(privateKeyFile, privateKeyPwd), null);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKey
	 * @param privateKeyPwd
	 * @param charset
	 * @return
	 */
	public static String decrypt(String encryptedText, byte[] privateKey, String privateKeyPwd, String charset) {
		return decrypt(encryptedText, loadPrivateKey(privateKey, privateKeyPwd), charset);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKeyFile
	 * @param privateKeyPwd
	 * @param charset
	 * @return
	 */
	public static String decrypt(String encryptedText, String privateKeyFile, String privateKeyPwd, String charset) {
		return decrypt(encryptedText, loadPrivateKey(privateKeyFile, privateKeyPwd), charset);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKey
	 * @param charset
	 * @return
	 */
	public static String decrypt(String encryptedText, PrivateKey privateKey, String charset) {
		return decrypt(encryptedText, privateKey, charset, RSA_ECB_PKCS1PADDING);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKey
	 * @param privateKeyPwd
	 * @param charset
	 * @return
	 */
	public static String decrypt(String encryptedText, byte[] privateKey, String privateKeyPwd, String charset, String algorithm) {
		return decrypt(encryptedText, loadPrivateKey(privateKey, privateKeyPwd), charset, algorithm);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKeyFile
	 * @param privateKeyPwd
	 * @param charset
	 * @return
	 */
	public static String decrypt(String encryptedText, String privateKeyFile, String privateKeyPwd, String charset, String algorithm) {
		return decrypt(encryptedText, loadPrivateKey(privateKeyFile, privateKeyPwd), charset, algorithm);
	}

	/**
	 * 使用私钥进行解密
	 * @param encryptedText
	 * @param privateKey
	 * @param charset
	 * @param algorithm
	 * @return
	 */
	public static String decrypt(String encryptedText, PrivateKey privateKey, String charset, String algorithm) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] byteText = null;
			if(StringUtils.isBlank(charset)) {
				byteText = encryptedText.getBytes();
				cipher.update(CommonUtils.ascii2Hex(byteText));
				return new String(cipher.doFinal());
			}else {
				byteText = encryptedText.getBytes(charset);
				cipher.update(CommonUtils.ascii2Hex(byteText));
				return new String(cipher.doFinal(), charset);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 使用keytool生成密码钥文件
	 * @param keytool
	 * @param validity
	 * @param keySize
	 * @param alias
	 * @param keyAlg
	 * @param keyStorePath
	 * @param dName
	 * @param storePass
	 * @param keyPass
	 * @param sigAlg
	 * @return
	 */
	public static boolean genKeyStore(String keytool, long validity, int keySize, String alias, String keyAlg, String keyStorePath, 
			String dName, String storePass, String keyPass, String sigAlg) {
		String[] cmd = new String[] {
				keytool,
				"-genkey", 
				"-validity", String.valueOf(validity),
				"-keysize", String.valueOf(keySize), 
				"-alias", alias,
				"-keyalg", keyAlg, 
				"-keystore", keyStorePath,
				"-dname", dName,
				"-storepass", storePass, 
				"-keypass", keyPass, 
				"-sigalg", sigAlg,
				"-v"
			};
		return CommandUtils.execCommand(cmd);
	}
	
	/**
	 * 导出私钥文件
	 * @param keyStorePath
	 * @param alias
	 * @param storePass
	 * @param keyPass
	 * @param filePath
	 * @return
	 */
	public static boolean exportPfxFile(String keyStorePath, String alias, String storePass, String keyPass, String filePath) {
		try {
			KeyStore keyStore = KeyStore.getInstance(JKS);
			keyStore.load(new FileInputStream(keyStorePath), storePass.toCharArray());
			Certificate cert = keyStore.getCertificate(alias);
			Certificate[] certs = new Certificate[] {cert};		
			KeyStore pfx = KeyStore.getInstance(PKCS12);
			pfx.load(null, storePass.toCharArray());
			pfx.setKeyEntry(alias, (PrivateKey)keyStore.getKey(alias, keyPass.toCharArray()), keyPass.toCharArray(), certs);
			FileOutputStream fos = new FileOutputStream(filePath);
			pfx.store(fos, storePass.toCharArray());
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return false;
		} 
	}
	
	/**
	 * 导出公钥文件
	 * @param keytool
	 * @param alias
	 * @param cerFile
	 * @param keyStorePath
	 * @param storePass
	 * @return
	 */
	public static boolean exportCerFile(String keytool, String alias, String cerFile, String keyStorePath, String storePass) {
		String[] cmd = new String[] {
				keytool,
				"-export", 
				"-alias", alias,
				"-keystore", keyStorePath,
				"-storepass", storePass, 
				"-storetype", JKS,
				"-file", cerFile
			};
		return CommandUtils.execCommand(cmd);
	}
	
	/**
	 * 生成所有证书文件
	 * @param keytool
	 * @param validity
	 * @param keySize
	 * @param alias
	 * @param keyAlg
	 * @param dName
	 * @param storePass
	 * @param keyPass
	 * @param storeFile
	 * @param pfxFile
	 * @param cerFile
	 * @return
	 */
	public static boolean genAllKeyFile(String keytool, long validity, int keySize, String alias, String keyAlg, String dName, 
			String storePass, String keyPass, String storeFile, String pfxFile, String cerFile, String sigAlg) {
		try {
			if(!genKeyStore(keytool, validity, keySize, alias, keyAlg, storeFile, dName, storePass, keyPass, sigAlg)) {
				log.error("gen key store failed.");
				return false;
			}
			
			if(!exportPfxFile(storeFile, alias, storePass, keyPass, pfxFile)) {
				log.error("export pfx file failed.");
				return false;
			}
			
			if(!exportCerFile(keytool, alias, cerFile, storeFile, storePass)) {
				log.error("export cer file failed.");
				return false;
			}				
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return false;
		} 
	}

	// public static void main(String[] args) {
	// 	String planText = "abcdefghijk";
	// 	String encryptedText = encrypt(planText, "/Users/zjy/Downloads/test_public_sm2.cer", "utf-8", SM2);
	// 	System.out.println(encryptedText + " : " + decrypt(encryptedText, "/Users/zjy/Downloads/test_private_sm2.p12", "123456", "utf-8", SM2));

	// 	String sign = sign(planText, "/Users/zjy/Downloads/test_private_sm2.p12", "123456","utf-8", SM3_WITH_SM2);
	// 	System.out.println(sign + " : " + verifySignature(sign, planText, "/Users/zjy/Downloads/test_public_sm2.cer", "utf-8",SM3_WITH_SM2));


	// 	encryptedText = encrypt(planText, "/Users/zjy/Downloads/test_public_php.cer");
	// 	System.out.println(encryptedText + " : " + decrypt(encryptedText, "/Users/zjy/Downloads/test_private_php.p12", "123456"));

	// 	sign = sign(planText, "/Users/zjy/Downloads/test_private_php.p12", "123456","utf-8", MD5_WITH_RSA);
	// 	System.out.println(sign + " : " + verifySignature(sign, planText, "/Users/zjy/Downloads/test_public_php.cer", "utf-8",MD5_WITH_RSA));

	// 	// CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),  
	// 	// ST=(州或省份名称), C=(单位的两字母国家代码)"
	// 	String dName = "CN=(WWW.COM.CN),OU=(),O=(),L=(BJ),ST=(BJ),C=(CN)";
	// 	boolean ret = genAllKeyFile("keytool", 36500, 2048, "test_rsa", "RSA", dName, "123456", "123456",
	// 			"/Users/zjy/Downloads/RSA.keystore",
	// 			"/Users/zjy/Downloads/private_RSA.p12",
	// 			"/Users/zjy/Downloads/public_RSA.cer", SHA256_WITH_RSA);
	// 	System.out.println("RSA:" + ret);

	// 	ret = genAllKeyFile("/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/bin/keytool", 36500L, 256, "test_sm2", "SM2", dName, "123456", "123456",
	// 			"/Users/zjy/Downloads/SM2.keystore",
	// 			"/Users/zjy/Downloads/private_SM2.p12",
	// 			"/Users/zjy/Downloads/public_SM2.cer", SM3_WITH_SM2);
	// 	System.out.println("SM2:" + ret);
	// }
}
