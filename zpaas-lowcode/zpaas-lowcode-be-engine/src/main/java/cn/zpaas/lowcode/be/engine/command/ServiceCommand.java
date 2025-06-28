package cn.zpaas.lowcode.be.engine.command;


/**
 * @author zjy
 *	包装服务调用的命令类
 */
public class ServiceCommand extends BaseCommand {
	private String serviceId;  //服务标识
	private String operationId;  //方法标识
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	
}
