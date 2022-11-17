package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.user.UserOnlineDealRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserOnlineDealRecordMapper extends BasicMapper<UserOnlineDealRecord> {

    List<UserOnlineDealRecord> selectUserOnlineDealRecordList(UserOnlineDealRecord userOnlineDealRecord);

    int insertUserOnlineDealRecord(UserOnlineDealRecord userOnlineDealRecord);
}
