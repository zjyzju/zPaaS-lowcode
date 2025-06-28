package cn.zpaas.lowcode.be.engine.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.ResponseResult;
import cn.zpaas.lowcode.be.engine.ability.DataTransferAbility;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.ability.DynamicMappingAbility;
import cn.zpaas.lowcode.be.engine.ability.FileObjectTransferAbility;
import cn.zpaas.lowcode.be.engine.ability.FtpRepositoryAbility;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.be.engine.ability.MailSendAbility;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.be.engine.ability.PluggableAbilityMgr;
import cn.zpaas.lowcode.be.engine.ability.SecurityKeyMgrAbility;
import cn.zpaas.lowcode.be.engine.ability.SmsSendAbility;
import cn.zpaas.lowcode.be.engine.ability.ValidateAbility;
import cn.zpaas.lowcode.be.engine.auth.SecurityContextHolder;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.flow.BusinessFlowTemplate;
import cn.zpaas.lowcode.be.engine.flow.ComplexBusinessFlow;
import cn.zpaas.lowcode.be.engine.flow.QueryBusinessFlow;
import cn.zpaas.lowcode.be.engine.flow.SimpleBusinessFlow;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.FlowType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 业务流的解析执行引擎
 */
public class BusinessFlowProxy {
	private static Logger logger = LoggerFactory.getLogger(BusinessFlowProxy.class);
	
	//用于实现BusinessFlowProxy的单例模式
	private static BusinessFlowProxy instance = null;
	//用于缓存业务流的配置数据，包括业务流、业务流节点信息，第一层Key是systemId，第二层Key是businessFlowId
	private Map<String, Map<String,BusinessFlow>> businessFlowMap = new HashMap<>();
	//用于存放业务流实现模板对象，Key与be_business_flow表中的flow_type字段对应，
	//C：复杂类型，对应ComplexBusinessFlow；S：简单类型，对应SimpleBusinessFlow；Q：查询类型，对应QueryBusinessFlow
	private Map<String, BusinessFlowTemplate> businessFlowTemplateMap = new HashMap<>();
	
