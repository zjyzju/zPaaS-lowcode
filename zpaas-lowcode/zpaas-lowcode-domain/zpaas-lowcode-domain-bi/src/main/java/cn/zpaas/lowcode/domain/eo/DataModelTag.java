package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DataModelTagMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModelTag {
    private String id;

    private String code;

    private String name;

    private String dataModelId;

    private String filterFormula;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DataModelTagMapper mapper;

    public DataModelTag byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<DataModelTag> list(String dataModelId) {
        DataModelTagExample criteria = new DataModelTagExample();
        criteria.createCriteria().andDataModelIdEqualTo(dataModelId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelTag> listBySystem(String systemId) {
        DataModelTagExample criteria = new DataModelTagExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelTag> list() {
        DataModelTagExample criteria = new DataModelTagExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public int add(DataModelTag dataModelTag) {
        if(StringUtils.isBlank(dataModelTag.getId())) {
            dataModelTag.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataModelTag.setCreateTime(nowDate);
        dataModelTag.setUpdateTime(nowDate);
        dataModelTag.setStatus(Status.EFF);
        return this.mapper.insert(dataModelTag);
    }

    public int update(DataModelTag dataModelTag) {
        Date nowDate = new Date();
        dataModelTag.setUpdateTime(nowDate);
        return this.mapper.updateByPrimaryKey(dataModelTag);
    }

    public int delete(String Id) {
        DataModelTag dataModelTag = this.byId(Id);
        if(dataModelTag != null) {
            Date nowDate = new Date();
            dataModelTag.setUpdateTime(nowDate);
            dataModelTag.setStatus(Status.EXP);
            return mapper.updateByPrimaryKey(dataModelTag);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId == null ? null : dataModelId.trim();
    }

    public String getFilterFormula() {
        return filterFormula;
    }

    public void setFilterFormula(String filterFormula) {
        this.filterFormula = filterFormula == null ? null : filterFormula.trim();
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