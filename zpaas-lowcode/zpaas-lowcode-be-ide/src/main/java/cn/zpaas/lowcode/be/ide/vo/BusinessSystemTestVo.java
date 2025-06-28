package cn.zpaas.lowcode.be.ide.vo;

/**
 * 业务系统相关的测试信息
 *
 * @author zjy
 * createTime 2025年04月28日-11:33:52
 */
public class BusinessSystemTestVo {
    private String platformUrl;

    private String platformStreamUrl;
    
    private String systemUrl;
    
    private String platformFileUploadUrl;
    
    private String platformFileDownloadUrl;

    private String platformBatchUrl;

    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatformBatchUrl() {
        return platformBatchUrl;
    }

    public void setPlatformBatchUrl(String platformBatchUrl) {
        this.platformBatchUrl = platformBatchUrl;
    }

    public String getPlatformStreamUrl() {
        return platformStreamUrl;
    }

    public void setPlatformStreamUrl(String platformStreamUrl) {
        this.platformStreamUrl = platformStreamUrl;
    }

        
}
