package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.FuncObjectAttrOptionsMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncObjectAttrOptions {
	public static Logger logger = LoggerFactory.getLogger(FuncObjectAttrOptions.class);
	
	
    private String id;

    private String objectAssignId;

    private String attributeId;
    
    private String interactionType;

    private String optionCfgType;

    private String optionCfg;
    
    private String parentObjectAssignId;
    
    private String parentAttributeId;
    
    private String displayHideCfg;
    
    private String popupCfg;
    
    private String queryType;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private JsonArray options;
    
    @Autowired
    private FuncObjectAttrOptionsMapper mapper;
    
    public FuncObjectAttrOptions byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
	 /**
     * 加载绑定对象属性选项配置信息
     * @param objectAssignId
     * @param initFlag
     * @return
     * @throws EngineException
     */
    public List<FuncObjectAttrOptions> loadByObjectAssign(String objectAssignId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andObjectAssignIdEqualTo(objectAssignId);
    	return mapper.selectByExample(criteria);
    }
      
    public FuncObjectAttrOptions loadAttrOptions(String objectAssignId, String attributeId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andObjectAssignIdEqualTo(objectAssignId).andAttributeIdEqualTo(attributeId);
    	List<FuncObjectAttrOptions> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		return list.get(0);
    	}
    	
    	return null;
    }

	public FuncObjectAttrOptions saveAttrOptions(FuncObjectAttrOptions objectAssignOptions) {
    	Date nowDate = new Date();
    	objectAssignOptions.setUpdateTime(nowDate);
    	if(objectAssignOptions.getId() == null || objectAssignOptions.getId().trim().length() == 0) {
    		objectAssignOptions.setId(KeyGenerate.uuidKey());
    		if(mapper.insert(objectAssignOptions) > 0) {
    			return objectAssignOptions;
    		}
    	}else {
    		if(mapper.updateByPrimaryKey(objectAssignOptions) > 0) {
    			return objectAssignOptions;
    		}
    	}
    	return null;
    }
    
    public int deleteAttrOptions(String objectAssignId, String attributeId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andObjectAssignIdEqualTo(objectAssignId).andAttributeIdEqualTo(attributeId);
    	return mapper.deleteByExample(criteria);
    }

	public int deleteAttrOptions(String objectAssignId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andObjectAssignIdEqualTo(objectAssignId);
    	return mapper.deleteByExample(criteria);
    }

	public List<FuncObjectAttrOptions> listAttrOptions(String objectAssignId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andObjectAssignIdEqualTo(objectAssignId);
    	return mapper.selectByExample(criteria);
	}

	public List<FuncObjectAttrOptions> listBySystem(String systemId) {
    	FuncObjectAttrOptionsExample criteria = new FuncObjectAttrOptionsExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
	}

	public List<FuncObjectAttrOptions> list() {
    	return mapper.selectByExample(null);
	}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getObjectAssignId() {
        return objectAssignId;
    }

    public void setObjectAssignId(String objectAssignId) {
        this.objectAssignId = objectAssignId == null ? null : objectAssignId.trim();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public String getOptionCfgType() {
        return optionCfgType;
    }

    public void setOptionCfgType(String optionCfgType) {
        this.optionCfgType = optionCfgType == null ? null : optionCfgType.trim();
    }

    public String getOptionCfg() {
        return optionCfg;
    }

    public void setOptionCfg(String optionCfg) {
        this.optionCfg = optionCfg == null ? null : optionCfg.trim();
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

	public JsonArray getOptions() {
		return options;
	}

	public void setOptions(JsonArray options) {
		this.options = options;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getParentObjectAssignId() {
		return parentObjectAssignId;
	}

	public void setParentObjectAssignId(String parentObjectAssignId) {
		this.parentObjectAssignId = parentObjectAssignId;
	}

	public String getParentAttributeId() {
		return parentAttributeId;
	}

	public void setParentAttributeId(String parentAttributeId) {
		this.parentAttributeId = parentAttributeId;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public String getDisplayHideCfg() {
		return displayHideCfg;
	}

	public void setDisplayHideCfg(String displayHideCfg) {
		this.displayHideCfg = displayHideCfg;
	}

	public String getPopupCfg() {
		return popupCfg;
	}

	public void setPopupCfg(String popupCfg) {
		this.popupCfg = popupCfg;
	}
}