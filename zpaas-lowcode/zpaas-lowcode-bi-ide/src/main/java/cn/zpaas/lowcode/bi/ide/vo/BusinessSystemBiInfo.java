package cn.zpaas.lowcode.bi.ide.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.DataModel;
import cn.zpaas.lowcode.domain.eo.DataSet;


/**
 * Bi领域业务系统信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class BusinessSystemBiInfo {
	
	private List<DataModel> dataModels;

	private List<DataSet> dataSets;

	public List<DataModel> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModel> dataModels) {
		this.dataModels = dataModels;
	}

	public List<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
}
