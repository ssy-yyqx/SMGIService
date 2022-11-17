package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.component.web.service.SysPermissionService;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemPermissionStr;
import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.system.service.ISystemPermissionService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.utils.core.page.TableDataInfo;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.system.service.ISystemRoleService;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 角色信息
 *
 * @author htht
 */
@RestController
@Api(tags = "角色管理")
@RequestMapping("/system/role")
public class  SystemRoleController extends BaseController {
    @Autowired
    private ISystemRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISystemPermissionService systemPermissionService;

    @Autowired
    private ISystemUserService userService;

    //@PreAuthorize("@ss.hasPermi('system:role:list')")
    @PostMapping("/list")
    @ApiOperation("角色列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "roleName", value = "角色名称", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页容量", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query")
    })
    @IsLogin
    public TableDataInfo list(@RequestBody @ApiIgnore JSONObject jsonObject) {
        PageHelper.startPage(jsonObject.getInteger("pageNumber"), jsonObject.getInteger("pageSize"));
        SystemRole systemRole = new SystemRole();
        systemRole.setRoleName(jsonObject.getString("roleName"));
        List<SystemRole> list = roleService.selectRoleList(systemRole);
        return getDataTable(list);
    }

    /**
     * 根据角色编号获取详细信息
     */
    //@PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public ResponseEntity getInfo(@PathVariable String roleId) {
        return ResponseEntity.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    //@PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(module = "角色管理", businessType = BusinessType.INSERT)
    @PutMapping("/add")
    @ApiOperation("新增角色")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "roleName", value = "角色名称", required = true, paramType = "query"),
        @ApiImplicitParam(name = "roleKey", value = "角色权限关键字", required = true, paramType = "query"),
        @ApiImplicitParam(name = "status", value = "角色状态 (0 生效 1 未生效)", required = true, paramType = "query"),
        @ApiImplicitParam(name = "describe", value = "角色描述", required = true, paramType = "query"),
        @ApiImplicitParam(name = "permStrIds", value = "所选权限的Id字符串，多个以逗号分隔", required = true, paramType = "query")
    })
    @IsLogin
    public ResponseEntity add(@Validated @ApiIgnore @RequestBody SystemRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return ResponseEntity.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return ResponseEntity.error("新增角色'" + role.getRoleName() + "'失败，角色权限关键字已存在");
        }
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改保存角色
     */
    //@PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(module = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @ApiOperation("修改角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "permStrIds", value = "所选权限的Id字符串，多个以逗号分隔", required = true, paramType = "query")
    })
    @IsLogin
    public ResponseEntity edit(@Validated @ApiIgnore @RequestBody SystemRole role) {
        SystemRole systemRole = roleService.selectRoleById(role.getId());
        if (systemRole == null) {
            return ResponseEntity.error("修改角色'" + role.getRoleName() + "'失败，角色不存在");
        }
        roleService.checkRoleAllowed(systemRole);
        if (roleService.updateRole(role) > 0) {
            // 更新缓存用户权限
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            if (StringUtils.isNotNull(loginUser.getUser()) && loginUser.getUser().getIsAdmin() == 0) {
                loginUser.setPermissions(permissionService.getPermissionKeyByUser(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUsername()));
                tokenService.setLoginUser(loginUser);
            }
            return ResponseEntity.success("修改成功");
        }
        return ResponseEntity.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 获取所有权限名称
     * @return
     */
    @GetMapping("/permission")
    @ApiOperation("获取所有权限列表")
    @IsLogin
    public ResponseEntity permissionList() {
        List<SystemPermissionStr> systemPermissionList = systemPermissionService.selectAllPermission();
        return ResponseEntity.success(systemPermissionList);
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @Log(module = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleId}")
    public ResponseEntity delete(@PathVariable(name = "roleId") String roleId){
        return toAjax(roleService.deleteRoleById(roleId));
    }
}
