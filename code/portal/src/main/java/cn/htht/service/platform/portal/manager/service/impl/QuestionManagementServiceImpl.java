package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.manager.mapper.QuestionManagementMapper;
import cn.htht.service.platform.portal.manager.service.QuestionManagementService;
import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.QuestionManagement;
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
 * @date 2021/10/14
 */
@Service
public class QuestionManagementServiceImpl extends AbstractBaseService<QuestionManagement> implements QuestionManagementService {

    @Autowired
    QuestionManagementMapper questionManagementMapper;

    /**
     * 用户提问
     *
     * @param questionManagement
     */
    @Override
    public int questionCommit(QuestionManagement questionManagement) {
        questionManagement.setId(IdUtils.fastSimpleUUID());
        questionManagement.setReplyBy(null);
        questionManagement.setReplyContent(null);
        questionManagement.setReplyTime(null);
        questionManagement.setReplyUpdateBy(null);
        questionManagement.setReplyUpdateTime(null);
        return questionManagementMapper.insertQuestion(questionManagement);
    }

    /**
     * 用户评分
     *
     * @param id
     * @param serviceRating
     */
    @Override
    public int rateQuestion(String id, Integer serviceRating) {
        return questionManagementMapper.rateQuestion(id, serviceRating);
    }

    /**
     * 管理员答复
     *
     * @param id
     * @param replyBy
     * @param replyContent
     */
    @Override
    public int replyQuestion(String id, String replyBy, String replyContent) {
        return questionManagementMapper.replyQuestion(id, replyBy, replyContent);
    }

    /**
     * 管理员更新答复
     *
     * @param id
     * @param replyUpdateBy
     * @param replyContent
     */
    @Override
    public int updateReplyQuestion(String id, String replyUpdateBy, String replyContent) {
        return questionManagementMapper.updateReplyQuestion(id, replyUpdateBy, replyContent);
    }

    /**
     * 管理员答复撤回
     *
     * @param id
     */
    @Override
    public int withdrawReply(String id) {
        return questionManagementMapper.withdrawReply(id);
    }

    /**
     * 管理员查看问题列表
     */
    @Override
    public PageInfo<QuestionManagement> getAdminQuestionManagementList(Integer pageSize, Integer pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<QuestionManagement> questionManagementList = questionManagementMapper.selectQuestionManagementList(null,null);
        return new PageInfo<>(ListUtils.emptyIfNull(questionManagementList));
    }

    @Override
    public Integer getUserUnreadNumber(String userId) {
        return questionManagementMapper.getUserUnreadNumber(userId);
    }

    /**
     * 用户查看问题列表
     *
     * @param keyword
     * @param userId
     * @param pageSize
     * @param pageNumber
     */
    @Override
    public PageInfo<QuestionManagement> getUserQuestionManagementList(String keyword, String userId, Integer pageSize, Integer pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<QuestionManagement> questionManagementList = questionManagementMapper.selectQuestionManagementList(keyword, userId);
        return new PageInfo<>(ListUtils.emptyIfNull(questionManagementList));
    }

    /**
     * 搜索问题内容
     *
     * @param keyword
     */
    @Override
    public PageInfo<QuestionManagement> getQuestionManagementListByKeyword(Integer pageSize, Integer pageNumber, String questionerId, String keyword) {
        PageHelper.startPage(pageNumber, pageSize);
        List<QuestionManagement> questionManagementList = questionManagementMapper.selectQuestionManagementListByKeyword(questionerId, '%' + keyword + '%');
        return new PageInfo<>(questionManagementList);
    }

    /**
     * 删除问题
     *
     * @param id
     */
    @Override
    public int deleteQuestionById(String id) {
        return questionManagementMapper.deleteQuestionById(id);
    }

    /**
     * 根据ID获取问题
     *
     * @param id
     */
    @Override
    public QuestionManagement selectQuestionById(String id) {
        return questionManagementMapper.selectQuestionById(id);
    }

    /**
     * 用户确认已读
     *
     * @param id
     */
    @Override
    public int readQuestion(String id) {
        return questionManagementMapper.readQuestion(id);
    }
}
