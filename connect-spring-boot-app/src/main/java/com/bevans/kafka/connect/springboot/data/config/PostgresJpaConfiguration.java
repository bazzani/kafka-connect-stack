package com.bevans.kafka.connect.springboot.data.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = PostgresJpaConfiguration.ENTITY_REPO_PACKAGE,
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager"
)
public class PostgresJpaConfiguration {

    public static final String ENTITY_REPO_PACKAGE = "com.bevans.kafka.connect.springboot.data.ally";

    @Bean
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource postgresDataSource() {
        return postgresDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            @Qualifier("postgresDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages(ENTITY_REPO_PACKAGE)
                .build();
    }

    @Bean
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresEntityManagerFactory") LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(postgresEntityManagerFactory.getObject()));
    }
}
