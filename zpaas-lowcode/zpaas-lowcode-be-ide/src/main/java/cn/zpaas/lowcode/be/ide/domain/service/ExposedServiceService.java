package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.DomainOperationExpose;

/**
 * 暴露服务领域方法
 *
 * @author zjy
 * createTime 2025年04月28日-11:23:52
 */
@Service
public class ExposedServiceService {
    private static Logger logger = LoggerFactory.getLogger(ExposedServiceService.class);

    public static final String HTTP_METHOD_POST = "POST";
	public static final String SLASH = "/";

    @Autowired
    private ExposedService exposedServiceSV; //暴露服务领域对象

    @Autowired
    private OperationService operationService;//方法领域服务

    /**
     * 对外暴露服务方法
     * @param targetObject
     * @param targetOperation
     * @return
     * @throws EngineException
     */
    public ExposedService exposeServiceOperation(ServiceObject serviceObject, Operation targetOperation, DomainObject targetDomainObject) throws EngineException{
    	ExposedService exposedService = new ExposedService();
    	exposedService.setDomainId(serviceObject.getDomainId());
    	exposedService.setHttpMethod(HTTP_METHOD_POST);
    	exposedService.setHttpMapping(SLASH + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, targetDomainObject.getCode()) + SLASH + targetOperation.getCode());
    	exposedService.setOperationId(targetOperation.getId());
    	exposedService.setServiceId(serviceObject.getId());
    	exposedService.setStatus(Status.EFF);
    	exposedService.setSystemId(serviceObject.getSystemId());
    	exposedService.setTenantId(serviceObject.getTenantId());
    	Date nowDate = new Date();
    	exposedService.setCreateTime(nowDate);
    	exposedService.setUpdateTime(nowDate);
    	
    	if(exposedServiceSV.save(exposedService) == null) {
    		logger.error("expose service operation failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "expose service operation failed!");
    	}else {
    		return exposedService;
    	}
    }
    
    /**
     * 领域对象方法一键对外暴露
     * @param serviceObject
     * @param domainObject
     * @param domainOperationExport
     * @return
     * @throws EngineException
     */
    public ExposedService exposeDomainOperation(ServiceObject serviceObject, DomainObject domainObject, DomainOperationExpose domainOperationExpose) throws EngineException{
    	//根据领域对象方法自动生成服务方法
    	Operation serviceOperation = this.operationService.generateServiceOperationByDomainOperation(serviceObject.getId(), domainObject, domainOperationExpose.getOperationId());
    	
    	//暴露服务方法
    	ExposedService exposedService = new ExposedService();
    	exposedService.setDomainId(serviceObject.getDomainId());
    	exposedService.setHttpMethod(domainOperationExpose.getHttpMethod());
    	exposedService.setHttpMapping(domainOperationExpose.getHttpMapping());
    	exposedService.setOperationId(serviceOperation.getId());
    	exposedService.setServiceId(serviceObject.getId());
    	exposedService.setStatus(Status.EFF);
    	exposedService.setSystemId(serviceObject.getSystemId());
    	exposedService.setTenantId(serviceObject.getTenantId());
    	Date nowDate = new Date();
    	exposedService.setCreateTime(nowDate);
    	exposedService.setUpdateTime(nowDate);
    	
    	if(exposedServiceSV.save(exposedService) == null) {
    		logger.error("expose service operation failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "expose service operation failed!");
    	}else {
    		return exposedService;
    	}
    }
}
