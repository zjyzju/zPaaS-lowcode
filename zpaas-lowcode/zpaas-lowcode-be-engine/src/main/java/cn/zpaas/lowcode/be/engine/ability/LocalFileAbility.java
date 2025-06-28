package cn.zpaas.lowcode.be.engine.ability;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.utils.FileUtils;
import cn.zpaas.lowcode.utils.StringUtils;

/**
 * @author zjy 本地文件使用方法封装
 */
public class LocalFileAbility {

	private static Logger logger = LoggerFactory.getLogger(LocalFileAbility.class);

	private static final String DOT_DOT = "..";

	// LocalFileAbility实例，实现单例模式
	private static LocalFileAbility instance = null;
	
	private String tempFilePath = "./tempFile/";

	private String fileRootPath = "./fileRoot/";
	
	// 私有化构造函数
	private LocalFileAbility() {

	}

	public static LocalFileAbility getInstance() {
		return instance;
	}

	/**
	 * LocalFileAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// LocalFileAbility
		LocalFileAbility repository = new LocalFileAbility();

		//初始化临时文件目录
		repository.tempFilePath = initService.getTempFilePath();
		//初始化根文件目录
		repository.fileRootPath = initService.getFileRootPath();

		// 初始化完成，赋值给属性instance
		instance = repository;
		
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}
	
	/**
	 * 获取临时文件Path
	 * @param srcFile
	 * @return
	 */
	public Path getTempFilePath(String srcFile) {
		if(!this.isValidPath(srcFile)) {
			logger.error("invalid file, can't contain '..'");
			return null;
		}
		Path path = Paths.get(srcFile);
		path = FileUtils.addUUID2FilePath(path);
		StringBuilder stringBuilder = new StringBuilder(this.tempFilePath);
		stringBuilder.append(path.getFileName());
		Path tmpPath = Paths.get(stringBuilder.toString());
		return tmpPath;
	}

	/**
	 * 删除临时文件
	 * @param srcFile
	 * @return
	 */
	public boolean deleteTempFile(Path srcFile) {
		if(srcFile == null) {
			return false;
		}
		Path tmpPath = Paths.get(this.tempFilePath);
		if(srcFile.startsWith(tmpPath)) {//判断是否是临时文件
			try {
				Files.deleteIfExists(srcFile);
			} catch (IOException e) {
				
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取文件Path, 限制在根文件目录下
	 * @param srcFile
	 * @return
	 */
	public String getFilePath(String srcFile) {
		if(!this.isValidPath(srcFile)) {
			logger.error("invalid file, can't contain '..'");
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder(this.fileRootPath);
		stringBuilder.append(srcFile);
		Path filePath = Paths.get(stringBuilder.toString());
		
		return filePath.toAbsolutePath().toString();
	}

	/**
	 * 判断是否是合法的文件路径，在根路径下
	 * @param rootPath
	 * @param destPath
	 * @return
	 */
	private boolean isValidPath(String destPath) {
		if(!StringUtils.isBlank(destPath) && destPath.indexOf(DOT_DOT) < 0) {
			return true;
		}else {
			return false;
		}
	}
}
