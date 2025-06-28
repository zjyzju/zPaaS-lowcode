package cn.zpaas.lowcode.bi.ide.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.be.ide.service.PlatformService;
import cn.zpaas.lowcode.bi.doamin.service.DataSetService;
import cn.zpaas.lowcode.bi.ide.service.BiPlatformService;
import cn.zpaas.lowcode.bi.ide.vo.BusinessSystemBiInfo;
import cn.zpaas.lowcode.bi.ide.vo.DataModelSchemaDataVo;
import cn.zpaas.lowcode.bi.vo.LoadDataApplyColumnDataReq;
import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;
import cn.zpaas.lowcode.constant.ResultStatus;
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
 * BI领域平台服务实现类
 */
@Service
public class BiPlatformServiceImpl implements BiPlatformService {
	public static Logger logger = LoggerFactory.getLogger(BiPlatformServiceImpl.class);

	@Autowired
	private DataModel dataModelSV;

	@Autowired
	private DataModelMetrics dataModelMetricsSV;

	@Autowired
	private DataModelTag dataModelTagSV;

	@Autowired
	private DataModelFilter dataModelFilterSV;

	@Autowired
	private DataModelFilterValue dataModelFilterValueSV;

	@Autowired
	private DataSet dataSetSV;

	@Autowired
	private DataSetService dataSetService;

