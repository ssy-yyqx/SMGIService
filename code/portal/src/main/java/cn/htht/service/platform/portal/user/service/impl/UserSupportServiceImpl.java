package cn.htht.service.platform.portal.user.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import cn.htht.service.platform.portal.user.mapper.UserSupportMapper;
import cn.htht.service.platform.portal.user.service.UserSupportService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName UserSupportServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class UserSupportServiceImpl extends AbstractBaseService<UserSupport> implements UserSupportService {

    @Autowired
    private UserSupportMapper userSupportMapper;

    @Override
    public void support(UserSupport userSupport) {
        userSupport.setId(IdUtils.fastSimpleUUID());
        userSupportMapper.insertUserSupport(userSupport);
    }

    @Override
    public void cancel(String id) {
        userSupportMapper.deleteById(id);
    }

    @Override
    public void userCancel(String userId, String router) {
        userSupportMapper.deleteByUserId(userId, router);
    }

    @Override
    public List<UserSupport> getListByUser(UserSupport userSupport) {
        return userSupportMapper.selectUserSupportList(userSupport);
    }
}
