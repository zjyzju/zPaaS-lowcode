package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.security.SignatureUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.SecurityKeyMgrAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.domain.eo.SecurityKeyResource;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy
 *         SignatureUtilsNode是签名/验签节点的实现类，主要提供非对称签名/验签能力
 */
public class SignatureUtilsNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(SignatureUtilsNode.class);

	private static final String CFG_CIPHER_ALGORITHM_TYPE_KEY = "cipherAlgorithmType"; // 算法类型存放的Key
	private static final String CFG_CIPHER_ALGORITHM_KEY = "cipherAlgorithm"; // 算法存放的Key
	private static final String CFG_SECURITY_KEY_RESOURCE_ID_KEY = "securityKeyResourceId"; // 安全密钥资源标识存放的Key
	private static final String CFG_OPERATION_TYPE_KEY = "operationType"; // 操作类型存放的Key

	private static final String CFG_SRC_TEXT_SOURCE_KEY = "srcTextSource"; // 源文本存放的Key
	private static final String CFG_SRC_TEXT_KEY_KEY = "srcTextKey"; // 源文本的Key值存放的Key
	private static final String CFG_SRC_TEXT_ATTR_KEY = "srcTextAttr"; // 当源文本是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String CFG_SIGN_TEXT_SOURCE_KEY = "signTextSource"; // 签名文本存放的Key
	private static final String CFG_SIGN_TEXT_KEY_KEY = "signTextKey"; // 签名文本的Key值存放的Key
	private static final String CFG_SIGN_TEXT_ATTR_KEY = "signTextAttr"; // 当签名文本是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String OPERATION_TYPE_SIGN = "S"; // 签名
	private static final String OPERATION_TYPE_VERIFY = "V"; // 验签

	
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

		String cipherAlgorithmType = JsonUtils.getString(nodeCfg, CFG_CIPHER_ALGORITHM_TYPE_KEY);
		String cipherAlgorithm = JsonUtils.getString(nodeCfg, CFG_CIPHER_ALGORITHM_KEY);
		String securityKeyResourceId = JsonUtils.getString(nodeCfg, CFG_SECURITY_KEY_RESOURCE_ID_KEY);
		String operationType = JsonUtils.getString(nodeCfg, CFG_OPERATION_TYPE_KEY);

		String srcTextSource = JsonUtils.getString(nodeCfg, CFG_SRC_TEXT_SOURCE_KEY);
		String srcTextKey = JsonUtils.getString(nodeCfg, CFG_SRC_TEXT_KEY_KEY);
		String srcTextAttr = JsonUtils.getString(nodeCfg, CFG_SRC_TEXT_ATTR_KEY);

		String signTextSource = JsonUtils.getString(nodeCfg, CFG_SIGN_TEXT_SOURCE_KEY);
		String signTextKey = JsonUtils.getString(nodeCfg, CFG_SIGN_TEXT_KEY_KEY);
		String signTextAttr = JsonUtils.getString(nodeCfg, CFG_SIGN_TEXT_ATTR_KEY);

		// 配置参数合法性校验
		if (StringUtils.isBlank(cipherAlgorithmType) || StringUtils.isBlank(cipherAlgorithm) || StringUtils.isBlank(securityKeyResourceId) || StringUtils.isBlank(operationType)) {
			logger.error("T[{}] cipherAlgorithmType cipherAlgorithm securityKeyResourceId and operationType can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "cipherAlgorithmType cipherAlgorithm securityKeyResourceId and operationType can't be null. ");
		}
		if (StringUtils.isBlank(srcTextSource) || StringUtils.isBlank(srcTextKey)) {
			logger.error("T[{}] srcTextSource and srcTextKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcTextSource and srcTextKey can't be null. ");
		}
		if (OPERATION_TYPE_VERIFY.equals(operationType) && (StringUtils.isBlank(signTextSource) || StringUtils.isBlank(signTextKey))) {
			logger.error("T[{}] when operationType is V, signTextSource and signTextKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when operationType is v, signTextSource and signTextKey can't be null. ");
		}

		//获取安全密钥资源
		SecurityKeyResource securityKeyResource = SecurityKeyMgrAbility.getInstance().getSecurityKeyResource(businessFlowNode.getSystemId(), securityKeyResourceId);
		if (securityKeyResource == null) {
			logger.error("T[{}] securityKeyResource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "securityKeyResource can't be null. ");
		}

		// 获取源文本值
		srcTextAttr = this.dynamicAttrProcess(srcTextAttr, businessFlowNode, context);
		String srcText = (String) getContextObject(srcTextSource, srcTextKey, false, srcTextAttr, context, businessFlowNode);

		if (StringUtils.isBlank(srcText)) {
			logger.error("T[{}] srcText is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcText is null");
		}

		// 获取签名文本值
		signTextAttr = this.dynamicAttrProcess(signTextAttr, businessFlowNode, context);
		String signText = (String) getContextObject(signTextSource, signTextKey, false, signTextAttr, context, businessFlowNode);

		if (OPERATION_TYPE_VERIFY.equals(operationType) && StringUtils.isBlank(signText)) {
			logger.error("T[{}] when operationType is v, signText cannot be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "when operationType is v, signText cannot be null");
		}

		Object result = null;

		if(OPERATION_TYPE_SIGN.equals(operationType)) {//签名
			if(securityKeyResource.getPrivateKeyFile() == null) {
				logger.error("T[{}] privateKeyFile is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "privateKeyFile is null");
			}
			result = SignatureUtils.sign(srcText, securityKeyResource.getPrivateKeyFile(), securityKeyResource.getPrivateKeyPwd(),"utf-8", cipherAlgorithm);
		}else if(OPERATION_TYPE_VERIFY.equals(operationType)) {//验签
			if(securityKeyResource.getPublicKeyFile() == null) {
				logger.error("T[{}] publicKeyFile is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "publicKeyFile is null");
			}
			result = SignatureUtils.verifySignature(signText, srcText, securityKeyResource.getPublicKeyFile(),"utf-8", cipherAlgorithm);
		}else {
			logger.error("T[{}] invalid operationType:{}. businessflowNodeId: {}", businessFlowNode.getTenantId(), operationType, businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operationType");
		}

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
