package cn.zpaas.lowcode.be.engine.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.flow.node.Node;
import cn.zpaas.lowcode.be.engine.service.EngineInitService;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.domain.eo.FlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * 业务流实现模板的基类
 */
public abstract class BusinessFlowTemplate {
	private static Logger logger = LoggerFactory.getLogger(BusinessFlowTemplate.class);
	//用于存放节点的具体业务实现类实例
	private static Map<String, Node> nodeMap = new HashMap<>();
	
	/**
	 * 用于初始化BusinessFlowTemplate类
	 * @param initService 获取初始化数据的服务
	 */
	public static void init(String systemId, String tenantId, EngineInitService initService) {
		if (logger.isDebugEnabled()) {
			logger.debug("init method start...");
		}
		//加载FlowNode并创建Node实例
		List<FlowNode> flowNodes = initService.listFlowNodes();
		if(!CollectionUtils.isEmpty(flowNodes)) {
			Node node = null;
			for(FlowNode flowNode : flowNodes) {
				if(!StringUtils.isBlank(systemId) && !StringUtils.isBlank(flowNode.getSystemId()) &&
						!systemId.equals(flowNode.getSystemId())) {//非公共节点以及当前业务系统的节点不加载
					continue;
				}
				if(!StringUtils.isBlank(flowNode.getNodeClass())) {
					try {
						node = (Node)Class.forName(flowNode.getNodeClass()).getDeclaredConstructor().newInstance();
						node.setFlowNode(flowNode);
						nodeMap.put(flowNode.getId(), node);
					} catch (Exception e) {
						logger.error("Init FlowNode's nodeClass failed: {}", flowNode.getNodeClass());
					} 
				} else {
					logger.error("FlowNode's nodeClass is null: {}", flowNode);
				}
			}
		}else {
			logger.error("No invalid FlowNode is found!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("init method end...");
		}
	}
	
	/**
	 * 根据flowNodeId获取Node信息
	 * @param flowNodeId 节点标识
	 * @return 返回Node对象
	 */
	public static Node getNode(String flowNodeId) {
		return nodeMap.get(flowNodeId);
	}
	
	/**
	 * 业务流实现模板的具体业务实现方法，一般由子类实现业务逻辑
	 * @param businessFlow 业务流实例
	 * @param context 业务流上下文对象实例
	 * @return 返回业务流执行结果
	 */
	public abstract Object execute(BusinessFlow businessFlow, BusinessFlowContext context) throws EngineException;

}
