package cn.zpaas.lowcode.be.engine.container;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.domain.eo.AttributedObject;

/**
 * @author zjy
 * 保存属性对象数据的容器类
 */
public class AttributedObjectContainer {
	//保存容器对应的属性对象信息（DO或RO）
	private AttributedObject attributedObject;
	//保存属性对象数据的容器
	private JsonObject values = new JsonObject();
	
	//获取指定属性的值
	public Object getAttributeValue(String attributeCode) {
		return JsonUtils.getObject(this.values, attributeCode);
	}
	
	//修改指定属性的值
	public void setAttributeValue(String attributeCode, Object value) {
		this.values.add(attributeCode, JsonUtils.toJsonElement(value));
	}
	
	//设置属性对象的所有值
	public void setObjectValues(JsonObject objectValues) {
		this.values = objectValues;
	}
	
	//clone所有属性的值并返回
	public JsonObject objectValues() {
		return values;
	}

	//返回对应的属性对象
	public AttributedObject getAttributedObject() {
		return attributedObject;
	}
	
	//设置属性对象
	public void setAttributedObject(AttributedObject attributedObject) {
		this.attributedObject = attributedObject;
	}

	
}
