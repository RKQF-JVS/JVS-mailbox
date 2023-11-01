package cn.bctools.mail.component;


import org.springframework.web.socket.WebSocketSession;

/**
 * @author admin
 * @ClassName: ReceivingInterface
 * @Description: 客户端发送数据
 */
public interface ReceivingInterface {
    /**
     * 接收消息
     *
     * @param session 客户端
     * @param payload 客户端发送消息
     * @param type    客户端类型
     */
    void accept(WebSocketSession session, String payload, String type);
}
