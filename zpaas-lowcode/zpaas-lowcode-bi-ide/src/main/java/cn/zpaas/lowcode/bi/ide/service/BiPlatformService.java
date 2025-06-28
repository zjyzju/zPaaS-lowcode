package cn.zpaas.lowcode.bi.ide.service;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.bi.ide.vo.BusinessSystemBiInfo;
import cn.zpaas.lowcode.bi.ide.vo.DataModelSchemaDataVo;
import cn.zpaas.lowcode.bi.vo.LoadDataApplyColumnDataReq;
import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;
import cn.zpaas.lowcode.domain.eo.DataModel;
import cn.zpaas.lowcode.domain.eo.DataModelFilter;
import cn.zpaas.lowcode.domain.eo.DataModelFilterValue;
import cn.zpaas.lowcode.domain.eo.DataModelMetrics;
import cn.zpaas.lowcode.domain.eo.DataModelTag;
import cn.zpaas.lowcode.domain.eo.DataSet;
import cn.zpaas.lowcode.domain.eo.DataSetDetail;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * BI领域平台服务接口类
 */
public interface BiPlatformService {
    /**
     * 加载业务系统BI领域对象信息
     * @param systemId
     * @return
     */
    public BusinessSystemBiInfo loadBusinessSystemBiInfo(String systemId);

    /**
     * 新增数据模型
     * @param dataModel
     * @return
     */
    public DataModel addDataModel(DataModel dataModel);

    /**
     * 更新数据模型
     * @param dataModel
     * @return
     */
    public DataModel saveDataModel(DataModel dataModel) throws EngineException;

    /**
     * 加载数据模型-全量
     * @param dataModelId
     * @return
     */
    public DataModel loadDataModelWithColumn(String dataModelId);

    /**
     * 加载数据模型-全量
     * @param dataModelId
     * @return
     */
    public DataModel loadDataModelAll(String dataModelId);

    /**
     * 加载数据模型
     * @param dataModelId
     * @return
     */
    public DataModel loadDataModel(String dataModelId);

    /**
     * 加载业务系统包含的数据模型列表
     * @param systemId
     * @return
     */
    public List<DataModel> listDataModelBySystem(String systemId);

    /**
     * 删除数据模型
     * @param dataModelId
     * @return
     */
    public int deleteDataModel(String dataModelId);

    /**
     * 加载数据模型的结构及数据
     * @param dataModel
     * @return
     */
    public DataModelSchemaDataVo loadDataAndShcema(DataModel dataModel) throws EngineException;

    /**
     * 新增指标
     * @param dataModelMetrics
     * @return
     */
    public DataModelMetrics addDataModelMetrics(DataModelMetrics dataModelMetrics);

    /**
     * 修改指标
     * @param dataModelMetrics
     * @return
     */
    public DataModelMetrics saveDataModelMetrics(DataModelMetrics dataModelMetrics);

    /**
     * 删除指标
     * @param id
     * @return
     */
    public int deleteDataModelMetrics(String id);

    /**
     * 加载指标信息
     * @param id
     * @return
     */
    public DataModelMetrics loadDataModelMetrics(String id);

    /**
     * 加载数据模型包含的指标列表
     * @param dataModelId
     * @return
     */
    public List<DataModelMetrics> listDataModelMetricsByDataModel(String dataModelId);

    /**
     * 新增标签
     * @param addDataModelTag
     * @return
     */
    public DataModelTag addDataModelTag(DataModelTag dataModelTag);

    /**
     * 修改标签
     * @param dataModelTag 
     * @return
     */
    public DataModelTag saveDataModelTag (DataModelTag dataModelTag);

    /**
     * 删除标签
     * @param id
     * @return
     */
    public int deleteDataModelTag(String id);

    /**
     * 加载标签信息
     * @param id
     * @return
     */
    public DataModelTag loadDataModelTag(String id);

    /**
     * 加载数据模型包含的标签列表
     * @param dataModelId
     * @return
     */
    public List<DataModelTag> listDataModelTagByDataModel(String dataModelId);

    /**
     * 新增筛选器
     * @param addDataModelFilter
     * @return
     */
    public DataModelFilter addDataModelFilter(DataModelFilter dataModelFilter) throws EngineException;

    /**
     * 修改筛选器
     * @param dataModelFilter 
     * @return
     */
    public DataModelFilter saveDataModelFilter (DataModelFilter dataModelFilter);

    /**
     * 删除筛选器
     * @param id
     * @return
     */
    public int deleteDataModelFilter(String id);

    /**
     * 加载筛选器信息
     * @param id
     * @return
     */
    public DataModelFilter loadDataModelFilter(String id);

    /**
     * 加载数据模型包含的筛选器列表
     * @param dataModelId
     * @return
     */
    public List<DataModelFilter> listDataModelFilterByDataModel(String dataModelId);

    /**
     * 新增筛选值
     * @param addDataModelFilterValue
     * @return
     */
    public DataModelFilterValue addDataModelFilterValue(DataModelFilterValue dataModelFilterValue) throws EngineException;

    /**
     * 修改筛选值
     * @param dataModelFilterValue 
     * @return
     */
    public DataModelFilterValue saveDataModelFilterValue (DataModelFilterValue dataModelFilterValue);

    /**
     * 删除筛选值
     * @param id
     * @return
     */
    public int deleteDataModelFilterValue(String id);

    /**
     * 加载筛选值信息
     * @param id
     * @return
     */
    public DataModelFilterValue loadDataModelFilterValue(String id);

    /**
     * 新增数据集
     * @param addDataSet
     * @return
     */
    public DataSet addDataSet(DataSet dataSet) throws EngineException;

    /**
     * 修改数据集
     * @param dataSet 
     * @return
     */
    public DataSet saveDataSet (DataSet dataSet) throws EngineException;

    /**
     * 删除数据集
     * @param id
     * @return
     */
    public int deleteDataSet(String id);

    /**
     * 加载数据集信息
     * @param id
     * @return
     */
    public DataSet loadDataSet(String id);

    /**
     * 加载数据集全量信息
     * @param id
     * @return
     */
    public DataSet loadDataSetAll(String id);

    /**
     * 加载业务系统包含的数据集列表
     * @param systemId
     * @return
     */
    public List<DataSet> listDataSetBySystem(String systemId);

    /**
     * 加载数据集的明细列表信息
     * @param dataSetId
     * @return
     */
    public List<DataSetDetail> listDataSetDetails(String dataSetId);

    /**
     * 根据数据集明细列表加载数据集数据
     * @param loadReportDataReq
     * @return
     */
    public JsonArray loadDataSetDatas(LoadReportDataReq loadReportDataReq) throws EngineException;

    /**
     * 加载数据提供列配置的数据
     * @param loadDataApplyColumnDataReq
     * @return
     * @throws EngineException
     */
    public JsonObject loadDataWithDataApplyColumn(LoadDataApplyColumnDataReq loadDataApplyColumnDataReq) throws EngineException;
}  
