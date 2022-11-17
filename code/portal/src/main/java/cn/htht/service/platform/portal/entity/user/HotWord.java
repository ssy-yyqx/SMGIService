package cn.htht.service.platform.portal.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class HotWord {

    /**
     * 分数
     */
    private Integer score;
    /**
     * 关键字值
     */
    private String value;
}
