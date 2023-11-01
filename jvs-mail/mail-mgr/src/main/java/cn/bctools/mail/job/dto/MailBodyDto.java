package cn.bctools.mail.job.dto;

import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.MailContent;
import cn.bctools.mail.entity.MailRecipient;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MailBodyDto {
    //基础信息
    MailBody mailBody;
    //邮件内容
    MailContent mailContent;
    //邮件接收人
    List<MailRecipient> mailRecipients;
}
