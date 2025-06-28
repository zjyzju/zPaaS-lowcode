package cn.zpaas.lowcode.be.engine.utils;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;
import cn.zpaas.lowcode.vo.MailInfo;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;

/**
 * 邮件发送工具类
 *
 * @author zjy
 * createTime 2025年04月27日-14:26:43
 */
public class MailClient {
    public static Logger logger = LoggerFactory.getLogger(MailClient.class);
	
	public static final String MAIL_AUTH_USER = "mail.smtp.auth.user";//用户名
	public static final String MAIL_AUTH_CODE = "mail.smtp.auth.code";//密码/授权码
	public static final String MAIL_AUTH_HOST = "mail.smtp.host";//stmp服务器地址
	public static final String MAIL_AUTH_PORT = "mail.imap.port";//stmp服务器端口
	
	public static final String MAIL_SEND_ATTACHMENT_NAME = "attachmentName";//邮件附件名
	public static final String MAIL_SEND_ATTACHMENT_TYPE = "attachmentType";//邮件附件类型
	public static final String MAIL_SEND_ATTACHMENT_ID = "attachmentId";//邮件附件标识
	public static final String MAIL_SEND_ATTACHMENT_FILE = "attachmentFile";//邮件附件文件
	
	public static final String MAIL_SEND_ATTACHMENT_TYPE_DEFAULT = "application/octet-stream";//默认附件类型
	
	public static final String MAIL_SEND_CONTENT_TYPE_PLAIN = "text/plain;charset=utf-8";//邮件内容类型
	public static final String MAIL_SEND_CONTENT_TYPE_HTML = "text/html;charset=utf-8";//邮件内容类型
	public static final String MAIL_SEND_CONTENT_TYPE_P = "P";//邮件内容类型
	public static final String MAIL_SEND_CONTENT_TYPE_H = "H";//邮件内容类型
	
	//保存邮件SMTP服务器参数信息
	private Properties smtpProps = null;
	
	public MailClient(Properties smtpProps) {
		this.smtpProps = smtpProps;
	}
	
	/**
	 * 根据服务器参数集合和认证器来获取Session实例
	 * @return
	 */
	private Session getMailSession() {
		if(this.smtpProps == null || this.smtpProps.isEmpty()) {
			logger.error("smtp server properties is null!");
			return null;
		}
		//Authenticator是一个接口表示认证器，即校验客户端的身份。 我们需要自己来实现这个接口，实现这个接口需要使用账户和密码。
		Session session = Session.getInstance(this.smtpProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 使用账户和密码，有些邮件服务器可能需要的是申请的授权码
				return new PasswordAuthentication(smtpProps.getProperty(MAIL_AUTH_USER), smtpProps.getProperty(MAIL_AUTH_CODE));
			}
		});
		return session;
	}
	
	public void sendMail(MailInfo mailInfo) throws EngineException {
		if(mailInfo == null) {
			logger.error("mailInfo is null!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "mailInfo is null!");
		}
		Session session = this.getMailSession();
		if(session == null) {
			logger.error("get mail session failed!");
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "get mail session failed!");
		}
		
		try {
			MimeMessage msg = new MimeMessage(session);
			if(mailInfo.getFrom() == null || mailInfo.getFrom().trim().length() == 0 ||
					mailInfo.getTo() == null || mailInfo.getTo().trim().length() == 0) {
				logger.error("mail's from and to can't be null!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "mail's from and to can't be null!");
			}
			// 设置发信人
			msg.setFrom(new InternetAddress(mailInfo.getFrom()));
			// 设置收信人，可以设置多个，采用参数数组或者","分隔
			msg.addRecipients(Message.RecipientType.TO, mailInfo.getTo());
			// 设置抄送人，可以设置多个，采用参数数组或者","分隔
			if(mailInfo.getCc() != null && mailInfo.getCc().trim().length() != 0) {
				msg.addRecipients(Message.RecipientType.CC, mailInfo.getCc());
			}
			// 设置暗送人，可以设置多个，采用参数数组或者","分隔
			if(mailInfo.getBcc() != null && mailInfo.getBcc().trim().length() != 0) {
				msg.addRecipients(Message.RecipientType.BCC, mailInfo.getBcc());
			}
			// 设置邮件主题（标题）
			msg.setSubject(mailInfo.getSubject());
			
			//不包含附件
			if(mailInfo.getAttachments() == null || mailInfo.getAttachments().isEmpty()) {
				msg.setContent(mailInfo.getContent(), mailInfo.getContentType());
			}else {//包含附件
				//创建一个多部件对象Multipart，可以理解为是部件的集合。
				//一个Multipart对象可以添加若干个BodyPart，其中第一个BodyPart是文本，即邮件正文，后面的BodyPart是附件
				Multipart parts = new MimeMultipart();
				//设置邮件的内容为多部件内容。
				msg.setContent(parts);
				
				//创建第一个部件，为邮件正文
				BodyPart contentPart = new MimeBodyPart();
				String contentType = MAIL_SEND_CONTENT_TYPE_HTML;
				if(MAIL_SEND_CONTENT_TYPE_P.equals(mailInfo.getContentType())) {
					contentType = MAIL_SEND_CONTENT_TYPE_PLAIN;
				}
				// 给部件设置正文内容
				contentPart.setContent(mailInfo.getContent(), contentType);
				// 把部件添加到部件集中
				parts.addBodyPart(contentPart);
				
				for(int i=0; i<mailInfo.getAttachments().size(); i++) {
					Map<String, Object> attachment = mailInfo.getAttachments().get(i);
					//创建用于发送附件的部件
					MimeBodyPart part = new MimeBodyPart();
					
					// 设置附件名称,文件名称中包含了中文的话，那么需要使用MimeUitlity类来给中文编码
					part.setFileName((String)attachment.get(MimeUtility.encodeText(MAIL_SEND_ATTACHMENT_NAME)));
					// 设置附件标识。
					part.setContentID((String)attachment.get(MAIL_SEND_ATTACHMENT_ID));
					// 设置附件，二进制文件可以用application/octet-stream，Word文档则是application/msword, 图片image/jpeg, 文本text/plain,text/html,image/png,image/jpg,image/gif
					part.attachFile((File)attachment.get(MAIL_SEND_ATTACHMENT_FILE), (String)attachment.get(MAIL_SEND_ATTACHMENT_TYPE), null);
					
					// 把附件添加到部件集中
					parts.addBodyPart(part);
				}
			}
			
			//发送邮件
			Transport.send(msg);
		} catch (Exception e) {
			logger.error("send mail failed!", e);
			throw new EngineException(ResultStatus.BUSINESS_ERROR, "send mail failed!", e.getMessage(), e);
		} 
	}
}
