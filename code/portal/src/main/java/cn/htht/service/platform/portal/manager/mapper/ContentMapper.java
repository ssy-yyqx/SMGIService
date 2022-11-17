package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import cn.htht.service.platform.portal.entity.vo.ServiceDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
@Component
public interface ContentMapper extends BasicMapper<Content> {

    void updateContent(@Param("contentDto") ContentDto contentDto);

    Content selectContentById(@Param("contentId") String contentId);

    List<ContentVo> getContentByModuleId(@Param("params") Map<String, Set<String>> templateTypeMap);

    List<ServiceDataVo> getServiceDataByModuleId(@Param("params") Map<String, Set<String>> templateTypeMap);

    List<DocEntity> selectAllContent();
}
