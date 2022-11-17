package cn.htht.service.platform.portal.utils.helper;

import cn.htht.service.platform.portal.constant.DateConstant;
import com.google.common.collect.Lists;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * JAVA8时间工具类
 */
public class Date8Helper {


    /**
     * 获取当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getNowDate_EN() {
        return String.valueOf(LocalDate.now());
    }

    /**
     * 获取当前日期
     *
     * @return 字符串yyyy-MM-dd HH:mm:ss
     */
    public static String getNowTime_EN() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHHmmss_EN);
    }

    /**
     * 获取当前时间（yyyy-MM-dd HH）
     */
    public static String getNowTime_EN_yMdH() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHH_EN);
    }

    /**
     * 获取当前时间（yyyy年MM月dd日）
     */
    public static String getNowTime_CN_yMdH() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHH_CN);
    }

    /**
     * 获取当前时间（yyyy-MM-dd HH:mm）
     */
    public static String getNowTime_EN_yMdHm() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHHmm_EN);
    }

    /**
     * 获取当前时间（yyyy年MM月dd日HH时mm分）
     */
    public static String getNowTime_CN_yMdHm() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHHmm_CN);
    }

    /**
     * 获取当前时间（HH时mm分ss秒）
     */
    public static String getNowTime_CN_HHmmss() {
        return LocalDateTime.now().format(DateConstant.HHmmss_CN);
    }

    /**
     * 根据日期格式，获取当前时间
     *
     * @param formatStr 日期格式<br>
     *                  <li>yyyy</li>
     *                  <li>yyyy-MM-dd</li>
     *                  <li>yyyy-MM-dd HH:mm:ss</li>
     *                  <li>HH:mm:ss</li>
     * @return
     */
    public static String getTime(String formatStr) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatStr));
    }

    /**
     * 获取中文的当前日期
     *
     * @return yyyy年mm月dd日
     */
    public static String getNowDate_CN() {
        return LocalDate.now().format(DateConstant.yyyyMMdd_CN);
    }

    /**
     * 获取中文当前时间
     *
     * @return yyyy年MM月dd日HH时mm分ss秒
     */
    public static String getNowTime_CN() {
        return LocalDateTime.now().format(DateConstant.yyyyMMddHHmmss_CN);
    }


    /**
     * 获取当前日期的节点时间（年，月，周，日，时，分，秒）
     *
     * @param node 日期中的节点元素（年，月，周，日，时，分，秒）
     * @return 节点数字，如创建此方法的时间：年 2019，月 3，日 30，周 6
     */
    public static Integer getNodeTime(String node) {
        LocalDateTime today = LocalDateTime.now();
        Integer resultNode = null;
        switch (node) {
            case DateConstant.YEAR:
                resultNode = today.getYear();
                break;
            case DateConstant.MONTH:
                resultNode = today.getMonthValue();
                break;
            case DateConstant.WEEK:
                resultNode = transformWeekEN2Num(String.valueOf(today.getDayOfWeek()));
                break;
            case DateConstant.DAY:
                resultNode = today.getDayOfMonth();
                break;
            case DateConstant.HOUR:
                resultNode = today.getHour();
                break;
            case DateConstant.MINUTE:
                resultNode = today.getMinute();
                break;
            case DateConstant.SECOND:
                resultNode = today.getSecond();
                break;
            default:
                // 当前日期是当前年的第几天。例如：2019/1/3是2019年的第三天
                resultNode = today.getDayOfYear();
                break;
        }
        return resultNode;
    }

    /**
     * 将英文星期转换成数字
     *
     * @param enWeek 英文星期
     * @return int，如果数字小于0，则检查，看是否输入错误 or 入参为null
     */
    public static int transformWeekEN2Num(String enWeek) {
        if (DateConstant.MONDAY.equals(enWeek)) {
            return 1;
        } else if (DateConstant.TUESDAY.equals(enWeek)) {
            return 2;
        } else if (DateConstant.WEDNESDAY.equals(enWeek)) {
            return 3;
        } else if (DateConstant.THURSDAY.equals(enWeek)) {
            return 4;
        } else if (DateConstant.FRIDAY.equals(enWeek)) {
            return 5;
        } else if (DateConstant.SATURDAY.equals(enWeek)) {
            return 6;
        } else if (DateConstant.SUNDAY.equals(enWeek)) {
            return 7;
        } else {
            return -1;
        }
    }

    /**
     * 获取当前日期之后（之后）的节点事件<br>
     * <ul>
     * 比如当前时间为：2019-03-30 10:20:30
     * </ul>
     * <li>node="hour",num=5L:2019-03-30 15:20:30</li>
     * <li>node="day",num=1L:2019-03-31 10:20:30</li>
     * <li>node="year",num=1L:2020-03-30 10:20:30</li>
     *
     * @param node 节点元素（“year”,"month","week","day","huor","minute","second"）
     * @param num  第几天（+：之后，-：之前）
     * @return 之后或之后的日期
     */
    public static String getAfterOrPreNowTime(String node, Long num) {
        LocalDateTime now = LocalDateTime.now();
        if (DateConstant.HOUR.equals(node)) {
            return now.plusHours(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.DAY.equals(node)) {
            return now.plusDays(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.WEEK.equals(node)) {
            return now.plusWeeks(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.MONTH.equals(node)) {
            return now.plusMonths(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.YEAR.equals(node)) {
            return now.plusYears(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.MINUTE.equals(node)) {
            return now.plusMinutes(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else if (DateConstant.SECOND.equals(node)) {
            return now.plusSeconds(num).format(DateConstant.yyyyMMddHHmmss_EN);
        } else {
            return "Node is Error!";
        }
    }

    /**
     * 获取与当前日期相距num个之后（之前）的日期<br>
     * <ul>
     * 比如当前时间为：2019-03-30 10:20:30的格式日期
     * <li>node="hour",num=5L:2019-03-30 15:20:30</li>
     * <li>node="day",num=1L:2019-03-31 10:20:30</li>
     * <li>node="year",num=1L:2020-03-30 10:20:30</li>
     * </ul>
     *
     * @param dtf  格式化当前时间格式（dtf = yyyyMMddHHmmss_EN）
     * @param node 节点元素（“year”,"month","week","day","huor","minute","second"）
     * @param num  （+：之后，-：之前）
     * @return 之后之前的日期
     */
    public static String getAfterOrPreNowTimePlus(DateTimeFormatter dtf, String node, Long num) {
        LocalDateTime now = LocalDateTime.now();
        if (DateConstant.HOUR.equals(node)) {
            return now.plusHours(num).format(dtf);
        } else if (DateConstant.DAY.equals(node)) {
            return now.plusDays(num).format(dtf);
        } else if (DateConstant.WEEK.equals(node)) {
            return now.plusWeeks(num).format(dtf);
        } else if (DateConstant.MONTH.equals(node)) {
            return now.plusMonths(num).format(dtf);
        } else if (DateConstant.YEAR.equals(node)) {
            return now.plusYears(num).format(dtf);
        } else if (DateConstant.MINUTE.equals(node)) {
            return now.plusMinutes(num).format(dtf);
        } else if (DateConstant.SECOND.equals(node)) {
            return now.plusSeconds(num).format(dtf);
        } else {
            return "Node is Error!";
        }
    }

    /**
     * 当前时间的hour，minute，second之后（之前）的时刻
     *
     * @param node 时间节点元素（hour，minute，second）
     * @param num  之后（之后）多久时，分，秒（+：之后，-：之前）
     * @return HH:mm:ss 字符串
     */
    public static String getAfterOrPreNowTimeSimp(String node, Long num) {
        LocalTime now = LocalTime.now();
        if (DateConstant.HOUR.equals(node)) {
            return now.plusHours(num).format(DateConstant.HHmmss_EN);
        } else if (DateConstant.MINUTE.equals(node)) {
            return now.plusMinutes(num).format(DateConstant.HHmmss_EN);
        } else if (DateConstant.SECOND.equals(node)) {
            return now.plusSeconds(num).format(DateConstant.HHmmss_EN);
        } else {
            return "Node is Error!";
        }
    }

    /**
     * 检查重复事件，比如生日。TODO This is a example.
     *
     * @return
     */
    public static boolean isBirthday(int month, int dayOfMonth) {
        MonthDay birthDay = MonthDay.of(month, dayOfMonth);
        MonthDay curMonthDay = MonthDay.from(LocalDate.now());// MonthDay只存储了月、日。
        if (birthDay.equals(curMonthDay)) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前日期第index日之后(之前)的日期（yyyy-MM-dd）
     *
     * @param index 第index天
     * @return 日期字符串：yyyy-MM-dd
     */
    public static String getAfterOrPreDayDate(int index) {
        return LocalDate.now().plus(index, ChronoUnit.DAYS).format(DateConstant.yyyyMMdd_EN);
    }

    /**
     * 获取当前日期第index周之前（之后）的日期（yyyy-MM-dd）
     *
     * @param index 第index周（+：之后，-：之前）
     * @return 日期字符串：yyyy-MM-dd
     */
    public static String getAfterOrPreWeekDate(int index) {
        return LocalDate.now().plus(index, ChronoUnit.WEEKS).format(DateConstant.yyyyMMdd_EN);
    }

    /**
     * 获取当前日期第index月之前（之后）的日期（yyyy-MM-dd）
     *
     * @param index 第index月（+：之后，-：之前）
     * @return 日期字符串：yyyy-MM-dd
     */
    public static String getAfterOrPreMonthDate(int index) {
        return LocalDate.now().plus(index, ChronoUnit.MONTHS).format(DateConstant.yyyyMMdd_EN);
    }

    /**
     * 获取当前日期第index年之前（之后）的日期（yyyy-MM-dd）
     *
     * @param index 第index年（+：之后，-：之前）
     * @return 日期字符串：yyyy-MM-dd
     * @author zero 2019/03/31
     */
    public static String getAfterOrPreYearDate(int index) {
        return LocalDate.now().plus(index, ChronoUnit.YEARS).format(DateConstant.yyyyMMdd_EN);
    }

    /**
     * 获取指定日期之前之后的第index的日，周，月，年的日期
     *
     * @param date  指定日期格式：yyyy-MM-dd
     * @param node  时间节点元素（日周月年）
     * @param index 之前之后第index个日期
     * @return yyyy-MM-dd 日期字符串
     */
    public static String getAfterOrPreDate(String date, String node, int index) {
        date = date.trim();
        if (DateConstant.DAY.equals(node)) {
            return LocalDate.parse(date).plus(index, ChronoUnit.DAYS).format(DateConstant.yyyyMMdd_EN);
        } else if (DateConstant.WEEK.equals(node)) {
            return LocalDate.parse(date).plus(index, ChronoUnit.WEEKS).format(DateConstant.yyyyMMdd_EN);
        } else if (DateConstant.MONTH.equals(node)) {
            return LocalDate.parse(date).plus(index, ChronoUnit.MONTHS).format(DateConstant.yyyyMMdd_EN);
        } else if (DateConstant.YEAR.equals(node)) {
            return LocalDate.parse(date).plus(index, ChronoUnit.YEARS).format(DateConstant.yyyyMMdd_EN);
        } else {
            return "Wrong date format!";
        }
    }

    /**
     * 检测：输入年份是否是闰年？
     *
     * @param date 日期格式：yyyy-MM-dd
     * @return true：闰年，false：平年
     */
    public static boolean isLeapYear(String date) {
        return LocalDate.parse(date.trim()).isLeapYear();
    }

    /**
     * 计算两个日期字符串之间相差多少个周期（天，月，年）
     *
     * @param date1 yyyy-MM-dd
     * @param date2 yyyy-MM-dd
     * @param node  三者之一:(day，month,year)
     * @return 相差多少周期
     */
    public static int peridCount(String date1, String date2, String node) {
        date1 = date1.trim();
        date2 = date2.trim();
        if (DateConstant.DAY.equals(node)) {
            return Period.between(LocalDate.parse(date1), LocalDate.parse(date2)).getDays();
        } else if (DateConstant.MONTH.equals(node)) {
            return Period.between(LocalDate.parse(date1), LocalDate.parse(date2)).getMonths();
        } else if (DateConstant.YEAR.equals(node)) {
            return Period.between(LocalDate.parse(date1), LocalDate.parse(date2)).getYears();
        } else {
            return 0;
        }
    }

    /**
     * 切割日期。按照周期切割成小段日期段。例如： <br>
     *
     * @param startDate 开始日期（yyyy-MM-dd）
     * @param endDate   结束日期（yyyy-MM-dd）
     * @param period    周期（天，周，月，年）
     * @return 切割之后的日期集合
     * @example <li>startDate="2019-02-28",endDate="2019-03-05",period="day"</li>
     * <li>结果为：[2019-02-28, 2019-03-01, 2019-03-02, 2019-03-03, 2019-03-04, 2019-03-05]</li><br>
     * <li>startDate="2019-02-28",endDate="2019-03-25",period="week"</li>
     * <li>结果为：[2019-02-28,2019-03-06, 2019-03-07,2019-03-13, 2019-03-14,2019-03-20,
     * 2019-03-21,2019-03-25]</li><br>
     * <li>startDate="2019-02-28",endDate="2019-05-25",period="month"</li>
     * <li>结果为：[2019-02-28,2019-02-28, 2019-03-01,2019-03-31, 2019-04-01,2019-04-30,
     * 2019-05-01,2019-05-25]</li><br>
     * <li>startDate="2019-02-28",endDate="2020-05-25",period="year"</li>
     * <li>结果为：[2019-02-28,2019-12-31, 2020-01-01,2020-05-25]</li><br>
     */
    public static List<String> getPieDateRange(String startDate, String endDate, String period) {
        List<String> result = Lists.newArrayList();
        LocalDate end = LocalDate.parse(endDate, DateConstant.yyyyMMdd_EN);
        LocalDate start = LocalDate.parse(startDate, DateConstant.yyyyMMdd_EN);
        LocalDate tmp = start;
        switch (period) {
            case DateConstant.DAY:
                while (start.isBefore(end) || start.isEqual(end)) {
                    result.add(start.toString());
                    start = start.plusDays(1);
                }
                break;
            case DateConstant.WEEK:
                while (tmp.isBefore(end) || tmp.isEqual(end)) {
                    if (tmp.plusDays(6).isAfter(end)) {
                        result.add(tmp.toString() + "," + end);
                    } else {
                        result.add(tmp.toString() + "," + tmp.plusDays(6));
                    }
                    tmp = tmp.plusDays(7);
                }
                break;
            case DateConstant.MONTH:
                while (tmp.isBefore(end) || tmp.isEqual(end)) {
                    LocalDate lastDayOfMonth = tmp.with(TemporalAdjusters.lastDayOfMonth());
                    if (lastDayOfMonth.isAfter(end)) {
                        result.add(tmp.toString() + "," + end);
                    } else {
                        result.add(tmp.toString() + "," + lastDayOfMonth);
                    }
                    tmp = lastDayOfMonth.plusDays(1);
                }
                break;
            case DateConstant.YEAR:
                while (tmp.isBefore(end) || tmp.isEqual(end)) {
                    LocalDate lastDayOfYear = tmp.with(TemporalAdjusters.lastDayOfYear());
                    if (lastDayOfYear.isAfter(end)) {
                        result.add(tmp.toString() + "," + end);
                    } else {
                        result.add(tmp.toString() + "," + lastDayOfYear);
                    }
                    tmp = lastDayOfYear.plusDays(1);
                }
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 指定日期月的最后一天（yyyy-MM-dd）
     *
     * @param curDate     日期格式（yyyy-MM-dd）
     * @param firstOrLast true：第一天，false：最后一天
     * @return
     */
    public static String getLastDayOfMonth(String curDate, boolean firstOrLast) {
        if (firstOrLast) {
            return LocalDate.parse(curDate, DateConstant.yyyyMMdd_EN).with(TemporalAdjusters.firstDayOfMonth()).toString();
        } else {
            return LocalDate.parse(curDate, DateConstant.yyyyMMdd_EN).with(TemporalAdjusters.lastDayOfMonth()).toString();
        }
    }

    /**
     * 指定日期年的最后一天（yyyy-MM-dd）
     *
     * @param curDate     指定日期（格式：yyyy-MM-dd）
     * @param firstOrLast true:第一天，false:最后一天
     * @return
     * @author zero 2019/04/13
     */
    public static String getLastDayOfYear(String curDate, boolean firstOrLast) {
        if (firstOrLast) {
            return LocalDate.parse(curDate, DateConstant.yyyyMMdd_EN).with(TemporalAdjusters.firstDayOfYear()).toString();
        } else {
            return LocalDate.parse(curDate, DateConstant.yyyyMMdd_EN).with(TemporalAdjusters.lastDayOfYear()).toString();
        }
    }

    /**
     * 获取下一个星期的日期
     *
     * @param curDay          yyyy-MM-dd
     * @param dayOfWeek       monday:1~sunday:7
     * @param isContainCurDay 是否包含当天，true：是，false：不包含
     * @return 日期（yyyy-MM-dd）
     * @author zero 2019/04/02
     */
    public static String getNextWeekDate(String curDay, int dayOfWeek, boolean isContainCurDay) {
        dayOfWeek = dayOfWeek < 1 || dayOfWeek > 7 ? 1 : dayOfWeek;
        if (isContainCurDay) {
            return LocalDate.parse(curDay).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek))).toString();
        } else {
            return LocalDate.parse(curDay).with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek))).toString();
        }
    }

    /**
     * 获取上一个星期的日期
     *
     * @param curDay    指定日期（yyyy-MM-dd）
     * @param dayOfWeek 数字范围（monday:1~sunday:7）
     * @param isCurDay  是否包含当天，true：是，false：不包含
     * @return 日期（yyyy-MM-dd）
     * @author zero 2019/04/02
     */
    public static String getPreWeekDate(String curDay, int dayOfWeek, boolean isCurDay) {
        dayOfWeek = dayOfWeek < 1 || dayOfWeek > 7 ? 1 : dayOfWeek;
        if (isCurDay) {
            return LocalDate.parse(curDay).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(dayOfWeek))).toString();
        } else {
            return LocalDate.parse(curDay).with(TemporalAdjusters.previous(DayOfWeek.of(dayOfWeek))).toString();
        }
    }

    /**
     * 获取指定日期当月的最后或第一个星期日期
     *
     * @param curDay      指定日期（yyyy-MM-dd）
     * @param dayOfWeek   周几（1~7）
     * @param lastOrFirst true：最后一个，false本月第一个
     * @return 日期（yyyy-MM-dd）
     * @author zero 2019/04/02
     */
    public static String getFirstOrLastWeekDate(String curDay, int dayOfWeek, boolean lastOrFirst) {
        dayOfWeek = dayOfWeek < 1 || dayOfWeek > 7 ? 1 : dayOfWeek;
        if (lastOrFirst) {
            return LocalDate.parse(curDay).with(TemporalAdjusters.lastInMonth(DayOfWeek.of(dayOfWeek))).toString();
        } else {
            return LocalDate.parse(curDay).with(TemporalAdjusters.firstInMonth(DayOfWeek.of(dayOfWeek))).toString();
        }
    }
}
