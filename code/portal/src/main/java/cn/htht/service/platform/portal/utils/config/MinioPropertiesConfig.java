package cn.htht.service.platform.portal.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MinioPropertiesConfig
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioPropertiesConfig {
    /**
     * 服务器 URL
     */
    private String endpoint;

    /**
     * Access key
     */
    private String accessKey;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * 运行上传的文件类型
     */
    private String allowFileType;

    /**
     * 分片上传有效期（单位：秒）
     */
    private Integer chunkUploadExpirySecond;
}
