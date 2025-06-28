package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonObject;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.DataTransferMethod;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 值转换方法基类
 *
 * @author zjy
 * createTime 2025年04月07日-14:09:02
 */
public abstract class Transfer {
    private DataTransferMethod dataTransferMethod; //转换器对应的配置信息类

    public DataTransferMethod getDataTransferMethod() {
        return dataTransferMethod;
    }

    public void setDataTransferMethod(DataTransferMethod dataTransferMethod) {
        this.dataTransferMethod = dataTransferMethod;
    }

    /**
     * 转换方法
     * @param fromAttr 源属性
     * @param fromAttrValue 源属性值
     * @param toAttr 目标属性
     * @param transferCfg 转换配置
     * @param fromObject 源对象
     * @return 返回转换后的值
     */
    public abstract Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg, JsonObject fromObject) throws EngineException;
}
