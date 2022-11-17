package cn.netty.common.entity;

import java.io.Serializable;

/**
 * @ClassName CompleteUploadRequest
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class CompleteUploadRequest implements Serializable {

    private String uploadId;

    private String taskId;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
