package cn.zpaas.lowcode.fe.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.bi.proxy.DataSetProxy;
import cn.zpaas.lowcode.constant.ManagedObjectType;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.constant.TemplateType;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.BusinessSystem;
import cn.zpaas.lowcode.domain.eo.DataModelFilter;
import cn.zpaas.lowcode.domain.eo.DataModelFilterValue;
import cn.zpaas.lowcode.domain.eo.DataSet;
import cn.zpaas.lowcode.domain.eo.DataSetDetail;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayout;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedLayoutRegion;
import cn.zpaas.lowcode.domain.eo.FuncCustomizedPage;
import cn.zpaas.lowcode.domain.eo.FuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncObjectAssign;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.domain.eo.FuncRegion;
import cn.zpaas.lowcode.domain.eo.FuncRegionAttrAssign;
import cn.zpaas.lowcode.domain.eo.FuncRegionOperation;
import cn.zpaas.lowcode.domain.eo.FuncTemplate;
import cn.zpaas.lowcode.domain.eo.FuncTemplateRegion;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.vo.AttributeVo;
import cn.zpaas.lowcode.fe.vo.AttributedObjectVo;
import cn.zpaas.lowcode.fe.vo.DataSetDetailVo;
import cn.zpaas.lowcode.fe.vo.FuncCustomizedLayoutRegionVo;
import cn.zpaas.lowcode.fe.vo.FuncCustomizedLayoutVo;
import cn.zpaas.lowcode.fe.vo.FuncCustomizedPageVo;
import cn.zpaas.lowcode.fe.vo.FuncDefineVo;
import cn.zpaas.lowcode.fe.vo.FuncObjectAssignVo;
import cn.zpaas.lowcode.fe.vo.FuncObjectAttrOptionsVo;
import cn.zpaas.lowcode.fe.vo.FuncRegionAttrAssignVo;
import cn.zpaas.lowcode.fe.vo.FuncRegionOperationVo;
import cn.zpaas.lowcode.fe.vo.FuncRegionVo;
import cn.zpaas.lowcode.fe.vo.FuncTemplateRegionVo;
import cn.zpaas.lowcode.fe.vo.FuncTemplateVo;
import cn.zpaas.lowcode.fe.vo.OperationVo;
import cn.zpaas.lowcode.fe.vo.ParameterVo;
import cn.zpaas.lowcode.be.engine.controller.FrontController;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ValueObjectProxy;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;

/**
 * 功能定义领域服务
 *
 * @author zjy
 * createTime 2025年04月05日-09:32:57
 */
@Service
public class FuncDefineService {
	
    @Value("${init.param.servicePath:service}")
	private String initServicePath;
	@Value("${init.param.streamServicePath:streamService}")
	private String initstreamServicePath;
	@Value("${init.param.fileUploadServicePath:fileUploadService}")
	private String initFileUploadServicePath;
	@Value("${init.param.fileDownloadServicePath:fileDownloadService}")
	private String initFileDownloadServicePath;

	@Autowired
	private FrontController frontController;

    @Autowired
    private FuncDefine funcDefineSV;//领域对象

    @Autowired
    private FuncRegion funcRegionSV;

	@Autowired
	private FuncRegionOperation funcRegionOperationSV;

	@Autowired
	private FuncRegionAttrAssign funcRegionAttrAssignSV;
    
    @Autowired
    private FuncTemplateRegion funcTemplateRegionSV;

    @Autowired
    private FuncTemplate funcTemplateSV;
    
    @Autowired
    private BusinessSystem businessSystemSV;

	@Autowired
    private FuncObjectAssign funcObjectAssignSV;//领域对象

    @Autowired
    private FuncObjectAssignService funcObjectAssignService;

	@Autowired
    private FuncObjectAttrOptions funcObjectAttrOptionsSV;

	@Autowired
    private Attribute attributeSV;

	@Autowired
	private FuncCustomizedPage funcCustomizedPageSV;

	@Autowired
	private FuncCustomizedLayout funcCustomizedLayoutSV;

	@Autowired
	private FuncCustomizedLayoutRegion funcCustomizedLayoutRegionSV;

	/**
	 * 缓存加载时，初始化功能定义的请求前缀信息
	 * @param funcDefine
	 */
	public void initFuncDefineUrlPrefix(FuncDefine funcDefine) {
		if(funcDefine == null) {
			return;
		}
		//设置业务系统的URL以及引擎提供三类服务的URL信息
    	funcDefine.setSystemUrl(frontController.getBusinessSystem(funcDefine.getSystemId()).getServletContext());
    	funcDefine.setPlatformUrl(this.initServicePath);
    	funcDefine.setPlatformFileUploadUrl(this.initFileUploadServicePath);
    	funcDefine.setPlatformFileDownloadUrl(this.initFileDownloadServicePath);
	}

    /**
     * 加载功能定义的全部信息，包括子对象信息，用于工作台配置功能信息
     * @param id
     * @return
     * @throws EngineException
     */
    public FuncDefine loadFuncDefineAll(String id) throws EngineException{
    	return this.loadFuncDefineAll(id, false);
    }
    
