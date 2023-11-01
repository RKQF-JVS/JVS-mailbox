package cn.bctools.mail;

import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import com.xxl.job.core.config.annotation.EnableXxlJobExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author Administrator
 */
@EnableJvsMgrResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@RefreshScope
@EnableXxlJobExecutor
public class MailMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailMgrApplication.class, args);
    }
}
