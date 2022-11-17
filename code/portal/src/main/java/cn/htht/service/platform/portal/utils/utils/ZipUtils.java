package cn.htht.service.platform.portal.utils.utils;

import cn.htht.service.platform.portal.utils.config.SMGIConfig;
import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文档相关的工具类
 * 注意因为jre中自带的java.util.zip.*包不支持中文及加密压缩，如果需要选择使用zip4j包
 */
public final class ZipUtils {

    private static final int BUFFER_SIZE = 2 * 1024;
    private static Logger logger = LoggerFactory.getLogger(ZipUtils.class);
    private static byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
    private static byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};
    private static final String encoding = System.getProperty("sun.jnu.encoding");

    static {
        /**
         * 解压依据的编码是sun.zip.encoding 所以需要设置 解压编码
         */
        System.setProperty("sun.zip.encoding", encoding);
    }

    public static void unzip(String source, String target) throws IOException {
        unzip(new File(source), target);
    }

    public static void unzip(InputStream is, String zipName, String target) throws IOException {
        File zipFile = new File(SMGIConfig.createUploadPath() + File.separator + zipName);
        FileUtils.copyInputStreamToFile(is, zipFile);
        unzip(zipFile, target);
        zipFile.delete();
    }

    /**
     * @param source
     * @param target
     * @return 返回结果map, 失败返回 -1, 成功返回文件大小和解压的文件数。
     * @throws IOException
     */
    public static void unzip(File source, String target) throws IOException {
        if (!source.exists()) {
            logger.error("解压文件不存在");
            return;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        logger.info("读取解压源数据：{}", source);
        ZipFile zipFile = new ZipFile(source, "gbk");
        for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            String entryName = zipEntry.getName();
            logger.info("获取文件数据流");
            inputStream = zipFile.getInputStream(zipEntry);
            String outPath = (target + "/" + entryName).replaceAll("\\*", "/");
            logger.info("读取解压源数据文件：{}", outPath);
            File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }
            logger.info("创建解压文件输出流");
            outputStream = new FileOutputStream(outPath);
            byte[] bytes = new byte[1024];
            int length;
            int count = 0;
            while ((length = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
                if (count >= 100) {
                    outputStream.flush();
                    count = 0;
                } else {
                    count++;
                }
            }
        }
        outputStream.close();
        inputStream.close();
        zipFile.close();
        System.gc();
    }

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure) {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            logger.debug("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("系统异常：", e);
                }
            }
        }
    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, OutputStream out) {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            logger.debug("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("系统异常：", e);
                }
            }
        }
    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFilePaths 需要压缩的文件路径列表
     * @param outFilePath  压缩文件
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZipB(List<String> srcFilePaths, String outFilePath) {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outFilePath));
            for (String srcFilePath : srcFilePaths) {
                File srcFile = new File(srcFilePath);
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            logger.debug("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("系统异常：", e);
                }
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile    源文件
     * @param zos           zip输出流
     * @param name          压缩后的名称
     * @param KeepDirStruct ture：是否保留原来的目录结构,true:保留目录结构;
     *                      false：所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStruct) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStruct) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStruct) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStruct);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStruct);
                    }
                }
            }
        }

    }

    /**
     * 判断文件是否为一个压缩文件
     *
     * @param file
     * @return
     */
    public static boolean isArchiveFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            return false;
        }
        boolean isArchive = false;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return isArchive;
    }

}
