package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ValidateRuleGroupMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ValidateRuleGroup {
    

    private String id;

    private String name;

    private String status;

    private String objectType;

    private String objectId;

    private String subRuleGroups;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<ValidateRule> validateRules;
    private Map<String, ValidateRuleGroup> subRuleGroupMap;
    
    

    private String objectCode;
    
    @Autowired
    private ValidateRuleGroupMapper mapper;
    
    @Autowired
    private ValidateRule validateRule;
    
    public ValidateRuleGroup byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public ValidateRuleGroup loadValidateRuleGroup(String id) {
    	ValidateRuleGroup validateRuleGroup = this.byId(id);
    	if(validateRuleGroup != null) {
    		validateRuleGroup.setValidateRules(this.validateRule.listByGroupId(id, validateRuleGroup.getObjectType()));
    	}
    	return validateRuleGroup;
    }
    
    public List<ValidateRuleGroup> list() {
    	ValidateRuleGroupExample criteria = new ValidateRuleGroupExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    
    public List<ValidateRuleGroup> listBySystemId(String systemId) {
    	ValidateRuleGroupExample criteria = new ValidateRuleGroupExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ValidateRuleGroup> listBySystemId(String systemId, String objectType, String objectId) {
    	ValidateRuleGroupExample criteria = new ValidateRuleGroupExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andObjectTypeEqualTo(objectType).andObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public void save(ValidateRuleGroup validateRuleGroup) {
    	if(validateRuleGroup.getId() == null || validateRuleGroup.getId().trim().length() == 0) {
    		validateRuleGroup.setId(KeyGenerate.uuidKey());
    	}
    	mapper.insert(validateRuleGroup);
    }
    
    public int updateById(ValidateRuleGroup validateRuleGroup) {
    	if(validateRuleGroup.getId() == null || validateRuleGroup.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(validateRuleGroup);
    }
    
    public ValidateRuleGroup saveValidateRuleGroup(ValidateRuleGroup validateRuleGroup) {
		Date nowDate = new Date();
		if(validateRuleGroup.getId() == null || validateRuleGroup.getId().trim().length() == 0) {//新增
			validateRuleGroup.setCreateTime(nowDate);
			validateRuleGroup.setStatus(Status.EFF);
			this.save(validateRuleGroup);
			
		}else {//更新
			validateRuleGroup.setUpdateTime(nowDate);
			this.updateById(validateRuleGroup);
			this.validateRule.deleteByGroupId(validateRuleGroup.getId());
		}
		
		if(validateRuleGroup.getValidateRules() != null && validateRuleGroup.getValidateRules().size() > 0) {
			validateRuleGroup.getValidateRules().forEach((rule) -> {
				rule.setRuleGroupId(validateRuleGroup.getId());
				if(rule.getId() == null || rule.getId().trim().length() == 0) {
					rule.setCreateTime(nowDate);
				}else {
					rule.setUpdateTime(nowDate);
				}
				this.validateRule.save(rule);
			});
		}
		return validateRuleGroup;
	}
    
    public int deleteByObject(String objectType,  String objectId) {
    	ValidateRuleGroup group = new ValidateRuleGroup();
    	group.setStatus(Status.EXP);
    	group.setUpdateTime(new Date());
		
    	ValidateRuleGroupExample criteria = new ValidateRuleGroupExample();
		criteria.createCriteria().andObjectTypeEqualTo(objectType).andObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
		
		return mapper.updateByExampleSelective(group, criteria);
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

	public List<ValidateRule> getValidateRules() {
		return validateRules;
	}

	public void setValidateRules(List<ValidateRule> validateRules) {
		this.validateRules = validateRules;
	}

	public String getObjectCode() {
		return objectCode;
	}

	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}
	
    public String getSubRuleGroups() {
        return subRuleGroups;
    }

    public void setSubRuleGroups(String subRuleGroups) {
        this.subRuleGroups = subRuleGroups;
    }

    public Map<String, ValidateRuleGroup> getSubRuleGroupMap() {
        return subRuleGroupMap;
    }

    public void setSubRuleGroupMap(Map<String, ValidateRuleGroup> subRuleGroupMap) {
        this.subRuleGroupMap = subRuleGroupMap;
    }
}