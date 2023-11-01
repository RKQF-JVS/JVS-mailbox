package cn.bctools.mail.job.dto;

import cn.bctools.mail.entity.MailContent;
import cn.bctools.mail.entity.MailContents;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MailContentDto {
    /**
     * 邮件扩展 附件
     */
    MailContent mailContent;
    /**
     * 文本内容
     */
    MailContents mailContents;
}
