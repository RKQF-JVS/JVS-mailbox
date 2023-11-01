package cn.bctools.mail.socket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.oauth2.client")
@Data
public class ClientConfig {

    private String clientId;
    private String clientSecret;
}
