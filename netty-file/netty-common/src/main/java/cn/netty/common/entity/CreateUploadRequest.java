package cn.netty.common.entity;

import java.io.Serializable;

/**
 * @ClassName CreateUploadRequest
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class CreateUploadRequest implements Serializable {

    private String bucketName;

    private String fileName;

    private Integer totalChunks;

    private String identifier;

    private Long totalSize;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
}
