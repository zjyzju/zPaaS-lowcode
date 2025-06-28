package cn.zpaas.lowcode.be.engine.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.constant.DBType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * DBSchemaProxy类是数据库配置信息、数据库连接池、数据库连接以及事务开启和提交的管理类
 */
public class DBSchemaProxy {
	private static Logger logger = LoggerFactory.getLogger(DBSchemaProxy.class);
	
	//用来缓存DBSchema对象，第一层Key是systemId，第二层Key是dbSchemaId
	private static Map<String, Map<String, DbSchema>> dbSchemaMap = null;
	
	//用来缓存数据源对象，第一层key是systemId，第二层Key是dbSchemaId，数据源对象跟DBSchema对象一一对应
	private static Map<String, Map<String, DataSource>> dataSourceMap = null;
	
	//用于缓存Spring封装的TransactionTemplate对象，第一层key是systemId，第二层Key是dbSchemaId，数据源对象跟DBSchema对象一一对应。当采用Spring封装方案时需要实现。
	private static Map<String, Map<String, TransactionTemplate>> transactionTemplateMap = null;
	
	//用于缓存Spring封装的JdbcTemplate对象，第一层key是systemId，第二层Key是dbSchemaId，数据源对象跟DBSchema对象一一对应。当采用Spring封装方案时需要实现。
	private static Map<String, Map<String, JdbcTemplate>> jdbcTemplateMap = null;
	
	//用来存放数据库连接Connection对象，当开启事务或第一次获取数据库连接时存放到该属性中，在连接关闭时移除。
	private static ThreadLocal<Connection> connectionHolder = null;
	
	private static final String DS_CONTAINER_MANAGED = "containerManaged";
	private static final String DS_CONTAINER_MANAGED_DATASOURCE = "containerManagedDatasource";
	private static final String DS_IS_DEFAULT = "isDefault";
	
	
	
