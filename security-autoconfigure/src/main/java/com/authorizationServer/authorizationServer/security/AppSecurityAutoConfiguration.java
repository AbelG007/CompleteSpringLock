package com.authorizationServer.authorizationServer.security;

import com.authorizationServer.authorizationServer.security.auth.UserConfig;
import com.authorizationServer.authorizationServer.security.config.AuthorizationServerPersistenceConfig;
import com.authorizationServer.authorizationServer.security.config.AuthorizationServerSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.BasicSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.DefaultWebSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.FormSessionSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.JwtResourceServerSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.NoneSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.PasswordSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.SecurityModeLogger;
import com.authorizationServer.authorizationServer.security.config.TokenSecurityConfig;
import com.authorizationServer.authorizationServer.security.properties.AppSecurityProperties;
import com.authorizationServer.authorizationServer.security.properties.JwtResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties({
        AppSecurityProperties.class,
        JwtResourceServerProperties.class
})
@Import({
        AuthorizationServerPersistenceConfig.class,
        AuthorizationServerSecurityConfig.class,
        BasicSecurityConfig.class,
        DefaultWebSecurityConfig.class,
        FormSessionSecurityConfig.class,
        JwtResourceServerSecurityConfig.class,
        NoneSecurityConfig.class,
        PasswordSecurityConfig.class,
        SecurityModeLogger.class,
        TokenSecurityConfig.class,
        UserConfig.class
})
public class AppSecurityAutoConfiguration {
}
