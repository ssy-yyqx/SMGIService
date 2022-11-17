package cn.htht.service.platform.portal.entity.manager;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import java.util.List;

/**
 * @ClassName InfoTemplate
 * @Description 信息页面模板内容
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_info_template")
public class InfoTemplate extends Template {

    public InfoTemplate() {
        super();
    }

    public InfoTemplate(Template template) {
        super(template);
    }

    /**
     * 内容列表
     */
    private List<Content> contentList;

}
