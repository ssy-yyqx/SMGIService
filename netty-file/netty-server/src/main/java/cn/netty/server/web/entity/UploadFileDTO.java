package cn.netty.server.web.entity;

import lombok.Data;


@Data
public class UploadFileDTO {

    private String bucketName;

    private String fileName;

    private int chunkNumber;

    private long chunkSize;

    private int totalChunks;

    private long totalSize;

    private long currentChunkSize;

    private String identifier;

}
