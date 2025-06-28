package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowCommand;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.domain.service.BusinessFlowService;
import cn.zpaas.lowcode.be.engine.proxy.BusinessFlowProxy;
import cn.zpaas.lowcode.be.engine.utils.TxtUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * TextImportNode是Text文件解析导入的实现类，主要用于实现Text文件的解析和处理能力
 */
public class TextImportNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(TextImportNode.class);
	
	private static final String CFG_TXT_FILE_SOURCE_KEY = "txtFileSource"; //txt文件的来源存放的Key
	private static final String CFG_TXT_FILE_NAME_SOURCE_KEY = "txtFileNameSource"; //txt文件名的来源存放的Key
	private static final String CFG_TXT_FILE_NAME_KEY_KEY = "txtFileNameKey"; //txt文件名的Key值存放的Key
	private static final String CFG_TXT_FILE_NAME_ATTR_KEY = "txtFileNameAttr"; //当txt文件名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	private static final String CFG_SPLIT_SYMBOL_KEY = "splitSymbol"; //splitSymbol存放的Key
	private static final String CFG_PROCESS_MODE_KEY = "processMode"; //处理模式存放的Key
	private static final String CFG_START_ROW_INDEX_KEY = "startRowIndex"; //开始处理的行数存放的Key
	private static final String CFG_COLUMN_MAPPINGS_KEY = "columnMappings"; //列映射存放的Key
	public static final String CFG_SUB_BUSINESS_FLOW_ID_KEY = "subBusinessFlowId"; //流式处理子业务流标识存放的Key
	private static final String CFG_COLUMN_INDEX_KEY = "columnIndex"; //txt列序号存放的Key
	private static final String CFG_MAPPING_NAME_KEY = "mappingName"; //映射属性名存放的Key
	
	private static final String PROCESS_MODE_B = "B"; //批量处理
	private static final String PROCESS_MODE_F = "F"; //流式处理
	
	private static final String TXT_FILE_SOURCE_U = "U"; //（过程数据：过程数据中的文件对象）
	private static final String TXT_FILE_SOURCE_P = "P"; //（上传文件：上传的Multipart文件对象）
	
	
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
		String txtFileSource = JsonUtils.getString(nodeCfg, CFG_TXT_FILE_SOURCE_KEY);
		String txtFileNameSource = JsonUtils.getString(nodeCfg, CFG_TXT_FILE_NAME_SOURCE_KEY);
		String txtFileNameKey = JsonUtils.getString(nodeCfg, CFG_TXT_FILE_NAME_KEY_KEY);
		String txtFileNameAttr = JsonUtils.getString(nodeCfg, CFG_TXT_FILE_NAME_ATTR_KEY);
		if (!StringUtils.isBlank(txtFileNameAttr)) {
			txtFileNameAttr = this.dynamicAttrProcess(txtFileNameAttr, businessFlowNode, context);
		}
		String splitSymbol = JsonUtils.getString(nodeCfg, CFG_SPLIT_SYMBOL_KEY);
		String processMode = JsonUtils.getString(nodeCfg, CFG_PROCESS_MODE_KEY);
		Integer startRowIndex = JsonUtils.getInteger(nodeCfg, CFG_START_ROW_INDEX_KEY);
		JsonArray columnMappings = JsonUtils.getJsonArray(nodeCfg, CFG_COLUMN_MAPPINGS_KEY);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);
		
		//配置参数合法性校验
		if(StringUtils.isBlank(txtFileSource) || StringUtils.isBlank(txtFileNameSource) || StringUtils.isBlank(txtFileNameKey)) {
			logger.error("T[{}] txtFileSource txtFileNameSource and txtFileNameKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "txtFileSource txtFileNameSource and txtFileNameKey can't be null. ");
		}
		if(StringUtils.isBlank(processMode)) {
			logger.error("T[{}] processMode can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "processMode can't be null. ");
		}
		if(!PROCESS_MODE_B.equals(processMode) && !PROCESS_MODE_F.equals(processMode)) {
			logger.error("T[{}] invalid process mode. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid process mode. ");
		}
		if(PROCESS_MODE_B.equals(processMode)) {//批量处理
			if(subBusinessFlowId!= null && subBusinessFlowId.trim().length() > 0) {
				logger.error("T[{}] subBusinessFlowId can't be set when process mode is batch. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "subBusinessFlowId can't be set when process mode is batch. ");
			}
		}else {//流式处理
			if(subBusinessFlowId == null || subBusinessFlowId.trim().length() == 0) {
				logger.error("T[{}] subBusinessFlowId must be set when process mode is flow. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "subBusinessFlowId must be set when process mode is flow. ");
			}
		}
		
		//获取txt文件名
		String txtFileName = (String)getContextObject(txtFileNameSource, txtFileNameKey, false, txtFileNameAttr, context, businessFlowNode);
		
		// 转换列映射
		Map<Integer, String> columnMap = null;
		if (!JsonUtils.isEmpty(columnMappings)) {
			columnMap = new HashMap<>();
			JsonObject row = null;
			for (int i = 0; i < columnMappings.size(); i++) {
				row = columnMappings.get(i).getAsJsonObject();
				columnMap.put(JsonUtils.getInteger(row, CFG_COLUMN_INDEX_KEY), JsonUtils.getString(row, CFG_MAPPING_NAME_KEY));
			}
		}
		Object txtFile = null;
		if(TXT_FILE_SOURCE_U.equals(txtFileSource)) {
			List<MultipartFile> files = context.getMultipartFileMap().get(txtFileName);
			if(CollectionUtils.isEmpty(files)) {
				logger.error("T[{}] Text file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "Text file not found. ");
			}
			
			txtFile = files.get(0);
		}else if(TXT_FILE_SOURCE_P.equals(txtFileSource)) {
			txtFile = (File)getContextObject(txtFileSource, txtFileName, false, null, context, businessFlowNode);
			if(txtFile == null) {
				logger.error("T[{}] Text file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "Text file not found. ");
			}
		}else {
			logger.error("T[{}] invalid TextFileSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid TextFileSource");
		}
		
		Object result = null;
		
		if(PROCESS_MODE_B.equals(processMode)) {//批量处理
			result = TxtUtils.processObjectType(txtFile, splitSymbol, columnMap, startRowIndex);
		}else {//流式处理
			TxtUtils.processObjectType(txtFile, splitSymbol, columnMap, startRowIndex, (index, rowObject)-> {
				//将循环的元素设置到过程数据中
				context.setAttribute(ForEachNode.FOR_EACH_ROW_INDEX_KEY+subBusinessFlowId, index);
				context.setAttribute(ForEachNode.FOR_EACH_ELEMENT_KEY+subBusinessFlowId, rowObject);
				if(logger.isDebugEnabled()) {
					logger.debug("T[{}] FOR_EACH_ROW_INDEX: {} and FOR_EACH_ELEMENT: {} is set.", businessFlowNode.getTenantId(), index, rowObject);
				}
				
				//设置循环子业务流标志，因为存在嵌套，需要进行层次处理
				if(context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {
					Integer forEachBusinessFlowCount = (Integer)context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY);
					forEachBusinessFlowCount++;
					context.setAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY, forEachBusinessFlowCount);
				}else {
					context.setAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY, Integer.valueOf(1));
				}
				
				//初始化业务流命令对象
				BusinessFlowCommand businessFlowCommand = new BusinessFlowCommand();
				businessFlowCommand.setBusinessFlowId(subBusinessFlowId);
				businessFlowCommand.setSystemId(context.getSystemId());
				businessFlowCommand.setReqData(context.getReqData());
				//执行子业务流
				BusinessFlowProxy.getInstance().execute(businessFlowCommand, context);
				
				//该子业务流不处理返回结果
				
				//清理过程数据
				context.removeAttribute(ForEachNode.FOR_EACH_ELEMENT_KEY+subBusinessFlowId);
				context.removeAttribute(ForEachNode.FOR_EACH_ROW_INDEX_KEY+subBusinessFlowId);
				context.removeAttribute(NODE_RESULT_KEY);
				if(context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null) {
					Integer forEachBusinessFlowCount = (Integer)context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY);
					forEachBusinessFlowCount--;
					if(forEachBusinessFlowCount <= 0) {
						context.removeAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY);
					}else {
						context.setAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY, forEachBusinessFlowCount);
					}
				}
				//判断中断标志并中断循环
				if(context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY) != null) {
					Integer breakTimes = (Integer)context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY);
					breakTimes--;
					if(breakTimes <=0) {
						context.removeAttribute(BreakNode.CFG_BREAK_TIMES_KEY);
					}else {
						context.setAttribute(BreakNode.CFG_BREAK_TIMES_KEY, breakTimes);
					}
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "interrupt process.");
				}
			});
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

	@Override
	public void nodeCfgLoad(JsonObject nodeCfg, BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) {
		super.nodeCfgLoad(nodeCfg, node, info, businessFlowService);
		String subBusinessFlowId = JsonUtils.getString(nodeCfg, CFG_SUB_BUSINESS_FLOW_ID_KEY);
		if(subBusinessFlowId != null && subBusinessFlowId.trim().length() > 0) {
			BusinessFlowInfo subBusinessFlowInfo = businessFlowService.loadBusinessFlowInfo(subBusinessFlowId);
			if(subBusinessFlowInfo != null) {
				info.getSubBusinessFlowMap().put(node.getBpmnNodeId(), subBusinessFlowInfo);
			}
		}
	}

	@Override
	public void beforeSave(BusinessFlowNode node, BusinessFlowInfo info, BusinessFlowService businessFlowService) throws EngineException {
		super.beforeSave(node, info, businessFlowService);
		businessFlowService.saveSubBusinessFlow(node, info, businessFlowService, CFG_SUB_BUSINESS_FLOW_ID_KEY);
	}
}
