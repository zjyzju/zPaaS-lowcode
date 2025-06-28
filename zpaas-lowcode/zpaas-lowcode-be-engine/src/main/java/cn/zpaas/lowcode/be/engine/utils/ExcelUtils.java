package cn.zpaas.lowcode.be.engine.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.DateUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.MapUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.be.engine.ability.LocalFileAbility;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * excel解析处理工具类
 *
 * @author zjy
 * createTime 2025年04月27日-09:46:53
 */
public class ExcelUtils {
	public static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	
	private static final String CFG_MAPPING_NAME_KEY = "mappingName"; //映射属性名存放的Key
	private static final String CFG_NEED_MERGE_KEY = "needMerge"; //是否需要合并字段存放的Key
	private static final String CFG_IS_SUM_KEY = "isSum"; //是否累加字段存放的Key
	private static final String CFG_SUM_BY_COLUMN_INDEX_KEY = "sumByColumnIndex"; //累加维度列序号字段存放的Key
	private static final String CFG_SUM_COLUMN_INDEX_KEY = "sumColumnIndex"; //累加列序号字段存放的Key
	
	public static final String DEFAULT_FILE_NAME = "untitled.xlsx";
	public static final String ANNOTATIONS_ADD_BY_LCDP = "annotationsAddByLCDP";//用来存放单元格批注信息列的列名
	public static final String ANNOTATIONS_SPLIT = ";";//单元格批注信息的分隔符
	
	/**
	 * 加载excel文件
	 * @param excelFile
	 * @return
	 */
	public static Workbook getWorkBook(File excelFile) {
		Workbook workbook = null;
        try {
            // 根据文件类型加载Workbook实现类对象
            workbook = WorkbookFactory.create(excelFile);          
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return workbook;
    }
	
	/**
	 * 加载excel文件
	 * @param excelFile
	 * @return
	 */
	public static Workbook getWorkBook(MultipartFile excelFile) {
		Workbook workbook = null;
        try {
            // 根据文件类型加载Workbook实现类对象
            workbook = WorkbookFactory.create(excelFile.getInputStream());          
        } catch (IOException e) {
        	logger.error("read file failed!", e);
        }
        return workbook;
    }
	
	/**
	 * 加载excel文件
	 * @param excelFilePath
	 * @return
	 */
	public static Workbook getWorkBook(Path excelFilePath) {
		return getWorkBook(excelFilePath.toFile());
	}
	
	
	
	/**
	 * 获取当前单元格合并的情况，返回null表示无合并
	 * @param sheet
	 * @param cell
	 * @return
	 */
	private static CellRangeAddress getCellRangeAddress(Sheet sheet, Cell cell) {
		//获取sheet中合并单元合的总数量
		int sheetMergeCount = sheet.getNumMergedRegions();
		//获取当前单元格的行号和列号
		int row = cell.getRowIndex();
		int column = cell.getColumnIndex();
		//循环所有的合并单无格
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			//判断当前单元格是否在合并单元格的范围内，如果是则返回CellRangeAddress对象
			if (row >= range.getFirstRow() && row <= range.getLastRow()) {
				if (column >= range.getFirstColumn() && column <= range.getLastColumn()) {
					return range;
				}
			}
		}
		//如果当前单元格不在所有有合并单元格范围内，则返回null
		return null;
	}
	
