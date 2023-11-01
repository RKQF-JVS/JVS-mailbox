package cn.bctools.mail.entity.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author R
 */
@Data
public class MailDelDto {

    /**
     * 邮件id集
     */
    private String mailIds;

    /**
     * 0 未删  1 已删除 2彻底删除
     */
    @TableLogic
    private String delFlag;

    /**
     * 邮箱配置id
     */
    private String configId;
}
