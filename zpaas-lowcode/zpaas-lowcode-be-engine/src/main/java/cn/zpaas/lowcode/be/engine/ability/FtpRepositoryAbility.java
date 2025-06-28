package cn.zpaas.lowcode.be.engine.ability;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.FtpClient;
import cn.zpaas.lowcode.be.engine.utils.SftpClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 基于FTP/SFTP的存取方法封装
 */
public class FtpRepositoryAbility {

	private static Logger logger = LoggerFactory.getLogger(FtpRepositoryAbility.class);

	// FtpRepositoryAbility实例，实现单例模式
	private static FtpRepositoryAbility instance = null;
	// SftpClient的缓存对象，第一层Key是systemId，第二层Key是sftpId
	private Map<String, Map<String, SftpClient>> sftpMap = new HashMap<>();
	// FtpClient的缓存对象，第一层Key是systemId，第二层Key是ftpId
	private Map<String, Map<String, FtpClient>> ftpMap = new HashMap<>();

	public static final String FTP_TYPE_FTP = "F"; //ftp类型-ftp
	public static final String FTP_TYPE_SFTP = "S"; //ftp类型-sftp
	
	
	
	// 私有化构造函数
	private FtpRepositoryAbility() {

	}

	public static FtpRepositoryAbility getInstance() {
		return instance;
	}

	/**
	 * FtpRepositoryAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache Ftp resource to mappingMap...");
		}
		// 初始化ORMRepository实例
		FtpRepositoryAbility repository = new FtpRepositoryAbility();
		
		// 加载ServerResource数据-ftp
		List<ServerResource> serverResources = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_FTP);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_FTP);
		}

		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, FtpClient> map = repository.ftpMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.ftpMap.put(serverResource.getSystemId(), map);
				}
				map.put(serverResource.getId(), new FtpClient(serverResource.getHost(), Integer.valueOf(serverResource.getPort()),
						serverResource.getUsername(), serverResource.getPassword()));
			}
		} else {
			logger.info("no valid FTP ServerResource.");
		}
		
		// 加载ServerResource数据-sftp
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_SFTP);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SFTP);
		}

		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, SftpClient> map = repository.sftpMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.sftpMap.put(serverResource.getSystemId(), map);
				}
				map.put(serverResource.getId(),
						new SftpClient(serverResource.getHost(), Integer.valueOf(serverResource.getPort()),
								serverResource.getUsername(), serverResource.getPassword()));
			}
		} else {
			logger.info("no valid SFTP ServerResource.");
		}

		// 初始化完成，赋值给属性instance
		instance = repository;
		
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
			logger.debug("load and cache Ftp resource to mappingMap...");
		}
		// 加载ServerResource数据-ftp
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_FTP);
		Map<String, FtpClient> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				map.put(serverResource.getId(), new FtpClient(serverResource.getHost(), Integer.valueOf(serverResource.getPort()),
						serverResource.getUsername(), serverResource.getPassword()));
			}
		} 
		instance.ftpMap.put(systemId, map);
		
		// 加载ServerResource数据-sftp
		serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_SFTP);
		Map<String, SftpClient> sftpMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(serverResource.getSystemId())) {
					continue;
				}
				// 将ServerResource对象加入缓存
				sftpMap.put(serverResource.getId(),
						new SftpClient(serverResource.getHost(), Integer.valueOf(serverResource.getPort()),
								serverResource.getUsername(), serverResource.getPassword()));
			}
		} 
		instance.sftpMap.put(systemId, sftpMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}
	
	/**
	 * 根据systemId和sftp资源Id获取Sftp客户端实例
	 * @param systemId
	 * @param sftpResourceId
	 * @return
	 */
	public SftpClient getSftpClient(String systemId, String sftpResourceId) {
		Map<String, SftpClient> map = sftpMap.get(systemId);
		if(map != null) {
			return map.get(sftpResourceId);
		}else {
			return null;
		}
	}
	
	/**
	 * 根据systemId和ftp资源Id获取Ftp客户端实例
	 * @param systemId
	 * @param ftpResourceId
	 * @return
	 */
	public FtpClient getFtpClient(String systemId, String ftpResourceId) {
		Map<String, FtpClient> map = ftpMap.get(systemId);
		if(map != null) {
			return map.get(ftpResourceId);
		}else {
			return null;
		}
	}

	/**
	 * 上传文件到sftp
	 * @param srcFile
	 * @param dst
	 * @param systemId
	 * @param sftpResourceId
	 * @throws EngineException
	 */
	public void upload2Sftp(InputStream srcFile, String dst, String systemId, String sftpResourceId) throws EngineException{
		
		if(srcFile == null) {
			logger.error("upload src file is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file is null!");
		}
		//获取sftp客户端
		SftpClient sftpClient = this.getSftpClient(systemId, sftpResourceId);
		if(sftpClient == null) {
			logger.error("invalid sftp resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid sftp resourceId!");
		}
		
		try {
			sftpClient.put(srcFile, dst);
		} catch (Exception e) {
			logger.error("upload src file to sftp failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file to sftp failed!", e.getMessage(), e);
		} 
	}
	
