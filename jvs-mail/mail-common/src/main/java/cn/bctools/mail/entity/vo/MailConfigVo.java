package cn.bctools.mail.entity.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author R
 */
@Data
@Accessors(chain = true)
public class MailConfigVo {
    private static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * id
     */
    private String id;

    /**
     * 对应的用户
     */
    private String sysUserId;

    /**
     * 邮箱类型
     */
    private String mailType;


    /**
     * 账户名称
     */
    private String account;

    /**
     * 服务器地址（发送)
     */
    private String sendHost;

    /**
     * 服务器地址（接受）
     */
    private String acceptHost;

    /**
     * 端口（发送)
     */
    private Integer sendPort;

    /**
     * 端口（接受）
     */
    private Integer acceptPort;

    /**
     * 邮箱名
     */
    private String username;


    /**
     * 发送协议（SMTP）
     */
    private String sendProtocol;

    /**
     * 接受协议（POP/IMAP）
     */
    private String acceptProtocol;

    /**
     * 编码
     */
    private String defaultEncodingS = DEFAULT_CHARSET;

    /**
     * Session JNDI name. When set, takes precedence over other Session settings.
     */
    private String jndiName;

    /**
     * 使用方式
     */
    private Integer useWay;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 备注
     */
    private String remarks;


    public final static Integer LOCK = 0;

    public final static Integer UN_LOCK = 1;

    private static final long serialVersionUID = 1L;

    private String name;
}
