package cn.bctools.mail.socket.manager;

import cn.bctools.mail.utils.SystemTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author R
 * @Description: websocket 会话管理
 */
@Slf4j
public class SocketSessionManager {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * websocket 会话池
     */
    private final static Cache<String, WebSocketSession> WEB_SOCKET_SESSION_MAP = CacheBuilder.newBuilder()
            .maximumSize(10240)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    /**
     * 添加 websocket 会话
     *
     * @param key
     * @param session
     */
    public static void add(String key, WebSocketSession session) {
        WEB_SOCKET_SESSION_MAP.put(key, session);
    }

    /**
     * 移除 websocket 会话,并将该会话内容返回
     *
     * @param key
     * @return
     */
    public static WebSocketSession remove(String key) {
        WebSocketSession session = WEB_SOCKET_SESSION_MAP.getIfPresent(key);
        WEB_SOCKET_SESSION_MAP.invalidate(key);
        return session;
    }

    /**
     * 删除 websocket,并关闭连接
     *
     * @param key
     */
    public static void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("Websocket session close exception ", e);
            }
        }
    }

    /**
     * 获取 websocket 会话
     *
     * @param key
     * @return
     */
    public static WebSocketSession get(String key) {
        return WEB_SOCKET_SESSION_MAP.getIfPresent(key);
    }

    /**
     * 发送对应用户消息
     * 用户登录强制下线
     *
     * @param key  用户key SystemTool.MOBLIE_USER + userid + 租户id
     * @param data 数据
     */
    public static <T> void sendMessages(String key, T data) {
        String sendData = "";
        if (data != null) {
            try {
                sendData = OBJECT_MAPPER.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                log.error("json序列化异常，{}", e.getMessage());
                return;
            }
        }
        WebSocketSession session = WEB_SOCKET_SESSION_MAP.getIfPresent(key);
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(sendData));
            }
        } catch (IOException e) {
            log.error("消息发送异常，{}", e.getMessage());
        }
    }

    /**
     * 发送消息
     * 用于任务及邮件消息推送 同时通知PC端 和APP端
     *
     * @param key  用户ID userid + 租户id
     * @param data 数据
     */
    public static <T> void sendMessage(String key, T data) {
        String sendData = "";
        if (data != null) {
            try {
                sendData = OBJECT_MAPPER.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                log.error("json序列化异常，{}", e.getMessage());
                return;
            }
        }
        WebSocketSession moblie = WEB_SOCKET_SESSION_MAP.getIfPresent(SystemTool.MOBLIE_USER + key);
        WebSocketSession pc = WEB_SOCKET_SESSION_MAP.getIfPresent(SystemTool.PC_USER + key);
        try {
            if (moblie != null && moblie.isOpen()) {
                moblie.sendMessage(new TextMessage(sendData));
            }
            if (pc != null && pc.isOpen()) {
                pc.sendMessage(new TextMessage(sendData));
            }
        } catch (IOException e) {
            log.error("消息发送异常，{}", e.getMessage());
        }
    }


}
