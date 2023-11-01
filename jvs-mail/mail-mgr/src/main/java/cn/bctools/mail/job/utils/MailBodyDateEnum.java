package cn.bctools.mail.job.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailBodyDateEnum {

    本月("0"),
    上月("1"),
    更早("2")
    ;
    private final String code;
}
