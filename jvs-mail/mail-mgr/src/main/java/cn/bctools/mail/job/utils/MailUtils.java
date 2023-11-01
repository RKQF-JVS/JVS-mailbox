package cn.bctools.mail.job.utils;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.mail.component.FolderMessageFuntion;
import cn.bctools.mail.component.ShowMailCompontent;
import cn.bctools.mail.constant.MailConstant;
import cn.bctools.mail.entity.*;
import cn.bctools.mail.entity.enums.EmailProtocolEnum;
import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import cn.bctools.mail.entity.enums.MailDefaultGroupEnum;
import cn.bctools.mail.job.dto.MailBodyDto;
import cn.bctools.mail.job.dto.MailContentDto;
import cn.bctools.mail.component.ExchangeData;
import cn.bctools.mail.service.*;
import cn.bctools.mail.utils.TimeUtils;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@AllArgsConstructor
public class MailUtils {

    public static final String PULLING_KEY = "mail:pulling:";

    private final SysUserMailAcceptService sysUserMailAcceptService;
    private final MailSensitivityService mailSensitivityService;
    private final MailContentService mailContentService;
    private final MailRecipientService mailRecipientService;
    private final SysUserMailConfigService sysUserMailConfigService;
    private final MailBodyService mailBodyService;

    private final RedisUtils redisUtils;

    public boolean isNew(Message msg) throws javax.mail.MessagingException {
        boolean isNew = false;
        Flags flags = msg.getFlags();
        if (flags.contains(Flags.Flag.SEEN) && !flags.contains(Flags.Flag.RECENT)) {
            isNew = true;
        }
        return isNew;
    }

    /**
     * 实时同步
     * @param session
     * @param exchangeData
     */
    public void realtimePull(WebSocketSession session, ExchangeData exchangeData) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(exchangeData.getData());
        String configId = jsonObject.getString("configId");

        Map<String,Object> sessionRes = new HashMap<>();
        sessionRes.put("type",exchangeData.getType());
        sessionRes.put("configId",configId);

        String redisPullingKey = PULLING_KEY + configId;
        //判断是否正在拉取
        Boolean isPulling = this.isPullingMail(configId);
        if(isPulling){
            sessionRes.put("status","error");
            sessionRes.put("msg","该邮箱配置正在拉取中");
            session.sendMessage(new TextMessage(JSONUtil.toJsonStr(sessionRes)));
            return;
        }else{
            redisUtils.set(redisPullingKey,configId);
        }

        SysUserMailConfig mailConfig = sysUserMailConfigService.getById(configId);

