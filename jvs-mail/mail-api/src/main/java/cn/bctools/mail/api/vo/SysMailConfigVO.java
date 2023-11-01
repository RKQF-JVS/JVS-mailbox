package cn.bctools.mail.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * @author admin
 * @ClassName: SysUserMailConfigVO
 * @Description: 邮件配置

 */
@Data
@ApiModel(value = "邮件配置输出对象")
@Accessors(chain = true)
public class SysMailConfigVO implements Serializable {
    @ApiModelProperty(value = "邮件id")
    private Integer id;

    /**
     * 对应的用户
     */
    @ApiModelProperty(value = "用户id")
    private Integer sysUserId;

    /**
     * 邮箱类型
     */
    @ApiModelProperty(value = "邮箱类型")
    private String mailTypes;

    /**
     * 服务器地址
     */
    @ApiModelProperty(value = "服务器地址")
    private String host;

    /**
     * 端口
     */
    @ApiModelProperty(value = "端口")
    private Integer port;

    /**
     * 邮箱名
     */
    @ApiModelProperty(value = "邮箱名")
    private String username;

    /**
     * 协议
     */
    @ApiModelProperty(value = "协议")
    private String protocol;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private Charset defaultEncoding;

    /**
     * 使用方式
     */
    @ApiModelProperty(value = "使用方式 1是使用")
    private Integer useWay;
}
