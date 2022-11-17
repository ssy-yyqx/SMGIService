package cn.htht.service.platform.portal.system.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.SysLoginService;
import cn.htht.service.platform.portal.component.web.service.SysPermissionService;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.entity.system.LoginBody;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.system.SystemUser;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author htht
 */
@RestController
public class SystemLoginController extends BaseController {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginBody loginBody) {
        ResponseEntity ajax = ResponseEntity.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), false);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public ResponseEntity getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SystemUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getPermissionKeyByUser(user);
        ResponseEntity ajax = ResponseEntity.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

}
