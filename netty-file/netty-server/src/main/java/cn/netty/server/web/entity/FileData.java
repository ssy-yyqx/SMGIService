package cn.netty.server.web.entity;

import cn.netty.common.utils.uuid.IdUtils;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName FileData
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@TableName("t_file_data")
@Accessors(chain = true)
public class FileData {

    public static FileData getByUploadTask(UploadTask uploadTask){
        return new FileData()
                .setFileId(IdUtils.fastUUID())
                .setBucketName(uploadTask.getBucketName())
                .setExtend(uploadTask.getExtendName())
                .setFileName(uploadTask.getFileName())
                .setFileSize(uploadTask.getTotalSize())
                .setMd5(uploadTask.getIdentifier())
                .setObjectName(uploadTask.getObjectName())
                .setUploadId(uploadTask.getUploadId());
    }

    /**
     * 文件id
     */
    @TableId
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * md5
     */
    private String md5;

    /**
     * 存储桶
     */
    private String bucketName;

    /**
     * 存储对象
     */
    private String objectName;

    /**
     * 扩展名
     */
    private String extend;

    /**
     * 断点上传截止位置，上传完成为 -1
     */
    private String uploadId;

}
