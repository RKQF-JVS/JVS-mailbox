package cn.bctools.mail.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author R
 */
@Data
@Accessors(chain = true)
public class EmailGroupDto {

    /**
     * 分组id
     */
    private Integer id;

    /**
     * 邮箱集
     */
    private List<String> list;
}
