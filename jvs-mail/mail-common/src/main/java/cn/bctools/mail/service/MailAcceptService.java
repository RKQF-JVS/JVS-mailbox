package cn.bctools.mail.service;

import cn.bctools.mail.api.dto.UpdateMailStatusDTO;

import java.util.List;

/**
 * @author admin
 * @ClassName: MailAcceptService
 * @Description: 邮件收取服务

 */
public interface MailAcceptService {

    /**
     * 更新全部用户邮件
     *

     **/
    void acceptMailAll();

    /**
     * 更新 当前全部邮件
     *
     * @param id 用户id

     **/
    void acceptMailUserAll(String id);

    /**
     * 更新当前用户
     *
     * @param id 用户id

     **/
    void acceptMailUserUnread(Integer id);

    /**
     * 已读邮件
     *
     * @param messageId 邮件ID 自己库的
     * @return boolean

     **/
    boolean updateMessage(List<UpdateMailStatusDTO> messageId);

    /**
     * 获取当前邮件附件
     * @param id 邮件id （唯一标识）
     */
    boolean getAccessory(String id);
}
