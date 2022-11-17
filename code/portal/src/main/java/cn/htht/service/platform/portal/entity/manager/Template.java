package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

import javax.persistence.Id;
import java.util.List;

/**
 * @ClassName Template
 * @Description 模板基本信息表
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class Template {

    public Template() {}

    public Template(Template t) {
        this.id = t.id;
        this.moduleId = t.moduleId;
        this.searchKey = t.searchKey;
    }

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 信息页面id
     */
    private String moduleId;

    /**
     * 查询关键字
     */
    private String searchKey;

}
