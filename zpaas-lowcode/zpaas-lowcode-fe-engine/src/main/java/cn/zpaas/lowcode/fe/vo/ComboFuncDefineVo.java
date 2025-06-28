package cn.zpaas.lowcode.fe.vo;

import java.util.List;

/**
 * 组合功能定义值传递对象
 *
 * @author zjy
 * createTime 2025年04月04日-15:42:06
 */
public class ComboFuncDefineVo {
    private String id;

    private String name;

    private String comboTemplateId;

    private String tenantId;

    private String systemId;

    private List<ComboFuncTabVo> comboFuncTabs;

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

    public String getComboTemplateId() {
        return comboTemplateId;
    }

    public void setComboTemplateId(String comboTemplateId) {
        this.comboTemplateId = comboTemplateId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<ComboFuncTabVo> getComboFuncTabs() {
        return comboFuncTabs;
    }

    public void setComboFuncTabs(List<ComboFuncTabVo> comboFuncTabs) {
        this.comboFuncTabs = comboFuncTabs;
    }

    
}
