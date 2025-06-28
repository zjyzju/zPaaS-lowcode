package cn.zpaas.lowcode.bi.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.bi.service.BiInitService;
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
 * 数据集代理
 *
 * @author zjy
 * createTime 2025年04月30日-11:03:07
 */
public class DataSetProxy {
    private static Logger logger = LoggerFactory.getLogger(DataSetProxy.class);

	// DataSetProxy实例，用于实现单例模式
	private static DataSetProxy instance = null;

	// 用于缓存数据集对象，第一层Key为systemId，第二层Key为DataSetId
	private Map<String, Map<String, DataSet>> dataSetMap = new HashMap<>();
	// 用于缓存数据集明细对象，便于使用，第一层Key为systemId，第二层Key为DataSetDetailId
	private Map<String, Map<String, DataSetDetail>> dataSetDetailMap = new HashMap<>();
	// 用于缓存数据集构成对象，便于使用，第一层Key为systemId，第二层Key为DataSetCompositionId
	private Map<String, Map<String, DataSetComposition>> dataSetCompositionMap = new HashMap<>();
	// 用于缓存数据集涉及模型Join关系对象，便于使用，第一层Key为systemId，第二层Key为dataSetCompositionJoinId
	private Map<String, Map<String, DataSetCompositionJoin>> dataSetCompositionJoinMap = new HashMap<>();

    // 用于缓存数据模型对象，第一层Key为systemId，第二层Key为DataModelId
	private Map<String, Map<String, DataModel>> dataModelMap = new HashMap<>();
	// 用于缓存数据模型字段对象，便于使用，第一层Key为systemId，第二层Key为DataModelColumnId
	private Map<String, Map<String, DataModelColumn>> dataModelColumnMap = new HashMap<>();
    // 用于缓存数据模型指标对象，便于使用，第一层Key为systemId，第二层Key为DataModelMetricId
	private Map<String, Map<String, DataModelMetrics>> dataModelMetricsMap = new HashMap<>();
    // 用于缓存数据模型标签对象，便于使用，第一层Key为systemId，第二层Key为DataModelTagId
	private Map<String, Map<String, DataModelTag>> dataModelTagMap = new HashMap<>();
    // 用于缓存数据模型筛选器对象，便于使用，第一层Key为systemId，第二层Key为DataModelFilterId
	private Map<String, Map<String, DataModelFilter>> dataModelFilterMap = new HashMap<>();
	// 用于缓存数据模型筛选值对象，便于使用，第一层Key为systemId，第二层Key为DataModelFilterValueId
	private Map<String, Map<String, DataModelFilterValue>> dataModelFilterValueMap = new HashMap<>();
	

