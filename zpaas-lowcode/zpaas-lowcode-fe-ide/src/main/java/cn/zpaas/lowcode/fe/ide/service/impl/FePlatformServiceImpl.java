package cn.zpaas.lowcode.fe.ide.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.FuncDefineGenerateRequest;
import cn.zpaas.lowcode.constant.ReportRegionAttrConst;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.ComboFuncDefine;
import cn.zpaas.lowcode.domain.eo.ComboFuncTab;
import cn.zpaas.lowcode.domain.eo.ComboFuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayout;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedPage;
import cn.zpaas.lowcode.domain.eo.FuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.domain.eo.FuncRegion;
import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionOperation;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.domain.service.FuncDefineService;
import cn.zpaas.lowcode.fe.ide.domain.service.FuncDefineGenerator;
import cn.zpaas.lowcode.fe.ide.service.FePlatformService;
import cn.zpaas.lowcode.fe.ide.vo.BusinessSystemFeInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionChartDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionReportDesignInfo;

/**
 * @author zjy
 * 前端领域平台服务实现类
 */
@Service
public class FePlatformServiceImpl implements FePlatformService {
	public static Logger logger = LoggerFactory.getLogger(FePlatformServiceImpl.class);
	
	@Autowired
	private FuncDefine funcDefineSV;
	
	@Autowired
	private FuncObjectAssign funcObjectAssignSV;
	
	@Autowired
	private FuncObjectAttrOptions funcObjectAttrOptionsSV;
	
	@Autowired
	private FuncRegion funcRegionSV;
	
	@Autowired 
	private FuncRegionAttrAssign funcRegionAttrAssignSV;
	
	@Autowired
	private FuncRegionOperation funcRegionOperationSV;
	
	@Autowired
	private FuncTemplateRegionOperation funcTemplateRegionOperationSV;
	
	@Autowired
	private FuncTemplate funcTemplateSV;
	
	@Autowired
	private ComboFuncDefine comboFuncDefineSV;
	
	@Autowired
	private ComboFuncTemplate comboFuncTemplateSV;
	
	@Autowired
	private ComboFuncTab comboFuncTabSV;

	@Autowired
	private FuncDefineGenerator funcDefineGenerator;
	
	@Autowired
	private FuncDefineService funcDefineService;

	@Autowired
	private FuncCustomizedPage funcCustomizedPageSV;

