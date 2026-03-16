package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.DataTransferAbility;
import cn.zpaas.lowcode.be.engine.ability.DynamicMappingAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 动态参数转换节点的实现类
 */
public class DynamicTransferNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(DynamicTransferNode.class);

	private static final String CFG_IS_LIST_TYPE_KEY = "isListType"; // 是否列表类型存放的Key
	private static final String CFG_FROM_OBJECT_INSTANCE_SOURCE_KEY = "fromObjectInstanceSource"; // 源对象实例的来源存放的Key
	private static final String CFG_FROM_OBJECT_INSTANCE_KEY_KEY = "fromObjectInstanceKey"; // 源对象实例的Key值存放的Key
	private static final String CFG_FROM_OBJECT_INSTANCE_ATTR_KEY = "fromObjectInstanceAttr"; // 源对象实例对应对象属性存放的Key
	private static final String CFG_TO_OBJECT_INSTANCE_SOURCE_KEY = "toObjectInstanceSource"; // 目标对象实例的来源存放的Key
	private static final String CFG_TO_OBJECT_INSTANCE_KEY_KEY = "toObjectInstanceKey"; // 目标对象实例的Key值存放的Key
	private static final String CFG_DYNAMIC_MAPPING_ID_KEY = "dynamicMappingId"; //动态映射标识存放的Key
	private static final String CFG_KEY_VALUE_SOURCE_KEY = "keyValueSource"; //动态关键值来源存放的Key
	private static final String CFG_KEY_VALUE_KEY_KEY = "keyValueKey"; //动态关键值Key存放的Key
	private static final String CFG_KEY_VALUE_ATTR_KEY = "keyValueAttr"; //动态关键值属性存放的Key
	
	/*
	 {
		isListType: 是否列表类型，true/false

		fromObjectInstanceSource：源对象实例的来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；N（预处理产生的nodeParams）
		fromObjectInstanceKey：源对象实例的Key值，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据isListType的值进行区分）中的key值；为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）时，Key值无效
		fromObjectInstanceAttr：当源对象是指定对象的某个属性时有效，通过该字段指定对应属性的code，此时指定对象不能是列表类型，当以“$”开头时（先处理前面等于“$”的情况，再考虑以“$”开头的情况），表示该属性的值是动态取值的情况，只支持从过程数据中获取，如“$user”,表示从过程数据中获取user属性的对象，“$user.id”,表示取过程数据中user属性对象id属性的值，该情况主要支持目标对象是Map的情况（Map的key值是动态确定的）。
		
		toObjectInstanceSource：目标对象实例的来源，可空，如果目标对象实例已经存在的情况下配置，这种情况下，后置配置信息中的相关信息可以不用配置，包括：P（过程数据）；D（领域对象）；
		toObjectInstanceKey：目标对象实例的Key值
		
		dynamicMappingId：动态映射标识，选择映射类型为D（数据映射）的动态映射数据。
		keyValueSource：动态关键值来源，包括：I（输入参数）；P（过程数据）；D（领域对象）；N（预处理产生的nodeParams）；
		keyValueKey：动态关键值Key，源对象类型为I（输入参数）时，表示输入参数中的Key；为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据isListType的值进行区分）中的key值；为P（过程数据）时，该值表示context.values中的key值，当为N（预处理产生的nodeParams）时，Key值无效；
		keyValueAttr：当动态关键值是指定对象的某个属性时有效，通过该字段指定对应属性的code，支持JSONPath，源对象实例属性的值只能是字符串或数字，当以“$”开头时（先处理前面等于“$”的情况，再考虑以“$”开头的情况），表示该属性的值是动态取值的情况，只支持从过程数据中获取，如“$user”,表示从过程数据中获取user属性的对象，“$user.id”,表示取过程数据中user属性对象id属性的值，该情况主要支持目标对象是Map的情况（Map的key值是动态确定的）。
				
		isListResult：节点执行结果是否是List类型，包括：true/false
		nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
		nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构
	}
	 */
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		boolean isListType = JsonUtils.getBoolean(nodeCfg, CFG_IS_LIST_TYPE_KEY);
		String fromObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_SOURCE_KEY);
		String fromObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_KEY_KEY);
		String fromObjectInstanceAttr = JsonUtils.getString(nodeCfg, CFG_FROM_OBJECT_INSTANCE_ATTR_KEY);
		String toObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_INSTANCE_SOURCE_KEY);
		String toObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_INSTANCE_KEY_KEY);
		String keyValueSource = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_SOURCE_KEY);
		String keyValueKey = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_KEY_KEY);
		String keyValueAttr = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_ATTR_KEY);
		String dynamicMappingId = JsonUtils.getString(nodeCfg, CFG_DYNAMIC_MAPPING_ID_KEY);
		
		if (StringUtils.isBlank(fromObjectInstanceSource) || StringUtils.isBlank(fromObjectInstanceKey)) {
			logger.error("T[{}] fromObjectInstanceSource and fromObjectInstanceKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObjectInstanceSource and fromObjectInstanceKey can't be null. ");
		}

		if(StringUtils.isBlank(dynamicMappingId) || StringUtils.isBlank(keyValueSource) || StringUtils.isBlank(keyValueKey)) {
			logger.error("T[{}] dynamicMappingId keyValueSource and keyValueKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dynamicMappingId keyValueSource and keyValueKey can't be null. ");
		}
		// 动态属性的情况
		if (!StringUtils.isBlank(keyValueAttr)) {
			keyValueAttr = this.dynamicAttrProcess(keyValueAttr, businessFlowNode, context);
		}
		if (!StringUtils.isBlank(fromObjectInstanceAttr)) {
			fromObjectInstanceAttr = this.dynamicAttrProcess(fromObjectInstanceAttr, businessFlowNode, context);
		}

		//获取动态映射的关键属性值
		String keyValue = (String)getContextObject(keyValueSource, keyValueKey, false, keyValueAttr, context, businessFlowNode);
		if(StringUtils.isBlank(keyValue)) {
			logger.error("T[{}] keyValue can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "keyValue can't be null!");
		} 
		
		//根据动态映射标识以及关键属性值获取数据映射标识
		String dataMappingId = DynamicMappingAbility.getInstance().getMappingObjectId(businessFlowNode.getSystemId(), dynamicMappingId, keyValue);
		if(StringUtils.isBlank(dataMappingId)) {
			logger.error("T[{}] dataMappingId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataMappingId can't be null!");
		} 

		DataMapping dataMapping = DataTransferAbility.getInstance().getDataMapping(businessFlowNode.getSystemId(), dataMappingId);
		if (dataMapping == null) {
			logger.error("T[{}] invalid dataMappingId . businessflowId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid dataMappingId.");
		}

		// 获取源对象实例
		Object fromObject = getContextObject(fromObjectInstanceSource, fromObjectInstanceKey, isListType, fromObjectInstanceAttr, context, businessFlowNode);

		// 获取或实例化目标对象实例
		Object toObject = null;
		// 新建目标对象实例
		if (StringUtils.isBlank(toObjectInstanceSource) || StringUtils.isBlank(toObjectInstanceKey)) {
			if (isListType) {
				toObject = new JsonArray();
			} else {
				if (ManagedObjectType.DOMAIN_OBJECT.equals(dataMapping.getToObjectType())) {
					DomainObject object = DomainObjectProxy.getInstance().getDomainObject(businessFlowNode.getSystemId(), dataMapping.getToObjectId());
					toObject = object.newInstance();
				} else if (ManagedObjectType.VALUE_OBJECT.equals(dataMapping.getToObjectType())) {
					ValueObject object = ValueObjectProxy.getInstance().getValueObject(businessFlowNode.getSystemId(), dataMapping.getToObjectId());
					toObject = object.newInstance();
				} else {
					toObject = new JsonObject();
				}
			}
		} else {// 获取目标对象实例
			if (isListType) {
				logger.error(
						"T[{}] when isListType is true, toObjectInstanceSource and toObjectInstanceKey must be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "when isListType is true, toObjectInstanceSource and toObjectInstanceKey must be null.");
			}
			// 目标对象实例来自输入数据
			if (OBJECT_INSTANCE_SOURCE_P.equals(toObjectInstanceSource)) {// 目标对象实例来自过程数据
				toObject = context.getAttribute(toObjectInstanceKey);
			} else if (OBJECT_INSTANCE_SOURCE_D.equals(toObjectInstanceSource)) {// 目标对象实例来自领域对象
				toObject = context.getAttributedObject(toObjectInstanceKey).objectValues();
			} else {
				logger.error("T[{}] invalid toObjectInstanceSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid toObjectInstanceSource.");
			}
		}

		

		if (isListType) {// 列表类型
			if (!(toObject instanceof JsonArray) || !(fromObject instanceof JsonArray)) {
				logger.error("T[{}] fromObject and toObject must be JsonArray. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject and toObject must be JsonArray");
			}
			DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId, ((JsonArray) fromObject), ((JsonArray) toObject));
		} else {// 非列表类型
			if (!(toObject instanceof JsonObject) || !(fromObject instanceof JsonObject)) {
				logger.error("T[{}] fromObject and toObject must be JsonObject. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "fromObject and toObject must be JsonObject");
			}
			DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId, ((JsonObject) fromObject),
					((JsonObject) toObject));
		}
		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, toObject);
	}

}
