package cn.netty.server.web.service;

import cn.netty.common.constant.ResponseEntity;
import cn.netty.server.web.entity.FileData;
import cn.netty.server.web.entity.UploadFileDTO;
import cn.netty.server.web.entity.UploadFileVo;
import cn.netty.server.web.entity.UploadTask;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName UploadService.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
public interface UploadService {

    /**
     * 获取上传进度信息
     * @param uploadFileDTO 上传文件相关信息
     * @return
     */
    UploadFileVo getUploaded(UploadFileDTO uploadFileDTO);

    /**
     * 获取文件列表
     * @return
     */
    List<FileData> getFileDataList();

    /**
     * 合并完成更新一系列任务状态
     * @param task
     */
    String completeMultipartUpload(UploadTask task);

    /**
     * 返回分片上传需要的信息
     * @param bucketName
     * @param fileName
     * @param totalChunks
     * @param identifier
     * @param totalSize
     * @return
     */
    Map<String, Object> createMultipartUpload(String bucketName, String fileName, Integer totalChunks, String identifier, Long totalSize);

    /**
     * 删除数据及相关信息
     * @param identifiers
     * @return
     */
    void delete(String[] identifiers);

    /**
     * 删除数据及相关信息
     * @param identifier
     * @return
     */
    void delete(String identifier);
}
