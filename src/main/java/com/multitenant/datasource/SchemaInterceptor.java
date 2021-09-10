package com.multitenant.datasource;

import com.multitenant.tenant.TenantContextService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;

@Slf4j
public class SchemaInterceptor extends EmptyInterceptor {

    private final TenantContextService tenantContextService;

    public SchemaInterceptor(TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
    }

    @Override
    public String onPrepareStatement(String sql) {
        return formatSql(sql);
    }

    public String formatSql(String sql) {
        log.debug("current sql: {}", sql);
        String schema = tenantContextService.getTenantSchema();
        final String formattedSql = StringUtils.isNotBlank(sql) ? sql.replaceAll("dbo", schema) : sql;
        log.debug("converted sql: {}", formattedSql);
        return formattedSql;
    }
}