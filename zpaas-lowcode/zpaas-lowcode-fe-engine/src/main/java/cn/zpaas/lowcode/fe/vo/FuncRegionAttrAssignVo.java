package cn.zpaas.lowcode.fe.vo;



/**
 * 功能区域绑定属性值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-14:23:24
 */
public class FuncRegionAttrAssignVo {
    private String id;

    private String funcRegionId;

    private String objectAssignId;

    private String objectType;

    private String objectId;

    private String attributeId;

    private String displayType;

    private String displayName;
    
    private String displayWidth;
    
    private Integer rowSpan;
    
    private Integer colSpan;
    
    private String readonly;
    
    private String required;
    
    private Integer displayOrder;
    
    private String displayCfg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getFuncRegionId() {
        return funcRegionId;
    }

    public void setFuncRegionId(String funcRegionId) {
        this.funcRegionId = funcRegionId;
    }

    public String getObjectAssignId() {
        return objectAssignId;
    }

    public void setObjectAssignId(String objectAssignId) {
        this.objectAssignId = objectAssignId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(String displayWidth) {
        this.displayWidth = displayWidth;
    }

    public Integer getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(Integer rowSpan) {
        this.rowSpan = rowSpan;
    }

    public Integer getColSpan() {
        return colSpan;
    }

    public void setColSpan(Integer colSpan) {
        this.colSpan = colSpan;
    }

    public String getReadonly() {
        return readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDisplayCfg() {
        return displayCfg;
    }

    public void setDisplayCfg(String displayCfg) {
        this.displayCfg = displayCfg;
    }
}
