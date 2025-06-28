package cn.zpaas.lowcode.be.ide.ability;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.be.ide.domain.service.DomainObjectService;
import cn.zpaas.lowcode.be.ide.domain.service.ObjectRelationMappingService;
import cn.zpaas.lowcode.be.ide.domain.service.ValueObjectService;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.exception.EngineException;


/**
 * @author zjy
 * 模型驱动开发的能力提供类
 */
@Component
public class ModelDrivenAbility {
	private static Logger logger = LoggerFactory.getLogger(ModelDrivenAbility.class);
	
	//表信息
	private static final String TABLE_CAT = "TABLE_CAT"; //table catalog
	private static final String TABLE_NAME = "TABLE_NAME";//表名
	private static final String TABLE_SCHEM = "TABLE_SCHEM";//table schema
	private static final String TABLE_TYPE = "TABLE_TYPE";//表类型， TABLE/VIEW
	private static final String TABLE_REMARKS = "REMARKS";//备注
	
	//字段信息
	private static final String DATA_TYPE = "DATA_TYPE";//数据类型，SqlType，int类型
	private static final String TYPE_NAME = "TYPE_NAME";//类型名称
	private static final String COLUMN_SIZE = "COLUMN_SIZE";//字段长度
	private static final String COLUMN_NAME = "COLUMN_NAME";//字段名称
	private static final String NULLABLE = "NULLABLE";//是否可空
	private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";//小数位数
	private static final String REMARKS = "REMARKS";//备注
	private static final String COLUMN_DEF = "COLUMN_DEF";//默认值	
	
	//主键信息
	public static final String KEY_SEQ = "KEY_SEQ";//主键的顺序	
	
	@Autowired
	private DbTable dbTable;//DBTable实体对象
	
	@Autowired
	private DataMapping dataMapping;//数据映射领域对象

	@Autowired
	private DomainObjectService domainObjectService;//领域对象领域服务

	@Autowired
	private ValueObjectService valueObjectService;//值传递对象领域方法

	@Autowired
	private ObjectRelationMappingService objectRelationMappingService;//对象关系映射领域服务
	
	
	/**
	 * 根据表结构信息，生成领域对象以及关联的一系列信息
	 * @param tableList 表列表，包括字段信息
	 * @param domain 生成实体对象归属的业务域
	 * @return
	 */
	public List<DomainObject> generateEntityObject(List<MdaTable> tableList, BusinessDomain domain, JsonArray ignorePrefixes, String dbSchemaId) throws EngineException{
		if(logger.isDebugEnabled()) {
			logger.debug("generateEntityObject starting");
		}
		List<DomainObject> result = domainObjectService.generateEntityObject(tableList, domain, ignorePrefixes, dbSchemaId);
		if(logger.isDebugEnabled()) {
			logger.debug("generateEntityObject end");
		}
		return result;
	}
	
	/**
	 * 根据表结构信息，生成值传递对象以及关联的属性信息
	 * @param tableList 表列表，包括字段信息
	 * @param domain 生成实体对象归属的业务域
	 * @return
	 */
	public List<ValueObject> generateValueObject(List<MdaTable> tableList, BusinessDomain domain, JsonArray ignorePrefixes, String dbSchemaId) throws EngineException{
		if(logger.isDebugEnabled()) {
			logger.debug("generateValueObject starting");
		}
		List<ValueObject> result = valueObjectService.generateValueObject(tableList, domain, ignorePrefixes, dbSchemaId);
		if(logger.isDebugEnabled()) {
			logger.debug("generateValueObject end");
		}
		return result;
	}
	
	/**
     * 生成领域对象和值传递对象的数据映射规则，两个列表的顺序和数量必须都相同
     * @param domainObjects
     * @param valueObjects
     * @return
     */
    public boolean generateDataMappings(List<DomainObject> domainObjects, List<ValueObject> valueObjects) throws EngineException{
    	if(logger.isDebugEnabled()) {
    		logger.debug("generateDataMappings starting");
    	}
    	
    	if(!dataMapping.generateDataMappings(domainObjects, valueObjects)) {
    		logger.error("generateDataMappings failed!");
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateDataMappings failed!");
    	}
    	
    	if(logger.isDebugEnabled()) {
			logger.debug("generateDataMappings end");
		}
    	return true;
    }
	
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
		boolean result = dbTable.generateTables(tableList, dbSchema);
		
