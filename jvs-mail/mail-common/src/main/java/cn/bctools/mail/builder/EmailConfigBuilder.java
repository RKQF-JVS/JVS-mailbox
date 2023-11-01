package cn.bctools.mail.builder;


import cn.bctools.common.exception.BusinessException;
import cn.bctools.mail.constant.MailConstant;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.entity.enums.EmailProtocolEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @ClassName: EmailConfigBuilder
 * @Description: 配置工厂
 */
public class EmailConfigBuilder {
    public static Map<String, String> builder(SysUserMailConfig config) {
        HashMap<String, String> map = new HashMap<>(4);
        map.put("mail.store.protocol", config.getSendProtocol());
        // 设置邮箱参数
        if (EmailProtocolEnum.SMTP.getValue().equals(config.getAcceptProtocol())) {
            return MailConstant.ENCRYPTED_SMTP_SENDING;
        } else if (EmailProtocolEnum.IMAP.getValue().equals(config.getAcceptProtocol())) {
            map.put("mail.imap.socketFactory.port", config.getAcceptPort().toString());
            map.put("mail.imap.host", config.getAcceptHost());
            map.put("mail.imap.port", config.getAcceptPort().toString());
            return map;
        } else if (EmailProtocolEnum.POP.getValue().equals(config.getAcceptProtocol())) {
            map.put("mail.pop3.socketFactory.port", config.getAcceptHost());
            map.put("mail.pop3.host", config.getAcceptHost());
            map.put("mail.pop3.port", config.getAcceptPort().toString());
            return map;
        }

        throw new BusinessException("未获取对应参数");
    }
}
