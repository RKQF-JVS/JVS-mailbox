package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.entity.enums.EmailProtocolEnum;
import cn.bctools.mail.builder.EmailConfigBuilder;
import cn.bctools.mail.builder.EmailFactoryBuilder;
import cn.bctools.mail.mapper.SysUserMailConfigMapper;
import cn.bctools.mail.service.SysUserMailAcceptService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author admin
 * @ClassName: SysUserMailAServiceImpl
 * @Description: 邮件接收

 */
@Service
@Slf4j
public class SysUserMailAcceptServiceImpl extends ServiceImpl<SysUserMailConfigMapper, SysUserMailConfig> implements SysUserMailAcceptService {

    @Override
    @SneakyThrows
    public Map<String, Object> getAccptService(String id) {
         SysUserMailConfig config = getById(id);
        if (config == null) {
            log.warn("对应用户{},邮件服务未配置，请检查", id);
            throw new BusinessException("对应用户邮件服务未配置，请检查");
        }
        Integer one = 1;
        Integer acceptSsl = config.getAcceptSsl();
        Properties props = new Properties();
        if (acceptSsl.equals(one)){
            props.putAll(EmailFactoryBuilder.builder(config.getAcceptProtocol()));
            props.putAll(EmailConfigBuilder.builder(config));
        }
        // 创建Session实例对象
        Session session = Session.getInstance(props);
        // 创建协议的Store对象
        Store store = session.getStore(config.getAcceptProtocol());
        // 连接邮件服务器
        try {
            if (acceptSsl.equals(one)){
                store.connect(config.getUsername(), config.getPassword());
            }else {
                store.connect(config.getAcceptHost(),config.getAcceptPort(),config.getUsername(),config.getPassword());
            }
        } catch (Exception e) {
            log.warn("对应用户无法连接到服务器，请检查{}", e.getMessage());
            e.printStackTrace();
            throw new BusinessException("服务连接失败,请检查邮箱："+config.getUsername()+"的密码："+config.getPassword()+"是否正确！");
        }
        // 获得收件箱
        Map<String, Object> map = new HashMap<>(2);
        Folder folder = store.getFolder("INBOX");
        map.put(FOLDER, folder);
        map.put(STORE, store);
        map.put("acceptProtocol",config.getAcceptProtocol());
        map.put("username",config.getUsername());
        map.put("userId",config.getSysUserId());
        return map;
    }

    @Override
    @SneakyThrows
    public Map<String, Object> getAcceptService(SysUserMailConfig config) {
        Integer one = 1;
        Integer acceptSsl = config.getAcceptSsl();
        Properties props = new Properties();
        Map<String, String> builder = EmailFactoryBuilder.builder(config.getAcceptProtocol());
        if (acceptSsl.equals(one)){
            if ( EmailProtocolEnum.IMAP.getValue().equals(config.getAcceptProtocol())) {
                builder.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            } else if (EmailProtocolEnum.POP.getValue().equals(config.getAcceptProtocol())) {
                builder.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            props.putAll(builder);
            props.putAll(EmailConfigBuilder.builder(config));
        }
        // 创建Session实例对象
        Session session = Session.getInstance(props);
        // 创建协议的Store对象
        Store store = session.getStore(config.getAcceptProtocol());
        // 连接邮件服务器
        try {
            if (acceptSsl.equals(one)){
                store.connect(config.getUsername(), config.getPassword());
            }else {
                store.connect(config.getAcceptHost(),config.getAcceptPort(),config.getUsername(),config.getPassword());
            }
        } catch (Exception e) {
            log.warn("对应用户无法连接到服务器，请检查{}", e.getMessage());
            e.printStackTrace();
            log.error("服务连接失败,请检查邮箱："+config.getUsername()+"的密码："+config.getPassword()+"是否正确！");
        }
        // 获得收件箱
        Map<String, Object> map = new HashMap<>(2);
        Folder folder = store.getFolder("INBOX");
        map.put(FOLDER, folder);
        map.put(STORE, store);
        map.put("userId",config.getSysUserId());
        return map;

    }

}
