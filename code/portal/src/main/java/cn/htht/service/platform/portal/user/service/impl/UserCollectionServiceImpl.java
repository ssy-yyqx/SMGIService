package cn.htht.service.platform.portal.user.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.user.UserCollection;
import cn.htht.service.platform.portal.user.mapper.UserCollectionMapper;
import cn.htht.service.platform.portal.user.service.UserCollectionService;
import cn.htht.service.platform.portal.user.service.UserSupportService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName UserCollectionServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class UserCollectionServiceImpl extends AbstractBaseService<UserCollection> implements UserCollectionService {

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Override
    public void collect(UserCollection userCollection) {
        userCollection.setId(IdUtils.fastSimpleUUID());
        userCollectionMapper.insertUserCollection(userCollection);
    }

    @Override
    public void cancel(String id) {
        userCollectionMapper.deleteById(id);
    }

    @Override
    public void userCancel(String userId, String router) {
        userCollectionMapper.deleteByUserId(userId, router);
    }

    @Override
    public List<UserCollection> getListByUser(UserCollection userCollection) {
        return userCollectionMapper.selectUserCollectionList(userCollection);
    }
}
