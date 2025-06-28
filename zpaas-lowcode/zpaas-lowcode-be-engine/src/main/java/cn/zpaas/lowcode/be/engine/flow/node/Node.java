package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.be.engine.ability.DataTransferAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.BusinessFlowProxy;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

/**
 * @author zjy
 *
 */
public abstract class Node {
	private static Logger logger = LoggerFactory.getLogger(Node.class);

	private FlowNode flowNode;// 存放该节点实现类的节点定义信息

	public static final String REQ_DATA_KEY = "reqData"; // 静态常量，表示请求数据的属性名
	public static final String DOMAIN_OBJECT_KEY = "attributedObject"; // 静态常量，表示包含领域对象数据的容器对象的属性名
	public static final String RETURN_DATA_KEY = "returnData"; // 静态常量，表示返回数据的属性名
	public static final String SUB_FLOW_RETURN_DATA_KEY = "subFlowReturnData"; // 静态常量，表示子流量返回数据的KEY值
	public static final String NODE_PARAM_KEY = "nodeParams"; // 静态常量，节点process方法需要用到的参数存放的容器（JSON或Map）的Key值
	public static final String NODE_RESULT_KEY = "nodeResult"; // 静态常量，节点process方法输出结果对象存放的Key值

	public static final String TO_OBJECT_TYPE_KEY = "toObjectType"; // 预处理配置信息中toObjectType信息的Key值
	public static final String PARAMS_RULE_KEY = "paramsRule"; // 预处理配置信息中paramsRule信息的Key值
	public static final String IS_LIST_TYPE_KEY = "isListType"; // 预处理配置信息中isListType信息的Key值
	public static final String FROM_OBJECT_TYPE_KEY = "fromObjectType"; // 预处理配置信息中fromObjectType信息的Key值
	public static final String FROM_OBJECT_KEY_KEY = "fromObjectKey"; // 预处理配置信息中fromObjectKey信息的Key值
	public static final String ATTR_MAPPINGS_KEY = "attrMappings"; // 预处理配置信息中attrMappings信息的Key值
	public static final String FROM_ATTR_PATH_KEY = "fromAttrPath"; // 预处理配置信息中fromAttrPath信息的Key值
	public static final String TO_OBJECT_ATTR_KEY = "toObjectAttr"; // 预处理配置信息中toObjectAttr信息的Key值
	public static final String TO_OBJECT_TYPE_MAP = "Map"; // 预处理配置信息中toObjectType信息的Map值
	public static final String TO_OBJECT_TYPE_JSON = "JSON"; // 预处理配置信息中toObjectType信息的JSON值
	public static final String FROM_OBJECT_TYPE_INPUT = "I"; // 预处理配置信息中fromObjectType信息的对应输入参数的值
	public static final String FROM_OBJECT_TYPE_DOMAIN = "D"; // 预处理配置信息中fromObjectType信息的对应领域对象的值
	public static final String FROM_OBJECT_TYPE_IN_PROCESS = "P"; // 预处理配置信息中fromObjectType信息的过程数据的值
	public static final String FROM_OBJECT_TYPE_FIX = "F"; // 预处理配置信息中fromObjectType信息的过程数据的值
	public static final String FROM_ATTR_PATH_ROOT = "/"; // 预处理配置信息中fromAttrPath信息，表示源对象本身的字符

	public static final String IS_LIST_RESULT_KEY = "isListResult"; // 配置信息中isListResult信息的Key值
	public static final String NODE_RESULT_TYPE_KEY = "nodeResultType"; // 配置信息中nodeResultType信息的Key值
	public static final String NODE_RESULT_CLASS_KEY = "nodeResultClass"; // 配置信息中nodeResultClass信息的Key值

