package cn.htht.service.platform.portal.business.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.business.PortalOverview;
import cn.htht.service.platform.portal.entity.manager.Module;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/11
 */
@Mapper
public interface PortalOverviewMapper extends BasicMapper<PortalOverview> {
    /**
     * 获取首页新闻聚焦概览列表
     * */
    List<PortalOverview> selectPortalNewsList();
    /**
     * 获取首页通知公告概览列表
     * */
    List<PortalOverview> selectPortalNoticeList();
    /**
     * 获取首页中心概况概览列表
     * */
    List<PortalOverview> selectPortalCenterList();
    /**
     * 获取首页中心介绍
     * */
    String selectPortalCenterOverview();
    /**
     * 获取业务导航首页概览信息
     * */
    List<Module> selectPortalServiceList();
}
