package cn.zpaas.lowcode.fe.vo;


/**
 * 校验规则值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:55:43
 */
public class ValidateRuleVo {
    private String id;

    private String attributeId;
    
    private String subAttributeId;

    private String ruleTypeId;

    private String ruleValue1;

    private String ruleValue2;

    private String ruleValue3;

    private String attributeCode;
    
    private String subAttributeCode;
    
    private String ruleTypeClass;
    private String ruleTypeName;

    public String getRuleTypeName() {
        return ruleTypeName;
    }

    public void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getSubAttributeId() {
        return subAttributeId;
    }

    public void setSubAttributeId(String subAttributeId) {
        this.subAttributeId = subAttributeId;
    }

    public String getRuleTypeId() {
        return ruleTypeId;
    }

    public void setRuleTypeId(String ruleTypeId) {
        this.ruleTypeId = ruleTypeId;
    }

    public String getRuleValue1() {
        return ruleValue1;
    }

    public void setRuleValue1(String ruleValue1) {
        this.ruleValue1 = ruleValue1;
    }

    public String getRuleValue2() {
        return ruleValue2;
    }

    public void setRuleValue2(String ruleValue2) {
        this.ruleValue2 = ruleValue2;
    }

    public String getRuleValue3() {
        return ruleValue3;
    }

    public void setRuleValue3(String ruleValue3) {
        this.ruleValue3 = ruleValue3;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getSubAttributeCode() {
        return subAttributeCode;
    }

    public void setSubAttributeCode(String subAttributeCode) {
        this.subAttributeCode = subAttributeCode;
    }

    public String getRuleTypeClass() {
        return ruleTypeClass;
    }

    public void setRuleTypeClass(String ruleTypeClass) {
        this.ruleTypeClass = ruleTypeClass;
    }

    
}
