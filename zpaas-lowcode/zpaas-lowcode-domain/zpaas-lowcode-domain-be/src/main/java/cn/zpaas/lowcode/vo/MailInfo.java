package cn.zpaas.lowcode.vo;

import java.util.List;
import java.util.Map;

/**
 * 邮件信息vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class MailInfo {
	private String from;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String content;
	private String contentType;
	private List<Map<String, Object>> attachments;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public List<Map<String, Object>> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Map<String, Object>> attachments) {
		this.attachments = attachments;
	}
	
}
