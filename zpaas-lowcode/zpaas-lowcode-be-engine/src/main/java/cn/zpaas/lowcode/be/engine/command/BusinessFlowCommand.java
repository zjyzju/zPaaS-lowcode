package cn.zpaas.lowcode.be.engine.command;

import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;

/**
 * @author zjy
 * 业务流执行命令类
 */
public class BusinessFlowCommand extends BaseCommand {
	private String businessFlowId; //业务流标识
	private AttributedObjectContainer attributedObject; //保存领域对象包含数据的容器对象
	
	public String getBusinessFlowId() {
		return businessFlowId;
	}
	public void setBusinessFlowId(String businessFlowId) {
		this.businessFlowId = businessFlowId;
	}
	public AttributedObjectContainer getAttributedObject() {
		return attributedObject;
	}
	public void setAttributedObject(AttributedObjectContainer attributedObject) {
		this.attributedObject = attributedObject;
	}
	
	
}
