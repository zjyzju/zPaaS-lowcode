package cn.zpaas.lowcode.fe.vo;

import com.google.gson.JsonArray;

/**
 * 功能绑定对象属性选项信息值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-11:39:37
 */
public class FuncObjectAttrOptionsVo {
    private String id;

    private String objectAssignId;

    private String attributeId;
    
    private String interactionType;

    private String optionCfgType;

    private String optionCfg;

    private String parentObjectAssignId;
    
    private String parentAttributeId;
    
    private String displayHideCfg;
    
    private String popupCfg;
    
    private String queryType;

    private JsonArray options;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectAssignId() {
        return objectAssignId;
    }

    public void setObjectAssignId(String objectAssignId) {
        this.objectAssignId = objectAssignId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getOptionCfgType() {
        return optionCfgType;
    }

    public void setOptionCfgType(String optionCfgType) {
        this.optionCfgType = optionCfgType;
    }

    public String getParentObjectAssignId() {
        return parentObjectAssignId;
    }

    public void setParentObjectAssignId(String parentObjectAssignId) {
        this.parentObjectAssignId = parentObjectAssignId;
    }

    public String getParentAttributeId() {
        return parentAttributeId;
    }

    public void setParentAttributeId(String parentAttributeId) {
        this.parentAttributeId = parentAttributeId;
    }

    public String getDisplayHideCfg() {
        return displayHideCfg;
    }

    public void setDisplayHideCfg(String displayHideCfg) {
        this.displayHideCfg = displayHideCfg;
    }

    public String getPopupCfg() {
        return popupCfg;
    }

    public void setPopupCfg(String popupCfg) {
        this.popupCfg = popupCfg;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public JsonArray getOptions() {
        return options;
    }

    public void setOptions(JsonArray options) {
        this.options = options;
    }

    public String getOptionCfg() {
        return optionCfg;
    }

    public void setOptionCfg(String optionCfg) {
        this.optionCfg = optionCfg;
    }

    
}
