package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.TemplateType;
import cn.zpaas.lowcode.domain.mapper.FuncTemplateMapper;

@Repository
public class FuncTemplate {
    private String id;

    private String name;

    private String description;

    private String url;

    private String templateType;
    
    private String isMainFunc;

    private String status;

    private Date createTime;

    private Date updateTime;
    
    private List<FuncTemplateComposite> funcTemplateComposites;
    
    @Autowired
    private FuncTemplateMapper mapper;
    
    @Autowired
    private FuncTemplateComposite funcTemplateCompositeSV;
    
    @Autowired
    private FuncTemplateRegion funcTemplateRegionSV;
    
    @Autowired
    private FuncTemplateRegionOperation funcTemplateRegionOperationSV;
    
    public List<FuncTemplate> loadAll() {
    	List<FuncTemplate> funcTemplates = this.list();
    	if(funcTemplates != null && !funcTemplates.isEmpty()) {
    		List<FuncTemplateComposite> funcTemplateComposites = funcTemplateCompositeSV.list();
    		Map<String, List<FuncTemplateComposite>> funcTemplateCompositeMap = new HashMap<>();
    		if(funcTemplateComposites != null && !funcTemplateComposites.isEmpty()) {
    			funcTemplateCompositeMap = funcTemplateComposites.stream().collect(Collectors.groupingBy(FuncTemplateComposite::getTemplateId));
    		}
    		
    		List<FuncTemplateRegion> funcTemplateRegions = funcTemplateRegionSV.list();
    		Map<String, FuncTemplateRegion> funcTemplateRegionMap = new HashMap<>();
    		if(funcTemplateRegions != null && !funcTemplateRegions.isEmpty()) {
    			funcTemplateRegionMap = funcTemplateRegions.stream().collect(Collectors.toMap(FuncTemplateRegion::getId, t->t));
    		}
    		
    		List<FuncTemplateRegionOperation> funcTemplateRegionOperations = funcTemplateRegionOperationSV.list();
    		Map<String, List<FuncTemplateRegionOperation>> funcTemplateRegionOperationMap = new HashMap<>();
    		if(funcTemplateRegionOperations != null && !funcTemplateRegionOperations.isEmpty()) {
    			funcTemplateRegionOperationMap = funcTemplateRegionOperations.stream().collect(Collectors.groupingBy(FuncTemplateRegionOperation::getTplRegionId));
    		}
    		
    		for(FuncTemplate template : funcTemplates) {
    			template.setFuncTemplateComposites(funcTemplateCompositeMap.get(template.getId()));
    			if(template.getFuncTemplateComposites() != null && !template.getFuncTemplateComposites().isEmpty()) {
    				for(FuncTemplateComposite composite : template.getFuncTemplateComposites()) {
    					composite.setFuncTemplateRegion(funcTemplateRegionMap.get(composite.getTplRegionId()));
    					if(composite.getFuncTemplateRegion() != null) {
    						composite.getFuncTemplateRegion().setFuncTemplateRegionOperations(funcTemplateRegionOperationMap.get(composite.getFuncTemplateRegion().getId()));
    					}
    				}
    			}
    		}
    	}
    	return funcTemplates;
    }

    public FuncTemplateComposite loadSubRegionByTpl(String tplId) {
        List<FuncTemplateComposite> funcTemplateComposites = this.funcTemplateCompositeSV.listByTpl(tplId);
        if(!CollectionUtils.isEmpty(funcTemplateComposites)) {
            for(FuncTemplateComposite composite : funcTemplateComposites) {
                FuncTemplateRegion templateRegion = this.funcTemplateRegionSV.byId(composite.getTplRegionId());
                if(TemplateType.TEMPLATE_REGION_TYPE_CS.equals(templateRegion.getRegionType())) {
                    return composite;
                }
            }
        }
        return null;
    }
    
    public FuncTemplate byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<FuncTemplate> list() {
    	FuncTemplateExample criteria = new FuncTemplateExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
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

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

	public List<FuncTemplateComposite> getFuncTemplateComposites() {
		return funcTemplateComposites;
	}

	public void setFuncTemplateComposites(List<FuncTemplateComposite> funcTemplateComposites) {
		this.funcTemplateComposites = funcTemplateComposites;
	}

	public String getIsMainFunc() {
		return isMainFunc;
	}

	public void setIsMainFunc(String isMainFunc) {
		this.isMainFunc = isMainFunc;
	}
}