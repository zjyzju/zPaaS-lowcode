package cn.zpaas.lowcode.be.ide.vo;

/**
 * AI流程编排需求信息
 *
 * @author zjy
 * createTime 2026年3月11日-20:53:02
 */
public class AiFlowComposerChatInfo {
    private String businessRequirement;
    private String businessFlowId;
    private String systemId;
    private String tenantId;
    
    public String getBusinessRequirement() {
        return businessRequirement;
    }
    public void setBusinessRequirement(String businessRequirement) {
        this.businessRequirement = businessRequirement;
    }
    public String getBusinessFlowId() {
        return businessFlowId;
    }
    public void setBusinessFlowId(String businessFlowId) {
        this.businessFlowId = businessFlowId;
    }
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

    
}
