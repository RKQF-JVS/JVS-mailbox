package cn.bctools.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.mail.entity.SysUserExternalMail;

import java.util.List;

/**
 * @author R
 */
public interface ExternalMailService extends IService<SysUserExternalMail> {

    /**
     * 获取当前用户外部邮箱
     * @param userId
     * @return
     */
    List<String> queryExternalMailByUserId(String userId);


}
