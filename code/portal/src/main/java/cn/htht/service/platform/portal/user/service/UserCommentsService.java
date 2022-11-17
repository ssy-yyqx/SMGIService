package cn.htht.service.platform.portal.user.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.user.UserComments;
import com.github.pagehelper.PageInfo;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
public interface UserCommentsService extends BaseService<UserComments> {

    int insertComments(UserComments userComments);

    PageInfo<UserComments> getComments(String moduleId, Integer pageSize, Integer pageNumber);
}
