package cn.zpaas.lowcode.be.engine.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Cache;

import cn.zpaas.lowcode.utils.SpringContextUtils;


/**
 * @author zjy 本地存取能力实现类
 */
public class LocalCacheAbility {
	private static Logger logger = LoggerFactory.getLogger(LocalCacheAbility.class);

	private static final String DEFAULT_CACHE_NAME = "caffeineCache";
	
	// LocalCacheAbility实例，实现单例模式
	private static LocalCacheAbility instance = null;
	
	private Cache<String, Object> cache;
	
	// 私有化构造函数
	private LocalCacheAbility() {

	}

	public static LocalCacheAbility getInstance() {
		if(instance != null) {
			return instance;
		}else {
			synchronized(logger) {
				if(instance == null) {
					init();
				}
				return instance;
			}
		}
	}

	/**
	 * LocalCacheAbility类的初始化方法
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
			logger.debug("init LocalCacheAbility");
		}
		// 初始化LocalCacheAbility实例
		LocalCacheAbility repository = new LocalCacheAbility();
		String localCacheName = SpringContextUtils.getEnvironmentProperty("caffeine.name", DEFAULT_CACHE_NAME);
		repository.cache = SpringContextUtils.getBean(localCacheName, Cache.class);
		instance = repository;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}
	
	/**
	 * 设置缓存
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		this.cache.put(key, value);
	}
	
	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return this.cache.getIfPresent(key);
	}

	/**
	 * 失效缓存
	 * @param key
	 * @return
	 */
	public void invalidate(String key) {
		this.cache.invalidate(key);
	}
}
