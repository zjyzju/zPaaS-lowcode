package cn.zpaas.lowcode.be.engine.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.DynamicMappingDetail;

/**
 * @author zjy 实现动态映射能力
 */
public class DynamicMappingAbility {
	private static Logger logger = LoggerFactory.getLogger(DynamicMappingAbility.class);
	
	// DynamicMappingAbility实例，用于实现单例模式
	private static DynamicMappingAbility instance = null;

	// 用来缓存DynamicMapping对象数据的Map
	private Map<String, Map<String, DynamicMapping>> dynamicMappingMap = new HashMap<>();

	// 私有化构造函数
	private DynamicMappingAbility() {

	}

	// 获取DynamicMappingAbility实例，用于实现单例模式
	public static DynamicMappingAbility getInstance() {
		return instance;
	}

	/**
	 * DynamicMappingAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化DynamicMappingAbility对象
		DynamicMappingAbility ability = new DynamicMappingAbility();

		// 加载DynamicMapping数据
		List<DynamicMapping> dynamicMappings = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			dynamicMappings = initService.listDynamicMappings();
		} else {// 加载指定业务系统的数据
			dynamicMappings = initService.listDynamicMappings(systemId);
		}

		if (!CollectionUtils.isEmpty(dynamicMappings)) {
			// 循环处理每条DynamicMapping数据
			for (DynamicMapping dynamicMapping : dynamicMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dynamicMapping.getSystemId())) {
					continue;
				}
				// 将DynamicMapping对象加入缓存
				Map<String, DynamicMapping> map = ability.dynamicMappingMap.get(dynamicMapping.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					ability.dynamicMappingMap.put(dynamicMapping.getSystemId(), map);
				}
				map.put(dynamicMapping.getId(), dynamicMapping);
			}
		} else {
			logger.info("no valid DynamicMapping.");
		}

		// 初始化完成，将DynamicMappingAbility对象赋值给属性instance
		instance = ability;

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
		}
		// 加载DynamicMapping数据
		List<DynamicMapping> dynamicMappings = initService.listDynamicMappings(systemId);
		Map<String, DynamicMapping> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(dynamicMappings)) {
			// 循环处理每条DynamicMapping数据
			for (DynamicMapping dynamicMapping : dynamicMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dynamicMapping.getSystemId())) {
					continue;
				}
				// 将DynamicMapping对象加入缓存
				map.put(dynamicMapping.getId(), dynamicMapping);
			}
		} 
		instance.dynamicMappingMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和数据映射标识获取对应的动态映射对象
	 * 
	 * @param systemId      业务系统标识
	 * @param dynamicMappingId 动态映射标识
	 * @return 返回DynamicMapping对象
	 */
	public DynamicMapping getDynamicMapping(String systemId, String dynamicMappingId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, DynamicMapping> map = dynamicMappingMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(dynamicMappingId);
	}

	/**
	 * 根据业务系统标识和数据映射标识获取对应的动态映射对象
	 * 
	 * @param systemId      业务系统标识
	 * @param dynamicMappingId 动态映射标识
	 * @param keyValue 关键值
	 * @return 返回映射对象标识
	 */
	public String getMappingObjectId(String systemId, String dynamicMappingId, String keyValue) {
		if (StringUtils.isBlank(systemId) || StringUtils.isBlank(keyValue)) {
			return null;
		}
		DynamicMapping dynamicMapping = this.getDynamicMapping(systemId, dynamicMappingId);
		if(dynamicMapping != null && dynamicMapping.getDetailMap() != null) {
			DynamicMappingDetail dynamicMappingDetail = dynamicMapping.getDetailMap().get(keyValue);
			if(dynamicMappingDetail != null) {
				return dynamicMappingDetail.getMappingObjectId();
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
}
