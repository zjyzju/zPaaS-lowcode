package cn.zpaas.lowcode.be.engine.ability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import cn.zpaas.lowcode.utils.FileUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.MinIOClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 基于MinIO的存取方法封装
 */
public class MinioRepositoryAbility {

	private static Logger logger = LoggerFactory.getLogger(MinioRepositoryAbility.class);

	// MinioRepositoryAbility实例，实现单例模式
	private static MinioRepositoryAbility instance = null;
	// MinIOClient的缓存对象，第一层Key是systemId，第二层Key是ftpId
	private Map<String, Map<String, MinIOClient>> minioMap = new HashMap<>();

	private String tempFilePath = "./tempFile/";
	
	// 私有化构造函数
	private MinioRepositoryAbility() {

	}

	public static MinioRepositoryAbility getInstance() {
		return instance;
	}

	/**
	 * MinioRepositoryAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache minio resource to mappingMap...");
		}
		// 初始化MinioRepositoryAbility实例
		MinioRepositoryAbility repository = new MinioRepositoryAbility();
		
		// 加载ServerResource数据-minio
		List<ServerResource> serverResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_MINIO);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_MINIO);
		}

		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条minio数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, MinIOClient> map = repository.minioMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.minioMap.put(serverResource.getSystemId(), map);
				}
				map.put(serverResource.getId(), new MinIOClient(serverResource.getUrl(), serverResource.getUsername(), serverResource.getPassword()));
			}
		} else {
			logger.info("no valid MinIO ServerResource.");
		}

		// 初始化完成，赋值给属性instance
		instance = repository;
		//初始化临时文件目录
		repository.tempFilePath = initService.getTempFilePath();
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
			logger.debug("load and cache minio resource to mappingMap...");
		}
		// 加载ServerResource数据-minio
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_MINIO);
		Map<String, MinIOClient> map = new HashMap<>();
		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条minio数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				map.put(serverResource.getId(), new MinIOClient(serverResource.getUrl(), serverResource.getUsername(), serverResource.getPassword()));
			}
		} 

		instance.minioMap.put(systemId, map);
		
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据systemId和minio资源Id获取minio客户端实例
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 */
	public MinIOClient getMinIOClient(String systemId, String minioResourceId) {
		Map<String, MinIOClient> map = minioMap.get(systemId);
		if(map != null) {
			return map.get(minioResourceId);
		}else {
			return null;
		}
	}
	
