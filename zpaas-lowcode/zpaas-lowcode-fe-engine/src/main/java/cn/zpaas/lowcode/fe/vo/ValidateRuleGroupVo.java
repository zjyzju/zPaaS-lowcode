package cn.zpaas.lowcode.fe.vo;

import java.util.List;
import java.util.Map;

/**
 * 校验规则组值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:54:14
 */
public class ValidateRuleGroupVo {
    private String id;

    private String name;

    private String objectType;

    private String objectId;

    private String subRuleGroups;

    private List<ValidateRuleVo> validateRules;

    private Map<String, ValidateRuleGroupVo> subRuleGroupMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSubRuleGroups() {
        return subRuleGroups;
    }

    public void setSubRuleGroups(String subRuleGroups) {
        this.subRuleGroups = subRuleGroups;
    }

    public List<ValidateRuleVo> getValidateRules() {
        return validateRules;
    }

    public void setValidateRules(List<ValidateRuleVo> validateRules) {
        this.validateRules = validateRules;
    }

    public Map<String, ValidateRuleGroupVo> getSubRuleGroupMap() {
        return subRuleGroupMap;
    }

    public void setSubRuleGroupMap(Map<String, ValidateRuleGroupVo> subRuleGroupMap) {
        this.subRuleGroupMap = subRuleGroupMap;
    }

    
}
