package com.authorizationServer.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "authorization-server",
        matchIfMissing = true
)
public class AuthorizationServerClientSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationServerClientSeeder.class);

    private final RegisteredClientRepository registeredClientRepository;
    private final AuthorizationServerClientProperties authorizationServerClientProperties;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationServerClientSeeder(
            RegisteredClientRepository registeredClientRepository,
            AuthorizationServerClientProperties authorizationServerClientProperties,
            PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationServerClientProperties = authorizationServerClientProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedClients();
    }

    void seedClients() {
        validateDistinctClientIds();

        for (AuthorizationServerClientProperties.Client client : authorizationServerClientProperties.getClients()) {
            if (client.getClientId() == null || client.getClientId().isBlank()) {
                continue;
            }

            RegisteredClient existingClient = registeredClientRepository.findByClientId(client.getClientId());
            if (existingClient != null) {
                continue;
            }

            RegisteredClient registeredClient = client.toRegisteredClient(passwordEncoder);
            registeredClientRepository.save(registeredClient);
            log.info("Seeded authorization server client {}", client.getClientId());
        }
    }

    private void validateDistinctClientIds() {
        Set<String> seenClientIds = new HashSet<>();
        for (AuthorizationServerClientProperties.Client client : authorizationServerClientProperties.getClients()) {
            String clientId = client.getClientId();
            if (clientId == null || clientId.isBlank()) {
                continue;
            }
            if (!seenClientIds.add(clientId)) {
                throw new IllegalStateException("Duplicate authorization server clientId found: " + clientId);
            }
        }
    }
}
