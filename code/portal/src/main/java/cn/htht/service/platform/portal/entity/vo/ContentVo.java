package cn.htht.service.platform.portal.entity.vo;

import cn.htht.service.platform.portal.entity.manager.FileData;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @ClassName ContentVo
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class ContentVo {

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
     * 内容标题
     */
    private String title;

    /**
     * 内容名称
     */
    private String content;

    /**
     * 页面缩略图
     */
    private String imageUrl;

    /**
     * 页面附件id
     */
    private String dataId;

    /**
     * 页面附件
     */
    @Transient
    private FileData fileData;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    private String remark;

    /**
     * 修改人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

}
