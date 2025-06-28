package cn.zpaas.lowcode.utils;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类工具
 *
 * @author zjy
 * createTime 2025年04月15日-17:16:57
 */
public class ClassUtils {
    public static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    //原始类型的Map
    private static HashMap<String, Class<?>> primitiveClassMap = new HashMap<>();
	static {
		primitiveClassMap.put("boolean", Boolean.TYPE);
		primitiveClassMap.put("byte", Byte.TYPE);
		primitiveClassMap.put("short", Short.TYPE);
		primitiveClassMap.put("int", Integer.TYPE);
		primitiveClassMap.put("long", Long.TYPE);
		primitiveClassMap.put("float", Float.TYPE);
		primitiveClassMap.put("double", Double.TYPE);
		primitiveClassMap.put("char", Character.TYPE);
		
	}

    /**
     * 私有化构构造方法
     */
    private ClassUtils() {

    }
	
	/**
	 * 获取对应的类对象
	 * @param className
	 * @return
	 * @throws EngineException
	 */
	public static Class<?> getClass(String className){
		Class<?> resultClass = primitiveClassMap.get(className);
		if(resultClass != null) {
			return resultClass;
		}
		
		try {
			resultClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			logger.error("get target class failed.", e);
		}
		
		return resultClass;
	}
	
	/**
	 * 获取传入类的所有父类
	 * @param clazz
	 * @param superClasseSet，Set类型的列表，用于去重和返回
	 */
	public static void getParentClass(Class<?> clazz, Set<String> superClasseSet) {
		if(superClasseSet == null) {
			return;
		}		
		//获取父类
		Class<?> superClass = clazz.getSuperclass();
		//获取不到时中止递归
		if(superClass == null) {
			return;
		}
		//加入到set中
		superClasseSet.add(superClass.getName());
		//递归调用获取父类的父类
		ClassUtils.getParentClass(superClass, superClasseSet);
	}
	
	/**
	 * 获取传入类的所有父类	接口
	 * @param clazz
	 * @param superClasseSet，Set类型的列表，用于去重和返回
	 */
	public static void getParentInterface(Class<?> clazz, Set<String> superClasseSet) {
		if(superClasseSet == null) {
			return;
		}		
		//获取实现的接口类
		Class<?>[] interfaces = clazz.getInterfaces();
		//获取不到时中止递归
		if(interfaces == null || interfaces.length == 0) {
			return;
		}
		//循环处理每个接口
		for(Class<?> inter : interfaces) {
			//加入到set中
			superClasseSet.add(inter.getName());
			////递归调用获取接口类的接口类
			ClassUtils.getParentClass(inter, superClasseSet);
		}
	}
}
