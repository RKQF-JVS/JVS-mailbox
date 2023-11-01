package cn.bctools.mail.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.R;
import cn.bctools.mail.api.api.MailSendApi;
import cn.bctools.mail.api.dto.MailSendDTO;
import cn.bctools.mail.component.RedisDelayedQueue;
import cn.bctools.mail.service.MailSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @ClassName: MailSendController
 * @Description: 邮件发送
 */
@RestController
@RequestMapping("mail")
@Api(value = "邮箱发送", tags = "邮箱发送接口")
@Slf4j
@AllArgsConstructor
public class MailSendController implements MailSendApi {

    private final MailSendService mailSendService;

    private final RedisDelayedQueue redisDelayedQueue;

    @Override
    @PostMapping("/send")
    @ApiOperation("邮箱立即发送服务")
    @SneakyThrows
    public R<Void> mailSendAttach(@Validated @RequestBody MailSendDTO send) {
        long start = System.currentTimeMillis();
        log.info("start 邮件发送服务，开始发送时间{}，接收参数为：{}", start, JSONUtil.toJsonPrettyStr(send));
        if (!send.getIsTiming()) {
            try {
                mailSendService.sendMail(send);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("邮件发送失败,发送邮箱：{}，失败原因：{}", send.getFrom(), e.getMessage());
                throw new BusinessException(StrUtil.isBlank(e.getMessage())?"邮件发送失败":e.getMessage());
            }
            log.info("邮件发送服务耗时时间为：{}", System.currentTimeMillis() - start);
            log.info("END 邮件发送服务");
            return R.ok();
        }
        if (send.getTiming().isAfter(LocalDateTime.now())) {
            Duration between = Duration.between(LocalDateTime.now(), send.getTiming());
            log.debug("延迟事件时间:{}", Math.abs(between.getSeconds()));
            redisDelayedQueue.addQueue(send, MailSendDTO.class, Math.abs(between.getSeconds()), TimeUnit.SECONDS);
        }
        return R.ok();
    }

    @Override
    @PostMapping("/save")
    @ApiOperation("邮箱保存邮件服务")
    @SneakyThrows
    public R<Void> saveEmail(@Validated @RequestBody MailSendDTO send) {
        mailSendService.save(send);
        return R.ok();
    }
}
