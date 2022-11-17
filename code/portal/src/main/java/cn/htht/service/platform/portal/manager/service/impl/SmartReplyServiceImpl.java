package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.SmartReply;
import cn.htht.service.platform.portal.manager.mapper.SmartReplyMapper;
import cn.htht.service.platform.portal.manager.service.SmartReplyService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
@Service
public class SmartReplyServiceImpl extends AbstractBaseService<SmartReply> implements SmartReplyService {
    @Autowired
    SmartReplyMapper smartReplyMapper;

    @Override
    public int updateSmartReply(SmartReply smartReply) {
        return smartReplyMapper.updateSmartReply(smartReply);
    }

    @Override
    public int deleteSmartReply(String id) {
        return smartReplyMapper.deleteSmartReply(id);
    }

    @Override
    public PageInfo<SmartReply> selectSmartReplyListByKeyword(Integer pageSize, Integer pageNumber, String keyword) {
        PageHelper.startPage(pageNumber, pageSize);
        if (!(keyword == null || "".equals(keyword))){
            keyword = '%' + keyword + '%';
        }
        List<SmartReply> smartReplyList = smartReplyMapper.selectSmartReplyListByKeyword(keyword);
        return new PageInfo<>(ListUtils.emptyIfNull(smartReplyList));
    }

    @Override
    public int insertSmartReply(SmartReply smartReply) {
        smartReply.setId(IdUtils.fastSimpleUUID());
        return smartReplyMapper.insertSmartReply(smartReply);
    }
}
