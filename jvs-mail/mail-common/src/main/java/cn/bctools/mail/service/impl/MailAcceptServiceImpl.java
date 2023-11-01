package cn.bctools.mail.service.impl;

import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import cn.bctools.mail.constant.RedisMqConstant;
import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.service.MailAcceptService;
import cn.bctools.mail.service.MailBodyService;
import cn.bctools.mail.service.SysUserMailAcceptService;
import cn.bctools.mail.utils.TimeUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @ClassName: MailAcceptServiceImpl
 * @Description: 邮件收取服务

 */
@Service
@Slf4j
public class MailAcceptServiceImpl implements MailAcceptService {

    @Autowired
    private SysUserMailAcceptService sysUserMailAcceptService;

    @Autowired
    private MailBodyService mailBodyService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @SneakyThrows
    public void acceptMailAll() {
        List<String> list = sysUserMailAcceptService.list(Wrappers.<SysUserMailConfig>lambdaQuery()
                .select(SysUserMailConfig::getId)
                .eq(SysUserMailConfig::getUseWay, SysUserMailConfig.UN_LOCK))
                .stream()
                .map(SysUserMailConfig::getId)
                .collect(Collectors.toList());
        RTopic topic = redissonClient.getTopic(RedisMqConstant.MAIL_KEY, new SerializationCodec());
        list.parallelStream().forEach(topic::publish);
    }

    /**
     * 主动收取邮件
     * 收取当前 前1天后的所有邮件
     *
     * @param id 用户id
     */
    @SneakyThrows
    @Override
    public void acceptMailUserAll(String id) {
        int count = mailBodyService.count(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getMailConfigId, id));
        mailBodyService.save((Folder folder) -> {
            Message[] messages = new Message[0];
            if (count > 0) {
                LocalDateTime before = LocalDateTime.now().plusDays(-1);
                SearchTerm comparisonTermGe = new ReceivedDateTerm(ComparisonTerm.GE, TimeUtils.localDataTimeToDate(before));
                messages = folder.search(comparisonTermGe);
            } else {
                messages = folder.getMessages();
            }
            log.debug("用户邮箱配置id：" + id + ",主动收取邮件数量:　" + messages.length);
            return messages;
        }, id);
    }


    @SneakyThrows
    @Override
    public void acceptMailUserUnread(Integer id) {
        RTopic topic = redissonClient.getTopic(RedisMqConstant.MAIL_KEY, new SerializationCodec());
        topic.publish(id);
    }

    @Override
    public boolean updateMessage(List<UpdateMailStatusDTO> messageId) {
        return mailBodyService.updateMessage(messageId);
    }

    @Override
    public boolean getAccessory(String id) {
       return mailBodyService.getAccessory(id);
    }
}
