package cn.zpaas.lowcode.utils;

import java.util.Collection;

/**
 * 容器工具类
 *
 * @author zjy
 * createTime 2025年04月15日-14:24:25
 */
public class CollectionUtils {
    /**
     * 私有化构造方法
     */
    private CollectionUtils() {

    }

    /**
     * 配置列表对象是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection==null || collection.isEmpty();
    } 
}
