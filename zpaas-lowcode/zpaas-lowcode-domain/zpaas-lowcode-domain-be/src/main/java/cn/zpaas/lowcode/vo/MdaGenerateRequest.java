package cn.zpaas.lowcode.vo;

import com.google.gson.JsonArray;

/**
 * 模型驱动辅助生成请求vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class MdaGenerateRequest {
	private String systemId;
	private String businessDomainId;
	private String dbSchemaId;
	
	private JsonArray ignorePrefixes;
	private JsonArray tableArray;
	
	private boolean generateValueObject;
	private boolean generateDataMapping;
	
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getBusinessDomainId() {
		return businessDomainId;
	}
	public void setBusinessDomainId(String businessDomainId) {
		this.businessDomainId = businessDomainId;
	}
	public String getDbSchemaId() {
		return dbSchemaId;
	}
	public void setDbSchemaId(String dbSchemaId) {
		this.dbSchemaId = dbSchemaId;
	}
	public JsonArray getIgnorePrefixes() {
		return ignorePrefixes;
	}
	public void setIgnorePrefixes(JsonArray ignorePrefixes) {
		this.ignorePrefixes = ignorePrefixes;
	}
	public JsonArray getTableArray() {
		return tableArray;
	}
	public void setTableArray(JsonArray tableArray) {
		this.tableArray = tableArray;
	}
	public boolean isGenerateValueObject() {
		return generateValueObject;
	}
	public void setGenerateValueObject(boolean generateValueObject) {
		this.generateValueObject = generateValueObject;
	}
	public boolean isGenerateDataMapping() {
		return generateDataMapping;
	}
	public void setGenerateDataMapping(boolean generateDataMapping) {
		this.generateDataMapping = generateDataMapping;
	}
	
	
}
