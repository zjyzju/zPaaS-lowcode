package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DataModelMetricsMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModelMetrics {
    private String id;

    private String code;

    private String name;

    private String metricType;

    private String dataModelId;

    private String metricFormula;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DataModelMetricsMapper mapper;

    public DataModelMetrics byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<DataModelMetrics> list(String dataModelId) {
        DataModelMetricsExample criteria = new DataModelMetricsExample();
        criteria.createCriteria().andDataModelIdEqualTo(dataModelId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelMetrics> listBySystem(String systemId) {
        DataModelMetricsExample criteria = new DataModelMetricsExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelMetrics> list() {
        DataModelMetricsExample criteria = new DataModelMetricsExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public int add(DataModelMetrics dataModelMetrics) {
        if(StringUtils.isBlank(dataModelMetrics.getId())) {
            dataModelMetrics.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataModelMetrics.setCreateTime(nowDate);
        dataModelMetrics.setUpdateTime(nowDate);
        dataModelMetrics.setStatus(Status.EFF);
        return this.mapper.insert(dataModelMetrics);
    }

    public int update(DataModelMetrics dataModelMetrics) {
        Date nowDate = new Date();
        dataModelMetrics.setUpdateTime(nowDate);
        return this.mapper.updateByPrimaryKey(dataModelMetrics);
    }

    public int delete(String Id) {
        DataModelMetrics dataModelMetrics = this.byId(Id);
        if(dataModelMetrics != null) {
            Date nowDate = new Date();
            dataModelMetrics.setUpdateTime(nowDate);
            dataModelMetrics.setStatus(Status.EXP);
            return mapper.updateByPrimaryKey(dataModelMetrics);
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

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType == null ? null : metricType.trim();
    }

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId == null ? null : dataModelId.trim();
    }

    public String getMetricFormula() {
        return metricFormula;
    }

    public void setMetricFormula(String metricFormula) {
        this.metricFormula = metricFormula == null ? null : metricFormula.trim();
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