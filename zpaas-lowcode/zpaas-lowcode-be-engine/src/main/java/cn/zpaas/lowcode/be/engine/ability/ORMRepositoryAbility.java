package cn.zpaas.lowcode.be.engine.ability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.bean.PageParam;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.KeyGenerateMethod;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.ColumnMapping;
import cn.zpaas.lowcode.domain.eo.DbColumn;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

/**
 * @author zjy 基于ORM的通用关系数据库存取方法封装
 */
public class ORMRepositoryAbility {

	private static Logger logger = LoggerFactory.getLogger(ORMRepositoryAbility.class);

	// ORMRepository实例，实现单例模式
	private static ORMRepositoryAbility instance = null;

	// CRUD语句的模板字符串常量
	private static final String INSERT_EXPR = "insert into ${tableName} ( ${columns} ) values( ${values} )";
	private static final String UPDATE_EXPR = "update ${tableName} set ${columns} where ${conditions} ";
	private static final String DELETE_EXPR = "delete from ${tableName} where ${conditions} ";
	private static final String QUERY_EXPR = "select * from ${tableName} where ${conditions} ";
	
	private static final String PAGED_QUERY_EXPR = " limit ?, ?";
	private static final String PAGED_QUERY_TOTAL_BEGIN_EXPR = "select count(*) from ( ";
	private static final String PAGED_QUERY_TOTAL_END_EXPR = ") a";
	// SQL语句中常用的符号常量
	private static final String EQUAL_MARK = "=";
	private static final String AND_MARK = " and ";
	private static final String COMMA_MARK = ",";
	private static final String FUZZY_MARK = "%";
	private static final String LIKE_MARK = " like ";
	private static final String BETWEEN_MARK = " between ";
	private static final String ORDER_BY_MARK = " order by ";
	private static final String IN_MARK = " in ";
	private static final String EMPTY_SPACE_MARK = " ";
	private static final String LEFT_BRACKET_MARK = " ( ";
	private static final String RIGHT_BRACKET_MARK = " ) ";
	private static final String QUESTION_MARK = "?";
	private static final String QUERY_OPTION_FUZZY = "fuzzyQuery";//模糊查询
	private static final String QUERY_OPTION_FUZZY_LEFT = "leftFuzzyQuery";//模糊查询-左
	private static final String QUERY_OPTION_FUZZY_RIGHT = "rightFuzzyQuery";//模糊查询-右
	private static final String QUERY_OPTION_RANGE = "rangeQuery";//范围查询
	private static final String QUERY_OPTION_MULTI_VALUE = "multiValueQuery";//多值查询
	private static final String QUERY_OPTION_ORDER_BY = "orderBy";//排序选项
	private static final String QUERY_OPTION_MULTI_VALUE_SPLIT = "\\$;";//多值分隔符

	private static final String STRING_TYPE = "java.lang.String";//String类型

	// ObjectRelationMapping的缓存对象，第一层Key是systemId，第二层Key是ormId
	private Map<String, Map<String, ObjectRelationMapping>> mappingMap = new HashMap<>();
	// DbTable的缓存对象，第一层Key是systemId，第二层Key是dbTableId
	private Map<String, Map<String, DbTable>> tableMap = new HashMap<>();
	// columnMap的缓存对象，第一层Key是systemId，第二层Key是dbColumnId
	private Map<String, Map<String, DbColumn>> columnMap = new HashMap<>();

	// 私有化构造函数
	private ORMRepositoryAbility() {

	}

	public static ORMRepositoryAbility getInstance() {
		return instance;
	}

