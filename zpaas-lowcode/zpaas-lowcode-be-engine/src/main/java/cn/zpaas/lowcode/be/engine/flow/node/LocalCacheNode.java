package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.LocalCacheAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         LocalCacheNode是本地缓存使用能力节点的实现类，主要提供基于本地缓存的使用能力
 */
public class LocalCacheNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(LocalCacheNode.class);

	private static final String CFG_CACHE_OPERATION_KEY = "cacheOperation"; // 缓存操作类型存放的Key

	private static final String CFG_CACHE_KEY_SOURCE_KEY = "cacheKeySource"; // 缓存Key的来源存放的Key
	private static final String CFG_CACHE_KEY_KEY = "cacheKeyKey"; // 缓存Key的Key值存放的Key
	private static final String CFG_CACHE_KEY_ATTR_KEY = "cacheKeyAttr"; // 当缓存Key是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_CACHE_VALUE_SOURCE_KEY = "cacheValueSource"; // 缓存值的来源存放的Key
	private static final String CFG_CACHE_VALUE_KEY_KEY = "cacheValueKey"; // 缓存值的Key值存放的Key
	private static final String CFG_CACHE_VALUE_ATTR_KEY = "cacheValueAttr"; // 当缓存值是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CACHE_OPERATION_GET = "G"; // get
	private static final String CACHE_OPERATION_PUT = "P"; // put

	
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

		String cacheOperation = JsonUtils.getString(nodeCfg, CFG_CACHE_OPERATION_KEY);

		String cacheKeySource = JsonUtils.getString(nodeCfg, CFG_CACHE_KEY_SOURCE_KEY);
		String cacheKeyKey = JsonUtils.getString(nodeCfg, CFG_CACHE_KEY_KEY);
		String cacheKeyAttr = JsonUtils.getString(nodeCfg, CFG_CACHE_KEY_ATTR_KEY);

		String cacheValueSource = JsonUtils.getString(nodeCfg, CFG_CACHE_VALUE_SOURCE_KEY);
		String cacheValueKey = JsonUtils.getString(nodeCfg, CFG_CACHE_VALUE_KEY_KEY);
		String cacheValueAttr = JsonUtils.getString(nodeCfg, CFG_CACHE_VALUE_ATTR_KEY);

		// 配置参数合法性校验
		if (StringUtils.isBlank(cacheOperation)) {
			logger.error("T[{}] cacheOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "cacheOperation can't be null. ");
		}
		if (StringUtils.isBlank(cacheKeySource) || StringUtils.isBlank(cacheKeyKey)) {
			logger.error("T[{}] cacheKeySource and cacheKeyKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "cacheKeySource and cacheKeyKey can't be null. ");
		}
		if (CACHE_OPERATION_PUT.equals(cacheOperation) && (StringUtils.isBlank(cacheValueSource) || StringUtils.isBlank(cacheValueKey))) {
			logger.error("T[{}] when put cache, cacheValueSource and cacheValueKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when put cache, cacheValueSource and cacheValueKey can't be null. ");
		}
		if (!StringUtils.isBlank(cacheKeyAttr)) {
			cacheKeyAttr = this.dynamicAttrProcess(cacheKeyAttr, businessFlowNode, context);
		}
		// 获取明文
		String cacheKey = (String) getContextObject(cacheKeySource, cacheKeyKey, false, cacheKeyAttr, context,
				businessFlowNode);

		Object result = null;

		if (StringUtils.isBlank(cacheKey)) {
			logger.error("T[{}] cacheKey is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "cacheKey is null");
		}
		if (CACHE_OPERATION_PUT.equals(cacheOperation)) {// put
			if (!StringUtils.isBlank(cacheValueAttr)) {
				cacheValueAttr = this.dynamicAttrProcess(cacheValueAttr, businessFlowNode, context);
			}
			// 获取缓存值
			Object cacheValue = getContextObject(cacheValueSource, cacheValueKey, false, cacheValueAttr, context,
					businessFlowNode);
			if (cacheValue == null) {
				logger.error("T[{}] cacheValue is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "cacheValue is null");
			}
			LocalCacheAbility.getInstance().put(cacheKey, cacheValue);
		} else if (CACHE_OPERATION_GET.equals(cacheOperation)) {// get
			result = LocalCacheAbility.getInstance().get(cacheKey);
		} else {
			logger.error("T[{}] invalid cacheOperation {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), cacheOperation, businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid cacheOperation");
		}

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
