package cn.bctools.mail.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author admin
 * @ClassName: MultipartFilesDTO
 * @Description: 邮件附件对象

 */
@Data
@Accessors(chain = true)
@ApiModel(value = "邮件附件对象", description = "邮件附件对象")
public class MultipartFilesDTO implements Serializable {
    @ApiModelProperty(value = "请求地址")
    private String url;
    @ApiModelProperty(value = "附件名称")
    private String name;

    /**
     * 0 附件 1 内容图片
     */
    private Integer fileType;
}
