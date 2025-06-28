package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DictResourceMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;

@Repository
public class DictResource {
    public static final String DICT_RESOURCE_TYPE_SINGLE = "S";//单表模式
    public static final String DICT_RESOURCE_TYPE_MASTERSLAVE = "M";//主从表模式

    private String id;

    private String name;

    private String type;

    private String dbSchemaId;

    private String dictTable;

    private String subDictTable;

    private String dictIdCol;

    private String dictCodeCol;

    private String dictNameCol;

    private String parentDictCodeCol;

    private String parentDictValueCol;

    private String dictValueCol;

    private String dictValueLabelCol;

    private String dictValue2Col;

    private String dictValue3Col;

    private String dictStatusCol;

    private String effStatusValue;

    private String subTableDictCodeCol;

    private String subTableStatusCol;

    private String effSubTableStatusValue;

    private String status;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DictResourceMapper mapper;

    public DictResource byId(String id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    public int add(DictResource dictResource) {
        if(StringUtils.isBlank(dictResource.getId())) {
            dictResource.setId(KeyGenerate.uuidKey());
        }
        Date nowDate = new Date();
        dictResource.setCreateTime(nowDate);
        dictResource.setUpdateTime(nowDate);
        dictResource.setStatus(Status.EFF);
        return mapper.insert(dictResource);
    }

    public int save(DictResource dictResource) {
        Date nowDate = new Date();
        dictResource.setUpdateTime(nowDate);
        return mapper.updateByPrimaryKey(dictResource);
    }

    public int delete(String dictResourceId) {
        DictResource dictResource = this.byId(dictResourceId);
        if(dictResource == null) {
            return 0;
        }
        Date nowDate = new Date();
        dictResource.setUpdateTime(nowDate);
        dictResource.setStatus(Status.EXP);
        return mapper.updateByPrimaryKey(dictResource);
    }

    public List<DictResource> listBySystem(String systemId) {
        DictResourceExample criteria = new DictResourceExample();
        criteria.createCriteria().andSystemIdEqualTo(systemId).andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("name");
        return mapper.selectByExample(criteria);
    }

    public List<DictResource> list() {
        DictResourceExample criteria = new DictResourceExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        return mapper.selectByExample(criteria);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDbSchemaId() {
        return dbSchemaId;
    }

    public void setDbSchemaId(String dbSchemaId) {
        this.dbSchemaId = dbSchemaId == null ? null : dbSchemaId.trim();
    }

    public String getDictTable() {
        return dictTable;
    }

    public void setDictTable(String dictTable) {
        this.dictTable = dictTable == null ? null : dictTable.trim();
    }

    public String getSubDictTable() {
        return subDictTable;
    }

    public void setSubDictTable(String subDictTable) {
        this.subDictTable = subDictTable == null ? null : subDictTable.trim();
    }

    public String getDictIdCol() {
        return dictIdCol;
    }

    public void setDictIdCol(String dictIdCol) {
        this.dictIdCol = dictIdCol == null ? null : dictIdCol.trim();
    }

    public String getDictCodeCol() {
        return dictCodeCol;
    }

    public void setDictCodeCol(String dictCodeCol) {
        this.dictCodeCol = dictCodeCol == null ? null : dictCodeCol.trim();
    }

    public String getDictNameCol() {
        return dictNameCol;
    }

    public void setDictNameCol(String dictNameCol) {
        this.dictNameCol = dictNameCol == null ? null : dictNameCol.trim();
    }

    public String getParentDictCodeCol() {
        return parentDictCodeCol;
    }

    public void setParentDictCodeCol(String parentDictCodeCol) {
        this.parentDictCodeCol = parentDictCodeCol == null ? null : parentDictCodeCol.trim();
    }

    public String getParentDictValueCol() {
        return parentDictValueCol;
    }

    public void setParentDictValueCol(String parentDictValueCol) {
        this.parentDictValueCol = parentDictValueCol == null ? null : parentDictValueCol.trim();
    }

    public String getDictValueCol() {
        return dictValueCol;
    }

    public void setDictValueCol(String dictValueCol) {
        this.dictValueCol = dictValueCol == null ? null : dictValueCol.trim();
    }

    public String getDictValueLabelCol() {
        return dictValueLabelCol;
    }

    public void setDictValueLabelCol(String dictValueLabelCol) {
        this.dictValueLabelCol = dictValueLabelCol == null ? null : dictValueLabelCol.trim();
    }

    public String getDictValue2Col() {
        return dictValue2Col;
    }

    public void setDictValue2Col(String dictValue2Col) {
        this.dictValue2Col = dictValue2Col == null ? null : dictValue2Col.trim();
    }

    public String getDictValue3Col() {
        return dictValue3Col;
    }

    public void setDictValue3Col(String dictValue3Col) {
        this.dictValue3Col = dictValue3Col == null ? null : dictValue3Col.trim();
    }

    public String getDictStatusCol() {
        return dictStatusCol;
    }

    public void setDictStatusCol(String dictStatusCol) {
        this.dictStatusCol = dictStatusCol == null ? null : dictStatusCol.trim();
    }

    public String getEffStatusValue() {
        return effStatusValue;
    }

    public void setEffStatusValue(String effStatusValue) {
        this.effStatusValue = effStatusValue == null ? null : effStatusValue.trim();
    }

    public String getSubTableDictCodeCol() {
        return subTableDictCodeCol;
    }

    public void setSubTableDictCodeCol(String subTableDictCodeCol) {
        this.subTableDictCodeCol = subTableDictCodeCol == null ? null : subTableDictCodeCol.trim();
    }

    public String getSubTableStatusCol() {
        return subTableStatusCol;
    }

    public void setSubTableStatusCol(String subTableStatusCol) {
        this.subTableStatusCol = subTableStatusCol == null ? null : subTableStatusCol.trim();
    }

    public String getEffSubTableStatusValue() {
        return effSubTableStatusValue;
    }

    public void setEffSubTableStatusValue(String effSubTableStatusValue) {
        this.effSubTableStatusValue = effSubTableStatusValue == null ? null : effSubTableStatusValue.trim();
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