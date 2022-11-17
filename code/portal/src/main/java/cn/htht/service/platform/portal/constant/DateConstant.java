package cn.htht.service.platform.portal.constant;

import java.time.format.DateTimeFormatter;

/**
 * @ClassName DateConstant
 * @Description 日期常量
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class DateConstant {

    // 时间元素
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String WEEK = "week";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";

    // 星期元素
    // 星期一
    public static final String MONDAY = "MONDAY";
    // 星期二
    public static final String TUESDAY = "TUESDAY";
    // 星期三
    public static final String WEDNESDAY = "WEDNESDAY";
    // 星期四
    public static final String THURSDAY = "THURSDAY";
    // 星期五
    public static final String FRIDAY = "FRIDAY";
    // 星期六
    public static final String SATURDAY = "SATURDAY";
    // 星期日
    public static final String SUNDAY = "SUNDAY";

    // 根据指定格式显示日期和时间
    public static final DateTimeFormatter yyyyMMdd_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter yyyyMMddHH_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    public static final DateTimeFormatter yyyyMMddHHmm_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter yyyyMMddHHmmss_EN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter HHmmss_EN = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter yyyyMMdd_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public static final DateTimeFormatter yyyyMMddHH_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时");

    public static final DateTimeFormatter yyyyMMddHHmm_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分");

    public static final DateTimeFormatter yyyyMMddHHmmss_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒");

    public static final DateTimeFormatter HHmmss_CN = DateTimeFormatter.ofPattern("HH时mm分ss秒");

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(BaseConstant.DATETIME_FORMAT);

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(BaseConstant.DATE_FORMAT);

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(BaseConstant.TIME_FORMAT);
}
