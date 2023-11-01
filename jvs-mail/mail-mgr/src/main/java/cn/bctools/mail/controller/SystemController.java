package cn.bctools.mail.controller;

import cn.bctools.auth.api.dto.SearchUserDto;
import cn.bctools.auth.api.enums.UserQueryType;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.mail.entity.SysUserExternalMail;
import cn.bctools.mail.utils.SystemTool;
import cn.bctools.mail.service.ExternalMailService;
import cn.bctools.mail.entity.vo.MailQueryVO;
import cn.bctools.oauth2.utils.AuthorityManagementUtils;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author R
 */
@Slf4j
@RestController
@RequestMapping("system")
@Api(tags = "系统管理")
public class SystemController {

    final String MOBLIECODE = "MOBLIECODE";

    @Resource
    private ExternalMailService externalMailService;
    @Resource
    RedisUtils redisUtils;


    /**
     * 查找人
     *
     * @param name
     * @return
     */
    @ApiOperation("模糊查找人")
    @GetMapping("/queryExecutor")
    private R queryExecutor(String name) {
        boolean aNull = ObjectNull.isNull(name);
        List<UserDto> userDtos = AuthorityManagementUtils.userSearch(new SearchUserDto()
                .setType(aNull ? UserQueryType.all : UserQueryType.or)
                .setRealName(name)
                .setPhone(name));
        return R.ok(userDtos);
    }

    @ApiOperation("模糊查询邮件")
    @GetMapping("/queryEmail")
    public R queryEmail(String search) {
        if (ObjectNull.isNull(search)) {
            return R.ok();
        }
        //比对公司内部邮箱
        List<UserDto> userDtoList = AuthorityManagementUtils.userSearch(new SearchUserDto()
                .setType(UserQueryType.or)
                .setEmail(search)
                .setRealName(search));

        userDtoList = userDtoList.stream().filter(e -> StrUtil.contains(e.getRealName(),search) || StrUtil.contains(e.getEmail(),search)).collect(Collectors.toList());
        List<MailQueryVO> ls = new ArrayList();
        for (UserDto dto : userDtoList) {
            MailQueryVO vo = new MailQueryVO();
            if (ObjectNull.isNotNull(dto.getEmail())) {
                vo.setEmail(dto.getEmail());
                vo.setName(ObjectNull.isNull(dto.getRealName()) ? "" : dto.getRealName());
                ls.add(vo);
            }
        }
        //获取公司外部邮箱
        String userId = UserCurrentUtils.getUserId();
        LambdaQueryWrapper<SysUserExternalMail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserExternalMail::getUserId, userId);
        queryWrapper.likeRight(StringUtils.isNotEmpty(search), SysUserExternalMail::getEmail, search);
        List<SysUserExternalMail> list = externalMailService.list(queryWrapper);
        for (SysUserExternalMail mail : list) {
            MailQueryVO vo = new MailQueryVO();
            vo.setEmail(ObjectNull.isNull(mail.getEmail()) ? "" : mail.getEmail());
            vo.setName("");
            ls.add(vo);
        }
        return R.ok(ls);
    }


    @ApiOperation("获取用户信息")
    @GetMapping("/getUser")
    public R getUser() {
        UserDto sysUser = UserCurrentUtils.getCurrentUser();
        return R.ok(sysUser);
    }

    @ApiOperation("生成socket token 1PC/2APP")
    @GetMapping("/socket/{type}")
    public R socket(@PathVariable("type") Integer type, String phoneCode) {
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        String tenantId = currentUser.getTenantId();
        log.info("手机唯一标识为：{}", phoneCode);
        String loginType = "APP_";
        switch (type) {
            case 1:
                loginType = "PC_";
            default:
                break;
        }
        String phone = currentUser.getPhone();
        Integer two = 2;
        if (type.equals(two)) {
            redisUtils.set(MOBLIECODE + phone, phoneCode);
            log.info("极光推送标识存入Redis。唯一标识为：{}", phoneCode);
        }
        String phoneKey = SysConstant.redisKey("token", loginType + phone + "_" + tenantId);
        boolean exists = redisUtils.exists(phoneKey);
        if (exists) {
            Object o = redisUtils.get(phoneKey);
            //推送挤下登录提示
            String pc = "PC_";
            if (loginType.equals(pc)) {
                SystemTool.pushMsg(SystemTool.PC_USER + currentUser.getId() + "_" + currentUser.getTenantId());
            } else {
                SystemTool.pushMsg(SystemTool.MOBLIE_USER + currentUser.getId() + "_" + currentUser.getTenantId());
            }
            redisUtils.del(o + "");
            redisUtils.del(phoneKey);
        }
        String key = loginType + IdUtil.fastSimpleUUID();
        String token = SysConstant.redisKey("token", key);
        redisUtils.set(phoneKey, token);
        redisUtils.set(token, currentUser);
        HashMap<Object, Object> map = new HashMap<>(1);
        map.put("token", key);
        return R.ok(map);

    }


}
