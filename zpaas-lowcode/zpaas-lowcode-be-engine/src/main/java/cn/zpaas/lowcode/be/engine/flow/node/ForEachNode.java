package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.BusinessFlowProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         ForEachNode是循环节点的实现类，主要实现对列表元素的循环处理功能
 */
public class ForEachNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ForEachNode.class);

	private static final String CFG_ARRAY_OBJECT_SOURCE_KEY = "arrayObjectSource"; // 列表对象的来源存放的Key
	private static final String CFG_ARRAY_OBJECT_KEY_KEY = "arrayObjectKey"; // 列表对象的Key值存放的Key
	private static final String CFG_ARRAY_ATTR_PATH_KEY = "arrayAttrPath"; // 列表对象属性对应的json path存放的Key
	private static final String CFG_ARRAY_ELEMENT_IS_LIST_KEY = "arrayElementIsList"; // 列表的每个元素对象是否是列表存放的Key
	private static final String CFG_ARRAY_ELEMENT_OBJECT_TYPE_KEY = "arrayElementObjectType"; // 列表元素对象的类型存放的Key
	// private static final String CFG_ARRAY_ELEMENT_OBJECT_CLASS_KEY =
	// "arrayElementObjectClass"; //列表数据对象的类存放的Key
	public static final String CFG_SUB_BUSINESS_FLOW_ID_KEY = "subBusinessFlowId"; // 循环分支执行的子业务流标识存放的Key

	public static final String FOR_EACH_ROW_INDEX_KEY = "forEachRowIndex_"; // forEachRowIndex存放的Key
	public static final String FOR_EACH_ELEMENT_KEY = "forEachElement_"; // forEachElement存放的Key
	public static final String FOR_EACH_SUB_BUSINESS_FLOW_KEY = "forEachBusinessFlow"; // forEachElement存放的Key

	public static final String FOR_EACH_ROW_INDEX = "forEachRowIndex"; // forEachRowIndex存放的Key
	public static final String FOR_EACH_ELEMENT = "forEachElement"; // forEachElement存放的Key

	
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
		String arrayObjectSource = JsonUtils.getString(nodeCfg, CFG_ARRAY_OBJECT_SOURCE_KEY);
		String arrayObjectKey = JsonUtils.getString(nodeCfg, CFG_ARRAY_OBJECT_KEY_KEY);
		String arrayAttrPath = JsonUtils.getString(nodeCfg, CFG_ARRAY_ATTR_PATH_KEY);
		boolean arrayElementIsList = JsonUtils.getBoolean(nodeCfg, CFG_ARRAY_ELEMENT_IS_LIST_KEY);
		String arrayElementObjectType = JsonUtils.getString(nodeCfg, CFG_ARRAY_ELEMENT_OBJECT_TYPE_KEY);
		// String arrayElementObjectClass = nodeCfg,
		// CFG_ARRAY_ELEMENT_OBJECT_CLASS_KEY);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);

		if (StringUtils.isBlank(arrayObjectSource) || StringUtils.isBlank(subBusinessFlowId)) {
			logger.error("T[{}] arrayObjectSource and subBusinessFlowId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "arrayObjectSource and subBusinessFlowId can't be null. ");
		}

		// 动态属性的情况
		if (!StringUtils.isBlank(arrayAttrPath)) {
			arrayAttrPath = this.dynamicAttrProcess(arrayAttrPath, businessFlowNode, context);
		}

		// 获取源对象实例
		Object object = getContextObject(arrayObjectSource, arrayObjectKey, true, arrayAttrPath, context,
				businessFlowNode);
		// 循环的目标列表为null时，直接退出循环
		if (object == null) {
			logger.info("T[{}] arrayObject is null, exit forEach node.", businessFlowNode.getTenantId());
			return;
		}
		// 目标对象不是一个列表，报错
		if (!(object instanceof JsonArray)) {
			logger.error("T[{}] arrayObject is not a array{}. businessflowNodeId: {}", object.getClass().getName(), businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "arrayObject is not a array.");
		}
		JsonArray arrayObject = (JsonArray) object;
		// 循环的目标列表为空时，直接退出循环
		if (arrayObject.size() == 0) {
			logger.info("T[{}] arrayObject is empty, exit forEach node.", businessFlowNode.getTenantId());
			return;
		}

		JsonArray resultArray = new JsonArray();
		Object element = null;
		// 循环每一个元素
		for (int i = 0; i < arrayObject.size(); i++) {
			// 获取循环的元素
			if (arrayElementIsList) {// 列表类型元素
				element = arrayObject.get(i).getAsJsonArray();
			} else {// 非列表类型元素
				if (ManagedObjectType.JAVA_OBJECT.equals(arrayElementObjectType)) {// 原生Java类型
					element = JsonUtils.toObject(arrayObject.get(i));
				} else if (ManagedObjectType.DOMAIN_OBJECT.equals(arrayElementObjectType) ||
						ManagedObjectType.VALUE_OBJECT.equals(arrayElementObjectType)) {// 管理对象类型
					element = arrayObject.get(i).getAsJsonObject();
				} else {// 非法的配置
					logger.error("T[{}] invalid arrayElementObjectType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid arrayElementObjectType.");
				}
			}
			// 将循环的元素设置到过程数据中
			context.setAttribute(FOR_EACH_ROW_INDEX_KEY + subBusinessFlowId, i);
			context.setAttribute(FOR_EACH_ELEMENT_KEY + subBusinessFlowId, element);
			if (logger.isTraceEnabled()) {
				logger.trace("T[{}] FOR_EACH_ROW_INDEX: {} and FOR_EACH_ELEMENT: {} is set.", businessFlowNode.getTenantId(), i, element);
			}

			// 设置循环子业务流标志，因为存在嵌套，需要进行层次处理
			if (context.getAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {
				Integer forEachBusinessFlowCount = (Integer) context.getAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY);
				forEachBusinessFlowCount++;
				context.setAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY, forEachBusinessFlowCount);
			} else {
				context.setAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY, Integer.valueOf(1));
			}

			// 初始化业务流命令对象
			BusinessFlowCommand businessFlowCommand = new BusinessFlowCommand();
			businessFlowCommand.setBusinessFlowId(subBusinessFlowId);
			businessFlowCommand.setSystemId(context.getSystemId());
			businessFlowCommand.setReqData(context.getReqData());
			// 执行子业务流
			BusinessFlowProxy.getInstance().execute(businessFlowCommand, context);

			// 由于是子业务流，已经将目标对象保存到节点处理结果中
			if (context.getAttribute(NODE_RESULT_KEY) != null) {
				JsonElement nodeResuElement = JsonUtils.toJsonElement(context.getAttribute(NODE_RESULT_KEY));
				if (resultArray.contains(nodeResuElement)) {
					resultArray.remove(nodeResuElement);
				}
				if (context.getAttribute(NODE_RESULT_KEY) instanceof JsonArray) {
					if (((JsonArray) context.getAttribute(NODE_RESULT_KEY)).size() > 0) {
						resultArray.add(nodeResuElement);
					}
				} else {
					resultArray.add(nodeResuElement);
				}
			}

			// 清理过程数据
			context.removeAttribute(FOR_EACH_ELEMENT_KEY + subBusinessFlowId);
			context.removeAttribute(FOR_EACH_ROW_INDEX_KEY + subBusinessFlowId);
			context.removeAttribute(NODE_RESULT_KEY);
			if (context.getAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {
				Integer forEachBusinessFlowCount = (Integer) context.getAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY);
				forEachBusinessFlowCount--;
				if (forEachBusinessFlowCount <= 0) {
					context.removeAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY);
				} else {
					context.setAttribute(FOR_EACH_SUB_BUSINESS_FLOW_KEY, forEachBusinessFlowCount);
				}
			}
			// 判断中断标志并中断循环
			if (context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY) != null) {
				Integer breakTimes = (Integer) context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY);
				breakTimes--;
				if (breakTimes <= 0) {
					context.removeAttribute(BreakNode.CFG_BREAK_TIMES_KEY);
				} else {
					context.setAttribute(BreakNode.CFG_BREAK_TIMES_KEY, breakTimes);
				}
				break;
			}
		}

		// 将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, resultArray);
	}

	@Override
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {
		super.nodeCfgLoad(nodeCfg, node, info, businessFlowService);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);
		BusinessFlowInfo subBusinessFlowInfo = businessFlowService.loadBusinessFlowInfo(subBusinessFlowId);
		if(subBusinessFlowInfo != null) {
			info.getSubBusinessFlowMap().put(node.getBpmnNodeId(), subBusinessFlowInfo);
		}
	}

	@Override
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) throws EngineException {
		super.beforeSave(node, info, businessFlowService);
		businessFlowService.saveSubBusinessFlow(node, info, businessFlowService, CFG_SUB_BUSINESS_FLOW_ID_KEY);
	}
}
