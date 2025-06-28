package cn.zpaas.lowcode.be.engine.flow.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.security.CipherUtils;
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
 *         CipherUtilsNode是加解密能力节点的实现类，主要提供对称/非对称加解密能力
 */
public class CipherUtilsNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(CipherUtilsNode.class);

	private static final String CFG_CIPHER_ALGORITHM_TYPE_KEY = "cipherAlgorithmType"; // 算法类型存放的Key
	private static final String CFG_CIPHER_ALGORITHM_KEY = "cipherAlgorithm"; // 算法存放的Key
	private static final String CFG_SECURITY_KEY_RESOURCE_ID_KEY = "securityKeyResourceId"; // 安全密钥资源标识存放的Key
	private static final String CFG_OPERATION_TYPE_KEY = "operationType"; // 操作类型存放的Key

	private static final String CFG_SRC_TEXT_SOURCE_KEY = "srcTextSource"; // 源文本存放的Key
	private static final String CFG_SRC_TEXT_KEY_KEY = "srcTextKey"; // 源文本的Key值存放的Key
	private static final String CFG_SRC_TEXT_ATTR_KEY = "srcTextAttr"; // 当源文本是指定对象的某个属性时有效，通过该字段指定对应属性的code存放的Key

	private static final String OPERATION_TYPE_ENCRYPT = "E"; // 加密
	private static final String OPERATION_TYPE_DECRYPT = "D"; // 解密

	
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

		// 配置参数合法性校验
		if (StringUtils.isBlank(cipherAlgorithmType) || StringUtils.isBlank(cipherAlgorithm) || StringUtils.isBlank(securityKeyResourceId) || StringUtils.isBlank(operationType)) {
			logger.error("T[{}] cipherAlgorithmType cipherAlgorithm securityKeyResourceId and operationType can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "cipherAlgorithmType cipherAlgorithm securityKeyResourceId and operationType can't be null. ");
		}
		if (StringUtils.isBlank(srcTextSource) || StringUtils.isBlank(srcTextKey)) {
			logger.error("T[{}] srcTextSource and srcTextKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcTextSource and srcTextKey can't be null. ");
		}

		//获取安全密钥资源
		SecurityKeyResource securityKeyResource = SecurityKeyMgrAbility.getInstance().getSecurityKeyResource(businessFlowNode.getSystemId(), securityKeyResourceId);
		if (securityKeyResource == null) {
			logger.error("T[{}] securityKeyResource can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "securityKeyResource can't be null. ");
		}

		// 获取值
		srcTextAttr = this.dynamicAttrProcess(srcTextAttr, businessFlowNode, context);
		String srcText = (String) getContextObject(srcTextSource, srcTextKey, false, srcTextAttr, context, businessFlowNode);

		if (StringUtils.isBlank(srcText)) {
			logger.error("T[{}] srcText is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "srcText is null");
		}

		Object result = null;

		switch(cipherAlgorithmType) {
			case CipherUtils.ALGORITHM_AES:
			case CipherUtils.ALGORITHM_DES:
			case CipherUtils.ALGORITHM_TRIDES:
			case CipherUtils.ALGORITHM_SM4:
				if(StringUtils.isBlank(securityKeyResource.getSecurityKey())) {
					logger.error("T[{}] securityKey is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "securityKey is null");
				}
				if(OPERATION_TYPE_ENCRYPT.equals(operationType)) {
					result = CipherUtils.encrypt(srcText, securityKeyResource.getSecurityKey(), cipherAlgorithm, cipherAlgorithmType);
				}else if(OPERATION_TYPE_DECRYPT.equals(operationType)) {
					result = CipherUtils.decrypt(srcText, securityKeyResource.getSecurityKey(), cipherAlgorithm, cipherAlgorithmType);
				}else {
					logger.error("T[{}] invalid operationType:{}. businessflowNodeId: {}", businessFlowNode.getTenantId(), operationType, businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operationType");
				}
				break;
			
			case SignatureUtils.RSA:
			case SignatureUtils.SM2:
				if(OPERATION_TYPE_ENCRYPT.equals(operationType)) {
					if(securityKeyResource.getPublicKeyFile() == null) {
						logger.error("T[{}] publicKey is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "publicKey is null");
					}
					result = SignatureUtils.encrypt(srcText, securityKeyResource.getPublicKeyFile(), "utf-8", cipherAlgorithm);
				}else if(OPERATION_TYPE_DECRYPT.equals(operationType)) {
					if(securityKeyResource.getPrivateKeyFile() == null) {
						logger.error("T[{}] privateKey is null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "privateKey is null");
					}
					result = SignatureUtils.decrypt(srcText, securityKeyResource.getPrivateKeyFile(), securityKeyResource.getPrivateKeyPwd(), "utf-8", cipherAlgorithm);
				}else {
					logger.error("T[{}] invalid operationType:{}. businessflowNodeId: {}", businessFlowNode.getTenantId(), operationType, businessFlowNode.getId());
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid operationType");
				}
				break;

			default:
				logger.error("T[{}] invalid cipherAlgorithmType:{}. businessflowNodeId: {}", businessFlowNode.getTenantId(), cipherAlgorithmType, businessFlowNode.getId());
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid cipherAlgorithmType");
		}

		// 将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, result);
	}

}
