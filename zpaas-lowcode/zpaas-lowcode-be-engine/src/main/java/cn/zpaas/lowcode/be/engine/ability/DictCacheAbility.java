package cn.zpaas.lowcode.be.engine.ability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.DictResource;

/**
 * @author zjy 字典缓存实现类
 */
public class DictCacheAbility {
	private static Logger logger = LoggerFactory.getLogger(DictCacheAbility.class);

	public static final String DICT_CACHE_PREFIX = "dict_cache_";//字典缓存前缀
	public static final String DICT_VALUE_CACHE_PREFIX = "dict_value_cache_";//字典值缓存前缀
	public static final String DICT_SUB_CACHE_PREFIX = "dict_sub_cache_";//字典子列表缓存前缀
	public static final String DEFAULT_CAFFEINE_SPEC_KEY = "caffeine.spec";//默认缓存配置KEY
	public static final String DEFAULT_CAFFEINE_SPEC = "maximumSize=10000";//默认缓存配置
	public static final String DICT_CACHE_CAFFEINE_SPEC_KEY = "caffeine.dictSpecKey";//默认字典缓存配置KEY的KEY
	public static final String STATUS_VALUE_SEPERATE_FLAG = ",";//状态值分隔符

	public static final String DICT_RESOURCE_ID = "dictResourceId";
	public static final String DICT_CODE = "dictCode";
	public static final String DICT_NAME = "dictName";
	public static final String DICT_VALUE = "dictValue";
	public static final String DICT_VALUE_LABEL = "dictValueLabel";
	public static final String DICT_VALUE2 = "dictValue2";
	public static final String DICT_VALUE3 = "dictValue3";
	public static final String PARENT_DICT_CODE = "parentDictCode";
	public static final String PARENT_DICT_VALUE = "parentDictValue";

	// DictCacheAbility实例，实现单例模式
	private static DictCacheAbility instance = null;
	// DictResource的缓存对象，第一层Key是systemId，第二层Key是resourceId
	private Map<String, Map<String, DictResource>> dictResourceMap = new HashMap<>();
	private Map<String, Map<String, Cache<String, Object>>> dictCacheMap = new HashMap<>();

	// 私有化构造函数
	private DictCacheAbility() {

	}

	public static DictCacheAbility getInstance() {
		return instance;
	}

	/**
	 * 根据systemId和资源Id获取DictResource
	 * 
	 * @param systemId
	 * @param resourceId
	 * @return
	 */
	public DictResource getDictResource(String systemId, String resourceId) {
		Map<String, DictResource> map = dictResourceMap.get(systemId);
		if (map != null) {
			return map.get(resourceId);
		} else {
			return null;
		}
	}

	/**
	 * 根据systemId和资源Id获取DictResource
	 * 
	 * @param systemId
	 * @param resourceId
	 * @return
	 */
	private Cache<String, Object> getDictCache(String systemId, String resourceId) {
		Map<String, Cache<String, Object>> map = dictCacheMap.get(systemId);
		if (map != null) {
			return map.get(resourceId);
		} else {
			return null;
		}
	}

