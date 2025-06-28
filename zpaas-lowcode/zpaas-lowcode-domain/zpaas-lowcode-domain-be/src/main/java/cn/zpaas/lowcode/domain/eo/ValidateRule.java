package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.domain.mapper.ValidateRuleMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class ValidateRule {
    private String id;

    private String ruleGroupId;

    private String attributeId;
    
    private String subAttributeId;

    private String ruleTypeId;

    private String ruleValue1;

    private String ruleValue2;

    private String ruleValue3;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private String attributeCode;
    
    private String subAttributeCode;
    
    private ValidateRuleType validateRuleType;
    
    @Autowired
    private ValidateRuleMapper mapper;
    
    @Autowired
    private Attribute attributeSV;
    
    @Autowired
    private ValidateRuleType validateRuleTypeSV;
    
    public ValidateRule byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<ValidateRule> listByGroupId(String ruleGroupId, String objectType) {
    	ValidateRuleExample criteria = new ValidateRuleExample();
    	criteria.createCriteria().andRuleGroupIdEqualTo(ruleGroupId);
    	List<ValidateRule> list = mapper.selectByExample(criteria);
    	if(!CollectionUtils.isEmpty(list)) {
    		for(ValidateRule validateRule : list) {
    			if(ManagedObjectType.SERVICE_METHOD.equals(objectType)) {
    				validateRule.setAttributeCode(validateRule.getAttributeId());
                    if(!StringUtils.isBlank(validateRule.getSubAttributeId())) {
                        Attribute attr = attributeSV.byId(validateRule.getSubAttributeId());
                        if(attr != null) {
                            validateRule.setSubAttributeCode(attr.getCode());
                        }else {
                            validateRule.setSubAttributeCode(validateRule.getSubAttributeId());
                        }
                    }
    			}else {
    				validateRule.setAttributeCode(attributeSV.byId(validateRule.getAttributeId()).getCode());
    			}
    			
    			validateRule.setValidateRuleType(validateRuleTypeSV.byId(validateRule.getRuleTypeId()));
    		}
    	}
    	return list;
    }
    
    public List<ValidateRule> listByGroupId(String ruleGroupId) {
    	ValidateRuleExample criteria = new ValidateRuleExample();
    	criteria.createCriteria().andRuleGroupIdEqualTo(ruleGroupId);
    	return mapper.selectByExample(criteria);
    	
    }
    
    public int deleteByGroupId(String ruleGroupId) {
    	ValidateRuleExample criteria = new ValidateRuleExample();
    	criteria.createCriteria().andRuleGroupIdEqualTo(ruleGroupId);
    	return mapper.deleteByExample(criteria);
    }
    
    public void save(ValidateRule validateRule) {
    	if(validateRule.getId() == null || validateRule.getId().trim().length() == 0) {
    		validateRule.setId(KeyGenerate.uuidKey());
    	}
    	mapper.insert(validateRule);
    }
    
    public int updateById(ValidateRule validateRule) {
    	if(validateRule.getId() == null || validateRule.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(validateRule);
    }
    
    public List<ValidateRule> listBySystem(String systemId) {
    	ValidateRuleExample criteria = new ValidateRuleExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ValidateRule> list() {
    	return mapper.selectByExample(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(String ruleGroupId) {
        this.ruleGroupId = ruleGroupId == null ? null : ruleGroupId.trim();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public String getRuleTypeId() {
        return ruleTypeId;
    }

    public void setRuleTypeId(String ruleTypeId) {
        this.ruleTypeId = ruleTypeId == null ? null : ruleTypeId.trim();
    }

    public String getRuleValue1() {
        return ruleValue1;
    }

    public void setRuleValue1(String ruleValue1) {
        this.ruleValue1 = ruleValue1 == null ? null : ruleValue1.trim();
    }

    public String getRuleValue2() {
        return ruleValue2;
    }

    public void setRuleValue2(String ruleValue2) {
        this.ruleValue2 = ruleValue2 == null ? null : ruleValue2.trim();
    }

    public String getRuleValue3() {
        return ruleValue3;
    }

    public void setRuleValue3(String ruleValue3) {
        this.ruleValue3 = ruleValue3 == null ? null : ruleValue3.trim();
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

	public ValidateRuleType getValidateRuleType() {
		return validateRuleType;
	}

	public void setValidateRuleType(ValidateRuleType validateRuleType) {
		this.validateRuleType = validateRuleType;
	}

	public String getSubAttributeId() {
		return subAttributeId;
	}

	public void setSubAttributeId(String subAttributeId) {
		this.subAttributeId = subAttributeId;
	}

	public String getAttributeCode() {
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public String getSubAttributeCode() {
		return subAttributeCode;
	}

	public void setSubAttributeCode(String subAttributeCode) {
		this.subAttributeCode = subAttributeCode;
	}
}