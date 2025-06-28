package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DbSchemaMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
import cn.zpaas.lowcode.utils.StringUtils;
@Repository
public class DbSchema {
    public static final String PWD_MARK_STR = "******";//密码隐藏字符

    private String id;

    private String name;

    private String description;

    private String status;
    
    private String dbType;

    private String username;

    private String password;

    private String url;
    
    private String driverClass;
    
    private String dataSourceCfg;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<DbTable> dbTables;
    
    @Autowired
    private DbSchemaMapper mapper;
    
    public DbSchema save(DbSchema dbSchema) {
    	if(dbSchema.getId() == null || dbSchema.getId().trim().length() == 0) {
    		dbSchema.setId(KeyGenerate.uuidKey());
    	}
    	
    	if(mapper.insert(dbSchema) > 0) {
    		return dbSchema;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(DbSchema dbSchema) {
        if(!StringUtils.isBlank(dbSchema.getPassword()) && PWD_MARK_STR.equals(dbSchema.getPassword())) {
            DbSchema originDbSchema = this.byId(dbSchema.getId());
            dbSchema.setPassword(originDbSchema.getPassword());
        }
    	return mapper.updateByPrimaryKey(dbSchema);
    }
    
    public DbSchema byId(String dbSchemaId) {
    	return mapper.selectByPrimaryKey(dbSchemaId);
    }
    
    public List<DbSchema> list() {
    	DbSchemaExample criteria = new DbSchemaExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	return mapper.selectByExample(criteria);
    }
    
    public List<DbSchema> listBySystemId(String systemId) {
    	DbSchemaExample criteria = new DbSchemaExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }
    
    public int delete(String id) {
    	DbSchema object = mapper.selectByPrimaryKey(id);
    	if(object != null) {
    		object.setStatus(Status.EXP);
    		object.setUpdateTime(new Date());
    		DbSchemaExample criteria = new DbSchemaExample();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
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

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDataSourceCfg() {
		return dataSourceCfg;
	}

	public void setDataSourceCfg(String dataSourceCfg) {
		this.dataSourceCfg = dataSourceCfg;
	}

	public List<DbTable> getDbTables() {
		return dbTables;
	}

	public void setDbTables(List<DbTable> dbTables) {
		this.dbTables = dbTables;
	}
	
	
}