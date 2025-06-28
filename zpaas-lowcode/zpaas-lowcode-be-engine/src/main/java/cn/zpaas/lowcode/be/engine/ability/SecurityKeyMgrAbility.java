package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;

/**
 * @author zjy
 * 安全密钥资源管理能力
 */
public class SecurityKeyMgrAbility {
	public static Logger logger = LoggerFactory.getLogger(SecurityKeyMgrAbility.class);
	
	//单例实例
	private static SecurityKeyMgrAbility instance = null;
	
	// 用来缓存SecurityKeyResource对象数据的Map
	private Map<String, Map<String, SecurityKeyResource>> resourceMap = new HashMap<>();

	// 私有化构造函数
	private SecurityKeyMgrAbility() {
		
	}
	
	public static SecurityKeyMgrAbility getInstance() {
		return instance;
	}
	
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		SecurityKeyMgrAbility securityKeyMgrAbility = new SecurityKeyMgrAbility();
		
		// 加载SecurityKeyResource数据
		List<SecurityKeyResource> securityKeyResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			securityKeyResources = initService.listSecurityKeyResources();
		} else {// 加载指定业务系统的数据
			securityKeyResources = initService.listSecurityKeyResources(systemId);
		}

		if (!CollectionUtils.isEmpty(securityKeyResources)) {
			for(SecurityKeyResource securityKeyResource : securityKeyResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(securityKeyResource.getSystemId())) {
					continue;
				}
				// 将SecurityKeyResource对象加入缓存
				Map<String, SecurityKeyResource> map = securityKeyMgrAbility.resourceMap.get(securityKeyResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					securityKeyMgrAbility.resourceMap.put(securityKeyResource.getSystemId(), map);
				}
				map.put(securityKeyResource.getId(), securityKeyResource);
			}
		} else {
			logger.info("no valid WorkflowProcessResource.");
		}

		// 初始化完成，赋值给属性instance
		instance = securityKeyMgrAbility;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		// 加载SecurityKeyResource数据
		List<SecurityKeyResource> securityKeyResources = initService.listSecurityKeyResources(systemId);
		Map<String, SecurityKeyResource> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(securityKeyResources)) {
			for(SecurityKeyResource securityKeyResource : securityKeyResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(securityKeyResource.getSystemId())) {
					continue;
				}
				// 将WorkflowProcessResource对象加入缓存
				map.put(securityKeyResource.getId(), securityKeyResource);
			}
		} 
		instance.resourceMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和安全密钥资源标识获取对应的安全密钥资源对象
	 * 
	 * @param systemId      业务系统标识
	 * @param resourceId 安全密钥资源标识
	 * @return 返回WSecurityKeyResource对象
	 */
	public SecurityKeyResource getSecurityKeyResource(String systemId, String resourceId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, SecurityKeyResource> map = resourceMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(resourceId);
	}
}
