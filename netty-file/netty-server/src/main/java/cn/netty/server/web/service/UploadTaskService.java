package cn.netty.server.web.service;

import cn.netty.server.web.entity.UploadTask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UploadTaskService.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
public interface UploadTaskService extends IService<UploadTask> {

    UploadTask getTaskByIdentifier(String identifier);

    /**
     * 更新UploadTask状态
     * @param taskId
     * @param uploadId
     * @param taskStatus
     * @return
     */
    boolean updateStatus(String taskId, String uploadId, Integer taskStatus);
}
