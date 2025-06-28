package cn.zpaas.lowcode.be.engine.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;

/**
 * @author zjy 业务流上下文对象类，用于存放业务流执行过程中的输入和输出数据对象
 */
public class BusinessFlowContext {

	private Map<String, Object> values = new HashMap<>();// 保存业务流过程数据的对象
	private Map<String, List<MultipartFile>> multipartFileMap = null;//保存前端上传的文件
	private SseEmitter sseEmitter = null;//服务事件发送对象
	private String systemId;// 业务系统标识
	private String tenantId;//租户标识
	private String dbSchemaId; // 业务流对应的数据库标识
	private JsonObject reqData;// 输入参数
	private Object resulteData;// 保存返回结果数据的对象
	// 保存领域对象数据的容器对象Map，过程中领域对象
	private Map<String, AttributedObjectContainer> attributedObjectMap = new HashMap<>();
	// 保存领域对象列表数据的容器对象Map，过程中领域对象
	private Map<String, List<AttributedObjectContainer>> attributedObjectsMap = new HashMap<>();
	//当前领域对象，调用领域对象方法时有效
	private AttributedObjectContainer domainObject = null;
	//否中断业务流执行，比如参数校验节点校验不通过时，设置interrupted为true来中断业务流执行，message用来存放提示信息
	private boolean interrupted = false;
	//提示信息，还可以用来存放服务成功返回时的额外提示信息，比如参数校验节点产生了校验未通过的信息，但该节点配置不中断业务流执行时
	private String message;
	
	private static ThreadLocal<Boolean> transactionStarted = new ThreadLocal<>();
	
	// 获取业务流过程数据的方法
	public Object getAttribute(String key) {
		return values.get(key);
	}

	// 移除业务流过程数据的方法
	public void removeAttribute(String key) {
		values.remove(key);
	}

	// 保存业务流过程数据
	public void setAttribute(String key, Object object) {
		this.values.put(key, object);
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

	public Object getResulteData() {
		return resulteData;
	}

	public void setResulteData(Object resulteData) {
		this.resulteData = resulteData;
	}

	public AttributedObjectContainer getAttributedObject(String key) {
		return attributedObjectMap.get(key);
	}

	public void removeAttributedObject(String key) {
		attributedObjectMap.remove(key);
	}

	public void addAttributedObject(String key, AttributedObjectContainer attributedObject) {
		this.attributedObjectMap.put(key, attributedObject);
	}

	public static boolean isTransactionStarted() {
		if(transactionStarted.get() == null) {
			return false;
		}
		return transactionStarted.get();
	}

	public static void setTransactionStarted(Boolean isTransactionStarted) {
		transactionStarted.set(isTransactionStarted);
	}

	public AttributedObjectContainer getDomainObject() {
		return domainObject;
	}

	public void setDomainObject(AttributedObjectContainer domainObject) {
		this.domainObject = domainObject;
	}

	public String getDbSchemaId() {
		return dbSchemaId;
	}

	public void setDbSchemaId(String dbSchemaId) {
		this.dbSchemaId = dbSchemaId;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<AttributedObjectContainer> getAttributedObjects(String key) {
		return attributedObjectsMap.get(key);
	}

	public void removeAttributedObjects(String key) {
		attributedObjectsMap.remove(key);
	}

	public void addAttributedObjects(String key, List<AttributedObjectContainer> attributedObject) {
		this.attributedObjectsMap.put(key, attributedObject);
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