	@Override
	public BusinessSystemBiInfo loadBusinessSystemBiInfo(String systemId) {
		BusinessSystemBiInfo info = new BusinessSystemBiInfo();
		info.setDataModels(this.dataModelSV.list(systemId));
		info.setDataSets(this.dataSetSV.list(systemId));
		return info;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModel addDataModel(DataModel dataModel) {
		if(this.dataModelSV.add(dataModel) > 0) {
			return dataModel;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModel saveDataModel(DataModel dataModel) throws EngineException{
		if(this.dataModelSV.save(dataModel) > 0) {
			return dataModel;
		}else {
			return null;
		}
	}

	@Override
	public DataModel loadDataModelAll(String dataModelId) {
		return this.dataModelSV.loadAll(dataModelId);
	}

	@Override
	public DataModel loadDataModelWithColumn(String dataModelId) {
		return this.dataModelSV.loadWithColumn(dataModelId);
	}

	@Override
	public DataModel loadDataModel(String dataModelId) {
		return this.dataModelSV.byId(dataModelId);
	}

	@Override
	public List<DataModel> listDataModelBySystem(String systemId) {
		return this.dataModelSV.list(systemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataModel(String dataModelId) {
		return this.dataModelSV.delete(dataModelId);
	}

	@Override
	public DataModelSchemaDataVo loadDataAndShcema(DataModel dataModel)  throws EngineException{
		if(dataModel == null || StringUtils.isBlank(dataModel.getDataSourceId()) 
				|| StringUtils.isBlank(dataModel.getDataSourceType()) || StringUtils.isBlank(dataModel.getType())
				|| StringUtils.isBlank(dataModel.getPhysicalSrcObject())) {
			logger.error("config info of dataModel is not valid!");
			throw new EngineException(ResultStatus.BAD_REQUEST, "config info of dataModel is not valid!");
		}

		DataModelSchemaDataVo schemaDataVo = new DataModelSchemaDataVo();
		switch(dataModel.getType()) {
			case DataModel.DATA_MODEL_TYPE_TABLE:
			case DataModel.DATA_MODEL_TYPE_FILE:
				if(DataModel.DATA_MODEL_DATA_SOURCE_TYPE_RDBMS.equals(dataModel.getDataSourceType())) {//关系数据库
					PlatformService bePlatformService = SpringContextUtils.getBean(PlatformService.class);
					List<MdaColumn> columns = bePlatformService.queryColumnList(dataModel.getSystemId(), dataModel.getDataSourceId(), dataModel.getPhysicalSrcObject());
					schemaDataVo.setDataModelSchemas(columns);
					String sql = "select * from " + dataModel.getPhysicalSrcObject() + " limit 0, 10";
					JsonArray datas = ORMRepositoryAbility.getInstance().executeQuerySql(sql, null, dataModel.getSystemId(), dataModel.getDataSourceId(), dataModel.getTenantId());
					schemaDataVo.setDatas(datas);
					return schemaDataVo;
				}else if(DataModel.DATA_MODEL_DATA_SOURCE_TYPE_ES.equals(dataModel.getDataSourceType())) {
					return null;
				}else if(DataModel.DATA_MODEL_DATA_SOURCE_TYPE_ES.equals(dataModel.getDataSourceType())) {
					return null;
				}else {
					logger.error("invalid dataModel dataSourceType({})!", dataModel.getDataSourceType());
					throw new EngineException(ResultStatus.BAD_REQUEST, "invalid dataModel dataSourceType!");
				}
			case DataModel.DATA_MODEL_TYPE_SQL:

				return null;
			default:
				logger.error("invalid dataModel type({})!", dataModel.getType());
				throw new EngineException(ResultStatus.BAD_REQUEST, "invalid dataModel type!");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelMetrics addDataModelMetrics(DataModelMetrics dataModelMetrics) {
		if(dataModelMetricsSV.add(dataModelMetrics) > 0) {
			return dataModelMetrics;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataModelMetrics(String id) {
		return this.dataModelMetricsSV.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelMetrics saveDataModelMetrics(DataModelMetrics dataModelMetrics) {
		if(this.dataModelMetricsSV.update(dataModelMetrics) > 0) {
			return dataModelMetrics;
		}else {
			return null;
		}
	}

	@Override
	public DataModelMetrics loadDataModelMetrics(String id) {
		return this.dataModelMetricsSV.byId(id);
	}

	@Override
	public List<DataModelMetrics> listDataModelMetricsByDataModel(String dataModelId) {
		return this.dataModelMetricsSV.list(dataModelId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelTag addDataModelTag(DataModelTag dataModelTag) {
		if(dataModelTagSV.add(dataModelTag) > 0) {
			return dataModelTag;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataModelTag(String id) {
		return this.dataModelTagSV.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelTag saveDataModelTag(DataModelTag dataModelTag) {
		if(this.dataModelTagSV.update(dataModelTag) > 0) {
			return dataModelTag;
		}else {
			return null;
		}
	}

	@Override
	public DataModelTag loadDataModelTag(String id) {
		return this.dataModelTagSV.byId(id);
	}

	@Override
	public List<DataModelTag> listDataModelTagByDataModel(String dataModelId) {
		return this.dataModelTagSV.list(dataModelId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelFilter addDataModelFilter(DataModelFilter dataModelFilter) throws EngineException{
		if(dataModelFilterSV.add(dataModelFilter) > 0) {
			return dataModelFilter;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataModelFilter(String id) {
		return this.dataModelFilterSV.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelFilter saveDataModelFilter(DataModelFilter dataModelFilter) {
		if(this.dataModelFilterSV.update(dataModelFilter) > 0) {
			return dataModelFilter;
		}else {
			return null;
		}
	}

	@Override
	public DataModelFilter loadDataModelFilter(String id) {
		return this.dataModelFilterSV.loadWithValues(id);
	}

	@Override
	public List<DataModelFilter> listDataModelFilterByDataModel(String dataModelId) {
		return this.dataModelFilterSV.list(dataModelId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelFilterValue addDataModelFilterValue(DataModelFilterValue dataModelFilterValue) {
		if(dataModelFilterValueSV.add(dataModelFilterValue) > 0) {
			return dataModelFilterValue;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataModelFilterValue(String id) {
		return this.dataModelFilterValueSV.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataModelFilterValue saveDataModelFilterValue(DataModelFilterValue dataModelFilterValue) {
		if(this.dataModelFilterValueSV.update(dataModelFilterValue) > 0) {
			return dataModelFilterValue;
		}else {
			return null;
		}
	}

	@Override
	public DataModelFilterValue loadDataModelFilterValue(String id) {
		return this.dataModelFilterValueSV.byId(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataSet addDataSet(DataSet dataSet) throws EngineException{
		if(dataSetSV.add(dataSet) > 0) {
			return dataSet;
		}else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteDataSet(String id) {
		return this.dataSetSV.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public DataSet saveDataSet(DataSet dataSet) throws EngineException {
		if(this.dataSetSV.save(dataSet) > 0) {
			return dataSet;
		}else {
			return null;
		}
	}

	@Override
	public DataSet loadDataSet(String id) {
		return this.dataSetSV.byId(id);
	}

	@Override
	public DataSet loadDataSetAll(String id) {
		return this.dataSetSV.loadAll(id);
	}

	@Override
	public List<DataSet> listDataSetBySystem(String systemId) {
		return this.dataSetSV.list(systemId);
	}

	@Override
	public List<DataSetDetail> listDataSetDetails(String dataSetId){
		return this.dataSetSV.listDetails(dataSetId);
	}

	@Override
	public JsonArray loadDataSetDatas(LoadReportDataReq loadReportDataReq) throws EngineException{
		return this.dataSetService.loadDataSetDatas(loadReportDataReq);
	}

	@Override
	public JsonObject loadDataWithDataApplyColumn(LoadDataApplyColumnDataReq loadDataApplyColumnDataReq) throws EngineException {
		return this.dataSetService.loadDataWithDataApplyColumn(loadDataApplyColumnDataReq);
	}
}