    /**
     * 加载功能定义的全部信息，包括子对象信息
     * @param id
     * @param initFlag 功能初始化/配置功能加载
     * @return
     * @throws EngineException
     */
    public FuncDefine loadFuncDefineAll(String id, boolean initFlag) throws EngineException{
    	//加载功能定义信息
    	FuncDefine funcDefine = funcDefineSV.byId(id);
    	if(funcDefine == null || !Status.EFF.equals(funcDefine.getStatus())) {
    		return null;
    	}
    	//加载业务系统信息
    	BusinessSystem businessSystem = businessSystemSV.byId(funcDefine.getSystemId());
    	if(businessSystem == null || !Status.EFF.equals(businessSystem.getStatus())) {
    		return null;
    	}
    	//设置业务系统的URL以及引擎提供三类服务的URL信息
    	funcDefine.setSystemUrl(businessSystem.getServletContext());
    	funcDefine.setPlatformUrl(this.initServicePath);
    	funcDefine.setPlatformFileUploadUrl(this.initFileUploadServicePath);
    	funcDefine.setPlatformFileDownloadUrl(this.initFileDownloadServicePath);
    	//加载功能模板信息
    	funcDefine.setFuncTemplate(funcTemplateSV.byId(funcDefine.getTemplateId()));
    	//加载功能绑定对象信息，包括涉及的属性、对象信息
    	List<FuncObjectAssign> objectAssigns = funcObjectAssignService.loadByFunc(id, initFlag);
    	if(objectAssigns != null && !objectAssigns.isEmpty()) {
    		//设置绑定对象列表信息，方便页面循环展示使用
    		funcDefine.setObjectAssigns(objectAssigns);
    	}
		//自定义功能，需要加载功能自定义页面信息
		if(TemplateType.TEMPLATE_TYPE_C.equals(funcDefine.getFuncTemplate().getTemplateType())) {
			funcDefine.setCustomizedPages(funcCustomizedPageSV.loadFuncCustomizedPagesAll(id));
		}else {
			//加载功能区域列表信息，包括相关的子对象信息
			List<FuncRegion> funcRegions = funcRegionSV.loadAllByFuncId(id);
			if(!CollectionUtils.isEmpty(funcRegions)) {
				//加载区域模板信息
				List<FuncTemplateRegion> tplRegions = funcTemplateRegionSV.list();
				//tplRegionId-》FuncTemplateRegion
				Map<String, FuncTemplateRegion> tplRegionMap = new HashMap<>();
				if(tplRegions != null && !tplRegions.isEmpty()) {
					tplRegionMap = tplRegions.stream().collect(Collectors.toMap(FuncTemplateRegion::getId, t->t));
				}
				//补充功能区域的区域模板信息以及区域绑定属性对象中的属性信息
				for(FuncRegion funcRegion : funcRegions) {
					funcRegion.setTplRegion(tplRegionMap.get(funcRegion.getTplRegionId()));
				}
	
				//功能区域列表，前台界面用于循环展示
				funcDefine.setFuncRegions(funcRegions);
			}
		}
    	return funcDefine;
    }

	private void linkLayoutWithRegionRo(List<FuncCustomizedLayoutVo> customizedLayouts, Map<String, FuncRegionVo> regionMap) {
		if(CollectionUtils.isEmpty(customizedLayouts)) {
			return;
		}
		customizedLayouts.forEach((layout) -> {
			if(!CollectionUtils.isEmpty(layout.getLayoutRegions())) {
				layout.getLayoutRegions().forEach((layoutRegion) -> {
					layoutRegion.setFuncRegion(regionMap.get(layoutRegion.getRegionId()));
				});
			}
			if(!CollectionUtils.isEmpty(layout.getSubCustomizedLayouts())) {
				this.linkLayoutWithRegionRo(layout.getSubCustomizedLayouts(), regionMap);
			}
		});
	}
    
    /**
     * 功能初始化，用于功能初始化
     * @param id
     * @return
     * @throws EngineException
     */
    public FuncDefine funcInit(String id) throws EngineException{
    	return this.loadFuncDefineAll(id, true);
    }

	/**
	 * 缓存功能定义信息
	 * @param systemId
	 * @return
	 * @throws EngineException
	 */
	public List<FuncDefineVo> cacheFuncDefines(String systemId){
    	//加载功能定义信息
    	List<FuncDefine> funcDefines = null;
		if(StringUtils.isBlank(systemId)) {
			funcDefines = funcDefineSV.list();
		}else {
			funcDefines = funcDefineSV.listBySystem(systemId);
		}
    	if(CollectionUtils.isEmpty(funcDefines)) {
    		return new ArrayList<>();
    	}

		//初始化缓存FuncDefineRo对象列表
		List<FuncDefineVo> funcDefineVos = new ArrayList<>();

		//加载功能绑定对象信息，包括涉及的属性、对象信息
    	List<FuncObjectAssignVo> objectAssigns = cacheFuncObjectAssigns(systemId);
		Map<String, List<FuncObjectAssignVo>> funcObjectAssignMap = null;
    	if(!CollectionUtils.isEmpty(objectAssigns)) {
    		funcObjectAssignMap = objectAssigns.stream().collect(Collectors.groupingBy(FuncObjectAssignVo::getFuncId));
    	}else {
			funcObjectAssignMap = new HashMap<>();
		}

		//加载功能区域列表信息，包括相关的子对象信息
    	List<FuncRegionVo> funcRegions = this.cacheFuncRegions(systemId);
		Map<String, List<FuncRegionVo>> funcRegionMap = null;
    	if(!CollectionUtils.isEmpty(funcRegions)) {
			funcRegionMap = funcRegions.stream().collect(Collectors.groupingBy(FuncRegionVo::getFuncId));
    	}else {
			funcRegionMap = new HashMap<>();
		}

		//加载功能自定义页面信息，包括相关的子对象信息
    	List<FuncCustomizedPageVo> funcCustomizedPageVos = this.cacheFuncCustomizedPages(systemId);

		if(!CollectionUtils.isEmpty(funcCustomizedPageVos)) {
			Map<String, FuncRegionVo> regionMap = funcRegions.stream().collect(Collectors.toMap(FuncRegionVo::getId, t->t));
			funcCustomizedPageVos.forEach((page) -> {
				//递归关联自定义布局与功能区域
				this.linkLayoutWithRegionRo(page.getCustomizedLayouts(), regionMap);
			});
		}

		Map<String, List<FuncCustomizedPageVo>> funcCustomizedPageVosForFuncMap = funcCustomizedPageVos.stream().collect(Collectors.groupingBy(FuncCustomizedPageVo::getFuncId));

		//加载功能模板信息
		List<FuncTemplateVo> funcTemplateVos = this.cloneFuncTemplates(funcTemplateSV.list());
		Map<String, FuncTemplateVo> funcTemplateMap = null;
		if(CollectionUtils.isEmpty(funcTemplateVos)) {
			funcTemplateMap = new HashMap<>();
		}else {
			funcTemplateMap = funcTemplateVos.stream().collect(Collectors.toMap(FuncTemplateVo::getId, t->t));
		}

		FuncDefineVo funcDefineVo = null;
		for(FuncDefine funcDefine: funcDefines) {
			//初始化功能定义值传递对象
			funcDefineVo = this.cloneFuncDefine(funcDefine);

			//设置业务系统的URL以及引擎提供三类服务的URL信息
			if(frontController.getBusinessSystem(funcDefine.getSystemId()) != null) {
				funcDefineVo.setSystemUrl(frontController.getBusinessSystem(funcDefine.getSystemId()).getServletContext());
			}
			funcDefineVo.setPlatformUrl(this.initServicePath);
			funcDefineVo.setPlatformStreamUrl(this.initstreamServicePath);
			funcDefineVo.setPlatformFileUploadUrl(this.initFileUploadServicePath);
			funcDefineVo.setPlatformFileDownloadUrl(this.initFileDownloadServicePath);


			//设置绑定对象信息
			funcDefineVo.setObjectAssigns(funcObjectAssignMap.get(funcDefineVo.getId()));

			//设置对应的功能模板
			funcDefineVo.setFuncTemplate(funcTemplateMap.get(funcDefine.getTemplateId()));

			if(TemplateType.TEMPLATE_TYPE_C.equals(funcDefineVo.getFuncTemplate().getTemplateType())) {
				//自定义功能
				funcDefineVo.setCustomizedPages(funcCustomizedPageVosForFuncMap.get(funcDefineVo.getId()));
			}else {
				//功能区域列表，前台界面用于循环展示
				funcDefineVo.setFuncRegions(funcRegionMap.get(funcDefineVo.getId()));
			}
			
			//加入缓存列表
			funcDefineVos.add(funcDefineVo);
		}
   		return funcDefineVos;
    }

