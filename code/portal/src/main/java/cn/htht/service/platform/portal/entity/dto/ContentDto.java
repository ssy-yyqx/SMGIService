package cn.htht.service.platform.portal.entity.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName TemplateDto
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Data
@ToString
public class ContentDto {

    private String moduleId;

    private String contentId;

    private String title;

    private String content;

    private String searchKey;

    private String imageUrl;

    private String dataId;

    /**
     * 办理类型 1、在线办理 2、预约线下
     */
    private Integer handleType;

    /**
     * 是否需要申请预约 0 不是 1 是
     */
    private Integer isAppointment;

    /**
     * 跳转栏目id
     */
    private String redirectModuleId;

    /**
     * 跳转特色服务页面
     */
    private String redirectUrl;

    /**
     * 办理指南文件url
     */
    private String guideFileId;

    /**
     * 页签内容
     */
    private JSONArray tabContent;

}
