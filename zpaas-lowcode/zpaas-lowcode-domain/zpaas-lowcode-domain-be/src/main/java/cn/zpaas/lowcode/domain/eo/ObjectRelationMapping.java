package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.ObjectRelationMappingMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ObjectRelationMapping {
	private String id;

	private String name;

	private String domainObjectId;

	private String tableId;

	private String status;

	private String tenantId;

	private String systemId;

	private Date createTime;

	private Date updateTime;
	
	private List<ColumnMapping> columnMappings;
	
	private DomainObject relDomainObject;
	
	private DbTable relDbTable;
	
	@Autowired
	private ObjectRelationMappingMapper mapper;
	
	@Autowired
	private ColumnMapping columnMapping;
	
	
	public ObjectRelationMapping byId(String id) {
		ObjectRelationMapping orm = null;
		orm = mapper.selectByPrimaryKey(id);
		orm.setColumnMappings(columnMapping.getListByormId(id));
		return orm;
	}
	
	public List<ObjectRelationMapping> listByTableId(String dbTableId) {
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andTableIdEqualTo(dbTableId).andStatusEqualTo(Status.EFF);
		List<ObjectRelationMapping> mappings = mapper.selectByExample(criteria);
		if(mappings != null && mappings.size() > 0) {
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setColumnMappings(columnMapping.getListByormId(mapping.getId()));
			}
		}
		return mappings;
	}
	
	public List<ObjectRelationMapping> listByDomainObjectId(String domainObjectId) {
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andDomainObjectIdEqualTo(domainObjectId).andStatusEqualTo(Status.EFF);
		List<ObjectRelationMapping> mappings = mapper.selectByExample(criteria);
		if(mappings != null && mappings.size() > 0) {
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setColumnMappings(columnMapping.getListByormId(mapping.getId()));
			}
		}
		return mappings;
	}
	
	public List<ObjectRelationMapping> list() {
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andStatusEqualTo(Status.EFF);
		List<ObjectRelationMapping> mappings = mapper.selectByExample(criteria);
		if(mappings != null && mappings.size() > 0) {
			List<ColumnMapping> allColumnMappings = this.columnMapping.list();
			if(allColumnMappings == null) {
				allColumnMappings = new ArrayList<>();
			}
			Map<String, List<ColumnMapping>> columnMappingMap = allColumnMappings.stream().collect(Collectors.groupingBy(ColumnMapping::getObjectRelationMappingId));
			
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setColumnMappings(columnMappingMap.get(mapping.getId()));
			}
		}
		return mappings;
	}
	
	public List<ObjectRelationMapping> listBySystem(String systemId) {
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
		List<ObjectRelationMapping> mappings = mapper.selectByExample(criteria);
		if(mappings != null && mappings.size() > 0) {
			List<ColumnMapping> allColumnMappings = this.columnMapping.listBySystem(systemId);
			if(allColumnMappings == null) {
				allColumnMappings = new ArrayList<>();
			}
			Map<String, List<ColumnMapping>> columnMappingMap = allColumnMappings.stream().collect(Collectors.groupingBy(ColumnMapping::getObjectRelationMappingId));
			for(ObjectRelationMapping mapping : mappings) {
				mapping.setColumnMappings(columnMappingMap.get(mapping.getId()));
			}
		}
		return mappings;
	}
	
	public ObjectRelationMapping save(ObjectRelationMapping objectRelationMapping) throws EngineException{
		if(objectRelationMapping.getId() == null || objectRelationMapping.getId().trim().length() == 0) {
			objectRelationMapping.setId(KeyGenerate.uuidKey());
		}
		if( mapper.insert(objectRelationMapping) > 0) {
			if(objectRelationMapping.getColumnMappings() != null && objectRelationMapping.getColumnMappings().size() > 0) {
				for(ColumnMapping column : objectRelationMapping.getColumnMappings()){
					column.setObjectRelationMappingId(objectRelationMapping.getId());
					column.setCreateTime(objectRelationMapping.getCreateTime());
					column.setUpdateTime(objectRelationMapping.getCreateTime());
					if(this.columnMapping.save(column) == null) {
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "add ColumnMapping failed!");
					}
				}
			}
			return objectRelationMapping;
		}else {
			return null;
		}
	}
	
	public ObjectRelationMapping update(ObjectRelationMapping objectRelationMapping) throws EngineException{
		if( mapper.updateByPrimaryKey(objectRelationMapping) > 0) {
			this.columnMapping.deleteByormId(objectRelationMapping.getId());
			if(objectRelationMapping.getColumnMappings() != null && objectRelationMapping.getColumnMappings().size() > 0) {
				for(ColumnMapping column : objectRelationMapping.getColumnMappings()){
					column.setObjectRelationMappingId(objectRelationMapping.getId());
					if(column.getId() == null && column.getId().trim().length() == 0) {
						column.setCreateTime(objectRelationMapping.getCreateTime());
					}
					column.setUpdateTime(objectRelationMapping.getUpdateTime());
					if(this.columnMapping.save(column) == null) {
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "add ColumnMapping failed!");
					}
				}
			}
			return objectRelationMapping;
		}else {
			return null;
		}
	}
	
	public int deleteByDomainObjectId(String domainObjectId) {
		ObjectRelationMapping orm = new ObjectRelationMapping();
		orm.setStatus(Status.EXP);
		orm.setUpdateTime(new Date());
		
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andDomainObjectIdEqualTo(domainObjectId).andStatusEqualTo(Status.EFF);
		
		return mapper.updateByExampleSelective(orm, criteria);
	}
	
	public int deleteByTableId(String tableId) {
		ObjectRelationMapping orm = new ObjectRelationMapping();
		orm.setStatus(Status.EXP);
		orm.setUpdateTime(new Date());
		
		ObjectRelationMappingExample criteria = new ObjectRelationMappingExample();
		criteria.createCriteria().andTableIdEqualTo(tableId).andStatusEqualTo(Status.EFF);
		
		return mapper.updateByExampleSelective(orm, criteria);
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

	public String getDomainObjectId() {
		return domainObjectId;
	}

	public void setDomainObjectId(String domainObjectId) {
		this.domainObjectId = domainObjectId == null ? null : domainObjectId.trim();
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId == null ? null : tableId.trim();
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

	public List<ColumnMapping> getColumnMappings() {
		return columnMappings;
	}

	public void setColumnMappings(List<ColumnMapping> columnMappings) {
		this.columnMappings = columnMappings;
	}

	public DomainObject getRelDomainObject() {
		return relDomainObject;
	}

	public void setRelDomainObject(DomainObject relDomainObject) {
		this.relDomainObject = relDomainObject;
	}

	public DbTable getRelDbTable() {
		return relDbTable;
	}

	public void setRelDbTable(DbTable relDbTable) {
		this.relDbTable = relDbTable;
	}
	
	
}