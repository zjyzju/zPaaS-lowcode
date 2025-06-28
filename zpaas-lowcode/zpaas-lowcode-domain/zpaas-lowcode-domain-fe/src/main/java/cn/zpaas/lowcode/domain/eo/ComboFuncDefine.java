package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.ComboFuncDefineMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ComboFuncDefine {
    private String id;

    private String name;

    private String comboTemplateId;

    private String domainId;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private ComboFuncTemplate comboFuncTemplate;
    
    private List<ComboFuncTab> comboFuncTabs;
    
    @Autowired
    private ComboFuncDefineMapper mapper;
    
    @Autowired
    private ComboFuncTab comboFuncTabSV;
    
    @Autowired
    private ComboFuncTemplate comboFuncTemplateSV;
    
    public ComboFuncDefine loadAll(String comboFuncId) {
    	ComboFuncDefine comboFuncDefine = this.byId(comboFuncId);
    	if(comboFuncDefine == null || !Status.EFF.equals(comboFuncDefine.getStatus())) {
    		return null;
    	}
    	comboFuncDefine.setComboFuncTemplate(this.comboFuncTemplateSV.byId(comboFuncDefine.getComboTemplateId()));
    	comboFuncDefine.setComboFuncTabs(this.comboFuncTabSV.list(comboFuncId));
    	return comboFuncDefine;
    }
    
    public ComboFuncDefine byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public ComboFuncDefine update(ComboFuncDefine comboFuncDefine) {
    	comboFuncDefine.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(comboFuncDefine) > 0) {
    		return comboFuncDefine;
    	}else {
    		return null;
    	}
    }
    
    public ComboFuncDefine save(ComboFuncDefine comboFuncDefine) {
    	if(comboFuncDefine.getId() == null || comboFuncDefine.getId().trim().length() == 0) {
    		comboFuncDefine.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	comboFuncDefine.setCreateTime(nowDate);
    	comboFuncDefine.setUpdateTime(nowDate);
    	comboFuncDefine.setStatus(Status.EFF);
    	if(mapper.insert(comboFuncDefine)> 0) {
    		return comboFuncDefine;
    	}else {
    		return null;
    	}
    }
    
    public int delete(String id) {
    	ComboFuncDefine comboFuncDefine = this.byId(id);
    	if(comboFuncDefine == null || Status.EXP.equals(comboFuncDefine.getStatus())) {
    		return 0;
    	}
    	comboFuncDefine.setStatus(Status.EXP);
    	comboFuncDefine.setUpdateTime(new Date());
    	return mapper.updateByPrimaryKey(comboFuncDefine);
    }
    
    public List<ComboFuncDefine> list(String systemId) {
    	ComboFuncDefineExample criteria = new ComboFuncDefineExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
    }

    public List<ComboFuncDefine> list() {
    	ComboFuncDefineExample criteria = new ComboFuncDefineExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
    	return mapper.selectByExample(criteria);
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

    public String getComboTemplateId() {
        return comboTemplateId;
    }

    public void setComboTemplateId(String comboTemplateId) {
        this.comboTemplateId = comboTemplateId == null ? null : comboTemplateId.trim();
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

	public ComboFuncTemplate getComboFuncTemplate() {
		return comboFuncTemplate;
	}

	public void setComboFuncTemplate(ComboFuncTemplate comboFuncTemplate) {
		this.comboFuncTemplate = comboFuncTemplate;
	}

	public List<ComboFuncTab> getComboFuncTabs() {
		return comboFuncTabs;
	}

	public void setComboFuncTabs(List<ComboFuncTab> comboFuncTabs) {
		this.comboFuncTabs = comboFuncTabs;
	}
}