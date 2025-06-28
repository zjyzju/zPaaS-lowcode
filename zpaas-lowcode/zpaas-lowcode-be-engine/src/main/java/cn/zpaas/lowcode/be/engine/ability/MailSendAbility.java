package cn.zpaas.lowcode.be.engine.ability;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.vo.MailInfo;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.MailClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 邮件发送能力方法封装
 */
public class MailSendAbility {

	private static Logger logger = LoggerFactory.getLogger(MailSendAbility.class);

	// MainSendAbility实例，实现单例模式
	private static MailSendAbility instance = null;
	// MailClient的缓存对象，第一层Key是systemId，第二层Key是ftpId
	private Map<String, Map<String, MailClient>> mailClientMap = new HashMap<>();

	// 私有化构造函数
	private MailSendAbility() {

	}

	public static MailSendAbility getInstance() {
		return instance;
	}

	/**
	 * MainSendAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache SMTP server resource to mappingMap...");
		}
		// 初始化MainSendAbility实例
		MailSendAbility repository = new MailSendAbility();
		
		// 加载ServerResource数据-SMTP服务
		List<ServerResource> serverResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_SMTP);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SMTP);
		}

		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条SMTP服务数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, MailClient> map = repository.mailClientMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.mailClientMap.put(serverResource.getSystemId(), map);
				}
				Properties smtpProps = new Properties();
				smtpProps.put(MailClient.MAIL_AUTH_USER, serverResource.getUsername());//用户名
				smtpProps.put(MailClient.MAIL_AUTH_CODE, serverResource.getPassword());//密码
				smtpProps.put(MailClient.MAIL_AUTH_HOST, serverResource.getHost());//主机地址
				if(serverResource.getPort() != null && serverResource.getPort().trim().length() > 0) {//有设置端口时
					smtpProps.put(MailClient.MAIL_AUTH_PORT, serverResource.getPort());//端口
				}
				//其他配置信息
				if(serverResource.getServerCfg() != null && serverResource.getServerCfg().trim().length() > 0) {
					try {
						JsonObject serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
						smtpProps.putAll(JsonUtils.toMap(serverCfg));
					} catch (Exception e) {
						logger.error("server cfg is not json string!", e);
					}
				}
				map.put(serverResource.getId(), new MailClient(smtpProps));
			}
		} else {
			logger.info("no valid SMTP ServerResource.");
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
			logger.debug("load and cache SMTP server resource to mappingMap...");
		}
		// 加载ServerResource数据-SMTP服务
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SMTP);
		
		Map<String, MailClient> map = new HashMap<>();
		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条SMTP服务数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Properties smtpProps = new Properties();
				smtpProps.put(MailClient.MAIL_AUTH_USER, serverResource.getUsername());//用户名
				smtpProps.put(MailClient.MAIL_AUTH_CODE, serverResource.getPassword());//密码
				smtpProps.put(MailClient.MAIL_AUTH_HOST, serverResource.getHost());//主机地址
				if(serverResource.getPort() != null && serverResource.getPort().trim().length() > 0) {//有设置端口时
					smtpProps.put(MailClient.MAIL_AUTH_PORT, serverResource.getPort());//端口
				}
				//其他配置信息
				if(serverResource.getServerCfg() != null && serverResource.getServerCfg().trim().length() > 0) {
					try {
						JsonObject serverCfg = JsonUtils.toJsonObject(serverResource.getServerCfg());
						smtpProps.putAll(JsonUtils.toMap(serverCfg));
					} catch (Exception e) {
						logger.error("server cfg is not json string!", e);
					}
				}
				map.put(serverResource.getId(), new MailClient(smtpProps));
			}
		} 
		instance.mailClientMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据systemId和smtp资源Id获取mailClient客户端实例
	 * @param systemId
	 * @param mailClientResourceId
	 * @return
	 */
	public MailClient getMailClient(String systemId, String smtpResourceId) {
		Map<String, MailClient> map = mailClientMap.get(systemId);
		if(map != null) {
			return map.get(smtpResourceId);
		}else {
			return null;
		}
	}
	
	/**
	 * 发送email
	 * @param mailInfo
	 * @param systemId
	 * @param smtpResourceId
	 * @throws EngineException
	 */
	public void sendMail(MailInfo mailInfo, String systemId, String smtpResourceId) throws EngineException{
		//获取MailClient实例
		MailClient mailClient = this.getMailClient(systemId, smtpResourceId);		
		if(mailClient == null) {
			logger.error("get smtp server instance failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "get smtp server instance failed!");
		}
		//发送email
		mailClient.sendMail(mailInfo);
	}
}
