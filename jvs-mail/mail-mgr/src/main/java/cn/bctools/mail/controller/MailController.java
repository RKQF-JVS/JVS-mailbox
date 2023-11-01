package cn.bctools.mail.controller;

import cn.bctools.auth.api.dto.SearchUserDto;
import cn.bctools.auth.api.enums.UserQueryType;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.mail.api.api.MailAcceptApi;
import cn.bctools.mail.api.api.MailSendApi;
import cn.bctools.mail.api.dto.MailRecipientDTO;
import cn.bctools.mail.api.dto.MailSendDTO;
import cn.bctools.mail.api.dto.MultipartFilesDTO;
import cn.bctools.mail.api.dto.UpdateMailStatusDTO;
import cn.bctools.mail.entity.dto.*;
import cn.bctools.mail.entity.*;
import cn.bctools.mail.entity.enums.EmailGroupTypeEnum;
import cn.bctools.mail.entity.enums.EmailRecipientTypeEnum;
import cn.bctools.mail.entity.enums.MailDefaultGroupEnum;
import cn.bctools.mail.job.utils.MailBodyDateEnum;
import cn.bctools.mail.job.utils.MailUtils;
import cn.bctools.mail.utils.HtmlToTextUtil;
import cn.bctools.mail.service.*;
import cn.bctools.mail.entity.vo.MailConfigVo;
import cn.bctools.oauth2.utils.AuthorityManagementUtils;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.Transport;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author R
 */
@Slf4j
@RestController
@Api(value = "邮箱功能", tags = "邮箱功能接口")
@RequestMapping("mail/business")
public class MailController {

    @Resource
    private MailGroupService mailGroupService;

    @Resource
    private MailBodyService mailBodyService;

    @Resource
    private MailContentService mailContentService;

    @Resource
    private MailExtendService mailExtendService;

    @Resource
    private MailRecipientService mailRecipientService;

    @Resource
    private SysUserMailConfigService sysUserMailConfigService;

    @Resource
    private ExternalMailService externalMailService;

    @Resource
    private SysMailTagService sysMailTagService;

    @Resource
    MailSendApi mailSendApi;

    @Resource
    MailAcceptApi mailAcceptApi;

    @Resource
    MailUtils mailUtils;

    final Integer two = 2;
    final Integer three = 3;

    @Log
    @ApiOperation("获取邮件分组信息")
    @GetMapping("/queryGroup")
    public R queryGroup(String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        LambdaQueryWrapper<MailGroup> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(MailGroup::getConfigId, configId);
        queryWrapper.eq(MailGroup::getUserId, userId).eq(MailGroup::getType, EmailGroupTypeEnum.YOUR_SELF);
        List<MailGroup> list = mailGroupService.list(queryWrapper);
        return R.ok(list);
    }


    @Log
    @ApiOperation("新增分组")
    @GetMapping("/addGroup")
    public R addGroup(String name, String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        MailGroup group = new MailGroup();
        group.setGroupName(name);
        group.setConfigId(configId);
        group.setUserId(userId);
        group.setType(EmailGroupTypeEnum.YOUR_SELF);
        int count = mailGroupService.count(Wrappers.query(group));
        if (count > 0) {
            return R.failed("当前分组：" + name + "已存在！");
        }
        mailGroupService.save(group);
        return R.ok();
    }


    @Log
    @ApiOperation("邮件分组重命名")
    @PutMapping("/group/rename")
    public R renameGroup(@RequestBody MailGroup dto) {
        int count = mailGroupService.count(new LambdaQueryWrapper<MailGroup>()
                .eq(MailGroup::getConfigId,dto.getConfigId())
                .eq(MailGroup::getGroupName,dto.getGroupName()));
        if (count > 0) {
            return R.failed("当前分组：" + dto.getGroupName() + "已存在！");
        }
        mailGroupService.update(new LambdaUpdateWrapper<MailGroup>().eq(MailGroup::getId,dto.getId()).set(MailGroup::getGroupName,dto.getGroupName()));
        return R.ok();
    }

    /**
     * 删除邮箱分组
     *
     * @param id       分组id
     * @param configId 邮箱配置id
     * @return 删除结果
     */
    @Log
    @ApiOperation("删除分组")
    @GetMapping("/delGroup")
    public R delGroup(String id, String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(MailBody::getUserId, userId)
                .eq(MailBody::getGroupId, id)
                .eq(MailBody::getMailConfigId, configId)
                .eq(MailBody::getDelCompletely,Boolean.FALSE);
        int count = mailBodyService.count(queryWrapper);
        if (count > 0) {
            return R.failed("当前文件夹下存在邮件，请清空后在删除！");
        }
        mailGroupService.removeById(id);
        return R.ok();
    }

