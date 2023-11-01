package cn.bctools.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@TableName(value = "sys_mail_sensitivity")
@Accessors(chain = true)
@ApiModel("敏感词")
public class MailSensitivity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 敏感词
     */
    @TableField("sensitive_words")
    private String sensitiveWords;

}
