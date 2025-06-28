package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.constant.FlowType;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ManagedObject;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 方法对象领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:33:37
 */
@Service
public class OperationService {
    private static Logger logger = LoggerFactory.getLogger(OperationService.class);

    public static final String ADD = "add";
	public static final String ADD_CN = "新增";
	public static final String NEW = "new";
	public static final String RETURN = "return";
	public static final String RETURN_CN = "返回值";
	public static final String BUSINESS_FLOW_NAME_POSTFIX = "方法的主业务流";
	public static final String MODIFY = "modify";
	public static final String MODIFY_CN = "修改";
	public static final String BY_ID = "ById";
	public static final String BY_ID_CN = "根据主键";
	public static final String INTEGER_CLASS = "java.lang.Integer";
	public static final String DELETE = "delete";
	public static final String DELETE_CN = "删除";
	public static final String QUERY = "query";
	public static final String QUERY_CN = "查询";
	public static final String CONDITION = "condition";
	public static final String BY_CONDITION = "ByCondition";
	public static final String BY_CONDITION_CN = "根据条件";

    @Autowired
    private Operation operationSV;//方法领域对象

    @Autowired
    private Parameter parameterSV;//方法参数领域对象

    @Autowired
    private BusinessFlow businessFlowSV;//方法业务流领域对象

    @Autowired
    private BusinessFlowGenService businessFlowService;//方法业务流领域服务

