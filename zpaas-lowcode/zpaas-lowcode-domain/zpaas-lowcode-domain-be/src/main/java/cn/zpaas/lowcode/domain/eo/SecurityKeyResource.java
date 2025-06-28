package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.SecurityKeyResourceMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class SecurityKeyResource {
    @Autowired
    private SecurityKeyResourceMapper mapper;

    private String id;

    private String name;

    private String type;

    private String securityKey;

    private String cipherAlgorithm;

    private String privateKeyPwd;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private byte[] privateKeyFile;

    private String privateKeyFileName;

    private byte[] publicKeyFile;

    private String publicKeyFileName;

    

    public SecurityKeyResource byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int add(SecurityKeyResource securityKeyResource) {
        if(StringUtils.isBlank(securityKeyResource.getId())) {
            securityKeyResource.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        securityKeyResource.setCreateTime(nowDate);
        securityKeyResource.setUpdateTime(nowDate);
        return mapper.insert(securityKeyResource);
    }

    public int save(SecurityKeyResource securityKeyResource) {
        Date nowDate = new Date();
        securityKeyResource.setUpdateTime(nowDate);
        if(securityKeyResource.getPrivateKeyFile() != null && securityKeyResource.getPublicKeyFile() != null) {
            return mapper.updateByPrimaryKeyWithBLOBs(securityKeyResource);
        }else if(securityKeyResource.getPrivateKeyFile() == null && securityKeyResource.getPublicKeyFile() == null) {
            return mapper.updateByPrimaryKey(securityKeyResource);
        }else {
            SecurityKeyResource securityKey = this.byId(securityKeyResource.getId());
            if(securityKeyResource.getPrivateKeyFile() == null) {
                securityKeyResource.setPrivateKeyFile(securityKey.getPrivateKeyFile());
            }
            if(securityKeyResource.getPublicKeyFile() == null) {
                securityKeyResource.setPublicKeyFile(securityKey.getPublicKeyFile());
            }
            return mapper.updateByPrimaryKeyWithBLOBs(securityKeyResource);
        }       
    }

    public List<SecurityKeyResource> list() {
        return mapper.selectByExample(null);
    }

    public int delete(String id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public List<SecurityKeyResource> list(String systemId) {
        SecurityKeyResourceExample criteria = new SecurityKeyResourceExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        criteria.setOrderByClause("name");
        return mapper.selectByExample(criteria);
    }

    public List<SecurityKeyResource> listWithBloB() {
        return mapper.selectByExampleWithBLOBs(null);
    }

    public List<SecurityKeyResource> listWithBloB(String systemId) {
        SecurityKeyResourceExample criteria = new SecurityKeyResourceExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExampleWithBLOBs(criteria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey == null ? null : securityKey.trim();
    }

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm == null ? null : cipherAlgorithm.trim();
    }

    public String getPrivateKeyPwd() {
        return privateKeyPwd;
    }

    public void setPrivateKeyPwd(String privateKeyPwd) {
        this.privateKeyPwd = privateKeyPwd == null ? null : privateKeyPwd.trim();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public byte[] getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(byte[] privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public byte[] getPublicKeyFile() {
        return publicKeyFile;
    }

    public void setPublicKeyFile(byte[] publicKeyFile) {
        this.publicKeyFile = publicKeyFile;
    }

    public SecurityKeyResourceMapper getMapper() {
        return mapper;
    }

    public void setMapper(SecurityKeyResourceMapper mapper) {
        this.mapper = mapper;
    }

    public String getPrivateKeyFileName() {
        return privateKeyFileName;
    }

    public void setPrivateKeyFileName(String privateKeyFileName) {
        this.privateKeyFileName = privateKeyFileName;
    }

    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    public void setPublicKeyFileName(String publicKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
    }
}