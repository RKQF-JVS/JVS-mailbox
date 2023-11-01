package cn.bctools.mail.entity.dto;

import lombok.Data;

/**
 * @author R
 */
@Data
public class MailDto {

    /**
     * 分组id
     */
    private String groupId;

    /**
     * 主题
     */
    private String subject;


    /**
     * 1是强调 （红旗） 默认 0 不强调
     */
    private Integer stress;

    /**
     * 邮箱配置id
     */
    private String configId;


}
