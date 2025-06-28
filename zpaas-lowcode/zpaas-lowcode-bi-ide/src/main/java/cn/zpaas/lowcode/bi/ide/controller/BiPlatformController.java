package cn.zpaas.lowcode.bi.ide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.be.engine.registry.ServiceRegistry;
import cn.zpaas.lowcode.bi.controller.BiController;
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
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.vo.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月28日-14:53:46
 */
@RestController
@RequestMapping("/platform/bi/api/")
public class BiPlatformController {
    @Autowired
    private BiPlatformService service;

	@Autowired
	private BiController biController;

	@Autowired
	private ServiceRegistry serviceRegistry;

    //登录信息存放的Key
	@Value("${init.param.loginInfoKey:loginInfo}")
	private  String loginInfoKey;

	@RequestMapping("cache/reload/{systemId}")
	public ResponseResult<Boolean> reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		biController.reloadCache(systemId, request);
		return ResponseResult.success(true);
	}

	@RequestMapping("cache/clusterReload/{systemId}")
	public ResponseResult<Boolean> reloadClusterCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		serviceRegistry.reloadClusterCache("/platform/bi/api/cache/reload/"+systemId, request.getCookies());
		return ResponseResult.success(true);
	}

    @RequestMapping("businessSystem/loadBiInfo/{systemId}")
	public ResponseResult<BusinessSystemBiInfo> loadBusinessSystemInfo(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(service.loadBusinessSystemBiInfo(systemId));
	}

    @RequestMapping("dataModel/add")
	public ResponseResult<DataModel> addDataModel(@RequestBody DataModel dataModel, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
        return ResponseResult.success(service.addDataModel(dataModel));
    }

    @RequestMapping("dataModel/save")
	public ResponseResult<DataModel> saveDataModel(@RequestBody DataModel dataModel, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
        return ResponseResult.success(service.saveDataModel(dataModel));
    }

    @RequestMapping("dataModel/loadWithColumn/{dataModelId}")
	public ResponseResult<DataModel> loadDataModelWithColumn(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException {
		DataModel dataModel =  service.loadDataModelWithColumn(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
		}
		return ResponseResult.success(dataModel);
	}

	@RequestMapping("dataModel/loadAll/{dataModelId}")
	public ResponseResult<DataModel> loadDataModelAll(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException {
		DataModel dataModel =  service.loadDataModelAll(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
		}
		return ResponseResult.success(dataModel);
	}

    @RequestMapping("dataModel/delete/{dataModelId}")
	public ResponseResult<Integer> deleteDataModel(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException{
		DataModel dataModel = service.loadDataModel(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
			return ResponseResult.success(service.deleteDataModel(dataModelId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataModel/list/{systemId}")
	public ResponseResult<List<DataModel>> listDataModelBySystem(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(service.listDataModelBySystem(systemId));
	}

    @RequestMapping("dataModel/loadDataAndSchema")
	public ResponseResult<DataModelSchemaDataVo> loadDataAndShcema(@RequestBody DataModel dataModel, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
        return ResponseResult.success(service.loadDataAndShcema(dataModel));
    }

	@RequestMapping("dataModelMetrics/add")
	public ResponseResult<DataModelMetrics> addDataModelMetrics(@RequestBody DataModelMetrics dataModelMetrics, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelMetrics.getSystemId(), dataModelMetrics.getTenantId());
        return ResponseResult.success(service.addDataModelMetrics(dataModelMetrics));
    }

    @RequestMapping("dataModelMetrics/save")
	public ResponseResult<DataModelMetrics> saveDataModelMetrics(@RequestBody DataModelMetrics dataModelMetrics, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelMetrics.getSystemId(), dataModelMetrics.getTenantId());
        return ResponseResult.success(service.saveDataModelMetrics(dataModelMetrics));
    }

    @RequestMapping("dataModelMetrics/delete/{dataModelMetricsId}")
	public ResponseResult<Integer> deleteDataModelMetrics(@PathVariable String dataModelMetricsId, HttpServletRequest request) throws EngineException{
		DataModelMetrics dataModelMetrics = service.loadDataModelMetrics(dataModelMetricsId);
		if(dataModelMetrics != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelMetrics.getSystemId(), dataModelMetrics.getTenantId());
			return ResponseResult.success(service.deleteDataModelMetrics(dataModelMetricsId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataModelMetrics/byId/{dataModelMetricsId}")
	public ResponseResult<DataModelMetrics> loadDataModelMetrics(@PathVariable String dataModelMetricsId, HttpServletRequest request) throws EngineException{
		DataModelMetrics dataModelMetrics = service.loadDataModelMetrics(dataModelMetricsId);
		if(dataModelMetrics != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelMetrics.getSystemId(), dataModelMetrics.getTenantId());
			return ResponseResult.success(dataModelMetrics);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelMetrics/listByDataModel/{dataModelId}")
	public ResponseResult<List<DataModelMetrics>> listDataModelMetricsByDataModel(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException{
		DataModel dataModel = service.loadDataModel(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
			return ResponseResult.success(service.listDataModelMetricsByDataModel(dataModelId));
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelTag/add")
	public ResponseResult<DataModelTag> addDataModelTag(@RequestBody DataModelTag dataModelTag, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelTag.getSystemId(), dataModelTag.getTenantId());
        return ResponseResult.success(service.addDataModelTag(dataModelTag));
    }

    @RequestMapping("dataModelTag/save")
	public ResponseResult<DataModelTag> saveDataModelTag(@RequestBody DataModelTag dataModelTag, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelTag.getSystemId(), dataModelTag.getTenantId());
        return ResponseResult.success(service.saveDataModelTag(dataModelTag));
    }

    @RequestMapping("dataModelTag/delete/{dataModelTagId}")
	public ResponseResult<Integer> deleteDataModelTag(@PathVariable String dataModelTagId, HttpServletRequest request) throws EngineException{
		DataModelTag dataModelTag = service.loadDataModelTag(dataModelTagId);
		if(dataModelTag != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelTag.getSystemId(), dataModelTag.getTenantId());
			return ResponseResult.success(service.deleteDataModelTag(dataModelTagId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataModelTag/byId/{dataModelTagId}")
	public ResponseResult<DataModelTag> loadDataModelTag(@PathVariable String dataModelTagId, HttpServletRequest request) throws EngineException{
		DataModelTag dataModelTag = service.loadDataModelTag(dataModelTagId);
		if(dataModelTag != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelTag.getSystemId(), dataModelTag.getTenantId());
			return ResponseResult.success(dataModelTag);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelTag/listByDataModel/{dataModelId}")
	public ResponseResult<List<DataModelTag>> listDataModelTagByDataModel(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException{
		DataModel dataModel = service.loadDataModel(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
			return ResponseResult.success(service.listDataModelTagByDataModel(dataModelId));
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelFilter/add")
	public ResponseResult<DataModelFilter> addDataModelFilter(@RequestBody DataModelFilter dataModelFilter, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilter.getSystemId(), dataModelFilter.getTenantId());
        return ResponseResult.success(service.addDataModelFilter(dataModelFilter));
    }

    @RequestMapping("dataModelFilter/save")
	public ResponseResult<DataModelFilter> saveDataModelFilter(@RequestBody DataModelFilter dataModelFilter, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilter.getSystemId(), dataModelFilter.getTenantId());
        return ResponseResult.success(service.saveDataModelFilter(dataModelFilter));
    }

    @RequestMapping("dataModelFilter/delete/{dataModelFilterId}")
	public ResponseResult<Integer> deleteDataModelFilter(@PathVariable String dataModelFilterId, HttpServletRequest request) throws EngineException{
		DataModelFilter dataModelFilter = service.loadDataModelFilter(dataModelFilterId);
		if(dataModelFilter != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilter.getSystemId(), dataModelFilter.getTenantId());
			return ResponseResult.success(service.deleteDataModelFilter(dataModelFilterId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataModelFilter/byId/{dataModelFilterId}")
	public ResponseResult<DataModelFilter> loadDataModelFilter(@PathVariable String dataModelFilterId, HttpServletRequest request) throws EngineException{
		DataModelFilter dataModelFilter = service.loadDataModelFilter(dataModelFilterId);
		if(dataModelFilter != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilter.getSystemId(), dataModelFilter.getTenantId());
			return ResponseResult.success(dataModelFilter);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelFilter/listByDataModel/{dataModelId}")
	public ResponseResult<List<DataModelFilter>> listDataModelFilterByDataModel(@PathVariable String dataModelId, HttpServletRequest request) throws EngineException{
		DataModel dataModel = service.loadDataModel(dataModelId);
		if(dataModel != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModel.getSystemId(), dataModel.getTenantId());
			return ResponseResult.success(service.listDataModelFilterByDataModel(dataModelId));
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataModelFilterValue/add")
	public ResponseResult<DataModelFilterValue> addDataModelFilterValue(@RequestBody DataModelFilterValue dataModelFilterValue, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilterValue.getSystemId(), dataModelFilterValue.getTenantId());
        return ResponseResult.success(service.addDataModelFilterValue(dataModelFilterValue));
    }

    @RequestMapping("dataModelFilterValue/save")
	public ResponseResult<DataModelFilterValue> saveDataModelFilterValue(@RequestBody DataModelFilterValue dataModelFilterValue, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilterValue.getSystemId(), dataModelFilterValue.getTenantId());
        return ResponseResult.success(service.saveDataModelFilterValue(dataModelFilterValue));
    }

    @RequestMapping("dataModelFilterValue/delete/{dataModelFilterValueId}")
	public ResponseResult<Integer> deleteDataModelFilterValue(@PathVariable String dataModelFilterValueId, HttpServletRequest request) throws EngineException{
		DataModelFilterValue dataModelFilterValue = service.loadDataModelFilterValue(dataModelFilterValueId);
		if(dataModelFilterValue != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilterValue.getSystemId(), dataModelFilterValue.getTenantId());
			return ResponseResult.success(service.deleteDataModelFilterValue(dataModelFilterValueId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataModelFilterValue/byId/{dataModelFilterValueId}")
	public ResponseResult<DataModelFilterValue> loadDataModelFilterValue(@PathVariable String dataModelFilterValueId, HttpServletRequest request) throws EngineException{
		DataModelFilterValue dataModelFilterValue = service.loadDataModelFilterValue(dataModelFilterValueId);
		if(dataModelFilterValue != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataModelFilterValue.getSystemId(), dataModelFilterValue.getTenantId());
			return ResponseResult.success(dataModelFilterValue);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataSet/add")
	public ResponseResult<DataSet> addDataSet(@RequestBody DataSet dataSet, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataSet.getSystemId(), dataSet.getTenantId());
        return ResponseResult.success(service.addDataSet(dataSet));
    }

    @RequestMapping("dataSet/save")
	public ResponseResult<DataSet> saveDataSet(@RequestBody DataSet dataSet, HttpServletRequest request)  throws EngineException{
        LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataSet.getSystemId(), dataSet.getTenantId());
        return ResponseResult.success(service.saveDataSet(dataSet));
    }

    @RequestMapping("dataSet/loadAll/{dataSetId}")
	public ResponseResult<DataSet> loadDataSetAll(@PathVariable String dataSetId, HttpServletRequest request) throws EngineException {
		DataSet dataSet =  service.loadDataSetAll(dataSetId);
		if(dataSet != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataSet.getSystemId(), dataSet.getTenantId());
		}
		return ResponseResult.success(dataSet);
	}

    @RequestMapping("dataSet/delete/{dataSetId}")
	public ResponseResult<Integer> deleteDataSet(@PathVariable String dataSetId, HttpServletRequest request) throws EngineException{
		DataSet dataSet = service.loadDataSet(dataSetId);
		if(dataSet != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataSet.getSystemId(), dataSet.getTenantId());
			return ResponseResult.success(service.deleteDataSet(dataSetId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dataSet/list/{systemId}")
	public ResponseResult<List<DataSet>> listDataSetBySystem(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(service.listDataSetBySystem(systemId));
	}

	@RequestMapping("dataSet/listDetails/{dataSetId}")
	public ResponseResult<List<DataSetDetail>> listDataSetDetails(@PathVariable String dataSetId, HttpServletRequest request)  throws EngineException{
		DataSet dataSet = service.loadDataSet(dataSetId);
		if(dataSet != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataSet.getSystemId(), dataSet.getTenantId());
			return ResponseResult.success(service.listDataSetDetails(dataSetId));
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dataSet/loadData")
	public ResponseResult<JsonArray> loadDataSetDatas(@RequestBody LoadReportDataReq loadReportDataReq, HttpServletRequest request) throws EngineException{
		if(CollectionUtils.isEmpty(loadReportDataReq.getDataSetDetails())) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "dataSetDetails is null!");
		}
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, loadReportDataReq.getDataSetDetails().get(0).getSystemId());
		return ResponseResult.success(service.loadDataSetDatas(loadReportDataReq));
	}

	@RequestMapping("dataSet/loadDataWithDataApplyColumn")
	public ResponseResult<JsonObject> loadDataWithDataApplyColumn(@RequestBody LoadDataApplyColumnDataReq loadDataApplyColumnDataReq, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, loadDataApplyColumnDataReq.getSystemId());
		return ResponseResult.success(service.loadDataWithDataApplyColumn(loadDataApplyColumnDataReq));
	}
}
