package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.manager.TabInfo;

import java.util.List;

public interface ServiceTabInfoService extends BaseService<TabInfo> {

    List<TabInfo> getAsTabInfoByTemplateId(String templateId);
}
