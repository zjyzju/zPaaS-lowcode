package cn.zpaas.lowcode.vo;

import java.util.List;

/**
 * @author zjy
 * 统一的返回结果类，批量结果返回类
 */
public class ListResult<T> {
	private String status;//状态
	private String message;//提示信息
	private String detailMessage;//详细信息
	private List<T> data;//返回数据
	
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
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	
	
}
