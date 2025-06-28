package cn.zpaas.lowcode.be.engine.flow.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.ORMRepositoryAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.container.AttributedObjectContainer;
import cn.zpaas.lowcode.be.engine.proxy.DomainObjectProxy;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.AttributedObject;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.DomainObject;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 * ORM数据库存取节点实现类
 */
public class ORMRepositoryNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(ORMRepositoryNode.class);

	public static final String CFG_IS_LIST_TYPE_KEY = "isListType"; //领域对象是否列表类型存放的Key
	public static final String CFG_DOMAIN_OBJECT_SOURCE_KEY = "domainObjectSource"; //领域对象实例来源存放的Key
	public static final String CFG_DOMAIN_OBJECT_KEY = "domainObjectKey"; //领域对象实例存放的Key
	public static final String CFG_DOMAIN_OBJECT_ID_KEY = "domainObjectId"; //领域对象标识 存放的Key
	
	public static final String CFG_ORM_ID_KEY = "ormId"; //ORM标识存放的Key
	public static final String CFG_REPOSITORY_OPERATION_KEY = "repositoryOperation"; //要执行的数据库操作存放的Key
	public static final String CFG_PAGE_FLAG_KEY = "pageFlag"; //是否支持分页存放的Key
	public static final String CFG_QUERY_OPTIONS_KEY = "queryOptions"; //查询选项存放的Key
	
	public static final String REPOSITORY_OPERATION_INSERT = "I"; //insert操作
	public static final String REPOSITORY_OPERATION_BATCH_INSERT = "BI"; //批量insert操作
	public static final String REPOSITORY_OPERATION_UPDATE_BY_PK = "UK"; //updateByPK操作
	public static final String REPOSITORY_OPERATION_UPDATE_BY_PK_SELECTIVE = "UKS"; //updateByPKSelective操作
	public static final String REPOSITORY_OPERATION_DELETE_BY_PK = "DK"; //deleteByPK操作
	public static final String REPOSITORY_OPERATION_QUERY_BY_PK = "QK"; //queryByPK操作
	public static final String REPOSITORY_OPERATION_DELETE_BY_CONDITION = "DC"; //deleteByCondition操作
	public static final String REPOSITORY_OPERATION_QUERY_BY_CONDITION = "QC"; //queryByCondition操作
	public static final String REPOSITORY_OPERATION_SAVE = "S"; //save操作
	
	
	@Override
	/**
	 * 该节点类型的业务处理方法，参数为业务流节点信息和业务流上下文对象
	 * @param businessFlowNode 业务流节点信息
	 * @param context          业务流上下文对象
	 */
	public void process(BusinessFlowNode businessFlowNode, BusinessFlowContext context)  throws EngineException{
		//获取节点处理配置信息
		String nodeCfgString = businessFlowNode.getNodeCfg();
		//如果为空，则直接报错
		if(StringUtils.isBlank(nodeCfgString)) {
			logger.error("T[{}] node cfg is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "node cfg is null.");
		}
		
		//获取配置信息
		JsonObject nodeCfg = JsonUtils.toJsonObject(nodeCfgString);
		boolean isListType = JsonUtils.getBoolean(nodeCfg, CFG_IS_LIST_TYPE_KEY);
		String domainObjectSource = JsonUtils.getString(nodeCfg, CFG_DOMAIN_OBJECT_SOURCE_KEY);
		String domainObjectKey = JsonUtils.getString(nodeCfg, CFG_DOMAIN_OBJECT_KEY);
		String domainObjectId = JsonUtils.getString(nodeCfg, CFG_DOMAIN_OBJECT_ID_KEY);
		
		String ormId = JsonUtils.getString(nodeCfg, CFG_ORM_ID_KEY);
		String repositoryOperation	= JsonUtils.getString(nodeCfg, CFG_REPOSITORY_OPERATION_KEY);
		boolean pageFlag = JsonUtils.getBoolean(nodeCfg, CFG_PAGE_FLAG_KEY);
		String queryOptionsStr	= JsonUtils.getString(nodeCfg, CFG_QUERY_OPTIONS_KEY);
		//domainObjectSource不能为空
		if(StringUtils.isBlank(domainObjectSource)) {
			logger.error("T[{}] domainObjectSource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "domainObjectSource can't be null.");
		}
		//ormId不能为空
		if(StringUtils.isBlank(ormId)) {
			logger.error("T[{}] ormId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ormId can't be null.");
		}
		//repositoryOperation不能为空
		if(StringUtils.isBlank(repositoryOperation)) {
			logger.error("T[{}] repositoryOperation can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "repositoryOperation can't be null.");
		}
		JsonObject queryOptions = null;
		if(!StringUtils.isBlank(queryOptionsStr)) {
			queryOptions = JsonUtils.toJsonObject(queryOptionsStr);
		}
		
		if(isListType) {//针对列表的数据库操作
			//列表批量操作，仅对I（插入操作）、DK（按主键删除操作）、UK（按主键更新操作）、S（保存操作）有效
			if(REPOSITORY_OPERATION_QUERY_BY_CONDITION.equals(repositoryOperation) ||
					REPOSITORY_OPERATION_DELETE_BY_CONDITION.equals(repositoryOperation) ||
					REPOSITORY_OPERATION_QUERY_BY_PK.equals(repositoryOperation)) {
				logger.error("T[{}] the repositoryOperation{} doesn't support List operation.", businessFlowNode.getTenantId(), repositoryOperation);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "the repositoryOperation doesn't support List operation.");
			}
			//列表批量操作，仅对domainObjectSource为I（输入参数）、D（领域对象）有效
			if(!OBJECT_INSTANCE_SOURCE_I.equals(domainObjectSource) && !OBJECT_INSTANCE_SOURCE_D.equals(domainObjectSource)) {
				logger.error("T[{}] only domainObjectSource(I、D) support List operation. repositoryOperation is: {}", businessFlowNode.getTenantId(), repositoryOperation);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "only domainObjectSource(I、D) support List operation.");
			}
			//当domainObjectSource为I（输入参数）、D（领域对象）时，domainObjectKey不能为空
			if(StringUtils.isBlank(domainObjectKey))  {
				logger.error("T[{}] when domainObjectSource is I or D, domainObjectKey can't be null.", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "when domainObjectSource is I or D, domainObjectKey can't be null.");
			}
			
			List<AttributedObjectContainer> domainObjectList = null; //isListType为true时使用
			
			//当domainObjectSource为I（输入参数）
			if(OBJECT_INSTANCE_SOURCE_I.equals(domainObjectSource)) {
				if(StringUtils.isBlank(domainObjectId)) {
					logger.error("T[{}] when domainObjectSource is I, domainObjectId can't be null!", businessFlowNode.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when domainObjectSource is I, domainObjectId can't be null !");
				}
				DomainObject domainObj = DomainObjectProxy.getInstance().getDomainObject(context.getSystemId(), domainObjectId);
				if(domainObj == null) {
					logger.error("T[{}] can't find valid DomainObject. {}", businessFlowNode.getTenantId(), domainObjectId);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject.");
				}
				//从输入参数中获取数据
				JsonArray domainValues = JsonUtils.getJsonArray(context.getReqData(), domainObjectKey);
				
				//取不到领域对象数据对象的时候报错
				if(JsonUtils.isEmpty(domainValues)) {
					logger.error("T[{}] can't find valid DomainObject's data.  {}", businessFlowNode.getTenantId(), domainObjectId);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject's data!");
				}
				//创建领域对象列表
				domainObjectList = new ArrayList<>();
				JsonObject row = null;
				AttributedObjectContainer domainObjectContainer = null;
				for(int i=0; i<domainValues.size(); i++) {
					row = domainValues.get(i).getAsJsonObject();
					//格式错误的时候报错
					if(domainValues == null || !AttributedObject.validateAttributedObject(domainObj, row)) {
						logger.error("T[{}] can't find valid DomainObject's data. {}", businessFlowNode.getTenantId(), domainObjectId);
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject's data.");
					}
					domainObjectContainer = new AttributedObjectContainer();
					domainObjectContainer.setAttributedObject(domainObj);
					domainObjectContainer.setObjectValues(row);
					domainObjectList.add(domainObjectContainer);
				}
			}else {//当domainObjectSource为 D（领域对象）时，从领域对象多值Map中获取
				domainObjectList = context.getAttributedObjects(domainObjectKey);
			}
			//要进行数据库操作的列表为空时，报错
			if(CollectionUtils.isEmpty(domainObjectList)) {
				logger.error("T[{}] domainObjectList is null.", businessFlowNode.getTenantId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "domainObjectList is null.");
			}
			
			//根据配置的操作方法，执行相应的方法并获得结果
			JsonArray result = new JsonArray();
			Object resultRow = null;
			switch (repositoryOperation) {
				case REPOSITORY_OPERATION_INSERT:
					for(AttributedObjectContainer domainObject: domainObjectList) {
						resultRow = ORMRepositoryAbility.getInstance().insert(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
						result.add(JsonUtils.toJsonElement(resultRow));
					}
					break;
					
				case REPOSITORY_OPERATION_BATCH_INSERT:
					int[] batchResult = ORMRepositoryAbility.getInstance().batchInsert(domainObjectList, context.getSystemId(), ormId, context.getDbSchemaId());
					if(batchResult != null && batchResult.length > 0) {
						result.addAll(JsonUtils.toJsonArray(Arrays.asList(batchResult)));
					}
					break;
				
				case REPOSITORY_OPERATION_UPDATE_BY_PK:
					for(AttributedObjectContainer domainObject: domainObjectList) {
						resultRow = ORMRepositoryAbility.getInstance().updateByPK(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
						result.add(JsonUtils.toJsonElement(resultRow));
					}
					break;
					
				case REPOSITORY_OPERATION_UPDATE_BY_PK_SELECTIVE:
					for(AttributedObjectContainer domainObject: domainObjectList) {
						resultRow = ORMRepositoryAbility.getInstance().updateByPKSelective(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
						result.add(JsonUtils.toJsonElement(resultRow));
					}
					break;
					
				case REPOSITORY_OPERATION_DELETE_BY_PK:
					for(AttributedObjectContainer domainObject: domainObjectList) {
						resultRow = ORMRepositoryAbility.getInstance().deleteByPK(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
						result.add(JsonUtils.toJsonElement(resultRow));
					}
					break;
						
				case REPOSITORY_OPERATION_SAVE:
					for(AttributedObjectContainer domainObject: domainObjectList) {
						resultRow = ORMRepositoryAbility.getInstance().save(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
						result.add(JsonUtils.toJsonElement(resultRow));
					}
					break;
					
				default :
					logger.error("T[{}] invalid repositoryOperation value. {}", businessFlowNode.getTenantId(), repositoryOperation);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid repositoryOperation value.");
			}
			//将执行结果作为节点的执行结果
			context.setAttribute(NODE_RESULT_KEY, result);
		}else {
			AttributedObjectContainer domainObject = null;//isListType为false时使用
			//当domainObjectSource为I（输入参数）或N（预处理产生的nodeParams）
			if(OBJECT_INSTANCE_SOURCE_N.equals(domainObjectSource) || OBJECT_INSTANCE_SOURCE_I.equals(domainObjectSource)) {
				if(StringUtils.isBlank(domainObjectId)) {
					logger.error("T[{}] when domainObjectSource is I, domainObjectId can't be null.", businessFlowNode.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when domainObjectSource is I, domainObjectId can't be null.");
				}
				DomainObject domainObj = DomainObjectProxy.getInstance().getDomainObject(context.getSystemId(), domainObjectId);
				if(domainObj == null) {
					logger.error("T[{}] can't find valid DomainObject. {}", businessFlowNode.getTenantId(), domainObjectId);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject.");
				}
				domainObject = new AttributedObjectContainer();
				domainObject.setAttributedObject(domainObj);
				JsonObject domainValues = null;
				if(OBJECT_INSTANCE_SOURCE_N.equals(domainObjectSource)) {
					domainValues = (JsonObject)context.getAttribute(NODE_PARAM_KEY);
				}else {
					//当domainObjectSource为I（输入参数）时，domainObjectKey不能为空
					if(StringUtils.isBlank(domainObjectKey))  {
						logger.error("T[{}] when domainObjectSource is I, domainObjectKey can't be null.", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.INTERNAL_ERROR, "when domainObjectSource is I, domainObjectKey can't be null.");
					}
					domainValues = JsonUtils.getJsonObject(context.getReqData(), domainObjectKey);
				}
				
				//取不到领域对象数据对象或者格式错误的时候报错
				if(domainValues == null || !AttributedObject.validateAttributedObject(domainObj, domainValues)) {
					logger.error("T[{}] can't find valid DomainObject's data. {}", businessFlowNode.getTenantId(), domainObjectId);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject's data.");
				}				
				domainObject.setObjectValues(domainValues);
			}else if(OBJECT_INSTANCE_SOURCE_O.equals(domainObjectSource)) {//当domainObjectSource为O（业务流属主领域对象）
				domainObject = context.getDomainObject();
			}else if(OBJECT_INSTANCE_SOURCE_D.equals(domainObjectSource)){//当domainObjectSource为D（领域对象）
				//当domainObjectSource为D（领域对象）时，domainObjectKey不能为空
				if(StringUtils.isBlank(domainObjectKey))  {
					logger.error("T[{}] when domainObjectSource is D, domainObjectKey can't be null.", businessFlowNode.getTenantId());
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "when domainObjectSource is D, domainObjectKey can't be null.");
				}
				domainObject = context.getAttributedObject(domainObjectKey);
				
			}else {
				logger.error("T[{}] invalid domainObjectSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid domainObjectSource.");
			}
			
			if(domainObject == null) {
				logger.error("T[{}] can't find valid DomainObject instance. Key:{}", businessFlowNode.getTenantId(), domainObjectKey);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "can't find valid DomainObject instance.");
			}
			
			//根据配置的操作方法，执行相应的方法并获得结果
			Object resultObject = null;
			switch (repositoryOperation) {
				case REPOSITORY_OPERATION_INSERT:
					resultObject = ORMRepositoryAbility.getInstance().insert(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
				
				case REPOSITORY_OPERATION_BATCH_INSERT:
					List<AttributedObjectContainer> domainObjects = new ArrayList<>();
					domainObjects.add(domainObject);
					int[] batchResult = ORMRepositoryAbility.getInstance().batchInsert(domainObjects, context.getSystemId(), ormId, context.getDbSchemaId());
					JsonArray result = new JsonArray();
					if(batchResult != null && batchResult.length > 0) {
						result.addAll(JsonUtils.toJsonArray(Arrays.asList(batchResult)));
					}
					resultObject = result;
					break;
					
				case REPOSITORY_OPERATION_UPDATE_BY_PK:
					resultObject = ORMRepositoryAbility.getInstance().updateByPK(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				case REPOSITORY_OPERATION_UPDATE_BY_PK_SELECTIVE:
					resultObject = ORMRepositoryAbility.getInstance().updateByPKSelective(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				case REPOSITORY_OPERATION_DELETE_BY_PK:
					resultObject = ORMRepositoryAbility.getInstance().deleteByPK(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				case REPOSITORY_OPERATION_QUERY_BY_PK:
					resultObject = ORMRepositoryAbility.getInstance().queryByPK(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				case REPOSITORY_OPERATION_QUERY_BY_CONDITION:
					resultObject = ORMRepositoryAbility.getInstance().queryByCondition(domainObject, context.getSystemId(), ormId, context.getDbSchemaId(), pageFlag, queryOptions);
					break;
					
				case REPOSITORY_OPERATION_DELETE_BY_CONDITION:
					resultObject = ORMRepositoryAbility.getInstance().deleteByCondition(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				case REPOSITORY_OPERATION_SAVE:
					resultObject = ORMRepositoryAbility.getInstance().save(domainObject, context.getSystemId(), ormId, context.getDbSchemaId());
					break;
					
				default :
					logger.error("T[{}] invalid repositoryOperation value. {}", businessFlowNode.getTenantId(), repositoryOperation);
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "invalid repositoryOperation value.");
			}
			//将执行结果作为节点的执行结果
			context.setAttribute(NODE_RESULT_KEY, resultObject);
		}
	}

}
