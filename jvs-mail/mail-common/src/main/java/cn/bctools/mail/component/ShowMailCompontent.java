package cn.bctools.mail.component;

import cn.bctools.mail.entity.MailContents;
import cn.bctools.mail.entity.MailRecipients;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * @author admin
 * javaMail 包装器
 */
@Slf4j
public class ShowMailCompontent {

    private MimeMessage mimeMessage = null;

    /**
     * 构造函数,初始化一个MimeMessage对象
     *
     * @param mimeMessage
     */
    public ShowMailCompontent(final MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }


    /**
     *  获得发件人的地址和姓名
     */
    public String[] getFrom() throws Exception {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        String[] list = new String[3];
        if (from == null) {
            list[0] = "";
            log.debug("无法知道发送者.");
        }
        list[0] = from;
        String personal = address[0].getPersonal();
        if (personal == null) {
            list[1] = "";
            log.debug("无法知道发送者的姓名.");
        }else{
            personal = personal.replace("\"","").trim();
        }
        list[1] = personal;
        log.debug("发送者是：" + personal + "<" + from + ">");
        list[2] = personal + "<" + from + ">";
        return list;
    }

    /**
     * 　获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同
     * 　"to"----收件人　"cc"---抄送人地址　"bcc"---密送人地址
     */
    public List<MailRecipients> getMailAddress(String type) throws Exception {
        List<MailRecipients> mails = new LinkedList<>();
        String addType = type.toUpperCase();
        if ("TO".equalsIgnoreCase(addType) || "CC".equalsIgnoreCase(addType)
                || "BCC".equalsIgnoreCase(addType)) {
            if ("TO".equalsIgnoreCase(addType)) {
                InternetAddress[] address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.TO);
                getMailAddresses(address, mails);
            } else if ("CC".equalsIgnoreCase(addType)) {
                InternetAddress[] address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.CC);
                getMailAddresses(address, mails);
            } else {
                InternetAddress[] address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.BCC);
                getMailAddresses(address, mails);
            }
        }
        return mails;
    }

    private void getMailAddresses(InternetAddress[] address, List<MailRecipients> mails) throws UnsupportedEncodingException {
        if (address != null) {
            for (InternetAddress internetAddress : address) {
                MailRecipients mail = new MailRecipients();
                String emailAddr = internetAddress.getAddress();
                if (emailAddr == null) {
                    emailAddr = "";
                } else {
                    log.debug("转换之前的emailAddr: " + emailAddr);
                    emailAddr = MimeUtility.decodeText(emailAddr);
                    log.debug("转换之后的emailAddr: " + emailAddr);
                    mail.setMail(emailAddr);
                }
                String personal = internetAddress.getPersonal();
                if (personal == null) {
                    personal = "";
                } else {
                    log.debug("转换之前的personal: " + personal);
                    personal = MimeUtility.decodeText(personal);
                    personal = personal.replace("\"","").trim();
                    log.debug("转换之后的personal: " + personal);
                    mail.setName(personal);
                }
                String compositeto = personal + "<" + emailAddr + ">";
                log.debug("完整的邮件地址：" + compositeto);
                mail.setMailName(compositeto);
                mails.add(mail);
            }
        }
    }


    /**
     * 　*　获得邮件主题
     */
    public String getSubject() {
        String subject = "";
        try {
            log.debug("转换前的subject：" + mimeMessage.getSubject());
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            log.debug("转换后的subject: " + mimeMessage.getSubject());
            if (subject == null) {
                subject = "";
            }
        } catch (Exception exce) {
            exce.printStackTrace();
        }
        return subject;
    }

    /**
     * 　*　获得邮件发送日期
     */
    public LocalDateTime getSentDate() throws Exception {
        Date sentDate = mimeMessage.getSentDate();
        if (sentDate == null) {
            return null;
        }
        return sentDate.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件
     * 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     */
    public List<MailContents> getMailContent(Part part) throws Exception {
        List<MailContents> content = new LinkedList<>();
        setMail(part, content);
        return content;
    }

    private void setMail(Part part, List<MailContents> content) throws MessagingException, IOException {
//        log.debug("文档对象,{}", part);
//        log.debug("文档对象内容,{}", part.getContent());
//        log.debug("文档对象头,{}", part.getAllHeaders().toString());
//        log.debug("文档对象主体类型,{}", part.getContentType());
//        log.debug("文档对象对象头,{}", part.getDataHandler().toString());
//        log.debug("文档对象对象头内容,{}", part.getDataHandler().getContent().toString());
//        log.debug("文档对象对象头类型,{}", part.getDataHandler().getContentType());
//        log.debug("文档对象对象头名字,{}", part.getDataHandler().getName());
//        log.debug("文档对象Content-id,{}", part.getHeader("Content-id"));
        String contentType = part.getContentType();
        // 获得邮件的MimeType类型
        int nameIndex = contentType.indexOf("name");
        boolean conName = false;
        if (nameIndex != -1) {
            conName = true;
        }
        if (part.isMimeType("text/html") && !conName) {
            // text/plain 类型
            content.add(new MailContents().setContent((String) part.getContent()).setType(contentType));
        } else if (part.isMimeType("text/plain") && !conName) {
            // text/html 类型
            content.add(new MailContents().setContent((String) part.getContent()).setType(contentType));
        } else if (part.isMimeType("multipart/*")) {
            // multipart/*
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                setMail(multipart.getBodyPart(i), content);
            }
        } else if (part.isMimeType("message/rfc822")) {
            setMail((Part) part.getContent(), content);
        }
    }


    /**
     * 　　*　判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
     */
    public boolean getReplySign() throws MessagingException {
        boolean replySign = false;
        String[] needReply = mimeMessage
                .getHeader("Disposition-Notification-To");
        if (needReply != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 获取优先级
     *
     * @return 返回优先级
     * @throws MessagingException
     */
    public boolean getPriority() throws MessagingException {
        String needReply = mimeMessage
                .getHeader("X-Priority", null);
        return needReply == null ? false : true;
    }

    /**
     * 　获得此邮件的Message-ID
     */
    public String getMessageId() {
        String messageId = null;
        try {
            messageId = mimeMessage.getMessageID();
            log.debug("邮件ID: " + messageId);
        } catch (MessagingException e) {
            log.error("邮件ID,获取失败.失败原因:{}", e.getMessage());
        }
        return messageId;
    }

    /**
     * 　获得此邮件的Message-ID
     */
    public List<String> getMessageCid() {
        String[] contentId = new String[0];
        try {
            contentId = mimeMessage.getHeader("Content-ID");
            log.debug("邮件cid: " + contentId);
        } catch (MessagingException e) {
            log.error("邮件cid,获取失败.失败原因:{}", e.getMessage());
        }
        return Arrays.asList(contentId);
    }


    /**
     * 判断此邮件是否已读，如果未读返回false,反之返回true
     */
    public boolean isNew() throws MessagingException {
        boolean isNew = false;
        Flags flags = ((Message) mimeMessage).getFlags();
        if (flags.contains(Flags.Flag.SEEN) && !flags.contains(Flags.Flag.RECENT)) {
            isNew = true;
        }
//        for (Flags.Flag value : flag) {
//            if (value == Flags.Flag.SEEN) {
//                isNew = true;
//                log.debug("seen email...");
//            }
//        }
        return isNew;
    }

    /**
     * 判断此邮件是否包含附件
     */
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachFlag = false;
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mPart = mp.getBodyPart(i);
                String disposition = mPart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE)))) {
                    attachFlag = true;
                } else if (mPart.isMimeType("multipart/*")) {
                    attachFlag = isContainAttach(mPart);
                } else {
                    String conType = mPart.getContentType();
//                    if (conType.toLowerCase().contains("application")) {
                    if (conType.indexOf("application") != -1) {
                        attachFlag = true;
                    }
//                    if (conType.toLowerCase().contains("name")) {
                    if (conType.indexOf("name") != -1) {
                        attachFlag = true;
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachFlag = isContainAttach((Part) part.getContent());
        }
        log.debug("判断邮件是否存在附件：{}", attachFlag);
        return attachFlag;
    }

    /**
     *　保存附件
     */
    public void saveAttachMent(Part part, FileSaveMailFuntion file) throws Exception {
        String fileName = "";
        String cid = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mPart = mp.getBodyPart(i);
                String disposition = mPart.getDisposition();
                String[] header = mPart.getHeader("Content-id");
                cid = getCid(header);
                log.info("partHeader内嵌图片{}", header);
                if (disposition != null) {
                    fileName = MimeUtility.decodeText(mPart.getFileName());
                    log.info("文件名称{}", fileName);
                    if (disposition.equals(Part.ATTACHMENT)) {
//                        log.debug("流对象:{}", mPart.getInputStream());
                        saveFile(fileName, 0, mPart.getInputStream(), file, cid);
                    } else if (disposition.equals(Part.INLINE)) {
//                        log.debug("流对象:{}", mPart.getInputStream());
                        saveFile(fileName, 1, mPart.getInputStream(), file, cid);
                    }
                } else if (mPart.isMimeType("multipart/*")) {
                    saveAttachMent(mPart, file);
                } else {
                    fileName = mPart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
//                        log.debug("流对象:{}", mPart.getInputStream());
                        saveFile(fileName, 0, mPart.getInputStream(), file, cid);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent(), file);
        }
    }

    public static String getCid(String[] headers) throws MessagingException {
        String content, cid;
//        String[] headers = p.getHeader("Content-Id");
        if (headers != null && headers.length > 0) {
            content = headers[0];
        } else {
            return "";
        }
        if (content.startsWith("<") && content.endsWith(">")) {
            cid = "cid:" + content.substring(1, content.length() - 1);
        } else {
            cid = "cid:" + content;
        }
        return cid;
    }

    /**
     * 　*　真正的保存附件到指定目录里
     */
    private void saveFile(String fileName, Integer type, InputStream in, FileSaveMailFuntion file, String cid) throws Exception {
        try {
            file.saveFile(fileName, type, in, cid);
        } catch (Exception exception) {
            log.error("保存附件失败\n{},\n{}", exception.getMessage(), exception.getStackTrace());
            throw new Exception("文件保存失败!");
        }
    }
}