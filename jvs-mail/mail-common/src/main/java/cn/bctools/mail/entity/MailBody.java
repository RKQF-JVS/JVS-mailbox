package cn.bctools.mail.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 邮件主体内容
 *
 * @author admin
 */
@ApiModel(value = "邮件主体内容")
@Data
@TableName(value = "sys_mail_body")
@Accessors(chain = true)
public class MailBody {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * sys_mail_group ->id
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value = "sys_mail_group ->id")
    private String groupId;

    @TableField(value = "mail_config_id")
    @ApiModelProperty(value = "（sys_user_mail_config  ->id")
    private String mailConfigId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private String userId;


    /**
     * 邮件id
     */
    @TableField(value = "mail_id")
    @ApiModelProperty(value = "邮件id 接收时才有 防止重复")
    private String mailId;

    /**
     * 发件人
     */
    @TableField(value = "`from`")
    @ApiModelProperty(value = "发件人")
    private String from;

    /**
     * 回复地址
     */
    @TableField(value = "reply_to")
    @ApiModelProperty(value = "回复地址")
    private String replyTo;

    /**
     * 发送时间
     */
    @TableField(value = "sent_date")
    @ApiModelProperty(value = "发送时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    private LocalDateTime sentDate;

    /**
     * 主题
     */
    @TableField(value = "subject")
    @ApiModelProperty(value = "主题")
    private String subject;


    /**
     * 1是接收邮件 默认0 发送邮件
     */
    @TableField(value = "is_send")
    @ApiModelProperty(value = "1是接收邮件 默认0 发送邮件")
    private Boolean isSend;

    /**
     * 1是紧急 默认0 不紧急
     */
    @TableField(value = "urgent")
    @ApiModelProperty(value = "1是紧急 默认0 不紧急")
    private Boolean urgent;

    /**
     * 1是需要回执 默认0 不回执
     */
    @TableField(value = "receipt")
    @ApiModelProperty(value = "1是需要回执 默认0 不回执")
    private Boolean receipt;

    /**
     * 1 已回执 默认0 未回执
     */
    @TableField(value = "is_receipt")
    @ApiModelProperty(value = "1是需要回执 默认0 不回执")
    private Boolean isReceipt;

    /**
     * 1 存在附件  0 没有附件
     */
    @TableField(value = "is_extend")
    @ApiModelProperty(value = "1 存在附件  0 没有附件")
    private Boolean isExtend;

    /**
     * 1是需要定时发送 默认0 未定时
     */
    @TableField(value = "is_timing")
    @ApiModelProperty(value = "1是需要定时发送 默认0 未定时")
    private Boolean isTiming;

    /**
     * 1是已读 默认0 未读
     */
    @TableField(value = "rend")
    @ApiModelProperty(value = "1是已读 默认0 未读")
    private Boolean rend;

    /**
     * 1是已回复 默认0 未回复
     */
    @TableField(value = "answered")
    @ApiModelProperty(value = "1是已回复 默认0 未回复")
    private Boolean answered;

    /**
     * 1是强调 默认不强调（红旗）
     */
    @TableField(value = "stress")
    @ApiModelProperty(value = "1是强调 默认不强调（红旗）")
    private Boolean stress;

    /**
     * 定时发送时间 必须大于当前时间
     */
    @TableField(value = "timing")
    @ApiModelProperty(value = "定时发送时间 必须大于当前时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    private LocalDateTime timing;

    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    private LocalDateTime createTime;

    /**
     * 删除标记
     */
    @TableField(value = "del_flag")
    @ApiModelProperty(value = "删除标记")
    @TableLogic
    private Boolean delFlag;

    /**
     * 备注信息
     */
    @TableField(value = "remarks")
    @ApiModelProperty(value = "备注信息")
    private String remarks;

    /**
     * 标签id
     */
    @TableField(value = "mail_tag_id")
    @ApiModelProperty(value = "标签id")
    private String mailTagId;

    @TableField(exist = false)
    @ApiModelProperty(value = "标签详情")
    private SysMailTag sysMailTag;

    @TableField(exist = false)
    private String accepter;

    @TableField(exist = false)
    private String content;

    @TableField("del_completely")
    @ApiModelProperty(value = "是否彻底删除")
    private Boolean delCompletely;

    private static final long serialVersionUID = 1L;
}
