package cn.htht.service.platform.portal.system.service.impl;

import cn.htht.service.platform.portal.entity.system.SystemLoginLog;
import cn.htht.service.platform.portal.system.mapper.SystemLoginLogMapper;
import cn.htht.service.platform.portal.system.service.ISystemLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author htht
 */
@Service
public class SystemLoginLogServiceImpl implements ISystemLoginLogService {

    @Autowired
    private SystemLoginLogMapper loginLogMapper;

    /**
     * 新增系统登录日志
     *
     * @param loginLog 访问日志对象
     */
    @Override
    public void insertLoginLog(SystemLoginLog loginLog) {
        loginLogMapper.insertLoginLog(loginLog);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param loginLog 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SystemLoginLog> selectLoginLogList(SystemLoginLog loginLog) {
        return loginLogMapper.selectLoginLogList(loginLog);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @Override
    public int deleteLoginLogByIds(String[] infoIds) {
        return loginLogMapper.deleteLoginLogByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginLog() {
        loginLogMapper.cleanLoginLog();
    }
}
