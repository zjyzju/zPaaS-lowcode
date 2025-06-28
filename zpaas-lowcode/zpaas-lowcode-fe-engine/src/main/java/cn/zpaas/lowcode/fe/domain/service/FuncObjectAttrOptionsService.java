package cn.zpaas.lowcode.fe.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.AttrOptionCfgType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ExposedService;
import cn.zpaas.lowcode.domain.eo.FuncObjectAttrOptions;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.proxy.FuncDefineProxy;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;

/**
 * 功能绑定对象属性选项设置领域服务
 *
 * @author zjy
 * createTime 2025年04月05日-09:58:12
 */
@Service
public class FuncObjectAttrOptionsService {
    private static Logger logger = LoggerFactory.getLogger(FuncObjectAttrOptionsService.class);

    public static final String SQL_KEY = "sql";
	public static final String PARENT_KEY = "parent";
	public static final String CATAGORY_ATTR_KEY = "catagoryAttr";
	public static final String DATASOURCE_KEY = "datasource";
	public static final String EXPOSED_SERVICE_ID_KEY = "exposedServiceId";
	public static final String PARAMS_KEY = "params";
	public static final String TENANT_ID_KEY = "tenantId";

	public static final String VALUE_KEY = "value";
	public static final String LABEL_KEY = "label";
	public static final String OPTIONS_KEY = "options";

	public static final String OPTION_CFG_KEY = "optionCfg";
	public static final String OPTION_CFT_TYPE_KEY = "optionCfgType";
	public static final String PARENT_ATTRIBUTE_ID_KEY = "parentAttributeId";
	public static final String PARENT_OBJECT_ASSIGN_ID_KEY = "parentObjectAssignId";
	public static final String POPUP_CFG_KEY = "popupCfg";
	public static final String ID_KEY = "id";

    @Autowired
    private FuncObjectAttrOptions funcObjectAttrOptionsSV;

	@Autowired
	private ExposedService exposedServiceSV;
	
    /**
     * 加载绑定对象属性选项配置信息
     * @param objectAssignId
     * @param initFlag
     * @return
     * @throws EngineException
     */
    public List<FuncObjectAttrOptions> loadByObjectAssign(String objectAssignId, boolean initFlag) throws EngineException {
    	List<FuncObjectAttrOptions> list = funcObjectAttrOptionsSV.listAttrOptions(objectAssignId);
    	if(!CollectionUtils.isEmpty(list)) {
    		for(FuncObjectAttrOptions attrOption : list) {
    			if(!initFlag || StringUtils.isBlank(attrOption.getOptionCfgType()) || StringUtils.isBlank(attrOption.getOptionCfg())) {
					attrOption.setOptions(new JsonArray());
    				continue;
    			}
				//以下的逻辑已经废弃，实际已经走缓存
    			if(AttrOptionCfgType.FIX.equals(attrOption.getOptionCfgType())) {
    				attrOption.setOptions(JsonUtils.toJsonArray(attrOption.getOptionCfg()));
    			}else if(AttrOptionCfgType.SQL.equals(attrOption.getOptionCfgType())) {
    				if((attrOption.getParentAttributeId() == null || attrOption.getParentAttributeId().trim().length() == 0 ||
    						attrOption.getParentObjectAssignId() == null || attrOption.getParentObjectAssignId().trim().length() == 0) && 
    						(attrOption.getPopupCfg() == null || attrOption.getPopupCfg().trim().length() == 0)) {//没有配置联动关系
    					attrOption.setOptions(this.loadOptionsForObjectAttr(attrOption, null, null));//通过sql加载下拉列表信息
    				}else {
						attrOption.setOptions(new JsonArray());
					}
    			}else if(AttrOptionCfgType.DICT.equals(attrOption.getOptionCfgType())) {
    				//以下的逻辑已经废弃，实际已经走缓存
    			}else {
    				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid optionCfgType");
    			}
    		}
    	}
    	
    	return list;
    }

