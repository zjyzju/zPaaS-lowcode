package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
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
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 业务流本地方法调用节点的实现类，主要实现本地方法的调用，包括
 * 		调用服务方法（应用服务、技术服务、第三方服务和领域服务）
 * 		和领域对象方法（聚合根对象、实体对象和值对象）。
 */
public class LocalInvokeNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(LocalInvokeNode.class);

	public static final String CFG_OBJECT_TYPE_KEY = "objectType"; //调用对象类型存放的Key
	public static final String CFG_OBJECT_SUB_TYPE_KEY = "objectSubType"; //调用对象子类存放的Key
	public static final String CFG_OBJECT_ID_KEY = "objectId"; //调用对象标识存放的Key
	public static final String CFG_INSTANCE_KEY_KEY = "instanceKey"; //调用对象实例的Key值存放的Key
	public static final String CFG_OPERATION_ID_KEY = "operationId"; //调用方法标识存放的Key
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
		String objectType = JsonUtils.getString(nodeCfg, CFG_OBJECT_TYPE_KEY);
		String objectId = JsonUtils.getString(nodeCfg, CFG_OBJECT_ID_KEY);
		String instanceKey = JsonUtils.getString(nodeCfg, CFG_INSTANCE_KEY_KEY);
		String operationId = JsonUtils.getString(nodeCfg, CFG_OPERATION_ID_KEY);
		boolean asyncInvoke = JsonUtils.getBoolean(nodeCfg, CFG_ASYNC_INVOKE_KEY);
		String inParamInstanceSource = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_SOURCE_KEY);
		String inParamInstanceKey = JsonUtils.getString(nodeCfg, CFG_IN_PARAM_INSTANCE_KEY_KEY);
		
		//调用目标方法相关的信息不能为空
		if(StringUtils.isBlank(objectType) || StringUtils.isBlank(objectId) || StringUtils.isBlank(operationId)) {
			logger.error("T[{}] objectType objectId and operationId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "objectType objectId and operationId  can't be null. ");
		}
		
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
			if(!StringUtils.isBlank(instanceKey)) {
				domainObject = context.getAttributedObject(instanceKey);
				if(domainObject == null) {
					logger.error("T[{}] target domainObject({}) is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), instanceKey, businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "target domainObject is null. ");
				}
			}
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
							logger.info("T[{}] async invoke domain method {}.{}", businessFlowNode.getTenantId(), DomainObjectProxy.getInstance().getDomainObject(command.getSystemId(), command.getDomainObjectId()).getCode(), DomainObjectProxy.getInstance().getOperation(command.getSystemId(), command.getOperationId()).getCode());
							SecurityContextHolder.setAuthEnabled(false);
							DomainObjectProxy.getInstance().execute(command, null);
						} catch (EngineException e) {
							logger.error("T[{}] async invoke failed!", businessFlowNode.getTenantId(), e);
						}
					}
				}.start();
			}else {//同步调用
				logger.info("T[{}] invoke domain method {}.{}", businessFlowNode.getTenantId(), DomainObjectProxy.getInstance().getDomainObject(command.getSystemId(), command.getDomainObjectId()).getCode(), DomainObjectProxy.getInstance().getOperation(command.getSystemId(), command.getOperationId()).getCode());
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
							logger.info("T[{}] async invoke service method {}.{}", businessFlowNode.getTenantId(), ServiceProxy.getProxy().getServiceObject(command.getSystemId(), command.getServiceId()).getCode(), ServiceProxy.getProxy().getOperation(command.getSystemId(), command.getOperationId()).getCode());
							SecurityContextHolder.setAuthEnabled(false);
							ServiceProxy.getProxy().execute(command, null);
						} catch (EngineException e) {
							logger.error("T[{}] async invoke failed!", businessFlowNode.getTenantId(), e);
						}
					}
				}.start();
			}else {//同步调用
				logger.info("T[{}] invoke service method {}.{}", businessFlowNode.getTenantId(), ServiceProxy.getProxy().getServiceObject(command.getSystemId(), command.getServiceId()).getCode(), ServiceProxy.getProxy().getOperation(command.getSystemId(), command.getOperationId()).getCode());
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
