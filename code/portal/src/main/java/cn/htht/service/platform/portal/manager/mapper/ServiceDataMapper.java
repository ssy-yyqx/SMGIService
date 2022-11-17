package cn.htht.service.platform.portal.manager.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.manager.ServiceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceDataMapper extends BasicMapper<ServiceData> {
    /**
     * 按data_template_id查询
     * */
    List<ServiceData> selectServiceDataByTemplateId(@Param("templateId") String templateId);
}
