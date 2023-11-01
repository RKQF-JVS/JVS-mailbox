package cn.bctools.mail.entity.dto;

import lombok.Data;

/**
 * @author R
 */
@Data
public class MailTagDto {

    /**
     * 邮件id集
     */
    private String mailIds;

    /**
     * 标记 0 已读  1 未读  2 红旗 3 取消红旗
     */
    private Integer tag;

    /**
     * 邮箱配置id
     */
    private String configId;
}
