package cn.zpaas.lowcode.bi.vo;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.domain.eo.DataSetDetail;

/**
 * 报表数据加载请求对象
 *
 * @author zjy
 * createTime 2025年04月23日-09:51:49
 */
public class LoadReportDataReq {
    private JsonObject selectedFilterValues;

    private JsonObject selectedTags;

    private String funcId;

    private String funcRegionId; 
    
    private List<DataSetDetail> dataSetDetails;

    private JsonArray displayCfgs;

    

    public JsonObject getSelectedFilterValues() {
        return selectedFilterValues;
    }

    public void setSelectedFilterValues(JsonObject selectedFilterValues) {
        this.selectedFilterValues = selectedFilterValues;
    }

    public JsonObject getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(JsonObject selectedTags) {
        this.selectedTags = selectedTags;
    }

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

    public List<DataSetDetail> getDataSetDetails() {
        return dataSetDetails;
    }

    public void setDataSetDetails(List<DataSetDetail> dataSetDetails) {
        this.dataSetDetails = dataSetDetails;
    }

    public JsonArray getDisplayCfgs() {
        return displayCfgs;
    }

    public void setDisplayCfgs(JsonArray displayCfgs) {
        this.displayCfgs = displayCfgs;
    }

    
}
