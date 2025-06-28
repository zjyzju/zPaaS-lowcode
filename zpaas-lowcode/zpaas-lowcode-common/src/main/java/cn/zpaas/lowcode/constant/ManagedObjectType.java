package cn.zpaas.lowcode.constant;

/**
 * 管理对象类型定义
 *
 * @author zjy
 * createTime 2025年04月21日-17:37:54
 */
public class ManagedObjectType {
    public static final String SERVICE_OBJECT = "S"; //服务对象
	public static final String DOMAIN_OBJECT = "D"; //领域对象
	public static final String VALUE_OBJECT = "V"; //值传递对象
	public static final String JAVA_OBJECT = "J"; //Java对象
	public static final String SERVICE_METHOD = "M"; //服务方法
	public static final String BI_DATA_SET = "B"; //BI数据集

    private ManagedObjectType() {

    }
}
