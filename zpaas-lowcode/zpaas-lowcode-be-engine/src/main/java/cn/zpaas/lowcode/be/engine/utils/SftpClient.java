package cn.zpaas.lowcode.be.engine.utils;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * Sftp客户端能力封装类
 *
 * @author zjy
 * createTime 2025年04月27日-17:45:16
 */
public class SftpClient {
    public static Logger logger = LoggerFactory.getLogger(SftpClient.class);
	
	private String host; //sftp服务器地址
	private int port; //sftp服务端口
	private String user; //登录用户
	private String password; //登录密码
	
	
	private static String strictHostKeyChecking = "no";//是否检查HOST Key
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	private static final int CONNECT_TIME_OUT = 10000;
	private static final int CHANNEL_CONNECT_TIME_OUT = 5000;
	private static final String CHANNEL_TYPE = "sftp";
	
	
	public SftpClient(String host, int port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * 连接Sftp服务
	 * @throws EngineException
	 */
	public ChannelSftp connect() throws EngineException{
		Session jschSession = null;
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("connect to sftp {} ...", this.host);
			}
			JSch jSch = new JSch();
			jschSession = jSch.getSession(this.user, this.host, this.port);
			Properties config = new Properties();
			config.put(STRICT_HOST_KEY_CHECKING, strictHostKeyChecking);
			jschSession.setConfig(config);
			jschSession.setPassword(this.password);
			jschSession.connect(CONNECT_TIME_OUT);
			
			Channel sftp = jschSession.openChannel(CHANNEL_TYPE);		
			sftp.connect(CHANNEL_CONNECT_TIME_OUT);
			ChannelSftp channelSftp = (ChannelSftp) sftp;	
			if(logger.isDebugEnabled()) {
				logger.debug("connect to sftp {} succeed!", this.host);
			}
			return channelSftp;
		} catch (JSchException e) {
			logger.error("connect to sftp failed! {}", e);
			
			if(jschSession != null) {
				jschSession.disconnect();
				jschSession = null;
			}
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "connect sftp failed!");
		} 
	}
	
	/**
	 * 上传文件到SFTP指定路径
	 * @param src
	 * @param dst
	 */
	public void put(String src, String dst) throws EngineException{
		ChannelSftp channelSftp = this.connect();
		if(channelSftp != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("upload file to sftp ...");
			}
			try {
				channelSftp.put(src, dst);
				if(logger.isDebugEnabled()) {
					logger.debug("upload file to sftp succeed!");
				}
			} catch (SftpException e) {
				logger.error("upload file to sftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "upload file to sftp failed!");
			} finally {
				this.disconnect(channelSftp);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sftp not connected!");
		}
	}
	
	/**
	 * 上传文件到SFTP指定路径
	 * @param src
	 * @param dst
	 * @throws EngineException
	 */
	public void put(InputStream src, String dst) throws EngineException{
		ChannelSftp channelSftp = this.connect();
		if(channelSftp != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("upload file to sftp ...");
			}
			try {
				channelSftp.put(src, dst);
				if(logger.isDebugEnabled()) {
					logger.debug("upload file to sftp succeed!");
				}
			} catch (SftpException e) {
				logger.error("upload file to sftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "upload file to sftp failed!");
			} finally {
				this.disconnect(channelSftp);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sftp not connected!");
		}
	}
	
	/**
	 * 从SFTP上下载文件
	 * @param src
	 * @param dst
	 * @throws EngineException
	 */
	public void get(String src, String dst) throws EngineException{
		ChannelSftp channelSftp = this.connect();
		if(channelSftp != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("download file from sftp ...");
			}
			try {
				channelSftp.get(src, dst);
				if(logger.isDebugEnabled()) {
					logger.debug("download file from sftp succeed!");
				}
			} catch (SftpException e) {
				logger.debug("download file from sftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "download file from sftp failed!");
			}finally {
				this.disconnect(channelSftp);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sftp not connected!");
		}
	}
	
	/**
	 * 删除文件
	 * @param filePath
	 * @throws EngineException
	 */
	public void delete(String filePath) throws EngineException{
		ChannelSftp channelSftp = this.connect();
		if(channelSftp != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("delete file from sftp ...");
			}
			try {
				channelSftp.rm(filePath);
				if(logger.isDebugEnabled()) {
					logger.debug("delete file from sftp succeed!");
				}
			} catch (SftpException e) {
				logger.debug("delete file from sftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "delete file from sftp failed!");
			}finally {
				this.disconnect(channelSftp);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sftp not connected!");
		}
	}
	
//	/**
//	 * 从SFTP上下载文件
//	 * @param src
//	 * @return
//	 * @throws EngineException
//	 */
//	public InputStream get(String src) throws EngineException{
//		ChannelSftp channelSftp = this.connect();
//		if(channelSftp != null) {
//			if(logger.isDebugEnabled()) {
//				logger.debug("download file from sftp ...");
//			}
//			try {
//				InputStream is = channelSftp, src);
//				if(logger.isDebugEnabled()) {
//					logger.debug("download file from sftp succeed!");
//				}
//				return is;
//			} catch (SftpException e) {
//				logger.error("download file from sftp failed! {}", e);
//				
//				throw new EngineException(ResultStatus.INTERNAL_ERROR, "download file from sftp failed!");
//			}finally {
//				this.disconnect(channelSftp);
//			}
//		}else {
//			throw new EngineException(ResultStatus.INTERNAL_ERROR, "sftp not connected!");
//		}
//	}
	
	/**
	 * 断开与Sftp服务的连接
	 */
	public void disconnect(ChannelSftp channelSftp) {
		if(channelSftp != null) {
			channelSftp.exit();
			channelSftp.disconnect();
			try {
				channelSftp.getSession().disconnect();
			} catch (JSchException e) {
				e.printStackTrace();
			}
			channelSftp = null;
		}
	}
}
