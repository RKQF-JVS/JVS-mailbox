

package cn.bctools.mail.controller;

import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.entity.dto.DeptUserDto;
import cn.bctools.mail.entity.dto.SystemDeptDto;
import cn.bctools.mail.entity.dto.UserDeptDto;
import cn.bctools.oauth2.utils.AuthorityManagementUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author guojing
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Api(value = "dept", tags = "部门管理模块")
public class DeptController {

    private final AuthUserServiceApi userServiceApi;

    /**
     * 返回树形菜单集合
     *
     * @return 树形菜单
     */
    @Log
    @ApiOperation("查询部门树形菜单")
    @GetMapping(value = "/tree")
    public R getTree() {
        List<SysDeptDto> deptTree = AuthorityManagementUtils.getDeptTree();
        List<SystemDeptDto> copys = BeanCopyUtil.copys(deptTree, SystemDeptDto.class);
        setTotalPeople(copys);
        return R.ok(copys);
    }

    /**
     * 统计部门总人数
     *
     * @param copys
     */
    private void setTotalPeople(List<SystemDeptDto> copys) {
        for (SystemDeptDto copy : copys) {
            String id = copy.getId();
            long count = userServiceApi.getByDeptId(id).getData().size();
            copy.setTotalPeople(count);
            List<SystemDeptDto> childList = copy.getChildList();
            if (CollectionUtil.isNotEmpty(childList)) {
                setTotalPeople(childList);
            }
        }
    }

    @Log
    @ApiOperation("查询部门树形菜单")
    @GetMapping(value = "/tree/includeUsers")
    public R getTreeIncludeUsers() {
        List<SysDeptDto> deptTree = AuthorityManagementUtils.getDeptTree();
        List<DeptUserDto> deptUserDtos = BeanCopyUtil.copys(deptTree, DeptUserDto.class);
        putUsers(deptUserDtos);
        return R.ok(deptUserDtos);
    }

    public void putUsers(List<DeptUserDto> deptTree) {
        deptTree.forEach(e -> {
            List<UserDeptDto> userDeptDtos = Optional.ofNullable(AuthorityManagementUtils.getUsersByDeptId(e.getId())).orElse(Collections.emptyList())
                    .stream().map(x -> new UserDeptDto().setDeptId(x.getDeptId()).setUserId(x.getId()).setRealName(x.getRealName()))
                    .collect(Collectors.toList());
            e.setUsers(userDeptDtos);
            if (ObjectUtil.isNotEmpty(e.getChildList())) {
                putUsers(e.getChildList());
            }
        });
    }

    @Log
    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    public R getUserPage(Page page, UserDto userDTO) {
        List<UserDto> usersByDeptId = AuthorityManagementUtils.getUsersByDeptId(userDTO.getDeptId());
        page.setRecords(usersByDeptId);
        return R.ok(page);
    }

    /**
     * 根据部门id 获取用户信息
     *
     * @param userDTO
     * @return
     */
    @Log
    @ApiOperation("根据部门id获取用户信息")
    @PostMapping("/getUserByDeptId")
    public R getUserByDeptId(@RequestBody UserDto userDTO) {
        List<UserDto> usersByDeptId = AuthorityManagementUtils.getUsersByDeptId(userDTO.getDeptId());
        return R.ok(usersByDeptId);
    }

}
