package cn.zpaas.lowcode.be.engine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * Json文件处理类
 *
 * @author zjy
 * createTime 2025年04月27日-14:53:18
 */
public class JsonFileUtils {
    public static final Logger logger = LoggerFactory.getLogger(JsonFileUtils.class);
	
	public static final String DEFAULT_FILE_NAME = "untitled.json";
	
	/**
	 * 私有化构造方法
	 */
	private JsonFileUtils() {
		
	}
	
	/**
	 * 加载json文件
	 * @param jsonFile
	 * @return
	 */
	public static BufferedReader getJSONFileReader(File jsonFile) {
		BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return reader;
    }
	
	/**
	 * 加载json文件
	 * @param jsonFile
	 * @return
	 */
	public static BufferedReader getJSONFileReader(MultipartFile excelFile) {
		BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(excelFile.getInputStream()));
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return reader;
    }
	
	/**
	 * 加载json文件
	 * @param filePath
	 * @return
	 */
	public static BufferedReader getJSONFileReader(Path filePath) {
		return getJSONFileReader(filePath.toFile());
	}
	
	
	

	
	/**
	 * 解析getJSONFileReader文件
	 * @param file
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(MultipartFile file, Integer startRow) {
		BufferedReader reader = getJSONFileReader(file);
		return process(reader, startRow);
	}
	
	/**
	 * 解析excel文件
	 * @param file
	 * @param startRow
	 * @return
	 */
	public static JsonArray processObjectType(Object file,  Integer startRow) {
		BufferedReader reader = null;
		if(file instanceof File) {
			reader = getJSONFileReader((File)file);
		}else if(file instanceof MultipartFile) {
			reader = getJSONFileReader((MultipartFile)file);
		}
		return process(reader, startRow);
	}
	
	/**
	 * 解析json文件
	 * @param file
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(File file, Integer startRow) {
		BufferedReader reader = getJSONFileReader(file);
		return process(reader, startRow);
	}
	
	/**
	 * 解析json文件
	 * @param reader
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(BufferedReader reader, Integer startRow) {
		//初始化返回列表
		JsonArray resultArray = new JsonArray();
		//如果有数据
		if(reader != null) {
			String lineString = null;//存储当前行的原始数据
			int rowIndex = 0;//处理的当前行号
			JsonObject lineJsonObject = null;//存储当前行转换为JSON对象后的数据
			
			try {
				while((lineString = reader.readLine()) != null)  {
					rowIndex++;
					
					if(rowIndex > startRow) {//只处理startRow开始的行，之前的忽略
						if(lineString.trim().length() == 0) {//忽略空行
							continue;
						}
						lineJsonObject = JsonUtils.toJsonObject(lineString);
						resultArray.add(lineJsonObject);
					}
				}
			} catch (Exception e) {
				logger.error("process json file failed!", e);
				return new JsonArray();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					//do nothing
				}
			}
		}
		return resultArray;
	}
	
	/**
	 * 解析json文件并进行逐行处理
	 * @param file
	 * @param startRow
	 * @param processor
	 */
	public static void process(MultipartFile file, Integer startRow, RowProcessor processor) {
		BufferedReader reader = getJSONFileReader(file);
		process(reader,startRow, processor);
	}
	
	/**
	 * 解析json文件并进行逐行处理
	 * @param file
	 * @param startRow
	 * @param processor
	 */
	public static void processObjectType(Object file, Integer startRow, RowProcessor processor) {
		BufferedReader reader = null;
		if(file instanceof File) {
			reader = getJSONFileReader((File)file);
		}else if(file instanceof MultipartFile) {
			reader = getJSONFileReader((MultipartFile)file);
		}
		process(reader, startRow, processor);
	}
	
	/**
	 * 解析excel文件并进行逐行处理
	 * @param file
	 * @param startRow
	 * @param processor
	 */
	public static void process(File file, Integer startRow, RowProcessor processor) {
		BufferedReader reader = getJSONFileReader(file);
		process(reader, startRow, processor);
	}
	
	/**
	 * 解析json文件并进行逐行处理
	 * @param reader
	 * @param startRow
	 * @param processor
	 */
	public static void process(BufferedReader reader, Integer startRow, RowProcessor processor) {
		//如果有数据
		if (reader != null) {
			String lineString = null;// 存储当前行的原始数据
			int rowIndex = 0;// 处理的当前行号
			JsonObject lineJsonObject = null;// 存储当前行转换为JSON对象后的数据

			try {
				while ((lineString = reader.readLine()) != null) {
					rowIndex++;

					if (rowIndex > startRow) {// 只处理startRow开始的行，之前的忽略
						if (lineString.trim().length() == 0) {// 忽略空行
							continue;
						}
						lineJsonObject = JsonUtils.toJsonObject(lineString);
						//调用行处理器进行逐行处理
						processor.process(rowIndex-1, lineJsonObject);
						
					}
				}
			} catch (Exception e) {
				logger.error("process json file failed!", e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
	
	
	/**
	 * 导出json格式文件
	 * @param objectArray
	 * @param fileName
	 * @return
	 * @throws EngineException
	 */
	public static File export(JsonArray objectArray, String fileName)throws EngineException {
		if(objectArray == null || objectArray.isEmpty()) {
			return null;
		}
		if(fileName == null || fileName.trim().length() == 0) {
			fileName = DEFAULT_FILE_NAME;
		}
		//创建临时文件
		Path path = LocalFileAbility.getInstance().getTempFilePath(fileName);
		try {
			path = Files.createFile(path);
		} catch (IOException e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "create temp json file failed!", e.getMessage(), e);
		}
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())))) { 
			JsonObject rowObject = null;
			int totalRows = objectArray.size();
			//循环处理并生成每一行的数据
			for(int i=0; i<totalRows; i++) {
				rowObject = objectArray.get(i).getAsJsonObject();
				writer.write(rowObject.toString());
				writer.newLine();
			}
			return path.toFile();
		} catch (Exception e) {
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "export json file failed!", e.getMessage(), e);
		}
	}
}
