package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.BusinessSystemAuthMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class BusinessSystemAuth {
	private static final String URL_SEPARATE_SYMBOL = ";";//URL分隔符
	
    private String id;

    private String authType;

    private String userInfoKey;

    private String userIdAttr;

    private String userInfoService;

    private String userInfoMethod;

    private String authConfig;

    private String ignoreUrls;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<AntPathRequestMatcher> ignoreUrlMatchers;
    private Map<String, ThirdpartySystem> thirdparthSystemMap;
    
    @Autowired
    private BusinessSystemAuthMapper mapper;
    
    public BusinessSystemAuth byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public BusinessSystemAuth save(BusinessSystemAuth auth) {
        if(auth == null) {
            return null;
        }
    	if(auth.getId() == null || auth.getId().trim().length() == 0) {
    		auth.setId(KeyGenerate.uuidKey());
    	}
    	
    	Date nowDate = new Date();
    	auth.setCreateTime(nowDate);
    	auth.setUpdateTime(nowDate);
    	
    	if(mapper.insert(auth) > 0) {
    		return auth;
    	}else {
    		return null;
    	}
    }
    
    public int updateById(BusinessSystemAuth auth) {
    	Date nowDate = new Date();
    	auth.setUpdateTime(nowDate);
    	
    	return mapper.updateByPrimaryKey(auth);
    }
    
    public BusinessSystemAuth saveOrUpdate(BusinessSystemAuth auth) {
    	if(auth != null && (auth.getId() == null || auth.getId().trim().length() == 0)) {
    		return this.save(auth);
    	}else {
    		if(this.updateById(auth) > 0) {
    			return auth;
    		}else {
    			return null;
    		}
    	}
    }
    
    public BusinessSystemAuth queryBySystem(String systemId) {
    	BusinessSystemAuthExample criteria = new BusinessSystemAuthExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	List<BusinessSystemAuth> list = mapper.selectByExample(criteria);
    	if(list != null && !list.isEmpty()) {
    		return list.get(0);
    	}else {
    		return null;
    	}
    }
    
    public void initUrlMatchers() {
    	if(ignoreUrls != null && ignoreUrls.trim().length() > 0) {
    		String[] urls = ignoreUrls.split(URL_SEPARATE_SYMBOL);
    		if(urls != null && urls.length > 0) {
    			ignoreUrlMatchers = new ArrayList<>();
    			for(int i=0; i<urls.length; i++) {
        			ignoreUrlMatchers.add(new AntPathRequestMatcher(urls[i]));
        		}
    		}
    		
    	}
    }
    
    public List<BusinessSystemAuth> list() {
    	return mapper.selectByExample(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType == null ? null : authType.trim();
    }

    public String getUserInfoKey() {
        return userInfoKey;
    }

    public void setUserInfoKey(String userInfoKey) {
        this.userInfoKey = userInfoKey == null ? null : userInfoKey.trim();
    }

    public String getUserIdAttr() {
        return userIdAttr;
    }

    public void setUserIdAttr(String userIdAttr) {
        this.userIdAttr = userIdAttr == null ? null : userIdAttr.trim();
    }

    public String getUserInfoService() {
        return userInfoService;
    }

    public void setUserInfoService(String userInfoService) {
        this.userInfoService = userInfoService == null ? null : userInfoService.trim();
    }

    public String getUserInfoMethod() {
        return userInfoMethod;
    }

    public void setUserInfoMethod(String userInfoMethod) {
        this.userInfoMethod = userInfoMethod == null ? null : userInfoMethod.trim();
    }

    public String getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(String authConfig) {
        this.authConfig = authConfig == null ? null : authConfig.trim();
    }

    public String getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(String ignoreUrls) {
        this.ignoreUrls = ignoreUrls == null ? null : ignoreUrls.trim();
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

	public List<AntPathRequestMatcher> getIgnoreUrlMatchers() {
		return ignoreUrlMatchers;
	}

	public void setIgnoreUrlMatchers(List<AntPathRequestMatcher> ignoreUrlMatchers) {
		this.ignoreUrlMatchers = ignoreUrlMatchers;
	}

	public Map<String, ThirdpartySystem> getThirdparthSystemMap() {
		return thirdparthSystemMap;
	}

	public void setThirdparthSystemMap(Map<String, ThirdpartySystem> thirdparthSystemMap) {
		this.thirdparthSystemMap = thirdparthSystemMap;
	}
}