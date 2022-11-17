package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @ClassName SingleTemplate
 * @Description 单页面模板内容
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_single_template")
public class SingleTemplate extends Template {

    public SingleTemplate() {
        super();
    }

    public SingleTemplate(Template template) {
        super(template);
    }

    /**
     * 单一内容
     */
    @Transient
    private Content content;


}
