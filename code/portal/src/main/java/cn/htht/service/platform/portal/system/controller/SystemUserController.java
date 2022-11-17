package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.component.web.domain.server.Sys;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.UserConstants;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemRole;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.entity.vo.UserVo;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.utils.core.page.TableDataInfo;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.DateUtils;
import cn.htht.service.platform.portal.utils.utils.SecurityUtils;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.system.service.ISystemRoleService;
import cn.htht.service.platform.portal.system.service.ISystemUserService;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.apache.catalina.User;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

/**
 * 用户信息
 *
 * @author htht
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SystemUserController extends BaseController {
    @Autowired
    private ISystemUserService userService;

    @Autowired
    private ISystemRoleService roleService;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取用户列表
     */
    //@PreAuthorize("@ss.hasPermi('system:user:list')")
    @ApiOperation("用户列表")
    @PostMapping("/list")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页容量", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query")
    })
    @IsLogin
    public TableDataInfo list(@RequestBody @ApiIgnore JSONObject jsonObject) {
        PageHelper.startPage(jsonObject.getInteger("pageNumber"), jsonObject.getInteger("pageSize"));
        List<UserVo> list = userService.selectUserListByUsername(jsonObject.getString("username"));
        return getDataTable(list);
    }


    /**
     * 修改用户
     */
    // @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @ApiOperation("编辑用户")
    @Log(module = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "角色id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "phoneNumber", value = "电话", required = true, paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(name = "company", value = "单位", required = true, paramType = "query")
    })
    @IsLogin
    public ResponseEntity edit(@Validated @RequestBody JSONObject jsonObject) {
        SystemUser user = userService.selectUserById(jsonObject.getString("userId"));
        SystemRole role = roleService.selectRoleById(jsonObject.getString("roleId"));
        if (user == null) {
            return ResponseEntity.error("用户不存在");
        }
        if (role == null) {
            return ResponseEntity.error("角色不存在");
        }
        if (user.getIsAdmin() == 1) {
            return ResponseEntity.error("超级管理员不能修改");
        }
        user.setRealName(jsonObject.getString("realName"));
        user.setCompany(jsonObject.getString("company"));
        user.setEmail(jsonObject.getString("email"));
        user.setPhoneNumber(jsonObject.getString("phoneNumber"));
        user.setRoleIds(new String[] {role.getId()});
        userService.updateUser(user);
        return ResponseEntity.success("修改成功");
    }

    /**
     * 删除用户
     */
    // @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @ApiOperation("删除用户")
    @Log(module = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    @IsLogin
    public ResponseEntity remove(@PathVariable String[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    // @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @ApiOperation("重置密码")
    @Log(module = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "query")
    })
    @IsLogin
    public ResponseEntity resetPwd(@RequestBody @ApiIgnore SystemUser user) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (!loginUser.getUser().getIsAdmin().equals(UserConstants.IS_YES)) {
            return ResponseEntity.error("你不是管理员，无权重置他人密码");
        }
        user = userService.selectUserById(user.getId());
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(UserConstants.INIT_PASSWORD));
        return toAjax(userService.resetPwd(user));
    }

    @GetMapping("/role")
    @ApiOperation("获取角色列表")
    @IsLogin
    public ResponseEntity roleList() {
        List<SystemRole> systemRoles = roleService.selectRoleAll();
        return ResponseEntity.success(systemRoles);
    }
}
