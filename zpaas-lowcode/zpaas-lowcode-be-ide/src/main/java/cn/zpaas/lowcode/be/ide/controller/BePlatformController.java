package cn.zpaas.lowcode.be.ide.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.SpringContextUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.BusinessFlowInfo;
import cn.zpaas.lowcode.vo.DomainOperationExpose;
import cn.zpaas.lowcode.vo.LabelValueVo;
import cn.zpaas.lowcode.vo.ManagedObjectInfo;
import cn.zpaas.lowcode.vo.MdaColumn;
import cn.zpaas.lowcode.vo.MdaGenerateRequest;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.vo.NewManagedObject;
import cn.zpaas.lowcode.vo.OperationFlowInfo;
import cn.zpaas.lowcode.vo.OperationInfo;
import cn.zpaas.lowcode.vo.ResponseResult;
import cn.zpaas.lowcode.vo.UpdateManagedObject;
import cn.zpaas.lowcode.vo.ValidateRuleGroupInitInfo;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAuth;
import cn.zpaas.lowcode.domain.eo.BusinessSystemAutoLoad;
import cn.zpaas.lowcode.domain.eo.CronJob;
import cn.zpaas.lowcode.domain.eo.DataMapping;
import cn.zpaas.lowcode.domain.eo.DataTransferMethod;
import cn.zpaas.lowcode.domain.eo.DbColumn;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.Dict;
import cn.zpaas.lowcode.domain.eo.DictResource;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.DynamicMapping;
import cn.zpaas.lowcode.domain.eo.DynamicMappingDetail;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.FileObjectMapping;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.domain.eo.ManagedObject;
import cn.zpaas.lowcode.domain.eo.MessageConsumer;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.domain.eo.ServerResource;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.domain.eo.SqlManagement;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.domain.eo.WorkflowProcessResource;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.be.engine.proxy.DictProxy;
import cn.zpaas.lowcode.be.engine.registry.ServiceRegistry;
import cn.zpaas.lowcode.be.ide.service.PlatformService;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemGrantVo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemInfo;
import cn.zpaas.lowcode.be.ide.vo.BusinessSystemTestVo;
import cn.zpaas.lowcode.utils.LoginInfoUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zjy
 * 平台管理API控制器
 */
@RestController
@RequestMapping("/platform/be/api/")
public class BePlatformController {
	public static final Logger logger = LoggerFactory.getLogger(BePlatformController.class);

	@Autowired
	private PlatformService platformService;
	
	@Autowired
	private FrontController frontController;

	@Autowired
	private ServiceRegistry serviceRegistry;

	//登录信息存放的Key
	@Value("${init.param.loginInfoKey:loginInfo}")
	private  String loginInfoKey;
	
