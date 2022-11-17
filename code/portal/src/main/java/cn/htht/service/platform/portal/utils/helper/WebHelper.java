package cn.htht.service.platform.portal.utils.helper;


import cn.htht.service.platform.portal.utils.utils.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * web相关的一些操作方法
 */
@SuppressWarnings("unchecked")
public class WebHelper {

    /**
     * 对字符串进行编码
     *
     * @param str      需要处理的字符串
     * @param encoding 编码方式
     * @return 编码后的字符串
     */
    public static String escape(String str, String encoding) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        char[] chars = ConvertHepler.bytesToChars(ConvertHepler.encodeBytes(str.getBytes(encoding), '%'));
        return new String(chars);
    }

    /**
     * 对字符串进行解码
     *
     * @param str      需要处理的字符串
     * @param encoding 解码方式
     * @return 解码后的字符串
     */
    public static String unescape(String str, String encoding) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        return UrlHelper.decodeQuery(str, encoding);
    }


    /**
     * HTML标签转义方法
     * <p>
     * 空格	 &nbsp;
     * <	小于号	&lt;
     * >	大于号	&gt;
     * &	和号	 &amp;
     * "	引号	&quot;
     * '	撇号 	&apos;
     * ￠	分	 &cent;
     * £	镑	 &pound;
     * ¥	日圆	&yen;
     * €	欧元	&euro;
     * §	小节	&sect;
     * ©	版权	&copy;
     * ®	注册商标	&reg;
     * ™	商标	&trade;
     * ×	乘号	&times;
     * ÷	除号	&divide;
     */
    public static String unhtml(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String html = content;
        html = html.replaceAll("'", "&apos;");
        html = html.replaceAll("\"", "&quot;");
        html = html.replaceAll("\t", "&nbsp;&nbsp;");
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        return html;
    }

    /**
     * html转实体
     *
     * @param content
     * @return
     */
    public static String html(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String html = content;
        html = html.replaceAll("&apos;", "'");
        html = html.replaceAll("&quot;", "\"");
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&lt;", "<");
        html = html.replaceAll("&gt;", ">");
        return html;
    }

    /**
     * 去除待带script、src的语句，转义替换后的value值
     * 简单的过滤有一定的弊端
     */
    public static String replaceXSS(String value) {
        if (value != null) {
            StringBuilder result = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); ++i) {
                switch (value.charAt(i)) {
                    case '<':
                        result.append("<");
                        break;
                    case '>':
                        result.append(">");
                        break;
                    case '"':
                        result.append("\"");
                        break;
                    case '\'':
                        result.append("'");
                        break;
                    case '%':
                        result.append("%");
                        break;
                    case ';':
                        result.append(";");
                        break;
                    case '(':
                        result.append("(");
                        break;
                    case ')':
                        result.append(")");
                        break;
                    case '&':
                        result.append("&");
                        break;
                    case '+':
                        result.append("+");
                        break;
                    default:
                        result.append(value.charAt(i));
                        break;
                }
            }
            value = result.toString();
            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
            value = value.replaceAll("'", "&#39;");
        }
        return value;
    }


    /**
     * 设置 Cookie, 过期时间为30分钟
     *
     * @param name  名称
     * @param value 值
     */
    public static Cookie setCookie(HttpServletResponse response, String name, String value, String path) {
        return CookieHelper.addCookie(response, name, value, path, 60 * 30);
    }


    /**
     * 设置 Cookie, 过期时间自定义
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 过期时间, 单位秒
     */
    public static Cookie addCookie(HttpServletResponse response, String name, String value, String domain, String path, int maxAge) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));
            if (domain != null && !"".equals(domain)) {
                cookie.setDomain(domain);
            }
            cookie.setMaxAge(maxAge);
            if (null != path) {
                cookie.setPath(path);
            }
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cookie;
    }


    /**
     * 获得指定Cookie的值
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return CookieHelper.getCookie(request, null, name, false);
    }

    /**
     * 获得指定Cookie的值，并删除。
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        return CookieHelper.getCookie(request, response, name, true);
    }

}
