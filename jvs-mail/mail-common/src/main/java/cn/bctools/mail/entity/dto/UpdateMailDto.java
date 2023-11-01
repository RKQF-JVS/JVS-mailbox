package cn.bctools.mail.entity.dto;

import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.MailContent;
import cn.bctools.mail.entity.MailExtend;
import cn.bctools.mail.entity.MailRecipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author admin
 * @ClassName: UpdateMailDTO
 * @Description: 修改邮件操作

 */
@Data
@Accessors(chain = true)
@ApiModel(value = "修改邮件对象")
public class UpdateMailDto {
    @ApiModelProperty(value = "邮件主体")
    private MailBody mailBody;
    @ApiModelProperty(value = "邮件内容")
    private MailContent mailContent;
    @ApiModelProperty(value = "邮件附件")
    private List<MailExtend> mailExtend;
    @ApiModelProperty(value = "邮件收件人")
    private List<MailRecipient> mailRecipient;
}
