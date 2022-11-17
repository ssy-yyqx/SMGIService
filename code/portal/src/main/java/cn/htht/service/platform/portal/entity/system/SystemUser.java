package cn.htht.service.platform.portal.entity.system;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName SystemUser
 * @Description 用户表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class SystemUser extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    private String username;

    /**
     * 真实姓名
     */
    @Size(min = 0, max = 15, message = "用户名称长度不能超过15个字符")
    private String realName;

    /**
     * 密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String password;

    /**
     * 用户状态 0：正常 1：停用
     */
    private Integer status;

    /**
     * 用户删除标记 0：正常 1：已删除
     */
    private Integer delFlag;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private Long lastLoginDate;

    /**
     * 单位
     */
    @Size(min = 0, max = 50, message = "单位长度不能超过50个字符")
    private String company;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 电话
     */
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phoneNumber;

    /**
     * 是否是超级管理员 0 不是 1 是
     */
    private Integer isAdmin;

    /**
     * 角色列表
     */
    private List<SystemRole> roleList;

    /**
     * 角色数组
     */
    private String[] roleIds;

}
