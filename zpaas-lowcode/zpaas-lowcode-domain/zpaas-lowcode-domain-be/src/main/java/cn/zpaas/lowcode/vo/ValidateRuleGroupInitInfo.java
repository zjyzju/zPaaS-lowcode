package cn.zpaas.lowcode.vo;

import java.util.List;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.ValidateRuleType;

/**
 * 校验规则组初始化信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class ValidateRuleGroupInitInfo {
	private String tenantId;
	private String systemId;
	private String objectType;
	private String objectId;
	private AttributedObject targetObject;//对象类型为领域对象/值传递对象时，使用该属性
	private Operation operation;//对象类型为服务方法时
	private JsonObject inParams;//对象类型为服务方法时
	
	private List<ValidateRuleType> validateRuleTypes;
	private List<DbSchema> dbSchemas;

	public AttributedObject getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(AttributedObject targetObject) {
		this.targetObject = targetObject;
	}

	public List<ValidateRuleType> getValidateRuleTypes() {
		return validateRuleTypes;
	}

	public void setValidateRuleTypes(List<ValidateRuleType> validateRuleTypes) {
		this.validateRuleTypes = validateRuleTypes;
	}

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

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public List<DbSchema> getDbSchemas() {
		return dbSchemas;
	}

	public void setDbSchemas(List<DbSchema> dbSchemas) {
		this.dbSchemas = dbSchemas;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public JsonObject getInParams() {
		return inParams;
	}

	public void setInParams(JsonObject inParams) {
		this.inParams = inParams;
	}
	
	

}
