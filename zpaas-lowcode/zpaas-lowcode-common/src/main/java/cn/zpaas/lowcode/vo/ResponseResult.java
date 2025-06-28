package cn.zpaas.lowcode.vo;

import cn.zpaas.lowcode.constant.ResultStatus;

/**
 * @author zjy
 * 平台统一的返回结果类
 */
public class ResponseResult<T> {
	private String status;//状态
	private String message;//提示信息
	private String detailMessage;//详细信息
	private T data;//返回数据
	
	/**
	 * 构造方法
	 * @param <T>
	 * @param status
	 * @param message
	 * @param detailMessage
	 * @param data
	 * @return
	 */
	private static <T> ResponseResult<T> newResult(String status, String message, String detailMessage, T data) {
		ResponseResult<T> result = new ResponseResult<>();
		result.setStatus(status);
		result.setMessage(message);
		result.setDetailMessage(detailMessage);
		result.setData(data);
		return result;
	}

	/**
	 * 构造方法
	 * @param <T>
	 * @param status
	 * @param message
	 * @param detailMessage
	 * @return
	 */
	private static <T> ResponseResult<T> newResult(String status, String message, String detailMessage) {
		ResponseResult<T> result = new ResponseResult<>();
		result.setStatus(status);
		result.setMessage(message);
		result.setDetailMessage(detailMessage);
		return result;
	}

	public static <T> ResponseResult<T> success(T data) {
		return newResult(ResultStatus.SUCCEED, null, null, data);
	}

	public static <T> ResponseResult<T> success(String message, String detailMessage, T data) {
		return newResult(ResultStatus.SUCCEED, message, detailMessage, data);
	}

	public static <T> ResponseResult<T> success() {
		return newResult(ResultStatus.SUCCEED, null, null);
	}

	public static <T> ResponseResult<T> error(String status) {
		return newResult(status, null, null);
	}

	public static <T> ResponseResult<T> error(String status, String message) {
		return newResult(status, message, null);
	}

	public static <T> ResponseResult<T> error(String status, String message, String detailMessage) {
		return newResult(status, message, detailMessage);
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDetailMessage() {
		return detailMessage;
	}
	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
