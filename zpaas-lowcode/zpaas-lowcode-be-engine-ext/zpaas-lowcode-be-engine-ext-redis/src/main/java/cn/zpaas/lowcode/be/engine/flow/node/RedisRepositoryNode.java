package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.RedisRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;


/**
 * @author zjy
 * RedisRepositoryNode是Redis存取节点的实现类，主要提供基于redis的各种缓存操作。
 */
public class RedisRepositoryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(RedisRepositoryNode.class);

	private static final String CFG_REDIS_COMMAND_KEY = "redisCommand"; //redisCommand存放的Key
	private static final String CFG_REDIS_RESOURCE_ID_KEY = "redisResourceId"; //redisResourceId存放的Key
	private static final String CFG_USE_OBJECT_SERIALIZER_KEY = "useObjectSerializer"; //useObjectSerializer存放的Key
	private static final String CFG_PARAMS_KEY = "params"; //params存放的Key
	
	private static final String CFG_PARAM_SOURCE_KEY = "paramSource"; //参数对象的来源存放的Key
	private static final String CFG_PARAM_IS_LIST_KEY = "paramIsList"; //参数对象是否是列表的来源存放的Key
	private static final String CFG_PARAM_KEY_KEY = "paramKey"; //参数对象的Key值存放的Key
	private static final String CFG_PARAM_ATTR_PATH_KEY = "paramAttrPath"; //参数对象属性对应的json path存放的Key
	
	
	/*
	 {
		redisCommand：要执行的redis命令，如set、get、ladd、keys。。。，以下命令不允许执行："auth", "quit", "flushdb", "flushall", "select", "save", "bgsave", "shundown", "info", "monitor", "slaveof", "config"。人工输入方式（命令过多）。
		redisResourceId：要操作的redis技术服务资源标识，从类型为redis的技术服务资源中选择
		useObjectSerializer: 是否对返回结果使用对象序列器，true/false

		params：[//参数列表，如果服务端是cluster模式时，第一个参数同时作为sampleKey
			{
				paramSource：参数对象的来源，包括：P（过程数据）；D（领域对象）；I（输入参数）；N（预处理产生的nodeParams）；O（业务流的属主领域对象）；F（固定的值）
				paramIsList：参数对象是否是列表，true/false；
				paramKey：参数对象的Key值，条件对象的来源为I（输入参数）时，表示输入参数中的Key；
					为D（领域对象）时，表示领域对象在context.attributedObjectMap或context.attributedObjectsMap（根据conditionObjectIsList的值进行区分）中的key值；
					为P（过程数据）时，该值表示context.values中的key值，
					当为N（预处理产生的nodeParams）或O（业务流的属主领域对象）时，Key值无效；
					为F（固定值）时，该值为具体的值；
				paramAttrPath：参数对象属性对应的json path，如果是“$”，则表示条件对象本身
	
			}
		……
		]


		isListResult：节点执行结果是否是List类型，包括：true/false
		nodeResultType：节点执行结果对象的类型，包括：JDK原生对象（J）、领域对象（D）、值传递对象（R）
		nodeResultClass：节点执行结果对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）或值传递对象（RO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用默认结构
	 }
	 */
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
		String redisCommand = JsonUtils.getString(nodeCfg, CFG_REDIS_COMMAND_KEY);
		String redisResourceId = JsonUtils.getString(nodeCfg, CFG_REDIS_RESOURCE_ID_KEY);
		boolean useObjectSerializer = JsonUtils.getBoolean(nodeCfg, CFG_USE_OBJECT_SERIALIZER_KEY);
		JsonArray params = JsonUtils.getJsonArray(nodeCfg, CFG_PARAMS_KEY);
		
		//redisCommand、redisResourceId不能为空
		if(StringUtils.isBlank(redisCommand) || StringUtils.isBlank(redisResourceId)) {
			logger.error("T[{}] redisCommand and redisResourceId can't be empty.", businessFlowNode.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "redisCommand and redisResourceId can't be empty.");
		}
		
		if(JsonUtils.isEmpty(params)) {
			logger.error("T[{}] params can't be empty.", businessFlowNode.getTenantId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "params can't be empty.");
		}
		
		//循环处理params
		JsonObject param = null;
		String paramSource = null;
		boolean paramIsList;
		String paramKey = null;
		String paramAttrPath = null;
		List<Object> paramValueList = new ArrayList<>();
		Object paramValue = null;
		for(int i=0; i<params.size(); i++) {
			param = params.get(i).getAsJsonObject();
			//获取参数对象信息
			paramSource = JsonUtils.getString(param, CFG_PARAM_SOURCE_KEY);
			paramIsList = JsonUtils.getBoolean(param, CFG_PARAM_IS_LIST_KEY);
			paramKey = JsonUtils.getString(param, CFG_PARAM_KEY_KEY);
			paramAttrPath = JsonUtils.getString(param, CFG_PARAM_ATTR_PATH_KEY);
			
			if(StringUtils.isBlank(paramSource) || StringUtils.isBlank(paramKey)) {
				logger.error("T[{}] paramSource and paramKey can't be empty. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "paramSource and paramKey can't be empty. ");
			}
			
			//当对象是列表时或固定值时，不能设置valueAttrPath
			if(paramIsList || OBJECT_INSTANCE_SOURCE_F.equals(paramSource)) {
				if(paramAttrPath != null && paramAttrPath.trim().length() > 0) {
					logger.error("T[{}] when paramIsList is true, paramAttrPath can't has value. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when paramIsList is true, paramAttrPath can't has value.");
				}
			}
			if (!StringUtils.isBlank(paramAttrPath)) {
				paramAttrPath = this.dynamicAttrProcess(paramAttrPath, businessFlowNode, context);
			}
			//从上下文获取参数值
			paramValue = getContextObject(paramSource, paramKey, paramIsList, paramAttrPath, context, businessFlowNode);
			
			if(paramValue == null) {
				logger.error("T[{}] paramValue can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "paramValue can't be null.");
			}
			//加入参数值列表
			paramValueList.add(paramValue);
		}
		//执行redis命令
		Object result = RedisRepositoryAbility.getInstance().sendCommand(redisResourceId, context.getSystemId(), 
				useObjectSerializer, redisCommand, paramValueList.toArray());
		
		//将目标对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
