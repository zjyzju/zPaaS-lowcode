package cn.zpaas.lowcode.fe.vo;

import java.util.List;


/**
 * description
 *
 * @author zjy
 * createTime 2025年04月11日-16:14:35
 */
public class FuncCustomizedLayoutVo {
    private String id;

    private String name;

    private String componentCode;

    private String componentType;

    private String funcId;

    private String pageId;

    private String parentLayoutId;

    private Integer displayOrder;

    private String layoutCfg;

    private List<FuncCustomizedLayoutRegionVo> layoutRegions;

    private List<FuncCustomizedLayoutVo> subCustomizedLayouts;

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

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getParentLayoutId() {
        return parentLayoutId;
    }

    public void setParentLayoutId(String parentLayoutId) {
        this.parentLayoutId = parentLayoutId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getLayoutCfg() {
        return layoutCfg;
    }

    public void setLayoutCfg(String layoutCfg) {
        this.layoutCfg = layoutCfg;
    }

    public List<FuncCustomizedLayoutRegionVo> getLayoutRegions() {
        return layoutRegions;
    }

    public void setLayoutRegions(List<FuncCustomizedLayoutRegionVo> layoutRegions) {
        this.layoutRegions = layoutRegions;
    }

    public List<FuncCustomizedLayoutVo> getSubCustomizedLayouts() {
        return subCustomizedLayouts;
    }

    public void setSubCustomizedLayouts(List<FuncCustomizedLayoutVo> subCustomizedLayouts) {
        this.subCustomizedLayouts = subCustomizedLayouts;
    }

    
}
