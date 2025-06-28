package cn.zpaas.lowcode.be.engine.ability.validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.DictCacheAbility;
import cn.zpaas.lowcode.be.engine.proxy.DBSchemaProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.ValidateRule;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 可选值校验器OptionValidator的主要用途是校验参数值是否在可选值范围内。
 */
public class OptionValidator extends Validator {
	private static Logger logger = LoggerFactory.getLogger(OptionValidator.class);
	
	public static final String RULE_OPTION_VALUES_KEY = "optionValues";//静态常量，配置optionValues的Key
	public static final String RULE_OPTION_DICT_NAME_KEY = "optionDictName";//静态常量，配置optionDictName的Key
	public static final String RULE_OPTION_SQL_KEY = "optionSql";//静态常量，配置optionSql的Key
	public static final String RULE_OPTION_DB_KEY = "optionDB";//静态常量，配置optionDB的Key
	
	public static final String COMMA = ",";//逗号
	
	
	/**
	 * 校验实现方法，由子类实现
	 * @param validateRule 校验规则
	 * @param attrValue 属性值
	 * @param srcObject 校验对象（完整的对象）
	 * @return 校验结果
	 */
	@Override
	protected JsonObject validateImpl(ValidateRule validateRule, Object attrValue, JsonObject srcObject, JsonObject parentObject)
			throws EngineException {
		//初始化返回结果对象JSON
		JsonObject result = new JsonObject();
		result.addProperty(RESULT_STATUS_KEY, true);
		result.addProperty(RESULT_INTERRUPTED_KEY, false);
		JsonArray messages = new JsonArray();
		result.add(RESULT_MESSAGES_KEY, messages);
		
		// 获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		
		String optionValues = JsonUtils.getString(ruleValue, RULE_OPTION_VALUES_KEY);
		String optionDictName = JsonUtils.getString(ruleValue, RULE_OPTION_DICT_NAME_KEY);
		String optionSql = JsonUtils.getString(ruleValue, RULE_OPTION_SQL_KEY);
		String optionDB = JsonUtils.getString(ruleValue, RULE_OPTION_DB_KEY);
		
		boolean interrupt = JsonUtils.getBoolean(ruleValue, RULE_IS_INTERRUPT_KEY);
		// 两个属性的值都是false的情况下，是无效的校验规则
		if (StringUtils.isBlank(optionValues) && StringUtils.isBlank(optionDictName) && StringUtils.isBlank(optionSql)) {
			logger.info("optionValues optionDictName and optionSql are all empty. validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of  optionValues optionDictName and optionSql  must be configured!");
		}
		String message = JsonUtils.getString(ruleValue, RULE_MESSAGE_KEY);
		message = this.processMessage(validateRule.getAttributeCode(), validateRule.getSubAttributeCode(), message);
		
		String errorCode = JsonUtils.getString(ruleValue, RULE_ERROR_CODE_KEY);
		
		boolean isPass = true;
		if(attrValue == null || (attrValue instanceof String && ((String) attrValue).trim().length() == 0)) {
			return result;
		}
		
		//进行可选值校验
		List<String> options = null;
		//指定了optionValues
		if(!StringUtils.isBlank(optionValues)) {
			JsonArray optionArray = JsonUtils.toJsonArray(optionValues);
			options = new ArrayList<>();
			for(JsonElement element : optionArray) {
				options.add(element.getAsString());
			}
		}else if(optionDictName != null && optionDictName.trim().length() != 0) {
			JsonArray optionArray = new JsonArray();
    		JsonObject dictOption = JsonUtils.toJsonObject(optionDictName);
			String dictResourceId = JsonUtils.getString(dictOption, DictCacheAbility.DICT_RESOURCE_ID);//取字典资源标识
			String dictCode = JsonUtils.getString(dictOption, DictCacheAbility.DICT_CODE);//取字典编码
			if(!StringUtils.isBlank(dictResourceId) && !StringUtils.isBlank(dictCode)) {//必须配置
				String dictValue  = JsonUtils.getString(dictOption, DictCacheAbility.DICT_VALUE);
				String parentDictCode = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_CODE);
				String parentDictValue = JsonUtils.getString(dictOption, DictCacheAbility.PARENT_DICT_VALUE);
				if (!StringUtils.isBlank(dictValue)) {//取某个字典值的信息
					JsonObject dictJsonObject = DictCacheAbility.getInstance().getDictValue(validateRule.getSystemId(), dictResourceId, dictCode, dictValue);
					optionArray.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
				}else if(!StringUtils.isBlank(parentDictCode) && !StringUtils.isBlank(parentDictValue)) {//取子字典值列表
					JsonArray dictJsonArray = DictCacheAbility.getInstance().getSubDictValues(validateRule.getSystemId(), dictResourceId, dictCode, parentDictCode, parentDictValue);
					if(!JsonUtils.isEmpty(dictJsonArray)) {
						for(int i=0; i<dictJsonArray.size(); i++) {
							JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
							optionArray.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						}
					}
				}else {
					JsonArray dictJsonArray = DictCacheAbility.getInstance().getDictValues(validateRule.getSystemId(), dictResourceId, dictCode);
					if(!JsonUtils.isEmpty(dictJsonArray)) {
						for(int i=0; i<dictJsonArray.size(); i++) {
							JsonObject dictJsonObject = dictJsonArray.get(i).getAsJsonObject();
							optionArray.add(JsonUtils.getString(dictJsonObject, DictCacheAbility.DICT_VALUE));
						}
					}
				}
			}
			options = new ArrayList<>();
			for(JsonElement element : optionArray) {
				options.add(element.getAsString());
			}
		}else {
			//获取对应的JdbcTemplate
			JdbcTemplate jdbcTemplate = DBSchemaProxy.getJdbcTemplate(validateRule.getSystemId(), optionDB);
			
			// 使用jdbcTemplate执行sql并获得执行结果
			options = jdbcTemplate.query(optionSql,  new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}
			});
		}
		if(options == null) {
			options =  new ArrayList<>();
		}
		
		if(!options.contains(attrValue.toString())) {
			isPass = false;
		}
		
		if(!isPass) {
			JsonObject msg = new JsonObject();
			msg.addProperty(RULE_MESSAGE_KEY, message);
			msg.addProperty(RULE_ERROR_CODE_KEY, errorCode);
			messages.add(msg);
			result.addProperty(RESULT_STATUS_KEY, false);
			if(interrupt) {
				result.addProperty(RESULT_INTERRUPTED_KEY, true);
			}
		}

		return result;
	}
	
	/**
	 * 对校验规则中个性属性实现个性的逻辑。规则配置信息校验不通过时，抛出异常。
	 * @param validateRule 校验规则配置信息
	 * @throws EngineException
	 */
	@Override
	protected void ruleCheck(ValidateRule validateRule) throws EngineException {
		super.ruleCheck(validateRule);
		if(validateRule.getRuleValue1() == null || validateRule.getRuleValue1().trim().length() == 0) {
			logger.error("validateRule's cfg informarion(ruleValue1) can't be null! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "validateRule's cfg informarion(ruleValue1) can't be null!");
		}
		
		//获取校验规则配置信息
		JsonObject ruleValue = JsonUtils.toJsonObject(validateRule.getRuleValue1());
		//三个属性至少有一个有配置
		if(ruleValue == null || (!ruleValue.has(RULE_OPTION_VALUES_KEY) && !ruleValue.has(RULE_OPTION_DICT_NAME_KEY) 
				&& !ruleValue.has(RULE_OPTION_SQL_KEY))) {
			logger.error("one of optionValues optionDictName and optionSql must be configured! validateRuleId: {}", validateRule.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "one of  optionValues optionDictName and optionSql  must be configured!");
		}
		
		
	}
}