	/**
	 * 上传文件
	 * @param srcFile
	 * @param bucket
	 * @param objectKey
	 * @param contentType
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 * @throws EngineException
	 */
	public String upload(File srcFile, String bucket, String objectKey, String contentType, String systemId, String minioResourceId) throws EngineException{
		InputStream is = null;
		try {
			is = new FileInputStream(srcFile);
			return this.upload(is, bucket, objectKey, contentType, systemId, minioResourceId);
		} catch (FileNotFoundException e) {
			logger.error("srcFile is not found!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFile is not found!", e.getMessage(), e);
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	/**
	 * 上传文件
	 * @param srcFile
	 * @param bucket
	 * @param objectKey
	 * @param contentType
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 * @throws EngineException
	 */
	public String upload(MultipartFile srcFile, String bucket, String objectKey, String contentType, String systemId, String minioResourceId) throws EngineException{
		try {
			return this.upload(srcFile.getInputStream(), bucket, objectKey, contentType, systemId, minioResourceId);
		} catch (IOException e) {
			logger.error("srcFile is not found!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcFile is not found!", e.getMessage(), e);
		} 
	}

	/**
	 * 上传文件到minio
	 * @param srcFile
	 * @param bucket
	 * @param objectKey
	 * @param contentType
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 * @throws EngineException
	 */
	public String upload(InputStream srcFile, String bucket, String objectKey, String contentType, String systemId, String minioResourceId) throws EngineException{
		
		if(srcFile == null) {
			logger.error("upload src file is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file is null!");
		}
		//获取MinIO客户端
		MinIOClient minioClient = this.getMinIOClient(systemId, minioResourceId);
		if(minioClient == null) {
			logger.error("invalid minio resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio resourceId!");
		}
		
		try {
			String fileUrl = minioClient.uploadInputStream(bucket, objectKey, srcFile, contentType);
			return fileUrl;
		} catch (Exception e) {
			logger.error("upload src file to minio failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file to minio failed!", e.getMessage(), e);
		} 
		
	}
	
	/**
	 * 从minio下载文件
	 * @param bucket
	 * @param objectKey
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 * @throws EngineException
	 */
	public File download(String bucket, String objectKey, String systemId, String minioResourceId) throws EngineException{
		
		if(bucket == null || bucket.trim().length() == 0 || objectKey == null || objectKey.trim().length() == 0) {
			logger.error("bucket or objectKey is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucket or objectKey is null!");
		}
		//获取minio客户端
		MinIOClient minioClient = this.getMinIOClient(systemId, minioResourceId);
		if(minioClient == null) {
			logger.error("invalid minio resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio resourceId!");
		}
		
		try {
			Path tempPath = this.getTempFilePath(objectKey);
			if(!Files.exists(tempPath.getParent())) {
				Files.createDirectories(tempPath.getParent());
			}
			InputStream is = null;
			try {
				is = minioClient.download(bucket, objectKey);
				Files.copy(is, tempPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				logger.error("download file from minio failed! {}", e);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "download file from minio failed!");
			} finally {
				if(is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
			return tempPath.toFile();
		} catch (Exception e) {
			logger.error("download file from minio failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download file from minio failed!", e.getMessage(), e);
		} 
		
	}
	
	/**
	 * 创建桶
	 * @param bucket
	 * @param systemId
	 * @param minioResourceId
	 * @throws EngineException
	 */
	public void createBucket(String bucket, String systemId, String minioResourceId) throws EngineException{
		if(bucket == null) {
			logger.error("bucket name is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucket name is null!");
		}
		//获取MinIO客户端
		MinIOClient minioClient = this.getMinIOClient(systemId, minioResourceId);
		if(minioClient == null) {
			logger.error("invalid minio resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio resourceId!");
		}
		
		try {
			minioClient.makeBucket(bucket);
		} catch (Exception e) {
			logger.error("create bucket({}) on minio failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "create bucket on  minio failed!", e.getMessage(), e);
		} 
	}
	
	/**
	 * 删除文件
	 * @param bucket
	 * @param objectKey
	 * @param systemId
	 * @param minioResourceId
	 * @throws EngineException
	 */
	public void delete(String bucket, String objectKey, String systemId, String minioResourceId) throws EngineException{
		if(bucket == null || bucket.trim().length() == 0 || objectKey == null || objectKey.trim().length() == 0) {
			logger.error("bucket or objectKey is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucket or objectKey is null!");
		}
		//获取minio客户端
		MinIOClient minioClient = this.getMinIOClient(systemId, minioResourceId);
		if(minioClient == null) {
			logger.error("invalid minio resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio resourceId!");
		}
		
		try {
			minioClient.deleteFile(bucket, objectKey);
		} catch (Exception e) {
			logger.error("delete file from minio failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "delete file from minio failed!", e.getMessage(), e);
		} 
	}
	
	/**
	 * 分享文件
	 * @param bucket
	 * @param objectKey
	 * @param expire
	 * @param systemId
	 * @param minioResourceId
	 * @return
	 * @throws EngineException
	 */
	public String share(String bucket, String objectKey, int expire, String systemId, String minioResourceId) throws EngineException{
		if(bucket == null || bucket.trim().length() == 0 || objectKey == null || objectKey.trim().length() == 0) {
			logger.error("bucket or objectKey is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "bucket or objectKey is null!");
		}
		//获取minio客户端
		MinIOClient minioClient = this.getMinIOClient(systemId, minioResourceId);
		if(minioClient == null) {
			logger.error("invalid minio resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid minio resourceId!");
		}
		
		try {
			String url = minioClient.getSignedUrl(bucket, objectKey, expire);
			return url;
		} catch (Exception e) {
			logger.error("share file from minio failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "share file from minio failed!", e.getMessage(), e);
		} 
	}
	
	/**
	 * 获取临时文件Path
	 * @param srcFile
	 * @return
	 */
	public Path getTempFilePath(String srcFile) {
		Path path = Paths.get(srcFile);
		path = FileUtils.addUUID2FilePath(path);
		StringBuilder stringBuilder = new StringBuilder(this.tempFilePath);
		stringBuilder.append(path.getFileName());
		Path tmpPath = Paths.get(stringBuilder.toString());
		return tmpPath;
	}
}
