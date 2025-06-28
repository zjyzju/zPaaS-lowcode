package cn.zpaas.lowcode.vo;

import cn.zpaas.lowcode.bean.PageParam;

/**
 * @author zjy
 * 引擎统一的返回结果类
 */
public class Result<T> {
	private String status;//状态
	private String message;//提示信息
	private String detailMessage;//详细信息
	private T data;//返回数据
	private PageParam pageParam;//分页信息
	
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
	public PageParam getPageParam() {
		return pageParam;
	}
	public void setPageParam(PageParam pageParam) {
		this.pageParam = pageParam;
	}
	
	
}
