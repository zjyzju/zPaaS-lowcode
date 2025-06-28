package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.DynamicMappingAbility;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.command.DomainCommand;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 动态本地方法调用节点的实现类，主要实现本地方法的调用，包括
 * 		调用服务方法（应用服务、技术服务、第三方服务和领域服务）
 * 		和领域对象方法（聚合根对象、实体对象和值对象）。
 */
public class DynamicLocalInvokeNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(DynamicLocalInvokeNode.class);

	private static final String CFG_DYNAMIC_MAPPING_ID_KEY = "dynamicMappingId"; //动态映射标识存放的Key
	private static final String CFG_KEY_VALUE_SOURCE_KEY = "keyValueSource"; //动态关键值来源存放的Key
	private static final String CFG_KEY_VALUE_KEY_KEY = "keyValueKey"; //动态关键值Key存放的Key
	private static final String CFG_KEY_VALUE_ATTR_KEY = "keyValueAttr"; //动态关键值属性存放的Key

	public static final String CFG_ASYNC_INVOKE_KEY = "asyncInvoke"; //asyncInvoke存放的Key
	public static final String CFG_IN_PARAM_INSTANCE_SOURCE_KEY = "inParamInstanceSource"; //调用方法标识存放的Key
	public static final String CFG_IN_PARAM_INSTANCE_KEY_KEY = "inParamInstanceKey"; //调用方法标识存放的Key
	
	
	
	
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
		String keyValueSource = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_SOURCE_KEY);
		String keyValueKey = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_KEY_KEY);
		String keyValueAttr = JsonUtils.getString(nodeCfg, CFG_KEY_VALUE_ATTR_KEY);
		String dynamicMappingId = JsonUtils.getString(nodeCfg, CFG_DYNAMIC_MAPPING_ID_KEY);
		boolean asyncInvoke = JsonUtils.getBoolean(nodeCfg, CFG_ASYNC_INVOKE_KEY);
		String inParamInstanceSource = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_SOURCE_KEY);
		String inParamInstanceKey = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_KEY_KEY);
		
		if(StringUtils.isBlank(dynamicMappingId) || StringUtils.isBlank(keyValueSource) || StringUtils.isBlank(keyValueKey)) {
			logger.error("T[{}] dynamicMappingId keyValueSource and keyValueKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dynamicMappingId keyValueSource and keyValueKey can't be null. ");
		}
		// 动态属性的情况
		if (!StringUtils.isBlank(keyValueAttr)) {
			keyValueAttr = this.dynamicAttrProcess(keyValueAttr, businessFlowNode, context);
		}

		//获取动态映射的关键属性值
		String keyValue = (String)getContextObject(keyValueSource, keyValueKey, false, keyValueAttr, context, businessFlowNode);
		if(StringUtils.isBlank(keyValue)) {
			logger.error("T[{}] keyValue can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "keyValue can't be null!");
		} 
		
		//根据动态映射标识以及关键属性值获取方法标识
		String operationId = DynamicMappingAbility.getInstance().getMappingObjectId(businessFlowNode.getSystemId(), dynamicMappingId, keyValue);
		if(StringUtils.isBlank(operationId)) {
			logger.error("T[{}] operationId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "operationId can't be null!");
		} 

		Operation operation = ServiceProxy.getProxy().getOperation(businessFlowNode.getSystemId(), operationId);
		if(operation == null) {
			operation = DomainObjectProxy.getInstance().getOperation(businessFlowNode.getSystemId(), operationId);
			if(operation == null) {
				logger.error("T[{}] operation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "operation can't be null!");
			}
		}

		String objectType = operation.getObjectType();
		String objectId = operation.getObjectId();
		
		//根据配置获取输入参数
		JsonObject inParam = null;
		if(!StringUtils.isBlank(inParamInstanceSource)) {
			if(OBJECT_INSTANCE_SOURCE_I.equals(inParamInstanceSource)) {//以原始输入参数作为入参
				inParam = context.getReqData();
			}else if(OBJECT_INSTANCE_SOURCE_N.equals(inParamInstanceSource)) {//以预处理节点输出的对象作为入参
				inParam = (JsonObject)context.getAttribute(NODE_PARAM_KEY);
			}else if(OBJECT_INSTANCE_SOURCE_P.equals(inParamInstanceSource)) {//以过程数据中的对象作为入参
				if(inParamInstanceKey == null || inParamInstanceKey.trim().length() == 0) {
					logger.error("T[{}] when inParamInstanceSource's value is 'P', inParamInstanceKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when inParamInstanceSource's value is 'P', inParamInstanceKey can't be null.");
				}
				if(context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {//处理forEach循环的元素和index的情况
					if(ForEachNode.FOR_EACH_ELEMENT.equals(inParamInstanceKey) || ForEachNode.FOR_EACH_ROW_INDEX.equals(inParamInstanceKey)) {
						inParamInstanceKey = inParamInstanceKey + "_" + businessFlowNode.getBusinessFlowId();
					}
				}
				inParam = (JsonObject)context.getAttribute(inParamInstanceKey);		
			}else {//非法的配置信息
				logger.error("T[{}] invalid inParamInstanceSource: {}", businessFlowNode.getTenantId(), inParamInstanceSource);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid inParamInstanceSource. ");
			}
		}
		
		Object result = null;
		//进行目标方法调用
		if(ManagedObjectType.DOMAIN_OBJECT.equals(objectType)) {//调用领域对象的方法
			//如果instanceKey不为空时，获取领域对象
			AttributedObjectContainer domainObject = null;
			
			if(domainObject == null) {//如果为空，则初始化
				domainObject = new AttributedObjectContainer();
				domainObject.setAttributedObject(DomainObjectProxy.getInstance().getDomainObject(context.getSystemId(), objectId));
				domainObject.setObjectValues(new JsonObject());
			}
			
			DomainCommand command = new DomainCommand();
			command.setAttributedObject(domainObject);
			command.setDomainObjectId(objectId);
			command.setOperationId(operationId);
			command.setSystemId(context.getSystemId());
			command.setReqData(inParam);
			command.setTenantId(context.getTenantId());
			if(!MapUtils.isEmpty(context.getMultipartFileMap())) {
				command.setMultipartFileMap(context.getMultipartFileMap());
			}
			if(asyncInvoke) {//异步调用
				new Thread() {
					public void run() {
						try {
							logger.debug("T[{}] async invoke domain method {}.{}", businessFlowNode.getTenantId(), command.getDomainObjectId(), command.getOperationId());
							SecurityContextHolder.setAuthEnabled(false);
							DomainObjectProxy.getInstance().execute(command, null);
						} catch (EngineException e) {
							logger.error("T[{}] async invoke failed!", businessFlowNode.getTenantId(), e);
						}
					}
				}.start();
			}else {//同步调用
				result = DomainObjectProxy.getInstance().execute(command, null);
			}
		}else if(ManagedObjectType.SERVICE_OBJECT.equals(objectType)) {//调用服务对象的方法
			ServiceCommand command = new ServiceCommand();
			command.setServiceId(objectId);
			command.setOperationId(operationId);
			command.setSystemId(context.getSystemId());
			command.setReqData(inParam);
			command.setTenantId(context.getTenantId());
			if(!MapUtils.isEmpty(context.getMultipartFileMap())) {
				command.setMultipartFileMap(context.getMultipartFileMap());
			}
			
			if(asyncInvoke) {//异步调用
				new Thread() {
					public void run() {
						try {
							logger.debug("T[{}] async invoke service method {}.{}", businessFlowNode.getTenantId(), command.getServiceId(), command.getOperationId());
							SecurityContextHolder.setAuthEnabled(false);
							ServiceProxy.getProxy().execute(command, null);
						} catch (EngineException e) {
							logger.error("T[{}] async invoke failed!", businessFlowNode.getTenantId(), e);
						}
					}
				}.start();
			}else {//同步调用
				result = ServiceProxy.getProxy().execute(command, null);
			}
			
		}else {//非法的配置信息
			logger.error("T[{}] invalid invoke target objectType: {}", businessFlowNode.getTenantId(), objectType);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid invoke target objectType. ");
		}
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
