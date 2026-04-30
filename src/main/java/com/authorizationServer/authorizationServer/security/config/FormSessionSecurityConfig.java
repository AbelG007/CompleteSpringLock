package com.authorizationServer.authorizationServer.security.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "FORM_SESSION"
)
public class FormSessionSecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain formSecurityFilterChain (HttpSecurity httpSecurity){
        httpSecurity
                .authorizeHttpRequests(http -> http.anyRequest().authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .csrf(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return httpSecurity.build();

    }
}
