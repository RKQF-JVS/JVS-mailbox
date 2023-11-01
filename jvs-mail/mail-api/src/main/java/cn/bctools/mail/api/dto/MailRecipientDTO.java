package cn.bctools.mail.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 邮件收件人
 *
 * @author admin
 */
@ApiModel(value = "邮件接收人",description = "邮件接收人")
@Data
@Accessors(chain = true)
public class MailRecipientDTO implements Serializable {

    /**
     * 邮箱名称
     */
    @ApiModelProperty(value = "邮箱和名字组合", hidden = true)
    private String mailName;


    /**
     * 邮箱名称
     */
    @ApiModelProperty(value = "邮箱")
    private String mail;


    /**
     * 接收人
     */
    @ApiModelProperty(value = "接收人")
    private String name;
}