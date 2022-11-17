package cn.htht.service.platform.portal.system.service;

import cn.htht.service.platform.portal.entity.system.SystemLog;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 操作日志 服务层
 *
 * @author htht
 */
public interface ISystemLogService {
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    int insertSystemLog(SystemLog operLog);

    /**
     * 查询系统操作日志集合
     *
     * @return 操作日志集合
     */
    PageInfo<SystemLog> selectSystemLogList(Integer pageSize, Integer pageNumber, String keyword);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteSystemLogByIds(String[] operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    SystemLog selectSystemLogById(String operId);

    /**
     * 清空操作日志
     */
    void cleanSystemLog();
}
