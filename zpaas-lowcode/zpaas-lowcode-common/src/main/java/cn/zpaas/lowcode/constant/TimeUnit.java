package cn.zpaas.lowcode.constant;

/**
 * 时间单位枚举类
 *
 * @author zjy
 * createTime 2025年04月15日-18:16:08
 */
public enum TimeUnit {
    //Y：年，M：月，D：日，W：周，H：小时，Mi：分，S：秒
	YEAR("Y", "年"),
	MONTH("M", "月"),
	DAY("D", "日"),
	WEEK("W", "周"),
	HOUR("H", "小时"),
	MINUTE("Mi", "分"),
	SECOND("S", "秒");
	
	
	private String code;
	private String name;
	
	private TimeUnit(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
