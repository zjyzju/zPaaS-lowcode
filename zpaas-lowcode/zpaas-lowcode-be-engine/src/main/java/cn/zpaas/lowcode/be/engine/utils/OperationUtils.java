package cn.zpaas.lowcode.be.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;

/**
 * 方法工具类
 *
 * @author zjy
 * createTime 2025年04月26日-18:15:55
 */
public class OperationUtils {
    private static Logger logger = LoggerFactory.getLogger(OperationUtils.class);

    /**
     * 方法输入参数格式校验
     * @param operation
     * @param reqData
     * @return
     */
    public static boolean validateInParam(Operation operation, JsonObject reqData) {
    	if(operation.getInParams() != null && operation.getInParams().size() > 0) {
			//当方法有输入参数时，传入的参数不能为空
			if(reqData == null) {
				return false;
			}
			//默认格式相符
			boolean paramValid = true;
			//循环每一个输入参数
			for(Parameter param : operation.getInParams()) {
				//如果输入参数中不包含当前参数，则认为格式不符
				if(!reqData.has(param.getCode())) {
					logger.error("reqData not contain param: {}", param.getCode());
					paramValid = false;
					break;
				}
				//如果当前参数是自定义类型，获取参数的定义信息
				AttributedObject type = null;
				if(ManagedObjectType.DOMAIN_OBJECT.equals(param.getParamType())) {
					type = DomainObjectProxy.getInstance().getDomainObject(operation.getSystemId(), param.getParamClass());				
				}else if(ManagedObjectType.VALUE_OBJECT.equals(param.getParamType())) {
					type = ValueObjectProxy.getInstance().getValueObject(operation.getSystemId(), param.getParamClass());		
				}else {
					continue;
				}
				
				//未获取到定义信息，作为非自定义类型，则当前参数认为符合（JAVA原生类型，不作校验）
				if(type == null) {
					continue;
				}
				
				//如果是列表类型
				if(YesOrNo.YES.equals(param.getIsListParam()) && reqData.get(param.getCode())!=null) {
					//如果类型不是JsonArray，则认为格式不符
					if(!(reqData.get(param.getCode()).isJsonArray())) {
						logger.error("reqData contain param: {} is not type of JsonArray", param.getCode());
						paramValid = false;
						break;
					}
					//循环判断列表中的每个元素
					JsonArray array = JsonUtils.getJsonArray(reqData, param.getCode());
					for(int i=0; i< array.size(); i++) {
						//如果不是JsonObject类型，则认为格式不符
						if(!(array.get(i).isJsonObject())) {
							logger.error("reqData contain param: {}'s element is not type of JsonObject", param.getCode());
							paramValid = false;
							break;
						}
						//校验元素的格式与定义的格式是否相符
						if(!validateAttributedObject(type, array.get(i).getAsJsonObject())) {
							logger.error("reqData param: {}'s element is not valid with {}", param.getCode(), type.getCode());
							paramValid = false;
							break;
						}
					}
					if(!paramValid) {
						break;
					}
				}else {//非列表类型
					//如果参数值为空，则不作校验
					if(reqData.get(param.getCode()) == null) {
						continue;
					}
					//如果参数值类型不是JsonObject类型，则认为不符
					if(!(reqData.get(param.getCode()).isJsonObject())) {
						logger.error("reqData contain param: {} is not type of JsonObject", param.getCode());
						paramValid = false;
						break;
					}
					//校验元素的格式与定义的格式是否相符
					if(!validateAttributedObject(type, reqData.get(param.getCode()).getAsJsonObject())) {
						logger.error("reqData param: {} is not valid with {}", param.getCode(), type.getCode());
						paramValid = false;
						break;
					}
				}
			}
			return paramValid;
		}else {
			return true;
		}
    }
    