    /**
     * 生成领域对象默认的增删改查方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
	public boolean generateOperations(ManagedObject domainObject, String dbSchemaId, String ormId)
			throws EngineException {
		if (logger.isDebugEnabled()) {
			logger.debug("start generateOperations...");
		}
		// 调用生成新增方法
		if (this.generateAddOperation(domainObject, dbSchemaId, ormId) == null) {
			logger.error("generate AddOperation record failed!");
			return false;
		}
		// 调用生成根据主键修改方法
		if (this.generateModifyByIdOperation(domainObject, dbSchemaId, ormId) == null) {
			logger.error("generate ModifyByIdOperation record failed!");
			return false;
		}
		// 调用生成根据主键删除方法
		if (this.generateDeleteByIdOperation(domainObject, dbSchemaId, ormId) == null) {
			logger.error("generate DeleteByIdOperation record failed!");
			return false;
		}
		// 调用生成根据主键查询方法
		if (this.generateQueryByIdOperation(domainObject, dbSchemaId, ormId) == null) {
			logger.error("generate QueryByIdOperation record failed!");
			return false;
		}
		// 调用生成根据条件查询列表方法
		if (this.generateQueryByConditionOperation(domainObject, dbSchemaId, ormId) == null) {
			logger.error("generate QueryByConditionOperation record failed!");
			return false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("end generateOperations...");
		}
		return true;
	}
    
    /**
     * 生成新增方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
    public Operation generateAddOperation(ManagedObject domainObject, String dbSchemaId, String ormId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateAddOperation...");
    	}
    	//构建Operation对象实例
    	Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
    	newOperation.setObjectId(domainObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(domainObject.getTenantId());
    	newOperation.setSystemId(domainObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(ADD + domainObject.getCode());
    	newOperation.setName(ADD_CN + domainObject.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}

    	//构建入参Parameter对象实例
    	Parameter newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(NEW + domainObject.getCode());
    	newParameter.setName(newParameter.getCode());
    	newParameter.setIsInParam(YesOrNo.YES);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(1);
    	newParameter.setOperationId(newOperation.getId());
    	//调用入参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate inParam record failed!");
    		return null;
    	}
		List<Parameter> inParams = new ArrayList<>();
		inParams.add(newParameter);
		newOperation.setInParams(inParams);
		
		//构建出参Parameter对象实例
		newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(RETURN);
    	newParameter.setName(RETURN_CN);
    	newParameter.setIsInParam(YesOrNo.NO);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(2);
    	newParameter.setOperationId(newOperation.getId());
    	
    	//调用出参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate outParam record failed!");
    		return null;
    	}
		newOperation.setOutParam(newParameter);
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(YesOrNo.YES);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(domainObject.getTenantId());
    	newBusinessFlow.setSystemId(domainObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateAddOperationBusinessFlowNodes(newBusinessFlow, domainObject, ormId) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateAddOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 生成根据主键修改方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
    public Operation generateModifyByIdOperation(ManagedObject domainObject, String dbSchemaId, String ormId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateModifyByIdOperation...");
    	}
    	//构建Operation对象实例
		Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
    	newOperation.setObjectId(domainObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(domainObject.getTenantId());
    	newOperation.setSystemId(domainObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(MODIFY + domainObject.getCode() + BY_ID);
    	newOperation.setName(BY_ID_CN + MODIFY_CN + domainObject.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}

    	//构建入参Parameter对象实例
    	Parameter newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(MODIFY + domainObject.getCode());
    	newParameter.setName(newParameter.getCode());
    	newParameter.setIsInParam(YesOrNo.YES);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(1);
    	newParameter.setOperationId(newOperation.getId());
    	//调用入参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate inParam record failed!");
    		return null;
    	}
		List<Parameter> inParams = new ArrayList<>();
		inParams.add(newParameter);
		newOperation.setInParams(inParams);
		
		//构建出参Parameter对象实例
		newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(RETURN);
    	newParameter.setName(RETURN_CN);
    	newParameter.setIsInParam(YesOrNo.NO);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.JAVA_OBJECT);
    	newParameter.setParamClass(INTEGER_CLASS);
    	newParameter.setDisplayOrder(2);
    	newParameter.setOperationId(newOperation.getId());
    	
    	//调用出参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate outParam record failed!");
    		return null;
    	}
		newOperation.setOutParam(newParameter);
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(YesOrNo.YES);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(domainObject.getTenantId());
    	newBusinessFlow.setSystemId(domainObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateModifyByIdOperationBusinessFlowNodes(newBusinessFlow, domainObject, ormId) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateModifyByIdOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 生成根据主键删除方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
    public Operation generateDeleteByIdOperation(ManagedObject domainObject, String dbSchemaId, String ormId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateModifyByIdOperation...");
    	}
    	//构建Operation对象实例
    	Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
    	newOperation.setObjectId(domainObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(domainObject.getTenantId());
    	newOperation.setSystemId(domainObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(DELETE + domainObject.getCode() + BY_ID);
    	newOperation.setName(BY_ID_CN + DELETE_CN + domainObject.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}

    	//构建入参Parameter对象实例
    	Parameter newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(DELETE + domainObject.getCode());
    	newParameter.setName(newParameter.getCode());
    	newParameter.setIsInParam(YesOrNo.YES);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(1);
    	newParameter.setOperationId(newOperation.getId());
    	//调用入参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate inParam record failed!");
    		return null;
    	}
		List<Parameter> inParams = new ArrayList<>();
		inParams.add(newParameter);
		newOperation.setInParams(inParams);
		
		//构建出参Parameter对象实例
		newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(RETURN);
    	newParameter.setName(RETURN_CN);
    	newParameter.setIsInParam(YesOrNo.NO);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.JAVA_OBJECT);
    	newParameter.setParamClass(INTEGER_CLASS);
    	newParameter.setDisplayOrder(2);
    	newParameter.setOperationId(newOperation.getId());
    	//调用出参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate outParam record failed!");
    		return null;
    	}
		newOperation.setOutParam(newParameter);
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(YesOrNo.YES);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(domainObject.getTenantId());
    	newBusinessFlow.setSystemId(domainObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateDeleteByIdOperationBusinessFlowNodes(newBusinessFlow, domainObject, ormId) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateDeleteByIdOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 生成根据主键查询方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
    public Operation generateQueryByIdOperation(ManagedObject domainObject, String dbSchemaId, String ormId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateQueryByIdOperation...");
    	}
    	//构建Operation对象实例
    	Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
    	newOperation.setObjectId(domainObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(domainObject.getTenantId());
    	newOperation.setSystemId(domainObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(QUERY + domainObject.getCode() + BY_ID);
    	newOperation.setName(BY_ID_CN + QUERY_CN + domainObject.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}

    	//构建入参Parameter对象实例
    	Parameter newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(CONDITION + domainObject.getCode());
    	newParameter.setName(newParameter.getCode());
    	newParameter.setIsInParam(YesOrNo.YES);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(1);
    	newParameter.setOperationId(newOperation.getId());
    	//调用入参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate inParam record failed!");
    		return null;
    	}
		List<Parameter> inParams = new ArrayList<>();
		inParams.add(newParameter);
		newOperation.setInParams(inParams);
		
		//构建出参Parameter对象实例
		newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(RETURN);
    	newParameter.setName(RETURN_CN);
    	newParameter.setIsInParam(YesOrNo.NO);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(2);
    	newParameter.setOperationId(newOperation.getId());
    	//调用出参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate outParam record failed!");
    		return null;
    	}
		newOperation.setOutParam(newParameter);
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(YesOrNo.YES);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(domainObject.getTenantId());
    	newBusinessFlow.setSystemId(domainObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateQueryByIdOperationBusinessFlowNodes(newBusinessFlow, domainObject, ormId) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateQueryByIdOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 生成根据条件查询方法
     * @param domainObject
     * @param dbSchemaId
     * @param ormId
     * @return
     * @throws EngineException
     */
    public Operation generateQueryByConditionOperation(ManagedObject domainObject, String dbSchemaId, String ormId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateQueryByConditionOperation...");
    	}
    	//构建Operation对象实例
    	Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
    	newOperation.setObjectId(domainObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(domainObject.getTenantId());
    	newOperation.setSystemId(domainObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(QUERY + domainObject.getCode() + BY_CONDITION);
    	newOperation.setName(BY_CONDITION_CN + QUERY_CN + domainObject.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}

    	//构建入参Parameter对象实例
    	Parameter newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(CONDITION + domainObject.getCode());
    	newParameter.setName(newParameter.getCode());
    	newParameter.setIsInParam(YesOrNo.YES);
    	newParameter.setIsListParam(YesOrNo.NO);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(1);
    	newParameter.setOperationId(newOperation.getId());
    	//调用入参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate inParam record failed!");
    		return null;
    	}
		List<Parameter> inParams = new ArrayList<>();
		inParams.add(newParameter);
		newOperation.setInParams(inParams);
		
		//构建出参Parameter对象实例
		newParameter = new Parameter();
    	newParameter.setTenantId(domainObject.getTenantId());
    	newParameter.setSystemId(domainObject.getSystemId());
    	newParameter.setCreateTime(nowDate);
    	newParameter.setUpdateTime(nowDate);
    	newParameter.setId(null);
    	newParameter.setCode(RETURN);
    	newParameter.setName(RETURN_CN);
    	newParameter.setIsInParam(YesOrNo.NO);
    	newParameter.setIsListParam(YesOrNo.YES);
    	newParameter.setParamType(ManagedObjectType.DOMAIN_OBJECT);
    	newParameter.setParamClass(domainObject.getId());
    	newParameter.setDisplayOrder(2);
    	newParameter.setOperationId(newOperation.getId());
    	//调用出参Parameter的插入方法
		if(parameterSV.save(newParameter) <= 0) {
    		logger.error("generate outParam record failed!");
    		return null;
    	}
		newOperation.setOutParam(newParameter);
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(YesOrNo.YES);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(domainObject.getTenantId());
    	newBusinessFlow.setSystemId(domainObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateQueryByConditionOperationBusinessFlowNodes(newBusinessFlow, domainObject, ormId) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateQueryByConditionOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 生成本地方法调用方法
     * @param serviceObject
     * @param dbSchemaId
     * @param targetObject
     * @param targetOperation
     * @param transactionRequired
     * @return
     * @throws EngineException
     */
    public Operation generateLocalInvokeOperation(ManagedObject serviceObject, String dbSchemaId, DomainObject targetObject, Operation targetOperation, String transactionRequired) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateLocalInvokeOperation...");
    	}
    	//构建Operation对象实例
    	Operation newOperation = new Operation();
    	newOperation.setObjectType(ManagedObjectType.SERVICE_OBJECT);
    	newOperation.setObjectId(serviceObject.getId());
    	newOperation.setStatus(Status.EFF);
    	newOperation.setTenantId(serviceObject.getTenantId());
    	newOperation.setSystemId(serviceObject.getSystemId());
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setCode(targetOperation.getCode());
    	newOperation.setName(targetOperation.getName());
    	newOperation.setId(null);
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		return null;
    	}
    	
