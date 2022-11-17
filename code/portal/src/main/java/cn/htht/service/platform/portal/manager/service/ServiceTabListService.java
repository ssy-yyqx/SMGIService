package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.ListTab;

import java.util.List;

public interface ServiceTabListService extends BaseService<ListTab> {

    List<ListTab> getAsListTabByTemplateId(String id);
}
