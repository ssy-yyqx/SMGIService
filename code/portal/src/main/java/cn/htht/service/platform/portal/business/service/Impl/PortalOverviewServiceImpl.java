package cn.htht.service.platform.portal.business.service.Impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.dto.PortalOverviewDto;
import cn.htht.service.platform.portal.entity.business.PortalOverview;
import cn.htht.service.platform.portal.business.mapper.PortalOverviewMapper;
import cn.htht.service.platform.portal.business.service.PortalOverviewService;
import cn.htht.service.platform.portal.entity.manager.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/11
 */
@Service
public class PortalOverviewServiceImpl extends AbstractBaseService<PortalOverview> implements PortalOverviewService {
    @Autowired
    private PortalOverviewMapper portalOverviewMapper;
    /**
     * 获取首页新闻聚焦概览列表
     * */
    @Override
    public PortalOverviewDto getPortalOverview() {
        return new PortalOverviewDto(portalOverviewMapper.selectPortalCenterOverview(),
                portalOverviewMapper.selectPortalNewsList(),
                portalOverviewMapper.selectPortalNoticeList(),
                portalOverviewMapper.selectPortalCenterList());
    }

    /**
     * 获取业务导航首页概览信息
     */
    @Override
    public List<Module> getPortalService() {
        return portalOverviewMapper.selectPortalServiceList();
    }

}
