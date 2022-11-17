package cn.htht.service.platform.portal.entity.user;

import cn.htht.service.platform.portal.common.BaseServiceEntity;
import cn.htht.service.platform.portal.entity.manager.FileData;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @ClassName UserAppointment
 * @Description 用户申请线下预约实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "user_appointment")
public class UserAppointment {

    @Id
    private String id;
    /**
     * 日期 + 序号
     */
    private String sequenceCode;

    /**
     * 申请人id
     */
    private String applyUserId;

    /**
     * 申请人名称
     */
    private String applyUsername;

    /**
     * 服务id
     */
    private String moduleId;

    /**
     * 服务名称
     */
    private String moduleName;

    /**
     * 审批状态 1、待审批 2、审核通过 3、审核不通过 4、撤销
     */
    private Integer status;

    /**
     * 审批人id
     */
    private String approveUserId;

    /**
     * 审批人名称
     */
    private String approveUsername;

    /**
     * 上传材料文件id
     */
    private String approveFileId;

    /**
     * 上传材料文件实体
     */
    @Transient
    private FileData approveFile;

    /**
     * 详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 批准时间
     */
    private Long approveTime;

    private String remark;

}