	/**
	 * 上传文件到ftp
	 * @param srcFile
	 * @param dst
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public void upload2Ftp(InputStream srcFile, String dst, String systemId, String ftpResourceId) throws EngineException{
		
		if(srcFile == null) {
			logger.error("upload src file is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file is null!");
		}
		//获取ftp客户端
		FtpClient ftpClient = this.getFtpClient(systemId, ftpResourceId);
		if(ftpClient == null) {
			logger.error("invalid ftp resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp resourceId!");
		}
		
		try {
			ftpClient.put(srcFile, dst);
		} catch (Exception e) {
			logger.error("upload src file to ftp failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "upload src file to ftp failed!", e.getMessage(), e);
		} 
	}
	
	/**
	 * 上传文件
	 * @param ftpType
	 * @param srcFile
	 * @param dst
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public void upload(String ftpType, MultipartFile srcFile, String dst, String systemId, String ftpResourceId) throws EngineException{
		try {
			if(FTP_TYPE_FTP.equals(ftpType)) {
				this.upload2Ftp(srcFile.getInputStream(), dst, systemId, ftpResourceId);
			}else if(FTP_TYPE_SFTP.equals(ftpType)) {
				this.upload2Sftp(srcFile.getInputStream(), dst, systemId, ftpResourceId);
			}else {
				logger.error("invalid ftp type!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp type!");
			}
		} catch (IOException e) {
			logger.error("IOException!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "IOException!", e.getMessage(), e);
		}
	}
	
	/**
	 * 上传文件
	 * @param ftpType
	 * @param srcFile
	 * @param dst
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public void upload(String ftpType, File srcFile, String dst, String systemId, String ftpResourceId) throws EngineException{
		InputStream is = null;
		
		try {
			is = new FileInputStream(srcFile);
			if(FTP_TYPE_FTP.equals(ftpType)) {
				this.upload2Ftp(is, dst, systemId, ftpResourceId);
			}else if(FTP_TYPE_SFTP.equals(ftpType)) {
				this.upload2Sftp(is, dst, systemId, ftpResourceId);
			}else {
				logger.error("invalid ftp type!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp type!");
			}
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
	 * 从sftp下载文件
	 * @param srcFile
	 * @param systemId
	 * @param sftpResourceId
	 * @throws EngineException
	 */
	public File downloadFromSftp(String srcFile, String systemId, String sftpResourceId) throws EngineException{
		
		if(srcFile == null) {
			logger.error("download src file is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download src file is null!");
		}
		//获取sftp客户端
		SftpClient sftpClient = this.getSftpClient(systemId, sftpResourceId);
		if(sftpClient == null) {
			logger.error("invalid sftp resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid sftp resourceId!");
		}
		
		try {
			Path tempPath = LocalFileAbility.getInstance().getTempFilePath(srcFile);
			if(!Files.exists(tempPath.getParent())) {
				Files.createDirectories(tempPath.getParent());
			}
			sftpClient.get(srcFile, tempPath.toAbsolutePath().toString());
			
			return tempPath.toFile();
		} catch (Exception e) {
			logger.error("download src file from sftp failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download src file from sftp failed!", e.getMessage(), e);
		} 
		
	}
	
	/**
	 * 从ftp下载文件
	 * @param srcFile
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public File downloadFromFtp(String srcFile, String systemId, String ftpResourceId) throws EngineException{
		
		if(srcFile == null) {
			logger.error("download src file is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download src file is null!");
		}
		//获取ftp客户端
		FtpClient ftpClient = this.getFtpClient(systemId, ftpResourceId);
		if(ftpClient == null) {
			logger.error("invalid ftp resourceId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp resourceId!");
		}
		
		try {
			Path tempPath = LocalFileAbility.getInstance().getTempFilePath(srcFile);
			if(!Files.exists(tempPath.getParent())) {
				Files.createDirectories(tempPath.getParent());
			}
			ftpClient.get(srcFile, tempPath.toAbsolutePath().toString());
			return tempPath.toFile();
		} catch (Exception e) {
			logger.error("download src file from ftp failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "download src file from ftp failed!", e.getMessage(), e);
		} 
		
	}
	
	/**
	 * 下载文件
	 * @param ftpType
	 * @param srcFile
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public File download(String ftpType, String srcFile, String systemId, String ftpResourceId) throws EngineException{
		if(FTP_TYPE_SFTP.equals(ftpType)) {
			return this.downloadFromSftp(srcFile, systemId, ftpResourceId);
		}else if(FTP_TYPE_FTP.equals(ftpType)) {
			return this.downloadFromFtp(srcFile, systemId, ftpResourceId);
		}else {
			logger.error("invalid ftp type!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp type!");
		}
	}
	
	/**
	 * 删除文件
	 * @param ftpType
	 * @param filePath
	 * @param systemId
	 * @param ftpResourceId
	 * @throws EngineException
	 */
	public void delete(String ftpType, String filePath, String systemId, String ftpResourceId) throws EngineException{
		if(filePath == null) {
			logger.error("delete filePath is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "delete filePath is null!");
		}
		if(FTP_TYPE_SFTP.equals(ftpType)) {
			//获取sftp客户端
			SftpClient sftpClient = this.getSftpClient(systemId, ftpResourceId);
			if(sftpClient == null) {
				logger.error("invalid sftp resourceId!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid sftp resourceId!");
			}
			sftpClient.delete(filePath);
		}else if(FTP_TYPE_FTP.equals(ftpType)) {
			//获取ftp客户端
			FtpClient ftpClient = this.getFtpClient(systemId, ftpResourceId);
			if(ftpClient == null) {
				logger.error("invalid ftp resourceId!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp resourceId!");
			}
			if(!ftpClient.delete(filePath)) {
				logger.error("delete filePath from ftp failed!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "delete filePath from ftp failed!");
			}
		}else {
			logger.error("invalid ftp type!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid ftp type!");
		}
	}
}
