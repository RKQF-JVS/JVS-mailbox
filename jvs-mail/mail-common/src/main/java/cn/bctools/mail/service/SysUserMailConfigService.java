package cn.bctools.mail.service;


import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.mail.entity.dto.EmailUserDto;
import cn.bctools.mail.entity.SysUserMailConfig;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author admin
 */
public interface SysUserMailConfigService extends IService<SysUserMailConfig> {

    /**
     * 保存邮件配置
     * @param emailUserDTO
     * @param id
     */
    void saveMailConfig(EmailUserDto emailUserDTO, String id);

    /**
     * 根据用户ID获取邮件服务
     *
     * @param id       用户ID
     * @return org.springframework.mail.javamail.JavaMailSender

     **/
    JavaMailSender getMailServiceBasedOnUserId(String id);


}