	/**
	 * 加载下拉列表
	 * @param attrOption
	 * @throws EngineException
	 */
	public void loadOptions(JsonObject attrOption, String systemId) throws EngineException {
		String optionCfgType = JsonUtils.getString(attrOption, OPTION_CFT_TYPE_KEY);
		String optionCfg = JsonUtils.getString(attrOption, OPTION_CFG_KEY);
		if(StringUtils.isBlank(optionCfgType) || StringUtils.isBlank(optionCfg)) {
			attrOption.add(OPTIONS_KEY, new JsonArray());
    		return;
    	}
		if(AttrOptionCfgType.FIX.equals(optionCfgType)) {
    		attrOption.add(OPTIONS_KEY, JsonUtils.toJsonArray(optionCfg));
    	}else if(AttrOptionCfgType.SQL.equals(optionCfgType)) {
    		if((StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_ATTRIBUTE_ID_KEY)) ||
					StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_OBJECT_ASSIGN_ID_KEY))) && 
					StringUtils.isBlank(JsonUtils.getString(attrOption, POPUP_CFG_KEY))) {//没有配置联动关系
				//通过SQL加载下拉列表
				attrOption.add(OPTIONS_KEY, this.loadOptionsForObjectAttr(JsonUtils.getString(attrOption, ID_KEY), null, null));
			}else {
				attrOption.add(OPTIONS_KEY, new JsonArray());
			}
    	}else if(AttrOptionCfgType.DICT.equals(optionCfgType)) {
			if((StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_ATTRIBUTE_ID_KEY)) ||
					StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_OBJECT_ASSIGN_ID_KEY))) && 
					StringUtils.isBlank(JsonUtils.getString(attrOption, POPUP_CFG_KEY))) {//没有配置联动关系
				JsonObject dictOption = JsonUtils.toJsonObject(optionCfg);
				String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
				String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
				if(StringUtils.isBlank(dictResourceId) || StringUtils.isBlank(dictCode)) {//配置不完整时
					attrOption.add(OPTIONS_KEY, new JsonArray());
					return;
				}
				String dictValue  = JsonUtils.getString(dictOption, DictCacheAbility.DICT_VALUE);
				String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
				String parentDictValue = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_VALUE);
				if (!StringUtils.isBlank(dictValue)) {//取某个字典值的信息
					JsonObject dictJsonObject = DictCacheAbility.getInstance().getDictValue(systemId, dictResourceId, dictCode, dictValue);
					JsonArray options = new JsonArray();
					if(!JsonUtils.isEmpty(dictJsonObject)) {
						JsonObject option = new JsonObject();
						option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
						options.add(option);
					}
					attrOption.add(OPTIONS_KEY, options);
				}else if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentDictValue)) {//取子字典值列表
					JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(systemId, dictResourceId, dictCode, parentDictCode, parentDictValue);
					JsonArray options = new JsonArray();
					if(!JsonUtils.isEmpty(dictJsonArray)) {
						for(int i=0; i<dictJsonArray.size(); i++) {
							JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
							JsonObject option = new JsonObject();
							option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
							option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
							options.add(option);
						}
					}
					attrOption.add(OPTIONS_KEY, options);
				}else {
					JsonArray dictJsonArray = DictCacheAbility.getInstance().getDictValues(systemId, dictResourceId, dictCode);
					JsonArray options = new JsonArray();
					if(!JsonUtils.isEmpty(dictJsonArray)) {
						for(int i=0; i<dictJsonArray.size(); i++) {
							JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
							JsonObject option = new JsonObject();
							option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
							option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
							options.add(option);
						}
					}
					attrOption.add(OPTIONS_KEY, options);
				}
			}else {
				attrOption.add(OPTIONS_KEY, new JsonArray());
			}
		}else if(AttrOptionCfgType.METH.equals(optionCfgType)) {//服务方法
			if((StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_ATTRIBUTE_ID_KEY)) ||
					StringUtils.isBlank(JsonUtils.getString(attrOption, PARENT_OBJECT_ASSIGN_ID_KEY))) && 
					StringUtils.isBlank(JsonUtils.getString(attrOption, POPUP_CFG_KEY))) {//没有配置联动关系
				JsonArray optionCfgArray = JsonUtils.toJsonArray(optionCfg);
				if(!JsonUtils.isEmpty(optionCfgArray)) {
					JsonObject exposedServiceCfg = null;
					JsonObject defaultexposedServiceCfg = null;
					String parentValue = null;
					for(int i=0; i<optionCfgArray.size(); i++) {
						parentValue = JsonUtils.getString(optionCfgArray.get(i).getAsJsonObject(), PARENT_KEY);
						if(parentValue == null || parentValue.trim().length() == 0) {//上级属性值为空的，可作为默认的配置值
							defaultexposedServiceCfg = optionCfgArray.get(i).getAsJsonObject();
						}
					}
					if(exposedServiceCfg == null && defaultexposedServiceCfg != null) {//如果未找到对应的配置，则使用默认配置
						exposedServiceCfg = defaultexposedServiceCfg;
					}
					if(exposedServiceCfg == null) {
						logger.error("exposedServiceCfg is not found!");
						return;
					}
					JsonArray params = JsonUtils.getJsonArray(exposedServiceCfg, PARAMS_KEY);
					if(!JsonUtils.isEmpty(params)) {
						return;
					}
					String exposedServiceId = JsonUtils.getString(exposedServiceCfg, EXPOSED_SERVICE_ID_KEY);
	
					ExposedService exposedService = this.exposedServiceSV.byId(exposedServiceId);
					if(exposedService == null) {
						logger.error("invalid exposedServiceId: {}", exposedServiceId);
						return;
					}
	
					//构建服务调用命令对象
					ServiceCommand serviceCommand = new ServiceCommand();
					serviceCommand.setSystemId(systemId);
					serviceCommand.setTenantId(JsonUtils.getString(attrOption, TENANT_ID_KEY));
					serviceCommand.setServiceId(exposedService.getServiceId());
					serviceCommand.setOperationId(exposedService.getOperationId());
					JsonObject reqData = new JsonObject();
					reqData.add(PARAMS_KEY, null);
					serviceCommand.setReqData(reqData);
					serviceCommand.setMultipartFileMap(null);
					
					//调用ApplicationSerivceProxy的execute方法，获得返回结果
					ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
					Object returnData = null;
					try {
						returnData = applicationServiceProxy.execute(serviceCommand, null);
					} catch (EngineException e) {
						logger.error("invoke exposed service failed!", e);
						return ;
					}
					if(returnData != null && !(returnData instanceof JsonArray)) {
						logger.error("invalid returnData type: {}!", returnData.getClass().getName());
						return ;
					}
					attrOption.add(OPTIONS_KEY, (JsonArray)returnData);
				}
			}else {
				attrOption.add(OPTIONS_KEY, new JsonArray());
			}
			
		}else {
    		throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid optionCfgType");
    	}
    }
    
    /**
     * 根据上级属性值以及属性值，查询对应的label信息
     * @param attrOptionsId
     * @param attrValue
     * @param parentAttrValue
     * @return
     */
    public JsonArray loadOptionsForObjectAttr(String attrOptionsId, String parentAttrValue, JsonArray params) {
    	FuncObjectAttrOptions attrOptions = FuncDefineProxy.getInstance().getAttrOptions(attrOptionsId);
		return loadOptionsForObjectAttr(attrOptions, parentAttrValue, params);
	}

	/**
     * 加载下拉列表信息
     * @param attrOptions
     * @param parentAttrValue
     * @return
     */
    public JsonArray loadOptionsForObjectAttr(FuncObjectAttrOptions attrOptions, String parentAttrValue, JsonArray params) {
    	if(attrOptions == null) {
			logger.error("invalid attrOptions!");
			return null;
		}
		if(StringUtils.isBlank(attrOptions.getOptionCfg())) {
			logger.error("optionCfg is null!");
			return null;
		}

		if(AttrOptionCfgType.SQL.equals(attrOptions.getOptionCfgType())) {//sql类型
    		/*如下配置
			[
				{
					"sql": "select service_attr_id value, service_attr_name label from pcc_service_attr where status = ?",
					"parent": "S",
					"datasource": "",
					"params": [
						{
							"valueType": "F",
							"value": "1"
						}
					]
				},
				{
					"sql": "select material_attr_id value,material_attr_name label from pcc_material_attr where status = ?",
					"parent": "M",
					"datasource": "",
					"params": [
						{
							"valueType": "F",
							"value": "1"
						}
					]
				}
			]
			*/
			JsonArray optionCfgArray = JsonUtils.toJsonArray(attrOptions.getOptionCfg());
			
			if(!JsonUtils.isEmpty(optionCfgArray)) {
				JsonObject sqlCfg = null;
				JsonObject defaultSqlCfg = null;
				String parentValue = null;
				for(int i=0; i<optionCfgArray.size(); i++) {
					parentValue = JsonUtils.getString(optionCfgArray.get(i).getAsJsonObject(), PARENT_KEY);
					if(parentAttrValue != null && parentAttrValue.equals(parentValue)) {//获取对应上级属性值的配置
						sqlCfg = optionCfgArray.get(i).getAsJsonObject();
						break;
					}else if(parentValue == null || parentValue.trim().length() == 0) {//上级属性值为空的，可作为默认的配置值
						defaultSqlCfg = optionCfgArray.get(i).getAsJsonObject();
					}
				}
				if(sqlCfg == null && defaultSqlCfg != null) {//如果未找到对应的配置，则使用默认配置
					sqlCfg = defaultSqlCfg;
				}
				if(sqlCfg == null) {
					logger.error("sql is not found!");
					return null;
				}
				String querySql = JsonUtils.getString(sqlCfg, SQL_KEY);
				String datasource = JsonUtils.getString(sqlCfg, DATASOURCE_KEY);
				
				List<Object> valueList = new ArrayList<>();
				//加入额外参数
				if(!JsonUtils.isEmpty(params)) {
					params.forEach(paramValue -> valueList.add(JsonUtils.toObject(paramValue)));
				}
				return ORMRepositoryAbility.getInstance().executeQuerySql(querySql, valueList, attrOptions.getSystemId(), datasource, attrOptions.getTenantId());
			}
    	}else if(AttrOptionCfgType.DICT.equals(attrOptions.getOptionCfgType())) {//字典类型
			/*
			{
				"dictResourceId":"", 
				"dictCode":"",
				"dictValue":"",
				"parentDictCode":"",
				"parentDictValue":""
			}
			 */
			JsonObject dictOption = JsonUtils.toJsonObject(attrOptions.getOptionCfg());
			String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
			String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
			if(StringUtils.isBlank(dictResourceId) || StringUtils.isBlank(dictCode)) {//配置不完整时
				return null;
			}
			String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
			if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentAttrValue)) {//取子字典值列表
				JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(attrOptions.getSystemId(), dictResourceId, dictCode, parentDictCode, parentAttrValue);
				JsonArray options = new JsonArray();
				if(!JsonUtils.isEmpty(dictJsonArray)) {
					for(int i=0; i<dictJsonArray.size(); i++) {
						JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
						JsonObject option = new JsonObject();
						option.addProperty(VALUE_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						option.addProperty(LABEL_KEY, JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL));
						options.add(option);
					}
				}
				return options;
			}
		}else if(AttrOptionCfgType.METH.equals(attrOptions.getOptionCfgType())) {//服务方法
			/*
			[
				{
					"exposedServiceId": "",
					"parent": "",
					"catagoryAttr": {
						"valueType": "",
						"value": ""
					},
					"params": [
						{
							"valueType": "",
							"value": ""
						}
					]
				}
			]
			 */
			JsonArray optionCfgArray = JsonUtils.toJsonArray(attrOptions.getOptionCfg());
			
			if(!JsonUtils.isEmpty(optionCfgArray)) {
				JsonObject exposedServiceCfg = null;
				JsonObject defaultexposedServiceCfg = null;
				String parentValue = null;
				for(int i=0; i<optionCfgArray.size(); i++) {
					parentValue = JsonUtils.getString(optionCfgArray.get(i).getAsJsonObject(), PARENT_KEY);
					if(parentAttrValue != null && parentAttrValue.equals(parentValue)) {//获取对应上级属性值的配置
						exposedServiceCfg = optionCfgArray.get(i).getAsJsonObject();
						break;
					}else if(parentValue == null || parentValue.trim().length() == 0) {//上级属性值为空的，可作为默认的配置值
						defaultexposedServiceCfg = optionCfgArray.get(i).getAsJsonObject();
					}
				}
				if(exposedServiceCfg == null && defaultexposedServiceCfg != null) {//如果未找到对应的配置，则使用默认配置
					exposedServiceCfg = defaultexposedServiceCfg;
				}
				if(exposedServiceCfg == null) {
					logger.error("exposedServiceCfg is not found!");
					return null;
				}
				String exposedServiceId = JsonUtils.getString(exposedServiceCfg, EXPOSED_SERVICE_ID_KEY);

				ExposedService exposedService = this.exposedServiceSV.byId(exposedServiceId);
				if(exposedService == null) {
					logger.error("invalid exposedServiceId: {}", exposedServiceId);
					return null;
				}

				//构建服务调用命令对象
				ServiceCommand serviceCommand = new ServiceCommand();
				serviceCommand.setSystemId(attrOptions.getSystemId());
				serviceCommand.setTenantId(attrOptions.getTenantId());
				serviceCommand.setServiceId(exposedService.getServiceId());
				serviceCommand.setOperationId(exposedService.getOperationId());
				JsonObject reqData = new JsonObject();
				reqData.add(PARAMS_KEY, params);
				serviceCommand.setReqData(reqData);
				serviceCommand.setMultipartFileMap(null);
				
				//调用ApplicationSerivceProxy的execute方法，获得返回结果
				ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
				Object returnData = null;
				try {
					returnData = applicationServiceProxy.execute(serviceCommand, null);
				} catch (EngineException e) {
					logger.error("invoke exposed service failed!", e);
					return null;
				}
				if(returnData != null && !(returnData instanceof JsonArray)) {
					logger.error("invalid returnData type: {}!", returnData.getClass().getName());
					return null;
				}
				
				return (JsonArray)returnData;
			}
		}
		return null;
	}
    
    /**
     * 根据上级属性值以及属性值，查询对应的label信息
     * @param attrOptionsId
     * @param attrValue
     * @param parentAttrValue
     * @return
     */
    public String loadLabelForObjectAttr(String attrOptionsId, String attrValue, String parentAttrValue, JsonArray params) {
		if(StringUtils.isBlank(attrValue)) {
			return null;
		}
		FuncObjectAttrOptions attrOptions = FuncDefineProxy.getInstance().getAttrOptions(attrOptionsId);
		if(attrOptions == null) {
			logger.error("invalid attrOptionsId!");
			return null;
		}
		if(StringUtils.isBlank(attrOptions.getOptionCfg())) {
			logger.error("optionCfg is null!");
			return null;
		}

		if(AttrOptionCfgType.SQL.equals(attrOptions.getOptionCfgType())) {//sql类型
			/*如下配置
			[
				{
					"sql": "select service_name from pcc_service where service_id = ?",
					"parent": "S",
					"datasource": ""
				},
				{
					"sql": "select material_name from pcc_material where material_id = ?",
					"parent": "M",
					"datasource": ""
				}
			]
			*/
			JsonArray optionCfgArray = JsonUtils.toJsonArray(attrOptions.getOptionCfg());
			
			if(optionCfgArray != null && !optionCfgArray.isEmpty()) {
				JsonObject sqlCfg = null;
				JsonObject defaultSqlCfg = null;
				String parentValue = null;
				for(int i=0; i<optionCfgArray.size(); i++) {
					parentValue = JsonUtils.getString(optionCfgArray.get(i).getAsJsonObject(), PARENT_KEY);
					if(parentAttrValue != null && parentAttrValue.equals(parentValue)) {//获取对应上级属性值的配置
						sqlCfg = optionCfgArray.get(i).getAsJsonObject();
						break;
					}else if(parentValue == null || parentValue.trim().length() == 0) {//上级属性值为空的，可作为默认的配置值
						defaultSqlCfg = optionCfgArray.get(i).getAsJsonObject();
					}
				}
				if(sqlCfg == null && defaultSqlCfg != null) {//如果未找到对应的配置，则使用默认配置
					sqlCfg = defaultSqlCfg;
				}
				if(sqlCfg == null) {
					logger.error("sql is not found!");
					return null;
				}
				String querySql = JsonUtils.getString(sqlCfg, SQL_KEY);
				String datasource = JsonUtils.getString(sqlCfg, DATASOURCE_KEY);
				
				List<Object> valueList = new ArrayList<>();
				//加入当前属性值
				if(attrValue != null && attrValue.trim().length() > 0) {
					valueList.add(attrValue);
				}
				//加入额外参数
				if(params != null && params.size() > 0) {
					params.forEach((param) -> {
						valueList.add(param);
					});
				}
				JsonArray resultArray = ORMRepositoryAbility.getInstance().executeQuerySql(querySql, valueList, attrOptions.getSystemId(), datasource, attrOptions.getTenantId());
				if(!JsonUtils.isEmpty(resultArray)) {
					JsonObject rowObject = resultArray.get(0).getAsJsonObject();
					String label = JsonUtils.getString(rowObject, rowObject.keySet().iterator().next());
					return label;
				}
			}
		}else if(AttrOptionCfgType.DICT.equals(attrOptions.getOptionCfgType())) {//字典类型
			JsonObject dictOption = JsonUtils.toJsonObject(attrOptions.getOptionCfg());
			String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
			String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
			if(StringUtils.isBlank(dictResourceId) || StringUtils.isBlank(dictCode)) {//配置不完整时
				return null;
			}
			String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
			if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentAttrValue)) {//取子字典值列表
				JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(attrOptions.getSystemId(), dictResourceId, dictCode, parentDictCode, parentAttrValue);
				if(!JsonUtils.isEmpty(dictJsonArray)) {
					for(int i=0; i<dictJsonArray.size(); i++) {
						JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
						if(attrValue.equals(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE))) {
							return JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE_LABEL);
						}
					}
				}
			}
		}
		
		return null;
	}
}
