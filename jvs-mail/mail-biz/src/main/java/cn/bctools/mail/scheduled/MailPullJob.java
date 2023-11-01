//package cn.bctools.mail.scheduled;
//
//import cn.bctools.mail.service.MailAcceptService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author admin
// * @ClassName: AcceptHander
// * @Description: 邮件接收定期器
//
// */
//@Component
//@Slf4j
//public class MailPullJob {
//
//    @Autowired
//    private MailAcceptService mailAcceptService;
//
//    /**
//     * 15分组刷新一次
//     */
//    @Scheduled(fixedDelay = 1000 * 60 *5)
//    public void accept() {
//        log.debug("邮件全局定时刷新接收");
//        mailAcceptService.acceptMailAll();
//    }
//
//}
