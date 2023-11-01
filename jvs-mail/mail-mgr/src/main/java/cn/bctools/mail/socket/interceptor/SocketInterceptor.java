package cn.bctools.mail.socket.interceptor;

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.mail.constant.DeviceTypeConstant;
import cn.bctools.mail.utils.SystemTool;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * @Description: websocket 握手拦截器
 */
@Slf4j
@Component
public class SocketInterceptor extends HttpSessionHandshakeInterceptor {

    private static final String TOKEN_FIELD = "token";

    @Autowired
    RedisUtils redisTemplate;

    /**
     * websocket 握手之前
     *
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param webSocketHandler
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        // 获取请求参数
        String token = serverHttpRequest.getHeaders().getFirst(TOKEN_FIELD);
        String authorization = HttpUtil.decodeParamMap(serverHttpRequest.getURI().getQuery(), Charsets.UTF_8).get(TOKEN_FIELD);
        String auth = StrUtil.blankToDefault(authorization, token);
        log.debug("websocket 开始握手,token：{}", auth);
        if (StrUtil.isNotBlank(auth)) {
            String key = SysConstant.redisKey("token", auth);
            UserDto cacheUser = (UserDto) redisTemplate.get(key);
            if (cacheUser == null || cacheUser.getId() == null) {
                return false;
            }
            //处理token 解析token 转成用户信息 （缓存 数据库）
            //刷新登录
            log.debug("websocket 开始握手,用户id：{},租户id:{}", cacheUser.getId(), cacheUser.getTenantId());
            String str = auth.split("_")[0];
            String app = "APP";
            if (app.equals(str)) {
                map.put(SystemTool.MOBLIE_USER, cacheUser);
            } else {
                map.put(SystemTool.PC_USER, cacheUser);
            }
        }
        return true;
    }

    /**
     * 判断是否是移动端
     *
     * @param request 请求参数
     */
    public boolean isMoblie(ServerHttpRequest request) {
        boolean isMoblie = false;
        String agent = request.getHeaders().getFirst("User-Agent");
        if (agent != null) {
            if (DeviceTypeConstant.DEVICE.contains(agent.toLowerCase()) && agent.toLowerCase().indexOf("windows nt") <= 0 && agent.toLowerCase().indexOf("macintosh") <= 0) {
                isMoblie = true;
            }
        }
        return isMoblie;
    }

    /**
     * websocket 握手之后
     *
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param webSocketHandler
     * @param e
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler, Exception e) {
        log.debug("握手完成!");
    }
}
