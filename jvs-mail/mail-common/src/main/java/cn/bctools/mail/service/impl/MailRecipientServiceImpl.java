package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.mail.entity.MailRecipient;
import cn.bctools.mail.mapper.MailRecipientMapper;
import cn.bctools.mail.service.MailRecipientService;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class MailRecipientServiceImpl extends ServiceImpl<MailRecipientMapper, MailRecipient> implements MailRecipientService {

}
