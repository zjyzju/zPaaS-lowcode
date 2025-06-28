package cn.zpaas.lowcode.bi.ide.vo;

import java.util.List;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.vo.MdaColumn;

/**
 * 数据模型数据&结构vo类
 *
 * @author zjy
 * createTime 2025年04月20日-10:01:59
 */
public class DataModelSchemaDataVo {
    private List<MdaColumn> dataModelSchemas;

    private JsonArray datas;

    public List<MdaColumn> getDataModelSchemas() {
        return dataModelSchemas;
    }

    public void setDataModelSchemas(List<MdaColumn> dataModelSchemas) {
        this.dataModelSchemas = dataModelSchemas;
    }

    public JsonArray getDatas() {
        return datas;
    }

    public void setDatas(JsonArray datas) {
        this.datas = datas;
    }

    
}
