package cn.htht.service.platform.portal.manager.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.entity.manager.QuestionManagement;
import cn.htht.service.platform.portal.manager.service.QuestionManagementService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * @date 2021/10/14
 */
@RestController
@Api(tags = "问题管理")
@RequestMapping("/questions")
public class QuestionManagementController extends BaseController {
    @Autowired
    QuestionManagementService questionManagementService;

    @Autowired
    TokenService tokenService;

    @ApiOperation("用户提问")
    @PostMapping("/question")
    @Log(module = "问题管理", businessType = BusinessType.INSERT)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity questionCommit(HttpServletRequest request, @RequestBody QuestionManagement questionManagement){
        String questionContent = questionManagement.getQuestionContent();
        if (questionContent == null || "".equals(questionContent.replace(" ", ""))){
            return ResponseEntity.error("问题内容不能为空");
        }
        questionManagement.setQuestionerId(tokenService.getLoginUser(request).getUserId());
        questionManagement.setQuestionBy(tokenService.getLoginUser(request).getUsername());
        return ResponseEntity.success(questionManagementService.questionCommit(questionManagement));
    }

    @ApiOperation("用户确认已读")
    @PutMapping("/read/{id}")
    @Log(module = "问题管理", businessType = BusinessType.UPDATE)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity readQuestion(@PathVariable String id){
        if (questionManagementService.selectQuestionById(id).getIsReplied() == null || !questionManagementService.selectQuestionById(id).getIsReplied().equals(1)){
            return ResponseEntity.error("操作错误，管理员尚未回复");
        }
        return ResponseEntity.success(questionManagementService.readQuestion(id));
    }

    @ApiOperation("用户评分")
    @PutMapping("/rate")
    @Log(module = "问题管理", businessType = BusinessType.UPDATE)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity rateQuestion(@RequestParam(value = "id") @ApiParam(name = "id", value = "问题ID") String id, @RequestParam(value = "serviceRating") @ApiParam(name = "serviceRating", value = "评价") Integer serviceRating){
        return ResponseEntity.success(questionManagementService.rateQuestion(id, serviceRating));
    }

    @ApiOperation("管理员答复")
    @PutMapping("/reply/reply")
    @Log(module = "问题管理", businessType = BusinessType.UPDATE)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "问题ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "replyContent", value = "答复内容", required = true, paramType = "query")
    })
    public ResponseEntity replyQuestion(HttpServletRequest request, @ApiIgnore @RequestBody JSONObject jsonObject){
        String replyContent = jsonObject.getString("replyContent");
        if (replyContent == null || "".equals(replyContent.replace(" ", ""))){
            return ResponseEntity.error("答复内容不能为空");
        }
        return ResponseEntity.success(questionManagementService.replyQuestion(jsonObject.getString("id"), tokenService.getLoginUser(request).getUsername(), replyContent));
    }

//    @ApiOperation("管理员更新答复")
//    @PutMapping("/reply/update")
//    // 添加权限验证
//    // @PreAuthorize("@ss.hasPermi('')")
//    @IsLogin
//    public ResponseEntity updateReplyQuestion(@RequestParam(value = "id") @ApiParam(name = "id", value = "问题ID") String id, @RequestParam(value = "replyUpdateBy") @ApiParam(name = "replyUpdateBy", value = "答复更新人") String replyUpdateBy, @RequestParam(value = "replyContent") @ApiParam(name = "replyContent", value = "答复更新内容") String replyContent){
//        if (questionManagementService.selectQuestionById(id).getIsRead().equals(1)){
//            return ResponseEntity.error("用户已确认，无法编辑");
//        }
//        if (replyContent == null || "".equals(replyContent.replace(" ", ""))){
//            return ResponseEntity.error("答复内容不能为空");
//        }
//        return ResponseEntity.success(questionManagementService.updateReplyQuestion(id, replyUpdateBy, replyContent));
//    }

    @ApiOperation("管理员答复撤回")
    @PutMapping("/reply/withdraw/{id}")
    @Log(module = "问题管理", businessType = BusinessType.UPDATE)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity withdrawReply(@PathVariable String id){
        if (questionManagementService.selectQuestionById(id).getIsRead() != null && questionManagementService.selectQuestionById(id).getIsRead().equals(1)){
            return ResponseEntity.error("用户已确认，无法撤回");
        }
        return ResponseEntity.success(questionManagementService.withdrawReply(id));
    }

    @ApiOperation("管理员查看问题列表")
    @GetMapping("/list/admin")
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity getAdminQuestionManagementList(@RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber){
        PageInfo<QuestionManagement> questionManagementList = questionManagementService.getAdminQuestionManagementList(pageSize, pageNumber);
        return ResponseEntity.success(questionManagementList);
    }

    @ApiOperation("用户查看问题列表")
    @GetMapping("/list/user")
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity getUserQuestionManagementList(HttpServletRequest request, @RequestParam(value = "keyword", required = false) @ApiParam(name = "keyword", value = "关键字") String keyword, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber){
        PageInfo<QuestionManagement> questionManagementList = questionManagementService.getUserQuestionManagementList(keyword, tokenService.getLoginUser(request).getUserId(), pageSize, pageNumber);
        return ResponseEntity.success(questionManagementList);
    }

    @ApiOperation("获取用户未读数量")
    @GetMapping("/unread")
    @IsLogin
    public ResponseEntity getUserUnreadNumber(HttpServletRequest request){
        return ResponseEntity.success(questionManagementService.getUserUnreadNumber(tokenService.getLoginUser(request).getUserId()));
    }


    @ApiOperation("搜索问题内容")
    @GetMapping("/search")
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity getQuestionManagementListByKeyword(HttpServletRequest request, @RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber, @RequestParam(value = "keyword") @ApiParam(name = "keyword", value = "关键词") String keyword){
        PageInfo<QuestionManagement> questionManagementList = questionManagementService.getQuestionManagementListByKeyword(pageSize, pageNumber, tokenService.getLoginUser(request).getUserId(), keyword);
        return ResponseEntity.success(questionManagementList);
    }

    @ApiOperation("删除问题")
    @DeleteMapping("/delete/{id}")
    @Log(module = "问题管理", businessType = BusinessType.DELETE)
    // 添加权限验证
    // @PreAuthorize("@ss.hasPermi('')")
    @IsLogin
    public ResponseEntity deleteQuestionById(@PathVariable String id){
        return ResponseEntity.success(questionManagementService.deleteQuestionById(id));
    }

}
