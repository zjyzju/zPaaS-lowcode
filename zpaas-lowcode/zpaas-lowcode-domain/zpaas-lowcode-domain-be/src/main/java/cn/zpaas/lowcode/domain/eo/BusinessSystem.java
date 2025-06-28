package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.BusinessSystemMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class BusinessSystem {
    private String id;

    private String name;

    private String servletContext;

    private String description;

    private String systemIcon;

    private String version;

    private String status;

    private String tenantId;

    private Date createTime;

    private Date updateTime;
    
    private BusinessSystemAuth businessSystemAuth;
    
    private BusinessSystemAutoLoad businessSystemAutoLoad;
    
    @Autowired
    private BusinessSystemMapper mapper;
    
    public BusinessSystem byId(String id) {
		return mapper.selectByPrimaryKey(id);
	}
    
    public int updateById(BusinessSystem businessSystem) {
    	return mapper.updateByPrimaryKey(businessSystem);
    }
    
    public BusinessSystem save(BusinessSystem businessSystem) {
    	if(businessSystem.getId() == null || businessSystem.getId().trim().length() == 0) {
    		businessSystem.setId(KeyGenerate.uuidKey());
    	}
		if(mapper.insert(businessSystem)> 0) {
			return businessSystem;
		}else {
			return null;
		}
	}
    
    public void delete() {
    	this.status = Status.EXP;
		mapper.updateByPrimaryKey(this);
	}
    
    public List<BusinessSystem> list() {
    	BusinessSystemExample example = new BusinessSystemExample();
    	example.createCriteria().andStatusEqualTo("1");
		return mapper.selectByExample(example);
	}

    public List<BusinessSystem> queryByServletContext(String servletContext) {
    	BusinessSystemExample example = new BusinessSystemExample();
    	example.createCriteria().andStatusEqualTo("1").andServletContextEqualTo(servletContext);
		return mapper.selectByExample(example);
	}

    public List<BusinessSystem> queryByServletContext(String servletContext, String businessSystemId) {
    	BusinessSystemExample example = new BusinessSystemExample();
    	example.createCriteria().andStatusEqualTo("1").andServletContextEqualTo(servletContext).andIdNotEqualTo(businessSystemId);
		return mapper.selectByExample(example);
	}

    public List<BusinessSystem> listByTenant(String tenantId) {
    	BusinessSystemExample example = new BusinessSystemExample();
    	example.createCriteria().andTenantIdEqualTo(tenantId).andStatusEqualTo("1");
		return mapper.selectByExample(example);
	}

    public List<BusinessSystem> listByIds(String tenantId, List<String> systemIds) {
    	BusinessSystemExample example = new BusinessSystemExample();
    	example.createCriteria().andTenantIdEqualTo(tenantId).andStatusEqualTo("1").andIdIn(systemIds);
		return mapper.selectByExample(example);
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

    public String getServletContext() {
        return servletContext;
    }

    public void setServletContext(String servletContext) {
        this.servletContext = servletContext == null ? null : servletContext.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSystemIcon() {
        return systemIcon;
    }

    public void setSystemIcon(String systemIcon) {
        this.systemIcon = systemIcon;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
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

	public BusinessSystemAuth getBusinessSystemAuth() {
		return businessSystemAuth;
	}

	public void setBusinessSystemAuth(BusinessSystemAuth businessSystemAuth) {
		this.businessSystemAuth = businessSystemAuth;
	}

	public BusinessSystemAutoLoad getBusinessSystemAutoLoad() {
		return businessSystemAutoLoad;
	}

	public void setBusinessSystemAutoLoad(BusinessSystemAutoLoad businessSystemAutoLoad) {
		this.businessSystemAutoLoad = businessSystemAutoLoad;
	}
}