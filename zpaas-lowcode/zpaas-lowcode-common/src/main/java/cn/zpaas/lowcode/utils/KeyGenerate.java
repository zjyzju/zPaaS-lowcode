package cn.zpaas.lowcode.utils;


/**
 * KeyGenerate
 *
 * @author zjy
 * createTime 2025年04月21日-18:05:05
 */
public class KeyGenerate {
	// 雪花ID生成器
	private static SnowFlakeID snowFlakeID = null;
	
	private KeyGenerate() {

	}

	/**
	 * 使用UUID生成KEY
	 * 
	 * @return
	 */
	public static String uuidKey() {
		return UUID.uuidKey();
	}

	/**
	 * 使用雪花算法生成KEY
	 * 
	 * @return
	 */
	public static long snowFlakeKey() {
		if (snowFlakeID == null) {
			initSnowFlake();
		}
		return snowFlakeID.nextId();
	}

	private static synchronized  void initSnowFlake() {
		if (snowFlakeID == null) {
			// 尝试从Sping容器中获取
			try {
				snowFlakeID = (SnowFlakeID) SpringContextUtils.getBean("snowFlakeID");
			} catch (Exception e) {
				// do nothing
			}
			// 如果获取不到，则直接初始化
			if (snowFlakeID == null) {
				snowFlakeID = new SnowFlakeID();
				snowFlakeID.init();
			}
		}
	}


}
