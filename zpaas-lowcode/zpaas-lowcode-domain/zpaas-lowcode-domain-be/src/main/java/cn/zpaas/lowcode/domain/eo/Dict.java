package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.DictMapper;

@Repository
public class Dict {
    private String id;

    private String dictCode;

    private String dictName;

    private String parentDictCode;

    private String parentDictValue;

    private String dictValue;

    private String dictValueLabel;

    private String dictValue2;

    private String dictValue3;

    private Integer displayOrder;

    private String status;

    private Date createTime;

    private Date updateTime;

    @Autowired
    private DictMapper mapper;

    public List<Dict> list() {
        DictExample criteria = new DictExample();
        criteria.createCriteria().andStatusEqualTo(Status.EFF);
        criteria.setOrderByClause("display_order");
        return mapper.selectByExample(criteria);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode == null ? null : dictCode.trim();
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName == null ? null : dictName.trim();
    }

    public String getParentDictCode() {
        return parentDictCode;
    }

    public void setParentDictCode(String parentDictCode) {
        this.parentDictCode = parentDictCode == null ? null : parentDictCode.trim();
    }

    public String getParentDictValue() {
        return parentDictValue;
    }

    public void setParentDictValue(String parentDictValue) {
        this.parentDictValue = parentDictValue == null ? null : parentDictValue.trim();
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue == null ? null : dictValue.trim();
    }

    public String getDictValueLabel() {
        return dictValueLabel;
    }

    public void setDictValueLabel(String dictValueLabel) {
        this.dictValueLabel = dictValueLabel == null ? null : dictValueLabel.trim();
    }

    public String getDictValue2() {
        return dictValue2;
    }

    public void setDictValue2(String dictValue2) {
        this.dictValue2 = dictValue2 == null ? null : dictValue2.trim();
    }

    public String getDictValue3() {
        return dictValue3;
    }

    public void setDictValue3(String dictValue3) {
        this.dictValue3 = dictValue3 == null ? null : dictValue3.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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