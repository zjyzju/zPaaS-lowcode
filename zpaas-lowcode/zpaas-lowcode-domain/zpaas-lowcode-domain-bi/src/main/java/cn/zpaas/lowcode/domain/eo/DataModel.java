package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DataModelMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModel {
    public static final String DATA_MODEL_TYPE_TABLE = "T";//物理表
    public static final String DATA_MODEL_TYPE_SQL = "S";//SQL
    public static final String DATA_MODEL_TYPE_FILE = "F";//文件

    public static final String DATA_MODEL_DATA_SOURCE_TYPE_RDBMS = "R";//关系数据库
    public static final String DATA_MODEL_DATA_SOURCE_TYPE_ES = "E";//ElasticSearch
    public static final String DATA_MODEL_DATA_SOURCE_TYPE_CK = "C";//ClickHouse

    private String id;

    private String code;

    private String name;

    private String type;

    private String dataSourceType;

    private String dataSourceId;

    private String physicalSrcObject;

    private String srcObjectDesc;

    private String domainId;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<DataModelColumn> columns;

    private List<DataModelMetrics> metrics;

    private List<DataModelTag> tags;

    private List<DataModelFilter> filters;

    @Autowired
    private DataModelMapper mapper;

    @Autowired
    private DataModelColumn dataModelColumnSV;

    @Autowired
    private DataModelMetrics dataModelMetricsSV;

    @Autowired
    private DataModelTag dataModelTagSV;

    @Autowired
    private DataModelFilter dataModelFilterSV;

    public DataModel byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int add(DataModel dataModel) {
        dataModel.setId(KeyGenerate.uuidKey());
        Date nowDate = new Date();
        dataModel.setCreateTime(nowDate);
        dataModel.setUpdateTime(nowDate);
        dataModel.setStatus(Status.EFF);
        return mapper.insert(dataModel);
    }

    public int save(DataModel dataModel) throws EngineException{
        dataModel.setUpdateTime(new Date());
        int result = mapper.updateByPrimaryKey(dataModel);
        if(result <= 0) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "save dataModel failed!");
        }

        if(!CollectionUtils.isEmpty(dataModel.getColumns())) {
            for(DataModelColumn column : dataModel.getColumns()) {
                if(!StringUtils.isBlank(column.getId())) {//已有字段
                    if(!StringUtils.isBlank(column.getColumnType())) {//设置了字段类型
                        column.setDataModelId(dataModel.getId());
                        if(dataModelColumnSV.update(column)<=0) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "update dataModelColumn failed!");
                        }
                    }else {//未设置字段类型，表示要作废
                        if(dataModelColumnSV.delete(column.getId())<=0) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "delete dataModelColumn failed!");
                        }
                    }
                }else {//新设置字段
                    if(!StringUtils.isBlank(column.getColumnType())) {//设置了字段类型
                        column.setDataModelId(dataModel.getId());
                        if(dataModelColumnSV.add(column)<=0) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dataModelColumn failed!");
                        }
                    }else {//未设置字段类型，不需要处理
                        
                    }
                }
            }
        }
        return result;
    }

    public List<DataModel> list(String systemId) {
        DataModelExample criteria = new DataModelExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        return mapper.selectByExample(criteria);
    }

    public List<DataModel> list() {
        DataModelExample criteria = new DataModelExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        return mapper.selectByExample(criteria);
    }

    public int delete(String dataModelId) {
        DataModel dataModel = this.byId(dataModelId);
        if(dataModel != null) {
            dataModel.setStatus(Status.EXP);
            dataModel.setUpdateTime(new Date());
            return this.mapper.updateByPrimaryKey(dataModel);
        }else {
            return 0;
        }
    }

    public DataModel loadWithColumn(String id) {
        DataModel dataModel = this.byId(id);
        if(dataModel == null || !Status.EFF.equals(dataModel.getStatus())) {
            return null;
        }
        dataModel.setColumns(this.dataModelColumnSV.list(id));
        
        return dataModel;
    }

    public DataModel loadAll(String id) {
        DataModel dataModel = this.byId(id);
        if(dataModel == null || !Status.EFF.equals(dataModel.getStatus())) {
            return null;
        }
        dataModel.setColumns(this.dataModelColumnSV.list(id));
        dataModel.setMetrics(this.dataModelMetricsSV.list(id));
        dataModel.setTags(this.dataModelTagSV.list(id));
        dataModel.setFilters(this.dataModelFilterSV.list(id));

        return dataModel;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType == null ? null : dataSourceType.trim();
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId == null ? null : dataSourceId.trim();
    }

    public String getPhysicalSrcObject() {
        return physicalSrcObject;
    }

    public void setPhysicalSrcObject(String physicalSrcObject) {
        this.physicalSrcObject = physicalSrcObject == null ? null : physicalSrcObject.trim();
    }

    public String getSrcObjectDesc() {
        return srcObjectDesc;
    }

    public void setSrcObjectDesc(String srcObjectDesc) {
        this.srcObjectDesc = srcObjectDesc == null ? null : srcObjectDesc.trim();
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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

    public List<DataModelColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataModelColumn> columns) {
        this.columns = columns;
    }

    public List<DataModelMetrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<DataModelMetrics> metrics) {
        this.metrics = metrics;
    }

    public List<DataModelTag> getTags() {
        return tags;
    }

    public void setTags(List<DataModelTag> tags) {
        this.tags = tags;
    }

    public List<DataModelFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<DataModelFilter> filters) {
        this.filters = filters;
    }
}