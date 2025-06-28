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
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.FileObjectMappingMapper;

@Repository
public class FileObjectMapping {
    private String id;

    private String name;

    private String status;

    private String objectType;

    private String objectId;

    private String mappingDirect;

    private String fileDesc;

    private String fileType;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<AttributeColumnMapping> attributeColumnMappings;

    private String objectCode;

   

    @Autowired
    private FileObjectMappingMapper mapper;

    @Autowired
    private AttributeColumnMapping attributeColumnMappingSV;

    public FileObjectMapping byId(String fileObjectMappingId) {
        return mapper.selectByPrimaryKey(fileObjectMappingId);
    }

    public FileObjectMapping add(FileObjectMapping fileObjectMapping) {
        if(StringUtils.isBlank(fileObjectMapping.getId())) {
            fileObjectMapping.setId(UUID.uuidKey());
        }
        Date nowDate = new Date();
        fileObjectMapping.setCreateTime(nowDate);
        fileObjectMapping.setUpdateTime(nowDate);
        fileObjectMapping.setStatus(Status.EFF);
        if(mapper.insert(fileObjectMapping) > 0) {
            if(!attributeColumnMappingSV.deleteAdd(fileObjectMapping.getId(), fileObjectMapping.getAttributeColumnMappings())) {
                return null;
            }
            return fileObjectMapping;
        }else {
            return null;
        }
    }

    public FileObjectMapping save(FileObjectMapping fileObjectMapping) {
        Date nowDate = new Date();
        fileObjectMapping.setUpdateTime(nowDate);
        if(mapper.updateByPrimaryKey(fileObjectMapping) > 0) {
            if(!attributeColumnMappingSV.deleteAdd(fileObjectMapping.getId(), fileObjectMapping.getAttributeColumnMappings())) {
                return null;
            }
            return fileObjectMapping;
        }else {
            return null;
        }
    }

    public int delete(String fileObjectMappingId) {
        FileObjectMapping fileObjectMapping = this.byId(fileObjectMappingId);
        if(fileObjectMapping == null) {
            return 0;
        }
        fileObjectMapping.setStatus(Status.EXP);
        fileObjectMapping.setUpdateTime(new Date());
        return mapper.updateByPrimaryKey(fileObjectMapping);
    }

    public List<FileObjectMapping> list(String systemId) {
        FileObjectMappingExample criteria = new FileObjectMappingExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        List<FileObjectMapping> fileObjectMappings = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(fileObjectMappings)) {
            List<AttributeColumnMapping> attributeColumnMappings = attributeColumnMappingSV.list(systemId);
            if(!CollectionUtils.isEmpty(attributeColumnMappings)) {
                Map<String, List<AttributeColumnMapping>> map = attributeColumnMappings.stream().collect(Collectors.groupingBy(AttributeColumnMapping::getFileObjectMappingId));
                fileObjectMappings.forEach(fileObjectMapping->fileObjectMapping.setAttributeColumnMappings(map.get(fileObjectMapping.getId())));
            }
        }
        return fileObjectMappings;
    }

    public List<FileObjectMapping> list() {
        FileObjectMappingExample criteria = new FileObjectMappingExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        List<FileObjectMapping> fileObjectMappings = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(fileObjectMappings)) {
            List<AttributeColumnMapping> attributeColumnMappings = attributeColumnMappingSV.list();
            if(!CollectionUtils.isEmpty(attributeColumnMappings)) {
                Map<String, List<AttributeColumnMapping>> map = attributeColumnMappings.stream().collect(Collectors.groupingBy(AttributeColumnMapping::getFileObjectMappingId));
                fileObjectMappings.forEach(fileObjectMapping->fileObjectMapping.setAttributeColumnMappings(map.get(fileObjectMapping.getId())));
            }
        }
        return fileObjectMappings;
    }

    public List<FileObjectMapping> queryByObject(String objectType, String objectId) {
        FileObjectMappingExample criteria = new FileObjectMappingExample();
        criteria.createCriteria().andObjectTypeEqualTo(objectType).andObjectIdEqualTo(objectId).andStatusEqualTo(Status.EFF);
        List<FileObjectMapping> fileObjectMappings = mapper.selectByExample(criteria);
        if(!CollectionUtils.isEmpty(fileObjectMappings)) {
            fileObjectMappings.forEach(fileObjectMapping->fileObjectMapping.setAttributeColumnMappings(attributeColumnMappingSV.listByFileObjectMappingId(fileObjectMapping.getId())));
        }
        return fileObjectMappings;
    }

    public List<AttributeColumnMapping> getAttributeColumnMappings() {
        return attributeColumnMappings;
    }

    public void setAttributeColumnMappings(List<AttributeColumnMapping> attributeColumnMappings) {
        this.attributeColumnMappings = attributeColumnMappings;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType == null ? null : objectType.trim();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId == null ? null : objectId.trim();
    }

    public String getMappingDirect() {
        return mappingDirect;
    }

    public void setMappingDirect(String mappingDirect) {
        this.mappingDirect = mappingDirect == null ? null : mappingDirect.trim();
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc == null ? null : fileDesc.trim();
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
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

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }
}