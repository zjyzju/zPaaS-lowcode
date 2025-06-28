package cn.zpaas.lowcode.fe.ide.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;

/**
 * 功能区域统计报表设计信息
 *
 * @author zjy
 * createTime 2025年04月06日-09:18:18
 */
public class FuncRegionReportDesignInfo {
    private String funcId;
    private String funcRegionId;
    private List<FuncRegionAttrAssign> reportAttrs;
    private List<FuncRegionAttrAssign> filterAttrs;
    private List<FuncRegionAttrAssign> tagAttrs;
    
    public String getFuncId() {
        return funcId;
    }
    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }
    public String getFuncRegionId() {
        return funcRegionId;
    }
    public void setFuncRegionId(String funcRegionId) {
        this.funcRegionId = funcRegionId;
    }
    public List<FuncRegionAttrAssign> getReportAttrs() {
        return reportAttrs;
    }
    public void setReportAttrs(List<FuncRegionAttrAssign> reportAttrs) {
        this.reportAttrs = reportAttrs;
    }
    public List<FuncRegionAttrAssign> getFilterAttrs() {
        return filterAttrs;
    }
    public void setFilterAttrs(List<FuncRegionAttrAssign> filterAttrs) {
        this.filterAttrs = filterAttrs;
    }
    public List<FuncRegionAttrAssign> getTagAttrs() {
        return tagAttrs;
    }
    public void setTagAttrs(List<FuncRegionAttrAssign> tagAttrs) {
        this.tagAttrs = tagAttrs;
    }
    
    
}
