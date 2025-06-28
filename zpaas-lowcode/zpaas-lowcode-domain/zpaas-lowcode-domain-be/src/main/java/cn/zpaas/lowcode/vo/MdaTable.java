package cn.zpaas.lowcode.vo;

import java.util.List;

/**
 * 模型驱动辅助生成数据库表vo类
 *
 * @author zjy
 * createTime 2025年04月21日-18:06:12
 */
public class MdaTable {

	private String tableCatalog; //table catalog
	private String tableName;//表名
	private String tableSchema;//table schema
	private String tableType;//表类型， TABLE/VIEW
	private String tableRemarks;//备注
	private List<MdaColumn> tableColumns;//字段列表
	public String getTableCatalog() {
		return tableCatalog;
	}
	public void setTableCatalog(String tableCatalog) {
		this.tableCatalog = tableCatalog;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableSchema() {
		return tableSchema;
	}
	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getTableRemarks() {
		return tableRemarks;
	}
	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks = tableRemarks;
	}
	public List<MdaColumn> getTableColumns() {
		return tableColumns;
	}
	public void setTableColumns(List<MdaColumn> tableColumns) {
		this.tableColumns = tableColumns;
	}
	
	
}
