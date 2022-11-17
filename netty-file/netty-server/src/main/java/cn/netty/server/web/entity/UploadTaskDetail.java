package cn.netty.server.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("upload_task_detail")
@Accessors(chain = true)
public class UploadTaskDetail {
    @TableId(type = IdType.AUTO)
    private Long uploadTaskDetailId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 当前分片个数
     */
    private Integer chunkNumber;

    /**
     * 当前分片大小
     */
    private Integer chunkSize;

    /**
     * 总分片个数
     */
    private Integer totalChunks;

    /**
     * 总大小
     */
    private Integer totalSize;

    /**
     * 文件MD5
     */
    private String identifier;

    /**
     * 分片上传状态
     */
    private Integer status;
}
