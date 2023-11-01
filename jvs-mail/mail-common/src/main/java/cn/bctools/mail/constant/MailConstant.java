package cn.bctools.mail.constant;

import java.util.LinkedHashMap;

/**
 * @author admin
 * @ClassName: MailConstant
 * @Description: 邮箱配置

 */
public class MailConstant {
    /**
     * smtpSSL
     */
    public static LinkedHashMap<String, String> ENCRYPTED_SMTP_SENDING = new LinkedHashMap<>(8);


    /**
     * imtp SSL
     */
    public static LinkedHashMap<String, String> ENCRYPTED_IMAP_SENDING = new LinkedHashMap<>(8);

    /**
     * POP3 SSL
     */
    public static LinkedHashMap<String, String> ENCRYPTED_POP3_SENDING = new LinkedHashMap<>(8);
    /**
     * 接收参数
     */
    public static LinkedHashMap<String, String> CLIENT_PARAMS = new LinkedHashMap<>(3);

    static {
        ENCRYPTED_SMTP_SENDING.put("mail.smtp.starttls.enable", "true");
        ENCRYPTED_SMTP_SENDING.put("mail.smtp.starttls.required", "true");
        ENCRYPTED_SMTP_SENDING.put("mail.smtp.auth", "true");
        ENCRYPTED_SMTP_SENDING.put("mail.debug", "true");
        ENCRYPTED_SMTP_SENDING.put("mail.smtp.socketFactory.fallback", "true");


        ENCRYPTED_IMAP_SENDING.put("mail.imap.socketFactory.fallback", "false");
        ENCRYPTED_IMAP_SENDING.put("mail.imap.partialfetch", "false");


        ENCRYPTED_POP3_SENDING.put("mail.pop3.socketFactory.fallback", "false");
        ENCRYPTED_POP3_SENDING.put("mail.pop3.partialfetch", "false");

        CLIENT_PARAMS.put("name", "my-imap");
        CLIENT_PARAMS.put("version", "1.0");
        CLIENT_PARAMS.put("GUID", "FUTONG");
    }


}
