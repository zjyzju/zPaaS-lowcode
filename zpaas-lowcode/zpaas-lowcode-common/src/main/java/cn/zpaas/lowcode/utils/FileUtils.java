package cn.zpaas.lowcode.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件处理工具类
 *
 * @author zjy
 * createTime 2025年04月26日-17:55:32
 */
public class FileUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static final String DOT_SYMBOL = "."; 

	/**
	 * 创建文件目标
	 * @param path
	 * @return
	 */
	public static List<File> listFiles(String path) {
		File dir = new File(path);
		if(dir.exists() && dir.isDirectory()) {
			return Arrays.asList(dir.listFiles());
		}else {
			logger.error("list Files failed! {}", path);
			return null;
		}
	}

	/**
	 * 创建文件目标
	 * @param path
	 * @return
	 */
	public static List<File> listFiles(File path) {
		if(path.exists() && path.isDirectory()) {
			return Arrays.asList(path.listFiles());
		}else {
			logger.error("list Files failed! {}", path);
			return null;
		}
	}

	/**
	 * 创建文件目标
	 * @param path
	 * @return
	 */
	public static File getFile(String path) {
		File dir = new File(path);
		if(dir.exists()) {
			return dir;
		}else {
			logger.error("get file failed! {}", path);
			return null;
		}
	}

	/**
	 * 创建文件目标
	 * @param path
	 * @return
	 */
	public static File mkdir(String path) {
		File dir = new File(path);
		if(!dir.exists() && dir.mkdir()) {
			return dir;
		}else {
			logger.error("create directory failed! {}", path);
			return null;
		}
	}

	/**
	 * 创建文件目标
	 * @param path
	 * @return
	 */
	public static File mkdir(File path) {
		if(!path.exists() && path.mkdir()) {
			return path;
		}else {
			logger.error("create directory failed! {}", path);
			return null;
		}
	}

	/**
	 * 判断文件是否存在
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 判断文件是否存在
	 * @param path
	 * @return
	 */
	public static boolean exists(File path) {
		return path.exists();
	}

	/**
	 * 判断是否是文件
	 * @param path
	 * @return
	 */
	public static boolean isFile(String path) {
		File file = new File(path);
		return file.exists() && file.isFile();
	}

	/**
	 * 判断是否是文件
	 * @param path
	 * @return
	 */
	public static boolean isFile(File path) {
		return path.exists() && path.isFile();
	}

	/**
	 * 删除文件目录
	 * @param path
	 * @return
	 */
	public static boolean rmdir(String path) {
		File dir = new File(path);
		if(dir.isDirectory() && dir.delete()) {
			return true;
		}else {
			logger.error("delete directory failed! {}", path);
			return false;
		}
	}

	/**
	 * 删除文件目录
	 * @param path
	 * @return
	 */
	public static boolean rmdir(File path) {
		if(path.isDirectory() && path.delete()) {
			return true;
		}else {
			logger.error("delete directory failed! {}", path);
			return false;
		}
	}

	/**
	 * 重命名文件/目录
	 * @param path
	 * @return
	 */
	public static boolean rename(String path, String dest) {
		File dir = new File(path);
		if(dir.exists() && dir.renameTo(Path.of(dest).resolve(path).toFile())) {
			return true;
		}else {
			logger.error("delete directory failed! {}", path);
			return false;
		}
	}

	/**
	 * 重命名文件/目录
	 * @param path
	 * @return
	 */
	public static boolean rename(File path, String dest) {
		if(path.exists() && path.renameTo(Path.of(dest).resolve(path.toPath()).toFile())) {
			return true;
		}else {
			logger.error("delete directory failed! {}", path);
			return false;
		}
	}

	/**
	 * 创建文件
	 * @param path
	 * @return
	 */
	public static File createFile(String path) {
		File file = new File(path);
		try {
			if(!file.exists() && file.createNewFile()) {
				return file;
			}else {
				logger.error("create file failed! {}", path);
				return null;
			}
		} catch (IOException e) {
			logger.error("create file failed!", e);
			return null;
		}
	}

	/**
	 * 创建文件
	 * @param path
	 * @return
	 */
	public static File createFile(File path) {
		try {
			if(!path.exists() && path.createNewFile()) {
				return path;
			}else {
				logger.error("create file failed! {}", path);
				return null;
			}
		} catch (IOException e) {
			logger.error("create file failed!", e);
			return null;
		}
	}

	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		File file = new File(path);
		if(file.exists() && file.delete()) {
			return true;
		}else {
			logger.error("delete file failed! {}", path);
			return false;
		}
	}

	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(File path) {
		if(path.exists() && path.delete()) {
			return true;
		}else {
			logger.error("delete file failed! {}", path);
			return false;
		}
	}

	/**
	 * 复制文件
	 * @param path
	 * @param dest
	 * @return
	 */
	public static File copyFile(String path, String dest) {
		try {
			Path destPath = Files.copy(Paths.get(path), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
			return destPath.toFile();
		} catch (IOException e) {
			logger.error("copy file failed!", e);
			return null;
		}
	}

	/**
	 * 移动文件
	 * @param path
	 * @param dest
	 * @return
	 */
	public static File moveFile(String path, String dest) {
		try {
			Path destPath = Files.move(Paths.get(path), Paths.get(dest));
			return destPath.toFile();
		} catch (IOException e) {
			logger.error("move file failed!", e);
			return null;
		}
	}

	/**
	 * 写文件
	 * @param path
	 * @param dest
	 * @return
	 */
	public static File writeFile(String path, String dest) {
		File destFile = new File(dest);
		try (OutputStream os = new FileOutputStream(destFile)){
			Files.copy(Paths.get(path), os);
			return destFile;
		} catch (IOException e) {
			logger.error("copy file failed!", e);
			return null;
		}
	}

	/**
	 * 写文件
	 * @param src
	 * @param dest
	 * @return
	 */
	public static File writeFile(InputStream src, String dest) {
		Path destFile = Path.of(dest);
		try {
			Files.copy(src, destFile);
			return destFile.toFile();
		} catch (IOException e) {
			logger.error("copy file failed!", e);
			return null;
		}
	}
	
	/**
	 * 增加UUID串到文件路径
	 * @param srcFile
	 * @return
	 */
	public static Path addUUID2FilePath(Path path) {
		StringBuilder stringBuilder = new StringBuilder(path.toAbsolutePath().toString());
		stringBuilder.append(DOT_SYMBOL).append(UUID.uuidKey());
		Path tmpPath = Paths.get(stringBuilder.toString());
		return tmpPath;
	}
	
	/**
	 * 移除文件路径中的UUID串
	 * @param path
	 * @return
	 */
	public static Path removeUUIDFromFilePath(Path path) {
		String realPath = path.toAbsolutePath().toString();
		int index = realPath.lastIndexOf(DOT_SYMBOL);
		if(index > 0 && (realPath.length()-index) > 16) {
			return Paths.get(realPath.substring(0, index));
		}else {
			return path;
		}
	}
}
