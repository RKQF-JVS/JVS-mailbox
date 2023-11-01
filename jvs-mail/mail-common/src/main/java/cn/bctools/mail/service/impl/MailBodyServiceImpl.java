package cn.bctools.mail.service.impl;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import cn.bctools.mail.component.FolderMessageFuntion;
import cn.bctools.mail.component.ShowMailCompontent;
import cn.bctools.mail.constant.MailConstant;
import cn.bctools.mail.entity.dto.UpdateMailDto;
import cn.bctools.mail.entity.*;
import cn.bctools.mail.entity.enums.EmailProtocolEnum;
import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import cn.bctools.mail.entity.enums.MailDefaultGroupEnum;
import cn.bctools.mail.mapper.*;
import cn.bctools.mail.service.MailBodyService;
import cn.bctools.mail.service.SysUserMailAcceptService;
import cn.bctools.mail.utils.FlagesUtil;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.template.OssTemplate;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
@Slf4j
@AllArgsConstructor
public class MailBodyServiceImpl extends ServiceImpl<MailBodyMapper, MailBody> implements MailBodyService {

    MailBodyMapper mailBodyMapper;
    MailContentMapper mailContentMapper;
    MailExtendMapper mailExtendMapper;
    MailRecipientMapper mailRecipientMapper;
    TransactionDefinition transactionDefinition;
    PlatformTransactionManager platformTransactionManager;
    SysUserMailAcceptService sysUserMailAcceptService;
    RedissonClient redissonClient;
    RedisUtils redisUtils;
    MailSensitivityMapper mailSensitivityMapper;
    private final OssTemplate ossTemplate;

