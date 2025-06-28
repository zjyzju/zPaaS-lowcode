package cn.zpaas.lowcode.vo;

import cn.zpaas.lowcode.domain.eo.FuncDefine;

/**
 * 功能生成请求vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class FuncDefineGenerateRequest {
	public static final String CREATE_MODE_D = "D";//根据领域对象生成
	public static final String CREATE_MODE_T = "T";//根据数据库表生成
	
	public static final String GENERATE_LEVEL_A = "A";//生成全部
	public static final String GENERATE_LEVEL_F = "F";//生成框架
	
	public static final String FUNC_PATTERN_D = "D";//动态配置
	public static final String FUNC_PATTERN_C = "C";//生成代码
	
	private String createMode;
	private String generateLevel;
	private String funcPattern;
	private FuncDefine newFuncDefine;
	private String businessDomainId;
	private String ignorePrefixString;
	private String dbSchemaId;
	
	public String getCreateMode() {
		return createMode;
	}
	public void setCreateMode(String createMode) {
		this.createMode = createMode;
	}
	public String getGenerateLevel() {
		return generateLevel;
	}
	public void setGenerateLevel(String generateLevel) {
		this.generateLevel = generateLevel;
	}
	public String getFuncPattern() {
		return funcPattern;
	}
	public void setFuncPattern(String funcPattern) {
		this.funcPattern = funcPattern;
	}
	public FuncDefine getNewFuncDefine() {
		return newFuncDefine;
	}
	public void setNewFuncDefine(FuncDefine newFuncDefine) {
		this.newFuncDefine = newFuncDefine;
	}
	public String getBusinessDomainId() {
		return businessDomainId;
	}
	public void setBusinessDomainId(String businessDomainId) {
		this.businessDomainId = businessDomainId;
	}
	public String getIgnorePrefixString() {
		return ignorePrefixString;
	}
	public void setIgnorePrefixString(String ignorePrefixString) {
		this.ignorePrefixString = ignorePrefixString;
	}
	public String getDbSchemaId() {
		return dbSchemaId;
	}
	public void setDbSchemaId(String dbSchemaId) {
		this.dbSchemaId = dbSchemaId;
	}
	
	
}
