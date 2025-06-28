package cn.zpaas.lowcode.constant;

/**
 * 结果状态编码定义
 *
 * @author zjy
 * createTime 2025年04月21日-17:43:40
 */
public class ResultStatus {
    public static final String SUCCEED = "200";//成功状态
	public static final String NOT_FOUND = "404";//未找到对应服务状态
	public static final String INTERNAL_ERROR = "500";//内部错误状态
	public static final String BAD_REQUEST = "400";//错误的请求状态
	public static final String BUSINESS_ERROR = "900";//业务错误状态
	public static final String NOT_LOGON_ERROR = "901";//未登录状态

    private ResultStatus() {
        
    }
}
