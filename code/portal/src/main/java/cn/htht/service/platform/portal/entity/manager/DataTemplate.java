package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Transient;
import java.util.List;

/**
 * @ClassName DataTemplate
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class DataTemplate extends Template {

    public DataTemplate() {
        super();
    }

    public DataTemplate(Template template) {
        super(template);
    }

    /**
     * 数据标题
     */
    private String title;

    /**
     * 数据介绍
     */
    private String content;

    /**
     * 数据展示图片url
     */
    private String imageUrl;

    /**
     * 办理指南文件id
     */
    private String guideFileId;

    /**
     * 办理指南文件实体
     */
    @Transient
    private FileData guideFileData;

    /**
     * 办理类型 1、在线办理 2、预约线下
     */
    private Integer handleType;

    /**
     * 跳转栏目id
     */
    private String redirectModuleId;

    /**
     * 跳转特色服务页面
     */
    private String redirectUrl;


    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 包含的数据表(数据模板)
     */
    @Transient
    private List<ServiceData> dataTable;

}
