package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.mail.entity.MailContent;
import cn.bctools.mail.mapper.MailContentMapper;
import cn.bctools.mail.service.MailContentService;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class MailContentServiceImpl extends ServiceImpl<MailContentMapper, MailContent> implements MailContentService {

}
