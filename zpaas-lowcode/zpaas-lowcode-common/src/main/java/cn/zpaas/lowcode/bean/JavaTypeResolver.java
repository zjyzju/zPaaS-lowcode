package cn.zpaas.lowcode.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理Sql类型与Java类型之间的映射关系
 *
 * @author zjy
 * createTime 2025年04月18日-09:35:08
 */
public class JavaTypeResolver {
    private Map<Integer, String> typeMap = null;
	
	private static JavaTypeResolver instance = new JavaTypeResolver();
    private static final String BYTE_ARRAY = "byte[]";
	
	private JavaTypeResolver() {
		typeMap = new HashMap<>();

        typeMap.put(Types.ARRAY, Object.class.getName());
        typeMap.put(Types.BIGINT, Long.class.getName());
        typeMap.put(Types.BINARY, BYTE_ARRAY); 
        typeMap.put(Types.BIT, Boolean.class.getName());
        typeMap.put(Types.BLOB, BYTE_ARRAY); 
        typeMap.put(Types.BOOLEAN, Boolean.class.getName());
        typeMap.put(Types.CHAR, String.class.getName());
        typeMap.put(Types.CLOB, String.class.getName());
        typeMap.put(Types.DATALINK, Object.class.getName());
        typeMap.put(Types.DATE, Date.class.getName());
        typeMap.put(Types.DECIMAL, BigDecimal.class.getName());
        typeMap.put(Types.DISTINCT, Object.class.getName());
        typeMap.put(Types.DOUBLE, Double.class.getName());
        typeMap.put(Types.FLOAT, Double.class.getName());
        typeMap.put(Types.INTEGER, Integer.class.getName());
        typeMap.put(Types.JAVA_OBJECT, Object.class.getName());
        typeMap.put(Types.LONGNVARCHAR, String.class.getName());
        typeMap.put(Types.LONGVARBINARY, BYTE_ARRAY);
        typeMap.put(Types.LONGVARCHAR, String.class.getName());
        typeMap.put(Types.NCHAR, String.class.getName());
        typeMap.put(Types.NCLOB, String.class.getName());
        typeMap.put(Types.NVARCHAR, String.class.getName());
        typeMap.put(Types.NULL, Object.class.getName());
        typeMap.put(Types.NUMERIC, BigDecimal.class.getName());
        typeMap.put(Types.OTHER, Object.class.getName());
        typeMap.put(Types.REAL, Float.class.getName());
        typeMap.put(Types.REF, Object.class.getName());
        typeMap.put(Types.SMALLINT, Short.class.getName());
        typeMap.put(Types.STRUCT, Object.class.getName());
        typeMap.put(Types.TIME, Date.class.getName());
        typeMap.put(Types.TIMESTAMP, Date.class.getName());
        typeMap.put(Types.TINYINT, Byte.class.getName());
        typeMap.put(Types.VARBINARY, BYTE_ARRAY);
        typeMap.put(Types.VARCHAR, String.class.getName());
        typeMap.put(Types.TIME_WITH_TIMEZONE, "java.time.OffsetTime");
        typeMap.put(Types.TIMESTAMP_WITH_TIMEZONE, "java.time.OffsetDateTime"); 
	}
	
	public static JavaTypeResolver getInstance() {
		return instance;
	}
	
	public String resolveJavaType(Integer sqlType) {
		return typeMap.get(sqlType);
	}
}
