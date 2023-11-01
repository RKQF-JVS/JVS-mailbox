package cn.bctools.mail.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author admin
 * @ClassName: UpdateMailStatusDTO
 * @Description: 修改邮件状态

 */
@Data
@Accessors(chain = true)
@ApiModel(value = "修改邮件状态", description = "修改邮件状态")
public class UpdateMailStatusDTO {
    /**
     * 邮件回复标记，标识邮件是否已回复。
     */
    public final static int ANSWERED_BIT 	= 0x01;
    /**
     * 邮件删除标记，标识邮件是否需要删除。
     */
    public final static int DELETED_BIT 	= 0x02;
    /**
     * 草稿邮件标记，标识邮件是否为草稿。
     */
    public final static int DRAFT_BIT 		= 0x04;
    /**
     * 表示邮件是否为回收站中的邮件。
     */
    public final static int FLAGGED_BIT 	= 0x08;
    /**
     * 新邮件标记，表示邮件是否为新邮件
     */
    public final static int RECENT_BIT		= 0x10;
    /**
     * 邮件阅读标记，标识邮件是否已被阅读。
     */
    public final static int SEEN_BIT		= 0x20;
    /**
     * 底层系统是否支持用户自定义标记，应用程序只能检索这个属性，而不能设置这个属性。
     */
    public final static int USER_BIT		= 0x80000000;

    @ApiModelProperty(value = "邮件id 全局ID,非数据邮件ID")
    private String mailId;
    @ApiModelProperty(value = "邮件状态")
    private String flags;
    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "邮箱配置id")
    private String configId;
}
