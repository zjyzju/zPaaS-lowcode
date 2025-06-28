package cn.zpaas.lowcode.fe.vo;


/**
 * 组合功能标签页值传递对象
 *
 * @author zjy
 * createTime 2025年04月04日-15:46:42
 */
public class ComboFuncTabVo {
    private String id;

    private String name;

    private String comboFuncId;

    private String tabParams;

    private String tabFuncId;

    private Integer displayOrder;

   private FuncDefineVo funcDefine;

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

    public String getComboFuncId() {
        return comboFuncId;
    }

    public void setComboFuncId(String comboFuncId) {
        this.comboFuncId = comboFuncId;
    }

    public String getTabParams() {
        return tabParams;
    }

    public void setTabParams(String tabParams) {
        this.tabParams = tabParams;
    }

    public String getTabFuncId() {
        return tabFuncId;
    }

    public void setTabFuncId(String tabFuncId) {
        this.tabFuncId = tabFuncId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public FuncDefineVo getFuncDefine() {
        return funcDefine;
    }

    public void setFuncDefine(FuncDefineVo funcDefine) {
        this.funcDefine = funcDefine;
    }
}
