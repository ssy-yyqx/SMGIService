package cn.htht.service.platform.portal.system.service;


import cn.htht.service.platform.portal.entity.system.SystemLoginLog;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author htht
 */
public interface ISystemLoginLogService {
    /**
     * 新增系统登录日志
     *
     * @param loginLog 访问日志对象
     */
    void insertLoginLog(SystemLoginLog loginLog);

    /**
     * 查询系统登录日志集合
     *
     * @param loginLog 访问日志对象
     * @return 登录记录集合
     */
    List<SystemLoginLog> selectLoginLogList(SystemLoginLog loginLog);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    int deleteLoginLogByIds(String[] infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLoginLog();
}
