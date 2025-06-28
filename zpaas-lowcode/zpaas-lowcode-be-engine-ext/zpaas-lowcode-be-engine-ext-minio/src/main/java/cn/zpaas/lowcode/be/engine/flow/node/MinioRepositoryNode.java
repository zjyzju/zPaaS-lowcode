package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.MinioRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * FtpRepositoryNode是Ftp存取节点的实现类，主要用于实现Ftp/Sminio的上传下载能力
 */
public class MinioRepositoryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(MinioRepositoryNode.class);

	private static final String CFG_MINIO_RESOURCE_ID_KEY = "minioResourceId"; //minio资源标识存放的Key
	private static final String CFG_MINIO_OPERATION_KEY = "minioOperation"; //minio操作存放的Key
	
	private static final String CFG_SRC_FILE_SOURCE_KEY = "srcFileSource"; //源文件的来源存放的Key
	
	private static final String CFG_SRC_FILE_NAME_SOURCE_KEY = "srcFileNameSource"; //源文件名的来源存放的Key
	private static final String CFG_SRC_FILE_NAME_KEY_KEY = "srcFileNameKey"; //源文件名的Key值存放的Key
	private static final String CFG_SRC_FILE_NAME_ATTR_KEY = "srcFileNameAttr"; //当源文件名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_BUCKET_SOURCE_KEY = "bucketSource"; //bucket的来源存放的Key
	private static final String CFG_BUCKET_KEY_KEY = "bucketKey"; //bucket的Key值存放的Key
	private static final String CFG_BUCKET_ATTR_KEY = "bucketAttr"; //当bucket是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_OBJECT_KEY_SOURCE_KEY = "objectKeySource"; //对象Key的来源存放的Key
	private static final String CFG_OBJECT_KEY_KEY = "objectKeyKey"; //对象Key的Key值存放的Key
	private static final String CFG_OBJECT_KEY_ATTR_KEY = "objectKeyAttr"; //当对象Key是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_CONTENT_TYPE_SOURCE_KEY = "contentTypeSource"; //内容类型的来源存放的Key
	private static final String CFG_CONTENT_TYPE_KEY_KEY = "contentTypeKey"; //内容类型的Key值存放的Key
	private static final String CFG_CONTENT_TYPE_ATTR_KEY = "contentTypeAttr"; //当内容类型是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String CFG_EXPIRE_SOURCE_KEY = "expireSource"; //有效期的来源存放的Key
	private static final String CFG_EXPIRE_KEY_KEY = "expireKey"; //有效期的Key值存放的Key
	private static final String CFG_EXPIRE_ATTR_KEY = "expireAttr"; //当有效期是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key
	
	private static final String MINIO_OPERATION_U = "U"; //minio操作-上传
	private static final String MINIO_OPERATION_D = "D"; //minio操作-下载
	private static final String MINIO_OPERATION_B = "B"; //minio操作-创建桶
	private static final String MINIO_OPERATION_R = "R"; //minio操作-删除文件
	private static final String MINIO_OPERATION_S = "S"; //minio操作-分享文件
	
	private static final String SRC_FILE_SOURCE_U = "U"; //上传文件
	private static final String SRC_FILE_SOURCE_P = "P"; //过程数据
		
	
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
		
		String minioResourceId = JsonUtils.getString(nodeCfg, CFG_MINIO_RESOURCE_ID_KEY);
		String minioOperation = JsonUtils.getString(nodeCfg, CFG_MINIO_OPERATION_KEY);
		
		String srcFileSource = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_SOURCE_KEY);
		
		String srcFileNameSource = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_SOURCE_KEY);
		String srcFileNameKey = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_KEY_KEY);
		String srcFileNameAttr = JsonUtils.getString(nodeCfg, CFG_SRC_FILE_NAME_ATTR_KEY);
		if (!StringUtils.isBlank(srcFileNameAttr)) {
			srcFileNameAttr = this.dynamicAttrProcess(srcFileNameAttr, businessFlowNode, context);
		}
		
		String bucketSource = JsonUtils.getString(nodeCfg, CFG_BUCKET_SOURCE_KEY);
		String bucketKey = JsonUtils.getString(nodeCfg, CFG_BUCKET_KEY_KEY);
		String bucketAttr = JsonUtils.getString(nodeCfg, CFG_BUCKET_ATTR_KEY);
		if (!StringUtils.isBlank(bucketAttr)) {
			bucketAttr = this.dynamicAttrProcess(bucketAttr, businessFlowNode, context);
		}
		
		String objectKeySource = JsonUtils.getString(nodeCfg, CFG_OBJECT_KEY_SOURCE_KEY);
		String objectKeyKey = JsonUtils.getString(nodeCfg, CFG_OBJECT_KEY_KEY);
		String objectKeyAttr = JsonUtils.getString(nodeCfg, CFG_OBJECT_KEY_ATTR_KEY);
		if (!StringUtils.isBlank(objectKeyAttr)) {
			objectKeyAttr = this.dynamicAttrProcess(objectKeyAttr, businessFlowNode, context);
		}
		
		String contentTypeSource = JsonUtils.getString(nodeCfg, CFG_CONTENT_TYPE_SOURCE_KEY);
		String contentTypeKey = JsonUtils.getString(nodeCfg, CFG_CONTENT_TYPE_KEY_KEY);
		String contentTypeAttr = JsonUtils.getString(nodeCfg, CFG_CONTENT_TYPE_ATTR_KEY);
		if (!StringUtils.isBlank(contentTypeAttr)) {
			contentTypeAttr = this.dynamicAttrProcess(contentTypeAttr, businessFlowNode, context);
		}
		
		String expireSource = JsonUtils.getString(nodeCfg, CFG_EXPIRE_SOURCE_KEY);
		String expireKey = JsonUtils.getString(nodeCfg, CFG_EXPIRE_KEY_KEY);
		String expireAttr = JsonUtils.getString(nodeCfg, CFG_EXPIRE_ATTR_KEY);
		if (!StringUtils.isBlank(expireAttr)) {
			expireAttr = this.dynamicAttrProcess(expireAttr, businessFlowNode, context);
		}
		
		//配置参数合法性校验
		if(StringUtils.isBlank(minioResourceId) || StringUtils.isBlank(minioOperation)) {
			logger.error("T[{}] minioResourceId and minioOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "minioResourceId and minioOperation can't be null. ");
		}
		if(StringUtils.isBlank(bucketSource) || StringUtils.isBlank(bucketKey)) {
			logger.error("T[{}] bucketSource and bucketKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucketSource and bucketKey can't be null. ");
		}
		if(!MINIO_OPERATION_B.equals(minioOperation) &&(StringUtils.isBlank(objectKeySource) || StringUtils.isBlank(objectKeyKey))) {
			logger.error("T[{}] when not create bucket, objectKeySource and objectKeyKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when not create bucket, objectKeySource and objectKeyKey can't be null. ");
		}
		
		if(MINIO_OPERATION_U.equals(minioOperation) && (StringUtils.isBlank(srcFileNameSource) || StringUtils.isBlank(srcFileNameKey) || 
			StringUtils.isBlank(srcFileSource) || StringUtils.isBlank(contentTypeSource) || StringUtils.isBlank(contentTypeKey))) {
			logger.error("T[{}] when upload to minio, srcFileSource srcFileNameSource and srcFileNameKey contentTypeSource and contentTypeKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when upload to minio,  srcFileSource srcFileNameSource and srcFileNameKey contentTypeSource and contentTypeKey can't be null. ");
		}
		
		if(MINIO_OPERATION_S.equals(minioOperation) &&(StringUtils.isBlank(expireSource) || StringUtils.isBlank(expireKey))) {
			logger.error("T[{}] when share file, expireSource and objectKeyKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when share file, expireKey and expireKey can't be null. ");
		}
		
		//获取桶
		String bucket = (String)getContextObject(bucketSource, bucketKey, false, bucketAttr, context, businessFlowNode);
		if(StringUtils.isBlank(bucket)) {
			logger.error("T[{}] bucket can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucket can't be null. ");
		}
		
		//返回结果
		Object result = null;

		if (MINIO_OPERATION_B.equals(minioOperation)) {// 创建桶
			// 创建桶
			MinioRepositoryAbility.getInstance().createBucket(bucket, businessFlowNode.getSystemId(), minioResourceId);
		}else {//其他
			//获取对象Key
			String objectKey = (String)getContextObject(objectKeySource, objectKeyKey, false, objectKeyAttr, context, businessFlowNode);
			if(StringUtils.isBlank(objectKey)) {
				logger.error("T[{}] when not create bucket, objectKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "when not create bucket, objectKey can't be null. ");
			}
			
			if (MINIO_OPERATION_D.equals(minioOperation)) {// 下载
				// 创建桶
				result = MinioRepositoryAbility.getInstance().download(bucket, objectKey, businessFlowNode.getSystemId(), minioResourceId);
			}else if (MINIO_OPERATION_R.equals(minioOperation)) {// 删除文件
				// 删除文件
				MinioRepositoryAbility.getInstance().delete(bucket, objectKey, businessFlowNode.getSystemId(), minioResourceId);
			}else if (MINIO_OPERATION_S.equals(minioOperation)) {// 分享文件
				//获取expire
				String expireString = (String)getContextObject(expireSource, expireKey, false, expireAttr, context, businessFlowNode);
				if(StringUtils.isBlank(expireString)) {
					logger.error("T[{}] when share file, expireString can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "when share file, expireString can't be null. ");
				}
				int expire = Integer.parseInt(expireString);
				// 分享文件
				result = MinioRepositoryAbility.getInstance().share(bucket, objectKey, expire, businessFlowNode.getSystemId(), minioResourceId);
			}else if (MINIO_OPERATION_U.equals(minioOperation)) {// 上传文件
				//获取源文件名
				String srcFileName = (String)getContextObject(srcFileNameSource, srcFileNameKey, false, srcFileNameAttr, context, businessFlowNode);
				if(StringUtils.isBlank(srcFileName)) {
					logger.error("T[{}] when upload, srcFileName can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "when upload, srcFileName can't be null. ");
				}
				
				//获取文件类型
				String contentType = (String)getContextObject(contentTypeSource, contentTypeKey, false, contentTypeAttr, context, businessFlowNode);
				if(StringUtils.isBlank(contentType)) {
					logger.error("T[{}] when upload, contentType can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "when upload, contentType can't be null. ");
				}
				
				if(SRC_FILE_SOURCE_U.equals(srcFileSource)) {//来自上传的文件
					List<MultipartFile> files = context.getMultipartFileMap().get(srcFileName);
					if(CollectionUtils.isEmpty(files)) {
						logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
					}
					//上传文件
					result = MinioRepositoryAbility.getInstance().upload(files.get(0), bucket, objectKey, contentType, businessFlowNode.getSystemId(), minioResourceId);
				}else if(SRC_FILE_SOURCE_P.equals(srcFileSource)) {//来自过程数据
					File srcFile = (File)getContextObject(srcFileSource, srcFileName, false, null, context, businessFlowNode);
					if(srcFile == null) {
						logger.error("T[{}] src file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "src file not found. ");
					}
					//上传文件
					result = MinioRepositoryAbility.getInstance().upload(srcFile, bucket, objectKey, contentType, businessFlowNode.getSystemId(), minioResourceId);
				}else {
					logger.error("T[{}] invalid srcFileSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid srcFileSource");
				}
			}else {
				logger.error("T[{}] invalid minio operation {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), minioOperation, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio operation");
			}
		}
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
