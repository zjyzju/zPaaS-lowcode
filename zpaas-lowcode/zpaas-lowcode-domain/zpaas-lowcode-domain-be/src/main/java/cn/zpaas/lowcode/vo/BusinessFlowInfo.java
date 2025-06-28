package cn.zpaas.lowcode.vo;

import java.util.List;
import java.util.Map;

import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;

/**
 * 业务流信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class BusinessFlowInfo {
	
	private BusinessFlow businessFlow;
	
	private List<BusinessFlowNode> businessFlowNodes;
	
	private Map<String, BusinessFlowInfo> subBusinessFlowMap;
	
	private String bpmnXml;

	public BusinessFlow getBusinessFlow() {
		return businessFlow;
	}

	public void setBusinessFlow(BusinessFlow businessFlow) {
		this.businessFlow = businessFlow;
	}

	public List<BusinessFlowNode> getBusinessFlowNodes() {
		return businessFlowNodes;
	}

	public void setBusinessFlowNodes(List<BusinessFlowNode> businessFlowNodes) {
		this.businessFlowNodes = businessFlowNodes;
	}

	public String getBpmnXml() {
		return bpmnXml;
	}

	public void setBpmnXml(String bpmnXml) {
		this.bpmnXml = bpmnXml;
	}

	public Map<String, BusinessFlowInfo> getSubBusinessFlowMap() {
		return subBusinessFlowMap;
	}

	public void setSubBusinessFlowMap(Map<String, BusinessFlowInfo> subBusinessFlowMap) {
		this.subBusinessFlowMap = subBusinessFlowMap;
	}
	
	

}
