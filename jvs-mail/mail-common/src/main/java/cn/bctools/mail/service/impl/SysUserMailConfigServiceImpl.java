package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.mail.entity.dto.EmailUserDto;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.builder.EmailFactoryBuilder;
import cn.bctools.mail.mapper.SysUserMailConfigMapper;
import cn.bctools.mail.service.SysUserMailConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;


/**
 * @author admin
 */
@Service
@Slf4j
public class SysUserMailConfigServiceImpl extends ServiceImpl<SysUserMailConfigMapper, SysUserMailConfig> implements SysUserMailConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMailConfig(EmailUserDto email, String id) {
        SysUserMailConfig config = BeanCopyUtil.copy(email, SysUserMailConfig.class);
        config.setMailType(email.getMailType());
        config.setUseWay(SysUserMailConfig.UN_LOCK);
        config.setSysUserId(id);
        save(config);
    }

    final Integer one =1;
    @Override
    public JavaMailSender getMailServiceBasedOnUserId(String id) {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        SysUserMailConfig config = getById(id);
        if (config == null) {
            log.warn("对应用户{},邮件服务未配置，请检查", id);
            throw new BusinessException("对应用户邮件服务未配置，请检查");
        }

        MailProperties properties = new MailProperties();
        BeanCopyUtil.copy(config, properties);
        properties.setHost(config.getSendHost());
        properties.setPort(config.getSendPort());
        properties.setDefaultEncoding(Charset.forName(config.getDefaultEncodingS()));
        Map<String, String> builder = new HashMap<>(1);
        if (config.getSendSsl().equals(one)){
            //勾选ssl 发送端口为 465
            builder = EmailFactoryBuilder.builder(config.getSendProtocol());
            builder.put("mail.smtp.socketFactoryClass", "javax.net.ssl.SSLSocketFactory");
            builder.put("mail.smtp.ssl.enable", "true");
        }else {
            //未勾选 ssl 发送端口为 25
        }
        log.debug("邮箱配置builder:{}",builder);
        applyProperties(properties, sender, Optional.ofNullable(builder).orElseThrow(() -> new BusinessException("未找到对应配置")));

        return sender;
    }

    /**
     * 复写springboot方法 <br>
     * 手动装配 {@link } 邮件服务
     * org.springframework.boot.autoconfigure.mail.MailSenderPropertiesConfiguration
     * @param properties 配置
     * @param sender     服务
     * @param map        seeion 配置

     **/
    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender, Map<String, String> map) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }
        if (!map.isEmpty()) {
            sender.setJavaMailProperties(asProperties(map));
        }
    }

    /**
     * @param source 源
     * @return java.util.Properties

     **/
    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

}
