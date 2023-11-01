package cn.bctools.mail.service;



import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.mail.entity.SysUserMailConfig;

import java.util.Map;

/**
 * @author admin
 * @ClassName: SysUserMailAServiceImpl
 * @Description: 邮件接收

 */
public interface SysUserMailAcceptService extends IService<SysUserMailConfig> {
    /**
     * Folder 邮件消息
     */
    String FOLDER = "Folder";
    /**
     * Store 数据存储对象
     */
    String STORE = "Store";

    /**
     * 获取邮件接收服务
     *
     * @param id 邮箱配置id
     * @return javax.mail.Folder

     **/
    Map<String, Object> getAccptService(String id);

    /**
     * 获取邮件接收服务
     * @param mailConfig  邮箱配置
     * @return
     */
    Map<String, Object> getAcceptService(SysUserMailConfig mailConfig);

}
