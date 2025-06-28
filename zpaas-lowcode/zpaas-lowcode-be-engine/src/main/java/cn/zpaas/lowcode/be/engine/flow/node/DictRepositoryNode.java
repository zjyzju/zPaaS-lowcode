package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         DictRepositoryNode是字典缓存读取能力节点的实现类，主要提供基于字典缓存的使用能力
 */
public class DictRepositoryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(DictRepositoryNode.class);

	private static final String CFG_DICT_RESOURCE_ID_KEY = "dictResourceId"; // 字典资源标识存放的Key

	private static final String CFG_DICT_CODE_SOURCE_KEY = "dictCodeSource"; // 字典编码的来源存放的Key
	private static final String CFG_DICT_CODE_KEY = "dictCodeKey"; // 字典编码的Key值存放的Key
	private static final String CFG_DICT_CODE_ATTR_KEY = "dictCodeAttr"; // 当字典编码是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_DICT_VALUE_SOURCE_KEY = "dictValueSource"; // 字典值的来源存放的Key
	private static final String CFG_DICT_VALUE_KEY_KEY = "dictValueKey"; // 字典值的Key值存放的Key
	private static final String CFG_DICT_VALUE_ATTR_KEY = "dictValueAttr"; // 当字典值是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_PARENT_DICT_CODE_SOURCE_KEY = "parentDictCodeSource"; // 上级字典编码的来源存放的Key
	private static final String CFG_PARENT_DICT_CODE_KEY = "parentDictCodeKey"; // 上级字典编码的Key值存放的Key
	private static final String CFG_PARENT_DICT_CODE_ATTR_KEY = "parentDictCodeAttr"; // 当上级字典编码是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_PARENT_DICT_VALUE_SOURCE_KEY = "parentDictValueSource"; // 上级字典值的来源存放的Key
	private static final String CFG_PARENT_DICT_VALUE_KEY_KEY = "parentDictValueKey"; // 上级字典值的Key值存放的Key
	private static final String CFG_PARENT_DICT_VALUE_ATTR_KEY = "parentDictValueAttr"; // 上级当字典值是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	
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

		String dictResourceId = JsonUtils.getString(nodeCfg, CFG_DICT_RESOURCE_ID_KEY);

		String dictCodeSource = JsonUtils.getString(nodeCfg, CFG_DICT_CODE_SOURCE_KEY);
		String dictCodeKey = JsonUtils.getString(nodeCfg, CFG_DICT_CODE_KEY);
		String dictCodeAttr = JsonUtils.getString(nodeCfg, CFG_DICT_CODE_ATTR_KEY);

		String dictValueSource = JsonUtils.getString(nodeCfg, CFG_DICT_VALUE_SOURCE_KEY);
		String dictValueKey = JsonUtils.getString(nodeCfg, CFG_DICT_VALUE_KEY_KEY);
		String dictValueAttr = JsonUtils.getString(nodeCfg, CFG_DICT_VALUE_ATTR_KEY);

		String parentDictCodeSource = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_CODE_SOURCE_KEY);
		String parentDictCodeKey = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_CODE_KEY);
		String parentDictCodeAttr = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_CODE_ATTR_KEY);

		String parentDictValueSource = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_VALUE_SOURCE_KEY);
		String parentDictValueKey = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_VALUE_KEY_KEY);
		String parentDictValueAttr = JsonUtils.getString(nodeCfg, CFG_PARENT_DICT_VALUE_ATTR_KEY);

		// 配置参数合法性校验
		if (StringUtils.isBlank(dictResourceId)) {
			logger.error("T[{}] dictResourceId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dictResourceId can't be null. ");
		}
		if (StringUtils.isBlank(dictCodeSource) || StringUtils.isBlank(dictCodeKey)) {
			logger.error("T[{}] dictCodeSource and dictCodeKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dictCodeSource and dictCodeKey can't be null. ");
		}

		if (!StringUtils.isBlank(parentDictCodeSource) && !StringUtils.isBlank(parentDictCodeKey) 
				&& (StringUtils.isBlank(parentDictValueSource) || StringUtils.isBlank(parentDictValueKey))) {
			logger.error("T[{}] when both parentDictCodeSource and parentDictCodeKey is not null, parentDictValueSource and parentDictValueKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when both parentDictCodeSource and parentDictCodeKey is not null, parentDictValueSource and parentDictValueKey can't be null. ");
		}
		
		// 获取值
		dictCodeAttr = this.dynamicAttrProcess(dictCodeAttr, businessFlowNode, context);
		String dictCode = (String) getContextObject(dictCodeSource, dictCodeKey, false, dictCodeAttr, context, businessFlowNode);

		Object result = null;

		if (StringUtils.isBlank(dictCode)) {
			logger.error("T[{}] dictCode is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dictCode is null");
		}

		
		if (!StringUtils.isBlank(dictValueSource) && !StringUtils.isBlank(dictValueKey)) {//取某个字典值的信息
			dictValueAttr = this.dynamicAttrProcess(dictValueAttr, businessFlowNode, context);
			String dictValue = (String) getContextObject(dictValueSource, dictValueKey, false, dictValueAttr, context, businessFlowNode);
			result = DictCacheAbility.getInstance().getDictValue(businessFlowNode.getSystemId(), dictResourceId, dictCode, dictValue);
		}else if(!StringUtils.isBlank(parentDictCodeSource) && !StringUtils.isBlank(parentDictCodeKey)) {//取子字典值列表
			parentDictCodeAttr = this.dynamicAttrProcess(parentDictCodeAttr, businessFlowNode, context);
			String parentDictCode = (String) getContextObject(parentDictCodeSource, parentDictCodeKey, false, parentDictCodeAttr, context, businessFlowNode);
			parentDictValueAttr = this.dynamicAttrProcess(parentDictValueAttr, businessFlowNode, context);
			String parentDictValue = (String) getContextObject(parentDictValueSource, parentDictValueKey, false, parentDictValueAttr, context, businessFlowNode);
			result = DictCacheAbility.getInstance().getSubDictValues(businessFlowNode.getSystemId(), dictResourceId, dictCode, parentDictCode, parentDictValue);
		}else {
			result = DictCacheAbility.getInstance().getDictValues(businessFlowNode.getSystemId(), dictResourceId, dictCode);
		}
		

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
