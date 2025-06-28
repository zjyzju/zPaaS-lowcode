package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.vo.MdaTable;

/**
 * 对象关系映射领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:23:43
 */
@Service
public class ObjectRelationMappingService {
    private Logger logger = LoggerFactory.getLogger(ObjectRelationMappingService.class);

    @Autowired
    private ObjectRelationMapping objectRelationMappingSV;//对象关系映射领域对象

    @Autowired
    private DbTable dbTableSV;//数据库表领域对象

    @Autowired
    private ColumnMappingService columnMappingService;//属性/列映射领域服务

    /**
	 * 生成ORM数据
	 * @param dmObject
	 * @param dbTable
	 * @return
	 * @throws EngineException
	 */
	public ObjectRelationMapping generateObjectRelationMapping(DomainObject dmObject, MdaTable table) throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("generateObjectRelationMapping starting");
		}
		if(dmObject == null || table == null) {
			logger.error("domainObject or dbTable is null!");
			return null;
		}
		DbTable dTable = dbTableSV.queryByTableName(dmObject.getSystemId(), table.getTableName());
		if(dTable == null) {
			logger.error("not found table: {}", table.getTableName());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "not found DbTable: " + table.getTableName());
		}
		
		ObjectRelationMapping orm = new ObjectRelationMapping();
		orm.setDomainObjectId(dmObject.getId());
		orm.setTableId(dTable.getId());
		orm.setName(dmObject.getCode() + ":" + dTable.getName());
		orm.setStatus(Status.EFF);
		orm.setSystemId(dmObject.getSystemId());
		orm.setTenantId(dmObject.getTenantId());
		Date nowDate = new Date();
		orm.setCreateTime(nowDate);
		orm.setUpdateTime(nowDate);
		
		if(objectRelationMappingSV.save(orm)!= null) {
			List<MdaColumn> columns = table.getTableColumns();
			if(dmObject.getAttributes() != null && dmObject.getAttributes().size() > 0 && columns != null) {
				if(dmObject.getAttributes().size() != columns.size()) {
					logger.error("the size of attributes and columns must be equal.");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "the size of attributes and columns must be equal.");
				}
				int size = columns.size();
				Attribute attribute = null;
				MdaColumn tableColumn = null;
				for(int i=0; i< size; i++) {
					attribute = dmObject.getAttributes().get(i);
					tableColumn =columns.get(i);
					if(columnMappingService.generateColumnMapping(orm, attribute, tableColumn) == null) {
						logger.error("generateColumnMapping failed!");
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "generate ObjectRelationMapping failed!");
					}
				}
			}	
			if(logger.isDebugEnabled()) {
				logger.debug("generateObjectRelationMapping end");
			}
			return orm;
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "generate ObjectRelationMapping failed!");
		}
	}
}
