package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.mail.entity.SysUserExternalMail;
import cn.bctools.mail.mapper.ExternalMailMapper;
import cn.bctools.mail.service.ExternalMailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author R
 */
@Service
public class ExternalMailServiceImpl extends ServiceImpl<ExternalMailMapper, SysUserExternalMail> implements ExternalMailService {

    @Resource
    private ExternalMailMapper externalMailMapper;

    @Override
    public List<String> queryExternalMailByUserId(String userId) {

        return externalMailMapper.queryExternalMailByUserId(userId);
    }
}
