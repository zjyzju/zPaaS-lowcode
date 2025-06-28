package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.UserBusinessSystemMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class UserBusinessSystem {
    private String id;

    private String userId;

    private String systemId;

    private String tenantId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private UserBusinessSystemMapper mapper;

    public List<UserBusinessSystem> loadByUserId(String userId) {
        UserBusinessSystemExample criteria = new UserBusinessSystemExample();
        criteria.createCriteria().andUserIdEqualTo(userId);
        return mapper.selectByExample(criteria);
    }

    public int deleteByUserId(String userId) {
        UserBusinessSystemExample criteria = new UserBusinessSystemExample();
        criteria.createCriteria().andUserIdEqualTo(userId);
        return mapper.deleteByExample(criteria);
    }

    public int add(UserBusinessSystem userBusinessSystem) {
        if(StringUtils.isBlank(userBusinessSystem.getId())) {
            userBusinessSystem.setId(KeyGenerate.uuidKey());
            Date nowDate = new Date();
            userBusinessSystem.setCreateTime(nowDate);
            userBusinessSystem.setUpdateTime(nowDate);
        }
        return mapper.insert(userBusinessSystem);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
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