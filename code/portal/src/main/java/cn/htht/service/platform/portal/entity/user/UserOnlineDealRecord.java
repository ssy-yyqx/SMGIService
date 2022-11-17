package cn.htht.service.platform.portal.entity.user;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName UserOnlineDealRecord
 * @Description 用户在线办理记录
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class UserOnlineDealRecord {

    /**
     * 主键id
     */
    private String id;

    /**
     * 日期 + 序号
     */
    private String sequenceCode;

    /**
     * 办理用户id
     */
    private String userId;

    /**
     * 办理业务名称
     */
    private String businessName;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 结果文件id
     */
    private String resultFileId;

    /**
     * 完成结果状态 0 完成 1 未完成
     */
    private Integer status;

}
