package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author R
 */
@ApiModel("外部用户分组")
@Data
@TableName(value = "sys_user_external_group")
@Accessors(chain = true)
public class SysUserExternalGroup  {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private String userId;

    /**
     * 邮箱配置id
     */
    @TableField(value = " config_id")
    @ApiModelProperty(value="邮箱配置id")
    private String configId;

    /**
     * 分组名字
     */
    @TableField(value = "name")
    @ApiModelProperty(value="分组名字")
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
