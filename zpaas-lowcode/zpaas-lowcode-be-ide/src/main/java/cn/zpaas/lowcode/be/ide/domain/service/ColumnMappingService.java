package cn.zpaas.lowcode.be.ide.domain.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.ColumnMapping;
import cn.zpaas.lowcode.domain.eo.DbColumn;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.MdaColumn;

/**
 * 属性/列映射领域服务
 *
 * @author zjy
 * createTime 2025年04月28日-10:28:19
 */
@Service
public class ColumnMappingService {
    private Logger logger = LoggerFactory.getLogger(ColumnMappingService.class);

    @Autowired
    private ColumnMapping columnMappingSV;//属性/列映射领域对象

    @Autowired
    private DbColumn dbColumnSV;//数据库表字段领域对象

    /**
	 * 生成ColumnMapping数据
	 * @param ormId
	 * @param attr
	 * @param column
	 * @return
	 * @throws EngineException
	 */
	public ColumnMapping generateColumnMapping(ObjectRelationMapping orm, Attribute attr, MdaColumn column) throws EngineException {	
		if(logger.isDebugEnabled()) {
			logger.debug("generateColumnMapping starting");
		}
		DbColumn dbCol = dbColumnSV.queryByColumnName(attr.getSystemId(), orm.getTableId(), column.getColumnName());
		if(dbCol == null) {
			logger.error("not found dbColumn: {}", column.getColumnName());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "not found dbColumn: " + column.getColumnName());
		}
		
		ColumnMapping cm = new ColumnMapping();
		cm.setAttributeId(attr.getId());
		cm.setColumnId(dbCol.getId());
		cm.setObjectRelationMappingId(orm.getId());
		cm.setSystemId(orm.getSystemId());
		cm.setTenantId(orm.getTenantId());
		Date nowDate = new Date();
		cm.setCreateTime(nowDate);
		cm.setUpdateTime(nowDate);
		
		if(columnMappingSV.save(cm) != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("generateColumnMapping end");
			}
			return cm;
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "generate ColumnMapping failed!");
		}
	}
}
