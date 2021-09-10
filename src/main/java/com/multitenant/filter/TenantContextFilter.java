package com.multitenant.filter;

import com.multitenant.tenant.TenantContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@WebFilter(urlPatterns = "/**", asyncSupported = true)
public class TenantContextFilter extends OncePerRequestFilter {

    @Autowired
    private TenantContextService tenantContextService;

    private final List<String> ignoredList = Arrays.asList("h2", "actuator", "swagger", "api-docs", "/");
//
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (ignoredList.stream().noneMatch(url -> request.getRequestURI().contains(url))) {
            final String tenantId = request.getHeader("tenantId");
            try {
                String currentTenant = tenantContextService.setTenantContext(tenantId);
                if (!StringUtils.hasLength(currentTenant)) {
                    log.warn("Tenant id is empty for req: {}", request.getRequestURI());
                }
                log.debug("Tenant: {} is set for request: {}", currentTenant, request.getRequestURI());
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                log.error("Error {}",request.getRequestURI(), ex);
                throw ex;
            } finally {
                String previousTenant = tenantContextService.cleatTenant();
                log.debug("Tenant: {} is cleared for request: {}", previousTenant, request.getRequestURI());
            }
//        } else {
//            log.debug("ignoring tenant-filter");
//            filterChain.doFilter(request, response);
//        }
    }
}