	@RequestMapping("cache/reload/{systemId}")
	public ResponseResult<Boolean> reloadCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		frontController.reloadCache(systemId, request);
		return ResponseResult.success(true);
	}

	@RequestMapping("cache/clusterReload/{systemId}")
	public ResponseResult<Boolean> reloadClusterCache(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		serviceRegistry.reloadClusterCache("/platform/be/api/cache/reload/"+systemId, request.getCookies());
		return ResponseResult.success(true);
	}
	
	@RequestMapping("businessSystem/list")
	public ResponseResult<List<BusinessSystem>> listBusinessSystem(HttpServletRequest request)throws EngineException {
		LoginInfoUtils.tenantAdminCheck(request, loginInfoKey);
		return ResponseResult.success(platformService.listBusinessSystem(LoginInfoUtils.getLoginUserTenant(request, loginInfoKey)));
	}

	@RequestMapping("businessSystem/listByUser")
	public ResponseResult<List<BusinessSystem>> listBusinessSystemByUser(HttpServletRequest request) {
		return ResponseResult.success(platformService.listBusinessSystemByUser(LoginInfoUtils.getLoginUserTenant(request, loginInfoKey), LoginInfoUtils.getLoginUserId(request, loginInfoKey)));
	}

	@RequestMapping("businessSystem/grant")
	public ResponseResult<Integer> grantBusinessSystem(@RequestBody BusinessSystemGrantVo businessSystemGrantVo, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAdminCheck(request, loginInfoKey);
		businessSystemGrantVo.setTenantId(LoginInfoUtils.getLoginUserTenant(request, loginInfoKey));
		return ResponseResult.success(platformService.grantBusinessSystem(businessSystemGrantVo));
	}

	@RequestMapping("businessSystem/loadGrantInfo/{userId}")
	public ResponseResult<BusinessSystemGrantVo> loadBusinessSystemGrantInfo(@PathVariable String userId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAdminCheck(request, loginInfoKey);
		return ResponseResult.success(platformService.loadBusinessSystemGrantInfo(userId));
	}
	
	@RequestMapping("businessSystem/loadInfo/{systemId}")
	public ResponseResult<BusinessSystemInfo> loadBusinessSystemInfo(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.loadBusinessSystemInfo(systemId));
	}

	@RequestMapping("businessSystem/loadBusinessSystemTestInfo/{systemId}")
	public ResponseResult<BusinessSystemTestVo> loadBusinessSystemTestInfo(@PathVariable String systemId, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		BusinessSystemTestVo vo = platformService.loadBusinessSystemTestInfo(systemId);
		if(vo != null) {
			vo.setPlatformUrl(frontController.getServicePath());
			vo.setPlatformFileDownloadUrl(frontController.getFileDownloadServicePath());
			vo.setPlatformFileUploadUrl(frontController.getFileUploadServicePath());
			vo.setPlatformBatchUrl(frontController.getBatchServicePath());
			vo.setPlatformStreamUrl(frontController.getStreamServicePath());
		}
		return ResponseResult.success(vo);
	}
	
	@RequestMapping("businessSystem/save")
	public ResponseResult<Integer> saveBusinessSystem(@RequestBody BusinessSystem businessSystem, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAdminCheck(request, loginInfoKey);
		LoginInfoUtils.tenantCheck(request, loginInfoKey, businessSystem.getTenantId());
		return ResponseResult.success(platformService.saveBusinessSystem(businessSystem));
	}
	
	@RequestMapping("businessSystemAuth/save")
	public ResponseResult<BusinessSystemAuth> saveBusinessSystemAuth(@RequestBody BusinessSystemAuth businessSystemAuth, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessSystemAuth.getSystemId(), businessSystemAuth.getTenantId());
		return ResponseResult.success(platformService.saveBusinessSystemAuth(businessSystemAuth));
	}
	
	@RequestMapping("businessSystemAutoLoad/save")
	public ResponseResult<BusinessSystemAutoLoad> saveBusinessSystemAutoLoad(@RequestBody BusinessSystemAutoLoad businessSystemAutoLoad, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessSystemAutoLoad.getId(), businessSystemAutoLoad.getTenantId());
		return ResponseResult.success(platformService.saveBusinessSystemAutoLoad(businessSystemAutoLoad));
	}
	
	@RequestMapping("businessSystem/add")
	public ResponseResult<BusinessSystem> createBusinessSystem(@RequestBody BusinessSystem businessSystem, HttpServletRequest request)  throws EngineException{
		businessSystem.setTenantId(LoginInfoUtils.getLoginUserTenant(request, loginInfoKey));
		BusinessSystem businessSystemAdded = platformService.createBusinessSystem(businessSystem, LoginInfoUtils.getLoginUserId(request, loginInfoKey));
		SpringContextUtils.getBean(FrontController.class).addBusinessSystem(businessSystemAdded);
		return ResponseResult.success(businessSystemAdded);
	}
	
	@RequestMapping("businessSystem/delete/{id}")
	public ResponseResult<Integer> deleteBusinessSystem(@PathVariable String id, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAdminCheck(request, loginInfoKey);
		return ResponseResult.success(platformService.deleteBusinessSystem(id, LoginInfoUtils.getLoginUserTenant(request, loginInfoKey)));
	}
	
	@RequestMapping("managedObject/loadInfo/{objectType}/{objectId}")
	public ResponseResult<ManagedObjectInfo> loadManagedObjectInfo(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request)  throws EngineException{
		ManagedObjectInfo managedObjectInfo = platformService.loadManagedObjectInfo(objectType, objectId);
		if(managedObjectInfo != null && managedObjectInfo.getManagedObject() != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObjectInfo.getManagedObject().getSystemId(), managedObjectInfo.getManagedObject().getTenantId());
		}
		return ResponseResult.success(managedObjectInfo);
	}
	
	@RequestMapping("managedObject/queryObjectCode/{objectType}/{objectId}")
	public ResponseResult<String> queryObjectCode(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request)  throws EngineException{
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(managedObject.getCode());
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("managedObject/queryObjectCodes")
	public ResponseResult<JsonObject> queryObjectCodes(@RequestBody JsonObject objects, HttpServletRequest request)  throws EngineException{
		JsonObject result = new JsonObject();
		if(objects != null && !objects.isEmpty()) {
			for(Entry<String, JsonElement> entry : objects.asMap().entrySet()) {
				result.addProperty(entry.getKey(), this.queryObjectCode(entry.getValue().getAsString(), entry.getKey(), request).getData());
			}
		}
		return ResponseResult.success(result);
	}
	
	@RequestMapping("managedObject/delete/{objectType}/{objectId}")
	public ResponseResult<Integer> deleteManagedObject(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request)  throws EngineException{
		return ResponseResult.success(platformService.deleteManagedObject(objectType, objectId, LoginInfoUtils.getLoginUserTenant(request, loginInfoKey), LoginInfoUtils.getLoginUserGrantedSystems(request, loginInfoKey)));
	}
	
	@RequestMapping("businessFlow/loadInfo/{businessFlowId}")
	public ResponseResult<BusinessFlowInfo> loadBusinessFlowInfo(@PathVariable String businessFlowId, HttpServletRequest request)  throws EngineException{
		BusinessFlowInfo businessFlowInfo = platformService.loadBusinessFlowInfo(businessFlowId);
		if(businessFlowInfo != null && businessFlowInfo.getBusinessFlow() != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessFlowInfo.getBusinessFlow().getSystemId(), businessFlowInfo.getBusinessFlow().getTenantId());
		}
		return ResponseResult.success(businessFlowInfo);
	}
	
	@RequestMapping("businessFlow/save")
	public ResponseResult<BusinessFlow> saveBusinessFlow(@RequestBody BusinessFlow businessFlow, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessFlow.getSystemId(), businessFlow.getTenantId());
		return ResponseResult.success(platformService.saveBusinessFlow(businessFlow));
	}
	
	@RequestMapping("businessFlow/saveAll")
	public ResponseResult<BusinessFlowInfo> saveBusinessFlowAll(@RequestBody BusinessFlowInfo businessFlowInfo, HttpServletRequest request) throws EngineException{
		if(businessFlowInfo != null && businessFlowInfo.getBusinessFlow() != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessFlowInfo.getBusinessFlow().getSystemId(), businessFlowInfo.getBusinessFlow().getTenantId());
		}
		return ResponseResult.success(platformService.saveBusinessFlowAllInfo(businessFlowInfo));
	}
	
	@RequestMapping("businessFlowNode/add")
	public ResponseResult<BusinessFlowNode> addBusinessFlowNode(@RequestBody BusinessFlowNode businessFlowNode, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessFlowNode.getSystemId(), businessFlowNode.getTenantId());
		return ResponseResult.success(platformService.addBusinessFlowNode(businessFlowNode));
	}
	
	@RequestMapping("businessFlowNode/save")
	public ResponseResult<Integer> saveBusinessFlowNode(@RequestBody BusinessFlowNode businessFlowNode, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessFlowNode.getSystemId(), businessFlowNode.getTenantId());
		return ResponseResult.success(platformService.saveBusinessFlowNode(businessFlowNode));
	}

	@RequestMapping("flowNode/list/{systemId}")
	public ResponseResult<List<FlowNode>> listFlowNode(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listFlowNode(systemId));
	}
	
	@RequestMapping("managedObject/add") 
	public ResponseResult<ManagedObjectInfo> addManageObject(@RequestBody NewManagedObject managedObject, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
		return ResponseResult.success(platformService.addManagedObject(managedObject));
	}
	
	@RequestMapping("managedObject/save") 
	public ResponseResult<Integer> saveManageObject(@RequestBody UpdateManagedObject managedObject, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
		return ResponseResult.success(platformService.saveManagedObject(managedObject));
	}
	
	@RequestMapping("domainObject/byId/{domainObjectId}")
	public ResponseResult<DomainObject> loadDomainObject(@PathVariable String domainObjectId, HttpServletRequest request) throws EngineException{
		DomainObject domainObject = platformService.loadDomainObject(domainObjectId);
		if(domainObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, domainObject.getSystemId(), domainObject.getTenantId());
		}
		return ResponseResult.success(domainObject);
	}
	
	@RequestMapping("exposedService/exposeDomainOperation")
	public void exposeDomainOperation(@RequestBody DomainOperationExpose domainOperationExpose, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, domainOperationExpose.getSystemId(), domainOperationExpose.getTenantId());
		platformService.exposeDomainOperation(domainOperationExpose);
	}
	
	@RequestMapping("valueObject/byId/{valueObjectId}")
	public ResponseResult<ValueObject> loadValueObject(@PathVariable String valueObjectId, HttpServletRequest request) throws EngineException{
		ValueObject vo = platformService.loadValueObject(valueObjectId);
		if(vo != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, vo.getSystemId(), vo.getTenantId());
		}
		return ResponseResult.success(vo);
	}
	
	@RequestMapping("attribute/add")
	public ResponseResult<Attribute> addAttribute(@RequestBody Attribute attribute, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, attribute.getSystemId(), attribute.getTenantId());
		return ResponseResult.success(platformService.addAttribute(attribute));
	}
	
	@RequestMapping("attribute/list/{objectType}/{objectId}")
	public ResponseResult<List<Attribute>> listAttributes(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException{
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.listAttributes(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}		
	}
	
	@RequestMapping("attribute/save") 
	public ResponseResult<Integer> saveAttribute(@RequestBody Attribute attribute, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, attribute.getSystemId(), attribute.getTenantId());
		return ResponseResult.success(platformService.saveAttribute(attribute));
	}
	
	@RequestMapping("attribute/delete/{attrId}") 
	public ResponseResult<Integer> deleteAttribute(@PathVariable String attrId, HttpServletRequest request) throws EngineException{
		Attribute attribute = platformService.queryAttribute(attrId);
		if(attribute != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, attribute.getSystemId(), attribute.getTenantId());
			return ResponseResult.success(platformService.deleteAttribute(attrId));
		}else {
			return ResponseResult.success(0);
		}
		
	}
	
	@RequestMapping("operation/loadInfo/{operationId}")
	public ResponseResult<OperationInfo> loadOperationInfo(@PathVariable String operationId, HttpServletRequest request) throws EngineException{
		OperationInfo operationInfo = platformService.loadOperationInfo(operationId);
		if(operationInfo != null && operationInfo.getOperation() != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operationInfo.getOperation().getSystemId(), operationInfo.getOperation().getTenantId());
		}
		return ResponseResult.success(operationInfo);
	}

	@RequestMapping("operation/queryName/{operationId}")
	public ResponseResult<String> queryOperationName(@PathVariable String operationId, HttpServletRequest request) throws EngineException{
		Operation operation = platformService.queryOperation(operationId);
		if(operation != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operation.getSystemId(), operation.getTenantId());
			return ResponseResult.success(operation.getCode());
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("operation/add")
	public ResponseResult<Operation> addOperation(@RequestBody Operation operation, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operation.getSystemId(), operation.getTenantId());
		return ResponseResult.success(platformService.addOperation(operation));
	}
	
	@RequestMapping("operation/addWithFlow")
	public ResponseResult<Operation> addOperationAndFlow(@RequestBody OperationFlowInfo operationFlowInfo, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operationFlowInfo.getSystemId(), operationFlowInfo.getTenantId());
		return ResponseResult.success(platformService.addOperationAndFlow(operationFlowInfo));
	}
	
	@RequestMapping("operation/save")
	public ResponseResult<Integer> saveOperation(@RequestBody Operation operation, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operation.getSystemId(), operation.getTenantId());
		return ResponseResult.success(platformService.saveOperation(operation));
	}
	
	@RequestMapping("operation/delete/{operationId}")
	public ResponseResult<Integer> deleteOperation(@PathVariable String operationId, HttpServletRequest request) throws EngineException{
		Operation operation = platformService.queryOperation(operationId);
		if(operation != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operation.getSystemId(), operation.getTenantId());
			return ResponseResult.success(platformService.deleteOperation(operationId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("operation/list/{objectType}/{objectId}")
	public ResponseResult<List<Operation>> queryOperations(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException{
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.queryOperations(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("operation/listNoParam/{objectType}/{objectId}")
	public ResponseResult<List<Operation>> queryOperationsNoParam(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException{
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.queryOperationsNoParam(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("parameter/load/{operationId}")
	public ResponseResult<List<Parameter>> loadParameters(@PathVariable String operationId, HttpServletRequest request) throws EngineException{
		Operation operation = platformService.queryOperation(operationId);
		if(operation != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, operation.getSystemId(), operation.getTenantId());
			return ResponseResult.success(platformService.loadParameters(operationId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("parameter/add")
	public ResponseResult<Parameter> addParameter(@RequestBody Parameter parameter, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, parameter.getSystemId(), parameter.getTenantId());
		return ResponseResult.success(platformService.addParameter(parameter));
	}
	
	@RequestMapping("parameter/save")
	public ResponseResult<Parameter> saveParameter(@RequestBody Parameter parameter, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, parameter.getSystemId(), parameter.getTenantId());
		return ResponseResult.success(platformService.saveParameter(parameter));
	}
	
	@RequestMapping("parameter/delete/{paramId}")
	public ResponseResult<Integer> deleteParameter(@PathVariable String paramId, HttpServletRequest request) throws EngineException{
		Parameter parameter = platformService.queryParameter(paramId);
		if(parameter != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, parameter.getSystemId(), parameter.getTenantId());
			return ResponseResult.success(platformService.deleteParameter(paramId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("operation/expose")
	public ResponseResult<ExposedService> exposeOperation(@RequestBody ExposedService exposedService, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, exposedService.getSystemId(), exposedService.getTenantId());
		return ResponseResult.success(platformService.exposeOperation(exposedService));
	}
	
	@RequestMapping("operation/cancelExpose")
	public ResponseResult<Integer> cancelExposeOperation(@RequestBody ExposedService exposedService, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, exposedService.getSystemId(), exposedService.getTenantId());
		return ResponseResult.success(platformService.cancelExposeOperation(exposedService));
	}
	
	@RequestMapping("businessDomain/add")
	public ResponseResult<BusinessDomain> addBusinessDomain(@RequestBody BusinessDomain businessDomain, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessDomain.getSystemId(), businessDomain.getTenantId());
		return ResponseResult.success(platformService.addBusinessDomain(businessDomain));
	}
	
	@RequestMapping("businessDomain/save")
	public ResponseResult<Integer> saveBusinessDomain(@RequestBody BusinessDomain businessDomain, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessDomain.getSystemId(), businessDomain.getTenantId());
		return ResponseResult.success(platformService.saveBusinessDomain(businessDomain));
	}

	@RequestMapping("businessDomain/delete/{businessDomainId}")
	public ResponseResult<Integer> deleteBusinessDomain(@PathVariable String businessDomainId, HttpServletRequest request) throws EngineException{
		BusinessDomain businessDomain = platformService.queryBusinessDomain(businessDomainId);
		if(businessDomain != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessDomain.getSystemId(), businessDomain.getTenantId());
		}
		return ResponseResult.success(platformService.deleteBusinessDomain(businessDomain));
	}

	@RequestMapping("businessDomain/list/{systemId}")
	public ResponseResult<List<BusinessDomain>> listBusinessDomain(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listBusinessDomain(systemId));
	}

	@RequestMapping("businessDomain/byId/{businessDomainId}")
	public ResponseResult<BusinessDomain> queryBusinessDomain(@PathVariable String businessDomainId, HttpServletRequest request) throws EngineException{
		BusinessDomain businessDomain = platformService.queryBusinessDomain(businessDomainId);
		if(businessDomain != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, businessDomain.getSystemId(), businessDomain.getTenantId());
		}
		return ResponseResult.success(businessDomain);
	}
	
	@RequestMapping("mda/queryTableList/{systemId}/{dbSchemaId}")
	public ResponseResult<List<MdaTable>> queryTableList(@PathVariable String systemId, @PathVariable String dbSchemaId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		try {
			List<MdaTable> tableList = platformService.queryTableList(systemId, dbSchemaId);
			return ResponseResult.success(tableList);
		} catch (EngineException e) {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("mda/queryColumnList/{systemId}/{dbSchemaId}/{tableName}")
	public ResponseResult<List<MdaColumn>> queryColumnList(@PathVariable String systemId, @PathVariable String dbSchemaId, @PathVariable String tableName, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		try {
			List<MdaColumn> columnList = platformService.queryColumnList(systemId, dbSchemaId, tableName);
			return ResponseResult.success(columnList);
		} catch (EngineException e) {
			e.printStackTrace();
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("mda/queryTableColumnList/{systemId}/{dbSchemaId}")
	public ResponseResult<List<MdaTable>> queryTableColumnList(@PathVariable String systemId, @PathVariable String dbSchemaId, @RequestBody JsonArray tableArray, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		try {
			if(tableArray == null || tableArray.size() == 0) {
				return ResponseResult.success(null);
			}
			List<MdaTable> list = platformService.queryTableColumnList(systemId, dbSchemaId, tableArray.asList().stream().map(e->e.getAsString()).collect(Collectors.toList()));
			return ResponseResult.success(list);
		} catch (EngineException e) {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("mda/generateAll")
	public ResponseResult<Boolean> generateAll(@RequestBody MdaGenerateRequest mdaGenerateRequest, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, mdaGenerateRequest.getSystemId());
		try {
			if(mdaGenerateRequest == null || mdaGenerateRequest.getTableArray().size() == 0) {
				return ResponseResult.success(false);
			}
			return ResponseResult.success(platformService.generateAll(mdaGenerateRequest));
		} catch (EngineException e) {
			e.printStackTrace();
			return ResponseResult.success(false);
		}
	}
	
	@RequestMapping("serverResource/add")
	public ResponseResult<ServerResource> addServerResource(@RequestBody ServerResource serverResource, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, serverResource.getSystemId(), serverResource.getTenantId());
		return ResponseResult.success(platformService.addServerResource(serverResource));
	}
	
	@RequestMapping("serverResource/delete/{serverResourceId}")
	public ResponseResult<Integer> deleteServerResource(@PathVariable String serverResourceId, HttpServletRequest request) throws EngineException{
		if(this.queryServerResource(serverResourceId, request) != null) {
			return ResponseResult.success(platformService.deleteServerResource(serverResourceId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("serverResource/byId/{serverResourceId}")
	public ResponseResult<ServerResource> queryServerResource(@PathVariable String serverResourceId, HttpServletRequest request) throws EngineException{
		ServerResource serverResource = platformService.queryServerResource(serverResourceId);
		if(!StringUtils.isBlank(serverResource.getPassword())) {
			serverResource.setPassword(ServerResource.PWD_MARK_STR);
		}
		if(serverResource != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, serverResource.getSystemId(), serverResource.getTenantId());
		}
		return ResponseResult.success(serverResource);
	}
	
	@RequestMapping("serverResource/save")
	public ResponseResult<Integer> saveServerResource(@RequestBody ServerResource serverResource, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, serverResource.getSystemId(), serverResource.getTenantId());
		return ResponseResult.success(platformService.saveServerResource(serverResource));
	}

	@RequestMapping("dbSchema/add")
	public ResponseResult<DbSchema> addDbSchema(@RequestBody DbSchema dbSchema, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbSchema.getSystemId(), dbSchema.getTenantId());
		try {
			return ResponseResult.success(platformService.addDbSchema(dbSchema));
		} catch (EngineException e) {
			e.printStackTrace();
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dbSchema/save")
	public ResponseResult<Integer> saveDbSchema(@RequestBody DbSchema dbSchema, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbSchema.getSystemId(), dbSchema.getTenantId());
		try {
			return ResponseResult.success(platformService.saveDbSchema(dbSchema));
		} catch (EngineException e) {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("dbSchema/byId/{dbSchemaId}")
	public ResponseResult<DbSchema> loadDbSchema(@PathVariable String dbSchemaId, HttpServletRequest request) throws EngineException{
		DbSchema dbSchema = platformService.loadDbSchema(dbSchemaId);
		if(!StringUtils.isBlank(dbSchema.getPassword())) {
			dbSchema.setPassword(DbSchema.PWD_MARK_STR);
		}
		if(dbSchema != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbSchema.getSystemId(), dbSchema.getTenantId());
		}
		return ResponseResult.success(dbSchema);
	}

	@RequestMapping("dbSchema/list/{systemId}")
	public ResponseResult<List<DbSchema>> listDbSchema(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		List<DbSchema> dbSchemas = platformService.listDbSchema(systemId);
		if(!CollectionUtils.isEmpty(dbSchemas)) {
			dbSchemas.forEach((dbSchema)-> {
				if(!StringUtils.isBlank(dbSchema.getPassword())) {
					dbSchema.setPassword(DbSchema.PWD_MARK_STR);
				}
			});
		}
		return ResponseResult.success(dbSchemas);
	}
	
	@RequestMapping("dbSchema/delete/{dbSchemaId}")
	public ResponseResult<Integer> deleteDbSchema(@PathVariable String dbSchemaId, HttpServletRequest request) throws EngineException{
		if(this.loadDbSchema(dbSchemaId, request) != null) {
			return ResponseResult.success(platformService.deleteDbSchema(dbSchemaId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("dbTable/add")
	public ResponseResult<DbTable> addDbTable(@RequestBody DbTable dbTable, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbTable.getSystemId(), dbTable.getTenantId());
		try {
			return ResponseResult.success(platformService.addDbTable(dbTable));
		} catch (EngineException e) {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dbTable/byId/{dbTableId}")
	public ResponseResult<DbTable> loadDbTable(@PathVariable String dbTableId, HttpServletRequest request) throws EngineException{
		DbTable dbTable = platformService.loadDbTable(dbTableId);
		if(dbTable != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbTable.getSystemId(), dbTable.getTenantId());
		}
		return ResponseResult.success(dbTable);
	}
	
	@RequestMapping("dbTable/delete/{dbTableId}")
	public ResponseResult<Integer> deleteDbTable(@PathVariable String dbTableId, HttpServletRequest request) throws EngineException{
		if(this.loadDbTable(dbTableId, request) != null) {
			return ResponseResult.success(platformService.deleteDbTable(dbTableId));
		} else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("dbTable/list/{systemId}")
	public ResponseResult<List<DbTable>> listDbTables(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listDbTables(systemId));
	}
	
	@RequestMapping("dbTable/save")
	public ResponseResult<Integer> saveDbTable(@RequestBody DbTable dbTable, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbTable.getSystemId(), dbTable.getTenantId());
		try {
			return ResponseResult.success(platformService.saveDbTable(dbTable));
		} catch (EngineException e) {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("dbColumn/add")
	public ResponseResult<DbColumn> addColumn(@RequestBody DbColumn dbColumn, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbColumn.getSystemId(), dbColumn.getTenantId());
		try {
			return ResponseResult.success(platformService.addDbColumn(dbColumn));
		} catch (EngineException e) {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dbColumn/byId/{dbColumnId}")
	public ResponseResult<DbColumn> loadDbColumn(@PathVariable String dbColumnId, HttpServletRequest request) throws EngineException{
		DbColumn dbColumn = platformService.loadDbColumn(dbColumnId);
		if(dbColumn != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbColumn.getSystemId(), dbColumn.getTenantId());
		}
		return ResponseResult.success(dbColumn);
	}
	
	@RequestMapping("dbColumn/save")
	public ResponseResult<DbColumn> saveDbColumn(@RequestBody DbColumn dbColumn, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dbColumn.getSystemId(), dbColumn.getTenantId());
		try {
			return ResponseResult.success(platformService.saveDbColumn(dbColumn));
		} catch (EngineException e) {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dbColumn/delete/{dbColumnId}")
	public ResponseResult<Integer> deleteDbColumn(@PathVariable String dbColumnId, HttpServletRequest request) throws EngineException{
		if(this.loadDbColumn(dbColumnId, request) != null) {
			return ResponseResult.success(platformService.deleteDbColumn(dbColumnId));
		}else {
			return ResponseResult.success(0);
		}
	}
	
	@RequestMapping("objectRelationMapping/listByDomainObjectId/{domainObjectId}")
	public ResponseResult<List<ObjectRelationMapping>> queryObjectRelationMappingsByDomainObjectId(@PathVariable String domainObjectId, HttpServletRequest request) throws EngineException{
		if(this.loadDomainObject(domainObjectId, request) != null) {
			return ResponseResult.success(this.platformService.queryObjectRelationMappingsByDomainObjectId(domainObjectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("objectRelationMapping/list/{systemId}")
	public ResponseResult<List<ObjectRelationMapping>> queryObjectRelationMappings(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryObjectRelationMappings(systemId));
	}
	
	@RequestMapping("objectRelationMapping/queryName/{ormId}")
	public ResponseResult<String> queryObjectRelationMappingName(@PathVariable String ormId, HttpServletRequest request) throws EngineException{
		ObjectRelationMapping orm = this.platformService.queryObjectRelationMapping(ormId);
		if(orm != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, orm.getSystemId(), orm.getTenantId());
			return ResponseResult.success(orm.getName());
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("objectRelationMapping/listByTableId/{dbTableId}")
	public ResponseResult<List<ObjectRelationMapping>> queryObjectRelationMappingsByTableId(@PathVariable String dbTableId, HttpServletRequest request) throws EngineException{
		if(this.loadDbTable(dbTableId, request) != null) {
			return ResponseResult.success(this.platformService.queryObjectRelationMappingsByTableId(dbTableId));
		}else {
			return ResponseResult.success(null);
		}
		
	}
	
	@RequestMapping("objectRelationMapping/addWithColumnMapping")
	public ResponseResult<ObjectRelationMapping> addObjectRelationMappingWithColumnMapping(@RequestBody ObjectRelationMapping objectRelationMapping, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, objectRelationMapping.getSystemId(), objectRelationMapping.getTenantId());
		return ResponseResult.success(this.platformService.addObjectRelationMappingWithColumnMapping(objectRelationMapping));
	}
	
	@RequestMapping("objectRelationMapping/updateWithColumnMapping")
	public ResponseResult<ObjectRelationMapping> modifyObjectRelationMappingWithColumnMapping(@RequestBody ObjectRelationMapping objectRelationMapping, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, objectRelationMapping.getSystemId(), objectRelationMapping.getTenantId());
		return ResponseResult.success(this.platformService.modifyObjectRelationMappingWithColumnMapping(objectRelationMapping));
	}
	
	@RequestMapping("dataMapping/addWithAttributeMapping")
	public ResponseResult<DataMapping> addDataMappingWithAttributeMapping(@RequestBody DataMapping dataMapping, HttpServletRequest request)  throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataMapping.getSystemId(), dataMapping.getTenantId());
		return ResponseResult.success(this.platformService.addDataMappingWidthAttributeMapping(dataMapping));
	}
	
	@RequestMapping("dataMapping/updateWithAttributeMapping")
	public ResponseResult<DataMapping> modifyDataMappingWithAttributeMapping(@RequestBody DataMapping dataMapping, HttpServletRequest request)  throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataMapping.getSystemId(), dataMapping.getTenantId());
		return ResponseResult.success(this.platformService.modifyDataMappingWithAttributMapping(dataMapping));
	}
	
	@RequestMapping("dataMapping/listByFromObject/{objectType}/{objectId}")
	public ResponseResult<List<DataMapping>> queryDataMappingsByFromObject(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException {
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.queryDataMappingsByFromObject(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dataMapping/listByToObject/{objectType}/{objectId}")
	public ResponseResult<List<DataMapping>> queryDataMappingsByToObject(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException {
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.queryDataMappingsByToObject(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dataMapping/listByObject/{objectType}/{objectId}")
	public ResponseResult<List<DataMapping>> queryDataMappingsByObject(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException {
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.queryDataMappingsByObject(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("dataMapping/list/{systemId}")
	public ResponseResult<List<DataMapping>> queryDataMappings(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.queryDataMappings(systemId));
	}
	
	@RequestMapping("dataMapping/queryName/{dataMappingId}")
	public ResponseResult<String> queryDataMappingName(@PathVariable String dataMappingId, HttpServletRequest request) throws EngineException {
		DataMapping dataMapping = platformService.queryDataMapping(dataMappingId);
		if(dataMapping != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dataMapping.getSystemId(), dataMapping.getTenantId());
			return ResponseResult.success(dataMapping.getName());
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("sqlManagement/list/{systemId}/{resourceType}/{resourceId}")
	public ResponseResult<List<SqlManagement>> querySqlManagements(@PathVariable String systemId, @PathVariable String resourceType, @PathVariable String resourceId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.querySqlManagements(systemId, resourceType, resourceId));
	}
	
	@RequestMapping("serverResource/list/{systemId}/{resourceType}")
	public ResponseResult<List<ServerResource>> listServerResources(@PathVariable String systemId, @PathVariable String resourceType, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		List<ServerResource> serverResources = this.platformService.listServerResources(systemId, resourceType);
		if(!CollectionUtils.isEmpty(serverResources)) {
			serverResources.forEach((serverResource)-> {
				if(!StringUtils.isBlank(serverResource.getPassword())) {
					serverResource.setPassword(ServerResource.PWD_MARK_STR);
				}
			});
		}
		return ResponseResult.success(serverResources);
	}
	
	@RequestMapping("domainObject/list/{systemId}")
	public ResponseResult<List<DomainObject>> queryDomainObjects(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryDomainObjects(systemId));
	}
	
	@RequestMapping("valueObject/list/{systemId}")
	public ResponseResult<List<ValueObject>> queryValueObjects(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryValueObjects(systemId));
	}
	
	@RequestMapping("serviceObject/list/{systemId}")
	public ResponseResult<List<ServiceObject>> queryServiceObjects(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryServiceObjects(systemId));
	}
	
	@RequestMapping("serviceObject/byId/{id}")
	public ResponseResult<ServiceObject> queryServiceObject(@PathVariable String id, HttpServletRequest request) throws EngineException {
		ServiceObject serviceObject = this.platformService.queryServiceObject(id);
		if(serviceObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, serviceObject.getSystemId(), serviceObject.getTenantId());
		}
		return ResponseResult.success(serviceObject);
	}
	
	@RequestMapping("validateRuleGroup/list/{systemId}")
	public ResponseResult<List<ValidateRuleGroup>> queryValidateRuleGroups(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryValidateRuleGroups(systemId));
	}
	
	@RequestMapping("validateRuleGroup/listByObject/{systemId}/{objectType}/{objectId}")
	public ResponseResult<List<ValidateRuleGroup>> queryValidateRuleGroupsByObject(@PathVariable String systemId, @PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.queryValidateRuleGroups(systemId, objectType, objectId));
	}
	
	@RequestMapping("validateRuleGroup/init")
	public ResponseResult<ValidateRuleGroupInitInfo> loadValidateRuleGroupInitInfo(@RequestBody ValidateRuleGroupInitInfo initInfo, HttpServletRequest request) throws EngineException {
		ValidateRuleGroupInitInfo validateRuleGroupInitInfo = this.platformService.loadValidateRuleGroupInitInfo(initInfo);
		if(validateRuleGroupInitInfo != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, validateRuleGroupInitInfo.getSystemId(), validateRuleGroupInitInfo.getTenantId());
		}
		return ResponseResult.success(validateRuleGroupInitInfo);
	}
	
	@RequestMapping("validateRuleGroup/save")
	public ResponseResult<ValidateRuleGroup> saveValidateRuleGroup(@RequestBody ValidateRuleGroup validateRuleGroup, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, validateRuleGroup.getSystemId(), validateRuleGroup.getTenantId());
		return ResponseResult.success(this.platformService.saveValidateRuleGroup(validateRuleGroup));
	}
	
	@RequestMapping("validateRuleGroup/load/{ruleGroupId}")
	public ResponseResult<ValidateRuleGroup> loadValidateRuleGroup(@PathVariable String ruleGroupId, HttpServletRequest request) throws EngineException {
		ValidateRuleGroup validateRuleGroup = this.platformService.loadValidateRuleGroup(ruleGroupId);
		if(validateRuleGroup != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, validateRuleGroup.getSystemId(), validateRuleGroup.getTenantId());
		}
		return ResponseResult.success(validateRuleGroup);
	}
	
	@RequestMapping("validateRuleGroup/queryName/{ruleGroupId}")
	public ResponseResult<String> queryValidateRuleGroupName(@PathVariable String ruleGroupId, HttpServletRequest request) throws EngineException {
		ValidateRuleGroup validateRuleGroup = this.platformService.queryValidateRuleGroup(ruleGroupId);
		if(validateRuleGroup != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, validateRuleGroup.getSystemId(), validateRuleGroup.getTenantId());
			return ResponseResult.success(validateRuleGroup.getName());
		}else {
			return ResponseResult.success(null);
		}
	}
	
	@RequestMapping("exposedService/list/{systemId}")
	public ResponseResult<List<ExposedService>> listExposedServices(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(this.platformService.listExposedServices(systemId));
	}
	
	
	
	@RequestMapping("workflowProcessResource/load/{resourceId}")
	public ResponseResult<WorkflowProcessResource> loadWorkflowProcessResource(@PathVariable String resourceId, HttpServletRequest request)throws EngineException {
		WorkflowProcessResource workflowProcessResource = platformService.loadWorkflowProcessResource(resourceId);
		if(workflowProcessResource != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, workflowProcessResource.getSystemId(), workflowProcessResource.getTenantId());
		}
		return ResponseResult.success(workflowProcessResource);
	}

	@RequestMapping("workflowProcessResource/listDeployed/{systemId}")
	public ResponseResult<List<WorkflowProcessResource>> listWorkflowProcessResourceDeployed(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listWorkflowProcessResourcesDeployed(systemId));
	}
	
	@RequestMapping("businessSystemAuth/queryLoginSessionKey/{systemId}")
	public ResponseResult<String> queryLoginSessionKey(@PathVariable String systemId, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.queryLoginSessionKey(systemId));
	}
	
	@RequestMapping("messageConsumer/save")
	public ResponseResult<MessageConsumer> saveMessageConsumer(@RequestBody MessageConsumer messageConsumer, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, messageConsumer.getSystemId(), messageConsumer.getTenantId());
		return ResponseResult.success(platformService.saveMessageConsumer(messageConsumer));
	}
	
	@RequestMapping("messageConsumer/queryMessageConsumer/{systemId}/{id}")
	public ResponseResult<MessageConsumer> queryMessageConsumer(@PathVariable String systemId, @PathVariable String id, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.queryMessageConsumer(systemId, id));
	}
	
	@RequestMapping("messageConsumer/delete/{systemId}/{id}")
	public ResponseResult<Integer> deleteMessageConsumer(@PathVariable String systemId, @PathVariable String id, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.deleteMessageConsumer(systemId, id));
	}
	
	@RequestMapping("messageConsumer/modify")
	public ResponseResult<MessageConsumer> modifyMessageConsumer(@RequestBody MessageConsumer messageConsumer, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, messageConsumer.getSystemId(), messageConsumer.getTenantId());
		return ResponseResult.success(platformService.modifyMessageConsumer(messageConsumer));
	}

	@RequestMapping("dataTransferMethod/list")
	public ResponseResult<List<DataTransferMethod>> listDataTransferMethod() {
		return ResponseResult.success(platformService.listDataTransferMethod());
	}

	@RequestMapping("dynamicMapping/add")
	public ResponseResult<DynamicMapping> addDynamicMapping(@RequestBody DynamicMapping dynamicMapping, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId(), dynamicMapping.getTenantId());
		return ResponseResult.success(platformService.addDynamicMapping(dynamicMapping));
	}

	@RequestMapping("dynamicMapping/save")
	public ResponseResult<DynamicMapping> saveDynamicMapping(@RequestBody DynamicMapping dynamicMapping, HttpServletRequest request) throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId(), dynamicMapping.getTenantId());
		return ResponseResult.success(platformService.saveDynamicMapping(dynamicMapping));
	}

	@RequestMapping("dynamicMapping/queryByCondition")
	public ResponseResult<List<DynamicMapping>> queryDynamicMappingByCondition(@RequestBody DynamicMapping dynamicMapping, HttpServletRequest request) throws EngineException{
		dynamicMapping.setTenantId(LoginInfoUtils.getLoginUserTenant(request, loginInfoKey));
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId());
		return ResponseResult.success(platformService.queryDynamicMappingByCondition(dynamicMapping));
	}

	@RequestMapping("dynamicMapping/queryName/{dynamicMappingId}")
	public ResponseResult<String> queryDynamicMappingName(@PathVariable String dynamicMappingId, HttpServletRequest request) throws EngineException{
		DynamicMapping dynamicMapping = platformService.queryDynamicMapping(dynamicMappingId);
		if(dynamicMapping != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId(), dynamicMapping.getTenantId());
			return ResponseResult.success(dynamicMapping.getName());
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dynamicMapping/delete/{dynamicMappingId}")
	public ResponseResult<Integer> deleteDynamicMapping(@PathVariable String dynamicMappingId, HttpServletRequest request) throws EngineException{
		DynamicMapping dynamicMapping = platformService.queryDynamicMapping(dynamicMappingId);
		if(dynamicMapping != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId(), dynamicMapping.getTenantId());
			return ResponseResult.success(platformService.deleteDynamicMapping(dynamicMappingId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dynamicMapping/byId/{dynamicMappingId}")
	public ResponseResult<DynamicMapping> loadDynamicMapping(@PathVariable String dynamicMappingId, HttpServletRequest request) throws EngineException{
		DynamicMapping dynamicMapping = platformService.loadDynamicMapping(dynamicMappingId);
		if(dynamicMapping != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMapping.getSystemId(), dynamicMapping.getTenantId());
			return ResponseResult.success(dynamicMapping);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dynamicMappingDetail/add")
	public ResponseResult<DynamicMappingDetail> addDynamicMappingDetail(@RequestBody DynamicMappingDetail dynamicMappingDetail, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMappingDetail.getSystemId(), dynamicMappingDetail.getTenantId());
		return ResponseResult.success(platformService.addDynamicMappingDetail(dynamicMappingDetail));
	}

	@RequestMapping("dynamicMappingDetail/save")
	public ResponseResult<DynamicMappingDetail> saveDynamicMappingDetail(@RequestBody DynamicMappingDetail dynamicMappingDetail, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMappingDetail.getSystemId(), dynamicMappingDetail.getTenantId());
		return ResponseResult.success(platformService.saveDynamicMappingDetail(dynamicMappingDetail));
	}

	@RequestMapping("dynamicMappingDetail/delete/{dynamicMappingDetailId}")
	public ResponseResult<Integer> deleteDynamicMappingDetail(@PathVariable String dynamicMappingDetailId, HttpServletRequest request) throws EngineException{
		if(this.loadDynamicMappingDetail(dynamicMappingDetailId, request) != null) {
			return ResponseResult.success(platformService.deleteDynamicMappingDetail(dynamicMappingDetailId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dynamicMappingDetail/byId/{dynamicMappingId}")
	public ResponseResult<DynamicMappingDetail> loadDynamicMappingDetail(@PathVariable String dynamicMappingDetailId, HttpServletRequest request) throws EngineException{
		DynamicMappingDetail dynamicMappingDetail = platformService.loadDynamicMappingDetail(dynamicMappingDetailId);
		if(dynamicMappingDetail != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dynamicMappingDetail.getSystemId(), dynamicMappingDetail.getTenantId());
		}
		return ResponseResult.success(dynamicMappingDetail);
	}

	@RequestMapping("fileObjectMapping/listByObject/{objectType}/{objectId}") 
	public ResponseResult<List<FileObjectMapping>> loadFileObjectMappings(@PathVariable String objectType, @PathVariable String objectId, HttpServletRequest request) throws EngineException{
		ManagedObject managedObject = platformService.queryManagedObject(objectType, objectId);
		if(managedObject != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, managedObject.getSystemId(), managedObject.getTenantId());
			return ResponseResult.success(platformService.loadFileObjectMappings(objectType, objectId));
		}else {
			return ResponseResult.success(null);
		} 
	}

	@RequestMapping("fileObjectMapping/addWithAttributeColumnMapping")
	public ResponseResult<FileObjectMapping> addWithAttributeColumnMapping(@RequestBody FileObjectMapping fileObjectMapping, HttpServletRequest request)throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fileObjectMapping.getSystemId(), fileObjectMapping.getTenantId());
		return ResponseResult.success(platformService.addWithAttributeColumnMapping(fileObjectMapping));
	}

	@RequestMapping("fileObjectMapping/saveWithAttributeColumnMapping")
	public ResponseResult<FileObjectMapping> saveWithAttributeColumnMapping(@RequestBody FileObjectMapping fileObjectMapping, HttpServletRequest request)throws EngineException {
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fileObjectMapping.getSystemId(), fileObjectMapping.getTenantId());
		return ResponseResult.success(platformService.saveWithAttributeColumnMapping(fileObjectMapping));
	}

	@RequestMapping("fileObjectMapping/queryName/{fileObjectMappingId}")
	public ResponseResult<String> queryFileObjectMappingName(@PathVariable String fileObjectMappingId, HttpServletRequest request)throws EngineException {
		FileObjectMapping fileObjectMapping = platformService.queryFileObjectMapping(fileObjectMappingId);
		if(fileObjectMapping != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, fileObjectMapping.getSystemId(), fileObjectMapping.getTenantId());
			return ResponseResult.success(fileObjectMapping.getName());
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("dictResource/add")
	public ResponseResult<DictResource> addDictResource(@RequestBody DictResource dictResource, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dictResource.getSystemId(), dictResource.getTenantId());
		return ResponseResult.success(platformService.addDictResource(dictResource));
	}

	@RequestMapping("dictResource/save")
	public ResponseResult<Integer> saveDictResource(@RequestBody DictResource dictResource, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dictResource.getSystemId(), dictResource.getTenantId());
		return ResponseResult.success(platformService.saveDictResource(dictResource));
	}

	@RequestMapping("dictResource/delete/{dictResourceId}")
	public ResponseResult<Integer> deleteDictResource(@PathVariable String dictResourceId, HttpServletRequest request) throws EngineException{
		if(this.loadDictResource(dictResourceId, request) != null) {
			return ResponseResult.success(platformService.deleteDictResource(dictResourceId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("dictResource/byId/{dictResourceId}")
	public ResponseResult<DictResource> loadDictResource(@PathVariable String dictResourceId, HttpServletRequest request) throws EngineException{
		DictResource dictResource = platformService.loadDictResource(dictResourceId);
		if(dictResource != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, dictResource.getSystemId(), dictResource.getTenantId());
		}
		return ResponseResult.success(dictResource);
	}

	@RequestMapping("dictResource/list/{systemId}")
	public ResponseResult<List<DictResource>> listDictResources(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listDictResources(systemId));
	}

	@RequestMapping("securityKeyResource/add")
	public ResponseResult<SecurityKeyResource> addSecurityKeyResource(@RequestParam("securityKeyResource") String securityKeyResourceStr, HttpServletRequest request) throws EngineException{
		SecurityKeyResource securityKeyResource = JsonUtils.fromJson(securityKeyResourceStr, SecurityKeyResource.class);
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, securityKeyResource.getSystemId(), securityKeyResource.getTenantId());
		//获取上传的文件对象
		StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) request;
		Map<String, List<MultipartFile>> multipartFileMap = multipartRequest.getMultiFileMap();
		if(!MapUtils.isEmpty(multipartFileMap)) {
			try {
				for(String key : multipartFileMap.keySet()) {
					if(key.equals("privateKeyFile")) {
						securityKeyResource.setPrivateKeyFile(multipartFileMap.get(key).get(0).getBytes());
					}else if(key.equals("publicKeyFile")) {
						securityKeyResource.setPublicKeyFile(multipartFileMap.get(key).get(0).getBytes());
					}
				}
			} catch (IOException e) {
				logger.error("get cert file failed!", e);
				throw new EngineException(ResultStatus.BAD_REQUEST, "get cert file failed!");
			}
		}
		return ResponseResult.success(platformService.addSecurityKeyResource(securityKeyResource));
	}

	@RequestMapping("securityKeyResource/save")
	public ResponseResult<SecurityKeyResource> saveSecurityKeyResource(@RequestParam("securityKeyResource") String securityKeyResourceStr, HttpServletRequest request) throws EngineException{
		SecurityKeyResource securityKeyResource = JsonUtils.fromJson(securityKeyResourceStr, SecurityKeyResource.class);
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, securityKeyResource.getSystemId(), securityKeyResource.getTenantId());
		//获取上传的文件对象
		StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) request;
		Map<String, List<MultipartFile>> multipartFileMap = multipartRequest.getMultiFileMap();
		if(!MapUtils.isEmpty(multipartFileMap)) {
			try {
				for(String key : multipartFileMap.keySet()) {
					if(key.equals("privateKeyFile")) {
						securityKeyResource.setPrivateKeyFile(multipartFileMap.get(key).get(0).getBytes());
					}else if(key.equals("publicKeyFile")) {
						securityKeyResource.setPublicKeyFile(multipartFileMap.get(key).get(0).getBytes());
					}
				}
			} catch (IOException e) {
				logger.error("get cert file failed!", e);
				throw new EngineException(ResultStatus.BAD_REQUEST, "get cert file failed!");
			}
		}
		return ResponseResult.success(platformService.saveSecurityKeyResource(securityKeyResource));
	}

	@RequestMapping("securityKeyResource/byId/{securityKeyResourceId}")
	public ResponseResult<SecurityKeyResource> loadSecurityKeyResource(@PathVariable String securityKeyResourceId, HttpServletRequest request) throws EngineException{
		SecurityKeyResource securityKeyResource = platformService.loadSecurityKeyResource(securityKeyResourceId);
		if(securityKeyResource != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, securityKeyResource.getSystemId(), securityKeyResource.getTenantId());
			securityKeyResource.setPrivateKeyFile(null);
			securityKeyResource.setPublicKeyFile(null);
		}
		return ResponseResult.success(securityKeyResource);
	}

	@RequestMapping("securityKeyResource/list/{systemId}")
	public ResponseResult<List<SecurityKeyResource>> listSecurityKeyResources(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listSecurityKeyResources(systemId));
	}

	@RequestMapping("securityKeyResource/delete/{securityKeyResourceId}")
	public ResponseResult<Integer> deleteSecurityKeyResource(@PathVariable String securityKeyResourceId, HttpServletRequest request) throws EngineException{
		SecurityKeyResource securityKeyResource = platformService.loadSecurityKeyResource(securityKeyResourceId);
		if(securityKeyResource != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, securityKeyResource.getSystemId(), securityKeyResource.getTenantId());
			return ResponseResult.success(platformService.deleteSecurityKeyResource(securityKeyResourceId));
		}
		return ResponseResult.success(0);
	}


	@RequestMapping("cronJob/add")
	public ResponseResult<CronJob> addCronJob(@RequestBody CronJob cronJob, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, cronJob.getSystemId(), cronJob.getTenantId());
		return ResponseResult.success(platformService.addCronJob(cronJob));
	}

	@RequestMapping("cronJob/save")
	public ResponseResult<CronJob> saveCronJob(@RequestBody CronJob cronJob, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, cronJob.getSystemId(), cronJob.getTenantId());
		if(platformService.saveCronJob(cronJob) > 0) {
			return ResponseResult.success(cronJob);
		}else {
			return ResponseResult.success(null);
		}
	}

	@RequestMapping("cronJob/delete/{cronJobId}")
	public ResponseResult<Integer> deleteCronJob(@PathVariable String cronJobId, HttpServletRequest request) throws EngineException{
		if(this.loadCronJob(cronJobId, request) != null) {
			return ResponseResult.success(platformService.deleteCronJob(cronJobId));
		}else {
			return ResponseResult.success(0);
		}
	}

	@RequestMapping("cronJob/byId/{cronJobId}")
	public ResponseResult<CronJob> loadCronJob(@PathVariable String cronJobId, HttpServletRequest request) throws EngineException{
		CronJob cronJob = platformService.loadCronJob(cronJobId);
		if(cronJob != null) {
			LoginInfoUtils.tenantAndSystemGrantCheck(request, loginInfoKey, cronJob.getSystemId(), cronJob.getTenantId());
		}
		return ResponseResult.success(cronJob);
	}

	@RequestMapping("cronJob/list/{systemId}")
	public ResponseResult<List<CronJob>> listCronJobs(@PathVariable String systemId, HttpServletRequest request) throws EngineException{
		LoginInfoUtils.systemGrantCheck(request, loginInfoKey, systemId);
		return ResponseResult.success(platformService.listCronJobs(systemId));
	}

	@RequestMapping("dict/getDictLabelValues")
	public ResponseResult<JsonObject> getDictLabelValues(@RequestBody JsonArray dictCodes) {
		JsonObject dicts = new JsonObject();
		if(!JsonUtils.isEmpty(dictCodes)) {
			for(int i=0; i< dictCodes.size(); i++) {
				String dictCode = dictCodes.get(i).getAsString();
				List<LabelValueVo> labelValueVos = DictProxy.getLabelValues(dictCode);
				dicts.add(dictCode, JsonUtils.toJsonArray(labelValueVos));
			};
		}
		return ResponseResult.success(dicts);
	}

	@RequestMapping("dict/getDictLabelValuesMore")
	public ResponseResult<JsonObject> getDictLabelValuesMore(@RequestBody JsonArray dictCodes) {
		JsonObject dicts = new JsonObject();
		if(!JsonUtils.isEmpty(dictCodes)) {
			for(int i=0; i< dictCodes.size(); i++) {
				String dictCode = dictCodes.get(i).getAsString();
				List<Dict> s = DictProxy.getDicts(dictCode);
				JsonArray labelValueVosMore = new JsonArray();
				s.forEach((e)-> {
					JsonObject row = new JsonObject();
					row.addProperty("label", e.getDictValueLabel());
					row.addProperty("value", e.getDictValue());
					row.addProperty("value2", e.getDictValue2());
					row.addProperty("value3", e.getDictValue3());
					labelValueVosMore.add(row);
				});
				dicts.add(dictCode, labelValueVosMore);
			};
		}
		return ResponseResult.success(dicts);
	}

	@RequestMapping("dict/getDictLabelValuesByCond")
	public ResponseResult<JsonObject> getDictLabelValuesByCond(@RequestBody JsonArray dictConds) {
		JsonObject dicts = new JsonObject();
		if(!JsonUtils.isEmpty(dictConds)) {
			for(int i=0; i< dictConds.size(); i++) {
				JsonObject cond = dictConds.get(i).getAsJsonObject();
				String dictCode = JsonUtils.getString(cond, "dictCode");
				String parentDictCode = JsonUtils.getString(cond, "parentDictCode");
				String parentDictValue = JsonUtils.getString(cond, "parentDictValue");
				if(!StringUtils.isBlank(parentDictValue) && !StringUtils.isBlank(parentDictCode)) {
					List<LabelValueVo> d = DictProxy.getLabelValuesByParent(dictCode, parentDictCode, parentDictValue);
					dicts.add(dictCode, JsonUtils.toJsonArray(d));
				}else {
					List<LabelValueVo> d = DictProxy.getLabelValues(dictCode);
					dicts.add(dictCode, JsonUtils.toJsonArray(d));
				}
			};
		}
		return ResponseResult.success(dicts);
	}

	@RequestMapping("dict/getDicts")
	public ResponseResult<JsonObject> getDicts(@RequestBody JsonArray dictCodes) {
		JsonObject dicts = new JsonObject();
		if(!JsonUtils.isEmpty(dictCodes)) {
			for(int i=0; i< dictCodes.size(); i++) {
				String dictCode = dictCodes.get(i).getAsString();
				List<Dict> d = DictProxy.getDicts(dictCode);
				dicts.add(dictCode, JsonUtils.toJsonArray(d));
			};
		}
		return ResponseResult.success(dicts);
	}

	@RequestMapping("dict/getDictsByCond")
	public ResponseResult<JsonObject> getDictsByCond(@RequestBody JsonArray dictConds) {
		JsonObject dicts = new JsonObject();
		if(!JsonUtils.isEmpty(dictConds)) {
			for(int i=0; i< dictConds.size(); i++) {
				JsonObject cond = dictConds.get(i).getAsJsonObject();
				String dictCode = JsonUtils.getString(cond, "dictCode");
				String parentDictCode = JsonUtils.getString(cond, "parentDictCode");
				String parentDictValue = JsonUtils.getString(cond, "parentDictValue");
				if(!StringUtils.isBlank(parentDictValue) && !StringUtils.isBlank(parentDictCode)) {
					List<Dict> d = DictProxy.getDictsByParent(dictCode, parentDictCode, parentDictValue);
					dicts.add(dictCode, JsonUtils.toJsonArray(d));
				}else {
					List<Dict> d = DictProxy.getDicts(dictCode);
					dicts.add(dictCode, JsonUtils.toJsonArray(d));
				}
			};
		}
		return ResponseResult.success(dicts);
	}

	@RequestMapping("dict/getDictLabels/{dictCode}")
	public ResponseResult<JsonObject> getDictLabels(@PathVariable String dictCode, @RequestBody JsonArray dictValues) {
		JsonObject dictLabels = new JsonObject();
		if(!JsonUtils.isEmpty(dictValues)) {
			for(int i=0; i< dictValues.size(); i++) {
				String dictValue = dictValues.get(i).getAsString();
				String name = DictProxy.getDictLabel(dictCode, dictValue);
				dictLabels.addProperty(dictCode, name);
			};
		}
		return ResponseResult.success(dictLabels);
	}

	@RequestMapping("dict/getManagedObjecTypes")
	public ResponseResult<JsonArray> getManagedObjecTypes() {
		return ResponseResult.success(DictProxy.getManagedObjectTypes());
	}
}
