package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 邮件收件人
 * @author admin
 */
@ApiModel(value="邮件收件人")
@Data
@TableName(value = "sys_mail_recipient")
@Accessors(chain = true)
public class MailRecipient  {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * sys_mail_user_body ->id
     */
    @TableField(value = "mail_id")
    @ApiModelProperty(value="sys_mail_user_body ->id")
    private String mailId;

    /**
     * 邮箱名称
     */
    @TableField(value = "mail_name")
    @ApiModelProperty(value="邮箱和名字组合")
    private String mailName;


    /**
     * 邮箱名称
     */
    @TableField(value = "mail")
    @ApiModelProperty(value="邮箱")
    private String mail;


    /**
     * 接收人
     */
    @TableField(value = "name")
    @ApiModelProperty(value="接收人")
    private String name;

    /**
     * 类型
to ->接收人
cc ->抄送人
bcc ->密密抄送人
     */
    @TableField(value = "mail_type")
    @ApiModelProperty(value="类型,to ->接收人,cc ->抄送人,bcc ->密密抄送人")
    private EmailRecipientTypeEnum mailType;



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



    private static final long serialVersionUID = 1L;
}
