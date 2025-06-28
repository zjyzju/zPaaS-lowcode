package cn.zpaas.lowcode.be.engine.flow;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.flow.node.BreakNode;
import cn.zpaas.lowcode.be.engine.flow.node.ForEachNode;
import cn.zpaas.lowcode.be.engine.flow.node.LocalInvokeNode;
import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.be.engine.flow.node.ORMRepositoryNode;
import cn.zpaas.lowcode.be.engine.flow.node.ObjectSetterNode;
import cn.zpaas.lowcode.be.engine.flow.node.ReturnNode;
import cn.zpaas.lowcode.be.engine.flow.node.TransferNode;
import cn.zpaas.lowcode.be.engine.flow.node.ValidateNode;
import cn.zpaas.lowcode.constant.FlowType;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 简单业务流模板实现类SimpleBusinessFlow类是BusinessFlowTemplate类的子类，主要实现常用增删改查类业务流模板的业务逻辑。
 * 简单业务流模板只有三个业务流节点，第一个节点固化为参数校验节点（可不配置），第二个节点固化为参数转换节点（可不配置），
 * 第三个节点为ORM数据库存取节点或本地方法调用节点（必须配置其中一个）。
 */
public class SimpleBusinessFlow extends BusinessFlowTemplate {
	private static Logger logger = LoggerFactory.getLogger(SimpleBusinessFlow.class);
	
	@Override
	/**
	 * 简单业务流模板实现类的具体业务实现方法
	 * @param businessFlow 业务流实例
	 * @param context 业务流上下文对象实例
	 * @return 返回业务流执行结果
	 */
	public Object execute(BusinessFlow businessFlow, BusinessFlowContext context) throws EngineException {
		if(logger.isDebugEnabled()) {
			logger.debug("SimpleBusinessFlow is started.");
		}
		//判断businessFlow中的flowType属性值是否是简单业务流“S”，如果不是的话，直接返回错误。
		if(!FlowType.SIMPLE_BUSINESS_FLOW.equals(businessFlow.getFlowType())) {
			logger.error("The business flow's flowType is not SimpleBusinessFlow: {}", businessFlow.getFlowType());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "The business flow's flowType is not SimpleBusinessFlow");
		}
		
