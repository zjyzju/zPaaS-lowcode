package cn.zpaas.lowcode.vo;

import java.util.List;

/**
 * 工作流流程信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class ProcessDiagramInfo {
	private String bpmnXml;
	
	private List<String> histActivityIds;

	public String getBpmnXml() {
		return bpmnXml;
	}

	public void setBpmnXml(String bpmnXml) {
		this.bpmnXml = bpmnXml;
	}

	public List<String> getHistActivityIds() {
		return histActivityIds;
	}

	public void setHistActivityIds(List<String> histActivityIds) {
		this.histActivityIds = histActivityIds;
	}
}
