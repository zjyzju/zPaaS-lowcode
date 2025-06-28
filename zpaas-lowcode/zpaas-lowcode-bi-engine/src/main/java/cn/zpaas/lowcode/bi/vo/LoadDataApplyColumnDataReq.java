package cn.zpaas.lowcode.bi.vo;

import com.google.gson.JsonArray;

/**
 * 数据提供列配置数据的加载
 *
 * @author zjy
 * createTime 2025年04月27日-10:08:49
 */
public class LoadDataApplyColumnDataReq {
    private String systemId; //业务系统标识

    private String tenantId; //租户标识

    private LoadReportDataReq loadReportDataReq; //数据集数据加载请求信息

    private JsonArray dataApplyColumns; //数据提供列相关配置信息

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public JsonArray getDataApplyColumns() {
        return dataApplyColumns;
    }

    public void setDataApplyColumns(JsonArray dataApplyColumns) {
        this.dataApplyColumns = dataApplyColumns;
    }

    public LoadReportDataReq getLoadReportDataReq() {
        return loadReportDataReq;
    }

    public void setLoadReportDataReq(LoadReportDataReq loadReportDataReq) {
        this.loadReportDataReq = loadReportDataReq;
    }

    
}
