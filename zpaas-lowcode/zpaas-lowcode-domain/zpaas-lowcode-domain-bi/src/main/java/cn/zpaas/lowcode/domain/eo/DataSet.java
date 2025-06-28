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
import cn.zpaas.lowcode.domain.mapper.DataSetMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataSet {
    public static Logger logger = LoggerFactory.getLogger(DataSet.class);
    private String id;

    private String code;

    private String name;

    private String domainId;

    private String description;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private List<DataSetDetail> details;

    private List<DataSetComposition> compositions;
    

    @Autowired
    private DataSetMapper mapper;

    @Autowired
    private DataSetDetail dataSetDetailSV;

    @Autowired
    private DataSetComposition dataSetCompositionSV;

    @Autowired
    private DataModelColumn dataModelColumnSV;

    @Autowired
    private DataModelMetrics dataModelMetricsSV;

    @Autowired 
    private DataModelTag dataModelTagSV;

    @Autowired
    private DataModelFilter dataModelFilterSV;

    @Autowired
    private DataModel dataModelSV;

    public DataSet byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public DataSet loadAll(String id) {
        DataSet dataSet = this.byId(id);
        dataSet.setDetails(this.dataSetDetailSV.list(id));
        dataSet.setCompositions(this.dataSetCompositionSV.loadWithJoin(id));
        if(!CollectionUtils.isEmpty(dataSet.getDetails())) {
            for(DataSetDetail detail : dataSet.getDetails()) {
                this.loadContent(detail);
            }
        }
        if(!CollectionUtils.isEmpty(dataSet.getCompositions())) {
            for(DataSetComposition composition : dataSet.getCompositions()) {
                composition.setDataModel(this.dataModelSV.byId(composition.getDataModelId()));
            }
        }
        return dataSet;
    }

    public DataSet loadWithDetails(String id) {
        DataSet dataSet = this.byId(id);
        dataSet.setDetails(this.dataSetDetailSV.list(id));
        if(!CollectionUtils.isEmpty(dataSet.getDetails())) {
            for(DataSetDetail detail : dataSet.getDetails()) {
                this.loadContent(detail);
            }
        }
        return dataSet;
    }

    public List<DataSetDetail> listDetails(String dataSetId) {
        List<DataSetDetail> details = this.dataSetDetailSV.list(dataSetId);
        if(!CollectionUtils.isEmpty(details)) {
            for(DataSetDetail detail : details) {
                this.loadContent(detail);
            }
        }
        return details;
    }

    public DataSetDetail loadDetail(String dataSetDetailId) {
        DataSetDetail detail = this.dataSetDetailSV.byId(dataSetDetailId);
        if(detail != null) {
            this.loadContent(detail);
        }
        return detail;
    }

    private void loadContent(DataSetDetail detail) {
        if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType())) {
            detail.setContent(this.dataModelMetricsSV.byId(detail.getDetailContentId()));
        }else if(DataSetDetail.DETAIL_TYPE_TAG.equals(detail.getDetailType())) {
            detail.setContent(this.dataModelTagSV.byId(detail.getDetailContentId()));
        }else if(DataSetDetail.DETAIL_TYPE_FILTER.equals(detail.getDetailType())) {
            detail.setContent(this.dataModelFilterSV.loadWithValues(detail.getDetailContentId()));
        }else {
            detail.setContent(this.dataModelColumnSV.byId(detail.getDetailContentId()));
        }
    }

    public List<DataSet> list(String systemId) {
        DataSetExample criteria = new DataSetExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataSet> list() {
        DataSetExample criteria = new DataSetExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("system_id, code");
        return mapper.selectByExample(criteria);
    }

    public int add(DataSet dataSet) {
        if(StringUtils.isBlank(dataSet.getId())) {
            dataSet.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataSet.setCreateTime(nowDate);
        dataSet.setUpdateTime(nowDate);
        dataSet.setStatus(Status.EFF);
        return this.mapper.insert(dataSet);
    }

    public int save(DataSet dataSet) throws EngineException{
        Date nowDate = new Date();
        dataSet.setUpdateTime(nowDate);
        int result = this.mapper.updateByPrimaryKey(dataSet);

        if(result > 0) {
            dataSetDetailSV.deleteByDataSet(dataSet.getId());
            if(!CollectionUtils.isEmpty(dataSet.getDetails())) {
                for(DataSetDetail detail : dataSet.getDetails()) {
                    detail.setDataSetId(dataSet.getId());
                    if(dataSetDetailSV.add(detail)<=0) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dataSetDetail failed!");
                    }
                    if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType())) {
                        detail.setContent(this.dataModelMetricsSV.byId(detail.getDetailContentId()));
                    }else if(DataSetDetail.DETAIL_TYPE_TAG.equals(detail.getDetailType())) {
                        detail.setContent(this.dataModelTagSV.byId(detail.getDetailContentId()));
                    }else if(DataSetDetail.DETAIL_TYPE_FILTER.equals(detail.getDetailType())) {
                        detail.setContent(this.dataModelFilterSV.byId(detail.getDetailContentId()));
                    }else {
                        detail.setContent(this.dataModelColumnSV.byId(detail.getDetailContentId()));
                    }
                }
            }
            dataSetCompositionSV.deleteByDataSet(dataSet.getId());
            if(!CollectionUtils.isEmpty(dataSet.getCompositions())) {
                for(DataSetComposition composition : dataSet.getCompositions()) {
                    composition.setDataSetId(dataSet.getId());
                    if(dataSetCompositionSV.add(composition) <=0 ) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dataSetComposition failed!");
                    }
                }
            }
        }

        return result;
    }

    public int delete(String Id) {
        DataSet dataSet = this.byId(Id);
        if(dataSet != null) {
            Date nowDate = new Date();
            dataSet.setUpdateTime(nowDate);
            dataSet.setStatus(Status.EXP);
            return mapper.updateByPrimaryKey(dataSet);
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

    public List<DataSetDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DataSetDetail> details) {
        this.details = details;
    }

    public List<DataSetComposition> getCompositions() {
        return compositions;
    }

    public void setCompositions(List<DataSetComposition> compositions) {
        this.compositions = compositions;
    }
}