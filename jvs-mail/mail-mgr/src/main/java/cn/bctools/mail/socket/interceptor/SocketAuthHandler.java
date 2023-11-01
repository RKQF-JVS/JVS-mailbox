package cn.bctools.mail.socket.interceptor;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.mail.job.utils.MailUtils;
import cn.bctools.mail.socket.manager.SocketSessionManager;
import cn.bctools.mail.component.ExchangeData;
import cn.bctools.mail.component.ReceivingInterface;
import cn.bctools.mail.utils.SystemTool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * @Description: websocket 连接拦截器
 */
@Slf4j
@Component
public class SocketAuthHandler extends TextWebSocketHandler {

    /**
     * 握手成功之后
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserDto moblieUser = (UserDto) session.getAttributes().get(SystemTool.MOBLIE_USER);
        UserDto pcUser = (UserDto) session.getAttributes().get(SystemTool.PC_USER);
        if (ObjectNull.isNotNull(moblieUser)) {
            // 用户连接成功,缓存用户会话
            log.debug("移动用户[ {} ]创建连接,租户id为：{}", moblieUser.getId().toString(), moblieUser.getTenantId());
            SocketSessionManager.add(SystemTool.MOBLIE_USER + moblieUser.getId() + "_" + moblieUser.getTenantId(), session);
            return;
        }
        if (ObjectNull.isNotNull(pcUser)) {
            // 用户连接成功,缓存用户会话
            log.debug("PC用户[ {} ]创建连接,租户id为：{}", pcUser.getId().toString(), pcUser.getTenantId());
            SocketSessionManager.add(SystemTool.PC_USER + pcUser.getId() + "_" + pcUser.getTenantId(), session);
            return;
        }
        throw new BusinessException("用户登录已失效");
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 接收客户端消息
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UserDto moblieUser = (UserDto) session.getAttributes().get(SystemTool.MOBLIE_USER);
        UserDto pcUser = (UserDto) session.getAttributes().get(SystemTool.PC_USER);
        if (pcUser != null && "ping".equalsIgnoreCase(message.getPayload())) {
            SocketSessionManager.get(SystemTool.PC_USER + pcUser.getId() + "_" + pcUser.getTenantId());
            session.sendMessage(new TextMessage("pong"));
            return;
        }
        if (moblieUser != null && "ping".equalsIgnoreCase(message.getPayload())) {
            SocketSessionManager.get(SystemTool.MOBLIE_USER + moblieUser.getId() + "_" + moblieUser.getTenantId());
            session.sendMessage(new TextMessage("pong"));
            return;
        }
        //接收服务
        ExchangeData exchangeData;
        try {
            exchangeData = JSONObject.parseObject(message.getPayload(), ExchangeData.class);
        } catch (Exception e) {
            log.error("序列化失败:{}", e.getMessage());
            return;
        }
        if (ObjectNull.isNotNull(moblieUser)) {
            //添加租户处理
            TenantContextHolder.setTenantId(moblieUser.getTenantId());
        }
        if (ObjectNull.isNotNull(pcUser)) {
            //添加租户处理
            TenantContextHolder.setTenantId(pcUser.getTenantId());
        }

        Map<String, ReceivingInterface> bean = this.applicationContext.getBeansOfType(ReceivingInterface.class);
        if (bean.size() == 0) {
            return;
        }
        String type = isType(moblieUser, pcUser);
        String userId = null;
        if (moblieUser != null) {
            userId = moblieUser.getId();
        } else if (pcUser != null) {
            userId = pcUser.getId();
        }
        String finalUserId = userId;
        //判断是否为 实时同步新邮件
        if(ObjectUtil.equals("realtimePull",exchangeData.getType())){
            MailUtils mailUtils = SpringUtil.getBean(MailUtils.class);
            mailUtils.realtimePull(session,exchangeData);
        }else {
            bean.forEach((k, v) -> {
                if (k.equalsIgnoreCase(exchangeData.getType())) {
                    String data = exchangeData.getData();
                    if (ObjectNull.isNotNull(data)) {
                        JSONObject object = JSONObject.parseObject(data);
                        object.put("userId", finalUserId);
                        v.accept(session, object.toJSONString(), type);
                    }
                }
            });
        }
    }

    /**
     * 获取类型
     *
     * @param moblieUser 手机
     * @param pcUser     pcll
     * @return
     */
    private String isType(UserDto moblieUser, UserDto pcUser) {
        if (moblieUser != null) {
            return SystemTool.MOBLIE_USER;
        } else if (pcUser != null) {
            return SystemTool.PC_USER;
        }
        return "";
    }


    /**
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserDto moblieUser = (UserDto) session.getAttributes().get(SystemTool.MOBLIE_USER);
        UserDto pcUser = (UserDto) session.getAttributes().get(SystemTool.PC_USER);
        if (ObjectNull.isNotNull(moblieUser)) {
            log.debug("移动用户 [{}] 断开连接,租户id为：{}", moblieUser.getId() + "_" + moblieUser.getTenantId(), moblieUser.getTenantId());
            SocketSessionManager.removeAndClose(SystemTool.MOBLIE_USER + moblieUser.getId() + "_" + moblieUser.getTenantId());
        }
        if (ObjectNull.isNotNull(pcUser)) {
            log.debug("pc用户 [{}] 断开连接", pcUser.getId() + "_" + pcUser.getTenantId());
            SocketSessionManager.removeAndClose(SystemTool.MOBLIE_USER + pcUser.getId() + "_" + pcUser.getTenantId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        // 消息传输出错时调用
        log.error("socket session{}", session);
    }
}
