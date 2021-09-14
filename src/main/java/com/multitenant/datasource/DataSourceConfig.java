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
    @Bean(name = "ccDataSourceProperties")
    @ConfigurationProperties("spring.cc.datasource")
    public DataSourceProperties ccDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "ccdb")
    @ConfigurationProperties("spring.cc.datasource.hikari")
    public DataSource ccdb(@Qualifier("ccDataSourceProperties") DataSourceProperties ccDataSourceProperties) {
        return ccDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "samsDataSourceProperties")
    @ConfigurationProperties("spring.sams.datasource")
    public DataSourceProperties samsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "samsdb")
    @ConfigurationProperties("spring.sams.datasource.hikari")
    public DataSource samsdb(@Qualifier("samsDataSourceProperties") DataSourceProperties samsDataSourceProperties) {
        return samsDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
