package com.authorizationServer.authorizationServer.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app-security.jwt-resource-server")
public class JwtResourceServerProperties {

    private String issuerUri = "http://127.0.0.1:9000";

    private String adminPath = "/api/admin/**";

}
