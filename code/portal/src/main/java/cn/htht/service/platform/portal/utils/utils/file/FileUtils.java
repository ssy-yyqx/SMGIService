package cn.htht.service.platform.portal.utils.utils.file;

import java.io.*;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.htht.service.platform.portal.utils.helper.FileHelper;
import cn.htht.service.platform.portal.utils.helper.RegHelper;
import cn.htht.service.platform.portal.utils.helper.SysHepler;
import cn.htht.service.platform.portal.utils.utils.CheckUtil;
import org.apache.commons.lang3.ArrayUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 文件处理工具类
 *
 * @author htht
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除文件夹下的文件
     *
     * @param fileParentPath 文件夹
     * @return
     */
    public static void deleteParentFile(String fileParentPath) {
        File file = new File(fileParentPath);
        //判断是否为文件夹
        if (file.isDirectory()) {
            //获取该文件夹下的子文件夹
            File[] files = file.listFiles();
            //循环子文件夹重复调用delete方法
            for (int i = 0; i < files.length; i++) {
                deleteParentFile(files[i].getAbsolutePath());
            }
        }
        //文件删除，File类的删除方法
        deleteFile(file.getAbsolutePath());
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    public static void main(String[] args) {
        List<String> fileDirList = getFileDirList(new File("D:\\htht\\upload"));
        for (String s : fileDirList) {
            System.out.println("输出：" + s);
        }
    }

    /**
     * 获取文件夹是否有文件
     *
     * @param fileDir
     * @return
     */
    public static Boolean hasFile(String fileDir) {
        return !CollectionUtils.isEmpty(getFiles(new File(fileDir)));
    }

    /**
     * 获取文件夹里面的文件
     *
     * @param fileDir
     * @return
     */
    public static List<File> getFiles(File fileDir) {
        List<File> fileList = new ArrayList<>();
        File[] files = fileDir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getFiles(file));
                }
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 获取单文件夹里面的文件父级路径，多同级目录不支持
     *
     * @param fileDir
     * @return
     */
    public static String getFileString(File fileDir) {
        String fileParentPath = "";
        File[] files = fileDir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileParentPath = getFileString(file);
                }
                if (file.isFile()) {
                    return file.getParentFile().getAbsolutePath();
                }
            }
        }
        return fileParentPath;
    }

    /**
     * 获取文件夹里面的文件的父级目录，多文件夹不支持
     *
     * @param fileDir
     * @return
     */
    public static File getFileDir(File fileDir) {
        File fileParentFile = null;
        File[] files = fileDir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileParentFile = getFileDir(file);
                }
                if (file.isFile()) {
                    return file.getParentFile();
                }
            }
        }
        return fileParentFile;
    }

    /**
     * 判断是否存在多个目录有文件
     *
     * @param fileDir
     * @return
     */
    public static List<String> getFileDirList(File fileDir) {
        List<String> fileParentList = new ArrayList<>();
        File[] files = fileDir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileParentList.addAll(getFileDirList(file));
                }
                if (file.isFile()) {
                    fileParentList.add(file.getParentFile().getAbsolutePath());
                }
            }
        }
        return fileParentList;
    }

    public static File createFile(String fileParentDir, String fileName) {
        File fileParent = new File(fileParentDir);
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (StringUtils.isNotEmpty(fileName)) {
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
        System.gc();
        return fileParent;
    }

    /**
     * Buffer的大小
     */
    private static Integer BUFFER_SIZE = 1024 * 1024 * 10;

    public static MessageDigest MD5 = null;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) {
            ne.printStackTrace();
        }
    }

    /**
     * 获取文件的md5
     *
     * @param file
     * @return
     */
    public static String fileMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new BigInteger(1, MD5.digest()).toString(16);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件的行数
     *
     * @param file 统计的文件
     * @return 文件行数
     */
    public final static int countLines(File file) {
        try (LineNumberReader rf = new LineNumberReader(new FileReader(file))) {
            long fileLength = file.length();
            rf.skip(fileLength);
            return rf.getLineNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 以列表的方式获取文件的所有行
     *
     * @param file 需要出来的文件
     * @return 包含所有行的list
     */
    public final static List<String> lines(File file) {
        List<String> list = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的所有行
     *
     * @param file     需要处理的文件
     * @param encoding 指定读取文件的编码
     * @return 包含所有行的list
     */
    public final static List<String> lines(File file, String encoding) {
        List<String> list = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的指定的行数数据
     *
     * @param file  处理的文件
     * @param lines 需要读取的行数
     * @return 包含制定行的list
     */
    public final static List<String> lines(File file, int lines) {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (list.size() == lines) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的指定的行数数据
     *
     * @param file     需要处理的函数
     * @param lines    需要处理的行还俗
     * @param encoding 指定读取文件的编码
     * @return 包含制定行的list
     */
    public final static List<String> lines(File file, int lines, String encoding) {
        List<String> list = new ArrayList<>();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (list.size() == lines) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 在文件末尾追加一行
     *
     * @param file 需要处理的函数
     * @param str  添加的子字符串
     * @return 是否成功
     */
    public final static boolean appendLine(File file, String str) {
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(SysHepler.FILE_SEPARATOR + str);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 在文件末尾追加一行
     *
     * @param file     需要处理的文件
     * @param str      添加的字符串
     * @param encoding 指定写入的编码
     * @return 是否成功
     */
    public final static boolean appendLine(File file, String str, String encoding) {
        String lineSeparator = System.getProperty("line.separator", "\n");
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write((lineSeparator + str).getBytes(encoding));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将字符串写入到文件中
     */
    public final static boolean write(File file, String str) {
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            randomFile.writeBytes(str);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将字符串以追加的方式写入到文件中
     */
    public final static boolean writeAppend(File file, String str) {
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(str);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将字符串以制定的编码写入到文件中
     */
    public final static boolean write(File file, String str, String encoding) {
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            randomFile.write(str.getBytes(encoding));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将字符串以追加的方式以制定的编码写入到文件中
     */
    public final static boolean writeAppend(File file, String str, String encoding) {
        try (
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw")
        ) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(str.getBytes(encoding));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 快速清空一个超大的文件
     *
     * @param file 需要处理的文件
     * @return 是否成功
     */
    public final static boolean cleanFile(File file) {
        try (
                FileWriter fw = new FileWriter(file)
        ) {
            fw.write("");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件的Mime类型
     *
     * @param file 需要处理的文件
     * @return 返回文件的mime类型
     * @throws IOException
     */
    public final static String mimeType(String file) throws IOException {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(file);
    }

    /**
     * 获取文件的类型 只利用文件头做判断故不全
     *
     * @param file 需要处理的文件
     * @return 文件类型
     */
    public final static String fileType(File file) {
        return FileHelper.getFileType(file);
    }

    /**
     * 获取文件最后的修改时间
     *
     * @param file 需要处理的文件
     * @return 返回文件的修改时间
     */
    public final static Date modifyTime(File file) {
        return new Date(file.lastModified());
    }


    /**
     * 复制文件
     *
     * @param resourcePath 源文件
     * @param targetPath   目标文件
     * @return 是否成功
     */
    public final static boolean copy(String resourcePath, String targetPath) {
        File file = new File(resourcePath);
        return copy(file, targetPath);
    }

    /**
     * 复制文件
     * 通过该方式复制文件文件越大速度越是明显
     *
     * @param file       需要处理的文件
     * @param targetFile 目标文件
     * @return 是否成功
     */
    public final static boolean copy(File file, String targetFile) {
        try (
                FileInputStream fin = new FileInputStream(file);
                FileOutputStream fout = new FileOutputStream(new File(targetFile))
        ) {
            FileChannel in = fin.getChannel();
            FileChannel out = fout.getChannel();
            //设定缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                //准备写入，防止其他读取，锁住文件
                buffer.flip();
                out.write(buffer);
                //准备读取。将缓冲区清理完毕，移动文件内部指针
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 创建多级目录
     *
     * @param paths 需要创建的目录
     * @return 是否成功
     */
    public final static boolean createPaths(String paths) {
        File dir = new File(paths);
        return !dir.exists() && dir.mkdir();
    }

    /**
     * 创建文件支持多级目录
     *
     * @param filePath 需要创建的文件
     * @return 是否成功
     */
    public final static boolean createFiles(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            if (!file.exists()) {
                return file.mkdirs();
            }
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    try {
                        return file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 删除一个文件
     *
     * @param file 需要处理的文件
     * @return 是否成功
     */
    public final static boolean deleteFile(File file) {
        return file.delete();
    }

    /**
     * 删除一个目录
     *
     * @param file 需要处理的文件
     * @return 是否成功
     */
    public final static boolean deleteDir(File file) {
        List<File> files = listFileAll(file);
        if (CheckUtil.valid(files)) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    deleteFile(f);
                }
            }
        }
        return file.delete();
    }


    /**
     * 快速的删除超大的文件
     *
     * @param file 需要处理的文件
     * @return 是否成功
     */
    public final static boolean deleteBigFile(File file) {
        return cleanFile(file) && file.delete();
    }


    /**
     * 复制目录
     *
     * @param filePath   需要处理的文件
     * @param targetPath 目标文件
     */
    public final static void copyDir(String filePath, String targetPath) {
        File file = new File(filePath);
        copyDir(file, targetPath);
    }

    /**
     * 复制目录
     *
     * @param filePath   需要处理的文件
     * @param targetPath 目标文件
     */
    public final static void copyDir(File filePath, String targetPath) {
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            createPaths(targetPath);
        }
        File[] files = filePath.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                String path = file.getName();
                if (file.isDirectory()) {
                    copyDir(file, targetPath + "/" + path);
                } else {
                    copy(file, targetPath + "/" + path);
                }
            }
        }
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path 需要处理的文件
     * @return 包含所有文件的的list
     */
    public final static List<File> listFile(String path) {
        File file = new File(path);
        return listFile(file);
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path  需要处理的文件
     * @param child 是否罗列子文件
     * @return 包含所有文件的的list
     */
    public final static List<File> listFile(String path, boolean child) {
        return listFile(new File(path), child);
    }


    /**
     * 罗列指定路径下的全部文件
     *
     * @param path 需要处理的文件
     * @return 返回文件列表
     */
    public final static List<File> listFile(File path) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path  指定的路径
     * @param child 是否罗列子目录
     * @return
     */
    public final static List<File> listFile(File path, boolean child) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (child && file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 罗列指定路径下的全部文件包括文件夹
     *
     * @param path 需要处理的文件
     * @return 返回文件列表
     */
    public final static List<File> listFileAll(File path) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                list.add(file);
                if (file.isDirectory()) {
                    list.addAll(listFileAll(file));
                }
            }
        }
        return list;
    }

    /**
     * 罗列指定路径下的全部文件包括文件夹
     *
     * @param path   需要处理的文件
     * @param filter 处理文件的filter
     * @return 返回文件列表
     */
    public final static List<File> listFileFilter(File path, FilenameFilter filter) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFileFilter(file, filter));
                } else {
                    if (filter.accept(file.getParentFile(), file.getName())) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 获取指定目录下的特点文件,通过后缀名过滤
     *
     * @param dirPath  需要处理的文件
     * @param postfixs 文件后缀
     * @return 返回文件列表
     */
    public final static List<File> listFileFilter(File dirPath, final String postfixs) {
        /*
        如果在当前目录中使用Filter讲只罗列当前目录下的文件不会罗列孙子目录下的文件
        FilenameFilter filefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(postfixs);
            }
        };
        */
        List<File> list = new ArrayList<File>();
        File[] files = dirPath.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFileFilter(file, postfixs));
                } else {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(postfixs.toLowerCase())) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 在指定的目录下搜寻文个文件
     *
     * @param dirPath  搜索的目录
     * @param fileName 搜索的文件名
     * @return 返回文件列表
     */
    public final static List<File> searchFile(File dirPath, String fileName) {
        List<File> list = new ArrayList<>();
        File[] files = dirPath.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(searchFile(file, fileName));
                } else {
                    String Name = file.getName();
                    if (Name.equals(fileName)) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查找符合正则表达式reg的的文件
     *
     * @param dirPath 搜索的目录
     * @param reg     正则表达式
     * @return 返回文件列表
     */
    public final static List<File> searchFileReg(File dirPath, String reg) {
        List<File> list = new ArrayList<>();
        File[] files = dirPath.listFiles();
        if (CheckUtil.valid(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(searchFile(file, reg));
                } else {
                    String Name = file.getName();
                    if (RegHelper.isMatche(Name, reg)) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }


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
     * 合并文件
     * @param filePart 文件片段路径
     * @param filePath 合并文件路径
     */
    public static void combineFile(List<String> filePart, String filePath) {
        //这种方法的效率比较高
        ArrayList<FileInputStream> al = new ArrayList<FileInputStream>();
        FileOutputStream fos = null;
        SequenceInputStream sis = null;

        try {
            log.info("开始合并文件，文件分片个数：{}", filePart.size());
            for (String file : filePart) {
                log.info("添加合并文件:{}", file);
                al.add(new FileInputStream(file));
            }
            final Iterator it = al.iterator();
            Enumeration en = new Enumeration() {
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public FileInputStream nextElement() {
                    return (FileInputStream) it.next();
                }
            };
            sis = new SequenceInputStream(en);
            fos = new FileOutputStream(filePath);

            byte []buff = new byte[1024];
            int len = 0;
            while ((len=sis.read(buff)) != -1) {
                fos.write(buff,0,len);
            }
            log.info("合并完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并文件
     * @param filePart 文件片段相对路径
     * @param filePath 合并文件路径
     * @param splitPath 分割文件的父目录
     */
    public static void combineFile(List<String> filePart, String filePath, String splitPath) {
        //这种方法的效率比较高
        ArrayList<FileInputStream> al = new ArrayList<FileInputStream>();
        FileOutputStream fos = null;
        SequenceInputStream sis = null;

        try {
            log.info("开始合并文件，文件分片个数：{}", filePart.size());
            for (String file : filePart) {
                log.info("添加合并文件:{}", splitPath + File.separator + file);
                al.add(new FileInputStream(splitPath + File.separator + file));
            }
            final Iterator it = al.iterator();
            Enumeration en = new Enumeration() {
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public FileInputStream nextElement() {
                    return (FileInputStream) it.next();
                }
            };
            sis = new SequenceInputStream(en);
            fos = new FileOutputStream(filePath);

            byte []buff = new byte[1024];
            int len = 0;
            while ((len=sis.read(buff)) != -1) {
                fos.write(buff,0,len);
            }
            log.info("合并完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对文件进行分割
     * @param splitFilePath 分割文件路径
     * @param partSize 分片大小
     * @return 返回分割路径列表
     */
    public static List<String> splitFile(String splitFilePath, int partSize, String sourcFilePath, String fileName) {
        List<String> splitFileList = new ArrayList<>();
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourcFilePath);
            byte []buff = new byte[partSize];
            log.info("开始分割文件：{}，文件分片大小：{}", sourcFilePath, partSize);
            int len = 0;
            int count = 1;
            while ((len=fis.read(buff)) != -1) {
                String partName = fileName + "_" + (count++)+".part";
                log.info("分割文件{}, 分片名：{}", count-1, partName);
                fos = new FileOutputStream(splitFilePath + File.separator + partName);
                splitFileList.add(partName);
                fos.write(buff,0,len);
                fos.close();
            }
            log.info("分片结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return splitFileList;
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
