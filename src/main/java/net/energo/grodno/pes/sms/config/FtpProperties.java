package net.energo.grodno.pes.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ftp")
@Data
public class FtpProperties {
    private Connection connection;
    private String baseFolder;

    @Data
    public static class Connection {
        private String server;
        private Integer port;
        private String login;
        private String password;
    }
}
