package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.domain.mapper.AttributeColumnMappingMapper;

/**
 * 属性字段映射领域对象
 *
 * @author zjy
 * createTime 2025年04月6日-下午10:39:40
 */
@Repository
public class AttributeColumnMapping {
    private static Logger logger = LoggerFactory.getLogger(AttributeColumnMapping.class);

    private String id;

    private String fileObjectMappingId;

    private String attributeId;

    private String columnIndex;

    private String headerName;

    private String needMerge;

    private String isSum;

    private String sumByColumnIndex;

    private String sumColumnIndex;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private String attributeCode;

    

    @Autowired
    private AttributeColumnMappingMapper mapper;

    public List<AttributeColumnMapping> listByFileObjectMappingId(String fileObjectMappingId) {
        AttributeColumnMappingExample criteria = new AttributeColumnMappingExample();
        criteria.createCriteria().andFileObjectMappingIdEqualTo(fileObjectMappingId);
        return mapper.selectByExample(criteria);
    }

    public List<AttributeColumnMapping> list(String systemId) {
        AttributeColumnMappingExample criteria = new AttributeColumnMappingExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExample(criteria);
    }

    public List<AttributeColumnMapping> list() {
        return mapper.selectByExample(null);
    }

    public boolean deleteAdd(String fileObjectMappingId, List<AttributeColumnMapping> attributeColumnMappings) {
        AttributeColumnMappingExample criteria = new AttributeColumnMappingExample();
        criteria.createCriteria().andFileObjectMappingIdEqualTo(fileObjectMappingId);
        mapper.deleteByExample(criteria);

        if(!CollectionUtils.isEmpty(attributeColumnMappings)) {
            for(AttributeColumnMapping attributeColumnMapping : attributeColumnMappings) {
                attributeColumnMapping.setFileObjectMappingId(fileObjectMappingId);
                if(this.add(attributeColumnMapping) == null) {
                    logger.error("add attributeColumnMapping failed!");
                    return false;
                }
            }
        }
        return true;
    }

    public AttributeColumnMapping add(AttributeColumnMapping attributeColumnMapping) {
        if(StringUtils.isBlank(attributeColumnMapping.getId())) {
            attributeColumnMapping.setId(UUID.uuidKey());
        }
        Date nowDate = new Date();
        attributeColumnMapping.setCreateTime(nowDate);
        attributeColumnMapping.setUpdateTime(nowDate);
        if(mapper.insert(attributeColumnMapping) > 0) {
            return attributeColumnMapping;
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

    public String getFileObjectMappingId() {
        return fileObjectMappingId;
    }

    public void setFileObjectMappingId(String fileObjectMappingId) {
        this.fileObjectMappingId = fileObjectMappingId == null ? null : fileObjectMappingId.trim();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public String getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex == null ? null : columnIndex.trim();
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName == null ? null : headerName.trim();
    }

    public String getNeedMerge() {
        return needMerge;
    }

    public void setNeedMerge(String needMerge) {
        this.needMerge = needMerge == null ? null : needMerge.trim();
    }

    public String getIsSum() {
        return isSum;
    }

    public void setIsSum(String isSum) {
        this.isSum = isSum == null ? null : isSum.trim();
    }

    public String getSumByColumnIndex() {
        return sumByColumnIndex;
    }

    public void setSumByColumnIndex(String sumByColumnIndex) {
        this.sumByColumnIndex = sumByColumnIndex == null ? null : sumByColumnIndex.trim();
    }

    public String getSumColumnIndex() {
        return sumColumnIndex;
    }

    public void setSumColumnIndex(String sumColumnIndex) {
        this.sumColumnIndex = sumColumnIndex == null ? null : sumColumnIndex.trim();
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

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }
}