package cn.zpaas.lowcode.be.engine.flow;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.domain.eo.BusinessFlow;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * QueryBusinessFlow类是BusinessFlowTemplate类的子类，主要实现查询类业务流模板的业务逻辑。
 * 查询类业务流模板只有两个业务流节点，按顺序为参数校验节点和SQL查询节点，其中参数校验节点可以不配置。
 */
public class QueryBusinessFlow extends BusinessFlowTemplate {
	@Override
	public JsonObject execute(BusinessFlow businessFlow, BusinessFlowContext context) throws EngineException {
		// 已废弃，统一用复杂业务流
		return null;
	}
}
