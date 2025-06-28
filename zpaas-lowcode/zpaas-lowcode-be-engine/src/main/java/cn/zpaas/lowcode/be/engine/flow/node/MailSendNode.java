package cn.zpaas.lowcode.be.engine.flow.node;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.vo.MailInfo;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.be.engine.ability.MailSendAbility;
import cn.zpaas.lowcode.be.engine.command.BusinessFlowContext;
import cn.zpaas.lowcode.be.engine.utils.MailClient;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.domain.eo.BusinessFlowNode;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * @author zjy 
 * MailSendNode是邮件发送节点的实现类，主要用于发送邮件
 */
public class MailSendNode extends Node {
	private static Logger logger = LoggerFactory.getLogger(MailSendNode.class);

	private static final String CFG_SMTP_RESOURCE_ID = "smtpResourceId";//smtp资源标识
	
	private static final String CFG_MAIL_SEND_FROM_SOURCE = "fromSource";//发件人
	private static final String CFG_MAIL_SEND_FROM_KEY = "fromKey";//发件人
	private static final String CFG_MAIL_SEND_FROM_ATTR = "fromAttr";//发件人
	private static final String CFG_MAIL_SEND_TO_SOURCE = "toSource";//收件人
	private static final String CFG_MAIL_SEND_TO_KEY = "toKey";//收件人
	private static final String CFG_MAIL_SEND_TO_ATTR = "toAttr";//收件人
	private static final String CFG_MAIL_SEND_CC_SOURCE = "ccSource";//抄送人
	private static final String CFG_MAIL_SEND_CC_KEY = "ccKey";//抄送人
	private static final String CFG_MAIL_SEND_CC_ATTR = "ccAttr";//抄送人
	private static final String CFG_MAIL_SEND_BCC_SOURCE = "bccSource";//密送人
	private static final String CFG_MAIL_SEND_BCC_KEY = "bccKey";//密送人
	private static final String CFG_MAIL_SEND_BCC_ATTR = "bccAttr";//密送人
	private static final String CFG_MAIL_SEND_SUBJECT_SOURCE = "subjectSource";//邮件主题
	private static final String CFG_MAIL_SEND_SUBJECT_KEY = "subjectKey";//邮件主题
	private static final String CFG_MAIL_SEND_SUBJECT_ATTR = "subjectAttr";//邮件主题
	private static final String CFG_MAIL_SEND_CONTENT_SOURCE = "contentSource";//邮件内容
	private static final String CFG_MAIL_SEND_CONTENT_KEY = "contentKey";//邮件内容
	private static final String CFG_MAIL_SEND_CONTENT_ATTR = "contentAttr";//邮件内容
	private static final String CFG_MAIL_SEND_CONTENT_TYPE = "contentType";//邮件内容类型
	
	private static final String CFG_MAIL_SEND_CONTENT_PARAMS = "contentParams";//邮件内容参数列表
	private static final String CFG_MAIL_SEND_PARAM_ID = "paramId";//参数标识
	private static final String CFG_MAIL_SEND_PARAM_SOURCE = "paramSource";//参数值
	private static final String CFG_MAIL_SEND_PARAM_KEY = "paramKey";//参数值
	private static final String CFG_MAIL_SEND_PARAM_ATTR = "paramAttr";//参数值
	
	private static final String CFG_MAIL_SEND_ATTACHMENTS = "attachments";//邮件附件
	private static final String CFG_MAIL_SEND_ATTACHMENT_NAME = "attachmentName";//邮件附件名
	private static final String CFG_MAIL_SEND_ATTACHMENT_TYPE = "attachmentType";//邮件附件类型
	private static final String CFG_MAIL_SEND_ATTACHMENT_ID = "attachmentId";//邮件附件标识
	private static final String CFG_MAIL_SEND_ATTACHMENT_FILE_SOURCE = "attachmentFileSource";//邮件附件文件
	private static final String CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_SOURCE = "attachmentFileNameSource";//邮件附件文件
	private static final String CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_KEY = "attachmentFileNameKey";//邮件附件文件
	private static final String CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_ATTR = "attachmentFileNameAttr";//邮件附件文件
	
