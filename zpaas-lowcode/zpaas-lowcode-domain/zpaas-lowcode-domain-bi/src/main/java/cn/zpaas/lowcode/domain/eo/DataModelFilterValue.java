package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.DataModelFilterValueMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModelFilterValue {
    private String id;

    private String code;

    private String name;

    private String filterId;

    private String filterFormula;

    private String description;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DataModelFilterValueMapper mapper;

    public DataModelFilterValue byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<DataModelFilterValue> list(String dataModelFilterId) {
        DataModelFilterValueExample criteria = new DataModelFilterValueExample();
        criteria.createCriteria().andFilterIdEqualTo(dataModelFilterId);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelFilterValue> list() {
        return mapper.selectByExample(null);
    }

    public List<DataModelFilterValue> listBySystem(String systemId) {
        DataModelFilterValueExample criteria = new DataModelFilterValueExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExample(criteria);
    }

    public int add(DataModelFilterValue dataModelFilterValue) {
        if(StringUtils.isBlank(dataModelFilterValue.getId())) {
            dataModelFilterValue.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataModelFilterValue.setCreateTime(nowDate);
        dataModelFilterValue.setUpdateTime(nowDate);
        return this.mapper.insert(dataModelFilterValue);
    }

    public int update(DataModelFilterValue dataModelFilterValue) {
        Date nowDate = new Date();
        dataModelFilterValue.setUpdateTime(nowDate);
        return this.mapper.updateByPrimaryKey(dataModelFilterValue);
    }

    public int delete(String Id) {
        return mapper.deleteByPrimaryKey(Id);
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

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId == null ? null : filterId.trim();
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