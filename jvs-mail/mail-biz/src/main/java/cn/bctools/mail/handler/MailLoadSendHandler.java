package cn.bctools.mail.handler;

import cn.bctools.mail.entity.enums.MailDefaultGroupEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.api.dto.MailRecipientDTO;
import cn.bctools.mail.api.dto.MailSendDTO;
import cn.bctools.mail.api.dto.MultipartFilesDTO;
import cn.bctools.mail.component.RedisDelayedQueue;
import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.MailContent;
import cn.bctools.mail.entity.MailExtend;
import cn.bctools.mail.entity.MailRecipient;
import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import cn.bctools.mail.mapper.MailBodyMapper;
import cn.bctools.mail.mapper.MailContentMapper;
import cn.bctools.mail.mapper.MailRecipientMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 邮件启动补偿任务
 *
 * @author admin
 * @ClassName: SmsAcceptConfig
 * @Description: 邮件启动补偿任务
 */
@Slf4j
@Order(10000)
@Component
@AllArgsConstructor
public class MailLoadSendHandler implements ApplicationRunner {
    private final RedisDelayedQueue redisDelayedQueue;
    private final MailBodyMapper mailBodyMapper;
    private final MailContentMapper mailContentMapper;
    private final cn.bctools.mail.mapper.MailExtendMapper MailExtendMapper;
    private final MailRecipientMapper mailRecipientMapper;


    @Override
    @Log
    public void run(ApplicationArguments args) throws Exception {
        log.info("系统重启后补偿机制启动");
        List<MailBody> list = mailBodyMapper.selectList(Wrappers.<MailBody>lambdaQuery()
                .eq(MailBody::getIsTiming, 1)
                .eq(MailBody::getGroupId, MailDefaultGroupEnum.DRAFT_BOX.getCode())
//                .eq(MailBody::getDelFlag, "0")
                .gt(MailBody::getTiming, LocalDateTime.now()));
        List<String> ids = list.stream().map(MailBody::getId).collect(Collectors.toList());
        if (ids.size() == 0) {
            return;
        }
        Map<String, List<MailContent>> mailContents = mailContentMapper.selectList(Wrappers.<MailContent>lambdaQuery()
                .in(MailContent::getMailId, ids))
                .stream().collect(Collectors.groupingBy(MailContent::getMailId));
        Map<String, List<MailRecipient>> mailRecipients = mailRecipientMapper.selectList(Wrappers.<MailRecipient>lambdaQuery()
                .in(MailRecipient::getMailId, ids))
                .stream().collect(Collectors.groupingBy(MailRecipient::getMailId));
        List<MailExtend> mailExtends = MailExtendMapper.selectList(Wrappers.<MailExtend>lambdaQuery()
                .in(MailExtend::getMailId, ids));
        list.forEach(body -> {
            MailSendDTO mailSendDTO = new MailSendDTO();
            MailContent mailContent = mailContents.get(body.getId()).stream().findAny().orElseGet(MailContent::new);
            mailSendDTO.setId(body.getId())
                    .setIsTiming(true)
                    .setUserId(body.getUserId())
                    .setReplyTo(body.getReplyTo())
                    .setTiming(body.getTiming())
                    .setUrgent(body.getUrgent())
                    .setReceipt(body.getReceipt())
                    .setSubject(body.getSubject())
                    .setTextType(mailContent.getTextType())
                    .setText(mailContent.getText())
                    .setSentDate(body.getSentDate())
                    .setTo(BeanCopyUtil.copys(mailRecipients.get(body.getId()).stream()
                            .filter(e -> e.getMailType() == EmailRecipientTypeEnum.TO)
                            .collect(Collectors.toList()), MailRecipientDTO.class))
                    .setCc(BeanCopyUtil.copys(mailRecipients.get(body.getId()).stream()
                            .filter(e -> e.getMailType() == EmailRecipientTypeEnum.CC)
                            .collect(Collectors.toList()), MailRecipientDTO.class))
                    .setBcc(BeanCopyUtil.copys(mailRecipients.get(body.getId()).stream()
                            .filter(e -> e.getMailType() == EmailRecipientTypeEnum.BCC)
                            .collect(Collectors.toList()), MailRecipientDTO.class))
                    .setMultipartFiles(BeanCopyUtil.copys(mailExtends, MultipartFilesDTO.class));
            Duration between = Duration.between(LocalDateTime.now(), body.getTiming());
            redisDelayedQueue.addQueue(mailSendDTO, MailSendDTO.class, between.getSeconds(), TimeUnit.SECONDS);
        });
    }


}
