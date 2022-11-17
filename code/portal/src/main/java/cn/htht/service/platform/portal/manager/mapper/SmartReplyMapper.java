package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.SmartReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
@Mapper
@Component
public interface SmartReplyMapper extends BasicMapper<SmartReply> {
    /**
     * 增加
     * */
    int insertSmartReply(SmartReply smartReply);
    /**
     * 删
     * */
    int deleteSmartReply(@Param("id") String id);
    /**
     * 改
     * */
    int updateSmartReply(SmartReply smartReply);
    /**
     * 查
     * */
    List<SmartReply> selectSmartReplyListByKeyword(@Param("keyword") String keyword);
}
