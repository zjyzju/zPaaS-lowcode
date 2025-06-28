package cn.zpaas.lowcode.bean;

/**
 * 分页参数类
 *
 * @author zjy
 * createTime 2025年04月15日-18:06:40
 */
public class PageParam {
    public static final String DESC_ORDER = "desc";//倒序
	public static final String ASC_ORDER = "asc";//正序
	
	//当前查询的页号
	private int pageNo;
	//每页查询的数量
	private int pageRows;
	//总条数
	private Long total;
	//排序条件
	private String sortName;
	//排序的方向
	private String sortOrder;
	
	//是否查询总数
	private boolean queryTotal;
	
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageRows() {
		return pageRows;
	}
	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public boolean isQueryTotal() {
		return queryTotal;
	}
	public void setQueryTotal(boolean queryTotal) {
		this.queryTotal = queryTotal;
	}
}
