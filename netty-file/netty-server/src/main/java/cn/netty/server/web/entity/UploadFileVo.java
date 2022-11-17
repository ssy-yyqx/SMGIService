package cn.netty.server.web.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class UploadFileVo {

    /**
     * 时间戳
     */
    private String timeStampName;
    /**
     * 跳过上传
     */
    private boolean skipUpload;
    /**
     * 是否需要合并分片
     */
    private boolean needMerge;
    /**
     * 已经上传的分片
     */
    private List<UploadTaskDetail> uploaded;


}