package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.QuestionManagement;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/14
 */
@Component
public interface QuestionManagementService extends BaseService<QuestionManagement> {
    /**
     * 用户提问
     * */
    int questionCommit(QuestionManagement questionManagement);

    /**
     * 用户评分
     * */
    int rateQuestion(String id, Integer serviceRating);


    /**
     * 管理员答复
     * */
    int replyQuestion(String id, String replyBy, String replyContent);
    /**
     * 管理员更新答复
     * */
    int updateReplyQuestion(String id, String replyUpdateBy, String replyContent);
    /**
     * 管理员答复撤回
     * */
    int withdrawReply(String id);

    /**
     * 管理员查看问题列表
     * */
    PageInfo<QuestionManagement> getAdminQuestionManagementList(Integer pageSize, Integer pageNumber);

    /**
     * 用户查看问题列表
     * */
    PageInfo<QuestionManagement> getUserQuestionManagementList(String keyword, String userId, Integer pageSize, Integer pageNumber);

    /**
     * 搜索问题内容
     * */
    PageInfo<QuestionManagement> getQuestionManagementListByKeyword(Integer pageSize, Integer pageNumber, String questionerId, String keyword);

    /**
     * 删除问题
     * */
    int deleteQuestionById(String id);
    /**
     * 用户确认已读
     * */
    int readQuestion(String id);

    /**
     * 根据ID获取问题
     * */
    QuestionManagement selectQuestionById(String id);

    Integer getUserUnreadNumber(String userId);
}
