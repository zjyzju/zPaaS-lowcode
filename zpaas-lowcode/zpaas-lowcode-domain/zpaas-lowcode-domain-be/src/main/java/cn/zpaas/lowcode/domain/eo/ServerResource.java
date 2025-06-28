package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ServerResourceMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
import cn.zpaas.lowcode.utils.StringUtils;

@Repository
public class ServerResource {
    public static final String PWD_MARK_STR = "******";//密码隐藏字符

    private String id;

    private String name;

    private String description;

    private String status;

    private String resourceType;

    private String username;

    private String password;

    private String host;

    private String port;

    private String url;

    private String serverCfg;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private ServerResourceMapper mapper;
    
    public ServerResource byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<ServerResource> list(String systemId, String resourceType) {
    	ServerResourceExample criteria = new ServerResourceExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andResourceTypeEqualTo(resourceType).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ServerResource> listBySystem(String systemId) {
    	ServerResourceExample criteria = new ServerResourceExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }
    
    public List<ServerResource> list(String resourceType) {
    	ServerResourceExample criteria = new ServerResourceExample();
    	criteria.createCriteria().andResourceTypeEqualTo(resourceType).andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<ServerResource> list() {
    	ServerResourceExample criteria = new ServerResourceExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public ServerResource save(ServerResource serverResource) {
    	if(serverResource.getId() == null || serverResource.getId().trim().length() == 0) {
    		serverResource.setId(KeyGenerate.uuidKey());
    	}
    	if(mapper.insert(serverResource) > 0) {
    		return serverResource;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(ServerResource serverResource) {
        if(!StringUtils.isBlank(serverResource.getPassword()) && PWD_MARK_STR.equals(serverResource.getPassword())) {
            ServerResource originServerResource = this.byId(serverResource.getId());
            serverResource.setPassword(originServerResource.getPassword());
        }
        
    	return mapper.updateByPrimaryKey(serverResource);
    }
    
    public int delete(String id) {
    	ServerResource object = mapper.selectByPrimaryKey(id);
    	if(object != null) {
    		object.setStatus(Status.EXP);
    		object.setUpdateTime(new Date());
    		ServerResourceExample criteria = new ServerResourceExample();
    		criteria.createCriteria().andIdEqualTo(id).andStatusEqualTo(Status.EFF);
    		return mapper.updateByExample(object, criteria);
    	}else {
    		return 0;
    	}
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? null : resourceType.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port == null ? null : port.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getServerCfg() {
        return serverCfg;
    }

    public void setServerCfg(String serverCfg) {
        this.serverCfg = serverCfg == null ? null : serverCfg.trim();
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