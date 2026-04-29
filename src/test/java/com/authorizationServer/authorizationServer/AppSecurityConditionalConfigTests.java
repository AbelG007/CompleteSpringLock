package com.authorizationServer.authorizationServer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import com.authorizationServer.authorizationServer.config.AppSecurityProperties;
import com.authorizationServer.authorizationServer.config.AuthorizationServerPersistenceConfig;
import com.authorizationServer.authorizationServer.config.AuthorizationServerSecurityConfig;
import com.authorizationServer.authorizationServer.config.DefaultWebSecurityConfig;
import com.authorizationServer.authorizationServer.config.NoneSecurityConfig;
import com.authorizationServer.authorizationServer.config.SecurityMode;
import com.authorizationServer.authorizationServer.config.TokenSecurityConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app-security.mode=none"})
@ActiveProfiles("test")
public class AppSecurityConditionalConfigTests {

    @Autowired
    ListableBeanFactory listableBeanFactory;

    @Autowired
    AppSecurityProperties appSecurityProperties;

    @Test
    void noneSecurityConfigLoadsWhenModeIsNone() {
        assertThat(appSecurityProperties.getMode()).isEqualTo(SecurityMode.NONE);
        assertThat(listableBeanFactory.getBeanNamesForType(NoneSecurityConfig.class)).hasSize(1);
    }

    @Test
    void authorizationServerConfigDoesNotLoadWhenModeIsNone() {
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerPersistenceConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(TokenSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(DefaultWebSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(RegisteredClientRepository.class)).isEmpty();
    }

}
