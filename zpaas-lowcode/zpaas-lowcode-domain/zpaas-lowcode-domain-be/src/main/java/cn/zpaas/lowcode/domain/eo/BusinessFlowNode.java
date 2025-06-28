package cn.zpaas.lowcode.domain.eo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.zpaas.lowcode.domain.mapper.BusinessFlowNodeMapper;
import cn.zpaas.lowcode.utils.KeyGenerate;
@Repository
public class BusinessFlowNode {
    private String id;

    private String name;

    private String flowNodeId;
    
    private String bpmnNodeId;

    private String businessFlowId;

    private Integer nodeOrder;

    private String nodePreCfg;

    private String nodeCfg;

    private String nodePostCfg;

    private String nodeReservedCfg1;

    private String nodeReservedCfg2;

    private String tenantId;

    private String systemId;

    private Date createTime;

    private Date updateTime;
    
    private BusinessFlow businessFlow;
    
    private FlowNode flowNode;
    
    @Autowired
    private BusinessFlowNodeMapper mapper;
    
    
    
    
    public int save(BusinessFlowNode businessFlowNode) {
    	if(businessFlowNode.getId() == null || businessFlowNode.getId().trim().length() == 0) {
    		businessFlowNode.setId(KeyGenerate.uuidKey());
    	}
    	return mapper.insert(businessFlowNode);
    }
    
    public int updateById(BusinessFlowNode businessFlowNode) {
    	if(businessFlowNode.getId() == null || businessFlowNode.getId().trim().length() == 0) {
    		return 0;
    	}
    	return mapper.updateByPrimaryKey(businessFlowNode);
    }
    
    public BusinessFlowNode byId(String id) {
    	return mapper.selectByPrimaryKey(id);
    }
    
    public List<BusinessFlowNode> list() {
    	BusinessFlowNodeExample criteria = new BusinessFlowNodeExample();
    	criteria.setOrderByClause("node_order asc ");
    	return mapper.selectByExample(criteria);
    }
    
    public int saveOrUpdate(BusinessFlowNode businessFlowNode) {
    	Date nowDate = new Date();
    	businessFlowNode.setUpdateTime(nowDate);
    	int result = this.updateById(businessFlowNode);
    	if(result <=0) {
    		businessFlowNode.setCreateTime(nowDate);
    		result = this.save(businessFlowNode);
    	}
    	return result;
    }
    
    public int deleteById(String id) {
    	if(id == null || id.trim().length() == 0) {
    		return 0;
    	}
    	return mapper.deleteByPrimaryKey(id);
    }
    
    public List<BusinessFlowNode> listByBusinessFlowId(String businessFlowId) {
    	BusinessFlowNodeExample criteria = new BusinessFlowNodeExample();
    	criteria.createCriteria().andBusinessFlowIdEqualTo(businessFlowId);
    	criteria.setOrderByClause("node_order asc");
    	return mapper.selectByExample(criteria);
    }
    
    public List<BusinessFlowNode> listBySystem(String systemId) {
    	BusinessFlowNodeExample criteria = new BusinessFlowNodeExample();
    	criteria.createCriteria().andSystemIdEqualTo(systemId);
    	criteria.setOrderByClause("node_order asc");
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

    public String getFlowNodeId() {
        return flowNodeId;
    }

    public void setFlowNodeId(String flowNodeId) {
        this.flowNodeId = flowNodeId == null ? null : flowNodeId.trim();
    }

    public String getBusinessFlowId() {
        return businessFlowId;
    }

    public void setBusinessFlowId(String businessFlowId) {
        this.businessFlowId = businessFlowId == null ? null : businessFlowId.trim();
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public String getNodePreCfg() {
        return nodePreCfg;
    }

    public void setNodePreCfg(String nodePreCfg) {
        this.nodePreCfg = nodePreCfg == null ? null : nodePreCfg.trim();
    }

    public String getNodeCfg() {
        return nodeCfg;
    }

    public void setNodeCfg(String nodeCfg) {
        this.nodeCfg = nodeCfg == null ? null : nodeCfg.trim();
    }

    public String getNodePostCfg() {
        return nodePostCfg;
    }

    public void setNodePostCfg(String nodePostCfg) {
        this.nodePostCfg = nodePostCfg == null ? null : nodePostCfg.trim();
    }

    public String getNodeReservedCfg1() {
        return nodeReservedCfg1;
    }

    public void setNodeReservedCfg1(String nodeReservedCfg1) {
        this.nodeReservedCfg1 = nodeReservedCfg1 == null ? null : nodeReservedCfg1.trim();
    }

    public String getNodeReservedCfg2() {
        return nodeReservedCfg2;
    }

    public void setNodeReservedCfg2(String nodeReservedCfg2) {
        this.nodeReservedCfg2 = nodeReservedCfg2 == null ? null : nodeReservedCfg2.trim();
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

	public BusinessFlow getBusinessFlow() {
		return businessFlow;
	}

	public void setBusinessFlow(BusinessFlow businessFlow) {
		this.businessFlow = businessFlow;
	}

	public FlowNode getFlowNode() {
		return flowNode;
	}

	public void setFlowNode(FlowNode flowNode) {
		this.flowNode = flowNode;
	}

	public String getBpmnNodeId() {
		return bpmnNodeId;
	}

	public void setBpmnNodeId(String bpmnNodeId) {
		this.bpmnNodeId = bpmnNodeId;
	}
	
	
}