package cn.bctools.mail.builder;


import cn.bctools.common.exception.BusinessException;
import cn.bctools.mail.constant.MailConstant;
import cn.bctools.mail.entity.enums.EmailProtocolEnum;

import java.util.Map;

/**
 * @author admin
 * @ClassName: EmailFactoryBuilder
 * @Description: 邮件扩展接口工厂

 */
public class EmailFactoryBuilder {

    public static Map<String, String> builder(String protocol) {
        // 按协议获取对应参数
        if (EmailProtocolEnum.SMTP.getValue().equals(protocol)) {
            return MailConstant.ENCRYPTED_SMTP_SENDING;
        } else if ( EmailProtocolEnum.IMAP.getValue().equals(protocol)) {
            return MailConstant.ENCRYPTED_IMAP_SENDING;
        } else if (EmailProtocolEnum.POP.getValue().equals(protocol)) {
            return MailConstant.ENCRYPTED_POP3_SENDING;
        }
        throw new BusinessException("未获取对应参数："+protocol);
    }

}
