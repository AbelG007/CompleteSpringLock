package com.authorizationServer.authorizationServer.security.auth;

import com.authorizationServer.authorizationServer.security.properties.FormSessionUserProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import lombok.RequiredArgsConstructor;

@Configuration
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "FORM_SESSION"
)
@RequiredArgsConstructor
public class UserConfig { // Renamed to avoid conflict

    private final FormSessionUserProperties formSessionUserProperties;

    @Bean
    public UserDetailsService userDetailsService() {
        String[] roles = formSessionUserProperties.getRoles().isEmpty()
                ? new String[]{"USER"}
                : formSessionUserProperties.getRoles().toArray(new String[0]);

        UserDetails user = User.withDefaultPasswordEncoder()
                .username(formSessionUserProperties.getUsername())
                .password(formSessionUserProperties.getPassword())
                .roles(roles)
                .build();

        // Return the actual Spring Security implementation
        return new InMemoryUserDetailsManager(user);
    }
}
