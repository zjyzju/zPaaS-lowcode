package cn.zpaas.lowcode.be.engine.command;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * @author zjy
 * Command类的基类，用于定义公共属性
 */
public abstract class BaseCommand {
	private String tenantId;//租户标识
	private String systemId;//业务系统标识
	private JsonObject reqData;//请求数据
	@Expose(serialize = false, deserialize = false)
	private Map<String, List<MultipartFile>> multipartFileMap;//上传文件信息
	@Expose(serialize = false, deserialize = false)
	private SseEmitter sseEmitter;//服务器发送事件对象
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public JsonObject getReqData() {
		return reqData;
	}
	public void setReqData(JsonObject reqData) {
		this.reqData = reqData;
	}
	public Map<String, List<MultipartFile>> getMultipartFileMap() {
		return multipartFileMap;
	}
	public void setMultipartFileMap(Map<String, List<MultipartFile>> multipartFileMap) {
		this.multipartFileMap = multipartFileMap;
	}
	public SseEmitter getSseEmitter() {
		return sseEmitter;
	}
	public void setSseEmitter(SseEmitter sseEmitter) {
		this.sseEmitter = sseEmitter;
	}

	
}
