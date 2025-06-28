package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.FileUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * LocalFileNode是本地文件操作的实现类，主要用于实现本地文件的处理能力
 */
public class LocalFileNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(LocalFileNode.class);
	
	private static final String CFG_FILE_TYPE_KEY = "fileType"; //文件类型存放的Key
	private static final String CFG_FILE_OPERATION_KEY = "fileOperation"; //执行操作存放的Key

	private static final String CFG_LOCAL_FILE_SOURCE_KEY = "localFileSource"; //本地文件的来源存放的Key
	private static final String CFG_LOCAL_FILE_KEY_KEY = "localFileKey"; //本地文件Key存放的Key
	private static final String CFG_LOCAL_FILE_ATTR_KEY = "localFileAttr"; //当本地文件是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_DEST_FILE_SOURCE_KEY = "destFileSource"; //目标文件的来源存放的Key
	private static final String CFG_DEST_FILE_KEY_KEY = "destFileKey"; //目标文件Key存放的Key
	private static final String CFG_DEST_FILE_ATTR_KEY = "destFileAttr"; //目标地文件是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String FILE_TYPE_D = "D"; //目录
	private static final String FILE_TYPE_F = "F"; //文件
	
	private static final String FILE_OPERATION_C = "C"; //（创建）
	private static final String FILE_OPERATION_D = "D"; //（删除）
	private static final String FILE_OPERATION_R = "R"; //（重命名）
	private static final String FILE_OPERATION_M = "M"; //（移动（只对文件有效））
	private static final String FILE_OPERATION_P = "P"; //（复制（只对文件有效））
	private static final String FILE_OPERATION_W = "W"; //（写入（只对文件有效））
	private static final String FILE_OPERATION_F = "F"; //（判断是否是文件）
	private static final String FILE_OPERATION_E = "E"; //（判断是否存在）
	private static final String FILE_OPERATION_L = "L"; //（查看文件列表（只对目录有效））
	private static final String FILE_OPERATION_G = "G"; //（获取文件）

	private static final String LOCAL_FILE_SOURCE_U = "U"; //（上传的文件）
	
	
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

		String fileType = JsonUtils.getString(nodeCfg, CFG_FILE_TYPE_KEY);
		String fileOperation = JsonUtils.getString(nodeCfg, CFG_FILE_OPERATION_KEY);

		String localFileSource = JsonUtils.getString(nodeCfg, CFG_LOCAL_FILE_SOURCE_KEY);
		String localFileKey = JsonUtils.getString(nodeCfg, CFG_LOCAL_FILE_KEY_KEY);
		String localFileAttr = JsonUtils.getString(nodeCfg, CFG_LOCAL_FILE_ATTR_KEY);
		if (!StringUtils.isBlank(localFileAttr)) {
			localFileAttr = this.dynamicAttrProcess(localFileAttr, businessFlowNode, context);
		}

		String destFileSource = JsonUtils.getString(nodeCfg, CFG_DEST_FILE_SOURCE_KEY);
		String destFileKey = JsonUtils.getString(nodeCfg, CFG_DEST_FILE_KEY_KEY);
		String destFileAttr = JsonUtils.getString(nodeCfg, CFG_DEST_FILE_ATTR_KEY);
		if (!StringUtils.isBlank(localFileAttr)) {
			destFileAttr = this.dynamicAttrProcess(destFileAttr, businessFlowNode, context);
		}

		//配置参数合法性校验， 文件类型与执行操作不能为空
		if(StringUtils.isBlank(fileType) || StringUtils.isBlank(fileOperation)) {
			logger.error("T[{}] fileType and fileOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "fileType and fileOperation can't be null. ");
		}

		//配置参数合法性校验，本地文件来源与本地文件Key不能为空
		if(StringUtils.isBlank(localFileSource) || StringUtils.isBlank(localFileKey)) {
			logger.error("T[{}] txtFileSource and localFileSource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "localFileSource and localFileKey can't be null. ");
		}

		//配置参数合法性校验，当执行操作为R、M、P、W时，目标文件来源与目标文件Key不能为空
		if(FILE_OPERATION_R.equals(fileOperation) || FILE_OPERATION_M.equals(fileOperation) || FILE_OPERATION_P.equals(fileOperation) || FILE_OPERATION_W.equals(fileOperation)) {
			if(StringUtils.isBlank(destFileSource) || StringUtils.isBlank(destFileKey)) {
				logger.error("T[{}] when fileOperation is R、M、P、W, destFileSource and destFileKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "when fileOperation is R、M、P、W, destFileSource and destFileKey can't be null. ");
			}
		}
		if(!FILE_TYPE_D.equals(fileType) && !FILE_TYPE_F.equals(fileType)) {
			logger.error("T[{}] invalid fileType. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid fileType. ");
		}
		
		//获取本地文件
		Object localFile = null;
		if(LOCAL_FILE_SOURCE_U.equals(localFileSource)) {
			List<MultipartFile> files = context.getMultipartFileMap().get(localFileKey);
			if(CollectionUtils.isEmpty(files)) {
				logger.error("T[{}] upload file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload file not found. ");
			}
			localFile = files.get(0);
		}else  {
			localFile = getContextObject(localFileSource, localFileKey, false, localFileAttr, context, businessFlowNode);
			if(localFile != null && localFile instanceof String) {
				localFile = LocalFileAbility.getInstance().getFilePath((String)localFile);
				if(localFile == null) {
					logger.error("T[{}] invalid local file. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid local file ");
				}
			}
			
			if(localFile == null || (localFile instanceof String && StringUtils.isBlank((String)localFile))) {
				logger.error("T[{}] local file can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "local file can't be null. ");
			}
		}
		
		Object result = null;
		if(FILE_OPERATION_C.equals(fileOperation)) {//创建
			if(FILE_TYPE_F.equals(fileType)) {//文件
				if(localFile instanceof File) {
					result = FileUtils.createFile((File)localFile);
				}else {
					result = FileUtils.createFile((String)localFile);
				}
			}else {//目录
				if(localFile instanceof File) {
					result = FileUtils.mkdir((File)localFile);
				}else {
					result = FileUtils.mkdir((String)localFile);
				}
			}
		}else if(FILE_OPERATION_D.equals(fileOperation)) {//删除
			if(FILE_TYPE_F.equals(fileType)) {//文件
				if(localFile instanceof File) {
					result = FileUtils.deleteFile((File)localFile);
				}else {
					result = FileUtils.deleteFile((String)localFile);
				}
			}else {//目录
				if(localFile instanceof File) {
					result = FileUtils.rmdir((File)localFile);
				}else {
					result = FileUtils.rmdir((String)localFile);
				}
			}
		}else if(FILE_OPERATION_R.equals(fileOperation)) {//重命名
			String destFileName = (String)getContextObject(destFileSource, destFileKey, false, destFileAttr, context, businessFlowNode);
			if(localFile instanceof File) {
				result = FileUtils.rename((File) localFile, destFileName);
			}else {
				result = FileUtils.rename((String) localFile, destFileName);
			}
		}else if(FILE_OPERATION_M.equals(fileOperation)) {//移动（只对文件有效）
			if(!(localFile instanceof String)) {
				logger.error("T[{}] when fileOperation is rename, localFile must be String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "when fileOperation is rename, localFile must be String.");
			}
			String localFilePath = (String) localFile;
			String destFile = (String)getContextObject(destFileSource, destFileKey, false, destFileAttr, context, businessFlowNode);
			if(!StringUtils.isBlank(destFile)) {
				destFile = LocalFileAbility.getInstance().getFilePath(destFile);
				if(StringUtils.isBlank(destFile)) {
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dest file.");
				}
			}
			result = FileUtils.moveFile(localFilePath, destFile);
		}else if(FILE_OPERATION_P.equals(fileOperation)) {//复制（只对文件有效）
			if(!(localFile instanceof String)) {
				logger.error("T[{}] when fileOperation is rename, localFile must be String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "when fileOperation is rename, localFile must be String.");
			}
			String localFilePath = (String) localFile;
			String destFile = (String)getContextObject(destFileSource, destFileKey, false, destFileAttr, context, businessFlowNode);
			if(!StringUtils.isBlank(destFile)) {
				destFile = LocalFileAbility.getInstance().getFilePath(destFile);
				if(StringUtils.isBlank(destFile)) {
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dest file.");
				}
			}
			result = FileUtils.copyFile(localFilePath, destFile);
		}else if(FILE_OPERATION_W.equals(fileOperation)) {//写入（只对文件有效）
			String destFile = (String)getContextObject(destFileSource, destFileKey, false, destFileAttr, context, businessFlowNode);
			if(!StringUtils.isBlank(destFile)) {
				destFile = LocalFileAbility.getInstance().getFilePath(destFile);
				if(StringUtils.isBlank(destFile)) {
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dest file.");
				}
			}
			InputStream is = null;
			try {
				if(localFile instanceof File) {
					is = new FileInputStream(((File)localFile));
				}else if(localFile instanceof MultipartFile) {
					is = ((MultipartFile) localFile).getInputStream();
				}else {
					is = new FileInputStream((String) localFile);
				}
				result = FileUtils.writeFile(is, destFile);
			} catch (Exception e) {
				logger.error("T[{}] write file failed!", businessFlowNode.getTenantId(), e);
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "write file failed!",e.getMessage(),e);
			} finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
						
					}
				}
			}
		}else if(FILE_OPERATION_F.equals(fileOperation)) {//判断是否是文件
			if(localFile instanceof File) {
				result = FileUtils.isFile((File) localFile);
			}else {
				result = FileUtils.isFile((String) localFile);
			}
		}else if(FILE_OPERATION_E.equals(fileOperation)) {//判断是否存在
			if(localFile instanceof File) {
				result = FileUtils.exists((File) localFile);
			}else {
				result = FileUtils.exists((String) localFile);
			}
		}else if(FILE_OPERATION_L.equals(fileOperation)) {//查看文件列表（只对目录有效）
			if(localFile instanceof File) {
				result = FileUtils.listFiles((File) localFile);
			}else {
				result = FileUtils.listFiles((String) localFile);
			}
		}else if(FILE_OPERATION_G.equals(fileOperation)) {//获取文件
			if(!(localFile instanceof String)) {
				logger.error("T[{}] when fileOperation is getFile, localFile must be String. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "when fileOperation is getFile, localFile must be String.");
			}
			result = FileUtils.getFile((String) localFile);
		}else {
			logger.error("T[{}] invalid fileOperation. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid fileOperation. ");
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
