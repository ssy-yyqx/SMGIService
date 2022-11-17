package cn.htht.service.platform.portal.entity.vo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

@Data
@ToString
public class ServiceDataVo {


    @Id
    private String id;

    private String title;

    private String router;

    private String templateId;

    private String createTime;

    private String createBy;

    private String updateTime;

    private String updateBy;

    private String remark;

    private String type;

    private String moduleId;

    private String icon;

}
