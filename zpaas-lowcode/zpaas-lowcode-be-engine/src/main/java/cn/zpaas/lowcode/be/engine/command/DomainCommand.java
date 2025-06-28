package cn.zpaas.lowcode.be.engine.command;

import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;

/**
 * @author zjy
 * 领域对象方法调用命令
 */
public class DomainCommand extends BaseCommand {
	private String domainObjectId; //调用领域对象标识
	private String operationId; //调用领域对象方法标识
	private AttributedObjectContainer attributedObject;  //保存领域对象数据的容器类
	
	public String getDomainObjectId() {
		return domainObjectId;
	}
	public void setDomainObjectId(String domainObjectId) {
		this.domainObjectId = domainObjectId;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	public AttributedObjectContainer getAttributedObject() {
		return attributedObject;
	}
	public void setAttributedObject(AttributedObjectContainer attributedObject) {
		this.attributedObject = attributedObject;
	}
}
