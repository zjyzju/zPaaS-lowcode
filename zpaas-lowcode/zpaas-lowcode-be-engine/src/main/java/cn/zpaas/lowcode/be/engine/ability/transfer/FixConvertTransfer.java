package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 固定字典转换类转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:29:00
 */
public class FixConvertTransfer extends Transfer {
    
    private static final String CONVERT_DICT_KEY = "convertDict";// 转换字典
    
    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject) throws EngineException {
        if (fromAttrValue == null) {
            return null;
        }
        // 获取转换字典
        JsonObject convertDict = JsonUtils.getJsonObject(transferCfg, CONVERT_DICT_KEY);
        String fromValueString = fromAttrValue.toString();
        
        //未配置字典或字典中不包含当前值时，返回原值
        if (convertDict == null || !convertDict.has(fromValueString)) {
            return fromAttrValue;
        }

        return JsonUtils.getObject(convertDict, fromValueString);
    }

}
