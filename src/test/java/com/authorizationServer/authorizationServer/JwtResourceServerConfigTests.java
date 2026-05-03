package com.authorizationServer.authorizationServer;

import com.authorizationServer.authorizationServer.security.config.AuthorizationServerPersistenceConfig;
import com.authorizationServer.authorizationServer.security.config.AuthorizationServerSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.DefaultWebSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.JwtResourceServerSecurityConfig;
import com.authorizationServer.authorizationServer.security.config.TokenSecurityConfig;
import com.authorizationServer.authorizationServer.security.properties.AppSecurityProperties;
import com.authorizationServer.authorizationServer.security.properties.SecurityMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app-security.mode=JWT_RESOURCE_SERVER")
@ActiveProfiles("test")
class JwtResourceServerConfigTests {

    @Autowired
    private AppSecurityProperties appSecurityProperties;

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @Test
    void jwtResourceServerModeBinds() {
        assertThat(appSecurityProperties.getMode()).isEqualTo(SecurityMode.JWT_RESOURCE_SERVER);
        assertThat(listableBeanFactory.getBeanNamesForType(JwtResourceServerSecurityConfig.class)).hasSize(1);
    }

    @Test
    void authorizationServerConfigsDoNotLoadInJwtMode() {
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerPersistenceConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(TokenSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(DefaultWebSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(RegisteredClientRepository.class)).isEmpty();
    }
}
