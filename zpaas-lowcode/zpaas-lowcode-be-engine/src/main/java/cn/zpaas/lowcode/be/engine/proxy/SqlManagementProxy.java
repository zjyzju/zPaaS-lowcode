package cn.zpaas.lowcode.be.engine.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.SqlManagement;

/**
 * @author zjy
 * SqlManagementProxy类是统一管理SQL的缓存类
 */
public class SqlManagementProxy {
	private static Logger logger = LoggerFactory.getLogger(SqlManagementProxy.class);
	
	//用来缓存SqlManagement对象，第一层Key是systemId，第二层Key是dbSchemaId
	private static Map<String, Map<String, SqlManagement>> sqlMap = null;
	
	
	/**
	 * 用于初始化SqlManagementProxy，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		
		sqlMap = new HashMap<>();
		
		//获取SqlManagement列表，状态为有效的记录
		List<SqlManagement> sqlManagements = null;
		
		if(StringUtils.isBlank(systemId)) {//加载所有的记录
			sqlManagements = initService.listSqlManagements();
		}else {//加载指定业务系统的记录
			sqlManagements = initService.listSqlManagementsBySystemId(systemId);
		}
		//如果获取的列表为空，则直接返回
		if(CollectionUtils.isEmpty(sqlManagements)) {
			logger.info("no sqlManagement is configured. return.");
			return;
		}
		
		//循环处理每个SqlManagement，并进行初始化
		for(SqlManagement sqlManagement : sqlManagements) {
			//如果systemId为空，则忽略
			if(StringUtils.isBlank(sqlManagement.getSystemId())) {
				logger.error("sqlManagement({})'s systemId is null", sqlManagement.getSystemId());
				continue;
			}
			
			//缓存SqlManagement
			Map<String, SqlManagement> map = sqlMap.get(sqlManagement.getSystemId());
			if(map == null) {
				map = new HashMap<>();
				sqlMap.put(sqlManagement.getSystemId(), map);
			}
			map.put(sqlManagement.getId(), sqlManagement);	
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		
		//获取SqlManagement列表，状态为有效的记录
		List<SqlManagement> sqlManagements = initService.listSqlManagementsBySystemId(systemId);
		Map<String, SqlManagement> map = new HashMap<>();
		//循环处理每个SqlManagement，并进行初始化
		if(!CollectionUtils.isEmpty(sqlManagements)) {
			for(SqlManagement sqlManagement : sqlManagements) {
				//如果systemId为空，则忽略
				if(StringUtils.isBlank(sqlManagement.getSystemId())) {
					logger.error("sqlManagement({})'s systemId is null", sqlManagement.getSystemId());
					continue;
				}
				
				map.put(sqlManagement.getId(), sqlManagement);	
			}
		}
		sqlMap.put(systemId, map);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据systemId和sqlId，从sqlMap中获取SqlManagement对象并返回
	 * @param systemId 业务系统标识
	 * @param sqlId 
	 * @return SqlManagement对象
	 */
	public static SqlManagement getSqlManagement(String systemId, String sqlId) {
		Map<String, SqlManagement> map = sqlMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(sqlId);
	}
}
