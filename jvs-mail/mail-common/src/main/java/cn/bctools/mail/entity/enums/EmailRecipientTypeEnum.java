package cn.bctools.mail.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author admin
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EmailRecipientTypeEnum implements IEnum<String> {
    /**
     * 抄送
     */
    CC("cc"),
    /**
     * 接收人
     */
    TO("to"),
    /**
     * 密抄
     */
    BCC("bcc");


    @ApiModelProperty("文件类型")
    private final String value;

    EmailRecipientTypeEnum(String value) {
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
    public static EmailRecipientTypeEnum ofCode(String code) {
        for (EmailRecipientTypeEnum value : values()) {
            if (code.equals(value.getValue())) {
                return value;
            }
        }
        return null;
    }
}