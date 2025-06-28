package cn.zpaas.lowcode.fe.ide.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.ComboFuncDefine;
import cn.zpaas.lowcode.domain.eo.FuncDefine;

/**
 * 前端领域业务系统信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class BusinessSystemFeInfo {
	
	private List<FuncDefine> funcDefines;

	private List<FuncDefine> subFuncDefines;
	
	private List<ComboFuncDefine> comboFuncDefines;
	
	public List<FuncDefine> getFuncDefines() {
		return funcDefines;
	}

	public void setFuncDefines(List<FuncDefine> funcDefines) {
		this.funcDefines = funcDefines;
	}

	public List<ComboFuncDefine> getComboFuncDefines() {
		return comboFuncDefines;
	}

	public void setComboFuncDefines(List<ComboFuncDefine> comboFuncDefines) {
		this.comboFuncDefines = comboFuncDefines;
	}

	public List<FuncDefine> getSubFuncDefines() {
		return subFuncDefines;
	}

	public void setSubFuncDefines(List<FuncDefine> subFuncDefines) {
		this.subFuncDefines = subFuncDefines;
	}

	
}
