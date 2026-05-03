package com.authorizationServer.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationServerClientSeederTests {

    @Autowired
    private AuthorizationServerClientProperties authorizationServerClientProperties;

    @Autowired
    private AuthorizationServerClientSeeder authorizationServerClientSeeder;

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void demoClientIsSeededOnStartup() {
        AuthorizationServerClientProperties.Client clientProperties = authorizationServerClientProperties.getClients().get(0);

        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientProperties.getClientId());

        assertThat(registeredClient).isNotNull();
        assertThat(registeredClient.getClientId()).isEqualTo(clientProperties.getClientId());
        assertThat(registeredClient.getClientName()).isEqualTo(clientProperties.getClientName());
        assertThat(registeredClient.getRedirectUris()).containsExactly(clientProperties.getRedirectUris().get(0));
    }

    @Test
    void seedingIsIdempotent() {
        AuthorizationServerClientProperties.Client clientProperties = authorizationServerClientProperties.getClients().get(0);

        authorizationServerClientSeeder.seedClients();
        authorizationServerClientSeeder.seedClients();

        Long count = jdbcTemplate.queryForObject(
                "select count(*) from oauth2_registered_client where client_id = ?",
                Long.class,
                clientProperties.getClientId()
        );

        assertThat(count).isEqualTo(1L);
    }
}
