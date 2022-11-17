package cn.htht.service.platform.portal.entity.user;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

/**
 * @ClassName DocEntity
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Document(indexName = "titlecontent", type = "_doc")
@Data
@ToString
public class DocEntity {
    @Id
    private String id;
    //标题
    private String title;
    //栏目类型
    private String columnType;

    private String contentId;

    private String moduleId;

    private String router;

    private String moduleName;

    private String templeType;

    private long creatTime;

    private String creatBy;

    private String sysDate;

    private String firstRateId;

    private String serviceTemplateType;

    public DocEntity() {
    }

    public DocEntity(String id, String title, String columnType, String contentId,
                     String moduleId, String router, String moduleName, String templeType,
                     long creatTime, String creatBy, String sysDate, String firstRateId, String serviceTempaleType) {
        this.id = id;
        this.title = title;
        this.columnType = columnType;
        this.contentId = contentId;
        this.moduleId = moduleId;
        this.router = router;
        this.moduleName = moduleName;
        this.templeType = templeType;
        this.creatTime = creatTime;
        this.creatBy = creatBy;
        this.sysDate = sysDate;
        this.firstRateId = firstRateId;
        this.serviceTemplateType = serviceTemplateType;
    }

    public DocEntity(String id, String title, String columnType, String sysDate) {
        this.id = id;
        this.title = title;
        this.columnType = columnType;
        this.sysDate = sysDate;

    }
}

