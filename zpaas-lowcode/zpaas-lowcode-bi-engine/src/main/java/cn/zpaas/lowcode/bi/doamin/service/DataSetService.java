package cn.zpaas.lowcode.bi.doamin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.YesOrNo;
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
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.bi.proxy.DataSetProxy;
import cn.zpaas.lowcode.bi.vo.LoadDataApplyColumnDataReq;
import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 数据集领域服务
 *
 * @author zjy
 * createTime 2025年04月21日-10:27:00
 */
@Service
public class DataSetService {
    public static Logger logger = LoggerFactory.getLogger(DataSetService.class);

    private static final String ALIAS_PREFIX = "t"; //表别名的前缀
    private static final String SPACE_STRING = " "; //空字符
    private static final String INNER_JOIN = "inner join "; //左连接
    private static final String LEFT_JOIN = "left join "; //左连接
    private static final String RIGHT_JOIN = "right join "; //左连接
    private static final String ON = "on  "; //on
    private static final String DOT = "."; //.
    private static final String COMMA = ","; //逗号
    private static final String LEFT_BRACKET = "("; //左括号
    private static final String RIGHT_BRACKET = ")"; //右括号
    private static final String SINGLE_QUOTATION_MARK = "'"; //单引号
    private static final String FROM_KEY = " from "; //from
    private static final String GROUP_BY_KEY = " group by ";//group by
    private static final String ORDER_BY_KEY = " order by ";//order by
    private static final String SELECT_KEY = "select ";//select
    private static final String WHERE_KEY = " where ";//where
    private static final String AND_KEY = " and ";//and
    private static final String OR_KEY = " or ";//or
    private static final String DATE_FORMAT_FUNC = "date_format";//date_format

    private static final String DISPLAY_FORMAT_KEY = "displayFormat";//displayFormat属性的key
    private static final String DISPLAY_FORMAT_DATE = "D";//时间属性
    private static final String FORMAT_CFG_KEY = "formatCfg";//formatCfg属性的key
    private static final String IS_DIMENSION_KEY = "isDimension";//是否维度属性的key
    private static final String LABEL_KEY = "label";//label属性的key
    private static final String VALUE_KEY = "value";//value属性的key

    public static final String CODE_KEY = "code";//code属性的key
    public static final String DISPLAY_CFG_KEY = "displayCfg";//displayCfg属性的key
    public static final String ACQUIRE_TYPE_KEY = "acquireType";//acquireType属性的key
    private static final String JSON_DATA_KEY = "jsonData";//jsonData属性的key

    private static final String DATA_SET_DATAS_KEY = "dataSetDatas";//dataSetDatas属性的key
    private static final String DATA_APPLY_COLUMN_DATAS_KEY = "dataApplyColumnDatas";//dataApplyColumnDatas属性的key

    private static final String ACQUIRE_TYPE_F = "F";//acquireType属性：固定模式
    private static final String ACQUIRE_TYPE_M = "M";//acquireType属性：暴露服务模式

    private static final String TABLE_ALIAS_REPLACEMENT = "\\{tableAlias\\}";//value属性的key


    private static final String SQL_KEY = "sql";
	private static final String DATASOURCE_KEY = "datasource";
	private static final String EXPOSED_SERVICE_ID_KEY = "exposedServiceId";
    public static final String PARAMS_KEY = "params";
	
    @Autowired
    private DataModelColumn dataModelColumnSV;

    @Autowired
    private DataModelMetrics dataModelMetricsSV;

    @Autowired
    private DataModel dataModelSV;

    @Autowired
    private DataModelTag dataModelTagSV;

    @Autowired
    private DataModelFilterValue dataModelFilterValueSV;

    @Autowired
    private DataModelFilter dataModelFilterSV;

    @Autowired
    private DataSetComposition dataSetCompositionSV;

    @Autowired
    private DataSetCompositionJoin dataSetCompositionJoinSV;

    @Autowired
    private ExposedService exposedServiceSV;
    
