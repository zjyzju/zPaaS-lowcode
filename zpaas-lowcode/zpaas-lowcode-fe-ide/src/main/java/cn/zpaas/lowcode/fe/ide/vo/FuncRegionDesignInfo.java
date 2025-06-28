package cn.zpaas.lowcode.fe.ide.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;

/**
 * 功能区域设计信息
 *
 * @author zjy
 * createTime 2025年04月06日-09:18:18
 */
public class FuncRegionDesignInfo {
    private String funcId;
    private String funcRegionId;
    private List<FuncRegionAttrAssign> attrAssigns;
    private List<FuncRegionOperation> regionOperations;
    
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
    public List<FuncRegionAttrAssign> getAttrAssigns() {
        return attrAssigns;
    }
    public void setAttrAssigns(List<FuncRegionAttrAssign> attrAssigns) {
        this.attrAssigns = attrAssigns;
    }
    public List<FuncRegionOperation> getRegionOperations() {
        return regionOperations;
    }
    public void setRegionOperations(List<FuncRegionOperation> regionOperations) {
        this.regionOperations = regionOperations;
    }

    
}
