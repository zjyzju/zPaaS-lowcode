package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.utils.UUID;
import cn.zpaas.lowcode.constant.RegionTypes;
import cn.zpaas.lowcode.constant.ReportRegionAttrConst;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.domain.mapper.FuncRegionMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class FuncRegion {
	private static Logger logger = LoggerFactory.getLogger(FuncRegion.class);

	public static final String CHART_COMPONENT_KEY = "chartComponent";//图表类型key
	
	
    private String id;

    private String name;

    private String funcId;

    private String tplCompositeId;

    private String tplRegionId;

	private String regionCfg;

	private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private List<FuncRegionOperation> regionOperations;
    
    private List<FuncRegionAttrAssign> regionAttrAssigns;
    
    private FuncTemplateRegion tplRegion;
    
    @Autowired
    private FuncRegionMapper mapper;
    
    @Autowired
    private FuncRegionOperation funcRegionOperationSV;
    
    @Autowired
    private FuncRegionAttrAssign funcRegionAttrAssignSV;
    
    @Autowired
    private FuncTemplateRegion funcTemplateRegionSV;
    
    public void generateFuncRegionAttrAssigns(FuncRegion funcRegion, FuncObjectAssign mainObjectAssign, List<FuncObjectAssign> subObjectAssigns)throws EngineException {
    	if(mainObjectAssign.getAttributes() == null || mainObjectAssign.getAttributes().isEmpty()) {
    		logger.info("objectAssign's attribute list is null , ignore!");
    		return;
    	}
    	int i=1;
    	FuncRegionAttrAssign funcRegionAttrAssign = null;
    	if(funcRegion.getTplRegion().getRegionType() != null && 
    			funcRegion.getTplRegion().getRegionType().startsWith(RegionTypes.REGION_TYPE_M)) {//只有主区域才生成主对象对应的区域绑定属性
    		//生成主对象属性对应的区域绑定属性
    		
    		for(Attribute attribute : mainObjectAssign.getAttributes()) {
        		funcRegionAttrAssign = new FuncRegionAttrAssign();
        		funcRegionAttrAssign.setFuncId(mainObjectAssign.getFuncId());
        		funcRegionAttrAssign.setFuncRegionId(funcRegion.getId());
        		funcRegionAttrAssign.setObjectAssignId(mainObjectAssign.getId());
        		funcRegionAttrAssign.setObjectType(mainObjectAssign.getObjectType());
        		funcRegionAttrAssign.setObjectId(mainObjectAssign.getObjectId());
        		funcRegionAttrAssign.setAttributeId(attribute.getId());
        		funcRegionAttrAssign.setColSpan(1);
        		funcRegionAttrAssign.setDisplayName(attribute.getName());
        		funcRegionAttrAssign.setDisplayOrder(i++);
        		funcRegionAttrAssign.setDisplayType(FuncRegionAttrAssign.DISPLAY_TYPE_I);
        		funcRegionAttrAssign.setDisplayWidth(null);
        		funcRegionAttrAssign.setReadonly(YesOrNo.NO);
        		funcRegionAttrAssign.setRowSpan(1);
        		funcRegionAttrAssign.setSystemId(funcRegion.getSystemId());
        		funcRegionAttrAssign.setTenantId(funcRegion.getTenantId());
        		if(funcRegionAttrAssignSV.create(funcRegionAttrAssign) == null) {
        			logger.error("generate funcRegionAttrAssign failed!");
        			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate funcRegionAttrAssign failed!");
        		}
        	}
    	}
    	
    	if(funcRegion.getTplRegion().getRegionType() != null && subObjectAssigns != null && !subObjectAssigns.isEmpty() &&
    			!funcRegion.getTplRegion().getRegionType().equals(RegionTypes.REGION_TYPE_MQ) &&
    			!funcRegion.getTplRegion().getRegionType().equals(RegionTypes.REGION_TYPE_MR)) {//从区域 以及 非主查询及主结果区域的其他区域，需要生成从对象的区域绑定属性
    		//生成从对象属性对应的区域绑定属性
    		for(FuncObjectAssign subObjectAssign : subObjectAssigns) {
    			boolean isList = false;
    			if(subObjectAssign.getMainRelAttrId() != null && mainObjectAssign.getAttributes() != null && 
    					!funcRegion.getTplRegion().getRegionType().startsWith(RegionTypes.REGION_TYPE_M)) {//非主区域时，才需要判断是否一对多的关系
    				for(Attribute attribute : mainObjectAssign.getAttributes()) {
    					if(subObjectAssign.getMainRelAttrId().equals(attribute.getId())) {
    						if(YesOrNo.YES.equals(attribute.getIsListAttr())) {
    							isList = true;
    						}
    						break;
    					}
    				}
    			}
    			if(funcRegion.getTplRegion().getRegionType().startsWith(RegionTypes.REGION_TYPE_M) || isList) {//主区域或一对多的从对象的从区域
    				i=i+10;
    	    		for(Attribute attribute : subObjectAssign.getAttributes()) {
    	        		funcRegionAttrAssign = new FuncRegionAttrAssign();
    	        		funcRegionAttrAssign.setFuncId(subObjectAssign.getFuncId());
    	        		funcRegionAttrAssign.setFuncRegionId(funcRegion.getId());
    	        		funcRegionAttrAssign.setObjectAssignId(subObjectAssign.getId());
    	        		funcRegionAttrAssign.setObjectType(subObjectAssign.getObjectType());
    	        		funcRegionAttrAssign.setObjectId(subObjectAssign.getObjectId());
    	        		funcRegionAttrAssign.setAttributeId(attribute.getId());
    	        		funcRegionAttrAssign.setColSpan(1);
    	        		funcRegionAttrAssign.setDisplayName(attribute.getName());
    	        		funcRegionAttrAssign.setDisplayOrder(i++);
    	        		funcRegionAttrAssign.setDisplayType(FuncRegionAttrAssign.DISPLAY_TYPE_I);
    	        		funcRegionAttrAssign.setDisplayWidth(null);
    	        		funcRegionAttrAssign.setReadonly(YesOrNo.NO);
    	        		funcRegionAttrAssign.setRowSpan(1);
    	        		funcRegionAttrAssign.setSystemId(funcRegion.getSystemId());
    	        		funcRegionAttrAssign.setTenantId(funcRegion.getTenantId());
    	        		if(funcRegionAttrAssignSV.create(funcRegionAttrAssign) == null) {
    	        			logger.error("generate funcRegionAttrAssign failed!");
    	        			throw new EngineException(ResultStatus.BUSINESS_ERROR, "generate funcRegionAttrAssign failed!");
    	        		}
    	        	}
    			}
    		}
    	}
    }

    public FuncRegion byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public FuncRegion create(FuncRegion funcRegion) {
    	if(funcRegion.getId() == null || funcRegion.getId().trim().length() == 0) {
    		funcRegion.setId(KeyGenerate.uuidKey());
    	}
    	Date nowDate = new Date();
    	funcRegion.setUpdateTime(nowDate);
    	funcRegion.setCreateTime(nowDate);
    	
    	if(mapper.insert(funcRegion) > 0) {
    		return funcRegion;
    	}else {
    		return null;
    	}
    }
    
    public FuncRegion save(FuncRegion funcRegion) {
    	funcRegion.setUpdateTime(new Date());
    	if(mapper.updateByPrimaryKey(funcRegion) > 0) {
    		return funcRegion;
    	}else {
    		return null;
    	}
    }

	public FuncRegion saveRegionCfg(FuncRegion funcRegion) {
		FuncRegion region = this.byId(funcRegion.getId());
		if(region == null) {
			return null;
		}
    	region.setUpdateTime(new Date());
		region.setRegionCfg(funcRegion.regionCfg);
    	if(mapper.updateByPrimaryKey(region) > 0) {
    		return funcRegion;
    	}else {
    		return null;
    	}
    }

	public int saveFuncRegion(FuncRegion funcRegion)throws EngineException {
		Date nowDate = new Date();
		if(StringUtils.isBlank(funcRegion.getId())) {
			funcRegion.setId(UUID.uuidKey());
			funcRegion.setCreateTime(nowDate);
		}
		funcRegion.setUpdateTime(nowDate);
		if(this.mapper.insert(funcRegion) <= 0) {
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "insert funcRegion failed!");
		}
		if(!CollectionUtils.isEmpty(funcRegion.getRegionAttrAssigns())) {
			//当处理自定义功能模板的报表容器的报表属性时，需要建立上下级的关系。顺序需要前端去保障；前端临时使用displayOrder建立的上下级关系需要替换成实际的id
			Map<String, String> parentAssignId2IdMap = new HashMap<>();//用于获取实际的上级id
			for(FuncRegionAttrAssign attrAssign : funcRegion.getRegionAttrAssigns()) {
				attrAssign.setFuncRegionId(funcRegion.getId());
				attrAssign.setFuncId(funcRegion.getFuncId());
				String parentAssignId = null;
				if(!StringUtils.isBlank(attrAssign.getDisplayCfg())) {
					JsonObject displayCfg = JsonUtils.toJsonObject(attrAssign.getDisplayCfg());
					parentAssignId = JsonUtils.getString(displayCfg, ReportRegionAttrConst.PARENT_ASSIGN_ID_KEY);
					//只有报表属性且存在上级id的时候，才需要处理
					if(!StringUtils.isBlank(parentAssignId) && ReportRegionAttrConst.SUB_REGION_TYPE_R.equals(JsonUtils.getString(displayCfg, ReportRegionAttrConst.SUB_REGION_TYPE_KEY))) {
						displayCfg.addProperty(ReportRegionAttrConst.PARENT_ASSIGN_ID_KEY, parentAssignId2IdMap.get(parentAssignId));//找到并使用实际的id
						attrAssign.setDisplayCfg(JsonUtils.toJson(displayCfg));
					}
				}
				this.funcRegionAttrAssignSV.saveFuncRegionAttrAssign(attrAssign);
				parentAssignId2IdMap.put(String.valueOf(attrAssign.getDisplayOrder()), attrAssign.getId());//前端临时使用displayOrder建立的上下级关系
			}
		}
		if(!CollectionUtils.isEmpty(funcRegion.getRegionOperations())) {
			for(FuncRegionOperation operation : funcRegion.getRegionOperations()) {
				operation.setFuncRegionId(funcRegion.getId());
				operation.setFuncId(funcRegion.getFuncId());
				this.funcRegionOperationSV.saveFuncRegionOperation(operation);
			}
		}
		return 1;
	}
    
    public FuncRegion loadAllById(String id) {
    	FuncRegion funcRegion = this.byId(id);
    	if(funcRegion != null) {
    		funcRegion.setRegionOperations(funcRegionOperationSV.loadByFuncRegion(id));
    		funcRegion.setRegionAttrAssigns(funcRegionAttrAssignSV.loadByFuncRegion(id));
    		funcRegion.setTplRegion(funcTemplateRegionSV.byId(funcRegion.getTplRegionId()));
    	}
    	
    	return funcRegion;
    }
    
    /**
     * 根据功能定义标识，加载该功能包含的区域列表信息
     * @param funcId
     * @return
     */
    public List<FuncRegion> loadAllByFuncId(String funcId) {
    	//加载功能区域列表
    	FuncRegionExample criteria = new FuncRegionExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId);
    	List<FuncRegion> funcRegions = mapper.selectByExample(criteria);
    	if(funcRegions != null && !funcRegions.isEmpty()) {
    		//加载功能包含的所有功能区域操作列表信息
    		List<FuncRegionOperation> operations = funcRegionOperationSV.loadByFunc(funcId);
    		Map<String, List<FuncRegionOperation>> operationMap = new HashMap<>();
    		if(operations != null && !operations.isEmpty()) {
    			operationMap = operations.stream().collect(Collectors.groupingBy(FuncRegionOperation::getFuncRegionId));
    		}
    		//加载功能包含的所有功能区域绑定属性信息
    		List<FuncRegionAttrAssign> attrAssigns = funcRegionAttrAssignSV.loadByFunc(funcId);
    		Map<String, List<FuncRegionAttrAssign>> attrAssignMap = new HashMap<>();
    		if(attrAssigns != null && !attrAssigns.isEmpty()) {
    			attrAssignMap = attrAssigns.stream().collect(Collectors.groupingBy(FuncRegionAttrAssign::getFuncRegionId));
    		}
    		
    		//将功能区域操作以及绑定属性列表信息补充到相应的功能区域中
    		for(FuncRegion funcRegion : funcRegions) {
    			funcRegion.setRegionOperations(operationMap.get(funcRegion.getId()));
    			funcRegion.setRegionAttrAssigns(attrAssignMap.get(funcRegion.getId()));
    		}
    	}
    	
    	return funcRegions;
    }
    
    /**
     * 加载功能区域信息
     * @param systemId
     * @param funcId
     * @return
     */
    public List<FuncRegion> listByFuncId(String systemId, String funcId) {
    	//加载功能区域列表
    	FuncRegionExample criteria = new FuncRegionExample();
    	criteria.createCriteria().andFuncIdEqualTo(funcId).andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }

	public List<FuncRegion> listBySystem(String systemId) {
    	//加载功能区域列表
    	FuncRegionExample criteria = new FuncRegionExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	return mapper.selectByExample(criteria);
    }

	public List<FuncRegion> list() {
    	//加载功能区域列表
    	return mapper.selectByExample(null);
    }
    
    public List<FuncRegion> loadAllBySystem(String systemId) {
    	FuncRegionExample criteria = new FuncRegionExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	List<FuncRegion> funcRegions = mapper.selectByExample(criteria);
    	if(funcRegions != null && !funcRegions.isEmpty()) {
    		List<FuncRegionOperation> operations = funcRegionOperationSV.loadByFunc(funcId);
    		Map<String, List<FuncRegionOperation>> operationMap = new HashMap<>();
    		if(operations != null && !operations.isEmpty()) {
    			operationMap = operations.stream().collect(Collectors.groupingBy(FuncRegionOperation::getFuncRegionId));
    		}
    		
    		List<FuncRegionAttrAssign> attrAssigns = funcRegionAttrAssignSV.loadByFunc(funcId);
    		Map<String, List<FuncRegionAttrAssign>> attrAssignMap = new HashMap<>();
    		if(attrAssigns != null && !attrAssigns.isEmpty()) {
    			attrAssignMap = attrAssigns.stream().collect(Collectors.groupingBy(FuncRegionAttrAssign::getFuncRegionId));
    		}
    		
    		for(FuncRegion funcRegion : funcRegions) {
    			funcRegion.setRegionOperations(operationMap.get(funcRegion.getId()));
    			funcRegion.setRegionAttrAssigns(attrAssignMap.get(funcRegion.getId()));
    		}
    	}
    	
    	return funcRegions;
    }

	/**
	 * 删除功能区域以及区域的绑定属性和区域操作信息
	 * @param funcRegionId
	 * @return
	 */
	public int deleteAllById(String funcId, String regionId) {
		this.funcRegionAttrAssignSV.delete(funcId, regionId);
		this.funcRegionOperationSV.delete(funcId, regionId);
		return this.mapper.deleteByPrimaryKey(regionId);
	}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? null : systemId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public List<FuncRegionOperation> getRegionOperations() {
		return regionOperations;
	}

	public void setRegionOperations(List<FuncRegionOperation> regionOperations) {
		this.regionOperations = regionOperations;
	}

	public List<FuncRegionAttrAssign> getRegionAttrAssigns() {
		return regionAttrAssigns;
	}

	public void setRegionAttrAssigns(List<FuncRegionAttrAssign> regionAttrAssigns) {
		this.regionAttrAssigns = regionAttrAssigns;
	}

	public FuncTemplateRegion getTplRegion() {
		return tplRegion;
	}

	public void setTplRegion(FuncTemplateRegion tplRegion) {
		this.tplRegion = tplRegion;
	}
	
	public String getTplCompositeId() {
		return tplCompositeId;
	}

	public void setTplCompositeId(String tplCompositeId) {
		this.tplCompositeId = tplCompositeId;
	}

	public String getTplRegionId() {
		return tplRegionId;
	}

	public void setTplRegionId(String tplRegionId) {
		this.tplRegionId = tplRegionId;
	}

	public String getRegionCfg() {
		return regionCfg;
	}

	public void setRegionCfg(String regionCfg) {
		this.regionCfg = regionCfg;
	}
}