package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.FuncTemplateRegionOperationMapper;

@Repository
public class FuncTemplateRegionOperation {
	public  static final String OPERATION_TYPE_C = "C";//新增
	public  static final String OPERATION_TYPE_U = "U";//修改
	public  static final String OPERATION_TYPE_Q = "Q";//查询列表
	public  static final String OPERATION_TYPE_D = "D";//删除
	public  static final String OPERATION_TYPE_N = "N";//取消
	public  static final String OPERATION_TYPE_P = "P";//预新增
	public  static final String OPERATION_TYPE_B = "B";//主键查询
	public  static final String OPERATION_TYPE_S = "S";//选择
	
	public  static final String DISPLAY_TYPE_S = "S";//原页面显示
	public  static final String DISPLAY_TYPE_P = "P";//弹出页面显示
	
    private String id;

    private String name;

    private String tplRegionId;

    private String operationType;

    private Integer displayOrder;

    private String targetTplRegionId;

    private String displayType;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private FuncTemplateRegionOperationMapper mapper;
    
    public List<FuncTemplateRegionOperation> listByTplRegion(String tplRegionId) {
    	FuncTemplateRegionOperationExample criteria = new FuncTemplateRegionOperationExample();
    	criteria.createCriteria().andTplRegionIdEqualTo(tplRegionId);
    	return mapper.selectByExample(criteria);
    }
    
    public List<FuncTemplateRegionOperation> list() {
    	return mapper.selectByExample(null);
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

    public String getTplRegionId() {
        return tplRegionId;
    }

    public void setTplRegionId(String tplRegionId) {
        this.tplRegionId = tplRegionId == null ? null : tplRegionId.trim();
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType == null ? null : operationType.trim();
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getTargetTplRegionId() {
        return targetTplRegionId;
    }

    public void setTargetTplRegionId(String targetTplRegionId) {
        this.targetTplRegionId = targetTplRegionId == null ? null : targetTplRegionId.trim();
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType == null ? null : displayType.trim();
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