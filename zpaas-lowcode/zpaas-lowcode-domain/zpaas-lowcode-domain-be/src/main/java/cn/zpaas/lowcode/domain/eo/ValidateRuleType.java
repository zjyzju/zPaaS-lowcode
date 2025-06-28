package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ValidateRuleTypeMapper;
@Repository
public class ValidateRuleType {
    private String id;

    private String name;

    private String description;

    private String status;

    private String ruleClass;

    private String cfgRequirement;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private ValidateRuleTypeMapper mapper;
    
    public ValidateRuleType byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<ValidateRuleType> list() {
    	ValidateRuleTypeExample criteria = new ValidateRuleTypeExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getRuleClass() {
        return ruleClass;
    }

    public void setRuleClass(String ruleClass) {
        this.ruleClass = ruleClass == null ? null : ruleClass.trim();
    }

    public String getCfgRequirement() {
        return cfgRequirement;
    }

    public void setCfgRequirement(String cfgRequirement) {
        this.cfgRequirement = cfgRequirement == null ? null : cfgRequirement.trim();
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
}