package cn.bctools.mail.api.api;


import cn.bctools.common.utils.R;
import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author R
 */
@FeignClient(value = "mail-biz", contextId = "accept")
public interface MailAcceptApi {

    @GetMapping("mail/accept/{id}/all")
    @ApiOperation("邮箱接收服务")
    R<Void> mailAccept(@PathVariable("id") String id);

    @GetMapping("mail/accept/{id}/getAccessory")
    @ApiOperation("邮箱接收服务")
    R getAccessory(@PathVariable("id") String id);

    @GetMapping("mail/accept/{id}/user/unread/refresh")
    @ApiOperation("邮箱刷新当前用户未读邮件")
    R<Void> mailAcceptUser(@PathVariable("id") Integer id);

    /*****
     // a new flags object 只能添加一个，取第一个
     Flags f1 = new Flags();
     //删除标记
     f1.add(Flags.Flag.DELETED);
     //已读标记
     f1.add(Flags.Flag.SEEN);
     //草稿箱标记
     f1.add(Flags.Flag.DRAFT);
     //已答复
     f1.add(Flags.Flag.ANSWERED);
     //修改状态
     MailAcceptApi.updateMessage(update);
     ****/
    @PutMapping("mail/accept/user/unread/refresh")
    @ApiOperation("更改邮件状态")
    R<Boolean> updateMessage(@RequestBody List<UpdateMailStatusDTO> update);
}
