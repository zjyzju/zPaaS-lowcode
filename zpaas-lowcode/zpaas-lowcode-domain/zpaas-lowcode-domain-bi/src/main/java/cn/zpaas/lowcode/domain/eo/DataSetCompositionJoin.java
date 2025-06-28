package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.mapper.DataSetCompositionJoinMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataSetCompositionJoin {
    public static final String REL_COL_TYPE_COLUMN = "C";//字段
    public static final String REL_COL_TYPE_FIX = "F";//固定值

    public static final String COND_TYPE_EQ = "EQ";//等于
    public static final String COND_TYPE_NE = "NE";//不等于
    public static final String COND_TYPE_GT = "GT";//大于
    public static final String COND_TYPE_GE = "GE";//大于等于
    public static final String COND_TYPE_LT = "LT";//小于
    public static final String COND_TYPE_LE = "LE";//小于等于
    public static final String JOIN_TYPE_INNER = "I";//内连接
    public static final String JOIN_TYPE_LEFT = "L";//左连接
    public static final String JOIN_TYPE_RIGHT = "R";//右连接

    public static final String COND_TYPE_EQ_MYSQL = "=";//等于
    public static final String COND_TYPE_NE_MYSQL = "!=";//不等于
    public static final String COND_TYPE_GT_MYSQL = ">";//大于
    public static final String COND_TYPE_GE_MYSQL = ">=";//大于等于
    public static final String COND_TYPE_LT_MYSQL = "<";//小于
    public static final String COND_TYPE_LE_MYSQL = "<=";//小于等于

    public static String transferCondTypeMysql(String condType) {
        switch (condType) {
            case COND_TYPE_EQ:
                return COND_TYPE_EQ_MYSQL;
            case COND_TYPE_NE:
                return COND_TYPE_NE_MYSQL;
            case COND_TYPE_GT:
                return COND_TYPE_GT_MYSQL;
            case COND_TYPE_GE:
                return COND_TYPE_GE_MYSQL;
            case COND_TYPE_LT:
                return COND_TYPE_LT_MYSQL;
            case COND_TYPE_LE:
                return COND_TYPE_LE_MYSQL;
            default:
                return null;
        }
    }


    private String id;

    private String dataSetId;

    private String joinMainCompoId;

    private String mainColId;

    private String condType;

    private String joinType;

    private String joinRelDataModelId;

    private String relColType;

    private String relColId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DataSetCompositionJoinMapper mapper;

    public DataSetCompositionJoin byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<DataSetCompositionJoin> list(String compositionId) {
        DataSetCompositionJoinExample criteria = new DataSetCompositionJoinExample();
        criteria.createCriteria().andJoinMainCompoIdEqualTo(compositionId);
        return mapper.selectByExample(criteria);
    }

    public List<DataSetCompositionJoin> listByDataSet(String dataSetId) {
        DataSetCompositionJoinExample criteria = new DataSetCompositionJoinExample();
        criteria.createCriteria().andDataSetIdEqualTo(dataSetId);
        return mapper.selectByExample(criteria);
    }

    public List<DataSetCompositionJoin> listBySystem(String systemId) {
        DataSetCompositionJoinExample criteria = new DataSetCompositionJoinExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return mapper.selectByExample(criteria);
    }

    public List<DataSetCompositionJoin> list() {
        return mapper.selectByExample(null);
    }

    public int add(DataSetCompositionJoin dataSetCompositionJoin) {
        if(StringUtils.isBlank(dataSetCompositionJoin.getId())) {
            dataSetCompositionJoin.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataSetCompositionJoin.setCreateTime(nowDate);
        dataSetCompositionJoin.setUpdateTime(nowDate);
        return this.mapper.insert(dataSetCompositionJoin);
    }

    public int delete(String id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public int save(DataSetCompositionJoin dataSetCompositionJoin) {
        dataSetCompositionJoin.setUpdateTime(new Date());
        return this.mapper.updateByPrimaryKey(dataSetCompositionJoin);
    }

    public int deleteByComposition(String dataModelCompositionId) {
        DataSetCompositionJoinExample criteria = new DataSetCompositionJoinExample();
        criteria.createCriteria().andJoinMainCompoIdEqualTo(dataModelCompositionId);
        return this.mapper.deleteByExample(criteria);
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
        this.dataSetId = dataSetId;
    }

    public String getJoinMainCompoId() {
        return joinMainCompoId;
    }

    public void setJoinMainCompoId(String joinMainCompoId) {
        this.joinMainCompoId = joinMainCompoId == null ? null : joinMainCompoId.trim();
    }

    public String getMainColId() {
        return mainColId;
    }

    public void setMainColId(String mainColId) {
        this.mainColId = mainColId == null ? null : mainColId.trim();
    }

    public String getCondType() {
        return condType;
    }

    public void setCondType(String condType) {
        this.condType = condType == null ? null : condType.trim();
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getJoinRelDataModelId() {
        return joinRelDataModelId;
    }

    public void setJoinRelDataModelId(String joinRelDataModelId) {
        this.joinRelDataModelId = joinRelDataModelId;
    }

    public String getRelColType() {
        return relColType;
    }

    public void setRelColType(String relColType) {
        this.relColType = relColType == null ? null : relColType.trim();
    }

    public String getRelColId() {
        return relColId;
    }

    public void setRelColId(String relColId) {
        this.relColId = relColId == null ? null : relColId.trim();
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