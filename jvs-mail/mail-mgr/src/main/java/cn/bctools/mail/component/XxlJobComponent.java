package cn.bctools.mail.component;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.entity.po.BasalPo;
import cn.bctools.mail.entity.XxlJobExecutorLog;
import cn.bctools.mail.service.XxlJobExecutorLogService;
import cn.bctools.xxl.job.api.XxlAdminApi;
import cn.bctools.xxl.job.api.XxlJobInfoDto;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class XxlJobComponent {

    @Resource
    XxlAdminApi xxlAdminApi;

    @Value("${xxl.job.accessToken:qNAMzjEUPoqjaOBgaGMUWQUud2GNoqW7}")
    String token = "qNAMzjEUPoqjaOBgaGMUWQUud2GNoqW7";

    @Resource
    XxlJobExecutorLogService xxlJobExecutorLogService;

    /**
     * 生成一个定时任务
     *
     */
    @SneakyThrows
    public void add() {
        log.info("开始新增定时任务");
        List<XxlJobExecutorLog> list = xxlJobExecutorLogService
                .list(new LambdaQueryWrapper<XxlJobExecutorLog>()
                        .eq(XxlJobExecutorLog::getStatus,Boolean.TRUE)
                        .orderByDesc(BasalPo::getCreateTime));
        if(!list.isEmpty()){
            list.forEach(e -> {
                try {
                    xxlAdminApi.delete(e.getXxlJobId(),token);
                    e.setStatus(Boolean.FALSE);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    log.error("定时任务关闭失败");
                }
            });
            xxlJobExecutorLogService.updateBatchById(list);
        }
        XxlJobInfoDto xxlJobInfoDto = new XxlJobInfoDto();
        xxlJobInfoDto.setExecutorHandler("synchronous-mail-job");
        xxlJobInfoDto.setScheduleConf("0 0 0/1 * * ? ");
        xxlJobInfoDto.setJobDesc("定时拉取邮件");
        xxlJobInfoDto.setAuthor("admin");
        //启动定时任务
        Integer integerR;
        try {
            String applicationContextName = SpringContextUtil.getApplicationContextName();
            integerR = xxlAdminApi.save(xxlJobInfoDto, token, applicationContextName).getData();

            xxlJobExecutorLogService.save(new XxlJobExecutorLog()
                    .setExecutorHandler(xxlJobInfoDto.getExecutorHandler())
                    .setScheduleConf(xxlJobInfoDto.getScheduleConf())
                    .setXxlJobAuthor(xxlJobInfoDto.getAuthor())
                    .setXxlJobDesc(xxlJobInfoDto.getJobDesc())
                    .setXxlJobId(integerR)
                    .setStatus(Boolean.TRUE)
                    .setXxlJobName(applicationContextName));

        } catch (Exception exception) {
            log.error("启动定时任务失败", exception);
            throw new BusinessException("启动定时任务失败");
        }
        log.info("定时任务id:{}",integerR);
    }

}
