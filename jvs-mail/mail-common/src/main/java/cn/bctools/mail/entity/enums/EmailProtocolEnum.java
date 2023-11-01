package cn.bctools.mail.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author admin
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EmailProtocolEnum implements IEnum<String> {
    /**
     * 邮件发送协议
     */
    SMTP("smtp"),
    /**
     * 邮件接收协议
     */
    IMAP("imap"),

    /**
     * 邮件接收协议
     */
    POP3("pop3"),

    /**
     * 邮件接收协议
     */
    POP("pop3");

    @ApiModelProperty("邮件协议")
    private final String value;

    EmailProtocolEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static EmailProtocolEnum ofCode(String code) {
        for (EmailProtocolEnum value : values()) {
            if (code.equals(value.getValue())) {
                return value;
            }
        }
        return null;
    }
}
