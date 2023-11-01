package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 邮件扩展 附件
 *
 * @author admin
 */
@ApiModel(value = "邮件扩展 附件")
@Data
@TableName(value = "sys_mail_content")
@Accessors(chain = true)
public class MailContent  {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * sys_mail_user_body ->id
     */
    @TableField(value = "mail_id")
    @ApiModelProperty(value = "sys_mail_user_body ->id")
    private String mailId;

    /**
     * 文本内容
     */
    @TableField(value = "`text`")
    @ApiModelProperty(value = "文本内容")
    private String text;

    /**
     * 文件类型
     */
    @TableField(value = "text_type")
    @ApiModelProperty(value = "文件类型")
    private String textType;



    /**
     * 删除标记
     */
    @TableField(value = "del_flag")
    @ApiModelProperty(value = "删除标记")
    @TableLogic
    private Boolean delFlag;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    @ApiModelProperty(value = "备注")
    private String remarks;

    private static final long serialVersionUID = 1L;
}
