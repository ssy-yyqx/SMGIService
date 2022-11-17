package cn.netty.server.utils;

import cn.netty.server.config.CustomMinioClient;
import cn.netty.server.config.MinioPropertiesConfig;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Part;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MinioClientUtils {


    private final MinioPropertiesConfig minioPropertiesConfig;
    private final CustomMinioClient customMinioClient;


    @Autowired
    public MinioClientUtils(MinioPropertiesConfig minioPropertiesConfig, CustomMinioClient customMinioClient) {
        this.minioPropertiesConfig = minioPropertiesConfig;
        this.customMinioClient = customMinioClient;
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

    public void uploadMinio(String bucketName, String objectName, InputStream is, String contentType) {
        try {
            boolean isExist = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            if (StringUtils.isNotEmpty(contentType)) {
                customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).contentType(contentType).build());
            } else {
                customMinioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(is, is.available(), 524288000L).build());
            }
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

    public void deleteBatchFile(String bucketName, List<String> deleteObjects) {
        List<DeleteObject> delFileList = new ArrayList<>();
        try {
            for (String delete : deleteObjects) {
                DeleteObject deleteObject = new DeleteObject(delete);
                delFileList.add(deleteObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteFile(bucketName, delFileList);
    }

    public String previewFile(String bucketName, String objectName, String mime) {
        String url = "";
        try {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("response-content-type", mime);
            url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(objectName).extraQueryParams(paramMap).expiry(7000).build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Long getRemoteAvailableSpace(String url, String accessKey, String secretKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessKey", accessKey);
        jsonObject.put("secretKey", secretKey);
        CloseableHttpResponse response = HttpUtils.doPostJsonResponse(url + "/api/v1/login", jsonObject.toJSONString());
        Header header = response.getHeaders("Set-Cookie")[0];
        String resultJson = doGetMinioInfo(url + "/api/v1/admin/info", header.getValue());
        String availableSpace = resultJson.substring(resultJson.indexOf("availableSpace") + 16, resultJson.indexOf(",\"drivePath\""));
        long space = Long.parseLong(availableSpace);
        return space;
    }

    public static String doGetMinioInfo(String url, String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);

            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Cookie", token);
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return customMinioClient.listMultipart(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }

    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        return customMinioClient.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) {
        return customMinioClient.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
    }

    public String getPresignedObjectUrl(String bucketName, String ossFilePath, Map<String, String> queryParams, int expiry) {
            return customMinioClient.getPresignedObjectUrl(bucketName, ossFilePath, queryParams, expiry);
    }

    /**
     * 生成随机文件名，防止重复
     *
     * @param originalFilename 原始文件名
     * @return
     */
    public String generateOssUuidFileName(String originalFilename) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "files_" + sdf.format(new Date()) + UUID.randomUUID() + File.separator + originalFilename;
    }
}