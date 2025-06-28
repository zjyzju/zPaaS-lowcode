package cn.zpaas.lowcode.fe.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.bi.doamin.service.DataSetService;
import cn.zpaas.lowcode.bi.proxy.DataSetProxy;
import cn.zpaas.lowcode.bi.vo.LoadDataApplyColumnDataReq;
import cn.zpaas.lowcode.bi.vo.LoadReportDataReq;
import cn.zpaas.lowcode.constant.ReportRegionAttrConst;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.TemplateType;
import cn.zpaas.lowcode.domain.eo.DataSetDetail;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.domain.service.FuncObjectAttrOptionsService;
import cn.zpaas.lowcode.fe.domain.service.ValidateRuleGroupService;
import cn.zpaas.lowcode.fe.proxy.ComboFuncDefineProxy;
import cn.zpaas.lowcode.fe.proxy.FuncDefineProxy;
import cn.zpaas.lowcode.fe.service.FeService;
import cn.zpaas.lowcode.fe.vo.ComboFuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncCustomizedLayoutVo;
import cn.zpaas.lowcode.fe.vo.FuncCustomizedPageVo;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncRegionAttrAssignVo;
import cn.zpaas.lowcode.fe.vo.FuncRegionVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;
import cn.zpaas.lowcode.fe.vo.ValidateRuleGroupVo;

/**
 * @author zjy
 * 前端领域平台服务实现类
 */
@Service
public class FeServiceImpl implements FeService {
	private static Logger logger = LoggerFactory.getLogger(FeService.class);

	@Autowired
	private FuncObjectAttrOptionsService funcObjectAttrOptionsService;

	@Autowired
	private ValidateRuleGroupService validateRuleGroupService;

	@Autowired
	private DataSetService dataSetService;
	
	@Override
	public JsonObject funcInit(String funcId) throws EngineException{
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefine(funcId);
		if(funcDefine == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "The function is not found!");
		}
		JsonObject funcDefineJson = JsonUtils.toJsonObject(funcDefine);

