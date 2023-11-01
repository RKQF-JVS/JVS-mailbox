package cn.bctools.mail.entity;

import cn.bctools.database.entity.po.BasalPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * xxljob任务
 * </p>
 *
 * @author admin
 * @since 2023-01-06
 */
@Data
@Accessors(chain = true)
@TableName("xxl_job_executor_log")
@ApiModel(value = "XxlJobExecutorLog对象", description = "xxljob任务")
public class XxlJobExecutorLog extends BasalPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("任务id")
    @TableField("xxl_job_id")
    private Integer xxlJobId;

    @ApiModelProperty("调度方法名称")
    @TableField("executor_handler")
    private String executorHandler;

    @ApiModelProperty("调度周期")
    @TableField("schedule_conf")
    private String scheduleConf;

    @ApiModelProperty("任务描述")
    @TableField("xxl_job_desc")
    private String xxlJobDesc;

    @ApiModelProperty("任务负责人")
    @TableField("xxl_job_author")
    private String xxlJobAuthor;

    @ApiModelProperty("执行器名称")
    @TableField("xxl_job_name")
    private String xxlJobName;

    @ApiModelProperty("状态")
    @TableField("status")
    private Boolean status;

}
