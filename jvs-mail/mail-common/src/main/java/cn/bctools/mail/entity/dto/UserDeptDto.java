package cn.bctools.mail.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author admin
 */
@Data
@Accessors(chain = true)
public class UserDeptDto {

    private String userId;
    private String realName;
    private String deptId;
}
