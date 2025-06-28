package cn.zpaas.lowcode.fe.ide.domain.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.FuncDefineGenerateRequest;
import cn.zpaas.lowcode.vo.MdaTable;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.be.ide.ability.ModelDrivenAbility;
import cn.zpaas.lowcode.be.ide.domain.service.OperationService;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.BusinessDomain;
import cn.zpaas.lowcode.domain.eo.DbSchema;
import cn.zpaas.lowcode.domain.eo.DbTable;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedPage;
import cn.zpaas.lowcode.domain.eo.FuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegion;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegionOperation;
import cn.zpaas.lowcode.domain.eo.ObjectRelationMapping;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.ValueObject;
import cn.zpaas.lowcode.domain.eo.ServiceObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 功能定义领域服务
 *
 * @author zjy
 * createTime 2025年04月05日-09:00:01
 */
@Service
public class FuncDefineGenerator {
	private static Logger logger = LoggerFactory.getLogger(FuncDefineGenerator.class);

	@Autowired
	private FuncDefine funcDefineSV;// 功能定义领域对象

	@Autowired
	private DomainObject domainObjectSV;

	@Autowired
	private Operation operationSV;

	@Autowired
	private ObjectRelationMapping objectRelationMappingSV;

	@Autowired
	private DbTable dbTableSV;

	@Autowired
	private ServiceObject serviceObjectSV;

	@Autowired
	private BusinessDomain businessDomainSV;

	@Autowired
	private Attribute attributeSV;

	@Autowired
	private DbSchema dbSchemaSV;

	@Autowired
	private ModelDrivenAbility modelDrivenAbilitySV;

	@Autowired
	private FuncObjectAssign funcObjectAssignSV;

	@Autowired
	private FuncRegion funcRegionSV;

	@Autowired
	private FuncRegionOperation funcRegionOperationSV;// 领域对象

	@Autowired
	private FuncTemplateRegionOperation funcTemplateRegionOperationSV;

	@Autowired
	private OperationService operationService;

	@Autowired
	private ExposedService exposedServiceSV;

	@Autowired
	private FuncCustomizedPage funcCustomizedPageSV;

