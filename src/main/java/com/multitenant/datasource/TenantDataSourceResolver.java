package com.multitenant.datasource;

import com.multitenant.tenant.TenantContextService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantDataSourceResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    private TenantContextService tenantContextService;

    @Override
    public String resolveCurrentTenantIdentifier() {
        return tenantContextService.getTenantDatabase();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

