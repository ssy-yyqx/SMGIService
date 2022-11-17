package cn.netty.server.web.service.impl;

import cn.netty.common.constant.TaskStatus;
import cn.netty.common.utils.DateUtils;
import cn.netty.common.utils.file.FileUtils;
import cn.netty.common.utils.uuid.IdUtils;
import cn.netty.server.config.redis.RedisCache;
import cn.netty.server.utils.MinioClientUtils;
import cn.netty.server.web.entity.FileData;
import cn.netty.server.web.entity.UploadFileDTO;
import cn.netty.server.web.entity.UploadFileVo;
import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.entity.UploadTaskDetail;
import cn.netty.server.web.service.FileDataService;
import cn.netty.server.web.service.UploadService;
import cn.netty.server.web.service.UploadTaskDetailService;
import cn.netty.server.web.service.UploadTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.minio.CreateMultipartUploadResponse;
import io.minio.messages.DeleteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UploadServiceImpl.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
@Service
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class UploadServiceImpl implements UploadService {
    public Logger log = LoggerFactory.getLogger(this.getClass());
    private final MinioClientUtils minioClientUtils;

    private final RedisCache redisCache;

    private final FileDataService fileDataService;

    private final UploadTaskService uploadTaskService;

    private final UploadTaskDetailService uploadTaskDetailService;

    public UploadServiceImpl(MinioClientUtils minioClientUtils, RedisCache redisCache, FileDataService fileDataService, UploadTaskService uploadTaskService, UploadTaskDetailService uploadTaskDetailService) {
        this.minioClientUtils = minioClientUtils;
        this.redisCache = redisCache;
        this.fileDataService = fileDataService;
        this.uploadTaskDetailService = uploadTaskDetailService;
        this.uploadTaskService = uploadTaskService;
    }


    /**
     * 获取上传进度信息
     *
     * @param uploadFileDTO 上传文件相关信息
     * @return
     */
    @Override
    public UploadFileVo getUploaded(UploadFileDTO uploadFileDTO) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        FileData fileData = fileDataService.getFileDataByMd5(uploadFileDTO.getIdentifier());
        if (null != fileData) {
            return uploadFileVo.setNeedMerge(false).setSkipUpload(true);
        }
        QueryWrapper<UploadTaskDetail> uploadTaskDetailQueryWrapper = new QueryWrapper<>();
        uploadTaskDetailQueryWrapper
//                .select("chunk_number", "upload_url")
                .eq("identifier", uploadFileDTO.getIdentifier());
        int count = uploadTaskDetailService.count(uploadTaskDetailQueryWrapper);
        if (count >= 0) {
            return uploadFileVo.setUploaded(uploadTaskDetailService.list(uploadTaskDetailQueryWrapper));
        }
        return null;
    }

    /**
     * 获取文件列表
     *
     * @return
     */
    @Override
    public List<FileData> getFileDataList() {
        return fileDataService.list(null);
    }

    /**
     * 合并完成更新一系列任务状态
     *
     * @param task
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String completeMultipartUpload(UploadTask task) {
        String uploadId = task.getUploadId();
        String uploadTaskId = task.getUploadTaskId();
        uploadTaskService.updateStatus(uploadTaskId, uploadId, TaskStatus.UPLOADED);
        FileData fileData = FileData.getByUploadTask(task);
        fileDataService.save(fileData);
        QueryWrapper<UploadTaskDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", uploadTaskId);
        uploadTaskDetailService.remove(queryWrapper);
        return fileData.getFileId();
    }

    /**
     * 返回分片上传需要的信息
     *
     * @param bucketName
     * @param fileName
     * @param totalChunks
     * @param identifier
     * @param totalSize
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> createMultipartUpload(String bucketName, String fileName, Integer totalChunks, String identifier, Long totalSize) {
        // 1. 根据文件名创建签名
        Map<String, Object> result = new HashMap<>();

        FileData fileData = fileDataService.getFileDataByMd5(identifier);
        if (Optional.ofNullable(fileData).isPresent()) {
            result.put("needMerge", false);
            result.put("skipUpload", true);
            result.put("uploaded", null);
            result.put("fileId", fileData.getFileId());
            return result;
        }

        // 2. 查询当前是否存在上传进度
        UploadTask task = uploadTaskService.getTaskByIdentifier(identifier);
        if (Optional.ofNullable(task).isPresent()) {
            List<Integer> uploaded = uploadTaskDetailService.getUploadedChunk(task);
            result.put("needMerge", totalChunks.equals(uploaded.size()));
            result.put("uploadId", task.getUploadId());
            result.put("skipUpload", false);
            result.put("taskId", task.getUploadTaskId());
            result.put("uploaded", uploaded);
            return result;
        }

        // 3. 获取uploadId
        String objectName = minioClientUtils.generateOssUuidFileName(fileName);
        CreateMultipartUploadResponse response = minioClientUtils.createMultipartUpload(bucketName, null, objectName, null, null);
        String uploadId = response.result().uploadId();
        result.put("needMerge", false);
        result.put("skipUpload", false);
        result.put("uploadId", uploadId);
        result.put("objectName", objectName);
        // 包装上传任务类
        String uploadTaskId = IdUtils.fastSimpleUUID();
        UploadTask uploadTask = new UploadTask()
                .setUploadTaskId(uploadTaskId)
                .setUploadId(uploadId)
                .setBucketName(bucketName)
                .setObjectName(objectName)
                .setTotalSize(totalSize)
                .setFileName(fileName)
                .setIdentifier(identifier)
                .setExtendName(FileUtils.suffix(fileName))
                .setUploadTime(DateUtils.parseDateToStr("YYYY-MM-dd HH:mm:ss", new Date()))
                .setUploadStatus(TaskStatus.INCOMPLETE_TASK);
        uploadTaskService.save(uploadTask);
        result.put("taskId", uploadTaskId);
        // 4. 请求Minio 服务，获取每个分块带签名的上传URL
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("uploadId", uploadId);
        List<UploadTaskDetail> detailList = new ArrayList<>();
        // Map<Integer, String> urlMap = new HashMap<>();
        // 4. 循环分块数 从1开始
        for (int i = 0; i <= totalChunks - 1; i++) {
            reqParams.put("partNumber", String.valueOf(i));

            String uploadUrl = minioClientUtils.getPresignedObjectUrl(bucketName, objectName, reqParams, 60 * 60 * 24);// 获取URL
            // urlMap.put(i, uploadUrl);
            redisCache.setCacheMapValue(uploadId, i + "", uploadUrl);
            UploadTaskDetail uploadTaskDetail = new UploadTaskDetail()
                    .setTaskId(uploadTask.getUploadTaskId())
                    .setIdentifier(identifier)
                    .setChunkNumber(i)
                    .setTotalChunks(totalChunks)
                    .setStatus(TaskStatus.UPLOADING);
            detailList.add(uploadTaskDetail);
        }
        redisCache.setCacheObject(identifier, uploadTask, 1, TimeUnit.DAYS);
        uploadTaskDetailService.saveBatch(detailList);
        // result.put("uploadUrl", urlMap);
        return result;
    }

    /**
     * 删除数据及相关信息
     *
     * @param identifiers
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(String[] identifiers) {
        UploadService uploadService = (UploadService) AopContext.currentProxy();
        if (null != identifiers && identifiers.length > 0) {
            for (String identifier : identifiers) {
                uploadService.delete(identifier);
            }
        }
    }

    /**
     * 删除数据及相关信息
     *
     * @param identifier
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(String identifier) {
        QueryWrapper<FileData> fileDataQueryWrapper = new QueryWrapper<FileData>().eq("md5", identifier);
        FileData fileData = fileDataService.getOne(fileDataQueryWrapper);
        if (null == fileData) {
            return;
        }
        fileDataService.remove(fileDataQueryWrapper);
        uploadTaskService.remove(new QueryWrapper<UploadTask>().eq("identifier", identifier));
        minioClientUtils.deleteFile(fileData.getBucketName(), new ArrayList<DeleteObject>(){{add(new DeleteObject(fileData.getObjectName()));}});
    }
}
