package cn.htht.service.platform.portal.system.mapper;


import cn.htht.service.platform.portal.entity.system.SystemConfig;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 参数配置 数据层
 *
 * @author htht
 */
@Component
public interface SystemConfigMapper {
    /**
     * 查询参数配置信息
     *
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    SystemConfig selectConfig(SystemConfig config);

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    List<SystemConfig> selectConfigList(SystemConfig config);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    SystemConfig checkConfigKeyUnique(String configKey);

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int insertConfig(SystemConfig config);

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int updateConfig(SystemConfig config);

    /**
     * 删除参数配置
     *
     * @param configId 参数ID
     * @return 结果
     */
    int deleteConfigById(String configId);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    int deleteConfigByIds(String[] configIds);
}
