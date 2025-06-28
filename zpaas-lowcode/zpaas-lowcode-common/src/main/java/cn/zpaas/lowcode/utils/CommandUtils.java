package cn.zpaas.lowcode.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.exception.CommException;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月18日-09:47:39
 */
public class CommandUtils {
    public static final Logger log = LoggerFactory.getLogger(CommandUtils.class);

    private static final String COMMAND_EXECUTE_FAILED = "command excute failed!";
	private static final String WHITE_SPACE = " ";

    /**
     * 私有化构造方法
     */
    private CommandUtils() {

    }
	
    /**
     * 执行命令
     * @param cmd
     * @return
     */
	public static String execCommandForResult(String[] cmd){
		if(log.isDebugEnabled()) {
			StringBuilder cmdStr = new StringBuilder();
			for(int i=0; i<cmd.length; i++) {
                cmdStr.append(" ").append(cmd[i]);
			}
			log.debug(cmdStr.toString());
		}
		try {
			Process process = Runtime.getRuntime().exec(cmd);	
			StringBuilder sb = new StringBuilder();
			if(process.waitFor() == 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String tmp = null;
				while((tmp = reader.readLine()) != null) {
					sb.append(tmp).append("\n");
				}
			}else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String tmp = null;
				while((tmp = reader.readLine()) != null) {
					sb.append(tmp).append("\n");
				}
			}
			return sb.toString();
		} catch(InterruptedException ie) {
            log.error(COMMAND_EXECUTE_FAILED, ie);
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
			throw new CommException(COMMAND_EXECUTE_FAILED, e);
		}
	}
	
    /**
     * 执行命令
     * @param cmd
     * @return
     */
	public static String execCommandForResult(String cmd) {
		if(log.isDebugEnabled()) {
			log.debug(cmd);
		}
		String[] cmds = null;
		if(!StringUtils.isBlank(cmd)) {
			cmds = cmd.split(WHITE_SPACE);
		}
		try {
			Process process = Runtime.getRuntime().exec(cmds);	
			StringBuilder sb = new StringBuilder();
			if(process.waitFor() == 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String tmp = null;
				while((tmp = reader.readLine()) != null) {
					sb.append(tmp).append("\n");
				}
			}else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String tmp = null;
				while((tmp = reader.readLine()) != null) {
					sb.append(tmp).append("\n");
				}
			}
			return sb.toString();
		} catch(InterruptedException ie) {
            log.error(COMMAND_EXECUTE_FAILED, ie);
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
			throw new CommException(COMMAND_EXECUTE_FAILED, e);
		}
	}
	
    /**
     * 执行命令
     * @param cmd
     * @return
     */
	public static boolean execCommand(String[] cmd) {
		if(log.isDebugEnabled()) {
			StringBuilder cmdStr = new StringBuilder();
			for(int i=0; i<cmd.length; i++) {
                cmdStr.append(" ").append(cmd[i]);
			}
			log.debug(cmdStr.toString());
		}
		try {
			Process process = Runtime.getRuntime().exec(cmd);	

			return (process.waitFor() == 0) ;
		} catch(InterruptedException ie) {
            log.error(COMMAND_EXECUTE_FAILED, ie);
            Thread.currentThread().interrupt();
            return false;
        }catch (Exception e) {
			throw new CommException(COMMAND_EXECUTE_FAILED, e);
		}
	}
	
    /**
     * 执行命令
     * @param cmd
     * @return
     */
	public static boolean execCommand(String cmd) {
		log.debug("execute command: {}", cmd);
		String[] cmds = null;
		if(!StringUtils.isBlank(cmd)) {
			cmds = cmd.split(WHITE_SPACE);
		}
		try {
			Process process = Runtime.getRuntime().exec(cmds);	
			return (process.waitFor() == 0) ;
		} catch(InterruptedException ie) {
            log.error(COMMAND_EXECUTE_FAILED, ie);
            Thread.currentThread().interrupt();
            return false;
        }catch (Exception e) {
			throw new CommException(COMMAND_EXECUTE_FAILED, e);
		}
	}
}
