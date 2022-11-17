package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.annotation.RepeatSubmit;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.utils.core.page.TableDataInfo;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.entity.system.SystemConfig;
import cn.htht.service.platform.portal.system.service.ISystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author htht
 */
@RestController
@RequestMapping("/system/config")
public class SystemConfigController extends BaseController {
    @Autowired
    private ISystemConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SystemConfig config) {
        startPage();
        List<SystemConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }


    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public ResponseEntity getInfo(@PathVariable String configId) {
        return ResponseEntity.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public ResponseEntity getConfigKey(@PathVariable String configKey) {
        return ResponseEntity.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(module = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public ResponseEntity add(@Validated @RequestBody SystemConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return ResponseEntity.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(module = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseEntity edit(@Validated @RequestBody SystemConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return ResponseEntity.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(module = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public ResponseEntity remove(@PathVariable String[] configIds) {
        return toAjax(configService.deleteConfigByIds(configIds));
    }

    /**
     * 清空缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(module = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clearCache")
    public ResponseEntity clearCache() {
        configService.clearCache();
        return ResponseEntity.success();
    }
}
