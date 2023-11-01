package cn.bctools.mail.job.utils;

import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.socket.config.ClientConfig;
import cn.bctools.message.push.api.MessagePushUtilsApi;
import cn.bctools.message.push.dto.enums.InsideNotificationTypeEnum;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.dto.messagePush.MessageBatchSendDto;
import cn.bctools.message.push.dto.messagePush.ReceiversDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.WechatTemplateData;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Slf4j
public class MessageComponents {

    public static final String INSIDE_NOTICE_FORMAT = "邮箱：%s 已拉取邮件 %s 封";
    public static final String WX_TEMPLATE_FORMAT = "您有 %s 封新邮件";

    private final MessagePushUtilsApi messagePushUtilsApi;
    private final ClientConfig clientConfig;

    private final AuthUserServiceApi authUserServiceApi;

    /**
     * 创建消息发送主体
     * @param mailConfig
     */
    public InsideNotificationDto buildInsideMessageDto(SysUserMailConfig mailConfig,int mailCount){
        //站内信
        InsideNotificationDto insideNotificationDto = new InsideNotificationDto();
        Map<String,Object> content = new HashMap<>();
        content.put("title","邮件拉取消息");
        content.put("content",String.format(INSIDE_NOTICE_FORMAT,mailConfig.getUsername(),mailCount));
        //设置接收人
        insideNotificationDto
                //设置消息内容
                .setContent(JSONUtil.toJsonStr(content))
                .setLargeCategories(InsideNotificationTypeEnum.notice)
                .setSubClass("通知消息")
                .setDefinedReceivers(CollectionUtil.toList(new ReceiversDto()
                        .setTenantId(mailConfig.getTenantId())
                        .setUserId(mailConfig.getSysUserId())
                        .setUserName(mailConfig.getCreateBy())
                        .setReceiverConfig(mailConfig.getSysUserId())))
                //设置客户端唯一标识
                .setClientCode(clientConfig.getClientId());
        return insideNotificationDto;
    }

    /**
     * 创建消息发送主体
     * @param mailConfig
     */
    public TemplateMessageDTO buildWxTemplateMessageDto(SysUserMailConfig mailConfig, int mailCount){
        TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO();
        List<WechatTemplateData> data = new ArrayList<>();
        String now = DateUtil.now();
        //抬头
        WechatTemplateData first = new WechatTemplateData();
        first.setName("first").setValue("企业邮筒").setColor("#434343");
        data.add(first);
        //关键字1
        WechatTemplateData keyword1 = new WechatTemplateData();
        keyword1.setName("keyword1").setValue(now).setColor("#434343");
        data.add(keyword1);
        //关键字2
        WechatTemplateData keyword2 = new WechatTemplateData();
        keyword2.setName("keyword2").setValue(String.format(WX_TEMPLATE_FORMAT,mailCount)).setColor("#434343");
        data.add(keyword2);
        //备注
        WechatTemplateData remark = new WechatTemplateData();
        remark.setName("remark").setValue("请前往企业邮筒进行查看: http://mailbox.bctools.cn").setColor("#434343");
        data.add(remark);
        templateMessageDTO.setTemplateDataList(data);
        //模板id
        templateMessageDTO.setWechatTemplateId("qJVMwUUGadPkExGJOQVQ0IHFiCvdXqCoWD5bUq3oxFc");
        templateMessageDTO.setClientCode(clientConfig.getClientId());
        //要跳转的地址
        templateMessageDTO.setUrl("http://mailbox.bctools.cn");
        ReceiversDto receiversDto = new ReceiversDto();
        receiversDto.setTenantId(mailConfig.getTenantId())
                .setUserId(mailConfig.getSysUserId());
        templateMessageDTO.setDefinedReceivers(Stream.of(receiversDto).collect(Collectors.toList()));
        return templateMessageDTO;
    }

    /**
     * 添加微信openId
     * @param wxTemplateList
     */
    public void setWxConfig(List<TemplateMessageDTO> wxTemplateList){
        try {
            List<String> userIds = wxTemplateList.stream().map(TemplateMessageDTO::getDefinedReceivers)
                    .flatMap(Collection::stream)
                    .map(ReceiversDto::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<String, UserDto> userMap = authUserServiceApi.getByIds(userIds).getData().stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
            wxTemplateList.forEach(e -> {
                e.getDefinedReceivers().forEach(receivers ->{
                    UserDto userDto = userMap.getOrDefault(receivers.getUserId(), new UserDto());
                    //-1代表没有关注公众号
                    String openId = "-1";
                    if(ObjectNull.isNotNull(userDto.getExceptions()) && userDto.getExceptions().containsKey("WECHAT_MP")){
                        JSONObject wechat_mp = JSONUtil.parseObj(userDto.getExceptions().get("WECHAT_MP"));
                        openId = String.valueOf(wechat_mp.get("openId"));
                    }
                    receivers.setUserName(userDto.getRealName()).setReceiverConfig(openId);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("设置微信OpenId异常:"+e.getMessage());
        }
    }

    public void send(MessageBatchSendDto messageBatchSendDto){
        if(ObjectUtil.isNotNull(messageBatchSendDto)){
            messagePushUtilsApi.batchSend(messageBatchSendDto);
        }
    }
}
