package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.AttributeMappingMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class AttributeMapping {
	private static Logger logger = LoggerFactory.getLogger(AttributeMapping.class);
	
    private String id;

    private String fromAttributeId;

    private String toAttributeId;

    private String dataMappingId;

    private String transferMethod;

    private String transferCfg;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private Attribute fromAttribute;
    
    private Attribute toAttribute;
    
    @Autowired
    private AttributeMappingMapper mapper;
    
    /**
     * 生成属性映射数据，from和to的属性列表的顺序以及数量必须相同
     * @param fromAttributes
     * @param toAttributes
     * @param dataMapping
     * @return
     * @throws EngineException
     */
    public boolean generateAttributeMappings(List<Attribute> fromAttributes, List<Attribute> toAttributes, DataMapping dataMapping) throws EngineException{
    	if(logger.isDebugEnabled()) {
    		logger.debug("generateAttributeMappings starting");
    	}
    	if(CollectionUtils.isEmpty(fromAttributes) || CollectionUtils.isEmpty(toAttributes)) {
    		logger.error("fromAttributes and toAttributes can't be null");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "fromAttributes and toAttributes can't be null");
    	}
    	if(fromAttributes.size() != toAttributes.size()) {
    		logger.error("the size of fromAttributes and toAttributes must be equal");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "the size of fromAttributes and toAttributes must be equal");
    	}
    	int size = fromAttributes.size();
    	Attribute from = null, to= null;
    	AttributeMapping attributeMapping = null;
    	for(int i=0; i< size; i++) {
    		from = fromAttributes.get(i);
    		to = toAttributes.get(i);
    		attributeMapping = new AttributeMapping();
    		attributeMapping.setDataMappingId(dataMapping.getId());
    		attributeMapping.setFromAttributeId(from.getId());
    		attributeMapping.setToAttributeId(to.getId());
    		attributeMapping.setTenantId(dataMapping.getTenantId());
    		attributeMapping.setSystemId(dataMapping.getSystemId());
    		Date nowDate = new Date();
    		attributeMapping.setCreateTime(nowDate);
    		attributeMapping.setUpdateTime(nowDate);
    		if(this.save(attributeMapping) == null) {
    			logger.error("generateAttributeMappings failed!");
    			return false;
    		}
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("generateAttributeMappings end");
    	}
    	return true;
    }
    
    public AttributeMapping save(AttributeMapping attributeMapping) {
    	if(StringUtils.isBlank(attributeMapping.getId())) {
    		attributeMapping.setId(KeyGenerate.uuidKey());
    	}
    	if( mapper.insert(attributeMapping) > 0) {
    		return attributeMapping;
    	}else {
    		return null;
    	}
    }
    
    public AttributeMapping byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<AttributeMapping> list() {
    	return mapper.selectByExample(null);
    }
    
    public List<AttributeMapping> listByDataMappingId(String dataMappingId) {
    	AttributeMappingExample criteria = new AttributeMappingExample();
    	criteria.createCriteria().andDataMappingIdEqualTo(dataMappingId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<AttributeMapping> listBySystem(String systemId) {
    	AttributeMappingExample criteria = new AttributeMappingExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }
    
    public int deleteByDataMappingId(String dataMappingId) {
    	AttributeMappingExample criteria = new AttributeMappingExample();
    	criteria.createCriteria().andDataMappingIdEqualTo(dataMappingId);
    	return mapper.deleteByExample(criteria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getFromAttributeId() {
        return fromAttributeId;
    }

    public void setFromAttributeId(String fromAttributeId) {
        this.fromAttributeId = fromAttributeId == null ? null : fromAttributeId.trim();
    }

    public String getToAttributeId() {
        return toAttributeId;
    }

    public void setToAttributeId(String toAttributeId) {
        this.toAttributeId = toAttributeId == null ? null : toAttributeId.trim();
    }

    public String getDataMappingId() {
        return dataMappingId;
    }

    public void setDataMappingId(String dataMappingId) {
        this.dataMappingId = dataMappingId == null ? null : dataMappingId.trim();
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

	public Attribute getFromAttribute() {
		return fromAttribute;
	}

	public void setFromAttribute(Attribute fromAttribute) {
		this.fromAttribute = fromAttribute;
	}

	public Attribute getToAttribute() {
		return toAttribute;
	}

	public void setToAttribute(Attribute toAttribute) {
		this.toAttribute = toAttribute;
	}
    
    public String getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(String transferMethod) {
        this.transferMethod = transferMethod;
    }

    public String getTransferCfg() {
        return transferCfg;
    }

    public void setTransferCfg(String transferCfg) {
        this.transferCfg = transferCfg;
    }
}