	/**
	 * 解析excel文件
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(MultipartFile file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow) {
		Workbook workbook = getWorkBook(file);
		return process(workbook, sheetIndex, columnMapping, startRow);
	}
	
	/**
	 * 解析excel文件
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray processObjectType(Object file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow) {
		Workbook workbook = null;
		if(file instanceof File) {
			 workbook = getWorkBook((File)file);
		}else if(file instanceof MultipartFile) {
			workbook = getWorkBook((MultipartFile)file);
		}
		return process(workbook, sheetIndex, columnMapping, startRow);
	}
	
	/**
	 * 解析excel文件
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(File file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow) {
		Workbook workbook = getWorkBook(file);
		return process(workbook, sheetIndex, columnMapping, startRow);
	}
	
	/**
	 * 解析excel文件
	 * @param excelFile
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @return
	 */
	public static JsonArray process(Workbook workbook, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow) {
		//初始化返回列表
		JsonArray resultArray = new JsonArray();
		//如果有数据且是有效的sheet页
		if(workbook != null && sheetIndex <= workbook.getNumberOfSheets()) {
			//加载对应的sheet页
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			
			JsonObject rowJsonObject = null;
			String columnMappingName = null;
			if(columnMapping == null) {
				columnMapping = new HashMap<>();
			}
			//处理表头的情况，开始处理的行数
			if(startRow == null) {
				startRow = 0;
			}
			//进行循环处理
			for(int j=startRow; j<=sheet.getLastRowNum(); j++) {
				//加载第j行数据
				Row row = sheet.getRow(j);
				rowJsonObject = new JsonObject();
				resultArray.add(rowJsonObject);
				for(int k=0; k<row.getLastCellNum(); k++) {
					//取<j,k>单元格
					Cell cell = row.getCell(k);
					Object cellValue = null;
					//根据类型获取单元格中的值
					if(CellType.BOOLEAN.equals(cell.getCellType())) {
						cellValue = Boolean.valueOf(cell.getBooleanCellValue());
					}else if(CellType.NUMERIC.equals(cell.getCellType())) {
						cellValue = cell.getNumericCellValue();
					}else if(CellType.FORMULA.equals(cell.getCellType())) {
						cellValue = cell.getNumericCellValue();
					}else  {
						cellValue = cell.getStringCellValue();
					}
					String commentValue = null;
					if(cell.getCellComment() != null) {
						commentValue = cell.getCellComment().getString().getString();
					}
					
					//处理合并单元格的情况
					CellRangeAddress rangeAddress = getCellRangeAddress(sheet, cell);
					if(rangeAddress != null) {//表示当前单元格属于该合并单元格
						//当前单元格如果是合并单元格的第一个单元格（最左上角），不需要特殊处理；如果不是，则需要取第一个单元格的值
						if(cell.getRowIndex() > rangeAddress.getFirstRow() || cell.getColumnIndex() > rangeAddress.getFirstColumn()) {
							//取前面已经解析的合并单元格的第一行数据
							JsonObject firstRowOfMergedRange = resultArray.get(rangeAddress.getFirstRow()-startRow).getAsJsonObject();
							//取取前面已经解析的合并单元格的第一列的列名
							String firstColumnNameOfMergedRange = columnMapping.get(rangeAddress.getFirstColumn());
							//使用合并单元格的第一个单元格（最左上角）的值作为当前单元格的值
							cellValue = firstRowOfMergedRange.get(firstColumnNameOfMergedRange);
						}
					}
					//取对应的列映射名，为空时使用序号
					columnMappingName = columnMapping.get(k);
					if(columnMappingName == null) {
						columnMappingName = String.valueOf(k);
					}
					//设置单元格的值
					rowJsonObject.add(columnMappingName, JsonUtils.toJsonElement(cellValue));	
					if(!StringUtils.isBlank(commentValue)) {//当批注信息不为空时
						if(rowJsonObject.get(ANNOTATIONS_ADD_BY_LCDP) != null) {
							rowJsonObject.addProperty(ANNOTATIONS_ADD_BY_LCDP, JsonUtils.getString(rowJsonObject, ANNOTATIONS_ADD_BY_LCDP) + ANNOTATIONS_SPLIT + commentValue);
						}else {
							rowJsonObject.addProperty(ANNOTATIONS_ADD_BY_LCDP, commentValue);
						}
					}
					
				}
				
			}
		}
		return resultArray;
	}
	
