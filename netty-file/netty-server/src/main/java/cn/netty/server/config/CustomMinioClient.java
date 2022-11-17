package cn.netty.server.config;

import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.SneakyThrows;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @ClassName CustomMinioClient
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class CustomMinioClient extends MinioClient {
    protected CustomMinioClient(MinioClient client) {
        super(client);
    }

    /**
     * 创建分片上传请求
     *
     * @param bucketName       存储桶
     * @param region           区域
     * @param objectName       对象名
     * @param headers          消息头
     * @param extraQueryParams 额外查询参数
     */
    @Override
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) {
        try {
            return super.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 完成分片上传，执行合并文件
     *
     * @param bucketName       存储桶
     * @param region           区域
     * @param objectName       对象名
     * @param uploadId         上传ID
     * @param parts            分片
     * @param extraHeaders     额外消息头
     * @param extraQueryParams 额外查询参数
     */
    @Override
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return super.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询分片数据
     *
     * @param bucketName       存储桶
     * @param region           区域
     * @param objectName       对象名
     * @param uploadId         上传ID
     * @param extraHeaders     额外消息头
     * @param extraQueryParams 额外查询参数
     */
    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return super.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPresignedObjectUrl(String bucketName, String ossFilePath, Map<String, String> queryParams, int expiry) {
        try {
            return super.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .object(ossFilePath)
                    .expiry(expiry)
                    .extraQueryParams(queryParams)
                    .build());
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        }
        return "";
    }
}
