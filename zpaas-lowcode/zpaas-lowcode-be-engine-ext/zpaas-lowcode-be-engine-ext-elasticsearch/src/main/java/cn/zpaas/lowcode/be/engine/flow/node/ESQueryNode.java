package cn.zpaas.lowcode.be.engine.flow.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.ability.ElasticSearchRepositoryAbility;
import cn.zpaas.lowcode.be.engine.ability.PageAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.SqlManagementProxy;
import cn.zpaas.lowcode.bean.PageParam;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.exception.EngineException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ES查询类节点的实现类
 * @author zjy
 *
 */
public class ESQueryNode extends Node {
	
	private static Logger logger = LoggerFactory.getLogger(ESQueryNode.class);

	public static final String CFG_SQL_ID_KEY = "sqlId"; //sqlId存放的Key
	public static final String CFG_RESOURCE_ID_KEY = "resourceId"; //resourceId存放的Key
	public static final String CFG_PAGE_FLAG_KEY = "pageFlag"; //sqlId存放的Key
	public static final String CFG_SQL_SENTENCE_KEY = "sqlSentence"; //临时存放sql存放的Key

    public static final String ES_QUERY_BODY = "body"; //ES请求的body节点
    public static final String ES_QUERY_FROM = "from";//ES请求的from节点
    public static final String ES_QUERY_SIZE = "size";//ES请求的size节点
    
    public static final String ES_RESULT_AGGREGATIONS = "aggregations";  //ES返回结果中的aggregations节点
    public static final String ES_RESULT_AGGS = "aggs"; //ES返回结果中的aggs节点
    public static final String ES_RESULT_BUCKETS = "buckets"; //ES返回结果中的buckets节点    
    public static final String ES_RESULT_DETAIL_RESULT = "detailResult"; //ES返回结果中的detailResult节点
    public static final String ES_RESULT_KEY = "key"; //ES返回结果中的key节点
    public static final String ES_RESULT_DOC_COUNT = "doc_count"; //ES返回结果中的doc_count节点
    public static final String ES_RESULT_VALUE = "value"; //ES返回结果中的value节点
    public static final String ES_RESULT_VALUES = "values"; //ES返回结果中的values节点
    public static final String ES_RESULT_HITS = "hits"; //ES返回结果中的hits节点
    public static final String ES_RESULT_SOURCE = "_source"; //ES返回结果中的_source节点
    public static final String ES_RESULT_TOTAL = "total"; //ES返回结果中的total节点
    public static final String ES_RESULT_COUNT = "count"; //ES返回结果中的count节点
    public static final String ES_RESULT_MIN = "min"; //ES返回结果中的max节点
    public static final String ES_RESULT_MAX = "max"; //ES返回结果中的max节点
    public static final String ES_RESULT_AVG = "avg"; //ES返回结果中的avg节点
    public static final String ES_RESULT_SUM = "sum"; //ES返回结果中的sum节点
    
    public static final String ES_QUERY_LEFT_BRACE = "#{"; //ES请求的#{
    public static final String ES_QUERY_RIGHT_BRACE = "}"; //ES请求的}
    

    
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
		String resourceId = JsonUtils.getString(nodeCfg, CFG_RESOURCE_ID_KEY);
//		boolean isListResult = nodeCfg.getBooleanValue(IS_LIST_RESULT_KEY);
		boolean pageFlag = JsonUtils.getBoolean(nodeCfg, CFG_PAGE_FLAG_KEY);
		
		//sqlId不能为空
		if(StringUtils.isBlank(sqlId) || StringUtils.isBlank(resourceId)) {
			logger.error("T[{}] sqlId and resourceId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sqlId and resourceId can't be null. ");
		}
		
