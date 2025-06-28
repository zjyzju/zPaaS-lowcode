package cn.zpaas.lowcode.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zpaas.lowcode.exception.CommException;

/**
 * 日期工具类
 *
 * @author zjy
 * createTime 2025年04月15日-17:50:10
 */
public class DateUtils {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_NO_MINUS = "yyyyMMdd";
    private static final String INVALID_DATE_FORMAT = "invalid datetime format!";
	private static final String DATE_TIME_FORMAT_14 = "yyyyMMddHHmmss";

    /**
     * 私有化构造方法
     */
    private DateUtils() {

    }

    /**
	 * 字符串转日期
	 * @param dateTime,格式为yyyy-MM-dd hh:mm:ss
	 * @return Date
	 * @throws CommException
	 */
	public static Date parseDateTime(String dateTime) throws CommException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		try {
			return format.parse(dateTime);
		} catch (ParseException e) {
			throw new CommException(INVALID_DATE_FORMAT, e);
		}
	}
	
	/**
	 * 日期转字符串
	 * @param date
	 * @return 格式为yyyy-MM-dd hh:mm:ss
	 */
	public static String dateTimeString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		return format.format(date);
	}
	
	/**
	 * 字符串转日期
	 * @param date,格式为yyyy-mm-dd
	 * @return Date
	 * @throws CommException
	 */
	public static Date parseDate(String date) throws CommException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			throw new CommException(INVALID_DATE_FORMAT, e);
		}
	}
	
	/**
	 * 字符串转日期
	 * @param date,格式为yyyyMMdd
	 * @return Date
	 * @throws CommException
	 */
	public static Date parseDate2(String date) throws CommException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_NO_MINUS);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			throw new CommException(INVALID_DATE_FORMAT, e);
		}
	}
	
	/**
	 * 日期转字符串
	 * @param date
	 * @return 格式为yyyy-mm-dd
	 */
	public static String dateString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		return format.format(date);
	}

	/**
	 * 日期转字符串
	 * @param date
	 * @return 格式为yyyyMMddHHmmss
	 */
	public static String dateString14(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT_14);
		return format.format(date);
	}
	
	/**
	 * 尝试多种格式进行转换
	 * @param date
	 * @return
	 * @throws CommException
	 */
	public static Date parseDateTryFormat(String date) throws CommException {
		try {
			return parseDateTime(date);
		} catch (CommException e) {
			try {
				return parseDate(date);
			} catch (CommException e1) {
				return parseDate2(date);
			}
		}
	}
	
	/**
	 * 按转入的格式将字符串转换成Date类型
	 * @param dateTime
	 * @param dateFormate
	 * @return
	 * @throws CommException
	 */
	public static Date parseDateTime(String dateTime, String dateFormat) throws CommException {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try {
			return format.parse(dateTime);
		} catch (ParseException e) {
			throw new CommException(INVALID_DATE_FORMAT, e);
		}
	}

}
