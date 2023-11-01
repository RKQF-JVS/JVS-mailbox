package cn.bctools.mail.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author rk
 */
@Data
public class DeptUserDto {
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "上级部门为-1时为顶级部门")
    private Integer parentId;

    @ApiModelProperty(value = "下级部门")
    List<DeptUserDto> childList;

    @ApiModelProperty("用户列表")
    List<UserDeptDto> users;
}
