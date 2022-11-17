package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserSupportMapper extends BasicMapper<UserSupport> {

    /**
     * 查询点赞列表
     * @param userSupport
     * @return
     */
    List<UserSupport> selectUserSupportList(UserSupport userSupport);

    /**
     * 插入点赞数据
     * @param userSupport
     * @return
     */
    int insertUserSupport(UserSupport userSupport);

    /**
     * 用户取消点赞
     * @param userId
     * @return
     */
    int deleteByUserId(String userId, String routerURI);

    /**
     * 用户个人中心取消点赞
     * @param id
     * @return
     */
    int deleteById(String id);
}