	/**
	 * 解析excel文件并进行逐行处理
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(MultipartFile file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		Workbook workbook = getWorkBook(file);
		process(workbook, sheetIndex, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析excel文件并进行逐行处理
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void processObjectType(Object file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		Workbook workbook = null;
		if(file instanceof File) {
			 workbook = getWorkBook((File)file);
		}else if(file instanceof MultipartFile) {
			workbook = getWorkBook((MultipartFile)file);
		}
		process(workbook, sheetIndex, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析excel文件并进行逐行处理
	 * @param file
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(File file, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		Workbook workbook = getWorkBook(file);
		process(workbook, sheetIndex, columnMapping, startRow, processor);
	}
	
	/**
	 * 解析excel文件并进行逐行处理
	 * @param workbook
	 * @param sheetIndex
	 * @param columnMapping
	 * @param startRow
	 * @param processor
	 */
	public static void process(Workbook workbook, int sheetIndex, Map<Integer, String> columnMapping, Integer startRow, RowProcessor processor) {
		//如果有数据且是有效的sheet页
		if(workbook != null && sheetIndex <= workbook.getNumberOfSheets()) {
			//加载对应的sheet页
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			
			JsonObject rowJsonObject = null;
			String columnMappingName = null;
			if(columnMapping == null) {
				columnMapping = new HashMap<>();
			}
			//处理表头的情况，开始处理的行数
			if(startRow == null) {
				startRow = 0;
			}
			//进行循环处理
			for(int j=startRow; j<=sheet.getLastRowNum(); j++) {
				//加载第j行数据
				Row row = sheet.getRow(j);
				rowJsonObject = new JsonObject();
				for(int k=0; k<row.getLastCellNum(); k++) {
					//取<j,k>单元格
					Cell cell = row.getCell(k);
					
					//取对应的列映射名，为空时使用序号
					columnMappingName = columnMapping.get(k);
					if(columnMappingName == null) {
						columnMappingName = String.valueOf(k);
					}
					//根据类型获取单元格中的值
					if(CellType.BOOLEAN.equals(cell.getCellType())) {
						rowJsonObject.addProperty(columnMappingName, Boolean.valueOf(cell.getBooleanCellValue()));
					}else if(CellType.NUMERIC.equals(cell.getCellType())) {
						rowJsonObject.addProperty(columnMappingName, cell.getNumericCellValue());
					}else if(CellType.FORMULA.equals(cell.getCellType())) {
						rowJsonObject.addProperty(columnMappingName, cell.getNumericCellValue());
					} else  {
						rowJsonObject.addProperty(columnMappingName, cell.getStringCellValue());
					}
					
					String commentValue = null;
					if(cell.getCellComment() != null) {
						commentValue = cell.getCellComment().getString().getString();
					}
					if(commentValue != null && commentValue.trim().length() > 0) {//当批注信息不为空时
						if(rowJsonObject.get(ANNOTATIONS_ADD_BY_LCDP) != null) {
							rowJsonObject.addProperty(ANNOTATIONS_ADD_BY_LCDP, JsonUtils.getString(rowJsonObject, ANNOTATIONS_ADD_BY_LCDP) + ANNOTATIONS_SPLIT + commentValue);
						}else {
							rowJsonObject.addProperty(ANNOTATIONS_ADD_BY_LCDP, commentValue);
						}
					}
				}
				//调用行处理器进行逐行处理
				try {
					processor.process(j, rowJsonObject);
				} catch (EngineException e) {//用于中断循环
					break;
				}
			}
		}
	}
	
	
	
