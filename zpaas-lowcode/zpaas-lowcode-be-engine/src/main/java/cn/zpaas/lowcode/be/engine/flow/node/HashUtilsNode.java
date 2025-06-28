package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.security.MD5Utils;
import cn.zpaas.lowcode.security.SHA256Utils;
import cn.zpaas.lowcode.security.SM3Utils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.security.BCryptUtils;

/**
 * @author zjy
 *         HashUtilsNode是哈希能力工具节点的实现类，主要提供基于Hash加密以及密码正确性验证，至少需要支持MD5、SHA256以及BCrypt三种算法
 */
public class HashUtilsNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(HashUtilsNode.class);

	private static final String CFG_HASH_TYPE_KEY = "hashType"; // 哈希类型存放的Key
	private static final String CFG_OPERATE_TYPE_KEY = "operateType"; // 操作类型存放的Key

	private static final String CFG_RAW_STRING_SOURCE_KEY = "rawStringSource"; // 明文的来源存放的Key
	private static final String CFG_RAW_STRING_KEY_KEY = "rawStringKey"; // 明文的Key值存放的Key
	private static final String CFG_RAW_STRING_ATTR_KEY = "rawStringAttr"; // 当明文是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_ENCODED_STRING_SOURCE_KEY = "encodedStringSource"; // 目标文件名的来源存放的Key
	private static final String CFG_ENCODED_STRING_KEY_KEY = "encodedStringKey"; // 目标文件名的Key值存放的Key
	private static final String CFG_ENCODED_STRING_ATTR_KEY = "encodedStringAttr"; // 当目标文件名是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String HASH_TYPE_MD5 = "M"; // MD5
	private static final String HASH_TYPE_SHA256 = "S"; // SHA256
	private static final String HASH_TYPE_BCRYPT = "B"; // BCrypt
	private static final String HASH_TYPE_SM3 = "G"; // SM3

	private static final String OPERATE_TYPE_ENCODE = "E"; // 加密
	private static final String OPERATE_TYPE_VALIDATE = "V"; // 验证

	
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

		String hashType = JsonUtils.getString(nodeCfg, CFG_HASH_TYPE_KEY);
		String operateType = JsonUtils.getString(nodeCfg, CFG_OPERATE_TYPE_KEY);

		String rawStringSource = JsonUtils.getString(nodeCfg, CFG_RAW_STRING_SOURCE_KEY);
		String rawStringKey = JsonUtils.getString(nodeCfg, CFG_RAW_STRING_KEY_KEY);
		String rawStringAttr = JsonUtils.getString(nodeCfg, CFG_RAW_STRING_ATTR_KEY);

		String encodedStringSource = JsonUtils.getString(nodeCfg, CFG_ENCODED_STRING_SOURCE_KEY);
		String encodedStringKey = JsonUtils.getString(nodeCfg, CFG_ENCODED_STRING_KEY_KEY);
		String encodedStringAttr = JsonUtils.getString(nodeCfg, CFG_ENCODED_STRING_ATTR_KEY);

		// 配置参数合法性校验
		if (StringUtils.isBlank(hashType) || StringUtils.isBlank(operateType)) {
			logger.error("T[{}] hashType operateType can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "hashType operateType can't be null. ");
		}
		if (StringUtils.isBlank(rawStringSource) || StringUtils.isBlank(rawStringKey)) {
			logger.error("T[{}] rawStringSource and rawStringKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "rawStringSource and rawStringKey can't be null. ");
		}
		if (OPERATE_TYPE_VALIDATE.equals(operateType) && (StringUtils.isBlank(encodedStringSource) || StringUtils.isBlank(encodedStringKey))) {
			logger.error("T[{}] when validate, encodedStringSource and encodedStringKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when validate, encodedStringSource and encodedStringKey can't be null. ");
		}
		// 动态属性的情况
		if (!StringUtils.isBlank(rawStringAttr)) {
			rawStringAttr = this.dynamicAttrProcess(rawStringAttr, businessFlowNode, context);
		}
		// 获取明文
		String rawString = (String) getContextObject(rawStringSource, rawStringKey, false, rawStringAttr, context,
				businessFlowNode);

		Object result = null;

		if (StringUtils.isBlank(rawString)) {
			logger.error("T[{}] rawString is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "rawString is null");
		}
		if (OPERATE_TYPE_VALIDATE.equals(operateType)) {// 验证
			if (!StringUtils.isBlank(encodedStringAttr)) {
				encodedStringAttr = this.dynamicAttrProcess(encodedStringAttr, businessFlowNode, context);
			}
			// 获取密文
			String encodedString = (String) getContextObject(encodedStringSource, encodedStringKey, false,
					encodedStringAttr, context, businessFlowNode);
			if (StringUtils.isBlank(encodedString)) {
				logger.error("T[{}] encodedString is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "encodedString is null");
			}
			if (HASH_TYPE_BCRYPT.equals(hashType)) {// BCRYPT
				result = BCryptUtils.isValid(encodedString, rawString);
			} else if (HASH_TYPE_SHA256.equals(hashType)) {// SHA256
				result = SHA256Utils.isValid(encodedString, rawString);
			} else if (HASH_TYPE_MD5.equals(hashType)) {// MD5
				result = MD5Utils.isValid(encodedString, rawString);
			} else if (HASH_TYPE_SM3.equals(hashType)) {// SM3
				result = SM3Utils.isValid(encodedString, rawString);
			} else {
				logger.error("T[{}] invalid hashType {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), hashType, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid hashType");
			}
		} else if (OPERATE_TYPE_ENCODE.equals(operateType)) {// 加密
			if (HASH_TYPE_BCRYPT.equals(hashType)) {// BCRYPT
				result = BCryptUtils.encode(rawString);
			} else if (HASH_TYPE_SHA256.equals(hashType)) {// SHA256
				result = SHA256Utils.encode(rawString);
			} else if (HASH_TYPE_MD5.equals(hashType)) {// MD5
				result = MD5Utils.encode(rawString);
			} else if (HASH_TYPE_SM3.equals(hashType)) {// SM3
				result = SM3Utils.encode(rawString);
			} else {
				logger.error("T[{}] invalid hashType {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), hashType, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid hashType");
			}

		} else {
			logger.error("T[{}] invalid operateType {}. businessflowNodeId: {}", businessFlowNode.getTenantId(), operateType, businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operateType");
		}

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
