package cn.htht.service.platform.portal.user.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.domain.server.Sys;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.user.UserCollection;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import cn.htht.service.platform.portal.user.service.UserSupportService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @ClassName UserSupport
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "用户点赞")
@RequestMapping("/user/support")
public class UserSupportController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserSupportService userSupportService;

    @IsLogin
    @ApiOperation("用户点赞")
    @PostMapping
    @ApiImplicitParams({
        @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
        @ApiImplicitParam(name = "name", value = "点赞内容标题", required = true, paramType = "query"),
        @ApiImplicitParam(name = "routerURI", value = "路由URI", required = true, paramType = "query"),
    })
    public ResponseEntity support(@RequestBody @ApiIgnore UserSupport userSupport) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后点赞");
        } else {
            userSupport.setUserId(loginUser.getUserId());
            List<UserSupport> list = userSupportService.getListByUser(userSupport);
            if (list == null || list.isEmpty()){
                userSupportService.support(userSupport);
            }
            return ResponseEntity.success();
        }
    }

    @IsLogin
    @ApiOperation("用户个人中心取消点赞")
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity cancelSupport(@PathVariable String id) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后操作");
        } else {
            userSupportService.cancel(id);
            return ResponseEntity.success();
        }
    }

    @IsLogin
    @ApiOperation("用户页面取消点赞")
    @DeleteMapping("/cancel/user/{router}")
    public ResponseEntity cancelUserSupport(@PathVariable String router) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后点赞");
        } else {
            userSupportService.userCancel(loginUser.getUserId(), router);
            return ResponseEntity.success();
        }
    }

}
