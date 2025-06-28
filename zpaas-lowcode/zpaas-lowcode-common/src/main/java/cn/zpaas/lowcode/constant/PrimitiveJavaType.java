package cn.zpaas.lowcode.constant;

/**
 * 原始Java类枚举类
 *
 * @author zjy
 * createTime 2025年04月21日-17:40:18
 */
public enum PrimitiveJavaType {
    STRING_TYPE("java.lang.String", "String"),
	CHARACTER_TYPE("java.lang.Character", "Character"),
	DATE_TYPE("java.util.Date", "Date"),
	BOOLEAN_TYPE("java.lang.Boolean", "Boolean"),
	BYTE_TYPE("java.lang.Byte", "Byte"),
	INTEGER_TYPE("java.lang.Integer", "Integer"),
	LONG_TYPE("java.lang.Long", "Long"),
	FLOAT_TYPE("java.lang.Float","Fload"),
	DOUBLE_TYPE("java.lang.Double", "Double"),
	BIGINTEGER_TYPE("java.math.BigInteger", "BigInteger"),
	BIGDECIMAL_TYPE("java.math.BigDecimal", "BigDecimal");
	
	private PrimitiveJavaType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
		return name;
	}
}
