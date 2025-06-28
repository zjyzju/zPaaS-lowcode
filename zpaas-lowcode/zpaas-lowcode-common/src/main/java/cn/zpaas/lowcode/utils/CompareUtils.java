package cn.zpaas.lowcode.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zpaas.lowcode.exception.CommException;

/**
 * 比较工具类
 *
 * @author zjy
 * createTime 2025年04月15日-17:26:02
 */
public class CompareUtils {
    private static Logger logger = LoggerFactory.getLogger(CompareUtils.class);
	
	public static final String CFG_CONDITION_OPERATOR_EQ = "EQ"; //EQ（等于）
	public static final String CFG_CONDITION_OPERATOR_NE = "NE"; //NE（不等于）
	public static final String CFG_CONDITION_OPERATOR_GT = "GT"; //GT（大于）
	public static final String CFG_CONDITION_OPERATOR_GE = "GE"; //GE（大于等于）
	public static final String CFG_CONDITION_OPERATOR_LT = "LT"; //LT（小于）
	public static final String CFG_CONDITION_OPERATOR_LE = "LE"; //LE（小于等于）

    private static final String NOT_SUPPORT_COMPARE_TYPE = "not support compare type!";
    private static final String INVALID_COMPARE_TYPE = "invalid compare type!";

    /**
     * 私有化构造方法
     */
    private CompareUtils() {

    }
	
	/**
	 * Boolean类比较方法
	 * @param condition 条件值
	 * @param value 比较值
	 * @param op 比较操作符
	 * @return boolean
	 * @throws CommException
	 */
	public static boolean compare(boolean condition, boolean value, String op) throws CommException{
		switch(op) {
			case CFG_CONDITION_OPERATOR_EQ:
				return (condition == value);

			case CFG_CONDITION_OPERATOR_NE:
				return (condition != value);

			case CFG_CONDITION_OPERATOR_GT:
				
            case CFG_CONDITION_OPERATOR_GE:

			case CFG_CONDITION_OPERATOR_LT:

			case CFG_CONDITION_OPERATOR_LE:
				logger.error(NOT_SUPPORT_COMPARE_TYPE);
				throw new CommException(NOT_SUPPORT_COMPARE_TYPE);

			default:
				logger.error(INVALID_COMPARE_TYPE);
				throw new CommException(INVALID_COMPARE_TYPE);
		}
	}
	
	/**
	 * String类比较方法
	 * @param condition 条件值
	 * @param value 比较值
	 * @param op 比较操作符
	 * @return boolean
	 * @throws CommException
	 */
	public static boolean compare(String condition, String value, String op) throws CommException{
		switch(op) {
			case CFG_CONDITION_OPERATOR_EQ:
				return (condition.equals(value));

			case CFG_CONDITION_OPERATOR_NE:
				return !(condition.equals(value));

			case CFG_CONDITION_OPERATOR_GT:
				return (condition.compareTo(value) > 0);

			case CFG_CONDITION_OPERATOR_GE:
				return (condition.compareTo(value) >= 0);

			case CFG_CONDITION_OPERATOR_LT:
				return (condition.compareTo(value) < 0);

			case CFG_CONDITION_OPERATOR_LE:
				return (condition.compareTo(value) <= 0);

			default:
				logger.error(INVALID_COMPARE_TYPE);
				throw new CommException(INVALID_COMPARE_TYPE);
		}
	}
	
	/**
	 * Date类比较方法
	 * @param condition 条件值
	 * @param value 比较值
	 * @param op 比较操作符
	 * @return boolean
	 * @throws CommException
	 */
	public static boolean compare(Date condition, Date value, String op) throws CommException{
		switch(op) {
			case CFG_CONDITION_OPERATOR_EQ:
				return (condition.equals(value));

			case CFG_CONDITION_OPERATOR_NE:
				return !(condition.equals(value));

			case CFG_CONDITION_OPERATOR_GT:
				return (condition.compareTo(value) > 0);

			case CFG_CONDITION_OPERATOR_GE:
				return (condition.compareTo(value) >= 0);

			case CFG_CONDITION_OPERATOR_LT:
				return (condition.compareTo(value) < 0);

			case CFG_CONDITION_OPERATOR_LE:
				return (condition.compareTo(value) <= 0);

			default:
				logger.error(INVALID_COMPARE_TYPE);
				throw new CommException(INVALID_COMPARE_TYPE);
		}
	}
	
	/**
	 * double类比较方法
	 * @param condition 条件值
	 * @param value 比较值
	 * @param op 比较操作符
	 * @return boolean
	 * @throws CommException
	 */
	public static boolean compare(double condition, double value, String op) throws CommException{
		switch(op) {
			case CFG_CONDITION_OPERATOR_EQ:
				return (condition == value);

			case CFG_CONDITION_OPERATOR_NE:
				return (condition != value);

			case CFG_CONDITION_OPERATOR_GT:
				return (condition > value);

			case CFG_CONDITION_OPERATOR_GE:
				return (condition >= value);

			case CFG_CONDITION_OPERATOR_LT:
				return (condition < value);

			case CFG_CONDITION_OPERATOR_LE:
				return (condition <= value);

			default:
				logger.error(INVALID_COMPARE_TYPE);
				throw new CommException(INVALID_COMPARE_TYPE);
		}
	}
	
	/**
	 * int类比较方法
	 * @param condition 条件值
	 * @param value 比较值
	 * @param op 比较操作符
	 * @return boolean
	 * @throws CommException
	 */
	public static boolean compare(int condition, int value, String op) throws CommException{
		switch(op) {
			case CFG_CONDITION_OPERATOR_EQ:
				return (condition == value);

			case CFG_CONDITION_OPERATOR_NE:
				return (condition != value);

			case CFG_CONDITION_OPERATOR_GT:
				return (condition > value);

			case CFG_CONDITION_OPERATOR_GE:
				return (condition >= value);

			case CFG_CONDITION_OPERATOR_LT:
				return (condition < value);

			case CFG_CONDITION_OPERATOR_LE:
				return (condition <= value);

			default:
				logger.error(INVALID_COMPARE_TYPE);
				throw new CommException(INVALID_COMPARE_TYPE);
		}
	}
}