	public static final String AS_IN_PROCESS_DATA_KEY = "asInProcessData"; // 后处理配置信息中asInProcessData信息的Key值
	public static final String IN_PROCESS_DATA_TYPE_KEY = "inProcessDataType"; // 后处理配置信息中inProcessDataType信息的Key值
	public static final String IN_PROCESS_DATA_CLASS_KEY = "inProcessDataClass"; // 后处理配置信息中inProcessDataClass信息的Key值
	public static final String DATA_MAPPING_ID_IN_PROCESS_DATA_KEY = "dataMappingId_inProcessData"; // 后处理配置信息中dataMappingId_inProcessData信息的Key值
	// public static final String SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY = "subDataMappings_inProcessData"; // 后处理配置信息中subDataMappings_inProcessData信息的Key值
	public static final String IN_PROCESS_DATA_KEY = "inProcessDataKey"; // 后处理配置信息中inProcessDataKey信息的Key值
	public static final String AS_DOMAIN_OBJECT_VALUE_KEY = "asDomainObjectValue"; // 后处理配置信息中asDomainObjectValue信息的Key值
	public static final String DOMAIN_OBJECT_VALUE_KEY = "domainObjectValueKey"; // 后处理配置信息中domainObjectValueKey信息的Key值
	public static final String DOMAIN_OBJECT_ID_KEY = "domainObjectId"; // 后处理配置信息中domainObjectIdKey信息的Key值
	public static final String DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY = "dataMappingId_domainObjectValue"; // 后处理配置信息中dataMappingId_domainObjectValue信息的Key值
	// public static final String SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY = "subDataMappings_domainObjectValue"; // 后处理配置信息中subDataMappings_domainObjectValue信息的Key值
	public static final String AS_BUSINESS_PROCESS_RESULT_KEY = "asBusinessProcessResult"; // 后处理配置信息中asBusinessProcessResult信息的Key值
	public static final String DATA_MAPPING_ID_RESULT_KEY = "dataMappingId_Result"; // 后处理配置信息中dataMappingId_Result信息的Key值
	// public static final String SUB_DATA_MAPPINGS_RESULT = "subDataMappings_Result"; // 后处理配置信息中subDataMappings_Result信息的Key值
	public static final String IN_PROCESS_DATA_TYPE_JAVA = "J"; // 后处理配置信息中inProcessDataType信息的对应JAVA对象的值
	public static final String IN_PROCESS_DATA_TYPE_DOMAIN = "D"; // 后处理配置信息中inProcessDataType信息的对应领域对象的值值
	public static final String IN_PROCESS_DATA_TYPE_REPRESENTATION = "R"; // 后处理配置信息中inProcessDataType信息的对应值传递对象的值

	public static final String OBJECT_INSTANCE_SOURCE_I = "I"; // 对象实例的来源:输入数据
	public static final String OBJECT_INSTANCE_SOURCE_P = "P"; // 对象实例的来源:过程数据
	public static final String OBJECT_INSTANCE_SOURCE_D = "D"; // 对象实例的来源:领域对象
	public static final String OBJECT_INSTANCE_SOURCE_N = "N"; // 对象实例的来源:预处理产生的nodeParams
	public static final String OBJECT_INSTANCE_SOURCE_O = "O"; // 对象实例的来源:业务流属主领域对象
	public static final String OBJECT_INSTANCE_SOURCE_F = "F"; // 对象实例的来源:固定值

	public static final String NULL = "null";// 表示空值
	public static final String NOW_DATE = "now()";// 表示取当前时间
	public static final String UUID = "UUID()";// 表示取UUID
	public static final String SNOW_FLAKE = "snowFlake()";// 表示取雪花ID
	public static final String EMPTY_STRING = "\"\"";// 表示取空字符串

	public static final String CFG_ATTR_PATH_DYNAMIC_VALUE = "$";// 当以该标识'$'开头时，表示toObjectAttr是动态取值
	public static final String CFG_ATTR_PATH_DOLLOR_DOT = "$.";// $.attrName
	public static final String CFG_ATTR_PATH_DOT = "."; // 配置信息中fromAttrPath信息，表示点号
	public static final String START_WITH_DIGIT = "[0-9].*";

	public FlowNode getFlowNode() {
		return flowNode;
	}

	public void setFlowNode(FlowNode flowNode) {
		this.flowNode = flowNode;
	}

