package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.ElasticSearchClient;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * ElasticSearch存取能力实现类
 */
public class ElasticSearchRepositoryAbility {
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchRepositoryAbility.class);
	
	// ElasticSearchRepositoryAbility实例，实现单例模式
	private static ElasticSearchRepositoryAbility instance = null;
	// ElasticSearchClient的缓存对象，第一层Key是systemId，第二层Key是resourceId
	private Map<String, Map<String, ElasticSearchClient>> esMap = new HashMap<>();
    
	// 私有化构造函数
	private ElasticSearchRepositoryAbility() {

	}

	public static ElasticSearchRepositoryAbility getInstance() {
		return instance;
	}
	
	/**
	 * 根据systemId和资源Id获取ElasticSearchClient客户端实例
	 * @param systemId
	 * @param resourceId
	 * @return
	 */
	public ElasticSearchClient getElasticSearchClient(String systemId, String resourceId) {
		Map<String, ElasticSearchClient> map = esMap.get(systemId);
		if(map != null) {
			return map.get(resourceId);
		}else {
			return null;
		}
	}

	/**
	 * ElasticSearchRepositoryAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache ES Server resource");
		}
		// 初始化ElasticSearchRepositoryAbility实例
		ElasticSearchRepositoryAbility repository = new ElasticSearchRepositoryAbility();

		// 加载ServerResource数据
		List<ServerResource> serverResources = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_ES);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_ES);
		}

		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, ElasticSearchClient> map = repository.esMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.esMap.put(serverResource.getSystemId(), map);
				}
				map.put(serverResource.getId(), new ElasticSearchClient(serverResource.getUrl(),
						serverResource.getUsername(), serverResource.getPassword(), serverResource.getServerCfg()));
			}
		} else {
			logger.info("no valid ServerResource.");
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
			logger.debug("load and cache ES Server resource");
		}
		// 加载ServerResource数据
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_ES);
		Map<String, ElasticSearchClient> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				map.put(serverResource.getId(), new ElasticSearchClient(serverResource.getUrl(),
						serverResource.getUsername(), serverResource.getPassword(), serverResource.getServerCfg()));
			}
		} 
		instance.esMap.put(systemId, map);
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

    /**
     * 通过Post方式查询ES
     * @param url
     * @param request
     * @return
     * @throws EngineException
     */
    public JsonObject post(String url, JsonObject reqData, String systemId, String resourceId) throws EngineException {
    	JsonObject result = this.getElasticSearchClient(systemId, resourceId).post(url, reqData);
       return result;
    }

   /**
    * 通过GET方式查询ES
    * @param url
    * @return
    * @throws EngineException
    */
    public JsonObject get(String url, String systemId, String resourceId) throws EngineException {
    	JsonObject result = this.getElasticSearchClient(systemId, resourceId).get(url);
        return result; 
    }

    /**
     * 根据版本不同组织不同的url
     * @param reqData
     * @return
     */
    public String getSearchUrl(JsonObject reqData, String systemId, String resourceId) {
    	return this.getElasticSearchClient(systemId, resourceId).getSearchUrl(reqData);
    }
	
}
