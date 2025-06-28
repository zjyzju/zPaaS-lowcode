package cn.zpaas.lowcode.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;

import cn.zpaas.lowcode.constant.TimeUnit;
import cn.zpaas.lowcode.exception.CommException;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月15日-18:12:51
 */
public class TimeOffset {
	private static final ZoneId DEFULT_ZONE_ID = ZoneId.systemDefault();

	private static final String INVALID_TIMEUNIT = "invalid TimeUnit";

	// 时间偏移量
	private long offset;
	// 偏移单位
	private String timeUnit;// 取值有Y：年，M：月，D：日，W：周，H：小时，Mi：分，S：秒

	/**
	 * 判断是否是第xx
	 * 
	 * @param date
	 * @param period 包括：Y：年，M：月，D：日，W：周，H：小时
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	public static boolean isOffsetDateOf(Date date, String period, TimeOffset offset) throws CommException {
		if (date == null || offset == null || period == null || period.trim().length() == 0) {
			return false;
		}
		LocalDateTime dateTime = date.toInstant().atZone(DEFULT_ZONE_ID).toLocalDateTime();

		if (TimeUnit.YEAR.getCode().equals(period)) {// 在一年范围内
			return isOffsetDateOfYear(dateTime, offset);
		} else if (TimeUnit.MONTH.getCode().equals(period)) {// 在某一月范围内
			return isOffsetDateOfMonth(dateTime, offset);
		} else if (TimeUnit.WEEK.getCode().equals(period)) {// 在某一周范围内
			return isOffsetDateOfWeek(dateTime, offset);
		} else if (TimeUnit.DAY.getCode().equals(period)) {// 在某一天范围内
			return isOffsetDateOfDay(dateTime, offset);
		} else if (TimeUnit.HOUR.getCode().equals(period)) {// 在某一小范围内
			return isOffsetDateOfHour(dateTime, offset);
		} else {
			throw new CommException("invalid period type");
		}
	}

	/**
	 * 在一年内进行判断，判断是否是第xx个时间
	 * 
	 * @param date
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	private static boolean isOffsetDateOfYear(LocalDateTime dateTime, TimeOffset offset)
			throws CommException {
		if (TimeUnit.MONTH.getCode().equals(offset.getTimeUnit())) {// 第xx个月
			if (offset.getOffset() >= 0) {
				return (dateTime.getMonthValue() == offset.getOffset());// 第几个月
			} else {
				return (dateTime.getMonthValue() == (Month.DECEMBER.getValue() + offset.getOffset() + 1));// 倒数第几个月，-1表示12月
			}
		} else if (TimeUnit.WEEK.getCode().equals(offset.getTimeUnit())) {// 第xx周
			if (offset.getOffset() >= 0) {
				return (dateTime.get(WeekFields.ISO.weekOfWeekBasedYear()) == offset.getOffset());// 第几周
			} else {
				long weeks = dateTime.with(TemporalAdjusters.lastDayOfYear()).get(WeekFields.ISO.weekOfWeekBasedYear())
						+ offset.getOffset() + 1;// 倒数第几周
				return (dateTime.get(WeekFields.ISO.weekOfWeekBasedYear()) == weeks);// 倒数第几个周，-1表示最后一周
			}
		} else if (TimeUnit.DAY.getCode().equals(offset.getTimeUnit())) {// 第xx天
			if (offset.getOffset() >= 0) {
				return (dateTime.getDayOfYear() == offset.getOffset());// 第几天
			} else {
				long days = dateTime.with(TemporalAdjusters.lastDayOfYear()).getDayOfYear() + offset.getOffset() + 1;// 倒数第几天
				return (dateTime.getDayOfYear() == days);// 倒数第几个天，-1表示最后一天
			}
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}
	}

	/**
	 * 在一月内进行判断，判断是否是第xx个时间
	 * 
	 * @param date
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	private static boolean isOffsetDateOfMonth(LocalDateTime dateTime, TimeOffset offset) throws CommException {
		if (TimeUnit.WEEK.getCode().equals(offset.getTimeUnit())) {// 第xx周
			if (offset.getOffset() >= 0) {
				return (dateTime.get(WeekFields.ISO.weekOfMonth()) == offset.getOffset()); // 第几周
			} else {
				long weeks = dateTime.with(TemporalAdjusters.lastDayOfMonth()).get(WeekFields.ISO.weekOfMonth())
						+ offset.getOffset() + 1;// 倒数第几周
				return (dateTime.get(WeekFields.ISO.weekOfMonth()) == weeks);// 倒数第几周，-1表示最后一周
			}
		} else if (TimeUnit.DAY.getCode().equals(offset.getTimeUnit())) {// 第xx天
			if (offset.getOffset() >= 0) {
				return (dateTime.getDayOfMonth() == offset.getOffset());// 第几天
			} else {
				long days = dateTime.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth() + offset.getOffset()
						+ 1;// 倒数第几天
				return (dateTime.getDayOfMonth() == days);// 倒数第几天，-1表示最后一天
			}
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}
	}

	/**
	 * 在一周内进行判断，判断是否是第xx个时间
	 * 
	 * @param date
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	private static boolean isOffsetDateOfWeek(LocalDateTime dateTime, TimeOffset offset) throws CommException {
		if (TimeUnit.DAY.getCode().equals(offset.getTimeUnit())) {// 第xx天
			if (offset.getOffset() >= 0) {
				return (dateTime.getDayOfWeek().getValue() == offset.getOffset());// 第几天
			} else {
				return (dateTime.getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue() + offset.getOffset() + 1));// 倒数第几天，-1表示最后一天
			}
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}
	}

	/**
	 * 在一天内进行判断，判断是否是第xx个时间
	 * 
	 * @param date
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	private static boolean isOffsetDateOfDay(LocalDateTime dateTime, TimeOffset offset) throws CommException {
		if (TimeUnit.HOUR.getCode().equals(offset.getTimeUnit())) {// 第xx小时
			if (offset.getOffset() >= 0) {
				return (dateTime.getHour() == offset.getOffset());// 第几小时
			} else {
				return (dateTime.getHour() == (24 + offset.getOffset() + 1));// 倒数第几小时，-1表示最后一小时
			}
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}
	}

	/**
	 * 在一小时内进行判断，判断是否是第xx个时间
	 * 
	 * @param date
	 * @param offset 第几个时间，如+1D，-12H
	 * @return
	 */
	private static boolean isOffsetDateOfHour(LocalDateTime dateTime, TimeOffset offset) throws CommException {
		if (TimeUnit.MINUTE.getCode().equals(offset.getTimeUnit())) {// 第xx分钟
			if (offset.getOffset() >= 0) {
				return (dateTime.getMinute() == offset.getOffset());// 第几分钟
			} else {
				return (dateTime.getHour() == (59 + offset.getOffset() + 1));// 倒数第几分钟，-1表示最后一分钟
			}
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}
	}

