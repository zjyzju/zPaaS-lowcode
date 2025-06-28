package cn.zpaas.lowcode.bi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zpaas.lowcode.domain.eo.DataModel;
import cn.zpaas.lowcode.domain.eo.DataModelColumn;
import cn.zpaas.lowcode.domain.eo.DataModelFilter;
import cn.zpaas.lowcode.domain.eo.DataModelFilterValue;
import cn.zpaas.lowcode.domain.eo.DataModelMetrics;
import cn.zpaas.lowcode.domain.eo.DataModelTag;
import cn.zpaas.lowcode.domain.eo.DataSet;
import cn.zpaas.lowcode.domain.eo.DataSetComposition;
import cn.zpaas.lowcode.domain.eo.DataSetCompositionJoin;
import cn.zpaas.lowcode.domain.eo.DataSetDetail;

/**
 * Bdm引擎初始化服务
 *
 * @author zjy
 * createTime 2025年04月30日-11:08:04
 */
@Service
public class BiInitService {
    
    @Autowired
    private DataSet dataSetSV;

    @Autowired
    private DataSetDetail dataSetDetailSV;

    @Autowired
    private DataModel dataModelSV;

    @Autowired
    private DataModelColumn dataModelColumnSV;

    @Autowired
    private DataModelMetrics dataModelMetricsSV;

    @Autowired
    private DataModelTag dataModelTagSV;

    @Autowired
    private DataModelFilter dataModelFilterSV;

    @Autowired
    private DataModelFilterValue dataModelFilterValueSV;

    @Autowired
    private DataSetComposition dataSetCompositionSV;

    @Autowired
    private DataSetCompositionJoin dataSetCompositionJoinSV;

    /**
     * 加载所有数据集
     * @return
     */
    public List<DataSet> listDataSets() {
        return dataSetSV.list();
    }

    /**
     * 加载指定业务系统的所有数据集
     * @param systemId
     * @return
     */
    public List<DataSet> listDataSets(String systemId) {
        return dataSetSV.list(systemId);
    }

    /**
     * 加载所有数据集明细
     * @return
     */
    public List<DataSetDetail> listDataSetDetails() {
        return dataSetDetailSV.list();
    }

    /**
     * 加载指定业务系统的数据集明细
     * @param systemId
     * @return
     */
    public List<DataSetDetail> listDataSetDetails(String systemId) {
        return dataSetDetailSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据集构成
     * @return
     */
    public List<DataSetComposition> listDataSetCompositions() {
        return dataSetCompositionSV.list();
    }

    /**
     * 加载指定业务系统的数据集构成
     * @param systemId
     * @return
     */
    public List<DataSetComposition> listDataSetCompositions(String systemId) {
        return dataSetCompositionSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据集构成
     * @return
     */
    public List<DataSetCompositionJoin> listDataSetCompositionJoins() {
        return dataSetCompositionJoinSV.list();
    }

    /**
     * 加载指定业务系统的数据集构成
     * @param systemId
     * @return
     */
    public List<DataSetCompositionJoin> listDataSetCompositionJoins(String systemId) {
        return dataSetCompositionJoinSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据模型
     * @return
     */
    public List<DataModel> listDataModels() {
        return dataModelSV.list();
    }

    /**
     * 加载指定业务系统的数据模型
     * @param systemId
     * @return
     */
    public List<DataModel> listDataModels(String systemId) {
        return dataModelSV.list(systemId);
    }

    /**
     * 加载所有数据模型字段
     * @return
     */
    public List<DataModelColumn> listDataModelColumns() {
        return dataModelColumnSV.list();
    }

    /**
     * 加载指定业务系统的数据模型字段
     * @param systemId
     * @return
     */
    public List<DataModelColumn> listDataModelColumns(String systemId) {
        return dataModelColumnSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据模型指标
     * @return
     */
    public List<DataModelMetrics> listDataModelMetrics() {
        return dataModelMetricsSV.list();
    }

    /**
     * 加载指定业务系统的数据模型指标
     * @param systemId
     * @return
     */
    public List<DataModelMetrics> listDataModelMetrics(String systemId) {
        return dataModelMetricsSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据模型标签
     * @return
     */
    public List<DataModelTag> listDataModelTags() {
        return dataModelTagSV.list();
    }

    /**
     * 加载指定业务系统的数据模型
     * @param systemId
     * @return
     */
    public List<DataModelTag> listDataModelTags(String systemId) {
        return dataModelTagSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据模型筛选器
     * @return
     */
    public List<DataModelFilter> listDataModelFilters() {
        return dataModelFilterSV.list();
    }

    /**
     * 加载指定业务系统的数据模型筛选器
     * @param systemId
     * @return
     */
    public List<DataModelFilter> listDataModelFilters(String systemId) {
        return dataModelFilterSV.listBySystem(systemId);
    }

    /**
     * 加载所有数据模型筛选器
     * @return
     */
    public List<DataModelFilterValue> listDataModelFilterValues() {
        return dataModelFilterValueSV.list();
    }

    /**
     * 加载指定业务系统的数据模型筛选器
     * @param systemId
     * @return
     */
    public List<DataModelFilterValue> listDataModelFilterValues(String systemId) {
        return dataModelFilterValueSV.listBySystem(systemId);
    }
}
