package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ThirdpartySystemMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ThirdpartySystem {
    private String id;

    private String name;

    private String accessKey;

    private String accessSecret;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private ThirdpartySystemMapper mapper;
    
    public ThirdpartySystem save(ThirdpartySystem thirdpartySystem) {
    	if(thirdpartySystem.getId() == null || thirdpartySystem.getId().trim().length() ==0) {
    		thirdpartySystem.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	thirdpartySystem.setCreateTime(nowDate);
    	thirdpartySystem.setUpdateTime(nowDate);
    	thirdpartySystem.setStatus(Status.EFF);
    	if(mapper.insert(thirdpartySystem) > 0) {
    		return thirdpartySystem;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(ThirdpartySystem thirdpartySystem) {
    	thirdpartySystem.setUpdateTime(new Date());
    	return mapper.updateByPrimaryKey(thirdpartySystem);
    }
    
    public ThirdpartySystem queryByAccessKey(String accessKey, String systemId) {
    	ThirdpartySystemExample criteria = new ThirdpartySystemExample();
    	criteria.createCriteria().andAccessKeyEqualTo(accessKey).andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	List<ThirdpartySystem> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public List<ThirdpartySystem> listBySystem(String systemId) {
    	ThirdpartySystemExample criteria = new ThirdpartySystemExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ThirdpartySystem> list() {
    	ThirdpartySystemExample criteria = new ThirdpartySystemExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public ThirdpartySystem byId(String id) {
    	return mapper.selectByPrimaryKey(id);
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

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey == null ? null : accessKey.trim();
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret == null ? null : accessSecret.trim();
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