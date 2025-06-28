package cn.zpaas.lowcode.domain.eo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;


public class AttributedObject extends ManagedObject {
	public static Logger logger = LoggerFactory.getLogger(AttributedObject.class);
	
    
    private List<Attribute> attributes;
    
    /**
     * 校验传入的values对象的格式是否符合属性化对象的要求
     * @param object 属性化对象
     * @param values 值对象
     * @return true/false
     */
    public static boolean validateAttributedObject(AttributedObject object, JsonObject values) {
    	//如果values为空，则默认校验不通过
    	if(values == null) {
    		return false;
    	}
    	//循环所有属性，只要有一个属性不包含，就认为校验不通过
    	if(object != null && !CollectionUtils.isEmpty(object.attributes)) {
    		for(Attribute attr: object.getAttributes()) {
    			if(!values.has(attr.getCode())) {
    				logger.error("not contain key: {}", attr.getCode());
    				return false;
    			}
    		}
    	}
    	//默认校验通过
    	return true;
    }
    
    /**
     * 根据id获取Attribute对象
     * @param attributeId
     * @return
     */
    public Attribute getAttributeById(String attributeId) {
    	if(StringUtils.isBlank(attributeId)) {
    		return null;
    	}
    	if(attributes != null) {
    		for(Attribute attribute: attributes) {
    			if(attribute.getId().equals(attributeId)) {
    				return attribute;
    			}
    		}
    	}
    	return null;
    	
    }
    
    public JsonObject newInstance() {
    	JsonObject newObject = new JsonObject();
    	if(!CollectionUtils.isEmpty(this.attributes)) {
    		this.attributes.forEach(attrRow ->  newObject.add(attrRow.getCode(), null));
    	}
    	return newObject;
    }
    

    public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}