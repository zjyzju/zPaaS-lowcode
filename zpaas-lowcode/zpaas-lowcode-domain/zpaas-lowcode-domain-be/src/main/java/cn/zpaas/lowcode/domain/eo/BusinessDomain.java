package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.BusinessDomainMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class BusinessDomain {
    private String id;

    private String name;

    private String description;

    private String status;

    private String systemId;

    private String tenantId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private BusinessDomainMapper mapper;
    
    public void save(BusinessDomain businessDomain) {
    	if(businessDomain.getId() == null || businessDomain.getId().trim().length() == 0) {
    		businessDomain.setId(KeyGenerate.uuidKey());
    	}
    	mapper.insert(businessDomain);
    }
    
    public int updateById(BusinessDomain businessDomain) {
    	if(businessDomain.getId() == null || businessDomain.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(businessDomain);
    }
    
    public BusinessDomain byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<BusinessDomain> listBySystemId(String systemId) {
    	BusinessDomainExample criteria = new BusinessDomainExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
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

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
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