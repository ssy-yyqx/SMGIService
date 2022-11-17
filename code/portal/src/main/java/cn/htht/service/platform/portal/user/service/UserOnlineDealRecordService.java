package cn.htht.service.platform.portal.user.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.user.UserOnlineDealRecord;

import java.util.List;

public interface UserOnlineDealRecordService extends BaseService<UserOnlineDealRecord> {
    /**
     * 根据用户id查询在线处理列表
     * @return
     */
    List<UserOnlineDealRecord> getListByUserId(String userId);
}
