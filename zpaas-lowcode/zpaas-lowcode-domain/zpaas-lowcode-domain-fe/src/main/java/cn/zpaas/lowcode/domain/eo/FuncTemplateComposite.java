package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.FuncTemplateCompositeMapper;

@Repository
public class FuncTemplateComposite {
    private String id;

    private String name;

    private String description;

    private String templateId;

    private String tplRegionId;

    private Date createTime;

    private Date updateTime;
    
    private FuncTemplateRegion funcTemplateRegion;
    
    @Autowired
    private FuncTemplateCompositeMapper mapper;
    
    public List<FuncTemplateComposite> list() {
    	return mapper.selectByExample(null);
    }

    public List<FuncTemplateComposite> listByTpl(String tplId) {
        FuncTemplateCompositeExample criteria = new FuncTemplateCompositeExample();
        criteria.createCriteria().andTemplateIdEqualTo(tplId);
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

    public String getTplRegionId() {
        return tplRegionId;
    }

    public void setTplRegionId(String tplRegionId) {
        this.tplRegionId = tplRegionId == null ? null : tplRegionId.trim();
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

	public FuncTemplateRegion getFuncTemplateRegion() {
		return funcTemplateRegion;
	}

	public void setFuncTemplateRegion(FuncTemplateRegion funcTemplateRegion) {
		this.funcTemplateRegion = funcTemplateRegion;
	}
}