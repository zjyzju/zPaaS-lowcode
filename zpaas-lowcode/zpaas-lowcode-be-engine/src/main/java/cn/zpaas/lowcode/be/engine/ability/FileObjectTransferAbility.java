package cn.zpaas.lowcode.be.engine.ability;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.RowProcessor;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.FileObjectMapping;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 实现文件对象转换的能力
 */
public class FileObjectTransferAbility {
	private static Logger logger = LoggerFactory.getLogger(FileObjectTransferAbility.class);
	
	// FileObjectTransferAbility实例，用于实现单例模式
	private static FileObjectTransferAbility instance = null;

	// 用来缓存FileObjectMapping对象数据的Map
	private Map<String, Map<String, FileObjectMapping>> fileObjectMappingMap = new HashMap<>();

	// 私有化构造函数
	private FileObjectTransferAbility() {

	}

	// 获取FileObjectTransferAbility实例，用于实现单例模式
	public static FileObjectTransferAbility getInstance() {
		return instance;
	}

	/**
	 * FileObjectTransferAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化FileObjectTransferAbility对象
		FileObjectTransferAbility ability = new FileObjectTransferAbility();

		// 加载FileObjectMapping数据
		List<FileObjectMapping> fileObjectMappings = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			fileObjectMappings = initService.listFileObjectMappings();
		} else {// 加载指定业务系统的数据
			fileObjectMappings = initService.listFileObjectMappings(systemId);
		}

		if (!CollectionUtils.isEmpty(fileObjectMappings)) {
			// 循环处理每条FileObjectMapping数据
			for (FileObjectMapping fileObjectMapping : fileObjectMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(fileObjectMapping.getSystemId())) {
					continue;
				}
				// 将FileObjectMapping对象加入缓存
				Map<String, FileObjectMapping> map = ability.fileObjectMappingMap.get(fileObjectMapping.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					ability.fileObjectMappingMap.put(fileObjectMapping.getSystemId(), map);
				}
				map.put(fileObjectMapping.getId(), fileObjectMapping);
			}
		} else {
			logger.info("no valid FileObjectMapping.");
		}

		// 初始化完成，将FileObjectTransferAbility对象赋值给属性instance
		instance = ability;

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
		}
		// 加载FileObjectMapping数据
		List<FileObjectMapping> fileObjectMappings = initService.listFileObjectMappings(systemId);
		Map<String, FileObjectMapping> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(fileObjectMappings)) {
			// 循环处理每条FileObjectMapping数据
			for (FileObjectMapping fileObjectMapping : fileObjectMappings) {
				// systemId为空的数据不缓存
				if (StringUtils.isBlank(fileObjectMapping.getSystemId())) {
					continue;
				}
				// 将FileObjectMapping对象加入缓存
				map.put(fileObjectMapping.getId(), fileObjectMapping);
			}
		} 
		instance.fileObjectMappingMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 根据业务系统标识和文件对象映射标识获取对应的文件对象映射对象
	 * 
	 * @param systemId      业务系统标识
	 * @param fileObjectMappingId 文件对象映射标识
	 * @return 返回FileObjectMapping对象
	 */
	public FileObjectMapping getFileObjectMapping(String systemId, String fileObjectMappingId) {
		if (StringUtils.isBlank(systemId)) {
			return null;
		}
		Map<String, FileObjectMapping> map = fileObjectMappingMap.get(systemId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return map.get(fileObjectMappingId);
	}

	/**
	 * 按mappingId指定的文件对象映射规则，将源对象的数据转换成目标文件
	 * 
	 * @param systemId      业务系统标识
	 * @param fileObjectMappingId 文件对象映射标识
	 * @param srcArray     源对象
	 */
	public File objectToFile(String systemId, String fileObjectMappingId, JsonArray srcArray)
			throws EngineException {
		// 如果systemId或fileObjectMappingId为空，直接返回
		if (StringUtils.isBlank(systemId)) {
			logger.error("systemId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId is null");
		}
		if (StringUtils.isBlank(fileObjectMappingId)) {
			logger.error("fileObjectMappingId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fileObjectMappingId is null");
		}

		// 源对象不能为空
		if (JsonUtils.isEmpty(srcArray)) {
			logger.error("srcObject is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "srcObject is null");
		}

		// 获取对应的文件对象映射对象
		FileObjectMapping fileObjectMapping = getFileObjectMapping(systemId, fileObjectMappingId);

		// 如果获取不到有效的对象，直接报错
		if (fileObjectMapping == null || CollectionUtils.isEmpty(fileObjectMapping.getAttributeColumnMappings())) {
			logger.error("can't get valid FileObjectMapping or AttributeColumnMappings. {}", fileObjectMappingId);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid FileObjectMapping or AttributeColumnMappings.");
		}
		
		
		return null;
	}
	
	/**
	 * 按mappingId指定的文件对象映射规则，将源文件中的数据转换到目标对象
	 * 
	 * @param systemId      业务系统标识
	 * @param fileObjectMappingId 文件对象映射标识
	 * @param srcFile     源对象
	 */
	public JsonArray fileToObject(String systemId, String fileObjectMappingId, File srcFile)
			throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("Object to file transfer is started.");
		}
		// 如果systemId或fileObjectMappingId为空，直接返回
		if (StringUtils.isBlank(systemId)) {
			logger.error("systemId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId is null");
		}
		if (StringUtils.isBlank(fileObjectMappingId)) {
			logger.error("fileObjectMappingId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fileObjectMappingId is null");
		}

		// 源文件不能为空
		if (srcFile == null) {
			logger.error("srcFile is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "srcFile is null");
		}

		// 获取对应的文件对象映射对象
		FileObjectMapping fileObjectMapping = getFileObjectMapping(systemId, fileObjectMappingId);

		// 如果获取不到有效的对象，直接报错
		if (fileObjectMapping == null || CollectionUtils.isEmpty(fileObjectMapping.getAttributeColumnMappings())) {
			logger.error("can't get valid FileObjectMapping or AttributeColumnMappings. {}", fileObjectMappingId);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid FileObjectMapping or AttributeColumnMappings.");
		}
		
		
		if(logger.isDebugEnabled()) {
			logger.debug("Object to file transfer is finished.");
		}

		return null;
	}

	/**
	 * 按mappingId指定的文件对象映射规则，将源文件中的数据转换到目标对象，流式处理
	 * 
	 * @param systemId      业务系统标识
	 * @param fileObjectMappingId 文件对象映射标识
	 * @param srcFile     源对象
	 * @param processor 行数据处理器
	 */
	public void fileToObject(String systemId, String fileObjectMappingId, File srcFile, RowProcessor processor)
			throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("Object to file transfer is started.");
		}
		// 如果systemId或fileObjectMappingId为空，直接返回
		if (StringUtils.isBlank(systemId)) {
			logger.error("systemId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "systemId is null");
		}
		if (StringUtils.isBlank(fileObjectMappingId)) {
			logger.error("fileObjectMappingId is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "fileObjectMappingId is null");
		}

		// 源文件不能为空
		if (srcFile == null) {
			logger.error("srcFile is null");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "srcFile is null");
		}

		// 获取对应的文件对象映射对象
		FileObjectMapping fileObjectMapping = getFileObjectMapping(systemId, fileObjectMappingId);

		// 如果获取不到有效的对象，直接报错
		if (fileObjectMapping == null || CollectionUtils.isEmpty(fileObjectMapping.getAttributeColumnMappings())) {
			logger.error("can't get valid FileObjectMapping or AttributeColumnMappings. {}", fileObjectMappingId);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't get valid FileObjectMapping or AttributeColumnMappings.");
		}
		
		
		if(logger.isDebugEnabled()) {
			logger.debug("Object to file transfer is finished.");
		}

	}
}
