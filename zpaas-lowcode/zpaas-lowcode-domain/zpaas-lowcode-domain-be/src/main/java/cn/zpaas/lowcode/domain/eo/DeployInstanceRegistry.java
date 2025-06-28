package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.domain.mapper.DeployInstanceRegistryMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DeployInstanceRegistry {
    private String id;

    private String address;

    private Integer port;

    private String contextPath;

    private String isHttps;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DeployInstanceRegistryMapper mapper;

    public DeployInstanceRegistry byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public DeployInstanceRegistry loadByAddrAndPort(String address, int port) {
        DeployInstanceRegistryExample criteria = new DeployInstanceRegistryExample();
        criteria.createCriteria().andAddressEqualTo(address).andPortEqualTo(port);
        List<DeployInstanceRegistry> deployInstanceRegistries = mapper.selectByExample(criteria);
        if(CollectionUtils.isEmpty(deployInstanceRegistries)) {
            return null;
        }else {
            return deployInstanceRegistries.getFirst();
        }
    }

    public List<DeployInstanceRegistry> loadNotTimeout(Long timeout) {
        Date effectiveTime = new Date(System.currentTimeMillis()-timeout);
        DeployInstanceRegistryExample criteria = new DeployInstanceRegistryExample();
        criteria.createCriteria().andUpdateTimeGreaterThan(effectiveTime);
        return mapper.selectByExample(criteria);
    }

    public int create(DeployInstanceRegistry deployInstanceRegistry) {
        deployInstanceRegistry.setId(KeyGenerate.uuidKey());
        Date nowDate = new Date();
        deployInstanceRegistry.setCreateTime(nowDate);
        deployInstanceRegistry.setUpdateTime(nowDate);
        return mapper.insert(deployInstanceRegistry);
    }

    public int save(DeployInstanceRegistry deployInstanceRegistry) {
        Date nowDate = new Date();
        deployInstanceRegistry.setUpdateTime(nowDate);
        return mapper.updateByPrimaryKey(deployInstanceRegistry);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath == null ? null : contextPath.trim();
    }

    public String getIsHttps() {
        return isHttps;
    }

    public void setIsHttps(String isHttps) {
        this.isHttps = isHttps;
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