package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.utils.ExcelUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * ExcelExportNode是Excel文件导出的实现类，主要用于实现Excel文件的导出能力
 */
public class ExcelExportNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ExcelExportNode.class);

	private static final String CFG_EXPORT_DATA_SOURCE_KEY = "exportDataSource"; //导出数据的来源存放的Key
	private static final String CFG_EXPORT_DATA_KEY_KEY = "exportDataKey"; //导出数据的Key值存放的Key
	private static final String CFG_EXPORT_DATA_ATTR_KEY = "exportDataAttr"; //当导出数据是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	private static final String CFG_EXPORT_HEADER_KEY = "exportHeader"; //是否导出表头存放的Key
	private static final String CFG_IS_REORDER_KEY = "isReOrder"; //是否重排序存放的Key
	private static final String CFG_EXCEL_TEMPLATE_SOURCE_KEY = "excelTemplateSource"; //excel文件模板来源存放的Key
	private static final String CFG_EXCEL_TEMPLATE_KEY_KEY = "excelTemplateKey"; //excel文件模板KEY存放的Key
	private static final String CFG_SHEET_INDEX_KEY = "sheetIndex"; //待导出的sheet页号存放的Key
	private static final String CFG_START_ROW_INDEX_KEY = "startRowIndex"; //开始插入的行号存放的Key
	private static final String CFG_FILE_NAME_KEY = "fileName"; //导出文件名存放的Key
	private static final String CFG_COLUMN_MAPPINGS_KEY = "columnMappings"; //列映射存放的Key
	private static final String CFG_COLUMN_INDEX_KEY = "columnIndex"; //excel列序号存放的Key
	private static final String CFG_MAPPING_NAME_KEY = "mappingName"; //映射属性名存放的Key
	private static final String CFG_HEADER_NAME_KEY = "headerName"; //表头名字存放的Key
	private static final String CFG_NEED_MERGE_KEY = "needMerge"; //是否需要合并存放的Key
	private static final String CFG_IS_SUM_KEY = "isSum"; //是否累加存放的Key
	
	
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
		boolean exportHeader = JsonUtils.getBoolean(nodeCfg, CFG_EXPORT_HEADER_KEY);
		String fileName = JsonUtils.getString(nodeCfg, CFG_FILE_NAME_KEY);
		JsonArray columnMappings = JsonUtils.getJsonArray(nodeCfg, CFG_COLUMN_MAPPINGS_KEY);
		boolean isReOrder = JsonUtils.getBoolean(nodeCfg, CFG_IS_REORDER_KEY);
		String excelTemplateSource = JsonUtils.getString(nodeCfg, CFG_EXCEL_TEMPLATE_SOURCE_KEY);
		String excelTemplateKey = JsonUtils.getString(nodeCfg, CFG_EXCEL_TEMPLATE_KEY_KEY);
		Integer sheetIndex = JsonUtils.getInteger(nodeCfg, CFG_SHEET_INDEX_KEY);
		Integer startRowIndex = JsonUtils.getInteger(nodeCfg, CFG_START_ROW_INDEX_KEY);
		
		
		//配置参数合法性校验
		if(StringUtils.isBlank(exportDataSource) || StringUtils.isBlank(exportDataKey)) {
			logger.error("T[{}] exportDataSource and exportDataKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "exportDataSource and exportDataKey can't be null. ");
		}
		if(JsonUtils.isEmpty(columnMappings)) {
			logger.error("T[{}] columnMappings can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "columnMappings can't be null. ");
		}
		// 动态属性的情况
		if (!StringUtils.isBlank(exportDataAttr)) {
			exportDataAttr = this.dynamicAttrProcess(exportDataAttr, businessFlowNode, context);
		}
		
		//获取导出数据对象
		Object exportObject = getContextObject(exportDataSource, exportDataKey, false, exportDataAttr, context, businessFlowNode);
		
		if(exportObject == null || !(exportObject instanceof JsonArray)) {
			logger.error("T[{}] exportObject can't be null and must be type of JsonArray. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "exportObject can't be null and must be type of JsonArray!");
		}
		
		JsonArray objectArray = (JsonArray) exportObject;
		
		//转换列映射
		Map<Integer, JsonObject> columnMap = new HashMap<>();
		Map<Integer, String> headerMap = new HashMap<>();
		if(columnMappings != null && !columnMappings.isEmpty()) {
			JsonObject row = null;
			for(int i = 0; i<columnMappings.size(); i++) {
				row = columnMappings.get(i).getAsJsonObject();
				columnMap.put(JsonUtils.getInteger(row, CFG_COLUMN_INDEX_KEY), row);
				headerMap.put(JsonUtils.getInteger(row, CFG_COLUMN_INDEX_KEY), JsonUtils.getString(row, CFG_HEADER_NAME_KEY));
			}
		}
		//需要重新排序
		if(isReOrder) {
			//获取排序的列名，按列序号的顺序保存
			List<String> orderByList = new ArrayList<>();
			columnMap.forEach((index,columnCfg)-> {
				//需要合并且非累加的列
				if(JsonUtils.getBoolean(columnCfg, CFG_NEED_MERGE_KEY) && !JsonUtils.getBoolean(columnCfg, CFG_IS_SUM_KEY)) {
					orderByList.add(JsonUtils.getString(columnCfg, CFG_MAPPING_NAME_KEY));
				}
			});
			//进行重排序
			List<JsonElement> objecElements = objectArray.asList();
			objecElements.sort(new Comparator<JsonElement>() {
				@Override
				public int compare(JsonElement o1, JsonElement o2) {
					JsonObject object1 = (JsonObject) o1;
					JsonObject object2 = (JsonObject) o2;
					String value1 = null;
					String value2 = null;
					for(String orderBy : orderByList) {
						value1 = JsonUtils.getString(object1, orderBy);
						value2 = JsonUtils.getString(object2, orderBy);
						if(value1 == null && value2 == null) {
							continue;
						}else if(value1 == null) {
							return -1;
						}else if(value2 == null) {
							return 1;
						}else {
							if(value1.equals(value2)) {
								continue;
							}else {
								return value1.compareTo(value2);
							}
						}
					}
					return 0;
				}
			});
			objectArray = JsonUtils.toJsonArray(objecElements);
		}
		//取模板文件
		File templateFile = null;
		if(!StringUtils.isBlank(excelTemplateSource) && !StringUtils.isBlank(excelTemplateKey)) {
			templateFile = (File)getContextObject(excelTemplateSource, excelTemplateKey, false, null, context, businessFlowNode);
		}
		
		//导出数据到excel文件
		File result = ExcelUtils.export(objectArray, columnMap, exportHeader, headerMap, templateFile, sheetIndex, startRowIndex, fileName);
		
		//清理模板文件
		if(templateFile != null) {
			templateFile.delete();
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
