package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.DataMappingMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class DataMapping {
	private static Logger logger = LoggerFactory.getLogger(DataMapping.class);
	
    private String id;

    private String name;

    private String status;

    private String fromObjectType;

    private String fromObjectId;

    private String toObjectType;

    private String toObjectId;

	private String subDataMappings;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<AttributeMapping> attributeMappings;
    
    private AttributedObject fromObject;
    
    private AttributedObject toObject;
    
    @Autowired
    private DataMappingMapper mapper;
    
    @Autowired
    private AttributeMapping attributeMapping;
    
    /**
     * 生成领域对象和值传递对象的数据映射规则，两个列表的顺序和数量必须都相同
     * @param domainObjects
     * @param valueObjects
     * @return
     */
    public boolean generateDataMappings(List<DomainObject> domainObjects, List<ValueObject> valueObjects) throws EngineException{
    	if(logger.isDebugEnabled()) {
    		logger.debug("generateDataMappings starting");
    	}
    	if(domainObjects == null || domainObjects.size() == 0 || valueObjects == null || valueObjects.size() == 0) {
    		logger.error("domainObjects and valueObjects can't be null");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "domainObjects and valueObjects can't be null");
    	}
    	if(domainObjects.size() != valueObjects.size()) {
    		logger.error("the size of domainObjects and valueObjects must be equal");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "the size of domainObjects and valueObjects must be equal");
    	}
    	
    	int size = domainObjects.size();
    	DomainObject domainObject = null;
    	ValueObject valueObject = null;
    	DataMapping mapping = null;
    	for(int i=0; i<size; i++) {
    		domainObject = domainObjects.get(i);
    		valueObject = valueObjects.get(i);
    		
    		//DomainObject to RO
    		mapping = new DataMapping();
    		mapping.setName(domainObject.getCode() + ":" + valueObject.getCode());
    		mapping.setFromObjectType(ManagedObjectType.DOMAIN_OBJECT);
    		mapping.setFromObjectId(domainObject.getId());
    		mapping.setToObjectType(ManagedObjectType.VALUE_OBJECT);
    		mapping.setToObjectId(valueObject.getId());
    		mapping.setStatus(Status.EFF);
    		mapping.setTenantId(domainObject.getTenantId());
    		mapping.setSystemId(domainObject.getSystemId());
    		Date nowDate = new Date();
    		mapping.setCreateTime(nowDate);
    		mapping.setUpdateTime(nowDate);
    		
    		if(this.save(mapping) == null) {
    			logger.error("generateDataMapping failed!");
    			return false;
    		}
    		
    		if(!attributeMapping.generateAttributeMappings(domainObject.getAttributes(), valueObject.getAttributes(), mapping)) {
    			logger.error("generateAttributeMappings failed!");
    			throw new EngineException(ResultStatus.INTERNAL_ERROR, "generateAttributeMappings failed!");
    		}
    		//Vo to DO
    		mapping = new DataMapping();
    		mapping.setName(valueObject.getCode() + ":" + domainObject.getCode());
    		mapping.setFromObjectType(ManagedObjectType.VALUE_OBJECT);
    		mapping.setFromObjectId(valueObject.getId());
    		mapping.setToObjectType(ManagedObjectType.DOMAIN_OBJECT);
    		mapping.setToObjectId(domainObject.getId());
    		mapping.setStatus(Status.EFF);
    		mapping.setTenantId(domainObject.getTenantId());
    		mapping.setSystemId(domainObject.getSystemId());
    		mapping.setCreateTime(nowDate);
    		mapping.setUpdateTime(nowDate);
    		
    		if(this.save(mapping) == null) {
    			logger.error("generateDataMapping failed!");
    			return false;
    		}
    		
    		if(!attributeMapping.generateAttributeMappings(valueObject.getAttributes(), domainObject.getAttributes(), mapping)) {
    			logger.error("generateAttributeMappings failed!");
    			throw new EngineException(ResultStatus.INTERNAL_ERROR, "generateAttributeMappings failed!");
    		}
    		
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("generateDataMappings end");
    	}
    	return true;
    }
    
    public DataMapping save(DataMapping dataMapping)throws EngineException {
    	if(dataMapping.getId() == null || dataMapping.getId().trim().length() == 0) {
    		dataMapping.setId(KeyGenerate.uuidKey());
    	}
    	if( mapper.insert(dataMapping) > 0) {
    		if(dataMapping.getAttributeMappings() != null && dataMapping.getAttributeMappings().size() > 0) {
    			for(AttributeMapping attrMapping : dataMapping.getAttributeMappings()) {
    				attrMapping.setDataMappingId(dataMapping.getId());
    				attrMapping.setCreateTime(dataMapping.getCreateTime());
    				attrMapping.setUpdateTime(dataMapping.getUpdateTime());
    				if(this.attributeMapping.save(attrMapping) == null) {
    					throw new EngineException(ResultStatus.BUSINESS_ERROR, "add AttributeMapping failed!");
    				}
    			}
    		}
    		
    		return dataMapping;
    	}else {
    		return null;
    	}
    }
    
    public DataMapping update(DataMapping dataMapping) throws EngineException{
		if( mapper.updateByPrimaryKey(dataMapping) > 0) {
			this.attributeMapping.deleteByDataMappingId(dataMapping.getId());
			if(!CollectionUtils.isEmpty(dataMapping.getAttributeMappings())) {
				for(AttributeMapping attrMapping : dataMapping.getAttributeMappings()){
					attrMapping.setDataMappingId(dataMapping.getId());
					if(StringUtils.isBlank(attrMapping.getId())) {
						attrMapping.setCreateTime(dataMapping.getCreateTime());
					}
					attrMapping.setUpdateTime(dataMapping.getUpdateTime());
					if(this.attributeMapping.save(attrMapping) == null) {
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "add AttributeMapping failed!");
					}
				}
			}
			return dataMapping;
		}else {
			return null;
		}
	}

    public DataMapping byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<DataMapping> list() {
    	DataMappingExample criteria = new DataMappingExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	List<DataMapping> list = mapper.selectByExample(criteria);
    	return list;
    }
    
    public List<DataMapping> listByFromObject(String objectType, String objectId) {
    	DataMappingExample criteria = new DataMappingExample();
    	criteria.createCriteria().andFromObjectTypeEqualTo(objectType).andFromObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
    	List<DataMapping> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
    		list.forEach((mapping) -> {
    			mapping.setAttributeMappings(attributeMapping.listByDataMappingId(mapping.getId()));
    		});
    	}
    	return list;
    }
    
    public List<DataMapping> listByToObject(String objectType, String objectId) {
    	DataMappingExample criteria = new DataMappingExample();
    	criteria.createCriteria().andToObjectTypeEqualTo(objectType).andToObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
    	List<DataMapping> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
    		list.forEach((mapping) -> {
    			mapping.setAttributeMappings(attributeMapping.listByDataMappingId(mapping.getId()));
    		});
    	}
    	return list;
    }
    
    public List<DataMapping> listBySystemId(String systemId) {
    	DataMappingExample criteria = new DataMappingExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public int deleteByFromObject(String objectType,  String objectId) {
    	DataMapping dm = new DataMapping();
    	dm.setStatus(Status.EXP);
    	dm.setUpdateTime(new Date());
		
		DataMappingExample criteria = new DataMappingExample();
		criteria.createCriteria().andFromObjectTypeEqualTo(objectType).andFromObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
		
		return mapper.updateByExampleSelective(dm, criteria);
	}
    
    public int deleteByToObject(String objectType,  String objectId) {
    	DataMapping dm = new DataMapping();
    	dm.setStatus(Status.EXP);
    	dm.setUpdateTime(new Date());
		
		DataMappingExample criteria = new DataMappingExample();
		criteria.createCriteria().andToObjectTypeEqualTo(objectType).andToObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
		
		return mapper.updateByExampleSelective(dm, criteria);
	}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

	public List<AttributeMapping> getAttributeMappings() {
		return attributeMappings;
	}

	public void setAttributeMappings(List<AttributeMapping> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

	public String getFromObjectType() {
		return fromObjectType;
	}

	public void setFromObjectType(String fromObjectType) {
		this.fromObjectType = fromObjectType;
	}

	public String getFromObjectId() {
		return fromObjectId;
	}

	public void setFromObjectId(String fromObjectId) {
		this.fromObjectId = fromObjectId;
	}

	public String getToObjectType() {
		return toObjectType;
	}

	public void setToObjectType(String toObjectType) {
		this.toObjectType = toObjectType;
	}

	public String getToObjectId() {
		return toObjectId;
	}

	public void setToObjectId(String toObjectId) {
		this.toObjectId = toObjectId;
	}

	public DataMappingMapper getMapper() {
		return mapper;
	}

	public void setMapper(DataMappingMapper mapper) {
		this.mapper = mapper;
	}

	public AttributedObject getFromObject() {
		return fromObject;
	}

	public void setFromObject(AttributedObject fromObject) {
		this.fromObject = fromObject;
	}

	public AttributedObject getToObject() {
		return toObject;
	}

	public void setToObject(AttributedObject toObject) {
		this.toObject = toObject;
	}
	
	public String getSubDataMappings() {
		return subDataMappings;
	}

	public void setSubDataMappings(String subDataMappings) {
		this.subDataMappings = subDataMappings;
	}

	
}