package cn.htht.service.platform.portal.entity.manager;

import cn.htht.service.platform.portal.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/19
 */
@Data
@ToString
//@AllArgsConstructor
@Table(name = "t_smart_reply")
public class SmartReply{
    /**
     * 主键ID
     * */
    @Id
    private String id;

    /**
     * 检索关键字
     * */
    private String searchKey;

    /**
     * 问题
     * */
    private String question;

    /**
     * 回复内容
     * */
    private String reply;

    /**
     * 创建时间
     * */
    private Long createTime;

    /**
     * 创建人
     * */
    private String createBy;

    /**
     * 修改时间
     * */
    private Long updateTime;

    /**
     * 修改人
     * */
    private String updateBy;

    private String remark;

}
