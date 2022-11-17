package cn.htht.service.platform.portal.constant;

import java.util.regex.Pattern;

/**
 * @ClassName RegConstant
 * @Description 正则校验常量
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class RegConstant {
    /**
     * Alphanumeric characters
     */
    public static final String REG_ALNUM = "\\p{Alnum}";
    /**
     * Alphabetic characters
     */
    public static final String REG_ALPHA = "\\p{Alpha}";
    /**
     * ASCII characters
     */
    public static final String REG_ASCII = "\\p{ASCII}";
    /**
     * Space and tab
     */
    public static final String REG_BLANK = "\\p{Blank}";
    /**
     * Control characters
     */
    public static final String REG_CNTRL = "\\p{Cntrl}";
    /**
     * Digits
     */
    public static final String REG_DIGITS = "\\p{Digit}";
    /**
     * SVisible characters (i.e. anything except spaces, control characters, etc.)
     */
    public static final String REG_GRAPH = "\\p{Graph}";
    /**
     * Lowercase letters
     */
    public static final String REG_LOWER = "\\p{Lower}";
    /**
     * Visible characters and spaces (i.e. anything except control characters, etc.)
     */
    public static final String REG_PRINT = "\\p{Print}";
    /**
     * Punctuation and symbols.
     */
    public static final String REG_PUNCT = "\\p{Punct}";
    /**
     * All whitespace characters, including line breaks
     */
    public static final String REG_SPACE = "\\p{Space}";
    /**
     * Uppercase letters
     */
    public static final String REG_UPPER = "\\p{Upper}";
    /**
     * Hexadecimal digits
     */
    public static final String REG_XDIGIT = "\\p{XDigit}";
    /**
     * 空白行
     */
    public static final String REG_SPACE_LINE = "\\n\\s*\\r";
    /**
     * 首尾空白字符
     */
    public static final String REG_SPACE_POINT = "^\\s*|\\s*$";
    /**
     * HTML
     */
    public static final String REG_HTML = "<(\\S*?)[^>]*>.*?</\\1>|<.*? />";
    /**
     * Email
     */
    public static final String REG_EMAIL = "^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$";
    /**
     * 国内固定电话
     */
    public static final String REG_FIXED_TELEPHONE = "\\d{3}-\\d{8}|\\d{4}-\\d{7}";
    /**
     * 邮政编码
     */
    public static final String REG_POSTALCODE = "[1-9]\\d{5}(?!\\d)";
    /**
     * 身份证编码
     */
    public static final String REG_IDENTIFICATION_CARD = "(^[1-9]\\\\d{5}(18|19|20)\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}[0-9Xx]$)|\" +\n" +
            "                \"(^[1-9]\\\\d{5}\\\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\\\d{3}$)";
    /**
     * URL地址
     */
    public static final String REG_URL = "^http://([w-]+.)+[w-]+(/[w-./?%&=]*)?$";
    /**
     * 移动电话
     */
    public static final String REG_MOBILE_TELEPHONE = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
    /**
     * 合法的名字（字母开头，允许5-16字节，允许字母数字下划线）
     */
    public static final String REG_LEGAL_ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
    /**
     * 正则表达式：验证汉字
     */
    public static final String REG_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
    /**
     * ip地址
     */
    public static final String REG_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

    /**
     * scheme校验
     */
    private static final String SCHEME_PATTERN = "([^:/?#]+):";

    /**
     * http/https校验
     */
    private static final String HTTP_PATTERN = "(http|https):";

    /**
     * 用户校验
     */
    private static final String USERINFO_PATTERN = "([^@/]*)";

    /**
     * 域名校验
     */
    private static final String HOST_PATTERN = "([^/?#:]*)";

    /**
     * 端口校验
     */
    private static final String PORT_PATTERN = "(\\d*)";

    /**
     * url请求路径校验
     */
    private static final String PATH_PATTERN = "([^?#]*)";

    /**
     * url请求参数校验
     */
    private static final String QUERY_PATTERN = "([^#]*)";

    /**
     * url结尾校验
     */
    private static final String LAST_PATTERN = "(.*)";

    /**
     * 整数数字校验
     */
    public static final Pattern INTEGER_NUMERIC_PATTERN = Pattern.compile("^[0-9\\-]+$");
    /**
     *
     */
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");
    /**
     * 数字字符串校验
     */
    public static final Pattern NUMERIC_STRING_PATTERN = Pattern.compile("^[0-9\\-\\-]+$");
    /**
     * 浮点数校验
     */
    public static final Pattern FLOAT_NUMERIC_PATTERN = Pattern.compile("^[0-9\\-\\.]+$");
    /**
     * 字母校验
     */
    public static final Pattern ABC_PATTERN = Pattern.compile("^[a-z|A-Z]+$");
    /**
     * 时间段(秒)校验
     */
    public static final Pattern PATTERN_DATETIME_BURST = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}-\\d{2}:\\d{2}:\\d{2}");
    /**
     * 时间段(分)校验
     */
    public static final Pattern PATTERN_DATE_HOUR_DUAN = Pattern.compile("^\\d{2}:\\d{2}-\\d{2}:\\d{2}");

    /**
     * 空白字符验证
     */
    public static final Pattern PATTER_BLANK_CODE = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * URI校验
     */
    public static final Pattern URI_PATTERN = Pattern.compile("^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
            ")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

    /**
     * HTTP请求校验
     */
    public static final Pattern HTTP_URL_PATTERN = Pattern.compile('^' + HTTP_PATTERN + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" +
            PATH_PATTERN + "(\\?" + LAST_PATTERN + ")?");
}
