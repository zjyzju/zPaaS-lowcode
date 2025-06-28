package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.mapper.FuncCustomizedLayoutMapper;
import cn.zpaas.lowcode.exception.EngineException;

@Repository
public class FuncCustomizedLayout {
    private String id;

    private String name;

    private String componentCode;

    private String componentType;

    private String funcId;

    private String pageId;

    private String parentLayoutId;

    private Integer displayOrder;

    private String layoutCfg;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<FuncCustomizedLayoutRegion> layoutRegions;

    private List<FuncCustomizedLayout> subCustomizedLayouts;

    @Autowired
    private FuncCustomizedLayoutMapper mapper;

    @Autowired
    private FuncCustomizedLayoutRegion funcCustomizedLayoutRegionSV;

    public List<FuncCustomizedLayout> listBySystem(String systemId) {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        criteria.setOrderByClause("display_order");
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedLayout> list() {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.setOrderByClause("display_order");
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedLayout> listByFuncId(String funcId) {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.createCriteria().andFuncIdEqualTo(funcId);
        criteria.setOrderByClause("display_order");
        List<FuncCustomizedLayout> customizedLayouts = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(customizedLayouts)) {
            List<FuncCustomizedLayoutRegion> regions = funcCustomizedLayoutRegionSV.listByFuncId(funcId);
            if(!CollectionUtils.isEmpty(regions)) {
                Map<String, List<FuncCustomizedLayoutRegion>> regionMap = regions.stream().collect(Collectors.groupingBy(FuncCustomizedLayoutRegion::getLayoutId));
                customizedLayouts.forEach((layout)-> {
                    layout.setLayoutRegions(regionMap.get(layout.getId()));
                });
            }
        }

        return customizedLayouts;
    }

    public List<FuncCustomizedLayout> listByPageId(String pageId) {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.createCriteria().andPageIdEqualTo(pageId);
        criteria.setOrderByClause("display_order");
        List<FuncCustomizedLayout> customizedLayouts = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(customizedLayouts)) {
            List<FuncCustomizedLayoutRegion> regions = funcCustomizedLayoutRegionSV.listByPageId(pageId);
            if(!CollectionUtils.isEmpty(regions)) {
                Map<String, List<FuncCustomizedLayoutRegion>> regionMap = regions.stream().collect(Collectors.groupingBy(FuncCustomizedLayoutRegion::getLayoutId));
                customizedLayouts.forEach((layout)-> {
                    layout.setLayoutRegions(regionMap.get(layout.getId()));
                });
            }
        }

        return customizedLayouts;
    }

    public int deleteByPageId(String pageId) {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.createCriteria().andPageIdEqualTo(pageId);
        return this.mapper.deleteByExample(criteria);
    }

    public int deleteAllByPageId(String pageId) {
        FuncCustomizedLayoutExample criteria = new FuncCustomizedLayoutExample();
        criteria.createCriteria().andPageIdEqualTo(pageId);

        List<FuncCustomizedLayoutRegion> layoutRegions = this.funcCustomizedLayoutRegionSV.listByPageId(pageId);
        if(!CollectionUtils.isEmpty(layoutRegions)) {
            for(FuncCustomizedLayoutRegion layoutRegion : layoutRegions) {
                this.funcCustomizedLayoutRegionSV.deleteById(layoutRegion.getId());
            }
        }
        return this.mapper.deleteByExample(criteria);
    }

    public FuncCustomizedLayout saveFuncCustomizedLayout(FuncCustomizedLayout funcCustomizedLayout, String parentLayoutId, FuncTemplateComposite subTemplateComposite) throws EngineException {
        Date nowDate = new Date();
        if(StringUtils.isBlank(funcCustomizedLayout.getId())) {
            funcCustomizedLayout.setId(UUID.uuidKey());
            funcCustomizedLayout.setCreateTime(nowDate);
            funcCustomizedLayout.setUpdateTime(nowDate);
            funcCustomizedLayout.setParentLayoutId(parentLayoutId);
        }else {
            funcCustomizedLayout.setUpdateTime(nowDate);
            funcCustomizedLayout.setParentLayoutId(parentLayoutId);
        }
        if(this.mapper.insert(funcCustomizedLayout) <=0) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcCustomizedLayout failed!");
        }
        
