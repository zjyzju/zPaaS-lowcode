package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 数组拼接类转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:29:00
 */
public class ArrayConcatTransfer extends Transfer {
    
    private static final String CONCAT_SYMBOL_KEY = "concatSymbol";// 分隔符
    private static final String CONCAT_SUB_ATTR_KEY = "concatSubAttr";// 合并的子属性

    /**
     * {
     * "concatSymbol":",",
     * "concatSubAttr":"code"//可空。数组中的元素是个对象的情况
     * }
     */
    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject)  throws EngineException{
        if (fromAttrValue == null) {
            return null;
        }
        // 获取分隔符
        String concatSymbol = JsonUtils.getString(transferCfg, CONCAT_SYMBOL_KEY);
        if (concatSymbol == null) {
            concatSymbol = StringUtils.emptyString();
        }

        // 获取子属性
        String concatSubAttr = JsonUtils.getString(transferCfg, CONCAT_SUB_ATTR_KEY);
        String result = null;
        if (fromAttrValue instanceof JsonArray) {// 数组类型
            JsonArray fromAttrValueArray = (JsonArray) fromAttrValue;
            int size = fromAttrValueArray.size();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                Object elementObject = JsonUtils.toObject(fromAttrValueArray.get(i));
                //获取要合并的值
                Object valueObject = null;
                if (!StringUtils.isBlank(concatSubAttr) && elementObject instanceof JsonObject) {// 取对象类型的属性
                    valueObject = JsonUtils.eval(elementObject, concatSubAttr);
                } else {
                    valueObject = elementObject;
                }

                //处理值为空的情况，并转换为字符类型
                String valueString = null;
                if (valueObject == null) {
                    valueString = StringUtils.emptyString();
                } else {
                    valueString = valueObject.toString();
                }

                //合并字符串
                if (i == (size - 1)) {//最后一个
                    stringBuilder.append(valueString);
                } else {
                    stringBuilder.append(valueString).append(concatSymbol);
                }
            }

            result = stringBuilder.toString();
        } else if (fromAttrValue instanceof JsonObject && !StringUtils.isBlank(concatSubAttr)) {// 取对象的属性值
            Object valueObject = JsonUtils.eval(fromAttrValue, concatSubAttr);
            if (valueObject != null) {
                result = valueObject.toString();
            }
        } else {//其他单值的情况
            result = fromAttrValue.toString();
        }
        
        return result;
    }

}
