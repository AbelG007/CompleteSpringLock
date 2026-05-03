package com.authorizationServer.bootstrap;

import com.authorizationServer.dto.CustomerCreateRequest;
import com.authorizationServer.dto.CustomerResponse;
import com.authorizationServer.exception.CustomerNotFoundException;
import com.authorizationServer.services.CustomerManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "authorization-server",
        matchIfMissing = true
)
public class AuthorizationServerBootstrapUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationServerBootstrapUserSeeder.class);

    private final CustomerManagementService customerManagementService;
    private final BootstrapUserProperties bootstrapUserProperties;

    public AuthorizationServerBootstrapUserSeeder(
            CustomerManagementService customerManagementService,
            BootstrapUserProperties bootstrapUserProperties) {
        this.customerManagementService = customerManagementService;
        this.bootstrapUserProperties = bootstrapUserProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedBootstrapUser();
    }

    void seedBootstrapUser() {
        if (!bootstrapUserProperties.isEnabled()) {
            return;
        }

        try {
            customerManagementService.getCustomerByEmail(bootstrapUserProperties.getEmail());
        } catch (CustomerNotFoundException ex) {
            CustomerResponse createdCustomer = customerManagementService.createCustomer(
                    new CustomerCreateRequest(
                            bootstrapUserProperties.getName(),
                            bootstrapUserProperties.getEmail(),
                            bootstrapUserProperties.getMobileNumber(),
                            bootstrapUserProperties.getPassword(),
                            bootstrapUserProperties.getRole(),
                            bootstrapUserProperties.getAuthorities()
                    )
            );
            log.info("Seeded bootstrap login user {}", createdCustomer.email());
        }
    }
}
