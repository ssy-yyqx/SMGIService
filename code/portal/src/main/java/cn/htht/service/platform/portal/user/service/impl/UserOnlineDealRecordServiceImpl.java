package cn.htht.service.platform.portal.user.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.user.UserOnlineDealRecord;
import cn.htht.service.platform.portal.user.mapper.UserOnlineDealRecordMapper;
import cn.htht.service.platform.portal.user.service.UserOnlineDealRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName UserOnlineDealRecordServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class UserOnlineDealRecordServiceImpl extends AbstractBaseService<UserOnlineDealRecord> implements UserOnlineDealRecordService {

    @Autowired
    private UserOnlineDealRecordMapper userOnlineDealRecordMapper;

    @Override
    public List<UserOnlineDealRecord> getListByUserId(String userId) {
        UserOnlineDealRecord u = new UserOnlineDealRecord();
        u.setUserId(userId);
        return userOnlineDealRecordMapper.selectUserOnlineDealRecordList(u);
    }
}
