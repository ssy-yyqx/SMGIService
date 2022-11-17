package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @ClassName ServiceTemplate
 * @Description 业务页面模板内容
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_service_template")
public class ServiceTemplate extends Template {

    public ServiceTemplate() {
        super();
    }

    public ServiceTemplate(Template template) {
        super(template);
    }

    /**
     * 服务展示图片url
     */
    private String imageUrl;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 办理指南文件url
     */
    private String guideFileId;

    /**
     * 办理指南文件实体
     */
    @Transient
    private FileData guideFileData;

    /**
     * 办理类型 1、在线办理 2、预约线下 3、无办理类型
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
     * 包含的页签
     */
    private List<TabContent> tabList;

}
