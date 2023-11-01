package cn.bctools.mail.service;


import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import cn.bctools.mail.component.FolderMessageFuntion;
import cn.bctools.mail.entity.dto.UpdateMailDto;
import cn.bctools.mail.entity.MailBody;

import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
public interface MailBodyService extends IService<MailBody> {

    /**
     * 修改邮件状态
     *
     * @param mailIds
     * @param mailBody
     */
    void updateMailBody(String mailIds, MailBody mailBody);


    /**
     * 邮件详情 返回前后邮件id 如果不存在返回0\
     *
     * @param id       当前邮件id
     * @param userId   用户id
     * @param groupId  分组id
     * @param configId 配置id
     * @return K：“upId” 上一份邮件id
     * K：“downId” 下一份邮件id
     */
    Map<String, String> queryAroundMailBodyId(String id, String userId, String groupId, String configId);

    /**
     * 获取当前邮件上一封邮件id
     *
     * @param id       当前邮件id
     * @param userId   用户id
     * @param groupId  分组id
     * @param configId 配置id
     * @return （获取邮件详情废弃）
     */
    String queryMailUpId(String id, String userId, String groupId, String configId);

    /**
     * 获取当前邮件下一封邮件id
     *
     * @param id       当前邮件id
     * @param userId   用户id
     * @param groupId  分组id
     * @param configId 配置id
     * @return （获取邮件详情废弃）
     */
    String queryMailDownId(String id, String userId, String groupId, String configId);

    /**
     * 修改邮件内容
     *
     * @param updateMailDTO 邮件内容
     * @return boolean
     **/
    boolean updateMail(UpdateMailDto updateMailDTO);

    /**
     * 保存邮件
     *
     * @param copy
     * @return
     */
    MailBody saveMailBody(MailBody copy);


    /**
     * 保存邮件
     *
     * @param funtion 回调函数
     * @param userId  用户id
     * @return boolean
     **/
    boolean save(FolderMessageFuntion funtion, String userId);

    /**
     * 已读邮件
     *
     * @param messageId 消息ID
     * @return boolean
     **/
    boolean updateMessage(List<UpdateMailStatusDTO> messageId);

    /**
     * 保存邮件
     *
     * @param messages 邮件内容
     * @return boolean
     **/
    boolean save(Message[] messages, Integer userId);

    /**
     * 保存邮件
     *
     * @param id 用户id
     * @return boolean
     **/
    boolean save(String id);

    /**
     * 获取邮件附件
     *
     * @param id
     */
    boolean getAccessory(String id);


}
