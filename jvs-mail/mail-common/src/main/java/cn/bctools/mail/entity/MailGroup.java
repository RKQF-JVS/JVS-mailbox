package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;
import cn.bctools.mail.entity.enums.EmailGroupTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 邮箱分组
 *
 * @author admin
 */
@ApiModel(value = "邮箱分组")
@Data
@TableName(value = "sys_mail_group")
public class MailGroup{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 分组名字
     */
    @TableField(value = "group_name")
    @ApiModelProperty(value = "分组名字")
    private String groupName;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 邮箱配置id
     */
    @TableField(value = " config_id")
    @ApiModelProperty(value = "邮箱配置id")
    private String configId;

    /**
     * 'default', 默认 不可更改
     * 'yourself' 自定义
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "'default', 默认 不可更改,'yourself' 自定义")
    private EmailGroupTypeEnum type;

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