    /**
     * 获取邮件列表
     *
     * @param page 分页参数
     * @param dto  查询参数实体
     * @return 邮件列表
     */
    @Log
    @ApiOperation("PC获取邮件")
    @GetMapping("/getMail")
    public R getMail(Page page, MailDto dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        String groupId = dto.getGroupId();
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailBody::getMailConfigId, dto.getConfigId());
        queryWrapper.eq(MailBody::getGroupId, groupId);
        queryWrapper.eq(MailBody::getUserId, userId);
        queryWrapper.eq(MailBody::getDelCompletely,Boolean.FALSE);
        if (ObjectNull.isNotNull(dto.getSubject())) {
            queryWrapper.like(MailBody::getSubject, dto.getSubject());
        }
        if (ObjectNull.isNotNull(dto.getStress())) {
            queryWrapper.eq(MailBody::getStress, dto.getStress());
        }
        queryWrapper.orderByDesc(MailBody::getSentDate);
        //查询当前邮箱标签
        List<SysMailTag> tags = sysMailTagService.list(new LambdaQueryWrapper<SysMailTag>().eq(SysMailTag::getMailConfigId, dto.getConfigId()));
        List<MailBody> mailBodyList = this.resetSort(mailBodyService.list(queryWrapper));
        //设置发件人/接收人
        saveAccepter(groupId, mailBodyList);
        //设置标签
        setMailTag(tags,mailBodyList);
        Date now = new Date();
        List<MailBody> thisMonth = new ArrayList<>();
        List<MailBody> lastMonth = new ArrayList<>();
        List<MailBody> earlier = new ArrayList<>();
        long unReadCount = mailBodyList.stream().filter(e -> !e.getRend()).count();
        for (MailBody mailBody : mailBodyList) {
            LocalDateTime date;
            if(ObjectUtil.equals(MailDefaultGroupEnum.DRAFT_BOX.getCode(),dto.getGroupId())){
                date = mailBody.getCreateTime();
            }else{
                date = Optional.of(mailBody).map(MailBody::getSentDate).orElse(mailBody.getCreateTime());
            }
            MailBodyDateEnum mailBodyDateEnum = mailUtils.checkMailBodyType(date, now);
            switch (mailBodyDateEnum){
                case 本月:
                    thisMonth.add(mailBody);
                    break;
                case 上月:
                    lastMonth.add(mailBody);
                    break;
                case 更早:
                    earlier.add(mailBody);
                    break;
                default:
            }
        }
        //设置未读数
        Map<String,Long> unread = new HashMap<>();
        //本月未读
        long unread2This = thisMonth.stream().filter(e -> !e.getRend()).count();
        //上月未读
        long unread2pre = lastMonth.stream().filter(e -> !e.getRend()).count();
        //更早未读
        long unread2earlier = earlier.stream().filter(e -> !e.getRend()).count();
        unread.put("thisMonth",unread2This);
        unread.put("lastMonth",unread2pre);
        unread.put("earlier",unread2earlier);

