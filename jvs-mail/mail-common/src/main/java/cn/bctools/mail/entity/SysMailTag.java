package cn.bctools.mail.entity;

import cn.bctools.database.entity.po.BasalPo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 邮箱标签
 * </p>
 *
 * @author admin
 * @since 2022-12-01
 */
@Getter
@Setter
@TableName("sys_mail_tag")
@ApiModel(value = "SysMailTag对象", description = "邮箱标签")
public class SysMailTag extends BasalPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("邮箱配置id")
    @TableField("mail_config_id")
    private String mailConfigId;

    @ApiModelProperty("标签名称")
    @TableField("tag_name")
    private String tagName;

    @ApiModelProperty("标签颜色")
    @TableField("tag_color")
    private String tagColor;

}
