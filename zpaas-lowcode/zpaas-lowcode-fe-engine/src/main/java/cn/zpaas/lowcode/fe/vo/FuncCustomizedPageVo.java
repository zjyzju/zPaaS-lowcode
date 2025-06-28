package cn.zpaas.lowcode.fe.vo;

import java.util.List;

/**
 * FuncCustomizedPageRo
 *
 * @author zjy
 * createTime 2025年04月11日-16:12:47
 */
public class FuncCustomizedPageVo {
    private String id;

    private String name;

    private String funcId;

    private String pageCfg;

    private String isMainPage;

    private List<FuncCustomizedLayoutVo> customizedLayouts;

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

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    public String getPageCfg() {
        return pageCfg;
    }

    public void setPageCfg(String pageCfg) {
        this.pageCfg = pageCfg;
    }

    public String getIsMainPage() {
        return isMainPage;
    }

    public void setIsMainPage(String isMainPage) {
        this.isMainPage = isMainPage;
    }

    public List<FuncCustomizedLayoutVo> getCustomizedLayouts() {
        return customizedLayouts;
    }

    public void setCustomizedLayouts(List<FuncCustomizedLayoutVo> customizedLayouts) {
        this.customizedLayouts = customizedLayouts;
    }

    
}