	@Override
	public BusinessSystemFeInfo loadBusinessSystemFeInfo(String businessSystemId) {
		BusinessSystemFeInfo info = new BusinessSystemFeInfo();
		List<FuncDefine> funcDefines = funcDefineSV.listWithTplBySystem(businessSystemId);
		if(!CollectionUtils.isEmpty(funcDefines)) {
			List<FuncDefine> main = new ArrayList<>();
			List<FuncDefine> sub = new ArrayList<>();
			for(FuncDefine funcDefine : funcDefines) {
				if(YesOrNo.NO.equals(funcDefine.getFuncTemplate().getIsMainFunc())) {
					sub.add(funcDefine);
				}else {
					main.add(funcDefine);
				}
			}
			info.setFuncDefines(main);
			info.setSubFuncDefines(sub);
		}
		
		info.setComboFuncDefines(comboFuncDefineSV.list(businessSystemId));
		return info;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncDefine generateFuncDefineAll(FuncDefineGenerateRequest request) throws EngineException{
		return funcDefineGenerator.generateFuncDefineAll(request);
	}

	@Override
	public FuncDefine queryFuncDefine(String funcId) {
		return funcDefineSV.byId(funcId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteFuncDefine(String funcId) {
		return funcDefineSV.delete(funcId);
	}

	@Override
	public FuncDefine loadFuncDefineAll(String funcId) throws EngineException{
		return funcDefineService.loadFuncDefineAll(funcId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncDefine saveFuncDefine(FuncDefine funcDefine) {
		return funcDefineSV.save(funcDefine);
	}
	
	@Override
	public List<FuncDefine> listFuncDefines(String systemId) {
		return funcDefineSV.listBySystem(systemId);
	}

	@Override
	public List<FuncDefine> listTplFuncDefines(String systemId) {
		return funcDefineSV.listTplFuncBySystem(systemId);
	}

	@Override
	public List<FuncDefine> listFuncCustomizedDefines(String systemId) {
		return funcDefineSV.listCustomizedFuncBySystem(systemId);
	}
	
	@Override
	public FuncRegion loadFuncRegionAll(String funcRegionId) {
		return funcRegionSV.loadAllById(funcRegionId);
	}
	
	@Override
	public List<FuncRegion> listFuncRegions(String systemId, String funcId) {
		return funcRegionSV.listByFuncId(systemId, funcId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegion saveFuncRegion(FuncRegion funcRegion) {
		return funcRegionSV.save(funcRegion);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegion saveFuncRegionCfg(FuncRegion funcRegion) {
		return funcRegionSV.saveRegionCfg(funcRegion);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveFuncRegionDesignInfo(FuncRegionDesignInfo funcRegionDesignInfo) throws EngineException{
		if(funcRegionDesignInfo == null || StringUtils.isBlank(funcRegionDesignInfo.getFuncId()) || StringUtils.isBlank(funcRegionDesignInfo.getFuncRegionId())) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "funcRegionDesignInfo funcId and funcRegionId can't be null!");
		}
		funcRegionAttrAssignSV.delete(funcRegionDesignInfo.getFuncId(), funcRegionDesignInfo.getFuncRegionId());
		funcRegionAttrAssignSV.batchSave(funcRegionDesignInfo.getAttrAssigns());
		funcRegionOperationSV.delete(funcRegionDesignInfo.getFuncId(), funcRegionDesignInfo.getFuncRegionId());
		funcRegionOperationSV.batchSave(funcRegionDesignInfo.getRegionOperations());
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveFuncRegionReportDesignInfo(FuncRegionReportDesignInfo funcRegionDesignInfo) throws EngineException{
		if(funcRegionDesignInfo == null || StringUtils.isBlank(funcRegionDesignInfo.getFuncId()) || StringUtils.isBlank(funcRegionDesignInfo.getFuncRegionId())) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "funcRegionDesignInfo funcId and funcRegionId can't be null!");
		}
		funcRegionAttrAssignSV.delete(funcRegionDesignInfo.getFuncId(), funcRegionDesignInfo.getFuncRegionId());
		Integer index = 1;
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getFilterAttrs())) {
			for(FuncRegionAttrAssign attr : funcRegionDesignInfo.getFilterAttrs()) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_F);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcRegionDesignInfo.getFuncId());
				attr.setFuncRegionId(funcRegionDesignInfo.getFuncRegionId());
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "filter attr saveFuncRegionAttrAssign failed!");
				}
			}
		}
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getTagAttrs())) {
			for(FuncRegionAttrAssign attr : funcRegionDesignInfo.getTagAttrs()) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_T);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcRegionDesignInfo.getFuncId());
				attr.setFuncRegionId(funcRegionDesignInfo.getFuncRegionId());
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "tag attr saveFuncRegionAttrAssign failed!");
				}
			}
		}
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getReportAttrs())) {
			this.saveRegionReportAttr(funcRegionDesignInfo.getReportAttrs(), index, null, funcRegionDesignInfo.getFuncId(), funcRegionDesignInfo.getFuncRegionId());
		}
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveFuncRegionChartDesignInfo(FuncRegionChartDesignInfo funcRegionDesignInfo) throws EngineException{
		if(funcRegionDesignInfo == null || StringUtils.isBlank(funcRegionDesignInfo.getFuncId()) || StringUtils.isBlank(funcRegionDesignInfo.getFuncRegionId())) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "funcRegionDesignInfo funcId and funcRegionId can't be null!");
		}
		FuncRegion funcRegion = funcRegionSV.byId(funcRegionDesignInfo.getFuncRegionId());
		JsonObject regionCfgJSON = null;
		if(StringUtils.isBlank(funcRegion.getRegionCfg())) {
			regionCfgJSON = new JsonObject();
		}else {
			regionCfgJSON = JsonUtils.toJsonObject(funcRegion.getRegionCfg());
		}
		regionCfgJSON.addProperty(FuncRegion.CHART_COMPONENT_KEY, funcRegionDesignInfo.getChartComponent());
		funcRegion.setRegionCfg(JsonUtils.toJson(regionCfgJSON));
		if(funcRegionSV.saveRegionCfg(funcRegion) == null) {
			throw new EngineException(ResultStatus.BAD_REQUEST, "save funcRegionCfg failed!");
		}
		funcRegionAttrAssignSV.delete(funcRegionDesignInfo.getFuncId(), funcRegionDesignInfo.getFuncRegionId());
		Integer index = 1;
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getFilterAttrs())) {
			for(FuncRegionAttrAssign attr : funcRegionDesignInfo.getFilterAttrs()) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_F);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcRegionDesignInfo.getFuncId());
				attr.setFuncRegionId(funcRegionDesignInfo.getFuncRegionId());
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "filter attr saveFuncRegionAttrAssign failed!");
				}
			}
		}
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getTagAttrs())) {
			for(FuncRegionAttrAssign attr : funcRegionDesignInfo.getTagAttrs()) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_T);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcRegionDesignInfo.getFuncId());
				attr.setFuncRegionId(funcRegionDesignInfo.getFuncRegionId());
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "tag attr saveFuncRegionAttrAssign failed!");
				}
			}
		}
		if(!CollectionUtils.isEmpty(funcRegionDesignInfo.getChartAttrs())) {
			for(FuncRegionAttrAssign attr : funcRegionDesignInfo.getChartAttrs()) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_C);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcRegionDesignInfo.getFuncId());
				attr.setFuncRegionId(funcRegionDesignInfo.getFuncRegionId());
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "chart attr saveFuncRegionAttrAssign failed!");
				}
			}
		}
		return 1;
	}

	/**
	 * 递归保存统计报表表格属性信息
	 * @param attrs
	 * @param index
	 * @param parentAssignId
	 */
	private void saveRegionReportAttr(List<FuncRegionAttrAssign> attrs, Integer index, String parentAssignId, String funcId, String funcRegionId)throws EngineException {
		if(!CollectionUtils.isEmpty(attrs)) {
			for(FuncRegionAttrAssign attr : attrs) {
				attr.setDisplayOrder(index++);
				JsonObject displayCfg = null;
				if(StringUtils.isBlank(attr.getDisplayCfg())) {
					displayCfg = new JsonObject();
				}else {
					displayCfg = JsonUtils.toJsonObject(attr.getDisplayCfg());
				}
				displayCfg.addProperty(ReportRegionAttrConst.SUB_REGION_TYPE_KEY, ReportRegionAttrConst.SUB_REGION_TYPE_R);
				displayCfg.addProperty(ReportRegionAttrConst.PARENT_ASSIGN_ID_KEY, parentAssignId);
				attr.setDisplayCfg(JsonUtils.toJson(displayCfg));
				attr.setFuncId(funcId);
				attr.setFuncRegionId(funcRegionId);
				if(funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attr) <= 0) {
					throw new EngineException(ResultStatus.BAD_REQUEST, "report attr saveFuncRegionAttrAssign failed!");
				}
				if(!CollectionUtils.isEmpty(attr.getAttrAssigns())) {
					this.saveRegionReportAttr(attr.getAttrAssigns(), index, attr.getId(), funcId, funcRegionId);
				}
			}
		}
	}

	@Override
	public FuncObjectAssign loadFuncObjectAssignAll(String objectAssignId) throws EngineException {
		return funcObjectAssignSV.loadAllById(objectAssignId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteFuncObjectAssign(String objectAssignId) {
		return funcObjectAssignSV.deleteById(objectAssignId);
	}

	@Override
	public FuncObjectAssign queryFuncObjectAssign(String objectAssignId) {
		return funcObjectAssignSV.byId(objectAssignId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public List<FuncObjectAttrOptions> copyFuncObjectAttrOptionsFromOther(String fromFuncObjectAssignId, String targetFuncObjectAssignId) {
		return funcObjectAssignSV.copyFuncObjectAttrOptionsFromOther(fromFuncObjectAssignId, targetFuncObjectAssignId);
	}

	public List<FuncObjectAssign> listFuncObjectAssign(String funcId) throws EngineException {
		return funcObjectAssignSV.listWithAssignObjectByFunc(funcId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncObjectAssign saveFuncObjectAssign(FuncObjectAssign objectAssign)throws EngineException {
		if(funcObjectAssignSV.save(objectAssign) != null) {
			return funcObjectAssignSV.loadAllById(objectAssign.getId());
		}else {
			logger.error("sava funcObjectAssign failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "sava funcObjectAssign failed!");
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncObjectAssign addFuncObjectAssign(FuncObjectAssign objectAssign) {
		return funcObjectAssignSV.create(objectAssign);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteAttrOptions(String objectAssignId, String attributeId) {
		return funcObjectAttrOptionsSV.deleteAttrOptions(objectAssignId, attributeId);
	}
	
	@Override
	public FuncObjectAttrOptions loadAttrOptions(String objectAssignId, String attributeId) {
		return funcObjectAttrOptionsSV.loadAttrOptions(objectAssignId, attributeId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncObjectAttrOptions saveAttrOptions(FuncObjectAttrOptions objectAssignOptions) {
		return funcObjectAttrOptionsSV.saveAttrOptions(objectAssignOptions);
	}
	
	@Override
	public FuncRegionAttrAssign loadFuncRegionAttrAssign(String attrAssignId) {
		return funcRegionAttrAssignSV.byId(attrAssignId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegionAttrAssign saveFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign) {
		return funcRegionAttrAssignSV.save(funcRegionAttrAssign);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegionAttrAssign addFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign) {
		return funcRegionAttrAssignSV.create(funcRegionAttrAssign);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public List<FuncRegionAttrAssign> copyFuncRegionAttrAssign(String funcId, String fromRegionId, String targetRegionId) throws EngineException{
		return funcRegionAttrAssignSV.copyFuncRegionAttrAssign(funcId, fromRegionId, targetRegionId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public List<FuncRegionAttrAssign> copyFuncRegionAttrAssignFromOther(String fromFuncId, String fromRegionId, String targetFuncId, String targetRegionId) throws EngineException{
		List<FuncObjectAssign> objectAssigns = this.funcObjectAssignSV.listByFunc(targetFuncId);
		Map<String, String> objectId2AssignId = new HashMap<>();
		if(objectAssigns != null && !objectAssigns.isEmpty()) {
			objectAssigns.forEach((objectAssign)-> {
				objectId2AssignId.put(objectAssign.getObjectId(), objectAssign.getId());
			});
		}
		return funcRegionAttrAssignSV.copyFuncRegionAttrAssignFromOther(fromFuncId, fromRegionId, targetFuncId, targetRegionId, objectId2AssignId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteFuncRegionAttrAssign(String attrAssignId) {
		return funcRegionAttrAssignSV.delete(attrAssignId);
	}
	
	@Override
	public FuncRegionOperation loadFuncRegionOperation(String regionOperationId) {
		return funcRegionOperationSV.byId(regionOperationId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegionOperation saveFuncRegionOperation(FuncRegionOperation funcRegionOperation) {
		return funcRegionOperationSV.save(funcRegionOperation);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncRegionOperation addFuncRegionOperation(FuncRegionOperation funcRegionOperation) {
		return funcRegionOperationSV.create(funcRegionOperation);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteFuncRegionOperation(String regionOperationId) {
		return funcRegionOperationSV.delete(regionOperationId);
	}
	
	@Override
	public List<FuncTemplateRegionOperation> loadFuncTemplateRegionOperations(String tplRegionId) {
		return funcTemplateRegionOperationSV.listByTplRegion(tplRegionId);
	}
	
	@Override
	public List<FuncTemplate> queryFuncTemplateAll() {
		return funcTemplateSV.loadAll();
	}
	
	@Override
	public List<ComboFuncTemplate> listComboFuncTemplates() {
		return comboFuncTemplateSV.list();
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboFuncDefine saveComboFuncDefine(ComboFuncDefine comboFuncDefine) {
		return comboFuncDefineSV.update(comboFuncDefine);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboFuncDefine addComboFuncDefine(ComboFuncDefine comboFuncDefine) {
		return comboFuncDefineSV.save(comboFuncDefine);
	}

	@Override
	public ComboFuncDefine loadComboFuncDefineAll(String comboFuncId) {
		return comboFuncDefineSV.loadAll(comboFuncId);
	}

	@Override
	public ComboFuncDefine loadComboFuncDefine(String comboFuncId) {
		return comboFuncDefineSV.byId(comboFuncId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteComboFuncDefine(String comboFuncId) {
		return comboFuncDefineSV.delete(comboFuncId);
	}
	
	@Override
	public ComboFuncTab loadComboFuncTabAll(String comboFuncTabId) {
		return comboFuncTabSV.loadAll(comboFuncTabId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboFuncTab saveComboFuncTab(ComboFuncTab comboFuncTab) {
		return comboFuncTabSV.update(comboFuncTab);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ComboFuncTab addComboFuncTab(ComboFuncTab comboFuncTab) {
		return comboFuncTabSV.save(comboFuncTab);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteComboFuncTab(String systemId, String id) {
		return comboFuncTabSV.delete(systemId, id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int saveCustomizePageLayoutsAll(String pageId, List<FuncCustomizedLayout> customizedLayouts) throws EngineException{
		return this.funcCustomizedPageSV.saveCustomizePageLayoutsAll(pageId, customizedLayouts);
	}

	@Override
	public FuncCustomizedPage loadFuncCustomizedPageAll(String pageId) {
		return this.funcCustomizedPageSV.loadFuncCustomizedPageAll(pageId);
	}

	@Override
	public FuncCustomizedPage loadFuncCustomizedPage(String pageId) {
		return this.funcCustomizedPageSV.byId(pageId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncCustomizedPage saveFuncCustomizedPage(FuncCustomizedPage funcCustomizedPage) {
		return this.funcCustomizedPageSV.save(funcCustomizedPage);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public FuncCustomizedPage createFuncCustomizedPage(FuncCustomizedPage funcCustomizedPage) {
		return this.funcCustomizedPageSV.create(funcCustomizedPage);
	}

	@Override
	public List<FuncCustomizedPage> listFuncCustomizedPages(String systemId, String funcId) {
		return this.funcCustomizedPageSV.listByFuncId(systemId, funcId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean copyFuncCustomizedPageFromOther(String fromFuncId, String fromPageId, String targetFuncId, String targetPageId) throws EngineException {
		return this.funcCustomizedPageSV.copyFuncCustomizedPageFromOther(fromFuncId, fromPageId, targetFuncId, targetPageId);
	}
}
