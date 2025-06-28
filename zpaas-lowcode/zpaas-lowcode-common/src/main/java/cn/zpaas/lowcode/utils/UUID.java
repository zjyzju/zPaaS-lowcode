package cn.zpaas.lowcode.utils;

/**
 * 生成UUID
 *
 * @author zjy
 * createTime 2025年04月15日-18:01:15
 */
public class UUID {
    private static final String MINUS_FLAG = "-";
	private static final String EMPTY_FLAG = "";
	
    /**
     * 私有化构造方法
     */
    private UUID() {

    }

	/**
	 * 使用UUID生成KEY
	 * @return
	 */
	public static String uuidKey() {
		return java.util.UUID.randomUUID().toString().replace(MINUS_FLAG, EMPTY_FLAG);
	}

}
