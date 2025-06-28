package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.DbTableMapper;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.KeyGenerate;
import cn.zpaas.lowcode.vo.MdaTable;

@Repository
public class DbTable {
	private static Logger logger = LoggerFactory.getLogger(DbTable.class);
	
    private String id;

    private String name;

    private String description;

    private String status;

    private String schemaId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<DbColumn> dbColumns;
    
    private DbSchema dbSchema;
    
    @Autowired
    private DbTableMapper mapper;
    
    @Autowired
    private DbColumn dbColumn;
    
    
    /**
	 * 根据数据表及字段信息，自动生成DbTable以及DbColumn数据
	 * @param tableList
	 * @param dbSchema
	 * @throws EngineException
	 */
	public boolean generateTables(List<MdaTable> tableList, DbSchema dbSchema) throws EngineException{
		if(logger.isDebugEnabled()) {
			logger.debug("generateTables starting");
		}
		if(CollectionUtils.isEmpty(tableList)) {
			logger.info("table list is empty!");
			return true;
		}
		Date now = new Date();
		
		for(MdaTable row : tableList) {
			DbTable table = new DbTable();
			table.setName(row.getTableName());
			table.setDescription(row.getTableRemarks());
			table.setStatus(Status.EFF);
			table.setCreateTime(now);
			table.setUpdateTime(now);
			table.setSchemaId(dbSchema.getId());
			table.setSystemId(dbSchema.getSystemId());
			table.setTenantId(dbSchema.getTenantId());
			if(this.save(table) == null) {
				String jsonString = JsonUtils.toJson(table);
				logger.error("create DbTable record failed: {}", jsonString);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create DbTable record failed!");
			}			
			dbColumn.generateColumns(row.getTableColumns(), table);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("generateTables end");
		}
		return true;
	}
	
    
    public DbTable save(DbTable dbTable) {
    	if(dbTable.getId() == null || dbTable.getId().trim().length() == 0) {
    		dbTable.setId(KeyGenerate.uuidKey());
    	}
    	if(mapper.insert(dbTable) > 0) {
    		return dbTable;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(DbTable dbTable) {
    	return mapper.updateByPrimaryKey(dbTable);
    }
    
    public DbTable queryByTableName(String systemId, String tableName) {
    	DbTableExample criteria = new DbTableExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andNameEqualTo(tableName);
    	List<DbTable> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public DbTable byId(String id) {
    	DbTable table = mapper.selectByPrimaryKey(id);
    	table.setDbColumns(dbColumn.getByTableId(id));
    	return table;
    }
    
    
    public List<DbTable> list() {
    	DbTableExample criteria = new DbTableExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	List<DbTable> dbTables = mapper.selectByExample(criteria);
    	return dbTables;
    }
    
    public List<DbTable> listBySystem(String systemId) {
    	DbTableExample criteria = new DbTableExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	List<DbTable> dbTables = mapper.selectByExample(criteria);
    	return dbTables;
    }
    
    public List<DbTable> listByDbSchemaId(String dbSchemaId) {
    	DbTableExample criteria = new DbTableExample();
    	criteria.createCriteria().andSchemaIdEqualTo(dbSchemaId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	List<DbTable> dbTables = mapper.selectByExample(criteria);
    	return dbTables;
    }
    
    public int delete(String id) {
    	DbTable object = mapper.selectByPrimaryKey(id);
    	if(object != null) {
    		
    		object.setStatus(Status.EXP);
    		object.setUpdateTime(new Date());
    		DbTableExample criteria = new DbTableExample();
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

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId == null ? null : schemaId.trim();
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


	public List<DbColumn> getDbColumns() {
		return dbColumns;
	}


	public void setDbColumns(List<DbColumn> dbColumns) {
		this.dbColumns = dbColumns;
	}


	public DbSchema getDbSchema() {
		return dbSchema;
	}


	public void setDbSchema(DbSchema dbSchema) {
		this.dbSchema = dbSchema;
	}
}