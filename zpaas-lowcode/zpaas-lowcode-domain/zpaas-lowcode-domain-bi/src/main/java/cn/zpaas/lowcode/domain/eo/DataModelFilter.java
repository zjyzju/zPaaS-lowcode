package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DataModelFilterMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModelFilter {
    public static final Logger logger = LoggerFactory.getLogger(DataModelFilter.class);

    private String id;

    private String code;

    private String name;

    private String dataModelId;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<DataModelFilterValue> filterValues;

    @Autowired
    private DataModelFilterMapper mapper;

    @Autowired
    private DataModelFilterValue dataModelFilterValueSV;

    public DataModelFilter byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public DataModelFilter loadWithValues(String id) {
        DataModelFilter dataModelFilter = this.byId(id);
        if(dataModelFilter != null) {
            dataModelFilter.setFilterValues(dataModelFilterValueSV.list(id));
        }
        return dataModelFilter;
    }

    public List<DataModelFilter> list(String dataModelId) {
        DataModelFilterExample criteria = new DataModelFilterExample();
        criteria.createCriteria().andDataModelIdEqualTo(dataModelId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelFilter> listBySystem(String systemId) {
        DataModelFilterExample criteria = new DataModelFilterExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelFilter> list() {
        DataModelFilterExample criteria = new DataModelFilterExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public int add(DataModelFilter dataModelFilter) throws EngineException{
        if(StringUtils.isBlank(dataModelFilter.getId())) {
            dataModelFilter.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataModelFilter.setCreateTime(nowDate);
        dataModelFilter.setUpdateTime(nowDate);
        dataModelFilter.setStatus(Status.EFF);
        int result = this.mapper.insert(dataModelFilter);
        if(result > 0) {
            if(!CollectionUtils.isEmpty(dataModelFilter.getFilterValues())) {
                for(DataModelFilterValue filterValue : dataModelFilter.getFilterValues()) {
                    filterValue.setFilterId(dataModelFilter.getId());
                    if(this.dataModelFilterValueSV.add(filterValue) <= 0) {
                        logger.error("add dataModelFilterValue failed!");
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dataModelFilterValue failed!");
                    }
                 }
            }
        }
        return result;
    }

    public int update(DataModelFilter dataModelFilter) {
        Date nowDate = new Date();
        dataModelFilter.setUpdateTime(nowDate);
        return this.mapper.updateByPrimaryKey(dataModelFilter);
    }

    public int delete(String Id) {
        DataModelFilter dataModelFilter = this.byId(Id);
        if(dataModelFilter != null) {
            Date nowDate = new Date();
            dataModelFilter.setUpdateTime(nowDate);
            dataModelFilter.setStatus(Status.EXP);
            return mapper.updateByPrimaryKey(dataModelFilter);
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

    public List<DataModelFilterValue> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<DataModelFilterValue> filterValues) {
        this.filterValues = filterValues;
    }
}