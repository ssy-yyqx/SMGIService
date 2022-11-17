package cn.netty.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName UtilBeans
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioPropertiesConfig {

    private String endpoint;

    private String accessKey;

    private String secretKey;
}