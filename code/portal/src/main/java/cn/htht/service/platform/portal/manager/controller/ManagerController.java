package cn.htht.service.platform.portal.manager.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.SysLoginService;
import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.entity.system.LoginBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName ManagerController
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Api(tags = "后台管理")
@RestController
@RequestMapping("/manager")
public class ManagerController extends BaseController {

    @Autowired
    private SysLoginService loginService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public ResponseEntity login(@RequestBody LoginBody loginBody) {
        ResponseEntity responseEntity = ResponseEntity.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), true);
        responseEntity.put(Constants.TOKEN, token);
        return responseEntity;
    }
}
