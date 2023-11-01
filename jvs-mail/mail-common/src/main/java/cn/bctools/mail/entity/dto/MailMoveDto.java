package cn.bctools.mail.entity.dto;

import lombok.Data;

/**
 * @author R
 */
@Data
public class MailMoveDto {

    /**
     * 邮件id集
     */
    private String mailIds;

    /**
     * 分组id
     */
    private String groupId;

    private String configId;
}
