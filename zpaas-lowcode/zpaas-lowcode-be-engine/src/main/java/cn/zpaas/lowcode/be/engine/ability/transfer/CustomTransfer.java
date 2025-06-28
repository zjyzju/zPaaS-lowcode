package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 自定义方法类转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:29:00
 */
public class CustomTransfer extends Transfer {
    private static final String IMPL_CLASS_KEY = "implClass";// 自定义转换实现类

    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject) throws EngineException {
        if (fromAttrValue == null) {
            return null;
        }
        // 获取转换字典
        String implClass = JsonUtils.getString(transferCfg, IMPL_CLASS_KEY);

        // 未配置自定义实现类时，返回原值
        if (StringUtils.isBlank(implClass)) {
            return fromAttrValue;
        }

        Transfer transfer = null;
        try {
            transfer = (Transfer) Class.forName(implClass).getDeclaredConstructor().newInstance();
            return transfer.transfer(fromAttr, fromAttrValue, toAttr, transferCfg, fromObject);
        } catch (Exception e) {
            throw new EngineException(ResultStatus.INTERNAL_ERROR, "execute custom transfer failed!");
        }
    }

}
