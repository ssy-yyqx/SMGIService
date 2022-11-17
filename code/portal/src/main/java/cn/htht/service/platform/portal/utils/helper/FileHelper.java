package cn.htht.service.platform.portal.utils.helper;

import cn.htht.service.platform.portal.utils.utils.CheckUtil;
import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import org.mozilla.intl.chardet.nsDetector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileHelper
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class FileHelper {

    /**
     * **************************************************
     * 以下方式利用mozilla的jchardet作为探测工具
     */
    private static boolean found = false;
    /**
     * 如果完全匹配某个字符集检测算法, 则该属性保存该字符集的名称. 否则(如二进制文件)其值就为默认值 null, 这时应当查询属性
     */
    private static String encoding = null;

    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    static {
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); //JPEG (jpg)
        FILE_TYPE_MAP.put("png", "89504E47");  //PNG (png)
        FILE_TYPE_MAP.put("gif", "47494638");  //GIF (gif)
        FILE_TYPE_MAP.put("tif", "49492A00");  //TIFF (tif)
        FILE_TYPE_MAP.put("bmp", "424D"); //Windows Bitmap (bmp)
        FILE_TYPE_MAP.put("dwg", "41433130"); //CAD (dwg)
        FILE_TYPE_MAP.put("html", "68746D6C3E");  //HTML (html)
        FILE_TYPE_MAP.put("rtf", "7B5C727466");  //Rich Text Format (rtf)
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");
        FILE_TYPE_MAP.put("psd", "38425053");  //Photoshop (psd)
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");  //Email [thorough only] (eml)
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");  //Outlook Express (dbx)
        FILE_TYPE_MAP.put("pst", "2142444E");  //Outlook (pst)
        FILE_TYPE_MAP.put("xls", "D0CF11E0");  //MS Word
        FILE_TYPE_MAP.put("doc", "D0CF11E0");  //MS Excel 注意：word 和 excel的文件头一样
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");  //MS Access (mdb)
        FILE_TYPE_MAP.put("wpd", "FF575043"); //WordPerfect (wpd)
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");
        FILE_TYPE_MAP.put("pdf", "255044462D312E");  //Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F");  //Quicken (qdf)
        FILE_TYPE_MAP.put("pwl", "E3828596");  //Windows Password (pwl)
        FILE_TYPE_MAP.put("wav", "57415645");  //Wave (wav)
        FILE_TYPE_MAP.put("avi", "41564920");
        FILE_TYPE_MAP.put("ram", "2E7261FD");  //Real Audio (ram)
        FILE_TYPE_MAP.put("rm", "2E524D46");  //Real Media (rm)
        FILE_TYPE_MAP.put("mpg", "000001BA");  //
        FILE_TYPE_MAP.put("mov", "6D6F6F76");  //Quicktime (mov)
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11"); //Windows Media (asf)
        FILE_TYPE_MAP.put("mid", "4D546864");  //MIDI (mid)
    }

    /**
     * <p>Discription:[getFileByFile,获取文件类型,包括图片,若格式不是已配置的,则返回null]</p>
     *
     * @param file
     * @return fileType
     */
    public final static String getFileType(File file) {
        String filetype = null;
        byte[] b = new byte[50];
        try (InputStream is = new FileInputStream(file)) {
            is.read(b);
            filetype = getFileTypeByStream(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filetype;
    }

    /**
     * <p>Discription:[getFileTypeByStream]</p>
     *
     * @param b
     * @return fileType
     */
    public final static String getFileTypeByStream(byte[] b) {
        String filetypeHex = String.valueOf(getFileHexString(b));
        Iterator<Map.Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();
        while (entryiterator.hasNext()) {
            Map.Entry<String, String> entry = entryiterator.next();
            String fileTypeHexValue = entry.getValue();
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * <p>Discription:[getFileHexString]</p>
     *
     * @param b
     * @return fileTypeHex
     */
    public final static String getFileHexString(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        if (b == null || b.length <= 0) {
            return null;
        }
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param fileName        要转换的文件
     * @param fromCharsetName 源文件的编码
     * @param toCharsetName   要转换的编码
     */
    public static void convert(String fileName, String fromCharsetName, String toCharsetName) {
        convert(new File(fileName), fromCharsetName, toCharsetName, null);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param file            要转换的文件或目录
     * @param fromCharsetName 源文件的编码
     * @param toCharsetName   要转换的编码
     */
    public static void convert(File file, String fromCharsetName, String toCharsetName) {
        convert(file, fromCharsetName, toCharsetName, null);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param fileName        要转换的文件或目录
     * @param fromCharsetName 源文件的编码
     * @param toCharsetName   要转换的编码
     * @param filter          文件名过滤器
     */
    public static void convert(String fileName, String fromCharsetName, String toCharsetName, FilenameFilter filter) {
        convert(new File(fileName), fromCharsetName, toCharsetName, filter);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param file            要转换的文件或目录
     * @param fromCharsetName 源文件的编码
     * @param toCharsetName   要转换的编码
     * @param filter          文件名过滤器
     */
    public static void convert(File file, String fromCharsetName, String toCharsetName, FilenameFilter filter) {
        if (file.isDirectory()) {
            List<File> list = CheckUtil.valid(filter) ? FileUtils.listFileFilter(file, filter) :
                    FileUtils.listFile(file);
            if (CheckUtil.valid(list)) {
                for (File f : list) {
                    convert(f, fromCharsetName, toCharsetName, filter);
                }
            }
        } else {
            if (filter == null || filter.accept(file.getParentFile(), file.getName())) {
                String fileContent = getFileContentFromCharset(file, fromCharsetName);
                saveFile2Charset(file, toCharsetName, fileContent);
            }
        }
    }

    /**
     * 以指定编码方式读取文件，返回文件内容
     *
     * @param file            要转换的文件
     * @param fromCharsetName 源文件的编码
     */
    public static String getFileContentFromCharset(File file, String fromCharsetName) {
        String str = "";
        if (!Charset.isSupported(fromCharsetName)) {
            throw new UnsupportedCharsetException(fromCharsetName);
        }
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(inputStream, fromCharsetName)
        ) {
            char[] chs = new char[(int) file.length()];
            reader.read(chs);
            str = new String(chs).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 以指定编码方式写文本文件，存在会覆盖
     *
     * @param file          要写入的文件
     * @param toCharsetName 要转换的编码
     * @param content       文件内容
     */
    public static void saveFile2Charset(File file, String toCharsetName, String content) {
        if (!Charset.isSupported(toCharsetName)) {
            throw new UnsupportedCharsetException(toCharsetName);
        }
        try (
                OutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter outWrite = new OutputStreamWriter(outputStream, toCharsetName)
        ) {
            outWrite.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 传入一个文件(File)对象，检查文件编码
     *
     * @param file File对象实例
     * @return 文件编码，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(File file) throws IOException {
        return geestFileEncoding(file, new nsDetector());
    }

    /**
     * 获取文件的编码
     *
     * @param file         File对象实例
     * @param languageHint 语言提示区域代码 eg：1 : Japanese; 2 : Chinese; 3 : Simplified Chinese;
     *                     4 : Traditional Chinese; 5 : Korean; 6 : Dont know (default)
     * @return 文件编码，eg：UTF-8,GBK,GB2312形式，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(File file, int languageHint) throws IOException {
        return geestFileEncoding(file, new nsDetector(languageHint));
    }

    /**
     * 获取文件的编码
     *
     * @param path 文件路径
     * @return 文件编码，eg：UTF-8,GBK,GB2312形式，若无，则返回null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(String path) throws IOException {
        return guestFileEncoding(new File(path));
    }

    /**
     * 获取文件的编码
     *
     * @param path         文件路径
     * @param languageHint 语言提示区域代码 eg：1 : Japanese; 2 : Chinese; 3 : Simplified Chinese;
     *                     4 : Traditional Chinese; 5 : Korean; 6 : Dont know (default)
     * @return 返回文件的编码
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String guestFileEncoding(String path, int languageHint) throws FileNotFoundException, IOException {
        return guestFileEncoding(new File(path), languageHint);
    }

    /**
     * 获取文件的编码
     *
     * @param file 需要处理文件的编码
     * @param det  nsDetector
     * @return 返回文件编码
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String geestFileEncoding(File file, nsDetector det) {
        det.Init(charset -> {
            found = true;
            encoding = charset;
        });
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        try (
                BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
        ) {
            while ((len = imp.read(buf, 0, buf.length)) != -1) {
                // Check if the stream is only ascii.
                if (isAscii) {
                    isAscii = det.isAscii(buf, len);
                }

                // DoIt if non-ascii and not done yet.
                if (!isAscii && !done) {
                    done = det.DoIt(buf, len, false);
                }
            }
            det.DataEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (isAscii) {
            encoding = "ASCII";
            found = true;
        }

        if (!found) {
            String prob[] = det.getProbableCharsets();
            if (prob.length > 0) {
                // 在没有发现情况下，则取第一个可能的编码
                encoding = prob[0];
            } else {
                return null;
            }
        }
        return encoding;
    }

    /**
     * 利用FileChannel直接实现文件的对拷,对于大文件速度特别明显
     *
     * @param source
     * @param target
     */
    public static void copyFileWithChannel(File source, File target) {
        try (
                FileInputStream inStream = new FileInputStream(source);
                FileOutputStream outStream = new FileOutputStream(target);
                FileChannel in = inStream.getChannel();
                FileChannel out = outStream.getChannel();
        ) {
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用FileChannel+Buffer实现文件的读取拷贝是一种极佳的方案
     *
     * @param source
     * @param target
     */
    public static void copyFileWithBuffer(File source, File target) {
        try (
                FileInputStream inStream = new FileInputStream(source);
                FileOutputStream outStream = new FileOutputStream(target);
                FileChannel in = inStream.getChannel();
                FileChannel out = outStream.getChannel()
        ) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            while (in.read(buffer) != -1) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用Buffer实现文件的读取拷贝
     *
     * @param source
     * @param target
     */
    public static void customBufferBufferedStreamCopy(File source, File target) {
        try (
                InputStream fis = new BufferedInputStream(new FileInputStream(source));
                OutputStream fos = new BufferedOutputStream(new FileOutputStream(target))
        ) {
            byte[] buf = new byte[4096];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用Buffer实现文件的读取拷贝
     *
     * @param source
     * @param target
     */
    public static void customBufferStreamCopy(File source, File target) {
        try (
                InputStream fis = new FileInputStream(source);
                OutputStream fos = new FileOutputStream(target);
        ) {
            byte[] buf = new byte[4096];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
