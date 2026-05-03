package com.authorizationServer.authorizationServer.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "basic"
)
public class BasicSecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain basicSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(http -> http.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
}
