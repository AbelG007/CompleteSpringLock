package com.authorizationServer.authorizationServer;

import com.authorizationServer.authorizationServer.security.config.*;
import com.authorizationServer.authorizationServer.security.properties.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app-security.mode=basic")
@ActiveProfiles("test")
public class BasicSecurityConfigTests {


    @Autowired
    ListableBeanFactory listableBeanFactory;

    @Autowired
    AppSecurityProperties appSecurityProperties;

    @Test
    void basicSecurityConfigLoadsWhenModeIsBasic() {
        assertThat(appSecurityProperties.getMode()).isEqualTo(SecurityMode.BASIC);
        assertThat(listableBeanFactory.getBeanNamesForType(BasicSecurityConfig.class)).hasSize(1);
    }

    @Test
    void authorizationServerConfigDoesNotLoadWhenModeIsBasic() {
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(AuthorizationServerPersistenceConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(TokenSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(DefaultWebSecurityConfig.class)).isEmpty();
        assertThat(listableBeanFactory.getBeanNamesForType(RegisteredClientRepository.class)).isEmpty();
    }

}
