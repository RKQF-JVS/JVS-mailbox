package cn.bctools.mail.api.api;


import cn.bctools.common.utils.R;
import cn.bctools.mail.api.dto.MailSendDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author R
 */
@FeignClient(value = "mail-biz", contextId = "send")
public interface MailSendApi {

    @PostMapping("mail/send")
    @ApiOperation("邮箱发送服务")
    R<Void> mailSendAttach(@RequestBody MailSendDTO send);


    @PostMapping("mail/save")
    @ApiOperation("邮箱保存服务")
    R<Void> saveEmail(@Validated @RequestBody MailSendDTO send);
}
