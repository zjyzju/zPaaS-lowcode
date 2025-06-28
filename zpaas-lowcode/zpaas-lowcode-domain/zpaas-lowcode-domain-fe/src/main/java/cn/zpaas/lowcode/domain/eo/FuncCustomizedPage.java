package cn.zpaas.lowcode.domain.eo;

import java.util.ArrayList;
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
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.FuncCustomizedPageMapper;
import cn.zpaas.lowcode.exception.EngineException;

@Repository
public class FuncCustomizedPage {
    private String id;

    private String name;

    private String funcId;

    private String pageCfg;

    private String isMainPage;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<FuncCustomizedLayout> customizedLayouts;

    @Autowired
    private FuncCustomizedPageMapper mapper;

    @Autowired
    private FuncCustomizedLayout funcCustomizedLayoutSV;

    @Autowired
    private FuncDefine funcDefineSV;

    public FuncCustomizedPage create(FuncCustomizedPage funcCustomizedPage) {
        if(StringUtils.isBlank(funcCustomizedPage.getId())) {
            funcCustomizedPage.setId(UUID.uuidKey());
        }
        Date nowDate = new Date();
        funcCustomizedPage.setCreateTime(nowDate);
        funcCustomizedPage.setUpdateTime(nowDate);
        funcCustomizedPage.setStatus(Status.EFF);
        if(mapper.insert(funcCustomizedPage) > 0) {
            return funcCustomizedPage;
        }else {
            return null;
        }
    }

    public FuncCustomizedPage save(FuncCustomizedPage funcCustomizedPage) {
        Date nowDate = new Date();
        funcCustomizedPage.setUpdateTime(nowDate);
        if(mapper.updateByPrimaryKey(funcCustomizedPage) > 0) {
            return funcCustomizedPage;
        }else {
            return null;
        }
    }

