package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.DataSetDetailMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataSetDetail {
    public static final String DETAIL_TYPE_METRICS = "Q";//指标
    public static final String DETAIL_TYPE_TAG = "L";//标签
    public static final String DETAIL_TYPE_FILTER = "F";//筛选器
    public static final String DETAIL_TYPE_DIMENSION = "D";//维度
    public static final String DETAIL_TYPE_MESSUREMENT = "M";//度量
    public static final String DETAIL_TYPE_PRIMARY = "P";//主键
    public static final String DETAIL_TYPE_TIME = "T";//时间
    public static final String DETAIL_TYPE_COMMON = "C";//普通


    private String id;

    private String dataSetId;

    private String detailType;

    private String detailContentId;

    private String detailContentAlias;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private Object content;

    @Autowired
    private DataSetDetailMapper mapper;

    public DataSetDetail byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int add(DataSetDetail dataSetDetail) {
        if(StringUtils.isBlank(dataSetDetail.getId())) {
            dataSetDetail.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataSetDetail.setCreateTime(nowDate);
        dataSetDetail.setUpdateTime(nowDate);
        return this.mapper.insert(dataSetDetail);
    }

    public int delete(String id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public int save(DataSetDetail dataSetDetail) {
        dataSetDetail.setUpdateTime(new Date());
        return this.mapper.updateByPrimaryKey(dataSetDetail);
    }

    public int deleteByDataSet(String dataSetId) {
        DataSetDetailExample criteria = new DataSetDetailExample();
        criteria.createCriteria().andDataSetIdEqualTo(dataSetId);
        return this.mapper.deleteByExample(criteria);
    }

    public List<DataSetDetail> list(String dataSetId) {
        DataSetDetailExample criteria = new DataSetDetailExample();
        criteria.createCriteria().andDataSetIdEqualTo(dataSetId);
        criteria.setOrderByClause("detail_type");
        return this.mapper.selectByExample(criteria);
    }

    public List<DataSetDetail> list() {
        DataSetDetailExample criteria = new DataSetDetailExample();
        criteria.setOrderByClause("system_id, data_set_id");
        return this.mapper.selectByExample(criteria);
    }
    public List<DataSetDetail> listBySystem(String systemId) {
        DataSetDetailExample criteria = new DataSetDetailExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        criteria.setOrderByClause("data_set_id");
        return this.mapper.selectByExample(criteria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId == null ? null : dataSetId.trim();
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType == null ? null : detailType.trim();
    }

    public String getDetailContentId() {
        return detailContentId;
    }

    public void setDetailContentId(String detailContentId) {
        this.detailContentId = detailContentId == null ? null : detailContentId.trim();
    }

    public String getDetailContentAlias() {
        return detailContentAlias;
    }

    public void setDetailContentAlias(String detailContentAlias) {
        this.detailContentAlias = detailContentAlias;
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}