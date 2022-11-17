package cn.netty.server.handler;

import cn.netty.common.constant.HttpStatus;
import cn.netty.common.constant.ResponseEntity;
import cn.netty.common.constant.TaskStatus;
import cn.netty.common.entity.CompleteUploadRequest;
import cn.netty.common.entity.CreateUploadRequest;
import cn.netty.common.entity.TransferFileStream;
import cn.netty.server.config.redis.RedisCache;
import cn.netty.server.utils.HttpUtils;
import cn.netty.server.utils.MinioClientUtils;
import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.service.UploadService;
import cn.netty.server.web.service.UploadTaskDetailService;
import cn.netty.server.web.service.UploadTaskService;
import com.alibaba.fastjson.JSONObject;
import io.minio.ListPartsResponse;
import io.minio.messages.Part;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName BigFileUpload
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component(value = "fileUploadServerHandler")
@ChannelHandler.Sharable
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(FileUploadServerHandler.class);

    private Map<String, Object> channelMap = new ConcurrentHashMap<>();

    private final MinioClientUtils minioClientUtils;

    private final RedisCache redisCache;

    private final UploadService uploadService;

    private final UploadTaskService uploadTaskService;

    private final UploadTaskDetailService uploadTaskDetailService;

    public FileUploadServerHandler(MinioClientUtils minioClientUtils, RedisCache redisCache, UploadService uploadService, UploadTaskService uploadTaskService, UploadTaskDetailService uploadTaskDetailService) {
        this.minioClientUtils = minioClientUtils;
        this.redisCache = redisCache;
        this.uploadService = uploadService;
        this.uploadTaskService = uploadTaskService;
        this.uploadTaskDetailService = uploadTaskDetailService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TransferFileStream) {
            // 文件分片上传
            uploadFile(ctx, (TransferFileStream) msg);
        } else if (msg instanceof CreateUploadRequest) {
            // 文件上传请求
            createMultipartUpload(ctx, (CreateUploadRequest) msg);
        } else if (msg instanceof CompleteUploadRequest) {
            // 文件合并
            completeMultipartUpload(ctx, (CompleteUploadRequest) msg);
        } else {
            logger.error("不能识别的请求类型!");
            ctx.writeAndFlush("error: 不能识别的请求类型!");
        }
    }

    private void completeMultipartUpload(ChannelHandlerContext ctx, CompleteUploadRequest msg) {
        UploadTask task = uploadTaskService.getById(msg.getTaskId());
        String fileId = "";
        if (TaskStatus.UPLOADED.equals(task.getUploadStatus())) {
            ctx.channel().writeAndFlush("complete:success");
        }
        try {
            Part[] parts = new Part[10000];
            String objectName = task.getObjectName();
            String bucketName = task.getBucketName();
            ListPartsResponse partResult = minioClientUtils.listMultipart(bucketName, null, objectName, 1000, 0, msg.getUploadId(), null, null);
            for (Part part : partResult.result().partList()) {
                parts[part.partNumber()] = new Part(part.partNumber(), part.etag());
            }
            minioClientUtils.completeMultipartUpload(bucketName, null, objectName, msg.getUploadId(), parts, null, null);
            fileId = uploadService.completeMultipartUpload(task);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.channel().writeAndFlush("error:文件合并失败！");
        }
        redisCache.deleteObject(msg.getUploadId());
        ctx.channel().writeAndFlush("complete:" + task.getIdentifier() + "|" + fileId);
    }

    private void createMultipartUpload(ChannelHandlerContext ctx, CreateUploadRequest msg) {
        logger.info("接受到channelId:"+ctx.channel().id()+" 文件数据:" + msg.getFileName());
        Map<String, Object> result = uploadService.createMultipartUpload(msg.getBucketName(), msg.getFileName(), msg.getTotalChunks(), msg.getIdentifier(), msg.getTotalSize());
        //以channelId为key值,存储文件名
        channelMap.put(ctx.channel().id().toString() + "_md5", msg.getIdentifier());
        if (redisCache.getCacheMap(String.valueOf(result.get("uploadId"))).size() == 0) {
            ctx.channel().writeAndFlush("error: 数据为空");
        } else {
            ctx.channel().writeAndFlush("result:" + JSONObject.toJSONString(result));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("文件上传终止");
        //保留文件上传进度
        //断开连接,移除MAP保存的文件名
        channelMap.remove(ctx.channel().id() + "_md5");
    }


    private void handleRequest(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1、如果不是文件传输请求，则进行判断是获取分页链接还是文件合并请求
        // 2、如果是文件传输请求，则进行文件上传相关的操作


    }

    private void uploadFile(ChannelHandlerContext ctx, TransferFileStream msg) {
        logger.info("接受到channelId:"+ctx.channel().id()+" 上传分片:" + msg.getChunkNumber());
        /*
            ByteBuffer byteBuffer = ByteBuffer.allocate(msg.content().capacity());
            byteBuffer.clear();
            byte[] body = new byte[msg.content().readableBytes()];
            msg.content().getBytes(0, body);
            byteBuffer.put(body);
            byteBuffer.flip();
            InputStream stream = new ByteArrayInputStream(byteBuffer.array());
            Long fileCompSize = 0L;
        */
        UploadTask uploadTask = redisCache.getCacheObject(msg.getIdentifier());
        if (!Optional.ofNullable(uploadTask).isPresent()) {
            uploadTask = uploadTaskService.getTaskByIdentifier(msg.getIdentifier());
        }
        int chunkNumber = msg.getChunkNumber();
        String uploadUrl = redisCache.getCacheMapValue(uploadTask.getUploadId(), String.valueOf(chunkNumber - 1));
        try {
            Integer resultCode = HttpUtils.doPutFile(uploadUrl, null, msg.getStream());
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
            }
        } catch (IOException e) {
            ctx.channel().writeAndFlush("upload:error|" + chunkNumber + "|" + msg.getIdentifier());
        }
        String uploadKey = uploadTask.getUploadTaskId() + "_uploaded";
        List<Object> cacheList = redisCache.getCacheList(uploadKey);
        cacheList.add(chunkNumber);
        redisCache.setCacheList(uploadKey, cacheList);
        ctx.channel().writeAndFlush("upload:success|"+ chunkNumber + "|" + msg.getIdentifier());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getLocalizedMessage());
        ctx.close();
    }

    private Map<String, String> analysisParamMap(TextWebSocketFrame message) {
        String[] paramArr = message.text().split("|");
        Map<String, String> paramMap = new HashMap<>();
        for(int i = 1; i < paramArr.length; i++ ) {
            String[] param = paramArr[i].split(":");
            paramMap.put(param[0], param[1]);
        }
        return paramMap;
    }
}
