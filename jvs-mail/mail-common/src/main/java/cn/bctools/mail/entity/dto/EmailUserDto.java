package cn.bctools.mail.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author admin
 * @ClassName: EmailUserDTO
 * @Description: 邮箱用户字段

 */
@Data
public class EmailUserDto implements Serializable {


    /**
     * 主键id
     */
    private String id;

    /**
     * 邮箱类型
     */
    @ApiModelProperty(value = "邮箱类型")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mailType;

    /**
     * 账户名称
     */
    @ApiModelProperty(value = "账户名称")
    private String account;

    /**
     * 邮箱名
     */
    @ApiModelProperty(value = "邮箱名")
    @Email(message = "邮箱格式不正确")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 服务器地址（发送)
     */
    @ApiModelProperty(value = "服务器地址（发送)")
    @NotEmpty(message = "发送服务器地址不能为空")
    private String sendHost;

    /**
     * 服务器地址（接受）
     */
    @ApiModelProperty(value = "服务器地址（接受）")
    @NotEmpty(message = "接受服务器地址不能为空")
    private String acceptHost;

    /**
     * 端口（发送)
     */
    @ApiModelProperty(value = "端口（发送)")
    @NotNull(message = "发送端口不能为空")
    private Integer sendPort;

    /**
     * 端口（接受）
     */
    @ApiModelProperty(value = "端口（接受）")
    @NotNull(message = "接受端口不能为空")
    private Integer acceptPort;

    /**
     * 接受协议是否加密 0 未勾选 1 勾选
     */
    private Integer acceptSsl;

    /**
     * 发送协议是否加密 0 未勾选 1 勾选
     */
    private Integer sendSsl;


    /**
     * 发送协议（SMTP）
     */
    @ApiModelProperty(value = "发送协议（SMTP）")
    @NotEmpty(message = "发送协议类型不能为空")
    private String sendProtocol;

    /**
     * 接受协议（POP/IMAP）
     */
    @ApiModelProperty(value = "接受协议（POP/IMAP）")
    @NotEmpty(message = "接受协议类型不能为空")
    private String acceptProtocol;

    @TableField(value = "use_way")
    @ApiModelProperty(value = "使用方式 1是使用")
    private Integer useWay;
}
