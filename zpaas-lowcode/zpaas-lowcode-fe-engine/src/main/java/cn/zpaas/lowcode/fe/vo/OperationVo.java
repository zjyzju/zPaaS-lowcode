package cn.zpaas.lowcode.fe.vo;

import java.util.List;


/**
 * 方法值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-17:03:52
 */
public class OperationVo {
    private String id;
    
    private String name;

    private String code;

    private String ruleGroupId;

    private ParameterVo outParam;
    
    private List<ParameterVo> inParams;
    
    private ValidateRuleGroupVo validateRuleGroup;

    private String systemId;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(String ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public ParameterVo getOutParam() {
        return outParam;
    }

    public void setOutParam(ParameterVo outParam) {
        this.outParam = outParam;
    }

    public List<ParameterVo> getInParams() {
        return inParams;
    }

    public void setInParams(List<ParameterVo> inParams) {
        this.inParams = inParams;
    }

    public ValidateRuleGroupVo getValidateRuleGroup() {
        return validateRuleGroup;
    }

    public void setValidateRuleGroup(ValidateRuleGroupVo validateRuleGroup) {
        this.validateRuleGroup = validateRuleGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}