    /**
     * 用于设计态的数据加载
     * @param loadReportDataReq
     * @return
     * @throws EngineException
     */
    public JsonArray loadDataSetDatas(LoadReportDataReq loadReportDataReq) throws EngineException{
        if(CollectionUtils.isEmpty(loadReportDataReq.getDataSetDetails())) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataSetDetails is null!");
        }
        String dataSetId = null;
        String systemId = null;
        String tenantId = null;
        List<String> dataModelIds = new ArrayList<>();
        Map<String, DataModelMetrics> metricMap = new HashMap<>();//缓存指标对象，避免重复加载
        Map<String, DataModelColumn> columnMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelTag> tagMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelFilter> filterMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelFilterValue> filterValueMap = new HashMap<>();//缓存列对象，避免重复加载
        //循环获取数据集标识、业务系统标识以及数据模型标识列表，同时加载并缓存数据模型字段以及指标信息，用于后续处理。只有数据模型字段以及指标可用于统计数据的数据加载列。
        for(DataSetDetail detail : loadReportDataReq.getDataSetDetails()) {
            if(StringUtils.isBlank(dataSetId)) {
                dataSetId =  detail.getDataSetId();
                systemId = detail.getSystemId();
                tenantId = detail.getTenantId();
            }else {
                if(dataSetId != null && !dataSetId.equals(detail.getDataSetId())) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "not support two different dataSet!");
                }
            }
            if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType())) {
                DataModelMetrics metric = metricMap.get(detail.getDetailContentId());
                if(metric == null) {
                    metric = this.dataModelMetricsSV.byId(detail.getDetailContentId());
                    if(metric != null) {
                        metricMap.put(detail.getDetailContentId(), metric);
                    }
                }
                if(metric == null) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSetDetail!");
                }
            }else {
                DataModelColumn column = columnMap.get(detail.getDetailContentId());
                if(column == null) {
                    column = this.dataModelColumnSV.byId(detail.getDetailContentId());
                    if(column != null) {
                        columnMap.put(detail.getDetailContentId(), column);
                    }
                }
                if(column == null) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataModelColumn!");
                }
            }
        }
        //加载数据集构成信息列表以及数据集涉及数据模型间JOIN关系列表信息，用于后续处理；同时获取涉及的数据模型标识列表
        List<DataSetCompositionJoin> dataSetCompositionJoins = dataSetCompositionJoinSV.listByDataSet(dataSetId);
        List<DataSetComposition> compositions = dataSetCompositionSV.list(dataSetId);
        if(CollectionUtils.isEmpty(compositions) || (compositions.size() > 1 && CollectionUtils.isEmpty(dataSetCompositionJoins))) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid DataSetComposition or DataSetCompositionJoin!");
        }
        //循环处理每个关联条件,加载相应的数据模型字段信息并缓存
        for(DataSetCompositionJoin join : dataSetCompositionJoins) {
            DataModelColumn mainColumn = dataModelColumnSV.byId(join.getMainColId());//获取关联条件中当前的数据模型字段信息
            if(mainColumn == null) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid join main Column!");
            }
            columnMap.put(mainColumn.getId(), mainColumn);
            if(DataSetCompositionJoin.REL_COL_TYPE_COLUMN.equals(join.getRelColType())) {//关联条件
                DataModelColumn relColumn = dataModelColumnSV.byId(join.getRelColId());
                if(relColumn == null) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid join rel Column!");
                }
                columnMap.put(relColumn.getId(), relColumn);
            }
        }
        Map<String, DataSetComposition> compoId2CompoMap = compositions.stream().collect(Collectors.toMap(DataSetComposition::getId, t->t));
        for(DataSetComposition composition :compositions) {
            if(!dataModelIds.contains(composition.getDataModelId())) {
                dataModelIds.add(composition.getDataModelId());
            }
        }
        if(StringUtils.isBlank(dataSetId) || CollectionUtils.isEmpty(dataModelIds)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSetDetails!");
        }

        //从数据模型配置信息中获取数据源类型以及数据源标识，同时构建涉及的表名列表以及数据标识标识指向表名的Map，用于后续处理
        String dataSourceType = null;
        String dataSourceId = null;
        List<String> tableNames = new ArrayList<>();
        Map<String, String> dataModeId2TableNameMap = new HashMap<>();//dataModeId -> tableName
        //循环获取涉及的数据源类型、数据源标识以及物理表名列表
        for(String dataModelId : dataModelIds) {
            DataModel dataModel = this.dataModelSV.byId(dataModelId);
            if(dataModel == null || !Status.EFF.equals(dataModel.getStatus())) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataModel is not effective!");
            }
            if(StringUtils.isBlank(dataSourceId)) {
                dataSourceId = dataModel.getDataSourceId();
                dataSourceType = dataModel.getDataSourceType();
            }else {
                if((dataSourceId != null && !dataSourceId.equals(dataModel.getDataSourceId())) ||
                        (dataSourceType != null && !dataSourceType.equals(dataModel.getDataSourceType()))) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "not support two different dataSource!");
                }
            }
            if(!StringUtils.isBlank(dataModel.getPhysicalSrcObject())) {
                tableNames.add(dataModel.getPhysicalSrcObject());
                dataModeId2TableNameMap.put(dataModelId, dataModel.getPhysicalSrcObject());
            }
        }
        if(CollectionUtils.isEmpty(tableNames)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found tableName!");
        }

        //当涉及多表关联时，构建表名到JOIN信息列表以及表名到关联表名Set的Map信息，用于后续处理
        Map<String, List<DataSetCompositionJoin>> tableName2JoinMap = new HashMap<>();//joinTableName -> List<Joins>
        Map<String, Set<String>> tableName2JoinTableName = new HashMap<>(); //tableName -> Set<joinTableName>
        if(tableNames.size() > 1) {
            //构建joinTableName -> List<Joins>
            Map<String, List<DataSetCompositionJoin>> compoId2JoinMap = dataSetCompositionJoins.stream().collect(Collectors.groupingBy(DataSetCompositionJoin::getJoinMainCompoId));
            for(DataSetComposition composition : compositions) {
                if(compoId2JoinMap.containsKey(composition.getId())) {
                    tableName2JoinMap.put(dataModeId2TableNameMap.get(composition.getDataModelId()), compoId2JoinMap.get(composition.getId()));
                }
            }

            for(DataSetCompositionJoin join : dataSetCompositionJoins) {
                if(!StringUtils.isBlank(join.getJoinRelDataModelId())) {
                    String tableName = dataModeId2TableNameMap.get(compoId2CompoMap.get(join.getJoinMainCompoId()).getDataModelId());//当前关联记录归属的表名
                    
                    //构建tableName -> Set<joinTableName>
                    String relTableName = dataModeId2TableNameMap.get(join.getJoinRelDataModelId());//关联的表名，为主表
                    Set<String> joinTableNameSet = tableName2JoinTableName.get(relTableName);//获取主表涉及的关联表
                    if(joinTableNameSet == null) {//如果为空，则初始化
                        joinTableNameSet = new HashSet<>();
                        tableName2JoinTableName.put(relTableName, joinTableNameSet);
                    }
                    joinTableNameSet.add(tableName);//加入到关联表Set中
                    tableNames.remove(tableName);//从主表列表中移除
                }
            }
        }
        

        //处理选中的标签信息，加载数据模型标签信息并缓存
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedTags())) {
            //选中标签的Map，格式为{"tagId": ["tagId"]}，对应的数组正常为1个值，设计态key值用的code，但key无实际用途
            for(String tagCode : loadReportDataReq.getSelectedTags().keySet()) {
                JsonArray tagArray = JsonUtils.getJsonArray(loadReportDataReq.getSelectedTags(), tagCode);
                if(!JsonUtils.isEmpty(tagArray)) {
                    String tagId = tagArray.get(0).getAsString();
                    DataModelTag dataModelTag = this.dataModelTagSV.byId(tagId);//加载标签配置信息
                    if(dataModelTag == null) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found dataModelTag! {}", tagCode);
                    }
                    tagMap.put(dataModelTag.getId(), dataModelTag);
                }
            }
        }
        //处理选中的筛选值信息，加载数据模型筛选器及筛选值信息并缓存
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedFilterValues())) {
            //筛选器选择值的Map，格式为{"fileterId":["filterValueId1","filterValueId2"]}，设计态key值用的code，但key无实际用途
            for(String filterCode : loadReportDataReq.getSelectedFilterValues().keySet()) {
                JsonArray filterValueIds = JsonUtils.getJsonArray(loadReportDataReq.getSelectedFilterValues(), filterCode);
                if(!JsonUtils.isEmpty(filterValueIds)) {
                    for(int i=0; i<filterValueIds.size(); i++) {
                        String filterValueId = filterValueIds.get(i).getAsString();
                        DataModelFilterValue filterValue = this.dataModelFilterValueSV.byId(filterValueId);//加载筛选值配置信息
                        if(filterValue == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found DataModelFilterValue! {}", filterValueId);
                        }
                        filterValueMap.put(filterValue.getId(), filterValue);
                        DataModelFilter filter = this.dataModelFilterSV.byId(filterValue.getFilterId());//加载筛选器配置信息   
                        if(filter == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found DataModelFilter! {}", filterValue.getFilterId());
                        }
                        filterMap.put(filter.getId(), filter);
                    }
                }
            }
        }
        List<DataModelColumn> needTranslateColumn = new ArrayList<>();//设置需要翻译的字段
        List<String> needTranslateColumnAlias = new ArrayList<>();//设置需要翻译的字段别名
        String sql = this.buildStatisticSql(loadReportDataReq, tableNames, tableName2JoinTableName, tableName2JoinMap, dataModeId2TableNameMap, metricMap, columnMap, tagMap, filterMap, filterValueMap, needTranslateColumn, needTranslateColumnAlias);
        
        //执行sql获取统计数据
        JsonArray result = ORMRepositoryAbility.getInstance().executeQuerySql(sql, null, systemId, dataSourceId, tenantId);
        //对于需要翻译的字段，根据配置的翻译方式，获取并添加到返回结果中
        if(!CollectionUtils.isEmpty(needTranslateColumn)) {
            int index = 0;
            for(DataModelColumn column : needTranslateColumn) {
                String columnAlias = needTranslateColumnAlias.get(index++);
                if(DataModelColumn.TRANSLATE_TYPE_O.equals(column.getTranslateType())) {//配置选项
                    JsonArray options = this.loadOptionsForColumn(column);
                    if(!JsonUtils.isEmpty(options)) {
                        for(int i=0; i<result.size(); i++) {
                            JsonObject row = result.get(i).getAsJsonObject();
                            String value = JsonUtils.getString(row, columnAlias);
                            for(int j=0; j<options.size(); j++) {
                                JsonObject option = options.get(j).getAsJsonObject();
                                if(!StringUtils.isBlank(value) && value.equals(JsonUtils.getString(option, VALUE_KEY))) {
                                    row.addProperty(columnAlias, JsonUtils.getString(option, LABEL_KEY));
                                    break;
                                }
                            }
                        }
                    }
                }else if(DataModelColumn.TRANSLATE_TYPE_T.equals(column.getTranslateType())){//直接翻译
                    for(int i=0; i<result.size(); i++) {
                        JsonObject row = result.get(i).getAsJsonObject();
                        String value = JsonUtils.getString(row, columnAlias);
                        String label = this.translateForColumn(column, value);
                        if(StringUtils.isBlank(label)) {
                            label = value;
                        }
                        row.addProperty(columnAlias, label);
                    }
                }
                
            }
        }
        return result;
    }

    /**
     * 用于运行态的数据加载
     * @param loadReportDataReq
     * @return
     * @throws EngineException
     */
    public JsonArray loadReportDatas(LoadReportDataReq loadReportDataReq) throws EngineException{
        if(CollectionUtils.isEmpty(loadReportDataReq.getDataSetDetails())) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataSetDetails is null!");
        }
        String dataSetId = null;
        String systemId = null;
        String tenantId = null;
        List<String> dataModelIds = new ArrayList<>();
        Map<String, DataModelMetrics> metricMap = new HashMap<>();//缓存指标对象，避免重复加载
        Map<String, DataModelColumn> columnMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelTag> tagMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelFilter> filterMap = new HashMap<>();//缓存列对象，避免重复加载
        Map<String, DataModelFilterValue> filterValueMap = new HashMap<>();//缓存列对象，避免重复加载
        //循环获取数据集标识、业务系统标识以及数据模型标识列表，同时加载并缓存数据模型字段以及指标信息，用于后续处理。只有数据模型字段以及指标可用于统计数据的数据加载列。
        for(DataSetDetail detail : loadReportDataReq.getDataSetDetails()) {
            if(StringUtils.isBlank(dataSetId)) {
                dataSetId =  detail.getDataSetId();
                systemId = detail.getSystemId();
                tenantId = detail.getTenantId();
            }else {
                if(dataSetId != null && !dataSetId.equals(detail.getDataSetId())) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "not support two different dataSet!");
                }
            }
            if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType())) {
                DataModelMetrics metric = metricMap.get(detail.getDetailContentId());
                if(metric == null) {
                    metric = DataSetProxy.getDataModelMetric(detail.getSystemId(), detail.getDetailContentId());
                    if(metric != null) {
                        metricMap.put(detail.getDetailContentId(), metric);
                    }
                }
                if(metric == null) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSetDetail!");
                }
            }else {
                DataModelColumn column = columnMap.get(detail.getDetailContentId());
                if(column == null) {
                    column = DataSetProxy.getDataModelColumn(detail.getSystemId(), detail.getDetailContentId());
                    if(column != null) {
                        columnMap.put(detail.getDetailContentId(), column);
                    }
                }
                if(column == null) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataModelColumn!");
                }
            }
        }
        if(StringUtils.isBlank(dataSetId)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSetDetails!");
        }
        DataSet dataSet = DataSetProxy.getDataSet(systemId, dataSetId);
        if(dataSet == null) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSetId!");
        }
        List<DataSetComposition> compositions = dataSet.getCompositions();
        if(CollectionUtils.isEmpty(compositions)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid DataSetComposition!");
        }
        for(DataSetComposition composition :compositions) {
            if(!dataModelIds.contains(composition.getDataModelId())) {
                dataModelIds.add(composition.getDataModelId());
            }
        }
        if(CollectionUtils.isEmpty(dataModelIds)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dataSet!");
        }

        //从数据模型配置信息中获取数据源类型以及数据源标识，同时构建涉及的表名列表以及数据标识标识指向表名的Map，用于后续处理
        String dataSourceType = null;
        String dataSourceId = null;
        Map<String, String> dataModeId2TableNameMap = new HashMap<>();//dataModeId -> tableName
        List<String> tableNames = new ArrayList<>();
        //循环获取涉及的数据源类型、数据源标识以及物理表名列表
        for(String dataModelId : dataModelIds) {
            DataModel dataModel = DataSetProxy.getDataModel(systemId, dataModelId);
            if(dataModel == null || !Status.EFF.equals(dataModel.getStatus())) {
                throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataModel is not effective!");
            }
            if(StringUtils.isBlank(dataSourceId)) {
                dataSourceId = dataModel.getDataSourceId();
                dataSourceType = dataModel.getDataSourceType();
            }else {
                if((dataSourceId != null && !dataSourceId.equals(dataModel.getDataSourceId())) ||
                        (dataSourceType != null && !dataSourceType.equals(dataModel.getDataSourceType()))) {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "not support two different dataSource!");
                }
            }
            if(!StringUtils.isBlank(dataModel.getPhysicalSrcObject())) {
                tableNames.add(dataModel.getPhysicalSrcObject());
                dataModeId2TableNameMap.put(dataModelId, dataModel.getPhysicalSrcObject());
            }
        }
        if(CollectionUtils.isEmpty(tableNames)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found tableName!");
        }
        
        //当涉及多表关联时，构建表名到JOIN信息列表以及表名到关联表名Set的Map信息，用于后续处理
        Map<String, List<DataSetCompositionJoin>> tableName2JoinMap = new HashMap<>();//joinTableName -> List<Joins>
        Map<String, Set<String>> tableName2JoinTableName = new HashMap<>(); //tableName -> Set<joinTableName>
        if(tableNames.size() > 1) {
            for(DataSetComposition composition : compositions) {
                if(!CollectionUtils.isEmpty(composition.getCompositionJoins())) {
                    tableName2JoinMap.put(dataModeId2TableNameMap.get(composition.getDataModelId()), composition.getCompositionJoins());

                    for(DataSetCompositionJoin join : composition.getCompositionJoins()) {
                        //获取并缓存相关的数据模型字段信息
                        DataModelColumn mainColumn = DataSetProxy.getDataModelColumn(systemId, join.getMainColId());//获取关联条件中当前的数据模型字段信息
                        if(mainColumn == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid join main Column!");
                        }
                        columnMap.put(mainColumn.getId(), mainColumn);
                        if(DataSetCompositionJoin.REL_COL_TYPE_COLUMN.equals(join.getRelColType())) {//关联条件
                            DataModelColumn relColumn = DataSetProxy.getDataModelColumn(systemId, join.getRelColId());
                            if(relColumn == null) {
                                throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid join rel Column!");
                            }
                            columnMap.put(relColumn.getId(), relColumn);
                        }
                        if(!StringUtils.isBlank(join.getJoinRelDataModelId())) {
                            String tableName = dataModeId2TableNameMap.get(composition.getDataModelId());//当前关联记录归属的表名
                            
                            //构建tableName -> Set<joinTableName>
                            String relTableName = dataModeId2TableNameMap.get(join.getJoinRelDataModelId());//关联的表名，为主表
                            Set<String> joinTableNameSet = tableName2JoinTableName.get(relTableName);//获取主表涉及的关联表
                            if(joinTableNameSet == null) {//如果为空，则初始化
                                joinTableNameSet = new HashSet<>();
                                tableName2JoinTableName.put(relTableName, joinTableNameSet);
                            }
                            joinTableNameSet.add(tableName);//加入到关联表Set中
                            tableNames.remove(tableName);//从主表中移除
                        }
                    }
                }
            }
        }

        //处理选中的标签信息，加载数据模型标签信息并缓存
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedTags())) {
            //选中标签的Map，格式为{"tagId": ["tagId"]}，对应的数组正常为1个值，设计态key值用的code，但key无实际用途
            for(String tagCode : loadReportDataReq.getSelectedTags().keySet()) {
                JsonArray tagArray = JsonUtils.getJsonArray(loadReportDataReq.getSelectedTags(), tagCode);
                if(!JsonUtils.isEmpty(tagArray)) {
                    String tagId = tagArray.get(0).getAsString();
                    DataModelTag dataModelTag = DataSetProxy.getDataModelTag(systemId, tagId);//加载标签配置信息
                    if(dataModelTag == null) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found dataModelTag! {}", tagCode);
                    }
                    tagMap.put(dataModelTag.getId(), dataModelTag);
                }
            }
        }
        //处理选中的筛选值信息，加载数据模型筛选器及筛选值信息并缓存
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedFilterValues())) {
            //筛选器选择值的Map，格式为{"fileterId":["filterValueId1","filterValueId2"]}，设计态key值用的code，但key无实际用途
            for(String filterCode : loadReportDataReq.getSelectedFilterValues().keySet()) {
                JsonArray filterValueIds = JsonUtils.getJsonArray(loadReportDataReq.getSelectedFilterValues(), filterCode);
                if(!JsonUtils.isEmpty(filterValueIds)) {
                    for(int i=0; i<filterValueIds.size(); i++) {
                        String filterValueId = filterValueIds.get(i).getAsString();
                        DataModelFilterValue filterValue = DataSetProxy.getDataModelFilterValue(systemId, filterValueId);//加载筛选值配置信息
                        if(filterValue == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found DataModelFilterValue! {}", filterValueId);
                        }
                        filterValueMap.put(filterValue.getId(), filterValue);
                        DataModelFilter filter = DataSetProxy.getDataModelFilter(systemId, filterValue.getFilterId());//加载筛选器配置信息   
                        if(filter == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found DataModelFilter! {}", filterValue.getFilterId());
                        }
                        filterMap.put(filter.getId(), filter);
                    }
                }
            }
        }
        List<DataModelColumn> needTranslateColumn = new ArrayList<>();//设置需要翻译的字段
        List<String> needTranslateColumnAlias = new ArrayList<>();//设置需要翻译的字段

        String sql = this.buildStatisticSql(loadReportDataReq, tableNames, tableName2JoinTableName, tableName2JoinMap, dataModeId2TableNameMap, metricMap, columnMap, tagMap, filterMap, filterValueMap, needTranslateColumn, needTranslateColumnAlias);
        
        JsonArray result = ORMRepositoryAbility.getInstance().executeQuerySql(sql.toString(), null, systemId, dataSourceId, tenantId);
        if(!CollectionUtils.isEmpty(needTranslateColumn)) {
            int index=0;
            for(DataModelColumn column : needTranslateColumn) {
                String columnAlias = needTranslateColumnAlias.get(index++);
                if(DataModelColumn.TRANSLATE_TYPE_O.equals(column.getTranslateType())) {//配置选项
                    JsonArray options = this.loadOptionsForColumn(column);
                    if(!JsonUtils.isEmpty(options)) {
                        for(int i=0; i<result.size(); i++) {
                            JsonObject row = result.get(i).getAsJsonObject();
                            String value = JsonUtils.getString(row, columnAlias);
                            for(int j=0; j<options.size(); j++) {
                                JsonObject option = options.get(j).getAsJsonObject();
                                if(!StringUtils.isBlank(value) && value.equals(JsonUtils.getString(option, VALUE_KEY))) {
                                    row.addProperty(columnAlias, JsonUtils.getString(option, LABEL_KEY));
                                    break;
                                }
                            }
                        }
                    }
                }else if(DataModelColumn.TRANSLATE_TYPE_T.equals(column.getTranslateType())){//直接翻译
                    for(int i=0; i<result.size(); i++) {
                        JsonObject row = result.get(i).getAsJsonObject();
                        String value = JsonUtils.getString(row, columnAlias);
                        String label = this.translateForColumn(column, value);
                        if(StringUtils.isBlank(label)) {
                            label = value;
                        }
                        row.addProperty(columnAlias, label);
                    }
                }
                
            }
        }
        return result;
    }

    /**
     * 构建查询统计数据的Sql语句
     * @param loadReportDataReq
     * @param tableNames
     * @param tableName2JoinTableName
     * @param tableName2JoinMap
     * @param dataModeId2TableNameMap
     * @param metricMap
     * @param columnMap
     * @param tagMap
     * @param filterMap
     * @param filterValueMap
     * @param needTranslateColumn
     * @param needTranslateColumnAlias
     * @return
     * @throws EngineException
     */
    private String buildStatisticSql(LoadReportDataReq loadReportDataReq, List<String> tableNames, Map<String, Set<String>> tableName2JoinTableName,
            Map<String, List<DataSetCompositionJoin>> tableName2JoinMap, Map<String, String> dataModeId2TableNameMap, Map<String, DataModelMetrics> metricMap,
            Map<String, DataModelColumn> columnMap, Map<String, DataModelTag> tagMap, Map<String, DataModelFilter> filterMap,
            Map<String, DataModelFilterValue> filterValueMap, List<DataModelColumn> needTranslateColumn, List<String> needTranslateColumnAlias)throws EngineException {
        //from部分sql语句的拼装，同时构建表名到表别名的Map，用于后续处理
        StringBuilder fromPartBuilder = new StringBuilder(FROM_KEY);
        int mainTableCount = 0;//用于主表别名的记数
        int joinTableCount = 100; //用于从表别名的记数
        Map<String, String> tableName2Alias = new HashMap<>();//tableName-> aliasName
        if(CollectionUtils.isEmpty(tableNames)) {
            throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid table join set!");
        }
        //循环主表
        for(String tableName : tableNames) {
            //from tableName0 t0  
            fromPartBuilder.append(tableName).append(SPACE_STRING).append(ALIAS_PREFIX).append(mainTableCount).append(SPACE_STRING);//拼接表名及别名
            tableName2Alias.put(tableName, ALIAS_PREFIX+mainTableCount);//设置表名-》别名

            //处理Join表
            Set<String> joinTableNameSet = tableName2JoinTableName.get(tableName);//获取主表涉及的关联表
            if(!CollectionUtils.isEmpty(joinTableNameSet)) {
                joinTableCount++;
                for(String joinTableName : joinTableNameSet) {//循环处理每张关联表
                    List<DataSetCompositionJoin> joins = tableName2JoinMap.get(joinTableName);//获取关联表上设置的JOIN信息列表
                    String joinType = DataSetCompositionJoin.JOIN_TYPE_INNER; //默认的Join类型为内连接
                    //判断JOIN类型
                    for(DataSetCompositionJoin join : joins) {//循环处理每个关联条件
                        //非当前表的关联条件
                        if(!tableName.equals(dataModeId2TableNameMap.get(join.getJoinRelDataModelId()))) {
                            continue;
                        }
                        if(!StringUtils.isBlank(join.getJoinType())) {
                            joinType = join.getJoinType();
                            break;
                        }
                    }
                    //from tableName0 t0 inner join 
                    if(DataSetCompositionJoin.JOIN_TYPE_LEFT.equals(joinType)) {//左连接
                        fromPartBuilder.append(LEFT_JOIN);
                    }else if(DataSetCompositionJoin.JOIN_TYPE_RIGHT.equals(joinType)) {//右连接
                        fromPartBuilder.append(RIGHT_JOIN);
                    }else {//默认为内连接
                        fromPartBuilder.append(INNER_JOIN);
                    }
                    //from tableName0 t0 inner join joinTableName t101
                    fromPartBuilder.append(joinTableName).append(SPACE_STRING).append(ALIAS_PREFIX).append(joinTableCount).append(SPACE_STRING);
                    tableName2Alias.put(joinTableName, ALIAS_PREFIX+joinTableCount);//将关联表的别名加入map
                    if(!CollectionUtils.isEmpty(joins)) {
                        //from tableName0 t0 inner join joinTableName t101 on
                        fromPartBuilder.append(ON);
                        int i = -1;
                        for(DataSetCompositionJoin join : joins) {//循环处理每个关联条件
                            i++;
                            //非当前表的关联条件
                            if(!tableName.equals(dataModeId2TableNameMap.get(join.getJoinRelDataModelId()))) {
                                continue;
                            }
                            DataModelColumn mainColumn = columnMap.get(join.getMainColId());//获取关联条件中当前的数据模型字段信息
                            if(mainColumn == null) {
                                throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid join main Column!");
                            }
                            //from tableName0 t0 inner join joinTableName t101 on t101.mainColumn = t0.relCol
                            fromPartBuilder.append(ALIAS_PREFIX).append(joinTableCount).append(DOT).append(mainColumn.getCode())
                                    .append(SPACE_STRING).append(DataSetCompositionJoin.transferCondTypeMysql(join.getCondType())).append(SPACE_STRING);
                            if(DataSetCompositionJoin.REL_COL_TYPE_COLUMN.equals(join.getRelColType())) {//关联条件
                                DataModelColumn relColumn = columnMap.get(join.getRelColId());
                                fromPartBuilder.append(ALIAS_PREFIX).append(mainTableCount).append(DOT).append(relColumn.getCode()).append(SPACE_STRING);
                            }else if(DataSetCompositionJoin.REL_COL_TYPE_FIX.equals(join.getRelColType())) {//固定条件
                                fromPartBuilder.append(SINGLE_QUOTATION_MARK).append(join.getRelColId()).append(SINGLE_QUOTATION_MARK).append(SPACE_STRING);
                            }
                            //如果不是最后一个条件，添加and
                            //from tableName0 t0 inner join joinTableName t101 on t101.mainColumn = t0.relCol and
                            if(i < joins.size()-1) {
                                fromPartBuilder.append(AND_KEY);
                            }
                        }
                    }
                }
            }

            if(mainTableCount < tableNames.size()-1) {// 如果不是最后一个主表
                //from tableName0 t0 inner join joinTableName t101 on t101.mainColumn = t0.relCol，tableName1 t1....
                fromPartBuilder.append(COMMA).append(SPACE_STRING);
            }else {//已经是最后一个主表，只添加空格
                fromPartBuilder.append(SPACE_STRING);
            }
            mainTableCount++;
        }

        //进行select（包括维度字段、时间字段、指标和其他字段）、group by（维度字段）、order by（维度字段） 语句的拼接，同时构建需要翻译字段的列表，用于后续处理
        StringBuilder groupPartBuilder = new StringBuilder(GROUP_BY_KEY);//主要拼接维度及时间字段
        StringBuilder selectPartBuilder = new StringBuilder(SELECT_KEY);//主要拼接维度及时间字段
        StringBuilder selectPartBuilder2 = new StringBuilder(StringUtils.emptyString());//主要拼接维度及时间之外的其他字段
        StringBuilder orderPartBuilder = new StringBuilder(ORDER_BY_KEY);//主要拼接维度及时间字段
        int counter = -1;
        for(DataSetDetail detail : loadReportDataReq.getDataSetDetails()) {
            counter++;

            JsonObject displayCfgJSON = null;//displayCfg信息
            if(!JsonUtils.isEmpty(loadReportDataReq.getDisplayCfgs()) && loadReportDataReq.getDisplayCfgs().get(counter) != null) {
                displayCfgJSON = loadReportDataReq.getDisplayCfgs().get(counter).getAsJsonObject();
            }
            String isDimension = null;//是否维度
            if(displayCfgJSON != null) {
                isDimension = JsonUtils.getString(displayCfgJSON, IS_DIMENSION_KEY);
            }
            if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType()) && !YesOrNo.YES.equals(isDimension)) {//指标且未当维度使用
                DataModelMetrics metric = metricMap.get(detail.getDetailContentId());
                if(!StringUtils.isBlank(metric.getMetricFormula())) {
                    //如果配置时有设置"{tableAlias}"的占位符，需要用表的别名替换
                    selectPartBuilder2.append(metric.getMetricFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(metric.getDataModelId())))).append(SPACE_STRING).append(metric.getCode());
                    if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                        selectPartBuilder2.append(COMMA).append(SPACE_STRING);
                    }else {//设置了列的别名
                        selectPartBuilder2.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                    }
                }
            }else if(DataSetDetail.DETAIL_TYPE_METRICS.equals(detail.getDetailType()) && YesOrNo.YES.equals(isDimension)) {//指标且当维度使用
                DataModelMetrics metric = metricMap.get(detail.getDetailContentId());
                if(!StringUtils.isBlank(metric.getMetricFormula())) {
                    //如果配置时有设置"{tableAlias}"的占位符，需要用表的别名替换
                    selectPartBuilder.append(metric.getMetricFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(metric.getDataModelId())))).append(SPACE_STRING).append(metric.getCode());
                    if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                        selectPartBuilder.append(COMMA).append(SPACE_STRING);
                    }else {//设置了列的别名
                        selectPartBuilder.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                    }
                    groupPartBuilder.append(metric.getMetricFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(metric.getDataModelId())))).append(COMMA).append(SPACE_STRING);
                    orderPartBuilder.append(metric.getMetricFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(metric.getDataModelId())))).append(COMMA).append(SPACE_STRING);
                }
            }else if(DataSetDetail.DETAIL_TYPE_TAG.equals(detail.getDetailType())) {//标签，不能用于这部分
                //detail.setContent(this.dataModelTagSV.byId(detail.getDetailContentId()));
            }else if(DataSetDetail.DETAIL_TYPE_FILTER.equals(detail.getDetailType())) {//过滤器， 不能用于这部分
                //detail.setContent(this.dataModelFilterSV.byId(detail.getDetailContentId()));
            }else {//其他
                DataModelColumn column = columnMap.get(detail.getDetailContentId());
                if(DataSetDetail.DETAIL_TYPE_MESSUREMENT.equals(column.getColumnType()) 
                    || DataSetDetail.DETAIL_TYPE_PRIMARY.equals(column.getColumnType())
                    || DataSetDetail.DETAIL_TYPE_COMMON.equals(column.getColumnType())) {//度量、主键、普通字段
                    if(YesOrNo.YES.equals(isDimension)) {//当维度使用
                        selectPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode());
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                        groupPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode()).append(COMMA).append(SPACE_STRING);
                        orderPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode()).append(COMMA).append(SPACE_STRING);
                    }else {//默认
                        selectPartBuilder2.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode());
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder2.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder2.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                    }
                }else if(DataSetDetail.DETAIL_TYPE_DIMENSION.equals(column.getColumnType())) {//维度
                    if(YesOrNo.NO.equals(isDimension)) {//未当维度使用
                        selectPartBuilder2.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode());
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder2.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder2.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                    }else {//默认
                        selectPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode());
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                        groupPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode()).append(COMMA).append(SPACE_STRING);
                        orderPartBuilder.append(tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId()))).append(DOT).append(column.getCode()).append(COMMA).append(SPACE_STRING);
                    }
                }else if(DataSetDetail.DETAIL_TYPE_TIME.equals(column.getColumnType())) {//时间
                    String keyString = column.getCode();
                    //时间类型且如果有配置displayCfg信息
                    if(displayCfgJSON != null) {
                        String displayFormat = JsonUtils.getString(displayCfgJSON, DISPLAY_FORMAT_KEY);//显示格式
                        String formatCfg = JsonUtils.getString(displayCfgJSON, FORMAT_CFG_KEY);//格式配置
                        if(DISPLAY_FORMAT_DATE.equals(displayFormat) && !StringUtils.isBlank(formatCfg)) {//时间类型且有配置显示格式
                            //date_format(t0.create_date, '%Y-%m-%d %H:%I:%s')
                            keyString = DATE_FORMAT_FUNC + LEFT_BRACKET + tableName2Alias.get(dataModeId2TableNameMap.get(column.getDataModelId())) + DOT + keyString + COMMA + SPACE_STRING + SINGLE_QUOTATION_MARK + formatCfg + SINGLE_QUOTATION_MARK + RIGHT_BRACKET;
                        }
                    }
                    
                    if(YesOrNo.NO.equals(isDimension)) {//未当维度使用
                        selectPartBuilder2.append(keyString).append(SPACE_STRING).append(column.getCode());//设置别名
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder2.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder2.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                    }else {//默认
                        selectPartBuilder.append(keyString).append(SPACE_STRING).append(column.getCode());//设置别名
                        if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                            selectPartBuilder.append(COMMA).append(SPACE_STRING);
                        }else {//设置了列的别名
                            selectPartBuilder.append(SPACE_STRING).append(detail.getDetailContentAlias()).append(COMMA).append(SPACE_STRING);
                        }
                        groupPartBuilder.append(keyString).append(COMMA).append(SPACE_STRING);
                        orderPartBuilder.append(keyString).append(COMMA).append(SPACE_STRING);
                    }
                }
                //如果需要翻译
                if(!StringUtils.isBlank(column.getTranslateType()) && !StringUtils.isBlank(column.getOptionType()) && !StringUtils.isBlank(column.getOptionCfg())) {
                    needTranslateColumn.add(column);
                    if(StringUtils.isBlank(detail.getDetailContentAlias())) {//未设置列的别名
                        needTranslateColumnAlias.add(column.getCode());
                    }else {//设置了列的别名
                        needTranslateColumnAlias.add(detail.getDetailContentAlias());
                    }
                }
            }
        }

        //进行where部分sql语句的拼装，主要涉及筛选器和标签
        StringBuilder wherePartBuilder = new StringBuilder(WHERE_KEY);
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedTags())) {//处理标签，标签是与的关系
            //选中标签的Map，格式为{"tagId": ["tagId"]}，对应的数组正常为1个值，设计态key值用的code，但key无实际用途
            for(String tagCode : loadReportDataReq.getSelectedTags().keySet()) {
                JsonArray tagArray = JsonUtils.getJsonArray(loadReportDataReq.getSelectedTags(), tagCode);
                if(!JsonUtils.isEmpty(tagArray)) {
                    String tagId = tagArray.get(0).getAsString();
                    DataModelTag dataModelTag = tagMap.get(tagId);//加载标签配置信息
                    if(dataModelTag == null) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found dataModelTag! {}", tagCode);
                    }
                    if(StringUtils.isBlank(dataModelTag.getFilterFormula())) {
                        throw new EngineException(ResultStatus.BUSINESS_ERROR, "not config dataModelTag formula! {}", tagCode);
                    }
                    //使用标签配置信息中的filter_formula作为条件，如果有配置{tableAilas}占位符，则使用表的别我替换
                    //where (filter_formula) and
                    wherePartBuilder.append(LEFT_BRACKET).append(dataModelTag.getFilterFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(dataModelTag.getDataModelId())))).append(RIGHT_BRACKET).append(AND_KEY);
                }
            }
        }
        if(!JsonUtils.isEmpty(loadReportDataReq.getSelectedFilterValues())) {//处理选中的筛选值
            //筛选器选择值的Map，格式为{"fileterId":["filterValueId1","filterValueId2"]}，设计态key值用的code，但key无实际用途
            for(String filterCode : loadReportDataReq.getSelectedFilterValues().keySet()) {
                JsonArray filterValueIds = JsonUtils.getJsonArray(loadReportDataReq.getSelectedFilterValues(), filterCode);
                if(!JsonUtils.isEmpty(filterValueIds)) {
                    wherePartBuilder.append(LEFT_BRACKET);
                    for(int i=0; i<filterValueIds.size(); i++) {//同一个筛选器的筛选值是or的关系
                        String filterValueId = filterValueIds.get(i).getAsString();
                        DataModelFilterValue filterValue = filterValueMap.get(filterValueId);//加载筛选值配置信息
                        if(filterValue == null) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found DataModelFilterValue! {}", filterValueId);
                        }
                        if(StringUtils.isBlank(filterValue.getFilterFormula())) {
                            throw new EngineException(ResultStatus.BUSINESS_ERROR, "not config DataModelFilterValue formula! {}", filterValueId);
                        }
                        DataModelFilter filter = filterMap.get(filterValue.getFilterId());//加载筛选器配置信息   
                        //使用筛选值配置信息中的filter_formula作为条件，如果有配置{tableAilas}占位符，则使用表的别我替换                   
                        if(i == filterValueIds.size()-1) {//如果是最后一个
                            wherePartBuilder.append(LEFT_BRACKET).append(filterValue.getFilterFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(filter.getDataModelId())))).append(RIGHT_BRACKET).append(SPACE_STRING);
                        }else {//如果不是最后一个筛选值，需要拼接 or
                            wherePartBuilder.append(LEFT_BRACKET).append(filterValue.getFilterFormula().replaceAll(TABLE_ALIAS_REPLACEMENT, tableName2Alias.get(dataModeId2TableNameMap.get(filter.getDataModelId())))).append(RIGHT_BRACKET).append(OR_KEY);
                        }
                    }
                    wherePartBuilder.append(RIGHT_BRACKET).append(AND_KEY);//不同筛选器之间使用and连接
                }
            }
        }

        //组成完整的sql语句
        selectPartBuilder.append(selectPartBuilder2);
        StringBuilder sqlBulider = new StringBuilder();
        if(selectPartBuilder.toString().endsWith(COMMA+SPACE_STRING)) {//以逗号结尾，需要去除多余的逗号
            sqlBulider.append(selectPartBuilder.substring(0, selectPartBuilder.length()-2));
        }else {
            sqlBulider.append(selectPartBuilder);
        }
        if(fromPartBuilder.toString().endsWith(AND_KEY+SPACE_STRING)) {//以and结尾，需要去除多余的and
            sqlBulider.append(fromPartBuilder.substring(0, fromPartBuilder.length()-5));
        }else {
            sqlBulider.append(fromPartBuilder);
        }
        if(!wherePartBuilder.toString().equals(WHERE_KEY)) {//如果以where结尾表示没有增加条件，不需要拼接
            if(wherePartBuilder.toString().endsWith(AND_KEY)) {//以and结尾，需要去除多余的and
                sqlBulider.append(wherePartBuilder.substring(0, wherePartBuilder.length()-4));
            }else {
                sqlBulider.append(wherePartBuilder);
            }
        }
        if(!groupPartBuilder.toString().equals(GROUP_BY_KEY)) {//如果以group by结尾表示没有增加分组信息，不需要拼接
            if(groupPartBuilder.toString().endsWith(COMMA+SPACE_STRING)) {//以逗号结尾，需要去除多余的逗号
                sqlBulider.append(groupPartBuilder.substring(0, groupPartBuilder.length()-2));
                sqlBulider.append(orderPartBuilder.substring(0, orderPartBuilder.length()-2));
            }else {
                sqlBulider.append(groupPartBuilder);
                sqlBulider.append(orderPartBuilder);
            }
        }   
        String sql = sqlBulider.toString();
        logger.debug("load data sql: {}", sql);
        return sql;
    }

    /**
     * 加载翻译列表信息
     * @param column
     * @return
     */
    private JsonArray loadOptionsForColumn(DataModelColumn column) {
    	if(DataModelColumn.OPTION_TYPE_F.equals(column.getOptionType())) {//固定方式
            return JsonUtils.toJsonArray(column.getOptionCfg());
        }else if(DataModelColumn.OPTION_TYPE_S.equals(column.getOptionType())) {//sql类型
    		/*如下配置
			{
				"sql": "select service_attr_id value, service_attr_name label from pcc_service_attr where status = '1'",
				"datasource": ""
			}
			*/
			JsonObject optionCfg = JsonUtils.toJsonObject(column.getOptionCfg());
			if(!JsonUtils.isEmpty(optionCfg)) {
				String querySql = JsonUtils.getString(optionCfg, SQL_KEY);
				String datasource = JsonUtils.getString(optionCfg, DATASOURCE_KEY);
				
				List<Object> valueList = new ArrayList<>();
				return ORMRepositoryAbility.getInstance().executeQuerySql(querySql, valueList, column.getSystemId(), datasource, column.getTenantId());
			}
    	}else if(DataModelColumn.OPTION_TYPE_D.equals(column.getOptionType())) {//字典类型
			/*
			{
				"dictResourceId":"", 
				"dictCode":"",
				"dictValue":"",
				"parentDictCode":"",
				"parentDictValue":""
			}
			 */
			JsonObject dictOption = JsonUtils.toJsonObject(column.getOptionCfg());
			String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
			String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
			if(StringUtils.isBlank(dictResourceId) || StringUtils.isBlank(dictCode)) {//配置不完整时
				return null;
			}
			String dictValue  = JsonUtils.getString(dictOption, DictCacheAbility.DICT_VALUE);
			String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
			String parentDictValue = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_VALUE);
            JsonArray options = new JsonArray();
			if (!StringUtils.isBlank(dictValue)) {//取某个字典值的信息
				JsonObject dictJsonObject = DictCacheAbility.getInstance().getDictValue(column.getSystemId(), dictResourceId, dictCode, dictValue);
				if(!JsonUtils.isEmpty(dictJsonObject)) {
					JsonObject option = new JsonObject();
					option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
					option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
					options.add(option);
				}
				return options;
			}else if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentDictValue)) {//取子字典值列表
				JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(column.getSystemId(), dictResourceId, dictCode, parentDictCode, parentDictValue);
				if(!JsonUtils.isEmpty(dictJsonArray)) {
					for(int i=0; i<dictJsonArray.size(); i++) {
						JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
						JsonObject option = new JsonObject();
						option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
						options.add(option);
					}
				}
				return options;
			}else {
				JsonArray dictJsonArray = DictCacheAbility.getInstance().getDictValues(column.getSystemId(), dictResourceId, dictCode);
				if(!JsonUtils.isEmpty(dictJsonArray)) {
					for(int i=0; i<dictJsonArray.size(); i++) {
						JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
						JsonObject option = new JsonObject();
						option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
						options.add(option);
					}
				}
				return options;
			}
		}else if(DataModelColumn.OPTION_TYPE_M.equals(column.getOptionType())) {//服务方法
			/*
			{
				"exposedServiceId": "",
				"params": [
					{
						"key": "",
						"value": ""
					}
				]
			}
			 */
			JsonObject optionCfg = JsonUtils.toJsonObject(column.getOptionCfg());
			
			if(!JsonUtils.isEmpty(optionCfg)) {
				String exposedServiceId = JsonUtils.getString(optionCfg, EXPOSED_SERVICE_ID_KEY);
                JsonArray params = JsonUtils.getJsonArray(optionCfg, PARAMS_KEY);
				ExposedService exposedService = this.exposedServiceSV.byId(exposedServiceId);
				if(exposedService == null) {
					logger.error("invalid exposedServiceId: {}", exposedServiceId);
					return null;
				}

				//构建服务调用命令对象
				ServiceCommand serviceCommand = new ServiceCommand();
				serviceCommand.setSystemId(column.getSystemId());
				serviceCommand.setTenantId(column.getTenantId());
				serviceCommand.setServiceId(exposedService.getServiceId());
				serviceCommand.setOperationId(exposedService.getOperationId());
				JsonObject reqData = new JsonObject();
				reqData.add(PARAMS_KEY, params);
				serviceCommand.setReqData(reqData);
				serviceCommand.setMultipartFileMap(null);
				
				//调用ApplicationSerivceProxy的execute方法，获得返回结果
				ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
				Object returnData = null;
				try {
					returnData = applicationServiceProxy.execute(serviceCommand, null);
				} catch (EngineException e) {
					logger.error("invoke exposed service failed!", e);
					return null;
				}
				if(returnData != null && !(returnData instanceof JsonArray)) {
					logger.error("invalid returnData type: {}!", returnData.getClass().getName());
					return null;
				}
				
				return (JsonArray)returnData;
			}
		}
		return null;
	}

    /**
     * 翻译列表信息
     * @param column
     * @return
     */
    private String translateForColumn(DataModelColumn column, String value) {
        if(StringUtils.isBlank(value)) {
            return StringUtils.emptyString();
        }
    	if(DataModelColumn.OPTION_TYPE_F.equals(column.getOptionType())) {//固定方式
            JsonArray options = JsonUtils.toJsonArray(column.getOptionCfg());
            if(!JsonUtils.isEmpty(options)) {
                for(int j=0; j<options.size(); j++) {
                    JsonObject option = options.get(j).getAsJsonObject();
                    if(value.equals(JsonUtils.getString(option, VALUE_KEY))) {
                        return JsonUtils.getString(option, LABEL_KEY);
                    }
                }
            }
        }else if(DataModelColumn.OPTION_TYPE_S.equals(column.getOptionType())) {//sql类型
    		/*如下配置
			{
				"sql": "service_attr_name label from pcc_service_attr where status = '1' and value = ?",
				"datasource": ""
			}
			*/
			JsonObject optionCfg = JsonUtils.toJsonObject(column.getOptionCfg());
			if(!JsonUtils.isEmpty(optionCfg)) {
				String querySql = JsonUtils.getString(optionCfg, SQL_KEY);
				String datasource = JsonUtils.getString(optionCfg, DATASOURCE_KEY);
				
				List<Object> valueList = new ArrayList<>();
                valueList.add(value);
				JsonArray result = ORMRepositoryAbility.getInstance().executeQuerySql(querySql, valueList, column.getSystemId(), datasource, column.getTenantId());
                String label = value;
                if(!JsonUtils.isEmpty(result)) {
                    label = JsonUtils.getString(result.get(0).getAsJsonObject(), LABEL_KEY);
                }
                return label;
			}
    	}else if(DataModelColumn.OPTION_TYPE_D.equals(column.getOptionType())) {//字典类型
			/*
			{
				"dictResourceId":"", 
				"dictCode":"",
				"dictValue":"",
				"parentDictCode":"",
				"parentDictValue":""
			}
			 */
			JsonObject dictOption = JsonUtils.toJsonObject(column.getOptionCfg());
			String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
			String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
			if(StringUtils.isBlank(dictResourceId) || StringUtils.isBlank(dictCode)) {//配置不完整时
				return null;
			}
			String dictValue  = value;
			if (!StringUtils.isBlank(dictValue)) {//取某个字典值的信息
				JsonObject dictJsonObject = DictCacheAbility.getInstance().getDictValue(column.getSystemId(), dictResourceId, dictCode, dictValue);
				String label = value;
                if(!JsonUtils.isEmpty(dictJsonObject)) {
					label = JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL);
				}
				return label;
			}
		}else if(DataModelColumn.OPTION_TYPE_M.equals(column.getOptionType())) {//服务方法
			/*
			{
				"exposedServiceId": "",
				"params": [
					{
						"key": "",
						"value": ""
					}
				]
			}
			 */
			JsonObject optionCfg = JsonUtils.toJsonObject(column.getOptionCfg());
			
			if(!JsonUtils.isEmpty(optionCfg)) {
				String exposedServiceId = JsonUtils.getString(optionCfg, EXPOSED_SERVICE_ID_KEY);
                JsonArray params = JsonUtils.getJsonArray(optionCfg, PARAMS_KEY);
                JsonObject valueObject = new JsonObject();
                valueObject.addProperty(VALUE_KEY, value);
                params.add(valueObject);
				ExposedService exposedService = this.exposedServiceSV.byId(exposedServiceId);
				if(exposedService == null) {
					logger.error("invalid exposedServiceId: {}", exposedServiceId);
					return null;
				}

				//构建服务调用命令对象
				ServiceCommand serviceCommand = new ServiceCommand();
				serviceCommand.setSystemId(column.getSystemId());
				serviceCommand.setTenantId(column.getTenantId());
				serviceCommand.setServiceId(exposedService.getServiceId());
				serviceCommand.setOperationId(exposedService.getOperationId());
				JsonObject reqData = new JsonObject();
				reqData.add(PARAMS_KEY, params);
				serviceCommand.setReqData(reqData);
				serviceCommand.setMultipartFileMap(null);
				
				//调用ApplicationSerivceProxy的execute方法，获得返回结果
				ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
				Object returnData = null;
				try {
					returnData = applicationServiceProxy.execute(serviceCommand, null);
				} catch (EngineException e) {
					logger.error("invoke exposed service failed!", e);
					return null;
				}
				if(returnData != null && !(returnData instanceof String)) {
					logger.error("invalid returnData type: {}!", returnData.getClass().getName());
					return null;
				}
				
				return (String)returnData;
			}
		}
		return null;
	}

    /**
     * 加载数据提供列配置的数据
     * @param loadDataApplyColumnDataReq
     * @return
     */
    public JsonObject loadDataApplyColumnDatas(LoadDataApplyColumnDataReq loadDataApplyColumnDataReq) throws EngineException{
        JsonObject result = new JsonObject();
        if(!JsonUtils.isEmpty(loadDataApplyColumnDataReq.getDataApplyColumns())) {
            //循环处理字段配置信息列表，获取每个配置对应的数据
            for(int i=0; i< loadDataApplyColumnDataReq.getDataApplyColumns().size(); i++) {
                JsonObject dataApplyColumn = loadDataApplyColumnDataReq.getDataApplyColumns().get(i).getAsJsonObject();
                String code = JsonUtils.getString(dataApplyColumn, CODE_KEY);//字段的code
                JsonObject displayCfg = JsonUtils.getJsonObject(dataApplyColumn, DISPLAY_CFG_KEY);
                String acquireType = JsonUtils.getString(displayCfg, ACQUIRE_TYPE_KEY);//获取类型
                if(ACQUIRE_TYPE_M.equals(acquireType)) {//暴露服务
                    String exposedServiceId = JsonUtils.getString(displayCfg, EXPOSED_SERVICE_ID_KEY);//暴露服务对象标识
                    ExposedService exposedService = this.exposedServiceSV.byId(exposedServiceId);
                    if(exposedService == null) {
                        logger.error("invalid exposedServiceId: {}", exposedServiceId);
                        return null;
                    }
                    //构建服务调用命令对象
                    ServiceCommand command = new ServiceCommand();
                    command.setServiceId(exposedService.getServiceId());
                    command.setOperationId(exposedService.getOperationId());
                    command.setSystemId(loadDataApplyColumnDataReq.getSystemId());//systemId
                    command.setTenantId(loadDataApplyColumnDataReq.getTenantId());//tenantId
                    JsonObject param = new JsonObject();
                    command.setReqData(param);
                    //调用编排的服务
                    Object obj = ServiceProxy.getProxy().execute(command, null);
                    if(obj != null) {
                        result.add(code, JsonUtils.toJsonElement(obj));//设置到result中
                    }else {
                        result.add(code, new JsonArray());//设置到result中
                    }
                }else if(ACQUIRE_TYPE_F.equals(acquireType)) {//固定模式
                    String jsonData = JsonUtils.getString(displayCfg, JSON_DATA_KEY);//固定配置的json数据
                    result.add(code, JsonUtils.toJsonArray(jsonData));
                }else {
                    throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid acquireType!");
                }
                
            }
        }
        return result;
    }

    /**
     * 加载数据集数据以及数据提供列配置的数据-设计态
     * @param loadDataApplyColumnDataReq
     * @return
     */
    public JsonObject loadDataWithDataApplyColumn(LoadDataApplyColumnDataReq loadDataApplyColumnDataReq) throws EngineException{
        JsonObject result = new JsonObject();
        if(loadDataApplyColumnDataReq.getLoadReportDataReq() != null) {
            JsonArray dataSetDatas = this.loadDataSetDatas(loadDataApplyColumnDataReq.getLoadReportDataReq());
            result.add(DATA_SET_DATAS_KEY, dataSetDatas);
        }

        result.add(DATA_APPLY_COLUMN_DATAS_KEY, this.loadDataApplyColumnDatas(loadDataApplyColumnDataReq));
        return result;
    }

    /**
     * 加载数据集数据以及数据提供列配置的数据-运行态
     * @param loadDataApplyColumnDataReq
     * @return
     */
    public JsonObject loadDataWithDataApplyColumnForEngine(LoadDataApplyColumnDataReq loadDataApplyColumnDataReq) throws EngineException{
        JsonObject result = new JsonObject();
        if(loadDataApplyColumnDataReq.getLoadReportDataReq() != null && !CollectionUtils.isEmpty(loadDataApplyColumnDataReq.getLoadReportDataReq().getDataSetDetails())) {
            JsonArray dataSetDatas = this.loadReportDatas(loadDataApplyColumnDataReq.getLoadReportDataReq());
            result.add(DATA_SET_DATAS_KEY, dataSetDatas);
        }
        result.add(DATA_APPLY_COLUMN_DATAS_KEY, this.loadDataApplyColumnDatas(loadDataApplyColumnDataReq));
        return result;
    }
}
