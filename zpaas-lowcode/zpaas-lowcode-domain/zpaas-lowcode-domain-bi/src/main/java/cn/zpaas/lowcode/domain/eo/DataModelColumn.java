package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DataModelColumnMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DataModelColumn {

    public static final String TRANSLATE_TYPE_O = "O";//配置选项
    public static final String TRANSLATE_TYPE_T = "T";//直接翻译

    public static final String OPTION_TYPE_F = "F";//固定
    public static final String OPTION_TYPE_S = "S";//SQL
    public static final String OPTION_TYPE_D = "D";//字典
    public static final String OPTION_TYPE_M = "M";//暴露服务

    private String id;

    private String code;

    private String name;

    private String dataModelId;

    private String columnType;

    private String valueType;

    private String translateType;

    private String optionType;

    private String optionCfg;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DataModelColumnMapper mapper;

    public DataModelColumn byId(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<DataModelColumn> list(String dataModelId) {
        DataModelColumnExample criteria = new DataModelColumnExample();
        criteria.createCriteria().andDataModelIdEqualTo(dataModelId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelColumn> listBySystem(String systemId) {
        DataModelColumnExample criteria = new DataModelColumnExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public List<DataModelColumn> list() {
        DataModelColumnExample criteria = new DataModelColumnExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("code");
        return mapper.selectByExample(criteria);
    }

    public int add(DataModelColumn dataModelColumn) {
        if(StringUtils.isBlank(dataModelColumn.getId())) {
            dataModelColumn.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dataModelColumn.setCreateTime(nowDate);
        dataModelColumn.setUpdateTime(nowDate);
        dataModelColumn.setStatus(Status.EFF);
        return mapper.insert(dataModelColumn);
    }

    public int update(DataModelColumn dataModelColumn) {
        Date nowDate = new Date();
        dataModelColumn.setUpdateTime(nowDate);
        return mapper.updateByPrimaryKey(dataModelColumn);
    }

    public int delete(String Id) {
        DataModelColumn dataModelColumn = this.byId(Id);
        if(dataModelColumn != null) {
            Date nowDate = new Date();
            dataModelColumn.setUpdateTime(nowDate);
            dataModelColumn.setStatus(Status.EXP);
            return mapper.updateByPrimaryKey(dataModelColumn);
        }else {
            return 0;
        }        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId == null ? null : dataModelId.trim();
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType == null ? null : columnType.trim();
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType == null ? null : valueType.trim();
    }

    public String getTranslateType() {
        return translateType;
    }

    public void setTranslateType(String translateType) {
        this.translateType = translateType;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getOptionCfg() {
        return optionCfg;
    }

    public void setOptionCfg(String optionCfg) {
        this.optionCfg = optionCfg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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
}