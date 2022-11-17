package cn.htht.service.platform.portal.utils.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * 专门负责传输下载二进制流文件
 */
public class DownloadUtils {

    public void downloadFile(File file, HttpServletRequest request, HttpServletResponse response) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        try {
            String fileName = file.getName();
            if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
                // IE
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            byteStream(fileInputStream, response, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFileStream(InputStream stream, String fileName, HttpServletRequest request, HttpServletResponse response) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        try {
            if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
                // IE
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            byteStream(stream, response, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void byteStream(InputStream in, HttpServletResponse response, String fileName) throws IOException {
        ByteArrayOutputStream swapStream;
        swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc;
        while ((rc = in.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] bytes = swapStream.toByteArray();

        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        toClient.write(bytes);
        toClient.flush();
        toClient.close();
    }
}