	public FuncDefine generateFuncDefineAll(FuncDefineGenerateRequest request) throws EngineException {
		// 当生成级别为生成框架时，创建方式不能选择根据数据库表生成，功能模式不能选择生成代码
		if (FuncDefineGenerateRequest.GENERATE_LEVEL_F.equals(request.getGenerateLevel()) &&
				(FuncDefineGenerateRequest.CREATE_MODE_T.equals(request.getCreateMode())
						|| FuncDefineGenerateRequest.FUNC_PATTERN_C.equals(request.getFuncPattern()))) {
			logger.error("when generateLevel is F, createMode can't be T and funcPattern can't be C!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR,
					"when generateLevel is F, createMode can't be T and funcPattern can't be C!");
		}

		// 插入功能定义记录
		FuncDefine funcDefine = funcDefineSV.create(request.getNewFuncDefine());
		if (funcDefine == null) {
			logger.error("insert funcDefine failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcDefine failed!");
		}
		
		// 处理绑定对象//根据数据库表创建
		if (FuncDefineGenerateRequest.CREATE_MODE_T.equals(request.getCreateMode())) {
			this.processDomainObjectGenerate(funcDefine, request);
		}

		FuncObjectAssign mainObjectAssign = null;// 主绑定对象
		List<FuncObjectAssign> subObjectAssigns = new ArrayList<>();// 从绑定对象列表
		ServiceObject mainServiceObject = null;// 服务对象
		DomainObject mainDomainObject = null;// 主领域对象
		Map<String, Operation> mainOperationMap = new HashMap<>();// 方法名-》Operation
		Map<String, Operation> serviceOperationMap = new HashMap<>();// 方法名-》Operation
		String dbSchemaId = null;// 数据库标识
		String ormId = null;// ORM标识
		// 创建绑定对象
		if (!CollectionUtils.isEmpty(funcDefine.getObjectAssigns())) {
			for (FuncObjectAssign funcObjectAssign : funcDefine.getObjectAssigns()) {
				funcObjectAssign.setFuncId(funcDefine.getId());
				funcObjectAssign.setSystemId(funcDefine.getSystemId());
				funcObjectAssign.setTenantId(funcDefine.getTenantId());
				if (funcObjectAssignSV.create(funcObjectAssign) == null) {
					logger.error("insert funcObjectAssign failed!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcObjectAssign failed!");
				}
				if(FuncObjectAssign.OBJECT_TYPE_B.equals(funcObjectAssign.getObjectType()) || FuncObjectAssign.OBJECT_TYPE_V.equals(funcObjectAssign.getObjectType())) {//数据集/值传递对象不需要处理领域对象生成
					continue;
				}
				// 确定主对象
				if (FuncObjectAssign.ASSIGN_TYPE_M.equals(funcObjectAssign.getAssignType())
						&& mainObjectAssign == null) {
					mainObjectAssign = funcObjectAssign;
					// 加载主对象以及主对象的属性和方法列表信息，用于判断方法是否已经存在
					mainDomainObject = domainObjectSV.loadDomainObjectAndAttrs(mainObjectAssign.getObjectId());
					mainDomainObject.setOperations(operationSV.listByObjectId(mainObjectAssign.getObjectType(),
							mainObjectAssign.getObjectId()));

					if (mainDomainObject.getOperations() != null && !mainDomainObject.getOperations().isEmpty()) {
						for (Operation oper : mainDomainObject.getOperations()) {
							mainOperationMap.put(oper.getCode(), oper);
						}
					}
					// 加载领域对象的ORM信息，用于生成缺失的方法
					List<ObjectRelationMapping> orms = objectRelationMappingSV
							.listByDomainObjectId(mainDomainObject.getId());
					if (orms == null || orms.isEmpty()) {
						logger.error("load ObjectRelationMapping info of domain object failed!");
						throw new EngineException(ResultStatus.BUSINESS_ERROR,
								"load ObjectRelationMapping info of domain object failed!");
					}
					// 根据ORM信息获取到对应的ormId、DBTable和dbSchemaId，用于生成缺失的领域方法
					ObjectRelationMapping orm = orms.get(0);
					ormId = orm.getId();
					DbTable table = dbTableSV.byId(orm.getTableId());
					if (table == null) {
						logger.error("load talbe info of domain object failed!");
						throw new EngineException(ResultStatus.BUSINESS_ERROR,
								"load talbe info of domain object failed!");
					}
					dbSchemaId = table.getSchemaId();

					mainObjectAssign.setAssignObject(mainDomainObject);
					mainObjectAssign.setAttributes(mainDomainObject.getAttributes());

					// 尝试获取服务对象，根据服务对象的Code：mainDomainObject.getCode()+ServiceObject.SERVICE
					mainServiceObject = serviceObjectSV.queryByDomainAndCode(mainDomainObject.getDomainId(),
							mainDomainObject.getCode() + ServiceObject.SERVICE);
					// 如果未获取到，则新生成服务对象信息
					if (mainServiceObject == null) {
						// 当生成模式是生成全部时才生成，当生成模式是生成框架是不生成
						if (FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(request.getGenerateLevel())) {
							mainServiceObject = serviceObjectSV.generateSeviceObjectByDomainObject(mainDomainObject);
							if (mainServiceObject == null) {
								logger.error("generate service object failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"generate service object failed!");
							}
						}
					} else {
						List<Operation> serviceOperations = operationSV.listByObjectId(ManagedObjectType.SERVICE_OBJECT,
								mainServiceObject.getId());
						if (serviceOperations != null && !serviceOperations.isEmpty()) {
							for (Operation operation : serviceOperations) {
								serviceOperationMap.put(operation.getCode(), operation);
							}
						}
					}
				} else {
					DomainObject domainObject = domainObjectSV.loadDomainObjectAndAttrs(funcObjectAssign.getObjectId());
					funcObjectAssign.setAssignObject(domainObject);
					funcObjectAssign.setAttributes(domainObject.getAttributes());
					subObjectAssigns.add(funcObjectAssign);
				}
			}
		}
		//功能模板时，生成功能区域
		if (!CollectionUtils.isEmpty(funcDefine.getFuncRegions())) {
			// 该Map用于在生成功能区域操作时，获取目标区域
			Map<String, String> tplRegion2RegionMap = new HashMap<>();
			// 创建功能区域
			for (FuncRegion funcRegion : funcDefine.getFuncRegions()) {
				funcRegion.setFuncId(funcDefine.getId());
				funcRegion.setSystemId(funcDefine.getSystemId());
				funcRegion.setTenantId(funcDefine.getTenantId());
				if (funcRegionSV.create(funcRegion) == null) {
					logger.error("insert funcRegion failed!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcRegion failed!");
				}
				tplRegion2RegionMap.put(funcRegion.getTplRegionId(), funcRegion.getId());
			}
			if(mainDomainObject != null) {//数据集不需要处理领域对象生成
				// 生成功能区域中的操作和默认的绑定属性
				for (FuncRegion funcRegion : funcDefine.getFuncRegions()) {
					this.generateFuncRegionOperations(funcRegion, tplRegion2RegionMap, mainDomainObject, mainOperationMap,
							serviceOperationMap, mainServiceObject, dbSchemaId, ormId, request.getGenerateLevel());
					funcRegionSV.generateFuncRegionAttrAssigns(funcRegion, mainObjectAssign, subObjectAssigns);
				};
			}
		}

		//自定义功能是，生成自定义主页面
		if(!CollectionUtils.isEmpty(funcDefine.getCustomizedPages())) {
			funcDefine.getCustomizedPages().forEach((customizedPage) -> {
				customizedPage.setFuncId(funcDefine.getId());
				customizedPage.setSystemId(funcDefine.getSystemId());
				customizedPage.setTenantId(funcDefine.getTenantId());
				funcCustomizedPageSV.create(customizedPage);
			});
		}
		return funcDefine;
	}

	
	/**
	 * 当绑定对象是数据库表时，使用该方法生成相关的领域对象
	 * 
	 * @param funcDefine
	 * @param request
	 * @throws EngineException
	 */
	private void processDomainObjectGenerate(FuncDefine funcDefine, FuncDefineGenerateRequest request)
			throws EngineException {
		if (CollectionUtils.isEmpty(funcDefine.getObjectAssigns())) {
			return;
		}
		// 获取对应的业务域信息
		BusinessDomain domain = businessDomainSV.byId(request.getBusinessDomainId());
		if (domain == null) {
			logger.error("invalid businessDomainId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid businessDomainId!");
		}
		// 获取对应的数据库信息
		DbSchema dbSchema = dbSchemaSV.byId(request.getDbSchemaId());
		if (dbSchema == null) {
			logger.error("invalid dbSchemaId!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid dbSchemaId!");
		}
		// 处理表名忽略的前缀信息
		JsonArray ignorePrefixes = this.processIgnorePrefixes(request.getIgnorePrefixString());

		DomainObject domainObject = null;
		String code = null;

		for (FuncObjectAssign funcObjectAssign : funcDefine.getObjectAssigns()) {
			// 判断数据库表对应的领域对象是否已经存在，通过领域对象编码进行判断
			funcObjectAssign.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
			code = this.applyIgnorePrefixes(ignorePrefixes, funcObjectAssign.getObjectId());

			// 构造领域对象的编码
			code = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, code);
			// 根据业务域标识以及领域对象编码查询对应的领域对象
			domainObject = domainObjectSV.queryDomainObject(request.getBusinessDomainId(), code);
			// 当领域对象不时存在，则创建该领域对象
			if (domainObject == null) {
				domainObject = this.generateDomainObject(domain, dbSchema, funcObjectAssign.getObjectId(), ignorePrefixes);
			}

			funcObjectAssign.setObjectType(ManagedObjectType.DOMAIN_OBJECT);
			funcObjectAssign.setObjectId(domainObject.getId());
			// 处理关联属性
			if (!StringUtils.isBlank(funcObjectAssign.getRelAttrId())) {
				Attribute attrObject = this.queryAttributeByDbColumnName(funcObjectAssign.getRelAttrId(),
						domainObject.getId());
				funcObjectAssign.setRelAttribute(attrObject);
				funcObjectAssign.setRelAttrId(attrObject.getId());
			}
			// 处理主属性
			if (!StringUtils.isBlank(funcObjectAssign.getKeyAttrId())) {
				Attribute attrObject = this.queryAttributeByDbColumnName(funcObjectAssign.getKeyAttrId(),
						domainObject.getId());
				funcObjectAssign.setKeyAttribute(attrObject);
				funcObjectAssign.setKeyAttrId(attrObject.getId());
			}
		}
	}

