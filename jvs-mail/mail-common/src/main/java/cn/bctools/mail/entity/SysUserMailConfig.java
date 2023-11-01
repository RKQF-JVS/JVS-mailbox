package cn.bctools.mail.entity;

import cn.bctools.database.entity.po.BasalPo;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author admin
 */
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "邮箱配置")
@Data
@TableName(value = "sys_user_mail_config")
@Accessors(chain = true)
public class SysUserMailConfig extends BasalPo {

    private static final String DEFAULT_CHARSET = "UTF-8";

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 对应的用户
     */
    @TableField(value = "sys_user_id")
    @ApiModelProperty(value = "sys_user -> id")
    private String sysUserId;

    /**
     * 邮箱类型
     */
    @TableField(value = "mail_type")
    @ApiModelProperty(value = "邮箱类型")
    private String mailType;

    /**
     * 账户名称
     */
    @TableField(value = "account")
    @ApiModelProperty(value = "账户名称")
    private String account;

    /**
     * 服务器地址（发送)
     */
    @TableField(value = "send_host")
    @ApiModelProperty(value = "服务器地址（发送)")
    private String sendHost;

    /**
     * 服务器地址（接受）
     */
    @TableField(value = "accept_host")
    @ApiModelProperty(value = "服务器地址（接受）")
    private String acceptHost;

    /**
     * 端口（发送)
     */
    @TableField(value = "send_port")
    @ApiModelProperty(value = "端口（发送)")
    private Integer sendPort;

    /**
     * 端口（接受）
     */
    @TableField(value = "accept_port")
    @ApiModelProperty(value = "端口（接受）")
    private Integer acceptPort;

    /**
     * 接受协议是否加密 0 未勾选 1 勾选
     */
    @TableField(value = "accept_ssl")
    @ApiModelProperty(value = "接受协议是否加密 0 未勾选 1 勾选")
    private Integer acceptSsl;

    /**
     * 发送协议是否加密 0 未勾选 1 勾选
     */
    @TableField(value = "send_ssl")
    @ApiModelProperty(value = "发送协议是否加密 0 未勾选 1 勾选")
    private Integer sendSsl;

    /**
     * 邮箱名
     */
    @TableField(value = "username")
    @ApiModelProperty(value = "邮箱名")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 发送协议（SMTP）
     */
    @TableField(value = "send_protocol")
    @ApiModelProperty(value = "发送协议（SMTP）")
    private String sendProtocol;

    /**
     * 接受协议（POP/IMAP）
     */
    @TableField(value = "accept_protocol")
    @ApiModelProperty(value = "接受协议（POP/IMAP）")
    private String acceptProtocol;

    /**
     * 编码
     */
    @TableField(value = "default_encoding")
    @ApiModelProperty(value = "编码")
    private String defaultEncodingS = DEFAULT_CHARSET;

    /**
     * Session JNDI name. When set, takes precedence over other Session settings.
     */
    @TableField(value = "jndi_name")
    @ApiModelProperty(value = "JNI 名称")
    private String jndiName;

    /**
     * 使用方式
     */
    @TableField(value = "use_way")
    @ApiModelProperty(value = "使用方式 1是使用")
    private Integer useWay;

    /**
     * 删除标记
     */
    @TableField(value = "del_flag")
    @ApiModelProperty(value="删除标记")
    @TableLogic
    private Boolean delFlag;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    @ApiModelProperty(value="备注")
    private String remarks;

    /**
     * 备注
     */
    @TableField(value = "is_remind")
    @ApiModelProperty(value="是否开启消息提醒")
    private Boolean isRemind;

    /**
     * 租户Id
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value="租户Id")
    private String tenantId;

    public final static Integer LOCK = 0;

    public final static Integer UN_LOCK = 1;

    private static final long serialVersionUID = 1L;
}
