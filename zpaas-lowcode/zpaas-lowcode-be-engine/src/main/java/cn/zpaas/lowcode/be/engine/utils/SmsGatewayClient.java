package cn.zpaas.lowcode.be.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.exception.EngineException;

/**
 * 短信网关封装类
 *
 * @author zjy
 * createTime 2025年04月27日-16:52:23
 */
public class SmsGatewayClient {
    public static Logger logger = LoggerFactory.getLogger(SmsGatewayClient.class);
	
	//保存短信网关服务器参数信息
	private String authUser = null;//用户/SPID
	private String authCode = null;//密码/鉴权码
	private String host = null;//主机IP
	private String port = null;//端口
	private JsonObject serverCfg = null;//其他配置信息
	
	
	/**
	 * 构造函数
	 * @param authUser
	 * @param authCode
	 * @param host
	 * @param port
	 * @param serverCfg
	 */
	public SmsGatewayClient(String authUser, String authCode, String host, String port, JsonObject serverCfg) {
		this.authCode = authCode;
		this.authUser = authUser;
		this.host = host;
		this.port = port;
		this.serverCfg = serverCfg;
	}
	
	/**
	 * 发送短信
	 * @param receivers，多个接收者时，以逗号分隔
	 * @param msg
	 * @throws EngineException
	 */
	public void sendSMS(String receivers, String msg) throws EngineException {
		logger.info("use sms gateway {}:{}(by user{}/{}, other cfg is {}) to send sms to {} , message is: {}", this.host, this.port, this.authUser, this.authCode, this.serverCfg, receivers, msg);
	}
}
