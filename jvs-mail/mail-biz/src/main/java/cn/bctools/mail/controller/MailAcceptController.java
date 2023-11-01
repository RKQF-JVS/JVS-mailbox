package cn.bctools.mail.controller;


import cn.bctools.common.utils.R;
import cn.bctools.mail.api.api.MailAcceptApi;
import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import cn.bctools.mail.service.MailAcceptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author admin
 * @ClassName: MailSendController
 * @Description: 邮件发送
 */
@RestController
@RequestMapping("mail/accept")
@Api(value = "邮箱接收", tags = "邮箱接收接口")
@Slf4j
@AllArgsConstructor
public class MailAcceptController implements MailAcceptApi {

    private final MailAcceptService mailAcceptService;

    /**
     * 收取邮件
     *
     * @param id 邮箱配置id
     * @return
     */
    @Override
    @ApiOperation("收取前3天所有邮件")
    @SneakyThrows
    @GetMapping("/{id}/all")
    public R<Void> mailAccept(@PathVariable("id") String id) {
        mailAcceptService.acceptMailUserAll(id);
        return R.ok();
    }

    @Override
    @ApiOperation("收取邮件附件")
    @GetMapping("/{id}/getAccessory")
    public R<Boolean> getAccessory(@PathVariable("id") String id) {
        boolean accessory = mailAcceptService.getAccessory(id);
        return new R(accessory);
    }

    @Override
    @GetMapping("/{id}/user/unread/refresh")
    @ApiOperation("邮箱刷新当前用户未读邮件")
    @SneakyThrows
    public R<Void> mailAcceptUser(@PathVariable("id") Integer id) {
        mailAcceptService.acceptMailUserUnread(id);
        return R.ok();
    }

    @Override
    @PutMapping("/user/unread/refresh")
    @ApiOperation("更改邮件状态")
    @SneakyThrows
    public R<Boolean> updateMessage(@RequestBody List<UpdateMailStatusDTO> id) {
        return R.ok(mailAcceptService.updateMessage(id));
    }
}