    /**
     * 邮件详情 返回前后邮件id 如果不存在返回0
     *
     * @param userId   用户id
     * @param groupId  分组id
     * @param configId 配置id
     * @return K：“upId” 上一份邮件id
     * K：“downId” 下一份邮件id
     */
    @Override
    public Map<String, String> queryAroundMailBodyId(String id, String userId, String groupId, String configId) {
        LambdaQueryWrapper<MailBody> wrapper = Wrappers.<MailBody>lambdaQuery()
                .select(MailBody::getId)
                .eq(MailBody::getUserId, userId)
                .eq(MailBody::getGroupId, groupId)
                .eq(MailBody::getMailConfigId, configId)
                .eq(MailBody::getDelFlag, 0)
                .orderByDesc(MailBody::getSentDate);
        List<MailBody> list = this.list(wrapper);
        Map<String, String> map = new HashMap<String, String>(2);
        // 上一份邮件id
        map.put("upId", "0");
        // 下一份邮件id
        map.put("downId", "0");
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(list.get(i).getId())) {
                if (i != 0) {
                    map.put("upId", list.get(i - 1).getId());
                }
                if (i != list.size() - 1) {
                    map.put("downId", list.get(i + 1).getId());
                }
            }
        }

        return map;
    }

    /**
     * 修改邮件状态
     *
     * @param mailIds  邮件id集
     * @param mailBody 实体对象
     */
    @Override
    public void updateMailBody(String mailIds, MailBody mailBody) {
        if (ObjectNull.isNotNull(mailIds)) {
            String type = ",";
            if (mailIds.contains(type)) {
                String[] split = mailIds.split(",");
                for (String st : split) {
                    mailBody.setId(st);
                    mailBodyMapper.updateById(mailBody);
                }
            } else {
                mailBody.setId(mailIds);
                mailBodyMapper.updateById(mailBody);
            }
        }
    }

    @Override
    public String queryMailUpId(String id, String userId, String groupId, String configId) {

        return mailBodyMapper.queryMailUpId(id, userId, groupId, configId);
    }

    @Override
    public String queryMailDownId(String id, String userId, String groupId, String configId) {
        return mailBodyMapper.queryMailDownId(id, userId, groupId, configId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMail(UpdateMailDto updateMailDTO) {
        updateById(updateMailDTO.getMailBody());
        mailContentMapper.updateById(updateMailDTO.getMailContent());
        updateMailDTO.getMailExtend().forEach(e -> mailExtendMapper.updateById(e));
        updateMailDTO.getMailRecipient().forEach(e -> mailRecipientMapper.updateById(e));
        return true;
    }

    @Override
    public MailBody saveMailBody(MailBody copy) {
        copy.setId(null);
        mailBodyMapper.insert(copy);
        return copy;
    }

    /**
     * 主动收取邮件 收取前一天的收件
     *
     * @param funtion  回调函数
     * @param configId
     * @return
     */
    @Override
    public boolean save(FolderMessageFuntion funtion, String configId) {
        RLock lock = redissonClient.getLock(configId);
        lock.lock(3, TimeUnit.MINUTES);
        try {
            SysUserMailConfig config = sysUserMailAcceptService.getById(configId);
            if (config == null) {
                log.warn("对应用户{},邮件服务未配置，请检查", configId);
                throw new BusinessException("对应用户邮件服务未配置，请检查");
            }
            if (config.getUseWay().equals(1)) {
                //获取邮件过滤并保存
                getMail(funtion, config);
                return true;
            }
        } catch (Exception e) {
            log.error("保存数据 失败：{},{}", e.getMessage(), e.getStackTrace());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * 垃圾邮件判断
     */
    @Async
    public void updateMailType(MailBody mailBody, MailContents content) {
        List<String> filter = mailSensitivityMapper.selectList(Wrappers.lambdaQuery())
                .stream()
                .map(MailSensitivity::getSensitiveWords)
                .collect(Collectors.toList());
        if (filter == null) {
            return;
        }
        if (filter.size() == 0) {
            return;
        }
        for (String f : filter) {
            boolean matches = content.getContent().contains(f);
            if (matches) {
                updateById(new MailBody().setId(mailBody.getId()).setGroupId(MailDefaultGroupEnum.SPAM.getCode()));
                return;
            }
        }
    }

    @Override
    public boolean updateMessage(List<UpdateMailStatusDTO> messageList) {
        for (UpdateMailStatusDTO update : messageList) {
            try {
                Map<String, Object> acceptService = sysUserMailAcceptService.getAccptService(update.getConfigId());
                Folder folder = (Folder) acceptService.get(SysUserMailAcceptService.FOLDER);
                Store store = (Store) acceptService.get(SysUserMailAcceptService.STORE);
                String acceptProtocol = acceptService.get("acceptProtocol").toString();
                String username = acceptService.get("username").toString();
                String mailId = username + ":";
                Message[] messages = folder.getMessages();
                for (Message message : messages) {
                    String uid = "";
                    if (acceptProtocol.equalsIgnoreCase(EmailProtocolEnum.IMAP.getValue())) {
                        IMAPFolder imapFolder = (IMAPFolder) folder;
                        imapFolder.open(Folder.READ_WRITE);
                        uid = imapFolder.getUID(message) + "";
                    } else {
                        POP3Folder pop3Folder = (POP3Folder) folder;
                        pop3Folder.open(Folder.READ_WRITE);
                        uid = pop3Folder.getUID(message);
                    }
                    ShowMailCompontent re = new ShowMailCompontent((MimeMessage) message);
                    if (update.getMailId().equals(mailId + uid)) {
                        message.setFlag(FlagesUtil.getFlags(Integer.valueOf(update.getFlags())), true);
                        updateStatus(FlagesUtil.getFlags(Integer.valueOf(update.getFlags())), update.getMailId());
                    }
                }
                folder.close();
                store.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("修改状态异常:{},{}", e.getMessage(), e.getStackTrace());
            }
        }
        return true;
    }

    /**
     * 获取邮件过滤并保存
     *
     * @param funtion
     * @param config
     * @throws Exception
     */
    void getMail(FolderMessageFuntion funtion, SysUserMailConfig config) throws Exception {
        Map<String, Object> acceptService = sysUserMailAcceptService.getAcceptService(config);
        //邮件消息
        Folder folder = (Folder) acceptService.get(SysUserMailAcceptService.FOLDER);
        //数据存储对象
        Store store = (Store) acceptService.get(SysUserMailAcceptService.STORE);
        List<String> collect = list(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getUserId, config.getSysUserId()))
                .stream().map(MailBody::getMailId).collect(Collectors.toList());
        int count = 0;
        //IMAP协议
        if (config.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.IMAP.getValue())) {
            IMAPFolder imapFolder = (IMAPFolder) folder;
            if (config.getUsername().contains("@163") || config.getUsername().contains("@126")) {
                imapFolder.doOptionalCommand("ID not supported",
                        p -> p.id(MailConstant.CLIENT_PARAMS));
            }
            imapFolder.open(Folder.READ_WRITE);
            Message[] messages = funtion.search(folder);
            for (Message msg : messages) {
                count++;
                long uid = imapFolder.getUID(msg);
                log.debug("IMAP邮件UID为：{},邮箱为:{}", uid, config.getUsername());
                String mailId = config.getUsername() + ":" + uid;
                try {
                    if (!collect.contains(mailId)) {
                        boolean aNew = isNew(msg);
                        ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                        List<MailContents> mailContent = re.getMailContent(msg);
                        boolean containAttach = re.isContainAttach(msg);
                        saveMailBody(config.getSysUserId(), re, config.getId(), mailId, mailContent, containAttach, aNew);
                    }
                } catch (Exception e) {
                    log.error("IMAP邮件存储有误，mailId为：{}，错误原因为：{}", mailId, e.getMessage());
                }
            }
            //POP3协议
        } else if (config.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.POP3.getValue())) {
            POP3Folder pop3Folder = (POP3Folder) folder;
            pop3Folder.open(Folder.READ_WRITE);
            Message[] messages = pop3Folder.getMessages();
            for (Message msg : messages) {
                count++;
                String uid = pop3Folder.getUID(msg);
                log.debug("POP3邮件UID为：{},邮箱为：{}", uid, config.getUsername());
                String mailId = config.getUsername() + ":" + uid;
                try {
                    if (!collect.contains(mailId)) {
                        boolean aNew = isNew(msg);
                        ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                        boolean containAttach = re.isContainAttach(msg);
                        saveMailBody(config.getSysUserId(), re, config.getId(), mailId, re.getMailContent(msg), containAttach, aNew);
                    }
                } catch (Exception e) {
                    log.error("POP3邮件存储有误，mailId为：{}，错误原因为：{}", mailId, e.getMessage());
                }
            }
        }
        folder.close();
        store.close();
    }

    @Override
    public boolean save(Message[] messages, Integer userId) {
        return false;
    }

    @Override
    @Synchronized
    public boolean save(String id) {
        RLock lock = redissonClient.getLock(id);
        lock.lock(30, TimeUnit.SECONDS);
        try {
            SysUserMailConfig byId = sysUserMailAcceptService.getById(id);
            if (!byId.getUseWay().equals(1)) {
                return false;
            }
            Map<String, Object> acceptService = sysUserMailAcceptService.getAcceptService(byId);
            Folder folder = (Folder) acceptService.get(SysUserMailAcceptService.FOLDER);
            Store store = (Store) acceptService.get(SysUserMailAcceptService.STORE);
            String userId = acceptService.get("userId").toString();
            List<String> collect = list(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getUserId, userId))
                    .stream().map(e -> e.getMailId()).collect(Collectors.toList());
            if (byId.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.IMAP.getValue())) {
                IMAPFolder imapFolder = (IMAPFolder) folder;
                imapFolder.doOptionalCommand("ID not supported",
                        p -> p.id(MailConstant.CLIENT_PARAMS));
                imapFolder.open(Folder.READ_WRITE);
                //  Flags.Flag.SEEN 邮件阅读标记，标识邮件是否已被阅读   false 代表未读
                Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                log.debug("邮箱配置id为：{}，使用IMAP协议定时任务收取邮件数：{}", id, messages.length);
                for (Message msg : messages) {
                    long uid = imapFolder.getUID(msg);
                    log.debug("IMAP邮件UID为：{},邮箱为：{}", uid, byId.getUsername());
                    String mailId = byId.getUsername() + ":" + uid;
                    if (!collect.contains(mailId)) {
                        boolean aNew = isNew(msg);
                        ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                        List<MailContents> mailContent = re.getMailContent(msg);
                        boolean containAttach = re.isContainAttach(msg);
                        MailBody body = saveMailBody(userId, re, id, mailId, mailContent, containAttach, aNew);
                    }
                }
                //POP3协议
            } else if (byId.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.POP3.getValue())) {
                POP3Folder pop3Folder = (POP3Folder) folder;
                pop3Folder.open(Folder.READ_WRITE);
                Message[] messages = pop3Folder.getMessages();
                log.debug("邮箱配置id为：{}，使用POP3协议定时任务获取邮件数：{}", id, messages.length);
                for (Message msg : messages) {
                    String uid = pop3Folder.getUID(msg);
                    log.debug("POP3邮件UID为：{},邮箱为：{}", uid, byId.getUsername());
                    String mailId = byId.getUsername() + ":" + uid;
                    if (!collect.contains(mailId)) {
                        boolean aNew = isNew(msg);
                        ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                        boolean containAttach = re.isContainAttach(msg);
                        MailBody body = saveMailBody(userId, re, id, mailId, re.getMailContent(msg), containAttach, aNew);
                    }
                }
            }
            folder.close();
            store.close();
            return true;
        } catch (Exception e) {
            log.error("监听队列保存数据异常:{},{}", e.getMessage(), e.getStackTrace());
            return false;
        } finally {
            // 是否还是锁定状态
            if (lock.isLocked()) {
                // 时候是当前执行线程的锁
                if (lock.isHeldByCurrentThread()) {
                    // 释放锁
                    lock.unlock();
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(final Flags.Flag flag, String mailId) {
        Flags flags = new Flags(flag);
        LambdaUpdateWrapper<MailBody> wrapper = Wrappers.lambdaUpdate();
        if (flags.contains(Flags.Flag.SEEN)) {
            wrapper.set(MailBody::getRend, 1);
        } else if (flags.contains(Flags.Flag.DELETED)) {
            wrapper.set(MailBody::getGroupId, MailDefaultGroupEnum.DELETED.getCode());
        } else if (flags.contains(Flags.Flag.DRAFT)) {
            wrapper.set(MailBody::getGroupId, MailDefaultGroupEnum.DRAFT_BOX.getCode());
        } else if (flags.contains(Flags.Flag.ANSWERED)) {
            wrapper.set(MailBody::getAnswered, 1);
        }
        update(wrapper.eq(MailBody::getMailId, mailId));
    }

    @Override
    public boolean getAccessory(String id) {
        Boolean falg = false;
        try {
            log.debug("开始获取邮件附件：{},邮件id为：{}", LocalDateTime.now(), id);
            MailBody byId = getById(id);
            String mailId = byId.getMailId();
            SysUserMailConfig config = sysUserMailAcceptService.getOne(Wrappers.<SysUserMailConfig>lambdaQuery().eq(SysUserMailConfig::getId, byId.getMailConfigId()));
            Map<String, Object> acceptService = sysUserMailAcceptService.getAcceptService(config);
            log.debug("连接邮件服务器成功：{}", LocalDateTime.now());
            Folder folder = (Folder) acceptService.get(SysUserMailAcceptService.FOLDER);
            //数据存储对象
            Store store = (Store) acceptService.get(SysUserMailAcceptService.STORE);
            String s = mailId.split(":")[1];
            if (config.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.IMAP.getValue())) {
                IMAPFolder imapFolder = (IMAPFolder) folder;
                imapFolder.doOptionalCommand("ID not supported",
                        p -> p.id(MailConstant.CLIENT_PARAMS));
                imapFolder.open(Folder.READ_WRITE);
                Message msg = imapFolder.getMessageByUID(Integer.parseInt(s));
                ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                log.debug("IMAP开始解析邮件附件，当前邮件id为：{}", mailId);
                saveFile(msg, re, byId);
            } else if (config.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.POP3.getValue())) {
                log.debug("进入POP3协议解析邮件");
                POP3Folder pop3Folder = (POP3Folder) folder;
                pop3Folder.open(Folder.READ_WRITE);
                Message[] messages = pop3Folder.getMessages();
                for (Message msg : messages) {
                    String uid = pop3Folder.getUID(msg);
                    if (uid.equals(s)) {
                        log.debug("开始解析邮件附件，当前邮件id为：{}", uid);
                        ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                        saveFile(msg, re, byId);
                    }
                }
            }
            folder.close();
            store.close();
            falg = true;
        } catch (Exception e) {
            log.error("获取邮件附件并保存失败，失败原因：{}", e.getMessage());
            e.printStackTrace();
        }
        return falg;
    }

    /**
     * 保存文件
     *
     * @param message  消息
     * @param re       保存对象
     * @param mailBody 发送人ID
     * @return void
     **/
    void saveFile(Message message, ShowMailCompontent re, MailBody mailBody) {
        try {
            if (re.isContainAttach(message)) {
                re.saveAttachMent(message, (String fileName, Integer type, InputStream in, String cid) -> {
                    //重新转一次inputstream 防止实际读取大小与目标不一致
                    byte[] bytes = IoUtil.readBytes(in);
                    InputStream input = new ByteArrayInputStream(bytes);
                    BaseFile baseFile = ossTemplate.putFile("jvs-public", "", fileName, input);
                    String fileLink = ossTemplate.fileLink(baseFile.getFileName(), baseFile.getBucketName());
                    MailExtend mailExtend = new MailExtend();
                    mailExtend
                            .setCid(cid)
                            .setMailId(mailBody.getId())
                            .setUrl(fileLink)
                            .setName(fileName)
                            //附件
                            .setType(type);
                    mailExtendMapper.insert(mailExtend);
                    log.info("保存附件成功：{}", LocalDateTime.now());
                });
            }
        } catch (Exception e) {
            log.error("保存附件失败，失败原因：{}", e.getMessage());
        }

    }


    /**
     * 保存接收人
     *
     * @param data 数据
     * @param type 接收人类型
     * @param id   发送人ID
     * @return void
     **/
    void saveRecipient(List<MailRecipients> data, String type, String id) {
        if (data == null) {
            return;
        }
        List<MailRecipient> list = BeanCopyUtil.copys(data, MailRecipient.class);
        list.forEach(mailRecipient -> {
            mailRecipient.setMailId(id);
            switch (type) {
                case "to":
                    mailRecipient.setMailType(EmailRecipientTypeEnum.TO);
                    break;
                case "cc":
                    mailRecipient.setMailType(EmailRecipientTypeEnum.CC);
                    break;
                case "bcc":
                    mailRecipient.setMailType(EmailRecipientTypeEnum.BCC);
                    break;
                default:
                    throw new BusinessException("未知接收类型");
            }
            mailRecipientMapper.insert(mailRecipient);
        });
    }

    /**
     * @param mails 主体
     * @param id    发送人ID
     **/
    MailContents saveContent(List<MailContents> mails, String id) {
        if (CollectionUtil.isNotEmpty(mails)) {
            List<MailContents> collect = mails.stream().filter(e -> e.getType().contains("TEXT/HTML")).collect(Collectors.toList());
            Optional<MailContents> any = collect.size() > 0 ? collect.stream().findFirst() : mails.stream().findAny();
            any.ifPresent(mail -> mailContentMapper.insert(new MailContent().setMailId(id).setText(mail.getContent()).setTextType(mail.getType())));
            if (any.isPresent()) {
                return any.get();
            }
        }
        return null;
    }

    public boolean isNew(Message msg) throws javax.mail.MessagingException {
        boolean isNew = false;
        Flags flags = msg.getFlags();
        if (flags.contains(Flags.Flag.SEEN) && !flags.contains(Flags.Flag.RECENT)) {
            isNew = true;
        }
        return isNew;
    }

    /**
     * 保存 邮件基本信息集
     *
     * @param userId   用户id
     * @param re
     * @param configId 邮箱配置id
     * @return
     * @throws Exception
     */
    public MailBody saveMailBody(String userId, ShowMailCompontent re, String configId, String mailId, List<MailContents> mailContent, Boolean containAttach, Boolean aNew) throws Exception {
        String[] from = re.getFrom();
        log.info("form{}", from);
        String s = from[2];
        String k = "null";
        if (s.contains(k)) {
            s = s.replace("null", "").replace("<", "").replace(">", "");
        }
        MailBody mailBody = new MailBody()
                .setFrom(s)
                .setSentDate(re.getSentDate())
                .setMailConfigId(configId)
                .setReceipt(re.getReplySign())
                .setUrgent(re.getPriority())
                .setRend(aNew)
                .setMailId(mailId)
                .setUserId(userId)
                .setIsExtend(containAttach)
                .setSubject(re.getSubject() == null ? "" : re.getSubject())
                //收件箱 标识
                .setGroupId(MailDefaultGroupEnum.INBOX.getCode())
                .setIsSend(true);
        save(mailBody);
        //接收人
        saveRecipient(re.getMailAddress(EmailRecipientTypeEnum.TO.getValue()), EmailRecipientTypeEnum.TO.getValue(), mailBody.getId());
        saveRecipient(re.getMailAddress(EmailRecipientTypeEnum.CC.getValue()), EmailRecipientTypeEnum.CC.getValue(), mailBody.getId());
        saveRecipient(re.getMailAddress(EmailRecipientTypeEnum.BCC.getValue()), EmailRecipientTypeEnum.BCC.getValue(), mailBody.getId());
        //添加内容
        MailContents mailContents = saveContent(mailContent, mailBody.getId());
        //处理垃圾邮件
        updateMailType(mailBody, mailContents);
        return mailBody;
    }

}
