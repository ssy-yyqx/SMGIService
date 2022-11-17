package cn.netty.server.web.service.impl;

import cn.netty.common.constant.TaskStatus;
import cn.netty.server.config.redis.RedisCache;
import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.entity.UploadTaskDetail;
import cn.netty.server.web.mapper.UploadTaskDetailMapper;
import cn.netty.server.web.service.UploadTaskDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UploadTaskDetailServiceImpl.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
@Service
public class UploadTaskDetailServiceImpl extends ServiceImpl<UploadTaskDetailMapper, UploadTaskDetail> implements UploadTaskDetailService {

    private final RedisCache redisCache;

    public UploadTaskDetailServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

//    @Override
//    public List<Integer> getUploadedChunk(UploadTask task) {
//        List<Integer> uploaded = new ArrayList<>();
//        Map<String, Object> cacheMap = redisCache.getCacheMap(task.getUploadId());
//        if (cacheMap.isEmpty()) {
//            // 从数据中查询
//            QueryWrapper<UploadTaskDetail> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("task_id", task.getUploadTaskId()).eq("status", TaskStatus.UPLOADED);
//            List<UploadTaskDetail> uploadTaskDetailList = list(queryWrapper);
//            uploadTaskDetailList.stream().forEach(uploadTaskDetail -> uploaded.add(uploadTaskDetail.getChunkNumber()));
//        } else {
//            cacheMap.keySet().stream().forEach(num -> uploaded.add(Integer.parseInt(num)));
//        }
//        return uploaded;
//    }

    @Override
    public List<Integer> getUploadedChunk(UploadTask task) {
        List<Integer> uploaded = redisCache.getCacheList(task.getUploadTaskId() + "_uploaded");
        if (uploaded == null || uploaded.size() <= 0) {
            QueryWrapper<UploadTaskDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("task_id", task.getUploadTaskId())
                    .eq("status", TaskStatus.UPLOADED)
                    .select("chunk_number")
                    .orderByAsc("chunk_number");
            List<UploadTaskDetail> uploadTaskDetailList = list(queryWrapper);
            uploadTaskDetailList.forEach(uploadTaskDetail -> uploaded.add(uploadTaskDetail.getChunkNumber() + 1));
        }
        return uploaded;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStatus(String taskId, Integer chunkNum, Integer status) {
        UpdateWrapper<UploadTaskDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status);
        updateWrapper.eq("task_id", taskId).eq("chunk_number", chunkNum);
        update(new UploadTaskDetail(), updateWrapper);
    }

}
