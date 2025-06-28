package cn.zpaas.lowcode.fe.vo;

import java.util.List;
import java.util.Map;


/**
 * 值传递对象-功能绑定对象
 *
 * @author zjy
 * createTime 2025年04月31日-11:37:14
 */
public class FuncObjectAssignVo {
    private String id;

    private String funcId;

    private String objectType;

    private String objectId;

    private String assignType;
    
    private String keyAttrId;

    private String relAttrId;
    
    private String mainRelAttrId;
    
    private Integer displayOrder;

    private List<AttributeVo> attributes;
    
    private AttributeVo relAttribute;
    
    private AttributeVo mainRelAttribute;
    
    private AttributeVo keyAttribute;
    
    private Map<String, FuncObjectAttrOptionsVo> attrOptionMap;
    
    private String assignObjectName;

    private List<DataSetDetailVo> dataSetDetails;

    public List<DataSetDetailVo> getDataSetDetails() {
        return dataSetDetails;
    }

    public void setDataSetDetails(List<DataSetDetailVo> dataSetDetails) {
        this.dataSetDetails = dataSetDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
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

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public String getKeyAttrId() {
        return keyAttrId;
    }

    public void setKeyAttrId(String keyAttrId) {
        this.keyAttrId = keyAttrId;
    }

    public String getRelAttrId() {
        return relAttrId;
    }

    public void setRelAttrId(String relAttrId) {
        this.relAttrId = relAttrId;
    }

    public String getMainRelAttrId() {
        return mainRelAttrId;
    }

    public void setMainRelAttrId(String mainRelAttrId) {
        this.mainRelAttrId = mainRelAttrId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<AttributeVo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeVo> attributes) {
        this.attributes = attributes;
    }

    public AttributeVo getRelAttribute() {
        return relAttribute;
    }

    public void setRelAttribute(AttributeVo relAttribute) {
        this.relAttribute = relAttribute;
    }

    public AttributeVo getMainRelAttribute() {
        return mainRelAttribute;
    }

    public void setMainRelAttribute(AttributeVo mainRelAttribute) {
        this.mainRelAttribute = mainRelAttribute;
    }

    public AttributeVo getKeyAttribute() {
        return keyAttribute;
    }

    public void setKeyAttribute(AttributeVo keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

    public Map<String, FuncObjectAttrOptionsVo> getAttrOptionMap() {
        return attrOptionMap;
    }

    public void setAttrOptionMap(Map<String, FuncObjectAttrOptionsVo> attrOptionMap) {
        this.attrOptionMap = attrOptionMap;
    }

    public String getAssignObjectName() {
        return assignObjectName;
    }

    public void setAssignObjectName(String assignObjectName) {
        this.assignObjectName = assignObjectName;
    }

    
    
}
