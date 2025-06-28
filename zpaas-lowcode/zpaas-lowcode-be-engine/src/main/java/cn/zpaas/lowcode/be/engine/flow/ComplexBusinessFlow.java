package cn.zpaas.lowcode.be.engine.flow;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.flow.node.BreakNode;
import cn.zpaas.lowcode.be.engine.flow.node.ForEachNode;
import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.be.engine.flow.node.ReturnNode;
import cn.zpaas.lowcode.constant.FlowType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 复杂业务流模板实现类ComplexBusinessFlow是BusinessFlowTemplate类的子类，主要实现复杂业务流模板的业务逻辑。
 * 复杂业务流模板解析执行的业务流一般由BPMN2.0流程编辑器编辑生成，低代码开发平台支撑的各类业务流节点类型都可以使用，节点的数量没有限制，也支持子业务流的嵌套。
 */
public class ComplexBusinessFlow extends BusinessFlowTemplate {
	private static Logger logger = LoggerFactory.getLogger(ComplexBusinessFlow.class);
	/**
	 * 复杂业务流模板实现类的具体业务实现方法
	 * @param businessFlow 业务流实例
	 * @param context 业务流上下文对象实例
	 * @return 返回业务流执行结果
	 */
	@Override
	public Object execute(BusinessFlow businessFlow, BusinessFlowContext context) throws EngineException {
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] ComplexBusinessFlow is started. {}-{}", businessFlow.getTenantId(), businessFlow.getId(), businessFlow.getName());
		}
		
		
		//判断businessFlow中的flowType属性值是否是简单业务流“S”，如果不是的话，直接返回错误。
		if(!FlowType.COMPLEX_BUSINESS_FLOW.equals(businessFlow.getFlowType())) {
			logger.error("T[{}] The business flow's flowType is not ComplexBusinessFlow: {}", businessFlow.getTenantId(), businessFlow.getFlowType());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "The business flow's flowType is not ComplexBusinessFlow");
		}
		
		List<BusinessFlowNode> businessFlowNodes = businessFlow.getBusinessFlowNodes();
		if(CollectionUtils.isEmpty(businessFlowNodes)) {
			logger.error("T[{}] The business flow's businessFlowNodes size must greater than 0", businessFlow.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "The business flow's businessFlowNodes size must greater than 0");
		}
		
		Node node = null;		
		//循环执行每个业务流节点的业务逻辑
		for(BusinessFlowNode businessFlowNode : businessFlowNodes) {
			businessFlowNode.setBusinessFlow(businessFlow);
			if(logger.isTraceEnabled()) {
				logger.trace("T[{}] businessFlowNode cfg: {}", businessFlow.getTenantId(), businessFlowNode.getNodeCfg());
			}
			//从缓存中获取节点实现类
			node = getNode(businessFlowNode.getFlowNodeId());
			logger.info("T[{}] businessFlowNode ({})-({})-({}) start.", businessFlow.getTenantId(), businessFlowNode.getId(), node.getFlowNode().getName(), businessFlowNode.getName());
			//执行节点预处理方法
			node.preProcess(businessFlowNode, context);
			//执行节点处理方法
			if (logger.isTraceEnabled()) {
				logger.trace("T[{}] Node's process method start. ({})-({})-({})", businessFlow.getTenantId(), businessFlowNode.getId(), node.getFlowNode().getName(), businessFlowNode.getName());
			}
			node.process(businessFlowNode, context);
			if(logger.isDebugEnabled()) {
				logger.debug("T[{}] nodeResults: {}. {}", businessFlow.getTenantId(), context.getAttribute(Node.NODE_RESULT_KEY), businessFlowNode.getId());
			}
			if (logger.isTraceEnabled()) {
				logger.trace("T[{}] Node's process method finished. {}", businessFlow.getTenantId(), businessFlowNode.getId());
			}
			//执行节点后处理方法
			node.postProcess(businessFlowNode, context);
			logger.info("T[{}] businessFlowNode ({})-({})-({}) finished.", businessFlow.getTenantId(), businessFlowNode.getId(), node.getFlowNode().getName(), businessFlowNode.getName());
			//如果是forEach的子业务流，判断中断标志并中断业务流执行
			if(businessFlow.getParentFlowId() != null && context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null && 
					context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY) != null && ((Integer)context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY)) > 0) {
				logger.info("T[{}] break flag found! break! {}", businessFlow.getTenantId(), businessFlow.getId());
				break;
			}
			//判断业务流返回标志并中断业务流
			if(context.getAttribute(ReturnNode.CFG_BUSINESS_FLOW_RETURN_KEY) != null) {
				logger.info("T[{}] return flag found! return! {}", businessFlow.getTenantId(), businessFlow.getId());
				break;
			}
			//当需要中断业务流执行时，抛出业务错误异常
			if(context.isInterrupted()) {
				logger.info("T[{}] exception flag found! interrupt! {}", businessFlow.getTenantId(), businessFlow.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, context.getMessage());
			}
		}
		if(logger.isTraceEnabled()) {
			logger.trace("T[{}] ComplexBusinessFlow is finished. {}", businessFlow.getTenantId(), businessFlow.getId());
		}
		//返回resultData
		return context.getResulteData();
	
	}
}
