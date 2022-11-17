package cn.htht.service.platform.portal.business.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.dto.PortalOverviewDto;
import cn.htht.service.platform.portal.entity.business.PortalOverview;
import cn.htht.service.platform.portal.entity.manager.Module;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/11
 */
@Component
public interface PortalOverviewService extends BaseService<PortalOverview> {
    /**
     * 获取首页新闻聚焦概览列表
     * */
    PortalOverviewDto getPortalOverview();

    /**
     * 获取业务导航首页概览信息
     * */
    List<Module> getPortalService();
}
