package cn.netty.common.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.bouncycastle.jcajce.provider.digest.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName FileMD5Utils
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class FileMD5Utils {

    private static final Logger log = LoggerFactory.getLogger(FileMD5Utils.class);

    //首先初始化一个字符数组，用来存放每个16进制字符
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected static MessageDigest messageDigest = null;
    static {
        try {
            /**
             *  创建 MessageDigest 对象
             *  创建报文摘要实例:
             *  象所有的引擎类一样，获取某类报文摘要算法的 MessageDigest 对象的途径是
             *  调用 MessageDigest 类中的 getInstance 静态 factory 方法
             */
            messageDigest = MessageDigest.getInstance("MD5");
            //拿到一个MD5转换器，返回实现指定摘要算法的 MessageDigest 对象。
        } catch (NoSuchAlgorithmException exception) {
            log.error(MD5.class.getName()+"初始化失败，MessageDigest不支持MD5!");
        }
    }

    public static String getFileMD5String(MultipartFile file) {
        try {
            return getFileMD5((FileInputStream) file.getInputStream(), file.getSize());
        } catch (IOException e) {
            log.error("文件读取出现错误");
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 计算文件的MD5
     * @param file 文件对象
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
                return getFileMD5(in, file.length());
        } catch (FileNotFoundException e) {
            log.error("文件不存在");
        } catch (IOException e) {
            log.error("文件读取出现错误");
        }
        return "";
    }

    private static String getFileMD5(FileInputStream inputStream, long length) throws IOException {

        FileChannel ch = inputStream.getChannel();

        //缓冲区大小（这个可以抽出一个参数）
        int maxSize= 256 * 1024;

        long startPosition = 0L;
        long step= length / maxSize;
        /**
         *  MappedByteBuffer 将文件直接映射到内存（这里的内存指的是虚拟内存，并不是物理内存，后面说证明这一点）。
         *  通常，可以映射整个文件，如果文件比较大的话可以分段进行映射，只要指定文件的那个部分就可以。
         *  而且，与ByteBuffer十分类似，没有构造函数（你不可new MappedByteBuffer（）来构造一个MappedByteBuffer），
         *  我们可以通过 java.nio.channels.FileChannel 的 map() 方法来获取 MappedByteBuffer 。
         *  其实说的通俗一点就是Map把文件的内容被映像到计算机虚拟内存的一块区域，
         *  这样就可以直接操作内存当中的数据而无需操作的时候每次都通过I/O去物理硬盘读取文件，所以效率上有很大的提升！
         *
         */
        if(step == 0){
            /**
             * 1.MappedByteBuffer是ByteBuffer的直接子类
             * 因为Buffer是用字节数组实现的，MappedByteBuffer也是用字节数组实现的
             * 所以，byteBuffer是字节数组类型
             *
             * 2.向已初始化的报文摘要对象messageDigest提供数据：
             * 这将通过调用 update（更新）方法来完成，传入需要计算的字节数组byteBuffer
             *
             * 3.执行MessageDigest对象的digest()方法完成计算，计算的结果通过字节类型的数组返回。
             */
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, length);//READ_ONLY,（只读）
            messageDigest.update(byteBuffer);
            return bufferToHex(messageDigest.digest());
        }

        /**
         * 但是如果是一个特别大的文件，一下子把一个文件的数组全部读到内存中，那么估计内存也吃不消
         * 所以，当文件大于maxSize时，将文件分段计算，每一段大小都<=maxSize
         */
        for(int i=0;i<step;i++){
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,maxSize);
            messageDigest.update(byteBuffer);
            startPosition+=maxSize;
        }

        if(startPosition == length){
            return bufferToHex(messageDigest.digest());
        }

        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,length - startPosition);
        messageDigest.update(byteBuffer);
        return bufferToHex(messageDigest.digest());
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messageDigest.update(bytes);
        return bufferToHex(messageDigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        // new一个字符串，这个就是用来存放结果字符串的
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();//返回结果字符串
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        //通过位运算（位运算效率高），转换成字符放到字符c0/c1中去：
        // 取字节中高 4 位的数字转换为逻辑右移，将符号位一起右移
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        // 取字节中低 4位的数字转换
        char c1 = hexDigits[bt & 0xf];
        //将结果添加到stringbuffer里面
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
