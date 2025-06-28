package cn.zpaas.lowcode.vo;

import java.util.List;

import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.domain.eo.Parameter;

/**
 * 方法信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class OperationInfo {
	
	private Operation operation;
	
	private List<Parameter> parameters;

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	
	
	
}
