package cn.bctools.mail.service.impl;


import cn.bctools.common.exception.BusinessException;
import cn.bctools.mail.api.dto.MailRecipientDTO;
import cn.bctools.mail.api.dto.MailSendDTO;
import cn.bctools.mail.api.dto.MultipartFilesDTO;
import cn.bctools.mail.entity.*;
import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import cn.bctools.mail.entity.enums.MailDefaultGroupEnum;
import cn.bctools.mail.service.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author admin
 * @ClassName: MailSendService
 * @Description: 邮件发送
 */
@Service
@AllArgsConstructor
@Slf4j
public class MailSendServiceImpl implements MailSendService {
    private final SysUserMailConfigService sysUserMailConfigService;
    private final MailBodyService mailBodyService;
    private final MailContentService mailContentService;
    private final MailExtendService mailExtendService;
    private final MailRecipientService mailRecipientService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MailSendDTO send) {
        MailBody body = new MailBody()
                .setGroupId(MailDefaultGroupEnum.FAILED.getCode())
                .setFrom(send.getFrom())
                .setIsSend(false)
                .setMailConfigId(send.getConfigId())
                .setUserId(send.getUserId())
                .setSubject(send.getSubject())
                .setIsTiming(send.getIsTiming())
                .setReceipt(send.getReceipt())
                .setSentDate(send.getSentDate())
                .setReplyTo(send.getReplyTo() == null ? "" : send.getReplyTo())
                .setTiming(send.getTiming())
                .setUrgent(send.getUrgent());
        mailBodyService.save(body);
        //返回邮件Id
        send.setId(body.getId());
        mailContentService.save(new MailContent().setMailId(body.getId()).setText(send.getText()).setTextType(send.getTextType()));
        //保存附件
        Optional.ofNullable(send.getMultipartFiles())
                .ifPresent(e -> e.forEach(d -> mailExtendService.save(new MailExtend()
                        .setMailId(body.getId())
                        .setUrl(d.getUrl())
                        .setName(d.getName()))));
        saveRecipient(send.getCc(), EmailRecipientTypeEnum.CC, body);
        saveRecipient(send.getBcc(), EmailRecipientTypeEnum.BCC, body);
        saveRecipient(send.getTo(), EmailRecipientTypeEnum.TO, body);
    }

    /**
     * 保存接收人
     *
     * @param list 保存对象
     * @param type 用户类型
     * @param body 文件主体
     * @return void
     **/
    private void saveRecipient(List<MailRecipientDTO> list, EmailRecipientTypeEnum type, MailBody body) {
        Optional.ofNullable(list)
                .ifPresent(e -> e.forEach(d -> mailRecipientService.save(new MailRecipient()
                        .setMailId(body.getId())
                        .setName(d.getName())
                        .setMailName(d.getMailName())
                        .setMailType(type)
                        .setMail(d.getMail()))));
    }

    @Override
    @SneakyThrows
    public void sendMail(MailSendDTO send) {
        log.debug("获取邮箱配置配置id:{},邮箱发送地址：{}", send.getConfigId(), send.getFrom());
        JavaMailSender mailSender = sysUserMailConfigService.getMailServiceBasedOnUserId(send.getConfigId());
        log.debug("发送数据");
        SysUserMailConfig byId = sysUserMailConfigService.getById(send.getConfigId());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSentDate(new Date());
        List<MailRecipientDTO> to = send.getTo();
        List<InternetAddress> tos = new ArrayList<>();
        //接收人
        for (MailRecipientDTO e : to) {
            InternetAddress inAddress = new InternetAddress();
            inAddress.setAddress(e.getMail());
            inAddress.setPersonal(e.getName());
            tos.add(inAddress);
        }
        helper.setTo(tos.toArray(new InternetAddress[0]));
        helper.setFrom(byId.getAccount() + "<" + byId.getUsername() + ">");
        helper.setSubject(send.getSubject());
        helper.setPriority(send.getUrgent() ? 1 : 0);
        if (send.getReceipt() != null && send.getReceipt()) {
            helper.getMimeMessage().setHeader("Disposition-Notification-To", byId.getUsername());
            helper.setReplyTo(byId.getUsername());
        }

        // 第二个参数为是否支持html，如果为true，则表示支持，此时如果写入<h2>nice</h2>，则nice会被加粗，默认为false
        helper.setText(send.getText(), true);
        //抄送
        if (send.getCc() != null && send.getCc().size() > 0) {
            for (MailRecipientDTO e : send.getCc()) {
                helper.addCc(e.getMail(), e.getName());
            }
        }
        //密抄
        if (send.getBcc() != null && send.getBcc().size() > 0) {
            for (MailRecipientDTO e : send.getBcc()) {
                helper.addBcc(e.getMail(), e.getName());
            }
        }
        if (send.getMultipartFiles() != null && send.getMultipartFiles().size() > 0) {
            for (MultipartFilesDTO files : send.getMultipartFiles()) {
                URL url = new URL(files.getUrl());
                InputStream inputStream = url.openStream();
                String fileName = Optional.of(files).map(MultipartFilesDTO::getName).orElse(this.getFileName(files.getUrl()));
                helper.addAttachment(fileName,new ByteArrayResource(IOUtils.toByteArray(inputStream)));
            }
        }
        try {
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送失败,失败原因:{},{}", e.getMessage(), e.getStackTrace());
            throw new BusinessException("发送失败,失败原因:" + e.getMessage());
        }
        log.debug("发送数据成功");
        mailBodyService.updateById(new MailBody().setId(send.getId()).setGroupId(MailDefaultGroupEnum.HAS_BEEN_SENT.getCode()));
    }

    /**
     * 从路径中获取名称
     * @param url
     * @return
     */
    private String getFileName(String url){
        return Optional.ofNullable(url).map(e -> e.substring(e.lastIndexOf("/")+1)).orElse("未命名");
    }
}
