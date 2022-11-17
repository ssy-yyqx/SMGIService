package cn.htht.service.platform.portal.utils.utils;

import cn.htht.service.platform.portal.constant.NumberConstant;
import cn.htht.service.platform.portal.entity.dto.FileUpload;
import cn.htht.service.platform.portal.utils.config.CustomMinioClient;
import cn.htht.service.platform.portal.utils.config.MinioPropertiesConfig;
import cn.htht.service.platform.portal.utils.exception.CustomException;
import cn.htht.service.platform.portal.utils.utils.file.FileUtils;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.collect.HashMultimap;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.minio.messages.Part;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName MinioClientUtils
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component
public class MinioClientUtils {

    private final static Logger log = LoggerFactory.getLogger(MinioClientUtils.class);

    private final MinioPropertiesConfig minioPropertiesConfig;
    private final CustomMinioClient customMinioClient;

    @Autowired
    public MinioClientUtils(MinioPropertiesConfig minioPropertiesConfig, CustomMinioClient customMinioClient) {
        this.minioPropertiesConfig = minioPropertiesConfig;
        this.customMinioClient = customMinioClient;
    }

    public final static Map<Long, Long> CHUNKS_SIZE_MAP = new HashMap<>();
    static {
        // 50MB -- 500MB
        CHUNKS_SIZE_MAP.put(5L, NumberConstant.FIVE * NumberConstant.MB);
        // 500MB -- 5000MB
        CHUNKS_SIZE_MAP.put(50L, NumberConstant.TEN * NumberConstant.MB);
        // 5000MB 以上
        CHUNKS_SIZE_MAP.put(500L, NumberConstant.HUNDRED * NumberConstant.MB);
    }

