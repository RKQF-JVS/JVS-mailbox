package cn.bctools.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.mail.entity.SysUserExternalGroup;
import cn.bctools.mail.mapper.ExternalMailGroupMapper;
import cn.bctools.mail.service.ExternalMailGroupService;
import org.springframework.stereotype.Service;

@Service
public class ExternalMailGroupServiceImpl extends ServiceImpl<ExternalMailGroupMapper, SysUserExternalGroup> implements ExternalMailGroupService {
}