	/**
	 * DictCacheAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache DictResource");
		}
		// 初始化DictCacheAbility实例
		DictCacheAbility repository = new DictCacheAbility();

		// 加载DictResource数据
		List<DictResource> dictResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			dictResources = initService.listDictResources();
		} else {// 加载指定业务系统的数据
			dictResources = initService.listDictResources(systemId);
		}

		if (!CollectionUtils.isEmpty(dictResources)) {
			String dictSpecKey = SpringContextUtils.getEnvironmentProperty(DICT_CACHE_CAFFEINE_SPEC_KEY, DEFAULT_CAFFEINE_SPEC_KEY);
			// 循环处理每条DictResource数据
			for (DictResource dictResource : dictResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dictResource.getSystemId())) {
					continue;
				}
				// 将DictResource对象加入缓存
				Map<String, DictResource> map = repository.dictResourceMap.get(dictResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.dictResourceMap.put(dictResource.getSystemId(), map);
				}
				map.put(dictResource.getId(), dictResource);
				
				//加载字典缓存
				Map<String, Cache<String, Object>> cacheMap = repository.dictCacheMap.get(dictResource.getSystemId());
				if(cacheMap == null) {
					cacheMap = new HashMap<>();
					repository.dictCacheMap.put(dictResource.getSystemId(), cacheMap);
				}
				Cache<String, Object> cache = Caffeine.from(SpringContextUtils.getEnvironmentProperty(dictSpecKey, DEFAULT_CAFFEINE_SPEC)).build();
				cacheMap.put(dictResource.getId(), cache);
				try {
					cacheDict(dictResource, cache);
				}catch(Exception ex) {
					logger.error("cache dict faile! {}", dictResource.getId(), ex);
				}
				
			}
		} else {
			logger.info("no valid DictResource.");
		}

		// 初始化完成，赋值给属性instance
		if (instance != null && instance.dictCacheMap != null && !instance.dictCacheMap.isEmpty()) {// 缓存刷新时，释放原资源
			instance.dictCacheMap.forEach((system, map) -> {
				if (map != null && !map.isEmpty()) {
					map.forEach((resourceId, dictCache) -> {
						dictCache.invalidateAll();
					});
				}
			});
		}
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
			logger.debug("load and cache DictResource");
		}
		Map<String, Cache<String, Object>> oldCacheMap = instance.dictCacheMap.get(systemId);
		if (oldCacheMap != null && !oldCacheMap.isEmpty()) {
			oldCacheMap.forEach((resourceId, dictCache) -> {
				dictCache.invalidateAll();
			});
		}

		// 加载DictResource数据
		List<DictResource> dictResources = initService.listDictResources(systemId);
		Map<String, DictResource> map = new HashMap<>();
		Map<String, Cache<String, Object>> cacheMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(dictResources)) {
			String dictSpecKey = SpringContextUtils.getEnvironmentProperty(DICT_CACHE_CAFFEINE_SPEC_KEY, DEFAULT_CAFFEINE_SPEC_KEY);
			// 循环处理每条DictResource数据
			for (DictResource dictResource : dictResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dictResource.getSystemId())) {
					continue;
				}
				// 将DictResource对象加入缓存
				map.put(dictResource.getId(), dictResource);
				
				//加载字典缓存
				Cache<String, Object> cache = Caffeine.from(SpringContextUtils.getEnvironmentProperty(dictSpecKey, DEFAULT_CAFFEINE_SPEC)).build();
				cacheMap.put(dictResource.getId(), cache);
				try {
					cacheDict(dictResource, cache);
				}catch(Exception ex) {
					logger.error("cache dict faile! {}", dictResource.getId(), ex);
				}
				
			}
		} 
		instance.dictResourceMap.put(systemId, map);
		instance.dictCacheMap.put(systemId, cacheMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}


	/**
	 * 缓存字典数据
	 * @param dictResource
	 * @param cache
	 */
	private static void cacheDict(DictResource dictResource, Cache<String, Object> cache) {
		//获取对应的JdbcTemplate
    	JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(dictResource.getSystemId(), dictResource.getDbSchemaId());
		List<JsonObject> dictVos = null;
		if(DictResource.DICT_RESOURCE_TYPE_MASTERSLAVE.equals(dictResource.getType())) {//主从表模式
			StringBuilder loadSqlBuilder = new StringBuilder("select * from ").append(dictResource.getDictTable())
																					.append(" a inner join ")
																					.append(dictResource.getSubDictTable())
																					.append(" b on a.").append(dictResource.getDictIdCol())
																					.append(" = b.").append(dictResource.getSubTableDictCodeCol());
			String[] statusArray = null;
			if(!StringUtils.isBlank(dictResource.getDictStatusCol()) && !StringUtils.isBlank(dictResource.getEffStatusValue())) {
				loadSqlBuilder.append(" where a.").append(dictResource.getDictStatusCol());
				statusArray = dictResource.getEffStatusValue().split(STATUS_VALUE_SEPERATE_FLAG);
				if(statusArray.length == 1) {
					loadSqlBuilder.append(" = ?");
				}else {
					loadSqlBuilder.append(" in (");
					for(int i=0; i<statusArray.length; i++) {
						if(i != 0) {
							loadSqlBuilder.append(", ");
						}
						loadSqlBuilder.append("?");
					}
					loadSqlBuilder.append(")");
				}
			}

			String[] subStatusArray = null;
			if(!StringUtils.isBlank(dictResource.getSubTableStatusCol()) && !StringUtils.isBlank(dictResource.getEffSubTableStatusValue())) {
				if(statusArray == null) {
					loadSqlBuilder.append(" where b.").append(dictResource.getSubTableStatusCol());
				}else {
					loadSqlBuilder.append(" and b.").append(dictResource.getSubTableStatusCol());
				}
				
				subStatusArray = dictResource.getEffSubTableStatusValue().split(STATUS_VALUE_SEPERATE_FLAG);
				if(subStatusArray.length == 1) {
					loadSqlBuilder.append(" = ?");
				}else {
					loadSqlBuilder.append(" in (");
					for(int i=0; i<subStatusArray.length; i++) {
						if(i != 0) {
							loadSqlBuilder.append(", ");
						}
						loadSqlBuilder.append("?");
					}
					loadSqlBuilder.append(")");
				}
			}

			final String loadSql = loadSqlBuilder.toString();
			
			final String[] stsArray = statusArray;
			final String[] subStsArray = subStatusArray;
			dictVos = jdbcTemplate.query(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(loadSql);
					int i = 1;
					if(stsArray != null) {
						for(String status : stsArray) {
							ps.setObject(i++, status);
						}
					}
					if(subStsArray != null) {
						for(String status : subStsArray) {
							ps.setObject(i++, status);
						}
					}
					return ps;
				}
				
			}, new RowMapper<JsonObject>() {
				@Override
				public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
					JsonObject dictVo = new JsonObject();
					dictVo.addProperty(DICT_CODE, rs.getString(dictResource.getDictCodeCol()));
					dictVo.addProperty(DICT_NAME, rs.getString(dictResource.getDictNameCol()));
					dictVo.addProperty(DICT_VALUE, rs.getString(dictResource.getDictValueCol()));
					dictVo.addProperty(DICT_VALUE_LABEL, rs.getString(dictResource.getDictValueLabelCol()));
					if(!StringUtils.isBlank(dictResource.getDictValue2Col())) {
						dictVo.addProperty(DICT_VALUE2, rs.getString(dictResource.getDictValue2Col()));
					}
					if(!StringUtils.isBlank(dictResource.getDictValue3Col())) {
						dictVo.addProperty(DICT_VALUE3, rs.getString(dictResource.getDictValue3Col()));
					}
					if(!StringUtils.isBlank(dictResource.getParentDictCodeCol())) {
						dictVo.addProperty(PARENT_DICT_CODE, rs.getString(dictResource.getParentDictCodeCol()));
					}
					if(!StringUtils.isBlank(dictResource.getParentDictValueCol())) {
						dictVo.addProperty(PARENT_DICT_VALUE, rs.getString(dictResource.getParentDictValueCol()));
					}
					return dictVo;
				}
				
			});
		}else {//单表模式
			StringBuilder loadSqlBuilder = new StringBuilder("select * from ").append(dictResource.getDictTable());
			String[] statusArray = null;
			if(!StringUtils.isBlank(dictResource.getDictStatusCol()) && !StringUtils.isBlank(dictResource.getEffStatusValue())) {
				loadSqlBuilder.append(" where ").append(dictResource.getDictStatusCol());
				statusArray = dictResource.getEffStatusValue().split(STATUS_VALUE_SEPERATE_FLAG);
				if(statusArray.length == 1) {
					loadSqlBuilder.append(" = ?");
				}else {
					loadSqlBuilder.append(" in (");
					for(int i=0; i<statusArray.length; i++) {
						if(i != 0) {
							loadSqlBuilder.append(", ");
						}
						loadSqlBuilder.append("?");
					}
					loadSqlBuilder.append(")");
				}
			}
			final String loadSql = loadSqlBuilder.toString();
			
			final String[] stsArray = statusArray;
			dictVos = jdbcTemplate.query(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(loadSql);
					if(stsArray != null) {
						int i = 1;
						for(String status : stsArray) {
							ps.setObject(i++, status);
						}
					}
					return ps;
				}
				
			}, new RowMapper<JsonObject>() {
				@Override
				public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
					JsonObject dictVo = new JsonObject();
					dictVo.addProperty(DICT_CODE, rs.getString(dictResource.getDictCodeCol()));
					dictVo.addProperty(DICT_NAME, rs.getString(dictResource.getDictNameCol()));
					dictVo.addProperty(DICT_VALUE, rs.getString(dictResource.getDictValueCol()));
					dictVo.addProperty(DICT_VALUE_LABEL, rs.getString(dictResource.getDictValueLabelCol()));
					if(!StringUtils.isBlank(dictResource.getDictValue2Col())) {
						dictVo.addProperty(DICT_VALUE2, rs.getString(dictResource.getDictValue2Col()));
					}
					if(!StringUtils.isBlank(dictResource.getDictValue3Col())) {
						dictVo.addProperty(DICT_VALUE3, rs.getString(dictResource.getDictValue3Col()));
					}
					if(!StringUtils.isBlank(dictResource.getParentDictCodeCol())) {
						dictVo.addProperty(PARENT_DICT_CODE, rs.getString(dictResource.getParentDictCodeCol()));
					}
					if(!StringUtils.isBlank(dictResource.getParentDictValueCol())) {
						dictVo.addProperty(PARENT_DICT_VALUE, rs.getString(dictResource.getParentDictValueCol()));
					}
					return dictVo;
				}
				
			});
		}
    	
    	if(!CollectionUtils.isEmpty(dictVos)) {
			//缓存字典值对象
			logger.info("cache dict value ...");
			for(int i=0; i<dictVos.size(); i++) {
				JsonObject dictVo = dictVos.get(i).getAsJsonObject();
				cache.put(DICT_VALUE_CACHE_PREFIX + JsonUtils.getString(dictVo, DICT_CODE) + "_" + JsonUtils.getString(dictVo, DICT_VALUE), dictVo);
			}
			//缓存字典值列表对象
			logger.info("cache dict list ...");
			Map<String,List<JsonObject>> codeMap = dictVos.parallelStream().collect(Collectors.groupingBy((t)->{return JsonUtils.getString(t, DICT_CODE);}));
			codeMap.forEach((key, list)-> {
				cache.put(DICT_CACHE_PREFIX + key, JsonUtils.toJsonArray(list));
			});
			//缓存子字典值列表对象
			logger.info("cache sub dict list ...");
			Map<String,List<JsonObject>> subDictMap = dictVos.parallelStream()
				.filter((t) ->{return !StringUtils.isBlank(JsonUtils.getString(t, PARENT_DICT_CODE))&&!StringUtils.isBlank(JsonUtils.getString(t, PARENT_DICT_VALUE));})
				.collect(Collectors.groupingBy((t)-> {return JsonUtils.getString(t, DICT_CODE) + "_" + JsonUtils.getString(t, PARENT_DICT_CODE) + "_" + JsonUtils.getString(t, PARENT_DICT_VALUE);}));
			subDictMap.forEach((key, list)-> {
				cache.put(DICT_SUB_CACHE_PREFIX + key, JsonUtils.toJsonArray(list));
			});
		}
	}

	/**
	 * 获取字典值对象
	 * @param systemId
	 * @param dictResourceId
	 * @param dictCode
	 * @param dictValue
	 * @return
	 */
	public JsonObject getDictValue(String systemId, String dictResourceId, String dictCode, String dictValue) {
		Cache<String, Object> dictCache = this.getDictCache(systemId, dictResourceId);
		if(dictCache == null) {
			return null;
		}
		return (JsonObject)dictCache.getIfPresent(DICT_VALUE_CACHE_PREFIX + dictCode + "_" + dictValue);
	}

	/**
	 * 获取字典值名称
	 * @param systemId
	 * @param dictResourceId
	 * @param dictCode
	 * @param dictValue
	 * @return
	 */
	public String getDictValueLabel(String systemId, String dictResourceId, String dictCode, String dictValue) {
		JsonObject dictVo = this.getDictValue(systemId, dictResourceId, dictCode, dictValue);
		if(dictVo != null) {
			return JsonUtils.getString(dictVo, DICT_VALUE_LABEL);
		}else {
			return null;
		}
	}
	
	/**
	 * 获取字典值列表
	 * @param systemId
	 * @param dictResourceId
	 * @param dictCode
	 * @return
	 */
	public JsonArray getDictValues(String systemId, String dictResourceId, String dictCode) {
		Cache<String, Object> dictCache = this.getDictCache(systemId, dictResourceId);
		if(dictCache == null) {
			return null;
		}
		return (JsonArray)dictCache.getIfPresent(DICT_CACHE_PREFIX + dictCode);
	}

	/**
	 * 获取子字典值列表
	 * @param systemId
	 * @param dictResourceId
	 * @param dictCode
	 * @param parentDictCode
	 * @param parentDictValue
	 * @return
	 */
	public JsonArray getSubDictValues(String systemId, String dictResourceId, String dictCode, String parentDictCode, String parentDictValue) {
		Cache<String, Object> dictCache = this.getDictCache(systemId, dictResourceId);
		if(dictCache == null) {
			return null;
		}
		return (JsonArray)dictCache.getIfPresent(DICT_SUB_CACHE_PREFIX + dictCode + "_" + parentDictCode + "_" + parentDictValue);
	}
}
