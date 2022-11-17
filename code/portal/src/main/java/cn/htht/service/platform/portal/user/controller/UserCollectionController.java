package cn.htht.service.platform.portal.user.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.system.LoginUser;
import cn.htht.service.platform.portal.entity.user.UserCollection;
import cn.htht.service.platform.portal.entity.user.UserSupport;
import cn.htht.service.platform.portal.user.service.UserCollectionService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
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
 * @ClassName UserCollectionController
 * @Description 用户收藏controller
 * @Author Ken
 * @Date DATE{TIME}
 **/
@RestController
@Api(tags = "用户收藏")
@RequestMapping("/user/collect")
public class UserCollectionController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserCollectionService userCollectionService;

    @IsLogin
    @ApiOperation("用户收藏")
    @Log(module = "用户收藏", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "收藏内容标题", required = true, paramType = "query"),
            @ApiImplicitParam(name = "routerURI", value = "路由URI", required = true, paramType = "query"),
    })
    public ResponseEntity collect(@RequestBody @ApiIgnore UserCollection userCollection) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后收藏");
        } else {
            userCollection.setUserId(loginUser.getUserId());
            List<UserCollection> list = userCollectionService.getListByUser(userCollection);
            if (list == null || list.isEmpty()){
                userCollectionService.collect(userCollection);
            }
            return ResponseEntity.success();
        }
    }

    @IsLogin
    @Log(module = "用户收藏", businessType = BusinessType.DELETE)
    @ApiOperation("取消收藏")
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity cancelCollect(@PathVariable String id) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后操作");
        } else {
            userCollectionService.cancel(id);
            return ResponseEntity.success();
        }
    }

    @IsLogin
    @ApiOperation("取消收藏")
    @Log(module = "用户收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/cancel/user/{router}")
    public ResponseEntity cancelUserCollect(@PathVariable String router) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (loginUser == null) {
            return ResponseEntity.error("用户未登录，请登录后收藏");
        } else {
            userCollectionService.userCancel(loginUser.getUserId(), router);
            return ResponseEntity.success();
        }
    }


}
