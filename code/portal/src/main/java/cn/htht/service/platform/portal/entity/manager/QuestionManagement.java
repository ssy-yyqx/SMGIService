package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Map;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/14
 */
@Data
@ToString
@Table(name = "t_questions")
public class QuestionManagement {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 检索关键字
     */
    private String searchKey;

    /**
     * 提问时间
     */
    private Long questionTime;
    /**
     * 提问人
     */
    private String questionBy;
    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 答复时间
     */
    private Long replyTime;
    /**
     * 答复人
     */
    private String replyBy;
    /**
     * 答复内容
     */
    private String replyContent;
    /**
     * 答复更新时间
     * */
    private String replyUpdateTime;
    /**
     * 答复更新人
     * */
    private String replyUpdateBy;
    /**
     * 服务评价
     * */
    private Integer serviceRating;
    /**
     * 是否已有答复 0：否  1：是
     * */
    private Integer isReplied = 0;
    /**
     * 是否已评价 0：否  1：是
     * */
    private Integer isRated = 0;
    /**
     * 提问人ID
     * */
    private String questionerId;
    /**
     * 用户是否已读0：否  1：是
     * */
    private Integer isRead = 0;
    /**
     * 请求参数
     */
    private Map<String, Object> params;

    private String remark;
}
