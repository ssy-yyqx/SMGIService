package cn.htht.service.platform.portal.entity.user;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName UserCollection
 * @Description 用户收藏
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class UserCollection extends BaseEntity {

    /**
     * 主键id
     */
    private String id;

    /**
     * 收藏用户id
     */
    private String userId;

    /**
     * 栏目id
     */
    private String moduleId;

    /**
     * 收藏URI
     */
    private String routerURI;

    /**
     * 收藏的内容名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 点赞数
     */
    private Long collectCount;

}
