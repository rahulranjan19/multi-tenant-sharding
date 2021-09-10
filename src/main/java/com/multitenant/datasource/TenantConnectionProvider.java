package com.multitenant.datasource;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class TenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    private RoutingDataSource routingDataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return routingDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.debug("Get connection for tenant {}", tenantIdentifier);
        // connection.setSchema(tenantIdentifier);
        return routingDataSource.getConnection();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.debug("Release connection for tenant {}", tenantIdentifier);
        // connection.setSchema(TenantDataSourceResolver.DEFAULT_TENANT);
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
