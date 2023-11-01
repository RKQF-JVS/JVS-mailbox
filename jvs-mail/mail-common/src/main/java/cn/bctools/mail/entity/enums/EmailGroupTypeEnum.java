package cn.bctools.mail.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author admin
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EmailGroupTypeEnum implements IEnum<String> {
    /**
     * 默认
     */
    Default("default"),
    /**
     * 自定义
     */
    YOUR_SELF("yourself");


    @ApiModelProperty("文件类型")
    private final String value;

    EmailGroupTypeEnum(String value) {
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
    public static EmailGroupTypeEnum ofCode(String code) {
        for (EmailGroupTypeEnum value : values()) {
            if (code.equals(value.getValue())) {
                return value;
            }
        }
        return null;
    }
}