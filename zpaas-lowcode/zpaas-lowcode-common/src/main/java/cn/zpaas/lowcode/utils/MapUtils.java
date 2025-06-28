package cn.zpaas.lowcode.utils;

import java.util.Map;

/**
 * Map容器工具类
 *
 * @author zjy
 * createTime 2025年04月15日-14:24:25
 */
public class MapUtils {
    /**
     * 私有化构造方法
     */
    private MapUtils() {

    }

    /**
     * 配置列表对象是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Map<?,?> map) {
        return map==null || map.isEmpty();
    } 
}
