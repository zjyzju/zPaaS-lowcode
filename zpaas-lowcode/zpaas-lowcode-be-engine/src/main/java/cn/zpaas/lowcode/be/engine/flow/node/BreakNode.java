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
 * BreakNode是中断节点的实现类，主要用于中断ForEachNode的循环
 */
public class BreakNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(BreakNode.class);

	public static final String CFG_BREAK_TIMES_KEY = "breakTimes"; //中断循环的层次数存放的Key
		
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
		Integer breakTimes = JsonUtils.getInteger(nodeCfg, CFG_BREAK_TIMES_KEY);
		if(breakTimes == null || breakTimes == 0) {
			breakTimes = 1;
		}
		//如果已经存在breakTimes，则增加本次的值
		if(context.getAttribute(CFG_BREAK_TIMES_KEY) != null) {
			Integer timesInteger = (Integer) context.getAttribute(CFG_BREAK_TIMES_KEY);
			breakTimes = breakTimes + timesInteger;
		}
		//设置中断标志
		context.setAttribute(CFG_BREAK_TIMES_KEY, breakTimes);
	}
}