	/**
	 * 根据数据库表名生成相关的领域对象
	 * @param domain
	 * @param dbSchema
	 * @param tableName
	 * @param ignorePrefixes
	 * @return
	 * @throws EngineException
	 */
	private DomainObject generateDomainObject(BusinessDomain domain, DbSchema dbSchema, String tableName, 
						JsonArray ignorePrefixes) throws EngineException{
		List<String> tables = new ArrayList<>();
		tables.add(tableName);

		// 查询表结构信息
		Connection connection = DBSchemaProxy.createConnection(dbSchema);
		List<MdaTable> tableList = null;
		try {
			tableList = modelDrivenAbilitySV.queryTableColumnList(connection, dbSchema.getName(), tables);
		} finally {
			DBSchemaProxy.closeConnection(connection);
		}

		// 生成平台中的DbTable和DbColumn信息
		if (!modelDrivenAbilitySV.generateTables(tableList, dbSchema)) {
			logger.error("generateTables failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateTables failed!");
		}

		// 生成实体对象信息，包括属性、默认方法等
		List<DomainObject> domainObjects = modelDrivenAbilitySV.generateEntityObject(tableList, domain, ignorePrefixes, dbSchema.getId());
		if (domainObjects == null) {
			logger.error("generateEntityObject failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateEntityObject failed!");
		}
		DomainObject domainObject = domainObjects.get(0);

		// 默认生成值传递对象及属性信息
		List<ValueObject> valueObjects = modelDrivenAbilitySV
				.generateValueObject(tableList, domain, ignorePrefixes, dbSchema.getId());
		if (valueObjects == null) {
			logger.error("generateValueObject failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateValueObject failed!");
		}
		// 默认生成数据映射
		if (!modelDrivenAbilitySV.generateDataMappings(domainObjects, valueObjects)) {
			logger.error("generateDataMappings failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateDataMappings failed!");
		}
		return domainObject;
	}