    /**
     * 对方法的返回参数进行校验
     * @param operation
     * @param result
     * @return
     */
    public static boolean validateOutParam(Operation operation, Object result) {
    	if (operation.getOutParam() != null) {
			// 获取自定义对象
			AttributedObject attributedObject = null;
			if (ManagedObjectType.DOMAIN_OBJECT.equals(operation.getOutParam().getParamType())) {// 返回值是领域对象
				attributedObject = DomainObjectProxy.getInstance().getDomainObject(operation.getSystemId(),
						operation.getOutParam().getParamClass());
			} else if (ManagedObjectType.VALUE_OBJECT.equals(operation.getOutParam().getParamType())) {// 返回值是值传递对象
				attributedObject = ValueObjectProxy.getInstance()
						.getValueObject(operation.getSystemId(), operation.getOutParam().getParamClass());
			} else {// 返回值是源生JAVA类
				try {
					// 判断类型是否一致
					
					Class<?> outParamClass = Class.forName(operation.getOutParam().getParamClass());
					if (YesOrNo.NO.equals(operation.getOutParam().getIsListParam()) && outParamClass.isInstance(result)) {// 非列表类型且一致时直接返回
						return true;
					}else if (YesOrNo.YES.equals(operation.getOutParam().getIsListParam()) && (result instanceof JsonArray)) {//列表类型时
						JsonArray array = (JsonArray) result;
						boolean valid = true;
						for (int i = 0; i < array.size(); i++) {
							if(array.get(i).isJsonObject()) {//JsonArray套JsonArray是不合法的返回值
								valid = false;
								break;
							}
							Object itemObject = null;
							try {
								itemObject = JsonUtils.fromJson(array.get(i).getAsString(), outParamClass);
							} catch (Exception e) {
								//当出异常时，表示类型不一致
							}
							if (itemObject == null) {
								valid = false;
								break;
							}
						}
						if (!valid) {
							logger.error("result's element type must be invalid with {}.", operation.getOutParam().getParamClass());
						}
						return valid;
					} else {
						logger.error("result's type {} is not valid with operation's outParam type {}",
								result.getClass(), outParamClass);
						return false;
					}
				} catch (ClassNotFoundException e) {
					logger.error("operation's outParam type is invalid {}", operation.getOutParam().getParamClass());
					return false;
				}
			}

			if (YesOrNo.YES.equals(operation.getOutParam().getIsListParam())) {
				if (!(result instanceof JsonArray)) {
					logger.error("result's type must be JsonArray.");
					return false;
				}

				JsonArray array = (JsonArray) result;
				boolean valid = true;
				for (int i = 0; i < array.size(); i++) {
					if(array.get(i).isJsonArray()) {//JsonArray套JsonArray是不合法的返回值
						valid = false;
						break;
					}
					if (!validateAttributedObject(attributedObject, array.get(i).getAsJsonObject())) {
						valid = false;
						break;
					}
				}
				if (!valid) {
					logger.error("result's element type must be invalid with {}.", attributedObject.getCode());
				}
				return valid;

			} else {
				if (!(result instanceof JsonObject)) {
					logger.error("result's type must be JsonObject.");
					return false;
				}
				if (!validateAttributedObject(attributedObject, (JsonObject) result)) {
					logger.error("result's format is not valid with operation's outParam {}",
							attributedObject.getCode());
					return false;
				}
				return true;
			}
		}
    	return true;
    }

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
    	if(object != null && object.getAttributes() != null && object.getAttributes().size() > 0) {
    		for(Attribute attr: object.getAttributes()) {
				if(ManagedObjectType.DOMAIN_OBJECT.equals(attr.getAttrType()) || ManagedObjectType.VALUE_OBJECT.equals(attr.getAttrType())) {
					continue;//当属性类型是领域对象或值传递对象时，不再进行判断			
				}
    			if(!values.has(attr.getCode())) {
    				logger.error("not contain key: {}", attr.getCode());
    				return false;
    			}
    		}
    	}
    	//默认校验通过
    	return true;
    }
}