	/**
	 * 计算时间偏移
	 * 
	 * @param date
	 * @param offset
	 * @return
	 * @throws CommException
	 */
	public static Date calculateDate(Date originDate, TimeOffset offset) throws CommException {
		if (originDate == null) {
			return null;
		}
		LocalDateTime dateTime = originDate.toInstant().atZone(DEFULT_ZONE_ID).toLocalDateTime();

		LocalDateTime newDateTime = null;
		if (TimeUnit.YEAR.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusYears(offset.getOffset());
		} else if (TimeUnit.MONTH.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusMonths(offset.getOffset());
		} else if (TimeUnit.WEEK.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusWeeks(offset.getOffset());
		} else if (TimeUnit.DAY.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusDays(offset.getOffset());
		} else if (TimeUnit.HOUR.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusHours(offset.getOffset());
		} else if (TimeUnit.MINUTE.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusMinutes(offset.getOffset());
		} else if (TimeUnit.SECOND.getCode().equals(offset.getTimeUnit())) {
			newDateTime = dateTime.plusSeconds(offset.getOffset());
		} else {
			throw new CommException(INVALID_TIMEUNIT);
		}

		if (newDateTime == null) {
			throw new CommException("calculateDate failed!");
		} else {
			return Date.from(newDateTime.atZone(DEFULT_ZONE_ID).toInstant());
		}
	}

	/**
	 * 解析时间偏移
	 * 
	 * @param timeOffset 如+5D，-12H
	 * @return
	 * @throws CommException
	 */
	public static TimeOffset parseDuration(String timeOffset) throws CommException {
		if (timeOffset == null || timeOffset.trim().length() == 0) {
			return new TimeOffset(0, TimeUnit.SECOND.getCode());
		}

		long offset = 0;
		if (timeOffset.endsWith(TimeUnit.MINUTE.getCode())) {
			offset = Long.valueOf(timeOffset.substring(0, timeOffset.length() - 2));
			return new TimeOffset(offset, TimeUnit.MINUTE.getCode());
		} else {
			offset = Long.valueOf(timeOffset.substring(0, timeOffset.length() - 1));
			String timeUnit = timeOffset.substring(timeOffset.length() - 1);
			if (TimeUnit.YEAR.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.YEAR.getCode());
			} else if (TimeUnit.MONTH.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.MONTH.getCode());
			} else if (TimeUnit.WEEK.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.WEEK.getCode());
			} else if (TimeUnit.DAY.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.DAY.getCode());
			} else if (TimeUnit.HOUR.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.HOUR.getCode());
			} else if (TimeUnit.SECOND.getCode().equals(timeUnit)) {
				return new TimeOffset(offset, TimeUnit.SECOND.getCode());
			} else {
				throw new CommException(INVALID_TIMEUNIT);
			}
		}
	}

	public TimeOffset(long offset, String timeUnit) {
		this.offset = offset;
		this.timeUnit = timeUnit;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
}
