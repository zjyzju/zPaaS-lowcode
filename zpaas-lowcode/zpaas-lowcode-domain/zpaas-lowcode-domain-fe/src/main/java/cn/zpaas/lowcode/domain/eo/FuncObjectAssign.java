package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.FuncObjectAssignMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncObjectAssign {
	public static final String ASSIGN_TYPE_M = "M";//主对象
	public static final String ASSIGN_TYPE_S = "S";//从对象

	public static final String OBJECT_TYPE_B = "B";//BI数据集
	public static final String OBJECT_TYPE_V = "V";//值传递对象
	
    private String id;

    private String funcId;

    private String objectType;

    private String objectId;

    private String assignType;
    
    private String keyAttrId;

    private String relAttrId;
    
    private String mainRelAttrId;
    
    private Integer displayOrder;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<Attribute> attributes;
    
    private Attribute relAttribute;
    
    private Attribute mainRelAttribute;
    
    private Attribute keyAttribute;
    
    private Map<String, FuncObjectAttrOptions> attrOptionMap;
    
    private AttributedObject assignObject;//存放领域对象/值传递对象信息
    
	private DataSet biDataSet;//存放BI数据集信息

	@Autowired
    private FuncObjectAssignMapper mapper;
    
    @Autowired
    private FuncObjectAttrOptions funcObjectAttrOptionSV;
    
    @Autowired 
    private DomainObject domainObjectSV;
    
    @Autowired
    private ValueObject valueObjectSV;
    
    @Autowired
    private Attribute attributeSV;

	@Autowired
	private DataSet dataSetSV;
    
    public FuncObjectAssign byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public int deleteById(String id) {
    	
    	return mapper.deleteByPrimaryKey(id);
    }
    
    public FuncObjectAssign save(FuncObjectAssign objectAssign) {
    	objectAssign.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(objectAssign) > 0) {
    		return objectAssign;
    	}else {
    		return null;
    	}
    }

	public List<FuncObjectAttrOptions> copyFuncObjectAttrOptionsFromOther(String fromFuncObjectAssignId, String targetFuncObjectAssignId) {
		List<FuncObjectAttrOptions> objectAttrOptions = funcObjectAttrOptionSV.listAttrOptions(fromFuncObjectAssignId);
		if(!CollectionUtils.isEmpty(objectAttrOptions)) {
			funcObjectAttrOptionSV.deleteAttrOptions(targetFuncObjectAssignId);
			objectAttrOptions.forEach((objectAttrOption)-> {
				objectAttrOption.setObjectAssignId(targetFuncObjectAssignId);
				objectAttrOption.setId(null);
				Date nowDate = new Date();
				objectAttrOption.setCreateTime(nowDate);
				objectAttrOption.setUpdateTime(nowDate);
				objectAttrOption.setParentAttributeId(null);
				objectAttrOption.setParentObjectAssignId(null);
				funcObjectAttrOptionSV.saveAttrOptions(objectAttrOption);
			});
		}
		return objectAttrOptions;
	}
    
    public FuncObjectAssign create(FuncObjectAssign objectAssign) {
    	if(objectAssign.getId() == null || objectAssign.getId().trim().length() == 0) {
    		objectAssign.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	objectAssign.setUpdateTime(nowDate);
    	objectAssign.setCreateTime(nowDate);
    	if(mapper.insert(objectAssign) > 0) {
    		return objectAssign;
    	}else {
    		return null;
    	}
    }
    
    /**
     * 加载绑定对象信息
     * @param funcId
     * @return
     * @throws EngineException
     */
    public List<FuncObjectAssign> listByFunc(String funcId){
    	FuncObjectAssignExample criteria = new FuncObjectAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId);
		criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }

	public List<FuncObjectAssign> listBySystem(String systemId){
    	FuncObjectAssignExample criteria = new FuncObjectAssignExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
		criteria.setOrderByClause("display_order");
		return mapper.selectByExample(criteria);
    }

	public List<FuncObjectAssign> list(){
		FuncObjectAssignExample criteria = new FuncObjectAssignExample();
		criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public FuncObjectAssign loadAllById(String Id) throws EngineException{
    	FuncObjectAssign funcObjectAssign = this.byId(Id);
    	if(funcObjectAssign != null) {
			List<FuncObjectAttrOptions> attrOptions = funcObjectAttrOptionSV.loadByObjectAssign(funcObjectAssign.getId());
			if (attrOptions != null && !attrOptions.isEmpty()) {
				funcObjectAssign.setAttrOptionMap(
						attrOptions.stream().collect(Collectors.toMap(FuncObjectAttrOptions::getAttributeId, t -> t)));
			}

			if (ManagedObjectType.DOMAIN_OBJECT.equals(funcObjectAssign.getObjectType())) {
				funcObjectAssign.setAssignObject(domainObjectSV.loadDomainObjectAndAttrs(funcObjectAssign.getObjectId()));
			} else if (ManagedObjectType.VALUE_OBJECT.equals(funcObjectAssign.getObjectType())) {
				funcObjectAssign.setAssignObject(valueObjectSV.byId(funcObjectAssign.getObjectId()));
			}else if (OBJECT_TYPE_B.equals(funcObjectAssign.getObjectType())) {//BI数据集
				funcObjectAssign.setBiDataSet(dataSetSV.loadWithDetails(funcObjectAssign.getObjectId()));
			}
			if(!OBJECT_TYPE_B.equals(funcObjectAssign.getObjectType())) {//BI数据集不需要处理
				List<Attribute> attributes = null;
				if(funcObjectAssign.getAssignObject() != null) {
					attributes = funcObjectAssign.getAssignObject().getAttributes();
				}
				funcObjectAssign.setAttributes(attributes);
				if(attributes != null) {
					for(Attribute attribute : attributes) {
						if(attribute.getId().equals(funcObjectAssign.getKeyAttrId())) {
							funcObjectAssign.setKeyAttribute(attribute);
						} 
						if(attribute.getId().equals(funcObjectAssign.getRelAttrId())) {
							funcObjectAssign.setRelAttribute(attribute);
						}
						if (ManagedObjectType.DOMAIN_OBJECT.equals(attribute.getAttrType())) {
							attribute.setAttrClassObject(domainObjectSV.loadDomainObjectAndAttrs(attribute.getAttrClass()));
						} else if (ManagedObjectType.VALUE_OBJECT.equals(attribute.getAttrType())) {
							attribute.setAttrClassObject(valueObjectSV.byId(attribute.getAttrClass()));
						}
					}
				}
				
				if(funcObjectAssign.getMainRelAttrId() != null && funcObjectAssign.getMainRelAttrId().trim().length() > 0) {
					funcObjectAssign.setMainRelAttribute(attributeSV.byId(funcObjectAssign.getMainRelAttrId()));
				}
			}
			
    	}
    	
    	return funcObjectAssign;
    }

	public List<FuncObjectAssign> listWithAssignObjectByFunc(String funcId) throws EngineException{
    	FuncObjectAssignExample criteria = new FuncObjectAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId);
		criteria.setOrderByClause("display_order");
    	List<FuncObjectAssign> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		for (FuncObjectAssign objectAssign : list) {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(objectAssign.getObjectType())) {
	    			objectAssign.setAssignObject(domainObjectSV.loadDomainObjectAndAttrs(objectAssign.getObjectId()));
	    		}else if(ManagedObjectType.VALUE_OBJECT.equals(objectAssign.getObjectType())) {
	    			objectAssign.setAssignObject(valueObjectSV.byId(objectAssign.getObjectId()));
	    		}else if (OBJECT_TYPE_B.equals(objectAssign.getObjectType())) {//BI数据集
					objectAssign.setBiDataSet(dataSetSV.loadWithDetails(objectAssign.getObjectId()));
				}
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

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
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

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType == null ? null : assignType.trim();
    }

    public String getRelAttrId() {
        return relAttrId;
    }

    public void setRelAttrId(String relAttrId) {
        this.relAttrId = relAttrId == null ? null : relAttrId.trim();
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

	public Map<String, FuncObjectAttrOptions> getAttrOptionMap() {
		return attrOptionMap;
	}

	public void setAttrOptionMap(Map<String, FuncObjectAttrOptions> attrOptionMap) {
		this.attrOptionMap = attrOptionMap;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute getRelAttribute() {
		return relAttribute;
	}

	public void setRelAttribute(Attribute relAttribute) {
		this.relAttribute = relAttribute;
	}

	public AttributedObject getAssignObject() {
		return assignObject;
	}

	public void setAssignObject(AttributedObject assignObject) {
		this.assignObject = assignObject;
	}

	public String getMainRelAttrId() {
		return mainRelAttrId;
	}

	public void setMainRelAttrId(String mainRelAttrId) {
		this.mainRelAttrId = mainRelAttrId;
	}

	public Attribute getMainRelAttribute() {
		return mainRelAttribute;
	}

	public void setMainRelAttribute(Attribute mainRelAttribute) {
		this.mainRelAttribute = mainRelAttribute;
	}

	public String getKeyAttrId() {
		return keyAttrId;
	}

	public void setKeyAttrId(String keyAttrId) {
		this.keyAttrId = keyAttrId;
	}

	public Attribute getKeyAttribute() {
		return keyAttribute;
	}

	public void setKeyAttribute(Attribute keyAttribute) {
		this.keyAttribute = keyAttribute;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public DataSet getBiDataSet() {
		return biDataSet;
	}

	public void setBiDataSet(DataSet biDataSet) {
		this.biDataSet = biDataSet;
	}
}