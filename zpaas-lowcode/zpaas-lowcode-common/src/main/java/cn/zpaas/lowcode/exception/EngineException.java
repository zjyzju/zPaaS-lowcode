package cn.zpaas.lowcode.exception;

/**
 * 引擎异常类
 *
 * @author zjy
 * createTime 2025年04月21日-17:54:23
 */
public class EngineException extends Exception {
	
	private final String status;//状态
	private final String message;//提示信息
	private final String detailMessage;//详细信息
	
	public EngineException(String status, String message, String detailMessage, Exception rootException) {
		super(message, rootException);
		this.status = status;
		this.message = message;
		this.detailMessage = detailMessage;		
	}
	
	public EngineException(String status, String message, String detailMessage) {
		this(status, message, detailMessage, null);
	}
	
	public EngineException(String status, String message) {
		this(status, message, null, null);
	}
	
	public String getStatus() {
		return status;
	}
	
    @Override
	public String getMessage() {
		return message;
	}

	public String getDetailMessage() {
		return detailMessage;
	}
	
}