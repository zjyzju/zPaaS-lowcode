package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.FuncTemplateRegionMapper;

@Repository
public class FuncTemplateRegion {
    private String id;

    private String name;
    
    private String regionType;

    private String description;

    private String status;

    private String supportUserDefOps;
    
    private String supportOperation;

    private Date createTime;

    private Date updateTime;
    
    private List<FuncTemplateRegionOperation> funcTemplateRegionOperations;
    
    @Autowired
    private FuncTemplateRegionMapper mapper;
    
    
    public FuncTemplateRegion byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<FuncTemplateRegion> list() {
    	FuncTemplateRegionExample criteria = new FuncTemplateRegionExample();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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

	public List<FuncTemplateRegionOperation> getFuncTemplateRegionOperations() {
		return funcTemplateRegionOperations;
	}

	public void setFuncTemplateRegionOperations(List<FuncTemplateRegionOperation> funcTemplateRegionOperations) {
		this.funcTemplateRegionOperations = funcTemplateRegionOperations;
	}

	public String getRegionType() {
		return regionType;
	}

	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}

	public String getSupportUserDefOps() {
		return supportUserDefOps;
	}

	public void setSupportUserDefOps(String supportUserDefOps) {
		this.supportUserDefOps = supportUserDefOps;
	}

	public String getSupportOperation() {
		return supportOperation;
	}

	public void setSupportOperation(String supportOperation) {
		this.supportOperation = supportOperation;
	}
}