package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.ServiceObjectMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class ServiceObject extends ManagedObject {
	private static Logger logger = LoggerFactory.getLogger(ServiceObject.class);
	
	public static final String SERVICE = "Service";//服务名后缀
	
    private List<Operation> operations;
    
    @Autowired
    private ServiceObjectMapper mapper;
    
    /**
     * 根据领域对象生成应用服务对象
     * @param domainObject
     * @return
     * @throws EngineException
     */
    public ServiceObject generateSeviceObjectByDomainObject(DomainObject domainObject) throws EngineException {
    	String serviceCode = domainObject.getCode() + ServiceObject.SERVICE;
		ServiceObject newObject = new ServiceObject();
    	newObject.setCode(serviceCode);
    	newObject.setName(serviceCode);
		newObject.setDomainId(domainObject.getDomainId());
		newObject.setDescription("auto generated");
		newObject.setSystemId(domainObject.getSystemId());
		newObject.setTenantId(domainObject.getTenantId());
		Date nowDate = new Date();
		newObject.setCreateTime(nowDate);
		newObject.setUpdateTime(nowDate);
		newObject.setStatus(Status.EFF);
		
		if(this.save(newObject) == null) {
			logger.error("generate service object failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate service object failed!");
		}
		
		return newObject;
    }
    
    public ServiceObject save(ServiceObject serviceObject) {
    	if(serviceObject.getId() == null || serviceObject.getId().trim().length() == 0) {
    		serviceObject.setId(KeyGenerate.uuidKey());
    	}
    	if(mapper.insert(serviceObject) > 0) {
    		return serviceObject;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(ServiceObject serviceObject) {
    	if(serviceObject.getId() == null || serviceObject.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(serviceObject);
    }
    
    public ServiceObject byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public ServiceObject queryByDomainAndCode(String domainId, String code) {
    	ServiceObjectExample criteria = new ServiceObjectExample();
    	criteria.createCriteria().andDomainIdEqualTo(domainId).andCodeEqualTo(code).andStatusEqualTo(Status.EFF);
    	List<ServiceObject> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public List<ServiceObject> list() {
    	ServiceObjectExample criteria = new ServiceObjectExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ServiceObject> listBySystemId(String systemId) {
    	ServiceObjectExample criteria = new ServiceObjectExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("code");
    	return mapper.selectByExample(criteria);
    }
    
    public List<ServiceObject> listApplicationServiceBySystem(String systemId) {
    	ServiceObjectExample criteria = new ServiceObjectExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public int delete(String id) {
    	ServiceObject so = mapper.selectByPrimaryKey(id);
    	if(so != null) {
    		so.setStatus(Status.EXP);
    		so.setUpdateTime(new Date());
    		ServiceObjectExample criteria = new ServiceObjectExample();
    		criteria.createCriteria().andIdEqualTo(id).andStatusEqualTo(Status.EFF);
    		return mapper.updateByExample(so, criteria);
    	}else {
    		return 0;
    	}
    }

    public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

    
}