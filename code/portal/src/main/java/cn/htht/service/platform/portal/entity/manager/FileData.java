package cn.htht.service.platform.portal.entity.manager;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName NewsPicture
 * @Description 附属文件（包含图片、文档等相关数据）实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
@Table(name = "t_file_data")
public class FileData {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 文件类型 1、图片 2、地图数据 3、文档信息 4、程序文件
     *
     */
    private Integer fileType;

    /**
     * 文件的MD5
     */
    private String md5;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 存储对象名称
     */
    private String objectName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件展示标题
     */
    private String fileTitle;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 上传人
     */
    private String createBy;
}
