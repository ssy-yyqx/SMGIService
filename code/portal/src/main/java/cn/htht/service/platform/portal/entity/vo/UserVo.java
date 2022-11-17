package cn.htht.service.platform.portal.entity.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName UserVo
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class UserVo {

    private String id;

    private String username;

    private String realName;

    private String roleId;

    private String roleName;

    private String phoneNumber;

    private String company;

    private String email;
}
