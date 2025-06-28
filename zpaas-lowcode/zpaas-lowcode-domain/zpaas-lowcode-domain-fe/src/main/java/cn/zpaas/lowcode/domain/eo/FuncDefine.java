package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.FuncDefineMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncDefine {
	
	private String id;

    private String name;

	private String domainId;

    private String description;

    private String templateId;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private Map<String, FuncRegion> regionMap;//tplRegionId->FuncRegion
    
    private Map<String, FuncRegion> targetRegionMap;//regionId->FuncRegion
    
    private List<FuncRegion> funcRegions;
    
    private Map<String, FuncObjectAssign> objectMap;//objectId->FuncObjectAssign
    
    private List<FuncObjectAssign> objectAssigns;
    
    private FuncTemplate funcTemplate;
    
    private String platformUrl;
    
    private String systemUrl;
    
    private String platformFileUploadUrl;
    
    private String platformFileDownloadUrl;

	private List<FuncCustomizedPage> customizedPages;
    
    



	@Autowired
    private FuncDefineMapper mapper;
    
    @Autowired
    private FuncTemplate funcTemplateSV;
    
    public FuncDefine create(FuncDefine funcDefine) {
    	if(funcDefine.getId() == null || funcDefine.getId().trim().length() == 0) {
    		funcDefine.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	funcDefine.setCreateTime(nowDate);
    	funcDefine.setUpdateTime(nowDate);
    	funcDefine.setStatus(Status.EFF);
    	if(mapper.insert(funcDefine) > 0) {
    		return funcDefine;
    	}else {
    		return null;
    	}
    }
    
    public FuncDefine save(FuncDefine funcDefine) {
    	Date nowDate = new Date();
    	funcDefine.setUpdateTime(nowDate);
    	if(mapper.updateByPrimaryKey(funcDefine) > 0) {
    		return funcDefine;
    	}else {
    		return null;
    	}
    }
    
    public int delete(String id) {
    	FuncDefine funcDefine = this.byId(id);
    	if(funcDefine == null) {
    		return 0;
    	}
    	funcDefine.setStatus(Status.EXP);
    	funcDefine.setUpdateTime(new Date());
    	return mapper.updateByPrimaryKey(funcDefine);
    }
    
	/**
	 * 加载模板类功能列表
	 * @param systemId
	 * @return
	 */
    public List<FuncDefine> listBySystem(String systemId) {
    	FuncDefineExample criteria = new FuncDefineExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }

	/**
	 * 加载模板类功能列表
	 * @param systemId
	 * @return
	 */
    public List<FuncDefine> listTplFuncBySystem(String systemId) {
    	FuncDefineExample criteria = new FuncDefineExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andTemplateIdNotEqualTo("99").andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }

	/**
	 * 加载自定义类功能列表
	 * @param systemId
	 * @return
	 */
    public List<FuncDefine> listCustomizedFuncBySystem(String systemId) {
    	FuncDefineExample criteria = new FuncDefineExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andTemplateIdEqualTo("99").andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }

	public List<FuncDefine> listWithTplBySystem(String systemId) {
    	FuncDefineExample criteria = new FuncDefineExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	List<FuncDefine> funcDefines = mapper.selectByExample(criteria);
		if(!CollectionUtils.isEmpty(funcDefines)) {
			List<FuncTemplate> funcTemplates = this.funcTemplateSV.list();
			if(!CollectionUtils.isEmpty(funcTemplates)) {
				Map<String, FuncTemplate> funcTemplateMap = funcTemplates.stream().collect(Collectors.toMap(FuncTemplate::getId, t->t));
				for(FuncDefine funcDefine: funcDefines) {
					funcDefine.setFuncTemplate(funcTemplateMap.get(funcDefine.getTemplateId()));
				}
			}
			
		}
		return funcDefines;
    }

	public List<FuncDefine> list() {
    	FuncDefineExample criteria = new FuncDefineExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }
    
    public FuncDefine byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public FuncDefine loadFuncDefineAndTpl(String id) {
    	FuncDefine funcDefine = this.byId(id);
    	if(funcDefine != null && funcDefine.getTemplateId() != null && funcDefine.getTemplateId().trim().length() > 0) {
    		funcDefine.setFuncTemplate(funcTemplateSV.byId(funcDefine.getTemplateId()));
    	}
    	return funcDefine;
    }

	public FuncTemplateComposite getSubTplRegionByFunc(String id) {
		FuncDefine funcDefine = this.byId(id);
		if(funcDefine == null) {
			return null;
		}
		return this.funcTemplateSV.loadSubRegionByTpl(funcDefine.getTemplateId());
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

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId == null ? null : templateId.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

	public Map<String, FuncRegion> getRegionMap() {
		return regionMap;
	}

	public void setRegionMap(Map<String, FuncRegion> regionMap) {
		this.regionMap = regionMap;
	}

	public Map<String, FuncObjectAssign> getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(Map<String, FuncObjectAssign> objectMap) {
		this.objectMap = objectMap;
	}

	public List<FuncRegion> getFuncRegions() {
		return funcRegions;
	}

	public void setFuncRegions(List<FuncRegion> funcRegions) {
		this.funcRegions = funcRegions;
	}

	public List<FuncObjectAssign> getObjectAssigns() {
		return objectAssigns;
	}

	public void setObjectAssigns(List<FuncObjectAssign> objectAssigns) {
		this.objectAssigns = objectAssigns;
	}

	public FuncTemplate getFuncTemplate() {
		return funcTemplate;
	}

	public void setFuncTemplate(FuncTemplate funcTemplate) {
		this.funcTemplate = funcTemplate;
	}



	public Map<String, FuncRegion> getTargetRegionMap() {
		return targetRegionMap;
	}



	public void setTargetRegionMap(Map<String, FuncRegion> targetRegionMap) {
		this.targetRegionMap = targetRegionMap;
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

	public List<FuncCustomizedPage> getCustomizedPages() {
		return customizedPages;
	}

	public void setCustomizedPages(List<FuncCustomizedPage> customizedPages) {
		this.customizedPages = customizedPages;
	}
}