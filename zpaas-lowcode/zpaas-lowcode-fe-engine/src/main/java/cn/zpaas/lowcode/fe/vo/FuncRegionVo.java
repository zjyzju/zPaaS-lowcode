package cn.zpaas.lowcode.fe.vo;

import java.util.List;


/**
 * 功能区域值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-14:19:54
 */
public class FuncRegionVo {
    private String id;

    private String name;

    private String funcId;

    private String tplRegionId;

    private String regionCfg;

    private List<FuncRegionOperationVo> regionOperations;
    
    private List<FuncRegionAttrAssignVo> regionAttrAssigns;
    
    private FuncTemplateRegionVo tplRegion;

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

    public String getTplRegionId() {
        return tplRegionId;
    }

    public void setTplRegionId(String tplRegionId) {
        this.tplRegionId = tplRegionId;
    }

    public List<FuncRegionOperationVo> getRegionOperations() {
        return regionOperations;
    }

    public void setRegionOperations(List<FuncRegionOperationVo> regionOperations) {
        this.regionOperations = regionOperations;
    }

    public List<FuncRegionAttrAssignVo> getRegionAttrAssigns() {
        return regionAttrAssigns;
    }

    public void setRegionAttrAssigns(List<FuncRegionAttrAssignVo> regionAttrAssigns) {
        this.regionAttrAssigns = regionAttrAssigns;
    }

    public FuncTemplateRegionVo getTplRegion() {
        return tplRegion;
    }

    public void setTplRegion(FuncTemplateRegionVo tplRegion) {
        this.tplRegion = tplRegion;
    }

    public String getRegionCfg() {
        return regionCfg;
    }

    public void setRegionCfg(String regionCfg) {
        this.regionCfg = regionCfg;
    }

}