        long unreadNum = 0L;
        //本月未读
        long unread2This = 0L;
        //上月未读
        long unread2pre = 0L;
        //更早未读
        long unread2earlier = 0L;
        try {
            if (ObjectUtil.isNotNull(mailConfig)) {
                if (mailConfig.getUseWay().equals(1)) {
                    int count = mailBodyService.count(Wrappers.<MailBody>lambdaQuery().eq(MailBody::getMailConfigId, mailConfig.getId()));
                    //拉取邮件
                    List<MailBody> mailBodyList = this.pullMail((Folder folder) -> {
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

                    if(!mailBodyList.isEmpty()){
                        List<String> mailBodyIds = mailBodyList.stream().map(MailBody::getId).collect(Collectors.toList());

                        List<MailBody> mailBodyList1 = mailBodyService.listByIds(mailBodyIds);
                        Date now = new Date();
                        Map<String,Object> data = new HashMap<>();

                        unreadNum = mailBodyList.stream().filter(e -> !e.getRend()).count();
                        //计数 10封邮件发一次
                        int num = 0;
                        List<MailBody> thisMonth = new ArrayList<>();
                        List<MailBody> lastMonth = new ArrayList<>();
                        List<MailBody> earlier = new ArrayList<>();
                        for (int i = 0; i < mailBodyList1.size(); i++) {
                            num++;

                            MailBody mailBody = mailBodyList1.get(i);

                            MailBodyDateEnum mailBodyDateEnum = this.checkMailBodyType(mailBody.getSentDate(),now);

                            switch (mailBodyDateEnum){
                                case 本月:
                                    thisMonth.add(mailBody);
                                    break;
                                case 上月:
                                    lastMonth.add(mailBody);
                                    break;
                                case 更早:
                                    earlier.add(mailBody);
                                    break;
                                default:
                            }

                            if(num==10 || i==mailBodyList1.size()-1){
                                data.put("thisMonth",thisMonth);
                                data.put("lastMonth",lastMonth);
                                data.put("earlier",earlier);

                                unread2This+=thisMonth.size();
                                unread2pre+=lastMonth.size();
                                unread2earlier+=earlier.size();

                                sessionRes.put("data",data);
                                sessionRes.put("status","pulling");
                                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(sessionRes)));

                                thisMonth.clear();
                                lastMonth.clear();
                                earlier.clear();
                                num=0;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisUtils.del(redisPullingKey);
        }
        Map<String,Long> unread = new HashMap<>();
        unread.put("thisMonth",unread2This);
        unread.put("lastMonth",unread2pre);
        unread.put("earlier",unread2earlier);
        sessionRes.put("unreadByDate",unread);
        sessionRes.put("unreadNum",unreadNum);
        sessionRes.put("status","success");
        session.sendMessage(new TextMessage(JSONUtil.toJsonStr(sessionRes)));

    }

    /**
     * 拉取邮件并储存相关信息
     * @param funtion
     * @param mailConfig
     * @return
     */
    public List<MailBody> pullMail(FolderMessageFuntion funtion, SysUserMailConfig mailConfig){
        Folder folder = null;
        Store store = null;
        try {
            TenantContextHolder.setTenantId(mailConfig.getTenantId());
            List<MailBodyDto> newMailList = new ArrayList<>();
            //标记本次拉取是否有新邮件
            Boolean hasNewMail = Boolean.FALSE;
            Map<String, Object> acceptService = sysUserMailAcceptService.getAcceptService(mailConfig);
            //邮件消息
            folder = (Folder) acceptService.get(SysUserMailAcceptService.FOLDER);
            //数据存储对象
            store = (Store) acceptService.get(SysUserMailAcceptService.STORE);

            List<String> collect = mailBodyService.list(Wrappers.<MailBody>lambdaQuery()
                    .eq(MailBody::getMailConfigId,mailConfig.getId())
                    .eq(MailBody::getUserId, mailConfig.getSysUserId()))
                    .stream().map(MailBody::getMailId).collect(Collectors.toList());
            //IMAP协议
            if (mailConfig.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.IMAP.getValue())) {
                IMAPFolder imapFolder = (IMAPFolder) folder;
                if (mailConfig.getUsername().contains("@163") || mailConfig.getUsername().contains("@126")) {
                    imapFolder.doOptionalCommand("ID not supported",
                            p -> p.id(MailConstant.CLIENT_PARAMS));
                }
                imapFolder.open(Folder.READ_WRITE);
                Message[] messages = funtion.search(folder);
                for (Message msg : messages) {
                    long uid = imapFolder.getUID(msg);
                    log.debug("IMAP邮件UID为：{},邮箱为:{}", uid, mailConfig.getUsername());
                    String mailId = mailConfig.getUsername() + ":" + uid;
                    try {
                        if (!collect.contains(mailId)) {
                            boolean aNew = this.isNew(msg);
                            if(!hasNewMail&&aNew){
                                hasNewMail = Boolean.TRUE;
                            }
                            ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                            List<MailContents> mailContent = re.getMailContent(msg);
                            boolean containAttach = re.isContainAttach(msg);
                            MailBodyDto newMailBody = this.createNewMailBody(mailConfig.getSysUserId(), re, mailConfig.getId(), mailId, mailContent, containAttach, aNew);
                            newMailList.add(newMailBody);
                        }
                    } catch (Exception e) {
                        log.error("IMAP邮件存储有误，mailId为：{}，错误原因为：{}", mailId, e.getMessage());
                    }
                }
                //POP3协议
            } else if (mailConfig.getAcceptProtocol().equalsIgnoreCase(EmailProtocolEnum.POP3.getValue())) {
                POP3Folder pop3Folder = (POP3Folder) folder;
                pop3Folder.open(Folder.READ_WRITE);
                Message[] messages = pop3Folder.getMessages();
                for (Message msg : messages) {
                    String uid = pop3Folder.getUID(msg);
                    log.debug("POP3邮件UID为：{},邮箱为：{}", uid, mailConfig.getUsername());
                    String mailId = mailConfig.getUsername() + ":" + uid;
                    try {
                        if (!collect.contains(mailId)) {
                            boolean aNew = this.isNew(msg);
                            if(!hasNewMail&&aNew){
                                hasNewMail = Boolean.TRUE;
                            }
                            ShowMailCompontent re = new ShowMailCompontent((MimeMessage) msg);
                            boolean containAttach = re.isContainAttach(msg);
                            MailBodyDto newMailBody = this.createNewMailBody(mailConfig.getSysUserId(), re, mailConfig.getId(), mailId, re.getMailContent(msg), containAttach, aNew);
                            newMailList.add(newMailBody);
                        }
                    } catch (Exception e) {
                        log.error("POP3邮件存储有误，mailId为：{}，错误原因为：{}", mailId, e.getMessage());
                    }
                }
            }
            if(!newMailList.isEmpty()){
                List<MailBody> mailBodyList = newMailList.stream().map(MailBodyDto::getMailBody).collect(Collectors.toList());
                mailBodyService.saveBatch(mailBodyList);
                List<MailContent> mailContentList = newMailList.stream().map(MailBodyDto::getMailContent).collect(Collectors.toList());
                mailContentService.saveBatch(mailContentList);
                List<MailRecipient> mailRecipientList = newMailList.stream().map(MailBodyDto::getMailRecipients).flatMap(Collection::stream).collect(Collectors.toList());
                mailRecipientService.saveBatch(mailRecipientList);
                return mailBodyList;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(Optional.ofNullable(folder).isPresent()){
                    folder.close();
                }
                if(Optional.ofNullable(store).isPresent()){
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 创建新 邮件基本信息集
     *
     * @param userId   用户id
     * @param re
     * @param configId 邮箱配置id
     * @return
     * @throws Exception
     */
    public MailBodyDto createNewMailBody(String userId, ShowMailCompontent re, String configId, String mailId, List<MailContents> mailContent, Boolean containAttach, Boolean aNew) throws Exception {
        String[] from = re.getFrom();
        log.info("form{}", from);
        String s = from[2];
        String k = "null";
        if (s.contains(k)) {
            s = s.replace("null", "").replace("<", "").replace(">", "");
        }
        LocalDateTime sentDate = re.getSentDate();
        if(ObjectUtil.isNull(re.getSentDate())){
            sentDate = LocalDateTime.now();
        }
        MailBody mailBody = new MailBody()
                .setId(IdGenerator.getIdStr())
                .setFrom(s)
                .setSentDate(sentDate)
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

        //添加内容
        MailContentDto mailContents = createContents(mailContent, mailBody.getId());
        //接收人
        List<MailRecipient> to = this.createRecipient(re.getMailAddress(EmailRecipientTypeEnum.TO.getValue()), EmailRecipientTypeEnum.TO.getValue(), mailBody.getId());
        List<MailRecipient> cc = this.createRecipient(re.getMailAddress(EmailRecipientTypeEnum.CC.getValue()), EmailRecipientTypeEnum.CC.getValue(), mailBody.getId());
        List<MailRecipient> bcc = this.createRecipient(re.getMailAddress(EmailRecipientTypeEnum.BCC.getValue()), EmailRecipientTypeEnum.BCC.getValue(), mailBody.getId());
        List<MailRecipient> mailRecipients = Stream.of(to, cc, bcc).flatMap(Collection::stream).collect(Collectors.toList());

        //检查是否为垃圾邮件
        MailBody mailBody1 = checkMailType(mailBody, mailContents.getMailContents());

        return new MailBodyDto().setMailBody(mailBody1).setMailContent(mailContents.getMailContent()).setMailRecipients(mailRecipients);
    }

    /**
     * 创建接收人数据
     *
     * @param data 数据
     * @param type 接收人类型
     * @param id   发送人ID
     * @return void
     **/
    public List<MailRecipient> createRecipient(List<MailRecipients> data, String type, String id){
        if (data == null) {
            return new ArrayList<>();
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
        });
        return list;
    }

    /**
     * @param mails 主体
     * @param id    发送人ID
     **/
    public MailContentDto createContents(List<MailContents> mails, String id) {
        MailContentDto mailContentDto = new MailContentDto();
        if (CollectionUtil.isNotEmpty(mails)) {
//            log.debug("邮件主题类型有哪些{}",mails);
            List<MailContents> collect = mails.stream().filter(e -> e.getType().contains("TEXT/HTML")).collect(Collectors.toList());
            Optional<MailContents> any = collect.size() > 0 ? collect.stream().findFirst() : mails.stream().findAny();
            any.ifPresent(mail -> mailContentDto
                    .setMailContent(new MailContent().setMailId(id).setText(mail.getContent()).setTextType(mail.getType()))
                    .setMailContents(mail));
        }
        return mailContentDto;
    }

    /**
     * 垃圾邮件判断
     */
    public MailBody checkMailType(MailBody mailBody, MailContents content) {
        List<String> filter = this.getSensitivity();
        if (filter.size() == 0) {
            return mailBody;
        }
        for (String f : filter) {
            boolean matches = content.getContent().contains(f);
            if (matches) {
                return mailBody.setGroupId(MailDefaultGroupEnum.SPAM.getCode());
            }
        }
        return mailBody;
    }

    private List<String> getSensitivity(){
        String key = "mail:sensitivity";
        Object o = redisUtils.get(key);
        if(ObjectUtil.isNull(o)){
            List<String> filter = mailSensitivityService.list()
                    .stream()
                    .map(MailSensitivity::getSensitiveWords)
                    .collect(Collectors.toList());
            redisUtils.set(key,JSONUtil.toJsonStr(filter));
        }
        return JSONUtil.toList(JSONUtil.parseArray(o),String.class);
    }

    /**
     * 判断是否正在拉取邮件 如果正在拉取则返回true 没有拉取则返回false
     * @param mailConfigId 邮箱id
     * @return
     */
    public Boolean isPullingMail(String mailConfigId){
        String redisPullingKey = PULLING_KEY + mailConfigId;
        Object o = redisUtils.get(redisPullingKey);
        if(ObjectUtil.isNotNull(o)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 检查是邮件所属分类 本月 上月 更早
     * @param mailBodyDate
     * @param contrastDate
     * @return
     */
    public MailBodyDateEnum checkMailBodyType(LocalDateTime mailBodyDate,Date contrastDate){
        Date date = TimeUtils.localDataTimeToDate(mailBodyDate);
        //计算相差天数 绝对值
        long l = DateUtil.betweenMonth(date, contrastDate, true);
        if(l==0L){
            return MailBodyDateEnum.本月;
        }else{
            //上个月
            if(l==1){
                return MailBodyDateEnum.上月;
            }else {
                //更早
                return MailBodyDateEnum.更早;
            }
        }
    }
}