        Map<String, Object> map = new HashMap<>(2);
        map.put("thisMonth", this.resetSort(thisMonth));
        map.put("lastMonth",this.resetSort(lastMonth) );
        map.put("earlier", this.resetSort(earlier));
        map.put("unreadNum", unReadCount);
        map.put("unreadByDate", unread);
        return R.ok(map);
    }

    @Log
    @ApiOperation("APP获取邮件")
    @GetMapping("/getAPPMail")
    public R getappmail(Page page, MailDto dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        String groupId = dto.getGroupId();
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailBody::getMailConfigId, dto.getConfigId());
        queryWrapper.eq(MailBody::getGroupId, groupId);
        queryWrapper.eq(MailBody::getUserId, userId);
        if (ObjectNull.isNotNull(dto.getSubject())) {
            queryWrapper.like(MailBody::getSubject, dto.getSubject());
        }
        if (ObjectNull.isNotNull(dto.getStress())) {
            queryWrapper.eq(MailBody::getStress, dto.getStress());
        }
        queryWrapper.orderByDesc(MailBody::getSentDate);
        Page page1 = mailBodyService.page(page, queryWrapper);

        List<MailBody> records = page1.getRecords();
        saveAccepter(groupId, records);

        //查询邮箱标签
        List<SysMailTag> tags = sysMailTagService.list(new LambdaQueryWrapper<SysMailTag>().eq(SysMailTag::getMailConfigId, dto.getConfigId()));
        setMailTag(tags,records);

        records.stream().forEach(s -> {
                    MailContent one = mailContentService.getOne(Wrappers.<MailContent>lambdaQuery()
                            .eq(MailContent::getMailId, s.getId()));
                    if (ObjectNull.isNotNull(true)) {
                        s.setContent(
                                HtmlToTextUtil.toPlainText(one.getText()));
                    } else {
                        s.setContent("");
                    }
                }

        );
        //获取邮箱未读邮件
        LambdaQueryWrapper<MailBody> queryWrappers = new LambdaQueryWrapper<MailBody>()
                .eq(MailBody::getMailConfigId, dto.getConfigId())
                .eq(MailBody::getGroupId, dto.getGroupId())
                .eq(MailBody::getUserId, userId)
                .eq(MailBody::getRend, 0)
                .eq(MailBody::getIsSend, 1)
                .eq(MailBody::getDelCompletely,Boolean.FALSE);
        int count = mailBodyService.count(queryWrappers);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page1);
        map.put("unreadNum", count);
        return R.ok(map);
    }

    /**
     * 添加 接收人
     *
     * @param groupId
     * @param records
     */
    private void saveAccepter(String groupId, List<MailBody> records) {

        records.stream().forEach(
                s -> {
                    if (ObjectUtil.equals(groupId,MailDefaultGroupEnum.DRAFT_BOX.getCode())
                            || ObjectUtil.equals(groupId,MailDefaultGroupEnum.HAS_BEEN_SENT.getCode()) ) {
                        s.setFrom(
                                mailRecipientService.list(Wrappers.<MailRecipient>lambdaQuery()
                                                .eq(MailRecipient::getMailId, s.getId()).eq(MailRecipient::getMailType, "to"))
                                        .stream().map(MailRecipient::getMail).peek(j -> {
                                            if (j.contains("<")) {
                                                j = j.split("<")[0];
                                            }
                                        })
                                        .collect(Collectors.joining(","))
                        );
                    } else {
                        String from = s.getFrom();
                        s.setFrom(from.contains("<") ? from.split("<")[0] : from);
                    }
                }
        );
    }

    /**
     * 设置标签
     *
     * @param tags 标签
     * @param records
     */
    private void setMailTag(List<SysMailTag> tags, List<MailBody> records) {
        Map<String, SysMailTag> collect = tags.stream().collect(Collectors.toMap(SysMailTag::getId, Function.identity()));
        records.forEach(e -> e.setSysMailTag(collect.get(e.getMailTagId())));
    }


    @Log
    @ApiOperation("获取邮件未读数")
    @GetMapping("/queryUnreadCount")
    public R queryUnreadCount(String groupId, String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailBody::getUserId, userId)
                .eq(MailBody::getRend, 0)
                .eq(MailBody::getDelCompletely,Boolean.FALSE)
                .eq(MailBody::getMailConfigId, configId)
                .eq(MailBody::getGroupId, groupId);
        int count = mailBodyService.count(queryWrapper);
        return R.ok(count);
    }

    @Log
    @ApiOperation("删除")
    @PostMapping("/delMail")
    public R delMail(@RequestBody MailDelDto dto) {
        String mailIds = dto.getMailIds();
        MailBody mailBody = new MailBody();
        switch (dto.getDelFlag()){
            case "0":
                mailBody.setGroupId(MailDefaultGroupEnum.INBOX.getCode());
                break;
            case "1":
                mailBody.setGroupId(MailDefaultGroupEnum.DELETED.getCode());
                break;
            case "2":
                mailBody.setDelCompletely(true);
                break;
        }
        mailBodyService.updateMailBody(mailIds, mailBody);
        //同步服务器
        return R.ok();
    }


    @Log
    @ApiOperation("彻底删除")
    @DeleteMapping("/deleteMail")
    public R deleteMail(@RequestBody MailDelDto dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        String mailIds = dto.getMailIds();
        String configId = dto.getConfigId();
        //同步服务器
        String st = ",";
        List<MailBody> list = new ArrayList<>(1);
        if (mailIds.contains(st)) {
            String[] split = mailIds.split(st);
            list = mailBodyService.list(Wrappers.<MailBody>lambdaQuery().in(MailBody::getId, split));
        } else {
            MailBody byId = mailBodyService.getById(mailIds);
            list.add(byId);
        }
        if(!list.isEmpty()){
            List<MailBody> removeMails = list.stream().map(e -> e.setDelCompletely(Boolean.TRUE)).collect(Collectors.toList());
            mailBodyService.updateBatchById(removeMails);
            tagRead(list, userId, String.valueOf(UpdateMailStatusDTO.DELETED_BIT), configId);
        }
        return R.ok();
    }

    @Log
    @ApiOperation("标记邮件")
    @PostMapping("tagMail")
    public R tagMail(@RequestBody MailTagDto dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        Integer tag = dto.getTag();
        String mailIds = dto.getMailIds();
        String configId = dto.getConfigId();
        MailBody mailBody = new MailBody();
        //获取邮件信息
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper();
        String type = ",";
        if (mailIds.contains(type)) {
            queryWrapper.in(MailBody::getId, mailIds.split(type));
        } else {
            queryWrapper.eq(MailBody::getId, mailIds);
        }
        List<MailBody> list = mailBodyService.list(queryWrapper);
        if (tag.equals(0)) {
            //已读
            mailBody.setRend(true);

            //todo 同步对方服务器
            tagRead(list, userId, String.valueOf(UpdateMailStatusDTO.SEEN_BIT), configId);
        }
        if (tag.equals(1)) {
            //未读
            //todo 同步对方服务器
            mailBody.setRend(false);
            tagRead(list, userId, String.valueOf(UpdateMailStatusDTO.RECENT_BIT), configId);
        }
        if (tag.equals(two)) {
            //红旗
            mailBody.setStress(true);
        }
        if (tag.equals(three)) {
            //取消红旗
            mailBody.setStress(false);
        }
        mailBodyService.updateMailBody(mailIds, mailBody);
        return R.ok();
    }


    @Log
    @ApiOperation("移动邮件")
    @PostMapping("/moveMail")
    public R moveMail(@RequestBody MailMoveDto dto) {
        MailBody mailBody = new MailBody();
        String groupId = dto.getGroupId();
        mailBody.setGroupId(groupId);
        String mailIds = dto.getMailIds();
        String st = ",";
        if (mailIds.contains(st)) {
            String[] split = mailIds.split(",");
            List<MailBody> list = mailBodyService.list(Wrappers.<MailBody>lambdaQuery().in(MailBody::getId, split));
            for (MailBody body : list) {
                mailBody.setId(body.getId());
                mailBodyService.updateById(mailBody);
            }
        } else {
            mailBody.setId(mailIds);
            mailBodyService.updateById(mailBody);
        }
        return R.ok();
    }

    @Log
    @ApiOperation("全部标记已读")
    @GetMapping("/tagAllRead")
    public R tagAllRead(String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        LambdaQueryWrapper<MailBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MailBody::getUserId, userId).eq(MailBody::getRend, 0).eq(MailBody::getMailConfigId, configId);
        List<MailBody> list = mailBodyService.list(queryWrapper);
        for (MailBody mailBody : list) {
            mailBody.setRend(true);
            mailBodyService.updateById(mailBody);
        }
        //todo 同步对方服务器
        tagRead(list, userId, String.valueOf(UpdateMailStatusDTO.SEEN_BIT), configId);
        return R.ok();
    }

    /**
     * 标记已读
     *
     * @param list   邮件信息集
     * @param userId 用户id
     * @param type
     */
    public void tagRead(List<MailBody> list, String userId, String type, String configId) {
        List<UpdateMailStatusDTO> ls = new ArrayList<>();
        for (MailBody mailBody : list) {
            UpdateMailStatusDTO dto = new UpdateMailStatusDTO();
            dto.setMailId(mailBody.getMailId())
                    .setUserId(userId)
                    .setFlags(type)
                    .setConfigId(configId);
            ls.add(dto);
        }
        mailAcceptApi.updateMessage(ls);
    }

    /**
     * 查看邮件详情
     *
     * @param id       邮件body id
     * @param configId 邮件配置 id
     * @return 邮件详情
     */
    @Log
    @ApiOperation("邮件详情")
    @GetMapping("/mailDetails")
    public R<Dict> mailDetails(String id, String configId) {
        MailBody byId = mailBodyService.getById(id);
        //深拷贝
        MailBody copy = JSONUtil.toBean(JSONUtil.toJsonStr(byId), MailBody.class);
        Dict result = new Dict();
        if(Optional.ofNullable(copy).map(MailBody::getMailTagId).isPresent()){
            SysMailTag tag = sysMailTagService.getById(copy.getMailTagId());
            copy.setSysMailTag(tag);
        }
        result.put("mailBody", copy);

        // 修改未读邮件为已读邮件，并同步邮件服务器
        if (!byId.getRend()) {
            byId.setRend(true);
            mailBodyService.updateById(byId);
            List<MailBody> list = new ArrayList<>();
            list.add(byId);
            // TODO  更新邮件为已读，第三个参数传递为邮件状态，有误
            tagRead(list, byId.getUserId(), String.valueOf(UpdateMailStatusDTO.SEEN_BIT), String.valueOf(UpdateMailStatusDTO.SEEN_BIT));
        }

        MailContent one = mailContentService.getOne(Wrappers.query(new MailContent().setMailId(byId.getId())));
        List<MailRecipient> list = mailRecipientService.list(Wrappers.query(new MailRecipient().setMailId(byId.getId())));
        List<MailExtend> mailExtends = new ArrayList<>(1);
        //获取上一封 下一封
        // TODO  mailBody 主键为String类型，不能采用 >或 < 符号来进行比较，获取逻辑重新书写
//        String upId = mailBodyService.queryMailUpId(id, byId.getUserId(), byId.getGroupId(), configId);
//        String downId = mailBodyService.queryMailDownId(id, byId.getUserId(), byId.getGroupId(), configId);
        Map<String, String> aroundMailBodyId = mailBodyService.queryAroundMailBodyId(id, byId.getUserId(), byId.getGroupId(), configId);
        result.put("MailContent", one);
        result.put("MailExtendList", mailExtends);
        result.put("MailRecipientList", list);
        result.put("upId", aroundMailBodyId.get("upId"));
        result.put("downId", aroundMailBodyId.get("downId"));
        return R.ok(result);
    }

    @Log
    @ApiOperation("获取邮件附件")
    @GetMapping("/getMailAccessory")
    public R getMailAccessory(String id) {
        MailBody byId = mailBodyService.getById(id);
        if (ObjectNull.isNull(byId)) {
            return R.failed("获取附件失败");
        }
        List<MailExtend> mailExtends = new ArrayList<>(1);
        //判断当前邮件是否存在附件
        if (byId.getIsExtend()) {
            mailExtends = mailExtendService.list(Wrappers.query(new MailExtend().setMailId(id)));
            if (mailExtends.size() == 0) {
                if (byId.getIsSend().equals(true)) {
                    //获取邮件附件
                    boolean b = mailAcceptApi.getAccessory(id).is();
                    if (b) {
                        mailExtends = mailExtendService.list(Wrappers.query(new MailExtend().setMailId(id)));
                    }
                }
            }
            for (MailExtend extend : mailExtends) {
                String url = extend.getUrl();
                String bucketName = url.split("_")[0];
                R filelink = R.ok();
                //TODO 获取文件链接
//                fileApi.filelink(bucketName, url.substring(bucketName.length() + 1));
                String data = filelink.getData() + "";
//                String[] split = url.split("_");
//                String data = fileApi.filelink(split[0], split[1]).getData() + "";
                Integer status = extend.getType() == null ? 0 : extend.getType();
                if (status.equals(1)) {
                    String st = "\\?";
                    String urls = "";
                    if (data.contains("?")) {
                        String[] splits = data.split(st);
                        urls = splits[0];
                    } else {
                        urls = data;
                    }
                    extend.setImgUrl(urls);
                } else {
                    extend.setImgUrl(data);
                }
            }
        }
        return R.ok(mailExtends);
    }

    @Log
    @ApiOperation("修改邮件")
    @PutMapping("/mail")
    public R updateMail(UpdateMailDto updateMailDTO) {
        mailBodyService.updateMail(updateMailDTO);
        return R.ok();
    }

    @Log
    @ApiOperation("保存垃圾邮件")
    @PostMapping("/dumpMail")
    public R saveMail(@RequestBody MailSendDTO dto) {
        mailSendApi.saveEmail(dto.setGroupId(MailDefaultGroupEnum.SPAM.getCode()));
        return R.ok();
    }

    @Log
    @ApiOperation("发送邮件")
    @PostMapping("/sendMail")
    public R sendMail(@RequestBody MailSendDTO dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        //需要回执 todo 查询用户(发送人)邮件地址F
        dto.setUserId(userId);
        //判断是否需要分别发送
        Boolean isRespectively = dto.getIsRespectively();
        if (dto.getIsTiming()) {
            dto.setSentDate(dto.getTiming());
        } else {
            dto.setSentDate(LocalDateTime.now());
        }
        //保存邮件基本信息
        saveMailInfo(dto);
        try {
            if (isRespectively) {
                //需要分别发送
                List<MailRecipientDTO> to = dto.getTo();
                for (MailRecipientDTO recipient : to) {
                    List<MailRecipientDTO> tos = new ArrayList<>();
                    tos.add(recipient);
                    dto.setTo(tos);
                    mailSendApi.mailSendAttach(dto);
                }
            } else {
                mailSendApi.mailSendAttach(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送邮件失败,失败原因{}", e.getMessage());
            return R.failed(e.getMessage());
        }
        //添加 外部邮箱
        addUnEmail(dto, userId);
        return R.ok();
    }

    /**
     * 保存邮件基本信息
     *
     * @param dto
     */
    private void saveMailInfo(@RequestBody MailSendDTO dto) {
        List<MultipartFilesDTO> multipartFiles = dto.getMultipartFiles();
        MailBody body = new MailBody()
                .setGroupId(MailDefaultGroupEnum.FAILED.getCode())
                .setFrom(dto.getFrom())
                .setIsSend(false)
                .setMailConfigId(dto.getConfigId())
                .setUserId(dto.getUserId())
                .setSubject(dto.getSubject())
                .setIsTiming(dto.getIsTiming())
                .setReceipt(dto.getReceipt())
                .setSentDate(dto.getSentDate())
                .setReplyTo(dto.getReplyTo() == null ? "" : dto.getReplyTo())
                .setIsExtend(multipartFiles.size() > 0 ? true : false)
                .setTiming(dto.getTiming())
                .setUrgent(dto.getUrgent());
        mailBodyService.save(body);
        //返回邮件Id
        dto.setId(body.getId());
        mailContentService.save(new MailContent().setMailId(body.getId()).setText(dto.getText()).setTextType(dto.getTextType()));
        //保存附件
        Optional.ofNullable(dto.getMultipartFiles())
                .ifPresent(e -> e.forEach(d -> mailExtendService.save(new MailExtend()
                        .setMailId(body.getId())
                        .setUrl(d.getUrl())
                        .setName(d.getName()))));
        saveRecipient(dto.getCc(), EmailRecipientTypeEnum.CC, body.getId());
        saveRecipient(dto.getBcc(), EmailRecipientTypeEnum.BCC, body.getId());
        saveRecipient(dto.getTo(), EmailRecipientTypeEnum.TO, body.getId());
    }

    /**
     * 添加外部邮箱
     *
     * @param dto
     * @param userId
     */
    private void addUnEmail(@RequestBody MailSendDTO dto, String userId) {
        try {

            //获取当前公司所有邮箱
            List<String> ls = AuthorityManagementUtils.userSearch(new SearchUserDto().setType(UserQueryType.all))
                    .stream().map(UserDto::getEmail).collect(Collectors.toList());
            // 比对所邮箱 将不存在的邮箱保存到外部邮箱
            List<MailRecipientDTO> to = dto.getTo();
            List<MailRecipientDTO> cc = dto.getCc();
            List<MailRecipientDTO> bcc = dto.getBcc();
            saveExternalMail(userId, ls, to);
            saveExternalMail(userId, ls, cc);
            saveExternalMail(userId, ls, bcc);
        } catch (Exception e) {
            log.error("添加外部邮箱出错，错误原因{}", e.getMessage());
        }
    }

    /**
     * 保存到外部邮箱
     *
     * @param userId 用户id
     * @param to
     */
    private void saveExternalMail(String userId, List<String> ls, List<MailRecipientDTO> to) {
        if (ObjectNull.isNotNull(to) && to.size() > 0) {
            for (MailRecipientDTO recipient : to) {
                //获取当前用户所有外部邮箱
                List<String> list = externalMailService.queryExternalMailByUserId(userId);
                String mail = recipient.getMail();
                String name = recipient.getName() == null ? "" : recipient.getName();
                if (!list.contains(mail) && !ls.contains(mail)) {
                    //保存到外部邮箱
                    externalMailService.save(new SysUserExternalMail().setUserId(userId).setEmail(mail).setName(name));
                }
            }
        }
    }

    @Log
    @ApiOperation("保存当前用户邮件配置")
    @PostMapping("/saveMailConfig")
    public R save(@Validated @RequestBody EmailUserDto emailUserDTO) {
        try {
            Boolean verifyAccount = getVerifyAccount(emailUserDTO);
            if (!verifyAccount) {
                return R.failed("账号或授权码有误");
            }
            String userId = UserCurrentUtils.getCurrentUser().getId();
            int count = sysUserMailConfigService.count(Wrappers.<SysUserMailConfig>lambdaQuery().eq(SysUserMailConfig::getSysUserId, userId)
                    .eq(SysUserMailConfig::getUsername, emailUserDTO.getUsername())
            );
            if (count > 0) {
                return R.failed("当前账号已存在");
            }
            sysUserMailConfigService.saveMailConfig(emailUserDTO, userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存用户配置失败");
            return R.failed("保存用户配置失败");
        }
        return R.ok();
    }

    /**
     * 验证邮箱授权码
     *
     * @param dto
     * @return
     */
    private static Boolean getVerifyAccount(EmailUserDto dto) {
        Boolean flag = false;
        try {
            Properties props = new Properties();

            // 走25端口验证参数如下设置
            /*props.put("mail.smtp.host", dto.getSendHost());
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.auth", "false");*/

            // 走 465端口验证邮箱链接 邮箱配置如下
            // smtp服务器地址
            props.put("mail.smtp.host", dto.getSendHost());
            // SSLSocketFactory类的端口
            props.put("mail.smtp.socketFactory.port", "465");
            //SSLSocketFactory类
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            //网易提供的ssl加密端口,QQ邮箱也是该端口
            props.put("mail.smtp.port", "465");

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            // 阿里云服务器目前屏蔽掉了25 端口，只能走不加密的465端口
//            transport.connect(dto.getSendHost(), 25, dto.getUsername(), dto.getPassword());
            // 使用 465 端口验证邮箱链接
            transport.connect(dto.getSendHost(), dto.getUsername(), dto.getPassword());
            transport.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Log
    @ApiOperation("获取当前发件人邮箱")
    @GetMapping("/getSenderMail")
    public R getSenderMail(String configId) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        String username = UserCurrentUtils.getCurrentUser().getRealName();
        SysUserMailConfig smtp = getSysUserMailConfig(configId);
        MailConfigVo copy = BeanCopyUtil.copy(smtp, MailConfigVo.class);
        copy.setName(username);
        if (ObjectNull.isNull(smtp)) {
            return R.failed("未绑定邮箱，请先绑定");
        }
        return R.ok(copy);
    }

    private SysUserMailConfig getSysUserMailConfig(String configId) {
//        String protocol = "smtp";
//        return sysUserMailConfigService.gettrue(Wrappers.<SysUserMailConfig>lambdaQuery().eq(SysUserMailConfig::getSysUserId, userId)
//                .eq(SysUserMailConfig::getDelFlag, '0').eq(SysUserMailConfig::getUseWay, 1).eq(SysUserMailConfig::getSendProtocol, protocol));
        return sysUserMailConfigService.getById(configId);
    }

    @Log
    @ApiOperation("保存草稿箱")
    @PostMapping("/saveDraft")
    public R saveDraft(@RequestBody MailSendDTO dto) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        String username = UserCurrentUtils.getCurrentUser().getRealName();
        String configId = dto.getConfigId();
        //深拷贝
        MailBody copy = JSONUtil.toBean(JSONUtil.toJsonStr(dto), MailBody.class);
        copy.setMailConfigId(configId);
        SysUserMailConfig sysUserMailConfig = getSysUserMailConfig(dto.getConfigId());
        copy.setUserId(userId);
        String form = username + "<" + sysUserMailConfig.getUsername() + ">";
        copy.setFrom(form);
        copy.setGroupId(MailDefaultGroupEnum.DRAFT_BOX.getCode());
        MailBody body = mailBodyService.saveMailBody(copy);
        String id = body.getId();
        //返回邮件Id
        dto.setId(id);
        mailContentService.save(new MailContent().setMailId(id).setText(dto.getText()).setTextType(dto.getTextType()));
        //保存附件
        Optional.ofNullable(dto.getMultipartFiles())
                .ifPresent(e -> e.forEach(d -> mailExtendService.save(new MailExtend()
                        .setMailId(dto.getId())
                        .setUrl(d.getUrl())
                        .setName(d.getName()))));
        saveRecipient(dto.getCc(), EmailRecipientTypeEnum.CC, id);
        saveRecipient(dto.getBcc(), EmailRecipientTypeEnum.BCC, id);
        saveRecipient(dto.getTo(), EmailRecipientTypeEnum.TO, id);
        return R.ok();
    }


    /**
     * 保存接收人
     *
     * @param list 保存对象
     * @param type 用户类型
     * @param id   邮件id
     * @return void
     **/
    private void saveRecipient(List<MailRecipientDTO> list, EmailRecipientTypeEnum type, String id) {
        Optional.ofNullable(list)
                .ifPresent(e -> e.forEach(d -> mailRecipientService.save(new MailRecipient()
                        .setMailId(id)
                        .setName(d.getName())
                        .setMailName(d.getMailName())
                        .setMailType(type)
                        .setMail(d.getMail()))));
    }


    @Log
    @ApiOperation("信任邮件")
    @GetMapping("/trustMail")
    public R trustMail(String id) {
        MailBody byId = mailBodyService.getById(id);
        byId.setGroupId(MailDefaultGroupEnum.INBOX.getCode());
        mailBodyService.updateById(byId);
        return R.ok();
    }

    @Log
    @ApiOperation("判断是否第一次登陆")
    @GetMapping("/isFirstLogin")
    public R isFirstLogin() {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        List<SysUserMailConfig> list = sysUserMailConfigService.list(Wrappers.<SysUserMailConfig>lambdaQuery()
                .eq(SysUserMailConfig::getSysUserId, userId)
        );
        return R.ok(list);
    }

    @Log
    @ApiOperation("获取用户邮箱列表")
    @GetMapping("/getUserMailList")
    public R getUserMailList() {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        List<SysUserMailConfig> list = sysUserMailConfigService.list(Wrappers.<SysUserMailConfig>lambdaQuery()
                .eq(SysUserMailConfig::getSysUserId, userId)
                .eq(SysUserMailConfig::getUseWay, 1)
                .orderByAsc(SysUserMailConfig::getCreateTime));
        list.stream().forEach(s -> s.setPassword(null));
        return R.ok(list);
    }

    @Log
    @ApiOperation("获取用户邮箱列表")
    @GetMapping("/getMailConfigList")
    public R getMailConfigList() {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        List<SysUserMailConfig> list = sysUserMailConfigService.list(Wrappers.<SysUserMailConfig>lambdaQuery()
                .eq(SysUserMailConfig::getSysUserId, userId)
                .orderByAsc(SysUserMailConfig::getCreateTime));
        return R.ok(list);
    }

    @Log
    @ApiOperation("修改邮箱配置")
    @PostMapping("/updateConfig")
    public R updateConfig(@RequestBody EmailUserDto dto) {
        Boolean verifyAccount = getVerifyAccount(dto);
        if (!verifyAccount) {
            return R.failed("账号或授权码有误");
        }
        SysUserMailConfig copy = BeanCopyUtil.copy(dto, SysUserMailConfig.class);
        sysUserMailConfigService.updateById(copy);
        return R.ok();
    }


    @Log
    @ApiOperation("修改邮箱状态")
    @GetMapping("/updateConfigState")
    public R updateConfigState(String id, Integer useWay) {
        SysUserMailConfig config = new SysUserMailConfig();
        config.setId(id);
        config.setUseWay(useWay);
        sysUserMailConfigService.updateById(config);
        return R.ok();
    }

    @Log
    @ApiOperation("删除又想配置")
    @GetMapping("/delConfigById")
    public R delConfigById(String id) {
        sysUserMailConfigService.update(Wrappers.<SysUserMailConfig>lambdaUpdate()
                .set(SysUserMailConfig::getDelFlag, 1)
                .eq(SysUserMailConfig::getId, id));
        return R.ok();
    }

    @Log
    @ApiOperation("修改邮件已回执")
    @GetMapping("/updateIsReceipt")
    public R updateIsReceipt(String id) {
        mailBodyService.update(Wrappers.<MailBody>lambdaUpdate()
                .set(MailBody::getIsReceipt, 1)
                .eq(MailBody::getId, id));
        return R.ok();
    }

    @Log
    @ApiOperation("开启/关闭消息提醒")
    @PutMapping("/remind/{id}/{remindStatus}")
    public R<Boolean> editRemind(@ApiParam("配置id")@PathVariable("id")String configId
            ,@ApiParam("消息提醒状态 true开启消息提醒 false关闭消息提醒")@PathVariable("remindStatus")boolean status) {
        boolean update = sysUserMailConfigService.update(new LambdaUpdateWrapper<SysUserMailConfig>()
                .eq(SysUserMailConfig::getId, configId).set(SysUserMailConfig::getIsRemind, status));
        return R.ok(update);
    }


    /**
     * 重新排序
     * @param mailBodyList
     * @return
     */
    private List<MailBody> resetSort(List<MailBody> mailBodyList){
        boolean b = mailBodyList.stream().anyMatch(e -> ObjectUtil.isNull(e.getSentDate()));
        if(b){
            return mailBodyList.stream().sorted(Comparator.comparing(MailBody::getCreateTime).reversed()).collect(Collectors.toList());
        }
        return mailBodyList;
    }
}

