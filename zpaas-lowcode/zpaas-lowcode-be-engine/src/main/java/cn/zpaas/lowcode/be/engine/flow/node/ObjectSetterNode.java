package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         对象属性设置节点的实现类，主要用于初始化对象或者设置对象某些属性的值
 */
public class ObjectSetterNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ObjectSetterNode.class);

	private static final String CFG_IS_LIST_TYPE_KEY = "isListType"; // 目标对象是否列表类型存放的Key

	private static final String CFG_TO_OBJECT_INSTANCE_SOURCE_KEY = "toObjectInstanceSource"; // 目标对象实例的来源存放的Key
	private static final String CFG_TO_OBJECT_INSTANCE_KEY_KEY = "toObjectInstanceKey"; // 目标对象实例的Key值存放的Key
	private static final String CFG_TO_OBJECT_INSTANCE_ATTR_KEY = "toObjectInstanceAttr"; // 目标对象实例对应对象属性存放的Key
	private static final String CFG_TO_OBJECT_TYPE_KEY = "toObjectType"; // 目标对象的类型存放的Key
	private static final String CFG_TO_OBJECT_CLASS_KEY = "toObjectClass"; // 目标对象的类存放的Key

	private static final String CFG_PARAMS_RULE_KEY = "paramsRule"; // 配置信息中paramsRule信息的Key值
	private static final String CFG_FROM_OBJECT_IS_LIST_KEY = "fromObjectIsList"; // 配置信息中fromObjectIsList信息的Key值
	private static final String CFG_FROM_OBJECT_TYPE_KEY = "fromObjectType"; // 配置信息中fromObjectType信息的Key值
	private static final String CFG_FROM_OBJECT_KEY_KEY = "fromObjectKey"; // 配置信息中fromObjectKey信息的Key值
	private static final String CFG_ATTR_MAPPINGS_KEY = "attrMappings"; // 配置信息中attrMappings信息的Key值
	private static final String CFG_FROM_ATTR_PATH_KEY = "fromAttrPath"; // 配置信息中fromAttrPath信息的Key值
	private static final String CFG_TO_OBJECT_ATTR_KEY = "toObjectAttr"; // 配置信息中toObjectAttr信息的Key值

	private static final String CFG_ATTR_PATH_ROOT = "/"; // 配置信息中fromAttrPath信息，表示源对象本身的字符
	private static final String CFG_ADD_ARRAY_ELEMENT_ROOT = "[@+]";// 当以该标识结尾时，表示往数组中新增一条，当数组为null时，默认初始化

	
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
		String toObjectInstanceSource = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_INSTANCE_SOURCE_KEY);
		String toObjectInstanceKey = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_INSTANCE_KEY_KEY);
		String toObjectInstanceAttr = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_INSTANCE_ATTR_KEY);
		String toObjectType = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_TYPE_KEY);
		String toObjectClass = JsonUtils.getString(nodeCfg, CFG_TO_OBJECT_CLASS_KEY);

		toObjectInstanceAttr = this.dynamicAttrProcess(toObjectInstanceAttr, businessFlowNode, context);

		// 参数映射规则
		JsonArray paramsRule = JsonUtils.getJsonArray(nodeCfg, CFG_PARAMS_RULE_KEY);
		// 未配置参数映射信息，表示只创建一个空对象或列表
		if (paramsRule == null) {
			paramsRule = new JsonArray();
		}

		Object toObject = null;
		// 表示目标对象需要新建时，toObjectType和toObjectClass不能为空
		if (StringUtils.isBlank(toObjectInstanceSource)) {
			if (StringUtils.isBlank(toObjectType) || StringUtils.isBlank(toObjectClass)) {
				logger.error("T[{}] When toObject need to be created, toObjectType and toObjectClass can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "When toObject need to be created, toObjectType and toObjectClass can't be null.");
			}
			

			if (isListType) {
				JsonArray newArray = new JsonArray();
				// if (!JsonUtils.isEmpty(paramsRule)) {// 创建一个空列表时，不需要默认增加一个对象到列表中
				// 	newArray.add(JsonUtils.toJsonElement(newObject));
				// }
				toObject = newArray;
			} else {
				Object newObject = null;
				if (ManagedObjectType.DOMAIN_OBJECT.equals(toObjectType)) {
					DomainObject object = DomainObjectProxy.getInstance().getDomainObject(businessFlowNode.getSystemId(), toObjectClass);
					newObject = object.newInstance();
				} else if (ManagedObjectType.VALUE_OBJECT.equals(toObjectType)) {
					ValueObject object = ValueObjectProxy.getInstance().getValueObject(businessFlowNode.getSystemId(), toObjectClass);
					newObject = object.newInstance();
				} else {
					if(toObjectClass.equals(JsonObject.class.getName())) {
						newObject = new JsonObject();
					}else if(toObjectClass.equals(String.class.getName())) {
						newObject = StringUtils.emptyString();
					}else {
						newObject = null;
					}
				}
				toObject = newObject;// 初始化目标对象
			}
		} else {// 表示目标对象是已经存在的对象
			// 获取目标对象
			toObject = getContextObject(toObjectInstanceSource, toObjectInstanceKey, isListType, toObjectInstanceAttr, context, businessFlowNode);
			if (toObject == null) {//尝试根据对象类型及对象类初始化对象
				if (isListType) {
					JsonArray newArray = new JsonArray();
					// if (!JsonUtils.isEmpty(paramsRule)) {// 创建一个空列表时，不需要默认增加一个对象到列表中
					// 	newArray.add(JsonUtils.toJsonElement(newObject));
					// }
					toObject = newArray;
				} else {
					Object newObject = null;
					if (StringUtils.isBlank(toObjectType) || StringUtils.isBlank(toObjectClass)) {//未配置对象类型
						newObject = new JsonObject();
					} else if (ManagedObjectType.DOMAIN_OBJECT.equals(toObjectType)) {
						DomainObject object = DomainObjectProxy.getInstance().getDomainObject(businessFlowNode.getSystemId(), toObjectClass);
						newObject = object.newInstance();
					} else if (ManagedObjectType.VALUE_OBJECT.equals(toObjectType)) {
						ValueObject object = ValueObjectProxy.getInstance().getValueObject(businessFlowNode.getSystemId(), toObjectClass);
						newObject = object.newInstance();
					} else {
						if(toObjectClass.equals(JsonObject.class.getName())) {
							newObject = new JsonObject();
						}else if(toObjectClass.equals(String.class.getName())) {
							newObject = StringUtils.emptyString();
						}else {
							newObject = null;
						}
					}
					toObject = newObject;// 初始化目标对象
				}
				
				setContextObject(toObjectInstanceSource, toObjectInstanceKey, isListType, toObjectInstanceAttr, toObject, context, businessFlowNode);
			}
		}

		// if (toObject == null) {
		// 	logger.error("toObject can't be null. businessflowNodeId: {}", businessFlowNode.getId());
		// 	throw new EngineException(ResultStatus.INTERNAL_ERROR, "toObject can't be null.");
		// }

		// 源对象是否是列表
		boolean fromObjectIsList = false;
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
			fromObjectIsList = JsonUtils.getBoolean(paramRule, CFG_FROM_OBJECT_IS_LIST_KEY);
			fromObjectType = JsonUtils.getString(paramRule, CFG_FROM_OBJECT_TYPE_KEY);
			fromObjectKey = JsonUtils.getString(paramRule, CFG_FROM_OBJECT_KEY_KEY);
			attrMappings = JsonUtils.getJsonArray(paramRule, CFG_ATTR_MAPPINGS_KEY);

			// 当属性映射数据为空时，跳过
			if (JsonUtils.isEmpty(attrMappings)) {
				continue;
			}
			// 获取源对象
			fromObject = getContextObject(fromObjectType, fromObjectKey, fromObjectIsList, null, context, businessFlowNode);

			// 当获取到的源对象为空时，跳过
			if (fromObject == null && !OBJECT_INSTANCE_SOURCE_F.equals(fromObjectType)) {
				continue;
			}
			// 循环处理每一条属性映射
			for (JsonElement attrMapElement : attrMappings.asList()) {
				JsonObject attrMap = attrMapElement.getAsJsonObject();
				fromAttrPath = JsonUtils.getString(attrMap, CFG_FROM_ATTR_PATH_KEY);
				toObjectAttr = JsonUtils.getString(attrMap, CFG_TO_OBJECT_ATTR_KEY);

				// 非固定值时
				if (!OBJECT_INSTANCE_SOURCE_F.equals(fromObjectType)) {
					// fromAttrPath为空，跳过
					if (StringUtils.isBlank(fromAttrPath)) {
						continue;
					}
				}

				// toObjectAttr为空，跳过
				if (StringUtils.isBlank(toObjectAttr)) {
					continue;
				}
				// 特殊的值“/”，表示赋值给对象本身
				if (CFG_ATTR_PATH_ROOT.equals(toObjectAttr)) {
					if (OBJECT_INSTANCE_SOURCE_F.equals(fromObjectType)) {// 固定值
						toObject = fromObject;
					} else if (CFG_ATTR_PATH_ROOT.equals(fromAttrPath)) {// 特殊的值“/”，表示源对象整体复制
						toObject = fromObject;
					} else {// 正常的JSONPATH
						toObject = JsonUtils.eval(fromObject, fromAttrPath);
					}
					continue;
				}

				toObjectAttr = this.dynamicAttrProcess(toObjectAttr, businessFlowNode, context);
				// toObjectAttr为空，跳过
				if (toObjectAttr == null || toObjectAttr.trim().length() == 0) {
					continue;
				}

				// 属性复制
				if (isListType) {// 目标是个列表
					if (!(toObject instanceof JsonArray)) {
						logger.error("T[{}] toObject's type must be JsonArray. now is: {}", businessFlowNode.getTenantId(), toObject==null?"null":toObject.getClass());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "toObject's type must be JsonArray.");
					}

					// 使用JSONPATH将源属性的值复制到目标对象的属性上
					Object value = null;
					if (OBJECT_INSTANCE_SOURCE_F.equals(fromObjectType)) {// 固定值
						value = fromObject;
					} else if (CFG_ATTR_PATH_ROOT.equals(fromAttrPath)) {// 特殊的值“/”，表示源对象整体复制
						value = fromObject;
					} else {// 正常的JSONPATH
						value = JsonUtils.eval(fromObject, fromAttrPath);
					}

					JsonArray toArray = ((JsonArray) toObject);
					if (CFG_ADD_ARRAY_ELEMENT_ROOT.equals(toObjectAttr)) {// 当toObjectAttr为[@+]表示往目标对象列表中添加一个元素
						toArray.add(JsonUtils.toJsonElement(value));
					} else { // 其他情况
						JsonObject toArrayItemJsonObject = null;
						for (int i = 0; i < toArray.size(); i++) {
							toArrayItemJsonObject = toArray.get(i).getAsJsonObject();

							if (toObjectAttr.endsWith(CFG_ADD_ARRAY_ELEMENT_ROOT)) {// 表示新增一条数组元素，如"operationInfo.id[@+]"，表示item.operationInfo是个列表，往该列表中增加一个元素，设置该元素的id属性
								toObjectAttr = toObjectAttr.substring(0, toObjectAttr.length() - 4);
								Object subObject = null;
								if (toObjectAttr.contains(CFG_ATTR_PATH_DOT)) {// operationInfo.id[@+]
									String arrayKey = toObjectAttr.substring(0, toObjectAttr.indexOf(CFG_ATTR_PATH_DOT));
									String subObjectKey = toObjectAttr.substring(toObjectAttr.indexOf(CFG_ATTR_PATH_DOT) + 1);
									subObject = JsonUtils.eval(toArrayItemJsonObject, arrayKey);
									JsonArray array = null;
									if (subObject == null) {// 初始化JsonArray,并设置回原对象
										array = new JsonArray();
										JsonUtils.set(toArrayItemJsonObject, arrayKey, array);
									} else {
										if (!(subObject instanceof JsonArray)) {// 非数组，报错
											logger.error("T[{}] the object of toObjectAttr is not type of JsonArray. is: {}", businessFlowNode.getTenantId(), subObject.getClass());
											throw new EngineException(ResultStatus.INTERNAL_ERROR, "the object of toObjectAttr is not type of JsonArray.");
										}
										array = (JsonArray) subObject;
									}
									JsonObject valueJsonObject = null;
									if (array.isEmpty()) {// 当目标数据为空时
										valueJsonObject = new JsonObject();
										array.add(valueJsonObject);
									} else {
										valueJsonObject = array.get(0).getAsJsonObject();// 目前只考虑支持设置一个元素
									}
									JsonUtils.set(valueJsonObject, subObjectKey, value);
								} else {// operationInfo[@+]
									subObject = JsonUtils.eval(toArrayItemJsonObject, toObjectAttr);
									JsonArray array = null;
									if (subObject == null) {// 初始化JsonArray
										array = new JsonArray();
										JsonUtils.set(toArrayItemJsonObject, toObjectAttr, array);
									} else {
										if (!(subObject instanceof JsonArray)) {// 非数组，报错
											logger.error("T[{}] the object of toObjectAttr is not type of JsonArray. is: {}", businessFlowNode.getTenantId(), subObject.getClass());
											throw new EngineException(ResultStatus.INTERNAL_ERROR, "the object of toObjectAttr is not type of JsonArray.");
										}
										array = (JsonArray) subObject;
									}
									array.add(JsonUtils.toJsonElement(value));
								}
							} else {
								if (Pattern.matches(START_WITH_DIGIT, toObjectAttr)) {// 以数字开头的Path，暂时按JsonObject对象处理
									toArrayItemJsonObject.add(toObjectAttr, JsonUtils.toJsonElement(value));
								} else {
									JsonUtils.set(toArrayItemJsonObject, toObjectAttr, value);
								}
							}
						}
					}
				} else {// 目标不是列表
					// 使用JSONPATH将源属性的值复制到目标对象的属性上
					Object value = null;
					if (OBJECT_INSTANCE_SOURCE_F.equals(fromObjectType)) {// 固定值
						value = fromObject;
					} else if (CFG_ATTR_PATH_ROOT.equals(fromAttrPath)) {// 特殊的值“/”，表示源对象整体复制
						value = fromObject;
					} else {// 正常的JSONPATH
						value = JsonUtils.eval(fromObject, fromAttrPath);
					}
					if (!(toObject instanceof JsonObject)) {//表示原始类型，直接赋值
						toObject = value;
						continue;//直接中断本次执行
					}
					JsonObject toObjectJson = (JsonObject) toObject;
					
					if (toObjectAttr.endsWith(CFG_ADD_ARRAY_ELEMENT_ROOT)) {// 表示新增一条数组元素
						toObjectAttr = toObjectAttr.substring(0, toObjectAttr.length() - 4);
						Object subObject = null;
						if (toObjectAttr.contains(CFG_ATTR_PATH_DOT)) {
							String arrayKey = toObjectAttr.substring(0, toObjectAttr.indexOf(CFG_ATTR_PATH_DOT));
							String subObjectKey = toObjectAttr.substring(toObjectAttr.indexOf(CFG_ATTR_PATH_DOT) + 1);
							subObject = JsonUtils.eval(toObjectJson, arrayKey);
							JsonArray array = null;
							if (subObject == null) {// 初始化JsonArray
								array = new JsonArray();
								JsonUtils.set(toObject, arrayKey, array);
							} else {
								if (!(subObject instanceof JsonArray)) {// 非数组，报错
									logger.error("T[{}] the object of toObjectAttr is not type of JsonArray. is: {}", businessFlowNode.getTenantId(), subObject.getClass());
									throw new EngineException(ResultStatus.INTERNAL_ERROR, "the object of toObjectAttr is not type of JsonArray.");
								}
								array = (JsonArray) subObject;
							}
							JsonObject valueJsonObject = null;
							if (array.isEmpty()) {
								valueJsonObject = new JsonObject();
								array.add(valueJsonObject);
							} else {
								valueJsonObject = array.get(0).getAsJsonObject();// 目前只考虑支持设置一个元素
							}
							JsonUtils.set(valueJsonObject, subObjectKey, value);
						} else {
							subObject = JsonUtils.eval(toObjectJson, toObjectAttr);
							JsonArray array = null;
							if (subObject == null) {// 初始化JsonArray
								array = new JsonArray();
								JsonUtils.set(toObject, toObjectAttr, array);
							} else {
								if (!(subObject instanceof JsonArray)) {// 非数组，报错
									logger.error("T[{}] the object of toObjectAttr is not type of JsonArray. is: {}", businessFlowNode.getTenantId(), subObject.getClass());
									throw new EngineException(ResultStatus.INTERNAL_ERROR, "the object of toObjectAttr is not type of JsonArray.");
								}
								array = (JsonArray) subObject;
							}
							array.add(JsonUtils.toJsonElement(value));
							
						}
					} else {
						if (Pattern.matches(START_WITH_DIGIT, toObjectAttr)) {// 以数字开头的Path，暂时按JsonObject对象处理
							toObjectJson.add(toObjectAttr, JsonUtils.toJsonElement(value));
						} else {
							JsonUtils.set(toObject, toObjectAttr, value);
						}
					}
				}
			}
		}

		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, toObject);}

}
