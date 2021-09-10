package com.multitenant.datasource;

import com.multitenant.tenant.TenantContextService;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    private final TenantContextService tenantContextService;

    public RoutingDataSource(TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return tenantContextService.getTenantDatabase();
    }
}
