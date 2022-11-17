package cn.htht.service.platform.portal.entity.dto;

import cn.htht.service.platform.portal.entity.manager.FileData;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName PageHeaderDto
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class PageHeaderDto {

    private String moduleId;

    private String userId;

    /**
     * 跳转路由
     */
    private String redirectUrl;

    /**
     * 办理指南文件
     */
    private FileData guideFile;

    /**
     * 该用户是否点赞
     */
    private boolean isSupport;

    /**
     * 该用户是否收藏
     */
    private boolean isCollect;

    /**
     * 点赞个数
     */
    private long supportCount;

    /**
     * 收藏个数
     */
    private long collectCount;

    /**
     * 评论个数
     */
    private long commentCount;

}
