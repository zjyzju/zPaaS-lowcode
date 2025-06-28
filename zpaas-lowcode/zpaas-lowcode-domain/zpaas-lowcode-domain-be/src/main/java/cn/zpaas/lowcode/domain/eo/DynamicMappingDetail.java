package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.domain.mapper.DynamicMappingDetailMapper;

@Repository
public class DynamicMappingDetail {
    private String id;

    private String dynamicMappingId;

    private String keyAttrValue;

    private String mappingObjectId;

    private String description;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DynamicMappingDetailMapper mapper;

    public DynamicMappingDetail byId(String dynamicMappingDetailId) {
        return mapper.selectByPrimaryKey(dynamicMappingDetailId);
    }

    public DynamicMappingDetail add(DynamicMappingDetail dynamicMappingDetail) {
        if(StringUtils.isBlank(dynamicMappingDetail.getId())) {
            dynamicMappingDetail.setId(UUID.uuidKey());
        }
        Date nowDate = new Date();
        dynamicMappingDetail.setCreateTime(nowDate);
        dynamicMappingDetail.setUpdateTime(nowDate);
        if(mapper.insert(dynamicMappingDetail) > 0) {
            return dynamicMappingDetail;
        }else {
            return null;
        }
    }

    public DynamicMappingDetail save(DynamicMappingDetail dynamicMappingDetail) {
        Date nowDate = new Date();
        dynamicMappingDetail.setUpdateTime(nowDate);
        if(mapper.updateByPrimaryKey(dynamicMappingDetail) > 0) {
            return dynamicMappingDetail;
        }else {
            return null;
        }
    }

    public int delete(String dynamicMappingDetailId) {

        return mapper.deleteByPrimaryKey(dynamicMappingDetailId);
    }

    public List<DynamicMappingDetail> list(String dynamicMappingId) {
        DynamicMappingDetailExample criteria = new DynamicMappingDetailExample();
        criteria.createCriteria().andDynamicMappingIdEqualTo(dynamicMappingId);
        criteria.setOrderByClause("key_attr_value");
        return mapper.selectByExample(criteria);
    }

    public List<DynamicMappingDetail> listBySystem(String systemId) {
        DynamicMappingDetailExample criteria = new DynamicMappingDetailExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        
        return mapper.selectByExample(criteria);
    }

     public List<DynamicMappingDetail> list() {
        return mapper.selectByExample(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDynamicMappingId() {
        return dynamicMappingId;
    }

    public void setDynamicMappingId(String dynamicMappingId) {
        this.dynamicMappingId = dynamicMappingId == null ? null : dynamicMappingId.trim();
    }

    public String getKeyAttrValue() {
        return keyAttrValue;
    }

    public void setKeyAttrValue(String keyAttrValue) {
        this.keyAttrValue = keyAttrValue == null ? null : keyAttrValue.trim();
    }

    public String getMappingObjectId() {
        return mappingObjectId;
    }

    public void setMappingObjectId(String mappingObjectId) {
        this.mappingObjectId = mappingObjectId == null ? null : mappingObjectId.trim();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}