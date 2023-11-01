package cn.bctools.mail.component;

import cn.bctools.mail.api.api.MailAcceptApi;
import cn.bctools.mail.entity.SysUserMailConfig;
import cn.bctools.mail.utils.SystemUtil;
import cn.bctools.mail.service.SysUserMailConfigService;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @ClassName: SyncEmailComponent
 * @Description:
 */
@Component("syncEmail")
@Slf4j
public class SyncEmailComponent implements ReceivingInterface {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    final Integer one = 1;
    @Resource
    MailAcceptApi mailAcceptApi;
    @Resource
    SysUserMailConfigService sysUserMailConfigService;

    @Override
    @SneakyThrows
    public void accept(WebSocketSession session, String payload, String type) {
        log.info("接收数据,{}", payload);
        JSONObject object = JSONObject.parseObject(payload);
        String configId = object.getString("configId");
        SystemUtil.map.put(configId, session);
        SysUserMailConfig byId = sysUserMailConfigService.getById(configId);
        if (ObjectUtil.isNotNull(byId)) {
            if (byId.getUseWay().equals(one)) {
                mailAcceptApi.mailAccept(configId);
            }
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("type", "syncSuccess");
        map.put("data", true);
        String sendData = OBJECT_MAPPER.writeValueAsString(map);
        session.sendMessage(new TextMessage(sendData));
        log.info("同步数据成功");
    }
}
