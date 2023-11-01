package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author R
 */
@Data
@TableName(value = "sys_user_external_mail")
@Accessors(chain = true)
@ApiModel("外部邮箱")
public class SysUserExternalMail  {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 分组id
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="分组id")
    private String groupId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private String userId;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    @ApiModelProperty(value="邮箱")
    private String email;

    /**
     * 用户名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value="用户名称")
    private String name;



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

}
