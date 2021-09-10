package com.multitenant.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class TenantContextService {

    public static final String DEFAULT_DATABASE = "testdb";
    @Autowired
    private TenantProperties tenantProperties;

    /**
     * logic to set tenant and return tenant key
     *
     * @param tenant request tenant
     * @return created tenant-key
     */
    public String setTenantContext(String tenant) {
        if (!isEnabled(tenant)) {
            log.warn("tenant id is not enabled");
//            throw new RuntimeException("Tenant not supported: " + tenant);
        }
        TenantContext.setCurrentTenant(tenant);
        return tenant;
    }

    /**
     * get current tenant key
     *
     * @return tenant-key
     */
    public String getTenantContext() {
        return TenantContext.getCurrentTenant();
    }

    public String getTenantDatabase() {
        return Optional.ofNullable(tenantProperties.getDb().get(TenantContext.getCurrentTenant())).orElse(DEFAULT_DATABASE);
    }

    public String getTenantSchema() {
        return tenantProperties.getSchema().get(TenantContext.getCurrentTenant());
    }

    public Collection<String> getConfiguredDatabase() {
        return tenantProperties.getDb().values();
    }

    /**
     * clean tenant and return previous tenant
     *
     * @return prev-tenant-key
     */
    public String cleatTenant() {
        final String previousTenant = TenantContext.getCurrentTenant();
        TenantContext.clear();
        return previousTenant;
    }

    /**
     * @return
     */
    private boolean isEnabled(String tenant) {
        return !CollectionUtils.isEmpty(tenantProperties.getSchema()) && tenantProperties.getSchema().containsKey(tenant);
    }
}
