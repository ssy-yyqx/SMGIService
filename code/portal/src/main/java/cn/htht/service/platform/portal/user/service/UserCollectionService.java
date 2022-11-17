package cn.htht.service.platform.portal.user.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.user.UserCollection;

import java.util.List;

/**
 * UserCollection 服务层
 *
 * @author piesat
 * @date 2021/9/16
 */
public interface UserCollectionService extends BaseService<UserCollection> {

    /**
     * 用户进行点赞
     * @param userCollection
     */
    void collect(UserCollection userCollection);

    /**
     * 用户个人中心取消点赞
     * @param id
     */
    void cancel(String id);

    /**
     * 用户页面取消点赞
     */
    void userCancel(String userId, String router);

    /**
     * 查询用户的收藏列表
     * @param userCollection
     * @return
     */
    List<UserCollection> getListByUser(UserCollection userCollection);
}
