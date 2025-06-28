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
import java.util.Map;

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
 * @author zjy
 * txt解析处理工具类
 */
public class TxtUtils {
	public static Logger logger = LoggerFactory.getLogger(TxtUtils.class);
	
	private static final String CFG_MAPPING_NAME_KEY = "mappingName"; //映射属性名存放的Key
	
	public static final String DEFAULT_FILE_NAME = "untitled.txt";
	
	/**
	 * 加载txt文件
	 * @param txtFile
	 * @return
	 */
	public static BufferedReader getTxtBufferedReader(File txtFile) {
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile)));          
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return reader;
    }
	
	/**
	 * 加载txt文件
	 * @param txtFile
	 * @return
	 */
	public static BufferedReader getTxtBufferedReader(MultipartFile txtFile) {
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(txtFile.getInputStream()));         
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return reader;
    }
	
	/**
	 * 加载txt文件
	 * @param txtFilePath
	 * @return
	 */
	public static BufferedReader getTxtBufferedReader(Path txtFilePath) {
		return getTxtBufferedReader(txtFilePath.toFile());
	}
	
	/**
	 * 解析txt文件
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(MultipartFile file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow) {
		BufferedReader reader = getTxtBufferedReader(file);
		return process(reader, splitSymbol, columnMapping, startRow);
	}
	
	/**
	 * 解析txt文件
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray processObjectType(Object file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow) {
		BufferedReader reader = null;
		if(file instanceof File) {
			 reader = getTxtBufferedReader((File)file);
		}else if(file instanceof MultipartFile) {
			reader = getTxtBufferedReader((MultipartFile)file);
		}
		return process(reader, splitSymbol, columnMapping, startRow);
	}
	
	/**
	 * 解析txt文件
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(File file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow) {
		BufferedReader reader = getTxtBufferedReader(file);
		return process(reader, splitSymbol, columnMapping, startRow);
	}
	
	/**
	 * 解析txt文件
	 * @param txtFile
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(BufferedReader reader, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow) {
		//初始化返回列表
		JsonArray resultArray = new JsonArray();
		//如果有数据
		if(reader != null) {
			String lineString = null; //存储当前行的原始数据
			String[] columnValues = null; //存储使用分隔符拆分后列值数组
			String columnMappingName = null; //存储映射后的列名
			int rowIndex = 0;//处理的当前行号
			JsonObject lineJsonObject = null;//存储当前行转换为JSON对象后的数据
			
			try {
				while((lineString = reader.readLine()) != null)  {
					rowIndex++;
					
					if(rowIndex > startRow) {//只处理startRow开始的行，之前的忽略
						if(lineString.trim().length() == 0) {//忽略空行
							continue;
						}
						columnValues = lineString.split(splitSymbol);//使用分隔符拆分字符串
						if(columnValues != null && columnValues.length > 0) {
							lineJsonObject = new JsonObject();
							for(int k = 0; k< columnValues.length; k++) {//循环拆分后的例值数组
								//获取列对应的映射名
								columnMappingName = columnMapping.get(k);
								if(columnMappingName == null) {
									columnMappingName = String.valueOf(k);//如果未获取到有效的列映射名，默认使用列序号
								}
								//设置到json对象中，列映射名-》列值
								lineJsonObject.addProperty(columnMappingName, columnValues[k]);	
							}
							resultArray.add(lineJsonObject);//加入结果列表
						}
					}
				}
			} catch (Exception e) {
				logger.error("process txt file failed!", e);
				return null;
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
	 * 解析txt文件并进行逐行处理
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(MultipartFile file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		BufferedReader reader = getTxtBufferedReader(file);
		process(reader, splitSymbol, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析txt文件并进行逐行处理
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void processObjectType(Object file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		BufferedReader reader = null;
		if(file instanceof File) {
			 reader = getTxtBufferedReader((File)file);
		}else if(file instanceof MultipartFile) {
			reader = getTxtBufferedReader((MultipartFile)file);
		}
		process(reader, splitSymbol, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析txt文件并进行逐行处理
	 * @param file
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(File file, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		BufferedReader reader = getTxtBufferedReader(file);
		process(reader, splitSymbol, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析txt文件并进行逐行处理
	 * @param reader
	 * @param splitSymbol
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(BufferedReader reader, String splitSymbol, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		// 如果有数据
		if (reader != null) {
			String lineString = null; // 存储当前行的原始数据
			String[] columnValues = null; // 存储使用分隔符拆分后列值数组
			String columnMappingName = null; // 存储映射后的列名
			int rowIndex = 0;// 处理的当前行号
			JsonObject lineJsonObject = null;// 存储当前行转换为JSON对象后的数据

			try {
				while ((lineString = reader.readLine()) != null) {
					rowIndex++;

					if (rowIndex > startRow) {// 只处理startRow开始的行，之前的忽略
						if (lineString.trim().length() == 0) {// 忽略空行
							continue;
						}
						columnValues = lineString.split(splitSymbol);// 使用分隔符拆分字符串
						if (columnValues != null && columnValues.length > 0) {
							lineJsonObject = new JsonObject();
							for (int k = 0; k < columnValues.length; k++) {// 循环拆分后的例值数组
								// 获取列对应的映射名
								columnMappingName = columnMapping.get(k);
								if (columnMappingName == null) {
									columnMappingName = String.valueOf(k);// 如果未获取到有效的列映射名，默认使用列序号
								}
								// 设置到json对象中，列映射名-》列值
								lineJsonObject.addProperty(columnMappingName, columnValues[k]);
							}
							//调用行处理器进行逐行处理
							try {
								processor.process(rowIndex-1, lineJsonObject);
							} catch (EngineException e) {//用于中断循环
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("process txt file failed!", e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
	
	
	
	public static File export(JsonArray objectArray, Map<Integer, JsonObject> columnMapping, boolean exportHeader, 
			Map<Integer, String> headerMapping, String splitSymbol, String fileName)throws EngineException {
		BufferedWriter writer = null;
		
		try {
			if(objectArray == null || objectArray.size() == 0) {
				return null;
			}
			if(columnMapping == null || columnMapping.isEmpty()) {
				logger.error("columnMapping is empty!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "columnMapping is empty!");
			}
			if(fileName == null || fileName.trim().length() == 0) {
				fileName = DEFAULT_FILE_NAME;
			}
			
			//创建临时文件
			Path path = LocalFileAbility.getInstance().getTempFilePath(fileName);
			path = Files.createFile(path);
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
			
			int columnSize = columnMapping.size();
			
			//如果需要生成表头
			if(exportHeader) {
				if(headerMapping == null) {
					logger.error("headerMapping is null!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "headerMapping is null!");
				}
				//创建表头对应的行
				String headerLine = "";
				
				//循环生成表头的每个单元格
				String headerName = null;
				for(int i=0; i<columnSize; i++) {
					//获取表头的名字，如果为空，则直接使用序号
					headerName = headerMapping.get(i);
					if(headerName == null || headerName.trim().length() == 0) {
						headerName = String.valueOf(i);
					}
					headerLine += headerName;
					if(i != columnSize-1) {//如果不是最后一列，则增加分隔符
						headerLine += splitSymbol;
					}
				}	
				writer.write(headerLine);
				writer.newLine();
			}
			
			JsonObject rowObject = null;
			String rowString = null;
			JsonObject columnCfg = null;
			String attrName = null;	
			String columnValue = null;
			int totalRows = objectArray.size();
			//循环处理并生成每一行的数据
			for(int i=0; i<totalRows; i++) {
				rowObject = objectArray.get(i).getAsJsonObject();
				rowString = "";
				//循环处理并生成每一单元格
				for(int j=0; j<columnSize; j++) {
					//获取当前列的配置信息
					columnCfg = columnMapping.get(j);
					
					if(columnCfg != null) {
						//获取列对应的属性名
						attrName = JsonUtils.getString(columnCfg, CFG_MAPPING_NAME_KEY);
						if(attrName == null || attrName.trim().length() == 0) {
							logger.error("the columnMapping of column {} is null!", j);
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "the columnMapping of column "+j+" is null!!");
						}
						columnValue = JsonUtils.getString(rowObject, attrName);
						if(columnValue == null) {//列值为null时，默认设置为空字符串
							columnValue = "";
						}
						
						rowString += columnValue;
						if(j != columnSize-1) {//如果不是最后一列，则增加分隔符
							rowString += splitSymbol;
						}
					}
				}
				writer.write(rowString);
				writer.newLine();
			}
			return path.toFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "export txt file failed!", e.getMessage(), e);
		}finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				//do nothing
			}
		}
	}
}