		//判断businessFlow中的flowNodes属性中的元素个数，如果数量不是小于等于三个的话，直接返回错误。如果只有一个元素的情况下，
		//该businessFlowNode的flowNodeId必须是ORM数据库存取节点或本地方法调用节点；如果是两个元素的情况下，
		//第一个businessFlowNode的flowNodeId必须是参数校验节点或参数转换节点，第二个businessFlowNode的flowNodeId必须是ORM数据库存取节点或本地方法调用节点；
		//如果是三个元素的情况下，第一个节点必须参数校验节点，第二个节点必须是参数转换节点，第三个节点必须是ORM数据库存取节点或本地方法调用节点；否则的话直接返回错误。
		List<BusinessFlowNode> businessFlowNodes = businessFlow.getBusinessFlowNodes();
		if(businessFlowNodes == null || businessFlowNodes.size() <=0 || businessFlowNodes.size() > 3) {
			logger.error("The business flow's businessFlowNodes size must between: 1 and 3");
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "The business flow's businessFlowNodes size must between: 1 and 3");
		}
		
		//根据业务流节点数量的不同，进行业务逻辑判断
		BusinessFlowNode businessFlowNode = null;
		Node node = null;
		switch(businessFlowNodes.size()) {
			case 3://业务流只包含3个节点
				//第一个节点必须是参数校验节点
				businessFlowNode = businessFlowNodes.get(0);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ValidateNode.class.getName().equals(node.getFlowNode().getNodeClass()) 
						&& !TransferNode.class.getName().equals(node.getFlowNode().getNodeClass())
						&& !ObjectSetterNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The first node must be ValidateNode、ObjectSetterNode or TransferNode, where there are 3 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The first node must be ValidateNode or TransferNode, where there are 2 nodes.");
				}
				
				//第二个节点必须是参数转换节点
				businessFlowNode = businessFlowNodes.get(1);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ValidateNode.class.getName().equals(node.getFlowNode().getNodeClass()) 
						&& !TransferNode.class.getName().equals(node.getFlowNode().getNodeClass())
						&& !ObjectSetterNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The first node must be ValidateNode、ObjectSetterNode or TransferNode, where there are 3 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The first node must be ValidateNode or TransferNode, where there are 2 nodes.");
				}
				
				//第三个节点必须是ORM数据库存取节点
				businessFlowNode = businessFlowNodes.get(2);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ORMRepositoryNode.class.getName().equals(node.getFlowNode().getNodeClass()) &&
						!LocalInvokeNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The third node must be ORMRepositoryNode or LocalInvokeNode, where there are 3 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The third node must be ORMRepositoryNode, where there are 3 nodes.");
				}
				break;
				
			case 2://业务流只包含2个节点
				//第一个节点必须是参数转换节点
				businessFlowNode = businessFlowNodes.get(0);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ValidateNode.class.getName().equals(node.getFlowNode().getNodeClass()) 
						&& !TransferNode.class.getName().equals(node.getFlowNode().getNodeClass())
						&& !ObjectSetterNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The first node must be ValidateNode、ObjectSetterNode or TransferNode, where there are 2 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The first node must be ValidateNode or TransferNode, where there are 2 nodes.");
				}
				
				//第二个节点必须是ORM数据库存取节点
				businessFlowNode = businessFlowNodes.get(1);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ORMRepositoryNode.class.getName().equals(node.getFlowNode().getNodeClass()) &&
						!LocalInvokeNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The second node must be ORMRepositoryNode or LocalInvokeNode, where there are 2 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The second node must be ORMRepositoryNode  or LocalInvokeNode, where there are 3 nodes.");
				}
				break;
				
			default://业务流只包含一个节点
				//第一个节点必须是ORM数据库存取节点
				businessFlowNode = businessFlowNodes.get(0);
				node = getNode(businessFlowNode.getFlowNodeId());
				if(!ORMRepositoryNode.class.getName().equals(node.getFlowNode().getNodeClass()) &&
						!LocalInvokeNode.class.getName().equals(node.getFlowNode().getNodeClass())) {
					logger.error("The first node must be ORMRepositoryNode or LocalInvokeNode, where there are 1 nodes. {}", node.getFlowNode().getNodeClass());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "The first node must be ORMRepositoryNode, where there are 1 nodes.");
				}
		}
		
		//循环执行每个业务流节点的业务逻辑
		for(BusinessFlowNode businessFlowNode1 : businessFlowNodes) {
			businessFlowNode1.setBusinessFlow(businessFlow);
			//从缓存中获取节点实现类
			node = getNode(businessFlowNode1.getFlowNodeId());
			//执行节点预处理方法
			node.preProcess(businessFlowNode1, context);
			//执行节点处理方法
			node.process(businessFlowNode1, context);
			//执行节点后处理方法
			node.postProcess(businessFlowNode1, context);
			//如果是forEach的子业务流，判断中断标志并中断业务流执行
			if(businessFlow.getParentFlowId() != null && context.getAttribute(ForEachNode.FOR_EACH_SUB_BUSINESS_FLOW_KEY) != null && 
					context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY) != null) {
				if(((Integer)context.getAttribute(BreakNode.CFG_BREAK_TIMES_KEY)) > 0) {
					break;
				}
			}
			//判断业务流返回标志并中断业务流
			if(context.getAttribute(ReturnNode.CFG_BUSINESS_FLOW_RETURN_KEY) != null) {
				break;
			}
			//当需要中断业务流执行时，抛出业务错误异常
			if(context.isInterrupted()) {
				throw new EngineException(ResultStatus.BUSINESS_ERROR, context.getMessage());
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("SimpleBusinessFlow is finished.");
		}
		//返回resultData
		return context.getResulteData();
	}
}