	/**
	 * ORMRepository类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache ObjectRelationMapping to mappingMap...");
		}
		// 初始化ORMRepository实例
		ORMRepositoryAbility repository = new ORMRepositoryAbility();
		// 使用初始化服务加载状态为有效的ObjectRelationMapping数据，包含DBTable的数据，不包含ManagedObject对象数据
		List<ObjectRelationMapping> objectRelationMappings = null;
		if (systemId == null || systemId.length() == 0) {
			objectRelationMappings = initService.listObjectRelationMappings();
		} else {
			objectRelationMappings = initService.listObjectRelationMappingBySystem(systemId);
		}

		if (objectRelationMappings != null && !objectRelationMappings.isEmpty()) {
			for (ObjectRelationMapping mapping : objectRelationMappings) {
				// systemId为空的数据不缓存
				if (mapping.getSystemId() == null || mapping.getSystemId().trim().length() == 0) {
					continue;
				}

				// 处理第一层Key对应Map的初始化
				Map<String, ObjectRelationMapping> map = repository.mappingMap.get(mapping.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.mappingMap.put(mapping.getSystemId(), map);
				}

				// 将ObjectRelationMapping保存到第一层Key对应的Map中
				map.put(mapping.getId(), mapping);
			}
		} else {
			logger.info("no valid ORM data.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("load and cache DbTable and DbColumn data...");
		}

		// 使用初始化服务加载状态为有效的DBTable数据
		List<DbTable> dbTables = null;
		if (systemId == null || systemId.length() == 0) {
			dbTables = initService.listdbDbTables();
		} else {
			dbTables = initService.listdbDbTableBySystem(systemId);
		}
		
		if (dbTables != null && !dbTables.isEmpty()) {
			//加载所有字段
			List<DbColumn> allDbColumns = null;
			if (systemId == null || systemId.length() == 0) {
				allDbColumns = initService.listDbColumns();
			} else {
				allDbColumns = initService.listDbColumnBySystem(systemId);
			}
			if(allDbColumns == null) {
				allDbColumns = new ArrayList<>();
			}
			//按tableId进行分组
			Map<String, List<DbColumn>> dbColumnMap = allDbColumns.stream().collect(Collectors.groupingBy(DbColumn::getTableId));
			
			for (DbTable table : dbTables) {
				// systemId为空的数据不缓存
				if (table.getSystemId() == null || table.getSystemId().trim().length() == 0) {
					continue;
				}
				table.setDbColumns(dbColumnMap.get(table.getId()));
				
				// 处理第一层Key对应Map的初始化
				Map<String, DbTable> map = repository.tableMap.get(table.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.tableMap.put(table.getSystemId(), map);
				}

				// 将DbTable保存到第一层Key对应的Map中
				map.put(table.getId(), table);

				// 处理第一层Key对应Map的初始化
				Map<String, DbColumn> colMap = repository.columnMap.get(table.getSystemId());
				if (colMap == null) {
					colMap = new HashMap<>();
					repository.columnMap.put(table.getSystemId(), colMap);
				}

				// 将DbTable包含的所有DbColumn加入到第一层Key对应的Map中
				if(table.getDbColumns() != null && !table.getDbColumns().isEmpty()) {
					for (DbColumn column : table.getDbColumns()) {
						colMap.put(column.getId(), column);
					}
				}				
			}
		} else {
			logger.info("no valid DbTable.");
		}

		// 初始化完成，赋值给属性instance
		instance = repository;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache ObjectRelationMapping to mappingMap...");
		}
		// 使用初始化服务加载状态为有效的ObjectRelationMapping数据，包含DBTable的数据，不包含ManagedObject对象数据
		List<ObjectRelationMapping> objectRelationMappings = initService.listObjectRelationMappingBySystem(systemId);
		Map<String, ObjectRelationMapping> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(objectRelationMappings)) {
			for (ObjectRelationMapping mapping : objectRelationMappings) {
				// systemId为空的数据不缓存
				if (mapping.getSystemId() == null || mapping.getSystemId().trim().length() == 0) {
					continue;
				}

				// 将ObjectRelationMapping保存到第一层Key对应的Map中
				map.put(mapping.getId(), mapping);
			}
		} 
		instance.mappingMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("load and cache DbTable and DbColumn data...");
		}

		// 使用初始化服务加载状态为有效的DBTable数据
		List<DbTable> dbTables = initService.listdbDbTableBySystem(systemId);
		Map<String, DbTable> dbTableMap = new HashMap<>();
		Map<String, DbColumn> colMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(dbTables)) {
			//加载所有字段
			List<DbColumn> allDbColumns = initService.listDbColumnBySystem(systemId);
			if(allDbColumns == null) {
				allDbColumns = new ArrayList<>();
			}
			//按tableId进行分组
			Map<String, List<DbColumn>> dbColumnMap = allDbColumns.stream().collect(Collectors.groupingBy(DbColumn::getTableId));
			
			for (DbTable table : dbTables) {
				// systemId为空的数据不缓存
				if (table.getSystemId() == null || table.getSystemId().trim().length() == 0) {
					continue;
				}
				table.setDbColumns(dbColumnMap.get(table.getId()));
				
				// 将DbTable保存到第一层Key对应的Map中
				dbTableMap.put(table.getId(), table);

				// 将DbTable包含的所有DbColumn加入到第一层Key对应的Map中
				if(table.getDbColumns() != null && !table.getDbColumns().isEmpty()) {
					for (DbColumn column : table.getDbColumns()) {
						colMap.put(column.getId(), column);
					}
				}				
			}
		} 
		instance.tableMap.put(systemId, dbTableMap);
		instance.columnMap.put(systemId, colMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 封装使用systemId和ormId从缓存获取ObjectRelationMapping的方法
	 * 
	 * @param systemId 业务系统标识
	 * @param ormId    对象关系映射标识
	 * @return ObjectRelationMapping对象，取不到时返回null
	 */
	private ObjectRelationMapping getObjectRelationMapping(String systemId, String ormId) {
		Map<String, ObjectRelationMapping> map = mappingMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(ormId);
	}

	/**
	 * 封装使用systemId和tableId从缓存获取DbTable的方法
	 * 
	 * @param systemId 业务系统标识
	 * @param tableId  数据库表标识
	 * @return DbTable对象，取不到时返回null
	 */
	private DbTable getDbTable(String systemId, String tableId) {
		Map<String, DbTable> map = tableMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(tableId);
	}

	/**
	 * 封装使用systemId和columnId从缓存获取DbColumn对象的方法
	 * 
	 * @param systemId 业务系统标识
	 * @param columnId 数据库表字段标识
	 * @return DbColumn对象，取不到时返回null
	 */
	private DbColumn getDbColumn(String systemId, String columnId) {
		Map<String, DbColumn> map = columnMap.get(systemId);
		if (map == null) {
			return null;
		}
		return map.get(columnId);
	}

	/**
	 * 基于ORM的通用数据库插入方法
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统标识
	 * @param ormId        使用的ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 成功时返回生成主键后的原对象，否则返回null
	 */
	public JsonObject insert(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) throws EngineException {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return null;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return null;
		}

		// 初始化insert语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(INSERT_EXPR);

