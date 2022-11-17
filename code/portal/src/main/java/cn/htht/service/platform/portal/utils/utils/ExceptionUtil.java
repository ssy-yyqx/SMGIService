package cn.htht.service.platform.portal.utils.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 错误信息处理类。
 *
 * @author htht
 */
public class ExceptionUtil {
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        return str;
    }

    public static String getRootErrorMseeage(Exception e) {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null) {
            return "";
        }
        String msg = root.getMessage();
        if (msg == null) {
            return "null";
        }
        return StringUtils.defaultString(msg);
    }

    /**
     * 只返回指定包中的异常堆栈信息
     *
     * @param e           异常信息
     * @param packageName 只转换某个包下的信息
     * @return string
     */
    public static String stackTraceToString(Throwable e, String packageName) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        if (packageName == null) {
            return str;
        }
        String[] arrs = str.split("\n");
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(arrs[0] + "\n");
        for (int i = 0; i < arrs.length; i++) {
            String temp = arrs[i];
            if (temp != null && temp.indexOf(packageName) > 0) {
                sbuf.append(temp + "\n");
            }
        }
        return sbuf.toString();
    }

    /**
     * 获取异常信息
     *
     * @param e 异常信息
     * @return string
     */
    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
