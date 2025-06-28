package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.SqlManagementMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class SqlManagement {
    private String id;

    private String code;

    private String description;

    private String sqlSentence;

    private String resourceType;

    private String resourceId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private DbSchema dbSchema;
    private ServerResource serverResource;
    
    @Autowired
    private SqlManagementMapper mapper;
    
    public int save(SqlManagement sqlManagement) {
    	if(sqlManagement.getId() == null || sqlManagement.getId().trim().length() == 0) {
    		sqlManagement.setId(KeyGenerate.uuidKey());
    	}
    	return this.mapper.insert(sqlManagement);
    }
    
    public int update(SqlManagement sqlManagement) {
    	return this.mapper.updateByPrimaryKey(sqlManagement);
    }
    
    public SqlManagement byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<SqlManagement> list() {
    	return mapper.selectByExample(null);
    }

    public List<SqlManagement> listBySystemId(String systemId) {
    	SqlManagementExample criteria = new SqlManagementExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<SqlManagement> list(String systemId, String resourceType, String resourceId) {
    	SqlManagementExample criteria = new SqlManagementExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andResourceTypeEqualTo(resourceType).andResourceIdEqualTo(resourceId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<SqlManagement> list(String systemId, String resourceId) {
    	SqlManagementExample criteria = new SqlManagementExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andResourceIdEqualTo(resourceId);
    	return mapper.selectByExample(criteria);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSqlSentence() {
        return sqlSentence;
    }

    public void setSqlSentence(String sqlSentence) {
        this.sqlSentence = sqlSentence == null ? null : sqlSentence.trim();
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

	public DbSchema getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(DbSchema dbSchema) {
		this.dbSchema = dbSchema;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public ServerResource getServerResource() {
		return serverResource;
	}

	public void setServerResource(ServerResource serverResource) {
		this.serverResource = serverResource;
	}
    
    
}