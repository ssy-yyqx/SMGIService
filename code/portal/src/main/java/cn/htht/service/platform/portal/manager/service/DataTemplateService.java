package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.DataTemplate;
import cn.htht.service.platform.portal.entity.manager.ServiceData;
import com.github.pagehelper.PageInfo;
import cn.htht.service.platform.portal.entity.manager.Template;

public interface DataTemplateService extends BaseService<DataTemplate> {
    /**
     * 查看数据业务内容
     * */
    DataTemplate getDataTemplateByModuleId(String moduleId);

    /**
     * 查看数据业务数据列表
     * */
    PageInfo<ServiceData> getServiceDataListByTemplateId(String templateId, int pageSize, int pageNumber);
    void insertDataTemplate(Template template, ContentDto contentDto);
}