    /**
	 * DataSetProxy类的初始化方法
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void init(String systemId, String tenantId, BiInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		// 实例化DataSetProxy对象
		DataSetProxy proxy = new DataSetProxy();

        List<DataModel> dataModels = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			dataModels = initService.listDataModels();
		} else {// 加载指定业务系统的数据
			dataModels = initService.listDataModels(systemId);
		}

		if (!CollectionUtils.isEmpty(dataModels)) {
			// 加载所有的字段信息
			List<DataModelColumn> dataModelColumns = null;
			if(StringUtils.isBlank(systemId)) {
				dataModelColumns = initService.listDataModelColumns();
			}else {
				dataModelColumns = initService.listDataModelColumns(systemId);
			}
			if(dataModelColumns == null) {
				dataModelColumns = new ArrayList<>();
			}
            // 将字段加入缓存
			if (!CollectionUtils.isEmpty(dataModelColumns)) {
				for (DataModelColumn dataModelColumn : dataModelColumns) {
					// 当字段的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelColumn.getSystemId())) {
						logger.info("dataModelColumn's systemId is null. {}", dataModelColumn.getId());
						continue;
					}
					Map<String, DataModelColumn> columnMap = proxy.dataModelColumnMap.get(dataModelColumn.getSystemId());
					if (columnMap == null) {
						columnMap = new HashMap<>();
						proxy.dataModelColumnMap.put(dataModelColumn.getSystemId(), columnMap);
					}
					columnMap.put(dataModelColumn.getId(), dataModelColumn);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelColumn>> columnMapForDataModel = dataModelColumns.stream().collect(Collectors.groupingBy(DataModelColumn::getDataModelId));
			
            // 加载所有的指标信息
			List<DataModelMetrics> dataModelMetrics = null;
			if(StringUtils.isBlank(systemId)) {
				dataModelMetrics = initService.listDataModelMetrics();
			}else {
				dataModelMetrics = initService.listDataModelMetrics(systemId);
			}
			if(dataModelMetrics == null) {
				dataModelMetrics = new ArrayList<>();
			}
            // 将指标加入缓存
			if (!CollectionUtils.isEmpty(dataModelMetrics)) {
				for (DataModelMetrics dataModelMetric : dataModelMetrics) {
					// 当指标的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelMetric.getSystemId())) {
						logger.info("dataModelMetric's systemId is null. {}", dataModelMetric.getId());
						continue;
					}
					Map<String, DataModelMetrics> metricsMap = proxy.dataModelMetricsMap.get(dataModelMetric.getSystemId());
					if (metricsMap == null) {
						metricsMap = new HashMap<>();
						proxy.dataModelMetricsMap.put(dataModelMetric.getSystemId(), metricsMap);
					}
					metricsMap.put(dataModelMetric.getId(), dataModelMetric);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelMetrics>> metricMapForDataModel = dataModelMetrics.stream().collect(Collectors.groupingBy(DataModelMetrics::getDataModelId));

            // 加载所有的标签信息
			List<DataModelTag> dataModelTags = null;
			if(StringUtils.isBlank(systemId)) {
				dataModelTags = initService.listDataModelTags();
			}else {
				dataModelTags = initService.listDataModelTags(systemId);
			}
			if(dataModelTags == null) {
				dataModelTags = new ArrayList<>();
			}
            // 将标签加入缓存
			if (!CollectionUtils.isEmpty(dataModelTags)) {
				for (DataModelTag dataModelTag : dataModelTags) {
					// 当标签的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelTag.getSystemId())) {
						logger.info("dataModelTag's systemId is null. {}", dataModelTag.getId());
						continue;
					}
					Map<String, DataModelTag> tagMap = proxy.dataModelTagMap.get(dataModelTag.getSystemId());
					if (tagMap == null) {
						tagMap = new HashMap<>();
						proxy.dataModelTagMap.put(dataModelTag.getSystemId(), tagMap);
					}
					tagMap.put(dataModelTag.getId(), dataModelTag);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelTag>> tagMapForDataModel = dataModelTags.stream().collect(Collectors.groupingBy(DataModelTag::getDataModelId));
			
            // 加载所有的筛选器信息
			List<DataModelFilter> dataModelFilters = null;
			if(StringUtils.isBlank(systemId)) {
				dataModelFilters = initService.listDataModelFilters();
			}else {
				dataModelFilters = initService.listDataModelFilters(systemId);
			}
			if(dataModelFilters == null) {
				dataModelFilters = new ArrayList<>();
			}
            // 将筛选器加入缓存
			if (!CollectionUtils.isEmpty(dataModelFilters)) {

                // 加载所有的筛选值信息
                List<DataModelFilterValue> dataModelFilterValues = null;
                if(StringUtils.isBlank(systemId)) {
                    dataModelFilterValues = initService.listDataModelFilterValues();
                }else {
                    dataModelFilterValues = initService.listDataModelFilterValues(systemId);
                }
                if(dataModelFilterValues == null) {
                    dataModelFilterValues = new ArrayList<>();
                }

				for (DataModelFilterValue dataModelFilterValue : dataModelFilterValues) {
					// 当筛选值的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelFilterValue.getSystemId())) {
						logger.info("dataModelFilterValue's systemId is null. {}", dataModelFilterValue.getId());
						continue;
					}
                    Map<String, DataModelFilterValue> filterValueMap = proxy.dataModelFilterValueMap.get(dataModelFilterValue.getSystemId());
					if (filterValueMap == null) {
						filterValueMap = new HashMap<>();
						proxy.dataModelFilterValueMap.put(dataModelFilterValue.getSystemId(), filterValueMap);
					}
					filterValueMap.put(dataModelFilterValue.getId(), dataModelFilterValue);
				}

                //按归属的对象进行分组，形成Map
			    Map<String, List<DataModelFilterValue>> filterValueMapMapForFilter = dataModelFilterValues.stream().collect(Collectors.groupingBy(DataModelFilterValue::getFilterId));
			
				for (DataModelFilter dataModelFilter : dataModelFilters) {
					// 当筛选器的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelFilter.getSystemId())) {
						logger.info("dataModelFilter's systemId is null. {}", dataModelFilter.getId());
						continue;
					}
                    dataModelFilter.setFilterValues(filterValueMapMapForFilter.get(dataModelFilter.getId()));
					Map<String, DataModelFilter> filterMap = proxy.dataModelFilterMap.get(dataModelFilter.getSystemId());
					if (filterMap == null) {
						filterMap = new HashMap<>();
						proxy.dataModelFilterMap.put(dataModelFilter.getSystemId(), filterMap);
					}
					filterMap.put(dataModelFilter.getId(), dataModelFilter);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelFilter>> filterMapForDataModel = dataModelFilters.stream().collect(Collectors.groupingBy(DataModelFilter::getDataModelId));
			
            // 循环处理每一条数据模型对象
			for (DataModel dataModel : dataModels) {
				// 当对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(dataModel.getSystemId())) {
					logger.info("dataModel's systemId is null. {}", dataModel.getId());
					continue;
				}
				// 将数据模型加入缓存
				Map<String, DataModel> map = proxy.dataModelMap.get(dataModel.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					proxy.dataModelMap.put(dataModel.getSystemId(), map);
				}
                dataModel.setColumns((columnMapForDataModel.get(dataModel.getId())));
                dataModel.setMetrics((metricMapForDataModel.get(dataModel.getId())));
                dataModel.setTags((tagMapForDataModel.get(dataModel.getId())));
                dataModel.setFilters((filterMapForDataModel.get(dataModel.getId())));
				map.put(dataModel.getId(), dataModel);
			}
		} else {
			logger.info("no valid DataSet.");
		}

		List<DataSet> dataSets = null;
		if (StringUtils.isBlank(systemId)) {// 加载所有数据
			dataSets = initService.listDataSets();
		} else {// 加载指定业务系统的数据
			dataSets = initService.listDataSets(systemId);
		}

		if (!CollectionUtils.isEmpty(dataSets)) {
			// 加载所有的明细信息
			List<DataSetDetail> dataSetDetails = null;
			if(StringUtils.isBlank(systemId)) {
				dataSetDetails = initService.listDataSetDetails();
			}else {
				dataSetDetails = initService.listDataSetDetails(systemId);
			}
			if(dataSetDetails == null) {
				dataSetDetails = new ArrayList<>();
			}

            // 将明细加入缓存
			if (!CollectionUtils.isEmpty(dataSetDetails)) {
				for (DataSetDetail dataSetDetail : dataSetDetails) {
					// 当明细的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataSetDetail.getSystemId())) {
						logger.info("dataSetDetail's systemId is null. {}", dataSetDetail.getId());
						continue;
					}
					Map<String, DataSetDetail> detailMap = proxy.dataSetDetailMap.get(dataSetDetail.getSystemId());
					if (detailMap == null) {
						detailMap = new HashMap<>();
						proxy.dataSetDetailMap.put(dataSetDetail.getSystemId(), detailMap);
					}
                    if(DataSetDetail.DETAIL_TYPE_METRICS.equals(dataSetDetail.getDetailType())) {//指标
                        Map<String, DataModelMetrics> map = proxy.dataModelMetricsMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else if(DataSetDetail.DETAIL_TYPE_TAG.equals(dataSetDetail.getDetailType())) {//标签
                        Map<String, DataModelTag> map = proxy.dataModelTagMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else if(DataSetDetail.DETAIL_TYPE_FILTER.equals(dataSetDetail.getDetailType())) {//筛选器
                        Map<String, DataModelFilter> map = proxy.dataModelFilterMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else {//字段
                        Map<String, DataModelColumn> map = proxy.dataModelColumnMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }
					detailMap.put(dataSetDetail.getId(), dataSetDetail);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataSetDetail>> detailMapForDataSet = dataSetDetails.stream().collect(Collectors.groupingBy(DataSetDetail::getDataSetId));
			
			// 加载所有的构成信息
			List<DataSetComposition> dataSetCompositions = null;
			if(StringUtils.isBlank(systemId)) {
				dataSetCompositions = initService.listDataSetCompositions();
			}else {
				dataSetCompositions = initService.listDataSetCompositions(systemId);
			}
			if(dataSetCompositions == null) {
				dataSetCompositions = new ArrayList<>();
			}

            // 将构成加入缓存
			if (!CollectionUtils.isEmpty(dataSetCompositions)) {
				// 加载所有的涉及模型Join信息
				List<DataSetCompositionJoin> dataSetCompositionJoins = null;
				if(StringUtils.isBlank(systemId)) {
					dataSetCompositionJoins = initService.listDataSetCompositionJoins();
				}else {
					dataSetCompositionJoins = initService.listDataSetCompositionJoins(systemId);
				}
				if(dataSetCompositionJoins == null) {
					dataSetCompositionJoins = new ArrayList<>();
				}

				// 将Join信息加入缓存
				if (!CollectionUtils.isEmpty(dataSetCompositionJoins)) {
					for (DataSetCompositionJoin dataSetCompositionJoin : dataSetCompositionJoins) {
						// 当Join信息的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(dataSetCompositionJoin.getSystemId())) {
							logger.info("dataSetCompositionJoin's systemId is null. {}", dataSetCompositionJoin.getId());
							continue;
						}
						Map<String, DataSetCompositionJoin> compositionJoinMap = proxy.dataSetCompositionJoinMap.get(dataSetCompositionJoin.getSystemId());
						if (compositionJoinMap == null) {
							compositionJoinMap = new HashMap<>();
							proxy.dataSetCompositionJoinMap.put(dataSetCompositionJoin.getSystemId(), compositionJoinMap);
						}
						
						compositionJoinMap.put(dataSetCompositionJoin.getId(), dataSetCompositionJoin);
					}
				}
				//按归属的对象进行分组，形成Map
				Map<String, List<DataSetCompositionJoin>> compositionJoinMapForComposition = dataSetCompositionJoins.stream().collect(Collectors.groupingBy(DataSetCompositionJoin::getJoinMainCompoId));

				for (DataSetComposition dataSetComposition : dataSetCompositions) {
					// 当构成的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataSetComposition.getSystemId())) {
						logger.info("dataSetComposition's systemId is null. {}", dataSetComposition.getId());
						continue;
					}
					Map<String, DataSetComposition> compositionMap = proxy.dataSetCompositionMap.get(dataSetComposition.getSystemId());
					if (compositionMap == null) {
						compositionMap = new HashMap<>();
						proxy.dataSetCompositionMap.put(dataSetComposition.getSystemId(), compositionMap);
					}
                    dataSetComposition.setCompositionJoins(compositionJoinMapForComposition.get(dataSetComposition.getId()));
					compositionMap.put(dataSetComposition.getId(), dataSetComposition);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataSetComposition>> compositionMapForDataSet = dataSetCompositions.stream().collect(Collectors.groupingBy(DataSetComposition::getDataSetId));
			

			// 循环处理每一条数据集对象
			for (DataSet dataSet : dataSets) {
				// 当对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(dataSet.getSystemId())) {
					logger.info("dataSet's systemId is null. {}", dataSet.getId());
					continue;
				}
				// 将领域对象加入缓存
				Map<String, DataSet> map = proxy.dataSetMap.get(dataSet.getSystemId());
				if (map == null) {
					map = new HashMap<>();
					proxy.dataSetMap.put(dataSet.getSystemId(), map);
				}
                dataSet.setDetails(detailMapForDataSet.get(dataSet.getId()));
				dataSet.setCompositions(compositionMapForDataSet.get(dataSet.getId()));
				map.put(dataSet.getId(), dataSet);
			}
		} else {
			logger.info("no valid DataSet.");
		}

		// 初始化完成将proxy赋值给instance属性
		instance = proxy;

		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}

	/**
	 * 重新加载缓存
	 * 
	 * @param systemId    业务系统标识
	 * @param tenantId    租户标识
	 * @param initService 获取初始化数据的服务
	 */
	public static synchronized void reloadCache(String systemId, String tenantId, BiInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method start...");
		}

