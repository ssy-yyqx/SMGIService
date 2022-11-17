package cn.netty.server.web.service.impl;

import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.mapper.UploadTaskMapper;
import cn.netty.server.web.service.UploadTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UploadTaskServiceImpl.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
@Service
public class UploadTaskServiceImpl extends ServiceImpl<UploadTaskMapper, UploadTask> implements UploadTaskService {
    @Override
    public UploadTask getTaskByIdentifier(String identifier) {
        QueryWrapper<UploadTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("identifier", identifier);
        return getOne(queryWrapper);
    }

    /**
     * 更新UploadTask状态
     *
     * @param taskId
     * @param uploadId
     * @param taskStatus
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateStatus(String taskId, String uploadId, Integer taskStatus) {
        UpdateWrapper<UploadTask> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("upload_status", taskStatus);
        updateWrapper.eq("upload_task_id", taskId).eq("upload_id", uploadId);
        return this.update(new UploadTask(), updateWrapper);
    }
}
