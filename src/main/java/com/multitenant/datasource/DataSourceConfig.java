package com.multitenant.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "testDataSourceProperties")
    @ConfigurationProperties("spring.test.datasource")
    public DataSourceProperties testDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "testdb")
    @ConfigurationProperties("spring.test.datasource.hikari")
    public DataSource testDataSource(@Qualifier("testDataSourceProperties") DataSourceProperties testDataSourceProperties) {
        return testDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
