package cn.zpaas.lowcode.be.engine.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.transfer.Transfer;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributeMapping;
import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DataTransferMethod;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 实现两个管理对象间参数转换的能力
 */
public class DataTransferAbility {
	private static Logger logger = LoggerFactory.getLogger(DataTransferAbility.class);
	
	// DataTransferAbility实例，用于实现单例模式
	private static DataTransferAbility instance = null;

	// 用来缓存DataMapping对象数据的Map
	private Map<String, Map<String, DataMapping>> dataMappingMap = new HashMap<>();

	//用于缓存数据转换方法数据
	private List<DataTransferMethod> dataTransferMethods;

	//用于缓存转换器，key为dataTransferMethodId
	private Map<String, Transfer> transferMap = new HashMap<>();

	// 私有化构造函数
	private DataTransferAbility() {

	}

	// 获取DataTransferAbility实例，用于实现单例模式
	public static DataTransferAbility getInstance() {
		return instance;
	}

	/**
	 * DataTransferAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化DataTransferAbility对象
		DataTransferAbility ability = new DataTransferAbility();

		// 加载DataMapping数据
		List<DataMapping> dataMappings = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			dataMappings = initService.listDataMappings();
		} else {// 加载指定业务系统的数据
			dataMappings = initService.listDataMappingBySystemId(systemId);
		}

		if (!CollectionUtils.isEmpty(dataMappings)) {
			//加载所有AttributeMapping
			List<AttributeMapping> allAttributeMappings = null;
			if (StringUtils.isBlank(systemId)) {// 加载所有数据
				allAttributeMappings = initService.listAttributeMappings();
			} else {// 加载指定业务系统的数据
				allAttributeMappings = initService.listAttributeMappingBySystem(systemId);
			}
			if(allAttributeMappings == null) {
				allAttributeMappings = new ArrayList<>();
			}
			//根据dataMappingId进行分组
			Map<String, List<AttributeMapping>> attributeMappingMap = allAttributeMappings.stream().collect(Collectors.groupingBy(AttributeMapping::getDataMappingId));
			
			// 循环处理每条DataMapping数据
			for (DataMapping dataMapping : dataMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dataMapping.getSystemId())) {
					continue;
				}
				// 将DataMapping对象加入缓存
				Map<String, DataMapping> map = ability.dataMappingMap.get(dataMapping.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					ability.dataMappingMap.put(dataMapping.getSystemId(), map);
				}
				map.put(dataMapping.getId(), dataMapping);
				// 加载AttributeMapping数据
				dataMapping.setAttributeMappings(attributeMappingMap.get(dataMapping.getId()));
			}
		} else {
			logger.info("no valid DataMapping.");
		}

		//加载数据转换方法定义
		ability.dataTransferMethods = initService.listDataTransferMethods();
		if(!CollectionUtils.isEmpty(ability.dataTransferMethods)) {
			for(DataTransferMethod dataTransferMethod : ability.dataTransferMethods) {
				Transfer transfer = null;
				if(!StringUtils.isBlank(dataTransferMethod.getImplClass())) {
					try {
						transfer = (Transfer)Class.forName(dataTransferMethod.getImplClass()).getDeclaredConstructor().newInstance();
						transfer.setDataTransferMethod(dataTransferMethod);
						ability.transferMap.put(dataTransferMethod.getId(), transfer);
					} catch (Exception e) {
						logger.error("Init dataTransferMethod's implClass failed: {}", dataTransferMethod.getImplClass());
					} 
				}else {
					logger.error("Init dataTransferMethod's implClass is null: {}", dataTransferMethod.getId());
				}
			}
		}else {
			logger.info("no valid dataTransferMethods.");
		}

		// 初始化完成，将DataTransferAbility对象赋值给属性instance
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
		// 加载DataMapping数据
		List<DataMapping> dataMappings = initService.listDataMappingBySystemId(systemId);
		Map<String, DataMapping> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(dataMappings)) {
			//加载所有AttributeMapping
			List<AttributeMapping> allAttributeMappings = initService.listAttributeMappingBySystem(systemId);
			if(allAttributeMappings == null) {
				allAttributeMappings = new ArrayList<>();
			}
			//根据dataMappingId进行分组
			Map<String, List<AttributeMapping>> attributeMappingMap = allAttributeMappings.stream().collect(Collectors.groupingBy(AttributeMapping::getDataMappingId));
			
			// 循环处理每条DataMapping数据
			for (DataMapping dataMapping : dataMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(dataMapping.getSystemId())) {
					continue;
				}
				map.put(dataMapping.getId(), dataMapping);
				// 加载AttributeMapping数据
				dataMapping.setAttributeMappings(attributeMappingMap.get(dataMapping.getId()));
			}
		} 
		instance.dataMappingMap.put(systemId, map);

		//加载数据转换方法定义
		// instance.dataTransferMethods = initService.listDataTransferMethods();
		// if(!CollectionUtils.isEmpty(instance.dataTransferMethods)) {
		// 	for(DataTransferMethod dataTransferMethod : instance.dataTransferMethods) {
		// 		Transfer transfer = null;
		// 		if(!StringUtils.isBlank(dataTransferMethod.getImplClass())) {
		// 			try {
		// 				transfer = (Transfer)Class.forName(dataTransferMethod.getImplClass()).getDeclaredConstructor().newInstance();
		// 				transfer.setDataTransferMethod(dataTransferMethod);
		// 				instance.transferMap.put(dataTransferMethod.getId(), transfer);
		// 			} catch (Exception e) {
		// 				logger.error("Init dataTransferMethod's implClass failed: {}", dataTransferMethod.getImplClass());
		// 			} 
		// 		}else {
		// 			logger.error("Init dataTransferMethod's implClass is null: {}", dataTransferMethod.getId());
		// 		}
		// 	}
		// }else {
		// 	logger.info("no valid dataTransferMethods.");
		// }

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和数据映射标识获取对应的数据映射对象
	 * 
	 * @param systemId      业务系统标识
	 * @param dataMappingId 数据映射标识
	 * @return 返回DataMapping对象
	 */
	public DataMapping getDataMapping(String systemId, String dataMappingId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, DataMapping> map = dataMappingMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(dataMappingId);
	}

