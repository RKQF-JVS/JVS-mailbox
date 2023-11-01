package cn.bctools.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.mail.entity.SysUserExternalMail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author R
 */
public interface ExternalMailMapper extends BaseMapper<SysUserExternalMail> {

    /**
     * 获取当前用户 所有外部邮箱
     *
     * @param userId
     * @return
     */
    @Select("select email from sys_user_external_mail where user_id = #{userId} and del_flag = '0'")
    List<String> queryExternalMailByUserId(@Param("userId") String userId);


}
