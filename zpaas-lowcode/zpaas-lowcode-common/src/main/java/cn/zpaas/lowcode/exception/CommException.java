package cn.zpaas.lowcode.exception;

import cn.zpaas.lowcode.utils.StringUtils;

/**
 * 公共异常类
 *
 * @author zjy
 * createTime 2025年04月15日-17:36:23
 */
public class CommException extends RuntimeException{
    private static final String COLON = ":";

    private final String errorCode;
	private final String errorDetail;
	
    public CommException(String errorDetail) {
		super(errorDetail);
		this.errorCode = StringUtils.emptyString();
		this.errorDetail = errorDetail;
	}

    public CommException(String errorDetail, Exception ex) {
		super(errorDetail, ex);
		this.errorCode = StringUtils.emptyString();
		this.errorDetail = errorDetail;
	}

	public CommException(String errorCode, String errorDetail) {
		super(errorCode + COLON + errorDetail);
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}
	
	public CommException(String errorCode, String errorDetail, Exception ex) {
		super(errorCode + COLON + errorDetail, ex);
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDetail() {
		return errorDetail;
	}
}