	/**
	 * 用于初始化BusinessFlowProxy，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		BusinessFlowProxy proxy = new BusinessFlowProxy();
		//加载业务流以及业务流节点的信息缓存到businessFlowMap中
		List<BusinessFlow> businessFlows = null;
		if(StringUtils.isBlank(systemId)) {
			businessFlows = initService.listBusinessFlows();
		}else {
			businessFlows = initService.listBusinessFlowBySystemId(systemId);
		}
		if(!CollectionUtils.isEmpty(businessFlows)) {
			//加载所有业务流节点
			List<BusinessFlowNode> allBusinessFlowNodes = null;
			if(!StringUtils.isBlank(systemId)) {
				allBusinessFlowNodes = initService.listBusinessFlowNodeBySystem(systemId);
			}else {
				allBusinessFlowNodes = initService.listBusinessFlowNodes();
			}
			if(allBusinessFlowNodes == null) {
				allBusinessFlowNodes = new ArrayList<>();
			}
			//按归属方法进行分组，形成Map
			Map<String, List<BusinessFlowNode>> businessFlowNodeMap = allBusinessFlowNodes.stream().collect(Collectors.groupingBy(BusinessFlowNode::getBusinessFlowId));
			
			//循环加载每一个业务流包含的业务流节点						
			for(BusinessFlow businessFlow: businessFlows) {
				businessFlow.setBusinessFlowNodes(businessFlowNodeMap.get(businessFlow.getId()));
				proxy.addBusinessFlow(businessFlow);
			}
		}else {
			logger.info("No invalid BusinessFlow is found!");
		}
		
		//调用业务流模板的初始化方法
		BusinessFlowTemplate.init(systemId, tenantId, initService);
		
		//初始化各类业务流模板实例，并缓存到businessFlowTemplateMap中
		QueryBusinessFlow queryBusinessFlow = new QueryBusinessFlow();
		SimpleBusinessFlow simpleBusinessFlow = new SimpleBusinessFlow();
		ComplexBusinessFlow complexBusinessFlow = new ComplexBusinessFlow();
		proxy.businessFlowTemplateMap.put(FlowType.QUERY_BUSINESS_FLOW, queryBusinessFlow);
		proxy.businessFlowTemplateMap.put(FlowType.SIMPLE_BUSINESS_FLOW, simpleBusinessFlow);
		proxy.businessFlowTemplateMap.put(FlowType.COMPLEX_BUSINESS_FLOW, complexBusinessFlow);
		
		//调用LocalFileAbility进行初始化
		try {
			LocalFileAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init LocalFileAbility failed!", e);
		}

		//调用DBSchemaProxy进行初始化
		try {
			DBSchemaProxy.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DBSchemaProxy failed!", e);
		}
		
		//调用ORMRepository进行初始化
		try {
			ORMRepositoryAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init ORMRepositoryAbility failed!", e);
		}
		//调用DataTransferAbility进行初始化
		try {
			DataTransferAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DataTransferAbility failed!", e);
		}
		//调用ValidateAbility进行初始化
		try {
			ValidateAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init ValidateAbility failed!", e);
		}
		//调用SqlManagementProxy进行初始化
		try {
			SqlManagementProxy.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SqlManagementProxy failed!", e);
		}
		//调用FtpRepositoryAbility进行初始化
		try {
			FtpRepositoryAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init FtpRepositoryAbility failed!", e);
		}
		//调用MailSendAbility进行初始化
		try {
			MailSendAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init MailSendAbility failed!", e);
		}
		//调用SmsSendAbility进行初始化
		try {
			SmsSendAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SmsSendAbility failed!", e);
		}

		//调用FileObjectTransferAbility进行初始化
		try {
			FileObjectTransferAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init FileObjectTransferAbility failed!", e);
		}

		//调用DynamicMappingAbility进行初始化
		try {
			DynamicMappingAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DynamicMappingAbility failed!", e);
		}

		//调用DictCacheAbility进行初始化
		try {
			DictCacheAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DictCacheAbility failed!", e);
		}

		//调用SecurityKeyMgrAbility进行初始化
		try {
			SecurityKeyMgrAbility.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SecurityKeyMgrAbility failed!", e);
		}

		//可插拔能力缓存加载
		try {
			PluggableAbilityMgr.init(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("PluggableAbilityCacheMgr init failed!", e);
		}
		
		//初始化完成，将BusinessFlowProxy实例赋值给静态属性
		instance = proxy;
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		//加载业务流以及业务流节点的信息缓存到businessFlowMap中
		List<BusinessFlow> businessFlows = initService.listBusinessFlowBySystemId(systemId);
		Map<String, BusinessFlow> map = new HashMap<>();
		if(!CollectionUtils.isEmpty(businessFlows)) {
			//加载所有业务流节点
			List<BusinessFlowNode> allBusinessFlowNodes = initService.listBusinessFlowNodeBySystem(systemId);
			if(allBusinessFlowNodes == null) {
				allBusinessFlowNodes = new ArrayList<>();
			}
			//按归属方法进行分组，形成Map
			Map<String, List<BusinessFlowNode>> businessFlowNodeMap = allBusinessFlowNodes.stream().collect(Collectors.groupingBy(BusinessFlowNode::getBusinessFlowId));
			
			//循环加载每一个业务流包含的业务流节点						
			for(BusinessFlow businessFlow: businessFlows) {
				businessFlow.setBusinessFlowNodes(businessFlowNodeMap.get(businessFlow.getId()));
				map.put(businessFlow.getId(), businessFlow);
			}
		}
		instance.businessFlowMap.put(systemId, map);
		
		//调用业务流模板的初始化方法
		//BusinessFlowTemplate.init(systemId, tenantId, initService);
		
		//调用LocalFileAbility进行初始化
		// try {
		// 	LocalFileAbility.init(systemId, tenantId, initService);
		// } catch (Exception e) {
		// 	logger.error("init LocalFileAbility failed!", e);
		// }

		//调用DBSchemaProxy进行初始化
		try {
			DBSchemaProxy.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DBSchemaProxy failed!", e);
		}
		
		//调用ORMRepository进行初始化
		try {
			ORMRepositoryAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init ORMRepositoryAbility failed!", e);
		}
		//调用DataTransferAbility进行初始化
		try {
			DataTransferAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DataTransferAbility failed!", e);
		}
		//调用ValidateAbility进行初始化
		try {
			ValidateAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init ValidateAbility failed!", e);
		}
		//调用SqlManagementProxy进行初始化
		try {
			SqlManagementProxy.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SqlManagementProxy failed!", e);
		}
		//调用FtpRepositoryAbility进行初始化
		try {
			FtpRepositoryAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init FtpRepositoryAbility failed!", e);
		}
		//调用MailSendAbility进行初始化
		try {
			MailSendAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init MailSendAbility failed!", e);
		}
		//调用SmsSendAbility进行初始化
		try {
			SmsSendAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SmsSendAbility failed!", e);
		}

		//调用FileObjectTransferAbility进行初始化
		try {
			FileObjectTransferAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init FileObjectTransferAbility failed!", e);
		}

		//调用DynamicMappingAbility进行初始化
		try {
			DynamicMappingAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DynamicMappingAbility failed!", e);
		}

		//调用DictCacheAbility进行初始化
		try {
			DictCacheAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init DictCacheAbility failed!", e);
		}

		//调用SecurityKeyMgrAbility进行初始化
		try {
			SecurityKeyMgrAbility.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("init SecurityKeyMgrAbility failed!", e);
		}

		//可插拔能力缓存加载
		try {
			PluggableAbilityMgr.reloadCache(systemId, tenantId, initService);
		} catch (Exception e) {
			logger.error("PluggableAbilityCacheMgr init failed!", e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 将BusinessFlow加入到businessFlowMap缓存中
	 * @param businessFlow 业务流实例
	 */
	private void addBusinessFlow(BusinessFlow businessFlow) {
		Map<String, BusinessFlow> map = businessFlowMap.get(businessFlow.getSystemId());
		if(map == null) {
			map = new HashMap<>();
			businessFlowMap.put(businessFlow.getSystemId(), map);
		}
		map.put(businessFlow.getId(), businessFlow);
	}
	
