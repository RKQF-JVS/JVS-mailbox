package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.mail.entity.MailSignature;
import cn.bctools.mail.mapper.MailSignatureMapper;
import cn.bctools.mail.service.MailSignatureService;
import org.springframework.stereotype.Service;

/**
 * @author R
 */
@Service
public class MailSignatureServiceImpl extends ServiceImpl<MailSignatureMapper, MailSignature> implements MailSignatureService {

}
