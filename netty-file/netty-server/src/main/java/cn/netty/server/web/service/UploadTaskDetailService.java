package cn.netty.server.web.service;

import cn.netty.server.web.entity.UploadTask;
import cn.netty.server.web.entity.UploadTaskDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName UploadTaskDetailService.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月25日
 */
public interface UploadTaskDetailService extends IService<UploadTaskDetail> {

    /**
     * 获取已上传的分片
     * @param task
     * @return
     */
    List<Integer> getUploadedChunk(UploadTask task);

    /**
     * 修改状态
     * @param taskId
     * @param chunkNum
     */
    void updateStatus(String taskId, Integer chunkNum, Integer status);

}
