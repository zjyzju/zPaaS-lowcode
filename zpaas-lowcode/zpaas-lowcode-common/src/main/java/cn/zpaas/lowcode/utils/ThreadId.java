package cn.zpaas.lowcode.utils;

/**
 * ThreadId工具类
 *
 * @author zjy
 * createTime 2025年04月18日-10:30:21
 */
public class ThreadId {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
	
    /**
     * 私有化构造方法
     */
    private ThreadId() {

    }

	public static String getThreadId() {
		if(threadLocal.get() == null) {
			threadLocal.set(String.valueOf(UUID.uuidKey()));
		}
		return threadLocal.get();
	}
	
	public static void setThreadId(String threadId) {
		threadLocal.set(threadId);
	}
	
	public static void setThreadIdWithIP(String ip) {
		threadLocal.set(ip + ":" + UUID.uuidKey());
	}

    public static void removeThreadId() {
        threadLocal.remove();
    }
}
