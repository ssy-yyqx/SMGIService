package cn.htht.service.platform.portal.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FileUpload
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
public class FileUpload {

    /**
     * 文件上传id
     */
    private String uploadId;

    /**
     * 文件分片上传有效时间
     */
    private LocalDateTime expiryTime;

    /**
     * 分片上传 url 集合
     */
    private List<String> uploadUrls;
}
