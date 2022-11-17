package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName ServiceInfo
 * @Description 业务信息页签
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class TabContent {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 业务模板id
     */
    private String serviceTemplateId;

    /**
     * 页签类型：1、普通页签 2、列表页签
     */
    private Integer tabType;

    /**
     * 内容类型： 1、服务简介 2、接口说明 3、常见问题 4、使用案例
     */
    private Integer contentType;


    /**
     * 页签排序
     */
    private Integer sort;
}
