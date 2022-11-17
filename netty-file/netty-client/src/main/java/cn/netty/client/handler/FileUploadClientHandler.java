package cn.netty.client.handler;

import cn.netty.client.beans.UploadFileQueue;
import cn.netty.common.constant.ResponseEntity;
import cn.netty.common.entity.CompleteUploadRequest;
import cn.netty.common.entity.CreateUploadRequest;
import cn.netty.common.entity.TransferFileStream;
import cn.netty.common.utils.file.FileMD5Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName FileUploadClientHandler
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component(value = "fileUploadClientHandler")
@ChannelHandler.Sharable
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadClientHandler.class);

    private String fileKey;

    private String bucketName;

    private File file;

    private ConcurrentHashMap<String, Map<Integer, Boolean>> fileCompleteMap = new ConcurrentHashMap<>();

    // 分片大小
    private static final int CHUNK_SIZE = 5 * 1024 * 1024;

    public FileUploadClientHandler() {
    }

    public FileUploadClientHandler(String fileKey, String bucketName) {
        this.fileKey = fileKey;
        this.bucketName = bucketName;
        this.file = UploadFileQueue.getInstance().getFile(fileKey);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送上传请求，等待响应
        logger.info("准备上传文件:{}", file.getName());
        CreateUploadRequest createUploadRequest = createUploadRequest();
        ctx.channel().writeAndFlush(createUploadRequest);
    }

    private CreateUploadRequest createUploadRequest() {
        CreateUploadRequest createUploadRequest = new CreateUploadRequest();
        createUploadRequest.setFileName(file.getName());
        createUploadRequest.setBucketName(bucketName);
        createUploadRequest.setIdentifier(fileKey);
        createUploadRequest.setTotalSize(file.length());
        Long totalChunks = file.length() / CHUNK_SIZE + 1;
        createUploadRequest.setTotalChunks(totalChunks.intValue());
        return createUploadRequest;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            String info = (String) msg;
            String[] arr = info.split(":");
            if (arr[0].equals("error")) {
                logger.error("上传失败：{}", arr[1]);
                ctx.close();
            } else if (arr[0].equals("upload")) {
                String[] uploadResult = arr[1].split("|");
                if (uploadResult[0].equals("error")) {
                    putFileMap(fileCompleteMap, uploadResult[1], uploadResult[2], false);
                } else {
                    putFileMap(fileCompleteMap, uploadResult[1], uploadResult[2], true);
                }
            } else if (arr[0].equals("complete")) {
                // 合并结果
                String[] completeResult = arr[1].split("|");
                UploadFileQueue.getInstance().addResult(completeResult[0], completeResult[1]);
                ctx.close();
            } else if (arr[0].equals("result")) {
                // 获取到分片结果
                String result = info.substring(info.indexOf(":") + 1);
                JSONObject jsonObject = JSONObject.parseObject(result);
                List<Long> startIndex = new ArrayList<>();
                if (jsonObject.containsKey("fileId")) {
                    UploadFileQueue.getInstance().addResult(fileKey, jsonObject.getString("fileId"));
                    ctx.close();
                } else if (jsonObject.containsKey("uploaded")) {
                    JSONArray uploaded = jsonObject.getJSONArray("uploaded");
                    if (uploaded.size() > 0) {
                        for (int i = 0; i < uploaded.size(); i++) {
                            //计算起始位置
                            Integer chunkNum = uploaded.getInteger(i);
                            startIndex.add((long) (CHUNK_SIZE * (chunkNum - 1)));
                        }
                    }
                }
                // 处理分片数据
                FileInputStream fileInputStream = new FileInputStream(file);
                long startPoint = 0;
                Long totalChunks = file.length() / CHUNK_SIZE + 1;
                for (int i = 0; i < totalChunks; i++) {
                    TransferFileStream transferFileStream = new TransferFileStream();
                    // 如果已经上传了，则跳过
                    if (startIndex.contains(startPoint)) {
                        continue;
                    }
                    fileInputStream.skip(startPoint);
                    byte[] bytes = fileInputStream.readNBytes(CHUNK_SIZE);
                    transferFileStream.setStream(new ByteArrayInputStream(bytes));
                    transferFileStream.setChunkNumber(i);
                    transferFileStream.setIdentifier(fileKey);
                    ctx.channel().writeAndFlush(transferFileStream);
                    startPoint += CHUNK_SIZE;
                }

                // 检查每个分片是否上传正确
                Boolean isFinish = checkUpload(fileCompleteMap.get(fileKey));
                if (isFinish) {
                    CompleteUploadRequest completeUploadRequest = new CompleteUploadRequest();
                    completeUploadRequest.setUploadId(jsonObject.getString("uploadId"));
                    completeUploadRequest.setTaskId(jsonObject.getString("taskId"));
                    ctx.channel().writeAndFlush(completeUploadRequest);
                }
            }
        }
    }

    private Boolean checkUpload(Map<Integer, Boolean> completeMap) {
        int times = 0;
        while (true) {
            if(completeMap.containsValue(Boolean.FALSE) && times <= 10) {
                try {
                    Thread.sleep(3000);
                    times++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                if (times > 10) {
                    return false;
                }
                return true;
            }
        }
    }

    private void putFileMap(ConcurrentHashMap<String, Map<Integer, Boolean>> fileCompleteMap, String chunkNum, String identifier, Boolean isSuccess) {
        Map<Integer, Boolean> uploadResultMap;
        if (fileCompleteMap.containsKey(identifier)) {
            uploadResultMap = fileCompleteMap.get(identifier);
        } else {
            uploadResultMap = new HashMap<>();
        }
        uploadResultMap.put(Integer.parseInt(chunkNum), isSuccess);
        fileCompleteMap.put(identifier, uploadResultMap);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

}

