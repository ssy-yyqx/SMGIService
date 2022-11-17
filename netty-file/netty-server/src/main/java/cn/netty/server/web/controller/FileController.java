package cn.netty.server.web.controller;

import cn.netty.common.constant.HttpStatus;
import cn.netty.common.constant.ResponseEntity;
import cn.netty.common.constant.TaskStatus;
import cn.netty.common.utils.file.FileMD5Utils;
import cn.netty.common.utils.uuid.IdUtils;
import cn.netty.server.config.redis.RedisCache;
import cn.netty.server.utils.HttpUtils;
import cn.netty.server.utils.MinioClientUtils;
import cn.netty.server.web.entity.FileData;
import cn.netty.server.web.entity.UploadFileDTO;
import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.service.FileDataService;
import cn.netty.server.web.service.UploadService;
import cn.netty.server.web.service.UploadTaskDetailService;
import cn.netty.server.web.service.UploadTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.minio.ListPartsResponse;
import io.minio.messages.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName FileController
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    private final MinioClientUtils minioClientUtils;

    private final RedisCache redisCache;

    private final FileDataService fileDataService;

    private final UploadTaskService uploadTaskService;

    private final UploadTaskDetailService uploadTaskDetailService;

    private final UploadService uploadService;

    public FileController(MinioClientUtils minioClientUtils, RedisCache redisCache, FileDataService fileDataService, UploadTaskService uploadTaskService, UploadTaskDetailService uploadTaskDetailService, UploadService uploadService) {
        this.minioClientUtils = minioClientUtils;
        this.redisCache = redisCache;
        this.fileDataService = fileDataService;
        this.uploadTaskDetailService = uploadTaskDetailService;
        this.uploadTaskService = uploadTaskService;
        this.uploadService = uploadService;
    }


    /**
     * 返回分片上传需要的签名数据URL及 uploadId
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping("/createMultipartUpload")
    @ResponseBody
    public ResponseEntity createMultipartUpload(String bucketName, String fileName, Integer totalChunks, String identifier, Long totalSize) {
        return ResponseEntity.success(uploadService.createMultipartUpload(bucketName, fileName, totalChunks, identifier, totalSize));
    }

    @PostMapping("/createMultipartUpload")
    public ResponseEntity upload(UploadFileDTO uploadFileDto, HttpServletRequest httpServletRequest) {
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest)httpServletRequest;
        List<MultipartFile> file = request.getFiles("file");
        UploadTask uploadTask = redisCache.getCacheObject(uploadFileDto.getIdentifier());
        if (!Optional.ofNullable(uploadTask).isPresent()) {
            uploadTask = uploadTaskService.getTaskByIdentifier(uploadFileDto.getIdentifier());
        }
        int chunkNumber = uploadFileDto.getChunkNumber();
        String uploadUrl = redisCache.getCacheMapValue(uploadTask.getUploadId(), String.valueOf(chunkNumber - 1));
        try {
            Integer resultCode = HttpUtils.doPutFile(uploadUrl, null, file);
            if (resultCode == HttpStatus.SUCCESS) {
                Map<String, Object> result = new HashMap<>();
//                QueryWrapper<UploadTaskDetail> queryWrapper = new QueryWrapper<>();
//                queryWrapper
//                        .eq("task_id", uploadTask.getUploadTaskId())
//                        .eq("status", TaskStatus.UPLOADED);
//                result.put("needMerge", uploadFileDto.getTotalChunks() == uploadTaskDetailService.count(queryWrapper) + 1);
                result.put("needMerge", true);
                result.put("skipUpload", false);
                uploadTaskDetailService.updateStatus(uploadTask.getUploadTaskId(), chunkNumber - 1, TaskStatus.UPLOADED);
                return ResponseEntity.success(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uploadKey = uploadTask.getUploadTaskId() + "_uploaded";
        List<Object> cacheList = redisCache.getCacheList(uploadKey);
        cacheList.add(chunkNumber);
        redisCache.setCacheList(uploadKey, cacheList);
        return ResponseEntity.error("上传失败");
    }

    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity list() {
        return ResponseEntity.success(fileDataService.list(new QueryWrapper<>()));
    }

    @PostMapping("/api/upload")
    public ResponseEntity upload(MultipartFile multipartFile, String bucketName) {
        String md5String = FileMD5Utils.getFileMD5String(multipartFile);
        FileData fileData = fileDataService.getFileDataByMd5(md5String);
        if (Optional.ofNullable(fileData).isPresent()) {
            return ResponseEntity.success(fileData);
        }
        if (multipartFile.getSize() > 2 * 1024 * 1024 * 1024) {
            return ResponseEntity.error(413, "请求文件大小过大，请使用大文件上传");
        }
        String objectName = minioClientUtils.generateOssUuidFileName(multipartFile.getOriginalFilename());
        try {
            minioClientUtils.uploadMinio(bucketName, objectName, multipartFile.getInputStream(), "application/octet-stream");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileData.setFileId(IdUtils.fastSimpleUUID());
        fileData.setFileName(multipartFile.getOriginalFilename());
        fileData.setFileSize(multipartFile.getSize());
        fileData.setBucketName(bucketName);
        fileData.setObjectName(objectName);
        fileData.setMd5(md5String);
        fileDataService.save(fileData);
        return ResponseEntity.success(fileData);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity delete(@RequestParam(name = "identifiers") String[] identifiers) {
        uploadService.delete(identifiers);
        return ResponseEntity.success("删除成功");
    }

    /**
     * 分片上传完后合并
     *
     * @param uploadId   返回的uploadId
     * @return /
     */
    @GetMapping("/completeMultipartUpload")
    @ResponseBody
    public boolean completeMultipartUpload(String taskId, String uploadId) {
        UploadTask task = uploadTaskService.getById(taskId);
        if (TaskStatus.UPLOADED.equals(task.getUploadStatus())) {
            return true;
        }
        try {
            Part[] parts = new Part[10000];
            String objectName = task.getObjectName();
            String bucketName = task.getBucketName();
            ListPartsResponse partResult = minioClientUtils.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            for (Part part : partResult.result().partList()) {
                parts[part.partNumber()] = new Part(part.partNumber(), part.etag());
            }
            minioClientUtils.completeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
            uploadService.completeMultipartUpload(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        redisCache.deleteObject(uploadId);
        return true;
    }
}
