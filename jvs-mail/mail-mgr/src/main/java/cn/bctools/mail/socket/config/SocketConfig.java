package cn.bctools.mail.socket.config;

import cn.bctools.mail.socket.interceptor.SocketAuthHandler;
import cn.bctools.mail.socket.interceptor.SocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Description: websocket 拦截器配置
 */
@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    @Autowired
    private SocketAuthHandler socketAuthHandler;
    @Autowired
    private SocketInterceptor socketInterceptor;


    private static final String WEB_SOCKET_PATH = "websocket";

    /**
     * @param webSocketHandlerRegistry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(socketAuthHandler, WEB_SOCKET_PATH)
                .addInterceptors(socketInterceptor)
                .setAllowedOrigins("*");
    }

//    @Bean
//    public TomcatContextCustomizer tomcatContextCustomizer() {
//        return context -> context.addServletContainerInitializer(new WsSci(), null);
//    }
}
