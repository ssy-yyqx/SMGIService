package cn.htht.service.platform.portal.user.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.user.UserComments;
import cn.htht.service.platform.portal.user.mapper.UserCommentsMapper;
import cn.htht.service.platform.portal.user.service.UserCommentsService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
@Service
public class UserCommentsServiceImpl extends AbstractBaseService<UserComments> implements UserCommentsService {
    @Autowired
    UserCommentsMapper userCommentsMapper;

    @Override
    public PageInfo<UserComments> getComments(String moduleId, Integer pageSize, Integer pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<UserComments> userCommentsList = userCommentsMapper.getComments(moduleId);
        return new PageInfo<>(userCommentsList);
    }

    @Override
    public int insertComments(UserComments userComments) {
        userComments.setId(IdUtils.fastSimpleUUID());
        return userCommentsMapper.insertComments(userComments);
    }
}
