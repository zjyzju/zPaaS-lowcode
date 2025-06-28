package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.FuncRegionAttrAssignMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncRegionAttrAssign {
	
	public static final String DISPLAY_TYPE_I = "I";//输入框
	public static final String DISPLAY_TYPE_S = "S";//输入框
	public static final String DISPLAY_TYPE_A = "A";//文本域
	public static final String DISPLAY_TYPE_R = "R";//单选框
	public static final String DISPLAY_TYPE_C = "C";//多选框
	public static final String DISPLAY_TYPE_D = "D";//日期选择
	public static final String DISPLAY_TYPE_T = "T";//日期时间选择
	public static final String DISPLAY_TYPE_H = "H";//隐藏框
	public static final String DISPLAY_TYPE_L = "L";//文件标签
	
    private String id;

    private String funcId;

    private String funcRegionId;

    private String objectAssignId;

    private String objectType;

    private String objectId;

    private String attributeId;

    private String displayType;

    private String displayName;
    
    private String displayWidth;
    
    private Integer rowSpan;
    
    private Integer colSpan;
    
    private String readonly;
    
    private String required;
    
    private Integer displayOrder;
    
    private String displayCfg;

    private String hidden;
    
    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private Attribute attribute;

    private DataSetDetail dataSetDetail;

    private List<FuncRegionAttrAssign> attrAssigns;

    @Autowired
    private FuncRegionAttrAssignMapper mapper;
    
    @Autowired
    private Attribute attributeSV;

    @Autowired
    private DataSet dataSetSV;
    
    public FuncRegionAttrAssign byId(String id) {
    	FuncRegionAttrAssign funcRegionAttrAssign = mapper.selectByPrimaryKey(id);
    	if(funcRegionAttrAssign != null) {
            if(ManagedObjectType.BI_DATA_SET.equals(funcRegionAttrAssign.getObjectType())) {
                funcRegionAttrAssign.setDataSetDetail(dataSetSV.loadDetail(funcRegionAttrAssign.getAttributeId()));
            }else {
                funcRegionAttrAssign.setAttribute(attributeSV.byId(funcRegionAttrAssign.getAttributeId()));
            }
    	}
    	return funcRegionAttrAssign;
    }
    
    public List<FuncRegionAttrAssign> loadByFuncRegion(String funcRegionId) {
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andFuncRegionIdEqualTo(funcRegionId);
    	criteria.setOrderByClause("display_order");
    	List<FuncRegionAttrAssign> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		for(FuncRegionAttrAssign attrAssign : list) {
                if(ManagedObjectType.BI_DATA_SET.equals(attrAssign.getObjectType())) {
                    attrAssign.setDataSetDetail(dataSetSV.loadDetail(attrAssign.getAttributeId()));
                }else {
                    attrAssign.setAttribute(attributeSV.byId(attrAssign.getAttributeId()));
                }
    		}
    	}
    	return list;
    }
    
    public List<FuncRegionAttrAssign> loadByFunc(String funcId) {
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId);
    	criteria.setOrderByClause("func_region_id,display_order");
    	return mapper.selectByExample(criteria);
    }

    public List<FuncRegionAttrAssign> listBySystem(String systemId) {
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("func_region_id,display_order");
    	return mapper.selectByExample(criteria);
    }

    public List<FuncRegionAttrAssign> list() {
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.setOrderByClause("func_region_id,display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public FuncRegionAttrAssign save(FuncRegionAttrAssign funcRegionAttrAssign) {
    	funcRegionAttrAssign.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(funcRegionAttrAssign) > 0) {
            if(ManagedObjectType.BI_DATA_SET.equals(funcRegionAttrAssign.getObjectType())) {
                funcRegionAttrAssign.setDataSetDetail(dataSetSV.loadDetail(funcRegionAttrAssign.getAttributeId()));
            }else {
                funcRegionAttrAssign.setAttribute(attributeSV.byId(funcRegionAttrAssign.getAttributeId()));
            }
    		return funcRegionAttrAssign;
    	}else {
    		return null;
    	}
    }

    public int batchSave(List<FuncRegionAttrAssign> attrAssigns) throws EngineException {
        if(CollectionUtils.isEmpty(attrAssigns)) {
            return 0;
        }
        for(FuncRegionAttrAssign attrAssign : attrAssigns) {
            Date nowDate = new Date();
            if(StringUtils.isBlank(attrAssign.getId())) {
                attrAssign.setId(KeyGenerate.uuidKey());
                attrAssign.setCreateTime(nowDate);
            }else {
                attrAssign.setUpdateTime(nowDate);
            }
            if(mapper.insert(attrAssign) <= 0) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "save attrAssign failed!");
            }
        }
        return attrAssigns.size();
    }

    public int delete(String funcId, String regionId) {
        FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId).andFuncRegionIdEqualTo(regionId);
        return mapper.deleteByExample(criteria);
    }
    
    public List<FuncRegionAttrAssign> copyFuncRegionAttrAssign(String funcId, String fromRegionId, String targetRegionId) throws EngineException{
        this.delete(funcId, targetRegionId);
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId).andFuncRegionIdEqualTo(fromRegionId);
    	List<FuncRegionAttrAssign> srcAssigns = mapper.selectByExample(criteria);
    	if(srcAssigns != null && !srcAssigns.isEmpty()) {
    		for(FuncRegionAttrAssign funcRegionAttrAssign : srcAssigns) {
    			funcRegionAttrAssign.setId(null);
    			funcRegionAttrAssign.setFuncRegionId(targetRegionId);
    			if(this.create(funcRegionAttrAssign) == null) {
    				throw new EngineException(ResultStatus.BUSINESS_ERROR, "copy FuncRegionAttrAssign failed!");
    			}
    		}
    	}
    	return this.loadByFuncRegion(targetRegionId);
    }
    
    public List<FuncRegionAttrAssign> copyFuncRegionAttrAssignFromOther(String fromFuncId, String fromRegionId, 
    		String targetFuncId, String targetRegionId, Map<String, String> objectId2AssignId) throws EngineException{
        this.delete(targetFuncId, targetRegionId);
    	FuncRegionAttrAssignExample criteria = new FuncRegionAttrAssignExample();
    	criteria.createCriteria().andFuncIdEqualTo(fromFuncId).andFuncRegionIdEqualTo(fromRegionId);
    	List<FuncRegionAttrAssign> srcAssigns = mapper.selectByExample(criteria);
    	if(srcAssigns != null && !srcAssigns.isEmpty()) {
    		
    		for(FuncRegionAttrAssign funcRegionAttrAssign : srcAssigns) {
    			funcRegionAttrAssign.setId(null);
    			funcRegionAttrAssign.setFuncId(targetFuncId);
    			funcRegionAttrAssign.setFuncRegionId(targetRegionId);
    			funcRegionAttrAssign.setObjectAssignId(objectId2AssignId.get(funcRegionAttrAssign.getObjectId()));
    			if(this.create(funcRegionAttrAssign) == null) {
    				throw new EngineException(ResultStatus.BUSINESS_ERROR, "copy FuncRegionAttrAssign failed!");
    			}
    		}
    	}
    	return this.loadByFuncRegion(targetRegionId);
    }
    
    public FuncRegionAttrAssign create(FuncRegionAttrAssign funcRegionAttrAssign) {
    	if(StringUtils.isBlank(funcRegionAttrAssign.getId())) {
    		funcRegionAttrAssign.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	funcRegionAttrAssign.setCreateTime(nowDate);
    	funcRegionAttrAssign.setUpdateTime(nowDate);
    	if(mapper.insert(funcRegionAttrAssign) > 0) {
    		funcRegionAttrAssign.setAttribute(attributeSV.byId(funcRegionAttrAssign.getAttributeId()));
    		return funcRegionAttrAssign;
    	}else {
    		return null;
    	}
    }

    public int saveFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign) {
        Date nowDate = new Date();
    	if(StringUtils.isBlank(funcRegionAttrAssign.getId())) {
    		funcRegionAttrAssign.setId(KeyGenerate.uuidKey());
            funcRegionAttrAssign.setCreateTime(nowDate);
    	}
    	funcRegionAttrAssign.setUpdateTime(nowDate);
    	return mapper.insert(funcRegionAttrAssign);
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

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    public String getFuncRegionId() {
        return funcRegionId;
    }

    public void setFuncRegionId(String funcRegionId) {
        this.funcRegionId = funcRegionId == null ? null : funcRegionId.trim();
    }

    public String getObjectAssignId() {
        return objectAssignId;
    }

    public void setObjectAssignId(String objectAssignId) {
        this.objectAssignId = objectAssignId == null ? null : objectAssignId.trim();
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

    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType == null ? null : displayType.trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public String getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(String displayWidth) {
		this.displayWidth = displayWidth;
	}

	public Integer getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(Integer rowSpan) {
		this.rowSpan = rowSpan;
	}

	public Integer getColSpan() {
		return colSpan;
	}

	public void setColSpan(Integer colSpan) {
		this.colSpan = colSpan;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getDisplayCfg() {
		return displayCfg;
	}

	public void setDisplayCfg(String displayCfg) {
		this.displayCfg = displayCfg;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

    public List<FuncRegionAttrAssign> getAttrAssigns() {
        return attrAssigns;
    }

    public void setAttrAssigns(List<FuncRegionAttrAssign> attrAssigns) {
        this.attrAssigns = attrAssigns;
    }

    public DataSetDetail getDataSetDetail() {
        return dataSetDetail;
    }

    public void setDataSetDetail(DataSetDetail dataSetDetail) {
        this.dataSetDetail = dataSetDetail;
    }
}