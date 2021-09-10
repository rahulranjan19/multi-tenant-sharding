package com.multitenant.datasource;

import com.multitenant.tenant.TenantContextService;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.multitenant.tenant.TenantContextService.DEFAULT_DATABASE;

@Configuration
@AutoConfigureAfter({JmxAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.multitenant.repository",
        entityManagerFactoryRef = "multiTenantEntityManagerFactory", transactionManagerRef = "multiTenantTransactionManager")
public class TenantJpaConfig {

    @Primary
    @Bean("multiTenantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaProperties jpaProperties,
                                                                       JpaVendorAdapter jpaVendorAdapter,
                                                                       ConfigurableListableBeanFactory beanFactory,
                                                                       TenantConnectionProvider tenantConnectionProvider,
                                                                       TenantDataSourceResolver tenantDataSourceResolver,
                                                                       @Qualifier("schemaInterceptor") SchemaInterceptor schemaInterceptor) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPackagesToScan(getPackagesToScan(beanFactory));
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setJpaPropertyMap(getCustomJpaProperties(jpaProperties, schemaInterceptor, tenantConnectionProvider, tenantDataSourceResolver));

        final String[] mappingResources = getMappingResources(jpaProperties);
        if (Objects.nonNull(mappingResources)) {
            entityManagerFactoryBean.setMappingResources(mappingResources);
        }

        return entityManagerFactoryBean;
    }

    private Map<String, Object> getCustomJpaProperties(JpaProperties jpaProperties,
                                                       SchemaInterceptor schemaInterceptor,
                                                       TenantConnectionProvider tenantConnectionProvider,
                                                       TenantDataSourceResolver tenantDataSourceResolver) {
        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProvider);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantDataSourceResolver);
        jpaPropertiesMap.put(Environment.INTERCEPTOR, schemaInterceptor);
        jpaPropertiesMap.put(Environment.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class);
        return jpaPropertiesMap;
    }

    @Bean
    @Primary
    public SchemaInterceptor schemaInterceptor(final TenantContextService tenantContextService) {
        return new SchemaInterceptor(tenantContextService);
    }

    private String[] getPackagesToScan(final ConfigurableListableBeanFactory beanFactory) {
        List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
            packages = AutoConfigurationPackages.get(beanFactory);
        }
        return StringUtils.toStringArray(packages);
    }

    private String[] getMappingResources(JpaProperties properties) {
        List<String> mappingResources = properties.getMappingResources();
        return (!ObjectUtils.isEmpty(mappingResources) ? StringUtils.toStringArray(mappingResources) : null);
    }

    @Primary
    @Bean("multiTenantTransactionManager")
    public JpaTransactionManager createTransactionManager(@Qualifier("multiTenantEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @Primary
    public JpaVendorAdapter jpaVendorAdapter(JpaProperties properties) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(properties.isShowSql());
        adapter.setDatabase(properties.getDatabase());
        adapter.setDatabasePlatform(properties.getDatabasePlatform());
        adapter.setGenerateDdl(properties.isGenerateDdl());
        return adapter;
    }

    @Bean
    public BeanFactoryDataSourceLookup dataSourceLookup() {
        return new BeanFactoryDataSourceLookup();
    }

    @Bean
    public RoutingDataSource routingDataSource(final TenantContextService tenantContextService, BeanFactoryDataSourceLookup dataSourceLookup) {
        final RoutingDataSource routingDataSource = new RoutingDataSource(tenantContextService);
        routingDataSource.setDefaultTargetDataSource(dataSourceLookup.getDataSource(DEFAULT_DATABASE));

        Map<Object, Object> dataSources = new HashMap<>();
        tenantContextService.getConfiguredDatabase().forEach(dataSource ->
                dataSources.putIfAbsent(dataSource, dataSourceLookup.getDataSource(dataSource))
        );
        routingDataSource.setTargetDataSources(dataSources);

        return routingDataSource;
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(SchemaInterceptor schemaInterceptor, RoutingDataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource) {
            @Override
            public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
                sql = schemaInterceptor.formatSql(sql);
                return super.query(sql, rse);
            }

            @Override
            public <T> T query(String sql, @Nullable PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
                sql = schemaInterceptor.formatSql(sql);
                return super.query(sql, pss, rse);
            }

            @Override
            public int update(String sql, @Nullable Object... args) throws DataAccessException {
                sql = schemaInterceptor.formatSql(sql);
                return super.update(sql, args);
            }

            @Override
            public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
                sql = schemaInterceptor.formatSql(sql);
                return super.execute(sql, action);
            }
        };
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate, SchemaInterceptor schemaInterceptor) {
        return new NamedParameterJdbcTemplate(jdbcTemplate) {
            @Override
            protected ParsedSql getParsedSql(String sql) {
                sql = schemaInterceptor.formatSql(sql);
                return super.getParsedSql(sql);
            }
        };
    }
}
