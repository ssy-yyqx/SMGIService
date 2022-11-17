package cn.htht.service.platform.portal.utils.utils;

/**
 * 处理并记录日志文件
 *
 * @author htht
 */
public class LogUtils {
    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
