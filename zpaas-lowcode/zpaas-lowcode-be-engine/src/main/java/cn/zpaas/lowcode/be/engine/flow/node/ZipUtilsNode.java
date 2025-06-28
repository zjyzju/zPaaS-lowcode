package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.utils.ZipUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * ZipUtilsNode是zip格式压缩/解压缩的实现类，主要用于实现文件的压缩/解压缩能力
 */
public class ZipUtilsNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ZipUtilsNode.class);

	private static final String CFG_SRC_FILES_KEY = "srcFiles"; //源文件列表存放的Key
	private static final String CFG_SRC_FILE_SOURCE_KEY = "srcFileSource"; //源文件的来源存放的Key
	private static final String CFG_SRC_FILE_KEY_KEY = "srcFileKey"; //源文件的Key值存放的Key
	private static final String CFG_SRC_FILE_ATTR_KEY = "srcFileAttr"; //当源文件是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_ZIP_OPERTION_KEY = "zipOperation"; //Zip操作存放的Key
	private static final String CFG_FILE_NAME_KEY = "fileName"; //导出文件名存放的Key

	private static final String ZIP_OPERTION_C = "C"; //Zip操作:压缩
	private static final String ZIP_OPERTION_U = "U"; //Zip操作:解压缩
	private static final String DEFAULT_FILE_NAME = "untitled.zip";
	private static final String DEFAULT_TARGET_DIR = "unzip";
	
	
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
		JsonArray srcFiles = JsonUtils.getJsonArray(nodeCfg, CFG_SRC_FILES_KEY);
		
		String fileName = JsonUtils.getString(nodeCfg, CFG_FILE_NAME_KEY);
		String zipOperation = JsonUtils.getString(nodeCfg, CFG_ZIP_OPERTION_KEY);
	
		
		if(StringUtils.isBlank(zipOperation)) {
			logger.error("T[{}] zipOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "zipOperation can't be null. ");
		}
		if (JsonUtils.isEmpty(srcFiles)) {
			logger.error("T[{}] srcFiles is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "srcFiles is null.");
		}

		//解析处理获取源文件列表
		List<File> srcFileList = new ArrayList<>();
		JsonObject srcFileCfg = null;
		String srcFileSource = null;
		String srcFileKey = null;
		String srcFileAttr = null;
		for(int i=0; i<srcFiles.size(); i++) {
			srcFileCfg = srcFiles.get(i).getAsJsonObject();
			srcFileSource = JsonUtils.getString(srcFileCfg, CFG_SRC_FILE_SOURCE_KEY);
			srcFileKey = JsonUtils.getString(srcFileCfg, CFG_SRC_FILE_KEY_KEY);
			srcFileAttr = JsonUtils.getString(srcFileCfg, CFG_SRC_FILE_ATTR_KEY);
			if (!StringUtils.isBlank(srcFileAttr)) {
				srcFileAttr = this.dynamicAttrProcess(srcFileAttr, businessFlowNode, context);
			}
			//配置参数合法性校验
			if(StringUtils.isBlank(srcFileSource) || StringUtils.isBlank(srcFileKey)) {
				logger.error("T[{}] srcFileSource and srcFileKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFileSource and srcFileKey can't be null. ");
			}
			//获取源文件对象
			Object srcFile = getContextObject(srcFileSource, srcFileKey, false, srcFileAttr, context, businessFlowNode);
			
			if(srcFile == null) {
				logger.info("T[{}] srcFile is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				continue;
			}
			if(!(srcFile instanceof File)) {
				logger.error("T[{}] srcFile is not instanceof File. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFile is not instanceof File.");
			}
			srcFileList.add((File)srcFile);
		}
		
		if(CollectionUtils.isEmpty(srcFileList)) {
			logger.error("T[{}] srcFileList is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFileList is null.");
		}
		
		Object result = null;
		

		if(ZIP_OPERTION_C.equals(zipOperation)) {//压缩操作
			//创建临时文件名
			if(StringUtils.isBlank(fileName)) {
				fileName = DEFAULT_FILE_NAME;
			}
			//获取临时文件
			File zipFile = LocalFileAbility.getInstance().getTempFilePath(fileName).toFile();
			//压缩文件
			ZipUtils.zip(zipFile, srcFileList);
			result = zipFile;
		}else if(ZIP_OPERTION_U.equals(zipOperation)) {//解压缩
			//创建临时解压目录名
			if(StringUtils.isBlank(fileName)) {
				fileName = DEFAULT_TARGET_DIR;
			}
			//获取临时目标目录名
			String targetDir = LocalFileAbility.getInstance().getTempFilePath(fileName).toFile().getAbsolutePath();
			//解压缩
			List<File> unzipFiles = ZipUtils.unzip(srcFileList, targetDir);
			//返回结果为解压后文件的数量
			if(CollectionUtils.isEmpty(unzipFiles)) {
				result = 0;
			}else {
				result = unzipFiles.size();
				int index = 0;
				//由于目前业务流编排无法处理List类型且JsonArray不支持File文件，采用特殊处理的方式，将文件打散后存到过程数据中，key为fileName+index
				for(File file: unzipFiles) {
					setContextObject(OBJECT_INSTANCE_SOURCE_P, fileName+(index++), false, null, file, context, businessFlowNode);
				}
			}
		}else {
			logger.error("T[{}] invalid zipOperation. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid zipOperation.");
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
