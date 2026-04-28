package com.authorizationServer.authorizationServer.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class DatabaseMigrationConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .dataSource(dataSource)
                .load();
    }

    @Bean
    public static BeanFactoryPostProcessor entityManagerFactoryDependsOnFlyway() {
        return (ConfigurableListableBeanFactory beanFactory) -> {
            String[] beanNames = beanFactory.getBeanNamesForType(
                    LocalContainerEntityManagerFactoryBean.class, true, false);
            for (String beanName : beanNames) {
                String definitionName = beanName.startsWith("&") ? beanName.substring(1) : beanName;
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(definitionName);
                beanDefinition.setDependsOn("flyway");
            }
        };
    }
}
