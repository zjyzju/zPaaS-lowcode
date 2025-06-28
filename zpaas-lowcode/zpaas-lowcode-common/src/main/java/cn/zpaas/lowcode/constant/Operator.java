package cn.zpaas.lowcode.constant;

/**
 * 运算符枚举类
 *
 * @author zjy
 * createTime 2025年04月21日-17:39:14
 */
public enum Operator {
    PLUS("+", "加"),
	MINUS("-", "减"),
	MULTIPLY("*", "乘"),
	DIVIDE("/", "除");
	
	private Operator(String op, String name) {
		this.op = op;
		this.name = name;
	}
	
	private String op;
	private String name;

	public String getOp() {
		return op;
	}
	
    public String getName() {
		return name;
	}
}
