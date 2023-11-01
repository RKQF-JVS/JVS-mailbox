package cn.bctools.mail.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author admin
 * @ClassName: EmailDTO
 * @Description: 邮件服务配置对象

 */
@Data
@Accessors(chain = true)
@ApiModel(value = "邮件服务配置对象")
public class EmailConfigDto {
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
    /**
     * Additional JavaMail Session properties. 扩展字段
     */
    @ApiModelProperty(value = "扩展字段", hidden = true)
    private Map<String, String> propertiesMap;
}