		if(logger.isDebugEnabled()) {
			logger.debug("generateTables end");
		}
		return result;
	}
	
	/**
	 * 批量生成orm数据，两个列表的数据顺序要一致，数量要相同
	 * @param domainObjects
	 * @param tableList
	 * @return
	 * @throws EngineException
	 */
	public boolean generateObjectRelationMappings(List<DomainObject> domainObjects, List<MdaTable> tables) throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("generateObjectRelationMappings starting");
		}
		if(domainObjects == null || tables == null || domainObjects.size() == 0 || tables.size() == 0) {
			logger.info("domainObjects or tables is null!");
			return true;
		}
		if(domainObjects.size() != tables.size()) {
			logger.error("the size of domainObjects and tables must be equal.");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "the size of domainObjects and tables must be equal.");
		}
		int size = domainObjects.size();
		DomainObject dmObject = null;
		MdaTable table = null;
		for(int i=0; i< size; i++) {
			dmObject = domainObjects.get(i);
			table = tables.get(i);
			
			if(objectRelationMappingService.generateObjectRelationMapping(dmObject, table) == null) {
				logger.error("generateObjectRelationMapping failed!");
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "generateObjectRelationMapping failed!");
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("generateObjectRelationMappings end");
		}
		return true;
	}
	
	/**
	 * 获取数据库表和视图的列表
	 * @param ds 数据源
	 * @param dbSchemaName 数据库名
	 * @param fetchColumn 是否加载列信息
	 * @return 表的列表
	 * @throws EngineException
	 */
	public List<MdaTable> queryTableList(DataSource ds, String dbSchemaName, boolean fetchColumn) throws EngineException {
		Connection connection = null;
		List<MdaTable> tables =  null;
		try {
			connection = ds.getConnection();
			tables = this.queryTableList(connection, dbSchemaName, null, fetchColumn);
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "query table list from db failed!", null, e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tables;		
	}
	
	/**
	 * 获取数据库表和视图的列表
	 * @param ds 数据源
	 * @param dbSchemaName 数据库名
	 * @param fetchColumn 是否加载列信息
	 * @return 表的列表
	 * @throws EngineException
	 */
	private List<MdaTable> queryTableList(Connection connection, String dbSchemaName, String tableNamePattern, boolean fetchColumn) throws EngineException {
		ResultSet resultSet = null;
		List<MdaTable> tables =  new ArrayList<>();
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			
			resultSet = metaData.getTables(dbSchemaName, dbSchemaName, tableNamePattern, new String[] {"TABLE","VIEW"});			
			
			MdaTable mdaTable = null;			
			while(resultSet.next()) {
				mdaTable = new MdaTable();
				mdaTable.setTableCatalog(resultSet.getString(TABLE_CAT));
				mdaTable.setTableName(resultSet.getString(TABLE_NAME));
				mdaTable.setTableSchema(resultSet.getString(TABLE_SCHEM));
				mdaTable.setTableType(resultSet.getString(TABLE_TYPE));
				mdaTable.setTableRemarks(resultSet.getString(TABLE_REMARKS));
				
				if(fetchColumn) {
					mdaTable.setTableColumns(this.queryColumnList(connection, dbSchemaName, mdaTable.getTableName()));
				}
				
				tables.add(mdaTable);
			}
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "query table list from db failed!", null, e);
		}finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tables;		
	}
	
	/**
	 * 获取指定数据库表的信息，包括字段信息
	 * @param ds 数据源
	 * @param dbSchemaName 数据库名
	 * @param tableList 表名列表
	 * @return 表的列表
	 * @throws EngineException
	 */
	public List<MdaTable> queryTableColumnList(Connection connection, String dbSchemaName, List<String> tableList) throws EngineException {
		if(CollectionUtils.isEmpty(tableList)) {
			logger.info("tableList is null!");
			return null;
		}
		if(connection == null) {
			logger.error("db connection is null !");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "db connection is null !");
		}
		List<MdaTable> tables =  new ArrayList<>();
		for(String tableName : tableList) {
			tables.addAll(this.queryTableList(connection, dbSchemaName, tableName, true));
		}
		return tables;		
	}
	
	/**
	 * 查询数据库表的字段列表
	 * @param ds 数据源
	 * @param dbSchemaName 数据库名
	 * @param tableName 表名
	 * @return 字段列表
	 * @throws EngineException
	 */
	public List<MdaColumn> queryColumnList(DataSource ds, String dbSchemaName, String tableName) throws EngineException{
		Connection connection = null;
		List<MdaColumn> columns =  null;
		try {
			connection = ds.getConnection();
			columns = this.queryColumnList(connection, dbSchemaName, tableName);
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "query table list from db failed!", null, e);
		}finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return columns;
	}
	
	/**
	 * 查询数据库表的字段列表
	 * @param connection 数据库连接
	 * @param dbSchemaName 数据库名
	 * @param tableName 表名
	 * @return 字段列表
	 * @throws EngineException
	 */
	private List<MdaColumn> queryColumnList(Connection connection, String dbSchemaName, String tableName) throws EngineException{
		ResultSet resultSet = null;
		List<MdaColumn> columns =  new ArrayList<>();
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			
			resultSet = metaData.getPrimaryKeys(dbSchemaName, dbSchemaName, tableName);
			Map<String, Object> pkMap =  new HashMap<>();			
			while(resultSet.next()) {
				pkMap.put(resultSet.getString(COLUMN_NAME), resultSet.getObject(KEY_SEQ));
			}
			resultSet.close();
			
			resultSet = metaData.getColumns(dbSchemaName, dbSchemaName, tableName, null);	
			MdaColumn mdaColumn = null;
			while(resultSet.next()) {
				mdaColumn = new MdaColumn();
				mdaColumn.setDataType(resultSet.getInt(DATA_TYPE));
				mdaColumn.setTypeName(resultSet.getString(TYPE_NAME));
				mdaColumn.setColumnSize(resultSet.getInt(COLUMN_SIZE));
				mdaColumn.setColumnName(resultSet.getString(COLUMN_NAME));			
				mdaColumn.setNullable(resultSet.getInt(NULLABLE) == DatabaseMetaData.columnNullable);
				mdaColumn.setDecimalDigits(resultSet.getInt(DECIMAL_DIGITS));
				mdaColumn.setRemarks(resultSet.getString(REMARKS));
				mdaColumn.setColumnDefault(resultSet.getString(COLUMN_DEF));
				
				if(pkMap.containsKey(mdaColumn.getColumnName())) {
					mdaColumn.setPrimaryKey(true);
				}
				columns.add(mdaColumn);
			}
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "query table list from db failed!", null, e);
		}finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return columns;
	}
	
	
}
