package cn.bctools.mail.api.api;

import cn.bctools.mail.api.vo.SysMailConfigVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author R
 */
@FeignClient(value = "mail-biz",contextId = "config")
public interface MailConfigApi {

    @GetMapping("mail/config/{id}")
    @ApiOperation("获取当前用户邮件配置")
    List<SysMailConfigVO> getMail(@PathVariable("id") @ApiParam(value = "用户id", name = "id", required = true) String id);
}
