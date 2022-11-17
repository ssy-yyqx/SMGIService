package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStr;
import cn.htht.service.platform.portal.system.service.ISystemPermissionService;
import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单信息
 *
 * @author htht
 */
@RestController
@Api(tags = "权限管理")
@RequestMapping("/system/permission")
public class SystemPermissionController extends BaseController {
    @Autowired
    private ISystemPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @PutMapping("addKeys")
    @Log(module = "权限管理", businessType = BusinessType.INSERT)
    @ApiOperation("权限关键字添加")
    public ResponseEntity addKeys(@RequestParam(value = "keys") String key, @RequestParam(value = "router", required = false) String router) {
        return toAjax(permissionService.savePermissionKey(key, router));
    }

    @PutMapping("add")
    @ApiOperation("权限添加")
    @Log(module = "权限管理", businessType = BusinessType.INSERT)
    public ResponseEntity add(@RequestParam(value = "name") String name, @RequestParam(value = "keyIds") String keyIds) {
        return toAjax(permissionService.savePermission(name, keyIds));
    }
}