		//根据配置获取输入参数
		JsonObject param = (JsonObject)context.getAttribute(NODE_PARAM_KEY);
		//获取ES查询语句
		SqlManagement sqlManagement = SqlManagementProxy.getSqlManagement(context.getSystemId(), sqlId);
		if(sqlManagement == null || StringUtils.isBlank(sqlManagement.getSqlSentence())) {
			logger.error("T[{}] get sql failed. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "get sql failed.");
		}
		String sqlSentence = sqlManagement.getSqlSentence();
		resourceId = sqlManagement.getResourceId();
		
		//替换参数
		if(!JsonUtils.isEmpty(param)) {
			for (Map.Entry<String, JsonElement> entry : param.entrySet()) {
				StringBuilder sb = new StringBuilder();
	            String key = entry.getKey();
	            String value = entry.getValue().getAsString();
	            sb.append(ES_QUERY_LEFT_BRACE).append(key).append(ES_QUERY_RIGHT_BRACE);
	            sqlSentence = sqlSentence.replaceAll(sb.toString(), value);
	        }
		}
		
        JsonObject queryData = JsonUtils.toJsonObject(sqlSentence);
		JsonObject queryBody = JsonUtils.getJsonObject(queryData, ES_QUERY_BODY);
		// 增加分页能力
		if (pageFlag && PageAbility.getPageParam() != null) {
			PageParam pageParam = PageAbility.getPageParam();

			// 当每页数据小于等于0时，不进行分页处理
			if (PageAbility.getPageParam().getPageRows() > 0) {
				// 处理分页数据
				int pageNo = pageParam.getPageNo();
				int pageRows = pageParam.getPageRows();
				if (pageNo == 0) {
					pageNo = 1;
				}
				long start = (pageNo - 1) * pageRows;
				queryBody.addProperty(ES_QUERY_FROM, start);
				queryBody.addProperty(ES_QUERY_SIZE, pageRows);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("T[{}] execute es sql: {}", businessFlowNode.getTenantId(), sqlSentence);
		}
		// 执行查询
		String url = ElasticSearchRepositoryAbility.getInstance().getSearchUrl(queryData, businessFlowNode.getSystemId(),resourceId);
        JsonObject response = ElasticSearchRepositoryAbility.getInstance().post(url, queryBody, businessFlowNode.getSystemId(),resourceId);
        JsonObject result = processQueryResult(queryBody, response, pageFlag);
       
        //将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
    }


    /**
     * 获取查询结果
     *
     * @param queryBody 查询语句
     * @param response  ES详细结果
     * @return 转换后结果
     */
    private JsonObject processQueryResult(JsonObject queryBody, JsonObject response, boolean pageFlag) {
        JsonObject result = new JsonObject();
        List<JsonObject> sourceList = processHitResult(response, pageFlag);
        if (!CollectionUtils.isEmpty(sourceList)) {
            result.add(ES_RESULT_DETAIL_RESULT, JsonUtils.toJsonElement(sourceList));
        }
        if (queryBody.has(ES_RESULT_AGGS) || queryBody.has(ES_RESULT_AGGREGATIONS)) {
            // 获取查询结果中聚合子节点
            JsonObject subResultAggs = JsonUtils.getJsonObject(response, ES_RESULT_AGGREGATIONS);
            if (subResultAggs != null) {
                // 获取查询json中聚合定义节点
                JsonObject subQueryAggs = getSubQueryAggs(queryBody);
                // 处理聚合结果
                processAggsResult(subQueryAggs, subResultAggs, result);
            }
        }
        return result;
    }

    /**
     * 处理聚合查询结果
     * @param queryAggs
     * @param resultAgg
     * @param result
     */
    private void processAggsResult(JsonObject queryAggs, JsonObject resultAggs, JsonObject result) {
    	//queryAggs为空时直接返回
    	if(JsonUtils.isEmpty(queryAggs)) {
    		return;
    	}
    	
        for (Map.Entry<String, JsonElement> queryEntry : queryAggs.entrySet()) {
            String aggsKey = queryEntry.getKey();
            JsonObject queryAggsNode =  queryEntry.getValue().getAsJsonObject();
            JsonObject resultAggsNode = JsonUtils.getJsonObject(resultAggs, aggsKey);
            //取不到对应的Aggs结果节点，表示不是Aggs的名称，继续下次循环
            if (resultAggsNode == null) {
                continue;
            }
            
            //桶聚合
            if (resultAggsNode.has(ES_RESULT_BUCKETS)) {
                JsonArray buckets = JsonUtils.getJsonArray(resultAggsNode, ES_RESULT_BUCKETS);
                if (buckets == null) {
                    //没有查询结果
                    result.add(aggsKey, JsonUtils.toJsonElement(new JsonArray()));
                    continue;
                }
                //保存所有桶处理结果的列表
                List<JsonObject> bucketList = new ArrayList<>();
                //循环处理每个桶
                buckets.forEach(bucket -> {
                    JsonObject bucketNode = (JsonObject) bucket;
                    //保存当前桶的处理结果
                    JsonObject bucketResult = new JsonObject();
                    bucketResult.addProperty(ES_RESULT_KEY, JsonUtils.getString(bucketNode, ES_RESULT_KEY));
                    bucketResult.addProperty(ES_RESULT_DOC_COUNT, JsonUtils.getLong(bucketNode, ES_RESULT_DOC_COUNT));
                    //桶下还有嵌套的聚合
                    if (queryAggsNode.has(ES_RESULT_AGGS) || queryAggsNode.has(ES_RESULT_AGGREGATIONS)) {
                    	//获取查询体中聚合子节点
                    	JsonObject subQueryAggs = getSubQueryAggs(queryAggsNode);
                    	//递归调用进行子聚合处理
                        processAggsResult(subQueryAggs, bucketNode, bucketResult);
                    }
                    bucketList.add(bucketResult);
                });
                result.add(aggsKey, JsonUtils.toJsonElement(bucketList));
            }else {
	            
	            if (resultAggsNode.get(ES_RESULT_HITS)!=null) {            	
	                List<JsonObject> resultList = new ArrayList<>();
	                JsonArray hits = JsonUtils.getJsonArray(JsonUtils.getJsonObject(resultAggsNode, ES_RESULT_HITS), ES_RESULT_HITS);
	                hits.forEach(hit -> resultList.add(JsonUtils.getJsonObject(hit.getAsJsonObject(), ES_RESULT_SOURCE)));
	                result.add(aggsKey, JsonUtils.toJsonElement(resultList));
	            } else {
	            	//保存当前Aggs解析结果的对象
	            	JsonObject currAggsResult = new JsonObject();
	            	
	            	
	                if (resultAggsNode.get(ES_RESULT_VALUE)!=null) {            	
	                	currAggsResult.addProperty(ES_RESULT_VALUE, JsonUtils.getFloat(resultAggsNode, ES_RESULT_VALUE));
	                }  
	                
	                if (resultAggsNode.get(ES_RESULT_VALUES)!=null) {           	
	                    currAggsResult.addProperty(ES_RESULT_VALUES, JsonUtils.getFloat(resultAggsNode, ES_RESULT_VALUES));
	                } 
	            	
	            	if (resultAggsNode.get(ES_RESULT_COUNT)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_COUNT, JsonUtils.getFloat(resultAggsNode, ES_RESULT_COUNT));
	                }
	            	if (resultAggsNode.get(ES_RESULT_MIN)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_MIN, JsonUtils.getFloat(resultAggsNode, ES_RESULT_MIN));
	                }
	            	if (resultAggsNode.get(ES_RESULT_MAX)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_MAX, JsonUtils.getFloat(resultAggsNode, ES_RESULT_MAX));
	                }
	            	if (resultAggsNode.get(ES_RESULT_AVG)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_AVG, JsonUtils.getFloat(resultAggsNode, ES_RESULT_AVG));
	                }
	            	if (resultAggsNode.get(ES_RESULT_SUM)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_SUM, JsonUtils.getFloat(resultAggsNode, ES_RESULT_SUM));
	                }
	            	