	/**
	 * 用于初始化DBSchemaProxy，传入参数是业务系统标识和租户标识，当部署环境不同时，有不同的逻辑：
	 * 当部署环境是开发环境、测试环境或演示环境，这两个参数都传空，表示加载所有业务系统的配置；
	 * 当部署环境是生产环境时，这两个参数都不为空，表示加载对应业务系统的配置。
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		
		if(!MapUtils.isEmpty(dataSourceMap)) {//刷新缓存时释放数据源
			if (logger.isDebugEnabled()) {
				logger.debug("release old engine managed datasource...");
			}
			for(String system : dataSourceMap.keySet()) {
				Map<String, DataSource> map = dataSourceMap.get(system);
				for(String key : map.keySet()) {
					//Spring容器管理的数据源，不做关闭处理
					if(isSpringContainerManaged(getDbSchema(system, key))) {
						continue;
					}
					HikariDataSource ds = (HikariDataSource) map.get(key);
					ds.close();
				}
			}
		}
		
		dbSchemaMap = new HashMap<>();
		dataSourceMap = new HashMap<>();
		transactionTemplateMap = new HashMap<>();
		jdbcTemplateMap = new HashMap<>();
		connectionHolder = new ThreadLocal<>();
		
		
		//获取DBSchema列表，状态为有效的记录
		List<DbSchema> dbSchemas = null;
		
		if(StringUtils.isBlank(systemId)) {//加载所有的记录
			dbSchemas = initService.listDbSchemas();
		}else {//加载指定业务系统的记录
			dbSchemas = initService.listDbSchemaBySystemId(systemId);
		}
		//如果获取的列表为空，则直接返回
		if(CollectionUtils.isEmpty(dbSchemas)) {
			logger.info("no dbschema is configured. return.");
			return;
		}
		
		//循环处理每个数据库的配置，并进行初始化
		for(DbSchema dbSchema : dbSchemas) {
			//如果systemId为空，则忽略
			if(StringUtils.isBlank(dbSchema.getSystemId())) {
				logger.error("dbSchema({})'s systemId is null", dbSchema.getSystemId());
				continue;
			}
			
			//缓存dbSchema
			Map<String, DbSchema> map = dbSchemaMap.get(dbSchema.getSystemId());
			if(map == null) {
				map = new HashMap<>();
				dbSchemaMap.put(dbSchema.getSystemId(), map);
			}
			map.put(dbSchema.getId(), dbSchema);
			
			if (!DBType.DB_TYPE_MYSQL.equals(dbSchema.getDbType())
					&& !DBType.DB_TYPE_ORACLE.equals(dbSchema.getDbType())) {
				logger.error("The dbType({}) is not supported.", dbSchema.getDbType());
				continue;
			}

			// 准备dataSource初始化参数
			if (StringUtils.isBlank(dbSchema.getUrl())) {
				continue;
			}

			// 初始化DataSource并缓存
			try {
				DataSource ds = createDataSource(dbSchema);
				// 缓存DataSource
				Map<String, DataSource> dsMap = dataSourceMap.get(dbSchema.getSystemId());
				if (dsMap == null) {
					dsMap = new HashMap<>();
					dataSourceMap.put(dbSchema.getSystemId(), dsMap);
				}
				dsMap.put(dbSchema.getId(), ds);

				// 初始化TransactionTemplate
				TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
				// 缓存TransactionTemplate
				Map<String, TransactionTemplate> txMap = transactionTemplateMap.get(dbSchema.getSystemId());
				if (txMap == null) {
					txMap = new HashMap<>();
					transactionTemplateMap.put(dbSchema.getSystemId(), txMap);
				}
				txMap.put(dbSchema.getId(), transactionTemplate);

				// 初始化JdbcTemplate
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				// 缓存JdbcTemplate
				Map<String, JdbcTemplate> jtMap = jdbcTemplateMap.get(dbSchema.getSystemId());
				if (jtMap == null) {
					jtMap = new HashMap<>();
					jdbcTemplateMap.put(dbSchema.getSystemId(), jtMap);
				}
				jtMap.put(dbSchema.getId(), jdbcTemplate);
			} catch (Exception e) {
				logger.error("init DataSource for dbSchema({}) failed. Exception: ", dbSchema.getId(), e);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * @param systemId 业务系统标识
	 * @param tenantId 租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public synchronized static void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}
		
		if(!MapUtils.isEmpty(dataSourceMap)) {//刷新缓存时释放数据源
			if (logger.isDebugEnabled()) {
				logger.debug("release old engine managed datasource...");
			}
			Map<String, DataSource> oldMap = dataSourceMap.get(systemId);
			if(!MapUtils.isEmpty(oldMap)) {
				for(String key : oldMap.keySet()) {
					//Spring容器管理的数据源，不做关闭处理
					if(isSpringContainerManaged(getDbSchema(systemId, key))) {
						continue;
					}
					HikariDataSource ds = (HikariDataSource) oldMap.get(key);
					ds.close();
				}
			}
			
		}
		
		//获取DBSchema列表，状态为有效的记录
		List<DbSchema> dbSchemas = initService.listDbSchemaBySystemId(systemId);
		Map<String, DbSchema> map = new HashMap<>();
		Map<String, DataSource> dsMap = new HashMap<>();
		Map<String, TransactionTemplate> txMap = new HashMap<>();
		Map<String, JdbcTemplate> jtMap = new HashMap<>();
		//循环处理每个数据库的配置，并进行初始化
		if(!CollectionUtils.isEmpty(dbSchemas)) {
			for(DbSchema dbSchema : dbSchemas) {
				//如果systemId为空，则忽略
				if(StringUtils.isBlank(dbSchema.getSystemId())) {
					logger.error("dbSchema({})'s systemId is null", dbSchema.getSystemId());
					continue;
				}
				
				//缓存dbSchema
				map.put(dbSchema.getId(), dbSchema);
				
				if (!DBType.DB_TYPE_MYSQL.equals(dbSchema.getDbType())
						&& !DBType.DB_TYPE_ORACLE.equals(dbSchema.getDbType())) {
					logger.error("The dbType({}) is not supported.", dbSchema.getDbType());
					continue;
				}
	
				// 准备dataSource初始化参数
				if (StringUtils.isBlank(dbSchema.getUrl())) {
					continue;
				}
	
				// 初始化DataSource并缓存
				try {
					DataSource ds = createDataSource(dbSchema);
					// 缓存DataSource
					dsMap.put(dbSchema.getId(), ds);
	
					// 初始化TransactionTemplate
					TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
					// 缓存TransactionTemplate
					txMap.put(dbSchema.getId(), transactionTemplate);
	
					// 初始化JdbcTemplate
					JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
					// 缓存JdbcTemplate
					jtMap.put(dbSchema.getId(), jdbcTemplate);
				} catch (Exception e) {
					logger.error("init DataSource for dbSchema({}) failed. Exception: ", dbSchema.getId(), e);
				}
			}
		}
		dbSchemaMap.put(systemId, map);
		dataSourceMap.put(systemId, dsMap);
		transactionTemplateMap.put(systemId, txMap);
		jdbcTemplateMap.put(systemId, jtMap);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 创建DataSource
	 * @param dbSchema
	 * @return
	 * @throws EngineException
	 */
	public static DataSource createDataSource(DbSchema dbSchema) throws EngineException {
		if(!DBType.DB_TYPE_MYSQL.equals(dbSchema.getDbType()) && ! DBType.DB_TYPE_ORACLE.equals(dbSchema.getDbType())) {
			logger.error("The dbType({}) is not supported.", dbSchema.getDbType());
			return null;
		}
		
		//准备dataSource初始化参数
		if(StringUtils.isBlank(dbSchema.getUrl())) {
			logger.error("The url of dbSchema({}) is not supported.", dbSchema.getId());
			return null;
		}
		DataSourceBuilder<?> builder = DataSourceBuilder.create();
		builder.url(dbSchema.getUrl());
		
		if(!StringUtils.isBlank(dbSchema.getDriverClass())) {
			builder.driverClassName(dbSchema.getDriverClass());
		}
		if(!StringUtils.isBlank(dbSchema.getUsername())) {
			builder.username(dbSchema.getUsername());
		}
		if(!StringUtils.isBlank(dbSchema.getPassword())) {
			builder.password(dbSchema.getPassword());
		}
		
		boolean containerManaged = false;
		String containerManagedDatasource = null;
		if(!StringUtils.isBlank(dbSchema.getDataSourceCfg())) {
			JsonObject dsCfg = JsonUtils.toJsonObject(dbSchema.getDataSourceCfg());
			for(String key :dsCfg.keySet()) {
				if(DS_CONTAINER_MANAGED.equals(key)) {
					containerManaged = JsonUtils.getBoolean(dsCfg, DS_CONTAINER_MANAGED);
				} else if(DS_CONTAINER_MANAGED_DATASOURCE.equals(key)) {
					containerManagedDatasource = JsonUtils.getString(dsCfg, DS_CONTAINER_MANAGED_DATASOURCE);
				}
			}
		}
		
		//初始化DataSource并返回
		try {
			if(containerManaged) {//从Spring容器中获取DS
				if(logger.isDebugEnabled()) {
					logger.debug("get ds from SpringContainer by DataSource class or name({})", containerManagedDatasource);
				}
				if(containerManagedDatasource == null || containerManagedDatasource.trim().length() == 0) {
					return SpringContextUtils.getBean(DataSource.class);
				}else {
					return (DataSource)SpringContextUtils.getBean(containerManagedDatasource);
				}
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("init DataSource。");
				}
				//初始化DS
				return builder.build();
			}
		}catch(Exception e) {
			logger.error("init DataSource for dbSchema({}) failed. Exception: ", dbSchema.getId(),e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "init DataSource for dbSchema failed");
		}
	}
	
	public static Connection createConnection(DbSchema dbSchema) throws EngineException {
		if(!DBType.DB_TYPE_MYSQL.equals(dbSchema.getDbType()) && ! DBType.DB_TYPE_ORACLE.equals(dbSchema.getDbType())) {
			logger.error("The dbType({}) is not supported.", dbSchema.getDbType());
			return null;
		}
		
		//准备dataSource初始化参数
		if(StringUtils.isBlank(dbSchema.getUrl())) {
			logger.error("The url of dbSchema({}) is not supported.", dbSchema.getId());
			return null;
		}
		
		try {
			if(!StringUtils.isBlank(dbSchema.getDriverClass())) {
				Class.forName(dbSchema.getDriverClass());
			}else {
				if(DBType.DB_TYPE_ORACLE.equals(dbSchema.getDbType())) {
					Class.forName("oracle.jdbc.driver.OracleDriver");
				}else {
					Class.forName("com.mysql.cj.jdbc.Driver");
				}
			}
		} catch (ClassNotFoundException e1) {
			logger.error("load db driver failed: {} of {}", dbSchema.getDriverClass(),dbSchema.getDbType());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "load db driver failed!");
		}
		
		boolean containerManaged = false;
		String containerManagedDatasource = null;
		if(!StringUtils.isBlank(dbSchema.getDataSourceCfg())) {
			JsonObject dsCfg = JsonUtils.toJsonObject(dbSchema.getDataSourceCfg());
			for(String key :dsCfg.keySet()) {
				if(DS_CONTAINER_MANAGED.equals(key)) {
					containerManaged = JsonUtils.getBoolean(dsCfg, DS_CONTAINER_MANAGED);
				} else if(DS_CONTAINER_MANAGED_DATASOURCE.equals(key)) {
					containerManagedDatasource = JsonUtils.getString(dsCfg, DS_CONTAINER_MANAGED_DATASOURCE);
				} 
			}
		}
		
		//初始化DataSource并返回
		try {
			if(containerManaged) {//从Spring容器中获取DS
				if(logger.isDebugEnabled()) {
					logger.debug("get ds from SpringContainer by DataSource class or name({})", containerManagedDatasource);
				}
				if(StringUtils.isBlank(containerManagedDatasource)) {
					return SpringContextUtils.getBean(DataSource.class).getConnection();
				}else {
					return ((DataSource)SpringContextUtils.getBean(containerManagedDatasource)).getConnection();
				}
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("get Connection。");
				}
				//初始化Connection
				return DriverManager.getConnection(dbSchema.getUrl(), dbSchema.getUsername(), dbSchema.getPassword());
			}
		}catch(Exception e) {
			logger.error("get Connection for dbSchema({}) failed. Exception: ", dbSchema.getId(),e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "get Connection for dbSchema failed");
		}
	}
	
	/**
	 * 是否是Spring容器管理的数据源
	 * @param dbSchema
	 * @return
	 */
	public static boolean isSpringContainerManaged(DbSchema dbSchema) {
		boolean containerManaged = false;
		if(!StringUtils.isBlank(dbSchema.getDataSourceCfg())) {
			JsonObject dsCfg = JsonUtils.toJsonObject(dbSchema.getDataSourceCfg());
			containerManaged = JsonUtils.getBoolean(dsCfg, DS_CONTAINER_MANAGED);
		}
		return containerManaged;
	}
	
	/**
	 * 根据systemId和dbSchemaId，从dbSchemaMap中获取DBSchema对象并返回
	 * @param systemId 业务系统标识
	 * @param dbSchemaId 
	 * @return DbSchema对象
	 */
	public static DbSchema getDbSchema(String systemId, String dbSchemaId) {
		Map<String, DbSchema> map = dbSchemaMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(dbSchemaId);
	}
	
	/**
	 * 根据systemId和dbSchemaId，从dataSourceMap中获取数据源对象并返回
	 * @param systemId
	 * @param dbSchemaId
	 * @return 返回对应的数据源对象
	 */
	public static DataSource getDataSource(String systemId, String dbSchemaId) {
		Map<String, DataSource> map = dataSourceMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(dbSchemaId);
	}
	
	/**
	 * 根据systemId和dbSchemaId，从transactionTemplateMap中获取JdbcTemplate对象并返回
	 * @param systemId
	 * @param dbSchemaId
	 * @return 返回对应的JdbcTemplate对象
	 */
	public static TransactionTemplate getTransactionTemplate(String systemId, String dbSchemaId) {
		Map<String, TransactionTemplate> map = transactionTemplateMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(dbSchemaId);
	}
	
	/**
	 * 根据systemId和dbSchemaId，从jdbcTemplateMap中获取JdbcTemplate对象并返回
	 * @param systemId
	 * @param dbSchemaId
	 * @return 返回对应的JdbcTemplate对象
	 */
	public static JdbcTemplate getJdbcTemplate(String systemId, String dbSchemaId) {
		Map<String, JdbcTemplate> map = jdbcTemplateMap.get(systemId);
		if(MapUtils.isEmpty(map)) {
			return null;
		}
		if(!StringUtils.isBlank(dbSchemaId)) {
			return map.get(dbSchemaId);
		}else {//取默认的数据源
			Map<String, DbSchema> dsMap = dbSchemaMap.get(systemId);
			String defaultSchemaId = null;
			for(Entry<String, DbSchema> entry : dsMap.entrySet()) {
				DbSchema db = entry.getValue();
				if(!StringUtils.isBlank(db.getDataSourceCfg())) {
					JsonObject dsCfg = JsonUtils.toJsonObject(db.getDataSourceCfg());
					if(dsCfg.has(DS_IS_DEFAULT) && JsonUtils.getBoolean(dsCfg, DS_IS_DEFAULT)) {
						defaultSchemaId = db.getId();
						break;
					}
				}
				if(StringUtils.isBlank(defaultSchemaId)) {
					defaultSchemaId = db.getId();
				}
			}
			if(StringUtils.isBlank(defaultSchemaId)) {
				return null;
			}else {
				return map.get(defaultSchemaId);
			}
		}
		
	}
	
	/**
	 * 获取对应数据库的数据库连接, 当采用JDBC原生方案时需要实现。
	 * @param systemId
	 * @param dbSchemaId
	 * @return 返回数据库连接
	 */
	public static Connection getConnection(String systemId, String dbSchemaId) {
		//先尝试从connectionHolder中获取
		Connection connection = connectionHolder.get();
		//如果获取不到，则从DS中获取
		if(connection == null) {
			try {
				connection = getDataSource(systemId, dbSchemaId).getConnection();
			} catch (SQLException e) {
				logger.error("get Connection failed. Exception: {}", e);
			}
		}
		return connection;
	}

	/**
	 * 关闭连接
	 * @param connection
	 */
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}
	
	/**
	 * 用于开启事务, 当采用JDBC原生方案时需要实现。
	 * @throws EngineException
	 */
	public static void beginTransaction() throws EngineException {
		//从connectionHolder中获取
		Connection connection = connectionHolder.get();
		//如果连接为空，直接抛出异常
		if(connection == null) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Connection is null, please get a connection first.");
		}
		try {//开启事务
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "begin transaction failed.", e.getMessage(), e);
		}
	}
	
	/**
	 * 用于提交事务, 当采用JDBC原生方案时需要实现。
	 * @throws EngineException
	 */
	public static void commitTransaction() throws EngineException {
		//从connectionHolder中获取
		Connection connection = connectionHolder.get();
		//如果连接为空，直接抛出异常
		if(connection == null) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Connection is null, please get a connection first.");
		}
		try {//提交事务
			connection.commit();
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "commit transaction failed.", e.getMessage(), e);
		}
	}
	
	/**
	 * 用于回滚事务, 当采用JDBC原生方案时需要实现。
	 * @throws EngineException
	 */
	public static void rollbackTransaction() throws EngineException {
		//从connectionHolder中获取
		Connection connection = connectionHolder.get();
		//如果连接为空，直接抛出异常
		if(connection == null) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "Connection is null, please get a connection first.");
		}
		try {//回滚事务
			connection.rollback();
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "rollback transaction failed.", e.getMessage(), e);
		}
	}
	
	/**
	 * 用于关闭连接, 当采用JDBC原生方案时需要实现。
	 * @throws EngineException
	 */
	public static void closeTransaction() throws EngineException {
		//从connectionHolder中获取
		Connection connection = connectionHolder.get();
		//如果连接为空
		if(connection == null) {
			logger.info("connection is null, when try to close connection. return");
			return;
		}
		try {//关闭连接
			connection.close();
		} catch (SQLException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "close connection failed.", e.getMessage(), e);
		}finally {
			connectionHolder.remove();
		}
	}
}
