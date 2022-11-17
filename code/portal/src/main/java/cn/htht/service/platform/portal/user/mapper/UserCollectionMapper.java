package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.user.UserCollection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserCollectionMapper extends BasicMapper<UserCollection> {

    /**
     * 查询收藏列表
     * @param userCollection
     * @return
     */
    List<UserCollection> selectUserCollectionList(UserCollection userCollection);

    /**
     * 插入点赞收藏
     * @param userCollection
     * @return
     */
    int insertUserCollection(UserCollection userCollection);

    /**
     * 用户取消收藏
     * @param userId
     * @return
     */
    int deleteByUserId(String userId, String routerURI);

    /**
     * 用户个人中心取消收藏
     * @param id
     * @return
     */
    int deleteById(String id);
}
