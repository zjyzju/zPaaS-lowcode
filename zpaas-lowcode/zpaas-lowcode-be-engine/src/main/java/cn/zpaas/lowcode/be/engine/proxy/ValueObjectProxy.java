package cn.zpaas.lowcode.be.engine.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.ValueObject;

/**
 * @author zjy 值传递对象的代理类
 */
public class ValueObjectProxy {
	private static Logger logger = LoggerFactory.getLogger(ValueObjectProxy.class);

	// ValueObjectProxy对象，用于实现单例模式
	private static ValueObjectProxy instance = null;

	// 用于缓存值传递对象的Map，第一层Key是systemId， 第二层Key是值传递对象标识
	private Map<String, Map<String, ValueObject>> valueObjectMap = new HashMap<>();
	// 用于缓存属性对象的Map，第一层Key是systemId，第二层Key是属性标识
	private Map<String, Map<String, Attribute>> attributeMap = new HashMap<>();

	// 获取ValueObjectProxy对象，用于实现单例模式
	public static ValueObjectProxy getInstance() {
		return instance;
	}

	// 私有化构造函数
	private ValueObjectProxy() {

	}

	/**
	 * 根据业务系统标识和领域对象标识，获取值传递对象信息
	 * 
	 * @param systemId               业务系统标识
	 * @param valueObjectId 值传递对象标识
	 * @return 返回值传递对象
	 */
	public ValueObject getValueObject(String systemId, String ValueObjectId) {
		if (systemId == null || systemId.trim().length() == 0) {
			return null;
		}
		Map<String, ValueObject> map = valueObjectMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(ValueObjectId);
	}

	/**
	 * 根据业务系统标识和属性标识，获取属性对象信息
	 * 
	 * @param systemId    业务系统标识
	 * @param attributeId 属性标识
	 * @return 返回属性对象
	 */
	public Attribute getAttribute(String systemId, String attributeId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, Attribute> map = attributeMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(attributeId);
	}

	/**
	 * DomainObjectProxy类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化ValueObjectProxy对象
		ValueObjectProxy proxy = new ValueObjectProxy();

		List<ValueObject> ValueObjects = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			ValueObjects = initService.listValueObjects();
		} else {// 加载指定业务系统的数据
			ValueObjects = initService.listValueObjectBySystemId(systemId);
		}

		if (!CollectionUtils.isEmpty(ValueObjects)) {
			// 加载所有的属性信息
			List<Attribute> allAttributes = null;
			if (!StringUtils.isBlank(systemId)) {
				allAttributes = initService.listAttributeByObjectTypeAndSystem(ManagedObjectType.VALUE_OBJECT, systemId);
			} else {
				allAttributes = initService.listAttributeByObjectType(ManagedObjectType.VALUE_OBJECT);
			}
			if (allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, List<Attribute>> attributeMap = allAttributes.stream().collect(Collectors.groupingBy(Attribute::getObjectId));
			
			// 循环处理每一条值传递对象
			for (ValueObject ValueObject : ValueObjects) {
				// 当值传递对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(ValueObject.getSystemId())) {
					logger.info("ValueObject's systemId is null. {}", ValueObject.getId());
					continue;
				}
				// 将领域对象加入缓存
				Map<String, ValueObject> map = proxy.valueObjectMap.get(ValueObject.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					proxy.valueObjectMap.put(ValueObject.getSystemId(), map);
				}
				map.put(ValueObject.getId(), ValueObject);

				// 获取并缓存领域对象的属性信息
				List<Attribute> attributes = attributeMap.get(ValueObject.getId());
				ValueObject.setAttributes(attributes);
				// 将属性加入属性缓存
				if (!CollectionUtils.isEmpty(attributes)) {
					for (Attribute attribute : attributes) {
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(attribute.getSystemId())) {
							logger.info("attribute's systemId is null. {}", attribute.getId());
							continue;
						}
						Map<String, Attribute> attrMap = proxy.attributeMap.get(attribute.getSystemId());
						if (attrMap == null) {
							attrMap = new HashMap<>();
							proxy.attributeMap.put(attribute.getSystemId(), attrMap);
						}
						attrMap.put(attribute.getId(), attribute);
					}
				}

			}
		} else {
			logger.info("no valid ValueObject.");
		}

		// 初始化完成将proxy赋值给instance属性
		instance = proxy;

		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
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
		List<ValueObject> ValueObjects = initService.listValueObjectBySystemId(systemId);
		Map<String, ValueObject> map = new HashMap<>();
		Map<String, Attribute> attrMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(ValueObjects)) {
			// 加载所有的属性信息
			List<Attribute> allAttributes = initService.listAttributeByObjectTypeAndSystem(ManagedObjectType.VALUE_OBJECT, systemId);
			if (allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, List<Attribute>> attributeMap = allAttributes.stream().collect(Collectors.groupingBy(Attribute::getObjectId));
			
			// 循环处理每一条值传递对象
			for (ValueObject ValueObject : ValueObjects) {
				// 当值传递对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(ValueObject.getSystemId())) {
					logger.info("ValueObject's systemId is null. {}", ValueObject.getId());
					continue;
				}
				map.put(ValueObject.getId(), ValueObject);

				// 获取并缓存领域对象的属性信息
				List<Attribute> attributes = attributeMap.get(ValueObject.getId());
				ValueObject.setAttributes(attributes);
				// 将属性加入属性缓存
				if (!CollectionUtils.isEmpty(attributes)) {
					for (Attribute attribute : attributes) {
						// 当属性的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(attribute.getSystemId())) {
							logger.info("attribute's systemId is null. {}", attribute.getId());
							continue;
						}
						attrMap.put(attribute.getId(), attribute);
					}
				}

			}
		} 
		instance.valueObjectMap.put(systemId, map);
		instance.attributeMap.put(systemId, attrMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
	}
}
