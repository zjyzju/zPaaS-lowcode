package cn.zpaas.lowcode.be.engine.ability;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.SmsGatewayClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 短信发送能力方法封装
 */
public class SmsSendAbility {

	private static Logger logger = LoggerFactory.getLogger(SmsSendAbility.class);

	// SmsSendAbility实例，实现单例模式
	private static SmsSendAbility instance = null;
	// SmsGatewayClient的缓存对象，第一层Key是systemId，第二层Key是smsGatewayId
	private Map<String, Map<String, SmsGatewayClient>> smsGatewayClientMap = new HashMap<>();

	// 私有化构造函数
	private SmsSendAbility() {

	}

	public static SmsSendAbility getInstance() {
		return instance;
	}

	/**
	 * SmsSendAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache SMS Gateway server resource to mappingMap...");
		}
		// 初始化SmsSendAbility实例
		SmsSendAbility repository = new SmsSendAbility();
		
		// 加载ServerResource数据-短信网关服务
		List<ServerResource> serverResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_SMS_GATEWAY);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SMS_GATEWAY);
		}

		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条短信网关服务数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, SmsGatewayClient> map = repository.smsGatewayClientMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.smsGatewayClientMap.put(serverResource.getSystemId(), map);
				}
				if(serverResource.getHost() == null || serverResource.getHost().trim().length() == 0 ||
						serverResource.getPort() == null || serverResource.getPort().trim().length() == 0) {
					logger.error("host or port can't be null! serverResourceId: {}.", serverResource.getId());
				}
				//其他配置信息
				JsonObject serverCfg = null;
				if(serverResource.getServerCfg() != null && serverResource.getServerCfg().trim().length() > 0) {
					try {
						serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
					} catch (Exception e) {
						logger.error("server cfg is not json string!", e);
					}
				}
				map.put(serverResource.getId(), new SmsGatewayClient(serverResource.getUsername(), serverResource.getPassword(), serverResource.getHost(), serverResource.getPort(), serverCfg));
			}
		} else {
			logger.info("no valid 短信网关 ServerResource.");
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
			logger.debug("load and cache SMS Gateway server resource to mappingMap...");
		}
		// 加载ServerResource数据-短信网关服务
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SMS_GATEWAY);
		Map<String, SmsGatewayClient> map = new HashMap<>();
		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条短信网关服务数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				if(serverResource.getHost() == null || serverResource.getHost().trim().length() == 0 ||
						serverResource.getPort() == null || serverResource.getPort().trim().length() == 0) {
					logger.error("host or port can't be null! serverResourceId: {}.", serverResource.getId());
				}
				//其他配置信息
				JsonObject serverCfg = null;
				if(serverResource.getServerCfg() != null && serverResource.getServerCfg().trim().length() > 0) {
					try {
						serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
					} catch (Exception e) {
						logger.error("server cfg is not json string!", e);
					}
				}
				map.put(serverResource.getId(), new SmsGatewayClient(serverResource.getUsername(), serverResource.getPassword(), serverResource.getHost(), serverResource.getPort(), serverCfg));
			}
		} 
		instance.smsGatewayClientMap.put(systemId, map);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据systemId和短信网关资源Id获取smsGatewayClient客户端实例
	 * @param systemId
	 * @param smsGatewayId
	 * @return
	 */
	public SmsGatewayClient getSmsGatewayClient(String systemId, String smsGatewayId) {
		Map<String, SmsGatewayClient> map = smsGatewayClientMap.get(systemId);
		if(map != null) {
			return map.get(smsGatewayId);
		}else {
			return null;
		}
	}
	
	/**
	 * 发送sms
	 * @param receivers
	 * @param message
	 * @param systemId
	 * @param smsGatewayId
	 * @throws EngineException
	 */
	public void sendSMS(String receivers, String message, String systemId, String smsGatewayId) throws EngineException{
		//获取SmsGatewayClient实例
		SmsGatewayClient smsGatewayClient = this.getSmsGatewayClient(systemId, smsGatewayId);		
		if(smsGatewayClient == null) {
			logger.error("get sms gateway server instance failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "get sms gateway server instance failed!");
		}
		//发送sms
		smsGatewayClient.sendSMS(receivers, message);
	}
}
