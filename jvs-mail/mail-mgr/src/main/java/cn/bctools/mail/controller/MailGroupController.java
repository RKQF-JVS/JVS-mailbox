package cn.bctools.mail.controller;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.entity.dto.EmailGroupDto;
import cn.bctools.mail.entity.SysUserExternalGroup;
import cn.bctools.mail.entity.SysUserExternalMail;
import cn.bctools.mail.service.ExternalMailGroupService;
import cn.bctools.mail.service.ExternalMailService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author R
 */
@Slf4j
@RestController
@Api(value = "外部邮箱", tags = "外部邮箱分组接口")
@RequestMapping("mail/group")
public class MailGroupController {

    @Resource
    private ExternalMailGroupService externalMailGroupService;

    @Resource
    private ExternalMailService externalMailService;

    @Log
    @ApiOperation("获取邮件分组信息")
    @GetMapping("/queryGroup")
    public R queryGroup(String configId) {
        String userId = UserCurrentUtils.getUserId();
        LambdaQueryWrapper<SysUserExternalGroup> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUserExternalGroup::getUserId, userId);
        List<SysUserExternalGroup> list = externalMailGroupService.list(queryWrapper);
        return R.ok(list);
    }


    @Log
    @ApiOperation("新增分组或修改分组名称")
    @PostMapping("/saveOrUpdateGroup")
    public R saveOrUpdateGroup(@RequestBody SysUserExternalGroup group) {
        String userId = UserCurrentUtils.getUserId();
        String id = group.getId();
        group.setUserId(userId);
        if (ObjectNull.isNull(id)) {
            int num = externalMailGroupService.count(Wrappers.query(group));
            if (num > 0) {
                return R.failed("当前分组：" + group.getName() + "已存在！");
            }
        }
        externalMailGroupService.saveOrUpdate(group);
        return R.ok();
    }

    @Log
    @ApiOperation("删除分组")
    @GetMapping("/delGroup")
    public R delGroup(String id) {
        String userId = UserCurrentUtils.getUserId();
        LambdaQueryWrapper<SysUserExternalMail> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUserExternalMail::getUserId, userId).eq(SysUserExternalMail::getGroupId, id);
        int count = externalMailService.count(queryWrapper);
        if (count > 0) {
            return R.failed("当前分组下存在用户，请清空后在删除!");
        }
        externalMailGroupService.removeById(id);
        return R.ok();
    }

    @Log
    @ApiOperation("根据分组id获取分组下的邮箱")
    @GetMapping("/getEmailByGroupId")
    public R getEmailByGroupId(String id) {
        String userId = UserCurrentUtils.getUserId();
        LambdaQueryWrapper<SysUserExternalMail> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUserExternalMail::getUserId, userId);
        if (ObjectNull.isNull(id)) {
            queryWrapper.eq(SysUserExternalMail::getGroupId, 0);
        } else {
            queryWrapper.eq(SysUserExternalMail::getGroupId, id);
        }
        List<SysUserExternalMail> list = externalMailService.list(queryWrapper);
        return R.ok(list);
    }

    @Log
    @ApiOperation("给未分组邮箱添加分组")
    @PostMapping("/unGroupAdd")
    public R unGroupAdd(@RequestBody EmailGroupDto dto) {
        String userId = UserCurrentUtils.getUserId();
        List<String> list = dto.getList();
        for (String email : list) {
            externalMailService.update(Wrappers.<SysUserExternalMail>lambdaUpdate()
                    .set(SysUserExternalMail::getGroupId, dto.getId())
                    .eq(SysUserExternalMail::getUserId, userId)
                    .eq(SysUserExternalMail::getEmail, email));
        }
        return R.ok();
    }

}
