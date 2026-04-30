package com.authorizationServer.authorizationServer.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.authorizationServer.authorizationServer.security.properties.AppSecurityProperties;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityModeLogger {

    private final AppSecurityProperties appSecurityProperties;

    @Bean
    ApplicationRunner logSecurityMode() {
        return args -> log.info("Active app-security mode: {}", appSecurityProperties.getMode());
    }
}
