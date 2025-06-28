package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.ColumnMappingMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ColumnMapping {
	private String id;

    private String attributeId;

    private String columnId;

    private String objectRelationMappingId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private Attribute relAttribute;
    
    private DbColumn relDbColumn;
    
    @Autowired
    private ColumnMappingMapper mapper;
    
    @Autowired
    private DbColumn dbColumn;
    
    @Autowired
    private Attribute attribute;
    
    
    
    public ColumnMapping save(ColumnMapping columnMapping) {
    	if(columnMapping.getId() == null || columnMapping.getId().trim().length() == 0) {
    		columnMapping.setId(KeyGenerate.uuidKey());
    	}
    	if( mapper.insert(columnMapping) > 0) {
    		return columnMapping;
    	}else {
    		return null;
    	}
    }
    
    public ColumnMapping byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public int deleteByormId(String ormId) {
    	ColumnMappingExample criteria = new ColumnMappingExample();
    	criteria.createCriteria().andObjectRelationMappingIdEqualTo(ormId);
    	return mapper.deleteByExample(criteria);
    	
    }
    
    public List<ColumnMapping> getListByormId(String ormId) {
    	ColumnMappingExample criteria = new ColumnMappingExample();
    	criteria.createCriteria().andObjectRelationMappingIdEqualTo(ormId);
    	List<ColumnMapping> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
    		for(ColumnMapping mapping : list) {
    			mapping.setRelAttribute(attribute.byId(mapping.getAttributeId()));
    			mapping.setRelDbColumn(dbColumn.byId(mapping.getColumnId()));
    		}
    	}
    	return list;
    }
    
    public List<ColumnMapping> list() {
    	List<ColumnMapping> list = mapper.selectByExample(null);
    	if(list != null && list.size() > 0) {
			// 加载所有的属性信息
			List<Attribute> allAttributes = null;
			allAttributes = this.attribute.list();
			if (allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, Attribute> attributeMap = allAttributes.stream().collect(Collectors.toMap(Attribute::getId, (row)->row));
			
			// 加载所有的属性信息
			List<DbColumn> allDbColumns = null;
			allDbColumns = this.dbColumn.list();
			if (allDbColumns == null) {
				allDbColumns = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, DbColumn> dbColumnMap = allDbColumns.stream().collect(Collectors.toMap(DbColumn::getId, (row)->row));
			
    		for(ColumnMapping mapping : list) {
    			mapping.setRelAttribute(attributeMap.get(mapping.getAttributeId()));
    			mapping.setRelDbColumn(dbColumnMap.get(mapping.getColumnId()));
    		}
    	}
    	return list;
    }
    
    public List<ColumnMapping> listBySystem(String systemId) {
    	ColumnMappingExample criteria = new ColumnMappingExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	List<ColumnMapping> list = mapper.selectByExample(criteria);
    	if(list != null && list.size() > 0) {
			// 加载所有的属性信息
			List<Attribute> allAttributes = null;
			allAttributes = this.attribute.listBySystem(systemId);
			if (allAttributes == null) {
				allAttributes = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, Attribute> attributeMap = allAttributes.stream().collect(Collectors.toMap(Attribute::getId, (row)->row));
			
			// 加载所有的属性信息
			List<DbColumn> allDbColumns = null;
			allDbColumns = this.dbColumn.listBySystem(systemId);
			if (allDbColumns == null) {
				allDbColumns = new ArrayList<>();
			}
			// 按归属的对象进行分组，形成Map
			Map<String, DbColumn> dbColumnMap = allDbColumns.stream().collect(Collectors.toMap(DbColumn::getId, (row)->row));
			
    		for(ColumnMapping mapping : list) {
    			mapping.setRelAttribute(attributeMap.get(mapping.getAttributeId()));
    			mapping.setRelDbColumn(dbColumnMap.get(mapping.getColumnId()));
    		}
    	}
    	return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId == null ? null : columnId.trim();
    }

    public String getObjectRelationMappingId() {
        return objectRelationMappingId;
    }

    public void setObjectRelationMappingId(String objectRelationMappingId) {
        this.objectRelationMappingId = objectRelationMappingId == null ? null : objectRelationMappingId.trim();
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

	public Attribute getRelAttribute() {
		return relAttribute;
	}

	public void setRelAttribute(Attribute relAttribute) {
		this.relAttribute = relAttribute;
	}

	public DbColumn getRelDbColumn() {
		return relDbColumn;
	}

	public void setRelDbColumn(DbColumn relDbColumn) {
		this.relDbColumn = relDbColumn;
	}
    
    
}