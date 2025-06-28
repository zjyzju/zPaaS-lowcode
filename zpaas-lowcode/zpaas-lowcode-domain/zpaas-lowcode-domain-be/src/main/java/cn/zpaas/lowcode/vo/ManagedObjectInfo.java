package cn.zpaas.lowcode.vo;

import java.util.List;
import java.util.Map;

import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.ManagedObject;
import cn.zpaas.lowcode.domain.eo.Operation;

/**
 * 管理对象信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class ManagedObjectInfo {
	private ManagedObject managedObject;
	
	private String objectType;
	
	private List<Attribute> attributes;
	
	private List<Operation> operations;
	
	private Map<String, ExposedService> exposedServiceMap;

	public ManagedObject getManagedObject() {
		return managedObject;
	}

	public void setManagedObject(ManagedObject managedObject) {
		this.managedObject = managedObject;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Map<String, ExposedService> getExposedServiceMap() {
		return exposedServiceMap;
	}

	public void setExposedServiceMap(Map<String, ExposedService> exposedServiceMap) {
		this.exposedServiceMap = exposedServiceMap;
	}
	
	
}