		if(TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType())) {
			//自定义功能
			JsonArray customizedPages = JsonUtils.getJsonArray(funcDefineJson, "customizedPages");
			if(!JsonUtils.isEmpty(customizedPages)) {
				for(int i=0; i<customizedPages.size(); i++) {
					JsonObject customizedPage = customizedPages.get(i).getAsJsonObject();
					JsonArray customizedLayouts = JsonUtils.getJsonArray(customizedPage, "customizedLayouts");
					if(!JsonUtils.isEmpty(customizedLayouts)) {
						this.processCustomizedFuncOptions(customizedLayouts, funcDefine.getSystemId());
					}
				}
			}
			
		}else {
			JsonArray funcRegions = JsonUtils.getJsonArray(funcDefineJson, "funcRegions");
			if(!JsonUtils.isEmpty(funcRegions)) {
				this.processTemplateFuncOptions(funcRegions, funcDefine.getSystemId());
			}
		}

		JsonArray objectAssigns = funcDefineJson.getAsJsonArray("objectAssigns");
		if(!JsonUtils.isEmpty(objectAssigns)) {
			for(int i=0; i<objectAssigns.size(); i++) {
				JsonObject objectAssign = objectAssigns.get(i).getAsJsonObject();
				JsonObject attrOptionMap = JsonUtils.getJsonObject(objectAssign, "attrOptionMap");
				if(!JsonUtils.isEmpty(attrOptionMap)) {
					for(String key : attrOptionMap.keySet()) {
						JsonObject attrOptions = JsonUtils.getJsonObject(attrOptionMap, key);
						//加载下拉列表信息
						funcObjectAttrOptionsService.loadOptions(attrOptions, funcDefine.getSystemId());
					}
				}
			}
		}

		return funcDefineJson;
	}

	private void processTemplateFuncOptions(JsonArray funcRegions, String systemId) {
		for(int i=0; i<funcRegions.size(); i++) {
			JsonArray regionOperations = JsonUtils.getJsonArray(funcRegions.get(i).getAsJsonObject(), "regionOperations");
			if(!JsonUtils.isEmpty(regionOperations)) {
				for(int j=0; j<regionOperations.size(); j++) {
					JsonObject regionOperation = regionOperations.get(j).getAsJsonObject();
					JsonObject operation = JsonUtils.getJsonObject(regionOperation, "operation");
					//处理可选值校验器
					if(operation != null && !StringUtils.isBlank(JsonUtils.getString(operation, "ruleGroupId"))) {
						try {
							ValidateRuleGroupVo validateRuleGroup = validateRuleGroupService.loadValidateRuleGroupForFront(systemId, JsonUtils.getString(operation, "ruleGroupId"));
							operation.add("validateRuleGroup", JsonUtils.toJsonObject(validateRuleGroup));
						}catch(EngineException e) {
							logger.error("load validateRuleGroup failed!", e);
						}
					}
				}
			}
		}
	}

	private void processCustomizedFuncOptions(JsonArray customizedLayouts, String systemId) {
		for(int i=0; i<customizedLayouts.size(); i++) {
			JsonObject customizedLayout = customizedLayouts.get(i).getAsJsonObject();
			JsonArray layoutRegions = JsonUtils.getJsonArray(customizedLayout, "layoutRegions");
			if(!JsonUtils.isEmpty(layoutRegions)) {
				for(int j=0; j<layoutRegions.size(); j++) {
					JsonObject layoutRegion = layoutRegions.get(j).getAsJsonObject();
					JsonObject funcRegion = JsonUtils.getJsonObject(layoutRegion, "funcRegion");
					if(funcRegion != null) {
						JsonArray regionOperations = JsonUtils.getJsonArray(funcRegion, "regionOperations");
						if(!JsonUtils.isEmpty(regionOperations)) {
							for(int k=0; k<regionOperations.size(); k++) {
								JsonObject regionOperation = regionOperations.get(k).getAsJsonObject();
								JsonObject operation = JsonUtils.getJsonObject(regionOperation, "operation");
								//处理可选值校验器
								if(operation != null && !StringUtils.isBlank(JsonUtils.getString(operation, "ruleGroupId"))) {
									try {
										ValidateRuleGroupVo validateRuleGroup = validateRuleGroupService.loadValidateRuleGroupForFront(systemId, JsonUtils.getString(operation, "ruleGroupId"));
										operation.add("validateRuleGroup", JsonUtils.toJsonObject(validateRuleGroup));
									}catch(EngineException e) {
										logger.error("load validateRuleGroup failed!", e);
									}
								}
							}
						}
					}
				}
			}

			JsonArray subCustomizedLayouts = JsonUtils.getJsonArray(customizedLayout, "subCustomizedLayouts");
			if(!JsonUtils.isEmpty(subCustomizedLayouts)) {
				this.processCustomizedFuncOptions(subCustomizedLayouts, systemId);
			}
		}
	}

	@Override
	public OperationVo loadOperationAll(String operationId) throws EngineException {
		return FuncDefineProxy.getInstance().getOperation(operationId);
	}
	
	@Override
	public ComboFuncDefineVo comboFuncInit(String comboFuncId) throws EngineException {
		return ComboFuncDefineProxy.getInstance().getComboFuncDefine(comboFuncId);
	}
	
	@Override
	public FuncDefineVo loadFuncDefineAndTpl(String funcId) {
		return FuncDefineProxy.getInstance().getFuncDefineAndTpl(funcId);
	}
	
	@Override
	public String loadLabelForObjectAttr(String attrOptionsId, String attrValue, String parentAttrValue, JsonArray params) {
		return funcObjectAttrOptionsService.loadLabelForObjectAttr(attrOptionsId, attrValue, parentAttrValue, params);
	}
	
	@Override
	public JsonArray loadOptionsForObjectAttr(String attrOptionsId, String parentAttrValue , JsonArray params) {
		return funcObjectAttrOptionsService.loadOptionsForObjectAttr(attrOptionsId, parentAttrValue, params);
	}

	@Override
	public JsonArray loadReportDatas(LoadReportDataReq loadReportDataReq) throws EngineException{
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefine(loadReportDataReq.getFuncId());
		if(funcDefine == null || (CollectionUtils.isEmpty(funcDefine.getFuncRegions()) && !TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType()))) {
			logger.error("invalid funcDefine! {}", loadReportDataReq.getFuncId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid funcDefine!");
		}
		FuncRegionVo funcRegion = null;
		if(TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType())) {
			funcRegion = this.filterFuncRegionForCustomizedFunc(funcDefine.getCustomizedPages(), loadReportDataReq.getFuncRegionId());
		}else {
			for(FuncRegionVo vo : funcDefine.getFuncRegions()) {
				if(vo.getId().equals(loadReportDataReq.getFuncRegionId())) {
					funcRegion = vo;
					break;
				}
			}
		}
		
		if(funcRegion == null) {
			logger.error("not found funcRegion! {}", loadReportDataReq.getFuncRegionId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found funcRegion!");
		}
		this.loadReportLeaves(funcRegion.getRegionAttrAssigns(), funcDefine.getSystemId(), loadReportDataReq);
		if(CollectionUtils.isEmpty(loadReportDataReq.getDataSetDetails())) {
			logger.error("not config effective dataSetDetail! {}", loadReportDataReq.getFuncId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "not config effective dataSetDetail!");
		}
		return this.dataSetService.loadReportDatas(loadReportDataReq);
	}

	/**
	 * 从自定义页面中筛选出符合的功能区域
	 * @param customizedPageVos
	 * @param funcRegionId
	 * @return
	 */
	private FuncRegionVo filterFuncRegionForCustomizedFunc(List<FuncCustomizedPageVo> customizedPageVos, String funcRegionId) {
		if(CollectionUtils.isEmpty(customizedPageVos)) {
			return null;
		}
		for(FuncCustomizedPageVo customizedPageVo : customizedPageVos) {
			FuncRegionVo funcRegionVo = this.filterFuncRegionForCustomizedLayout(customizedPageVo.getCustomizedLayouts(), funcRegionId);
			if(funcRegionVo != null) {
				return funcRegionVo;
			}
		}
		return null;
	}

	/**
	 * 从自定义布局从筛选出符合的功能区域
	 * @param customizedLayoutVos
	 * @param funcRegionId
	 * @return
	 */
	private FuncRegionVo filterFuncRegionForCustomizedLayout(List<FuncCustomizedLayoutVo> customizedLayoutVos, String funcRegionId) {
		if(CollectionUtils.isEmpty(customizedLayoutVos)) {
			return null;
		}
		for(FuncCustomizedLayoutVo customizedLayoutVo : customizedLayoutVos) {
			if(!CollectionUtils.isEmpty(customizedLayoutVo.getLayoutRegions()) && 
				customizedLayoutVo.getLayoutRegions().get(0).getFuncRegion() != null &&
				funcRegionId.equals(customizedLayoutVo.getLayoutRegions().get(0).getFuncRegion().getId())) {
					return customizedLayoutVo.getLayoutRegions().get(0).getFuncRegion();
			}

			if(!CollectionUtils.isEmpty(customizedLayoutVo.getSubCustomizedLayouts())) {
				FuncRegionVo funcRegionVo = this.filterFuncRegionForCustomizedLayout(customizedLayoutVo.getSubCustomizedLayouts(), funcRegionId);
				if(funcRegionVo != null) {
					return funcRegionVo;
				}
			}
		}
		return null;
	}


	/**
	 * 从功能区域绑定属性中获取报表属性的叶子节点对应的数据集明细列表
	 * @param attrAssignVos
	 * @param systemId
	 * @return
	 */
	private void loadReportLeaves(List<FuncRegionAttrAssignVo> attrAssignVos, String systemId, LoadReportDataReq loadReportDataReq) {
		if(CollectionUtils.isEmpty(attrAssignVos)) {
			return ;
		}
		List<DataSetDetail> reportLeaves = new ArrayList<>();
		JsonArray displayCfgs = new JsonArray();
		Set<String> notLeafAttrSet = new HashSet<>();//存放非叶子节点绑定属性标识
		Map<String, String> id2attrMap = new HashMap<>();//绑定属性标识-》属性标识（数据集明细标识）
		for(FuncRegionAttrAssignVo attrAssignVo : attrAssignVos) {
			String displayCfg = attrAssignVo.getDisplayCfg();
			if(StringUtils.isBlank(displayCfg)) {
				continue;
			}
			JsonObject displayCfgJSON = JsonUtils.toJsonObject(displayCfg);
			if(!attrAssignVo.getAttributeId().startsWith("N/A") && ReportRegionAttrConst.SUB_REGION_TYPE_R.equals(JsonUtils.getString(displayCfgJSON, "subRegionType"))) {//报表区且非N/A属性
				id2attrMap.put(attrAssignVo.getId(), attrAssignVo.getAttributeId());
				String parentAssignId = JsonUtils.getString(displayCfgJSON, "parentAssignId");
				if(!StringUtils.isBlank(parentAssignId)) {//非叶子节点属性
					notLeafAttrSet.add(parentAssignId);
				}
			}
		}
		//从获取中获取叶子节点的数据集明细
		for(FuncRegionAttrAssignVo attrAssignVo : attrAssignVos) {
			if(!notLeafAttrSet.contains(attrAssignVo.getId()) && id2attrMap.containsKey(attrAssignVo.getId())) {
				DataSetDetail dataSetDetail = DataSetProxy.getDataSetDetail(systemId, attrAssignVo.getAttributeId());
				if(dataSetDetail != null) {
					reportLeaves.add(dataSetDetail);
					displayCfgs.add(JsonUtils.toJsonObject(attrAssignVo.getDisplayCfg()));
				}
			}
		}
		loadReportDataReq.setDataSetDetails(reportLeaves);
		loadReportDataReq.setDisplayCfgs(displayCfgs);
	}

	@Override
	public JsonObject loadChartDatas(LoadReportDataReq loadReportDataReq) throws EngineException{
		FuncDefineVo funcDefine = FuncDefineProxy.getInstance().getFuncDefine(loadReportDataReq.getFuncId());
		if(funcDefine == null || (CollectionUtils.isEmpty(funcDefine.getFuncRegions()) && !TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType()))) {
			logger.error("invalid funcDefine! {}", loadReportDataReq.getFuncId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid funcDefine!");
		}
		FuncRegionVo funcRegion = null;
		if(TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType())) {
			funcRegion = this.filterFuncRegionForCustomizedFunc(funcDefine.getCustomizedPages(), loadReportDataReq.getFuncRegionId());
		}else {
			for(FuncRegionVo vo : funcDefine.getFuncRegions()) {
				if(vo.getId().equals(loadReportDataReq.getFuncRegionId())) {
					funcRegion = vo;
					break;
				}
			}
		}
		if(funcRegion == null) {
			logger.error("not found funcRegion! {}", loadReportDataReq.getFuncRegionId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found funcRegion!");
		}
		this.loadChartAttrs(funcRegion.getRegionAttrAssigns(), funcDefine.getSystemId(), loadReportDataReq);
		
		LoadDataApplyColumnDataReq loadDataApplyColumnDataReq = new LoadDataApplyColumnDataReq();
		loadDataApplyColumnDataReq.setSystemId(funcDefine.getSystemId());
		loadDataApplyColumnDataReq.setTenantId(funcDefine.getTenantId());
		loadDataApplyColumnDataReq.setLoadReportDataReq(loadReportDataReq);
		loadDataApplyColumnDataReq.setDataApplyColumns(this.loadDataApplyColumns(funcRegion.getRegionAttrAssigns()));
		return this.dataSetService.loadDataWithDataApplyColumnForEngine(loadDataApplyColumnDataReq);
	}

	/**
	 * 从功能区域绑定属性中获取图表对应的数据集明细列表
	 * @param attrAssignVos
	 * @param systemId
	 * @return
	 */
	private void loadChartAttrs(List<FuncRegionAttrAssignVo> attrAssignVos, String systemId, LoadReportDataReq loadReportDataReq) {
		if(CollectionUtils.isEmpty(attrAssignVos)) {
			return ;
		}
		List<DataSetDetail> chartAttrs = new ArrayList<>();
		JsonArray displayCfgs = new JsonArray();
		for(FuncRegionAttrAssignVo attrAssignVo : attrAssignVos) {
			String displayCfg = attrAssignVo.getDisplayCfg();
			if(StringUtils.isBlank(displayCfg)) {
				continue;
			}
			JsonObject displayCfgJSON = JsonUtils.toJsonObject(displayCfg);
			if(!attrAssignVo.getAttributeId().startsWith("N/A") && ReportRegionAttrConst.SUB_REGION_TYPE_C.equals(JsonUtils.getString(displayCfgJSON, "subRegionType"))) {//报表区且非N/A属性
				DataSetDetail dataSetDetail = DataSetProxy.getDataSetDetail(systemId, attrAssignVo.getAttributeId());
				if(dataSetDetail != null) {
					chartAttrs.add(dataSetDetail);
					displayCfgs.add(displayCfgJSON);
				}
			}
		}
		
		loadReportDataReq.setDataSetDetails(chartAttrs);
		loadReportDataReq.setDisplayCfgs(displayCfgs);
	}

	/**
	 * 从功能区域绑定属性中获取图表对应的数据提供列信息
	 * @param attrAssignVos
	 * @param systemId
	 * @return
	 */
	private JsonArray loadDataApplyColumns(List<FuncRegionAttrAssignVo> attrAssignVos) {
		if(CollectionUtils.isEmpty(attrAssignVos)) {
			return null;
		}
		JsonArray datatApplyColumns = new JsonArray();
		for(FuncRegionAttrAssignVo attrAssignVo : attrAssignVos) {
			String displayCfg = attrAssignVo.getDisplayCfg();
			if(StringUtils.isBlank(displayCfg)) {
				continue;
			}
			JsonObject displayCfgJSON = JsonUtils.toJsonObject(displayCfg);
			//配置了数据字典或数据提供列
			if(!StringUtils.isBlank(JsonUtils.getString(displayCfgJSON, DataSetService.ACQUIRE_TYPE_KEY))  || (attrAssignVo.getAttributeId().startsWith("N/A") && ReportRegionAttrConst.SUB_REGION_TYPE_C.equals(JsonUtils.getString(displayCfgJSON, "subRegionType")))) {//报表区且非N/A属性
				JsonObject dataApplyColumn = new JsonObject();
				dataApplyColumn.addProperty(DataSetService.CODE_KEY, attrAssignVo.getId());
				dataApplyColumn.add(DataSetService.DISPLAY_CFG_KEY, displayCfgJSON);
				datatApplyColumns.add(dataApplyColumn);
			}
		}
		return datatApplyColumns;
	}
}
