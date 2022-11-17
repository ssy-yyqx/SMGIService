package cn.htht.service.platform.portal.manager.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.ApplyTemplate;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.system.LoginUser;

import java.util.List;

public interface ApplyTemplateService extends BaseService<ApplyTemplate>, TemplateService {

    ApplyTemplate getByModuleId(String moduleId);

    /**
     * 更改特色服务具体栏目的内容
     * @param contentDto 包含业务导航的内容
     * @param module 业务导航栏目
     * @param loginUser 登录用户
     * @return
     */
    int updateApplyTemplate(ContentDto contentDto, Module module, LoginUser loginUser);

}
