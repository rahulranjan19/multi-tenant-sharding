package com.multitenant.tenant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
public class TenantProperties {
    @Value("#{${tenant.schema}}")
    Map<String, String> schema;

    @Value("#{${tenant.db}}")
    Map<String, String> db;

    public Map<String, String> getSchema() {
        return Collections.unmodifiableMap(this.schema);
    }

    public Map<String, String> getDb() {
        return Collections.unmodifiableMap(this.db);
    }
}
