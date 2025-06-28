package cn.zpaas.lowcode.be.engine.flow.node;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * ExceptionNode是异常节点的实现类，主要用于抛出EngineException，可用来回滚事务
 */
public class ExceptionNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ExceptionNode.class);

	public static final String CFG_RESULT_STATUS_KEY = "resultStatus"; //状态存放的Key
	public static final String CFG_MESSAGE_KEY = "message";//详细信息存放的Key
		
	
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * 
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	@Override
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context) throws EngineException {
		// 获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		// 如果为空，则直接报错
		if (StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}

		// 获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		String resultStatus = JsonUtils.getString(nodeCfg, CFG_RESULT_STATUS_KEY);
		String message = JsonUtils.getString(nodeCfg, CFG_MESSAGE_KEY);
		if(StringUtils.isBlank(resultStatus)) {
			logger.error("T[{}] resultStatus is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "resultStatus is null.");
		}
		
		throw new EngineException(resultStatus, message);		
	}
}
