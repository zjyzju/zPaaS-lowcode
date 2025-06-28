package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.utils.JsonFileUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * JSONExportNode是JSON文件导出的实现类，主要用于实现JSON文件的导出能力
 */
public class JSONExportNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(JSONExportNode.class);

	private static final String CFG_EXPORT_DATA_SOURCE_KEY = "exportDataSource"; //导出数据的来源存放的Key
	private static final String CFG_EXPORT_DATA_KEY_KEY = "exportDataKey"; //导出数据的Key值存放的Key
	private static final String CFG_EXPORT_DATA_ATTR_KEY = "exportDataAttr"; //当导出数据是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	private static final String CFG_FILE_NAME_KEY = "fileName"; //导出文件名存放的Key
	
	
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
		String exportDataSource = JsonUtils.getString(nodeCfg, CFG_EXPORT_DATA_SOURCE_KEY);
		String exportDataKey = JsonUtils.getString(nodeCfg, CFG_EXPORT_DATA_KEY_KEY);
		String exportDataAttr = JsonUtils.getString(nodeCfg, CFG_EXPORT_DATA_ATTR_KEY);
		if (!StringUtils.isBlank(exportDataAttr)) {
			exportDataAttr = this.dynamicAttrProcess(exportDataAttr, businessFlowNode, context);
		}
		String fileName = JsonUtils.getString(nodeCfg, CFG_FILE_NAME_KEY);
		
		
		//配置参数合法性校验
		if(StringUtils.isBlank(exportDataSource) || StringUtils.isBlank(exportDataKey)) {
			logger.error("T[{}] exportDataSource and exportDataKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "exportDataSource and exportDataKey can't be null. ");
		}
		
		//获取导出数据对象
		Object exportObject = getContextObject(exportDataSource, exportDataKey, false, exportDataAttr, context, businessFlowNode);
		
		if(exportObject == null || !(exportObject instanceof JsonArray)) {
			logger.error("T[{}] exportObject can't be null and must be type of JsonArray. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "exportObject can't be null and must be type of JsonArray!");
		}
		
		JsonArray objectArray = (JsonArray) exportObject;
		
		//导出数据到excel文件
		File result = JsonFileUtils.export(objectArray, fileName);
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
