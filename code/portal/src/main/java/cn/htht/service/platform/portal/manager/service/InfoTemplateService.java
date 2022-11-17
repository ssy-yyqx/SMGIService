package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.InfoTemplate;
import cn.htht.service.platform.portal.entity.manager.Module;
import com.github.pagehelper.PageInfo;

public interface InfoTemplateService extends BaseService<InfoTemplate>, TemplateService {

    PageInfo<Content> contentPageList(Module module, int pageSize, int pageNumber);
}
