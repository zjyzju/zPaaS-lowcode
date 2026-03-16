package cn.zpaas.lowcode.be.ide.vo;

import com.google.gson.JsonArray;

/**
 * AI生成的业务流节点列表信息VO
 *
 * @author zjy
 * createTime 2026年3月09日-20:02:24
 */
public class AiGenBusinessFlowInfo {
    private JsonArray businessFlowNodes;
    private String businessFlowId;
    private String tenantId;
    private String systemId;
    
    public JsonArray getBusinessFlowNodes() {
        return businessFlowNodes;
    }
    public void setBusinessFlowNodes(JsonArray businessFlowNodes) {
        this.businessFlowNodes = businessFlowNodes;
    }
    public String getBusinessFlowId() {
        return businessFlowId;
    }
    public void setBusinessFlowId(String businessFlowId) {
        this.businessFlowId = businessFlowId;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getSystemId() {
        return systemId;
    }
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    
}
