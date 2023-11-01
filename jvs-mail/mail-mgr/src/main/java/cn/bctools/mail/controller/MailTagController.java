package cn.bctools.mail.controller;

import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.entity.MailBody;
import cn.bctools.mail.entity.SysMailTag;
import cn.bctools.mail.service.MailBodyService;
import cn.bctools.mail.service.SysMailTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "邮箱标签")
@RestController
@RequestMapping("/mail/tag")
@AllArgsConstructor
public class MailTagController {

    private final SysMailTagService sysMailTagService;
    private final MailBodyService mailBodyService;

    @Log
    @ApiOperation("新增标签")
    @PostMapping("/save")
    public R<SysMailTag> add(@RequestBody SysMailTag tag){
        sysMailTagService.save(tag);
        return R.ok(tag);
    }

    @Log
    @ApiOperation("编辑标签")
    @PutMapping("/edit")
    public R<SysMailTag> edit(@RequestBody SysMailTag tag){
        sysMailTagService.updateById(tag);
        return R.ok(tag);
    }

    @Log
    @ApiOperation("查询当前邮箱下的所有标签")
    @GetMapping("/all/{configId}")
    public R<List<SysMailTag>> getAll(@ApiParam("邮箱配置id")@PathVariable("configId")String configId){
        List<SysMailTag> list = sysMailTagService.list(new LambdaQueryWrapper<SysMailTag>().eq(SysMailTag::getMailConfigId, configId));
        return R.ok(list);
    }

    @Log
    @ApiOperation("删除标签")
    @DeleteMapping("/del/{id}")
    public R<Boolean> del(@ApiParam("标签id")@PathVariable("id")String tagId){
        return R.ok(sysMailTagService.removeById(tagId));
    }

    @Log
    @ApiOperation("邮件绑定标签")
    @PutMapping("/binding/{mailId}/{tagId}")
    public R<Boolean> bindingTag(@ApiParam("邮件id")@PathVariable("mailId")String mailId,@ApiParam("标签id")@PathVariable("tagId")String tagId){
        boolean b = mailBodyService.updateById(new MailBody().setId(mailId).setMailTagId(tagId));
        return R.ok(b);
    }

    @Log
    @ApiOperation("邮件解绑标签")
    @PutMapping("/unbind/{mailId}")
    public R<Boolean> unbindTag(@ApiParam("邮件id")@PathVariable("mailId")String mailId){
        boolean b = mailBodyService.update(new LambdaUpdateWrapper<MailBody>().eq(MailBody::getId,mailId).set(MailBody::getMailTagId,null));
        return R.ok(b);
    }

}
