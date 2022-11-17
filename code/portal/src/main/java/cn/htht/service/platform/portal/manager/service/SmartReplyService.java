package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.SmartReply;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
public interface SmartReplyService extends BaseService<SmartReply> {
    int insertSmartReply(SmartReply smartReply);

    int deleteSmartReply(String id);

    int updateSmartReply(SmartReply smartReply);

    PageInfo<SmartReply> selectSmartReplyListByKeyword(Integer pageSize, Integer pageNumber, String keyword);
}