    	String paramName = null;
    	//构建入参Parameter对象实例
    	if(targetOperation.getInParams() != null && targetOperation.getInParams().size() > 0) {
    		Parameter targetParameter = targetOperation.getInParams().get(0);
    		paramName = targetParameter.getCode();
    		Parameter newParameter = new Parameter();
        	newParameter.setTenantId(serviceObject.getTenantId());
        	newParameter.setSystemId(serviceObject.getSystemId());
        	newParameter.setCreateTime(nowDate);
        	newParameter.setUpdateTime(nowDate);
        	newParameter.setId(null);
        	newParameter.setCode(targetParameter.getCode());
        	newParameter.setName(targetParameter.getName());
        	newParameter.setIsInParam(YesOrNo.YES);
        	newParameter.setIsListParam(targetParameter.getIsListParam());
        	newParameter.setParamType(targetParameter.getParamType());
        	newParameter.setParamClass(targetParameter.getParamClass());
        	newParameter.setDisplayOrder(1);
        	newParameter.setOperationId(newOperation.getId());
        	//调用入参Parameter的插入方法
    		if(parameterSV.save(newParameter) <= 0) {
        		logger.error("generate inParam record failed!");
        		return null;
        	}
    	}
    	
		
		//构建出参Parameter对象实例
    	
