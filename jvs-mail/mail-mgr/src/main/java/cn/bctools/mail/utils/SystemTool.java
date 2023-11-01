package cn.bctools.mail.utils;

/**
 * @author admin
 * @ClassName: SystemTool
 * @Description: socketIo
 */


import cn.bctools.mail.socket.manager.SocketSessionManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取用户信息的处理器
 *
 * @author guojing
 */
@Slf4j
public class SystemTool {

    public static final String PC_USER = "pc";
    public static final String MOBLIE_USER = "moblie";

    public static void pushMsg(String userId) {
        try {
            Map<String, Object> map = new HashMap<>(1);
            map.put("type", "pushLogin");
            map.put("data", "您的账号在其他地方登录，您被迫退出，如果不是您本人的操作，请注意账号安全！");
            SocketSessionManager.sendMessages(userId, map);
            log.info("用户登录:{}，消息{}.........，", userId, map);
        } catch (Exception e) {
            log.error("消息推送有误{}", e.getMessage());
        }
    }
}
