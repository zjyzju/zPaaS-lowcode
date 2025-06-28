package cn.zpaas.lowcode.be.engine.ability.transfer;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * Sql转换类转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:29:00
 */
public class SqlConvertTransfer extends Transfer {

    private static final String CONVERT_SQL_KEY = "convertSql";// 转换Sql
    private static final String DATASOURCE_KEY = "datasource";// 转换Sql使用的数据源

    
    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject) throws EngineException {
        if (fromAttrValue == null) {
            return null;
        }
        // 获取转换Sql
        String convertSql = JsonUtils.getString(transferCfg, CONVERT_SQL_KEY);

        // 配置的转换Sql为空时，返回原值
        if (StringUtils.isBlank(convertSql)) {
            return fromAttrValue;
        }
        String datasource = JsonUtils.getString(transferCfg, DATASOURCE_KEY);

        List<Object> valueList = new ArrayList<>();
        // 加入当前属性值
        valueList.add(fromAttrValue);
        
        JsonArray resultArray = ORMRepositoryAbility.getInstance().executeQuerySql(convertSql, valueList, fromAttr.getSystemId(), datasource, fromAttr.getTenantId());
        //如果结果不为空，则返回第一条记录的第一列的值
        if (!JsonUtils.isEmpty(resultArray)) {
            JsonObject rowObject = resultArray.get(0).getAsJsonObject();
            return JsonUtils.getObject(rowObject, rowObject.keySet().iterator().next());
        }
        //否则返回原值
        return fromAttrValue;
    }

}
