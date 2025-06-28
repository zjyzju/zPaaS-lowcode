package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.CaseFormat;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.ExposedServiceMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class ExposedService {
	private static Logger logger = LoggerFactory.getLogger(ExposedService.class);
	
	public static final String HTTP_METHOD_POST = "POST";
	public static final String SLASH = "/";
	
    private String id;

    private String serviceId;

    private String operationId;

    private String httpMethod;

    private String httpMapping;

    private String status;

    private String domainId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private ServiceObject serviceObject;
    
    private Operation operation;
    
    @Autowired
    private ExposedServiceMapper mapper;
    
    @Autowired
    private ServiceObject serviceObjectSV;
    
    @Autowired
    private Operation operationSV;
    
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
    	
    	if(this.save(exposedService) == null) {
    		logger.error("expose service operation failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "expose service operation failed!");
    	}else {
    		return exposedService;
    	}
    }
    
    
    
    public ExposedService save(ExposedService exposedService) {
    	if(exposedService.getId() == null || exposedService.getId().trim().length() == 0) {
    		exposedService.setId(KeyGenerate.uuidKey());
    	}
    	if(mapper.insert(exposedService) > 0) {
    		return exposedService;
    	}else {
    		return null;
    	}
    }
    
    public int deleteById(String id) {
    	return mapper.deleteByPrimaryKey(id);
    }
    
    public int deleteByService(String serviceId) {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF).andServiceIdEqualTo(serviceId);
    	
    	ExposedService exposedService = new ExposedService();
    	exposedService.setStatus(Status.EXP);
    	exposedService.setUpdateTime(new Date());
    	return mapper.updateByExampleSelective(exposedService, criteria);
    }
    
    public ExposedService byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<ExposedService> list() {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ExposedService> listBySystemId(String businessSystemId) {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andSystemIdEqualTo(businessSystemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ExposedService> listByServiceId(String businessSystemId, String serviceId) {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andSystemIdEqualTo(businessSystemId).andServiceIdEqualTo(serviceId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public ExposedService listByServiceAndOperation(String serviceId, String operationId) {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andOperationIdEqualTo(operationId).andServiceIdEqualTo(serviceId).andStatusEqualTo(Status.EFF);
    	List<ExposedService> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public List<ExposedService> listAllBySystem(String businessSystemId) {
    	ExposedServiceExample criteria = new ExposedServiceExample();
    	criteria.createCriteria().andSystemIdEqualTo(businessSystemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("service_id, operation_id");
    	List<ExposedService> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		List<ServiceObject> serviceObjects = serviceObjectSV.listApplicationServiceBySystem(businessSystemId);
    		Map<String, ServiceObject> serviceObjectMap = new HashMap<>();
    		if(serviceObjects != null && !serviceObjects.isEmpty()) {
    			serviceObjectMap = serviceObjects.stream().collect(Collectors.toMap(ServiceObject::getId, t->t));
    		}
    		
    		List<Operation> operations = operationSV.listByObjectTypeAndSystem(ManagedObjectType.SERVICE_OBJECT, businessSystemId);
    		Map<String, Operation> operationMap = new HashMap<>();
    		if(operations != null && !operations.isEmpty()) {
    			operationMap = operations.stream().collect(Collectors.toMap(Operation::getId, t->t));
    		}
    		
    		for(ExposedService exposedService : list) {
    			exposedService.setServiceObject(serviceObjectMap.get(exposedService.getServiceId()));
    			exposedService.setOperation(operationMap.get(exposedService.getOperationId()));
    		}
    	}
    	
    	return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId == null ? null : serviceId.trim();
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId == null ? null : operationId.trim();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod == null ? null : httpMethod.trim();
    }

    public String getHttpMapping() {
        return httpMapping;
    }

    public void setHttpMapping(String httpMapping) {
        this.httpMapping = httpMapping == null ? null : httpMapping.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId == null ? null : domainId.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public ServiceObject getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(ServiceObject serviceObject) {
		this.serviceObject = serviceObject;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
}