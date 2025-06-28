package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonObject;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 按源值进行转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:22:11
 */
public class OriginalValueTransfer extends Transfer {

    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject) throws EngineException {
        return fromAttrValue;
    }
    
}
