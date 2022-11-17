package cn.htht.service.platform.portal.entity.user;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @ClassName UserSupport
 * @Description 用户点赞
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class UserSupport {

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 点赞用户id
     */
    private String userId;

    /**
     * 点赞内容的路由URI
     */
    private String routerURI;

    /**
     * 点赞的内容名称
     */
    private String name;

    /**
     * 点赞
     */
    private String moduleId;

    /**
     * 点赞总数
     */
    private Long supportCount;

    /**
     * 创建时间
     */
    private Long createTime;
}
