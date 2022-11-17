package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.user.UserComments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
@Mapper
@Component
public interface UserCommentsMapper extends BasicMapper<UserComments> {

    int insertComments(UserComments userComments);

    List<UserComments> getComments(@Param("moduleId") String moduleId);
}
