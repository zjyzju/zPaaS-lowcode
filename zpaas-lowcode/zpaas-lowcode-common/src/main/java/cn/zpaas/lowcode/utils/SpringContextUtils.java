package cn.zpaas.lowcode.utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 手动获取Spring管理Bean实例的工具类
 *
 * @author zjy
 * createTime 2025年04月18日-11:28:33
 */
@Component("springContextUtils")
public class SpringContextUtils implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext = null;
 
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }
 
    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
 
    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(@NonNull String name) {
        return getApplicationContext().getBean(name);
    }
 
    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(@NonNull Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过class获取Beans.
     *
     * @param clazz
     * @param Map<String, T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(@NonNull Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }
 
    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(@NonNull String name, @NonNull Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
    
    /**
     * 通过Key获取配置信息
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    public static String getEnvironmentProperty(@NonNull String key, @NonNull String defaultValue) {
    	return getApplicationContext().getEnvironment().getProperty(key, defaultValue);
    }
}
