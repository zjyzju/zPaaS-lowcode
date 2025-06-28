package cn.zpaas.lowcode.be.engine.ability;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.be.engine.utils.HessianSerializer;
import cn.zpaas.lowcode.be.engine.utils.RedisClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.ServerResourceType;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy Redis存取能力实现类
 */
public class RedisRepositoryAbility {
	private static Logger logger = LoggerFactory.getLogger(RedisRepositoryAbility.class);

	private static final List<String> IGNORE_COMMAND = Arrays.asList("auth", "quit", "flushdb", "flushall", "select",
			"save", "bgsave", "shundown", "info", "monitor", "slaveof", "config");//忽略执行的redis命令

	// RedisRepositoryAbility实例，实现单例模式
	private static RedisRepositoryAbility instance = null;
	// RedisClient的缓存对象，第一层Key是systemId，第二层Key是resourceId
	private Map<String, Map<String, RedisClient>> redisMap = new HashMap<>();

	// 私有化构造函数
	private RedisRepositoryAbility() {

	}

	public static RedisRepositoryAbility getInstance() {
		return instance;
	}

	/**
	 * 根据systemId和资源Id获取RedisClient客户端实例
	 * 
	 * @param systemId
	 * @param resourceId
	 * @return
	 */
	public RedisClient getRedisClient(String systemId, String resourceId) {
		Map<String, RedisClient> map = redisMap.get(systemId);
		if (map != null) {
			return map.get(resourceId);
		} else {
			return null;
		}
	}

	/**
	 * RedisRepositoryAbility类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("load and cache redis Server resource");
		}
		// 初始化RedisRepositoryAbility实例
		RedisRepositoryAbility repository = new RedisRepositoryAbility();

		// 加载ServerResource数据
		List<ServerResource> serverResources = null;
		if (systemId == null || systemId.trim().length() == 0) {// 加载所有数据
			serverResources = initService.listServerResources(ServerResourceType.RESOURCE_TYPE_REDIS);
		} else {// 加载指定业务系统的数据
			serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_REDIS);
		}

		if (serverResources != null && serverResources.size() > 0) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				Map<String, RedisClient> map = repository.redisMap.get(serverResource.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					repository.redisMap.put(serverResource.getSystemId(), map);
				}
				try {
					map.put(serverResource.getId(), new RedisClient(serverResource.getUrl(), serverResource.getUsername(),
							serverResource.getPassword(), serverResource.getServerCfg()));
				} catch (EngineException e) {
					logger.error("init RedisClient faile! resourceId:{}", serverResource.getId());
				}
			}
		} else {
			logger.info("no valid ServerResource.");
		}

		// 初始化完成，赋值给属性instance
		if (instance != null && instance.redisMap != null && !instance.redisMap.isEmpty()) {// 缓存刷新时，释放原资源
			instance.redisMap.forEach((system, map) -> {
				if (map != null && !map.isEmpty()) {
					map.forEach((resourceId, redisClient) -> {
						redisClient.close();
					});
				}
			});
		}
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
			logger.debug("load and cache redis Server resource");
		}

		//释放连接
		Map<String, RedisClient> oldMap = instance.redisMap.get(systemId);
		if (oldMap != null && !oldMap.isEmpty()) {
			oldMap.forEach((resourceId, redisClient) -> {
				redisClient.close();
			});
		}

		// 加载ServerResource数据
		List<ServerResource> serverResources = initService.listServerResources(systemId, ServerResourceType.RESOURCE_TYPE_REDIS);
		Map<String, RedisClient> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(serverResources)) {
			// 循环处理每条DataMapping数据
			for (ServerResource serverResource : serverResources) {
				// systemId为空的数据不缓存
				if (serverResource.getSystemId() == null || serverResource.getSystemId().trim().length() == 0) {
					continue;
				}
				// 将ServerResource对象加入缓存
				try {
					map.put(serverResource.getId(), new RedisClient(serverResource.getUrl(), serverResource.getUsername(),
							serverResource.getPassword(), serverResource.getServerCfg()));
				} catch (EngineException e) {
					logger.error("init RedisClient faile! resourceId:{}", serverResource.getId());
				}
			}
		} 
		instance.redisMap.put(systemId, map);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

	/**
	 * 执行redis命令
	 * @param resourceId
	 * @param systemId
	 * @param useObjectSerializer
	 * @param command
	 * @param params
	 * @return
	 * @throws EngineException
	 */
	public Object sendCommand(String resourceId, String systemId, boolean useObjectSerializer, String command, Object... params) throws EngineException {
		if(command == null || command.trim().length() == 0 || params == null || params.length == 0) {
			logger.error("redis command and param can't be null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "redis command and param can't be null!");
		}
		if(IGNORE_COMMAND.contains(command.toLowerCase())) {
			logger.error("the redis command is forbidden! {}", command);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "the redis command is forbidden!");
		}
		
		RedisClient redisClient = this.getRedisClient(systemId, resourceId);//获取redis客户端
		if(redisClient == null) {
			logger.error("get redis client failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "get redis client failed!");
		}
		
		//处理redis命令的参数
		List<byte[]> redisByteParams = new ArrayList<>();
		for(int i=0; i<params.length; i++) {
			Object param = params[i];
			if(param instanceof String) {//String类型的参数，直接使用String类型的getBytes方法进行序列化
				redisByteParams.add(((String)param).getBytes());
			}else if(param instanceof Integer || param instanceof Long || param instanceof Float) {//数字类型的参数，直接使用String类型的getBytes方法进行序列化
				redisByteParams.add((String.valueOf(param)).getBytes());
			}else {//对象类型的参数，使用HessianSerializer进行序列化
				try {
					redisByteParams.add(HessianSerializer.serialize(param));
				} catch (IOException e) {
					logger.error("serialize failed!", e);
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "serialize failed!");
				}
			}
		}
		//执行命令
		Object redisResult = redisClient.sendCommand(command, redisByteParams.toArray(new byte[0][]));
		
		//redis返回结果处理
		Object result = null;
		if(redisResult == null) {
			result = null;
		}else if(redisResult instanceof byte[]) {
			if(useObjectSerializer) {//使用对象序列化标识
				try {
					result = HessianSerializer.deserialize((byte[])redisResult);
				} catch (IOException e) {
					logger.error("deserialize failed!", e);
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "deserialize failed!");
				}
			}else {//直接使用String
				try {
					result = new String((byte[])redisResult, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncoding!", e);
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "UnsupportedEncoding!");
				}
			}
		}else if(redisResult instanceof List<?>) {//返回结果是List类型，转换成JsonArray类型
			if(redisResult != null) {
				@SuppressWarnings("unchecked")
				List<byte[]> list = (List<byte[]>)redisResult;
				JsonArray jsonArray = new JsonArray();
				for(byte[] item : list) {
					if(useObjectSerializer) {
						try {
							jsonArray.add(JsonUtils.toJsonElement(HessianSerializer.deserialize(item)));
						} catch (IOException e) {
							logger.error("deserialize failed!", e);
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "deserialize failed!");
						}
					}else {
						try {
							jsonArray.add(new String(item, "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							logger.error("UnsupportedEncoding!", e);
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "UnsupportedEncoding!");
						}
					}
				}
				result = jsonArray;
			}
		}else if(redisResult instanceof Long || redisResult instanceof Integer) {
			result = redisResult;
		}else {
			logger.error("invalid redis result!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid redis result!");
		}
		
		return result;
	}
}
