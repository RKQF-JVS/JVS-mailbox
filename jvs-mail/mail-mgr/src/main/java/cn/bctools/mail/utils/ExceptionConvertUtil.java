package cn.bctools.mail.utils;


public class ExceptionConvertUtil {

    public static String convert(String errMsg){
        if(errMsg.contains("550 Mail content denied")){
            return "邮件被拒收";
        }
        return "发送邮件失败,请检查邮箱设置！";
    }
}
