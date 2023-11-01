package cn.bctools.mail.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.mail.api.api.MailConfigApi;
import cn.bctools.mail.api.vo.SysMailConfigVO;
import cn.bctools.mail.entity.dto.EmailUserDto;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.service.SysUserMailConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @ClassName: MailConfigController
 * @Description: 邮箱配置接口
 */
@RestController
@RequestMapping("mail/config")
@Api(value = "邮箱配置", tags = "邮箱配置接口")
@Slf4j
@AllArgsConstructor
public class MailConfigController  implements MailConfigApi {

    private final SysUserMailConfigService sysUserMailConfigService;

    @Override
    @ApiOperation("获取当前用户邮件配置")
    @GetMapping("/{id}")
    public List<SysMailConfigVO> getMail(@PathVariable("id") @ApiParam(value = "用户id", name = "id", required = true) String id) {
        List<SysUserMailConfig> list = sysUserMailConfigService.list(Wrappers.<SysUserMailConfig>lambdaQuery()
                        .eq(SysUserMailConfig::getSysUserId, id)
//                .eq(SysUserMailConfig::getDelFlag, '0')
        );
        List<SysMailConfigVO> copy = new ArrayList<>();
        list.forEach(e -> copy.add(BeanCopyUtil.copy(e, SysMailConfigVO.class).setMailTypes(e.getMailType())));
        return copy;
    }

    @ApiOperation("保存当前用户邮件配置")
    @PostMapping("/{id}")
    public boolean save(@Validated @RequestBody EmailUserDto emailUserDTO, @PathVariable("id") @ApiParam(value = "用户id", name = "id", required = true) String id) {
        try {
            sysUserMailConfigService.saveMailConfig(emailUserDTO, id);
        } catch (Exception e) {
            log.error("保存用户配置失败");
            return false;
        }
        return true;
    }
}