        //删除原来功能区域以及相应的绑定属性和操作信息
        List<FuncCustomizedLayoutRegion> layoutRegions = this.funcCustomizedLayoutRegionSV.listByLayoutId(funcCustomizedLayout.getId());
        if(!CollectionUtils.isEmpty(layoutRegions)) {
            for(FuncCustomizedLayoutRegion layoutRegion : layoutRegions) {
                this.funcCustomizedLayoutRegionSV.deleteById(layoutRegion.getId());
            }
        }
        //处理功能区域以及相应的绑定属性和操作的保存
        if(!CollectionUtils.isEmpty(funcCustomizedLayout.getLayoutRegions())) {
            for(FuncCustomizedLayoutRegion layoutRegion : funcCustomizedLayout.getLayoutRegions()) {
                layoutRegion.setLayoutId(funcCustomizedLayout.getId());
                this.funcCustomizedLayoutRegionSV.saveFuncCustomizedLayoutRegion(layoutRegion, subTemplateComposite);
            }
        }
        //递归处理子功能页面自定义布局
        if(!CollectionUtils.isEmpty(funcCustomizedLayout.getSubCustomizedLayouts())) {
            for(FuncCustomizedLayout subCustomizedLayout : funcCustomizedLayout.getSubCustomizedLayouts()) {
                this.saveFuncCustomizedLayout(subCustomizedLayout, funcCustomizedLayout.getId(), subTemplateComposite);
            }
        }
        return funcCustomizedLayout;
    }

    public boolean copyFuncCustomizedLayout(FuncCustomizedLayout funcCustomizedLayout, String parentLayoutId, String targetFuncId, String targetPageId) throws EngineException {
        funcCustomizedLayout.setFuncId(targetFuncId);
        funcCustomizedLayout.setPageId(targetPageId);
        Date nowDate = new Date();
        funcCustomizedLayout.setId(UUID.uuidKey());
        funcCustomizedLayout.setCreateTime(nowDate);
        funcCustomizedLayout.setUpdateTime(nowDate);
        funcCustomizedLayout.setParentLayoutId(parentLayoutId);
        
        if(this.mapper.insert(funcCustomizedLayout) <=0) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcCustomizedLayout failed!");
        }
        
        //处理功能区域以及相应的绑定属性和操作的保存
        if(!CollectionUtils.isEmpty(funcCustomizedLayout.getLayoutRegions())) {
            for(FuncCustomizedLayoutRegion layoutRegion : funcCustomizedLayout.getLayoutRegions()) {
                layoutRegion.setLayoutId(funcCustomizedLayout.getId());
                this.funcCustomizedLayoutRegionSV.copyFuncCustomizedLayoutRegion(layoutRegion, targetFuncId, targetPageId);
            }
        }
        //递归处理子功能页面自定义布局
        if(!CollectionUtils.isEmpty(funcCustomizedLayout.getSubCustomizedLayouts())) {
            for(FuncCustomizedLayout subCustomizedLayout : funcCustomizedLayout.getSubCustomizedLayouts()) {
                this.copyFuncCustomizedLayout(subCustomizedLayout, funcCustomizedLayout.getId(), targetFuncId, targetPageId);
            }
        }
        return true;
    }

    public List<FuncCustomizedLayout> getSubCustomizedLayouts() {
        return subCustomizedLayouts;
    }

    public void setSubCustomizedLayouts(List<FuncCustomizedLayout> subCustomizedLayouts) {
        this.subCustomizedLayouts = subCustomizedLayouts;
    }

    public List<FuncCustomizedLayoutRegion> getLayoutRegions() {
        return layoutRegions;
    }

    public void setLayoutRegions(List<FuncCustomizedLayoutRegion> layoutRegions) {
        this.layoutRegions = layoutRegions;
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

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode == null ? null : componentCode.trim();
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType == null ? null : componentType.trim();
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId == null ? null : pageId.trim();
    }

    public String getParentLayoutId() {
        return parentLayoutId;
    }

    public void setParentLayoutId(String parentLayoutId) {
        this.parentLayoutId = parentLayoutId == null ? null : parentLayoutId.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getLayoutCfg() {
        return layoutCfg;
    }

    public void setLayoutCfg(String layoutCfg) {
        this.layoutCfg = layoutCfg == null ? null : layoutCfg.trim();
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
}