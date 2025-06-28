package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.constant.Status;
import cn.zpaas.lowcode.domain.mapper.FlowNodeMapper;
@Repository
public class FlowNode {
    private String id;

    private String name;

    private String type;

    private String description;

    private String status;

    private String nodeClass;

    private String cfgRequirement;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    @Autowired
    private FlowNodeMapper mapper;
    
    public FlowNode queryByNodeClass(String nodeClass) {
    	FlowNodeExample criteria = new FlowNodeExample();
    	criteria.createCriteria().andNodeClassEqualTo(nodeClass).andStatusEqualTo(Status.EFF);
    	List<FlowNode> flowNodes = mapper.selectByExample(criteria);
    	if(flowNodes != null && flowNodes.size() > 0) {
    		return flowNodes.get(0);
    	}else {
    		return null;
    	}
    }
    
    public FlowNode byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<FlowNode> list() {
    	FlowNodeExample criteria = new FlowNodeExample();
    	criteria.createCriteria().andStatusEqualTo(Status.EFF);
    	criteria.setOrderByClause("name");
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
        this.type = type;
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

    public String getNodeClass() {
        return nodeClass;
    }

    public void setNodeClass(String nodeClass) {
        this.nodeClass = nodeClass == null ? null : nodeClass.trim();
    }

    public String getCfgRequirement() {
        return cfgRequirement;
    }

    public void setCfgRequirement(String cfgRequirement) {
        this.cfgRequirement = cfgRequirement == null ? null : cfgRequirement.trim();
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