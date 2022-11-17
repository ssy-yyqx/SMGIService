package cn.htht.service.platform.portal.entity.dto;

import cn.htht.service.platform.portal.entity.manager.QuestionManagement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/18
 */
@Data
@ToString
@AllArgsConstructor
public class QuestionListDto {
    private Integer total;

    private List<QuestionManagement> questionList;
}
