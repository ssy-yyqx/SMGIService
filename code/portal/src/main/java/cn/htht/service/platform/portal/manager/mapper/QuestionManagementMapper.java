package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.QuestionManagement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/14
 */
@Mapper
@Component
public interface QuestionManagementMapper extends BasicMapper<QuestionManagement> {
    /**
     * 用户提问
     * */
    int insertQuestion(QuestionManagement questionManagement);
    /**
     * 用户确认已读
     * */
    int readQuestion(@Param("id")String id);
    /**
     * 用户评分
     * */
    int rateQuestion(@Param("id")String id, @Param("serviceRating") Integer serviceRating);



    /**
     * 管理员答复
     * */
    int replyQuestion(@Param("id")String id, @Param("replyBy") String replyBy, @Param("replyContent") String replyContent);
    /**
     * 管理员更新答复
     * */
    int updateReplyQuestion(@Param("id") String id, @Param("replyUpdateBy") String replyUpdateBy, @Param("replyContent") String replyContent);
    /**
     * 管理员答复撤回
     * */
    int withdrawReply(@Param("id") String id);

    /**
     * 查看问题列表
     * */
    List<QuestionManagement> selectQuestionManagementList(@Param("keyword") String keyword, @Param("userId")String userId);

    /**
     * 搜索问题内容
     * */
    List<QuestionManagement> selectQuestionManagementListByKeyword(@Param("questionerId") String questionerId, @Param("keyword")String keyword);

    /**
     * 删除提问内容
     * */
    int deleteQuestionById(@Param("id")String id);

    /**
     * 根据ID获取问题
     * */
    QuestionManagement selectQuestionById(@Param("id")String id);


    Integer getUserUnreadNumber(@Param("userId") String userId);
}
