package cn.bctools.mail.utils;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统工具
 *
 * @author R
 */
public class SystemUtil {

    public static Map<String, WebSocketSession> map = new ConcurrentHashMap<>(1);

}
