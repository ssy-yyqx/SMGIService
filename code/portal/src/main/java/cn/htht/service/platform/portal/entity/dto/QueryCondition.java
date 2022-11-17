package cn.htht.service.platform.portal.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName QueryCondition
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@ApiModel
@Data
@ToString
public class QueryCondition {

    @ApiModelProperty(name = "pageSize")
    private int pageSize;

    @ApiModelProperty(name = "pageNumber")
    private int pageNumber;
}