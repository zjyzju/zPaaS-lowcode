package cn.zpaas.lowcode.be.engine.flow.node;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.ability.PageAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.be.engine.proxy.SqlManagementProxy;
import cn.zpaas.lowcode.bean.PageParam;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * SQL查询类节点的实现类，是Node类的子类，核心业务逻辑为执行一个SQL语句，SQL语句用到的条件数据可以从传入参数或前置节点设置的过程数据中获取，
 * 执行SQL语句获得的结果集经过数据格式转换后，作为过程数据存到业务流上下文对象中或者作为业务流的返回结果返回。
 */
public class SqlQueryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(SqlQueryNode.class);

	public static final String CFG_SQL_ID_KEY = "sqlId"; //sqlId存放的Key
	public static final String CFG_PAGE_FLAG_KEY = "pageFlag"; //sqlId存放的Key
	public static final String CFG_SQL_SENTENCE_KEY = "sqlSentence"; //临时存放sql存放的Key
	
	private static String PAGED_QUERY_EXPR = " limit ?, ?";
	private static String PAGED_QUERY_TOTAL_BEGIN_EXPR = "select count(*) from ( ";
	private static String PAGED_QUERY_TOTAL_END_EXPR = ") a";
	
	
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		String sqlId = JsonUtils.getString(nodeCfg, CFG_SQL_ID_KEY);
		boolean isListResult = JsonUtils.getBoolean(nodeCfg, IS_LIST_RESULT_KEY);
		boolean pageFlag = JsonUtils.getBoolean(nodeCfg, CFG_PAGE_FLAG_KEY);
		
		//sqlId不能为空
		if(StringUtils.isBlank(sqlId)) {
			logger.error("T[{}] sqlId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sqlId  can't be null. ");
		}
		
		//根据配置获取输入参数
		JsonObject param = (JsonObject)context.getAttribute(NODE_PARAM_KEY);
		
		SqlManagement sqlManagement = SqlManagementProxy.getSqlManagement(context.getSystemId(), sqlId);
		if(sqlManagement == null || StringUtils.isBlank(sqlManagement.getSqlSentence())) {
			logger.error("T[{}] get sql failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "get sql failed.");
		}
		//检查并转换nodeParams，key必须是Integer
		final Map<Integer, Object> paramMap = new HashMap<>();
		Integer maxIndex = 0;
		if(param != null) {
			Iterator<String> iterator = param.keySet().iterator(); 
			String key = null;
			try {
				Integer index;
				while(iterator.hasNext()) {
					key = iterator.next();
					index = Integer.valueOf(key);
					paramMap.put(index, JsonUtils.getObject(param, key));
					if(maxIndex < index) {
						maxIndex = index;
					}
				}
			} catch (NumberFormatException e) {
				logger.error("T[{}] nodeParams's key must be Integer. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "nodeParams's key must be Integer.");
			}
		}
		
		String sql = sqlManagement.getSqlSentence();
		//获取对应的JdbcTemplate
		JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(context.getSystemId(), context.getDbSchemaId());
		//增加分页能力
				if(pageFlag && PageAbility.getPageParam() != null) {
					PageParam pageParam = PageAbility.getPageParam();
					//查询总记录数
					if(pageParam.isQueryTotal()) {
						String queryTotalSql = PAGED_QUERY_TOTAL_BEGIN_EXPR + sql + PAGED_QUERY_TOTAL_END_EXPR;		
						if (logger.isDebugEnabled()) {
							logger.debug("T[{}] queryTotal sql: {}", businessFlowNode.getTenantId(), queryTotalSql);
							logger.debug("T[{}] values: {}", businessFlowNode.getTenantId(), paramMap);
						}
						Long total = jdbcTemplate.query(queryTotalSql, new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								if(paramMap != null && paramMap.size() > 0) {
									paramMap.keySet().forEach((key) -> {
										try {
											ps.setObject(key, paramMap.get(key));
										} catch (Exception e) {
											e.printStackTrace();
										} 
									});
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
						sql = sql + PAGED_QUERY_EXPR;
						paramMap.put(++maxIndex, start);
						paramMap.put(++maxIndex, pageRows);
					}
				}
		
		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] query sql: {}", businessFlowNode.getTenantId(), sql);
			logger.debug("T[{}] values: {}", businessFlowNode.getTenantId(), paramMap);
		}
		
		// 使用jdbcTemplate执行sql、绑定参数并获得执行结果
		List<JsonObject> resultList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if(paramMap != null && paramMap.size() > 0) {
					paramMap.keySet().forEach((key) -> {
						try {
							ps.setObject(key, paramMap.get(key));
						} catch (Exception e) {
							e.printStackTrace();
						} 
					});
				}
			}
		}, new RowMapper<JsonObject>() {
			@Override
			public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
				JsonObject objectContainer = new JsonObject();
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				int size = resultSetMetaData.getColumnCount();
				for(int i=1; i<=size; i++) {
					objectContainer.add(resultSetMetaData.getColumnLabel(i), JsonUtils.toJsonElement(rs.getObject(i)));
				}				
				return objectContainer;
			}
		});
		
		Object result = null;
		if (!CollectionUtils.isEmpty(resultList)) {
			if (isListResult) {//返回结果是列表类型
				// 转换成JsonArray类型并返回
				JsonArray resutArray = new JsonArray();
				for (JsonObject rowJsonObject : resultList) {
					resutArray.add(rowJsonObject);
				}
				result = resutArray;
			} else {
				result = resultList.get(0);
			}
		}
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

	@Override
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {
		super.nodeCfgLoad(nodeCfg, node, info, businessFlowService);
		String sqlId = JsonUtils.getString(nodeCfg, SqlQueryNode.CFG_SQL_ID_KEY);
		nodeCfg.remove(CFG_SQL_SENTENCE_KEY);
		if(sqlId != null && sqlId.trim().length() > 0) {
			SqlManagement sql = businessFlowService.loadSqlManagement(sqlId);
			if(sql != null) {
				nodeCfg.addProperty(SqlQueryNode.CFG_SQL_SENTENCE_KEY, sql.getSqlSentence());
			}
			node.setNodeCfg(JsonUtils.toJson(nodeCfg));
		}
	}

	@Override
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService)
			throws EngineException {
		super.beforeSave(node, info, businessFlowService);
		businessFlowService.nodeSaveSqlManagement(node, info, businessFlowService, ServerResourceType.RESOURCE_TYPE_DATABASE, info.getBusinessFlow().getDbSchemaId(), CFG_SQL_ID_KEY, CFG_SQL_SENTENCE_KEY);
	}

	
}
