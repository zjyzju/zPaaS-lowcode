package cn.zpaas.lowcode.fe.vo;


/**
 * FuncCustomizedLayoutRegionRo
 *
 * @author zjy
 * createTime 2025年04月11日-16:17:21
 */
public class FuncCustomizedLayoutRegionVo {
    private String id;

    private String funcId;

    private String pageId;

    private String layoutId;

    private String regionId;

    private FuncRegionVo funcRegion;

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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public FuncRegionVo getFuncRegion() {
        return funcRegion;
    }

    public void setFuncRegion(FuncRegionVo funcRegion) {
        this.funcRegion = funcRegion;
    }

    
}
