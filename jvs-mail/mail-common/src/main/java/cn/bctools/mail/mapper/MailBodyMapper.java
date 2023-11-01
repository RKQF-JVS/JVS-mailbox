package cn.bctools.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.mail.entity.MailBody;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author admin
 */
public interface MailBodyMapper extends BaseMapper<MailBody> {


    /**
     * 获取当前邮件上一封邮件id
     * @param id 当前邮件id
     * @param userId 用户id
     * @param groupId 分组id
     * @param configId 配置id
     * @return
     */
    @Select("select ifnull(min(id),0) downId from sys_mail_body" +
            " where user_id = #{userId} and group_id = #{groupId} and mail_config_id = '#{configId}'" +
            "and id > ${id} and del_flag = '0'" +
            " ORDER BY create_time DESC")
    String queryMailUpId(@Param("id") String id, @Param("userId") String userId, @Param("groupId") String groupId, @Param("configId") String configId);


    /**
     * 获取当前邮件下一封邮件id
     * @param id 当前邮件id
     * @param userId 用户id
     * @param groupId 分组id
     * @param configId 配置id
     * @return
     */
    @Select("select ifnull(max(id),0) upId from sys_mail_body " +
            "where user_id = #{userId} and group_id = #{groupId} and mail_config_id = '#{configId}'" +
            " and id < ${id} and del_flag = '0' " +
            " ORDER BY create_time DESC")
    String queryMailDownId(@Param("id") String id, @Param("userId") String userId, @Param("groupId") String groupId, @Param("configId") String configId);
}