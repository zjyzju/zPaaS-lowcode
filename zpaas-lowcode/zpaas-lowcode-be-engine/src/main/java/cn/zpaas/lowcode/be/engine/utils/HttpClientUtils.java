package cn.zpaas.lowcode.be.engine.utils;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import jakarta.servlet.http.Cookie;

/**
 *  HttpClient工具类
 *
 * @author zjy
 * createTime 2025年04月16日-11:09:52
 */
public class HttpClientUtils {
    public static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	 /**
     * 创建HttpClient
     * @param connectTimeout
     * @param ignoreCertVerify
     * @return
     * @throws EngineException
     */
    public static HttpClient buildeHttpClient(Long connectTimeout, boolean ignoreCertVerify) throws EngineException{
		return buildeHttpClient(connectTimeout, ignoreCertVerify, null, null);
	}

    /**
     * 创建HttpClient
     * @param connectTimeout
     * @param ignoreCertVerify
     * @return
     * @throws EngineException
     */
    public static HttpClient buildeHttpClient(Long connectTimeout, boolean ignoreCertVerify,  String requestUri,  Cookie[] cookies) throws EngineException{
        //设置httpClient的参数
		HttpClient.Builder builder = HttpClient.newBuilder();
		if(connectTimeout != null) {
			builder.connectTimeout(Duration.ofSeconds(connectTimeout));
		}
		// if(readTimeout != null) {
		// 	builder.readTimeout(readTimeout, TimeUnit.SECONDS);
		// }
		// if(writeTimeout != null) {
		// 	builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
		// }

        //忽略Https证书校验
		if(ignoreCertVerify) {
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("SSL");
				TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}
						public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
						}
						public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
						}
					}
				};
				System.setProperty("jdk.internal.httpclient.disableHostnameVerification","true");//取消主机名验证
				sslContext.init(null, trustAllCerts, new SecureRandom());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				logger.error("ignore cert verify failed!", e);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "ignore cert verify failed!", e.getMessage(), e);
			}

			SSLParameters sslParams = new SSLParameters();	
			sslParams.setEndpointIdentificationAlgorithm("");

			builder.sslContext(sslContext).sslParameters(sslParams);
		}
		if(cookies != null && cookies.length > 0) {//设置cookie
			CookieManager cookieManager = new CookieManager();
			cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			for(Cookie cookie : cookies) {
				HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
				httpCookie.setDomain(requestUri);
				httpCookie.setPath("/");
				cookieManager.getCookieStore().add(URI.create(requestUri), httpCookie);
			}
			builder.cookieHandler(cookieManager);
		}
		
        return builder.build();
    }

}
