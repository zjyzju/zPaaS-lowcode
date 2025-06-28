package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.mapper.FuncCustomizedLayoutRegionMapper;
import cn.zpaas.lowcode.exception.EngineException;

@Repository
public class FuncCustomizedLayoutRegion {
    private String id;

    private String funcId;

    private String pageId;

    private String layoutId;

    private String regionId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private FuncRegion funcRegion;

    @Autowired
    private FuncCustomizedLayoutRegionMapper mapper;

    @Autowired
    private FuncRegion funcRegionSV;

    public List<FuncCustomizedLayoutRegion> list() {
        return mapper.selectByExample(null);
    }

    public List<FuncCustomizedLayoutRegion> listBySystem(String systemId) {
        FuncCustomizedLayoutRegionExample criteria = new FuncCustomizedLayoutRegionExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExample(criteria);
    }

    public List<FuncCustomizedLayoutRegion> listByFuncId(String funcId) {
        FuncCustomizedLayoutRegionExample criteria = new FuncCustomizedLayoutRegionExample();
        criteria.createCriteria().andFuncIdEqualTo(funcId);
        List<FuncCustomizedLayoutRegion> layoutRegions = mapper.selectByExample(criteria);
        for(FuncCustomizedLayoutRegion layoutRegion : layoutRegions) {
            layoutRegion.setFuncRegion(this.funcRegionSV.loadAllById(layoutRegion.getRegionId()));
        }
        return layoutRegions;
    }

    public List<FuncCustomizedLayoutRegion> listByPageId(String pageId) {
        FuncCustomizedLayoutRegionExample criteria = new FuncCustomizedLayoutRegionExample();
        criteria.createCriteria().andPageIdEqualTo(pageId);
        List<FuncCustomizedLayoutRegion> layoutRegions = mapper.selectByExample(criteria);
        for(FuncCustomizedLayoutRegion layoutRegion : layoutRegions) {
            layoutRegion.setFuncRegion(this.funcRegionSV.loadAllById(layoutRegion.getRegionId()));
        }
        return layoutRegions;
    }

    public List<FuncCustomizedLayoutRegion> listByLayoutId(String layoutId) {
        FuncCustomizedLayoutRegionExample criteria = new FuncCustomizedLayoutRegionExample();
        criteria.createCriteria().andLayoutIdEqualTo(layoutId);
        return mapper.selectByExample(criteria);
    }

    public int deleteById(String layoutRegionId) {
        FuncCustomizedLayoutRegion layoutRegion = this.mapper.selectByPrimaryKey(layoutRegionId);
        if(layoutRegion != null) {
            this.funcRegionSV.deleteAllById(layoutRegion.getFuncId(), layoutRegion.getRegionId());
        }
        return this.mapper.deleteByPrimaryKey(layoutRegionId);
    }

    public int saveFuncCustomizedLayoutRegion(FuncCustomizedLayoutRegion layoutRegion, FuncTemplateComposite subTemplateComposite) throws EngineException{
        if(layoutRegion.getFuncRegion() != null) {
            if(StringUtils.isBlank(layoutRegion.getFuncRegion().getName())) {
                layoutRegion.getFuncRegion().setName("region of layout-" + layoutRegion.getLayoutId());
            }
            layoutRegion.getFuncRegion().setTplCompositeId(subTemplateComposite.getId());
            layoutRegion.getFuncRegion().setTplRegionId(subTemplateComposite.getTplRegionId());
            this.funcRegionSV.saveFuncRegion(layoutRegion.getFuncRegion());
        }

        Date nowDate = new Date();
        if(StringUtils.isBlank(layoutRegion.getId())) {
            layoutRegion.setId(UUID.uuidKey());
            layoutRegion.setCreateTime(nowDate);
        }
        layoutRegion.setUpdateTime(nowDate);
        layoutRegion.setRegionId(layoutRegion.getFuncRegion().getId());
        if(this.mapper.insert(layoutRegion)<=0) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcCustomizedLayoutRegion failed!");
        }
        return 1;
    }

    public int copyFuncCustomizedLayoutRegion(FuncCustomizedLayoutRegion layoutRegion, String targetFuncId, String targetPageId) throws EngineException{
        if(layoutRegion.getFuncRegion() != null) {
            layoutRegion.getFuncRegion().setFuncId(targetFuncId);;
            layoutRegion.getFuncRegion().setId(null);
            if(!CollectionUtils.isEmpty(layoutRegion.getFuncRegion().getRegionAttrAssigns())) {
                for(FuncRegionAttrAssign attrAssign : layoutRegion.getFuncRegion().getRegionAttrAssigns()) {
                    attrAssign.setId(null);
                }
            }
            if(!CollectionUtils.isEmpty(layoutRegion.getFuncRegion().getRegionOperations())) {
                for(FuncRegionOperation operation : layoutRegion.getFuncRegion().getRegionOperations()) {
                    operation.setId(null);
                }
            }
            this.funcRegionSV.saveFuncRegion(layoutRegion.getFuncRegion());
            layoutRegion.setRegionId(layoutRegion.getFuncRegion().getId());
        }
        layoutRegion.setFuncId(targetFuncId);
        layoutRegion.setPageId(targetPageId);
        Date nowDate = new Date();
        layoutRegion.setId(UUID.uuidKey());
        layoutRegion.setCreateTime(nowDate);
        layoutRegion.setUpdateTime(nowDate);
        
        if(this.mapper.insert(layoutRegion)<=0) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcCustomizedLayoutRegion failed!");
        }
        return 1;
    }

    public FuncRegion getFuncRegion() {
        return funcRegion;
    }

    public void setFuncRegion(FuncRegion funcRegion) {
        this.funcRegion = funcRegion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
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

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId == null ? null : layoutId.trim();
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId == null ? null : regionId.trim();
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