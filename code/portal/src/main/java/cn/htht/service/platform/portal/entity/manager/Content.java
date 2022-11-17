package cn.htht.service.platform.portal.entity.manager;

import cn.htht.service.platform.portal.common.BaseServiceEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @ClassName Content
 * @Description 内容
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_content")
public class Content extends BaseServiceEntity {

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

}
