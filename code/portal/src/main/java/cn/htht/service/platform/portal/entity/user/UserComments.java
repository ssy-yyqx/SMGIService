package cn.htht.service.platform.portal.entity.user;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/22
 */
@Data
@ToString
public class UserComments {
    @Id
    private String id;

    private String moduleId;

    private String userId;

    private String content;

    private String createBy;

    private Long createTime;

    private String remark;
}
