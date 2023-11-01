package cn.bctools.mail.service;


import cn.bctools.mail.api.dto.MailSendDTO;

/**
 * @author admin
 * @ClassName: MailSendService
 * @Description: 邮件发送

 */
public interface MailSendService {
    /**
     * 保存邮件信息
     *
     * @param send
     * @return void

     **/
    void save(MailSendDTO send);

    /**
     * 发送邮件
     *
     * @param send 邮件对象

     **/
    void sendMail(MailSendDTO send);
}
