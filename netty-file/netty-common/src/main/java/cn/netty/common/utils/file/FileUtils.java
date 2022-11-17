package cn.netty.common.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件处理工具类
 *
 * @author htht
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 获取文件后缀名
     *
     * @param file
     * @return
     */
    public final static String suffix(File file) {
        String fileName = file.getName();
        return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0 ? fileName.substring(fileName.lastIndexOf(".") + 1) : null;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public final static String suffix(String fileName) {
        return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0 ? fileName.substring(fileName.lastIndexOf(".") + 1) : null;
    }

    /**
     * 文件大小字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        long tb = gb * 1024;
        if (size >= tb) {
            return String.format("%.1f TB", (float) size / tb);
        } else if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    /**
     * 关闭流
     * @param closeable
     */
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
