package cn.zpaas.lowcode.be.engine.flow.node;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.utils.HttpClientUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 业务流Rest远程方法调用节点的实现类，主要实现远程Rest方法的调用。
 */
public class RestInvokeNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(RestInvokeNode.class);

	private static final String CFG_HTTP_METHOD_KEY = "httpMethod"; //调用Rest方法的httpMethod存放的Key
	private static final String CFG_URL_KEY = "url"; //调用Rest方法的URL存放的Key
	private static final String CFG_CONNECT_TIMEOUT_KEY = "connectTimeout"; //httpClient的connectTimeout参数存放的Key
	// private static final String CFG_READ_TIMEOUT_KEY = "readTimeout"; //httpClient的readTimeout参数存放的Key
	// private static final String CFG_WRITE_TIMEOUT_KEY = "writeTimeout"; //httpClient的writeTimeout参数存放的Key
	private static final String CFG_IN_PARAM_INSTANCE_SOURCE_KEY = "inParamInstanceSource"; //参数实例来源存放的Key
	private static final String CFG_IN_PARAM_INSTANCE_KEY_KEY = "inParamInstanceKey"; //参数Key存放的Key
	private static final String CFG_IS_URI_PARAM_KEY = "isUriParam"; //是否Uri参数存放的Key
	private static final String CFG_RESULT_KEY_KEY = "resultKey"; //是否resultKey参数存放的Key
	private static final String CFG_IGNORE_CERT_VERIFY_KEY = "ignoreCertVerify"; //忽略证书校验存放的Key
	private static final String CFG_HEADER_SOURCE_KEY = "headerSource"; //header参数来源存放的Key
	private static final String CFG_HEADER_KEY_KEY = "headerKey"; //header参数Key存放的Key
	
	//private static final String RESULT_DATA_KEY = "data"; //Result类中返回数据对应的属性
	
	
	
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		String httpMethod = JsonUtils.getString(nodeCfg, CFG_HTTP_METHOD_KEY);
		String url = JsonUtils.getString(nodeCfg, CFG_URL_KEY);
		Long connectTimeout = JsonUtils.getLong(nodeCfg, CFG_CONNECT_TIMEOUT_KEY);
		// Long readTimeout = JsonUtils.getLong(nodeCfg, CFG_READ_TIMEOUT_KEY);
		// Long writeTimeout = JsonUtils.getLong(nodeCfg, CFG_WRITE_TIMEOUT_KEY);
		String inParamInstanceSource = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_SOURCE_KEY);
		String inParamInstanceKey = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_KEY_KEY);
		boolean isUriParam = JsonUtils.getBoolean(nodeCfg, CFG_IS_URI_PARAM_KEY);
		String resultKey = JsonUtils.getString(nodeCfg, CFG_RESULT_KEY_KEY);
		String headerSource = JsonUtils.getString(nodeCfg, CFG_HEADER_SOURCE_KEY);
		String headerKey = JsonUtils.getString(nodeCfg, CFG_HEADER_KEY_KEY);
		boolean ignoreCertVerify = JsonUtils.getBoolean(nodeCfg, CFG_IGNORE_CERT_VERIFY_KEY);
		
		boolean isListResult = JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
		
		
		//调用目标方法相关的信息不能为空
		if(StringUtils.isBlank(httpMethod ) || StringUtils.isBlank(url)) {
			logger.error("T[{}] httpMethod and url can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "httpMethod and url  can't be null. ");
		}
		
		//根据配置获取输入参数
		JsonObject inParam = (JsonObject)getContextObject(inParamInstanceSource, inParamInstanceKey, false, null, context, businessFlowNode);
		if(inParam == null) {
			inParam = new JsonObject();
		}
		
		//转换输入参数
		Map<String, Object> requestParaMap = null;
		JsonObject bodyParamObject = null;
		if(!isUriParam) {
			bodyParamObject = inParam;
		}else {
			requestParaMap = new HashMap<>();
			for(String key : inParam.keySet()) {
				requestParaMap.put(key, JsonUtils.getObject(inParam, key));
			}
		}

		//根据配置获取header参数
		JsonObject headerParam = (JsonObject)getContextObject(headerSource, headerKey, false, null, context, businessFlowNode);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
		if(!JsonUtils.isEmpty(headerParam)) {
			for(String key : headerParam.keySet()) {
				headers.add(key, JsonUtils.getString(headerParam, key));
			}
		}
		
		HttpEntity<JsonObject> request = new HttpEntity<JsonObject>(bodyParamObject, headers);

		//初始化RestTemplate
		HttpClient httpClient = HttpClientUtils.buildeHttpClient(connectTimeout, ignoreCertVerify);
		RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory(httpClient));
		restTemplate.setMessageConverters(SpringContextUtils.getBean(HttpMessageConverters.class).getConverters());
		
		Object result = null;
		//resultKey为空，返回体就是返回数据
		if(StringUtils.isBlank(resultKey)) {
			//进行目标方法调用
			if(isUriParam) {
				if(isListResult) {
					ResponseEntity<JsonArray> listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonArray.class, requestParaMap);
					if(listResult != null && HttpStatus.OK.equals(listResult.getStatusCode())) {
						result = listResult.getBody();
					}else {
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invode rest service failed!");
					}
				}else {
					ResponseEntity<JsonObject> listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonObject.class, requestParaMap);
					if(listResult != null && HttpStatus.OK.equals(listResult.getStatusCode())) {
						result = listResult.getBody();
					}else {
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invode rest service failed!");
					}
				}
				
			}else {
				if(isListResult) {
					ResponseEntity<JsonArray> listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonArray.class);
					if(listResult != null && HttpStatus.OK.equals(listResult.getStatusCode())) {
						result = listResult.getBody();
					}else {
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invode rest service failed!");
					}
				}else {
					ResponseEntity<JsonObject> listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonObject.class);
					if(listResult != null && HttpStatus.OK.equals(listResult.getStatusCode())) {
						result = listResult.getBody();
					}else {
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "invode rest service failed!");
					}
				}
			}
		}else {
			ResponseEntity<JsonObject> listResult = null;
			//进行目标方法调用
			if(isUriParam) {
				listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonObject.class, requestParaMap);
			}else {
				listResult = restTemplate.exchange(url, HttpMethod.valueOf(httpMethod), request, JsonObject.class);
			}
			if(listResult != null && HttpStatus.OK.equals(listResult.getStatusCode())) {
				if(isListResult) {
					result = JsonUtils.getJsonArray(listResult.getBody(), resultKey);
				}else {
					result = JsonUtils.getObject(listResult.getBody(), resultKey);
				}
			}else {
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invode rest service failed!");
			}
		}
		
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
		
	}
	
}