	            	if (resultAggsNode.get(ES_RESULT_DOC_COUNT)!=null) {
	            		currAggsResult.addProperty(ES_RESULT_DOC_COUNT, JsonUtils.getLong(resultAggsNode, ES_RESULT_DOC_COUNT));
	                }
	            	//聚合下还有嵌套的聚合
                    if (queryAggsNode.has(ES_RESULT_AGGS) || queryAggsNode.has(ES_RESULT_AGGREGATIONS)) {
                    	//获取查询体中聚合子节点
                    	JsonObject subQueryAggs = getSubQueryAggs(queryAggsNode);
                    	//递归调用进行子聚合处理
                        processAggsResult(subQueryAggs, resultAggsNode, currAggsResult);
                    }
	            	
	            	result.add(aggsKey, JsonUtils.toJsonElement(currAggsResult));
	            }
	        }
        }
    }

    /**
     * 获取满足条件的查询结果
     * @param response
     * @param pageFlag
     * @return
     */
    private List<JsonObject> processHitResult(JsonObject response, boolean pageFlag) {
        JsonObject hits = JsonUtils.getJsonObject(response, ES_RESULT_HITS);
        List<JsonObject> sourceList = new ArrayList<>();
        if (hits != null) {
            JsonArray hitsArr = JsonUtils.getJsonArray(hits, ES_RESULT_HITS);
            if (hitsArr != null && hits.size() != 0) {
                hitsArr.forEach(hit -> sourceList.add(JsonUtils.getJsonObject(hit.getAsJsonObject(), ES_RESULT_SOURCE)));
            }
            // 增加分页能力
    		if (pageFlag && PageAbility.getPageParam() != null) {
    			PageParam pageParam = PageAbility.getPageParam();
    			JsonObject totalAttr = JsonUtils.getJsonObject(hits, ES_RESULT_TOTAL);
    			if(totalAttr != null) {
    				pageParam.setTotal(JsonUtils.getLong(totalAttr, ES_RESULT_VALUE));
    			}
    		}
        }
        return sourceList;
    }


    /**
     * 获取查询语句中聚合语句子节点
     * @param queryBody
     * @return
     */
    private JsonObject getSubQueryAggs(JsonObject queryBody) {
        JsonObject subQueryAggs = JsonUtils.getJsonObject(queryBody, ES_RESULT_AGGREGATIONS);
        if (subQueryAggs == null) {
        	subQueryAggs = JsonUtils.getJsonObject(queryBody, ES_RESULT_AGGS);
        }
        return subQueryAggs;
    }


	@Override
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {
		super.nodeCfgLoad(nodeCfg, node, info, businessFlowService);
		String sqlId = JsonUtils.getString(nodeCfg, CFG_SQL_ID_KEY);
		nodeCfg.remove(CFG_SQL_SENTENCE_KEY);
		if(sqlId != null && sqlId.trim().length() > 0) {
			SqlManagement sql = businessFlowService.loadSqlManagement(sqlId);
			if(sql != null) {
				nodeCfg.addProperty(CFG_SQL_SENTENCE_KEY, sql.getSqlSentence());
			}
			node.setNodeCfg(JsonUtils.toJson(nodeCfg));
		}
	}

	@Override
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService)
			throws EngineException {
		super.beforeSave(node, info, businessFlowService);
		String nodeCfgString = node.getNodeCfg();
		if(!StringUtils.isBlank(nodeCfgString)) {
			JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
			businessFlowService.nodeSaveSqlManagement(node, info, businessFlowService, ServerResourceType.RESOURCE_TYPE_ES, JsonUtils.getString(nodeCfg, CFG_RESOURCE_ID_KEY), CFG_SQL_ID_KEY, CFG_SQL_SENTENCE_KEY);
		}
	}

}
