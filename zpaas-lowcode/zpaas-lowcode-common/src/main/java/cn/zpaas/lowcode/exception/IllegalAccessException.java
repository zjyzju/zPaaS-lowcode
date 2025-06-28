package cn.zpaas.lowcode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 非法访问异常类
 *
 * @author zjy
 * createTime 2025年04月21日-17:54:23
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class IllegalAccessException extends EngineException {
	
	public IllegalAccessException(String status, String message, String detailMessage, Exception rootException) {
		super(status, message, detailMessage, rootException);
	}
	
	public IllegalAccessException(String status, String message, String detailMessage) {
		this(status, message, detailMessage, null);
	}
	
	public IllegalAccessException(String status, String message) {
		this(status, message, null, null);
	}
	
}