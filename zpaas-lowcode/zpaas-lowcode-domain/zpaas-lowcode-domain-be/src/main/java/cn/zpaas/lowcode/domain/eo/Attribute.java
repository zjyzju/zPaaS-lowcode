package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.AttributeMapper;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.KeyGenerate;

/**
 * 属性领域对象
 *
 * @author zjy
 * createTime 2025年04月6日-下午10:39:40
 */
@Repository
public class Attribute {
	private String id;

    private String name;

    private String code;

    private String isListAttr;

    private String attrType;

    private String attrClass;

    private String objectType;

    private String objectId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private Integer displayOrder;
    
    private AttributedObject attrClassObject;
    
    @Autowired
    private AttributeMapper mapper;
    
    public int updateById(Attribute attribute) {
    	return mapper.updateByPrimaryKey(attribute);
    }
    
    public int save(Attribute attribute) {
    	if(StringUtils.isBlank(attribute.getId())) {
    		attribute.setId(KeyGenerate.uuidKey());
    	}
    	
    	return mapper.insert(attribute);
    }
    
    public Attribute byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public Attribute queryByObjectAndCode(String objectType, String domainObjectId, String attrCode) {
    	AttributeExample criteria = new AttributeExample();
    	criteria.createCriteria().andObjectIdEqualTo(domainObjectId).andObjectTypeEqualTo(objectType).andCodeEqualTo(attrCode);
    	List<Attribute> list = mapper.selectByExample(criteria);
    	if(!CollectionUtils.isEmpty(list)) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public List<Attribute> listByObjectId(String objectType, String domainObjectId) {
    	AttributeExample criteria = new AttributeExample();
    	criteria.createCriteria().andObjectIdEqualTo(domainObjectId).andObjectTypeEqualTo(objectType);
        criteria.setOrderByClause("code");
    	return mapper.selectByExample(criteria);
    }
    
    public List<Attribute> listByObjectTypeAndSystem(String objectType, String systemId) {
    	AttributeExample criteria = new AttributeExample();
    	criteria.createCriteria().andObjectTypeEqualTo(objectType).andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<Attribute> listBySystem(String systemId) {
    	AttributeExample criteria = new AttributeExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<Attribute> listByObjectType(String objectType) {
    	AttributeExample criteria = new AttributeExample();
    	criteria.createCriteria().andObjectTypeEqualTo(objectType);
    	return mapper.selectByExample(criteria);
    }
    
    public List<Attribute> list() {
    	return mapper.selectByExample(null);
    }
    
    public int delete(String id) {
    	return mapper.deleteByPrimaryKey(id);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getIsListAttr() {
        return isListAttr;
    }

    public void setIsListAttr(String isListAttr) {
        this.isListAttr = isListAttr == null ? null : isListAttr.trim();
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType == null ? null : attrType.trim();
    }

    public String getAttrClass() {
        return attrClass;
    }

    public void setAttrClass(String attrClass) {
        this.attrClass = attrClass == null ? null : attrClass.trim();
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType == null ? null : objectType.trim();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId == null ? null : objectId.trim();
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

	public AttributedObject getAttrClassObject() {
		return attrClassObject;
	}

	public void setAttrClassObject(AttributedObject attrClassObject) {
		this.attrClassObject = attrClassObject;
	}
}