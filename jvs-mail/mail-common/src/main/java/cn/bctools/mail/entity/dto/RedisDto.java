package cn.bctools.mail.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author R
 */
@Data
@Accessors(chain = true)
public class RedisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱配置id
     */
    private String id;

    /**
     * 邮件id
     */
    private String mailId;

    /**
     * 用户id
     */
    private String userId;

    private String phone;
    /**
     * 事件
     */
    private String event;

    private Integer total;

    private Integer count;

    private Object obj;
}