	/**
	 * 获取目标对象
	 * 
	 * @param objectSource     对象来源
	 * @param ObjectKey        对象Key
	 * @param objectIsList     对象是否是List
	 * @param objectAttr       对象属性
	 * @param context          业务流上下文
	 * @param businessFlowNode 业务流节点
	 * @return 返回目标对象,JsonObject or JsonArray
	 * @throws EngineException
	 */
	protected Object getContextObject(String objectSource, String objectKey, boolean objectIsList, String objectAttr,
			BusinessFlowContext context, BusinessFlowNode businessFlowNode)
			throws EngineException {
		if (StringUtils.isBlank(objectSource)) {
			return null;
		}
		if (context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {// 处理forEach循环的元素和index的情况
			if (ForEachNode.FOR_EACH_ELEMENT.equals(objectKey) || ForEachNode.FOR_EACH_ROW_INDEX.equals(objectKey)) {
				objectKey = objectKey + "_" + businessFlowNode.getBusinessFlowId();
			}
		}
		Object valueObject = null;
		if (OBJECT_INSTANCE_SOURCE_D.equals(objectSource)) {// 领域对象
			if (objectIsList) {
				List<AttributedObjectContainer> list = context.getAttributedObjects(objectKey);
				if (list != null) {
					JsonArray array = new JsonArray();
					for (AttributedObjectContainer objectContainer : list) {
						array.add(objectContainer.objectValues());
					}
					valueObject = array;
				} else {
					valueObject = null;
				}
				if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
					valueObject = JsonUtils.eval(valueObject, objectAttr);
				}
			} else {
				valueObject = context.getAttributedObject(objectKey).objectValues();
				if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
					valueObject = JsonUtils.eval(valueObject, objectAttr);
				}
			}
		} else if (OBJECT_INSTANCE_SOURCE_P.equals(objectSource)) {// 过程数据
			valueObject = context.getAttribute(objectKey);
			if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
				valueObject = JsonUtils.eval(valueObject, objectAttr);
			}
		} else if (OBJECT_INSTANCE_SOURCE_I.equals(objectSource)) {// 输入参数
			valueObject = JsonUtils.getObject(context.getReqData(), objectKey);
			if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
				valueObject = JsonUtils.eval(valueObject, objectAttr);
			}
		} else if (OBJECT_INSTANCE_SOURCE_O.equals(objectSource)) {// 属主对象
			valueObject = context.getDomainObject().objectValues();
			if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
				valueObject = JsonUtils.eval(valueObject, objectAttr);
			}
		} else if (OBJECT_INSTANCE_SOURCE_N.equals(objectSource)) {// 节点预处理参数
			valueObject = context.getAttribute(NODE_PARAM_KEY);
			if (!StringUtils.isBlank(objectAttr) && valueObject != null) {
				valueObject = JsonUtils.eval(valueObject, objectAttr);
			}
		} else if (OBJECT_INSTANCE_SOURCE_F.equals(objectSource)) {// 固定值
			if (NULL.equals(objectKey)) {//nul
				valueObject = null;
			} else if (NOW_DATE.equals(objectKey)) {//当前时间
				valueObject = DateUtils.dateTimeString(new Date());
			} else if (UUID.equals(objectKey)) {//uuid
				valueObject = KeyGenerate.uuidKey();
			} else if (SNOW_FLAKE.equals(objectKey)) {//雪花值
				valueObject = KeyGenerate.snowFlakeKey();
			} else if (EMPTY_STRING.equals(objectKey)) {//空字符串
				valueObject = StringUtils.emptyString();
			} else {
				valueObject = objectKey;
			}
		} else {// 非法的值
			logger.error("T[{}] invalid objectSource: {}", businessFlowNode.getTenantId(), objectSource);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid objectSource.");
		}
		return valueObject;
	}

	/**
	 * 设置目标对象
	 * 
	 * @param objectSource     对象来源
	 * @param ObjectKey        对象Key
	 * @param objectIsList     对象是否是List
	 * @param objectAttr       对象属性,因为从fastjson替换为Gson后，不支持通过JsonPath进行更新的操作，只按属性设置来支持
	 * @param value            值
	 * @param context          业务流上下文
	 * @param businessFlowNode 业务流节点
	 * @return 返回目标对象,JsonObject or JsonArray
	 * @throws EngineException
	 */
	@SuppressWarnings("unchecked")
	protected void setContextObject(String objectSource, String objectKey, boolean objectIsList, String objectAttr,
			Object value, BusinessFlowContext context, BusinessFlowNode businessFlowNode)
			throws EngineException {
		if (StringUtils.isBlank(objectSource)) {
			return;
		}
		if (context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {// 处理forEach循环的元素和index的情况
			if (ForEachNode.FOR_EACH_ELEMENT.equals(objectKey) || ForEachNode.FOR_EACH_ROW_INDEX.equals(objectKey)) {
				objectKey = objectKey + "_" + businessFlowNode.getBusinessFlowId();
			}
		}
		if (OBJECT_INSTANCE_SOURCE_D.equals(objectSource)) {// 领域对象
			if (objectIsList) {
				if (!StringUtils.isBlank(objectAttr)) {
					Object object = context.getAttributedObjects(objectKey);
					JsonUtils.set(object, objectAttr, value);
				} else {
					if (!(value instanceof List<?>)) {
						logger.error("T[{}] The value is not instanceof List<AttributedObjectContainer>!", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, objectAttr);
					}
					context.addAttributedObjects(objectKey, (List<AttributedObjectContainer>) value);
				}
			} else {
				if (!StringUtils.isBlank(objectAttr)) {
					JsonObject object = context.getAttributedObject(objectKey).objectValues();
					JsonUtils.set(object, objectAttr, value);
				} else {
					context.addAttributedObject(objectKey, (AttributedObjectContainer) value);
				}
			}
		} else if (OBJECT_INSTANCE_SOURCE_P.equals(objectSource)) {// 过程数据
			if (!StringUtils.isBlank(objectAttr)) {
				Object object = context.getAttribute(objectKey);
				JsonUtils.set(object, objectAttr, value);
			} else {
				context.setAttribute(objectKey, value);
			}
		} else if (OBJECT_INSTANCE_SOURCE_I.equals(objectSource)) {// 输入参数
			if (objectAttr != null && objectAttr.trim().length() > 0) {
				Object object = JsonUtils.getString(context.getReqData(), objectKey);
				JsonUtils.set(object, objectAttr, value);
			} else {
				context.getReqData().add(objectKey, JsonUtils.toJsonElement(value));
			}
		} else if (OBJECT_INSTANCE_SOURCE_O.equals(objectSource)) {// 属主对象
			if (!StringUtils.isBlank(objectAttr)) {
				Object object = context.getDomainObject().objectValues();
				JsonUtils.set(object.toString(), objectAttr, value);
			} else {
				context.getDomainObject().objectValues().add(objectKey, JsonUtils.toJsonElement(value));
			}
		} else if (OBJECT_INSTANCE_SOURCE_N.equals(objectSource)) {// 节点预处理参数
			if (!StringUtils.isBlank(objectAttr)) {
				Object object = context.getAttribute(NODE_PARAM_KEY);
				JsonUtils.set(object.toString(), objectAttr, value);
			} else {
				((JsonObject) context.getAttribute(NODE_PARAM_KEY)).add(objectKey, JsonUtils.toJsonElement(value));
			}
		} else {// 非法的值
			logger.error("T[{}] invalid objectSource: {}", businessFlowNode.getTenantId(), objectSource);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid objectSource.");
		}
		return;
	}

	/**
	 * 处理属性参数动态取值的情况，以$开头，如$user.name
	 * 
	 * @param toObjectAttr
	 * @param businessFlowNode
	 * @param context
	 * @return
	 * @throws EngineException
	 */
	public String dynamicAttrProcess(String toObjectAttr, BusinessFlowNode businessFlowNode,
			BusinessFlowContext context) throws EngineException {
		if (!StringUtils.isBlank(toObjectAttr) && toObjectAttr.startsWith(CFG_ATTR_PATH_DYNAMIC_VALUE)
				&& !toObjectAttr.startsWith(CFG_ATTR_PATH_DOLLOR_DOT)) {// 以$开头且不是$.时，表示动态取值,目前只支持从过程数据中获取
			if (toObjectAttr.contains(CFG_ATTR_PATH_DOT)) {// 如"user.id"
				String objectAttrKey = toObjectAttr.substring(1, toObjectAttr.indexOf(CFG_ATTR_PATH_DOT));
				String objectAttrAttr = toObjectAttr.substring(toObjectAttr.indexOf(CFG_ATTR_PATH_DOT) + 1);
				return (String) getContextObject(OBJECT_INSTANCE_SOURCE_P, objectAttrKey, false, objectAttrAttr,
						context, businessFlowNode);
			} else {
				return (String) getContextObject(OBJECT_INSTANCE_SOURCE_P, toObjectAttr, false, null, context,
						businessFlowNode);
			}
		} else {
			return toObjectAttr;
		}
	}

	
	/**
	 * 该节点类型的预处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public void preProcess(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		if (logger.isTraceEnabled()) {
			logger.trace("T[{}] Node's preProcess method start. {}({})", businessFlowNode.getTenantId(), businessFlowNode.getId(), businessFlowNode.getName());
		}
		// 获取节点的预配置信息
		String nodePreCfgString = businessFlowNode.getNodePreCfg();
		if (StringUtils.isBlank(nodePreCfgString )) {
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] The nodePreCfg is null.", businessFlowNode.getTenantId());
			}
			return;
		}
		// 解析节点预配置信息
		JsonObject nodePreCfg = JsonUtils.toJsonObject(nodePreCfgString);
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] nodePreCfg: {}", businessFlowNode.getTenantId(), JsonUtils.toJson(nodePreCfg));
		}
		
		// 目标对象类型
		String toObjectType = JsonUtils.getString(nodePreCfg, TO_OBJECT_TYPE_KEY);// Map和JSON
		// 参数映射规则
		JsonArray paramsRule = JsonUtils.getJsonArray(nodePreCfg, PARAMS_RULE_KEY);
		// toObjectType为空时，默认使用JSON格式
		if (StringUtils.isBlank(toObjectType)) {
			toObjectType = TO_OBJECT_TYPE_JSON;
		}
		// toObjectType为无效值时，直接报错处理
		if (!TO_OBJECT_TYPE_JSON.equals(toObjectType) && !TO_OBJECT_TYPE_MAP.equals(toObjectType)) {
			logger.error("T[{}] Invalid toObjectType(must be JSON or Map): {}", businessFlowNode.getTenantId(), toObjectType);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Invalid toObjectType(must be JSON or Map)");
		}
		// 未配置参数映射信息，直接返回
		if (JsonUtils.isEmpty(paramsRule)) {
			return;
		}

		// JSON类型
		if (TO_OBJECT_TYPE_JSON.equals(toObjectType)) {
			JsonObject nodeParams = new JsonObject();
			// 源对象是否是列表类型
			boolean isListType = false;
			// 源对象类型
			String fromObjectType = null;
			// 源对象的Key
			String fromObjectKey = null;
			// 属性映射数组
			JsonArray attrMappings = null;
			// 源对象
			Object fromObject = null;
			// 源属性路径
			String fromAttrPath = null;
			// 目标属性路径
			String toObjectAttr = null;

			// 循环处理每个参数映射规则
			for (JsonElement paramRuleElement : paramsRule.asList()) {
				JsonObject paramRule = paramRuleElement.getAsJsonObject();
				isListType = JsonUtils.getBoolean(paramRule, IS_LIST_TYPE_KEY);
				fromObjectType = JsonUtils.getString(paramRule, FROM_OBJECT_TYPE_KEY);
				fromObjectKey = JsonUtils.getString(paramRule, FROM_OBJECT_KEY_KEY);
				attrMappings = JsonUtils.getJsonArray(paramRule, ATTR_MAPPINGS_KEY);

				// 当属性映射数据为空时，跳过
				if (JsonUtils.isEmpty(attrMappings)) {
					continue;
				}

				// 获取源对象
				fromObject = this.getContextObject(fromObjectType, fromObjectKey, isListType, null, context,
						businessFlowNode);

				// 循环处理每一条属性映射
				for (JsonElement attrMapElement : attrMappings.asList()) {
					JsonObject attrMap = attrMapElement.getAsJsonObject();
					fromAttrPath = JsonUtils.getString(attrMap, FROM_ATTR_PATH_KEY);
					toObjectAttr = JsonUtils.getString(attrMap, TO_OBJECT_ATTR_KEY);
					// fromAttrPath为空，跳过
					if (StringUtils.isBlank(fromAttrPath)) {
						continue;
					}
					// toObjectAttr为空，跳过
					if (StringUtils.isBlank(toObjectAttr)) {
						continue;
					}
					toObjectAttr = this.dynamicAttrProcess(toObjectAttr, businessFlowNode, context);
					// toObjectAttr为空，跳过
					if (StringUtils.isBlank(toObjectAttr)) {
						continue;
					}

					// 使用JsonUtils将源属性的值复制到目标对象的属性上
					if (FROM_ATTR_PATH_ROOT.equals(fromAttrPath)) {// 特殊的值“/”，表示源对象整体复制
						nodeParams.add(toObjectAttr, JsonUtils.toJsonElement(fromObject));
						// JsonUtils.set(nodeParams, toObjectAttr,
						// fromObject);//JsonUtils在处理数字类型的Key时，存在问题
					} else {// 正常的JsonUtils
						// 当获取到的源对象为空时，跳过
						if (fromObject == null) {
							continue;
						}
						nodeParams.add(toObjectAttr, JsonUtils.toJsonElement(JsonUtils.eval(fromObject, fromAttrPath)));
						// JsonUtils.set(nodeParams, toObjectAttr, JsonUtils.eval(fromObject,
						// fromAttrPath));//JsonUtils在处理数字类型的Key时，存在问题
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("T[{}] nodeParams is: {}", businessFlowNode.getTenantId(), nodeParams);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("T[{}] Node's preProcess method finished. {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			}
			// 将转换好的节点参数保存到过程数据中
			context.setAttribute(NODE_PARAM_KEY, nodeParams);
		} else {// 增加对Map类型的支持，不再考虑支持
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "节点的预处理信息中toObjectType还未支持Map类型");
		}

	}

	
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public abstract void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException;

	
	/**
	 * 该节点类型的后置处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public void postProcess(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		if (logger.isTraceEnabled()) {
			logger.trace("T[{}] Node's postProcess method start. {}({})", businessFlowNode.getTenantId(), businessFlowNode.getId(), businessFlowNode.getName());
		}
		// 获取节点的预配置信息
		String nodePostCfgString = businessFlowNode.getNodePostCfg();
		if (StringUtils.isBlank(nodePostCfgString)) {
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] The nodePostCfg is null.", businessFlowNode.getTenantId());
			}
			return;
		}

		// 解析节点后配置信息
		JsonObject nodePostCfg = JsonUtils.toJsonObject(nodePostCfgString);
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] nodePostCfg: {}", businessFlowNode.getTenantId(), JsonUtils.toJson(nodePostCfg));
		}
		
		// 获取节点的配置信息，从中获得节点执行结果的数据格式
		String nodeCfgString = businessFlowNode.getNodeCfg();
		boolean isListResult = false;
		String nodeResultType = null;
		String nodeResultClass = null;
		if (!StringUtils.isBlank(nodeCfgString)) {
			// 解析节点配置信息
			JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
			isListResult = JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
			nodeResultType = JsonUtils.getString(nodeCfg, NODE_RESULT_TYPE_KEY);
			nodeResultClass = JsonUtils.getString(nodeCfg, NODE_RESULT_CLASS_KEY);
		}

		// 获取节点业务流处理后输出的结果对象
		Object nodeResult = context.getAttribute(NODE_RESULT_KEY);

		// 在过程数据中清空节点的输入数据和输出数据
		context.removeAttribute(NODE_PARAM_KEY);
		context.removeAttribute(NODE_RESULT_KEY);

		// 当结果为空时，不需要再进行后续处理
		if (nodeResult == null) {
			return;
		}

		// 业务流处理结果，是否作为过程数据输出
		boolean asInProcessData = JsonUtils.getBoolean(nodePostCfg, AS_IN_PROCESS_DATA_KEY);
		if (asInProcessData) {
			// String inProcessDataType = nodePostCfg, IN_PROCESS_DATA_TYPE_KEY);
			// String inProcessDataClass = nodePostCfg, IN_PROCESS_DATA_CLASS_KEY);
			String dataMappingId_inProcessData = JsonUtils.getString(nodePostCfg, DATA_MAPPING_ID_IN_PROCESS_DATA_KEY);
			// JsonObject subDataMappings_inProcessData = JsonUtils.getJsonObject(nodePostCfg,
			// 		SUB_DATA_MAPPINGS_IN_PROCESS_DATA_KEY);
			String inProcessDataKey = JsonUtils.getString(nodePostCfg, IN_PROCESS_DATA_KEY);

			if (StringUtils.isBlank(inProcessDataKey)) {
				logger.error("T[{}] inProcessDataKey of node post cfg is null", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "inProcessDataKey of node post cfg is null");
			}

			// 为空时表示使用原结构输出
			if (StringUtils.isBlank(dataMappingId_inProcessData)) {
				context.setAttribute(inProcessDataKey, nodeResult);
			} else {// 进行数据结构转换
				if (isListResult) {
					if (!(nodeResult instanceof JsonArray)) {
						logger.error("T[{}] nodeResult must be JsonArray. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonArray.");
					}
					JsonArray targetArray = new JsonArray();
					DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_inProcessData,
							(JsonArray) nodeResult, targetArray);
					context.setAttribute(inProcessDataKey, targetArray);
				} else {
					if (!(nodeResult instanceof JsonObject)) {
						logger.error("T[{}] nodeResult must be JsonObject. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonObject.");
					}
					JsonObject target = new JsonObject();
					DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_inProcessData,
							(JsonObject) nodeResult, target);
					context.setAttribute(inProcessDataKey, target);
				}
			}
		}

		// 业务流处理结果，是否作为领域对象数据输出
		boolean asDomainObjectValue = JsonUtils.getBoolean(nodePostCfg, AS_DOMAIN_OBJECT_VALUE_KEY);
		if (asDomainObjectValue) {
			String domainObjectValueKey = JsonUtils.getString(nodePostCfg, DOMAIN_OBJECT_VALUE_KEY);
			String dataMappingId_domainObjectValue = JsonUtils.getString(nodePostCfg,
					DATA_MAPPING_ID_DOMAIN_OBJECT_VALUE_KEY);
			String domainObjectId = JsonUtils.getString(nodePostCfg, DOMAIN_OBJECT_ID_KEY);
			// JsonObject subDataMappings_domainObjectValue = JsonUtils.getJsonObject(nodePostCfg,
			// 		SUB_DATA_MAPPINGS_DOMAIN_OBJECT_VALUE_KEY);

			if (StringUtils.isBlank(domainObjectValueKey)) {
				logger.error("T[{}] domainObjectValueKey of node post cfg is null", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "domainObjectValueKey of node post cfg is null");
			}
			// 当domainObjectId为空时，使用节点配置的结果类型信息
			if (StringUtils.isBlank(domainObjectId)
					&& ManagedObjectType.DOMAIN_OBJECT.equals(nodeResultType) && !StringUtils.isBlank(nodeResultClass)) {
				domainObjectId = nodeResultClass;
			}
			if (StringUtils.isBlank(domainObjectId)) {
				logger.error("T[{}] domainObjectId of node post cfg is null", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "domainObjectId of node post cfg is null");
			}

			DomainObject domainObject = DomainObjectProxy.getInstance().getDomainObject(context.getSystemId(),
					domainObjectId);
			if (domainObject == null) {
				logger.error("T[{}] can't get valid domainObject", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid domainObject");
			}

			// 目标对象
			if (!StringUtils.isBlank(dataMappingId_domainObjectValue)) {
				if (isListResult) {// 结果是列表类型
					if (!(nodeResult instanceof JsonArray)) {
						logger.error("T[{}] nodeResult must be JsonArray. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonArray.");
					}
					// 列表类型时，直接新增，不考虑已有列表的情况
					List<AttributedObjectContainer> list = new ArrayList<>();
					JsonArray resultArray = (JsonArray) nodeResult;
					JsonObject row = null;
					JsonObject target = null;
					AttributedObjectContainer attributedObjectContainer = null;
					// 循环创建领域对象容器实例
					for (int i = 0; i < resultArray.size(); i++) {
						row = resultArray.get(i).getAsJsonObject();
						attributedObjectContainer = new AttributedObjectContainer();
						attributedObjectContainer.setAttributedObject(domainObject);
						target = new JsonObject();
						// 进行参数转换
						DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_domainObjectValue, row, target);
						attributedObjectContainer.setObjectValues(target);
						list.add(attributedObjectContainer);
					}
					context.addAttributedObjects(domainObjectValueKey, list);
				} else {// 单个对象
					// 获取或初始化AttributedObjectContainer对象
					AttributedObjectContainer attributedObjectContainer = context
							.getAttributedObject(domainObjectValueKey);
					if (attributedObjectContainer == null) {
						attributedObjectContainer = new AttributedObjectContainer();
						attributedObjectContainer.setAttributedObject(domainObject);
						context.addAttributedObject(domainObjectValueKey, attributedObjectContainer);
					}
					if (!(nodeResult instanceof JsonObject)) {
						logger.error("T[{}] nodeResult must be JsonObject. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonObject.");
					}
					JsonObject target = new JsonObject();
					// 进行参数转换
					DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_domainObjectValue, (JsonObject) nodeResult, target);
					attributedObjectContainer.setObjectValues(target);
				}
			} else {// 使用原结果
				if (isListResult) {// 结果是列表类型
					if (!(nodeResult instanceof JsonArray)) {
						logger.error("T[{}] nodeResult must be JsonArray. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonArray.");
					}
					// 列表类型时，直接新增，不考虑已有列表的情况
					List<AttributedObjectContainer> list = new ArrayList<>();
					JsonArray resultArray = (JsonArray) nodeResult;
					JsonObject row = null;
					AttributedObjectContainer attributedObjectContainer = null;
					// 循环创建领域对象容器实例
					for (int i = 0; i < resultArray.size(); i++) {
						row = resultArray.get(i).getAsJsonObject();
						attributedObjectContainer = new AttributedObjectContainer();
						attributedObjectContainer.setAttributedObject(domainObject);
						attributedObjectContainer.setObjectValues(row);
						list.add(attributedObjectContainer);
					}
					context.addAttributedObjects(domainObjectValueKey, list);
				} else {// 结果是单对象类型
					if (!(nodeResult instanceof JsonObject)) {
						logger.error("T[{}] nodeResult must be JsonObject. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonObject.");
					}
					// 获取或初始化AttributedObjectContainer对象
					AttributedObjectContainer attributedObjectContainer = context
							.getAttributedObject(domainObjectValueKey);
					if (attributedObjectContainer == null) {
						attributedObjectContainer = new AttributedObjectContainer();
						attributedObjectContainer.setAttributedObject(domainObject);
						context.addAttributedObject(domainObjectValueKey, attributedObjectContainer);
					}
					attributedObjectContainer.setObjectValues((JsonObject) nodeResult);
				}
			}
		}
		// 业务流处理结果，是否作为业务流结果数据输出
		boolean asBusinessProcessResult = JsonUtils.getBoolean(nodePostCfg, AS_BUSINESS_PROCESS_RESULT_KEY);
		if (asBusinessProcessResult) {
			String dataMappingId_Result = JsonUtils.getString(nodePostCfg, DATA_MAPPING_ID_RESULT_KEY);
			// JsonObject subDataMappings_Result = JsonUtils.getJsonObject(nodePostCfg, SUB_DATA_MAPPINGS_RESULT);

			// 从缓存获取业务流对象，用于判断是否主业务流。
			BusinessFlow businessFlow = BusinessFlowProxy.getInstance().getBusinessFlow(context.getSystemId(),
					businessFlowNode.getBusinessFlowId());

			// 使用数据转换工具进行数据转换后，设置到上下文结果对象或作为业务流返回结果
			if (!StringUtils.isBlank(dataMappingId_Result)) {// 需要进行转换
				if (isListResult) {
					if (!(nodeResult instanceof JsonArray)) {
						logger.error("T[{}] T[{}] nodeResult must be JsonArray. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonArray.");
					}
					JsonArray targetArray = new JsonArray();
					DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_Result, (JsonArray) nodeResult, targetArray);
					if (StringUtils.isBlank(businessFlow.getParentFlowId())) {
						context.setResulteData(targetArray);
					} else {
						context.setAttribute(NODE_RESULT_KEY, targetArray);
					}
				} else {
					if (!(nodeResult instanceof JsonObject)) {
						logger.error("T[{}] nodeResult must be JsonObject. but type is {}", businessFlowNode.getTenantId(), nodeResult.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeResult must be JsonObject.");
					}
					JsonObject target = new JsonObject();
					DataTransferAbility.getInstance().transfer(context.getSystemId(), dataMappingId_Result, (JsonObject) nodeResult, target);
					if (StringUtils.isBlank(businessFlow.getParentFlowId())) {
						context.setResulteData(target);
					} else {
						context.setAttribute(NODE_RESULT_KEY, target);
					}
				}
			} else {// 为空，原结构输出
				if (StringUtils.isBlank(businessFlow.getParentFlowId())) {
					context.setResulteData(nodeResult);
				} else {
					context.setAttribute(NODE_RESULT_KEY, nodeResult);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("T[{}] Node's postProcess method finished. {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
		}
	}

	/**
	 * 节点配置信息加载逻辑
	 * @param nodeCfg
	 * @param node
	 * @param info
	 */
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {

	}

	/**
	 * 保存前节点配置信息处理逻辑
	 * @param nodeCfg
	 * @param node
	 * @param info
	 */
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) throws EngineException {

	}
}
