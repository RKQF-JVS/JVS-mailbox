package cn.bctools.mail.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailDefaultGroupEnum {
    /**
     * 失败邮件
     */
    FAILED("-1","失败邮件"),
    /**
     * 收件箱
     */
    INBOX("1","收件箱"),
    /**
     * 草稿箱
     */
    DRAFT_BOX("2","草稿箱"),
    /**
     * 已发送
     */
    HAS_BEEN_SENT("3","已发送"),
    /**
     * 已删除
     */
    DELETED("4","已删除"),
    /**
     * 垃圾邮件
     */
    SPAM("5","垃圾邮件"),
    ;

    private final String code;
    private final String desc;

}
