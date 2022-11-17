package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.SingleTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SingleTemplateMapper extends BasicMapper<SingleTemplate> {

    SingleTemplate getTemplateByModuleId(@Param("moduleId") String moduleId);

}