	public List<FuncDefineVo> cacheFuncDefineAndTpl(String systemId) {
    	//加载功能定义信息
    	List<FuncDefine> funcDefines = null;
		if(StringUtils.isBlank(systemId)) {
			funcDefines = funcDefineSV.list();
		}else {
			funcDefines = funcDefineSV.listBySystem(systemId);
		}
    	if(CollectionUtils.isEmpty(funcDefines)) {
    		return new ArrayList<>();
    	}

		//加载功能模板信息
		List<FuncTemplateVo> funcTemplateVos = this.cloneFuncTemplates(funcTemplateSV.list());
		Map<String, FuncTemplateVo> funcTemplateMap = null;
		if(CollectionUtils.isEmpty(funcTemplateVos)) {
			funcTemplateMap = new HashMap<>();
		}else {
			funcTemplateMap = funcTemplateVos.stream().collect(Collectors.toMap(FuncTemplateVo::getId, t->t));
		}

		List<FuncDefineVo> funcDefineVos = new ArrayList<>();
		FuncDefineVo funcDefineVo = null;
		for(FuncDefine funcDefine : funcDefines) {
			funcDefineVo = this.cloneFuncDefine(funcDefine);
			funcDefineVo.setFuncTemplate(funcTemplateMap.get(funcDefine.getTemplateId()));
			funcDefineVos.add(funcDefineVo);
		}
		return funcDefineVos;
    }

	/**
	 * 缓存绑定对象属性选项设置信息
	 * @param systemId
	 * @return
	 */
	public List<FuncObjectAttrOptions> cacheFuncObjectAttrOptions(String systemId){
		//加载绑定对象属性选项信息
		if(StringUtils.isBlank(systemId)) {
			return funcObjectAttrOptionsSV.list();
		}else {
			return funcObjectAttrOptionsSV.listBySystem(systemId);
		}
	}

	/**
	 * 用于初始化时的缓存加载
	 * @param systemId
	 * @return
	 */
	public List<FuncObjectAssignVo> cacheFuncObjectAssigns(String systemId){
    	//加载功能绑定对象信息
		List<FuncObjectAssign> funcObjectAssigns = null;
		if(StringUtils.isBlank(systemId)) {
			funcObjectAssigns = funcObjectAssignSV.list();
		}else {
			funcObjectAssigns = funcObjectAssignSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcObjectAssigns)) {
			return null;
		}

		//加载绑定对象属性选项信息
		List<FuncObjectAttrOptions> funcObjectAttrOptionss = null;
		if(StringUtils.isBlank(systemId)) {
			funcObjectAttrOptionss = funcObjectAttrOptionsSV.list();
		}else {
			funcObjectAttrOptionss = funcObjectAttrOptionsSV.listBySystem(systemId);
		}
		if(funcObjectAttrOptionss == null) {
			funcObjectAttrOptionss = new ArrayList<>();
		}
		//FuncObjectAttrOptions-->FuncObjectAttrOptionsRo
		List<FuncObjectAttrOptionsVo> funcObjectAttrOptionsVos = new ArrayList<>();
		for(FuncObjectAttrOptions funcObjectAttrOptions : funcObjectAttrOptionss) {
			funcObjectAttrOptionsVos.add(cloneFuncObjectAttrOptions(funcObjectAttrOptions));
		}

		Map<String, List<FuncObjectAttrOptionsVo>> attrOptionsMap = funcObjectAttrOptionsVos.stream().collect(Collectors.groupingBy(FuncObjectAttrOptionsVo::getObjectAssignId));

