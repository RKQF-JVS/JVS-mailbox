package cn.bctools.mail.api.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author admin
 * @ClassName: MailVo
 * @Description: 邮件发送

 */
@Data
@Accessors(chain = true)
@ApiModel(value = "邮件发送对象", description = "邮件发送对象")
public class MailSendDTO implements Serializable {
    /**
     * 邮件 当前库Id
     */
    @ApiModelProperty(value = "邮件Id", hidden = true)
    private String id;

    /**
     * 用户Id
     */
    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户Id")
    private String userId;

    /**
     * 邮箱配置id
     */
    @NotNull(message = "邮箱配置id不能为空")
    @ApiModelProperty(value = "邮箱配置id")
    private String configId;

    @ApiModelProperty(value = "分组ID")
    private String groupId;

    @ApiModelProperty(value = "发件人")
    private String from;
    /**
     * 邮件接收人
     */
    @ApiModelProperty(value = "邮件接收人")
    @NotEmpty(message = "邮件接收人不能为空")
    private List<MailRecipientDTO> to;
    /**
     * 邮件主题
     */
    @ApiModelProperty(value = "邮件主题")
    private String subject;
    /**
     * 邮件内容
     */
    @ApiModelProperty(value = "邮件内容")
    private String text;

    @ApiModelProperty(value = "邮件类型")
    @NotEmpty(message = "邮件类型不能为空")
    private String textType;
    /**
     * 回复地址
     */
    @ApiModelProperty(value = "回复地址")
    private String replyTo;
    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sentDate;

    /**
     * 是否紧急
     */
    @ApiModelProperty(value = "1是紧急 默认0 不紧急")
    private Boolean urgent;

    /**
     * 1是需要回执 默认0 不回执
     */
    @ApiModelProperty(value = "1是需要回执 默认0 不回执")
    private Boolean receipt;

    /**
     * 1是需要定时发送 默认0 未定时
     */
    @ApiModelProperty(value = "1是需要定时发送 默认0 未定时")
    private Boolean isTiming;


    /**
     * 是否需要分别发送 默认0 不需要 1 需要
     */
    @ApiModelProperty(value = "1 是分别发送 默认0 正常")
    private Boolean isRespectively;

    /**
     * 定时发送时间 必须大于当前时间
     * yyyy-MM-dd HH:mm
     */
    @ApiModelProperty(value = "定时发送时间 必须大于当前时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    private LocalDateTime timing;
    /**
     * 抄送
     */
    @ApiModelProperty(value = "抄送")
    private List<MailRecipientDTO> cc;
    /**
     * 密送
     */
    @ApiModelProperty(value = "密送")
    private List<MailRecipientDTO> bcc;
    /**
     * 邮件附件
     */
    @ApiModelProperty(value = "附件")
    private List<MultipartFilesDTO> multipartFiles;

}
