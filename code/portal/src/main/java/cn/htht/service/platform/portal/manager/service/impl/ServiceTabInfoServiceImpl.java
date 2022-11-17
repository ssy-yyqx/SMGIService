package cn.htht.service.platform.portal.manager.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.manager.TabInfo;
import cn.htht.service.platform.portal.manager.mapper.TabInfoMapper;
import cn.htht.service.platform.portal.manager.service.ServiceTabInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTabInfoServiceImpl extends AbstractBaseService<TabInfo> implements ServiceTabInfoService {

    @Autowired
    private TabInfoMapper tabInfoMapper;

    @Override
    public List<TabInfo> getAsTabInfoByTemplateId(String templateId) {
        return tabInfoMapper.getAsTabInfoByTemplateId(templateId);
    }

}