        List<DataModel> dataModels = initService.listDataModels(systemId);
        Map<String, DataModel> modelMap = new HashMap<>();
        Map<String, DataModelColumn> columnMap = new HashMap<>();
        Map<String, DataModelMetrics> metricMap = new HashMap<>();
        Map<String, DataModelTag> tagMap = new HashMap<>();
        Map<String, DataModelFilter> filterMap = new HashMap<>();
		Map<String, DataModelFilterValue> filterValueMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(dataModels)) {
            // 加载所有的字段信息
			List<DataModelColumn> dataModelColumns = initService.listDataModelColumns(systemId);
			if(dataModelColumns == null) {
				dataModelColumns = new ArrayList<>();
			}
            // 将字段加入缓存
			if (!CollectionUtils.isEmpty(dataModelColumns)) {
				for (DataModelColumn dataModelColumn : dataModelColumns) {
					// 当字段的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelColumn.getSystemId())) {
						logger.info("dataModelColumn's systemId is null. {}", dataModelColumn.getId());
						continue;
					}
					columnMap.put(dataModelColumn.getId(), dataModelColumn);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelColumn>> columnMapForDataModel = dataModelColumns.stream().collect(Collectors.groupingBy(DataModelColumn::getDataModelId));
			
            // 加载所有的指标信息
			List<DataModelMetrics> dataModelMetrics = initService.listDataModelMetrics(systemId);
			if(dataModelMetrics == null) {
				dataModelMetrics = new ArrayList<>();
			}
            // 将指标加入缓存
			if (!CollectionUtils.isEmpty(dataModelMetrics)) {
				for (DataModelMetrics dataModelMetric : dataModelMetrics) {
					// 当指标的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelMetric.getSystemId())) {
						logger.info("dataModelMetric's systemId is null. {}", dataModelMetric.getId());
						continue;
					}
					metricMap.put(dataModelMetric.getId(), dataModelMetric);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelMetrics>> metricMapForDataModel = dataModelMetrics.stream().collect(Collectors.groupingBy(DataModelMetrics::getDataModelId));

            // 加载所有的标签信息
			List<DataModelTag> dataModelTags = initService.listDataModelTags(systemId);
			if(dataModelTags == null) {
				dataModelTags = new ArrayList<>();
			}
            // 将标签加入缓存
			if (!CollectionUtils.isEmpty(dataModelTags)) {
				for (DataModelTag dataModelTag : dataModelTags) {
					// 当标签的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelTag.getSystemId())) {
						logger.info("dataModelTag's systemId is null. {}", dataModelTag.getId());
						continue;
					}
					tagMap.put(dataModelTag.getId(), dataModelTag);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelTag>> tagMapForDataModel = dataModelTags.stream().collect(Collectors.groupingBy(DataModelTag::getDataModelId));
			
            // 加载所有的筛选器信息
			List<DataModelFilter> dataModelFilters = initService.listDataModelFilters(systemId);
			if(dataModelFilters == null) {
				dataModelFilters = new ArrayList<>();
			}
            // 将筛选器加入缓存
			if (!CollectionUtils.isEmpty(dataModelFilters)) {

                // 加载所有的筛选值信息
                List<DataModelFilterValue> dataModelFilterValues = initService.listDataModelFilterValues(systemId);
                if(dataModelFilterValues == null) {
                    dataModelFilterValues = new ArrayList<>();
                }
				for (DataModelFilterValue dataModelFilterValue : dataModelFilterValues) {
					// 当筛选值的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelFilterValue.getSystemId())) {
						logger.info("dataModelFilterValue's systemId is null. {}", dataModelFilterValue.getId());
						continue;
					}
                    filterValueMap.put(dataModelFilterValue.getId(), dataModelFilterValue);
				}

                //按归属的对象进行分组，形成Map
			    Map<String, List<DataModelFilterValue>> filterValueMapMapForFilter = dataModelFilterValues.stream().collect(Collectors.groupingBy(DataModelFilterValue::getFilterId));
			
				for (DataModelFilter dataModelFilter : dataModelFilters) {
					// 当筛选器的业务系统标识为空时，忽略本条记录
					if (StringUtils.isBlank(dataModelFilter.getSystemId())) {
						logger.info("dataModelFilter's systemId is null. {}", dataModelFilter.getId());
						continue;
					}
                    dataModelFilter.setFilterValues(filterValueMapMapForFilter.get(dataModelFilter.getId()));
					filterMap.put(dataModelFilter.getId(), dataModelFilter);
				}
			}
            //按归属的对象进行分组，形成Map
			Map<String, List<DataModelFilter>> filterMapForDataModel = dataModelFilters.stream().collect(Collectors.groupingBy(DataModelFilter::getDataModelId));
			
            // 循环处理每一条数据模型对象
			for (DataModel dataModel : dataModels) {
				// 当对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(dataModel.getSystemId())) {
					logger.info("dataModel's systemId is null. {}", dataModel.getId());
					continue;
				}
				dataModel.setColumns((columnMapForDataModel.get(dataModel.getId())));
                dataModel.setMetrics((metricMapForDataModel.get(dataModel.getId())));
                dataModel.setTags((tagMapForDataModel.get(dataModel.getId())));
                dataModel.setFilters((filterMapForDataModel.get(dataModel.getId())));
				modelMap.put(dataModel.getId(), dataModel);
			}
		} else {
			logger.info("no valid DataSet.");
		}

        instance.dataModelMap.put(systemId, modelMap);
		instance.dataModelColumnMap.put(systemId, columnMap);
        instance.dataModelMetricsMap.put(systemId, metricMap);
        instance.dataModelTagMap.put(systemId, tagMap);
        instance.dataModelFilterMap.put(systemId, filterMap);
		instance.dataModelFilterValueMap.put(systemId, filterValueMap);

		List<DataSet> dataSets = initService.listDataSets(systemId);
		Map<String, DataSet> dataSetMap = new HashMap<>();
		Map<String, DataSetDetail> detailMap = new HashMap<>();
		Map<String, DataSetComposition> compositionMap = new HashMap<>();
		Map<String, DataSetCompositionJoin> compositionJoinMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(dataSets)) {
			// 加载所有的明细信息
			List<DataSetDetail> dataSetDetails = initService.listDataSetDetails(systemId);
			if(dataSetDetails == null) {
				dataSetDetails = new ArrayList<>();
			}
			// 将属性加入属性缓存
            if (!CollectionUtils.isEmpty(dataSetDetails)) {
                for (DataSetDetail dataSetDetail : dataSetDetails) {
                    // 当明细的业务系统标识为空时，忽略本条记录
                    if (StringUtils.isBlank(dataSetDetail.getSystemId())) {
                        logger.info("dataSetDetail's systemId is null. {}", dataSetDetail.getId());
                        continue;
                    }
					if(DataSetDetail.DETAIL_TYPE_METRICS.equals(dataSetDetail.getDetailType())) {//指标
                        Map<String, DataModelMetrics> map = instance.dataModelMetricsMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else if(DataSetDetail.DETAIL_TYPE_TAG.equals(dataSetDetail.getDetailType())) {//标签
                        Map<String, DataModelTag> map = instance.dataModelTagMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else if(DataSetDetail.DETAIL_TYPE_FILTER.equals(dataSetDetail.getDetailType())) {//筛选器
                        Map<String, DataModelFilter> map = instance.dataModelFilterMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }else {//字段
                        Map<String, DataModelColumn> map = instance.dataModelColumnMap.get(dataSetDetail.getSystemId());
                        if(map != null) {
                            dataSetDetail.setContent(map.get(dataSetDetail.getDetailContentId()));
                        }
                    }
                    detailMap.put(dataSetDetail.getId(), dataSetDetail);
                }
            }
            //按明细的对象进行分组，形成Map
			Map<String, List<DataSetDetail>> detailMapForDataSet = dataSetDetails.stream().collect(Collectors.groupingBy(DataSetDetail::getDataSetId));

			// 加载所有的构成信息
			List<DataSetComposition> dataSetCompositions = initService.listDataSetCompositions(systemId);
			if(dataSetCompositions == null) {
				dataSetCompositions = new ArrayList<>();
			}
			// 将构成加入属性缓存
            if (!CollectionUtils.isEmpty(dataSetCompositions)) {
				// 加载所有的Join信息
				List<DataSetCompositionJoin> dataSetCompositionJoins = initService.listDataSetCompositionJoins(systemId);
				if(dataSetCompositionJoins == null) {
					dataSetCompositionJoins = new ArrayList<>();
				}
				// 将Join加入缓存
				if (!CollectionUtils.isEmpty(dataSetCompositionJoins)) {
					for (DataSetCompositionJoin dataSetCompositionJoin : dataSetCompositionJoins) {
						// 当Join的业务系统标识为空时，忽略本条记录
						if (StringUtils.isBlank(dataSetCompositionJoin.getSystemId())) {
							logger.info("dataSetCompositionJoin's systemId is null. {}", dataSetCompositionJoin.getId());
							continue;
						}
						
						compositionJoinMap.put(dataSetCompositionJoin.getId(), dataSetCompositionJoin);
					}
				}
				//按Join的对象进行分组，形成Map
				Map<String, List<DataSetCompositionJoin>> compositionJoinMapForDataSet = dataSetCompositionJoins.stream().collect(Collectors.groupingBy(DataSetCompositionJoin::getJoinMainCompoId));

                for (DataSetComposition dataSetComposition : dataSetCompositions) {
                    // 当构成的业务系统标识为空时，忽略本条记录
                    if (StringUtils.isBlank(dataSetComposition.getSystemId())) {
                        logger.info("dataSetComposition's systemId is null. {}", dataSetComposition.getId());
                        continue;
                    }
					dataSetComposition.setCompositionJoins(compositionJoinMapForDataSet.get(dataSetComposition.getId()));
                    compositionMap.put(dataSetComposition.getId(), dataSetComposition);
                }
            }
            //按构成的对象进行分组，形成Map
			Map<String, List<DataSetComposition>> compositionMapForDataSet = dataSetCompositions.stream().collect(Collectors.groupingBy(DataSetComposition::getDataSetId));

			// 循环处理每一条数据集对象
			for (DataSet dataSet : dataSets) {
				// 当数据集对象的业务系统标识为空时，忽略本条记录
				if (StringUtils.isBlank(dataSet.getSystemId())) {
					logger.info("domainObject's systemId is null. {}", dataSet.getId());
					continue;
				}
                dataSet.setDetails(detailMapForDataSet.get(dataSet.getId()));
				dataSet.setCompositions(compositionMapForDataSet.get(dataSet.getId()));
				dataSetMap.put(dataSet.getId(), dataSet);
			}
		} 
		instance.dataSetMap.put(systemId, dataSetMap);
		instance.dataSetDetailMap.put(systemId, detailMap);
		instance.dataSetCompositionMap.put(systemId, compositionMap);
		instance.dataSetCompositionJoinMap.put(systemId, compositionJoinMap);

		if (logger.isDebugEnabled()) {
			logger.debug("reloadCache method end...");
		}
	}

    /**
     * 私有化构造函数
     */
    private DataSetProxy() {

    }

    /**
     * 获取单例实例
     * @return
     */
    public static DataSetProxy getInstance() {
        return instance;
    }

    /**
     * 获取数据集缓存
     * @param systemId
     * @param dataSetId
     * @return
     */
    public static DataSet getDataSet(String systemId, String dataSetId) {
        Map<String, DataSet> map = instance.dataSetMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataSetId);
    }

    /**
     * 获取数据集明细缓存
     * @param systemId
     * @param dataSetDetailId
     * @return
     */
    public static DataSetDetail getDataSetDetail(String systemId, String dataSetDetailId) {
        Map<String, DataSetDetail> map = instance.dataSetDetailMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataSetDetailId);
    }

	/**
     * 获取数据集构成缓存
     * @param systemId
     * @param dataSetCompositionId
     * @return
     */
    public static DataSetComposition getDataSetComposition(String systemId, String dataSetCompositionId) {
        Map<String, DataSetComposition> map = instance.dataSetCompositionMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataSetCompositionId);
    }

	/**
     * 获取数据集涉及模型Join信息缓存
     * @param systemId
     * @param dataSetCompositionJoinId
     * @return
     */
    public static DataSetCompositionJoin getDataSetCompositionJoin(String systemId, String dataSetCompositionJoinId) {
        Map<String, DataSetCompositionJoin> map = instance.dataSetCompositionJoinMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataSetCompositionJoinId);
    }

    /**
     * 获取数据模型缓存
     * @param systemId
     * @param dataModelId
     * @return
     */
    public static DataModel getDataModel(String systemId, String dataModelId) {
        Map<String, DataModel> map = instance.dataModelMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelId);
    }

    /**
     * 获取数据模型字段缓存
     * @param systemId
     * @param dataModelColumnId
     * @return
     */
    public static DataModelColumn getDataModelColumn(String systemId, String dataModelColumnId) {
        Map<String, DataModelColumn> map = instance.dataModelColumnMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelColumnId);
    }

    /**
     * 获取数据模型指标缓存
     * @param systemId
     * @param dataModelMetricId
     * @return
     */
    public static DataModelMetrics getDataModelMetric(String systemId, String dataModelMetricId) {
        Map<String, DataModelMetrics> map = instance.dataModelMetricsMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelMetricId);
    }

    /**
     * 获取数据模型标签缓存
     * @param systemId
     * @param dataModelTagId
     * @return
     */
    public static DataModelTag getDataModelTag(String systemId, String dataModelTagId) {
        Map<String, DataModelTag> map = instance.dataModelTagMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelTagId);
    }

    /**
     * 获取数据模型筛选器缓存
     * @param systemId
     * @param dataModelFilterId
     * @return
     */
    public static DataModelFilter getDataModelFilter(String systemId, String dataModelFilterId) {
        Map<String, DataModelFilter> map = instance.dataModelFilterMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelFilterId);
    }

	/**
     * 获取数据模型筛选值缓存
     * @param systemId
     * @param dataModelFilterValueId
     * @return
     */
    public static DataModelFilterValue getDataModelFilterValue(String systemId, String dataModelFilterValueId) {
        Map<String, DataModelFilterValue> map = instance.dataModelFilterValueMap.get(systemId);
        if(map == null) {
            return null;
        }
        return map.get(dataModelFilterValueId);
    }
}
