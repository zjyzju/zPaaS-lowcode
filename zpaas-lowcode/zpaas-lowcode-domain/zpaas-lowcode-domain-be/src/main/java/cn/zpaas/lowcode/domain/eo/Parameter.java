package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.YesOrNo;
import cn.zpaas.lowcode.domain.mapper.ParameterMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class Parameter {
	
    private String id;

    private String name;

    private String code;

    private String isListParam;

    private String paramType;

    private String paramClass;

    private String isInParam;

    private Integer displayOrder;

    private String operationId;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private AttributedObject paramClassObject;
    
    @Autowired
    private ParameterMapper mapper;
    
    
    public int save(Parameter parameter) {
    	if(parameter.getId() == null || parameter.getId().trim().length() == 0) {
    		parameter.setId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(parameter);
    }
    
    public int updateById(Parameter parameter) {
    	if(parameter.getId() == null || parameter.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(parameter);
    }
    
    public Parameter byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<Parameter> list() {
    	return mapper.selectByExample(null);
    }
    
    public List<Parameter> listByOperationId(String operationId) {
    	ParameterExample criteria = new ParameterExample();
    	criteria.createCriteria().andOperationIdEqualTo(operationId);
    	criteria.setOrderByClause("display_order");
    	List<Parameter> parameters = mapper.selectByExample(criteria);
    	
    	return parameters;
    }
    
    public List<Parameter> getInParamsByOperationId(String operationId) {
    	ParameterExample criteria = new ParameterExample();
    	criteria.createCriteria().andOperationIdEqualTo(operationId).andIsInParamEqualTo(YesOrNo.YES);
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public List<Parameter> listBySystem(String systemId) {
    	ParameterExample criteria = new ParameterExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("display_order");
    	return mapper.selectByExample(criteria);
    }
    
    public int delete(String id) {
    	return mapper.deleteByPrimaryKey(id);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getIsListParam() {
        return isListParam;
    }

    public void setIsListParam(String isListParam) {
        this.isListParam = isListParam == null ? null : isListParam.trim();
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType == null ? null : paramType.trim();
    }

    public String getParamClass() {
        return paramClass;
    }

    public void setParamClass(String paramClass) {
        this.paramClass = paramClass == null ? null : paramClass.trim();
    }

    public String getIsInParam() {
        return isInParam;
    }

    public void setIsInParam(String isInParam) {
        this.isInParam = isInParam == null ? null : isInParam.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId == null ? null : operationId.trim();
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

	public AttributedObject getParamClassObject() {
		return paramClassObject;
	}

	public void setParamClassObject(AttributedObject paramClassObject) {
		this.paramClassObject = paramClassObject;
	}
}