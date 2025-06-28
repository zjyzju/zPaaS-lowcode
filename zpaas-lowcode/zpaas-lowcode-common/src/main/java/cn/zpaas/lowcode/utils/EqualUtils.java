package cn.zpaas.lowcode.utils;

/**
 * 等于比较工具类
 *
 * @author zjy
 * createTime 2025年04月18日-09:41:29
 */
public class EqualUtils {
    /**
     * 私有化构造方法
     */
    private EqualUtils() {

    }

    /**
     * Long类型比较
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(Long a, Long b) {
        if(a == null && b == null) {
            return true;
        }else if(a == null) {
            return false;
        }else {
            return a.compareTo(b) == 0;
        }
    }

    public static boolean isEqual(long a, long b) {
        return isEqual(Long.valueOf(a), Long.valueOf(b));
    }

    public static boolean isEqual(Long a, long b) {
        return isEqual(a, Long.valueOf(b));
    }

    /**
     * Integer类型比较
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(Integer a, Integer b) {
        if(a == null && b == null) {
            return true;
        }else if(a == null) {
            return false;
        }else {
            return a.compareTo(b) == 0;
        }
    }

    public static boolean isEqual(Integer a, int b) {
        return a.compareTo(Integer.valueOf(b)) == 0;
    }

    public static boolean isEqual(int a, int b) {
        return a == b;
    }

    /**
     * String类型比较
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(String a, String b) {
        if(a == null && b == null) {
            return true;
        }else if(a == null) {
            return false;
        }else {
            return a.equals(b);
        }
    }

    /**
     * Object类型比较
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(Object a, Object b) {
        if(a == null && b == null) {
            return true;
        }else if(a == null) {
            return false;
        }else {
            return a.equals(b);
        }
    }
}
