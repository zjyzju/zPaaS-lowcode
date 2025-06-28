package cn.zpaas.lowcode.fe.domain.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.domain.eo.ValidateRuleGroup;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.fe.vo.ValidateRuleGroupVo;
import cn.zpaas.lowcode.fe.vo.ValidateRuleVo;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.ability.ValidateAbility;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;

/**
 * 校验规则组领域服务
 *
 * @author zjy
 * createTime 2025年04月27日-11:24:53
 */
@Service
public class ValidateRuleGroupService {
	public static final String RULE_OPTION_VALUES_KEY = "optionValues";//静态常量，配置optionValues的Key
	public static final String RULE_OPTION_DICT_NAME_KEY = "optionDictName";//静态常量，配置optionDictName的Key
	public static final String RULE_OPTION_SQL_KEY = "optionSql";//静态常量，配置optionSql的Key
	public static final String RULE_OPTION_DB_KEY = "optionDB";//静态常量，配置optionDB的Key
	
    /**
     * 为前端提供加载校验规则组信息
     * @param id
     * @return
     * @throws EngineException
     */
    public ValidateRuleGroupVo loadValidateRuleGroupForFront(String systemId, String id)throws EngineException {
		ValidateRuleGroupVo result = null;
    	ValidateRuleGroup validateRuleGroup = ValidateAbility.getInstance().getValidateRuleGroup(systemId, id);
		
		if(validateRuleGroup != null) {
			result = new ValidateRuleGroupVo();
			result.setId(validateRuleGroup.getId());
			result.setName(validateRuleGroup.getName());
			result.setObjectId(validateRuleGroup.getObjectId());
			result.setObjectType(validateRuleGroup.getObjectType());
			result.setSubRuleGroups(validateRuleGroup.getSubRuleGroups());
			List<ValidateRuleVo> validateRuleList = null;
    		if(validateRuleGroup.getValidateRules() != null && !validateRuleGroup.getValidateRules().isEmpty()) {
				validateRuleList = new ArrayList<>();
    			for(ValidateRule rule : validateRuleGroup.getValidateRules()) {
					ValidateRuleVo copyValidateRule = new ValidateRuleVo();
					copyValidateRule.setId(rule.getId());
					copyValidateRule.setAttributeCode(rule.getAttributeCode());
					copyValidateRule.setAttributeId(rule.getAttributeId());
					copyValidateRule.setRuleTypeId(rule.getRuleTypeId());
					copyValidateRule.setRuleValue1(rule.getRuleValue1());
					copyValidateRule.setRuleValue2(rule.getRuleValue2());
					copyValidateRule.setRuleValue3(rule.getRuleValue3());
					copyValidateRule.setSubAttributeCode(rule.getSubAttributeCode());
					copyValidateRule.setSubAttributeId(rule.getSubAttributeId());
					copyValidateRule.setRuleTypeClass(rule.getValidateRuleType().getRuleClass());
					copyValidateRule.setRuleTypeName(rule.getValidateRuleType().getName());
					if(rule.getValidateRuleType().getRuleClass().equals("cn.zpaas.lowcode.be.engine.ability.validator.OptionValidator")) {
    					// 获取校验规则配置信息
						
    					JsonObject ruleValue = JsonUtils.toJsonObject(copyValidateRule.getRuleValue1());
						String optionValues = JsonUtils.getString(ruleValue, RULE_OPTION_VALUES_KEY);
    					String optionDictName = JsonUtils.getString(ruleValue, RULE_OPTION_DICT_NAME_KEY);
    					String optionSql = JsonUtils.getString(ruleValue, RULE_OPTION_SQL_KEY);
    					String optionDB = JsonUtils.getString(ruleValue, RULE_OPTION_DB_KEY);
    					//进行可选值校验
    					JsonArray options = null;
    					//指定了optionValues
    					if(!StringUtils.isBlank(optionValues)) {
    						options = JsonUtils.toJsonArray(optionValues);
    					}else if(!StringUtils.isBlank(optionDictName)) {
							options = new JsonArray();
    						JsonObject dictOption = JsonUtils.toJsonObject(optionDictName);
							String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
							String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
							if(!StringUtils.isBlank(dictResourceId) && !StringUtils.isBlank(dictCode)) {//必须配置
								String dictValue  = JsonUtils.getString(dictOption, DictCacheAbility.DICT_VALUE);
								String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
								String parentDictValue = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_VALUE);
								if (!StringUtils.isBlank(dictValue)) {//取某个字典值的信息
									JsonObject dictJsonObject = DictCacheAbility.getInstance().getDictValue(systemId, dictResourceId, dictCode, dictValue);
									options.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
								}else if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentDictValue)) {//取子字典值列表
									JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(systemId, dictResourceId, dictCode, parentDictCode, parentDictValue);
									if(!JsonUtils.isEmpty(dictJsonArray)) {
										for(int i=0; i<dictJsonArray.size(); i++) {
											JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
											options.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
										}
									}
								}else {
									JsonArray dictJsonArray = DictCacheAbility.getInstance().getDictValues(systemId, dictResourceId, dictCode);
									if(!JsonUtils.isEmpty(dictJsonArray)) {
										for(int i=0; i<dictJsonArray.size(); i++) {
											JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
											options.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
										}
									}
								}
							}
						}else {
    						if(optionDB == null || optionDB.trim().length() == 0) {
    							throw new EngineException(ResultStatus.INTERNAL_ERROR, "optionDB must be configured when optionSql is used!");
    						}
    						//获取对应的JdbcTemplate
    						JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(rule.getSystemId(), optionDB);
    						
    						// 使用jdbcTemplate执行sql并获得执行结果
    						List<String> optionsList = jdbcTemplate.query(optionSql,  new RowMapper<String>() {
    							@Override
    							public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    								return rs.getString(1);
    							}
    						});
							if(optionsList != null) {
								options = JsonUtils.toJsonArray(optionsList);
							}
    					}
    					if(options == null) {
    						options =  new JsonArray();
    					}
    					ruleValue.remove(RULE_OPTION_DB_KEY);
    					ruleValue.remove(RULE_OPTION_SQL_KEY);
    					ruleValue.remove(RULE_OPTION_DICT_NAME_KEY);
    					ruleValue.add(RULE_OPTION_VALUES_KEY, options);
    					copyValidateRule.setRuleValue1(JsonUtils.toJson(ruleValue));
    				}
					validateRuleList.add(copyValidateRule);
    			}
    		}
			result.setValidateRules(validateRuleList);

			if(!StringUtils.isBlank(result.getSubRuleGroups())) {
				result.setSubRuleGroupMap(new HashMap<>());
				JsonObject subRuleGroups = JsonUtils.toJsonObject(result.getSubRuleGroups());
				for(String attrCode :subRuleGroups.keySet()) {
					String subRuleGroup = JsonUtils.getString(subRuleGroups, attrCode);
					// 进行递归加载
					if (!StringUtils.isBlank(subRuleGroup)) {
						result.getSubRuleGroupMap().put(attrCode, this.loadValidateRuleGroupForFront(systemId, subRuleGroup));
					}
				}
			}
    	}

    	return result;
    }
}
