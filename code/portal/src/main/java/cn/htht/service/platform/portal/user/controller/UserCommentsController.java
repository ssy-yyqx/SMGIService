package cn.htht.service.platform.portal.user.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.user.UserComments;
import cn.htht.service.platform.portal.user.service.UserCommentsService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
@RestController
@Api(tags = "用户评论")
@RequestMapping("/user/comments")
public class UserCommentsController extends BaseController {
    @Autowired
    UserCommentsService userCommentsService;

    @Autowired
    TokenService tokenService;

    @ApiOperation("添加评论")
    @PostMapping("/add")
    @Log(module = "用户评论", businessType = BusinessType.INSERT)
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "评论内容", required = true, paramType = "query")
    })
    public ResponseEntity addComments(HttpServletRequest request, @RequestBody @ApiIgnore UserComments userComments){
        userComments.setUserId(tokenService.getLoginUser(request).getUserId());
        userComments.setCreateBy(tokenService.getLoginUser(request).getUsername());
        if (StringUtils.isNull(userComments.getContent()) && StringUtils.isEmpty(userComments.getContent())){
            return ResponseEntity.error("评论内容不能为空");
        }
        return ResponseEntity.success(userCommentsService.insertComments(userComments));
    }

    @ApiOperation("查看评论")
    @GetMapping("/list")
    public ResponseEntity getComments(@RequestParam(value = "moduleId") @ApiParam(name = "moduleId", value = "模块Id") String moduleId, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber){
        PageInfo<UserComments> userCommentsList = userCommentsService.getComments(moduleId, pageSize, pageNumber);
        return ResponseEntity.success(userCommentsList);
    }

}
