package cn.zpaas.lowcode.be.engine.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.command.ServiceCommand;
import cn.zpaas.lowcode.be.engine.proxy.ServiceProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.Operation;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *
 */
public class ServiceInvokeDelegate implements JavaDelegate {
	public static Logger logger = LoggerFactory.getLogger(JavaDelegate.class);
	
	private static final String SYSTEM_ID = "systemId";
	private static final String SERVICE_ID = "serviceId";
	private static final String OPERATION_ID = "operationId";
	private static final String PROCESS_VARIABLES = "processVariables";
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String systemId = (String)execution.getVariableLocal(SYSTEM_ID);
		String serviceId = (String)execution.getVariableLocal(SERVICE_ID);
		String operationId = (String)execution.getVariableLocal(OPERATION_ID);
		if(systemId == null || systemId.trim().length() == 0 || serviceId== null || serviceId.trim().length() == 0 ||
				operationId == null || operationId.trim().length() == 0) {
			logger.error("The systemId serviceId and operationId of target service operation can't be null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "The systemId serviceId and operationId of target service operation can't be null!");
		}
		ServiceProxy applicationServiceProxy = ServiceProxy.getProxy();
		Operation operation = applicationServiceProxy.getOperation(systemId, operationId);
		if(operation == null) {
			logger.error("invalid operation!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operation!");
		}
		JsonObject reqData = new JsonObject();
		reqData.add(PROCESS_VARIABLES, JsonUtils.toJsonObject(execution.getVariables()));
		// 构建服务调用命令对象
		ServiceCommand serviceCommand = new ServiceCommand();
		serviceCommand.setSystemId(operation.getSystemId());
		serviceCommand.setTenantId(operation.getTenantId());
		serviceCommand.setServiceId(serviceId);
		serviceCommand.setOperationId(operationId);
		serviceCommand.setReqData(reqData);
		serviceCommand.setMultipartFileMap(null);

		// 调用ApplicationSerivceProxy的execute方法，获得返回结果
		applicationServiceProxy.execute(serviceCommand, null);
	}

}