	/**
	 * 根据业务系统标识和业务流标识，从缓存中获取对应的业务流对象
	 * @param systemId 业务系统标识
	 * @param businessFlowId 业务流标识
	 * @return 返回业务流对象
	 */
	public BusinessFlow getBusinessFlow(String systemId, String businessFlowId) {
		Map<String, BusinessFlow> map = businessFlowMap.get(systemId);
		if(map != null) {
			return map.get(businessFlowId);
		}
		return null;
	}
	
	/**
	 * 用于获取BusinessFlowProxy实例，实现单例模式
	 * @return BusinessFlowProxy对象
	 */
	public static BusinessFlowProxy getInstance() {
		return instance;
	}
	
	/**
	 * 私有化构造函数	
	 */
	private BusinessFlowProxy() {
		
	}
	
	/**
	 * 业务流执行代理BusinessFlowProxy类的核心业务方法，用于解析执行业务流。
	 * @param command 业务流执行命令对象
	 * @return 返回业务流执行结果
	 */
	public Object execute(BusinessFlowCommand command, BusinessFlowContext context) throws EngineException {
		//获取对应的业务流对象
		BusinessFlow businessFlow = this.getBusinessFlow(command.getSystemId(), command.getBusinessFlowId());
		if(businessFlow == null) {
			logger.error("T[{}] Can't find invalid BusinessFlow: {}", command.getTenantId(), command);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Can't find invalid BusinessFlow");
		}
		
		if(StringUtils.isBlank(businessFlow.getFlowType())) {
			logger.error("T[{}] BusinessFlow's flowType is null.", command.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "BusinessFlow's flowType is null");
		}
		
		BusinessFlowTemplate businessFlowTemplate = this.businessFlowTemplateMap.get(businessFlow.getFlowType());
		if(businessFlowTemplate == null) {
			logger.error("T[{}] can't find invalid BusinessFlowTemplate: {}", command.getTenantId(), businessFlow.getFlowType());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find invalid BusinessFlowTemplate");
		}
		
		//如果业务流上下文为空，则初始化业务流下文对象
		if(context == null) {
			context = new BusinessFlowContext();
			context.setSystemId(command.getSystemId());
			context.setReqData(command.getReqData());
			context.setDbSchemaId(businessFlow.getDbSchemaId());
			context.setTenantId(command.getTenantId());
			BusinessFlowContext.setTransactionStarted(false);
			//增加登录用户信息到过程数据中
			if(SecurityContextHolder.isAuthEnabled()) {
				context.setAttribute(SecurityContextHolder.getUserInfoKey(), SecurityContextHolder.getUserInfo());
			}
		}
		//	如果command中的attributedObject不为空，表示调用的是领域对象的方法，设置领域对象到context中
		if(command.getAttributedObject() != null) {
			context.setDomainObject(command.getAttributedObject());
		}
		//如果有上传文件信息，则将文件保存中过程数据中
		if(!MapUtils.isEmpty(command.getMultipartFileMap())) {
			context.setMultipartFileMap(command.getMultipartFileMap());
		}
		//如果有服务端事件发送对象
		if(command.getSseEmitter() != null) {
			context.setSseEmitter(command.getSseEmitter());
		}
		
		BusinessFlowContext businessFlowContext = context;
		
		//如果业务需要开启事务，且事务还未开启时，开启事务，调用业务流模板的execute方法
		logger.debug("T[{}] businessFlow:{}-{}, isTransactionStarted:{}, transactionRequired:{}", command.getTenantId(), businessFlow.getId(), businessFlow.getName(), BusinessFlowContext.isTransactionStarted(), businessFlow.getTransactionRequired());
		
		if(!BusinessFlowContext.isTransactionStarted() && YesOrNo.YES.equals(businessFlow.getTransactionRequired())) {
			BusinessFlowContext.setTransactionStarted(true);
			//获取当前业务流对应的事务管理器
			TransactionTemplate transactionTemplate = DBSchemaProxy.getTransactionTemplate(command.getSystemId(), businessFlow.getDbSchemaId());
			
			ResponseResult<Object> ret =  transactionTemplate.execute(new TransactionCallback<ResponseResult<Object>>() {
				@Override
				public ResponseResult<Object> doInTransaction(TransactionStatus status) {
					//当发生异常时，使用Result对象往外传递错误信息
					ResponseResult<Object> result = new ResponseResult<>();
					try {
						Object resultData = businessFlowTemplate.execute(businessFlow, businessFlowContext);					
						result.setStatus(ResultStatus.SUCCEED);
						result.setData(resultData);
						return result;
					} catch (EngineException e) {
						logger.error("T[{}] BusinessFlow execute failed: status:{}, message:{}, detailMessage:{}", command.getTenantId(), e.getStatus(),e.getMessage(),e.getDetailMessage());
						result.setStatus(e.getStatus());
						result.setMessage(e.getMessage());
						result.setDetailMessage(e.getDetailMessage());
						status.setRollbackOnly();
						return result;
					}catch (Exception e) {
						logger.error("T[{}] BusinessFlow execute failed!", command.getTenantId(), e);
						result.setStatus(ResultStatus.INTERNAL_ERROR);
						result.setMessage(e.toString());
						status.setRollbackOnly();
						return result;
					}
				}
			});
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] businessFlow is finished. {}-{}", command.getTenantId(), businessFlow.getId(), businessFlow.getName());
			}
			
			if(ret == null || !ResultStatus.SUCCEED.equals(ret.getStatus())) {
				if(ret == null) {
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "return result is null!", null);
				}else  {
					throw new EngineException(ret.getStatus(), ret.getMessage(), ret.getDetailMessage());
				}
			}else {
				return ret.getData();
			}
		}else {			
			Object result = businessFlowTemplate.execute(businessFlow, businessFlowContext);
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] businessFlow is finished. {}-{}", command.getTenantId(), businessFlow.getId(), businessFlow.getName());
			}
			return result;
		}
	}
}
