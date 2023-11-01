package cn.bctools.mail.service.impl;

import cn.bctools.mail.entity.MailSensitivity;
import cn.bctools.mail.mapper.MailSensitivityMapper;
import cn.bctools.mail.service.MailSensitivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MailSensitivityServiceImpl  extends ServiceImpl<MailSensitivityMapper, MailSensitivity> implements MailSensitivityService {
}
