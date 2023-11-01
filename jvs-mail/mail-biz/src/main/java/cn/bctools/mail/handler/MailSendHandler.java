package cn.bctools.mail.handler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.api.dto.MailSendDTO;
import cn.bctools.mail.component.RedisDelayedQueue;
import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.service.MailBodyService;
import cn.bctools.mail.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 邮件接收定时任务配置
 *
 * @author admin
 * @ClassName: SmsAcceptConfig
 * @Description: 邮件接收定时任务配置
 */
@Slf4j
@Order(10000)
@Component
public class MailSendHandler implements ApplicationRunner {
    @Autowired
    private RedisDelayedQueue redisDelayedQueue;

    @Autowired
    private MailBodyService mailBodyService;

    @Autowired
    private MailSendService mailSendService;

    @Override
    @Log
    public void run(ApplicationArguments args) {
        log.info("启动定时延迟队列，邮件发送监听");
        //监听延迟队列
        // 当获取到延迟队列后，实际处理逻辑
        RedisDelayedQueue.TaskEventListener<MailSendDTO> taskEventListener = mailSendDTO -> {
            MailBody one = mailBodyService.getOne(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getId, mailSendDTO.getId()));
            if (one != null && one.getTiming() != null && one.getTiming().isEqual(mailSendDTO.getTiming())) {
                log.debug("正在延迟发送数据");
                mailSendService.sendMail(mailSendDTO);
            }
        };
        //  触发获取队列的定时任务
        redisDelayedQueue.getQueue(MailSendDTO.class, taskEventListener);
    }


}
