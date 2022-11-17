package cn.htht.service.platform.portal.user.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import org.apache.catalina.User;

import java.util.List;

/**
 * UserSupport 服务层
 *
 * @author piesat
 * @date 2021/9/16
 */
public interface UserSupportService extends BaseService<UserSupport> {

    /**
     * 用户进行点赞
     * @param userSupport
     */
    void support(UserSupport userSupport);

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
     * 查询用户的点赞列表
     * @param userSupport
     * @return
     */
    List<UserSupport> getListByUser(UserSupport userSupport);
}