	/**
	 * 根据数据转换方法标识获取到对应的转换器
	 * @param dataTransferMethodId
	 * @return
	 */
	private Transfer getTransfer(String dataTransferMethodId) {
		if(StringUtils.isBlank(dataTransferMethodId)) {
			return null;
		}
		return transferMap.get(dataTransferMethodId);
	}

	/**
	 * 按mappingId指定的数据映射规则，将源对象的数据复制到目标对象
	 * 
	 * @param systemId      业务系统标识
	 * @param dataMappingId 数据映射标识
	 * @param srcObject     源对象
	 * @param targetObject  目标对象
	 */
	public void transfer(String systemId, String dataMappingId, JsonObject fromObject, JsonObject toObject)
			throws EngineException {
		if(logger.isTraceEnabled()) {
			logger.trace("Data transfer is started.");
		}
		// 如果systemId或dataMappingId为空，直接返回
		if (StringUtils.isBlank(systemId)) {
			logger.error("systemId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId is null");
		}
		if (StringUtils.isBlank(dataMappingId)) {
			logger.error("dataMappingId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "dataMappingId is null");
		}

		// 获取对应的数据映射对象
		DataMapping dataMapping = getDataMapping(systemId, dataMappingId);

		// 如果获取不到有效的对象，直接报错
		if (dataMapping == null || CollectionUtils.isEmpty(dataMapping.getAttributeMappings())) {
			logger.error("can't get valid DataMapping or AttributeMapping. {}", dataMappingId);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid DataMapping or AttributeMapping.");
		}

		// 源对象和目标对象都不能为空
		if (fromObject == null || toObject == null) {
			logger.error("T[{}] fromObject or toObject is null", dataMapping.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject or toObject is null");
		}

		JsonObject subDataMappings = null;
		if(!StringUtils.isBlank(dataMapping.getSubDataMappings())) {
			subDataMappings = JsonUtils.toJsonObject(dataMapping.getSubDataMappings());
		}
		
		//循环处理每一个属性
		Attribute fromAttribute = null, toAttribute = null;
		Object value = null;
		for (AttributeMapping attributeMapping : dataMapping.getAttributeMappings()) {
			// 获取源属性信息
			if (ManagedObjectType.DOMAIN_OBJECT.equals(dataMapping.getFromObjectType())) {// 源对象是领域对象
				fromAttribute = DomainObjectProxy.getInstance().getAttribute(systemId, attributeMapping.getFromAttributeId());
			} else {// 源对象是值传递对象
				fromAttribute = ValueObjectProxy.getInstance().getAttribute(systemId, attributeMapping.getFromAttributeId());
			}
			// 获取目标属性信息
			if (ManagedObjectType.DOMAIN_OBJECT.equals(dataMapping.getToObjectType())) {// 目标对象是领域对象
				toAttribute = DomainObjectProxy.getInstance().getAttribute(systemId, attributeMapping.getToAttributeId());
			} else {// 目标对象是值传递对象
				toAttribute = ValueObjectProxy.getInstance().getAttribute(systemId, attributeMapping.getToAttributeId());
			}
			//源和目标属性只要一个为空，直接报错
			if(fromAttribute == null || toAttribute == null) {
				logger.error("T[{}] fromAttribute or toAttribute is null", dataMapping.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromAttribute or toAttribute is null");
			}
			
			//从源对象获取属性值
			if(YesOrNo.YES.equals(fromAttribute.getIsListAttr())) {
				value = JsonUtils.getJsonArray(fromObject, fromAttribute.getCode());
			}else {
				value = JsonUtils.getObject(fromObject, fromAttribute.getCode());
			}

			//如果配置了数据转换方法
			if(!StringUtils.isBlank(attributeMapping.getTransferMethod())) {
				Transfer transfer = getTransfer(attributeMapping.getTransferMethod());
				if(transfer == null) {
					logger.error("T[{}] invalid transfer method {}", dataMapping.getTenantId(), attributeMapping.getTransferMethod());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid transfer method!");
				}
				JsonObject transferCfg = null;
				if(!StringUtils.isBlank(attributeMapping.getTransferCfg())) {
					transferCfg = JsonUtils.toJsonObject(attributeMapping.getTransferCfg());
				}

				//调用转换器的转换方法
				value = transfer.transfer(fromAttribute, value, toAttribute, transferCfg, fromObject);
			}
			
			if(value == null || ManagedObjectType.JAVA_OBJECT.equals(fromAttribute.getAttrType()) || ManagedObjectType.JAVA_OBJECT.equals(toAttribute.getAttrType())) {//value值为null或Java类型

				//设置到目标对象中
				toObject.add(toAttribute.getCode(), JsonUtils.toJsonElement(value));
			}else {//值传递对象类型或领域对象类型
				//如果未传入下一层对象的数据映射标识，则表示不再深入拷贝
				if(JsonUtils.isEmpty(subDataMappings)) {
					continue;
				}
				String subDataMappingId = JsonUtils.getString(subDataMappings, fromAttribute.getCode());
				if(StringUtils.isBlank(subDataMappingId)) {
					continue;
				}

				// 获取对应的数据映射对象
				DataMapping subDataMapping = getDataMapping(systemId, subDataMappingId);

				// 如果获取不到有效的对象，直接报错
				if (subDataMapping == null || CollectionUtils.isEmpty(subDataMapping.getAttributeMappings())) {
					logger.error("T[{}] can't get valid subDataMapping or AttributeMapping. {}", dataMapping.getTenantId(), subDataMappingId);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid subDataMapping or AttributeMapping.");
				}
							
				//当前属性是列表属性，需要进行循环
				if(YesOrNo.YES.equals(fromAttribute.getIsListAttr())) {
					if(value instanceof JsonArray) {
						//新建子目标对象
						JsonArray subTargetArray = new JsonArray();
						//循环处理列表中每个对象
						JsonArray valueArray = (JsonArray)value;	
						JsonObject subFromObject = null;
						JsonObject subToObject = null;
						for(int i=0; i<valueArray.size(); i++) {
							subFromObject = valueArray.get(i).getAsJsonObject();
							//如果为空，则跳过
							if(subFromObject == null) {
								continue;
							}
							//初始化子目标对象
							if (ManagedObjectType.DOMAIN_OBJECT.equals(subDataMapping.getToObjectType())) {
								DomainObject object = DomainObjectProxy.getInstance().getDomainObject(systemId, subDataMapping.getToObjectId());
								subToObject = object.newInstance();
							} else if (ManagedObjectType.VALUE_OBJECT.equals(subDataMapping.getToObjectType())) {
								ValueObject object = ValueObjectProxy.getInstance().getValueObject(systemId, subDataMapping.getToObjectId());
								subToObject = object.newInstance();
							} else {
								subToObject = new JsonObject();
							}
							//递归进行下一层级的拷贝
							this.transfer(systemId, subDataMappingId, subFromObject, subToObject);
							//将子目标对象加入列表
							subTargetArray.add(subToObject);
						}					
						//将拷贝好的子目标对象设置到目标对象中
						toObject.add(toAttribute.getCode(), subTargetArray);
					}else {//如果属性值的类型不是JsonArray，则报错
						logger.error("T[{}] fromAttribute value's class is not JsonArray.", dataMapping.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromAttribute value's class is not JsonArray.");
					}
				}else {//非列表属性
					if(value instanceof JsonObject) {
						//新建子目标对象
						JsonObject subToObject = null;
						//初始化子目标对象
						if (ManagedObjectType.DOMAIN_OBJECT.equals(subDataMapping.getToObjectType())) {
							DomainObject object = DomainObjectProxy.getInstance().getDomainObject(systemId, subDataMapping.getToObjectId());
							subToObject = object.newInstance();
						} else if (ManagedObjectType.VALUE_OBJECT.equals(subDataMapping.getToObjectType())) {
							ValueObject object = ValueObjectProxy.getInstance().getValueObject(systemId, subDataMapping.getToObjectId());
							subToObject = object.newInstance();
						} else {
							subToObject = new JsonObject();
						}
						//递归进行下一层级的拷贝
						this.transfer(systemId, subDataMappingId, ((JsonObject)value), subToObject);
						//将拷贝好的子目标对象设置到目标对象中
						toObject.add(toAttribute.getCode(), subToObject);
					}else {//如果属性值的类型不是JsonObject，则报错
						logger.error("T[{}] fromAttribute value's class is not JsonObject.", dataMapping.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromAttribute value's class is not JsonObject.");
					}
				}
			}
		}
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] Data transfer is finished.", dataMapping.getTenantId());
		}
	}
	
	/**
	 * 按mappingId指定的数据映射规则，将源对象的数据复制到目标对象，处理列表类型
	 * 
	 * @param systemId      业务系统标识
	 * @param dataMappingId 数据映射标识
	 * @param fromArray     源对象数组
	 * @param toArray  目标对象数组
	 */
	public void transfer(String systemId, String dataMappingId, JsonArray fromArray, JsonArray toArray)
			throws EngineException {
		// 如果systemId或dataMappingId为空，直接返回
		if (StringUtils.isBlank(systemId)) {
			logger.error("systemId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId is null");
		}
		if (StringUtils.isBlank(dataMappingId)) {
			logger.error("dataMappingId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "dataMappingId is null");
		}

		// 获取对应的数据映射对象
		DataMapping dataMapping = getDataMapping(systemId, dataMappingId);

		// 如果获取不到有效的对象，直接报错
		if (dataMapping == null || CollectionUtils.isEmpty(dataMapping.getAttributeMappings())) {
			logger.error("can't get valid DataMapping or AttributeMapping. {}", dataMappingId);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid DataMapping or AttributeMapping.");
		}

		// 源对象和目标对象都不能为空
		if (fromArray == null || toArray == null) {
			logger.error("T[{}] fromObject or toObject is null", dataMapping.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject or toObject is null");
		}
		//如果源数据的长度为0，直接返回
		if(fromArray.size() == 0) {
			logger.info("T[{}] fromArray's size is 0.", dataMapping.getTenantId());
			return;
		}
		
		JsonObject subFromObject = null;
		JsonObject subToObject = null;
		for(int i=0; i<fromArray.size(); i++) {
			subFromObject = fromArray.get(i).getAsJsonObject();
			//如果为空，则跳过
			if(subFromObject == null) {
				continue;
			}
			//初始化子目标对象
			if (ManagedObjectType.DOMAIN_OBJECT.equals(dataMapping.getToObjectType())) {
				DomainObject object = DomainObjectProxy.getInstance().getDomainObject(systemId, dataMapping.getToObjectId());
				subToObject = object.newInstance();
			} else if (ManagedObjectType.VALUE_OBJECT.equals(dataMapping.getToObjectType())) {
				ValueObject object = ValueObjectProxy.getInstance().getValueObject(systemId, dataMapping.getToObjectId());
				subToObject = object.newInstance();
			} else {
				subToObject = new JsonObject();
			}
			//调用非列表对象拷贝方法
			this.transfer(systemId, dataMappingId, subFromObject, subToObject);
			//将子目标对象加入列表
			toArray.add(subToObject);
		}					
	}

}
