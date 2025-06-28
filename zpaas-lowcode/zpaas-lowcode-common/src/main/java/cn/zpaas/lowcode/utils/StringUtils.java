package cn.zpaas.lowcode.utils;

import java.util.Random;

/**
 * 字符串处理工具类
 *
 * @author zjy
 * createTime 2025年04月14日-17:35:14
 */
public class StringUtils {

    //包含所有字符，用于生成随机字符串
    private static final String ALL_STRING="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    //随机对象，用于生成随机字符串
    private static final Random  random = new Random();
	//空字符串
	private static final String ENPTY_STRING = "";

    /**
     * 工具类私有化构造函数
     */
    private StringUtils() {
        //do nothing
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }
    
    /**
     * 判断字符串是否为null
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
		return (str == null);
	}

	/**
	 * 返回一个空字符串
	 * @return
	 */
	public static String emptyString() {
		return ENPTY_STRING;
	}
    
    /**
     * 生成随机字符串
     * @param length
     * @return
     */
    public static String randomString(int length){
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomNum = 0;
        for(int i = 0 ;i < length ; i ++) {
            randomNum = random.nextInt(ALL_STRING.length());
            randomStringBuilder.append(ALL_STRING.charAt(randomNum));
        }
        return randomStringBuilder.toString();
    }
	
	/**
	 * 左补齐
	 * @param target 目标字符串
	 * @param fix 补齐字符
	 * @param length 目标长度
	 * @return
	 */
	public static String lPad(String target, String fix, int length){
		if(target == null || fix == null || target.length() >= length)
			return target;
		StringBuilder newStr = new StringBuilder();
		for(int i=0; i<(length-target.length()); i++){
			newStr.append(fix);
		}
		return newStr.append(target).toString();
	}
	
	/**
	 * 右补齐
	 * @param target 目标字符串
	 * @param fix 补齐字符
	 * @param length 目标长度
	 * @return
	 */
	public static String rPad(String target, String fix, int length){
		if(target==null || fix ==null || target.length() >= length)
			return target;
		StringBuilder newStr = new StringBuilder();
		newStr.append(target);
		for(int i=0; i<length-target.length(); i++){
			newStr.append(fix);
		}
		return newStr.toString();
	}
	
	/**
	 * 字符串数据join操作
	 * @param strs 需要合并的字符串
	 * @param spi 分隔符
	 * @return
	 * @author 
	 */
	public static String join(String[] strs, String spi){
		StringBuilder buf = new StringBuilder();
		int step = 0;
		for(String str : strs){
			buf.append(str);
			if (step ++ < strs.length - 1)
				buf.append(spi);
		}
		return buf.toString();
	}
}
