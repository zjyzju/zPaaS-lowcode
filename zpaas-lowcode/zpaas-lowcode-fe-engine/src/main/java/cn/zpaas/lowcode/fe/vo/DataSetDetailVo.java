package cn.zpaas.lowcode.fe.vo;

import com.google.gson.JsonObject;

/**
 * 数据集明细值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:14:05
 */
public class DataSetDetailVo {
    private String id;

    private String detailType;

    private String detailContentId;

    private String detailContentAlias;

    private JsonObject content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDetailContentId() {
        return detailContentId;
    }

    public void setDetailContentId(String detailContentId) {
        this.detailContentId = detailContentId;
    }

    public String getDetailContentAlias() {
        return detailContentAlias;
    }

    public void setDetailContentAlias(String detailContentAlias) {
        this.detailContentAlias = detailContentAlias;
    }


    public JsonObject getContent() {
        return content;
    }

    public void setContent(JsonObject content) {
        this.content = content;
    }

    
}
