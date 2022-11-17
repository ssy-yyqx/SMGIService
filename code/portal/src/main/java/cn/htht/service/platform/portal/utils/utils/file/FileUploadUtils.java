package cn.htht.service.platform.portal.utils.utils.file;

import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.constant.FileConstant;
import cn.htht.service.platform.portal.constant.NumberConstant;
import cn.htht.service.platform.portal.utils.config.SMGIConfig;
import cn.htht.service.platform.portal.utils.core.redis.RedisCache;
import cn.htht.service.platform.portal.utils.exception.file.FileNameLengthLimitExceededException;
import cn.htht.service.platform.portal.utils.exception.file.FileSizeLimitExceededException;
import cn.htht.service.platform.portal.utils.exception.file.InvalidExtensionException;
import cn.htht.service.platform.portal.utils.utils.DateUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.spring.SpringUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.spring5.context.SpringContextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件上传工具类
 *
 * @author htht
 */
public class FileUploadUtils {

    private static final Logger log = LoggerFactory.getLogger(SliceUploadTemplate.class);

    /**
     * 默认大小 500M
     */
    public static final long DEFAULT_MAX_SIZE = 500 * NumberConstant.MB;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = SMGIConfig.getProfile();

    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file) throws IOException {
        try {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException {
        int fileNameLength = file.getOriginalFilename().length();
        if (fileNameLength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, allowedExtension);

        String fileName = extractFilename(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        FileUtils.copyInputStreamToFile(file.getInputStream(), desc);
        return getPathFileName(baseDir, fileName);
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = DateUtils.datePath() + "/" + IdUtils.fastSimpleUUID() + "." + extension;
        return fileName;
    }

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    private static final String getPathFileName(String uploadDir, String fileName) throws IOException {
        int dirLastIndex = SMGIConfig.getProfile().length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        String pathFileName = Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
        return pathFileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws InvalidExtensionException
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }


    public static boolean upload(FileUploadDto param) {
        RandomAccessFile accessTmpFile = null;
        try {
            File tmpFile = createTmpFile(param);
            accessTmpFile = new RandomAccessFile(tmpFile, "rw");
            //这个必须与前端设定的值一致
            long chunkSize = Objects.isNull(param.getChunkSize()) ? DEFAULT_MAX_SIZE
                    : param.getChunkSize();
            long offset = chunkSize * param.getChunk();
            //定位到该分片的偏移量
            accessTmpFile.seek(offset);
            //写入该分片数据
            accessTmpFile.write(param.getMultipartFile().getBytes());
            boolean isOk = checkAndSetUploadProgress(param);
            return isOk;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            FileUtils.close(accessTmpFile);
        }
        return false;
    }


    protected static File createTmpFile(FileUploadDto fileUploadDto) {
        String fileName = fileUploadDto.getMultipartFile().getOriginalFilename();
        String tempFileName = fileName + "_tmp";
        String uploadDirPath = fileUploadDto.getUploadDirPath();
        File tmpDir = new File(uploadDirPath);
        File tmpFile = new File(uploadDirPath, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return tmpFile;
    }

    public static FileUploadDto sliceUpload(FileUploadDto param) {
        boolean isOk = upload(param);
        if (isOk) {
            File tmpFile = createTmpFile(param);
            FileUploadDto fileUploadDTO = saveAndFileUploadDTO(param.getMultipartFile().getOriginalFilename(), tmpFile);
            return fileUploadDTO;
        }
        String md5 = FileMD5Utils.getFileMD5String(param.getMultipartFile());

        return new FileUploadDto().setMd5(md5).setChunk(param.getChunk());
    }

    /**
     * 检查并修改文件上传进度
     */
    public static boolean checkAndSetUploadProgress(FileUploadDto param) {

        String fileName = param.getMultipartFile().getOriginalFilename();
        File confFile = new File(param.getUploadDirPath(), fileName + ".conf");
        byte isComplete = 0;
        RandomAccessFile accessConfFile = null;
        try {
            accessConfFile = new RandomAccessFile(confFile, "rw");
            //把该分段标记为 true 表示完成
            System.out.println("set part " + param.getChunk() + " complete");
            //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认0,已上传的就是Byte.MAX_VALUE 127
            accessConfFile.setLength(param.getChunks());
            accessConfFile.seek(param.getChunk());
            accessConfFile.write(Byte.MAX_VALUE);

            //completeList 检查是否全部完成,如果数组里是否全部都是127(全部分片都成功上传)
            byte[] completeList = FileUtils.readFileToByteArray(confFile);
            isComplete = Byte.MAX_VALUE;
            for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
                //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
                isComplete = (byte) (isComplete & completeList[i]);
                System.out.println("check part " + i + " complete?:" + completeList[i]);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            FileUtils.close(accessConfFile);
        }
        boolean isOk = setUploadProgress2Redis(param, param.getUploadDirPath(), fileName, confFile, isComplete);
        return isOk;
    }

    /**
     * 把上传进度信息存进redis
     */
    private static boolean setUploadProgress2Redis(FileUploadDto param, String uploadDirPath,
                                            String fileName, File confFile, byte isComplete) {

        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        Map<String, Boolean> dataMap = new HashMap<>();
        if (isComplete == Byte.MAX_VALUE) {
            dataMap.put(param.getMd5(), true);
            redisCache.setCacheMap(FileConstant.FILE_UPLOAD_STATUS, dataMap);
            redisCache.deleteObject(FileConstant.FILE_MD5_KEY + param.getMd5());
            confFile.delete();
            return true;
        } else {
            if (!(Boolean) redisCache.getCacheMapValue(FileConstant.FILE_UPLOAD_STATUS, param.getMd5())) {
                dataMap.put(param.getMd5(), false);
                redisCache.setCacheMap(FileConstant.FILE_UPLOAD_STATUS, dataMap);
                redisCache.setCacheObject(FileConstant.FILE_MD5_KEY + param.getMd5(),
                        uploadDirPath + FileConstant.FILE_SEPARATORCHAR + fileName + ".conf");
            }
            return false;
        }
    }
    /**
     * 保存文件操作
     */
    public static FileUploadDto saveAndFileUploadDTO(String fileName, File tmpFile) {
        FileUploadDto fileUploadDto = null;
        try {
            fileUploadDto = renameFile(tmpFile, fileName);
            if (fileUploadDto.isUploadComplete()) {
                System.out
                        .println("upload complete !!" + fileUploadDto.isUploadComplete() + " name=" + fileName);
                //TODO 保存文件信息到数据库

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return fileUploadDto;
    }
    /**
     * 文件重命名
     *
     * @param toBeRenamed 将要修改名字的文件
     * @param toFileNewName 新的名字
     */
    private static FileUploadDto renameFile(File toBeRenamed, String toFileNewName) {
        //检查要重命名的文件是否存在，是否是文件
        FileUploadDto fileUploadDTO = new FileUploadDto();
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            log.info("File does not exist: {}", toBeRenamed.getName());
            fileUploadDTO.setUploadComplete(false);
            return fileUploadDTO;
        }
        String ext = FileUtils.suffix(toFileNewName);
        String p = toBeRenamed.getParent();
        String filePath = p + FileConstant.FILE_SEPARATORCHAR + toFileNewName;
        File newFile = new File(filePath);
        //修改文件名
        boolean uploadFlag = toBeRenamed.renameTo(newFile);

        fileUploadDTO.setCreateTime(System.currentTimeMillis());
        fileUploadDTO.setUploadComplete(uploadFlag);
        fileUploadDTO.setPath(filePath);
        fileUploadDTO.setFileSize(newFile.length());
        fileUploadDTO.setExt(ext);
        fileUploadDTO.setFileId(toFileNewName);

        return fileUploadDTO;
    }

}
