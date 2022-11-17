package cn.netty.server.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@TableName("upload_task")
@Accessors(chain = true)
public class UploadTask {

    @TableId
    private String uploadTaskId;

    /**
     * MD5
     */
    private String identifier;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 存储桶
     */
    private String bucketName;

    /**
     * 存储对象
     */
    private String objectName;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 扩展名
     */
    private String extendName;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 上传状态 0 未上传 1 正在上传 2 已上传
     */
    private Integer uploadStatus;

    /**
     * uploadId
     */
    private String uploadId;
}
