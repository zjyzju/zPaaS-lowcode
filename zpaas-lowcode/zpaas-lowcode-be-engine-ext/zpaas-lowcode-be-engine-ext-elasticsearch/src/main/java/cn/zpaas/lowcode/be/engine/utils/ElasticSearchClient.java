package cn.zpaas.lowcode.be.engine.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import co.elastic.clients.util.ContentType;

/**
 * ElasticSearch存取能力实现类
 *
 * @author zjy
 * createTime 2025年04月27日-17:53:38
 */
public class ElasticSearchClient {
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class);
	
	private static final List<String> numericTypes = new ArrayList<>();
	
	public static final String CFG_ES_MAIN_VERSION_KEY = "mainVersion";//保存ES主版本号的key
	private static final String QUERY_STRING_FOR_LESS_THEN_SEVEN = "%s/%s/%s/_search?ignore_unavailable=true";
	private static final String QUERY_STRING_FOR_GREAT_EQUAL_SEVEN = "%s/%s/_search?ignore_unavailable=true";
	private static final String INDEX_KEY = "index";
	private static final String TYPE_KEY = "type";

    //ES 大版本号
    private int mainVersion;
    //ES 数据源访问信息
    private String userName;
    private String password;
    private String url;
    private String serverCfg;
    //HttpClientContext信息
    private HttpClientContext httpClientContext;
    //HttpClientBuilder信息
    private HttpClientBuilder httpClientBuilder;

    static {
        numericTypes.add("long");
        numericTypes.add("integer");
        numericTypes.add("short");
        numericTypes.add("byte");
        numericTypes.add("double");
        numericTypes.add("float");
    }
    
    public ElasticSearchClient(String url, String username, String password, String serverCfg) {
    	this.url = url;
    	this.userName = username;
    	this.password = password;
    	this.serverCfg = serverCfg;
        this.httpClientBuilder = HttpClientBuilder.create();
        this.httpClientContext = getHttpContext();
        this.mainVersion = 7;//主版本号默认为7
        //当数据源配置中配置了版本号，则使用配置的版本号
        if(!StringUtils.isBlank(this.serverCfg)) {
        	JsonObject cfg = JsonUtils.toJsonObject(this.serverCfg);
        	if(cfg.get(CFG_ES_MAIN_VERSION_KEY) != null) {
        		this.mainVersion = JsonUtils.getInteger(cfg, CFG_ES_MAIN_VERSION_KEY);
        	}
        }
    }

    /**
     * 通过Post方式查询ES
     * @param url
     * @param request
     * @return
     * @throws EngineException
     */
    public JsonObject post(String url, JsonObject reqData) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Execute ES Query, post url is {}", url);
    	}
    	
        try {
        	//构造ES POST请求
			HttpPost httpPost = new HttpPost(url);
			StringEntity reqEntity = new StringEntity(reqData.toString());
			reqEntity.setContentType(ContentType.APPLICATION_JSON.toString());
			httpPost.setEntity(reqEntity);
			
			//发送请求
			HttpResponse httpResponse = this.httpClientBuilder.build().execute(httpPost, this.httpClientContext);			
			String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
			if(logger.isDebugEnabled()) {
				logger.debug("Execute ES Query, post url is {}, response is {}", url, response);
			}
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            return JsonUtils.toJsonObject(response);
	        } else {
	            throw new EngineException(ResultStatus.BUSINESS_ERROR, "ES query failed!");
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw new EngineException(ResultStatus.BUSINESS_ERROR,"execute ES query failed!",e.getMessage());
		} 
       
        
    }

   /**
    * 通过GET方式查询ES
    * @param url
    * @return
    * @throws EngineException
    */
    public JsonObject get(String url) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Execute ES Query, get url is {}", url);
    	}
        try {
        	//构造ES GET请求
			HttpGet httpGet = new HttpGet(url);
			//发送请求
			HttpResponse httpResponse = this.httpClientBuilder.build().execute(httpGet, this.httpClientContext);	
			String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
			if(logger.isDebugEnabled()) {
				logger.debug("Execute ES Query, get url is {}, response is {}", url, response);
			}
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            return JsonUtils.toJsonObject(response);
	        } else {
	            throw new EngineException(ResultStatus.BUSINESS_ERROR, "ES query failed!");
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw new EngineException(ResultStatus.BUSINESS_ERROR,"execute ES query failed!",e.getMessage());
		} 
    }

    /**
     * 初始化HttpClientContext
     * @return
     */
    private HttpClientContext getHttpContext() {
    	//创建HttpClientContext
        HttpClientContext context = HttpClientContext.create();
        //如果没有设置用户名密码，直接返回
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
        	if(logger.isDebugEnabled()) {
        		logger.debug("ES's username or password is null!");
        	}
            return context;
        }
        //创建认证信息
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(AuthScope.ANY),
                new UsernamePasswordCredentials(userName, password)
        );
        context.setCredentialsProvider(provider);
        AuthCache authCache = new BasicAuthCache();
        context.setAuthCache(authCache);
        return context;
    }

    /**
     * 根据版本不同组织不同的url
     * @param reqData
     * @return
     */
    public String getSearchUrl(JsonObject reqData) {
        if (this.mainVersion < 7) {//7以前的版本请求URL中包含type
            return String.format(QUERY_STRING_FOR_LESS_THEN_SEVEN, url, JsonUtils.getString(reqData, INDEX_KEY), JsonUtils.getString(reqData, TYPE_KEY));
        } else {//7及以后的版本不需要type
            return String.format(QUERY_STRING_FOR_GREAT_EQUAL_SEVEN, url, JsonUtils.getString(reqData, INDEX_KEY));
        }
    }
}
