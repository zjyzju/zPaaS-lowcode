package cn.zpaas.lowcode.fe.ide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.be.engine.proxy.DictProxy;
import cn.zpaas.lowcode.be.engine.registry.ServiceRegistry;
import cn.zpaas.lowcode.constant.ResultStatus;
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
import cn.zpaas.lowcode.exception.IllegalAccessException;
import cn.zpaas.lowcode.fe.controller.FeController;
import cn.zpaas.lowcode.fe.ide.service.FePlatformService;
import cn.zpaas.lowcode.fe.ide.vo.BusinessSystemFeInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionChartDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionDesignInfo;
import cn.zpaas.lowcode.fe.ide.vo.FuncRegionReportDesignInfo;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import cn.zpaas.lowcode.vo.FuncDefineGenerateRequest;
import cn.zpaas.lowcode.vo.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月28日-14:53:46
 */
@RestController
@RequestMapping("/platform/fe/api/")
@DependsOn("feController")
public class FePlatformController {

	@Autowired
	private FePlatformService feService;

	@Autowired
	private FeController feController;

	@Autowired
	private ServiceRegistry serviceRegistry;

	//登录信息存放的Key
	@Value("${init.param.loginInfoKey:loginInfo}")
	private  String loginInfoKey;

	@RequestMapping("cache/reload/{systemId}")
	public ResponseResult<Boolean> reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		feController.reloadCache(systemId, request);
		return ResponseResult.success(true);
	}

	@RequestMapping("cache/clusterReload/{systemId}")
	public ResponseResult<Boolean> reloadClusterCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		serviceRegistry.reloadClusterCache("/platform/fe/api/cache/reload/"+systemId, request.getCookies());
		return ResponseResult.success(true);
	}

	@RequestMapping("businessSystem/loadFeInfo/{systemId}")
	public ResponseResult<BusinessSystemFeInfo> loadBusinessSystemInfo(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.loadBusinessSystemFeInfo(systemId));
	}
    
    @RequestMapping("funcDefine/loadAll/{funcId}")
	public ResponseResult<FuncDefine> loadFuncDefineAll(@PathVariable String funcId, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.loadFuncDefineAll(funcId);
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
		}
		return ResponseResult.success(funcDefine);
	}
	
	@RequestMapping("funcDefine/delete/{funcId}")
	public ResponseResult<Integer> deleteFuncDefine(@PathVariable String funcId, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcId);
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.deleteFuncDefine(funcId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("funcDefine/generateAll")
	public ResponseResult<FuncDefine> generateFuncDefineAll(@RequestBody FuncDefineGenerateRequest funcDefineGenerateRequest, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefineGenerateRequest.getNewFuncDefine().getSystemId(), funcDefineGenerateRequest.getNewFuncDefine().getTenantId());
		return ResponseResult.success(feService.generateFuncDefineAll(funcDefineGenerateRequest));
	}
	
	@RequestMapping("funcDefine/save")
	public ResponseResult<FuncDefine> saveFuncDefine(@RequestBody FuncDefine funcDefine, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
		return ResponseResult.success(feService.saveFuncDefine(funcDefine));
	}
	
	@RequestMapping("funcDefine/list/{systemId}")
	public ResponseResult<List<FuncDefine>> listFuncDefines(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.listFuncDefines(systemId));
	}

	@RequestMapping("funcDefine/listTplFunc/{systemId}")
	public ResponseResult<List<FuncDefine>> listTplFuncDefines(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.listTplFuncDefines(systemId));
	}

	@RequestMapping("funcDefine/listCustomizedFunc/{systemId}")
	public ResponseResult<List<FuncDefine>> listFuncCustomizedDefines(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.listFuncCustomizedDefines(systemId));
	}
	
	@RequestMapping("funcRegion/loadAll/{funcRegionId}")
	public ResponseResult<FuncRegion> loadFuncRegionAll(@PathVariable String funcRegionId, HttpServletRequest request) throws EngineException {
		FuncRegion funcRegion =  feService.loadFuncRegionAll(funcRegionId);
		if(funcRegion != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegion.getSystemId(), funcRegion.getTenantId());
		}
		return ResponseResult.success(funcRegion);
	}
	
	@RequestMapping("funcRegion/list/{systemId}/{funcId}")
	public ResponseResult<List<FuncRegion>> listFuncRegions(@PathVariable String systemId, @PathVariable String funcId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.listFuncRegions(systemId, funcId));
	}
	
	@RequestMapping("funcRegion/save")
	public ResponseResult<FuncRegion> saveFuncRegion(@RequestBody FuncRegion funcRegion, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegion.getSystemId(), funcRegion.getTenantId());
		return ResponseResult.success(feService.saveFuncRegion(funcRegion));
	}

	@RequestMapping("funcRegion/saveRegionCfg")
	public ResponseResult<FuncRegion> saveFuncRegionCfg(@RequestBody FuncRegion funcRegion, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegion.getSystemId(), funcRegion.getTenantId());
		return ResponseResult.success(feService.saveFuncRegion(funcRegion));
	}

	@RequestMapping("funcRegion/saveDesignInfo")
	public ResponseResult<Integer> saveFuncRegionDesignInfo(@RequestBody FuncRegionDesignInfo funcRegionDesignInfo, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcRegionDesignInfo.getFuncId());
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.saveFuncRegionDesignInfo(funcRegionDesignInfo));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("funcRegion/saveReportDesignInfo")
	public ResponseResult<Integer> saveFuncRegionReportDesignInfo(@RequestBody FuncRegionReportDesignInfo funcRegionDesignInfo, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcRegionDesignInfo.getFuncId());
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.saveFuncRegionReportDesignInfo(funcRegionDesignInfo));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("funcRegion/saveChartDesignInfo")
	public ResponseResult<Integer> saveFuncRegionChartDesignInfo(@RequestBody FuncRegionChartDesignInfo funcRegionDesignInfo, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcRegionDesignInfo.getFuncId());
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.saveFuncRegionChartDesignInfo(funcRegionDesignInfo));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("funcObjectAssign/loadAll/{objectAssignId}")
	public ResponseResult<FuncObjectAssign> loadFuncObjectAssignAll(@PathVariable String objectAssignId, HttpServletRequest request) throws EngineException {
		FuncObjectAssign funcObjectAssign = feService.loadFuncObjectAssignAll(objectAssignId);
		if(funcObjectAssign != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcObjectAssign.getSystemId(), funcObjectAssign.getTenantId());
		}
		return ResponseResult.success(funcObjectAssign);
	}

	@RequestMapping("funcObjectAssign/listByFunc/{funcId}")
	public ResponseResult<List<FuncObjectAssign>> listFuncObjectAssign(@PathVariable String funcId, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcId);
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.listFuncObjectAssign(funcId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("funcObjectAssign/delete/{objectAssignId}")
	public ResponseResult<Integer> deleteFuncObjectAssign(@PathVariable String objectAssignId, HttpServletRequest request) throws EngineException{
		FuncObjectAssign funcObjectAssign = feService.queryFuncObjectAssign(objectAssignId);
		if(funcObjectAssign != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcObjectAssign.getSystemId(), funcObjectAssign.getTenantId());
			return ResponseResult.success(feService.deleteFuncObjectAssign(objectAssignId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("funcObjectAssign/save")
	public ResponseResult<FuncObjectAssign> saveFuncObjectAssign(@RequestBody FuncObjectAssign objectAssign, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, objectAssign.getSystemId(), objectAssign.getTenantId());
		return ResponseResult.success(feService.saveFuncObjectAssign(objectAssign));
	}
	
	@RequestMapping("funcObjectAssign/add")
	public ResponseResult<FuncObjectAssign> addFuncObjectAssign(@RequestBody FuncObjectAssign objectAssign, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, objectAssign.getSystemId(), objectAssign.getTenantId());
		return ResponseResult.success(feService.addFuncObjectAssign(objectAssign));
	}

	@RequestMapping("funcObjectAssign/copyOptionsFromOther/{fromFuncObjectAssignId}/{targetFuncObjectAssignId}")
	public ResponseResult<List<FuncObjectAttrOptions>> copyFuncObjectAttrOptionsFromOther(@PathVariable String fromFuncObjectAssignId, @PathVariable String targetFuncObjectAssignId, HttpServletRequest request)throws EngineException {
		FuncObjectAssign fromFuncObjectAssign = feService.queryFuncObjectAssign(fromFuncObjectAssignId);
		FuncObjectAssign targetFuncObjectAssign = feService.queryFuncObjectAssign(targetFuncObjectAssignId);
		if(fromFuncObjectAssign != null && targetFuncObjectAssign != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fromFuncObjectAssign.getSystemId(), fromFuncObjectAssign.getTenantId());
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, targetFuncObjectAssign.getSystemId(), targetFuncObjectAssign.getTenantId());
			if(!fromFuncObjectAssign.getSystemId().equals(targetFuncObjectAssign.getSystemId())) {
				throw new EngineException(ResultStatus.BAD_REQUEST, "illegal request!");
			}
			return ResponseResult.success(feService.copyFuncObjectAttrOptionsFromOther(fromFuncObjectAssignId, targetFuncObjectAssignId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("funcObjectAttrOptions/delete/{objectAssignId}/{attributeId}")
	public ResponseResult<Integer> deleteAttrOptions(@PathVariable String objectAssignId, @PathVariable String attributeId, HttpServletRequest request) throws EngineException {
		FuncObjectAssign funcObjectAssign = feService.queryFuncObjectAssign(objectAssignId);
		if(funcObjectAssign != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcObjectAssign.getSystemId(), funcObjectAssign.getTenantId());
			return ResponseResult.success(feService.deleteAttrOptions(objectAssignId, attributeId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("funcObjectAttrOptions/query/{objectAssignId}/{attributeId}")
	public ResponseResult<FuncObjectAttrOptions> loadAttrOptions(@PathVariable String objectAssignId, @PathVariable String attributeId, HttpServletRequest request) throws EngineException {
		FuncObjectAttrOptions attrOptions = feService.loadAttrOptions(objectAssignId, attributeId);
		if(attrOptions != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, attrOptions.getSystemId(), attrOptions.getTenantId());
		}
		return ResponseResult.success(attrOptions);
	}
	
	@RequestMapping("funcObjectAttrOptions/save")
	public ResponseResult<FuncObjectAttrOptions> saveAttrOptions(@RequestBody FuncObjectAttrOptions objectAssignOptions, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, objectAssignOptions.getSystemId(), objectAssignOptions.getTenantId());
		return ResponseResult.success(feService.saveAttrOptions(objectAssignOptions));
	}
	
	@RequestMapping("funcRegionAttrAssign/byId/{attrAssignId}")
	public ResponseResult<FuncRegionAttrAssign> loadFuncRegionAttrAssign(@PathVariable String attrAssignId, HttpServletRequest request) throws EngineException {
		FuncRegionAttrAssign attrAssign = feService.loadFuncRegionAttrAssign(attrAssignId);
		if(attrAssign != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, attrAssign.getSystemId(), attrAssign.getTenantId());
		}
		return ResponseResult.success(attrAssign);
	}
	
	@RequestMapping("funcRegionAttrAssign/save")
	public ResponseResult<FuncRegionAttrAssign> saveFuncRegionAttrAssign(@RequestBody FuncRegionAttrAssign funcRegionAttrAssign, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegionAttrAssign.getSystemId(), funcRegionAttrAssign.getTenantId());
		return ResponseResult.success(feService.saveFuncRegionAttrAssign(funcRegionAttrAssign));
	}
	
	@RequestMapping("funcRegionAttrAssign/add")
	public ResponseResult<FuncRegionAttrAssign> addFuncRegionAttrAssign(@RequestBody FuncRegionAttrAssign funcRegionAttrAssign, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegionAttrAssign.getSystemId(), funcRegionAttrAssign.getTenantId());
		return ResponseResult.success(feService.addFuncRegionAttrAssign(funcRegionAttrAssign));
	}
	
	@RequestMapping("funcRegionAttrAssign/copy/{funcId}/{fromRegionId}/{targetRegionId}")
	public ResponseResult<List<FuncRegionAttrAssign>> copyFuncRegionAttrAssign(@PathVariable String funcId, @PathVariable String fromRegionId, @PathVariable String targetRegionId, HttpServletRequest request) throws EngineException {
		FuncDefine funcDefine = feService.queryFuncDefine(funcId);
		if(funcDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcDefine.getSystemId(), funcDefine.getTenantId());
			return ResponseResult.success(feService.copyFuncRegionAttrAssign(funcId, fromRegionId, targetRegionId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("funcRegionAttrAssign/copyFromOther/{fromFuncId}/{fromRegionId}/{targetFuncId}/{targetRegionId}")
	public ResponseResult<List<FuncRegionAttrAssign>> copyFuncRegionAttrAssignFromOther(@PathVariable String fromFuncId, @PathVariable String fromRegionId, 
			@PathVariable String targetFuncId, @PathVariable String targetRegionId, HttpServletRequest request) throws EngineException {
		FuncDefine fromFuncDefine = feService.queryFuncDefine(fromFuncId);
		FuncDefine targetFuncDefine = feService.queryFuncDefine(targetFuncId);
		if(fromFuncDefine != null && targetFuncDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fromFuncDefine.getSystemId(), fromFuncDefine.getTenantId());
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, targetFuncDefine.getSystemId(), targetFuncDefine.getTenantId());
			if(!fromFuncDefine.getSystemId().equals(targetFuncDefine.getSystemId())) {
				throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
			}
			return ResponseResult.success(feService.copyFuncRegionAttrAssignFromOther(fromFuncId, fromRegionId, targetFuncId, targetRegionId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("funcRegionAttrAssign/delete/{attrAssignId}")
	public ResponseResult<Integer> deleteFuncRegionAttrAssign(@PathVariable String attrAssignId, HttpServletRequest request) throws EngineException {
		if(this.loadFuncRegionAttrAssign(attrAssignId, request) != null) {
			return ResponseResult.success(feService.deleteFuncRegionAttrAssign(attrAssignId));
		}else {
			return ResponseResult.success(0);
		}
	}	
	
	@RequestMapping("funcRegionOperation/byId/{regionOperationId}")
	public ResponseResult<FuncRegionOperation> loadFuncRegionOperation(@PathVariable String regionOperationId, HttpServletRequest request) throws EngineException {
		FuncRegionOperation funcRegionOperation = feService.loadFuncRegionOperation(regionOperationId);
		if(funcRegionOperation != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegionOperation.getSystemId(), funcRegionOperation.getTenantId());
		}
		return ResponseResult.success(funcRegionOperation);
	}
	
	@RequestMapping("funcRegionOperation/save")
	public ResponseResult<FuncRegionOperation> saveFuncRegionOperation(@RequestBody FuncRegionOperation funcRegionOperation, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegionOperation.getSystemId(), funcRegionOperation.getTenantId());
		return ResponseResult.success(feService.saveFuncRegionOperation(funcRegionOperation));
	}
	
	@RequestMapping("funcRegionOperation/add")
	public ResponseResult<FuncRegionOperation> addFuncRegionOperation(@RequestBody FuncRegionOperation funcRegionOperation, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcRegionOperation.getSystemId(), funcRegionOperation.getTenantId());
		return ResponseResult.success(feService.addFuncRegionOperation(funcRegionOperation));
	}
	
	@RequestMapping("funcRegionOperation/delete/{regionOperationId}")
	public ResponseResult<Integer> deleteFuncRegionOperation(@PathVariable String regionOperationId, HttpServletRequest request) throws EngineException {
		if(this.loadFuncRegionOperation(regionOperationId, request) != null) {
			return ResponseResult.success(feService.deleteFuncRegionOperation(regionOperationId));
		}else {
			return ResponseResult.success(0);
		}
	}

    @RequestMapping("funcTemplateRegionOperation/loadByTplRegion/{tplRegionId}")
	public ResponseResult<List<FuncTemplateRegionOperation>> loadFuncTemplateRegionOperations(@PathVariable String tplRegionId) {
		return ResponseResult.success(feService.loadFuncTemplateRegionOperations(tplRegionId));
	}
	
	@RequestMapping("funcTemplate/queryAll")
	public ResponseResult<List<FuncTemplate>> queryFuncTemplateAll() {
		return ResponseResult.success(feService.queryFuncTemplateAll());
	}
	
	@RequestMapping("comboFuncDefine/loadAll/{comboFuncId}") 
	public ResponseResult<ComboFuncDefine> loadComboFuncDefineAll(@PathVariable String comboFuncId, HttpServletRequest request) throws EngineException {
		ComboFuncDefine comboFuncDefine = feService.loadComboFuncDefineAll(comboFuncId);
		if(comboFuncDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncDefine.getSystemId(), comboFuncDefine.getTenantId());
		}
		return ResponseResult.success(comboFuncDefine);
	}
	
	@RequestMapping("comboFuncTemplate/list")
	public ResponseResult<List<ComboFuncTemplate>> listComboFuncTemplates() {
		return ResponseResult.success(feService.listComboFuncTemplates());
	}
	
	@RequestMapping("comboFuncDefine/save")
	public ResponseResult<ComboFuncDefine> saveComboFuncDefine(@RequestBody ComboFuncDefine comboFuncDefine, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncDefine.getSystemId(), comboFuncDefine.getTenantId());
		return ResponseResult.success(feService.saveComboFuncDefine(comboFuncDefine));
	}
	
	@RequestMapping("comboFuncDefine/add")
	public ResponseResult<ComboFuncDefine> addComboFuncDefine(@RequestBody ComboFuncDefine comboFuncDefine, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncDefine.getSystemId(), comboFuncDefine.getTenantId());
		return ResponseResult.success(feService.addComboFuncDefine(comboFuncDefine));
	}
	
	@RequestMapping("comboFuncDefine/delete/{comboFuncId}")
	public ResponseResult<Integer> addComboFuncDefine(@PathVariable String comboFuncId, HttpServletRequest request) throws EngineException {
		ComboFuncDefine comboFuncDefine = feService.loadComboFuncDefine(comboFuncId);
		if(comboFuncDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncDefine.getSystemId(), comboFuncDefine.getTenantId());
			return ResponseResult.success(feService.deleteComboFuncDefine(comboFuncId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("comboFuncTab/loadAll/{comboFuncTabId}")
	public ResponseResult<ComboFuncTab> loadComboFuncTabAll(@PathVariable String comboFuncTabId, HttpServletRequest request) throws EngineException {
		ComboFuncTab comboFuncTab = feService.loadComboFuncTabAll(comboFuncTabId);
		if(comboFuncTab != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncTab.getSystemId(), comboFuncTab.getTenantId());
		}
		return ResponseResult.success(comboFuncTab);
	}
	
	@RequestMapping("comboFuncTab/save")
	public ResponseResult<ComboFuncTab> saveComboFuncTab(@RequestBody ComboFuncTab comboFuncTab, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncTab.getSystemId(), comboFuncTab.getTenantId());
		return ResponseResult.success(feService.saveComboFuncTab(comboFuncTab));
	}
	
	@RequestMapping("comboFuncTab/add")
	public ResponseResult<ComboFuncTab> addComboFuncTab(@RequestBody ComboFuncTab comboFuncTab, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, comboFuncTab.getSystemId(), comboFuncTab.getTenantId());
		return ResponseResult.success(feService.addComboFuncTab(comboFuncTab));
	}
	
	@RequestMapping("comboFuncTab/delete/{systemId}/{id}")
	public ResponseResult<Integer> deleteComboFuncTab(@PathVariable String systemId, @PathVariable String id, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.deleteComboFuncTab(systemId, id));
	}

	@RequestMapping("customizedPage/savePageLayouts/{pageId}")
	public ResponseResult<Integer> saveCustomizePageLayouts(@PathVariable String pageId, @RequestBody List<FuncCustomizedLayout> customizedLayouts, HttpServletRequest request) throws EngineException {
		FuncCustomizedPage customizedPage = feService.loadFuncCustomizedPage(pageId);
		if(customizedPage != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, customizedPage.getSystemId(), customizedPage.getTenantId());
			if(!CollectionUtils.isEmpty(customizedLayouts)) {
				for(FuncCustomizedLayout customizedLayout : customizedLayouts) {
					LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, customizedLayout.getSystemId(), customizedLayout.getTenantId());
					if(!customizedPage.getSystemId().equals(customizedLayout.getSystemId())) {
						throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
					}
				}
			}
			return ResponseResult.success(feService.saveCustomizePageLayoutsAll(pageId, customizedLayouts));
		}else {
			return ResponseResult.success(0);
		}
		
	}

	@RequestMapping("customizedPage/loadAll/{pageId}")
	public ResponseResult<FuncCustomizedPage> loadFuncCustomizedPageAll(@PathVariable String pageId, HttpServletRequest request) throws EngineException {
		FuncCustomizedPage funcCustomizedPage = feService.loadFuncCustomizedPageAll(pageId);
		if(funcCustomizedPage != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcCustomizedPage.getSystemId(), funcCustomizedPage.getTenantId());
		}
		return ResponseResult.success(funcCustomizedPage);
	}

	@RequestMapping("customizedPage/save")
	public ResponseResult<FuncCustomizedPage> saveFuncCustomizedPage(@RequestBody FuncCustomizedPage funcCustomizedPage, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcCustomizedPage.getSystemId(), funcCustomizedPage.getTenantId());
		return ResponseResult.success(feService.saveFuncCustomizedPage(funcCustomizedPage));
	}

	@RequestMapping("customizedPage/create")
	public ResponseResult<FuncCustomizedPage> createFuncCustomizedPage(@RequestBody FuncCustomizedPage funcCustomizedPage, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, funcCustomizedPage.getSystemId(), funcCustomizedPage.getTenantId());
		return ResponseResult.success(feService.createFuncCustomizedPage(funcCustomizedPage));
	}

	@RequestMapping("customizedPage/list/{systemId}/{funcId}")
	public ResponseResult<List<FuncCustomizedPage>> listFuncCustomizedPages(@PathVariable String systemId, @PathVariable String funcId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(feService.listFuncCustomizedPages(systemId, funcId));
	}

	@RequestMapping("customizedPage/copyFromOther/{fromFuncId}/{fromPageId}/{targetFuncId}/{targetPageId}")
	public ResponseResult<Boolean> copyFuncCustomizedPageFromOther(@PathVariable String fromFuncId, @PathVariable String fromPageId, 
			@PathVariable String targetFuncId, @PathVariable String targetPageId, HttpServletRequest request) throws EngineException {
		FuncDefine fromFuncDefine = feService.queryFuncDefine(fromFuncId);
		FuncDefine targetFuncDefine = feService.queryFuncDefine(targetFuncId);
		if(fromFuncDefine != null && targetFuncDefine != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fromFuncDefine.getSystemId(), fromFuncDefine.getTenantId());
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, targetFuncDefine.getSystemId(), targetFuncDefine.getTenantId());
			if(!fromFuncDefine.getSystemId().equals(targetFuncDefine.getSystemId())) {
				throw new IllegalAccessException(ResultStatus.BAD_REQUEST, "illegal request!");
			}
			return ResponseResult.success(feService.copyFuncCustomizedPageFromOther(fromFuncId, fromPageId, targetFuncId, targetPageId));
		}else {
			return ResponseResult.success(false);
		}
	}

	@RequestMapping("dict/getCustomizedDesignComponents")
	public ResponseResult<JsonArray> getCustomizedDesignComponents() {
		return ResponseResult.success(DictProxy.getCustomizedDesignComponents());
	}

	@RequestMapping("dict/getChartDesignComponents")
	public ResponseResult<JsonArray> getChartDesignComponents() {
		return ResponseResult.success(DictProxy.getChartDesignComponents());
	}
}
