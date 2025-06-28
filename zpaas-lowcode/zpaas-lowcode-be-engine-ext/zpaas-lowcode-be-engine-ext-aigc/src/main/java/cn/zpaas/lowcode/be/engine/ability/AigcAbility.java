package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.AigcClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy Aigc能力封装类
 */
public class AigcAbility {

	private static Logger logger = LoggerFactory.getLogger(AigcAbility.class);

	public static final String CHAT_PATH = "chatPath";
	public static final String GENERATE_PATH = "generatePath";
	public static final String GENERATE_API = "/api/generate";
	public static final String CHAT_API = "/api/chat";

	// AigcAbility实例，实现单例模式
	private static AigcAbility instance = null;
	// AigcAbility，第一层Key是systemId，第二层Key是aigcServerId
	private Map<String, Map<String, AigcClient>> aigcMap = new HashMap<>();

	// 私有化构造函数
	private AigcAbility() {

	}

	public static AigcAbility getInstance() {
		return instance;
	}

	/**
	 * AigcAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache aigcServer resource...");
		}
		// 初始化AigcAbility实例
		AigcAbility repository = new AigcAbility();
		
		// 加载ServerResource数据-aigc
		List<ServerResource> serverResources = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_AIGC);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_AIGC);
		}

		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条aigc server数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, AigcClient> map = repository.aigcMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.aigcMap.put(serverResource.getSystemId(), map);
				}
				String chatPath = null, generatePath = null;
				if(!StringUtils.isBlank(serverResource.getServerCfg())) {
					JsonObject serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
					chatPath = JsonUtils.getString(serverCfg, CHAT_PATH);
					generatePath = JsonUtils.getString(serverCfg, GENERATE_PATH);
				}
				map.put(serverResource.getId(), new AigcClient(serverResource.getUrl(), generatePath, chatPath));
			}
		} else {
			logger.info("no valid MinIO ServerResource.");
		}

		// 初始化完成，赋值给属性instance
		instance = repository;
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache aigc resource...");
		}
		// 加载ServerResource数据-aigc
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_AIGC);
		Map<String, AigcClient> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条aigc server数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				String chatPath = null, generatePath = null;
				if(!StringUtils.isBlank(serverResource.getServerCfg())) {
					JsonObject serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
					chatPath = JsonUtils.getString(serverCfg, CHAT_PATH);
					generatePath = JsonUtils.getString(serverCfg, GENERATE_PATH);
				}
				map.put(serverResource.getId(), new AigcClient(serverResource.getUrl(), generatePath, chatPath));
			}
		} 

		instance.aigcMap.put(systemId, map);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 获取AIGC客户端
	 * @param systemId
	 * @param aigcResourceId
	 * @return
	 */
	public AigcClient getAigcClient(String systemId, String aigcResourceId) {
		Map<String, AigcClient> map = aigcMap.get(systemId);
		if(map != null) {
			return map.get(aigcResourceId);
		}else {
			return null;
		}
	}
	
	/**
	 * 调用aigc服务
	 * @param systemId
	 * @param aigcResourceId
	 * @param model
	 * @param invokeApi
	 * @param isStream
	 * @param options
	 * @param message
	 * @param historyMsgs
	 * @param sseEmitter
	 * @return
	 * @throws EngineException
	 */
	public Object invokeAigc(String systemId, String aigcResourceId, String model, String invokeApi, String isStream, String options, String message, JsonArray historyMsgs, SseEmitter sseEmitter, String userName, String assistantName) throws EngineException{
		AigcClient aigcClient = this.getAigcClient(systemId, aigcResourceId);
		if(aigcClient == null) {
			logger.error("aigcClient can't be null. businessflowNodeId: {}", aigcResourceId);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "aigcClient can't be null. ");
		}
		JsonObject chatTemplate = new JsonObject();
		chatTemplate.addProperty(AigcClient.MODEL_KEY, model);
		chatTemplate.addProperty(AigcClient.STREAM_KEY, (YesOrNo.YES.equals(isStream) ? true : false));
		chatTemplate.add(AigcClient.OPTIONS_KEY, JsonUtils.toJsonObject(options));
		if(GENERATE_API.equals(invokeApi)) {
			if(YesOrNo.YES.equals(isStream) && sseEmitter != null) {
				return aigcClient.generate(message, JsonUtils.toJson(chatTemplate), sseEmitter, userName, assistantName);
			}else {
				return aigcClient.generate(message, JsonUtils.toJson(chatTemplate));
			}
		}else {
			if(YesOrNo.YES.equals(isStream) && sseEmitter != null) {
				return aigcClient.chat(message, JsonUtils.toJson(chatTemplate), historyMsgs, sseEmitter, userName, assistantName);
			}else {
				return aigcClient.chat(message, JsonUtils.toJson(chatTemplate), historyMsgs);
			}
		}
	}
}
