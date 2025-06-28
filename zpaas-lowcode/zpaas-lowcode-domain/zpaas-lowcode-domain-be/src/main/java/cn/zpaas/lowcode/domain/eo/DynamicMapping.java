package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DynamicMappingMapper;

@Repository
public class DynamicMapping {
    private static final String PERCENT_FLAG = "%";
    private String id;

    private String name;

    private String description;

    private String status;

    private String mappingType;

    private String businessType;

    private String subBusinessType;

    private String keyAttribute;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<DynamicMappingDetail> dynamicMappingDetails;

    private Map<String, DynamicMappingDetail> detailMap;

    

    @Autowired
    private DynamicMappingMapper mapper;

    @Autowired
    private DynamicMappingDetail dynamicMappingDetailSV;

    public DynamicMapping byId(String dynamicMappingId) {
        return mapper.selectByPrimaryKey(dynamicMappingId);
    }

    public DynamicMapping loadDynamicMapping(String dynamicMappingId) {
        DynamicMapping dynamicMapping = mapper.selectByPrimaryKey(dynamicMappingId);
        if(dynamicMapping != null) {
            dynamicMapping.setDynamicMappingDetails(dynamicMappingDetailSV.list(dynamicMappingId));
        }
        return dynamicMapping;
    }

    public List<DynamicMapping> queryByCondition(DynamicMapping dynamicMapping) {
        DynamicMappingExample criteria = new DynamicMappingExample();
        DynamicMappingExample.Criteria condition = criteria.createCriteria().andSystemIdEqualTo(dynamicMapping.getSystemId()).andStatusEqualTo(Status.EFF);
        if(!StringUtils.isBlank(dynamicMapping.getMappingType())) {
            condition.andMappingTypeEqualTo(dynamicMapping.getMappingType());
        }
        if(!StringUtils.isBlank(dynamicMapping.getBusinessType())) {
            condition.andBusinessTypeLike(PERCENT_FLAG+dynamicMapping.getBusinessType()+PERCENT_FLAG);
        }
        if(!StringUtils.isBlank(dynamicMapping.getSubBusinessType())) {
            condition.andSubBusinessTypeLike(PERCENT_FLAG+dynamicMapping.getSubBusinessType()+PERCENT_FLAG);
        }
        if(!StringUtils.isBlank(dynamicMapping.getKeyAttribute())) {
            condition.andKeyAttributeLike(PERCENT_FLAG+dynamicMapping.getKeyAttribute()+PERCENT_FLAG);
        }
        return mapper.selectByExample(criteria);
    }

    public DynamicMapping add(DynamicMapping dynamicMapping) {
        if(StringUtils.isBlank(dynamicMapping.getId())) {
            dynamicMapping.setId(UUID.uuidKey());
        }
        Date nowDate = new Date();
        dynamicMapping.setCreateTime(nowDate);
        dynamicMapping.setUpdateTime(nowDate);
        dynamicMapping.setStatus(Status.EFF);
        if(mapper.insert(dynamicMapping) > 0) {
            return dynamicMapping;
        }else {
            return null;
        }
    }

    public List<DynamicMapping> list(String systemId) {
        DynamicMappingExample criteria = new DynamicMappingExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        List<DynamicMapping> dynamicMappings = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(dynamicMappings)) {
            List<DynamicMappingDetail> dynamicMappingDetailList = dynamicMappingDetailSV.listBySystem(systemId);
            if(!CollectionUtils.isEmpty(dynamicMappingDetailList)) {
                Map<String, List<DynamicMappingDetail>> detailMap = dynamicMappingDetailList.stream().collect(Collectors.groupingBy(DynamicMappingDetail::getDynamicMappingId));
                dynamicMappings.forEach(dynamicMapping->{
                    dynamicMapping.setDynamicMappingDetails(detailMap.get(dynamicMapping.getId()));
                    if(!CollectionUtils.isEmpty(dynamicMapping.getDynamicMappingDetails())) {
                        Map<String, DynamicMappingDetail> dynamicMappingDetailMap = new HashMap<>();
                        dynamicMapping.getDynamicMappingDetails().forEach(detail->dynamicMappingDetailMap.put(detail.getKeyAttrValue(), detail));
                        dynamicMapping.setDetailMap(dynamicMappingDetailMap);
                    }
                });
            }
        }
        return dynamicMappings;
    }

    public List<DynamicMapping> list() {
        DynamicMappingExample criteria = new DynamicMappingExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        List<DynamicMapping> dynamicMappings = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(dynamicMappings)) {
            List<DynamicMappingDetail> dynamicMappingDetailList = dynamicMappingDetailSV.list();
            if(!CollectionUtils.isEmpty(dynamicMappingDetailList)) {
                Map<String, List<DynamicMappingDetail>> detailMap = dynamicMappingDetailList.stream().collect(Collectors.groupingBy(DynamicMappingDetail::getDynamicMappingId));
                dynamicMappings.forEach(dynamicMapping->{
                    dynamicMapping.setDynamicMappingDetails(detailMap.get(dynamicMapping.getId()));
                    if(!CollectionUtils.isEmpty(dynamicMapping.getDynamicMappingDetails())) {
                        Map<String, DynamicMappingDetail> dynamicMappingDetailMap = new HashMap<>();
                        dynamicMapping.getDynamicMappingDetails().forEach(detail->dynamicMappingDetailMap.put(detail.getKeyAttrValue(), detail));
                        dynamicMapping.setDetailMap(dynamicMappingDetailMap);
                    }
                });
            }
        }
        return dynamicMappings;
    }

    public DynamicMapping save(DynamicMapping dynamicMapping) {
        Date nowDate = new Date();
        dynamicMapping.setUpdateTime(nowDate);
        if(mapper.updateByPrimaryKey(dynamicMapping) > 0) {
            return dynamicMapping;
        }else {
            return null;
        }
    }

    public int delete(String dynamicMappingId) {
        DynamicMapping dynamicMapping = this.byId(dynamicMappingId);
        if(dynamicMapping != null) {
            dynamicMapping.setStatus(Status.EXP);
            dynamicMapping.setUpdateTime(new Date());
            return mapper.updateByPrimaryKey(dynamicMapping);
        }else {
            return 0;
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

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType == null ? null : mappingType.trim();
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    public String getSubBusinessType() {
        return subBusinessType;
    }

    public void setSubBusinessType(String subBusinessType) {
        this.subBusinessType = subBusinessType == null ? null : subBusinessType.trim();
    }

    public String getKeyAttribute() {
        return keyAttribute;
    }

    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute == null ? null : keyAttribute.trim();
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

    public List<DynamicMappingDetail> getDynamicMappingDetails() {
        return dynamicMappingDetails;
    }

    public void setDynamicMappingDetails(List<DynamicMappingDetail> dynamicMappingDetails) {
        this.dynamicMappingDetails = dynamicMappingDetails;
    }

    public Map<String, DynamicMappingDetail> getDetailMap() {
        return detailMap;
    }

    public void setDetailMap(Map<String, DynamicMappingDetail> detailMap) {
        this.detailMap = detailMap;
    }
}