		List<FuncObjectAssignVo> funcObjectAssignVos = new ArrayList<>();
		FuncObjectAssignVo funcObjectAssignVo = null;
    	for(FuncObjectAssign objectAssign : funcObjectAssigns) {
			funcObjectAssignVo = cloneFuncObjectAssignRo(objectAssign);
			funcObjectAssignVos.add(funcObjectAssignVo);
			//设置绑定对象属性选项信息
    		List<FuncObjectAttrOptionsVo> attrOptions = attrOptionsMap.get(objectAssign.getId());
    		if(!CollectionUtils.isEmpty(attrOptions)) {
    			funcObjectAssignVo.setAttrOptionMap(attrOptions.stream().collect(Collectors.toMap(FuncObjectAttrOptionsVo::getAttributeId, t->t)));
    		}
    		
			if(ManagedObjectType.BI_DATA_SET.equals(objectAssign.getObjectType())) {
				DataSet dataSet = DataSetProxy.getDataSet(objectAssign.getSystemId(), objectAssign.getObjectId());
				if(dataSet != null) {
					funcObjectAssignVo.setAssignObjectName(dataSet.getName());
					funcObjectAssignVo.setDataSetDetails(this.cloneDataSetDetails(dataSet.getDetails()));
				}
			}else {
				//获取绑定对象的属性列表
				AttributedObject assignObject = null;
				if(ManagedObjectType.DOMAIN_OBJECT.equals(objectAssign.getObjectType())) {
					assignObject = DomainObjectProxy.getInstance().getDomainObject(objectAssign.getSystemId(), objectAssign.getObjectId());
				}else if(ManagedObjectType.VALUE_OBJECT.equals(objectAssign.getObjectType())) {
					assignObject = ValueObjectProxy.getInstance().getValueObject(objectAssign.getSystemId(), objectAssign.getObjectId());
				}
				
				if(assignObject != null) {
					List<AttributeVo> attributes  = this.cloneAttributes(assignObject.getAttributes());
					funcObjectAssignVo.setAssignObjectName(assignObject.getName());
					//设置关键属性、关联属性以及主对象关联属性
					if(!CollectionUtils.isEmpty(attributes)) {
						funcObjectAssignVo.setAttributes(attributes);
						for(AttributeVo attribute : attributes) {
							if(attribute.getId().equals(funcObjectAssignVo.getKeyAttrId())) {
								funcObjectAssignVo.setKeyAttribute(attribute);
							} 
							if(attribute.getId().equals(funcObjectAssignVo.getRelAttrId())) {
								funcObjectAssignVo.setRelAttribute(attribute);
							}
						}
					}
				}
			}
			if(!StringUtils.isBlank(funcObjectAssignVo.getMainRelAttrId())) {
				funcObjectAssignVo.setMainRelAttribute(this.cloneAttribute(attributeSV.byId(funcObjectAssignVo.getMainRelAttrId())));
			}
    	}
    	return funcObjectAssignVos;
    }

	/**
	 * 缓存功能自定义页面信息
	 * @param systemId
	 * @return
	 */
	private List<FuncCustomizedPageVo> cacheFuncCustomizedPages(String systemId) {
		List<FuncCustomizedPage> funcCustomizedPages = null;
		if(StringUtils.isBlank(systemId)) {
			funcCustomizedPages = funcCustomizedPageSV.list();
		}else {
			funcCustomizedPages = funcCustomizedPageSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcCustomizedPages)) {
			return new ArrayList<>();
		}

		List<FuncCustomizedLayoutVo> funcCustomizedLayoutVos = this.cacheFuncCustomizedLayouts(systemId);
		List<FuncCustomizedLayoutVo> firstLevelLayouts = new ArrayList<>();
        List<FuncCustomizedLayoutVo> subLevelLayouts = new ArrayList<>();
        for(FuncCustomizedLayoutVo funcCustomizedLayout : funcCustomizedLayoutVos) {
            if(StringUtils.isBlank(funcCustomizedLayout.getParentLayoutId())) {
                firstLevelLayouts.add(funcCustomizedLayout);
            }else {
                subLevelLayouts.add(funcCustomizedLayout);
            }
        }
        Map<String, List<FuncCustomizedLayoutVo>> layoutMap = subLevelLayouts.stream().collect(Collectors.groupingBy(FuncCustomizedLayoutVo::getParentLayoutId));
        this.processLayoutLevel(firstLevelLayouts, layoutMap);
        
		Map<String, List<FuncCustomizedLayoutVo>> funcCustomizedLayoutVosForPage = firstLevelLayouts.stream().collect(Collectors.groupingBy(FuncCustomizedLayoutVo::getPageId));

		List<FuncCustomizedPageVo> funcCustomizedPageVos = new ArrayList<>();
		for(FuncCustomizedPage funcCustomizedPage : funcCustomizedPages) {
			FuncCustomizedPageVo vo = this.cloneFuncCustomizedPage(funcCustomizedPage);
			vo.setCustomizedLayouts(funcCustomizedLayoutVosForPage.get(vo.getId()));
			funcCustomizedPageVos.add(vo);
		}
		return funcCustomizedPageVos;
	}

	/**
	 * 缓存FuncCustomizedLayout
	 * @param systemId
	 * @return
	 */
	private List<FuncCustomizedLayoutVo> cacheFuncCustomizedLayouts(String systemId) {
		List<FuncCustomizedLayout> funcCustomizedLayouts = null;
		if(StringUtils.isBlank(systemId)) {
			funcCustomizedLayouts = funcCustomizedLayoutSV.list();
		}else {
			funcCustomizedLayouts = funcCustomizedLayoutSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcCustomizedLayouts)) {
			return new ArrayList<>();
		}

		List<FuncCustomizedLayoutRegionVo> funcCustomizedLayoutRegionVos = this.cacheFuncCustomizedLayoutRegions(systemId);
		Map<String, List<FuncCustomizedLayoutRegionVo>> funcCustomizedLayoutRegionVosForLayoutMap = funcCustomizedLayoutRegionVos.stream().collect(Collectors.groupingBy(FuncCustomizedLayoutRegionVo::getLayoutId));
		List<FuncCustomizedLayoutVo> funcCustomizedLayoutVos = new ArrayList<>();
		for(FuncCustomizedLayout funcCustomizedLayout : funcCustomizedLayouts) {
			FuncCustomizedLayoutVo vo = this.cloneFuncCustomizedLayout(funcCustomizedLayout);
			vo.setLayoutRegions(funcCustomizedLayoutRegionVosForLayoutMap.get(vo.getId()));
			funcCustomizedLayoutVos.add(vo);	
		}
		return funcCustomizedLayoutVos;
	}

	/**
     * 处理布局的父子层级关系
     * @param firstLevelLayouts
     * @param layoutMap
     */
    private void processLayoutLevel(List<FuncCustomizedLayoutVo> firstLevelLayouts, Map<String, List<FuncCustomizedLayoutVo>> layoutMap) {
        if(CollectionUtils.isEmpty(firstLevelLayouts)) {
            return;
        }
        firstLevelLayouts.forEach((layout) -> {
            layout.setSubCustomizedLayouts(layoutMap.get(layout.getId()));
            if(!CollectionUtils.isEmpty(layout.getSubCustomizedLayouts())) {
                this.processLayoutLevel(layout.getSubCustomizedLayouts(), layoutMap);
            }
        });
    }

	/**
	 * 缓存FuncCustomizedLayoutRegionRo
	 * @param systemId
	 * @return
	 */
	private List<FuncCustomizedLayoutRegionVo> cacheFuncCustomizedLayoutRegions(String systemId) {
		List<FuncCustomizedLayoutRegion> funcCustomizedLayoutRegions = null;
		if(StringUtils.isBlank(systemId)) {
			funcCustomizedLayoutRegions = funcCustomizedLayoutRegionSV.list();
		}else {
			funcCustomizedLayoutRegions = funcCustomizedLayoutRegionSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcCustomizedLayoutRegions)) {
			return new ArrayList<>();
		}

		List<FuncCustomizedLayoutRegionVo> funcCustomizedLayoutRegionVos = new ArrayList<>();
		for(FuncCustomizedLayoutRegion funcCustomizedLayoutRegion : funcCustomizedLayoutRegions) {
			funcCustomizedLayoutRegionVos.add(this.cloneFuncCustomizedLayoutRegion(funcCustomizedLayoutRegion));
		}
		return funcCustomizedLayoutRegionVos;
	}

	/**
	 * clone FuncCustomizedPage
	 * @param funcCustomizedPage
	 * @return
	 */
	private FuncCustomizedPageVo cloneFuncCustomizedPage(FuncCustomizedPage funcCustomizedPage) {
		FuncCustomizedPageVo vo = new FuncCustomizedPageVo();
		vo.setId(funcCustomizedPage.getId());
		vo.setFuncId(funcCustomizedPage.getFuncId());
		vo.setIsMainPage(funcCustomizedPage.getIsMainPage());
		vo.setName(funcCustomizedPage.getName());
		vo.setPageCfg(funcCustomizedPage.getPageCfg());
		return vo;
	}

	/**
	 * clone FuncCustomizedLayout
	 * @param funcCustomizedLayout
	 * @return
	 */
	private FuncCustomizedLayoutVo cloneFuncCustomizedLayout(FuncCustomizedLayout funcCustomizedLayout) {
		FuncCustomizedLayoutVo vo = new FuncCustomizedLayoutVo();
		vo.setId(funcCustomizedLayout.getId());
		vo.setComponentCode(funcCustomizedLayout.getComponentCode());
		vo.setComponentType(funcCustomizedLayout.getComponentType());
		vo.setDisplayOrder(funcCustomizedLayout.getDisplayOrder());
		vo.setFuncId(funcCustomizedLayout.getFuncId());
		vo.setLayoutCfg(funcCustomizedLayout.getLayoutCfg());
		vo.setName(funcCustomizedLayout.getName());
		vo.setPageId(funcCustomizedLayout.getPageId());
		vo.setParentLayoutId(funcCustomizedLayout.getParentLayoutId());
		return vo;
	}

	/**
	 * clone FuncCustomizedLayoutRegion
	 * @param funcCustomizedLayoutRegion
	 * @return
	 */
	private FuncCustomizedLayoutRegionVo cloneFuncCustomizedLayoutRegion(FuncCustomizedLayoutRegion funcCustomizedLayoutRegion) {
		FuncCustomizedLayoutRegionVo vo = new FuncCustomizedLayoutRegionVo();
		vo.setId(funcCustomizedLayoutRegion.getId());
		vo.setFuncId(funcCustomizedLayoutRegion.getFuncId());
		vo.setLayoutId(funcCustomizedLayoutRegion.getLayoutId());
		vo.setPageId(funcCustomizedLayoutRegion.getPageId());
		vo.setRegionId(funcCustomizedLayoutRegion.getRegionId());
		return vo;
	}

	/**
	 * 缓存功能区域信息
	 * @param systemId
	 * @return
	 */
	private List<FuncRegionVo> cacheFuncRegions(String systemId) {
    	//加载功能区域列表
    	List<FuncRegion> funcRegions = null;
		if(StringUtils.isBlank(systemId)) {
			funcRegions = funcRegionSV.list();
		}else {
			funcRegions = funcRegionSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcRegions)) {
			return null;
		}

    	//加载功能包含的所有功能区域操作列表信息
    	List<FuncRegionOperationVo> operations = this.cacheFuncRegionOperations(systemId);
    	Map<String, List<FuncRegionOperationVo>> operationMap = new HashMap<>();
    	if(!CollectionUtils.isEmpty(operations)) {
    		operationMap = operations.stream().collect(Collectors.groupingBy(FuncRegionOperationVo::getFuncRegionId));
    	}

    	//加载功能包含的所有功能区域绑定属性信息
    	List<FuncRegionAttrAssign> attrAssigns = null;
		if(StringUtils.isBlank(systemId)) {
			attrAssigns = funcRegionAttrAssignSV.list();
		}else {
			attrAssigns = funcRegionAttrAssignSV.listBySystem(systemId);
		}
		//FuncRegionAttrAssign->FuncRegionAttrAssignRo
		List<FuncRegionAttrAssignVo> funcRegionAttrAssignVos = new ArrayList<>();
    	Map<String, List<FuncRegionAttrAssignVo>> attrAssignMap = null;
    	if(!CollectionUtils.isEmpty(attrAssigns)) {
			for(FuncRegionAttrAssign funcRegionAttrAssign : attrAssigns) {
				funcRegionAttrAssignVos.add(this.cloneFuncRegionAttrAssign(funcRegionAttrAssign));
			}
    		attrAssignMap = funcRegionAttrAssignVos.stream().collect(Collectors.groupingBy(FuncRegionAttrAssignVo::getFuncRegionId));
    	}else {
			attrAssignMap = new HashMap<>();
		}

		//加载区域模板信息
		List<FuncTemplateRegion> tplRegions = funcTemplateRegionSV.list();
    	//tplRegionId-》FuncTemplateRegion
    	Map<String, FuncTemplateRegionVo> tplRegionMap = null;
    	if(tplRegions != null && !tplRegions.isEmpty()) {
    		tplRegionMap = tplRegions.stream().collect(Collectors.toMap(FuncTemplateRegion::getId, t->cloneFuncTemplateRegion(t)));
    	}else {
			tplRegionMap = new HashMap<>();
		}
    	
    	//将功能区域操作以及绑定属性列表信息补充到相应的功能区域中
		List<FuncRegionVo> funcRegionVos = new ArrayList<>();
		FuncRegionVo funcRegionVo = null;
    	for(FuncRegion funcRegion : funcRegions) {
			funcRegionVo = this.cloneFuncRegion(funcRegion);
			if(funcRegionVo != null) {
				funcRegionVo.setRegionOperations(operationMap.get(funcRegionVo.getId()));
				funcRegionVo.setRegionAttrAssigns(attrAssignMap.get(funcRegionVo.getId()));
				funcRegionVo.setTplRegion(tplRegionMap.get(funcRegionVo.getTplRegionId()));
			}
    		funcRegionVos.add(funcRegionVo);
    	}
    	return funcRegionVos;
    }

	/**
	 * 缓存功能区域操作信息
	 * @param systemId
	 * @return
	 */
	private List<FuncRegionOperationVo> cacheFuncRegionOperations(String systemId) {
		//加载功能区域操作
    	List<FuncRegionOperation> funcRegionOperations = null;
		if(StringUtils.isBlank(systemId)) {
			funcRegionOperations = funcRegionOperationSV.list();
		}else {
			funcRegionOperations = funcRegionOperationSV.listBySystem(systemId);
		}
		if(CollectionUtils.isEmpty(funcRegionOperations)) {
			return null;
		}

		List<FuncRegionOperationVo> funcRegionOperationVos = new ArrayList<>();
		FuncRegionOperationVo funcRegionOperationVo = null;
		//设置绑定服务信息
    	for(FuncRegionOperation funcRegionOperation: funcRegionOperations) {
			funcRegionOperationVo = this.cloneFuncRegionOperation(funcRegionOperation);
			funcRegionOperationVos.add(funcRegionOperationVo);
    		if(StringUtils.isBlank(funcRegionOperationVo.getServiceOperationId())) {
    			continue;
    		}
			if(frontController.getExposedService(funcRegionOperation.getSystemId(), funcRegionOperationVo.getExposedServiceId()) != null) {
				funcRegionOperationVo.setExposedServiceMapping(frontController.getExposedService(funcRegionOperation.getSystemId(), funcRegionOperationVo.getExposedServiceId()).getHttpMapping());
    		}
    		Operation operation = ServiceProxy.getProxy().getOperation(funcRegionOperation.getSystemId(), funcRegionOperationVo.getServiceOperationId());
			funcRegionOperationVo.setOperation(this.cloneOperation(operation));
		}
    	
    	return funcRegionOperationVos;
    }

	/**
	 * 缓存方法信息
	 * @param systemId
	 * @return
	 */
	public List<OperationVo> cacheOperations(String systemId) {
		List<OperationVo> operationVos = new ArrayList<>();
		if(StringUtils.isBlank(systemId)) {
            Map<String, Map<String, Operation>> operationMap = ServiceProxy.getProxy().getOperations();
			if(!MapUtils.isEmpty(operationMap)) {
				for(Map<String, Operation> value : operationMap.values()) {
					if(!MapUtils.isEmpty(value)) {
						operationVos.addAll(this.cloneOperations(value.values()));
					}
				}
			}
        }else {
			Map<String, Operation> operationMap = ServiceProxy.getProxy().getOperations(systemId);
			if(!MapUtils.isEmpty(operationMap)) {
				operationVos.addAll(this.cloneOperations(operationMap.values()));
			}
        }
		return operationVos;
	}

	private List<FuncTemplateVo> cloneFuncTemplates(List<FuncTemplate> funcTemplates) {
		if(CollectionUtils.isEmpty(funcTemplates)) {
			return null;
		}
		List<FuncTemplateVo> funcTemplateVos = new ArrayList<>();
		for(FuncTemplate funcTemplate: funcTemplates) {
			funcTemplateVos.add(this.clonFuncTemplate(funcTemplate));
		}
		return funcTemplateVos;
	}

	private FuncTemplateVo clonFuncTemplate(FuncTemplate funcTemplate) {
		if(funcTemplate == null) {
			return null;
		}
		FuncTemplateVo funcTemplateVo = new FuncTemplateVo();
		funcTemplateVo.setId(funcTemplate.getId());
		funcTemplateVo.setIsMainFunc(funcTemplate.getIsMainFunc());
		funcTemplateVo.setName(funcTemplate.getName());
		funcTemplateVo.setUrl(funcTemplate.getUrl());
		funcTemplateVo.setTemplateType(funcTemplate.getTemplateType());
		return funcTemplateVo;
	}

	private FuncTemplateRegionVo cloneFuncTemplateRegion(FuncTemplateRegion funcTemplateRegion) {
		if(funcTemplateRegion == null) {
			return null;
		}
		FuncTemplateRegionVo funcTemplateRegionVo = new FuncTemplateRegionVo();
		funcTemplateRegionVo.setId(funcTemplateRegion.getId());
		funcTemplateRegionVo.setName(funcTemplateRegion.getName());
		funcTemplateRegionVo.setRegionType(funcTemplateRegion.getRegionType());
		return funcTemplateRegionVo;
	}

	private FuncObjectAttrOptionsVo cloneFuncObjectAttrOptions(FuncObjectAttrOptions funcObjectAttrOptions) {
		if(funcObjectAttrOptions == null) {
			return null;
		}
		FuncObjectAttrOptionsVo funcObjectAttrOptionsVo = new FuncObjectAttrOptionsVo();
		funcObjectAttrOptionsVo.setId(funcObjectAttrOptions.getId());
		funcObjectAttrOptionsVo.setAttributeId(funcObjectAttrOptions.getAttributeId());
		funcObjectAttrOptionsVo.setDisplayHideCfg(funcObjectAttrOptions.getDisplayHideCfg());
		funcObjectAttrOptionsVo.setInteractionType(funcObjectAttrOptions.getInteractionType());
		funcObjectAttrOptionsVo.setObjectAssignId(funcObjectAttrOptions.getObjectAssignId());
		funcObjectAttrOptionsVo.setOptionCfgType(funcObjectAttrOptions.getOptionCfgType());
		funcObjectAttrOptionsVo.setOptionCfg(funcObjectAttrOptions.getOptionCfg());
		funcObjectAttrOptionsVo.setParentAttributeId(funcObjectAttrOptions.getParentAttributeId());
		funcObjectAttrOptionsVo.setParentObjectAssignId(funcObjectAttrOptions.getParentObjectAssignId());
		funcObjectAttrOptionsVo.setPopupCfg(funcObjectAttrOptions.getPopupCfg());
		funcObjectAttrOptionsVo.setQueryType(funcObjectAttrOptions.getQueryType());
		//funcObjectAttrOptionsVo.setSystemId(funcObjectAttrOptions.getSystemId());
		//funcObjectAttrOptionsVo.setTenantId(funcObjectAttrOptions.getTenantId());
		return funcObjectAttrOptionsVo;
	}

	private FuncObjectAssignVo cloneFuncObjectAssignRo(FuncObjectAssign objectAssign) {
		if(objectAssign == null) {
			return null;
		}
		FuncObjectAssignVo funcObjectAssignVo = new FuncObjectAssignVo();
		funcObjectAssignVo.setId(objectAssign.getId());
		funcObjectAssignVo.setAssignType(objectAssign.getAssignType());
		funcObjectAssignVo.setDisplayOrder(objectAssign.getDisplayOrder());
		funcObjectAssignVo.setFuncId(objectAssign.getFuncId());
		funcObjectAssignVo.setKeyAttrId(objectAssign.getKeyAttrId());
		funcObjectAssignVo.setMainRelAttrId(objectAssign.getMainRelAttrId());
		funcObjectAssignVo.setObjectId(objectAssign.getObjectId());
		funcObjectAssignVo.setObjectType(objectAssign.getObjectType());
		funcObjectAssignVo.setRelAttrId(objectAssign.getRelAttrId());
		//funcObjectAssignVo.setSystemId(objectAssign.getSystemId());
		//funcObjectAssignVo.setTenantId(objectAssign.getTenantId());
		return funcObjectAssignVo;
	}

	private FuncDefineVo cloneFuncDefine(FuncDefine funcDefine) {
		if(funcDefine == null) {
			return null;
		}
		FuncDefineVo funcDefineVo = new FuncDefineVo();
		funcDefineVo.setId(funcDefine.getId());
		//funcDefineVo.setDescription(funcDefine.getDescription());
		funcDefineVo.setName(funcDefine.getName());
		funcDefineVo.setSystemId(funcDefine.getSystemId());
		//funcDefineVo.setTemplateId(funcDefine.getTemplateId());
		funcDefineVo.setTenantId(funcDefine.getTenantId());
		return funcDefineVo;
	}

	private FuncRegionOperationVo cloneFuncRegionOperation(FuncRegionOperation funcRegionOperation) {
		if(funcRegionOperation == null) {
			return null;
		}
		FuncRegionOperationVo funcRegionOperationVo = new FuncRegionOperationVo();
		//funcRegionOperationVo.setId(funcRegionOperation.getId());
		funcRegionOperationVo.setDisplayOrder(funcRegionOperation.getDisplayOrder());
		funcRegionOperationVo.setDisplayType(funcRegionOperation.getDisplayType());
		funcRegionOperationVo.setExposedServiceId(funcRegionOperation.getExposedServiceId());
		// funcRegionOperationVo.setFuncId(funcRegionOperation.getFuncId());
		funcRegionOperationVo.setFuncRegionId(funcRegionOperation.getFuncRegionId());
		funcRegionOperationVo.setIsUserDefined(funcRegionOperation.getIsUserDefined());
		funcRegionOperationVo.setName(funcRegionOperation.getName());
		funcRegionOperationVo.setObjectAssignId(funcRegionOperation.getObjectAssignId());
		funcRegionOperationVo.setOperationCfg(funcRegionOperation.getOperationCfg());
		funcRegionOperationVo.setOperationType(funcRegionOperation.getOperationType());
		funcRegionOperationVo.setServiceObjectId(funcRegionOperation.getServiceObjectId());
		funcRegionOperationVo.setServiceOperationId(funcRegionOperation.getServiceOperationId());
		//funcRegionOperationVo.setSystemId(funcRegionOperation.getSystemId());
		funcRegionOperationVo.setTargetRegionId(funcRegionOperation.getTargetRegionId());
		//funcRegionOperationVo.setTenantId(funcRegionOperation.getTenantId());
		funcRegionOperationVo.setTplOperationId(funcRegionOperation.getTplOperationId());
		return funcRegionOperationVo;
	}

	private FuncRegionVo cloneFuncRegion(FuncRegion funcRegion) {
		if(funcRegion == null) {
			return null;
		}
		FuncRegionVo funcRegionVo = new FuncRegionVo();
		funcRegionVo.setId(funcRegion.getId());
		funcRegionVo.setFuncId(funcRegion.getFuncId());
		funcRegionVo.setName(funcRegion.getName());
		funcRegionVo.setRegionCfg(funcRegion.getRegionCfg());
		//funcRegionVo.setSystemId(funcRegion.getSystemId());
		//funcRegionVo.setTenantId(funcRegion.getTenantId());
		//funcRegionVo.setTplCompositeId(funcRegion.getTplCompositeId());
		funcRegionVo.setTplRegionId(funcRegion.getTplRegionId());
		return funcRegionVo;
    }

	private FuncRegionAttrAssignVo cloneFuncRegionAttrAssign(FuncRegionAttrAssign funcRegionAttrAssign) {
		if(funcRegionAttrAssign == null) {
			return null;
		}
		FuncRegionAttrAssignVo funcRegionAttrAssignVo = new FuncRegionAttrAssignVo();
		funcRegionAttrAssignVo.setId(funcRegionAttrAssign.getId());
		funcRegionAttrAssignVo.setAttributeId(funcRegionAttrAssign.getAttributeId());
		funcRegionAttrAssignVo.setColSpan(funcRegionAttrAssign.getColSpan());
		funcRegionAttrAssignVo.setDisplayCfg(funcRegionAttrAssign.getDisplayCfg());
		funcRegionAttrAssignVo.setDisplayName(funcRegionAttrAssign.getDisplayName());
		funcRegionAttrAssignVo.setDisplayOrder(funcRegionAttrAssign.getDisplayOrder());
		funcRegionAttrAssignVo.setDisplayType(funcRegionAttrAssign.getDisplayType());
		funcRegionAttrAssignVo.setDisplayWidth(funcRegionAttrAssign.getDisplayWidth());
		funcRegionAttrAssignVo.setFuncRegionId(funcRegionAttrAssign.getFuncRegionId());
		funcRegionAttrAssignVo.setObjectAssignId(funcRegionAttrAssign.getObjectAssignId());
		funcRegionAttrAssignVo.setObjectId(funcRegionAttrAssign.getObjectId());
		funcRegionAttrAssignVo.setObjectType(funcRegionAttrAssign.getObjectType());
		funcRegionAttrAssignVo.setReadonly(funcRegionAttrAssign.getReadonly());
		funcRegionAttrAssignVo.setRequired(funcRegionAttrAssign.getRequired());
		funcRegionAttrAssignVo.setRowSpan(funcRegionAttrAssign.getRowSpan());
		return funcRegionAttrAssignVo;
	}

	private List<OperationVo> cloneOperations(Collection<Operation> operations) {
		if(CollectionUtils.isEmpty(operations)) {
			return null;
		}
		List<OperationVo> operationVos = new ArrayList<>();
		for(Operation operation : operations) {
			operationVos.add(this.cloneOperation(operation));
		}
		return operationVos;
	}

	private OperationVo cloneOperation(Operation operation) {
		if(operation == null) {
			return null;
		}
		OperationVo operationCopy = new OperationVo();
		operationCopy.setId(operation.getId());
		operationCopy.setCode(operation.getCode());
		//operationCopy.setId(operation.getId());
		operationCopy.setName(operation.getName());
		//operationCopy.setObjectId(operation.getObjectId());
		//operationCopy.setObjectType(operation.getObjectType());
		operationCopy.setRuleGroupId(operation.getRuleGroupId());
		if(operation.getOutParam() != null) {
			operationCopy.setOutParam(this.cloneParameter(operation.getOutParam()));
		}
		if(!CollectionUtils.isEmpty(operation.getInParams())) {
			operationCopy.setInParams(this.cloneParameters(operation.getInParams()));
		}
		operationCopy.setSystemId(operation.getSystemId());
		return operationCopy;
	}

	private List<ParameterVo> cloneParameters(List<Parameter> params) {
		if(CollectionUtils.isEmpty(params)) {
			return null;
		}
		List<ParameterVo> targets = new ArrayList<>();
		for(Parameter param : params) {
			targets.add(this.cloneParameter(param));
		}
		return targets;
	}

	private ParameterVo cloneParameter(Parameter param) {
		ParameterVo target = new ParameterVo();
		target.setCode(param.getCode());
		//target.setDisplayOrder(param.getDisplayOrder());
		//target.setId(param.getId());
		target.setIsInParam(param.getIsInParam());
		target.setIsListParam(param.getIsListParam());
		target.setName(param.getName());
		target.setParamClass(param.getParamClass());
		target.setParamType(param.getParamType());
		if(!StringUtils.isBlank(target.getParamClass())) {
			AttributedObject originClassObject = null;
			if(ManagedObjectType.DOMAIN_OBJECT.equals(target.getParamType())) {
    			originClassObject = DomainObjectProxy.getInstance().getDomainObject(param.getSystemId(), target.getParamClass());
			}else if(ManagedObjectType.VALUE_OBJECT.equals(target.getParamType())) {
    			originClassObject = ValueObjectProxy.getInstance().getValueObject(param.getSystemId(), target.getParamClass());
			}
			if(originClassObject != null) {
				target.setParamClassObject(this.cloneAttributedObject(originClassObject));
			}
		}
		return target;
	}

	private AttributedObjectVo cloneAttributedObject(AttributedObject origin) {
		if(origin == null) {
			return null;
		}
		AttributedObjectVo paramClassObject = new AttributedObjectVo();
		paramClassObject.setAttributes(this.cloneAttributes(origin.getAttributes()));
		paramClassObject.setCode(origin.getCode());
		paramClassObject.setId(origin.getId());
		paramClassObject.setName(origin.getName());
		return paramClassObject;
	}

	private List<DataSetDetailVo> cloneDataSetDetails(List<DataSetDetail> dataSetDetails) {
		if(CollectionUtils.isEmpty(dataSetDetails)) {
			return null;
		}
		List<DataSetDetailVo> attributeVos = new ArrayList<>();
		for(DataSetDetail detail : dataSetDetails) {
			attributeVos.add(this.cloneDataSetDetail(detail));
		}
		return attributeVos;
	}

	private DataSetDetailVo cloneDataSetDetail(DataSetDetail dataSetDetail) {
		if(dataSetDetail == null) {
			return null;
		}
		DataSetDetailVo target = new DataSetDetailVo();
		target.setDetailContentId(dataSetDetail.getDetailContentId());
		target.setDetailType(dataSetDetail.getDetailType());
		target.setDetailContentAlias(dataSetDetail.getDetailContentAlias());
		target.setId(dataSetDetail.getId());
		if(dataSetDetail.getContent() != null) {
			JsonObject object = JsonUtils.toJsonObject(dataSetDetail.getContent());
			JsonObject content = new JsonObject();
			JsonUtils.set(content, "id", JsonUtils.getObject(object, "id"));
			JsonUtils.set(content, "code", JsonUtils.getObject(object, "code"));
			JsonUtils.set(content, "name", JsonUtils.getObject(object, "name"));
			if(DataSetDetail.DETAIL_TYPE_FILTER.equals(dataSetDetail.getDetailType())) {
				DataModelFilter dataModelFilter = (DataModelFilter) dataSetDetail.getContent();
				if(!CollectionUtils.isEmpty(dataModelFilter.getFilterValues())) {
					JsonArray filterValues = new JsonArray();
					for(DataModelFilterValue filterValue : dataModelFilter.getFilterValues()) {
						JsonObject value = new JsonObject();
						JsonUtils.set(value, "id", filterValue.getId());
						JsonUtils.set(value, "code", filterValue.getCode());
						JsonUtils.set(value, "name", filterValue.getName());
						filterValues.add(value);
					}
					JsonUtils.set(content, "filterValues", filterValues);
				}
			}
			target.setContent(content);
		}
		return target;
	}

	private List<AttributeVo> cloneAttributes(List<Attribute> attributes) {
		if(CollectionUtils.isEmpty(attributes)) {
			return null;
		}
		List<AttributeVo> attributeVos = new ArrayList<>();
		for(Attribute attribute : attributes) {
			attributeVos.add(this.cloneAttribute(attribute));
		}
		return attributeVos;
	}

	private AttributeVo cloneAttribute(Attribute attribute) {
		if(attribute == null) {
			return null;
		}
		AttributeVo target = new AttributeVo();
		target.setAttrClass(attribute.getAttrClass());
		target.setAttrType(attribute.getAttrType());
		target.setCode(attribute.getCode());
		target.setId(attribute.getId());
		target.setIsListAttr(attribute.getIsListAttr());
		target.setName(attribute.getName());
		return target;
	}
}
