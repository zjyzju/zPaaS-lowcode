package cn.zpaas.lowcode.vo;

import java.util.List;

/**
 * 通用LabelValue类
 *
 * @author zjy
 * createTime 2025年4月21日-11:46:52
 */
public class LabelValueVo {
    private String label;
    private String value;
    private String parentValue;
    private List<LabelValueVo> children;
    
    public List<LabelValueVo> getChildren() {
        return children;
    }
    public void setChildren(List<LabelValueVo> children) {
        this.children = children;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getParentValue() {
        return parentValue;
    }
    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }
    
}
