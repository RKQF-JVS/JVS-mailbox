package cn.bctools.mail.job;

import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.socket.config.ClientConfig;
import cn.bctools.mail.job.utils.MailUtils;
import cn.bctools.mail.job.utils.MessageComponents;
import cn.bctools.mail.service.MailBodyService;
import cn.bctools.mail.service.SysUserMailConfigService;
import cn.bctools.mail.utils.TimeUtils;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.dto.messagePush.MessageBatchSendDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class SynchronousMailJob {

    private final SysUserMailConfigService sysUserMailConfigService;
    private final MailBodyService mailBodyService;
    private final MailUtils mailUtils;

    private final MessageComponents messageComponents;

    private final ClientConfig clientConfig;

    private final RedisUtils redisUtils;

    @XxlJob("synchronous-mail-job")
    private void configureTasks() {
        log.info("--start----"+DateUtil.now()+"------定时同步邮件开始-----------");
        //查询已启用邮箱配置
        List<SysUserMailConfig> mailConfigs = sysUserMailConfigService.list(new LambdaQueryWrapper<SysUserMailConfig>()
                .eq(SysUserMailConfig::getUseWay,1));

        //消息批量发送
        MessageBatchSendDto messageBatchSendDto = new MessageBatchSendDto();
        //站内信
        List<InsideNotificationDto> insideList = new ArrayList<>();
        //todo 微信模板消息
        List<TemplateMessageDTO> wxTemplateList = new ArrayList<>();

        for (SysUserMailConfig mailConfig : mailConfigs) {
            Boolean pullingMail = mailUtils.isPullingMail(mailConfig.getId());
            String pullingRedisKey = MailUtils.PULLING_KEY+mailConfig.getId();
            if(pullingMail){
                log.info("邮箱："+mailConfig.getUsername()+"正在拉取，跳过本次执行");
                continue;
            }else {
                redisUtils.set(pullingRedisKey,mailConfig.getId());
            }
            try {
                int count = mailBodyService.count(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getMailConfigId, mailConfig.getId()));
                List<MailBody> mailBodyList = mailUtils.pullMail((Folder folder) -> {
                    Message[] messages;
                    if (count > 0) {
                        LocalDateTime before = LocalDateTime.now().plusDays(-1);
                        SearchTerm comparisonTermGe = new ReceivedDateTerm(ComparisonTerm.GE, TimeUtils.localDataTimeToDate(before));
                        messages = folder.search(comparisonTermGe);
                    } else {
                        messages = folder.getMessages();
                    }
                    return messages;
                }, mailConfig);

                if(!mailBodyList.isEmpty() && mailConfig.getIsRemind()){
                    InsideNotificationDto insideNotificationDto = messageComponents.buildInsideMessageDto(mailConfig, mailBodyList.size());
                    insideList.add(insideNotificationDto);
                    TemplateMessageDTO templateMessageDTO = messageComponents.buildWxTemplateMessageDto(mailConfig, mailBodyList.size());
                    wxTemplateList.add(templateMessageDTO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtils.del(pullingRedisKey);
            }
        }
        //发送消息
        log.info("发送消息----start---");
        if(!insideList.isEmpty() || !wxTemplateList.isEmpty()){
            messageBatchSendDto.setClientCode(clientConfig.getClientId());
            messageBatchSendDto.setInsideNotificationDtoList(insideList);
            messageComponents.setWxConfig(wxTemplateList);
            messageBatchSendDto.setWxTemplateDtoList(wxTemplateList);
            messageComponents.send(messageBatchSendDto);
        }
        log.info("发送消息----end---");
        log.info("--end------"+DateUtil.now()+"------定时同步邮件结束-----------");
    }
}