	private static final String LEFT_$_BRACES = "\\$\\{";
	private static final String RIGHT_BRACES = "\\}";
	
	private static final String ATTACHMENT_FILE_SOURCE_U = "U"; //（过程数据：过程数据中的文件对象）
	private static final String ATTACHMENT_FILE_SOURCE_P = "P"; //（上传文件：上传的Multipart文件对象）
	
	
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
		
		//处理SMTP服务资源标识信息
		String smtpResourceId = JsonUtils.getString(nodeCfg, CFG_SMTP_RESOURCE_ID);
		if (StringUtils.isBlank(smtpResourceId)) {
			logger.error("T[{}] smtpResourceId can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "smtpResourceId can't be null. ");
		}
		
		//初始化邮件发送信息
		MailInfo mailInfo = new MailInfo();
		
		//处理发件人信息
		String fromSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_FROM_SOURCE);
		String fromKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_FROM_KEY);
		String fromAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_FROM_ATTR);
		if (!StringUtils.isBlank(fromAttr)) {
			fromAttr = this.dynamicAttrProcess(fromAttr, businessFlowNode, context);
		}
		// 配置参数合法性校验
		if (StringUtils.isBlank(fromSource) || StringUtils.isBlank(fromKey)) {
			logger.error("T[{}] fromSource and fromKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "fromSource and fromKey can't be null. ");
		}
		//获取发件人信息
		String from = (String)getContextObject(fromSource, fromKey, false, fromAttr, context, businessFlowNode);
		if(StringUtils.isBlank(from)) {
			logger.error("T[{}] from can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "from can't be null. ");
		}
		mailInfo.setFrom(from);
		
		// 处理收件人信息
		String toSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_TO_SOURCE);
		String toKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_TO_KEY);
		String toAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_TO_ATTR);
		if (!StringUtils.isBlank(toAttr)) {
			toAttr = this.dynamicAttrProcess(toAttr, businessFlowNode, context);
		}
		// 配置参数合法性校验
		if (StringUtils.isBlank(toSource) || StringUtils.isBlank(toKey)) {
			logger.error("T[{}] toSource and toKey can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "toSource and toKey can't be null. ");
		}
		// 获取收件人信息
		String to = (String) getContextObject(toSource, toKey, false, toAttr, context, businessFlowNode);
		if (StringUtils.isBlank(to)) {
			logger.error("T[{}] to can't be null. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "to can't be null. ");
		}
		mailInfo.setTo(to);
		
		// 处理抄送人信息
		String ccSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CC_SOURCE);
		String ccKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CC_KEY);
		String ccAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CC_ATTR);
		if (!StringUtils.isBlank(ccAttr)) {
			ccAttr = this.dynamicAttrProcess(ccAttr, businessFlowNode, context);
		}
		if (!StringUtils.isBlank(ccSource) && !StringUtils.isBlank(ccKey)) {
			// 获取抄送人信息
			String cc = (String) getContextObject(ccSource, ccKey, false, ccAttr, context, businessFlowNode);
			if (cc != null && cc.trim().length() > 0) {
				mailInfo.setCc(cc);
			}
		}
		
		// 处理密送人信息
		String bccSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_BCC_SOURCE);
		String bccKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_BCC_KEY);
		String bccAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_BCC_ATTR);
		if (!StringUtils.isBlank(bccAttr)) {
			bccAttr = this.dynamicAttrProcess(bccAttr, businessFlowNode, context);
		}
		if (!StringUtils.isBlank(bccSource) && !StringUtils.isBlank(bccKey)) {
			// 获取密送人信息
			String bcc = (String) getContextObject(bccSource, bccKey, false, bccAttr, context, businessFlowNode);
			if (bcc != null && bcc.trim().length() > 0) {
				mailInfo.setBcc(bcc);
			}
		}
		
		// 处理邮件主题信息
		String subjectSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_SUBJECT_SOURCE);
		String subjectKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_SUBJECT_KEY);
		String subjectAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_SUBJECT_ATTR);
		if (!StringUtils.isBlank(subjectAttr)) {
			subjectAttr = this.dynamicAttrProcess(subjectAttr, businessFlowNode, context);
		}
		if (!StringUtils.isBlank(subjectSource) && !StringUtils.isBlank(subjectKey)) {
			// 获取邮件主题信息
			String subject = (String) getContextObject(subjectSource, subjectKey, false, subjectAttr, context, businessFlowNode);
			if (subject != null && subject.trim().length() > 0) {
				mailInfo.setSubject(subject);
			}
		}
		
		// 处理邮件内容信息
		String contentSource = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CONTENT_SOURCE);
		String contentKey = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CONTENT_KEY);
		String contentAttr = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CONTENT_ATTR);
		if (!StringUtils.isBlank(contentAttr)) {
			contentAttr = this.dynamicAttrProcess(contentAttr, businessFlowNode, context);
		}
		if (!StringUtils.isBlank(contentSource) && !StringUtils.isBlank(contentKey)) {
			// 获取邮件内容信息
			String content = (String) getContextObject(contentSource, contentKey, false, contentAttr, context,
					businessFlowNode);
			if (!StringUtils.isBlank(content)) {
				//处理邮件内容参数值的替换
				JsonObject contentParam = null;
				String paramId = null;
				String paramSource = null;
				String paramKey = null;
				String paramAttr = null;
				String paramValue = null;
				JsonArray contentParams = JsonUtils.getJsonArray(nodeCfg, CFG_MAIL_SEND_CONTENT_PARAMS);
				if(!JsonUtils.isEmpty(contentParams)) {
					for(int i=0; i<contentParams.size(); i++) {
						contentParam = contentParams.get(i).getAsJsonObject();
						if(!JsonUtils.isEmpty(contentParam)) {
							paramId = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_ID);//参数ID
							paramSource = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_SOURCE);//参数值来源
							paramKey = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_KEY);//参数值Key
							paramAttr = JsonUtils.getString(contentParam, CFG_MAIL_SEND_PARAM_ATTR);//参数值属性
							if (!StringUtils.isBlank(paramAttr)) {
								paramAttr = this.dynamicAttrProcess(paramAttr, businessFlowNode, context);
							}
							if(StringUtils.isBlank(paramId) || StringUtils.isBlank(paramSource) || StringUtils.isBlank(paramKey)) {
								logger.error("T[{}] paramId paramSource or paramKey is null! continue...", businessFlowNode.getTenantId());
								continue;
							}
							//获取参数值
							paramValue = (String) getContextObject(paramSource, paramKey, false, paramAttr, context, businessFlowNode);
							if(!StringUtils.isBlank(paramValue)) {
								content = content.replaceAll(LEFT_$_BRACES+(paramId)+RIGHT_BRACES, Matcher.quoteReplacement(paramValue));
							}
						}
					}
				}
				
				mailInfo.setContent(content);
			}
		}
		
		// 处理邮件内容类型信息
		String contentType = JsonUtils.getString(nodeCfg, CFG_MAIL_SEND_CONTENT_TYPE);
		if (!StringUtils.isBlank(contentType)) {
			mailInfo.setContentType(contentType);
		}
		
		//处理邮件附件信息
		JsonArray attachments = JsonUtils.getJsonArray(nodeCfg, CFG_MAIL_SEND_ATTACHMENTS);
		if(!JsonUtils.isEmpty(attachments)) {
			JsonObject attachment = null;
			String attachmentId = null;
			String attachmentName = null;
			String attachmentType = null;
			String attachmentFileSource = null;
			String attachmentFileNameSource = null;
			String attachmentFileNameKey = null;
			String attachmentFileNameAttr = null;
			String attachmentFileName = null;
			File attachmentFile = null;
			List<Map<String, Object>> attachmentArray = new ArrayList<>();//目标附件列表
			Map<String, Object> attachmentObject = null;
			for(int i=0; i<attachments.size(); i++) {
				attachment = attachments.get(i).getAsJsonObject();
				if(!JsonUtils.isEmpty(attachment)) {
					attachmentId = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_ID);//附件标识
					attachmentName = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_NAME);//附件名称
					attachmentType = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_TYPE);//附件类型
					attachmentFileSource = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_FILE_SOURCE);//附件文件来源
					attachmentFileNameSource = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_SOURCE);//附件文件名来源
					attachmentFileNameKey = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_KEY);//附件文件名Key
					attachmentFileNameAttr = JsonUtils.getString(attachment, CFG_MAIL_SEND_ATTACHMENT_FILE_NAME_ATTR);//附件文件名属性
					if (!StringUtils.isBlank(attachmentFileNameAttr)) {
						attachmentFileNameAttr = this.dynamicAttrProcess(attachmentFileNameAttr, businessFlowNode, context);
					}
					if(StringUtils.isBlank(attachmentFileSource) || StringUtils.isBlank(attachmentFileNameSource) ||
									StringUtils.isBlank(attachmentFileNameKey)) {
						logger.error("T[{}] attachmentFileSource attachmentFileNameSource or attachmentFileNameKey is null!", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "attachmentFileSource attachmentFileNameSource or attachmentFileNameKey is null!");
					}
					//获取文件名
					attachmentFileName = (String) getContextObject(attachmentFileNameSource, attachmentFileNameKey, false, attachmentFileNameAttr, context, businessFlowNode);
					if(StringUtils.isBlank(attachmentFileName)) {
						logger.error("T[{}] attachmentFileName is null! ", businessFlowNode.getTenantId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "attachmentFileName is null! ");
					}
					//获取文件对象
					if(ATTACHMENT_FILE_SOURCE_U.equals(attachmentFileSource)) {//上传文件
						List<MultipartFile> files = context.getMultipartFileMap().get(attachmentFileName);
						if(CollectionUtils.isEmpty(files)) {
							logger.error("T[{}] attachment file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "attachment file not found. ");
						}
						MultipartFile multipartFile = files.get(0);
						try {
							//创建临时文件
							Path path = LocalFileAbility.getInstance().getTempFilePath(multipartFile.getOriginalFilename());
							path = Files.createFile(path);
							multipartFile.transferTo(path);
							attachmentFile = path.toFile();
						} catch (Exception e) {
							logger.error("T[{}] MultipartFile transferTo file failed!", businessFlowNode.getTenantId(), e);
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "MultipartFile transferTo file failed!");
						} 
					}else if(ATTACHMENT_FILE_SOURCE_P.equals(attachmentFileSource)) {//过程数据
						attachmentFile = (File)getContextObject(attachmentFileSource, attachmentFileName, false, null, context, businessFlowNode);
						if(attachmentFile == null) {
							logger.error("T[{}] attachment file not found. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "attachment file not found. ");
						}
					}else {//其他
						logger.error("T[{}] invalid attachmentFileSource. businessflowNodeId: {}", businessFlowNode.getTenantId(), businessFlowNode.getId());
						throw new EngineException(ResultStatus.BUSINESS_ERROR, "invalid attachmentFileSource");
					}
					
					attachmentObject = new HashMap<>();
					attachmentObject.put(MailClient.MAIL_SEND_ATTACHMENT_ID, attachmentId);
					attachmentObject.put(MailClient.MAIL_SEND_ATTACHMENT_NAME, attachmentName);
					if(StringUtils.isBlank(attachmentType)) {//为空时，默认使用application/octet-stream
						attachmentObject.put(MailClient.MAIL_SEND_ATTACHMENT_TYPE, MailClient.MAIL_SEND_ATTACHMENT_TYPE_DEFAULT);
					}else {
						attachmentObject.put(MailClient.MAIL_SEND_ATTACHMENT_TYPE, attachmentType);
					}
					attachmentObject.put(MailClient.MAIL_SEND_ATTACHMENT_FILE, attachmentFile);
					attachmentArray.add(attachmentObject);
				}
			}
			mailInfo.setAttachments(attachmentArray);
		}
	
		MailSendAbility.getInstance().sendMail(mailInfo,  businessFlowNode.getSystemId(), smtpResourceId);
		
		//将返回对象保存到节点处理结果中
		context.setAttribute(NODE_RESULT_KEY, null);
	}

}
