package cn.zpaas.lowcode.be.engine.flow.node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.FtpRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         FtpRepositoryNode是Ftp存取节点的实现类，主要用于实现Ftp/Sftp的上传下载能力
 */
public class FtpRepositoryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(FtpRepositoryNode.class);

	private static final String CFG_FTP_TYPE_KEY = "ftpType"; // ftp类型存放的Key
	private static final String CFG_FTP_RESOURCE_ID_KEY = "ftpResourceId"; // ftp资源标识存放的Key
	private static final String CFG_FTP_OPERATION_KEY = "ftpOperation"; // ftp操作存放的Key
	private static final String CFG_SRC_FILE_SOURCE_KEY = "srcFileSource"; // 源文件的来源存放的Key
	private static final String CFG_SRC_FILE_NAME_SOURCE_KEY = "srcFileNameSource"; // 源文件名的来源存放的Key
	private static final String CFG_SRC_FILE_NAME_KEY_KEY = "srcFileNameKey"; // 源文件名的Key值存放的Key
	private static final String CFG_SRC_FILE_NAME_ATTR_KEY = "srcFileNameAttr"; // 当源文件名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	private static final String CFG_SRC_FILE_IS_LIST_KEY = "srcFileIsList"; // 源文件是否是列表类型存放的Key
	private static final String CFG_DST_FILE_NAME_SOURCE_KEY = "dstFileNameSource"; // 目标文件名的来源存放的Key
	private static final String CFG_DST_FILE_NAME_KEY_KEY = "dstFileNameKey"; // 目标文件名的Key值存放的Key
	private static final String CFG_DST_FILE_NAME_ATTR_KEY = "dstFileNameAttr"; // 当目标文件名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String FTP_OPERATION_U = "U"; // ftp操作-上传
	private static final String FTP_OPERATION_D = "D"; // ftp操作-下载
	private static final String FTP_OPERATION_R = "R"; // ftp操作-删除

	private static final String SRC_FILE_SOURCE_U = "U"; // 上传文件
	private static final String SRC_FILE_SOURCE_P = "P"; // 过程数据

	
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
		String ftpType = JsonUtils.getString(nodeCfg, CFG_FTP_TYPE_KEY);
		String ftpResourceId = JsonUtils.getString(nodeCfg, CFG_FTP_RESOURCE_ID_KEY);
		String ftpOperation = JsonUtils.getString(nodeCfg, CFG_FTP_OPERATION_KEY);
		String srcFileSource = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_SOURCE_KEY);
		String srcFileNameSource = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_SOURCE_KEY);
		String srcFileNameKey = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_KEY_KEY);
		String srcFileNameAttr = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_ATTR_KEY);
		boolean srcFileIsList = JsonUtils.getBoolean(nodeCfg, CFG_SRC_FILE_IS_LIST_KEY);
		String dstFileNameSource = JsonUtils.getString(nodeCfg, CFG_DST_FILE_NAME_SOURCE_KEY);
		String dstFileNameKey = JsonUtils.getString(nodeCfg, CFG_DST_FILE_NAME_KEY_KEY);
		String dstFileNameAttr = JsonUtils.getString(nodeCfg, CFG_DST_FILE_NAME_ATTR_KEY);

		// 配置参数合法性校验
		if (StringUtils.isBlank(ftpType) || StringUtils.isBlank(ftpResourceId) || StringUtils.isBlank(ftpOperation)) {
			logger.error("T[{}] ftpType ftpResourceId and ftpOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "ftpType ftpResourceId and ftpOperation can't be null. ");
		}
		if (StringUtils.isBlank(srcFileNameSource) || StringUtils.isBlank(srcFileNameKey)) {
			logger.error("T[{}] srcFileNameKey and srcFileNameSource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFileNameKey and srcFileNameSource can't be null. ");
		}
		if (FTP_OPERATION_U.equals(ftpOperation)
				&& (StringUtils.isBlank(dstFileNameSource) || StringUtils.isBlank(dstFileNameKey) || StringUtils.isBlank(srcFileSource))) {
			logger.error("T[{}] when upload to ftp, srcFileSource dstFileNameSource and dstFileNameKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when upload to ftp, srcFileSource dstFileNameSource and dstFileNameKey can't be null. ");
		}

		// 动态属性的情况
		if (!StringUtils.isBlank(srcFileNameAttr)) {
			srcFileNameAttr = this.dynamicAttrProcess(srcFileNameAttr, businessFlowNode, context);
		}

		// 获取源文件名
		Object src = getContextObject(srcFileNameSource, srcFileNameKey, srcFileIsList, srcFileNameAttr, context,
				businessFlowNode);

		Object result = null;
		if (srcFileIsList) {// 多个文件
			JsonArray srcFiles = (JsonArray) src;
			if (JsonUtils.isEmpty(srcFiles)) {
				logger.error("T[{}] srcFiles is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFiles is null");
			}
			if (FTP_OPERATION_U.equals(ftpOperation)) {// 上传
				// 获取目标文件名
				JsonArray dstFiles = (JsonArray) getContextObject(dstFileNameSource, dstFileNameKey, srcFileIsList,
						dstFileNameAttr, context, businessFlowNode);
				if (JsonUtils.isEmpty(dstFiles) || dstFiles.size() != srcFiles.size()) {
					logger.error("T[{}] dstFiles is null or size of dstFiles is not equal to size of srcFiles. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "dstFiles is null  or size of dstFiles is not equal to size of srcFiles");
				}
				String srcFileName, dstFileName;
				for (int i = 0; i < srcFiles.size(); i++) {
					srcFileName = srcFiles.get(i).getAsString();
					dstFileName = dstFiles.get(i).getAsString();

					if (SRC_FILE_SOURCE_U.equals(srcFileSource)) {
						List<MultipartFile> files = context.getMultipartFileMap().get(srcFileName);
						if (CollectionUtils.isEmpty(files)) {
							logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
						}
						// 上传文件
						FtpRepositoryAbility.getInstance().upload(ftpType, files.get(0), dstFileName, businessFlowNode.getSystemId(), ftpResourceId);
					} else if (SRC_FILE_SOURCE_P.equals(srcFileSource)) {
						File srcFile = (File) getContextObject(srcFileSource, srcFileName, false, null, context, businessFlowNode);
						if (srcFile == null) {
							logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
						}
						// 上传文件
						FtpRepositoryAbility.getInstance().upload(ftpType, srcFile, dstFileName, businessFlowNode.getSystemId(), ftpResourceId);
					} else {
						logger.error("T[{}] invalid srcFileSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid srcFileSource");
					}
				}

			} else if (FTP_OPERATION_D.equals(ftpOperation)) {// 下载
				List<File> resultList = new ArrayList<>();
				String srcFile;
				for (int i = 0; i < srcFiles.size(); i++) {
					srcFile = srcFiles.get(i).getAsString();
					resultList.add(FtpRepositoryAbility.getInstance().download(ftpType, srcFile,
							businessFlowNode.getSystemId(), ftpResourceId));
				}
				result = resultList;
			} else if (FTP_OPERATION_R.equals(ftpOperation)) {// 删除
				String srcFile;
				for (int i = 0; i < srcFiles.size(); i++) {
					srcFile = srcFiles.get(i).getAsString();
					FtpRepositoryAbility.getInstance().delete(ftpType, srcFile, businessFlowNode.getSystemId(),
							ftpResourceId);
				}
			} else {
				logger.error("T[{}] invalid ftp operation {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), ftpOperation, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp operation");
			}
		} else {// 单个文件
			String srcFileName = (String) src;
			if (StringUtils.isBlank(srcFileName)) {
				logger.error("T[{}] srcFile is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFile is null");
			}
			if (FTP_OPERATION_U.equals(ftpOperation)) {// 上传
				// 动态属性的情况
				if (!StringUtils.isBlank(dstFileNameAttr)) {
					dstFileNameAttr = this.dynamicAttrProcess(dstFileNameAttr, businessFlowNode, context);
				}
				// 获取目标文件名
				String dstFile = (String) getContextObject(dstFileNameSource, dstFileNameKey, srcFileIsList, dstFileNameAttr, context, businessFlowNode);

				if (SRC_FILE_SOURCE_U.equals(srcFileSource)) {
					List<MultipartFile> files = context.getMultipartFileMap().get(srcFileName);
					if (CollectionUtils.isEmpty(files)) {
						logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
					}
					// 上传文件
					FtpRepositoryAbility.getInstance().upload(ftpType, files.get(0), dstFile, businessFlowNode.getSystemId(), ftpResourceId);
				} else if (SRC_FILE_SOURCE_P.equals(srcFileSource)) {
					File srcFile = (File) getContextObject(srcFileSource, srcFileName, false, null, context, businessFlowNode);
					if (srcFile == null) {
						logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
					}
					// 上传文件
					FtpRepositoryAbility.getInstance().upload(ftpType, srcFile, dstFile, businessFlowNode.getSystemId(), ftpResourceId);
				} else {
					logger.error("T[{}] invalid srcFileSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid srcFileSource");
				}
			} else if (FTP_OPERATION_D.equals(ftpOperation)) {// 下载
				// 下载文件
				result = FtpRepositoryAbility.getInstance().download(ftpType, srcFileName, businessFlowNode.getSystemId(), ftpResourceId);
			} else if (FTP_OPERATION_R.equals(ftpOperation)) {// 删除
				// 删除文件
				FtpRepositoryAbility.getInstance().delete(ftpType, srcFileName, businessFlowNode.getSystemId(), ftpResourceId);
			} else {
				logger.error("T[{}] invalid ftp operation {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), ftpOperation, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp operation");
			}
		}

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}
}
