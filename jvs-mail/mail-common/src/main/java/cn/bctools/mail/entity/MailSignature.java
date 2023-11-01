package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 个性签名
 *
 * @author R
 */
@Data
@TableName(value = "sys_mail_signature")
@ApiModel("sys_mail_signature")
@Accessors(chain = true)
public class MailSignature  {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 邮箱配置id
     */
    @TableField(value = " config_id")
    @ApiModelProperty(value = "邮箱配置id")
    private String configId;

    @TableField(value = "name")
    @ApiModelProperty(value = "名称")
    private String name;

    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否默认签名 0 否 1 是")
    private Integer isDefault;



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


}
