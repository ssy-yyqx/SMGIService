package cn.htht.service.platform.portal.system.service.impl;

import cn.htht.service.platform.portal.entity.system.SystemLog;
import cn.htht.service.platform.portal.system.mapper.SystemLogMapper;
import cn.htht.service.platform.portal.system.service.ISystemLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志 服务层处理
 *
 * @author htht
 */
@Service
public class SystemLogServiceImpl implements ISystemLogService {
    @Autowired
    private SystemLogMapper operLogMapper;

    /**
     * 新增操作日志
     *
     * @param systemLog 操作日志对象
     */
    @Override
    public int insertSystemLog(SystemLog systemLog) {
        return operLogMapper.insertSystemLog(systemLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @return 操作日志集合
     */
    @Override
    public PageInfo<SystemLog> selectSystemLogList(Integer pageSize, Integer pageNumber, String keyword) {
        PageHelper.startPage(pageNumber, pageSize);
        if (!(keyword == null || "".equals(keyword))){
            keyword = '%' + keyword + '%';
        }
        List<SystemLog> systemLogList = operLogMapper.selectSystemLogList(keyword);
        return new PageInfo<>(systemLogList);
    }

    /**
     * 批量删除系统操作日志
     *
     * @param logIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteSystemLogByIds(String[] logIds) {
        return operLogMapper.deleteSystemLogByIds(logIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param logId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SystemLog selectSystemLogById(String logId) {
        return operLogMapper.selectSystemLogById(logId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanSystemLog() {
        operLogMapper.cleanSystemLog();
    }
}
