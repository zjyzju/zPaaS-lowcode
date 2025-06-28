package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.ComboFuncTabMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class ComboFuncTab {
    private String id;

    private String name;

    private String comboFuncId;

    private String tabParams;

    private String tabFuncId;

    private Integer displayOrder;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private FuncDefine funcDefine;
    
    @Autowired
    private ComboFuncTabMapper mapper;
    
    @Autowired
    private FuncDefine funcDefineSV;
    
    public List<ComboFuncTab> list(String comboFuncId) {
    	ComboFuncTabExample criteria = new ComboFuncTabExample();
    	criteria.createCriteria().andComboFuncIdEqualTo(comboFuncId);
    	criteria.setOrderByClause("display_order");
    	List<ComboFuncTab> comboFuncTabs = mapper.selectByExample(criteria);
    	if(comboFuncTabs != null && !comboFuncTabs.isEmpty()) {
    		for(ComboFuncTab tab : comboFuncTabs) {
    			tab.setFuncDefine(funcDefineSV.loadFuncDefineAndTpl(tab.getTabFuncId()));
    		}
    	}
    	return comboFuncTabs;
    }

    public List<ComboFuncTab> listBySystem(String systemId) {
    	ComboFuncTabExample criteria = new ComboFuncTabExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }

    public List<ComboFuncTab> list() {
    	return mapper.selectByExample(null);
    }
    
    public ComboFuncTab loadAll(String id) {
    	ComboFuncTab comboFuncTab = this.byId(id);
    	if(comboFuncTab != null) {
    		comboFuncTab.setFuncDefine(funcDefineSV.byId(comboFuncTab.getTabFuncId()));
    	}
    	return comboFuncTab;
    }
    
    public int delete(String systemId, String id) {
    	if(id == null || id.trim().length() == 0) {
    		return 0;
    	}
    	ComboFuncTabExample criteria = new ComboFuncTabExample();
    	criteria.createCriteria().andIdEqualTo(id).andSystemIdEqualTo(systemId);
    	
    	return mapper.deleteByExample(criteria);
    }
    
    public ComboFuncTab byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public ComboFuncTab update(ComboFuncTab comboFuncTab) {
    	comboFuncTab.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(comboFuncTab)> 0) {
    		return comboFuncTab;
    	}else {
    		return null;
    	}
    }
    
    public ComboFuncTab save(ComboFuncTab comboFuncTab) {
    	if(comboFuncTab.getId() == null || comboFuncTab.getId().trim().length() == 0) {
    		comboFuncTab.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	comboFuncTab.setCreateTime(nowDate);
    	comboFuncTab.setUpdateTime(nowDate);
    	if(mapper.insert(comboFuncTab) > 0) {
    		return comboFuncTab;
    	}else {
    		return null;
    	}
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

    public String getComboFuncId() {
        return comboFuncId;
    }

    public void setComboFuncId(String comboFuncId) {
        this.comboFuncId = comboFuncId == null ? null : comboFuncId.trim();
    }

    public String getTabParams() {
        return tabParams;
    }

    public void setTabParams(String tabParams) {
        this.tabParams = tabParams == null ? null : tabParams.trim();
    }

    public String getTabFuncId() {
        return tabFuncId;
    }

    public void setTabFuncId(String tabFuncId) {
        this.tabFuncId = tabFuncId == null ? null : tabFuncId.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

	public FuncDefine getFuncDefine() {
		return funcDefine;
	}

	public void setFuncDefine(FuncDefine funcDefine) {
		this.funcDefine = funcDefine;
	}
}