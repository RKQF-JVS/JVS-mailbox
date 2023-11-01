package cn.bctools.mail.utils;

import cn.hutool.core.util.ObjectUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @description: 时间转换
 * @time: 2020/7/14 12:02
 */
public class TimeUtils {

    /**
     * LocalDateTime转为String
     *
     * @param time
     * @return
     */
    public static String localDataTimeToString(LocalDateTime time) {
        return ObjectUtil.isNull(time) ? "" : time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * LocalDate转为String
     *
     * @param time
     * @return
     */
    public static String localDataToString(LocalDate time) {
        return ObjectUtil.isNull(time) ? "" : time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * String转为LocalDateTime
     *
     * @param time
     * @return
     */
    public static LocalDate stringToLocalData(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(time, df);
        return dateToLocalDate(localDataTimeToDate(ldt));
    }

    /**
     * String转为LocalDateTime
     *
     * @param time
     * @return
     */
    public static LocalDateTime stringToLocalDataTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(time, df);
        return ldt;
    }

    /**
     * LocalDateTime转为Date
     *
     * @param time
     * @return
     */
    public static Date localDataTimeToDate(LocalDateTime time) {
        Date date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * LocalDate转为Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * Date转为LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * Date转为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * long转为LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime longToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 现在时间LocalDataTime
     *
     * @return
     */
    public static LocalDateTime nowLocalDataTime() {
        Date date = new Date();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);
        return localDateTime;
    }

    /**
     * 比较两个date是否为同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            return false;
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            return false;
        }
    }

}
