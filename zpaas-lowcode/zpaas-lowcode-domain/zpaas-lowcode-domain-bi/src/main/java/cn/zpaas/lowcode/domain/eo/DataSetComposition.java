package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.mapper.DataSetCompositionMapper;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataSetComposition {
    private String id;

    private String dataSetId;

    private String dataModelId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    private DataModel dataModel;

    private List<DataSetCompositionJoin> compositionJoins;

    @Autowired
    private DataSetCompositionMapper mapper;

    @Autowired
    private DataSetCompositionJoin dataSetCompositionJoinSV;

    public DataSetComposition byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int add(DataSetComposition dataSetComposition)throws EngineException {
        if(StringUtils.isBlank(dataSetComposition.getId())) {
            dataSetComposition.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataSetComposition.setCreateTime(nowDate);
        dataSetComposition.setUpdateTime(nowDate);
        int result = this.mapper.insert(dataSetComposition);
        if(result > 0 && !CollectionUtils.isEmpty(dataSetComposition.getCompositionJoins())) {
            for(DataSetCompositionJoin compositionJoin : dataSetComposition.getCompositionJoins()) {
                compositionJoin.setJoinMainCompoId(dataSetComposition.getId());
                compositionJoin.setDataSetId(dataSetComposition.dataSetId);
                if(this.dataSetCompositionJoinSV.add(compositionJoin)<=0) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "add dataSetCompositionJoin failed!");
                }
            }
        }
        return result;
    }

    public int delete(String id) {
        int result = mapper.deleteByPrimaryKey(id);
        if(result > 0) {
            this.dataSetCompositionJoinSV.deleteByComposition(id);
        }
        return result;
    }

    public int save(DataSetComposition dataSetComposition) {
        dataSetComposition.setUpdateTime(new Date());
        return this.mapper.updateByPrimaryKey(dataSetComposition);
    }

    public int deleteByDataSet(String dataSetId) {
        DataSetCompositionExample criteria = new DataSetCompositionExample();
        criteria.createCriteria().andDataSetIdEqualTo(dataSetId);
        List<DataSetComposition> compositions = this.list(dataSetId);
        if(!CollectionUtils.isEmpty(compositions)) {
            for(DataSetComposition composition : compositions) {
                this.dataSetCompositionJoinSV.deleteByComposition(composition.id);
            }
        }
        return this.mapper.deleteByExample(criteria);
    }

    public List<DataSetComposition> list(String dataSetId) {
        DataSetCompositionExample criteria = new DataSetCompositionExample();
        criteria.createCriteria().andDataSetIdEqualTo(dataSetId);
        return this.mapper.selectByExample(criteria);
    }

    public List<DataSetComposition> listBySystem(String systemId) {
        DataSetCompositionExample criteria = new DataSetCompositionExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId);
        return this.mapper.selectByExample(criteria);
    }

    public List<DataSetComposition> list() {
        return this.mapper.selectByExample(null);
    }

    public List<DataSetComposition> loadWithJoin(String dataSetId) {
        List<DataSetComposition> compositions = this.list(dataSetId);
        if(!CollectionUtils.isEmpty(compositions)) {
            for(DataSetComposition composition : compositions) {
                composition.setCompositionJoins(this.dataSetCompositionJoinSV.list(composition.getId()));
            }
        }
        return compositions;
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

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId == null ? null : dataModelId.trim();
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

    public List<DataSetCompositionJoin> getCompositionJoins() {
        return compositionJoins;
    }

    public void setCompositionJoins(List<DataSetCompositionJoin> compositionJoins) {
        this.compositionJoins = compositionJoins;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

}