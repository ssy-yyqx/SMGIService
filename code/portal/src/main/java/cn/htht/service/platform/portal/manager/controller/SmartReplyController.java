package cn.htht.service.platform.portal.manager.controller;

import cn.htht.service.platform.portal.common.BaseController;
import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.entity.manager.SmartReply;
import cn.htht.service.platform.portal.manager.service.SmartReplyService;
import cn.htht.service.platform.portal.utils.annotation.IsLogin;
import cn.htht.service.platform.portal.utils.annotation.Log;
import cn.htht.service.platform.portal.utils.enums.BusinessType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.bcel.generic.NEW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
@RestController
@Api(tags = "智能回复")
@RequestMapping("/smart")
public class SmartReplyController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SmartReplyController.class);
    @Autowired
    SmartReplyService smartReplyService;

    @ApiOperation("新增")
    @PostMapping("/insert")
    @Log(module = "智能回复", businessType = BusinessType.INSERT)
    @IsLogin
    public ResponseEntity insertSmartReply(@RequestBody SmartReply smartReply){
        String question = smartReply.getQuestion();
        String reply = smartReply.getReply();
        if (question == null || "".equals(question.replace(" ", ""))){
            return ResponseEntity.error("问题不能为空");
        }
        if (reply == null || "".equals(reply.replace(" ", ""))){
            return ResponseEntity.error("解决方案不能为空");
        }
        return ResponseEntity.success(smartReplyService.insertSmartReply(smartReply));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    @Log(module = "智能回复", businessType = BusinessType.DELETE)
    @IsLogin
    public ResponseEntity deleteSmartReply(@PathVariable String id){
        return ResponseEntity.success(smartReplyService.deleteSmartReply(id));
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    @Log(module = "智能回复", businessType = BusinessType.UPDATE)
    @IsLogin
    public ResponseEntity updateSmartReply(@RequestBody SmartReply smartReply){
        String question = smartReply.getQuestion();
        String reply = smartReply.getReply();
        if (question == null || "".equals(question.replace(" ", ""))){
            return ResponseEntity.error("问题不能为空");
        }
        if (reply == null || "".equals(reply.replace(" ", ""))){
            return ResponseEntity.error("解决方案不能为空");
        }
        return ResponseEntity.success(smartReplyService.updateSmartReply(smartReply));
    }


    @ApiOperation("查看/搜索 列表")
    @GetMapping("/list")
    public ResponseEntity selectSmartReplyListByKeyword(@RequestParam(value = "pageSize") @ApiParam(name = "pageSize", value = "每页多少条") Integer pageSize, @RequestParam(value = "pageNumber") @ApiParam(name = "pageNumber", value = "页码") Integer pageNumber, @RequestParam(value = "keyword", required = false) @ApiParam(name = "keyword", value = "关键词") String keyword){
        PageInfo<SmartReply> smartReplyList = smartReplyService.selectSmartReplyListByKeyword(pageSize, pageNumber, keyword);
        return ResponseEntity.success(smartReplyList);
    }
}
