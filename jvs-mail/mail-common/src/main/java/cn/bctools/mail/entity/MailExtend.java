package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 邮件扩展 附件
 *
 * @author admin
 */
@ApiModel(value = "邮件扩展 附件")
@Data
@TableName(value = "sys_mail_extend")
@Accessors(chain = true)
public class MailExtend  {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * sys_mail_user_body ->id
     */
    @TableField(value = "mail_id")
    @ApiModelProperty(value = "sys_mail_user_body ->id")
    private String mailId;


    /**
     * 附件地址
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "附件名称")
    private String name;


    /**
     * 附件地址
     */
    @TableField(value = "url")
    @ApiModelProperty(value = "附件地址")
    private String url;

    @TableField(value = "type")
    @ApiModelProperty(value = "附件类型")
    private Integer type;

    @TableField(value = "cid")
    @ApiModelProperty(value = "内部图片id")
    private String cid;

    /**
     * 附件过期时间
     */
    @TableField(value = "out_time")
    @ApiModelProperty(value = "附件过期时间")
    private LocalDateTime outTime;



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

    @TableField(exist = false)
    private String imgUrl;
}
