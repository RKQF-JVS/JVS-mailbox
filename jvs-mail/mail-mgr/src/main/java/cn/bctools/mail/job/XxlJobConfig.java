package cn.bctools.mail.job;

import cn.bctools.mail.component.XxlJobComponent;
import cn.bctools.mail.job.utils.MailUtils;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class XxlJobConfig implements ApplicationRunner {

    XxlJobComponent xxlJobComponent;
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化 xxljob 执行器");
        xxlJobComponent.add();
        log.info("初始化完成");


        //清除redis中 残留的key
        Set<String> keys = redisTemplate.keys(MailUtils.PULLING_KEY+"*");
        if(CollectionUtil.isNotEmpty(keys)){
            redisTemplate.delete(keys);
        }
    }
}
