package cn.bctools.mail.controller;

/**
 * @author R
 */

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.entity.MailSignature;
import cn.bctools.mail.service.MailSignatureService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author R
 */
@Slf4j
@RestController
@Api(value = "邮箱个性签名", tags = "邮箱个性签名")
@RequestMapping("mail/signature")
public class MailSignatureController {

    @Resource
    private MailSignatureService mailSignatureService;

    @Log
    @ApiOperation("添加个性签名")
    @PostMapping("/save")
    public R save(@RequestBody MailSignature mailSignature) {
        String userId = UserCurrentUtils.getUserId();
        mailSignature.setUserId(userId);
        mailSignatureService.save(mailSignature);
        return R.ok();
    }

    @Log
    @ApiOperation("获取个性签名")
    @GetMapping("/getSignature")
    public R getSignature(String configId) {
        String userId = UserCurrentUtils.getUserId();
        List<MailSignature> list = mailSignatureService.list(Wrappers.<MailSignature>lambdaQuery()
                        .eq(MailSignature::getUserId, userId)
//                        .eq(MailSignature::getDelFlag, '0')
//                .eq(MailSignature::getConfigId, configId)
        );
        return R.ok(list);
    }

    @Log
    @ApiOperation("编辑签名")
    @PostMapping("/editSignature")
    public R editSignature(@RequestBody MailSignature mailSignature) {
        mailSignatureService.updateById(mailSignature);
        return R.ok();
    }

    @Log
    @ApiOperation("删除签名")
    @PostMapping("/del")
    public R delSignature(@RequestBody MailSignature mailSignature) {
        mailSignatureService.removeById(mailSignature.getId());
        return R.ok();
    }

    @Log
    @ApiOperation("设置默认签名")
    @GetMapping("/setDefault")
    public R setDefault(String id, String configId) {
        String userId = UserCurrentUtils.getUserId();
        MailSignature byId = mailSignatureService.getById(id);
        Integer one = 1;
        if (ObjectNull.isNotNull(byId)) {
            if (byId.getIsDefault().equals(one)) {
                return new R(200, "成功", null, LocalDateTime.now());
            } else {
                // TODO 默认用户签名为唯一，用户可以拥有多个签名
                MailSignature mailSignature = mailSignatureService.getOne(Wrappers.<MailSignature>lambdaQuery()
                        .eq(MailSignature::getUserId, userId)
                        .eq(MailSignature::getIsDefault, 1)
                );
                if (ObjectNull.isNotNull(mailSignature)) {
                    mailSignature.setIsDefault(0);
                    mailSignatureService.updateById(mailSignature);
                }
                byId.setIsDefault(one);
                mailSignatureService.updateById(byId);
            }
        } else {
            mailSignatureService.update(Wrappers.<MailSignature>lambdaUpdate().set(MailSignature::getIsDefault, '0').eq(MailSignature::getUserId, userId));
        }
        return R.ok();
    }
}
