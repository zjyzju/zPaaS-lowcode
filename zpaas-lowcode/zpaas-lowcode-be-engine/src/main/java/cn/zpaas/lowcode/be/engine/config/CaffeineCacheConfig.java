package cn.zpaas.lowcode.be.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author zjy
 * CaffeineCache
 */
@Configuration
public class CaffeineCacheConfig {

	@Value("${caffeine.spec}")
	private String caffeineSpec;
	 
	@Bean
	public Cache<String, Object> caffeineCache() {
		return Caffeine.from(caffeineSpec).build();
	}
}