	public static File export(JsonArray objectArray, Map<Integer, JsonObject> columnMapping, boolean exportHeader, 
			Map<Integer, String> headerMapping, File templateFile, Integer sheetIndex, Integer startRowIndex, String fileName)throws EngineException {
		Workbook workbook = null;
		Sheet sheet = null;
		try {
			if(JsonUtils.isEmpty(objectArray)) {
				return null;
			}
			if(MapUtils.isEmpty(columnMapping)) {
				logger.error("columnMapping is empty!");
				throw new EngineException(ResultStatus.BUSINESS_ERROR, "columnMapping is empty!");
			}
			if(StringUtils.isBlank(fileName)) {
				fileName = DEFAULT_FILE_NAME;
			}
			//取最大的列序号作为循环次数
			Integer maxColumnIndex = columnMapping.keySet().stream().max((a,b)->{
				return a-b;
			}).get();
			int columnSize = maxColumnIndex+1;
			//不导出表头且有配置模板文件时
			if(!exportHeader && templateFile != null) {
				workbook = getWorkBook(templateFile);
				int exportSheetIndex = 0;
				if(sheetIndex != null) {
					exportSheetIndex = sheetIndex;
				}
				sheet = workbook.getSheetAt(exportSheetIndex);
			}else {//否则创建新的
				workbook = WorkbookFactory.create(true);
				sheet = workbook.createSheet();
			}
			int rowIndex = 0;
			//设置默认样式
			CellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			//如果需要生成表头
			if(exportHeader) {
				if(headerMapping == null) {
					logger.error("headerMapping is null!");
					throw new EngineException(ResultStatus.BUSINESS_ERROR, "headerMapping is null!");
				}
				//创建表头对应的行
				Row row = sheet.createRow(rowIndex++);
				
				//循环生成表头的每个单元格
				String headerName = null;
				for(int i=0; i<columnSize; i++) {
					Cell cell = row.createCell(i);
					//获取表头的名字，如果为空，则直接使用序号
					headerName = headerMapping.get(i);
					if(StringUtils.isBlank(headerName)) {
						headerName = String.valueOf(i);
					}
					cell.setCellValue(headerName);
					cell.setCellStyle(style);
				}				
			}
			//有配置起始行号的情况下，使用配置的起始行号
			if(startRowIndex != null) {
				rowIndex = startRowIndex;
			}
			
			JsonObject rowObject = null;
			String attrName = null;			
			JsonObject columnCfg = null;
			Row row = null;
			Object cellObject = null;
			boolean needMerge, isSum;//是否需要合并，是否累加
			Integer sumByColumnIndex = null, sumColumnIndex = null;//累加维度列，累加列
			//用来进行单元格合并的控制，目前只支持单列的行之间合并<columnIndex,startRowMerged>
			Map<Integer, Integer> mergeMap = new HashMap<>();
			//当前行已经合并列的Map，用来控制isSum为true的列，<sumByColumnIndex, currentRowIndex>
			Map<Integer, Integer> currentRowMergedColumnMap = new HashMap<>();
			Integer startRowMerged = null;//合并的起始行
			Integer endRowMerged = null;//合并的结束行
			String sumColumnAttrName = null;//累加的列
			float sumValueFloat;//合并累加值浮点类型
			long sumValueLong;//合并累加值整数类型
			boolean isFloat;//是否浮点类型
			Object startCellObject = null;//合并起始单元格的值
			CellRangeAddress mergedRange = null;
			int totalRows = objectArray.size();
			//循环处理并生成每一行的数据
			for(int i=0; i<totalRows; i++) {
				rowObject = objectArray.get(i).getAsJsonObject();
				row = sheet.createRow(i+rowIndex);
				//循环处理并生成每一单元格
				for(int j=0; j<columnSize; j++) {					
					//获取当前列的配置信息
					columnCfg = columnMapping.get(j);
					
					if(columnCfg != null) {
						//获取单元格对应的属性名
						attrName = JsonUtils.getString(columnCfg, CFG_MAPPING_NAME_KEY);
						if(StringUtils.isBlank(attrName)) {
							logger.error("the columnMapping of column {} is null!", j);
							throw new EngineException(ResultStatus.BUSINESS_ERROR, "the columnMapping of column "+j+" is null!!");
						}
						cellObject = JsonUtils.getString(rowObject, attrName);
						
						//处理单元格合并的问题
						needMerge = JsonUtils.getBoolean(columnCfg, CFG_NEED_MERGE_KEY);//是否需要合并
						isSum = JsonUtils.getBoolean(columnCfg, CFG_IS_SUM_KEY);//是否累加
                        if(columnCfg.get(CFG_SUM_BY_COLUMN_INDEX_KEY) != null) {
                            sumByColumnIndex = JsonUtils.getInteger(columnCfg, CFG_SUM_BY_COLUMN_INDEX_KEY);//累加的维度列序号
                        }
						
						sumColumnIndex = JsonUtils.getInteger(columnCfg, CFG_SUM_COLUMN_INDEX_KEY);//累加的列序号
						//配置为需要合并的情况
						if(needMerge) {
							//尝试获取当前列开始合并的行
							startRowMerged = mergeMap.get(j);
							//当startRowMerged不为空时，表示已经有开始的行
							if(startRowMerged != null) {
								//当前行的值为空时，结束当前列的合并状态，合并之前的单元格
								if((cellObject == null || cellObject.toString().trim().length() == 0) && !isSum) {
									//合并起始行到当前行的前一行
									if((startRowMerged+rowIndex) < (i-1+rowIndex)) {//超过一行时才需要合并
										mergedRange = new CellRangeAddress(startRowMerged+rowIndex, i-1+rowIndex, j, j);
										sheet.addMergedRegion(mergedRange);
										//设置合并单元格的值
										if(startCellObject instanceof Date) {
											sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(DateUtils.dateTimeString((Date)startCellObject));
										}else {
											if(cellObject != null && startCellObject != null) {
												sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(startCellObject.toString());
											}else {
												sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue("");
											}
										}
										sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellStyle(style);
									}
									//设置触发合并的列的情况，此处的i-1是objectArray的序号，对应sheet的行需要加上rowIndex
									currentRowMergedColumnMap.put(j, i-1);
									//移除已合并完成的合并信息
									mergeMap.remove(j);
									//当前值为空时，不需要发起新的合并
								}else {//当前行的值不为空时
									//值相同时才进行合并的情况
									if(!isSum) {//isSum为false的情况下，合并列的值必须相同
										//当前行的值不为空且与开始行的值不相等时，结束当前列的合并状态，合并之前的单元格
										startCellObject = JsonUtils.getString(objectArray.get(startRowMerged).getAsJsonObject(), attrName);
										if(cellObject != null && !cellObject.equals(startCellObject)) {
											//合并起始行到当前行的前一行
											if((startRowMerged+rowIndex) < (i-1+rowIndex)) {//超过一行时才需要合并
												mergedRange = new CellRangeAddress(startRowMerged+rowIndex, i-1+rowIndex, j, j);
												sheet.addMergedRegion(mergedRange);
												//设置合并单元格的值
												if(startCellObject instanceof Date) {
													sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(DateUtils.dateTimeString((Date)startCellObject));
												}else {
													sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(startCellObject.toString());
												}
												sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellStyle(style);
											}
											
											//设置触发合并的列的情况，此处的i-1是objectArray的序号，对应sheet的行需要加上rowIndex
											currentRowMergedColumnMap.put(j, i-1);
											//移除已合并完成的合并信息
											mergeMap.remove(j);
											//新的合并，只有当前行的值不为空时，才参与合并
											if(cellObject != null && cellObject.toString().trim().length() > 0) {
												mergeMap.put(j, i);//设置合并状态
											}
										}
									}else {//值要加的合并情况
										endRowMerged = currentRowMergedColumnMap.get(sumByColumnIndex);
										//判断是需要触发合并操作
										//需要进行合并操作，如果sumByColumnIndex不为空，取到的endRowMerged不为空且结束行是前一行时；或者sumByColumnIndex为空且当前行是最后一行时
										if((endRowMerged != null && endRowMerged == (i-1)) || (sumByColumnIndex == null && i == (totalRows-1))) {
											//获取要累加列的映射属性名
											sumColumnAttrName = attrName;//默认使用当前列的映射属性名
											if(sumColumnIndex != null) {//如果有配置累加列，则使用累加列对应的映射属性名
												sumColumnAttrName = JsonUtils.getString(columnMapping.get(sumColumnIndex), CFG_MAPPING_NAME_KEY);
											}
											int endRow = i-1;//设置结束行
											if(sumByColumnIndex == null) {//如果sumByColumnIndex为空，则结束行是最后一行
												endRow = totalRows-1;
											}
											//合并起始行到当前行的前一行
											if((startRowMerged+rowIndex) < (endRow+rowIndex)) {//超过一行时才需要合并
												mergedRange = new CellRangeAddress(startRowMerged+rowIndex, endRow+rowIndex, j, j);
												sheet.addMergedRegion(mergedRange);
												
												//计算合并值
												sumValueFloat = 0;
												sumValueLong = 0;
												isFloat = false;
												for(int k=startRowMerged; k<=endRow; k++) {
													JsonObject sumRow = objectArray.get(k).getAsJsonObject();
													Object sumCellValue = JsonUtils.getObject(sumRow, sumColumnAttrName);
													if(sumCellValue == null) {
														continue;
													}else if(sumCellValue instanceof Float || sumCellValue instanceof Double) {
														isFloat = true;
														sumValueFloat = sumValueFloat + JsonUtils.getFloat(sumRow, sumColumnAttrName);
													}else {
														sumValueLong = sumValueLong + JsonUtils.getLong(sumRow, sumColumnAttrName);
													}
												}
												//设置合并单元格的值
												if(isFloat) {
													sumValueFloat = sumValueFloat + sumValueLong;
													sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(sumValueFloat);
												}else {
													sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellValue(sumValueLong);
												}
												sheet.getRow(startRowMerged+rowIndex).getCell(j).setCellStyle(style);
											}
											//移除已合并完成的合并信息
											mergeMap.remove(j);
											//新的合并，只有当前行的值不为空时，才参与合并
											if(cellObject != null && cellObject.toString().trim().length() > 0) {
												mergeMap.put(j, i);//设置合并状态
											}
											//如果是累加的列，但未指定累加维度，表示累加所有的行，这种情况只需要初次设置就行
										}
									}
								}
								
							}else {//当startRowMerged为空时，表示还没有开始的行，此处的i是objectArray的序号，对应sheet的行需要加上rowIndex
								//只有当前行的值不为空时，才参与合并
								if(cellObject != null && cellObject.toString().trim().length() > 0) {
									mergeMap.put(j, i);//设置合并状态
								}else {
									if(isSum && sumByColumnIndex == null) {//如果是累加的列，但未指定累加维度，表示累加所有的行
										mergeMap.put(j, i);//设置合并状态
									}
								}
								
							}
						}
						
						if(cellObject instanceof Date) {
							row.createCell(j).setCellValue(DateUtils.dateTimeString((Date) cellObject));
						}else {
							if(cellObject != null) {
								row.createCell(j).setCellValue(cellObject.toString());
							}else {
								row.createCell(j).setCellValue("");
							}
						}
					}else {
						logger.info("the columnCfg of column {} is null!", j);
						//throw new EngineException(ResultStatus.BUSINESS_ERROR, "the columnCfg of column "+j+" is null!!");
					}
				}
			}
			
			Path path = LocalFileAbility.getInstance().getTempFilePath(fileName);
			path = Files.createFile(path);
			workbook.write(new FileOutputStream(path.toFile()));
			
			return path.toFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EngineException(ResultStatus.INTERNAL_ERROR, "export excel file failed!", e.getMessage(), e);
		}finally {
			try {
                if(workbook != null) {
                    workbook.close();
                }
			} catch (IOException e) {
				
			}
		}
	}
}