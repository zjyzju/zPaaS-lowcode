package cn.zpaas.lowcode.be.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * ftp客户端能力封装类
 *
 * @author zjy
 * createTime 2025年04月27日-17:48:31
 */
public class FtpClient {
    public static Logger logger = LoggerFactory.getLogger(FtpClient.class);
	
	private static final String UTF_8 = "utf-8";
	
	private String host; //ftp服务器地址
	private int port; //ftp服务端口
	private String user; //登录用户
	private String password; //登录密码
	
	public FtpClient(String host, int port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		
	}
	
	/**
	 * 连接ftp服务
	 * @throws EngineException
	 */
	public FTPClient connect() throws EngineException{
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("connect to ftp {} ...", this.host);
			}
			FTPClient ftpClient = new FTPClient();
			ftpClient.setControlEncoding(UTF_8);
			
			ftpClient.connect(this.host, this.port);
			ftpClient.login(this.user, this.password);
			int replyCode = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(replyCode) == false) {
				logger.error("connect to ftp failed!");
				ftpClient = null;
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "connect ftp failed!");
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			if(logger.isDebugEnabled()) {
				logger.debug("connect to ftp {} succeed!", this.host);
			}
			return ftpClient;
		} catch (IOException e) {
			logger.error("connect to ftp failed! {}", e);
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "connect ftp failed!");
		} 
	}
	
	/**
	 * 上传文件到FTP指定路径
	 * @param src
	 * @param dst
	 */
	public void put(String src, String dst) throws EngineException{
		FTPClient ftpClient = this.connect();
		if(ftpClient != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("upload file to ftp ...");
			}
			OutputStream os = null;
			try {
				ftpClient.enterLocalPassiveMode();
				os = ftpClient.storeFileStream(dst);
				Files.copy(Paths.get(src), os);
				
				if(logger.isDebugEnabled()) {
					logger.debug("upload file to ftp succeed!");
				}
			} catch (Exception e) {
				logger.error("upload file to ftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "upload file to ftp failed!");
			}finally {
				if(os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				this.disconnect(ftpClient);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ftp not connected!");
		}
	}
	
	/**
	 * 上传文件到FTP指定路径
	 * @param src
	 * @param dst
	 * @throws EngineException
	 */
	public void put(InputStream src, String dst) throws EngineException{
		FTPClient ftpClient = this.connect();
		if(ftpClient != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("upload file to ftp ...");
			}
			try {
				
				ftpClient.enterLocalPassiveMode();
				boolean result = ftpClient.storeFile(dst, src);
				if(result) {
					if(logger.isDebugEnabled()) {
						logger.debug("upload file to ftp succeed!");
					}
				}else {
					logger.error("upload file to ftp failed!");
					throw new EngineException(ResultStatus.INTERNAL_ERROR, "upload file to ftp failed!");
				}
				
			} catch (IOException e) {
				logger.error("upload file to ftp failed! {}", e);
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "upload file to ftp failed!");
			} finally {
				if(src != null) {
					try {
						src.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				this.disconnect(ftpClient);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ftp not connected!");
		}
	}
	
	/**
	 * 从FTP上下载文件
	 * @param src
	 * @param dst
	 * @throws EngineException
	 */
	public void get(String src, String dst) throws EngineException{
		FTPClient ftpClient = this.connect();
		if(ftpClient != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("download file from ftp ...");
			}
			try {
				Path path = Paths.get(dst);
				InputStream is = ftpClient.retrieveFileStream(src);
				Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
				if(logger.isDebugEnabled()) {
					logger.debug("download file from ftp succeed!");
				}
			} catch (Exception e) {
				logger.error("download file from ftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "download file from ftp failed!");
			} finally {
				this.disconnect(ftpClient);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ftp not connected!");
		}
	}
	
	/**
	 * 删除文件
	 * @param file
	 * @return
	 * @throws EngineException
	 */
	public boolean delete(String filePath) throws EngineException{
		FTPClient ftpClient = this.connect();
		if(ftpClient != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("delete file from ftp ...");
			}
			try {
				boolean result = ftpClient.deleteFile(filePath);
				if(logger.isDebugEnabled()) {
					logger.debug("delete file from ftp succeed!");
				}
				return result;
			} catch (Exception e) {
				logger.error("delete file from ftp failed! {}", e);
				
				throw new EngineException(ResultStatus.INTERNAL_ERROR, "delete file from ftp failed!");
			} finally {
				this.disconnect(ftpClient);
			}
		}else {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ftp not connected!");
		}
	}
	
//	/**
//	 * 从FTP上下载文件
//	 * @param src
//	 * @return
//	 * @throws EngineException
//	 */
//	public InputStream get(String src) throws EngineException{
//		FTPClient ftpClient = this.connect();
//		if(ftpClient != null) {
//			if(logger.isDebugEnabled()) {
//				logger.debug("download file from ftp ...");
//			}
//			try {
//				InputStream is = ftpClient.retrieveFileStream(src);
//				if(logger.isDebugEnabled()) {
//					logger.debug("download file from ftp succeed!");
//				}
//				return is;
//			} catch (Exception e) {
//				logger.error("download file from ftp failed! {}", e);
//				
//				throw new EngineException(ResultStatus.INTERNAL_ERROR, "download file from ftp failed!");
//			} finally {
//				this.disconnect(ftpClient);
//			}
//		}else {
//			throw new EngineException(ResultStatus.INTERNAL_ERROR, "ftp not connected!");
//		}
//	}
	
	/**
	 * 断开与ftp服务的连接
	 */
	public void disconnect(FTPClient ftpClient) {
		if(ftpClient != null) {
			try {
				ftpClient.abort();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			};
			ftpClient = null;
		}
		
	}
}
