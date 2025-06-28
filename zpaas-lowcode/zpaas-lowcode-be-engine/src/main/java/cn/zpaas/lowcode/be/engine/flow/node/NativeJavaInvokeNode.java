package cn.zpaas.lowcode.be.engine.flow.node;


import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.ClassUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * Java方法调用节点的实现类
 */
public class NativeJavaInvokeNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(NativeJavaInvokeNode.class);

	private static final String CFG_TARGET_JAVA_CLASS_KEY = "targetJavaClass"; //调用目标Java类存放的Key
	private static final String CFG_TARGET_JAVA_METHOD_KEY = "targetJavaMethod"; //调用目标方法名存放的Key
	private static final String CFG_TARGET_OBJECT_SOURCE_KEY = "targetObjectSource"; //调用目标对象来源存放的Key
	private static final String CFG_TARGET_METHOD_PARAM_TYPES_KEY = "targetMethodParamTypes"; //调用目标方法参数列表存放的Key
	
	private static final String CFG_PARAM_TYPE_KEY = "paramType"; // 参数类型的Key值
	private static final String CFG_PARAM_NAME_KEY = "paramName"; // 参数名称的Key值
	private static final String CFG_IS_LIST_KEY = "isList"; // 是否列表的Key值
	private static final String CFG_ELEMENT_TYPE_KEY = "elementType"; // 列表中元素的类型信息的Key值
	
	private static final String CFG_PARAM_OBJECT_SOURCE_KEY = "paramObjectSource"; // 调用目标对象参数的来源信息的Key值
	private static final String CFG_PARAM_OBJECT_KEY_KEY = "paramObjectKey"; // 调用目标对象参数的key信息的Key值
	
	private static final String CFG_TARGET_OBJECT_SOURCE_N = "N"; //新建
	private static final String CFG_TARGET_OBJECT_SOURCE_C = "C"; //来自Spring容器
	
	
	
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
		String targetJavaClass = JsonUtils.getString(nodeCfg, CFG_TARGET_JAVA_CLASS_KEY);
		String targetJavaMethod = JsonUtils.getString(nodeCfg, CFG_TARGET_JAVA_METHOD_KEY);
		String targetObjectSource = JsonUtils.getString(nodeCfg, CFG_TARGET_OBJECT_SOURCE_KEY);
		JsonArray targetMethodParamTypes = JsonUtils.getJsonArray(nodeCfg, CFG_TARGET_METHOD_PARAM_TYPES_KEY);
		String paramObjectSource = JsonUtils.getString(nodeCfg, CFG_PARAM_OBJECT_SOURCE_KEY);
		String paramObjectKey = JsonUtils.getString(nodeCfg, CFG_PARAM_OBJECT_KEY_KEY);
		boolean isListResult =  JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
		String nodeResultType = JsonUtils.getString(nodeCfg, NODE_RESULT_TYPE_KEY);
		
		// 必须的配置信息，为空直接报错
		if (StringUtils.isBlank(targetJavaClass) || StringUtils.isBlank(targetJavaMethod) || StringUtils.isBlank(targetObjectSource)) {
			logger.error("T[{}] targetJavaClass targetJavaMethod and targetObjectSource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "argetJavaClass targetJavaMethod and targetObjectSource can't be null.");
		}
		
		//获取源参数实例
		JsonObject sourceParamObject = (JsonObject) getContextObject(paramObjectSource, paramObjectKey, false, null, context, businessFlowNode);
		
		//获取调用目标类
		Class<?> clazz = null;
		clazz = ClassUtils.getClass(targetJavaClass);
		
		//构建调用目标方法参数类型数组
		Class<?>[] paramClasses = null;
		if(targetMethodParamTypes != null && targetMethodParamTypes.size() > 0) {
			paramClasses = new Class[targetMethodParamTypes.size()];	
			String paramTypeString = null;
			for(int i=0; i< targetMethodParamTypes.size(); i++) {
				paramTypeString = JsonUtils.getString(targetMethodParamTypes.get(i).getAsJsonObject(), CFG_PARAM_TYPE_KEY);
				if(StringUtils.isBlank(paramTypeString)) {
					logger.error("T[{}] paramType can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "paramType can't be null.");
				}
				paramClasses[i] = ClassUtils.getClass(paramTypeString);
				
			}
		}
		
		//获取调用目标方法
		Method method = ReflectionUtils.findMethod(clazz, targetJavaMethod, paramClasses);
		
		//进行参数处理及转换
		int paramCount = method.getParameterCount();
		Parameter[] params = method.getParameters();
		//初始化参数实例数组
		Object[] paramInstances = new Object[paramCount];	
		if(paramCount > 0) {
			Parameter param = null;
			String paramName = null;
			boolean isList = false;
			
			for(int i=0; i<paramCount; i++) {
				param = params[i];
				paramName = null;
				if(targetMethodParamTypes != null && targetMethodParamTypes.get(i) != null) {
					paramName = JsonUtils.getString(targetMethodParamTypes.get(i).getAsJsonObject(), CFG_PARAM_NAME_KEY);
				}
				if(StringUtils.isBlank(paramName)) {
					paramName = param.getName();
				}
				
				//参数是否是列表类型
				isList = false;
				if(targetMethodParamTypes != null && targetMethodParamTypes.get(i) != null) {
					isList = JsonUtils.getBoolean(targetMethodParamTypes.get(i).getAsJsonObject(), CFG_IS_LIST_KEY);
				}
				Object paramObject = null;
				if(isList) {//如果是列表类型
					//获取列表元素的类型
					String elementTypeString = null;
					if(targetMethodParamTypes != null && targetMethodParamTypes.get(i) != null) {
						elementTypeString = JsonUtils.getString(targetMethodParamTypes.get(i).getAsJsonObject(), CFG_ELEMENT_TYPE_KEY);
					}
					//如果为空，则从泛型参数中获取
					if((StringUtils.isBlank(elementTypeString)) &&
							param.getAnnotatedType() instanceof AnnotatedParameterizedType) {
						elementTypeString = ((AnnotatedParameterizedType)param.getAnnotatedType()).getAnnotatedActualTypeArguments()[0].getType().getTypeName();
					}
					//如果为空，则报错
					if(StringUtils.isBlank(elementTypeString)) {
						logger.error("T[{}] elementTypeString can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "elementTypeString can't be null.");
					}
					//获取列表元素的类型
					Class<?> elementClass = null;
					try {
						elementClass = Class.forName(elementTypeString);
					} catch (ClassNotFoundException e) {
						logger.error("T[{}] get element object class failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "get element object class failed.", null, e);
					}
					//创建列表实例
					@SuppressWarnings("unchecked")
					Class<? extends Collection<Object>> paramClass = (Class<? extends Collection<Object>>)param.getType();
					//如果能直接创建则直接创建，如果不能是判断是List或Set的子类，使用ArrayList以及HashSet来创建，：其他情况需要再扩充
					Collection<Object> list = initCollectionObject(paramClass);
					if(list == null) {
						logger.error("T[{}] create list object failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "create list object .");
					}
					//循环进行参数转换
					JsonArray array = JsonUtils.getJsonArray(sourceParamObject, paramName);
					for(int k =0; k<array.size(); k++) {
						//将JSON格式转换成Java对象格式
						list.add(JsonUtils.fromJson(array.get(k).getAsString(), elementClass));
					}					
					paramObject = list;
				}else {//非列表类型
					//将JSON格式转换成Java对象格式
					paramObject = JsonUtils.fromJson(JsonUtils.getString(sourceParamObject, paramName), param.getType());
				}				
				
				paramInstances[i] = paramObject;
			}
		}
		
		Object object = null;
		if(CFG_TARGET_OBJECT_SOURCE_N.equals(targetObjectSource)) {//需要新建对象
			//新建调用目标对象实例
			try {
				object = clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				logger.error("T[{}] new target object failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "new target object failed.", null, e);
			} 		
		}else if(CFG_TARGET_OBJECT_SOURCE_C.equals(targetObjectSource)) {
			object = SpringContextUtils.getBean(clazz);
			if(object == null) {
				logger.error("T[{}] get target object from spring context failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "get target object from spring context failed.");
			}
		}else {
			logger.error("T[{}] invalid targetObjectSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid targetObjectSource.");
		}
		//调用目标方法并获取返回值
		Object result = ReflectionUtils.invokeMethod(method, object, paramInstances);
		
		//当节点的结果类型为领域对象或值传递对象时，转换成JSON格式
		if((ManagedObjectType.DOMAIN_OBJECT.equals(nodeResultType) || ManagedObjectType.VALUE_OBJECT.equals(nodeResultType)) && result != null) {	
			String jsonString = JsonUtils.toJson(result);
			if(isListResult) {
				result = JsonUtils.toJsonArray(jsonString);
			}else {
				result = JsonUtils.toJsonObject(jsonString);
			}
		}
		
		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<Object> initCollectionObject(Class<? extends Collection<?>> collectionClass) {
		Collection<Object> list = null;
		try {//尝试直接创建
			list = (Collection<Object>)collectionClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			//忽略，设计如此
		} 
		//直接创建失败
		if(list == null) {
			Set<String> superSet = new HashSet<>();
			ClassUtils.getParentClass(collectionClass, superSet);
			ClassUtils.getParentInterface(collectionClass, superSet);
			superSet.add(collectionClass.getName());
			if(superSet.contains("java.util.List")) {//List的子类，创建ArrayList对象
				list = new ArrayList<>();
			}else if(superSet.contains("java.util.Set")) {//Set的子类，创建Set对象
				list = new HashSet<>();
			}else {
				//不支持的情况
			}
		}
		return list;
	}
	
}
