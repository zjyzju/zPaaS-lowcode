package cn.zpaas.lowcode.be.ide.vo;

import com.google.gson.JsonArray;

/**
 * 业务系统授权Vo
 *
 * @author zjy
 * createTime 2025年04月21日-17:42:12
 */
public class  BusinessSystemGrantVo {
    private String userId;
    private String userName;
    private String realName;
    private JsonArray grantedSystems;
    private String tenantId;

    
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public JsonArray getGrantedSystems() {
        return grantedSystems;
    }
    public void setGrantedSystems(JsonArray grantedSystems) {
        this.grantedSystems = grantedSystems;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    
}