    public FuncCustomizedPage byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<FuncCustomizedPage> listByFuncId(String funcId) {
        FuncCustomizedPageExample criteria = new FuncCustomizedPageExample();
        criteria.createCriteria().andFuncIdEqualTo(funcId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("create_time");
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedPage> listByFuncId(String systemId, String funcId) {
        FuncCustomizedPageExample criteria = new FuncCustomizedPageExample();
        criteria.createCriteria().andFuncIdEqualTo(funcId).andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("create_time");
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedPage> listBySystem(String systemId) {
        FuncCustomizedPageExample criteria = new FuncCustomizedPageExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedPage> list() {
        FuncCustomizedPageExample criteria = new FuncCustomizedPageExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedPage> loadFuncCustomizedPagesAll(String funcId) {
        List<FuncCustomizedPage> customizedPages = this.listByFuncId(funcId);
        if(CollectionUtils.isEmpty(customizedPages)) {
            return null;
        }
        List<FuncCustomizedLayout> funcCustomizedLayouts = this.funcCustomizedLayoutSV.listByFuncId(funcId);
        if(!CollectionUtils.isEmpty(funcCustomizedLayouts)) {
            Map<String, List<FuncCustomizedLayout>> funcCustomizedLayoutsForPageMap = funcCustomizedLayouts.stream().collect(Collectors.groupingBy(FuncCustomizedLayout::getPageId));
            for(FuncCustomizedPage customizedPage : customizedPages) {
                List<FuncCustomizedLayout> funcCustomizedLayoutsPage = funcCustomizedLayoutsForPageMap.get(customizedPage.getId());
                if(!CollectionUtils.isEmpty(funcCustomizedLayoutsPage)) {
                    List<FuncCustomizedLayout> firstLevelLayouts = new ArrayList<>();
                    List<FuncCustomizedLayout> subLevelLayouts = new ArrayList<>();
                    for(FuncCustomizedLayout funcCustomizedLayout : funcCustomizedLayoutsPage) {
                        if(StringUtils.isBlank(funcCustomizedLayout.getParentLayoutId())) {
                            firstLevelLayouts.add(funcCustomizedLayout);
                        }else {
                            subLevelLayouts.add(funcCustomizedLayout);
                        }
                    }
                    Map<String, List<FuncCustomizedLayout>> layoutMap = subLevelLayouts.stream().collect(Collectors.groupingBy(FuncCustomizedLayout::getParentLayoutId));
                    this.processLayoutLevel(firstLevelLayouts, layoutMap);
                    customizedPage.setCustomizedLayouts(firstLevelLayouts);
                }
            }
        }
        return customizedPages;
    }

    public FuncCustomizedPage loadFuncCustomizedPageAll(String pageId) {
        FuncCustomizedPage funcCustomizedPage = this.mapper.selectByPrimaryKey(pageId);
        if(funcCustomizedPage == null || !Status.EFF.equals(funcCustomizedPage.getStatus())) {
            return null;
        }
        List<FuncCustomizedLayout> funcCustomizedLayouts = this.funcCustomizedLayoutSV.listByPageId(pageId);
        if(!CollectionUtils.isEmpty(funcCustomizedLayouts)) {
            List<FuncCustomizedLayout> firstLevelLayouts = new ArrayList<>();
            List<FuncCustomizedLayout> subLevelLayouts = new ArrayList<>();
            for(FuncCustomizedLayout funcCustomizedLayout : funcCustomizedLayouts) {
                if(StringUtils.isBlank(funcCustomizedLayout.getParentLayoutId())) {
                    firstLevelLayouts.add(funcCustomizedLayout);
                }else {
                    subLevelLayouts.add(funcCustomizedLayout);
                }
            }
            Map<String, List<FuncCustomizedLayout>> layoutMap = subLevelLayouts.stream().collect(Collectors.groupingBy(FuncCustomizedLayout::getParentLayoutId));
            this.processLayoutLevel(firstLevelLayouts, layoutMap);
            funcCustomizedPage.setCustomizedLayouts(firstLevelLayouts);
        }
        return funcCustomizedPage;
    }

    /**
     * 处理布局的父子层级关系
     * @param firstLevelLayouts
     * @param layoutMap
     */
    private void processLayoutLevel(List<FuncCustomizedLayout> firstLevelLayouts, Map<String, List<FuncCustomizedLayout>> layoutMap) {
        if(CollectionUtils.isEmpty(firstLevelLayouts)) {
            return;
        }
        firstLevelLayouts.forEach((layout) -> {
            layout.setSubCustomizedLayouts(layoutMap.get(layout.getId()));
            if(!CollectionUtils.isEmpty(layout.getSubCustomizedLayouts())) {
                this.processLayoutLevel(layout.getSubCustomizedLayouts(), layoutMap);
            }
        });
    }

    public int saveCustomizePageLayoutsAll(String pageId, List<FuncCustomizedLayout> customizedLayouts)throws EngineException {
        FuncCustomizedPage customizedPage = this.mapper.selectByPrimaryKey(pageId);
        if(customizedPage == null) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid pageId!");
        }

        FuncTemplateComposite subTemplateComposite = this.funcDefineSV.getSubTplRegionByFunc(customizedPage.getFuncId());
        if(subTemplateComposite == null) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "can't find valid subTemplateComposite!");
        }

        this.funcCustomizedLayoutSV.deleteByPageId(pageId);
        if(!CollectionUtils.isEmpty(customizedLayouts)) {
            for(FuncCustomizedLayout customizedLayout : customizedLayouts) {
                this.funcCustomizedLayoutSV.saveFuncCustomizedLayout(customizedLayout, null, subTemplateComposite);
            }
            return customizedLayouts.size();
        }else {
            return 0;
        }
        
    }

    public boolean copyFuncCustomizedPageFromOther(String fromFuncId, String fromPageId, String targetFuncId, String targetPageId) throws EngineException  {
        //加载源页面设计信息
        FuncCustomizedPage fromPage = this.loadFuncCustomizedPageAll(fromPageId);

        //删除目标页面原布局信息
        this.funcCustomizedLayoutSV.deleteAllByPageId(targetPageId);

        if(!CollectionUtils.isEmpty(fromPage.getCustomizedLayouts())) {
            for(FuncCustomizedLayout customizedLayout : fromPage.getCustomizedLayouts()) {
                this.funcCustomizedLayoutSV.copyFuncCustomizedLayout(customizedLayout, null, targetFuncId, targetPageId);
            }
        }

        return true;
    }

    public List<FuncCustomizedLayout> getCustomizedLayouts() {
        return customizedLayouts;
    }

    public void setCustomizedLayouts(List<FuncCustomizedLayout> customizedLayouts) {
        this.customizedLayouts = customizedLayouts;
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

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    public String getPageCfg() {
        return pageCfg;
    }

    public void setPageCfg(String pageCfg) {
        this.pageCfg = pageCfg == null ? null : pageCfg.trim();
    }

    public String getIsMainPage() {
        return isMainPage;
    }

    public void setIsMainPage(String isMainPage) {
        this.isMainPage = isMainPage == null ? null : isMainPage.trim();
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
}