    /**
     * 上传文件至minio：
     * 2、若小于1G，则上传原始文件
     *
     * @param bucketName  桶
     * @param objectName  对象
     * @param filePath    上传文件地址
     * @param contentType 文件MIME
     * @return 返回上传文件路径，若采用分片则为上传包含分片文件夹的名称_part
     */
    public String uploadMinio(String bucketName, String objectName, String filePath, String contentType) {
        try {
            boolean isExist = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            FileInputStream is = null;
            if (!isExist) {
                log.info("当前不存在bucketname，进行创建");
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            File file = new File(filePath);
            // 如果大于1G就分片上传
            if (file.length() > 1024 * 1024 * 1024) {
                List<String> splitFile = FileUtils.splitFile(file.getParent(), 500 * 1024 * 1024, file.getCanonicalPath(), file.getName().substring(0, file.getName().lastIndexOf(".")));
                for (String f : splitFile) {
                    File partFile = new File(file.getParentFile() + File.separator + f);
                    is = new FileInputStream(partFile);
                    log.info("开始将分片文件上传至minio, 分片文件路径为:{}， 分片文件大小：{}", partFile, partFile.length());
                    String partObjectName = objectName.substring(0, objectName.lastIndexOf(".")) + File.separator + f;
                    customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(partObjectName).stream(is, is.available(), 524288000L).build());
                    FileUtils.deleteDir(partFile);
                }
                filePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_part";
            } else {
                is = new FileInputStream(file);
                log.info("开始将文件上传至minio, 文件路径为:{}， 文件大小：{}", filePath, file.length());
                if (StringUtils.isNotBlank(contentType)) {
                    customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).contentType(contentType).build());
                } else {
                    customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).build());
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public void uploadMinio(String bucketName, String objectName, InputStream is, String contentType) {
        try {
            boolean isExist = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                log.info("当前不存在bucketname，进行创建");
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            if (StringUtils.isNotBlank(contentType)) {
                customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).contentType(contentType).build());
            } else {
                customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadMinio(String bucketName, String objectName, MultipartFile multipartFile, String contentType) {
        FileUpload fileUpload = null;
        try {
            boolean isExist = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                log.info("当前不存在bucketname，进行创建");
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            if (multipartFile.getSize() < 50 * NumberConstant.MB) {
                customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(multipartFile.getInputStream(), multipartFile.getInputStream().available(), 524288000L).build());
                return;
            } else {
                Long chunkSize = CHUNKS_SIZE_MAP.get(multipartFile.getSize() / 10);
                // 计算分片数量
                double partCount = Math.ceil(multipartFile.getSize() / chunkSize);
                fileUpload = initMultiPartUpload(bucketName, objectName, (int) partCount, contentType);
                //保存分页数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 分片文件下载
     *
     * @param downloadPath 文件下载路径
     * @param ObjectName   文件前缀
     */
    public void downloadPartFile(String downloadPath, String bucketName, String ObjectName) {
        InputStream fileInputStream = null;
        File file = new File(downloadPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        log.info("分片文件，逐个从minio下载");
        Iterable<Result<Item>> results = customMinioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(ObjectName).recursive(true).build());
        List<String> partFileList = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                String partOutput = file.getParentFile() + File.separator + item.objectName().substring(item.objectName().lastIndexOf(File.separator) + 1);
                FileOutputStream out = new FileOutputStream(partOutput);
                log.info("开始下载分片文件:{}, 大小size:{}", item.objectName(), item.size());
                fileInputStream = customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                IOUtils.copy(fileInputStream, out);
                partFileList.add(partOutput);
                fileInputStream.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtils.combineFile(partFileList, downloadPath);
        for (String part : partFileList) {
            File f = new File(part);
            f.delete();
        }
    }

    public void downloadFile(String bucketName, String objectName, String downloadPath) {
        try {
            File file = new File(downloadPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(downloadPath);
            InputStream fileInputStream = customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
            IOUtils.copy(fileInputStream, out);
            fileInputStream.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String bucketName, List<DeleteObject> delFileList) {
        for (Result<DeleteError> errorResult : customMinioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(delFileList).build())) {
            DeleteError error = null;
            try {
                error = errorResult.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showPicture(String objectName, String bucketName, HttpServletResponse response) throws IOException {
        //使用字节流读取本地图片
        try {
            //从minio获取到文件流
            InputStream inputStream = customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream downloadFile(String bucketName, String objectName) {
        InputStream inputStream = null;
        try {
            inputStream = customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public void deletePartFile(String bucketName, String ObjectName) {
        Iterable<Result<Item>> results = customMinioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(ObjectName).recursive(true).build());
        List<DeleteObject> delFileList = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                DeleteObject deleteObject = new DeleteObject(item.objectName());
                delFileList.add(deleteObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteFile(bucketName, delFileList);
    }

    /**
     * 初始化获取 uploadId
     * @param objectName 文件名
     * @param partCount 分片总数
     * @param contentType contentType
     * @return
     */
    public FileUpload initMultiPartUpload(String bucketName, String objectName, int partCount, String contentType) {
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);

        String uploadId = "";
        List<String> partUrlList = new ArrayList<>();
        try {
            // 获取 uploadId
            uploadId = customMinioClient.getUploadId(bucketName,
                    null,
                    objectName,
                    headers,
                    null);
            Map<String, String> paramsMap = new HashMap<>(2);
            paramsMap.put("uploadId", uploadId);
            for (int i = 1; i <= partCount; i++) {
                paramsMap.put("partNumber", String.valueOf(i));
                // 获取上传 url
                String uploadUrl = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        // 注意此处指定请求方法为 PUT，前端需对应，否则会报 `SignatureDoesNotMatch` 错误
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(objectName)
                        // 指定上传连接有效期
                        .expiry(minioPropertiesConfig.getChunkUploadExpirySecond(), TimeUnit.SECONDS)
                        .extraQueryParams(paramsMap).build());
                partUrlList.add(uploadUrl);
            }
        } catch (Exception e) {
            log.error("initMultiPartUpload Error:" + e);
            return null;
        }
        // 过期时间
        LocalDateTime expireTime = LocalDateTimeUtil.offset(LocalDateTime.now(), minioPropertiesConfig.getChunkUploadExpirySecond(), ChronoUnit.SECONDS);

        FileUpload result = new FileUpload();
        result.setUploadId(uploadId);
        result.setExpiryTime(expireTime);
        result.setUploadUrls(partUrlList);
        return result;
    }

    /**
     * 分片合并
     * @param objectName 文件名
     * @param uploadId uploadId
     * @return
     */
    public String mergeMultiPartUpload(String bucketName, String objectName, String uploadId) {
        // todo 最大1000分片 这里好像可以改吧
        Part[] parts = new Part[1000];
        int partIndex = 0;
        ListPartsResponse partsResponse = listUploadPartsBase(bucketName, objectName, uploadId);
        if (null == partsResponse) {
            log.error("查询文件分片列表为空");
            throw new CustomException("分片列表为空");
        }
        for (Part partItem : partsResponse.result().partList()) {
            parts[partIndex] = new Part(partIndex + 1, partItem.etag());
            partIndex++;
        }
        ObjectWriteResponse objectWriteResponse;
        try {
            objectWriteResponse = customMinioClient.mergeMultipart(bucketName, null, objectName, uploadId, parts, null, null);
        } catch (Exception e) {
            log.error("分片合并失败：" + e);
            throw new CustomException("分片合并失败：" + e.getMessage());
        }
        if (null == objectWriteResponse) {
            log.error("合并失败，合并结果为空");
            throw new CustomException("分片合并失败");
        }
        return objectWriteResponse.region();
    }

    /**
     * 获取已上传的分片列表
     * @param objectName 文件名
     * @param uploadId uploadId
     * @return
     */
    public List<Integer> listUploadChunkList(String bucketName, String objectName, String uploadId) {
        ListPartsResponse partsResponse = listUploadPartsBase(bucketName, objectName, uploadId);
        if (null == partsResponse) {
            return Collections.emptyList();
        }
        return partsResponse.result().partList().stream().map(Part::partNumber).collect(Collectors.toList());
    }

    private ListPartsResponse listUploadPartsBase(String bucketName, String objectName, String uploadId) {
        int maxParts = 1000;
        ListPartsResponse partsResponse;
        try {
            partsResponse = customMinioClient.listMultipart(bucketName, null, objectName, maxParts, 0, uploadId, null, null);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | IOException | XmlParserException | InvalidKeyException | InternalException | InvalidResponseException e) {
            log.error("查询文件分片列表错误：{}，uploadId:{}", e, uploadId);
            return null;
        }
        return partsResponse;
    }

}
