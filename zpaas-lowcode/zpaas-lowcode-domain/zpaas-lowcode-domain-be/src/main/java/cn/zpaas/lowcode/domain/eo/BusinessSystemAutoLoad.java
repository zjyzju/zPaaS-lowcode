package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.BusinessSystemAutoLoadMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class BusinessSystemAutoLoad {
    private String id;

    private String autoLoadService;

    private String autoLoadMethod;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private BusinessSystemAutoLoadMapper mapper;
    
    public List<BusinessSystemAutoLoad> list() {
    	return mapper.selectByExample(null);
    }
    
    public BusinessSystemAutoLoad queryBySystem(String systemId) {
    	BusinessSystemAutoLoadExample criteria = new BusinessSystemAutoLoadExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	List<BusinessSystemAutoLoad> result = mapper.selectByExample(criteria);
    	if(result == null || result.isEmpty()) {
    		return null;
    	}else {
    		return result.get(0);
    	}
    }
    
    public BusinessSystemAutoLoad save(BusinessSystemAutoLoad businessSystemAutoLoad) {
        if(businessSystemAutoLoad == null) {
            return null;
        }

    	if(businessSystemAutoLoad.getId() == null || businessSystemAutoLoad.getId().trim().length() == 0) {
    		businessSystemAutoLoad.setId(KeyGenerate.uuidKey());
    	}
    	
    	Date nowDate = new Date();
    	businessSystemAutoLoad.setCreateTime(nowDate);
    	businessSystemAutoLoad.setUpdateTime(nowDate);
    	
    	if(mapper.insert(businessSystemAutoLoad) > 0) {
    		return businessSystemAutoLoad;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(BusinessSystemAutoLoad businessSystemAutoLoad) {
    	Date nowDate = new Date();
    	businessSystemAutoLoad.setUpdateTime(nowDate);
    	
    	return mapper.updateByPrimaryKey(businessSystemAutoLoad);
    }
    
    public BusinessSystemAutoLoad saveOrUpdate(BusinessSystemAutoLoad businessSystemAutoLoad) {
    	if(businessSystemAutoLoad != null && (businessSystemAutoLoad.getId() == null || businessSystemAutoLoad.getId().trim().length() == 0)) {
    		return this.save(businessSystemAutoLoad);
    	}else {
    		if(this.updateById(businessSystemAutoLoad) > 0) {
    			return businessSystemAutoLoad;
    		}else {
    			return null;
    		}
    	}
    }
    
   public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAutoLoadService() {
        return autoLoadService;
    }

    public void setAutoLoadService(String autoLoadService) {
        this.autoLoadService = autoLoadService == null ? null : autoLoadService.trim();
    }

    public String getAutoLoadMethod() {
        return autoLoadMethod;
    }

    public void setAutoLoadMethod(String autoLoadMethod) {
        this.autoLoadMethod = autoLoadMethod == null ? null : autoLoadMethod.trim();
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