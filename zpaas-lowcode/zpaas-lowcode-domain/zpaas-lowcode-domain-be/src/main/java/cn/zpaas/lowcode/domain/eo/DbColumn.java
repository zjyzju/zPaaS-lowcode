package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.constant.KeyGenerateMethod;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.DbColumnMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
import cn.zpaas.lowcode.vo.MdaColumn;
@Repository
public class DbColumn {
	private static Logger logger = LoggerFactory.getLogger(DbColumn.class);
	private static final String COMMA = ",";
	private static final String VARCHAR = "VARCHAR";
	
    private String id;

    private String name;

    private String description;

    private String type;

    private String length;

    private String isPrimaryKey;

    private String isNull;

    private String keyGenerateMethod;

    private String tableId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private Integer displayOrder;
    
    @Autowired
    private DbColumnMapper mapper;
    
    /**
	 * 根据数据库的列信息自动生成DbColumn表的记录
	 * @param columnList
	 * @param table
	 * @throws EngineException
	 */
	public boolean generateColumns(List<MdaColumn> columnList, DbTable table)throws EngineException {
		logger.debug("generateColumns starting");
		
		if(CollectionUtils.isEmpty(columnList)) {
			logger.info("column list is empty!");
			return true;
		}
		Date now = new Date();
		
		int displayOrderCount = 1;
		for(MdaColumn row : columnList) {
			DbColumn column = new DbColumn();
			column.setName(row.getColumnName());
			column.setDescription(row.getRemarks());
			column.setType(row.getTypeName());
			int digits = row.getDecimalDigits();
			if(digits > 0) {
				column.setLength(String.valueOf(row.getColumnSize()) + COMMA + digits);
			}else {
				column.setLength(String.valueOf(row.getColumnSize()));
			}
			column.setIsNull(row.isNullable() ? YesOrNo.YES : YesOrNo.NO);
			column.setIsPrimaryKey(row.isPrimaryKey() ? YesOrNo.YES : YesOrNo.NO);
			if(row.isPrimaryKey() && VARCHAR.equalsIgnoreCase(column.getType()) && row.getColumnSize() >= 32) {
				column.setKeyGenerateMethod(KeyGenerateMethod.UUID);
			}
			column.setCreateTime(now);
			column.setUpdateTime(now);
			column.setSystemId(table.getSystemId());
			column.setTenantId(table.getTenantId());
			column.setTableId(table.getId());
			column.setDisplayOrder(displayOrderCount++);
			if(this.save(column) == null) {
                String jsonString = JsonUtils.toJson(column);
				logger.error("create DbColumn record failed: {}", jsonString);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "create DbColumn record failed!");
			}
		};
		logger.debug("generateColumns end");
		
		return true;
	}
    
    public DbColumn queryByColumnName(String systemId, String tableId, String columnName) {
    	DbColumnExample criteria = new DbColumnExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andTableIdEqualTo(tableId).andNameEqualTo(columnName);
    	List<DbColumn> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public DbColumn save(DbColumn dbColumn) {
    	if(dbColumn.getId() == null || dbColumn.getId().trim().length() == 0) {
    		dbColumn.setId(KeyGenerate.uuidKey());
    	}
    	if(mapper.insert(dbColumn) > 0) {
    		return dbColumn;
    	}else {
    		return null;
    	}
    }
    
    public DbColumn byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<DbColumn> getByTableId(String tableId) {
    	DbColumnExample criteria = new DbColumnExample();
    	criteria.createCriteria().andTableIdEqualTo(tableId);
    	criteria.setOrderByClause("display_order ");
    	return mapper.selectByExample(criteria);
    }
    
    public List<DbColumn> listBySystem(String systemId) {
    	DbColumnExample criteria = new DbColumnExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public List<DbColumn> list() {
    	DbColumnExample criteria = new DbColumnExample();
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public int updateById(DbColumn dbColumn) {
    	return mapper.updateByPrimaryKey(dbColumn);
    }
    
    public int delete(String id) {
    	return mapper.deleteByPrimaryKey(id);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length == null ? null : length.trim();
    }

    public String getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey == null ? null : isPrimaryKey.trim();
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull == null ? null : isNull.trim();
    }

    public String getKeyGenerateMethod() {
        return keyGenerateMethod;
    }

    public void setKeyGenerateMethod(String keyGenerateMethod) {
        this.keyGenerateMethod = keyGenerateMethod == null ? null : keyGenerateMethod.trim();
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId == null ? null : tableId.trim();
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}