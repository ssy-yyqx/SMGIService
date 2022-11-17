package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.InfoTemplate;
import cn.htht.service.platform.portal.entity.manager.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InfoTemplateMapper extends BasicMapper<InfoTemplate> {

    InfoTemplate getTemplateByModuleId(@Param("id") String id);

    InfoTemplate getTemplateByContentId(String contentId);

    List<Content> page(@Param("id") String id);
}
