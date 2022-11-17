package cn.htht.service.platform.portal.system.mapper;


import cn.htht.service.platform.portal.entity.system.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作日志 数据层
 *
 * @author htht
 */
@Mapper
@Component
public interface SystemLogMapper {
    /**
     * 新增操作日志
     *
     * @param systemLog 操作日志对象
     */
    int insertSystemLog(SystemLog systemLog);

    /**
     * 查询系统操作日志集合
     *
     * @return 操作日志集合
     */
    List<SystemLog> selectSystemLogList(@Param("keyword")String keyword);

    /**
     * 批量删除系统操作日志
     *
     * @param logIds 需要删除的操作日志ID
     * @return 结果
     */
    int deleteSystemLogByIds(String[] logIds);


    /**
     * 查询操作日志详细
     *
     * @param logId 操作ID
     * @return 操作日志对象
     */
    SystemLog selectSystemLogById(String logId);

    /**
     * 清空操作日志
     */
    void cleanSystemLog();
}