		// 用于拼接insert语句中表字段部分的字符串
		StringBuilder columnStr = new StringBuilder();
		// 用于拼接insert语句中values部分的字符串
		StringBuilder valueStr = new StringBuilder();
		// 按字段的拼接顺序，加入对应的值到该列表中
		List<Object> valuesList = new ArrayList<>();

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		DbColumn column = null;
		Attribute attribute = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return null;
			}

			// 如果当前字段是主键，且字段值为空的话，需要根据配置的主键生成方法生成值
			if (YesOrNo.YES.equals(column.getIsPrimaryKey())) {
				if (domainObject.getAttributeValue(attribute.getCode()) == null || ((String)domainObject.getAttributeValue(attribute.getCode())).trim().length() == 0) {
					if(KeyGenerateMethod.UUID.equals(column.getKeyGenerateMethod())) {
						domainObject.setAttributeValue(attribute.getCode(),KeyGenerate.uuidKey());
					}else if(KeyGenerateMethod.SNOW_FLAKE.equals(column.getKeyGenerateMethod())) {
						domainObject.setAttributeValue(attribute.getCode(),KeyGenerate.snowFlakeKey());
					}else {
						logger.error("T[{}] primary key is null, but no valid key generate method is assigned!", orm.getTenantId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "primary key is null, but no valid key generate method is assigned!");
					}
					
				}
			}
			
			if (domainObject.getAttributeValue(attribute.getCode()) == null 
					|| (domainObject.getAttributeValue(attribute.getCode())).toString().trim().length() == 0) {//为空的字段，不处理
				continue;
			}

			// 拼接表字段部分
			columnStr.append(column.getName()).append(COMMA_MARK);
			// 按表字段的顺序拼接values部分
			valueStr.append(QUESTION_MARK).append(COMMA_MARK);
			// 按表字段的顺序将字段值加入到列表中
			valuesList.add(domainObject.getAttributeValue(attribute.getCode()));
		}

		// 按相反的顺序将表名、表字段部分字符串，values部分字符串替换到insert语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(48, 57, valueStr.substring(0, valueStr.length() - 1));
		sql.replace(27, 37, columnStr.substring(0, columnStr.length() - 1));
		sql.replace(12, 24, table.getName());

		// 获得最终的Sql
		String insertSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] insert sql: {}", orm.getTenantId(), insertSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);

		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		if( jdbcTemplate.execute(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// 创建PreparedStatement对象并按顺序绑定字段值
				PreparedStatement ps = con.prepareStatement(insertSql);
				int size = valuesList.size();
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, valuesList.get(i));
				}
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {
			@Override
			// 执行语句
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		}) > 0) {//执行成功时返回生成主键后的原对象
			return domainObject.objectValues();
		}else {//执行失败时，返回null
			return null;
		}
	}

	/**
	 * 基于ORM的通用数据库插入方法
	 * 
	 * @param domainObjects 领域对象实例列表
	 * @param systemId     业务系统标识
	 * @param ormId        使用的ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 成功时返回生成主键后的原对象，否则返回null
	 */
	public int[] batchInsert(List<AttributedObjectContainer> domainObjects, String systemId, String ormId, String dbSchemaId) throws EngineException {
		//如果领域对象列表为空，则直接返回
		if(domainObjects == null || domainObjects.isEmpty()) {
			logger.info("domainObjects is null!");
			return null;
		}
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "orm is null!");
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "can't find table!");
		}
		AttributedObject attributedObject = domainObjects.get(0).getAttributedObject();
		if (attributedObject == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "can't find domainObject!");
		}

		// 初始化insert语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(INSERT_EXPR);

		// 用于拼接insert语句中表字段部分的字符串
		StringBuilder columnStr = new StringBuilder();
		// 用于拼接insert语句中values部分的字符串
		StringBuilder valueStr = new StringBuilder();
		// 按字段的拼接顺序，加入对应的值到该列表中
		List<Object[]> valuesList = new ArrayList<>();
		for(int i=0; i<domainObjects.size(); i++) {
			valuesList.add(new Object[orm.getColumnMappings().size()]);
		}

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		DbColumn column = null;
		Attribute attribute = null;
		int columnIndex = 0;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : attributedObject.getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "can't find Attribute info!");
			}

			// 如果当前字段是主键，且字段值为空的话，需要根据配置的主键生成方法生成值
			if (YesOrNo.YES.equals(column.getIsPrimaryKey())) {
				for(int i=0; i<domainObjects.size(); i++) {
					JsonObject rowDomainObject = domainObjects.get(i).objectValues();
					if (rowDomainObject.get(attribute.getCode()) == null || JsonUtils.getString(rowDomainObject, attribute.getCode()).trim().length() == 0) {
						logger.debug("T[{}] primary key is null, generated key.", orm.getTenantId());
						if(KeyGenerateMethod.UUID.equals(column.getKeyGenerateMethod())) {
							rowDomainObject.addProperty(attribute.getCode(),KeyGenerate.uuidKey());
						}else if(KeyGenerateMethod.SNOW_FLAKE.equals(column.getKeyGenerateMethod())) {
							rowDomainObject.addProperty(attribute.getCode(),KeyGenerate.snowFlakeKey());
						}else {
							logger.error("T[{}] primary key is null, but no valid key generate method is assigned!", orm.getTenantId());
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "primary key is null, but no valid key generate method is assigned!");
						}
						
					}
				}
			}
			
			// 拼接表字段部分
			columnStr.append(column.getName()).append(COMMA_MARK);
			// 按表字段的顺序拼接values部分
			valueStr.append(QUESTION_MARK).append(COMMA_MARK);
			// 按表字段的顺序将字段值加入到列表中
			for(int i=0; i<domainObjects.size(); i++) {
				JsonObject rowDomainObject = domainObjects.get(i).objectValues();
				valuesList.get(i)[columnIndex] = rowDomainObject.get(attribute.getCode());
			}
			columnIndex++;
		}

		// 按相反的顺序将表名、表字段部分字符串，values部分字符串替换到insert语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(48, 57, valueStr.substring(0, valueStr.length() - 1));
		sql.replace(27, 37, columnStr.substring(0, columnStr.length() - 1));
		sql.replace(12, 24, table.getName());

		// 获得最终的Sql
		String insertSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] insert sql: {}", orm.getTenantId(), insertSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);

		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		return jdbcTemplate.batchUpdate(insertSql, valuesList);
	}
	
	/**
	 * 基于ORM的通用基于主键的update方法，使用领域对象中对应主键属性的值作为更新条件，其余的属性全部会更新到表中（不带空值）
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回更新的记录数，大于0时表示更新成功，等于0时表示更新失败
	 */
	public int updateByPKSelective(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return 0;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return 0;
		}

		// 初始化update语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE_EXPR);

		// 用于拼接update语句中条件字段部分的字符串
		StringBuilder conditionStr = new StringBuilder();
		// 用于拼接update语句中更新字段值部分的字符串
		StringBuilder columnStr = new StringBuilder();
		// 按更新字段值的顺序将字段值加入到列表中
		List<Object> valuesList = new ArrayList<>();
		// 按条件字段的顺序将字段值加入到列表中
		List<Object> conditionList = new ArrayList<>();

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		Attribute attribute = null;
		DbColumn column = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return 0;
			}

			Object value = domainObject.getAttributeValue(attribute.getCode());
			// 如果是主键，则作为update语句的条件
			if (YesOrNo.YES.equals(column.getIsPrimaryKey())) {
				// 拼接条件部分的字符串
				conditionStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(AND_MARK);
				// 按条件字段的顺序，将对应的值加入条件值列表
				conditionList.add(value);
			} else { // 如果不是主健，则作为update语句的更新字段
				if (value == null || value.toString().trim().length() == 0) {//为空的字段，不处理
					continue;
				}
				// 拼接更新字段值部分的字符串
				columnStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(COMMA_MARK);
				// 按更新字段的顺序，将对应的值加入更新值列表
				valuesList.add(value);
			}
		}

		// 由于更新字段值部分肯定在条件前面，所以可以直接将条件值全部加入到更新值列表中
		valuesList.addAll(conditionList);

		// 按相反的顺序将表名、更新字段值部分字符串，条件部分字符串替换到update语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(41, 54, conditionStr.substring(0, conditionStr.length() - 5));
		sql.replace(24, 34, columnStr.substring(0, columnStr.length() - 1));
		sql.replace(7, 19, table.getName());

		// 获得最终的update语句
		String updateSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] update sql: {}", orm.getTenantId(), updateSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		
		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		return jdbcTemplate.execute(new PreparedStatementCreator() {
			@Override
			// 创建PreparedStatement对象并按顺序绑定字段值
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(updateSql);
				int size = valuesList.size();
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, valuesList.get(i));
				}
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {
			@Override
			// 执行语句
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}
	
	/**
	 * 基于ORM的通用基于主键的update方法，使用领域对象中对应主键属性的值作为更新条件，其余的属性全部会更新到表中（空值字段也会更新）
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回更新的记录数，大于0时表示更新成功，等于0时表示更新失败
	 */
	public int updateByPK(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return 0;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return 0;
		}

		// 初始化update语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE_EXPR);

		// 用于拼接update语句中条件字段部分的字符串
		StringBuilder conditionStr = new StringBuilder();
		// 用于拼接update语句中更新字段值部分的字符串
		StringBuilder columnStr = new StringBuilder();
		// 按更新字段值的顺序将字段值加入到列表中
		List<Object> valuesList = new ArrayList<>();
		// 按条件字段的顺序将字段值加入到列表中
		List<Object> conditionList = new ArrayList<>();

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		Attribute attribute = null;
		DbColumn column = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return 0;
			}

			Object value = domainObject.getAttributeValue(attribute.getCode());
			// 如果是主键，则作为update语句的条件
			if (YesOrNo.YES.equals(column.getIsPrimaryKey())) {
				// 拼接条件部分的字符串
				conditionStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(AND_MARK);
				// 按条件字段的顺序，将对应的值加入条件值列表
				conditionList.add(value);
			} else { // 如果不是主健，则作为update语句的更新字段
				// 拼接更新字段值部分的字符串
				columnStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(COMMA_MARK);
				// 按更新字段的顺序，将对应的值加入更新值列表
				if(STRING_TYPE.equals(attribute.getAttrClass())) {
					valuesList.add(value);
				}else {
					if(value != null && (value instanceof String) && ((String)value).trim().length() == 0) {//非字符类型，值为空字符串时，变更为null
						valuesList.add(null);
					}else {
						valuesList.add(value);
					}
				}
				
			}
		}

		// 由于更新字段值部分肯定在条件前面，所以可以直接将条件值全部加入到更新值列表中
		valuesList.addAll(conditionList);

		// 按相反的顺序将表名、更新字段值部分字符串，条件部分字符串替换到update语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(41, 54, conditionStr.substring(0, conditionStr.length() - 5));
		sql.replace(24, 34, columnStr.substring(0, columnStr.length() - 1));
		sql.replace(7, 19, table.getName());

		// 获得最终的update语句
		String updateSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] update sql: {}", orm.getTenantId(), updateSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		
		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		return jdbcTemplate.execute(new PreparedStatementCreator() {
			@Override
			// 创建PreparedStatement对象并按顺序绑定字段值
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(updateSql);
				int size = valuesList.size();
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, valuesList.get(i));
				}
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {
			@Override
			// 执行语句
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}

	/**
	 * 基于ORM的通用基于主键的delete方法，使用领域对象中对应主键属性的值作为更新条件，其余的属性不起作用
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回删除的记录数，大于0时表示删除成功，等于0时表示删除失败
	 */
	public int deleteByPK(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return 0;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return 0;
		}

		// 初始化delete语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_EXPR);

		// 用于拼接delete语句中条件字段部分的字符串
		StringBuilder conditionStr = new StringBuilder();
		// 按条件字段的顺序将对应的值加入到列表中
		List<Object> valuesList = new ArrayList<>();

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		DbColumn column = null;
		Attribute attribute = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 只使用主键字段，其他字段直接忽略
			if (!"Y".equals(column.getIsPrimaryKey())) {
				continue;
			}

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return 0;
			}

			// 拼接条件部分的字符串
			conditionStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(AND_MARK);
			// 按条件字段的顺序将对应的值加入列表中
			valuesList.add(domainObject.getAttributeValue(attribute.getCode()));

		}

		// 按相反的顺序将表名、条件部分字符串替换到delete语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(31, 44, conditionStr.substring(0, conditionStr.length() - 5));
		sql.replace(12, 24, table.getName());

		// 获得最终的sql
		String deleteSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] delete sql: {}", orm.getTenantId(), deleteSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		return jdbcTemplate.execute(new PreparedStatementCreator() {
			// 创建PreparedStatement对象并按顺序绑定字段值
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(deleteSql);
				int size = valuesList.size();
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, valuesList.get(i));
				}
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {
			@Override
			// 执行语句
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}

	/**
	 * 基于ORM的通用delete方法，使用领域对象中非空值的属性作为查询条件
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回删除的记录数，大于0时表示删除成功，等于0时表示删除失败
	 */
	public int deleteByCondition(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return 0;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return 0;
		}

		// 初始化delete语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_EXPR);

		// 用于拼接delete语句中条件字段部分的字符串
		StringBuilder conditionStr = new StringBuilder();
		// 按条件字段的顺序将对应的值加入到列表中
		List<Object> valuesList = new ArrayList<>();

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		DbColumn column = null;
		Attribute attribute = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			// 从缓存中获取对应表字段的DbColumn对象信息
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return 0;
			}

			// 如果属性值为空，则不作为条件
			if (domainObject.getAttributeValue(attribute.getCode()) == null 
					|| (domainObject.getAttributeValue(attribute.getCode())).toString().trim().length() == 0) {//为空的字段，不处理
				continue;
			}

			// 拼接条件部分的字符串
			conditionStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(AND_MARK);
			// 按条件字段的顺序将对应的值加入列表中
			valuesList.add(domainObject.getAttributeValue(attribute.getCode()));
		}

		// 如果未增加任何条件，直接返回0
		if (valuesList.size() == 0) {
			logger.error("T[{}] there is no condition is found, ignore executing delete sql.", orm.getTenantId());
			return 0;
		}

		// 按相反的顺序将表名、条件部分字符串替换到delete语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(31, 44, conditionStr.substring(0, conditionStr.length() - 5));
		sql.replace(12, 24, table.getName());

		// 获得最终的sql
		String deleteSql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] delete sql: {}", orm.getTenantId(), deleteSql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		// 使用jdbcTemplate绑定参数并执行语句，返回执行结果
		return jdbcTemplate.execute(new PreparedStatementCreator() {
			// 创建PreparedStatement对象并按顺序绑定字段值
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(deleteSql);
				int size = valuesList.size();
				for (int i = 0; i < size; i++) {
					ps.setObject(i + 1, valuesList.get(i));
				}
				return ps;
			}
		}, new PreparedStatementCallback<Integer>() {
			@Override
			// 执行语句
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				return ps.executeUpdate();
			}
		});
	}

	/**
	 * 基于ORM的通用基于主键的save方法，当主键为空时，执行insert操作；当主键不为空时，执行update操作
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回操作成功的记录数，大于0时表示保存成功，等于0时表示保存失败
	 */
	public int save(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) throws EngineException{
		// 从缓存中获取对应的ObjectRelationMapping数据，如果取不到则返回0
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return 0;
		}

		// 循环ORM中领域对象属性与表字段的映射数据ColumnMappings
		DbColumn column = null;
		Attribute attribute = null;
		boolean insert = false;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return 0;
			}

			// 如果有主键字段的值为空，侧更新insert标志为true，并中断循环
			if (YesOrNo.YES.equals(column.getIsPrimaryKey())) {
				if (domainObject.getAttributeValue(attribute.getCode()) == null) {
					insert = true;
					break;
				}
			}
		}
		if (insert) {// 执行insert操作
			if(insert(domainObject, systemId, ormId, dbSchemaId) != null) {
				return 1;
			} else {
				return 0;
			}
		} else {// 执行update操作
			return updateByPK(domainObject, systemId, ormId, dbSchemaId);
		}
	}

	/**
	 * 基于ORM的通用基于主键的查询方法，使用领域对象中对应主键属性的值作为查询条件，其余的属性不起作用
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @return 返回符合条件的结果
	 */
	public JsonObject queryByPK(AttributedObjectContainer domainObject, String systemId, String ormId, String dbSchemaId) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回null
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return null;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return null;
		}

		// 初始化select语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(QUERY_EXPR);

		// 用于拼接条件部分字符串的对象
		StringBuilder conditionStr = new StringBuilder();
		// 按条件字段的顺序，将相应的值加入列表对象中
		List<Object> valuesList = new ArrayList<>();

		Attribute attribute = null;
		DbColumn column = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());
			// 非主键字段直接忽略
			if (!"Y".equals(column.getIsPrimaryKey())) {
				continue;
			}

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return null;
			}

			// 拼装查询语句的条件部分字符串
			conditionStr.append(column.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(AND_MARK);
			// 按条件字段的顺序，将字段值加入列表中
			valuesList.add(domainObject.getAttributeValue(attribute.getCode()));
		}
		// 按相反的顺序将表名、条件部分字符串替换到select语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		sql.replace(33, 46, conditionStr.substring(0, conditionStr.length() - 5));
		sql.replace(14, 26, table.getName());

		// 获得最终的sql
		String querySql = sql.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] query sql: {}", orm.getTenantId(), querySql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		// 使用jdbcTemplate执行sql、绑定参数并获得执行结果
		List<JsonObject> list = jdbcTemplate.query(querySql, new PreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					for(int i=0; i<valuesList.size(); i++) {
						ps.setObject(i+1, valuesList.get(i));
					}
				}
			}, new RowMapper<JsonObject>() {
					@Override
					// 使用JsonObject对象构造返回结果
					public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
						DbColumn column = null;
						JsonObject objectContainer = domainObject.getAttributedObject().newInstance();
						
						for (ColumnMapping columnMapping : orm.getColumnMappings()) {
							column = getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

							// 从领域对象容器类实例中找到对应的Attribute对象
							Attribute attribute = null;
							for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
								if (columnMapping.getAttributeId().equals(attr.getId())) {
									attribute = attr;
									break;
								}
							}
							// 找不到对应的Attribute信息时，忽略当前字段
							if (attribute == null) {
								logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
								continue;
							}
							objectContainer.add(attribute.getCode(), JsonUtils.toJsonElement(rs.getObject(column.getName())));
						}
						return objectContainer;
					}
				});
		// 返回查询结果
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 基于ORM的通用查询方法，使用领域对象中非空值的属性作为查询条件
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @param pageFlag	是否支持分页
	 * @return 返回符合条件的结果
	 */
	public JsonArray queryByCondition(AttributedObjectContainer domainObject, String systemId,
			String ormId, String dbSchemaId, boolean pageFlag) {
		return this.queryByCondition(domainObject, systemId, ormId, dbSchemaId, pageFlag, null);
	}

	/**
	 * 基于ORM的通用查询方法，使用领域对象中非空值的属性作为查询条件
	 * 
	 * @param domainObject 领域对象的容器类实例
	 * @param systemId     业务系统桔
	 * @param ormId        ORM标识
	 * @param dbSchemaId   对应数据库标识
	 * @param pageFlag	是否支持分页
	 * @param queryOptions 查询选项
	 * @return 返回符合条件的结果
	 */
	public JsonArray queryByCondition(AttributedObjectContainer domainObject, String systemId,
			String ormId, String dbSchemaId, boolean pageFlag, JsonObject queryOptions) {
		// 从缓存中获取对应的ObjectRelationMapping和DbTable数据，如果取不到则返回null
		ObjectRelationMapping orm = getObjectRelationMapping(systemId, ormId);
		if (orm == null) {
			return null;
		}
		DbTable table = getDbTable(systemId, orm.getTableId());
		if (table == null) {
			return null;
		}

		// 初始化select语句的StringBuilder对象
		StringBuilder sql = new StringBuilder();
		sql.append(QUERY_EXPR);

		// 用于拼接条件部分字符串的对象
		StringBuilder conditionStr = new StringBuilder();
		// 按条件字段的顺序，将相应的值加入列表对象中
		List<Object> valuesList = new ArrayList<>();

		Attribute attribute = null;
		DbColumn column = null;
		Object value = null;
		for (ColumnMapping columnMapping : orm.getColumnMappings()) {
			column = this.getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

			// 从领域对象容器类实例中找到对应的Attribute对象
			attribute = null;
			for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
				if (columnMapping.getAttributeId().equals(attr.getId())) {
					attribute = attr;
					break;
				}
			}
			// 找不到对应的Attribute信息时，直接返回0
			if (attribute == null) {
				logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
				return null;
			}

			value = domainObject.getAttributeValue(attribute.getCode());
			if (value != null && value.toString().trim().length() > 0) {
				if(this.checkQueryOption(queryOptions, QUERY_OPTION_FUZZY, attribute.getCode())) {//模糊查询
					// 当字段值不为空时，作为查询条件
					conditionStr.append(column.getName()).append(LIKE_MARK).append(QUESTION_MARK).append(AND_MARK);
					// 按条件字段的顺序将值加入到列表中
					valuesList.add(FUZZY_MARK + value + FUZZY_MARK);
				}else if(this.checkQueryOption(queryOptions, QUERY_OPTION_FUZZY_LEFT, attribute.getCode())) {//左模糊
					// 当字段值不为空时，作为查询条件
					conditionStr.append(column.getName()).append(LIKE_MARK).append(QUESTION_MARK).append(AND_MARK);
					// 按条件字段的顺序将值加入到列表中
					valuesList.add(FUZZY_MARK + value);
				}else if(this.checkQueryOption(queryOptions, QUERY_OPTION_FUZZY_RIGHT, attribute.getCode())) {//右模糊
					// 当字段值不为空时，作为查询条件
					conditionStr.append(column.getName()).append(LIKE_MARK).append(QUESTION_MARK).append(AND_MARK);
					// 按条件字段的顺序将值加入到列表中
					valuesList.add(value + FUZZY_MARK);
				}else if(this.checkQueryOption(queryOptions, QUERY_OPTION_RANGE, attribute.getCode())) {//范围查询
					// 当字段值不为空时，作为查询条件
					conditionStr.append(column.getName()).append(BETWEEN_MARK).append(QUESTION_MARK).append(AND_MARK).append(QUESTION_MARK).append(AND_MARK);
					String[] values = value.toString().split(QUERY_OPTION_MULTI_VALUE_SPLIT);
					if(values == null || values.length != 2) {
						logger.error("T[{}] range query attribute must have two values! attribute({}), value({})", orm.getTenantId(), attribute.getCode(), value);
						return null;
					}
					// 按条件字段的顺序将值加入到列表中
					valuesList.add(values[0]);
					valuesList.add(values[1]);
				}else if(this.checkQueryOption(queryOptions, QUERY_OPTION_MULTI_VALUE, attribute.getCode())) {//多值查询
					// 当字段值不为空时，作为查询条件
					String[] values = value.toString().split(QUERY_OPTION_MULTI_VALUE_SPLIT);
					conditionStr.append(column.getName()).append(IN_MARK).append(LEFT_BRACKET_MARK);
					for(int i=0; i<values.length; i++) {
						if(i == (values.length-1)) {
							conditionStr.append(QUESTION_MARK);
						}else {
							conditionStr.append(QUESTION_MARK).append(COMMA_MARK);
						}
						// 按条件字段的顺序将值加入到列表中
						valuesList.add(values[i]);
					}
					conditionStr.append(RIGHT_BRACKET_MARK).append(AND_MARK);
				}else {//>、>=、< <= == != <>
					String compareStr = EQUAL_MARK;
					if(queryOptions != null && queryOptions.has(attribute.getCode()) && queryOptions.get(attribute.getCode()) != null && 
					JsonUtils.getString(queryOptions, attribute.getCode()).trim().length() > 0) {
						compareStr = JsonUtils.getString(queryOptions, attribute.getCode());
					}
					// 当字段值不为空时，作为查询条件
					conditionStr.append(column.getName()).append(compareStr).append(QUESTION_MARK).append(AND_MARK);
					// 按条件字段的顺序将值加入到列表中
					valuesList.add(value);
				}
			}
		}
		// 按相反的顺序将表名、条件部分字符串替换到select语句模板对应的位置
		// 如果模板发生变化，需要相应的调整位置序号
		if(conditionStr.length() > 5) {
			sql.replace(33, 46, conditionStr.substring(0, conditionStr.length() - 5));
		}else {
			sql.replace(33, 46, conditionStr.toString());
		}
		
		sql.replace(14, 26, table.getName());

		// 获得最终的sql
		String querySql = sql.toString();
		if(valuesList.isEmpty()) {
			querySql = querySql.substring(0, querySql.length()-7);
		}

		
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		
		//增加分页能力
		if(pageFlag && PageAbility.getPageParam() != null) {
			PageParam pageParam = PageAbility.getPageParam();
			//查询总记录数
			if(pageParam.isQueryTotal()) {
				String queryTotalSql = PAGED_QUERY_TOTAL_BEGIN_EXPR + querySql + PAGED_QUERY_TOTAL_END_EXPR;		
				if (logger.isDebugEnabled()) {
					logger.debug("T[{}] queryTotal sql: {}", orm.getTenantId(), queryTotalSql);
					logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
				}
				Long total = jdbcTemplate.query(queryTotalSql, new PreparedStatementSetter() {
					
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							for(int i=0; i<valuesList.size(); i++) {
								ps.setObject(i+1, valuesList.get(i));
							}
						}
					}, new ResultSetExtractor<Long>() {
					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						if(rs.next()) {
							return rs.getLong(1);
						}else {
							return null;
						}
					}
				});
				pageParam.setTotal(total);
			}

			//处理排序逻辑，查询总数时不增加排序逻辑
			if(!StringUtils.isBlank(JsonUtils.getString(queryOptions, QUERY_OPTION_ORDER_BY))) {//QueryOption设置了排序选择，优先使用
				querySql = querySql + EMPTY_SPACE_MARK + JsonUtils.getString(queryOptions, QUERY_OPTION_ORDER_BY);
			}else if(!StringUtils.isBlank(PageAbility.getPageParam().getSortName())) {//分页中设置了排序字段
				querySql = querySql + ORDER_BY_MARK + PageAbility.getPageParam().getSortName();
				if(!StringUtils.isBlank(PageAbility.getPageParam().getSortOrder())) {
					querySql = querySql + EMPTY_SPACE_MARK + PageAbility.getPageParam().getSortOrder();
				}
			}

			//当每页数据小于等于0时，不进行分页处理
			if(PageAbility.getPageParam().getPageRows() >0) {
				//处理分页数据
				
				int pageNo = pageParam.getPageNo();
				int pageRows = pageParam.getPageRows();
				if(pageNo == 0) {
					pageNo = 1;
				}
				long start = (pageNo-1) * pageRows;
				//拼接分布条件
				querySql = querySql + PAGED_QUERY_EXPR;
				valuesList.add(start);
				valuesList.add(pageRows);
			}
		}else {//不需要分页
			//处理排序逻辑
			if(!StringUtils.isBlank(JsonUtils.getString(queryOptions, QUERY_OPTION_ORDER_BY))) {//QueryOption设置了排序选择，优先使用
				querySql = querySql + EMPTY_SPACE_MARK + JsonUtils.getString(queryOptions, QUERY_OPTION_ORDER_BY);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] query sql: {}", orm.getTenantId(), querySql);
			logger.debug("T[{}] values: {}", orm.getTenantId(), JsonUtils.toJson(valuesList));
		}
		
		// 使用jdbcTemplate执行sql、绑定参数并获得执行结果
		List<JsonObject> resultList = jdbcTemplate.query(querySql, new PreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					for(int i=0; i<valuesList.size(); i++) {
						ps.setObject(i+1, valuesList.get(i));
					}
				}
			}, new RowMapper<JsonObject>() {
			@Override
			// 使用AttributedObjectContainer对象构造返回结果
			public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
				DbColumn column = null;
				JsonObject objectContainer = domainObject.getAttributedObject().newInstance();
				
				for (ColumnMapping columnMapping : orm.getColumnMappings()) {
					column = getDbColumn(columnMapping.getSystemId(), columnMapping.getColumnId());

					// 从领域对象容器类实例中找到对应的Attribute对象
					Attribute attribute = null;
					for (Attribute attr : domainObject.getAttributedObject().getAttributes()) {
						if (columnMapping.getAttributeId().equals(attr.getId())) {
							attribute = attr;
							break;
						}
					}
					// 找不到对应的Attribute信息时，忽略当前字段
					if (attribute == null) {
						logger.error("T[{}] can't find Attribute info of ORM({}).ColumnMapping({}).attributeId({})", orm.getTenantId(), ormId, columnMapping.getId(), columnMapping.getAttributeId());
						continue;
					}
					// if(logger.isDebugEnabled() && rs.getObject(column.getName()) != null) {
					// 	logger.debug(column.getName() + ":" + rs.getObject(column.getName()).getClass().toString());
					// }
					objectContainer.add(attribute.getCode(), JsonUtils.toJsonElement(rs.getObject(column.getName())));
				}
				return objectContainer;
			}
		});
		//转换成JsonArray类型并返回
		JsonArray resutArray = new JsonArray();
		if(resultList != null && resultList.size() > 0) {
			for(JsonObject rowJsonObject: resultList) {
				resutArray.add(rowJsonObject);
			}
		}
		
		return resutArray;
	}
	
	public JsonArray executeQuerySql(String querySql, List<Object> valuesList, String systemId, String dbSchemaId, String tenantId) {
		if(StringUtils.isBlank(querySql)) {
			logger.error("T[{}] querySql is null!", tenantId);
			return null;
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		
		if (logger.isTraceEnabled()) {
			logger.trace("T[{}] query sql: {}", tenantId, querySql);
			logger.trace("T[{}] values: {}", tenantId, JsonUtils.toJson(valuesList));
		}
		
		// 使用jdbcTemplate执行sql、绑定参数并获得执行结果
		List<JsonObject> resultList = jdbcTemplate.query(querySql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if(!CollectionUtils.isEmpty(valuesList)) {
					for(int i=0; i<valuesList.size(); i++) {
						ps.setObject(i+1, valuesList.get(i));
					}
				}
			}
		}, new RowMapper<JsonObject>() {
			@Override
			// 使用AttributedObjectContainer对象构造返回结果
			public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
				JsonObject rowObject = new JsonObject();
				
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++ ) {
					rowObject.add(rs.getMetaData().getColumnLabel(i), JsonUtils.toJsonElement(rs.getObject(i)));
				}
				
				return rowObject;
			}
		});
		//转换成JsonArray类型并返回
		JsonArray resutArray = new JsonArray();
		if(resultList != null && resultList.size() > 0) {
			for(JsonObject rowJsonObject: resultList) {
				resutArray.add(rowJsonObject);
			}
		}
		return resutArray;
	}

	public List<Map<String, Object>> executeQuerySqlL(String querySql, List<Object> valuesList, String systemId, String dbSchemaId, String tenantId) {
		if(StringUtils.isBlank(querySql)) {
			logger.error("T[{}] querySql is null!", tenantId);
			return null;
		}
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(systemId, dbSchemaId);
		
		if (logger.isTraceEnabled()) {
			logger.trace("T[{}] query sql: {}", tenantId, querySql);
			logger.trace("T[{}] values: {}", tenantId, JsonUtils.toJson(valuesList));
		}
		
		// 使用jdbcTemplate执行sql、绑定参数并获得执行结果
		List<Map<String, Object>> resultList = jdbcTemplate.query(querySql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if(!CollectionUtils.isEmpty(valuesList)) {
					for(int i=0; i<valuesList.size(); i++) {
						ps.setObject(i+1, valuesList.get(i));
					}
				}
			}
		}, new RowMapper<Map<String, Object>>() {
			@Override
			// 使用AttributedObjectContainer对象构造返回结果
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> rowObject = new HashMap<>();
				
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++ ) {
					rowObject.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
				}
				
				return rowObject;
			}
		});
		return resultList;
	}
	
	/**
	 * 判断当前属性使用的特殊查询方式
	 * @param queryOptions 查询选项配置信息
	 * @param queryType 模糊查询/范围查询/多值查询
	 * @param attribute 判断的属性
	 * @return
	 */
	private boolean checkQueryOption(JsonObject queryOptions, String queryType, String attribute) {
		if(queryOptions != null && queryOptions.get(queryType) != null) {
			List<String> attributes = Arrays.asList(JsonUtils.getString(queryOptions, queryType).split(COMMA_MARK));//将对应查询类型的配置信息，根据逗号拆分，并转换成列表
			if(!CollectionUtils.isEmpty(attributes) && attributes.contains(attribute)) {//判断是否包含当前字段
				return true;
			}
		}
		return false;
	}
}
