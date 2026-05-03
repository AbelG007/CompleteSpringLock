package com.authorizationServer.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Order(0)
@ConditionalOnProperty(
        prefix = "app-security",
        name = "mode",
        havingValue = "authorization-server",
        matchIfMissing = true
)
public class AuthorizationServerSchemaInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    public AuthorizationServerSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/bootstrap/oauth2-authorization-server-schema.sql"));
        populator.execute(dataSource);
    }
}
