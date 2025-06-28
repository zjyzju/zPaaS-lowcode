package cn.zpaas.lowcode.fe.vo;

import java.util.List;



/**
 * 功能定义值传递对象
 *
 * @author zjy
 * createTime 2025年04月31日-11:21:17
 */
public class FuncDefineVo {

    private String platformUrl;
    
    private String systemUrl;

    private String platformStreamUrl;

    private String platformFileUploadUrl;
    
    private String platformFileDownloadUrl;

    private String id;

    private String name;

    private String tenantId;

    private String systemId;

    private List<FuncRegionVo> funcRegions;
    
    private List<FuncObjectAssignVo> objectAssigns;

    private FuncTemplateVo funcTemplate;

    private List<FuncCustomizedPageVo> customizedPages;
    
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

    public List<FuncRegionVo> getFuncRegions() {
        return funcRegions;
    }

    public void setFuncRegions(List<FuncRegionVo> funcRegions) {
        this.funcRegions = funcRegions;
    }

    public List<FuncObjectAssignVo> getObjectAssigns() {
        return objectAssigns;
    }

    public void setObjectAssigns(List<FuncObjectAssignVo> objectAssigns) {
        this.objectAssigns = objectAssigns;
    }

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    public String getSystemUrl() {
        return systemUrl;
    }

    public void setSystemUrl(String systemUrl) {
        this.systemUrl = systemUrl;
    }

    public String getPlatformStreamUrl() {
        return platformStreamUrl;
    }

    public void setPlatformStreamUrl(String platformStreamUrl) {
        this.platformStreamUrl = platformStreamUrl;
    }

    public String getPlatformFileUploadUrl() {
        return platformFileUploadUrl;
    }

    public void setPlatformFileUploadUrl(String platformFileUploadUrl) {
        this.platformFileUploadUrl = platformFileUploadUrl;
    }

    public String getPlatformFileDownloadUrl() {
        return platformFileDownloadUrl;
    }

    public void setPlatformFileDownloadUrl(String platformFileDownloadUrl) {
        this.platformFileDownloadUrl = platformFileDownloadUrl;
    }

    public FuncTemplateVo getFuncTemplate() {
        return funcTemplate;
    }

    public void setFuncTemplate(FuncTemplateVo funcTemplate) {
        this.funcTemplate = funcTemplate;
    }

    public List<FuncCustomizedPageVo> getCustomizedPages() {
        return customizedPages;
    }

    public void setCustomizedPages(List<FuncCustomizedPageVo> customizedPages) {
        this.customizedPages = customizedPages;
    }

    
}