    	if(targetOperation.getOutParam() != null) {
    		Parameter targetParameter = targetOperation.getOutParam();
    		Parameter newParameter = new Parameter();
        	newParameter.setTenantId(serviceObject.getTenantId());
        	newParameter.setSystemId(serviceObject.getSystemId());
        	newParameter.setCreateTime(nowDate);
        	newParameter.setUpdateTime(nowDate);
        	newParameter.setId(null);
        	newParameter.setCode(targetParameter.getCode());
        	newParameter.setName(targetParameter.getName());
        	newParameter.setIsInParam(YesOrNo.NO);
        	newParameter.setIsListParam(targetParameter.getIsListParam());
        	newParameter.setParamType(targetParameter.getParamType());
        	newParameter.setParamClass(targetParameter.getParamClass());
        	newParameter.setDisplayOrder(2);
        	newParameter.setOperationId(newOperation.getId());
        	//调用出参Parameter的插入方法
    		if(parameterSV.save(newParameter) <= 0) {
        		logger.error("generate outParam record failed!");
        		return null;
        	}
    	}
    	
		
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = new BusinessFlow();
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setName(newOperation.getCode()+BUSINESS_FLOW_NAME_POSTFIX);
    	newBusinessFlow.setFlowType(FlowType.COMPLEX_BUSINESS_FLOW);
    	newBusinessFlow.setDbSchemaId(dbSchemaId);
    	newBusinessFlow.setTransactionRequired(transactionRequired);
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setTenantId(serviceObject.getTenantId());
    	newBusinessFlow.setSystemId(serviceObject.getSystemId());
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		return null;
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateLocalInvokeBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateBusinessFlowBpmnFile record failed!");
			return null;
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateLocalInvokeOperationBusinessFlowNodes(newBusinessFlow, serviceObject, targetObject, targetOperation, paramName) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		return null;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateLocalInvokeOperation...");
    	}   	
    	return newOperation;
    }
    
    /**
     * 根据领域对象方法生成服务方法
     * @param serviceObjectId
     * @param domainObject
     * @param domainOperationId
     * @return
     * @throws EngineException
     */
    public Operation generateServiceOperationByDomainOperation(String serviceObjectId, DomainObject domainObject, String domainOperationId) throws EngineException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("start generateServiceOperationByDomainOperation...");
    	}
    	
    	if(serviceObjectId == null || serviceObjectId.trim().length() ==0 ||
    			domainObject == null || domainObject.getId() == null || domainObject.getId().trim().length() == 0 ||
    			domainOperationId == null || domainOperationId.trim().length() == 0) {
    		logger.error("serviceObjectId domainObjectId and domainOperationId can't be null!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "serviceObjectId domainObjectId and domainOperationId can't be null!");
    	}
    	
    	//构建Operation对象实例
    	Operation newOperation = operationSV.byId(domainOperationId);
    	if(newOperation == null) {
    		logger.error("invalid domainOperationId!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid domainOperationId!");
    	}
    	newOperation.setObjectType(ManagedObjectType.SERVICE_OBJECT);
    	newOperation.setObjectId(serviceObjectId);
    	newOperation.setStatus(Status.EFF);
    	Date nowDate = new Date();
    	newOperation.setCreateTime(nowDate);
    	newOperation.setUpdateTime(nowDate);
    	newOperation.setId(null);
    	String oldBusinessFlowId = newOperation.getBusinessFlowId();
    	newOperation.setBusinessFlowId(null);
    	//调用插入方法
    	if(operationSV.save(newOperation) <=0) {
    		logger.error("generate operation record failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate operation record failed!");
    	}

    	//构建入参Parameter对象实例
    	List<Parameter> parameters = parameterSV.listByOperationId(domainOperationId);
    	if(parameters != null && !parameters.isEmpty()) {
    		for(Parameter newParameter : parameters) {
    			newParameter.setCreateTime(nowDate);
    	    	newParameter.setUpdateTime(nowDate);
    	    	newParameter.setId(null);
    	    	newParameter.setOperationId(newOperation.getId());
    	    	//调用入参Parameter的插入方法
    			if(parameterSV.save(newParameter) <= 0) {
    	    		logger.error("generate Param record failed!");
    	    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate Param record failed!");
    	    	}
    		}
    	}
    	
		//构建业务流BusinessFlow对象实例
		BusinessFlow newBusinessFlow = businessFlowSV.byId(oldBusinessFlowId);
    	newBusinessFlow.setId(newOperation.getBusinessFlowId());
    	newBusinessFlow.setParentFlowId(null);
    	newBusinessFlow.setCreateTime(nowDate);
    	newBusinessFlow.setUpdateTime(nowDate);
    	//调用业务流BusinessFlow的插入方法
		if(businessFlowSV.save(newBusinessFlow) <= 0) {
    		logger.error("generate businessFlow record failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate businessFlow record failed!");
    	}
		
		// 调用业务流BusinessFlow的generateBusinessFlowBpmnFile方法
		if (businessFlowService.generateLocalInvokeBusinessFlowBpmnFile(newBusinessFlow) <= 0) {
			logger.error("generate generateLocalInvokeBusinessFlowBpmnFile record failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate generateLocalInvokeBusinessFlowBpmnFile record failed!");
		}

		//调用BusinessFlow的业务流节点生成方法
		if(businessFlowService.generateLocalInvokeOperationBusinessFlowNodes(newBusinessFlow, domainObject, domainOperationId, parameters) == null) {
    		logger.error("generate BusinessFlowNodes record failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate BusinessFlowNodes record failed!");
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("end generateServiceOperationByDomainOperation...");
    	}   	
    	return newOperation;
    }
}
