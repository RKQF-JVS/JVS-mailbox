package cn.bctools.mail.handler;

import cn.bctools.mail.constant.RedisMqConstant;
import cn.bctools.mail.service.MailBodyService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @ClassName: AcceptHander
 * @Description: 邮件接收定期器
 */
@Component
@Slf4j
@Order(1000)
public class MailAcceptHandler implements Runnable {
    @Autowired
    private MailBodyService mailBodyService;
    @Autowired
    private RedissonClient redissonClient;


    @Override
    public void run() {
        //添加topic监听
        RTopic topic = redissonClient.getTopic(RedisMqConstant.MAIL_KEY, new SerializationCodec());
        topic.addListener(String.class, (charSequence, id) -> {
            log.debug("onMessage:" + charSequence + "; Thread: " + Thread.currentThread().getName());
            mailBodyService.save(id);
        });

    }
}
