package cn.htht.service.platform.portal.entity.manager;

import cn.htht.service.platform.portal.common.BaseServiceEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import java.util.List;

/**
 * @ClassName Module
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_module")
public class Module extends BaseServiceEntity {

    /**
     * 栏目名称
     */
    private String moduleName;

    /**
     * 上级栏目
     */
    private String parentModuleId;

    /**
     * 上级栏目名称
     */
    private String parentModuleName;

    /**
     * 模板类型：1、首页 2、单页面 3、信息页面 4、业务页面 5、数据页面 6、特色服务
     */
    private Integer templateType;

    /**
     * 栏目节点类型：1、分类节点 2、最终节点
     */
    private Integer moduleType;

    /**
     * 图标标识
     */
    private String icon;

    /**
     * 是否显示 1、显示 2、隐藏
     */
    private Integer isShow;

    /**
     * 路由标识
     */
    private String router;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否是外部链接 1 是 2 不是
     */
    private Integer isLink;

    /**
     * 链接url
     */
    private String linkUrl;

    /**
     * 路由层级
     */
    private String routerStr;

    /**
     * 内容模板id
     */
    private String templateId;

    /**
     * 页面使用的模板
     */
    private Template template;

    /**
     * 子栏目列表
     */
    private List<Module> childrenList;

}