	/**
	 * 根据数据库字段名查询属性信息
	 * 
	 * @param columnName
	 * @param domainObjectId
	 * @return
	 * @throws EngineException
	 */
	private Attribute queryAttributeByDbColumnName(String columnName, String domainObjectId) throws EngineException {
		String attrCode = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
		Attribute attrObject = attributeSV.queryByObjectAndCode(ManagedObjectType.DOMAIN_OBJECT, domainObjectId,
				attrCode);
		if (attrObject == null) {
			logger.error("not found relAttr!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "not found relAttr!");
		}
		return attrObject;
	}

	/**
	 * 处理需要忽略的前缀
	 * 
	 * @param ignorePrefixes
	 * @param code
	 * @return
	 */
	private String applyIgnorePrefixes(JsonArray ignorePrefixes, String code) {
		if (!JsonUtils.isEmpty(ignorePrefixes) && !StringUtils.isBlank(code)) {
			for (int i = 0; i < ignorePrefixes.size(); i++) {
				if (code.startsWith(ignorePrefixes.get(i).getAsString())) {
					code = code.replaceFirst(ignorePrefixes.get(i).getAsString(), "");
					break;
				}
			}
		}
		return code;
	}

	/**
	 * 处理前缀字符串
	 * 
	 * @param ignorePrefixString
	 * @return
	 */
	private JsonArray processIgnorePrefixes(String ignorePrefixString) {
		JsonArray ignorePrefixes = new JsonArray();
		if (!StringUtils.isBlank(ignorePrefixString)) {
			String[] prefixes = ignorePrefixString.split(",");
			for (String prefix : prefixes) {
				ignorePrefixes.add(prefix);
			}
		}
		return ignorePrefixes;
	}

	/**
	 * 根据功能区域操作模板生成功能区域操作
	 * 
	 * @param funcRegion          功能区域
	 * @param tplRegion2RegionMap TplRegionId-》regionId
	 * @param mainDomainObject    主领域对象
	 * @param mainOperationMap    主领域对象的方法map，operationCode-》Operation
	 * @param mainServiceObject   主服务对象
	 * @param dbSchemaId          数据库标识
	 * @param ormId               orm映射标识
	 * @throws EngineException
	 */
	public void generateFuncRegionOperations(FuncRegion funcRegion, Map<String, String> tplRegion2RegionMap,
			DomainObject mainDomainObject, Map<String, Operation> mainOperationMap,
			Map<String, Operation> serviceOperationMap, ServiceObject mainServiceObject,
			String dbSchemaId, String ormId, String generateLevel) throws EngineException {
		if (mainServiceObject == null) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "mainServiceObject is null!");
		}
		// 处理功能区域操作的创建
		List<FuncTemplateRegionOperation> operations = funcTemplateRegionOperationSV
				.listByTplRegion(funcRegion.getTplRegionId());
		if (operations != null && !operations.isEmpty()) {
			FuncRegionOperation funcRegionOperation = null;
			String operationCode = null;
			// 根据操作模板生成功能区域操作信息
			for (FuncTemplateRegionOperation tplOperation : operations) {
				funcRegionOperation = new FuncRegionOperation();
				funcRegionOperation.setFuncId(funcRegion.getFuncId());
				funcRegionOperation.setFuncRegionId(funcRegion.getId());
				funcRegionOperation.setTplOperationId(tplOperation.getId());
				if (tplOperation.getTargetTplRegionId() != null) {
					funcRegionOperation.setTargetRegionId(tplRegion2RegionMap.get(tplOperation.getTargetTplRegionId()));
				}
				funcRegionOperation.setDisplayOrder(tplOperation.getDisplayOrder());
				funcRegionOperation.setDisplayType(tplOperation.getDisplayType());
				funcRegionOperation.setIsUserDefined(YesOrNo.NO);
				funcRegionOperation.setName(tplOperation.getName());
				funcRegionOperation.setOperationType(tplOperation.getOperationType());
				funcRegionOperation.setSystemId(funcRegion.getSystemId());
				funcRegionOperation.setTenantId(funcRegion.getTenantId());

				// 根据操作类型获取对应的领域对象方法，如果没有，则创建
				Operation domainOperation = null;
				String transactionRequired = null;
				switch (funcRegionOperation.getOperationType()) {
					// 新增类领域方法
					case FuncTemplateRegionOperation.OPERATION_TYPE_C:
						// 尝试根据方法名获取对应的领域方法
						operationCode = OperationService.ADD + mainDomainObject.getCode();
						domainOperation = mainOperationMap.get(operationCode);
						// 如果未获取到且生成级别为生成全部时，生成对应的领域方法
						if (domainOperation == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							domainOperation = operationService.generateAddOperation(mainDomainObject, dbSchemaId,
									ormId);
							if (domainOperation == null) {
								logger.error("generateAddOperation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR, "generateAddOperation failed!");
							} else {
								mainOperationMap.put(operationCode, domainOperation);
							}
						}
						transactionRequired = YesOrNo.YES;
						break;
					// 修改类领域方法
					case FuncTemplateRegionOperation.OPERATION_TYPE_U:
						operationCode = OperationService.MODIFY + mainDomainObject.getCode() + OperationService.BY_ID;
						domainOperation = mainOperationMap.get(operationCode);
						if (domainOperation == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							domainOperation = operationService.generateModifyByIdOperation(mainDomainObject, dbSchemaId,
									ormId);
							if (domainOperation == null) {
								logger.error("generateModifyByIdOperation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"generateModifyByIdOperation failed!");
							} else {
								mainOperationMap.put(operationCode, domainOperation);
							}
						}
						transactionRequired = YesOrNo.YES;
						break;
					// 删除类领域方法
					case FuncTemplateRegionOperation.OPERATION_TYPE_D:
						operationCode = OperationService.DELETE + mainDomainObject.getCode() + OperationService.BY_ID;
						domainOperation = mainOperationMap.get(operationCode);
						if (domainOperation == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							domainOperation = operationService.generateDeleteByIdOperation(mainDomainObject, dbSchemaId,
									ormId);
							if (domainOperation == null) {
								logger.error("generateDeleteByIdOperation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"generateDeleteByIdOperation failed!");
							} else {
								mainOperationMap.put(operationCode, domainOperation);
							}
						}
						transactionRequired = YesOrNo.YES;
						break;
					// 查询列表类领域方法
					case FuncTemplateRegionOperation.OPERATION_TYPE_Q:
						operationCode = OperationService.QUERY + mainDomainObject.getCode()
								+ OperationService.BY_CONDITION;
						domainOperation = mainOperationMap.get(operationCode);
						if (domainOperation == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							domainOperation = operationService.generateQueryByConditionOperation(mainDomainObject,
									dbSchemaId, ormId);
							if (domainOperation == null) {
								logger.error("generateQueryByConditionOperation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"generateQueryByConditionOperation failed!");
							} else {
								mainOperationMap.put(operationCode, domainOperation);
							}
						}
						transactionRequired = YesOrNo.NO;
						break;
					// 主键查询类领域方法
					case FuncTemplateRegionOperation.OPERATION_TYPE_B:
						operationCode = OperationService.QUERY + mainDomainObject.getCode() + OperationService.BY_ID;
						domainOperation = mainOperationMap.get(operationCode);
						if (domainOperation == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							domainOperation = operationService.generateQueryByIdOperation(mainDomainObject, dbSchemaId,
									ormId);
							if (domainOperation == null) {
								logger.error("generateQueryByIdOperation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"generateQueryByIdOperation failed!");
							} else {
								mainOperationMap.put(operationCode, domainOperation);
							}
						}
						transactionRequired = YesOrNo.NO;
						break;
					case FuncTemplateRegionOperation.OPERATION_TYPE_N:

						break;
					case FuncTemplateRegionOperation.OPERATION_TYPE_P:

						break;
					case FuncTemplateRegionOperation.OPERATION_TYPE_S:

						break;
					default:
						logger.error("invalid operation type!");
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operation type!");
				}
				// 当domainOperation不为空时，表示当前操作需要生成对应的服务方法并对外暴露
				if (domainOperation != null) {
					// 尝试获取领域方法对应的服务方法，根据方法名
					Operation operation = serviceOperationMap.get(domainOperation.getCode());
					// 如果获取不到且生成级别为生成全部时，生成对应的服务方法
					if (operation == null && FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
						// 生成对应的服务方法
						operation = operationService.generateLocalInvokeOperation(mainServiceObject, dbSchemaId,
								mainDomainObject, domainOperation, transactionRequired);
						if (operation == null) {
							logger.error("generate service operation failed!");
							throw new EngineException(ResultStatus.BUSINESS_ERROR,
									"generate service operation failed!");
						}
						serviceOperationMap.put(operation.getCode(), operation);
					}
					ExposedService exposedService = null;
					// 当存在对应的服务方法时
					if (operation != null) {
						// 尝试获取该服务方法对应的对外暴露信息
						exposedService = exposedServiceSV.listByServiceAndOperation(mainServiceObject.getId(),
								operation.getId());
						// 如果未获取到对应的对外暴露信息且生成级别为生成全部时，生成对外暴露记录
						if (exposedService == null
								&& FuncDefineGenerateRequest.GENERATE_LEVEL_A.equals(generateLevel)) {
							// 对外暴露服务
							exposedService = exposedServiceSV.exposeServiceOperation(mainServiceObject, operation,
									mainDomainObject);
							if (exposedService == null) {
								logger.error("expose service operation failed!");
								throw new EngineException(ResultStatus.BUSINESS_ERROR,
										"expose service operation failed!");
							}
						}
					}

					if (exposedService != null) {
						funcRegionOperation.setExposedServiceId(exposedService.getId());
					}
					if (mainServiceObject != null) {
						funcRegionOperation.setServiceObjectId(mainServiceObject.getId());
					}
					if (operation != null) {
						funcRegionOperation.setServiceOperationId(operation.getId());
					}
				} else {
					funcRegionOperation.setExposedServiceId(null);
					funcRegionOperation.setServiceObjectId(null);
					funcRegionOperation.setServiceOperationId(null);
				}
				// 生成功能区域操作信息
				if (funcRegionOperationSV.insert(funcRegionOperation) == null) {
					logger.error("generate funcRegionOperation failed!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate funcRegionOperation failed!");
				}
			}
		}

	}
}
