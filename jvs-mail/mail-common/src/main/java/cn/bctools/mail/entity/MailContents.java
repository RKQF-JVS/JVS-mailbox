package cn.bctools.mail.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author admin
 * @ClassName: MailContent
 * @Description: 文本内容

 */
@Data
@Accessors(chain = true)
public class MailContents {
    /**
     * 文本类型
     */
    private String type;
    /**
     * 文本内容
     */
    private String content;
}
